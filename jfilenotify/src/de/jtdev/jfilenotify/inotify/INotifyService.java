package de.jtdev.jfilenotify.inotify;

import de.jtdev.jfilenotify.FileNotifyConstants;
import de.jtdev.jfilenotify.FileNotifyException;
import de.jtdev.jfilenotify.FileNotifyListener;
import de.jtdev.jfilenotify.FileNotifyService;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * This class implements a FileNotifyService with inotify as its backend.
 * It uses a thread to constantly read incomming events from an inotify
 * instance and passes them to the corresponding listeners.
 *
 * It uses the library "jfilenotify" to access the inotify C interface.
 * 
 * @author Tobias Oelgarte
 */
public class INotifyService extends Thread implements FileNotifyService {

	static {
		System.loadLibrary("jfilenotify");
	}

	/**
	 * The file descriptor to the native allocated inotify instance
	 */
	private long fileDescriptor;

	/**
	 * If the service is disposed he will no longer acceppt that new Listeners
	 * will be added.
	 */
	private boolean isDisposed = false;
	
	/**
	 * This object is used as monitor to stop the thread if no listener is 
	 * registered.
	 */
	private final Object threadLock = new Object();
	
	/**
	 * This set stores the added listerns in groups sorted by the watch 
	 * descriptor returned by inotify. Listeners that listen to the same inode 
	 * will be added to the same ListenerGroup.
	 */
	private final TreeSet<ListenerGroup> listenerGroupSet = new TreeSet<ListenerGroup>();

	/**
	 * Creates a new INotifyService instance and allocates the needed native
	 * resources. It also starts a thread that notifies the listeners about
	 * changes.
	 * @throws FileNotifyException
	 *         if no inotify instance could be allocated.
	 */
	public INotifyService() throws FileNotifyException {
		fileDescriptor = createINotifyInstance(); // native call
		if (fileDescriptor < 0) {
			String reason = ErrnoMessages.getDescription((int) -fileDescriptor);
			throw new FileNotifyException("Service could not be created (" + reason + ")");
		}
		this.start();
		this.setDaemon(true);
	}

	public void addFileNotifyListener(FileNotifyListener listener) throws FileNotifyException {
		if (isDisposed)
			throw new FileNotifyException("Can't add listener to disposed service");
		
		String fileName = listener.getFileName();
		
		// convertig mask to inotify specific mask
		int mask = listener.getMask();
		mask = importMask(mask) | INotifyEvent.IN_MASK_ADD;
		
		long watchDescriptor = addWatch(fileDescriptor, fileName, mask); // native call
		if (watchDescriptor < 0) {
			String reason = ErrnoMessages.getDescription((int) -watchDescriptor);
			throw new FileNotifyException("Listener could not be registered (" + reason + ")");
		}
		
		synchronized (listenerGroupSet) {
			ListenerGroup g = searchForListenerGroup(watchDescriptor);
			if (g == null) {
				g = new ListenerGroup(watchDescriptor);
				listenerGroupSet.add(g);
			}
			g.addListener(listener);
		}
		
		// at least one listener has was added, awake the thread so that it can 
		// read events for this listener
		synchronized (threadLock) {
			threadLock.notify();
		}
		
	}

	/**
	 * 
	 */
	public boolean removeFileNotifyListener(FileNotifyListener listener) throws FileNotifyException {
		
		// finds the listener, removes it from his group and removes also 
		// the group if it is empty after removing this listener.
		ListenerGroup g = null;
		synchronized (listenerGroupSet) {
			Iterator<ListenerGroup> iter = listenerGroupSet.iterator();
			while (iter.hasNext()) {
				ListenerGroup tmp = iter.next();
				if (tmp.removeListener(listener)) {
					if (tmp.isEmpty()) {
						iter.remove();
					}
					g = tmp;
					break;
				}
			}
		}
		
		// if the listener was not added to this service return false.
		if (g == null)
			return false;
		
		synchronized (g) {
			if (g.isEmpty()) { // remove the watch if the group is empty
				long ret = removeWatch(fileDescriptor, g.getWatchDescriptor()); // native call
				if (ret < 0) {
					String reason = ErrnoMessages.getDescription((int) -ret);
					throw new FileNotifyException("Listener could not be unregisterd (" + reason + ")");
				}
			} else { // update the mask of the watch, if group is not empty
				long ret = addWatch(fileDescriptor, g.getLastFileName(), g.getCombinedMask());
				if (ret < 0) {
					String reason = ErrnoMessages.getDescription((int) -ret);
					throw new FileNotifyException("Listener could not be updated (" + reason + ")");
				}
				// TODO is inotify free to return a diffrent watch as before?
				//      in that case the watchDescriptor of the group must be 
				//      updated, the listenerGroupSet newly sorted, 
				if (ret != g.getWatchDescriptor()) {
					throw new FileNotifyException("Other descriptor returned, yet missing code to handle this.");
				}
			}
		}
		return true;
	}

	/**
	 * Disposes this service. It will release all allocated native resources
	 * and removes all waiting listeners and call their discarded() method.
	 * @throws FileNotifyException
	 *         if not all resources could be freed. This
	 *         should never happen.
	 */
	public synchronized void dispose() throws FileNotifyException {
		if (!isDisposed) {
			isDisposed = true;
			long ret = releaseINotifyInstance(fileDescriptor); // native call
			
			// removing all listener groups, so that the thread can terminate
			synchronized (listenerGroupSet) {
				listenerGroupSet.clear();
			}
			
			// awake the thread, so that he can terminate
			synchronized (threadLock) {
				threadLock.notify();
			}
			
			if (ret < 0) {
				String reason = ErrnoMessages.getDescription((int) -ret);
				throw new FileNotifyException("Service could not be disposed (" + reason + ")");
			}
		}
	}
	
	/**
	 * <p>Called by the garbage collector on this object when garbage collection
	 * determines that there are no more references to the object.</p>
	 *
	 * <p>Internaly it calles {@link #dispose()} to make sure not needed
	 * resources are released.</p>
	 */
	public void finalize() {
		try {
			dispose();
		} catch (FileNotifyException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * The constants for inotify and this project are not the same, so this 
	 * method convert a mask that is used by project to a mask that inotify 
	 * understands.
	 * 
	 * @param externalMask 
	 *        the mask that should be converted to a mask that fits inotify
	 * @return returns a mask suitable for inotify
	 */
	private static int importMask(final int externalMask) {
		int m = 0x00000000;
		
		if ((FileNotifyConstants.OPENED               & externalMask) != 0)  m |= INotifyEvent.IN_OPEN;
		if ((FileNotifyConstants.ACCESSED             & externalMask) != 0)  m |= INotifyEvent.IN_ACCESS;
		if ((FileNotifyConstants.MODIFIED             & externalMask) != 0)  m |= INotifyEvent.IN_MODIFY;
		if ((FileNotifyConstants.CLOSED_WRITEABLE     & externalMask) != 0)  m |= INotifyEvent.IN_CLOSE_WRITE;
		if ((FileNotifyConstants.CLOSED_NOT_WRITEABLE & externalMask) != 0)  m |= INotifyEvent.IN_CLOSE_NOWRITE;
		if ((FileNotifyConstants.ATTRIBUTES_CHANGED   & externalMask) != 0)  m |= INotifyEvent.IN_ATTRIB;
		
		if ((FileNotifyConstants.SUBFILE_CREATED & externalMask) != 0)  m |= INotifyEvent.IN_CREATE;
		if ((FileNotifyConstants.SUBFILE_DELETED & externalMask) != 0)  m |= INotifyEvent.IN_DELETE;
		if ((FileNotifyConstants.MOVED_FROM      & externalMask) != 0)  m |= INotifyEvent.IN_MOVED_FROM;
		if ((FileNotifyConstants.MOVED_TO        & externalMask) != 0)  m |= INotifyEvent.IN_MOVED_TO;
		if ((FileNotifyConstants.SELF_DELETED    & externalMask) != 0)  m |= INotifyEvent.IN_DELETE_SELF;
		if ((FileNotifyConstants.SELF_MOVED      & externalMask) != 0)  m |= INotifyEvent.IN_MOVE_SELF;
		
		if ((FileNotifyConstants.ONLY_DIRECTORY & externalMask) != 0)  m |= INotifyEvent.IN_ONLYDIR;
		if ((FileNotifyConstants.DONT_FOLLOW    & externalMask) != 0)  m |= INotifyEvent.IN_DONT_FOLLOW;
		
		return m;
	}
	
	/**
	 * The constants for inotify and this project are not the same, so this 
	 * method convert a mask that is used by inotify to a mask that this 
	 * project uses.
	 * 
	 * @param internalMask
	 *        the mask tath should be converted to a mask that fits this 
	 *        project
	 * @return returns a mask suitable fot this project
	 */
	protected static int exportMask(final int internalMask) {
		int m = 0x00000000;
		
		if ((INotifyEvent.IN_OPEN          & internalMask) != 0)  m |= FileNotifyConstants.OPENED;
		if ((INotifyEvent.IN_ACCESS        & internalMask) != 0)  m |= FileNotifyConstants.ACCESSED;
		if ((INotifyEvent.IN_MODIFY        & internalMask) != 0)  m |= FileNotifyConstants.MODIFIED;
		if ((INotifyEvent.IN_CLOSE_WRITE   & internalMask) != 0)  m |= FileNotifyConstants.CLOSED_WRITEABLE;
		if ((INotifyEvent.IN_CLOSE_NOWRITE & internalMask) != 0)  m |= FileNotifyConstants.CLOSED_NOT_WRITEABLE;
		if ((INotifyEvent.IN_ATTRIB        & internalMask) != 0)  m |= FileNotifyConstants.ATTRIBUTES_CHANGED;
		
		if ((INotifyEvent.IN_CREATE      & internalMask) != 0)  m |= FileNotifyConstants.SUBFILE_CREATED;
		if ((INotifyEvent.IN_DELETE      & internalMask) != 0)  m |= FileNotifyConstants.SUBFILE_DELETED;
		if ((INotifyEvent.IN_MOVED_FROM  & internalMask) != 0)  m |= FileNotifyConstants.MOVED_FROM;
		if ((INotifyEvent.IN_MOVED_TO    & internalMask) != 0)  m |= FileNotifyConstants.MOVED_TO;
		if ((INotifyEvent.IN_DELETE_SELF & internalMask) != 0)  m |= FileNotifyConstants.SELF_DELETED;
		if ((INotifyEvent.IN_MOVE_SELF   & internalMask) != 0)  m |= FileNotifyConstants.SELF_MOVED;
		
		// TODO overflow is ignored completly so far.. pray that it does not happen ;-|
		// if ((INotifyEvent.IN_ISDIR   & internalMask) != 0)  m |= FileNotifyConstants.IS_DIRECTORY;
		if ((INotifyEvent.IN_UNMOUNT & internalMask) != 0)  m |= FileNotifyConstants.UNMOUNTED;
		
		return m;
	}
	
	private ListenerGroup searchForListenerGroup(long watchDecsriptor) {
		ListenerGroup searchGroup = new ListenerGroup(watchDecsriptor);
		ListenerGroup g;
		synchronized (listenerGroupSet) {
			g = listenerGroupSet.floor(searchGroup);
		}
		if (searchGroup.equals(g))
			return g;
		return null;
	}
	
	private boolean removeListenerGroup(INotifyEvent event) {
		// TODO use cached version of searchGroup for better performance
		ListenerGroup searchGroup = new ListenerGroup(event.getWatchDescriptor());
		synchronized (listenerGroupSet) {
			ListenerGroup g = listenerGroupSet.floor(searchGroup);
			if (searchGroup.equals(g)) {
				listenerGroupSet.remove(g);
				g.discardAllListeners(event);
				return true;
			}
		}
		return false;
	}

	public void run() {
		while (true) {
			
			while (listenerGroupSet.isEmpty() && !isDisposed) {
				synchronized (threadLock) {
					try { threadLock.wait(); } catch (InterruptedException ex) { }
				}
			}
			
			if (isDisposed)
				break;
			
			List<INotifyEvent> events = readEvents(fileDescriptor); // native call
			
			if (events == null) {
				if (!isDisposed) {
					new FileNotifyException("Critical error while reading events").printStackTrace();
				}
				break; // no more events while all watches are removed and service is disposed
			}
			
			for (INotifyEvent event : events) {
				if (event.isIgnoreEvent()) {
					removeListenerGroup(event);
				} else {
					ListenerGroup g = searchForListenerGroup(event.getWatchDescriptor());
					if (g != null) {
						g.notifyAllListener(event);
					}
				}
			}
		}
	}
	
	private List<INotifyEvent> readEvents(long fileDescriptor) {
		
		byte[] data = readEventData(fileDescriptor);
		
		if (data == null)
			return null;
		
		if (data.length == 0)
			return new LinkedList<INotifyEvent>();
		
		LinkedList<INotifyEvent> list = new LinkedList<INotifyEvent>();
		
		int index = 0;
		while (index < data.length) {
			
			long watchDescriptor;
			if (getIntegerSize() == 8) {
				watchDescriptor = readUnsignedLongReversedOrder(data, index);
				index += 8;
			} else {
				watchDescriptor = readUnsignedIntReversedOrder(data, index);
				index += 4;
			}
			
			int mask = readUnsignedIntReversedOrder(data, index);
			index += 4;
			
			int cookie = readUnsignedIntReversedOrder(data, index);
			index += 4;
			
			int length = readUnsignedIntReversedOrder(data, index);
			index += 4;
			
			String fileName = null;
			if (length > 0) {
				int strlen = strlen(data, index, length);
				fileName = new String(data, index, strlen);
			}
			index += length;
			
			INotifyEvent event = new INotifyEvent(watchDescriptor, cookie, mask, fileName);
			list.add(event);
		}
		
		return list;
	}
		
	private int readUnsignedIntReversedOrder(byte[] b, int pos) {
		return 
				b[pos]   <<  0 | 
				b[pos+1] <<  8 | 
				b[pos+2] << 16 | 
				b[pos+3] << 24;
	}
	
	private long readUnsignedLongReversedOrder(byte[] b, int pos) {
		return 
				(long) b[pos]   <<  0 |
				(long) b[pos+1] <<  8 | 
				(long) b[pos+2] << 16 |
				(long) b[pos+3] << 24 | 
				(long) b[pos+4] << 32 | 
				(long) b[pos+5] << 40 | 
				(long) b[pos+6] << 48 |
				(long) b[pos+7] << 54;
	}
	
	private int strlen(byte[] b, int from, int len) {
		int i = from;
		int end = from + len;
		while (b[i] != 0 && i < end)
			i++;
		return i - from;
	}
	
	/**
	 * Creates a native inotify instance and returns its file descriptor.
	 * If an error occurs, this method returns a negative value which is the 
	 * negative errno number.
	 * @return the file descriptor or a negative errno number if it
	 *         fails
	 */
	private native long createINotifyInstance();
	
	/**
	 * Releases a native inotify instance and return 0 on success or the 
	 * negative errno number in case of an error.
	 * @param fileDescriptor 
	 *        The file descriptor the native inotify instance that sould be 
	 *        released.
	 * @return 0 on success or a negative errno number if it fails
	 */
	private native long releaseINotifyInstance(long fileDescriptor);
	
	/**
	 * Adds a watch to the inotify instance by passing the corresponding
	 * file descriptor, file name and mask.
	 *
	 * @param fileDescriptor
	 *        the descriptor used to access the inotify instance
	 * @param fileName
	 *        the name of the file to watch for
	 * @param mask
	 *        the mask decides which types of events will trigger the
	 *        watch
	 * @return the watch descriptor or a negative errno number if it
	 *         fails
	 */
	private native long addWatch(long fileDescriptor, String fileName, int mask);
	
	/**
	 * Removes a watch from the inotify instance by passing the
	 * corresponding file descriptor and watch descriptor.
	 *
	 * @param fileDescriptor
	 *        the file descriptor used to access the inotify instance
	 * @param watchDescriptor
	 *        the watch descriptor used to define which watch will be
	 *        removed
	 * @return 0 on success or a negative errno number if it fails
	 */
	private native long removeWatch(long fileDescriptor, long watchDescriptor);
	
	private native byte[] readEventData(long fileDescriptor);
	
	private native int getIntegerSize();

}

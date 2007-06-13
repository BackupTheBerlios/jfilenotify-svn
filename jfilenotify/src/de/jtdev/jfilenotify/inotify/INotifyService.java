package de.jtdev.jfilenotify.inotify;

import de.jtdev.jfilenotify.FileNotifyException;
import de.jtdev.jfilenotify.FileNotifyListener;
import de.jtdev.jfilenotify.FileNotifyService;

/**
 * This class implements a FileNotifyService with inotify as its backend.
 * It uses a thread to constantly read incomming events from an inotify
 * instance and passes them to the corresponding listeners.
 *
 * It uses the library "jfilenotify" to access the inotify C interface.
 */
public class INotifyService extends Thread implements FileNotifyService {

	static {
		System.loadLibrary("jfilenotify");
	}

	/**
	 * The file descriptor to the native allocated inotify instance
	 */
	private int fileDescriptor;

	/**
	 * If the service is disposed he will no longer acceppt that new Listeners
	 * will be added.
	 */
	private boolean isDisposed = false;

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
			String reason = ErrnoMessages.getDescription(-fileDescriptor);
			throw new FileNotifyException("Service could not be creted (" + reason + ")");
		}
		this.start();
	}

	public void addFileNotifyListener(FileNotifyListener listener) throws FileNotifyException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean removeFileNotifyListener(FileNotifyListener listener) throws FileNotifyException {
		throw new UnsupportedOperationException("Not supported yet.");
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
			int ret = releaseINotifyInstance(fileDescriptor); // native call
			if (ret < 0) {
				String reason = ErrnoMessages.getDescription(-ret);
				throw new FileNotifyException("Service could not be disposed (" + reason + ")");
			}
		}
	}

	/**
	 * Creates a native inotify instance and returns its file descriptor.
	 * If an error occurs, this method returns a negative value which is the 
	 * negative errno number.
	 * @return the file descriptor on success. Otherwise the negative errno 
	 *         number.
	 */
	private native int createINotifyInstance();
	
	/**
	 * Releases a native inotify instance and return 0 on success or the 
	 * negative errno number in case of an error.
	 * @param fileDescriptor 
	 *        The file descriptor the native inotify instance that sould be 
	 *        released.
	 * @return 0 on success. Otherwise the negative errno number.
	 */
	private native int releaseINotifyInstance(int fileDescriptor);
	
	private native int addWatch(int fileDescriptor, String fileName, int mask);
	private native int removeWatch(int fileDescriptor, int watchDescriptor);
	private native INotifyEvent[] readEvents(int fileDescriptor);

}

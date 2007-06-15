package de.jtdev.jfilenotify.inotify;

import de.jtdev.jfilenotify.FileNotifyConstants;
import de.jtdev.jfilenotify.FileNotifyListener;
import java.util.LinkedList;
import java.util.List;

/**
 * Since inotify only allows one listener per inode, this group is used to 
 * store listeners that returned the same watch descriptor while added.
 * 
 * This class also provides a mechanism to inform only that listeners that have 
 * been registerd for a particular event type.
 * 
 * @author Tobias Oelgarte
 */
public class ListenerGroup implements Comparable<ListenerGroup> {
	
	private long watchDescriptor;
	private LinkedList<FileNotifyListener> listenerList = new LinkedList<FileNotifyListener>();
	private int combinedMask = 0x00000000;
	private String lastFileName = null;
	
	/**
	 * Creates a new ListenerGroup with its unique watch descriptor.
	 * 
	 * @param watchDescriptor
	 *        the unique watch descriptor of this group
	 */
	public ListenerGroup(long watchDescriptor) {
		this.watchDescriptor = watchDescriptor;
	}
	
	/**
	 * Returns the unique watch descriptor used by this group.
	 * 
	 * @return the unique watch descriptor
	 */
	public long getWatchDescriptor() {
		return watchDescriptor;
	}
	
	/**
	 * Returns the combined mask of all added listeners.
	 * 
	 * @return the combined mask.
	 */
	public int getCombinedMask() {
		return combinedMask;
	}
	
	/**
	 * Returns the file name of the lastest added listener.
	 * 
	 * @return the file name of the lastest added listener
	 */
	public String getLastFileName() {
		return lastFileName;
	}
	
	/**
	 * Adds the listener to this group and updates the most common mask for 
	 * inotify.
	 * 
	 * @param listener
	 *        the listener that will be added to this group
	 */
	public void addListener(FileNotifyListener listener) {
		synchronized (listenerList) {
			// make sure that the same listener can only be once registered
			if (!listenerList.contains(listener))
				listenerList.add(listener);
		
			// TODO Option ONLY_DIRECTORY must be cleared if only one listener is 
			// listening in not only directory mode.
			updateCombinedMask(listener.getMask());
			lastFileName = listener.getFileName();
		}
	}
	
	/**
	 * This method updates the combined mask of all listeners in the right way.
	 * 
	 * @param addedMask
	 *        the mask of the newly added listener
	 */
	private void updateCombinedMask(int addedMask) {
		// enabels a event if it is allready enabled or added
		combinedMask |= addedMask & INotifyEvent.IN_EVENT_MASK;
		// disables a restricting option, if the added mask dont restrict it.
		combinedMask &= addedMask | (~INotifyEvent.IN_OPTION_MASK);
	}
	
	/**
	 * Removes the listener from this group and updates the common mask for 
	 * inotify. Returns true if the group contained the listener, false 
	 * otherwise.
	 * @param listener
	 *        the listener that should be removed
	 * @return true if the listener could be removed, false otherwise
	 */
	public boolean removeListener(FileNotifyListener listener) {
		synchronized (listenerList) {
			boolean removed = listenerList.remove(listener);
			if (removed) {
				recomputeCombinedMask();
			}
			return removed;
		}
	}
	
	/**
	 * Returns true if no listener is stored in this group, false otherwise.
	 * 
	 * @return true is this group is empty
	 */
	public boolean isEmpty() {
		return listenerList.isEmpty();
	}
	
	/**
	 * This method computes the combined mask of all listeners in the 
	 * listenerList. This is more time intensive as updateCombinedMask().
	 * 
	 * @param listenerList
	 *        the list that contains all remaining listener
	 */
	private void recomputeCombinedMask() {
		combinedMask = 0x00000000;
		for (FileNotifyListener f : listenerList) {
			updateCombinedMask(f.getMask());
		}
	}
	
	public void discardAllListeners(INotifyEvent event) {
		int externalMask = INotifyService.exportMask(event.getChangeMask());
		event.setChangeMask(externalMask);
		
		for (FileNotifyListener l : listenerList) {
			l.discarded(event);
		}
		
		listenerList.clear();
	}
	
	public void notifyAllListener(INotifyEvent evt) {
		int externalMask = INotifyService.exportMask(evt.getChangeMask());
		evt.setChangeMask(externalMask);
		synchronized (listenerList) {
			for (FileNotifyListener l : listenerList) {
				int listenerMask = l.getMask() | FileNotifyConstants.UNMOUNTED;
				// only inform listeners that have registered for this type of event
				// unmounted is a special type that can occur anytime.
				// Note: the informal parts will not trigger the listener, 
				// because they are not definied inside the listenerMask.
				if ((listenerMask & externalMask) != 0x00000000) {
					l.notificationRecieved(evt);
				}
			}
		}
	}

	/**
	 * Compares to groups by the number of their watch descriptor.
	 * 
	 * @param g
	 *        the group to compare with
	 * @return the arithmetic diffrence between the  watch descriptors
	 */
    public int compareTo(ListenerGroup g) {
		long diff = this.getWatchDescriptor() - g.getWatchDescriptor();
		return (diff > 0) ? 1 : ((diff == 0) ? 0 : -1);
    }
	
	public boolean equals(Object obj) {
		return obj instanceof ListenerGroup ? ((ListenerGroup)obj).getWatchDescriptor() == this.getWatchDescriptor() : false;
	}
	
}

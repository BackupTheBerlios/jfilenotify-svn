package de.jtdev.jfilenotify.inotify;

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
	
	private int watchDescriptor;
	private LinkedList<FileNotifyListener> listenerList = new LinkedList<FileNotifyListener>();
	private int combinedMask = 0x00000000;
	private String lastFileName = null;
	
	/**
	 * Creates a new ListenerGroup with its unique watch descriptor.
	 * 
	 * @param watchDescriptor
	 *        the unique watch descriptor of this group
	 */
	public ListenerGroup(int watchDescriptor) {
		this.watchDescriptor = watchDescriptor;
	}
	
	/**
	 * Returns the unique watch descriptor used by this group.
	 * 
	 * @return the unique watch descriptor
	 */
	public int getWatchDescriptor() {
		return watchDescriptor;
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
			combinedMask |= listener.getMask();
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
		// TODO implement this, and use it in addListener
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
				recomputeCombinedMask(listenerList);
			}
			return removed;
		}
	}
	
	/**
	 * This method computes the combined mask of all listeners in the 
	 * listenerList. This is more time intensive as updateCombinedMask().
	 * 
	 * @param listenerList
	 *        the list that contains all remaining listener
	 */
	private void recomputeCombinedMask(List<FileNotifyListener> listenerList) {
		// TODO implement this
	}

	/**
	 * Compares to groups by the number of their watch descriptor.
	 * 
	 * @param g
	 *        the group to compare with
	 * @return the arithmetic diffrence between the  watch descriptors
	 */
    public int compareTo(ListenerGroup g) {
		return this.getWatchDescriptor() - g.getWatchDescriptor();
    }
	
}

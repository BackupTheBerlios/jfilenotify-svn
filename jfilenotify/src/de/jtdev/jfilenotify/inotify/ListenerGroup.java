package de.jtdev.jfilenotify.inotify;

import de.jtdev.jfilenotify.FileNotifyListener;
import java.util.LinkedList;

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
		}
		// TODO Option ONLY_DIRECTORY must be cleared if only one listener is 
		// listening in not only directory mode.
		combinedMask |= listener.getMask();
		lastFileName = listener.getFileName();
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

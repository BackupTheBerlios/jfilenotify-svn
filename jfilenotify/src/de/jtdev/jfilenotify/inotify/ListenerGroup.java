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
public class ListenerGroup {
	
	private int watchDescriptor;
	private LinkedList<FileNotifyListener> listenerList = new LinkedList<FileNotifyListener>();
	private int combinedMask = 0x00000000;
	private String lastFileName = null;
	
	public ListenerGroup(int watchDescriptor) {
		this.watchDescriptor = watchDescriptor;
	}
	
	
	
}

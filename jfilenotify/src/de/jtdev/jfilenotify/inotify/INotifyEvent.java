package de.jtdev.jfilenotify.inotify;

import de.jtdev.jfilenotify.FileNotifyEvent;

public class INotifyEvent implements FileNotifyEvent {
	
	// Supported events suitable for MASK parameter of INOTIFY_ADD_WATCH.
	public static final int IN_ACCESS        = 0x00000001; // File was accessed.
	public static final int IN_MODIFY        = 0x00000002; // File was modified.
	public static final int IN_ATTRIB        = 0x00000004; // Metadata changed.
	public static final int IN_CLOSE_WRITE   = 0x00000008; // Writtable file was closed.
	public static final int IN_CLOSE_NOWRITE = 0x00000010; // Unwrittable file closed.
	public static final int IN_CLOSE         = (IN_CLOSE_WRITE | IN_CLOSE_NOWRITE); // Close.
	public static final int IN_OPEN          = 0x00000020; // File was opened.
	public static final int IN_MOVED_FROM    = 0x00000040; // File was moved from X.
	public static final int IN_MOVED_TO      = 0x00000080; // File was moved to Y.
	public static final int IN_MOVE          = (IN_MOVED_FROM | IN_MOVED_TO); // Moves.
	public static final int IN_CREATE        = 0x00000100; // Subfile was created.
	public static final int IN_DELETE        = 0x00000200; // Subfile was deleted.
	public static final int IN_DELETE_SELF   = 0x00000400; // Self was deleted.
	public static final int IN_MOVE_SELF     = 0x00000800; // Self was moved.

	// Events sent by the kernel.
	public static final int IN_UNMOUNT       = 0x00002000; // Backing fs was unmounted.
	public static final int IN_Q_OVERFLOW    = 0x00004000; // Event queued overflowed.
	public static final int IN_IGNORED       = 0x00008000; // File was ignored.

	// Special flags.
	public static final int IN_ONLYDIR       = 0x01000000; // Only watch the path if it is a directory.
	public static final int IN_DONT_FOLLOW   = 0x02000000; // Do not follow a sym link.
	public static final int IN_MASK_ADD      = 0x20000000; // Add to the mask of an already existing watch.
	public static final int IN_ISDIR         = 0x40000000; // Event occurred against dir.
	public static final int IN_ONESHOT       = 0x80000000; // Only send event once. 
	
	private int watchDescriptor;
	private int cookie;
	private int mask;
	private String fileName;
	
	public INotifyEvent() {
	}
	
	public int getWatchDescriptor() {
		return watchDescriptor;
	}
	
	public int getCookie() {
		return cookie;
	}
	
	public int getChangeMask() {
		return mask;
	}
	
	public void setChangeMask(int newMask) {
		this.mask = newMask;
	}
	
	public String getChangedFileName() {
		return fileName;
	}

	private static final int NOT_IGNORE_MASK = 0x0007fff;
	
	/**
	 * Returns true if this event is an ignore event that forces the service to 
	 * automatically remove the listener. False in any other case.
	 *
	 * TODO: This method uses a hack to differ between the maybe faulty event 
	 * caused by IN_MOVED_TO. (this bug appeared under ubuntu 7.04)
	 *
	 * @return true if event is ignore event
	 */
	protected boolean isIgnoreEvent() {
		if ((mask & IN_IGNORED) == 0x00000000)
			return false; // mask not set
		if ((mask & NOT_IGNORE_MASK) != 0x00000000)
			return false; // other options set (secure hack)
		return true;
	}
	
	public String toString() {
		return "INotifyEvent: wd="+watchDescriptor+" cookie="+cookie+" mask="+Integer.toHexString(mask)+" filename="+fileName;
	}
}

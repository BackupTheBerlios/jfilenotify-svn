package de.jtdev.jfilenotify;

/**
 * TODO: This is no Javadoc, this is just developer information!
 * This constants are currently very close related to inotify, but not all
 * implementations can provide such fine notification. This should be reduced 
 * to the most common case
 */
public interface FileNotifyConstants {

	public static final int OPENED               = 0x00000001;
	public static final int ACCESSED             = 0x00000002;
	public static final int MODIFIED             = 0x00000004;
	public static final int CLOSED_WRITEABLE     = 0x00000008;
	public static final int CLOSED_NOT_WRITEABLE = 0x00000010;
	public static final int ATTRIBUTES_CHANGED   = 0x00000020;

	public static final int SUBFILE_CREATED      = 0x00000100;
	public static final int SUBFILE_DELETED      = 0x00000200;
	public static final int MOVED_FROM           = 0x00000400;
	public static final int MOVED_TO             = 0x00000800;
	public static final int SELF_DELETED         = 0x00001000;
	public static final int SELF_MOVED           = 0x00002000;
	public static final int UNMOUNTED            = 0x00004000;

	public static final int ONLY_DIRECTORY       = 0x00010000;
	public static final int DONT_FOLLOW          = 0x00020000;

}

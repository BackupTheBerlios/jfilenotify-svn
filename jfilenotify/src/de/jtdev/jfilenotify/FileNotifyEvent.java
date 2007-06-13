package de.jtdev.jfilenotify;

/**
 * An event that indicates that a file/directory has accessed or modified. What
 * exactly has changed can be found out by testing the mask
 * ({@link FileNotifyEvent#getChangeMask() getChangeMask()}) against the mask 
 * constants defined in {@link FileNotifyConstants}.
 *
 * <p>For events that occured to a file/directory under directory that is
 * monitored, the name of the modified file/directory can be obtained by
 * calling {@link FileNotifyEvent#getChangedFileName() getChangedFileName()}.
 * If the name is {@code null} the monitored file/directory itself is affected.
 *
 * @author Tobias Oelgarte
 */
public interface FileNotifyEvent {

	/**
	 * Returns the mask of this event. The mask provides information about
	 * what has changed. Use the mask constants defined in
	 * {@link FileNotifyConstants} to test it against the returned mask.
	 *
	 * @return The mask providing information about what has changed.
	 */
	public int getChangeMask();

	/**
	 * Returns the relative name of the file/directory that has changed. If the
	 * change affects the monitored file/directory itself, then {@code null}
	 * will be returned.
	 *
	 * @return the ralitve name to the subfile/subdirectory that has changed or
	 *         {@code null} if the change affects the file/directory itself.
	 */
	public String getChangedFileName();

}

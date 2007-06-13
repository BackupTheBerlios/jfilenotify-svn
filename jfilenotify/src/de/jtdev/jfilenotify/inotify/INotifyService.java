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
public class INotifyService implements FileNotifyService {
	
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
	 * resources.
	 */
	public INotifyService() {
	}
	
	public void addFileNotifyListener(FileNotifyListener listener) throws FileNotifyException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public boolean removeFileNotifyListener(FileNotifyListener listener) throws FileNotifyException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public void dispose() throws FileNotifyException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}

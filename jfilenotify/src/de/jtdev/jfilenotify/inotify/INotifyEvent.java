package de.jtdev.jfilenotify.inotify;

public class INotifyEvent {
	
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
	
	public int getMask() {
		return mask;
	}
	
	public String getFileName() {
		return fileName;
	}

}

package de.jtdev.jfilenotify;

public abstract class FileNotifyListener {
	
	private String fileName;
	private int mask;
	
	public FileNotifyListener(String fileName, int mask) {
		this.fileName = fileName;
		this.mask = mask;
	}

	public final String getFileName() {
		return fileName;
	}
	
	public final int getMask() {
		return mask;
	}

	public abstract void notificationRecieved(FileNotifyEvent event);
	
	public abstract void discarded(FileNotifyEvent event);
	
}

package de.jtdev.jfilenotify;

public interface FileNotifyEvent {
	
	public int getChangeMask();
	
	public String getChangedFileName();
	
}

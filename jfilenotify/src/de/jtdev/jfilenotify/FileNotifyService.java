package de.jtdev.jfilenotify;

public interface FileNotifyService {
	
	public void addFileNotifyListener(FileNotifyListener listener) throws FileNotifyException;
	
	public boolean removeFileNotifyListener(FileNotifyListener listener) throws FileNotifyException;
	
	public void dispose() throws FileNotifyException;
	
}

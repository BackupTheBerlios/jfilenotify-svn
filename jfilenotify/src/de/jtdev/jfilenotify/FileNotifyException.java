package de.jtdev.jfilenotify;

/**
 * <p>Signals that some service of notification is not supported or used in the
 * wrong way.</p>
 *
 * @author  Tobias Oelgarte
 */
public class FileNotifyException extends Exception {
	
	/**
	 * <p>Constructs an {@code FileNotifyException} with {@code null} as its
	 * error detail message.</p>
	 */
	public FileNotifyException() {
		super();
	}
	
	/**
	 * <p>Constructs an {@code FileNotifyException} with the specified detail
	 * message.</p>
	 * 
	 * @param message
	 *        <p>The detail message (which is saved for later retrieval by the
	 *        {@link #getMessage()} method)</p>
	 */
	public FileNotifyException(String message) {
		super(message);
	}
	
	/**
	 * <p>Constructs an {@code FileNotifyException} with the specified detail
	 * message and cause.</p>
	 * 
	 * <p>Note that the detail message associated with {@code cause} is
	 * <i>not</i> automatically incorporated into this exception's detail
	 * message.</p>
	 * 
	 * @param message
	 *        <p>The detail message (which is saved for later retrieval by the
	 *        {@link #getMessage()} method)</p>
	 * @param cause
	 *        <p>The cause (which is saved for later retrieval by the
	 *        {@link #getCause()} method).  (A null value is permitted, and
	 *        indicates that the cause is nonexistent or unknown.)</p>
	 */
	public FileNotifyException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * <p>Constructs an {@code IOException} with the specified cause and a
	 * detail message of {@code (cause==null ? null : cause.toString())}
	 * (which typically contains the class and detail message of {@code cause}).
	 * This constructor is useful for IO exceptions that are little more
	 * than wrappers for other throwables.</p>
	 *
	 * @param cause
	 *        <p>The cause (which is saved for later retrieval by the
	 *        {@link #getCause()} method).  (A null value is permitted, and
	 *        indicates that the cause is nonexistent or unknown.)</p>
	 */
	public FileNotifyException(Throwable cause) {
		super(cause);
	}
	
}

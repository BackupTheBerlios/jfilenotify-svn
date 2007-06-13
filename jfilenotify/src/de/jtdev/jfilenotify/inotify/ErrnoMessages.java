package de.jtdev.jfilenotify.inotify;

public class ErrnoMessages {
	
	public static final int EPERM = 1;
	public static final String EPERM_MESSAGE = "Operation not permitted";
	
	public static final int ENOENT = 2;
	public static final String ENOENT_MESSAGE = "No such file or directory";
	
	public static final int ESRCH = 3;
	public static final String ESRCH_MESSAGE = "No such process";

	public static final int EINTR = 4;
	public static final String EINTR_MESSAGE = "Interrupted system call";

	public static final int EIO = 5;
	public static final String EIO_MESSAGE = "I/O error";
	
	public static final int	ENXIO = 6;
	public static final String ENXIO_MESSAGE = "No such device or address";
	
	public static final int	E2BIG = 7;
	public static final String E2BIG_MESSAGE = "Argument list too long";
	
	public static final int	ENOEXEC = 8;
	public static final String ENOEXEC_MESSAGE = "Exec format error";
	
	public static final int	EBADF = 9;
	public static final String EBADF_MESSAGE = "Bad file number";
	
	public static final int	ECHILD = 10;
	public static final String ECHILD_MESSAGE = "No child processes";
	
	public static final int	EAGAIN = 11;
	public static final String EAGAIN_MESSAGE = "Try again";
	
	public static final int	ENOMEM = 12;
	public static final String ENOMEM_MESSAGE = "Out of memory";
	
	public static final int	EACCES = 13;
	public static final String EACCES_MESSAGE = "Permission denied";
	
	public static final int	EFAULT = 14;
	public static final String EFAULT_MESSAGE = "Bad address";
	
	public static final int	ENOTBLK = 15;
	public static final String ENOTBLK_MESSAGE = "Block device required";
	
	public static final int	EBUSY = 16;
	public static final String EBUSY_MESSAGE = "Device or resource busy";

	public static final int	EEXIST = 17;
	public static final String EEXIST_MESSAGE = "File exists";

	public static final int	EXDEV = 18;
	public static final String EXDEV_MESSAGE = "Cross-device link";

	public static final int	ENODEV = 19;
	public static final String ENODEV_MESSAGE = "No such device";

	public static final int	ENOTDIR = 20;
	public static final String ENOTDIR_MESSAGE = "Not a directory";

	public static final int	EISDIR = 21;
	public static final String EISDIR_MESSAGE = "Is a directory";

	public static final int	EINVAL = 22;
	public static final String EINVAL_MESSAGE = "Invalid argument";

	public static final int	ENFILE = 23;
	public static final String ENFILE_MESSAGE = "File table overflow";

	public static final int	EMFILE = 24;
	public static final String EMFILE_MESSAGE = "Too many open files";

	public static final int	ENOTTY = 25;
	public static final String ENOTTY_MESSAGE = "Not a typewriter";

	public static final int	ETXTBSY = 26;
	public static final String ETXTBSY_MESSAGE = "Text file busy";

	public static final int	EFBIG = 27;
	public static final String EFBIG_MESSAGE = "File too large";

	public static final int	ENOSPC = 28;
	public static final String ENOSPC_MESSAGE = "No space left on device";

	public static final int	ESPIPE = 29;
	public static final String ESPIPE_MESSAGE = "Illegal seek";

	public static final int	EROFS = 30;
	public static final String EROFS_MESSAGE = "Read-only file system";

	public static final int	EMLINK = 31;
	public static final String EMLINK_MESSAGE = "Too many links";

	public static final int	EPIPE = 32;
	public static final String EPIPE_MESSAGE = "Broken pipe";

	public static final int	EDOM = 33;
	public static final String EDOM_MESSAGE = "Math argument out of domain of func";

	public static final int	ERANGE = 34;
	public static final String ERANGE_MESSAGE = "Math result not representable";	
	
	private static final String[] ERRNO_MESSAGES = {
		EPERM_MESSAGE, // 1
		ENOENT_MESSAGE, // 2
		ESRCH_MESSAGE, // 3
		EINTR_MESSAGE, // 4
		EIO_MESSAGE, // 5
		ENXIO_MESSAGE, // 6
		E2BIG_MESSAGE, // 7
		ENOEXEC_MESSAGE, // 8
		EBADF_MESSAGE, // 9
		ECHILD_MESSAGE, // 10
		EAGAIN_MESSAGE, // 11
		ENOMEM_MESSAGE, // 12
		EACCES_MESSAGE, // 13
		EFAULT_MESSAGE, // 14
		ENOTBLK_MESSAGE, // 15
		EBUSY_MESSAGE, // 16
		EEXIST_MESSAGE, // 17
		EXDEV_MESSAGE, // 18
		ENODEV_MESSAGE, // 19
		ENOTDIR_MESSAGE, // 20
		EISDIR_MESSAGE, // 21
		EINVAL_MESSAGE, // 22
		ENFILE_MESSAGE, // 23
		EMFILE_MESSAGE, // 24
		ENOTTY_MESSAGE, // 25
		ETXTBSY_MESSAGE, // 26
		EFBIG_MESSAGE, // 27
		ENOSPC_MESSAGE, // 28
		ESPIPE_MESSAGE, // 29
		EROFS_MESSAGE, // 30
		EMLINK_MESSAGE, // 31
		EPIPE_MESSAGE, // 32
		EDOM_MESSAGE, // 33
		ERANGE_MESSAGE, // 34
	};
	
	private ErrnoMessages() {
	}
	
	public static final String getDescription(int errno) {
		errno -= 1;
		if (errno < 0 || errno >= ERRNO_MESSAGES.length)
			return null;
		return ERRNO_MESSAGES[errno];
	}
	
}

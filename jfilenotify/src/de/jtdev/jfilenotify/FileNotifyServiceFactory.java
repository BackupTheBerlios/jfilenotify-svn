package de.jtdev.jfilenotify;

import de.jtdev.jfilenotify.inotify.INotifyService;

/**
 * This factory delivers a platform specific {@link FileNotifyService} through
 * its {@link #createNotificationService()} method.
 *
 * @author Tobias Oelgarte
 */
public class FileNotifyServiceFactory {
	
	/**
	 * Private constructor that doesnt allow the creation of objects from this
	 * instance. Use {@link #createNotifcationService()} to create a
	 * {@code FileNotifyInstance}
	 */
	private FileNotifyServiceFactory() {
	}
	
	/**
	 * <p>Creates a new {@code FileNotifyService} instance and binding it to
	 * the underlying notification subsystem.</p>
	 *
	 * @return A system depended FileNotifyService instance, that is capable
	 *         to monitor the filesystem.
	 *
	 * @throws FileNotifyException
	 *         <p>If the service could not be registrated for any reason. This
	 *         can happen when an application registers to many
	 *         {@code FileNotifyService} instances without releasing the
	 *         resources of no longer used services, when the subsystem dont
	 *         permit the registration of filesytem notifications or when the
	 *         operating system is not supported.</p>
	 */
	public static FileNotifyService createNotificationService() throws FileNotifyException {
		String osName = System.getProperty("os.name");
		if ("Linux".equals(osName)) {
			// TODO: use Reflection API?
			return new INotifyService();
		}
		throw new FileNotifyException("Sorry, your operating system is not supported.");
	}
	
}

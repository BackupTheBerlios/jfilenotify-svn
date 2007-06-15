#include <sys/inotify.h>
#include <errno.h>
#include "jfilenotify.h"

#define DEBUG_ENABLED

JNIEXPORT jint JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_createINotifyInstance
(JNIEnv *env, jobject caller) {
	int fd = inotify_init();

#ifdef DEBUG_ENABLED
	printf("createINotifyInstance: fd=%i\n", fd);
#endif

	if (fd < 0) return (jint) -errno;
	return (jint) fd;
}

JNIEXPORT jint JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_releaseINotifyInstance
(JNIEnv *env, jobject caller, jint fd) {
	int ret = close(fd);

#ifdef DEBUG_ENABLED
	printf("releaseINotifyInstance: fd=%i, result=%i", fd, ret);
#endif

	if (ret < 0) return (jint) -errno;
	return (jint) ret;
}

JNIEXPORT jint JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_addWatch
(JNIEnv *env, jobject caller, jint fd, jstring fileName, jint mask) {
	jboolean iscopy;
	const char *path = (*env)->GetStringUTFChars(env, fileName, &iscopy);
	
	int ret = inotify_add_watch((int) fd, path, (uint32_t) mask);
	
	(*env)->ReleaseStringUTFChars(env, fileName, path);
	if (ret < 0) return (jint) -errno;
	return (jint) ret;
}

JNIEXPORT jint JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_removeWatch
(JNIEnv *env, jobject caller, jint fd, jint wd) {
	int ret = inotify_rm_watch(fd, wd);
	if (ret < 0) return (jint) -errno;
	return (jint) ret;
}

#define EVENT_SIZE (sizeof(struct inotify_event))
#define BUF_LEN (1024 * (EVENT_SIZE + 16))

char buf[BUF_LEN];

JNIEXPORT jobject JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_readEvents
(JNIEnv *env, jobject caller, jint fd) {
	int len, i = 0;
	len = read (fd, buf, BUF_LEN);
	if (len < 0) {
		if (errno = EINTR)
			;// need to reissue system call
		else
			return NULL; // return null (error)
	}

	if (len == 0) {
		// BUF_LEN too small?
	}

	// creating List
	jclass linkedListClass = (*env)->FindClass(env, "java/util/LinkedList");
	jmethodID linkedListConstructor = (*env)->GetMethodID(env, linkedListClass, "<init>", "()V");
	jobject linkedList = (*env)->NewObject(env, linkedListClass, linkedListConstructor);
	
	jmethodID addMethodID = (*env)->GetMethodID(env, linkedListClass, "add", "(L/java/lang/Object;)Z");

	while (i < len) {
		struct inotify_event *event;
		event = (struct inotify_event *) &buf[i];
		if (event->len > 0) {
			// filename
		}
		i += EVENT_SIZE + event->len;
	}

	(*env)->DeleteLocalRef(env, linkedListClass);

	return linkedList;
}

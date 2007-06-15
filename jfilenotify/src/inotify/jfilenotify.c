#include <sys/inotify.h>
#include <errno.h>
#include <unistd.h>
#include "jfilenotify.h"

#define DEBUG_ENABLED

JNIEXPORT jlong JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_createINotifyInstance
(JNIEnv *env, jobject caller) {
	int fd = inotify_init();

#ifdef DEBUG_ENABLED
	printf("createINotifyInstance: fd=%i\n", fd);
#endif

	if (fd < 0) return (jlong) -errno;
	return (jlong) fd;
}

JNIEXPORT jlong JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_releaseINotifyInstance
(JNIEnv *env, jobject caller, jlong fd) {
	int ret = close(fd);

#ifdef DEBUG_ENABLED
	printf("releaseINotifyInstance: fd=%i, result=%i", fd, ret);
#endif

	if (ret < 0) return (jlong) -errno;
	return (jlong) ret;
}

JNIEXPORT jlong JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_addWatch
(JNIEnv *env, jobject caller, jlong fd, jstring fileName, jint mask) {
	jboolean iscopy;
	const char *path = (*env)->GetStringUTFChars(env, fileName, &iscopy);
	
	int ret = inotify_add_watch((int) fd, path, (uint32_t) mask);
	
	(*env)->ReleaseStringUTFChars(env, fileName, path);
	if (ret < 0) return (jlong) -errno;
	return (jlong) ret;
}

JNIEXPORT jlong JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_removeWatch
(JNIEnv *env, jobject caller, jlong fd, jlong wd) {
	int ret = inotify_rm_watch((int) fd, (int) wd);
	if (ret < 0) return (jlong) -errno;
	return (jlong) ret;
}

JNIEXPORT jbyteArray JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_readEventData
(JNIEnv *env, jobject caller, jlong fd) {
	size_t eventSize = sizeof(struct inotify_event) + 16;
	size_t bufferSize = eventSize * 1000;
    char buffer[bufferSize];
    
	ssize_t bytesRead = read((int) fd, &buffer, bufferSize);
	
	jbyteArray array = NULL;
	if (bytesRead > 0) {
		array = (*env)->NewByteArray(env, (jsize) bytesRead);
		(*env)->SetByteArrayRegion(env, array, (jsize) 0, (jsize) bytesRead, (jbyte*) buffer);
	} else if (bytesRead == 0) {
		array = (*env)->NewByteArray(env, (jsize) 0);
	}
	return array;
}

JNIEXPORT jint JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_getIntegerSize
(JNIEnv *env, jobject caller) {
	return sizeof(int); // 4 on 32 bit systems, 8 on 64 bit systems.
}

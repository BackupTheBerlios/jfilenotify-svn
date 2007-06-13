#include <sys/inotify.h>
#include <errno.h>
#include "jfilenotify.h"

JNIEXPORT jint JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_createINotifyInstance
(JNIEnv *env, jobject value) {
	int fd = inotify_init();
	if (fd < 0) return (jint) -errno;
	return (jint) fd;
}

JNIEXPORT jint JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_releaseINotifyInstance
(JNIEnv *env, jobject value, jint fd) {
	int ret = close(fd);
	if (ret < 0) return (jint) -errno;
	return (jint) ret;
}


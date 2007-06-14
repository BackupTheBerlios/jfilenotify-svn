/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class de_jtdev_jfilenotify_inotify_INotifyService */

#ifndef _Included_de_jtdev_jfilenotify_inotify_INotifyService
#define _Included_de_jtdev_jfilenotify_inotify_INotifyService
#ifdef __cplusplus
extern "C" {
#endif
/* Inaccessible static: threadInitNumber */
/* Inaccessible static: threadSeqNumber */
#undef de_jtdev_jfilenotify_inotify_INotifyService_MIN_PRIORITY
#define de_jtdev_jfilenotify_inotify_INotifyService_MIN_PRIORITY 1L
#undef de_jtdev_jfilenotify_inotify_INotifyService_NORM_PRIORITY
#define de_jtdev_jfilenotify_inotify_INotifyService_NORM_PRIORITY 5L
#undef de_jtdev_jfilenotify_inotify_INotifyService_MAX_PRIORITY
#define de_jtdev_jfilenotify_inotify_INotifyService_MAX_PRIORITY 10L
/* Inaccessible static: EMPTY_STACK_TRACE */
/* Inaccessible static: SUBCLASS_IMPLEMENTATION_PERMISSION */
/* Inaccessible static: subclassAudits */
/* Inaccessible static: defaultUncaughtExceptionHandler */
/*
 * Class:     de_jtdev_jfilenotify_inotify_INotifyService
 * Method:    createINotifyInstance
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_createINotifyInstance
  (JNIEnv *, jobject);

/*
 * Class:     de_jtdev_jfilenotify_inotify_INotifyService
 * Method:    releaseINotifyInstance
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_releaseINotifyInstance
  (JNIEnv *, jobject, jint);

/*
 * Class:     de_jtdev_jfilenotify_inotify_INotifyService
 * Method:    addWatch
 * Signature: (ILjava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_addWatch
  (JNIEnv *, jobject, jint, jstring, jint);

/*
 * Class:     de_jtdev_jfilenotify_inotify_INotifyService
 * Method:    removeWatch
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_removeWatch
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     de_jtdev_jfilenotify_inotify_INotifyService
 * Method:    readEvents
 * Signature: (I)[Lde/jtdev/jfilenotify/inotify/INotifyEvent;
 */
JNIEXPORT jobjectArray JNICALL Java_de_jtdev_jfilenotify_inotify_INotifyService_readEvents
  (JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif

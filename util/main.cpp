#include "Timer.h"
#include <windows.h>

static jlong frequency;

JNIEXPORT void JNICALL Java_engine_util_Timer_init
(JNIEnv *env, jclass cls) {
	LARGE_INTEGER f;
	::QueryPerformanceFrequency(&f);
	frequency = f.QuadPart;
}

JNIEXPORT jdouble JNICALL Java_engine_util_Timer_getNativeTime(JNIEnv *env, jclass cls) {
	LARGE_INTEGER time;
	::QueryPerformanceCounter(&time);
	return time.QuadPart / (double)frequency;
}

JNIEXPORT jlong JNICALL Java_engine_util_Timer_getNativeResolution
(JNIEnv *env, jclass cls) {
	return frequency;
}

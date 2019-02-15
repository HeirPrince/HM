package nasaaty.com.hm.utils;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

public class DemoObserver implements LifecycleObserver {

	private String LOG_TAG = "DemoObserver";

	@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
	public void onResume() {
		Log.i(LOG_TAG, "onResume");
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
	public void onPause() {
		Log.i(LOG_TAG, "onPause");
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
	public void onCreate() {
		Log.i(LOG_TAG, "onCreate");
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	public void onStart() {
		Log.i(LOG_TAG, "onStart");
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
	public void onStop() {
		Log.i(LOG_TAG, "onStop");
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
	public void onDestroy() {
		Log.i(LOG_TAG, "onDestroy");
	}

}

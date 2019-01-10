package nasaaty.com.hm.utils;

import android.arch.lifecycle.LiveData;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import javax.annotation.Nullable;

public class UserQLiveData extends LiveData<DocumentSnapshot> {

	private static final String TAG = "uQLvData";
	private final Handler handler = new Handler();
	private DocumentReference documentReference;
	private UserQLiveData.MyEventListener listener = new UserQLiveData.MyEventListener();
	private ListenerRegistration listenerRegistration;
	private boolean listenerRemovePending = false;
	private final Runnable removeListener = new Runnable() {
		@Override
		public void run() {
			if (listenerRegistration != null){
				listenerRegistration.remove();
			}
			listenerRemovePending = false;
		}
	};

	public UserQLiveData(DocumentReference documentReference) {
		this.documentReference = documentReference;
	}

	@Override
	protected void onActive() {
		super.onActive();
		if (listenerRemovePending)
			handler.removeCallbacks(removeListener);
		else
		if (listenerRegistration == null)
			listenerRegistration = documentReference.addSnapshotListener(listener);
		listenerRemovePending = false;

	}

	@Override
	protected void onInactive() {
		super.onInactive();

		Log.d(TAG, "onInactive: onInactive()");


		// Listener removal is schedule on a two second delay
		// This is to save on counts against the quota or the bill of Firebase :)
		handler.postDelayed(removeListener, 2000);
		listenerRemovePending = true;

	}

	private class MyEventListener implements EventListener<DocumentSnapshot>{

		@Override
		public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
			if (e != null){
				Log.e(TAG, "Can't listen to doc snapshot: " + documentSnapshot + ":::" + e.getMessage());
			}

			setValue(documentSnapshot);
		}
	}

}

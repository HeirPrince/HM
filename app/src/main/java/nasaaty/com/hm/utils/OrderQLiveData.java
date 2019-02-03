package nasaaty.com.hm.utils;

import android.arch.lifecycle.LiveData;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class OrderQLiveData extends LiveData<DocumentSnapshot> {

	private static final String TAG = "uQLvData";
	private final Handler handler = new Handler();
	private CollectionReference collectionReference;
	private MyEventListener listener = new MyEventListener();
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

	@Override
	protected void onActive() {
		super.onActive();
		if (listenerRemovePending)
			handler.removeCallbacks(removeListener);
		else
		if (listenerRegistration == null)
			listenerRegistration = collectionReference.addSnapshotListener(listener);
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

	public OrderQLiveData(CollectionReference collectionReference) {
		this.collectionReference = collectionReference;
	}

	private class MyEventListener implements EventListener<QuerySnapshot> {
		@Override
		public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
			if (queryDocumentSnapshots != null) {
				for (DocumentSnapshot snapshot : queryDocumentSnapshots){
					if (snapshot != null){
						setValue(snapshot);
					}
				}
			}

		}
	}
}

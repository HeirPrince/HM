package nasaaty.com.hm.utils;

import android.arch.lifecycle.LiveData;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class ProductQLiveData extends LiveData<DocumentSnapshot> {

	private static final String TAG = "uQLvData";
	private final Handler handler = new Handler();
	private Query query;
	private CollectionReference collectionReference;
	private ProductQLiveData.MyEventListener listener = new ProductQLiveData.MyEventListener();
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
			listenerRegistration = query.addSnapshotListener(listener);
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

	public ProductQLiveData(Query query) {
		this.query = query;
	}

	private class MyEventListener implements EventListener<QuerySnapshot> {

		@Override
		public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
			for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
				if (snapshot != null){
					setValue(snapshot);
				}
			}
		}
	}
}

package nasaaty.com.hm.utils;

import android.arch.lifecycle.LiveData;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseQueryLiveData extends LiveData<QuerySnapshot> {

private static final String TAG = "FirebaseQueryLiveData";
private final Handler handler = new Handler();
private FirebaseQueryLiveData.MyValueEventListener listener = new FirebaseQueryLiveData.MyValueEventListener();
private ListenerRegistration listenerRegistration;
private Query query;
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

public FirebaseQueryLiveData(Query query) {
		this.query = query;
		}

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

public class MyValueEventListener implements EventListener<QuerySnapshot> {
	@Override
	public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
		if (e != null){
			Log.e(TAG, "Can't listen to doc snapshots: " + queryDocumentSnapshots + ":::" + e.getMessage());
			return;
		}
		setValue(queryDocumentSnapshots);
	}
}
}

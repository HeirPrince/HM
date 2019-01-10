package nasaaty.com.hm.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import nasaaty.com.hm.model.User;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.utils.UserQLiveData;

public class UserVModel extends AndroidViewModel {

	FirebaseAuth firebaseAuth;
	FirebaseUser firebaseUser;
	FirebaseFirestore firestore;
	private HahaDB hahaDB;
	private UserQLiveData liveData;



	public UserVModel(@NonNull Application application) {
		super(application);
		hahaDB = HahaDB.getInstance(this.getApplication());
		this.firestore = FirebaseFirestore.getInstance();
		this.firebaseAuth = FirebaseAuth.getInstance();
		this.firebaseUser = firebaseAuth.getCurrentUser();
	}

	public void insertUser(User user, onUserSaved callback){
		DocumentReference userRef = firestore.collection("users").document(user.getUid());
		new insertUserAsync(hahaDB, userRef, callback).execute(user);
	}

	public LiveData<DocumentSnapshot> getUserDetails(String uid) {
		DocumentReference userRef = firestore.collection("users").document(uid);
		this.liveData = new UserQLiveData(userRef);
		return liveData;
	}

	//TODO comes from sign up/sign in
	public interface onUserSaved{
		void done(Boolean yes);
	}

	private static class insertUserAsync extends AsyncTask<User, Void, Void> {

		private HahaDB hahaDBase;
		private onUserSaved call;
		private DocumentReference userRef;

		public insertUserAsync(HahaDB hahaDB, DocumentReference userRef, onUserSaved callback) {
			this.userRef = userRef;
			hahaDBase = hahaDB;
			call = callback;
		}

		@Override
		protected Void doInBackground(final User... users) {
			if (users[0] != null){
				hahaDBase.userEntity().addUser(users[0]);
				userRef.set(users[0])
						.addOnCompleteListener(new OnCompleteListener<Void>() {
							@Override
							public void onComplete(@NonNull Task<Void> task) {
								if (task.isSuccessful()){
									call.done(true);
								}else {
									call.done(false);
								}
							}
						});
			}
			return null;
		}
	}
}

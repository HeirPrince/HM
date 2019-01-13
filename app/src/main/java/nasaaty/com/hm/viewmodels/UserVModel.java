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

	public void insertUser(final User user, final onUserSaved callback){
		DocumentReference userRef = firestore.collection("users").document(user.getUid());
		userRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				if (task.isSuccessful()){
					callback.done(true);
					new insertUserAsync(hahaDB).execute(user);
				}else {
					callback.done(false);
				}
			}
		});
	}

	public LiveData<DocumentSnapshot> getUserDetails(String uid) {
		DocumentReference userRef = firestore.collection("users").document(uid);
		liveData = new UserQLiveData(userRef);
		return liveData;
	}

	//TODO comes from sign up/sign in
	public interface onUserSaved{
		void done(Boolean yes);
	}

	private static class insertUserAsync extends AsyncTask<User, Void, Void> {

		private HahaDB hahaDBase;

		public insertUserAsync(HahaDB hahaDB) {
			hahaDBase = hahaDB;
		}

		@Override
		protected Void doInBackground(final User... users) {
			if (users[0] != null){
				hahaDBase.userEntity().addUser(users[0]);
			}
			return null;
		}
	}
}

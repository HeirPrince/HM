package nasaaty.com.hm.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

	public void updateUser(String uid, final HashMap map, final onUserSaved callback){
		DocumentReference userRef = firestore.collection("users").document(uid);
		userRef
				.update(map)
				.addOnCompleteListener(new OnCompleteListener() {
					@Override
					public void onComplete(@NonNull Task task) {
						if (task.isSuccessful()){
							new updateUserAsync(hahaDB).execute(map);
							callback.done(true);
						}else {
							callback.done(false);
						}
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						callback.done(false);
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

	private class updateUserAsync extends AsyncTask<HashMap<String, String>, Void, Void> {
		private HahaDB hahaDBase;
		public updateUserAsync(HahaDB hahaDB) {
			hahaDBase = hahaDB;
		}

		@Override
		protected Void doInBackground(HashMap<String, String>... hashMaps) {
			if (!hashMaps[0].isEmpty()){
				HashMap<String, String> map = new HashMap<>();
				int id = Integer.valueOf(map.get("uid"));
				String phone = map.get("phoneNum");
				String photoUrl = map.get("photoUrl");
				String username = map.get("name");
				String email = map.get("email");

				if (TextUtils.isEmpty(username)){
					hahaDBase.userEntity().updateUserPhone(id, username, photoUrl, email);
				}else if (TextUtils.isEmpty(phone)){
					hahaDBase.userEntity().updateUserEmail(id, username, phone);
				}else if (TextUtils.isEmpty(photoUrl)){
					hahaDBase.userEntity().updateUserPhoto(id, photoUrl);
				}
			}
			return null;
		}
	}
}

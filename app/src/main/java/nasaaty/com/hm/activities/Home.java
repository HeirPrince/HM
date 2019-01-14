package nasaaty.com.hm.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.User;
import nasaaty.com.hm.utils.DialogUtilities;
import nasaaty.com.hm.viewmodels.UserVModel;

public class Home extends AppCompatActivity {

	TextView username;
	private FirebaseAuth firebaseAuth;
	private FirebaseAuth.AuthStateListener listener;
	private FirebaseUser currentUser;
	boolean doubleBackToExitPressedOnce = false;
	private Handler handler = new Handler();
	private UserVModel vModel;
	private DialogUtilities dialogUtilities;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		username = findViewById(R.id.username);
		vModel = ViewModelProviders.of(this).get(UserVModel.class);
		dialogUtilities = new DialogUtilities(this);

		firebaseAuth = FirebaseAuth.getInstance();
		listener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth fireAuth) {
				currentUser = fireAuth.getCurrentUser();

				if (currentUser != null){
					FirebaseUserMetadata metadata = currentUser.getMetadata();
					if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
						// The user is new, show them a fancy intro screen!
						User newUser = new User();
						newUser.setUid(currentUser.getUid());
						newUser.setName(currentUser.getDisplayName());
						newUser.setEmail(currentUser.getEmail());
						newUser.setPhotoUrl(currentUser.getPhotoUrl().toString());
						newUser.setProviderID(currentUser.getProviderId());

						vModel.insertUser(newUser, new UserVModel.onUserSaved() {
							@Override
							public void done(Boolean yes) {
								dialogUtilities.showSuccessDialog("Haha", "Hey "+currentUser.getDisplayName()+" welcome to HaHa");
							}
						});


					} else {
						// This is an existing user, show them a welcome back screen.
						dialogUtilities.showSuccessDialog("Welcome back", "Happy to see u back "+currentUser.getDisplayName());
					}
				}else {
					finish();
					startActivity(new Intent(Home.this, SignIn.class));
				}
			}
		};
	}

	private void updateUserImage(final String uid, final String image) {
		FirebaseFirestore.getInstance().collection("users").document(uid)
				.update("photoUrl", image)
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						Toast.makeText(Home.this, image+" "+uid, Toast.LENGTH_LONG).show();
//						uploadProfileImage(Uri.parse(image), uid);
					}
				});
	}

	public void uploadProfileImage(Uri uri, String uid){
		StorageReference userRef = FirebaseStorage.getInstance().getReference().child(uid);

		userRef.putFile(uri)
				.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
					@Override
					public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
						if (task.isSuccessful()){
							Toast.makeText(Home.this, "image uploaded", Toast.LENGTH_SHORT).show();
						}
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Toast.makeText(Home.this, "upload failed with "+e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
	}


	public void signOut(View view) {

	}

	public void detachListeners(){
		firebaseAuth.removeAuthStateListener(listener);
	}

	public void attachListeners(){
		firebaseAuth.addAuthStateListener(listener);
	}

	@Override
	protected void onStart() {
		super.onStart();
		attachListeners();
	}

	@Override
	protected void onStop() {
		super.onStop();
		detachListeners();
	}

	private final Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			doubleBackToExitPressedOnce = false;
		}
	};

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if (handler != null) { handler.removeCallbacks(mRunnable); }
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

		handler.postDelayed(mRunnable, 2000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.out:
				AuthUI.getInstance()
						.signOut(this)
						.addOnCompleteListener(new OnCompleteListener<Void>() {
							public void onComplete(@NonNull Task<Void> task) {
								// user is now signed out
								startActivity(new Intent(Home.this, BaseActivity.class));
								finish();
							}
						});
				break;
			case R.id.act:
				startActivity(new Intent(Home.this, Account.class));
				break;
		}

		return true;
	}

	public void addP(View view) {
		startActivity(new Intent(Home.this, AddProduct.class));
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}

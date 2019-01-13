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
import nasaaty.com.hm.viewmodels.UserVModel;

public class Home extends AppCompatActivity {

	TextView username;
	private FirebaseAuth firebaseAuth;
	private FirebaseAuth.AuthStateListener listener;
	private FirebaseUser currentUser;
	boolean doubleBackToExitPressedOnce = false;
	private Handler handler = new Handler();
	private UserVModel vModel;
	private AwesomeProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		username = findViewById(R.id.username);
		vModel = ViewModelProviders.of(this).get(UserVModel.class);
		setupDialog();

		firebaseAuth = FirebaseAuth.getInstance();
		listener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth fireAuth) {
				currentUser = fireAuth.getCurrentUser();

				if (currentUser != null){
					if (getIntent().getIntExtra("type", 0) == 1){

						if (!getIntent().getBooleanExtra("provider", false)){
							FirebaseUserMetadata metadata = currentUser.getMetadata();
							if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp())
							{
								final User user = new User();
								user.setName(getIntent().getStringExtra("uname"));
								user.setEmail(currentUser.getEmail());
								user.setUid(currentUser.getUid());
								user.setProviderID("email");

								vModel.insertUser(user, new UserVModel.onUserSaved() {
									@Override
									public void done(Boolean yes) {
										if (yes) {
											updateUserImage(user.getUid(), getIntent().getStringExtra("image"));
											progressDialog.hide();

										}
									}
								});
							}
							else {
								Toast.makeText(Home.this, "Welcome back", Toast.LENGTH_SHORT).show();
							}
						}else {
							for (final UserInfo profile : currentUser.getProviderData()) {
								// Id of the provider (ex: google.com)
								String providerId = profile.getProviderId();

								// UID specific to the provider
								String uid = profile.getUid();

								// Name, email address, and profile photo Url
								String name = profile.getDisplayName();
								String email = profile.getEmail();
								Uri photoURl = profile.getPhotoUrl();

								username.setText(name);

								FirebaseUserMetadata metadata = currentUser.getMetadata();
								if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp())
								{
									final User user = new User();
									user.setName(getIntent().getStringExtra("uname"));
									user.setEmail(email);
									user.setUid(uid);
									user.setProviderID(providerId);
									user.setPhotoUrl(photoURl.toString());

									vModel.insertUser(user, new UserVModel.onUserSaved() {
										@Override
										public void done(Boolean yes) {
											if (yes) {
												updateUserImage(currentUser.getUid(), getIntent().getStringExtra("image"));
												progressDialog.hide();

											}
										}
									});
								}
								else {
									Toast.makeText(Home.this, "Welcome back", Toast.LENGTH_SHORT).show();
								}
								break;
							}
						}



					}else if (getIntent().getIntExtra("type", 0) == 2){
						Toast.makeText(Home.this, "sign up", Toast.LENGTH_SHORT).show();

						FirebaseUserMetadata metadata = currentUser.getMetadata();
						if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp())
						{
							final User user = new User();
							user.setName(getIntent().getStringExtra("uname"));
							user.setEmail(currentUser.getEmail());
							user.setUid(currentUser.getUid());
							user.setProviderID("email");

							vModel.insertUser(user, new UserVModel.onUserSaved() {
								@Override
								public void done(Boolean yes) {
									if (yes) {
										updateUserImage(currentUser.getUid(), getIntent().getStringExtra("image"));

										progressDialog.hide();

									}
								}
							});
						}
						else {
							Toast.makeText(Home.this, "Welcome back", Toast.LENGTH_SHORT).show();
						}

					}else {
						//invalid intent
						Toast.makeText(Home.this, "invalid intent", Toast.LENGTH_SHORT).show();
					}
				}else {
					finish();
					startActivity(new Intent(Home.this, SignIn.class));
				}
			}
		};
	}

	private void updateUserImage(final String uid, final String image) {
		progressDialog.setMessage("Saving Profile Data");
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
							progressDialog.hide();
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

	private void setupDialog() {
		progressDialog = new AwesomeProgressDialog(this);
		progressDialog.setTitle(R.string.app_name)
				.setMessage("Creating account")
				.setColoredCircle(R.color.dialogNoticeBackgroundColor)
				.setDialogIconAndColor(R.drawable.ic_notice, R.color.white)
				.setCancelable(false);
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

		switch (id){
			case R.id.out:
				firebaseAuth.signOut();
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
		progressDialog.hide();
	}
}

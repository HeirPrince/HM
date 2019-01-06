package nasaaty.com.hm.activities;

import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.auth.User;

import nasaaty.com.hm.R;

public class Home extends AppCompatActivity {

	TextView username;
	private FirebaseAuth firebaseAuth;
	private FirebaseAuth.AuthStateListener listener;
	private FirebaseUser curretUser;
	boolean doubleBackToExitPressedOnce = false;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		username = findViewById(R.id.username);

		firebaseAuth = FirebaseAuth.getInstance();
		listener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth fireAuth) {
				curretUser = fireAuth.getCurrentUser();
				if (curretUser != null){
					for (UserInfo profile : curretUser.getProviderData()) {
						// Id of the provider (ex: google.com)
						String providerId = profile.getProviderId();

						// UID specific to the provider
						String uid = profile.getUid();

						// Name, email address, and profile photo Url
						String name = profile.getDisplayName();
						String email = profile.getEmail();
						Uri photoUrl = profile.getPhotoUrl();

						username.setText(name);
					}
				}else {
					finish();
				}
			}
		};
	}

	public void signOut(View view) {
		firebaseAuth.signOut();
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
}

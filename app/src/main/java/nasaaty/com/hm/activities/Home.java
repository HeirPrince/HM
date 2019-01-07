package nasaaty.com.hm.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import nasaaty.com.hm.R;

public class Home extends AppCompatActivity {

	TextView username;
	private FirebaseAuth firebaseAuth;
	private FirebaseAuth.AuthStateListener listener;
	private FirebaseUser currentUser;
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
				currentUser = fireAuth.getCurrentUser();
				if (currentUser != null){
					for (UserInfo profile : currentUser.getProviderData()) {
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
}

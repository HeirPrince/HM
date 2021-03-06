package nasaaty.com.hm.activities;

import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.franmontiel.fullscreendialog.FullScreenDialogFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;

import nasaaty.com.hm.R;
import nasaaty.com.hm.fragments.SignUpFragment;
import nasaaty.com.hm.model.User;
import nasaaty.com.hm.viewmodels.UserVModel;

public class SignIn extends AppCompatActivity implements
		GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
		View.OnClickListener, FullScreenDialogFragment.OnConfirmListener,
		FullScreenDialogFragment.OnDiscardListener {

	private static final int SIGNED_IN = 0;
	private static final int STATE_SIGNING_IN = 1;
	private static final int STATE_IN_PROGRESS = 2;
	private static final int RC_SIGN_IN = 0;


	private GoogleApiClient mGoogleApiClient;
	private FirebaseAuth firebaseAuth;
	private FirebaseAuth.AuthStateListener listener;
	private FirebaseUser firebaseUser;
	private UserVModel userVModel;
	EditText input_mail, input_pwd;
	private int mSignInProgress;
	private PendingIntent mSignInIntent;

	private SignInButton mSignInButton;
	private FullScreenDialogFragment dialogFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);

		input_mail = findViewById(R.id.input_email);
		input_pwd = findViewById(R.id.input_password);
		mSignInButton = findViewById(R.id.btnGgl);
		mSignInButton.setOnClickListener(this);
		userVModel = ViewModelProviders.of(this).get(UserVModel.class);

		firebaseAuth = FirebaseAuth.getInstance();
		
		listener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth fireAuth) {
				firebaseUser = fireAuth.getCurrentUser();
				if (firebaseUser != null){
					finish();
					Intent i = new Intent(SignIn.this, Home.class);
					i.putExtra("type", 1);
					startActivity(i);
				}else {
					Toast.makeText(SignIn.this, "sign in or create account", Toast.LENGTH_SHORT).show();
				}
			}
		};

		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();

		mGoogleApiClient = buildGoogleApiClient(gso);
	}

	private GoogleApiClient buildGoogleApiClient(GoogleSignInOptions gso) {
		return new GoogleApiClient.Builder(this)
				.enableAutoManage(this, this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.addScope(new Scope("email"))
				.build();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
		firebaseAuth.addAuthStateListener(listener);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mGoogleApiClient.disconnect();
		firebaseAuth.removeAuthStateListener(listener);
	}

	public void signUp(View view) {

		final Bundle args = new Bundle();
		args.putString(SignUpFragment.EXTRA_UID, "jj");

		dialogFragment = new FullScreenDialogFragment.Builder(this)
				.setTitle(R.string.dialog_title)
				.setConfirmButton(R.string.dialog_positive_button)
				.setOnConfirmListener(this)
				.setOnDiscardListener(this)
				.setContent(SignUpFragment.class, args)
				.build();

		dialogFragment.show(getSupportFragmentManager(), "tg");

	}

	public void signIn(View view) {

		String email = input_mail.getText().toString();
		String password = input_pwd.getText().toString();

		firebaseAuth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						Toast.makeText(SignIn.this, "Welcome back again", Toast.LENGTH_SHORT).show();
						finish();
						Intent i = new Intent(SignIn.this, Home.class);
						i.putExtra("type", 1);
						i.putExtra("provider", false);
						startActivity(i);
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.btnGgl){
			Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
			startActivityForResult(intent, RC_SIGN_IN);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent();
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			if (result.isSuccess()) {
				// successful -> authenticate with Firebase
				GoogleSignInAccount account = result.getSignInAccount();
				firebaseAuthWithGoogle(account);
			} else {
				// failed -> update UI
				Toast.makeText(getApplicationContext(), "SignIn: failed!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
		AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
		firebaseAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success
							finish();
							Intent i = new Intent(SignIn.this, Home.class);
							i.putExtra("type", 1);
							i.putExtra("provider", true);
							startActivity(i);
						} else {
							// Sign in fails
							Toast.makeText(getApplicationContext(), "Authentication failed!",
									Toast.LENGTH_SHORT).show();
						}

					}
				});
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Toast.makeText(this, "connection to google servers established", Toast.LENGTH_SHORT).show();
		mSignInButton.setEnabled(true);
	}

	@Override
	public void onConnectionSuspended(int i) {
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		mSignInButton.setEnabled(false);
	}

	@Override
	public void onConfirm(@Nullable Bundle result) {
		
	}

	@Override
	public void onDiscard() {

	}
}

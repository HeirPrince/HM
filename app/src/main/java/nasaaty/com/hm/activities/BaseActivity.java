package nasaaty.com.hm.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import nasaaty.com.hm.BuildConfig;
import nasaaty.com.hm.R;
import nasaaty.com.hm.utils.DialogUtilities;

public class BaseActivity extends AppCompatActivity {

	private static int RC_SIGN_IN = 123;
	DialogUtilities dialogUtilities;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		dialogUtilities = new DialogUtilities(this);

		FirebaseAuth auth = FirebaseAuth.getInstance();
		if (auth.getCurrentUser() != null) {
			// already signed in
			startActivity(new Intent(BaseActivity.this, Home.class));
		} else {
			// not signed in
			startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
					.setIsSmartLockEnabled(!BuildConfig.DEBUG)
					.setAvailableProviders(Arrays.asList(
							new AuthUI.IdpConfig.GoogleBuilder().build(),
							new AuthUI.IdpConfig.EmailBuilder().build(),
							new AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("rw").build()
					))
					.build(), RC_SIGN_IN);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
		if (requestCode == RC_SIGN_IN) {
			IdpResponse response = IdpResponse.fromResultIntent(data);

			// Successfully signed in
			if (resultCode == RESULT_OK) {
				startActivity(new Intent(BaseActivity.this, Home.class));
				finish();
			} else {
				// Sign in failed
				if (response == null) {
					// User pressed back button
					dialogUtilities.showInfoDialog(getResources().getString(R.string.app_name), "Sign in cancelled");
					return;
				}

				if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
					dialogUtilities.showErrorDialog(getResources().getString(R.string.app_name), "Network not found");
					return;
				}

				dialogUtilities.showErrorDialog(getResources().getString(R.string.app_name), "Sign in error, please try again");
			}
		}
	}
}

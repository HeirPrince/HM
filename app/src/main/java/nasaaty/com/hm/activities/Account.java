package nasaaty.com.hm.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import nasaaty.com.hm.R;

public class Account extends AppCompatActivity {

	FirebaseAuth firebaseAuth;
	FirebaseUser firebaseUser;

	TextView username, email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);

		firebaseAuth = FirebaseAuth.getInstance();
		firebaseUser = firebaseAuth.getCurrentUser();

		bindViews();

		if (firebaseUser != null){
			for (UserInfo info : firebaseUser.getProviderData()){
				username.setText(info.getDisplayName());
				email.setText(info.getEmail());
			}
		}
	}

	private void bindViews() {
		username = findViewById(R.id.username);
		email = findViewById(R.id.email);
	}
}

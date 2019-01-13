package nasaaty.com.hm.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import nasaaty.com.hm.R;
import nasaaty.com.hm.model.User;
import nasaaty.com.hm.viewmodels.UserVModel;

public class Account extends AppCompatActivity {

	FirebaseAuth firebaseAuth;
	FirebaseAuth.AuthStateListener listener;
	FirebaseUser firebaseUser;
	UserVModel vModel;

	TextView username, email;
	CircleImageView user_image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);

		firebaseAuth = FirebaseAuth.getInstance();
		bindViews();

		listener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth fireAuth) {
				firebaseUser = fireAuth.getCurrentUser();
				if (firebaseUser != null){
					if (firebaseUser.getDisplayName().contains("")){
						vModel.getUserDetails(firebaseUser.getUid()).observe(Account.this, new Observer<DocumentSnapshot>() {
							@Override
							public void onChanged(@Nullable DocumentSnapshot documentSnapshot) {
								if (documentSnapshot.exists()){
									User user = documentSnapshot.toObject(User.class);
									Toast.makeText(Account.this, user.getName()+" "+user.getEmail(), Toast.LENGTH_SHORT).show();
									setUser(user.getName(), user.getEmail(), user.getPhotoUrl());
								}else {
									Toast.makeText(Account.this, "document not found", Toast.LENGTH_SHORT).show();
								}
							}
						});
					}else {

						for (UserInfo info : firebaseUser.getProviderData()){
							setUser(info.getDisplayName(), info.getEmail(), info.getPhotoUrl().toString());
						}
					}


				}
			}
		};
	}

	public void setUser(String displayName, String mail, String photoUrl) {
		username.setText(displayName);
		email.setText(mail);
		Picasso.get().load(photoUrl).into(user_image);
	}

	private void bindViews() {
		username = findViewById(R.id.username);
		email = findViewById(R.id.email);
		user_image = findViewById(R.id.user_image);

		vModel = ViewModelProviders.of(this).get(UserVModel.class);
	}

	@Override
	protected void onStop() {
		super.onStop();
		firebaseAuth.removeAuthStateListener(listener);
	}

	@Override
	protected void onStart() {
		super.onStart();
		firebaseAuth.addAuthStateListener(listener);
	}
}

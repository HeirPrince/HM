package nasaaty.com.hm.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
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
	FirebaseUser firebaseUser;
	UserVModel vModel;

	TextView username, email;
	CircleImageView user_image;

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
				Picasso.get().load(info.getPhotoUrl()).into(user_image);

				vModel.getUserDetails(info.getUid()).observe(this, new Observer<DocumentSnapshot>() {
					@Override
					public void onChanged(@Nullable DocumentSnapshot documentSnapshot) {
						if (documentSnapshot.exists()){
							User user = documentSnapshot.toObject(User.class);
							Toast.makeText(Account.this, user.getName(), Toast.LENGTH_LONG).show();
						}else {
							Toast.makeText(Account.this, "document not found", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		}
	}

	private void bindViews() {
		username = findViewById(R.id.username);
		email = findViewById(R.id.email);
		user_image = findViewById(R.id.user_image);

		vModel = ViewModelProviders.of(this).get(UserVModel.class);
	}
}

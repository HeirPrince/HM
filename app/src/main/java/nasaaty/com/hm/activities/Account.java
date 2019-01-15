package nasaaty.com.hm.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.hdodenhof.circleimageview.CircleImageView;
import nasaaty.com.hm.R;
import nasaaty.com.hm.fragments.UserFragment;
import nasaaty.com.hm.model.User;
import nasaaty.com.hm.viewmodels.UserVModel;

public class Account extends AppCompatActivity {

	FirebaseAuth firebaseAuth;
	FirebaseAuth.AuthStateListener listener;
	FirebaseUser firebaseUser;
	UserVModel vModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);

		firebaseAuth = FirebaseAuth.getInstance();
		firebaseUser = firebaseAuth.getCurrentUser();
		bindViews();



		listener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth fireAuth) {
//				firebaseUser = fireAuth.getCurrentUser();
//				if (firebaseUser != null){
//					User user = new User();
//					user.setEmail(firebaseUser.getEmail());
//					user.setName(firebaseUser.getDisplayName());
//					user.setPhotoUrl(firebaseUser.getPhotoUrl().toString());
//					user.setUid(firebaseUser.getUid());
//					Toast.makeText(Account.this, user.getName(), Toast.LENGTH_SHORT).show();
//
//					EventBus.getDefault().post(user);
//				} else {
//					Toast.makeText(Account.this, "null", Toast.LENGTH_SHORT).show();
//				}
			}
		};
	}

	interface userDetails{
		void User (User details);
	}

	private void bindViews() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		UserFragment userFragment = new UserFragment();
		fragmentTransaction.add(R.id.prof_container, userFragment);
		

		vModel = ViewModelProviders.of(this).get(UserVModel.class);

		if (firebaseUser != null){
			for (UserInfo info : firebaseUser.getProviderData()){
				User user = new User();
				user.setEmail(info.getEmail());
				user.setName(info.getDisplayName());
				user.setPhotoUrl(info.getPhotoUrl().toString());
				user.setUid(info.getUid());
				user.setPhoneNum(info.getPhoneNumber());
				user.setProviderID(info.getProviderId());

				setUser(user, fragmentTransaction);
				break;
			}


		} else {
			Toast.makeText(Account.this, "null", Toast.LENGTH_SHORT).show();
		}
	}

	private void setUser(User user, FragmentTransaction fragmentTransaction) {
		Bundle bundle = new Bundle();
		bundle.putString("uname", user.getName());
		bundle.putString("mail", user.getEmail());
		bundle.putString("url", user.getPhotoUrl());
		bundle.putString("uid", user.getUid());
		bundle.putString("phone", user.getPhoneNum());
		bundle.putString("provider", user.getProviderID());

		UserFragment myFrag = new UserFragment();
		myFrag.setArguments(bundle);
		fragmentTransaction.replace(R.id.prof_container, myFrag);
		fragmentTransaction.commit();
	}


	public void attachListeners(){
		firebaseAuth.addAuthStateListener(listener);
	}

	public void detachListeners(){
		firebaseAuth.removeAuthStateListener(listener);
	}

	@Override
	protected void onStop() {
		super.onStop();
		attachListeners();
	}

	@Override
	protected void onStart() {
		super.onStart();
		detachListeners();
	}
}

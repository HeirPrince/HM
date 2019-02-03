package nasaaty.com.hm.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
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

	private void bindViews() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		

		vModel = ViewModelProviders.of(this).get(UserVModel.class);

		if (firebaseUser != null){

			vModel.getUserDetails(firebaseUser.getUid()).observe(this, new Observer<DocumentSnapshot>() {
				@Override
				public void onChanged(@Nullable DocumentSnapshot documentSnapshot) {
					User user = documentSnapshot.toObject(User.class);
					if (user != null){
						//get user from db
						UserFragment myFrag = new UserFragment();
						myFrag.setArguments(getBundle(user));
						fragmentTransaction.replace(R.id.prof_container, myFrag);
						fragmentTransaction.commit();

					}else {
						//get user from firebase
						for (final UserInfo info : firebaseUser.getProviderData()){
							final User fUser = new User();
							fUser.setEmail(info.getEmail());
							fUser.setName(info.getDisplayName());

							if (info.getPhotoUrl() == null){
								//search image in db
								vModel.getUserDetails(info.getUid()).observe(Account.this, new Observer<DocumentSnapshot>() {
									@Override
									public void onChanged(@Nullable DocumentSnapshot documentSnapshot) {
										User user1 = documentSnapshot.toObject(User.class);
										if (user1 != null){
											String url = user1.getPhotoUrl();
											fUser.setPhotoUrl(url);
										}else { //not found
											fUser.setPhotoUrl("none");
										}
									}
								});
							}else {
								String url = info.getPhotoUrl().toString();
								fUser.setPhotoUrl(url);
							}

							fUser.setUid(info.getUid());
							fUser.setPhoneNum(info.getPhoneNumber());
							fUser.setProviderID(info.getProviderId());

							UserFragment myFrag = new UserFragment();
							myFrag.setArguments(getBundle(user));
							fragmentTransaction.replace(R.id.prof_container, myFrag);
							fragmentTransaction.commit();

							break;
						}
					}
				}
			});

		} else {
			Toast.makeText(Account.this, "null", Toast.LENGTH_SHORT).show();
		}
	}

	@NonNull
	public Bundle getBundle(User user) {
		Bundle bundle = new Bundle();
		bundle.putString("uname", user.getName());
		bundle.putString("email", user.getEmail());
		bundle.putString("url", user.getPhotoUrl());
		bundle.putString("uid", user.getUid());
		bundle.putString("phone", user.getPhoneNum());
		bundle.putString("provider", user.getProviderID());
		return bundle;
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

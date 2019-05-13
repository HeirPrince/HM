package nasaaty.com.hm.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

import nasaaty.com.hm.R;
import nasaaty.com.hm.adapters.MenuAdapter;
import nasaaty.com.hm.fragments.Cart;
import nasaaty.com.hm.fragments.Explore;
import nasaaty.com.hm.fragments.Favorites;
import nasaaty.com.hm.fragments.MyOrders;
import nasaaty.com.hm.fragments.HahaDelivery;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.model.User;
import nasaaty.com.hm.utils.DialogUtilities;
import nasaaty.com.hm.viewmodels.UserVModel;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class Main extends AppCompatActivity implements DuoMenuView.OnMenuClickListener, MyOrders.sendDetailsToSheet{

	boolean doubleBackToExitPressedOnce = false;
	private final Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			doubleBackToExitPressedOnce = false;
		}
	};
	private MenuAdapter mMenuAdapter;
	private ViewHolder mViewHolder;
	private ArrayList<String> mTitles = new ArrayList<>();
	private DialogUtilities dialogUtilities;
	private FirebaseAuth firebaseAuth;
	private FirebaseAuth.AuthStateListener listener;
	private FirebaseUser currentUser;
	private FirebaseFirestore firestore;
	private Handler handler = new Handler();
	private UserVModel vModel;
	HahaDelivery sheetMethod;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer);
		firestore = FirebaseFirestore.getInstance();
		vModel = ViewModelProviders.of(this).get(UserVModel.class);
		dialogUtilities = new DialogUtilities(this);
		sheetMethod = new HahaDelivery();

		mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));

		// Initialize the views
		mViewHolder = new ViewHolder();

		// Handle toolbar actions
		handleToolbar();

		// Handle menu actions
		handleMenu();

		// Handle drawer actions
		handleDrawer();

		// Show main fragment in container
		goToFragment(new Explore(), false);
		mMenuAdapter.setViewSelected(0, true);
		setTitle(mTitles.get(0));


		firebaseAuth = FirebaseAuth.getInstance();
		listener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth fireAuth) {
				currentUser = fireAuth.getCurrentUser();

				if (currentUser != null) {
					FirebaseUserMetadata metadata = currentUser.getMetadata();
					if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
						// The user is new, show them a fancy intro screen!
						final User newUser = new User();
						newUser.setUid(currentUser.getUid());
						newUser.setName(currentUser.getDisplayName());

						if (currentUser.getPhotoUrl() == null) {
							newUser.setPhotoUrl("");
						} else {
							newUser.setPhotoUrl(currentUser.getPhotoUrl().toString());
						}

						mViewHolder.uname.setText(newUser.getName());

						if (!TextUtils.isEmpty(currentUser.getEmail())) {
							newUser.setEmail(currentUser.getEmail());
						}

						newUser.setProviderID(currentUser.getProviderId());

						vModel.getUserDetails(newUser.getUid()).observe(Main.this, new Observer<DocumentSnapshot>() {
							@Override
							public void onChanged(@Nullable DocumentSnapshot documentSnapshot) {
								User user = documentSnapshot.toObject(User.class);
								if (user == null) {
									//user doesn't exist
									vModel.insertUser(newUser, new UserVModel.onUserSaved() {
										@Override
										public void done(Boolean yes) {
											dialogUtilities.showSuccessDialog("Haha", "Hey " + currentUser.getDisplayName() + " welcome to HaHa");
										}
									});
								} else {
									//user exists do nothing
									return;
								}
							}
						});


					} else {
						// This is an existing user, show them a welcome back screen.
//						dialogUtilities.showSuccessDialog("Welcome back", "Happy to see u back "+currentUser.getDisplayName());
					}
				} else {
					finish();
					startActivity(new Intent(Main.this, SignIn.class));
				}
			}
		};

	}

	private void handleToolbar() {
		setSupportActionBar(mViewHolder.mToolbar);
	}

	private void handleDrawer() {
		DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(Main.this,
				mViewHolder.mDuoDrawerLayout,
				mViewHolder.mToolbar,
				R.string.navigation_drawer_open,
				R.string.navigation_drawer_close);

		mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
		duoDrawerToggle.syncState();

	}

	private void handleMenu() {
		mMenuAdapter = new MenuAdapter(mTitles);

		mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
		mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
	}

	@Override
	public void onFooterClicked() {
		Toast.makeText(this, "onFooterClicked", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onHeaderClicked() {
		Toast.makeText(this, "onHeaderClicked", Toast.LENGTH_SHORT).show();
	}

	private void goToFragment(Fragment fragment, boolean addToBackStack) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.replace(R.id.container, fragment).commit();


	}

	@Override
	public void onOptionClicked(int position, Object objectClicked) {
		// Set the toolbar title
		setTitle(mTitles.get(position));

		// Set the right options selected
		mMenuAdapter.setViewSelected(position, true);

		// Navigate to the right fragment
		switch (position) {

			case 0:
				goToFragment(new Explore(), false);
				break;
			case 1:
				goToFragment(new Cart(), false);
				break;
			case 2:
				goToFragment(new Favorites(), false);
				break;
			case 3:
				goToFragment(new MyOrders(), false);
				break;

			default:
				goToFragment(new Explore(), false);
				break;
		}

		// Close the drawer
		mViewHolder.mDuoDrawerLayout.closeDrawer();
	}

	public void addP() {
		startActivity(new Intent(Main.this, AddProduct.class));
	}

	public void detachListeners() {
		firebaseAuth.removeAuthStateListener(listener);
//		f_adapter.startListening();
	}

	public void attachListeners() {
		firebaseAuth.addAuthStateListener(listener);
//		f_adapter.stopListening();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (handler != null) {
			handler.removeCallbacks(mRunnable);
		}
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

		switch (id) {
			case R.id.out:
				AuthUI.getInstance()
						.signOut(this)
						.addOnCompleteListener(new OnCompleteListener<Void>() {
							public void onComplete(@NonNull Task<Void> task) {
								// user is now signed out
								startActivity(new Intent(Main.this, BaseActivity.class));
								finish();
							}
						});
				break;
			case R.id.act:
				startActivity(new Intent(Main.this, Account.class));
				break;
			case R.id.fav:
				startActivity(new Intent(Main.this, nasaaty.com.hm.activities.Favorites.class));
				break;
			case R.id.cart:
				startActivity(new Intent(Main.this, Orders.class));
				break;
		}

		return true;
	}

	@Override
	public void details(Product product) {

	}

	private class ViewHolder {
		private DuoDrawerLayout mDuoDrawerLayout;
		private DuoMenuView mDuoMenuView;
		private TextView uname;
		private Toolbar mToolbar;

		ViewHolder() {
			mDuoDrawerLayout = findViewById(R.id.drawer);
			mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
			mToolbar = findViewById(R.id.toolbar);
			uname = findViewById(R.id.duo_view_header_text_title);
		}
	}


}


package nasaaty.com.hm.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.ImageFile;
import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.model.User;
import nasaaty.com.hm.utils.DialogUtilities;
import nasaaty.com.hm.utils.StorageRepository;
import nasaaty.com.hm.utils.repos.TransactionRepository;
import nasaaty.com.hm.viewmodels.FavVModel;
import nasaaty.com.hm.viewmodels.OrderVModel;
import nasaaty.com.hm.viewmodels.ProductListVModel;
import nasaaty.com.hm.viewmodels.UserVModel;

public class Home extends AppCompatActivity {

	Toolbar tb;
	private FirebaseAuth firebaseAuth;
	private FirebaseAuth.AuthStateListener listener;
	private FirebaseUser currentUser;
	private FirebaseFirestore firestore;
	boolean doubleBackToExitPressedOnce = false;
	private Handler handler = new Handler();
	private UserVModel vModel;
	private ProductListVModel productListVModel;
	private OrderVModel orderVModel;
	private FavVModel favVModel;
	private DialogUtilities dialogUtilities;
	StorageRepository storageRepository;
	private FirestoreRecyclerAdapter f_adapter;
	private TransactionRepository transactionRepository;

	RecyclerView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		tb = findViewById(R.id.toolbar);
		setSupportActionBar(tb);
		firestore = FirebaseFirestore.getInstance();


		list = findViewById(R.id.product_list);
		storageRepository = new StorageRepository(this);
		vModel = ViewModelProviders.of(this).get(UserVModel.class);
		productListVModel = ViewModelProviders.of(this).get(ProductListVModel.class);
		orderVModel = ViewModelProviders.of(this).get(OrderVModel.class);
		favVModel = ViewModelProviders.of(this).get(FavVModel.class);
		dialogUtilities = new DialogUtilities(this);
		transactionRepository = new TransactionRepository(this);

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

						if (!TextUtils.isEmpty(currentUser.getEmail())) {
							newUser.setEmail(currentUser.getEmail());
						}

						newUser.setProviderID(currentUser.getProviderId());

						vModel.getUserDetails(newUser.getUid()).observe(Home.this, new Observer<DocumentSnapshot>() {
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
									getD();
								} else {
									//user exists do nothing
									getD();
									return;
								}
							}
						});


					} else {
						// This is an existing user, show them a welcome back screen.
//						dialogUtilities.showSuccessDialog("Welcome back", "Happy to see u back "+currentUser.getDisplayName());
						getD();
					}
				} else {
					finish();
					startActivity(new Intent(Home.this, SignIn.class));
				}
			}
		};
	}


	public void getD() {
		list.setLayoutManager(new LinearLayoutManager(this));
		list.setHasFixedSize(true);
		Query first_five = firestore.collection("products");

		FirestoreRecyclerOptions<Product> options =
				new FirestoreRecyclerOptions.Builder<Product>()
						.setQuery(first_five, Product.class)
						.setLifecycleOwner(this)
						.build();

		f_adapter = new FirestoreRecyclerAdapter<Product, productVHolder>(options) {
			@NonNull
			@Override
			public productVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
				View view = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.layout_product_item, parent, false);

				return new productVHolder(view);
			}

			@Override
			protected void onBindViewHolder(@NonNull final productVHolder holder, int position, @NonNull final Product model) {
				holder.label.setText(model.getLabel());
				holder.price.setText(String.format("%s RWF", String.valueOf(model.getPrice())));
				holder.desc.setText(model.getDescription());
				holder.ratingBar.setRating((float) model.getAvgRatings());
				holder.starCount.setText(String.valueOf(model.getAvgRatings()));
				holder.setLikeBtn(model.getPid());
				holder.toggleProgress(true);

				//get default image
				DocumentReference reference = firestore.collection("images").document(model.getPid()).collection("images").document("default");

				reference
						.get()
						.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
							@Override
							public void onSuccess(DocumentSnapshot documentSnapshot) {
								if (documentSnapshot.exists()){
									ImageFile imageFile = documentSnapshot.toObject(ImageFile.class);
									StorageReference defRef = storageRepository.getDefaultIMage(model.getPid(), imageFile.getFileName());
									defRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
										@Override
										public void onSuccess(Uri uri) {
											Picasso.get()
													.load(uri)
													.placeholder(R.drawable.deliverer)
													.into(holder.pro_image);
											holder.toggleProgress(false);
										}

									});
								}else {
									Toast.makeText(Home.this, "error loading image", Toast.LENGTH_SHORT).show();
								}
							}
						});


				holder.add_likes.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						transactionRepository.allowUserLikes(model.getPid());
					}
				});

				holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(Home.this, ProductDetails.class);
						intent.putExtra("pid", model.getPid());
						intent .putExtra("uid", firebaseAuth.getCurrentUser().getUid());
						startActivity(intent);
					}
				});

				holder.plc_order.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Order order = new Order();
						order.setOwner(model.getOwner());
						order.setProduct_id(model.getPid());
						order.setOrder_id(model.getPid());
						orderVModel.insertOrder(order);
					}
				});

			}
		};

		f_adapter.notifyDataSetChanged();
		list.setAdapter(f_adapter);

	}


//	private void attachRecyclerViewAdapter() {
//		final RecyclerView.Adapter adapter = getD();
//
//		// Scroll to bottom on new messages
//		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//			@Override
//			public void onItemRangeInserted(int positionStart, int itemCount) {
//				list.smoothScrollToPosition(0);
//			}
//		});
//
//		list.setAdapter(adapter);
//	}

	class productVHolder extends RecyclerView.ViewHolder {

		TextView label, price, starCount;
		ReadMoreTextView desc;
		Button plc_order;
		ImageButton fav, add_likes;
		ImageView pro_image;
		ProgressBar progressBar;
		AppCompatRatingBar ratingBar;

		public productVHolder(View itemView) {
			super(itemView);
			label = itemView.findViewById(R.id.product_title);
			price = itemView.findViewById(R.id.product_price);
			desc = itemView.findViewById(R.id.product_description);
			plc_order = itemView.findViewById(R.id.plc_order);
			pro_image = itemView.findViewById(R.id.product_image);
			fav = itemView.findViewById(R.id.fav);
			progressBar = itemView.findViewById(R.id.progress);
			add_likes = itemView.findViewById(R.id.btnLike);
			ratingBar = itemView.findViewById(R.id.ratingBar);
			starCount = itemView.findViewById(R.id.count);
		}

		public void setLikeBtn(final String pid) {
			firestore.collection("likes").document(pid).collection(firebaseAuth.getCurrentUser().getUid())
					.addSnapshotListener(new EventListener<QuerySnapshot>() {
						@Override
						public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
							if (e != null) return;

							for (DocumentSnapshot snapshot : queryDocumentSnapshots) {

								if (snapshot.exists()) {
									if (snapshot.get(firebaseAuth.getCurrentUser().getUid()) != null) {
										add_likes.setImageResource(R.drawable.vector_like_selected);
									}else {
										add_likes.setImageResource(R.drawable.vector_like);
									}
								} else {
									add_likes.setImageResource(R.drawable.vector_like);
								}
							}
						}
					});
		}

		public void toggleProgress(Boolean state){
			if (state)
				progressBar.setVisibility(View.VISIBLE);
			else
				progressBar.setVisibility(View.GONE);
		}


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
								startActivity(new Intent(Home.this, BaseActivity.class));
								finish();
							}
						});
				break;
			case R.id.act:
				startActivity(new Intent(Home.this, Account.class));
				break;
			case R.id.fav:
				startActivity(new Intent(Home.this, Favorites.class));
				break;
			case R.id.cart:
				startActivity(new Intent(Home.this, Orders.class));
				break;
		}

		return true;
	}

	public void addP(View view) {
		startActivity(new Intent(Home.this, AddProduct.class));
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}

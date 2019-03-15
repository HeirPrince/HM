package nasaaty.com.hm.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.franmontiel.fullscreendialog.FullScreenDialogFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import nasaaty.com.hm.R;
import nasaaty.com.hm.fragments.RevFragment;
import nasaaty.com.hm.model.ImageFile;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.model.Review;
import nasaaty.com.hm.model.User;
import nasaaty.com.hm.utils.StorageRepository;
import nasaaty.com.hm.utils.repos.TransactionRepository;
import nasaaty.com.hm.viewmodels.OrderVModel;
import nasaaty.com.hm.viewmodels.ProductListVModel;

import static java.lang.String.format;

public class ProductDetails extends AppCompatActivity implements FullScreenDialogFragment.OnConfirmListener,
		FullScreenDialogFragment.OnDiscardListener {

	android.support.v7.widget.Toolbar toolbar;
	TextView p_title, p_desc, p_price, user_name, user_review;
	CircleImageView user_image;
	OrderVModel orderVModel;
	ProductListVModel productListVModel;
	String title, price;
	RatingBar ratingBar, viewedRatingBar;
	TransactionRepository transactionRepository;
	RelativeLayout isUfound;
	LinearLayout notFound;
	FirestoreRecyclerAdapter adapter;
	StorageRepository storageRepository;
	private FullScreenDialogFragment dialogFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_details);
		storageRepository = new StorageRepository(this);

		bindViews();

	}

	private void setViews() {
		String pid = getIntent().getStringExtra("pid");
		transactionRepository = new TransactionRepository(this);

		productListVModel.getProductById(pid).observe(this, new Observer<DocumentSnapshot>() {
			@Override
			public void onChanged(@Nullable DocumentSnapshot documentSnapshot) {
				Product product = documentSnapshot.toObject(Product.class);
				if (product != null){
					title = product.getLabel();
					price = String.valueOf(product.getPrice());

					p_title.setText(product.getLabel());
					p_desc.setText(product.getDescription());
					p_price.setText(format(getString(R.string.RDACurrency), product.getPrice()));
					Objects.requireNonNull(getSupportActionBar()).setTitle("Product Details");
				}else {
					Toast.makeText(ProductDetails.this, "null", Toast.LENGTH_SHORT).show();
				}
			}
		});

		transactionRepository.checkUserReview(getIntent().getStringExtra("uid"), getIntent().getStringExtra("pid"), new TransactionRepository.isUserReviewFound() {
			@Override
			public void isFound(Boolean found, Review review, final Product product, User user) {
				if (found) {

					if (notFound.getVisibility() == View.VISIBLE){
						notFound.setVisibility(View.GONE);
						isUfound.setVisibility(View.VISIBLE);
					}else {
						isUfound.setVisibility(View.VISIBLE);
					}

						viewedRatingBar.setRating((float) product.getAvgRatings());
						user_name.setText(user.getName());
						user_review.setText(review.getReview());
						Picasso.get().load(user.getPhotoUrl()).into(user_image);
				}
				else {

					if (isUfound.getVisibility() == View.VISIBLE){
						isUfound.setVisibility(View.GONE);
						notFound.setVisibility(View.VISIBLE);
					}else {
						notFound.setVisibility(View.VISIBLE);
					}
						ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
							@Override
							public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

								final Bundle args = new Bundle();
								args.putFloat(RevFragment.EXTRA_RATE, v);
								args.putString(RevFragment.EXTRA_UID, getIntent().getStringExtra("uid"));
								args.putString(RevFragment.EXTRA_PID, getIntent().getStringExtra("pid"));

								dialogFragment = new FullScreenDialogFragment.Builder(ProductDetails.this)
										.setTitle("Rate "+p_title.getText())
										.setConfirmButton("POST")
										.setOnConfirmListener(ProductDetails.this)
										.setOnDiscardListener(ProductDetails.this)
										.setContent(RevFragment.class, args)
										.build();

								dialogFragment.show(getSupportFragmentManager(), "tg");
							}
						});
				}
			}
		});

		loadImages(pid);

	}

	private void bindViews() {
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Product Details");

		orderVModel = ViewModelProviders.of(this).get(OrderVModel.class);
		productListVModel = ViewModelProviders.of(this).get(ProductListVModel.class);

		p_title = findViewById(R.id.product_title);
		p_desc = findViewById(R.id.product_description);
		p_price = findViewById(R.id.product_price);
		ratingBar = findViewById(R.id.stars);
		notFound = findViewById(R.id.isUserNotFound);
		isUfound = findViewById(R.id.isUserFound);
		user_name = findViewById(R.id.prof_name);
		user_image = findViewById(R.id.prof_pic);
		viewedRatingBar = findViewById(R.id.u_rating);
		user_review = findViewById(R.id.review);

		setViews();
	}

	public void shareProduct(View view) {
		String message =
				"Hello, check "+title+" on Haha"
				+" for only "+price+
				"RWF. order it at Haha or download the app from PlayStore or AppStore";

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, message);
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, "Haha"));
	}

	@Override
	public void onConfirm(@Nullable Bundle result) {

	}

	@Override
	public void onDiscard() {

	}

	private void loadImages(final String pid) {
	   	RecyclerView list = findViewById(R.id.imList);
		list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		list.setHasFixedSize(true);
		final Query ims = FirebaseFirestore.getInstance().collection("images").document(pid).collection("images").limit(10);

		FirestoreRecyclerOptions<ImageFile> options =
				new FirestoreRecyclerOptions.Builder<ImageFile>()
						.setQuery(ims, ImageFile.class)
						.setLifecycleOwner(this)
						.build();

		adapter = new FirestoreRecyclerAdapter<ImageFile, ImageViewHolder>(options) {
			@NonNull
			@Override
			public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
				View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_image_item_tall, parent, false);
				return new ImageViewHolder(view);
			}

			@Override
			protected void onBindViewHolder(@NonNull final ImageViewHolder holder, int position, @NonNull final ImageFile model) {

				StorageReference reference = storageRepository.getProductImage(pid);
				reference.child(model.getFileName()).getDownloadUrl()
						.addOnSuccessListener(new OnSuccessListener<Uri>() {
							@Override
							public void onSuccess(Uri uri) {
								Toast.makeText(ProductDetails.this, uri.toString(), Toast.LENGTH_SHORT).show();
								Picasso.get().load(uri).into(holder.image);
							}
						});

				holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Toast.makeText(ProductDetails.this, model.getFileName(), Toast.LENGTH_SHORT).show();
					}
				});
			}
		};
		adapter.notifyDataSetChanged();
		list.setAdapter(adapter);
	}

	class ImageViewHolder extends RecyclerView.ViewHolder {

		ImageView image;
		View share;

		public ImageViewHolder(View itemView) {
			super(itemView);
			image = itemView.findViewById(R.id.image);
			share = itemView.findViewById(R.id.share);
		}
	}
}

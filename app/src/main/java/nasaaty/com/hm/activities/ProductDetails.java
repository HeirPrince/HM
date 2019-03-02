package nasaaty.com.hm.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import nasaaty.com.hm.R;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.model.Review;
import nasaaty.com.hm.model.User;
import nasaaty.com.hm.utils.repos.TransactionRepository;
import nasaaty.com.hm.viewmodels.OrderVModel;
import nasaaty.com.hm.viewmodels.ProductListVModel;

import static java.lang.String.format;

public class ProductDetails extends AppCompatActivity {

	android.support.v7.widget.Toolbar toolbar;
	TextView p_title, p_desc, p_price, user_name, user_review;
	EditText review;
	CircleImageView user_image;
	OrderVModel orderVModel;
	ProductListVModel productListVModel;
	String title, price;
	RatingBar ratingBar, viewedRatingBar;
	TransactionRepository transactionRepository;
	RelativeLayout isUfound, notFound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_details);

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
			public void isFound(Boolean found, Review review, Product product, User user) {
				if (found) {
//						notFound.setVisibility(View.GONE);
						isUfound.setVisibility(View.VISIBLE);
						viewedRatingBar.setRating((float) product.getAvgRatings());
						user_name.setText(user.getName());
						user_review.setText(review.getReview());
						Picasso.get().load(user.getPhotoUrl()).into(user_image);
				}
				else {
						notFound.setVisibility(View.VISIBLE);
				}
			}
		});

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
		review = findViewById(R.id.rev);
		isUfound = findViewById(R.id.isUserFound);
		notFound = findViewById(R.id.isUserNotFound);
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

	public void postRev(View view) {
		Float rate = ratingBar.getRating();
		String rev = review.getText().toString();

		Review r = new Review();
		r.setReview(rev);
		r.setUid(getIntent().getStringExtra("uid"));

		transactionRepository.addRating(getIntent().getStringExtra("pid"), r, rate);
	}
}

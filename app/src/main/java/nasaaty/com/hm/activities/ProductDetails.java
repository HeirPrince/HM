package nasaaty.com.hm.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.viewmodels.OrderVModel;
import nasaaty.com.hm.viewmodels.ProductListVModel;

public class ProductDetails extends AppCompatActivity {

	android.support.v7.widget.Toolbar toolbar;
	TextView p_title, p_desc, p_price;

	OrderVModel orderVModel;
	ProductListVModel productListVModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_details);

		bindViews();
		setViews(getIntent().getStringExtra("product_id"));
	}

	private void setViews(String product_id) {

		productListVModel.getProductById(product_id).observe(this, new Observer<DocumentSnapshot>() {
			@Override
			public void onChanged(@Nullable DocumentSnapshot documentSnapshot) {
				Product product = documentSnapshot.toObject(Product.class);
				if (product !=null){
					p_title.setText(product.getLabel());
					p_desc.setText(product.getDescription());
					p_title.setText(product.getPrice()+" RWF");
					getSupportActionBar().setTitle(product.getLabel());
				}else {
					Toast.makeText(ProductDetails.this, "null", Toast.LENGTH_SHORT).show();
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

		p_title = findViewById(R.id.p_title);
		p_desc = findViewById(R.id.p_desc);
		p_price = findViewById(R.id.price);
	}
}

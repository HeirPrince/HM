package nasaaty.com.hm.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.firestore.DocumentSnapshot;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.viewmodels.OrderVModel;
import nasaaty.com.hm.viewmodels.ProductListVModel;
import nasaaty.com.hm.viewmodels.ProductVModel;

public class OrderDetails extends AppCompatActivity {

	android.support.v7.widget.Toolbar toolbar;
	TextView p_title, p_desc, p_price;

	OrderVModel orderVModel;
	ProductListVModel productListVModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_details);

		bindViews();
		setViews(getIntent().getIntExtra("order_id", 0));
	}

	private void setViews(int order_id) {
		Order order = orderVModel.getDetails(order_id);

		productListVModel.getProductById(order.getProduct_id()).observe(this, new Observer<DocumentSnapshot>() {
			@Override
			public void onChanged(@Nullable DocumentSnapshot documentSnapshot) {
				Product product = documentSnapshot.toObject(Product.class);
				p_title.setText(product.getLabel());
				p_desc.setText(product.getDescription());
			}
		});

	}

	private void bindViews() {
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Order Details");

		orderVModel = ViewModelProviders.of(this).get(OrderVModel.class);
		productListVModel = ViewModelProviders.of(this).get(ProductListVModel.class);

		p_title = findViewById(R.id.p_title);
		p_desc = findViewById(R.id.desc);
		p_price = findViewById(R.id.price);
	}
}

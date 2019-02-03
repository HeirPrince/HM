package nasaaty.com.hm.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.viewmodels.ProductVModel;

public class AddProduct extends AppCompatActivity {

	EditText label, desc, price;
	private HahaDB hahaDB;
	private FirebaseAuth auth;
	private ProductVModel vModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_product);

		bindViews();
		hahaDB = HahaDB.getInstance(this);
		auth = FirebaseAuth.getInstance();
		vModel = ViewModelProviders.of(this).get(ProductVModel.class);
	}

	private void bindViews() {
		label = findViewById(R.id.label);
		desc = findViewById(R.id.desc);
		price = findViewById(R.id.price);
	}

	public void addP(View view) {
		String l = label.getText().toString();
		String d = desc.getText().toString();
		int p = Integer.valueOf(price.getText().toString());

		Product product = new Product();
		product.setLabel(l);
		product.setPrice(p);
		product.setDescription(d);
		product.setOwner(auth.getCurrentUser().getUid());

		vModel.insertNew(product);
	}
}

package nasaaty.com.hm.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.adapters.NProductListAdapter;
import nasaaty.com.hm.adapters.ProductListAdapter;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.utils.DialogUtilities;
import nasaaty.com.hm.viewmodels.OrderVModel;
import nasaaty.com.hm.viewmodels.ProductListVModel;

public class ADDPList extends AppCompatActivity {

	private ProductListVModel productListVModel;
	private OrderVModel orderVModel;
	private NProductListAdapter adapter;
	RecyclerView list;
	DialogUtilities dialogUtilities;
	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addplist);
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		productListVModel = ViewModelProviders.of(this).get(ProductListVModel.class);
		orderVModel = ViewModelProviders.of(this).get(OrderVModel.class);
		list = findViewById(R.id.p_list);
		dialogUtilities = new DialogUtilities(this);

		getData();
	}

	private void getData() {
		final List<Product> products = new ArrayList<>();
		list.setLayoutManager(new LinearLayoutManager(this));
		list.setHasFixedSize(true);

		productListVModel.getProducts().observe(this, new Observer<DocumentSnapshot>() {
			@Override
			public void onChanged(@Nullable DocumentSnapshot documentSnapshot) {
				if (documentSnapshot != null) {
					Product product = documentSnapshot.toObject(Product.class);
					products.add(product);
					adapter = new NProductListAdapter(ADDPList.this,products, orderVModel, dialogUtilities);
					list.setAdapter(adapter);
				}
			}
		});
	}
}

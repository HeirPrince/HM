package nasaaty.com.hm.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.adapters.OrderListAdapter;
import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.viewmodels.OrderListVModel;
import nasaaty.com.hm.viewmodels.ProductListVModel;

public class Orders extends AppCompatActivity {

	android.support.v7.widget.Toolbar toolbar;
	RecyclerView order_list;
	ProductListVModel vModel;
	OrderListVModel orderListVModel;
	OrderListAdapter adapter;
	List<Order> orders;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orders);
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("My Orders");

		bindViews();

		getOrders();
	}

	private void bindViews() {
		vModel = ViewModelProviders.of(this).get(ProductListVModel.class);
		orderListVModel = ViewModelProviders.of(this).get(OrderListVModel.class);

		order_list = findViewById(R.id.order_List);
		order_list.setLayoutManager(new LinearLayoutManager(this));
		order_list.setHasFixedSize(true);
		orders = new ArrayList<>();
		adapter = new OrderListAdapter(this, orders, vModel);
		order_list.setAdapter(adapter);
	}

	public void getOrders() {
		orderListVModel.getOrders().observe(this, new Observer<DocumentSnapshot>() {
			@Override
			public void onChanged(@Nullable DocumentSnapshot documentSnapshot) {
				if (documentSnapshot.exists()){
					Order order = documentSnapshot.toObject(Order.class);
					orders.add(order);
					adapter.notifyDataSetChanged();
				}
			}
		});
	}
}

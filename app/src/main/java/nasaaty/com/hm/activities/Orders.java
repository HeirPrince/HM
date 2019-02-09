package nasaaty.com.hm.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.adapters.OrderListAdapter;
import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.utils.DialogUtilities;
import nasaaty.com.hm.viewmodels.OrderListVModel;
import nasaaty.com.hm.viewmodels.OrderVModel;
import nasaaty.com.hm.viewmodels.ProductListVModel;

public class Orders extends AppCompatActivity {

	android.support.v7.widget.Toolbar toolbar;
	RecyclerView order_list;
	ProductListVModel vModel;
	OrderVModel orderVModel;
	OrderListVModel orderListVModel;
	OrderListAdapter adapter;
	DialogUtilities utilities;
	List<Order> orderList;

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
		orderVModel = ViewModelProviders.of(this).get(OrderVModel.class);
		utilities = new DialogUtilities(this);
		orderList = new ArrayList<>();

		order_list = findViewById(R.id.order_List);
		order_list.setLayoutManager(new LinearLayoutManager(this));
		order_list.setHasFixedSize(true);
	}

	public void getOrders() {
		utilities.showProgressDialog("My Cart", "Loading your orders", true);
		orderListVModel.getOrders(this).observe(this, new Observer<List<Order>>() {
			@Override
			public void onChanged(@Nullable List<Order> orders) {
				if (orders != null){
					orderList.addAll(orders);
					adapter = new OrderListAdapter(Orders.this, orderList, vModel, orderVModel);
					adapter.notifyDataSetChanged();
					order_list.setAdapter(adapter);
					utilities.showProgressDialog("", "",false);
				}else {
					utilities.showInfoDialog("Orders", "Your cart is empty");
				}
			}
		});
	}

	public void checkOutOrders(View view) {
		orderVModel.sync(orderList);
		adapter.notifyDataSetChanged();
	}
}

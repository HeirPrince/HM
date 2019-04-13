package nasaaty.com.hm.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.adapters.OrderListAdapter;
import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.utils.DemoObserver;
import nasaaty.com.hm.utils.DialogUtilities;
import nasaaty.com.hm.utils.TimeUts;
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
	List<Product> orderList;
	Button chkout;

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

		getLifecycle().addObserver(new DemoObserver());

		order_list = findViewById(R.id.order_List);
		chkout = findViewById(R.id.chkout);

		chkout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				checkOutOrders();
			}
		});

		order_list.setLayoutManager(new LinearLayoutManager(this));
		order_list.setHasFixedSize(true);
	}

	public void getOrders() {
		utilities.showProgressDialog("My Cart", "Loading your orders", true);
		orderListVModel.getOrders(this).observe(this, new Observer<List<Product>>() {
			@Override
			public void onChanged(@Nullable List<Product> orders) {
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

	public void checkOutOrders() {

		orderListVModel.getOrders(this).observe(this, new Observer<List<Product>>() {
			@Override
			public void onChanged(@Nullable List<Product> products) {
				if (products != null){
					for (Product product : products){
						Order order = new Order();
						order.setCustomer_id(product.getOwner());
						order.setTimeStamp(TimeUts.getTimeStamp());
						order.setProduct(product);
						orderVModel.sync(order);
					}
					adapter.notifyDataSetChanged();
				}else {
					Toast.makeText(Orders.this, "you have not made any orders yet", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.order_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.add:
				startActivity(new Intent(Orders.this, ADDPList.class));
				break;
		}
		return false;
	}
}

package nasaaty.com.hm.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nasaaty.com.hm.CallBacks.SwipeControl;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class Cart extends Fragment implements SwipeControl.SwipeListener {

	CoordinatorLayout cord;
	RecyclerView order_list;
	ProductListVModel vModel;
	OrderVModel orderVModel;
	OrderListVModel orderListVModel;
	OrderListAdapter adapter;
	DialogUtilities utilities;
	List<Product> orderList;
	Button chkout;
	TextView tot_price;

	private View view;
	private boolean add = false;
	private Paint p = new Paint();

	public Cart() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_cart, container, false);

		return bindViews(view);

	}

	private View bindViews(View view) {
		vModel = ViewModelProviders.of(this).get(ProductListVModel.class);
		orderListVModel = ViewModelProviders.of(this).get(OrderListVModel.class);
		orderVModel = ViewModelProviders.of(this).get(OrderVModel.class);
		utilities = new DialogUtilities(getActivity());
		orderList = new ArrayList<>();

		getLifecycle().addObserver(new DemoObserver());

		order_list = view.findViewById(R.id.order_List);
		chkout = view.findViewById(R.id.chkout);
		cord = view.findViewById(R.id.cord);
		tot_price = view.findViewById(R.id.tot_price);

		chkout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				checkOutOrders();
			}
		});

		order_list.setLayoutManager(new LinearLayoutManager(getContext()));
		order_list.setHasFixedSize(true);


		getOrders();

		return view;
	}

	public void getOrders() {
		
		orderListVModel.getOrders(getContext()).observe(this, new Observer<List<Product>>() {
			@Override
			public void onChanged(@Nullable List<Product> orders) {
				if (orders != null){

					orderList = addOrders(orders);

					adapter = new OrderListAdapter(getContext(), orderList, vModel, orderVModel);
					adapter.notifyDataSetChanged();
					order_list.setAdapter(adapter);

					//swipe to delete
					ItemTouchHelper.SimpleCallback simpleCallback = new SwipeControl(0, ItemTouchHelper.LEFT, Cart.this);

					new ItemTouchHelper(simpleCallback).attachToRecyclerView(order_list);
					
				}else {
					utilities.showInfoDialog("Orders", "Your cart is empty");
				}
			}
		});

	}

	private List<Product> addOrders(List<Product> orders) {
		List<Product> products = new ArrayList<>();
		int count = 0, price = 0;

		for (Product product : orders){
			count = count + 1;
			price = price + product.getPrice();
			products.add(product);
		}
		tot_price.setText(String.format("%s RF", price));
		return products;
	}

	public void checkOutOrders() {

		orderListVModel.getOrders(getContext()).observe(this, new Observer<List<Product>>() {
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
					Toast.makeText(getContext(), "you have not made any orders yet", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}


	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
		if (viewHolder instanceof OrderListAdapter.OrderVHolder){
			String name = orderList.get(viewHolder.getAdapterPosition()).getLabel();

			final Product product = orderList.get(viewHolder.getAdapterPosition());
			final int delIndex = viewHolder.getAdapterPosition();

			adapter.removeItem(delIndex);
			Snackbar snackbar = Snackbar.make(cord, name +" removed from cart", Snackbar.LENGTH_LONG);
			snackbar.setAction("UNDO", new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					adapter.addItem(product, delIndex);
				}
			});

			snackbar.setActionTextColor(Color.YELLOW);
			snackbar.show();
		}
	}
}

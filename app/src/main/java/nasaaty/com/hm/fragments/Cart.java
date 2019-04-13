package nasaaty.com.hm.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class Cart extends Fragment {

	RecyclerView order_list;
	ProductListVModel vModel;
	OrderVModel orderVModel;
	OrderListVModel orderListVModel;
	OrderListAdapter adapter;
	DialogUtilities utilities;
	List<Product> orderList;
	Button chkout;

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
		utilities.showProgressDialog("My Cart", "Loading your orders", true);
		orderListVModel.getOrders(getContext()).observe(this, new Observer<List<Product>>() {
			@Override
			public void onChanged(@Nullable List<Product> orders) {
				if (orders != null){
					orderList.addAll(orders);
					adapter = new OrderListAdapter(getContext(), orderList, vModel, orderVModel);
					adapter.notifyDataSetChanged();
					order_list.setAdapter(adapter);
					initSwipe();
					utilities.showProgressDialog("", "",false);
				}else {
					utilities.showInfoDialog("Orders", "Your cart is empty");
				}
			}
		});


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

	private void initSwipe(){
		ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

			@Override
			public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
				return false;
			}

			@Override
			public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
				int position = viewHolder.getAdapterPosition();

				if (direction == ItemTouchHelper.LEFT){
					adapter.removeItem(position);
				} else {
					removeView();
				}
			}

			@Override
			public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

				Bitmap icon;
				if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

					View itemView = viewHolder.itemView;
					float height = (float) itemView.getBottom() - (float) itemView.getTop();
					float width = height / 3;

					if(dX > 0){
						p.setColor(Color.parseColor("#388E3C"));
						RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
						c.drawRect(background,p);
						icon = BitmapFactory.decodeResource(getResources(), R.drawable.vector_edit);
						
						RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
						c.drawBitmap(icon,null,icon_dest,p);
					} else {
//						p.setColor(Color.parseColor("#D32F2F"));
//						RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
//						c.drawRect(background,p);
//						icon = BitmapFactory.decodeResource(getResources(), R.drawable.vector_delete);
//						RectF icon_d = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
//						c.drawBitmap(icon,null,icon_d,p);
					}
				}
				super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
			}
		};
		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
		itemTouchHelper.attachToRecyclerView(order_list);
	}

	private void removeView(){
		if(view.getParent()!=null) {
			((ViewGroup) view.getParent()).removeView(view);
		}
	}



}

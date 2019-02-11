package nasaaty.com.hm.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.activities.ProductDetails;
import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.viewmodels.OrderVModel;
import nasaaty.com.hm.viewmodels.ProductListVModel;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderVHolder>{

	private Context context;
	private List<Order> orders;
	private FirebaseFirestore firestore;

	private ProductListVModel productVModel;
	private OrderVModel orderVModel;

	public OrderListAdapter(Context context, List<Order> orders, ProductListVModel productVModel, OrderVModel listVmodel) {
		this.context = context;
		this.orders = orders;
		this.productVModel = productVModel;
		this.orderVModel = listVmodel;
		this.firestore = FirebaseFirestore.getInstance();
	}

	@NonNull
	@Override
	public OrderVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new OrderVHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_item, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull final OrderVHolder holder, final int position) {
		//get product by id
		final Order order = orders.get(position);

		Query query = firestore.collection("products")
				.whereEqualTo("pid", order.getProduct_id());
		query.get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task) {
						QuerySnapshot snapshots = task.getResult();
						for (DocumentSnapshot snapshot : snapshots.getDocuments()){
							final Product product = snapshot.toObject(Product.class);
							holder.p_title.setText(product.getLabel());
							holder.p_desc.setText(product.getDescription());
							holder.p_price.setText(String.valueOf(product.getPrice()));

							holder.delete.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									orderVModel.deleteOrder(order);
									notifyDataSetChanged();
									notifyItemRemoved(position);
								}
							});


							holder.itemView.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									Intent i = new Intent(context, ProductDetails.class);
									i.putExtra("product_id", product.getPid());
									context.startActivity(i);
								}
							});

							holder.qty.addTextChangedListener(new TextWatcher() {
								@Override
								public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

								}

								@Override
								public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
									if (TextUtils.isEmpty(charSequence)){
										holder.p_price.setText(String.valueOf(product.getPrice()));
									}else {
										int val = Integer.valueOf(charSequence.toString());
										int price = Integer.valueOf(holder.p_price.getText().toString());

										holder.p_price.setText(String.valueOf(val*price));
									}
								}

								@Override
								public void afterTextChanged(Editable editable) {
									String charSequence = editable.toString();
									if (TextUtils.isEmpty(charSequence)){
										holder.p_price.setText(String.valueOf(product.getPrice()));
									}else {
										int val = Integer.valueOf(charSequence.toString());
										int price = Integer.valueOf(holder.p_price.getText().toString());

										holder.p_price.setText(String.valueOf(val*price));
									}
								}
							});
						}
					}
				});
	}

	@Override
	public int getItemCount() {
		return orders.size();
	}

	class OrderVHolder extends RecyclerView.ViewHolder {

		TextView p_title, p_desc, p_price;
		EditText qty;
		View delete;
		ImageView p_img;

		public OrderVHolder(View itemView) {
			super(itemView);
			p_title = itemView.findViewById(R.id.title);
			p_desc = itemView.findViewById(R.id.desc);
			p_price = itemView.findViewById(R.id.price);
			qty = itemView.findViewById(R.id.qty);
			delete = itemView.findViewById(R.id.del);
		}
	}


}

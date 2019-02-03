package nasaaty.com.hm.adapters;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.utils.OrderQLiveData;
import nasaaty.com.hm.utils.ProductQLiveData;
import nasaaty.com.hm.viewmodels.ProductListVModel;
import nasaaty.com.hm.viewmodels.ProductVModel;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderVHolder>{

	private Context context;
	private List<Order> orders;
	private FirebaseFirestore firestore;

	private ProductListVModel productVModel;

	public OrderListAdapter(Context context, List<Order> orders, ProductListVModel productVModel) {
		this.context = context;
		this.orders = orders;
		this.productVModel = productVModel;
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
		Order order = orders.get(position);

		Query query = firestore.collection("products")
				.whereEqualTo("pid", order.getProduct_id());
		query.get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task) {
						QuerySnapshot snapshots = task.getResult();
						for (DocumentSnapshot snapshot : snapshots.getDocuments()){
							Product product = snapshot.toObject(Product.class);
							holder.p_title.setText(product.getLabel());
							holder.p_desc.setText(product.getDescription());
							holder.p_price.setText(String.valueOf(product.getPrice()));
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
		ImageView p_img;

		public OrderVHolder(View itemView) {
			super(itemView);
			p_title = itemView.findViewById(R.id.title);
			p_desc = itemView.findViewById(R.id.desc);
			p_price = itemView.findViewById(R.id.price);
		}
	}


}

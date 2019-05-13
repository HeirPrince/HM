package nasaaty.com.hm.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.activities.ProductDetails;
import nasaaty.com.hm.model.ImageFile;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.utils.StorageRepository;
import nasaaty.com.hm.viewmodels.OrderVModel;
import nasaaty.com.hm.viewmodels.ProductListVModel;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderVHolder> {

	private Context context;
	public List<Product> orders;
	private FirebaseFirestore firestore;

	private ProductListVModel productVModel;
	private OrderVModel orderVModel;
	private StorageRepository repository;

	public OrderListAdapter(Context context, List<Product> orders, ProductListVModel productVModel, OrderVModel listVmodel) {
		this.context = context;
		this.orders = orders;
		this.productVModel = productVModel;
		this.orderVModel = listVmodel;
		this.firestore = FirebaseFirestore.getInstance();
		this.repository = new StorageRepository(context);
	}

	@NonNull
	@Override
	public OrderVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new OrderVHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_item, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull final OrderVHolder holder, final int position) {
		//get product by id
		final Product product = orders.get(position);

		holder.p_title.setText(product.getLabel());
		holder.p_price.setText(String.valueOf(product.getPrice()));

		//load product image
		DocumentReference reference = firestore.collection("images").document(product.getPid()).collection("images").document("default");
		reference
				.get()
				.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
					@Override
					public void onSuccess(DocumentSnapshot documentSnapshot) {
						if (documentSnapshot.exists()) {
							ImageFile imageFile = documentSnapshot.toObject(ImageFile.class);
							StorageReference defRef = repository.getDefaultIMage(product.getPid(), imageFile.getFileName());
							defRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
								@Override
								public void onSuccess(Uri uri) {
									Picasso.get()
											.load(uri)
											.placeholder(R.drawable.deliverer)
											.into(holder.p_img);
								}

							});
						} else {
							Toast.makeText(context, "error loading image", Toast.LENGTH_SHORT).show();
						}
					}
				});


//		holder.delete.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				orderVModel.deleteOrder(product);
//				notifyDataSetChanged();
//				orders.remove(position);
//			}
//		});

		holder.qty.setText("1");


		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(context, ProductDetails.class);
				i.putExtra("product_id", product.getPid());
				context.startActivity(i);
			}
		});

	}

	@Override
	public int getItemCount() {
		return orders.size();
	}

	public void addItem(Product order, int pos){
		orders.add(pos, order);
		orderVModel.insertOrder(order);
		notifyItemInserted(orders.size());
	}

	public void removeItem(int position){
		orderVModel.deleteOrder(orders.get(position));
		orders.remove(position);
		notifyItemRemoved(position);
		notifyItemRangeRemoved(position, orders.size());
	}

	public class OrderVHolder extends RecyclerView.ViewHolder {

		TextView p_title, p_price, qty;
		ImageView p_img;
		public RelativeLayout backgroundView, foregroundView;

		public OrderVHolder(View itemView) {
			super(itemView);
			p_title = itemView.findViewById(R.id.title);
			p_price = itemView.findViewById(R.id.p_price);
			qty = itemView.findViewById(R.id.qty);
			p_img = itemView.findViewById(R.id.img);
			backgroundView = itemView.findViewById(R.id.view_background);
			foregroundView = itemView.findViewById(R.id.view_foreground);
		}
	}


}

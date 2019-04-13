package nasaaty.com.hm.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import nasaaty.com.hm.R;
import nasaaty.com.hm.activities.ProductDetails;
import nasaaty.com.hm.model.ImageFile;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.utils.StorageRepository;
import nasaaty.com.hm.utils.repos.TransactionRepository;
import nasaaty.com.hm.viewmodels.OrderVModel;

public class ProductListAdapter extends ProductRepo<ProductListAdapter.productVHolder> {

	Context context;
	FirebaseFirestore firestore;
	OrderVModel orderVModel;
	FirebaseAuth firebaseAuth;
	StorageRepository storageRepository;
	TransactionRepository transactionRepository;


	public ProductListAdapter(Context context, Query mQuery, OrderVModel orderVModel) {
		super(context, mQuery);
		this.context = context;
		this.firestore = FirebaseFirestore.getInstance();
		this.firebaseAuth = FirebaseAuth.getInstance();
		this.storageRepository = new StorageRepository(context);
		this.orderVModel = orderVModel;
		this.transactionRepository = new TransactionRepository(context);
	}

	@NonNull
	@Override
	public ProductListAdapter.productVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		return new productVHolder(inflater.inflate(R.layout.layout_product_item, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull ProductListAdapter.productVHolder holder, int position) {
		holder.bind(getProduct(position));
	}

	@Override
	public int getItemCount() {
		return super.getItemCount();
	}

	public class productVHolder extends RecyclerView.ViewHolder {

		TextView label, price, starCount;
		ReadMoreTextView desc;
		Button plc_order;
		ImageButton fav, add_likes;
		ImageView pro_image;
		ProgressBar progressBar;
		AppCompatRatingBar ratingBar;

		public productVHolder(View itemView) {
			super(itemView);
			label = itemView.findViewById(R.id.product_title);
			price = itemView.findViewById(R.id.product_price);
			desc = itemView.findViewById(R.id.product_description);
			plc_order = itemView.findViewById(R.id.plc_order);
			pro_image = itemView.findViewById(R.id.product_image);
			fav = itemView.findViewById(R.id.fav);
			progressBar = itemView.findViewById(R.id.progress);
			add_likes = itemView.findViewById(R.id.btnLike);
			ratingBar = itemView.findViewById(R.id.ratingBar);
			starCount = itemView.findViewById(R.id.count);
		}

		public void setLikeBtn(final String pid) {
			firestore.collection("likes").document(pid).collection(firebaseAuth.getCurrentUser().getUid())
					.addSnapshotListener(new EventListener<QuerySnapshot>() {
						@Override
						public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
							if (e != null) return;

							for (DocumentSnapshot snapshot : queryDocumentSnapshots) {

								if (snapshot.exists()) {
									if (snapshot.get(firebaseAuth.getCurrentUser().getUid()) != null) {
										add_likes.setImageResource(R.drawable.vector_like_selected);
									}else {
										add_likes.setImageResource(R.drawable.vector_like);
									}
								} else {
									add_likes.setImageResource(R.drawable.vector_like);
								}
							}
						}
					});
		}

		public void toggleProgress(Boolean state){
			if (state)
				progressBar.setVisibility(View.VISIBLE);
			else
				progressBar.setVisibility(View.GONE);
		}


		public void bind(final Product model) {
			label.setText(model.getLabel());
			price.setText(String.format("%s RWF", String.valueOf(model.getPrice())));
			desc.setText(model.getDescription());
			ratingBar.setRating((float) model.getAvgRatings());
			starCount.setText(String.valueOf(model.getAvgRatings()));
			setLikeBtn(model.getPid());
			toggleProgress(true);

			//get default image
			DocumentReference reference = firestore.collection("images").document(model.getPid()).collection("images").document("default");

			reference
					.get()
					.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
						@Override
						public void onSuccess(DocumentSnapshot documentSnapshot) {
							if (documentSnapshot.exists()) {
								ImageFile imageFile = documentSnapshot.toObject(ImageFile.class);
								StorageReference defRef = storageRepository.getDefaultIMage(model.getPid(), imageFile.getFileName());
								defRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
									@Override
									public void onSuccess(Uri uri) {
										Picasso.get()
												.load(uri)
												.placeholder(R.drawable.deliverer)
												.into(pro_image);
										toggleProgress(false);
									}

								});
							} else {
								Toast.makeText(context, "error loading image", Toast.LENGTH_SHORT).show();
							}
						}
					});


			add_likes.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					transactionRepository.allowUserLikes(model.getPid());
				}
			});

			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(context, ProductDetails.class);
					intent.putExtra("pid", model.getPid());
					intent.putExtra("uid", firebaseAuth.getCurrentUser().getUid());
					context.startActivity(intent);
				}
			});

			plc_order.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					orderVModel.insertOrder(model);
				}
			});


		}
	}
}

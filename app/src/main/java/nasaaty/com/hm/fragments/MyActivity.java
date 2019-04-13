package nasaaty.com.hm.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class MyActivity extends Fragment {

	RecyclerView myList;
	FirebaseFirestore firestore;
	FirebaseAuth auth;
	FirestoreRecyclerAdapter adapter;
	StorageRepository storageRepository;
	private OrderVModel orderVModel;
	private TransactionRepository transactionRepository;

	public MyActivity() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_my, container, false);

		firestore = FirebaseFirestore.getInstance();
		myList = view.findViewById(R.id.my_list);
		auth = FirebaseAuth.getInstance();
		storageRepository = new StorageRepository(getContext());
		orderVModel = ViewModelProviders.of(this).get(OrderVModel.class);
		transactionRepository = new TransactionRepository(getContext());

		getMyJobs();

		return view;
	}

	private void getMyJobs() {
		myList.setLayoutManager(new LinearLayoutManager(getContext()));
		myList.setHasFixedSize(true);

		//query
		Query myProducts = firestore.collection("products")
				.whereEqualTo("owner", auth.getCurrentUser().getUid());

		FirestoreRecyclerOptions<Product> options =
				new FirestoreRecyclerOptions.Builder<Product>()
						.setQuery(myProducts, Product.class)
						.setLifecycleOwner(this)
						.build();

		adapter = new FirestoreRecyclerAdapter<Product, productVHolder>(options) {
			@NonNull
			@Override
			public productVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
				View view = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.layout_product_item, parent, false);

				return new productVHolder(view);
			}

			@Override
			protected void onBindViewHolder(@NonNull final productVHolder holder, int position, @NonNull final Product model) {
				holder.label.setText(model.getLabel());
				holder.price.setText(String.format("%s RWF", String.valueOf(model.getPrice())));
				holder.desc.setText(model.getDescription());
				holder.ratingBar.setRating((float) model.getAvgRatings());
				holder.starCount.setText(String.valueOf(model.getAvgRatings()));
				holder.setLikeBtn(model.getPid());
				holder.toggleProgress(true);

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
													.into(holder.pro_image);
											holder.toggleProgress(false);
										}

									});
								} else {
									Toast.makeText(getContext(), "error loading image", Toast.LENGTH_SHORT).show();
								}
							}
						});


				holder.add_likes.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						transactionRepository.allowUserLikes(model.getPid());
					}
				});

				holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getContext(), ProductDetails.class);
						intent.putExtra("pid", model.getPid());
						intent.putExtra("uid", auth.getCurrentUser().getUid());
						startActivity(intent);
					}
				});

				holder.plc_order.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						orderVModel.insertOrder(model);
					}
				});

			}
		};

		adapter.notifyDataSetChanged();
		myList.setAdapter(adapter);

	}


	class productVHolder extends RecyclerView.ViewHolder {

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
			firestore.collection("likes").document(pid).collection(auth.getCurrentUser().getUid())
					.addSnapshotListener(new EventListener<QuerySnapshot>() {
						@Override
						public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
							if (e != null) return;

							for (DocumentSnapshot snapshot : queryDocumentSnapshots) {

								if (snapshot.exists()) {
									if (snapshot.get(auth.getCurrentUser().getUid()) != null) {
										add_likes.setImageResource(R.drawable.vector_like_selected);
									} else {
										add_likes.setImageResource(R.drawable.vector_like);
									}
								} else {
									add_likes.setImageResource(R.drawable.vector_like);
								}
							}
						}
					});
		}

		public void toggleProgress(Boolean state) {
			if (state)
				progressBar.setVisibility(View.VISIBLE);
			else
				progressBar.setVisibility(View.GONE);
		}

	}
}

package nasaaty.com.hm.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.thunder413.datetimeutils.DateTimeUnits;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.ImageFile;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.utils.StorageRepository;

public class SmallProductAdapter extends RecyclerView.Adapter<SmallProductAdapter.SmallVHolder> {

	private Context context;
	private List<Product> products;
	private StorageRepository storageRepository;
	private FirebaseFirestore firestore;

	public SmallProductAdapter(Context context, List<Product> products) {
		this.context = context;
		this.products = products;
		this.storageRepository = new StorageRepository(context);
		this.firestore = FirebaseFirestore.getInstance();
	}

	@NonNull
	@Override
	public SmallVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_item_small, parent, false);
		return new SmallVHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull SmallVHolder holder, int position) {
		Product product = products.get(position);
		if (product != null){
			holder.bind(product);
		}else {
			Toast.makeText(context, "false", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public int getItemCount() {
		return (null != products ? products.size() : 0);
	}

	class SmallVHolder extends RecyclerView.ViewHolder {

		TextView title, price, ts;
		AppCompatRatingBar ratingBar;
		ImageView product_image;
		Button orderBtn;
		ProgressBar progressBar;


		SmallVHolder(View itemView) {
			super(itemView);
			title = itemView.findViewById(R.id.job_title);
			price = itemView.findViewById(R.id.price);
			product_image = itemView.findViewById(R.id.image);
			ratingBar = itemView.findViewById(R.id.ratingBar);
			orderBtn = itemView.findViewById(R.id.add_to_cart);
			progressBar = itemView.findViewById(R.id.progress);
			ts = itemView.findViewById(R.id.timeStamp);
		}

		void bind(final Product product) {
			toggleProgress(true);
			title.setText(product.getLabel());
			price.setText(String.format("%s RWF", String.valueOf(product.getPrice())));
			ratingBar.setRating((float) product.getAvgRatings());
			Date date = DateTimeUtils.formatDate(Long.parseLong(product.getTimeStamp()), DateTimeUnits.SECONDS);
			ts.setText(DateTimeUtils.getTimeAgo(context, date));

			DocumentReference reference = firestore.collection("images").document(product.getPid()).collection("images").document("default");

			reference
					.get()
					.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
						@Override
						public void onSuccess(DocumentSnapshot snapshot) {
							if (snapshot.exists()) {
								ImageFile imageFile = snapshot.toObject(ImageFile.class);
								StorageReference defRef = storageRepository.getDefaultIMage(product.getPid(), imageFile.getFileName());
								defRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
									@Override
									public void onSuccess(Uri uri) {
										Picasso.get()
												.load(uri)
												.placeholder(R.drawable.deliverer)
												.into(product_image);
										toggleProgress(false);
									}

								});
							} else {
								toggleProgress(false);
								Toast.makeText(context, "error loading image", Toast.LENGTH_SHORT).show();
							}
						}
					});

		}

		private void toggleProgress(boolean b) {
			if (b)
				progressBar.setVisibility(View.VISIBLE);
			else
				progressBar.setVisibility(View.GONE);

		}
	}
}

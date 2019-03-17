package nasaaty.com.hm.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.Favorite;
import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.utils.StorageRepository;
import nasaaty.com.hm.utils.repos.TransactionRepository;
import nasaaty.com.hm.viewmodels.FavVModel;
import nasaaty.com.hm.viewmodels.OrderVModel;
import nasaaty.com.hm.viewmodels.ProductListVModel;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.productVHolder> {

	Context context;
	List<Product> products;
	OrderVModel vModel;
	FavVModel favVModel;
	ProductListVModel productVModel;
	HahaDB hahaDB;
	Boolean inspect;
	StorageRepository repository;
	TransactionRepository transactionRepository;
	public Boolean processView = false;


	public ProductListAdapter(Context context, List<Product> products, OrderVModel vModel, FavVModel favVModel, ProductListVModel productVModel) {
		this.context = context;
		this.products = products;
		this.vModel = vModel;
		this.favVModel = favVModel;
		this.productVModel = productVModel;
		this.hahaDB = HahaDB.getInstance(context);
		this.repository = new StorageRepository(context);
		this.transactionRepository = new TransactionRepository(context);
		this.inspect = false;
	}

	@Override
	public productVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new productVHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_item, parent, false));
	}

	@Override
	public void onBindViewHolder(final productVHolder holder, final int position) {
		final Product product = products.get(position);
		if (product != null) {
			holder.label.setText(product.getLabel());
			holder.price.setText(String.valueOf(product.getPrice())+" RWF");
			holder.desc.setText(product.getDescription());

			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
					processView = true;
				}
			});


			holder.toggleProgress(true);

			StorageReference ref = repository.getProductImage(product.getPid());

			ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
				@Override
				public void onSuccess(Uri uri) {
					if (uri != null){
						holder.toggleProgress(false);
						Picasso.get().load(uri).into(holder.pro_image);
					}else {
						holder.toggleProgress(false);
						Picasso.get().load(R.drawable.vector_pic).into(holder.pro_image);
						return;
					}
				}
			});

			if (checkFav(product.getPid())) {
				holder.toggleFav(true);
			} else {
				holder.toggleFav(false);
			}

			final Favorite favorite = new Favorite();
			favorite.setProduct_id(product.getPid());
			favorite.setProduct_label(product.getLabel());

			holder.fav.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					if (checkFav(product.getPid())) {
						Toast.makeText(context, "already favorited" + product.getPid(), Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "welcome to favorites", Toast.LENGTH_SHORT).show();
						favVModel.insert(favorite);
					}
				}
			});

			//place order
			holder.plc_order.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					Order order = new Order();
					order.setOwner(product.getOwner());
					order.setProduct_id(product.getPid());

					vModel.insertOrder(order);

				}
			});
		} else {
			Toast.makeText(context, "null", Toast.LENGTH_SHORT).show();
		}

	}

	public void updateLists(List<Product> list){
		this.products.clear();
		this.products.addAll(list);
	}

	// FIXME: 2/14/2019
	private class getFavAsync extends AsyncTask<String, String, String> {

		HahaDB hahaDBase;

		public getFavAsync(HahaDB hahaDB) {
			this.hahaDBase = hahaDB;
		}

		@Override
		protected String doInBackground(String... strings) {
			return hahaDBase.favoriteEntity().getFavorite(strings[0]);
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			if (TextUtils.isEmpty(s)) {
				inspect = false;
				getResult();
			} else {
				inspect = true;
				getResult();
			}
		}
	}

	public boolean checkFav(String pid) {
		new getFavAsync(hahaDB).execute(pid);
		if (getResult())
			return true;
		else
			return false;
	}


	private boolean getResult() {
		if (inspect) {
			return true;
		} else {
			return false;
		}
	}


	@Override
	public int getItemCount() {
		return products.size();
	}

	class productVHolder extends RecyclerView.ViewHolder {

		TextView label, price;
		ReadMoreTextView desc;
		Button plc_order;
		ImageButton fav;
		ImageView pro_image;
		ProgressBar progressBar;

		public productVHolder(View itemView) {
			super(itemView);
			label = itemView.findViewById(R.id.product_title);
			price = itemView.findViewById(R.id.product_price);
			desc = itemView.findViewById(R.id.product_description);
			plc_order = itemView.findViewById(R.id.plc_order);
			pro_image = itemView.findViewById(R.id.product_image);
			fav = itemView.findViewById(R.id.fav);
			progressBar = itemView.findViewById(R.id.progress);
		}

		public void toggleFav(boolean b) {
			if (b)
				fav.setImageResource(R.drawable.vector_fav_checked);
			else
				fav.setImageResource(R.drawable.vector_fav);
		}

		private void toggleProgress(boolean b) {
			if (b)
				progressBar.setVisibility(View.VISIBLE);
			else
				progressBar.setVisibility(View.GONE);

		}
	}

}

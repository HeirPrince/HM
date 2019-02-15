package nasaaty.com.hm.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.Favorite;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.viewmodels.FavVModel;

public class favProductListAdapter extends RecyclerView.Adapter<favProductListAdapter.productVHolder>{

	Context context;
	List<Favorite> favorites;
	FavVModel vModel;


	public favProductListAdapter(Context context, List<Favorite> favorites, FavVModel vModel) {
		this.context = context;
		this.favorites = favorites;
		this.vModel = vModel;
	}

	@Override
	public productVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new productVHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_favorite_item, parent, false));
	}

	@Override
	public void onBindViewHolder(final productVHolder holder, final int position) {
		final Favorite favorite = favorites.get(position);
		holder.label.setText(favorite.getProduct_label());

		holder.remove.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				vModel.delete(favorite);
				favorites.remove(favorite);
				notifyDataSetChanged();
			}
		});
	}

	@Override
	public int getItemCount() {
		return favorites.size();
	}

	class productVHolder extends RecyclerView.ViewHolder {

		TextView label;
		ImageButton remove;

		public productVHolder(View itemView) {
			super(itemView);
			label = itemView.findViewById(R.id.title);
			remove = itemView.findViewById(R.id.remove);
		}
	}

}

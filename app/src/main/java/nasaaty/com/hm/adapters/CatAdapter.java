package nasaaty.com.hm.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.CatModel;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.CatViewHolder>{

	private Context context;
	private List<CatModel> catModels;

	public CatAdapter(Context context, List<CatModel> catModels) {
		this.context = context;
		this.catModels = catModels;
	}

	@NonNull
	@Override
	public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cat_item, parent, false);
		return new CatViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {
		CatModel model = catModels.get(position);
		holder.feed(model);
	}

	@Override
	public int getItemCount() {
		return catModels.size();
	}

	class CatViewHolder extends RecyclerView.ViewHolder {

		TextView title;
		ImageView image;

		public CatViewHolder(View itemView) {
			super(itemView);
			title = itemView.findViewById(R.id.cat_title);
			image = itemView.findViewById(R.id.cat_image);
		}

		public void feed(CatModel model){
			if (model.getResource() == 0){
				bind(model.getTitle(), model.getUrl());
			}else {
				bind(model.getTitle(), model.getResource());
			}
		}

		public void bind(String t, String url){
			title.setText(t);
			Picasso.get().load(url).into(image);
		}

		public void bind(String t, int resource){
			title.setText(t);
			Picasso.get().load(resource).into(image);
		}
	}
}

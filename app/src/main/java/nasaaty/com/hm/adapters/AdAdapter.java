package nasaaty.com.hm.adapters;

import java.util.List;

import nasaaty.com.hm.model.AdModel;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class AdAdapter extends SliderAdapter {
	List<AdModel> adModels;

	public AdAdapter(List<AdModel> adModels) {
		this.adModels = adModels;
	}

	@Override
	public int getItemCount() {
		return adModels.size();
	}

	@Override
	public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
		imageSlideViewHolder.bindImageSlide(adModels.get(position).getUrl());
	}
}


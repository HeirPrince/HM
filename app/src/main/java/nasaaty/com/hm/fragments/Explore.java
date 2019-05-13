package nasaaty.com.hm.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.adapters.AdAdapter;
import nasaaty.com.hm.adapters.CatAdapter;
import nasaaty.com.hm.model.AdModel;
import nasaaty.com.hm.model.CatModel;
import nasaaty.com.hm.utils.PicassoImageLoadingService;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.event.OnSlideClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class Explore extends Fragment {

	RecyclerView cat_list;

	public Explore() {
		// Required empty public constructor
	}

	public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
		int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
		return noOfColumns;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_explore, container, false);

		Slider slider = view.findViewById(R.id.banner_slider1);
		Slider.init(new PicassoImageLoadingService(getContext()));
		slider.setAdapter(new AdAdapter(getImages()));

		slider.setOnSlideClickListener(new OnSlideClickListener() {
			@Override
			public void onSlideClick(int position) {
				Toast.makeText(getContext(), "selected "+position, Toast.LENGTH_SHORT).show();
			}
		});

		cat_list = view.findViewById(R.id.cat_list);

		int colCount = calculateNoOfColumns(getContext(), 120);

		cat_list.setLayoutManager(new GridLayoutManager(getContext(), colCount));
		cat_list.setHasFixedSize(true);
		CatAdapter adapter = new CatAdapter(getContext(), getCats());
		cat_list.setAdapter(adapter);

		return view;
	}

	private List<CatModel> getCats() {
		List<CatModel> catModels = new ArrayList<>();
		catModels.add(new CatModel("Electronics", R.drawable.electronics));
		catModels.add(new CatModel("Vehicles", R.drawable.vehicles));
		catModels.add(new CatModel("Fashion", R.drawable.fashion));
		catModels.add(new CatModel("Kitchen Tools", R.drawable.kitchen_tools));
		catModels.add(new CatModel("Toys", R.drawable.toys));
		catModels.add(new CatModel("Food", R.drawable.food));
		catModels.add(new CatModel("Furniture", R.drawable.furniture));
		catModels.add(new CatModel("Services", R.drawable.services));

		return catModels;
	}

	private List<AdModel> getImages() {
		List<AdModel> adModels = new ArrayList<>();

		adModels.add(new AdModel("https://assets.materialup.com/uploads/dcc07ea4-845a-463b-b5f0-4696574da5ed/preview.jpg", "kk"));
		adModels.add(new AdModel("https://assets.materialup.com/uploads/20ded50d-cc85-4e72-9ce3-452671cf7a6d/preview.jpg", "kk"));
		adModels.add(new AdModel("https://assets.materialup.com/uploads/76d63bbc-54a1-450a-a462-d90056be881b/preview.png", "kk"));
		adModels.add(new AdModel("https://www.google.com/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&ved=2ahUKEwj-jIPCyJniAhWQZlAKHWyuDDIQjRx6BAgBEAU&url=https%3A%2F%2Fwww.adweek.com%2Fprogrammatic%2Fbiddable-ads-are-coming-to-tiktok-opening-up-the-popular-platform-to-more-marketers%2F&psig=AOvVaw20qzkzcZH0sSeFXSqOAVQG&ust=1557873232611850",
				"kk"));
		adModels.add(new AdModel("https://www.google.com/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&ved=2ahUKEwjO86SLyZniAhVDUlAKHeYMDLsQjRx6BAgBEAU&url=%2Furl%3Fsa%3Di%26rct%3Dj%26q%3D%26esrc%3Ds%26source%3Dimages%26cd%3D%26ved%3D%26url%3Dhttps%253A%252F%252Fwww.adweek.com%252Fcreativity%252Fpepsi-is-blanketing-cokes-hometown-with-ads-as-atlanta-prepares-for-the-super-bowl%252F%26psig%3DAOvVaw20qzkzcZH0sSeFXSqOAVQG%26ust%3D1557873232611850&psig=AOvVaw20qzkzcZH0sSeFXSqOAVQG&ust=1557873232611850",
				"kk"));
		
		return adModels;
	}


}

package nasaaty.com.hm.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.adapters.favProductListAdapter;
import nasaaty.com.hm.model.Favorite;
import nasaaty.com.hm.viewmodels.FavVModel;

public class Favorites extends AppCompatActivity {

	Toolbar toolbar;
	RecyclerView list;
	favProductListAdapter adapter;
	List<Favorite> favoriteList;
	FavVModel favVModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorites);
		bindViews();
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Favorites");

		favVModel = ViewModelProviders.of(this).get(FavVModel.class);
		favoriteList = new ArrayList<>();
		adapter = new favProductListAdapter(this, favoriteList, favVModel);

		populate();
	}

	private void populate() {
		favVModel.getFavorites().observe(this, new Observer<List<Favorite>>() {
			@Override
			public void onChanged(@Nullable List<Favorite> favorites) {
				if (favorites != null){
					favoriteList.addAll(favorites);
					adapter.notifyDataSetChanged();
					list.setAdapter(adapter);
				}
			}
		});
	}

	private void bindViews() {
		toolbar = findViewById(R.id.toolbar);
		list = findViewById(R.id.fav_list);
		list.setLayoutManager(new LinearLayoutManager(this));
		list.setHasFixedSize(true);
	}


}

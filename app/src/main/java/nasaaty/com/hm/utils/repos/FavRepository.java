package nasaaty.com.hm.utils.repos;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.List;

import nasaaty.com.hm.model.Favorite;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.viewmodels.FavVModel;

public class FavRepository {

	Context context;
	HahaDB hahaDB;
	LiveData<List<Favorite>> liveData;
	FavVModel vModel;
	Boolean isReg;

	public FavRepository(Context context, FavVModel vModel) {
		this.context = context;
		hahaDB = HahaDB.getInstance(context);
		this.liveData = hahaDB.favoriteEntity().favorites();
		this.vModel = vModel;
	}

	public void insert(Favorite favorite){
		new insertAsync(hahaDB).execute(favorite);
	}

	public void delete(Favorite favorite){
		new deleteAsync(hahaDB).execute(favorite);
	}

	public LiveData<List<Favorite>> getFavorites(){
		return liveData;
	}

	public void getFavById(String id) {
		new getFavAsync(hahaDB).execute(id);
	}

	private class insertAsync extends AsyncTask<Favorite, Void, Void>{

		HahaDB hahaDBase;

		public insertAsync(HahaDB hahaDB) {
			this.hahaDBase = hahaDB;
		}

		@Override
		protected Void doInBackground(Favorite... favorites) {
			hahaDBase.favoriteEntity().insert(favorites[0]);
			return null;
		}
	}

	private class deleteAsync extends AsyncTask<Favorite, Void, Void>{

		HahaDB hahaDBase;

		public deleteAsync(HahaDB hahaDB) {
			this.hahaDBase = hahaDB;
		}

		@Override
		protected Void doInBackground(Favorite... favorites) {
			hahaDBase.favoriteEntity().delete(favorites[0]);
			return null;
		}
	}

	private class getFavAsync extends AsyncTask<String, String, String>{

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
			if (TextUtils.isEmpty(s)){
				isReg = true;
				check();
			}
			else {
				isReg = false;
				check();
			}
		}
	}

	public Boolean check() {
		if (isReg) {
			Toast.makeText(context, "already", Toast.LENGTH_SHORT).show();
			return true;
		}
		else {
			Toast.makeText(context, "new", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

}

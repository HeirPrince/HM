package nasaaty.com.hm.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import nasaaty.com.hm.model.Favorite;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.utils.repos.FavRepository;

public class FavVModel extends AndroidViewModel {

	FavRepository repository;
	HahaDB hahaDB;
	Boolean inspect;

	public FavVModel(@NonNull Application application) {
		super(application);
		repository = new FavRepository(application, this);
		hahaDB = HahaDB.getInstance(application);
		inspect = false;
	}

	public void insert(Favorite favorite){
		repository.insert(favorite);
	}

	public void delete(Favorite favorite){
		repository.delete(favorite);
	}

	public LiveData<List<Favorite>> getFavorites(){
		return repository.getFavorites();
	}


}

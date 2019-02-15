package nasaaty.com.hm.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import nasaaty.com.hm.model.Favorite;
import nasaaty.com.hm.model.Product;

import static android.arch.persistence.room.OnConflictStrategy.ABORT;
import static android.arch.persistence.room.OnConflictStrategy.FAIL;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface FavDao {

	@Insert(onConflict = FAIL)
	void insert(Favorite favorite);

	@Delete
	void delete(Favorite favorite);

	@Query("SELECT * FROM favorite")
	LiveData<List<Favorite>> favorites();

	@Query("SELECT product_id FROM favorite WHERE product_id = :id")
	String getFavorite(String id);
}

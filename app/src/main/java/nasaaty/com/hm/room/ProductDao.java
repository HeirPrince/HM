package nasaaty.com.hm.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import nasaaty.com.hm.model.Product;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ProductDao {

	@Query("SELECT * FROM Product")
	LiveData<List<Product>> getProducts();

	@Insert(onConflict = REPLACE)
	void insertProduct(Product product);

	@Query("SELECT * FROM product WHERE pid = :id")
	Product getById(String id);

}

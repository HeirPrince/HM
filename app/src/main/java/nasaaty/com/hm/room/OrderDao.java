package nasaaty.com.hm.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import nasaaty.com.hm.model.Product;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface OrderDao {

	@Insert(onConflict = REPLACE)
	void placeOrder(Product product);

	@Query("SELECT * FROM `Product`")
	LiveData<List<Product>> getOrders();

	@Query("DELETE FROM `Product`")
	void deleteAll();

	@Query("SELECT * FROM `Product` WHERE pid = :pid")
	Product getOrderDetails(Integer pid);

	@Query("SELECT * FROM `Product` WHERE pid = :pid")
	Product getOrderByID(String pid);

	@Delete
	void deleteOrder(Product product);

	//get undone orders
}

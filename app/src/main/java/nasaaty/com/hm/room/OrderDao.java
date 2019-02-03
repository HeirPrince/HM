package nasaaty.com.hm.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import nasaaty.com.hm.model.Order;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface OrderDao {

	@Insert(onConflict = REPLACE)
	public void placeOrder(Order order);

	@Query("SELECT * FROM `Order`")
	LiveData<List<Order>> getOrders();

	//get undone orders
}

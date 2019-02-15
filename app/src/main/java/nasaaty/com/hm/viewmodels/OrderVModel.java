package nasaaty.com.hm.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.utils.repos.OrderRepository;

public class OrderVModel extends AndroidViewModel {

	OrderRepository repository;
	private HahaDB hahaDB;


	public OrderVModel(@NonNull Application application) {
		super(application);
		repository = new OrderRepository(application);
	}

	public void insertOrder(final Order order){
		repository.insertOrder(order);
	}

	public void deleteAll(){
		repository.flush();
	}

	public void sync(List<Order> orders){
		repository.sync(orders);
	}

	public Order getDetails(int order_id){
		return repository.getOrder(order_id).getValue();
	}

	public void deleteOrder(Order order) {
		repository.delete(order);
	}

	public Boolean checkIfProductExists(String pid){
		repository.checkIfExists(pid);
		if (repository.check())
			return false;
		else
			return true;
	}
}

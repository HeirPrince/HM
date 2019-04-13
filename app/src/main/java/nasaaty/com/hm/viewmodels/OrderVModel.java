package nasaaty.com.hm.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.utils.repos.OrderRepository;

public class OrderVModel extends AndroidViewModel {

	OrderRepository repository;
	private HahaDB hahaDB;


	public OrderVModel(@NonNull Application application) {
		super(application);
		repository = new OrderRepository(application);
	}

	public void insertOrder(final Product product){
		repository.insertOrder(product);
	}

	public void deleteAll(){
		repository.flush();
	}

	public void sync(Order order){
		repository.sync(order);
	}

	public Product getDetails(int order_id){
		return repository.getOrder(order_id).getValue();
	}

	public void deleteOrder(Product product) {
		repository.delete(product);
	}

//	public Boolean checkIfProductExists(String pid){
////		repository.checkIfExists(pid);
//		if (repository.check())
//			return false;
//		else
//			return true;
//	}
}

package nasaaty.com.hm.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.utils.repos.OrderRepository;

public class OrderListVModel extends AndroidViewModel {

	private OrderRepository repository;
	LiveData<List<Product>> orders;

	public OrderListVModel(@NonNull Application application) {
		super(application);
		repository = new OrderRepository(application);
		orders = repository.getAllOrders();
	}

	public LiveData<List<Product>> getOrders(Context context){
		return orders;

	}

	
}

package nasaaty.com.hm.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.utils.OrderQLiveData;
import nasaaty.com.hm.utils.repos.OrderRepository;

public class OrderListVModel extends AndroidViewModel {

	private OrderRepository repository;
	LiveData<List<Order>> orders;

	public OrderListVModel(@NonNull Application application) {
		super(application);
		repository = new OrderRepository(application);
		orders = repository.getAllOrders();
	}

	public LiveData<List<Order>> getOrders(Context context){
		return orders;

	}

	
}

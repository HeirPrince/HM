package nasaaty.com.hm.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.utils.OrderQLiveData;

public class OrderListVModel extends AndroidViewModel {

	private HahaDB hahaDB;
	private OrderQLiveData liveData;

	FirebaseFirestore firebaseFirestore;

	public OrderListVModel(@NonNull Application application) {
		super(application);
		hahaDB = HahaDB.getInstance(this.getApplication());
		this.firebaseFirestore = FirebaseFirestore.getInstance();
	}

	public LiveData<DocumentSnapshot> getOrders(){
		CollectionReference proRef = firebaseFirestore.collection("orders");
		liveData = new OrderQLiveData(proRef);
		return liveData;
	}

	
}

package nasaaty.com.hm.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.utils.ProductQLiveData;
import nasaaty.com.hm.utils.repos.TransactionRepository;

public class ProductListVModel extends AndroidViewModel implements
		TransactionRepository.onViewNodeCreated,
		TransactionRepository.onViewAdded,
		TransactionRepository.onViewRemoved {

	private HahaDB hahaDB;
	private ProductQLiveData liveData;
	private TransactionRepository transactionRepository;
	FirebaseFirestore firestore;

	public ProductListVModel(@NonNull Application application) {
		super(application);
		this.hahaDB = HahaDB.getInstance(this.getApplication());
		this.firestore = FirebaseFirestore.getInstance();
		this.transactionRepository = new TransactionRepository(application);
	}

	public LiveData<DocumentSnapshot> getProducts(){
		CollectionReference proRef = firestore.collection("products");
		liveData = new ProductQLiveData(proRef);
		return liveData;
	}

	public LiveData<DocumentSnapshot> getProductById(String id){
		Query query = firestore.collection("products")
				.whereEqualTo("pid", id);
		liveData = new ProductQLiveData(query);
		return liveData;
	}

	@Override
	public void onComplete(Boolean complete) {
		Toast.makeText(getApplication(), "view created", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onAdded(Boolean complete) {
		Toast.makeText(getApplication(), "onAdded", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRemoved(Boolean complete) {
		Toast.makeText(getApplication(), "onRemoved", Toast.LENGTH_SHORT).show();
	}


}

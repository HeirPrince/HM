package nasaaty.com.hm.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.room.ProductDao;
import nasaaty.com.hm.utils.ProductQLiveData;

public class ProductListVModel extends AndroidViewModel {

	private HahaDB hahaDB;
	private ProductQLiveData liveData;

	FirebaseFirestore firestore;

	public ProductListVModel(@NonNull Application application) {
		super(application);
		hahaDB = HahaDB.getInstance(this.getApplication());
		this.firestore = FirebaseFirestore.getInstance();
	}

	public LiveData<DocumentSnapshot> getProducts(){
		CollectionReference proRef = firestore.collection("products");
		liveData = new ProductQLiveData(proRef);
		return liveData;
	}

	Product p = new Product();

	public LiveData<DocumentSnapshot> getProductById(String id){
		Query query = firestore.collection("products")
				.whereEqualTo("pid", id);
		liveData = new ProductQLiveData(query);
		return liveData;
	}

	private class QueryAsyncTask extends AsyncTask<String, Void, Product> {

		FirebaseFirestore firestore;

		public QueryAsyncTask(FirebaseFirestore firebaseFirestore) {
			this.firestore = firebaseFirestore;
		}

		@Override
		protected Product doInBackground(String... strings) {
			firestore.collection("orders")
					.whereEqualTo("pid", strings[0])
					.get()
					.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
						@Override
						public void onComplete(@NonNull Task<QuerySnapshot> task) {
							QuerySnapshot snapshot = task.getResult();
							for (DocumentSnapshot snapshot1 : snapshot.getDocuments()){
								if (snapshot1.exists()){
									Product product = snapshot1.toObject(Product.class);
									setP(product);
								}
							}
						}
					});
			return null;
		}

		@Override
		protected void onPostExecute(Product product) {
			setP(product);
		}
	}

	private void setP(Product product) {
		this.p = product;
	}


}

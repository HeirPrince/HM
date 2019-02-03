package nasaaty.com.hm.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.utils.ProductQLiveData;

public class ProductVModel extends AndroidViewModel{

	private FirebaseFirestore firebaseFirestore;
	private ProductQLiveData liveData;
	private HahaDB hahaDB;

	public ProductVModel(@NonNull Application application) {
		super(application);
		hahaDB = HahaDB.getInstance(this.getApplication());
		this.firebaseFirestore = FirebaseFirestore.getInstance();
	}

	public void insertNew(final Product product){
		DocumentReference push = firebaseFirestore.collection("products").document();
		String id = push.getId();
		DocumentReference proRef = firebaseFirestore.collection("products").document(id);
		product.setPid(id);

		proRef.set(product)
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.isSuccessful()){
							new insertProductAsync(hahaDB).execute(product);
						}
					}
				});
	}

	private class insertProductAsync extends AsyncTask<Product, Void, Void> {

		HahaDB hahaDBase;

		public insertProductAsync(HahaDB hahaDB) {
			hahaDBase = hahaDB;
		}

		@Override
		protected Void doInBackground(Product... products) {
			hahaDBase.productEntity().insertProduct(products[0]);
			return null;
		}
	}
}

package nasaaty.com.hm.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.utils.ProductQLiveData;
import nasaaty.com.hm.utils.StorageRepository;
import nasaaty.com.hm.utils.repos.TransactionRepository;

public class ProductVModel extends AndroidViewModel{

	private FirebaseFirestore firebaseFirestore;
	private ProductQLiveData liveData;
	private HahaDB hahaDB;
	private StorageRepository storageRepository;
	private TransactionRepository transactionRepository;

	public ProductVModel(@NonNull Application application) {
		super(application);
		hahaDB = HahaDB.getInstance(this.getApplication());
		this.firebaseFirestore = FirebaseFirestore.getInstance();
		this.storageRepository = new StorageRepository(application);
		this.transactionRepository = new TransactionRepository(application);
	}

	public void insertNew(final Product product, final List<Uri> images){
		DocumentReference push = firebaseFirestore.collection("products").document();
		String id = push.getId();
		DocumentReference proRef = firebaseFirestore.collection("products").document(id);
		product.setPid(id);

		proRef.set(product)
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.isSuccessful()){
							new insertProductAsync(hahaDB, images).execute(product);
						}
					}
				});
	}

	private class insertProductAsync extends AsyncTask<Product, Void, Void> {

		HahaDB hahaDBase;
		List<Uri> uris;

		public insertProductAsync(HahaDB hahaDB, List<Uri> images) {
			hahaDBase = hahaDB;
			uris = images;
		}

		@Override
		protected Void doInBackground(Product... products) {
			hahaDBase.productEntity().insertProduct(products[0]);
			for (Uri uri : uris) {
				storageRepository.uploadProductImage(products[0].getPid(), uri);
			}
			return null;
		}
	}
}

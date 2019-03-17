package nasaaty.com.hm.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

	public void insertNew(final Product product, final List<Uri> images, final Uri def_image, final onUploadDone done){
		DocumentReference push = firebaseFirestore.collection("products").document();
		String id = push.getId();
		DocumentReference proRef = firebaseFirestore.collection("products").document(id);
		product.setPid(id);

		proRef.set(product)
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.isSuccessful()){
							done.done(true);
							new insertProductAsync(hahaDB, images, def_image).execute(product);
						}
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						done.done(false);
						Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_LONG).show();
					}
				});
	}

	public interface onUploadDone{
		void done(Boolean ok);
	}

	private class insertProductAsync extends AsyncTask<Product, Void, Void> {

		HahaDB hahaDBase;
		List<Uri> uris;
		Uri default_image;

		public insertProductAsync(HahaDB hahaDB, List<Uri> images, Uri def_image) {
			hahaDBase = hahaDB;
			uris = images;
			default_image = def_image;
		}

		@Override
		protected Void doInBackground(Product... products) {
			hahaDBase.productEntity().insertProduct(products[0]);
			storageRepository.uploadDefaultImage(products[0].getPid(), default_image);
			for (Uri uri : uris) {
				storageRepository.uploadProductImage(products[0].getPid(), uri, true);
			}
			return null;
		}
	}
}

package nasaaty.com.hm.utils;

import android.content.Context;
import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StorageRepository {

	Context context;
	FirebaseStorage storage;

	StorageReference userRef;
	StorageReference productRef;

	public StorageRepository(Context context) {
		this.context = context;
		this.storage = FirebaseStorage.getInstance();
		this.productRef = storage.getReference().child("products");
	}

	public void uploadProductImage(String pid, Uri uri){
		productRef.child(pid)
				.putFile(uri);
	}

	public StorageReference getProductImage(String pid){
		return productRef.child(pid);
	}

	public void deleteProductImage(String pid){

	}

	public void updateProductImage(String pid){
		deleteProductImage(pid);
	}
}

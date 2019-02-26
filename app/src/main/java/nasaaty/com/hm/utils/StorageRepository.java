package nasaaty.com.hm.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StorageRepository {

	Context context;
	FirebaseStorage storage;
	FirebaseFirestore firestore;

	StorageReference userRef;
	StorageReference productRef;
	CollectionReference imageRef;
	DialogUtilities dialogUtilities;

	public StorageRepository(Context context) {
		this.context = context;
		this.storage = FirebaseStorage.getInstance();
		this.firestore = FirebaseFirestore.getInstance();
		this.dialogUtilities = new DialogUtilities(context);
		this.productRef = storage.getReference().child("products");
		this.imageRef = firestore.collection("images");
	}

	public void uploadProductImage(final String pid, final Uri uri) {
		//add image to db
//		dialogUtilities.showProgressDialog("Haha", "Saving "+fileName,true);

		UploadTask uploadTask = productRef.child("images/" + pid + "/" + UUID.randomUUID().toString()).putFile(uri);

		uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
				String downloadUrl = taskSnapshot.getMetadata().getPath();
				String fileName = taskSnapshot.getMetadata().getName();

				Map<String, String> file_data = new HashMap<>();
				file_data.put("fileName", fileName);
				file_data.put("downloadUrl", downloadUrl);

				writeDB(pid, file_data);
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
			}
		});


	}

	private void writeDB(String pid, Map<String, String> file_data) {
		imageRef.document(pid).collection("images")
				.add(file_data)
				.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
					@Override
					public void onComplete(@NonNull Task<DocumentReference> task) {
						if (task.isSuccessful()) {
							Toast.makeText(context, "image(s) saved", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(context, "failed to save image Ref", Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	public StorageReference getProductImage(String pid) {
		return productRef.child(pid);
	}

	public void deleteProductImage(String pid) {

	}

	public void updateProductImage(String pid) {
		deleteProductImage(pid);
	}
}

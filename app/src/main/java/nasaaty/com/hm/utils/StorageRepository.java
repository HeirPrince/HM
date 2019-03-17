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

import java.util.UUID;

import nasaaty.com.hm.model.ImageFile;

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
		this.productRef = storage.getReference().child("products").child("images");
		this.imageRef = firestore.collection("images");
	}

	public void uploadProductImage(final String pid, final Uri uri, final boolean isDefault) {
		//add image to db
//		dialogUtilities.showProgressDialog("Haha", "Saving "+fileName,true);

		UploadTask uploadTask = productRef.child(pid + "/" + UUID.randomUUID().toString()).putFile(uri);

		uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
				String downloadUrl = taskSnapshot.getMetadata().getPath();
				String fileName = taskSnapshot.getMetadata().getName();

				ImageFile imageFile = new ImageFile();
				imageFile.setFileName(fileName);
				imageFile.setDownloadUrl(downloadUrl);

				if (isDefault)
					imageFile.setDefault(true);
				else
					imageFile.setDefault(false);

				writeDB(pid, imageFile);
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
			}
		});


	}

	private void writeDB(String pid, ImageFile file_data) {
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

	public StorageReference getDefaultIMage(String pid, String name){
		return productRef.child(pid).child("default").child(name);
	}

	public void uploadDefaultImage(final String pid, Uri default_image) {
		UploadTask uploadTask = productRef.child(pid + "/default/" + UUID.randomUUID().toString()).putFile(default_image);

		uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
				String downloadUrl = taskSnapshot.getMetadata().getPath();
				String fileName = taskSnapshot.getMetadata().getName();

				ImageFile imageFile = new ImageFile();
				imageFile.setFileName(fileName);
				imageFile.setDownloadUrl(downloadUrl);

				imageRef.document(pid).collection("images").document("default")
						.set(imageFile)
						.addOnCompleteListener(new OnCompleteListener<Void>() {
							@Override
							public void onComplete(@NonNull Task<Void> task) {
								if (task.isSuccessful()) {
									Toast.makeText(context, "default image saved", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(context, "failed to save image Ref", Toast.LENGTH_SHORT).show();
								}
							}
						});
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}
}

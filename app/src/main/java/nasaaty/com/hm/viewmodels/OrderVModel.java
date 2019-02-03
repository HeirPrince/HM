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

import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.room.HahaDB;

public class OrderVModel extends AndroidViewModel {

	FirebaseFirestore firebaseFirestore;
	HahaDB hahaDB;


	public OrderVModel(@NonNull Application application) {
		super(application);
		hahaDB = HahaDB.getInstance(this.getApplication());
		firebaseFirestore = FirebaseFirestore.getInstance();
	}

	public void insertOrder(final Order order){
		CollectionReference orderRef = firebaseFirestore.collection("orders");
		orderRef.add(order)
				.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
					@Override
					public void onComplete(@NonNull Task<DocumentReference> task) {
						if (task.isSuccessful()){
							new placeOrderAsync(hahaDB).execute(order);
						}
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {

					}
				});
	}

	class placeOrderAsync extends AsyncTask<Order, Void, Void>{

		HahaDB hahaDBase;

		public placeOrderAsync(HahaDB hahaDB) {
			this.hahaDBase = hahaDB;
		}

		@Override
		protected Void doInBackground(Order... orders) {
			hahaDB.orderEntity().placeOrder(orders[0]);
			return null;
		}
	}
}

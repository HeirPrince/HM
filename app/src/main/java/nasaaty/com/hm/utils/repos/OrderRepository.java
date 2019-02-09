package nasaaty.com.hm.utils.repos;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.utils.DialogUtilities;
import nasaaty.com.hm.viewmodels.OrderVModel;

public class OrderRepository {

	Context context;
	FirebaseFirestore firebaseFirestore;
	CollectionReference orderRef;
	HahaDB hahaDB;
	LiveData<List<Order>> ordersLiveData;
	DialogUtilities dialogUtilities;

	public OrderRepository(Context context) {
		this.context = context;
		this.firebaseFirestore = FirebaseFirestore.getInstance();
		this.orderRef = firebaseFirestore.collection("orders");
		this.hahaDB = HahaDB.getInstance(context);
		this.ordersLiveData = hahaDB.orderEntity().getOrders();
		this.dialogUtilities = new DialogUtilities(context);
	}

	public LiveData<Order> getOrder(int order_id){
		final MutableLiveData<Order> orderMutableLiveData = new MutableLiveData<>();
		new getDetailsAsync(hahaDB, orderMutableLiveData).execute(order_id);

		return orderMutableLiveData;
	}

	public LiveData<List<Order>> getAllOrders(){
		return ordersLiveData;
	}

	public void flush(){
		new deleteAsync(hahaDB).execute();
	}

	public void sync(List<Order> orders){
		dialogUtilities.showProgressDialog("Checkout", "Placing your orders", true);
		if (orders!= null){
			new syncOrders(firebaseFirestore).execute(orders);
		}
	}


	public void insertOrder(Order order){
		new insertOrderAsync(hahaDB).execute(order);
	}

	private class insertOrderAsync extends AsyncTask<Order, Void, Void>{

		HahaDB hahaDBase;

		public insertOrderAsync(HahaDB hahaDB) {
			this.hahaDBase = hahaDB;
		}

		@Override
		protected Void doInBackground(Order... orders) {
			hahaDBase.orderEntity().placeOrder(orders[0]);
			return null;
		}
	}

	private class deleteAsync extends AsyncTask<Void, Void, Void>{

		HahaDB hahaDBase;

		public deleteAsync(HahaDB hahaDB) {
			this.hahaDBase = hahaDB;
		}

		@Override
		protected Void doInBackground(Void... voids) {
			hahaDBase.orderEntity().deleteAll();
			return null;
		}
	}

	private class syncOrders extends AsyncTask<List<Order>, Void, Void>{

		FirebaseFirestore firestore;

		public syncOrders(FirebaseFirestore fstore) {
			this.firestore = fstore;
		}

		@Override
		protected Void doInBackground(List<Order>... lists) {
			for (Order order : lists[0]){
				DocumentReference pushRef = firestore.collection("orders").document();
				String id = pushRef.getId();
				DocumentReference saveRef = firestore.collection("orders").document(id);
				order.setOrder_id(id);
				saveRef.set(order)
						.addOnCompleteListener(new OnCompleteListener<Void>() {
							@Override
							public void onComplete(@NonNull Task<Void> task) {
								dialogUtilities.showProgressDialog("Checkout", "Placing your orders", false);
								dialogUtilities.showSuccessDialog("Checkout", "Thank you, you will receive your products very soon");
								flush();
								Toast.makeText(context, "save succeeded", Toast.LENGTH_SHORT).show();
							}
						})
						.addOnFailureListener(new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {
								Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
								dialogUtilities.showErrorDialog("Checkout", "Sorry there's been a problem "+e.getMessage());
							}
						});

			}

			return null;
		}
	}

	private class getDetailsAsync extends AsyncTask<Integer, Void, Void>{

		HahaDB hahaDBase;
		final MutableLiveData<Order> mutableLiveData;

		public getDetailsAsync(HahaDB hahaDB, MutableLiveData<Order> orderMutableLiveData) {
			this.hahaDBase = hahaDB;
			this.mutableLiveData = orderMutableLiveData;
		}

		@Override
		protected Void doInBackground(Integer... integers) {
			Order order = hahaDB.orderEntity().getOrderDetails(integers[0]);
			mutableLiveData.setValue(order);
			return null;
		}
	}

	public void isOrdered(final Order order){
		Query query = orderRef.whereEqualTo("product_id", order.getProduct_id());

		query.addSnapshotListener(new EventListener<QuerySnapshot>() {
			@Override
			public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
				if (e != null)
					e.printStackTrace();

				for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
					if (snapshot.exists()){

						Toast.makeText(context, "recent", Toast.LENGTH_SHORT).show();

					}else {
						Toast.makeText(context, "new", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	private void placeOrder(final Order order) {
		CollectionReference orderRef = firebaseFirestore.collection("orders");

		DocumentReference push = orderRef.document();
		String id = push.getId();
		DocumentReference oRef = orderRef.document(id);

		oRef.set(order)
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
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

	class placeOrderAsync extends AsyncTask<Order, Void, Void> {

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

	private void updateOrderCount(Order order) {
		if (order == null) {

		}else {
			int count = order.getCount() + 1;
			orderRef.document(order.getOrder_id())
					.update("count", count)
					.addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if (task.isSuccessful()){
								return;
							}
						}
					});
		}
	}

}

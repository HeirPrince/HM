package nasaaty.com.hm.utils.repos;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import nasaaty.com.hm.model.MyOrder;
import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.utils.DialogUtilities;

public class OrderRepository {

	static Context context;
	FirebaseFirestore firebaseFirestore;
	static FirebaseAuth auth;
	CollectionReference orderRef;
	static HahaDB hahaDB;
	static DialogUtilities dialogUtilities;
	LiveData<List<Product>> ordersLiveData;
	Boolean isReg;

	public OrderRepository(Context context) {
		this.context = context;
		this.firebaseFirestore = FirebaseFirestore.getInstance();
		this.auth = FirebaseAuth.getInstance();
		this.orderRef = firebaseFirestore.collection("orders");
		this.hahaDB = HahaDB.getInstance(context);
		this.ordersLiveData = hahaDB.orderEntity().getOrders();
		this.dialogUtilities = new DialogUtilities(context);
	}

	public static void flush() {
		new deleteAsync(hahaDB).execute();
	}

	public LiveData<Product> getOrder(int pid) {
		final MutableLiveData<Product> orderMutableLiveData = new MutableLiveData<>();
		new getDetailsAsync(hahaDB, orderMutableLiveData).execute(pid);

		return orderMutableLiveData;
	}

	public LiveData<List<Product>> getAllOrders() {
		return ordersLiveData;
	}

	public void sync(Order order) {
		dialogUtilities.showProgressDialog("Checkout", "Placing your orders", true);
		if (order != null) {
			new syncOrders(firebaseFirestore).execute(order);
		}
	}


	public void insertOrder(Product product) {
		new insertOrderAsync(hahaDB).execute(product);
	}

	public void delete(Product product) {
		new deleteOrderAsync(hahaDB).execute(product);
	}

//	public void checkIfExists(String pid){
//		checkOrderAsync chk = new checkOrderAsync(hahaDB);
//		chk.execute(pid);
//	}

	private void updateOrderCount(MyOrder order) {
		if (order == null) {

		} else {
			int count = order.getCount() + 1;
			orderRef.document(order.getOrder_id())
					.update("count", count)
					.addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if (task.isSuccessful()) {
								return;
							}
						}
					});
		}
	}

	private interface AsyncResponse {
		void response(String result);
	}

	private static class deleteAsync extends AsyncTask<Void, Void, Void> {

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

//	private class checkOrderAsync extends AsyncTask<Void, Void, Void>{
//
//		HahaDB hahaDBase;
//
//		public checkOrderAsync(HahaDB hahaDB) {
//			this.hahaDBase = hahaDB;
//			isReg = false;
//		}
//
//		@Override
//		protected Product doInBackground(String... strings) {
//			Product pid = hahaDB.orderEntity().getOrderByID(strings[0]);
//			return pid;
//		}
//
//		@Override
//		protected void onPostExecute(String s) {
//			super.onPostExecute(s);
//			if (TextUtils.isEmpty(s)){
//				isReg = false;
//				check();
//			}
//			else {
//				Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
//				isReg = true;
//				check();
//			}
//		}
//
//		@Override
//		protected Void doInBackground(Void... voids) {
//			return null;
//		}
//	}

	public Boolean check() {
		if (isReg)
			return true;
		else
			return false;
	}

	private static class syncOrders extends AsyncTask<Order, Void, Void> {

		FirebaseFirestore firestore;


		public syncOrders(FirebaseFirestore fstore) {
			this.firestore = fstore;
		}

		@Override
		protected Void doInBackground(Order... orders) {

			CollectionReference cartRef = firestore.collection("carts").document(orders[0].getProduct().getOwner()).collection("orders");

//			Order order = new Order();
//			order.setCustomer_id(auth.getCurrentUser().getUid());
//			order.setProduct(null);
//			order.setTimeStamp(TimeUts.getTimeStamp());

			cartRef.add(orders[0])
					.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
						@Override
						public void onSuccess(DocumentReference documentReference) {

							dialogUtilities.showProgressDialog("Checkout", "Placing your orders", false);
							dialogUtilities.showSuccessDialog("Checkout", "Thank you, you will receive your products very soon");
							flush();
						}
					}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
					dialogUtilities.showErrorDialog("Checkout", "Sorry there's been a problem " + e.getMessage());
				}
			});
			return null;
		}
	}

	private class insertOrderAsync extends AsyncTask<Product, Void, Void> {

		HahaDB hahaDBase;

		public insertOrderAsync(HahaDB hahaDB) {
			this.hahaDBase = hahaDB;
		}

		@Override
		protected Void doInBackground(Product... products) {
			hahaDBase.orderEntity().placeOrder(products[0]);
			return null;
		}
	}

	private class deleteOrderAsync extends AsyncTask<Product, Void, Void> {

		HahaDB hahaDBase;

		public deleteOrderAsync(HahaDB hahaDB) {
			this.hahaDBase = hahaDB;
		}

		@Override
		protected Void doInBackground(Product... products) {
			hahaDBase.orderEntity().deleteOrder(products[0]);
			return null;
		}
	}

	private class getDetailsAsync extends AsyncTask<Integer, Void, Void> {

		HahaDB hahaDBase;
		final MutableLiveData<Product> mutableLiveData;

		public getDetailsAsync(HahaDB hahaDB, MutableLiveData<Product> orderMutableLiveData) {
			this.hahaDBase = hahaDB;
			this.mutableLiveData = orderMutableLiveData;
		}

		@Override
		protected Void doInBackground(Integer... integers) {
			Product product = hahaDB.orderEntity().getOrderDetails(integers[0]);
			mutableLiveData.setValue(product);
			return null;
		}
	}

	class placeOrderAsync extends AsyncTask<Product, Void, Void> {

		HahaDB hahaDBase;

		public placeOrderAsync(HahaDB hahaDB) {
			this.hahaDBase = hahaDB;
		}

		@Override
		protected Void doInBackground(Product... products) {
			hahaDB.orderEntity().placeOrder(products[0]);
			return null;
		}
	}

}

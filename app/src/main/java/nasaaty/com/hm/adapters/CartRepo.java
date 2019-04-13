package nasaaty.com.hm.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import nasaaty.com.hm.model.Order;

public abstract class CartRepo
		<VH extends RecyclerView.ViewHolder>
		extends RecyclerView.Adapter<VH>
		implements com.google.firebase.firestore.EventListener<QuerySnapshot> {

	private Context context;
	private Query mQuery;
	private ListenerRegistration mRegistration;

	private List<Order> orders = new ArrayList<>();

	CartRepo(Context context, Query mQuery) {
		this.context = context;
		this.mQuery = mQuery;
	}

	@Override
	public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
		if (e != null)
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

		for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()){
			switch (change.getType()){
				case ADDED:
					onDocumentAdded(change);
					break;
				case MODIFIED:
					onDocumentModified(change);
					break;
				case REMOVED:
					onDocumentRemoved(change);
					break;
			}
		}

		onDataChanged();
	}

	public void startListening(){
		if (mQuery != null && mRegistration == null){
			mRegistration = mQuery.addSnapshotListener(this);
		}
	}

	public void stopListening(){
		if (mRegistration != null){
			mRegistration.remove();
			mRegistration = null;
		}

		orders.clear();
		notifyDataSetChanged();
	}

	public void setmQuery(Query query){
		//Stop listening
		stopListening();

		//Clear existing data
		orders.clear();
		notifyDataSetChanged();

		//Listen to new query
		mQuery = query;
		startListening();
	}

	@Override
	public int getItemCount() {
		return orders.size();
	}

	protected Order getCart(int index){
		return orders.get(index);
	}

	public List<Order> getCarts() {
		return orders;
	}

	protected void onDocumentAdded(DocumentChange change){
		orders.add(change.getNewIndex(), change.getDocument().toObject(Order.class));
		notifyItemInserted(change.getNewIndex());
	}

	protected void onDocumentModified(DocumentChange change){
		if (change.getOldIndex() == change.getNewIndex()){
			orders.set(change.getOldIndex(), change.getDocument().toObject(Order.class));
		}else {
			orders.remove(change.getOldIndex());
			orders.add(change.getNewIndex(), change.getDocument().toObject(Order.class));
			notifyItemMoved(change.getOldIndex(), change.getNewIndex());
		}
	}

	protected void onDocumentRemoved(DocumentChange change) {
		orders.remove(change.getOldIndex());
		notifyItemRemoved(change.getOldIndex());
	}

	protected void onError(FirebaseFirestoreException e){
		Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
	}

	protected void onDataChanged(){}

}

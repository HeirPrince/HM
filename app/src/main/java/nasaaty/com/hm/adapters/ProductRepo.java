package nasaaty.com.hm.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import nasaaty.com.hm.model.Product;

public abstract class ProductRepo<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>
 implements EventListener<QuerySnapshot> {

	private Context context;
	private Query mQuery;
	private ListenerRegistration mRegistration;

	private List<Product> products = new ArrayList<>();

	public ProductRepo(Context context, Query mQuery) {
		this.context = context;
		this.mQuery = mQuery;
	}

	@Override
	public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
		if (e != null)
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

		for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()){
			switch (change.getType()) {
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

	public void startListening() {
		if (mQuery != null && mRegistration == null) {
			mRegistration = mQuery.addSnapshotListener(this);
		}
	}

	public void stopListening() {
		if (mRegistration != null) {
			mRegistration.remove();
			mRegistration = null;
		}

		products.clear();
		notifyDataSetChanged();
	}

	public void setQuery(Query query) {
		// Stop listening
		stopListening();

		// Clear existing data
		products.clear();
		notifyDataSetChanged();

		// Listen to new query
		mQuery = query;
		startListening();
	}

	@Override
	public int getItemCount() {
		return products.size();
	}

	protected Product getProduct(int index){
		return products.get(index);
	}

	protected List<Product> getProducts(){
		return products;
	}

	protected void onDocumentAdded(DocumentChange change) {
		products.add(change.getNewIndex(), change.getDocument().toObject(Product.class));
		notifyItemInserted(change.getNewIndex());
	}

	protected void onDocumentModified(DocumentChange change) {
		if (change.getOldIndex() == change.getNewIndex()) {
			// Item changed but remained in same position
			products.set(change.getOldIndex(), change.getDocument().toObject(Product.class));
			notifyItemChanged(change.getOldIndex());
		} else {
			// Item changed and changed position
			products.remove(change.getOldIndex());
			products.add(change.getNewIndex(), change.getDocument().toObject(Product.class));
			notifyItemMoved(change.getOldIndex(), change.getNewIndex());
		}
	}

	protected void onDocumentRemoved(DocumentChange change) {
		products.remove(change.getOldIndex());
		notifyItemRemoved(change.getOldIndex());
	}

	protected void onError(FirebaseFirestoreException e) {
		Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
	};

	protected void onDataChanged(){}
}

package nasaaty.com.hm.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.Product;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardVHolder> {

	private Context context;
	private List<Query> queries;
	private FirebaseFirestore firestore;

	public CardAdapter(Context context, List<Query> queries) {
		this.context = context;
		this.queries = queries;
		this.firestore = FirebaseFirestore.getInstance();
	}

	@NonNull
	@Override
	public CardVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_item, parent, false);
		return new CardVHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull final CardVHolder holder, final int position) {
		final Query query = queries.get(position);

		final List<Product> products = new ArrayList<>();
		query.addSnapshotListener(new EventListener<QuerySnapshot>() {
			@Override
			public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
				if (null != e)
					return;

				for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()){
					if (change.getDocument().exists()){
						Product product = change.getDocument().toObject(Product.class);
						products.add(product);

						holder.list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
						holder.list.setItemAnimator(new DefaultItemAnimator());
						holder.title.setText(String.format("#"+getItemCount()));
						notifyDataSetChanged();
					}else {
						notifyItemRemoved(position);
						notifyDataSetChanged();
					}

				}

				SmallProductAdapter adapter = new SmallProductAdapter(context, products);
				holder.list.setAdapter(adapter);

				holder.more.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						queries.remove(position);
						notifyItemRemoved(position);
						notifyDataSetChanged();
					}
				});
			}
		});
	}

	@Override
	public int getItemCount() {
		return queries.size();
	}

	class CardVHolder extends RecyclerView.ViewHolder {

		TextView title;
		Button more;
		RecyclerView list;


		public CardVHolder(View itemView) {
			super(itemView);
			title = itemView.findViewById(R.id.title);
			more = itemView.findViewById(R.id.moreBtn);
			list = itemView.findViewById(R.id.list);
		}
	}

}

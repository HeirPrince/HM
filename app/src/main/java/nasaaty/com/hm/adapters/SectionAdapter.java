package nasaaty.com.hm.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.Category;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.utils.Constants;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.CardVHolder> {

	private Context context;
	private List<Category> categories;
	private RecyclerView.RecycledViewPool recycledViewPool;
	private SnapHelper snapHelper;
	private FirebaseFirestore firestore;
	private Constants constants;

	public SectionAdapter(Context context, List<Category> categories) {
		this.context = context;
		this.constants = new Constants();
		this.categories = categories;
		this.recycledViewPool = new RecyclerView.RecycledViewPool();
		this.firestore = FirebaseFirestore.getInstance();
	}

	private List<Category> getCats(final List<String> categories) {
		final List<Product> products = new ArrayList<>();
		final List<Category> cats = new ArrayList<>();
		for (int i = 1; i<= categories.size(); i++){
			final Category category = new Category();
			category.setText(categories.get(i));

			Query query = firestore.collection("products").whereEqualTo("category", category.getText());

			query.addSnapshotListener(new EventListener<QuerySnapshot>() {
				@Override
				public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
					if (null != e)
						return;

					for (DocumentSnapshot item : queryDocumentSnapshots){
						if (item.exists()){
							Product product = item.toObject(Product.class);
							products.add(product);
							cats.add(category);
						}else
							return;
					}
				}
			}) ;

			return cats;
		}
		return null;
	}

	@NonNull
	@Override
	public CardVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_item, parent, false);
		snapHelper = new GravitySnapHelper(Gravity.START);
		return new CardVHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull final CardVHolder holder, final int position) {
		final Category category = categories.get(position);
		final String section_name = category.getText();
		final List<Product> products = new ArrayList<>();

		category.getQuery().addSnapshotListener(new EventListener<QuerySnapshot>() {
			@Override
			public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
				if (null != e)
					return;
				for (DocumentSnapshot snapshot : queryDocumentSnapshots){
					if (snapshot.exists()){
						Product product = snapshot.toObject(Product.class);
						products.add(product);
						
						SmallProductAdapter adapter = new SmallProductAdapter(context, products);
						holder.list.setHasFixedSize(true);
						holder.list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
						holder.list.setAdapter(adapter);
						holder.title.setText(section_name);
					}
				}
			}
		});

		holder.list.setRecycledViewPool(recycledViewPool);
		snapHelper.attachToRecyclerView(holder.list);
		holder.more.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(context, "more", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public int getItemCount() {
		return (null != categories ? categories.size() : 0);
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

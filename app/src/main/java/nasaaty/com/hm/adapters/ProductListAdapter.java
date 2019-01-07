package nasaaty.com.hm.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.Product;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.productVHolder>{

	Context context;
	List<Product> products;

	@Override
	public productVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new productVHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false));
	}

	@Override
	public void onBindViewHolder(productVHolder holder, int position) {
		Product product = products.get(position);
		holder.label.setText(product.getLabel());
		holder.price.setText(String.valueOf(product.getPrice()));
	}

	@Override
	public int getItemCount() {
		return products.size();
	}

	class productVHolder extends RecyclerView.ViewHolder {

		TextView label, price;

		public productVHolder(View itemView) {
			super(itemView);
			label = itemView.findViewById(R.id.label);
			price = itemView.findViewById(R.id.price);
		}
	}

}

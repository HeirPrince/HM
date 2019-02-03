package nasaaty.com.hm.adapters;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.viewmodels.OrderVModel;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.productVHolder>{

	Context context;
	List<Product> products;
	OrderVModel vModel;

	public ProductListAdapter(Context context, List<Product> products, OrderVModel vModel) {
		this.context = context;
		this.products = products;
		this.vModel = vModel;
	}

	@Override
	public productVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new productVHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_item, parent, false));
	}

	@Override
	public void onBindViewHolder(productVHolder holder, int position) {
		final Product product = products.get(position);
		holder.label.setText(product.getLabel());
		holder.price.setText(String.valueOf(product.getPrice()));
		holder.desc.setText(String.valueOf(product.getDescription()));

		//place order
		holder.plc_order.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Order order = new Order();
				order.setOwner(product.getOwner());
				order.setProduct_id(product.getPid());

				vModel.insertOrder(order);
			}
		});
	}

	@Override
	public int getItemCount() {
		return products.size();
	}

	class productVHolder extends RecyclerView.ViewHolder {

		TextView label, price, desc;
		Button plc_order;
		ImageView pro_image;

		public productVHolder(View itemView) {
			super(itemView);
			label = itemView.findViewById(R.id.product_title);
			price = itemView.findViewById(R.id.product_price);
			desc = itemView.findViewById(R.id.product_description);
			plc_order = itemView.findViewById(R.id.plc_order);
			pro_image = itemView.findViewById(R.id.product_image);
		}
	}

}

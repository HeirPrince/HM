package nasaaty.com.hm.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.utils.DialogUtilities;
import nasaaty.com.hm.viewmodels.OrderVModel;

public class NProductListAdapter extends RecyclerView.Adapter<NProductListAdapter.productVHolder>{

	Context context;
	List<Product> products;
	OrderVModel vModel;
	int count = 0;
	DialogUtilities dialogUtilities;


	public NProductListAdapter(Context context, List<Product> products, OrderVModel vModel, DialogUtilities utilities) {
		this.context = context;
		this.products = products;
		this.vModel = vModel;
		this.dialogUtilities = utilities;
	}

	@Override
	public productVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new productVHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_item_add, parent, false));
	}

	@Override
	public void onBindViewHolder(final productVHolder holder, int position) {
		final Product product = products.get(position);
		holder.label.setText(product.getLabel());
		holder.price.setText(String.valueOf(product.getPrice()));
//		holder.desc.setText(String.valueOf(product.getDescription()));
		//place order
		holder.add_product.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if(vModel.checkIfProductExists(product.getPid())){
					dialogUtilities.showErrorDialog("Insert Product", "This Order has already been placed");
				}else {
					Order order = new Order();
					order.setOwner(product.getOwner());
					order.setProduct_id(product.getPid());
//				vModel.getDetails()
//
					vModel.insertOrder(order);
				}

			}
		});
	}

	public void increaseInteger(Button plc_order) {

		count = count + 1;
		plc_order.setText("Amt : "+String.valueOf(count));
	}

	@Override
	public int getItemCount() {
		return products.size();
	}

	class productVHolder extends RecyclerView.ViewHolder {

		TextView label, price, desc;
		Button add_product;
		ImageView pro_image;

		public productVHolder(View itemView) {
			super(itemView);
			label = itemView.findViewById(R.id.p_title);
			price = itemView.findViewById(R.id.p_price);
//			desc = itemView.findViewById(R.id.product_description);
			add_product = itemView.findViewById(R.id.add);
			pro_image = itemView.findViewById(R.id.p_image);
		}
	}

}

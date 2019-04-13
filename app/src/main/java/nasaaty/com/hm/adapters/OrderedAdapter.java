package nasaaty.com.hm.adapters;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import nasaaty.com.hm.R;
import nasaaty.com.hm.model.Order;
import nasaaty.com.hm.model.User;
import nasaaty.com.hm.utils.StorageRepository;
import nasaaty.com.hm.utils.TimeUts;
import nasaaty.com.hm.viewmodels.UserVModel;

public class OrderedAdapter extends CartRepo<OrderedAdapter.OrderedVHolder> {

	Context context;
	FirebaseFirestore firestore;
	FirebaseAuth firebaseAuth;
	StorageRepository repository;
	UserVModel userVModel;

	public OrderedAdapter(Context context, Query mQuery, UserVModel userVModel) {
		super(context, mQuery);
		this.context = context;
		firestore = FirebaseFirestore.getInstance();
		firebaseAuth = FirebaseAuth.getInstance();
		repository = new StorageRepository(context);
		this.userVModel = userVModel;
	}

	@NonNull
	@Override
	public OrderedVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		return new OrderedVHolder(inflater.inflate(R.layout.layout_ordererd_item, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull OrderedVHolder holder, int position) {
		holder.bind(getCart(position));
	}

	@Override
	public int getItemCount() {
		return getCarts().size();
	}

	class OrderedVHolder extends RecyclerView.ViewHolder {

		TextView username, user_phone, time, price;
		CircleImageView user_image;
		Button view_more;

		OrderedVHolder(View itemView) {
			super(itemView);
			username = itemView.findViewById(R.id.user_name);
			user_phone = itemView.findViewById(R.id.user_phone);
			time = itemView.findViewById(R.id.time);
			price = itemView.findViewById(R.id.total_amt);
			view_more = itemView.findViewById(R.id.view_list);
			user_image = itemView.findViewById(R.id.u_image);
		}

		public void bind(Order order){
			//get user details
			userVModel.getUserDetails(order.getCustomer_id()).observe((LifecycleOwner) context, new Observer<DocumentSnapshot>() {
				@Override
				public void onChanged(@Nullable DocumentSnapshot documentSnapshot) {
					User user = documentSnapshot.toObject(User.class);
					if (user != null){
						username.setText(user.getName());
						user_phone.setText(user.getPhoneNum());
						Picasso.get().load(user.getPhotoUrl()).into(user_image);
					}
				}
			});

			String tm = TimeUts.getTmAgo(context, order.getTimeStamp());
			time.setText(tm);

			price.setText(String.format("%s RF", order.getProduct().getPrice()));

			view_more.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Toast.makeText(context, "view more "+getItemCount()+" products", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}

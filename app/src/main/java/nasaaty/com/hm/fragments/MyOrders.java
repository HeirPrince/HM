package nasaaty.com.hm.fragments;


import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.michaelbel.bottomsheet.BottomSheet;

import java.util.ArrayList;

import nasaaty.com.hm.R;
import nasaaty.com.hm.adapters.OrderedAdapter;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.utils.Constants;
import nasaaty.com.hm.viewmodels.UserVModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyOrders extends Fragment {

	RecyclerView order_list;
	private FirebaseFirestore firestore;
	private FirebaseAuth firebaseAuth;
	private UserVModel userVModel;
	private Query mQuery;
	private OrderedAdapter adapter;
	HahaDelivery sheetMethod;
	Constants constants;
	Product edProduct;

	sendDetailsToSheet callback;


	public MyOrders() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_my_orders, container, false);
		order_list = view.findViewById(R.id.list);
		order_list.setLayoutManager(new LinearLayoutManager(getContext()));
		order_list.setHasFixedSize(true);
		order_list.setItemAnimator(new DefaultItemAnimator());
		sheetMethod = new HahaDelivery();
	 	constants = new Constants();


		FirebaseFirestore.setLoggingEnabled(true);
		firestore = FirebaseFirestore.getInstance();
		firebaseAuth = FirebaseAuth.getInstance();
		userVModel = ViewModelProviders.of(getActivity()).get(UserVModel.class);

		String myId = firebaseAuth.getCurrentUser().getUid();

		mQuery = firestore.collection("carts").document(myId).collection("orders");

		getData();

		return view;
	}

	private void getData() {
		adapter = new OrderedAdapter(getContext(), getActivity().getSupportFragmentManager(), mQuery, userVModel, new OrderedAdapter.onclickedProduct()
		{
			@Override
			public void clicked(Product product) {
				setupBottomSheet(product);
				callback.details(product);
				edProduct = product;
			}
		})
		{
			@Override
			protected void onDataChanged() {
				if (getItemCount() == 0){
					Toast.makeText(getContext(), "no orders yet", Toast.LENGTH_SHORT).show();
					order_list.setVisibility(View.GONE);
				}else {
					order_list.setVisibility(View.VISIBLE);
				}
			}

			@Override
			protected void onError(FirebaseFirestoreException e) {
				super.onError(e);
			}
		};

		order_list.setAdapter(adapter);
	}

	private void setupBottomSheet(final Product product) {
		BottomSheet.Builder bottomBuilder = new BottomSheet.Builder(getContext());
		bottomBuilder.setTitle("Choose Method");


		//add items
		bottomBuilder.setMenu(R.menu.method_menu, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				switch (i){
					case 0:
						showDialog(product);
						break;
					case 1:
						Permissions.check(getContext(), Manifest.permission.CALL_PHONE, null, new PermissionHandler() {
							@Override
							public void onGranted() {

								Intent callIntent = new Intent(Intent.ACTION_CALL);
								callIntent.setData(Uri.parse("tel:"+782085695));//change the number
								startActivity(callIntent);
							}

							@Override
							public void onDenied(Context context, ArrayList<String> deniedPermissions) {
								super.onDenied(context, deniedPermissions);
								if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)){
									Toast.makeText(context, "please this permission is needed, allow it.", Toast.LENGTH_SHORT).show();
								}
							}
						});
						break;
					case 2:
						Toast.makeText(getContext(), "Send Message", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		});

		bottomBuilder.setContentType(BottomSheet.LIST);
		bottomBuilder.show();

	}

	private void showDialog(Product product) {

		HahaDelivery dialog = new HahaDelivery();

		Bundle b = new Bundle();

		b.putString("owner", product.getOwner());
		b.putString("pid", product.getPid());
		b.putString("label", product.getLabel());
		b.putInt("price", product.getPrice());
		b.putDouble("rating", product.getAvgRatings());
		b.putString("timestamp", product.getTimeStamp());
		b.putString("category", product.getCategory());
		b.putString("description", product.getDescription());
		b.putInt("numRatings", product.getNumRatings());

		dialog.setArguments(b);

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		dialog.show(ft, HahaDelivery.TAG);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof sendDetailsToSheet) {
			callback = (sendDetailsToSheet) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnGreenFragmentListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		callback = null;
	}

	public interface sendDetailsToSheet{
		void details(Product product);
	}

	@Override
	public void onStart() {
		super.onStart();
		adapter.startListening();
	}

	@Override
	public void onStop() {
		super.onStop();
		adapter.stopListening();
	}
}

package nasaaty.com.hm.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import nasaaty.com.hm.R;
import nasaaty.com.hm.adapters.OrderedAdapter;
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
		adapter = new OrderedAdapter(getContext(), mQuery, userVModel){
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

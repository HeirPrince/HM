package nasaaty.com.hm.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.activities.AddProduct;
import nasaaty.com.hm.adapters.ProductListAdapter;
import nasaaty.com.hm.model.Section;
import nasaaty.com.hm.utils.Constants;
import nasaaty.com.hm.utils.StorageRepository;
import nasaaty.com.hm.viewmodels.OrderVModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class Explore extends Fragment {

	StorageRepository storageRepository;
	List<Section> sections;
	Constants constants;
	RecyclerView list;
	FloatingActionButton fab;
	private FirebaseAuth firebaseAuth;
	private FirebaseFirestore firestore;
	private OrderVModel orderVModel;
	private Query mQuery;
	private ProductListAdapter mAdapter;

	public Explore() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_explore, container, false);

		FirebaseFirestore.setLoggingEnabled(true);
		firestore = FirebaseFirestore.getInstance();
		firebaseAuth = FirebaseAuth.getInstance();
		list = view.findViewById(R.id.product_list);
		fab = view.findViewById(R.id.fab);

		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setHasFixedSize(true);
		list.setItemAnimator(new DefaultItemAnimator());

		constants = new Constants();

		mQuery = firestore.collection("products").orderBy("avgRatings", Query.Direction.DESCENDING);
		sections = new ArrayList<>();

		storageRepository = new StorageRepository(getContext());
		orderVModel = ViewModelProviders.of(this).get(OrderVModel.class);
		
		getData();
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				addProduct();
			}
		});

		return view;
	}

	private void getData() {
		mAdapter = new ProductListAdapter(getContext(), mQuery, orderVModel){
			@Override
			protected void onDataChanged() {
				if (getItemCount() == 0) {
					list.setVisibility(View.GONE);
					//empty view
				} else {
					list.setVisibility(View.VISIBLE);
					//empty view
				}
			}

			@Override
			protected void onError(FirebaseFirestoreException e) {
				super.onError(e);
			}
		};

		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setAdapter(mAdapter);
	}

	public void refreshLists(List<Section> sections){
		if (!sections.isEmpty()){
			sections.clear();
			list.setVisibility(View.VISIBLE);
		}else {
			list.setVisibility(View.GONE);
		}
	}



	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getData();
	}

//	public void getD() {
//
//		mAdapter = new ProductListAdapter(getContext(), mQuery, orderVModel){
//			@Override
//			protected void onDataChanged() {
//				if (getItemCount() == 0){
//					list.setVisibility(View.GONE);
//					//show empty view
//				} else {
//					list.setVisibility(View.VISIBLE);
//					//hide empty view
//				}
//			}
//
//			@Override
//			protected void onError(FirebaseFirestoreException e) {
//				Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//			}
//		};
//
//		list.setLayoutManager(new LinearLayoutManager(getContext()));
//		list.setAdapter(mAdapter);
//	}


	@Override
	public void onStart() {
		super.onStart();
		mAdapter.startListening();
	}

	@Override
	public void onStop() {
		super.onStop();
		mAdapter.stopListening();
	}

	public void addProduct() {
		getContext().startActivity(new Intent(getContext(), AddProduct.class));
	}
}

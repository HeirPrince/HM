package nasaaty.com.hm.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import nasaaty.com.hm.R;
import nasaaty.com.hm.adapters.ProductListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class onDelivery extends DialogFragment {

	public static String TAG = "on";
	FirebaseFirestore firestore;
	FirebaseAuth auth;
	RecyclerView list;
	ProgressBar progressBar;
	private ProductListAdapter mAdapter;
	private Query mQuery;

	public onDelivery() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
		firestore = FirebaseFirestore.getInstance();
		auth = FirebaseAuth.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_on_delivery, container, false);

		progressBar = view.findViewById(R.id.progress);
		list = view.findViewById(R.id.onlist);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		mQuery = firestore.collection("products").orderBy("avgRatings", Query.Direction.DESCENDING);

		android.support.v7.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
		toolbar.setNavigationIcon(R.drawable.vector_close);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				 getDialog().dismiss();
			}
		});

		toolbar.setTitle("Products on Delivery");
		getProducts();

		return view;
	}

	private void getProducts() {
		progressBar.setVisibility(View.VISIBLE);
		mAdapter = new ProductListAdapter(getContext(), mQuery, null){
			@Override
			protected void onDataChanged() {
				if (getItemCount() == 0) {
					progressBar.setVisibility(View.VISIBLE);
					list.setVisibility(View.GONE);
					//empty view
				} else {
					progressBar.setVisibility(View.GONE);
					list.setVisibility(View.VISIBLE);
					//empty view
				}
			}

			@Override
			protected void onError(FirebaseFirestoreException e) {
				super.onError(e);
			}
		};

		list.setAdapter(mAdapter);
	}

	@Override
	public void onStart() {
		super.onStart();
		Dialog dialog = getDialog();
		if (dialog != null) {
			int width = ViewGroup.LayoutParams.MATCH_PARENT;
			int height = ViewGroup.LayoutParams.MATCH_PARENT;
			dialog.getWindow().setLayout(width, height);
		}
	}
}

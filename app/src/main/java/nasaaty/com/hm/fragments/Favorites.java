package nasaaty.com.hm.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.adapters.SectionAdapter;
import nasaaty.com.hm.model.Category;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class Favorites extends Fragment {

	FirebaseFirestore firestore;
	ListenerRegistration listenerRegistration;
	FirebaseAuth auth;
	List<Category> categories;
	List<Product> productList;
	RecyclerView list;


	public Favorites() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_favorites, container, false);
		categories = new ArrayList<>();
		productList = new ArrayList<>();
		auth = FirebaseAuth.getInstance();
		firestore = FirebaseFirestore.getInstance();

		list = view.findViewById(R.id.list);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setItemAnimator(new DefaultItemAnimator());
		setCategories();

		SectionAdapter adapter = new SectionAdapter(getContext(), categories);
		list.setAdapter(adapter);

		return view;
	}

	public void setCategories() {
		Constants constants = new Constants();
		final List<String> cats = constants.getCategories();

		for (int i = 0; i < cats.size(); i++) {
			Category category = new Category();
			category.setText(cats.get(i));

			Query query = firestore.collection("products").whereEqualTo("category", cats.get(i));
			category.setQuery(query);
			categories.add(category);
		}

	}
}

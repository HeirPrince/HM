package nasaaty.com.hm.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import nasaaty.com.hm.R;
import nasaaty.com.hm.activities.Track_Order;
import nasaaty.com.hm.model.Product;

/**
 * A simple {@link Fragment} subclass.
 */
public class HahaDelivery extends DialogFragment{

	public static String TAG = "FullScreenDialog";
	FirebaseFirestore firestore;
	FirebaseAuth auth;
	AwesomeProgressDialog dialog;

	public HahaDelivery() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
		firestore = FirebaseFirestore.getInstance();
		auth = FirebaseAuth.getInstance();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_haha_delivery, container, false);
		Toolbar toolbar = view.findViewById(R.id.toolbar);
		toolbar.setNavigationIcon(R.drawable.vector_close);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getDialog().dismiss();
			}
		});

		toolbar.setTitle("Deliver with us");
		readOrder(view);

		return view;

	}

	private void readOrder(View view) {
		//init views
		ImageView imageView = view.findViewById(R.id.p_image);
		TextView label = view.findViewById(R.id.p_title);
		TextView price = view.findViewById(R.id.p_price);
		TextView fee = view.findViewById(R.id.fees);
		Button track = view.findViewById(R.id.trckBtn);
		AppCompatRatingBar ratingBar = view.findViewById(R.id.ratingBar);

		final Bundle bundle = getArguments();

		//Deserialize Product
		String owner = bundle.getString("owner");
		String pid = bundle.getString("pid");
		String lbl = bundle.getString("label");
		final int prc = bundle.getInt("price");
		double rating = bundle.getDouble("rating");
		String tstamp = bundle.getString("timestamp");
		String cat = bundle.getString("category");
		String desc = bundle.getString("description");
		int numRatings = bundle.getInt("numRatings");

		//Assembling Product
		final Product product = new Product();
		product.setPid(pid);
		product.setLabel(lbl);
		product.setPrice(prc);
		product.setDescription(desc);
		product.setOwner(owner);
		product.setCategory(cat);
		product.setNumRatings(numRatings);
		product.setAvgRatings(rating);
		product.setTimeStamp(tstamp);

		label.setText(product.getLabel());
		price.setText(String.format("%s RF", String.valueOf(product.getPrice())));

		int fees = (product.getPrice() * 5) / 100;//5%
		fee.setText(String.format("%s RF", String.valueOf(fees)));

		ratingBar.setRating((float) product.getAvgRatings());

		track.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				deliver(product);
			}
		});

	}

	public void deliver(Product product){

		showProgress(true);

		firestore.collection("hahaDelivery").document(auth.getCurrentUser().getUid()).collection(product.getPid())
				.add(product)
				.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
					@Override
					public void onComplete(@NonNull Task<DocumentReference> task) {
						showProgress(false);
						startActivity(new Intent(getActivity(), Track_Order.class));
						getDialog().dismiss();
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Toast.makeText(getContext(), "delivery failed", Toast.LENGTH_SHORT).show();
					}
				});
	}

	private void showProgress(boolean b) {
		if (b && dialog == null){
			dialog = new AwesomeProgressDialog(getContext());
			dialog
					.setTitle("Haha Delivery")
					.setMessage("Searching for nearby Delivery Boys")
					.setColoredCircle(R.color.dialogInfoBackgroundColor)
					.setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
					.setCancelable(true)
					.show();
		}else {
			dialog.hide();
		}
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

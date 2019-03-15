package nasaaty.com.hm.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.franmontiel.fullscreendialog.FullScreenDialogContent;
import com.franmontiel.fullscreendialog.FullScreenDialogController;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.Review;
import nasaaty.com.hm.utils.repos.TransactionRepository;

/**
 * A simple {@link Fragment} subclass.
 */
public class RevFragment extends Fragment implements FullScreenDialogContent {

	public static final String EXTRA_RATE = "rate";
	public static final String EXTRA_UID = "uid";
	public static final String EXTRA_PID = "pid";
	TextView info;
	AppCompatRatingBar ratingBar;
	EditText rev;
	private FullScreenDialogController dialogController;
	private TransactionRepository transactionRepository;


	public RevFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.activity_review_p, container, false);
		transactionRepository = new TransactionRepository(getContext());
		bindViews(view);
		return view;
	}

	private void bindViews(View view) {
		info = view.findViewById(R.id.info);
		ratingBar = view.findViewById(R.id.stars);
		rev = view.findViewById(R.id.rev);

		setViews();
	}

	private void setViews() {
		float rate = getArguments().getFloat(EXTRA_RATE);
		ratingBar.setRating(rate);
	}

	@Override
	public void onDialogCreated(FullScreenDialogController dialogController) {
		this.dialogController = dialogController;
	}

	@Override
	public boolean onConfirmClick(FullScreenDialogController dialogController) {
		Float rate = ratingBar.getRating();
		String review = rev.getText().toString();

		Review r = new Review();
		r.setReview(review);
		r.setUid(getArguments().getString(EXTRA_UID));

		transactionRepository.addRating(getArguments().getString(EXTRA_PID), r, rate);
		dialogController.discard();
		return true;
	}

	@Override
	public boolean onDiscardClick(final FullScreenDialogController dialogController) {
		new AlertDialog.Builder(getContext())
				.setTitle(R.string.discard_confirmation_title)
				.setMessage(R.string.discard_confirmation_message)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialogController.discard();
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Nothing to do
					}
				}).show();

		return true;
	}
}

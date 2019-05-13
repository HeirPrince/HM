package nasaaty.com.hm.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.franmontiel.fullscreendialog.FullScreenDialogContent;
import com.franmontiel.fullscreendialog.FullScreenDialogController;

import nasaaty.com.hm.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MethodFragment extends Fragment implements FullScreenDialogContent {

	public static final String EXTRA_UID = "EXTRA_UID";
	public static final String EXTRA_PID = "EXTRA_PID";

	CardView call;
	Button hahadev, mydev;
	private FullScreenDialogController controller;


	public MethodFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_method, container, false);
		call = view.findViewById(R.id.call);
		hahadev = view.findViewById(R.id.hhahadev);
		mydev = view.findViewById(R.id.mydv);

		call.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
			}
		});

		hahadev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
			}
		});
		
		
		mydev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
			}
		});

		return view;
	}

	@Override
	public void onDialogCreated(FullScreenDialogController dialogController) {
		this.controller = dialogController;
	}

	@Override
	public boolean onConfirmClick(FullScreenDialogController dialogController) {
		return false;
	}

	@Override
	public boolean onDiscardClick(FullScreenDialogController dialogController) {
		new AlertDialog.Builder(getContext())
				.setTitle(R.string.discard_confirmation_title)
				.setMessage(R.string.discard_confirmation_message)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						controller.discard();
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

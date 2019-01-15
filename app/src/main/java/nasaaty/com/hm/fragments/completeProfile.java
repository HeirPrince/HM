package nasaaty.com.hm.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.franmontiel.fullscreendialog.FullScreenDialogContent;
import com.franmontiel.fullscreendialog.FullScreenDialogController;

import nasaaty.com.hm.R;


public class completeProfile extends Fragment implements FullScreenDialogContent {

	EditText uname, email;
	TextView phone;

	public completeProfile() {
		// Required empty public constructor
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v =  inflater.inflate(R.layout.fragment_complete_profile, container, false);

		uname = v.findViewById(R.id.username);
		email = v.findViewById(R.id.email);
		phone = v.findViewById(R.id.phone);

		updateUI();

		return v;
	}

	private void updateUI() {
		uname.setText(getArguments().getString("uname"));
		email.setText(getArguments().getString("mail"));
		phone.setText(getArguments().getString("phone"));
	}

	@Override
	public void onDialogCreated(FullScreenDialogController dialogController) {
		
	}

	@Override
	public boolean onConfirmClick(FullScreenDialogController dialogController) {
		return false;
	}

	@Override
	public boolean onDiscardClick(FullScreenDialogController dialogController) {
		return false;
	}
}

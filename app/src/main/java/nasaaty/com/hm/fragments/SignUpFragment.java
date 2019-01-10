package nasaaty.com.hm.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.franmontiel.fullscreendialog.FullScreenDialogContent;
import com.franmontiel.fullscreendialog.FullScreenDialogController;

import nasaaty.com.hm.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements FullScreenDialogContent {

	public static final String EXTRA_NAME = "EXTRA_NAME";
	public static final String RESULT_FULL_NAME = "RESULT_FULL_NAME";
	public static final String EXTRA_EMAIL = "EXTRA_EMAIL";
	public static final String RESULT_EMAIL = "RESULT_EMAIL";
	public static final String EXTRA_PASSWORD = "EXTRA_PASSWORD";
	public static final String RESULT_PASSWORD = "RESULT_PASSWORD";
	EditText input_name, input_email, input_password;
	private FullScreenDialogController dialogController;

	public SignUpFragment() {
		// Required empty public constructor

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v =  inflater.inflate(R.layout.fragment_sign_up, container, false);

		input_name = v.findViewById(R.id.user_name);
		input_email = v.findViewById(R.id.input_email);
		input_password = v.findViewById(R.id.input_password);
		
		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		dialogController.setConfirmButtonEnabled(!input_name.getText().toString().isEmpty());
		input_name.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				dialogController.setConfirmButtonEnabled(!s.toString().trim().isEmpty());
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}

	@Override
	public void onDialogCreated(FullScreenDialogController dialogController) {
		this.dialogController = dialogController;
	}

	@Override
	public boolean onConfirmClick(final FullScreenDialogController dialogController) {
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

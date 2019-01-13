package nasaaty.com.hm.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.franmontiel.fullscreendialog.FullScreenDialogContent;
import com.franmontiel.fullscreendialog.FullScreenDialogController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import nasaaty.com.hm.R;
import nasaaty.com.hm.activities.Home;
import nasaaty.com.hm.model.User;
import nasaaty.com.hm.viewmodels.UserVModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements FullScreenDialogContent {

	public static final String EXTRA_UID = "EXTRA_UID";
	public static final String EXTRA_NAME = "EXTRA_NAME";
	public static final String RESULT_FULL_NAME = "RESULT_FULL_NAME";
	public static final String EXTRA_EMAIL = "EXTRA_EMAIL";
	public static final String RESULT_EMAIL = "RESULT_EMAIL";
	public static final String EXTRA_PASSWORD = "EXTRA_PASSWORD";
	public static final String RESULT_PASSWORD = "RESULT_PASSWORD";
	EditText input_name, input_email, input_password;
	CircleImageView image;
	private FullScreenDialogController dialogController;
	FirebaseStorage storage;
	FirebaseAuth firebaseAuth;
	UserVModel vModel;
	private int GALLERY = 1, CAMERA = 2;
	private Uri uri;

	public SignUpFragment() {
		// Required empty public constructor

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v =  inflater.inflate(R.layout.fragment_sign_up, container, false);
		storage = FirebaseStorage.getInstance();
		vModel = ViewModelProviders.of(getActivity()).get(UserVModel.class);
		firebaseAuth = FirebaseAuth.getInstance();

		input_name = v.findViewById(R.id.user_name);
		input_email = v.findViewById(R.id.input_email);
		input_password = v.findViewById(R.id.input_password);
		image = v.findViewById(R.id.user_image);

		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showPictureDialog();
			}
		});
		
		return v;
	}

	private void showPictureDialog() {
		AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
		pictureDialog.setTitle("Select Action");
		String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
		pictureDialog.setItems(pictureDialogItems,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								choosePhotoFromGallery();
								break;
							case 1:
								takePhotoFromCamera();
								break;
						}
					}
				});
		pictureDialog.show();
	}

	private void choosePhotoFromGallery() {
		Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(galleryIntent, GALLERY);
	}

	private void takePhotoFromCamera() {
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, CAMERA);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		dialogController.setConfirmButtonEnabled(!input_name.getText().toString().isEmpty()&& !input_email.getText().toString().isEmpty() && !input_password.getText().toString().isEmpty());
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

		//auth with email
		firebaseAuth.createUserWithEmailAndPassword(input_email.getText().toString(), input_password.getText().toString())
				.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()){
							Toast.makeText(getContext(), "Account created successfully", Toast.LENGTH_SHORT).show();

//							User user = new User();
//							user.setName(input_name.getText().toString());
//							user.setPhotoUrl(uri.toString());
//							user.setEmail(input_email.getText().toString());
//							user.setUid(firebaseAuth.getCurrentUser().getUid());
//							uploadImage(uri);
//
//							vModel.insertUser(user, new UserVModel.onUserSaved() {
//								@Override
//								public void done(Boolean yes) {
//									if (yes) {

										getActivity().finish();
										Intent i = new Intent(getActivity(), Home.class);
										i.putExtra("type", 2);
										i.putExtra("uname", input_name.getText().toString());
										i.putExtra("image", uri.toString());
										startActivity(i);
//									}
//									else
//										Toast.makeText(getContext(), "local account failed", Toast.LENGTH_SHORT).show();
//								}
//							});
						}else {
							Toast.makeText(getContext(), "failed creating account", Toast.LENGTH_SHORT).show();
						}
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});

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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == getActivity().RESULT_CANCELED) {
			return;
		}
		if (requestCode == GALLERY) {
			if (data != null) {
				uri = data.getData();
				try {
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
					image.setImageBitmap(bitmap);

				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
				}
			}

		} else if (requestCode == CAMERA) {
			if (data != null) {
				uri = data.getData();
				try {
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
					image.setImageBitmap(bitmap);
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}

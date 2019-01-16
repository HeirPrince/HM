package nasaaty.com.hm.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.franmontiel.fullscreendialog.FullScreenDialogContent;
import com.franmontiel.fullscreendialog.FullScreenDialogController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import nasaaty.com.hm.R;
import nasaaty.com.hm.viewmodels.UserVModel;


public class completeProfile extends Fragment implements FullScreenDialogContent {

	private int GALLERY = 1, CAMERA = 2;
	EditText uname, email;
	TextView phone;
	CircleImageView imageView;
	private Uri uri;

	FirebaseAuth firebaseAuth;
	UserVModel vModel;

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
		imageView = v.findViewById(R.id.user_image);

		updateUI();

		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		firebaseAuth = FirebaseAuth.getInstance();
		vModel = ViewModelProviders.of(this).get(UserVModel.class);
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				 showPictureDialog();
			}
		});
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

	private void updateUI() {
		uname.setText(getArguments().getString("uname"));
		email.setText(getArguments().getString("email"));
		phone.setText(getArguments().getString("phone"));
	}

	@Override
	public void onDialogCreated(FullScreenDialogController dialogController) {
		
	}

	@Override
	public boolean onConfirmClick(final FullScreenDialogController dialogController) {

		if (uri != null){
			updateUserImage(uri);
		}else {
			updateUserImage(null);
		}

		HashMap<String, String> details = new HashMap<>();

		String uid = getArguments().getString("uid");
		String un = uname.getText().toString();
		String mail = "";
		String p = phone.getText().toString();
		String url = "";
		if (TextUtils.isEmpty(getArguments().getString("url"))){
			url = uri.toString();
			details.put("name", un);

			if (TextUtils.isEmpty(getArguments().getString("mail"))){
				mail = email.getText().toString();
			}else {
				mail = getArguments().getString("mail");
			}

			details.put("email",mail);
			details.put("phoneNum", p);
			details.put("photoUrl", url);
			details.put("uid",uid);
		}else {

			if (TextUtils.isEmpty(getArguments().getString("mail"))){
				mail = email.getText().toString();
			}else {
				mail = getArguments().getString("mail");
			}

			details.put("email",mail);
			details.put("name", un);
			details.put("phoneNum", p);
			details.put("uid",uid);
		}

		vModel.updateUser(details.get("uid"), details, new UserVModel.onUserSaved() {
			@Override
			public void done(Boolean yes) {
				if (yes)
					dialogController.confirm(getArguments());
				else
					Toast.makeText(getActivity(), "sum ting wong", Toast.LENGTH_SHORT).show();
			}
		});

		return false;
	}

	private void updateUserImage(Uri uri) {
		if (uri == null){
			Picasso.get().load(R.drawable.vector_avatar).into(imageView);
		}
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
					imageView.setImageBitmap(bitmap);

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
					imageView.setImageBitmap(bitmap);
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
				}
			}
		}

	}
}

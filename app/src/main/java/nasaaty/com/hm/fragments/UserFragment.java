package nasaaty.com.hm.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.franmontiel.fullscreendialog.FullScreenDialogContent;
import com.franmontiel.fullscreendialog.FullScreenDialogController;
import com.franmontiel.fullscreendialog.FullScreenDialogFragment;
import com.franmontiel.fullscreendialog.FullScreenDialogFragment.OnDiscardListener;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.hdodenhof.circleimageview.CircleImageView;
import nasaaty.com.hm.R;
import nasaaty.com.hm.model.User;
import nasaaty.com.hm.viewmodels.UserVModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment implements FullScreenDialogFragment.OnConfirmListener,
		OnDiscardListener, FullScreenDialogContent {

	TextView name, email, phone;
	CircleImageView user_image;
	UserVModel vModel;
	RelativeLayout toggle_Comp;
	private FullScreenDialogFragment dialogFragment;

	public UserFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v =  inflater.inflate(R.layout.fragment_user, container, false);

		
		name = v.findViewById(R.id.username);
		email = v.findViewById(R.id.email);
		phone = v.findViewById(R.id.phone);
		user_image = v.findViewById(R.id.user_image);
		toggle_Comp = v.findViewById(R.id.act_setup);
		vModel = ViewModelProviders.of(getActivity()).get(UserVModel.class);

		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		toggle_Comp.setVisibility(View.GONE);
		toggle_Comp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				final Bundle bundle = new Bundle();
				bundle.putString("uname", serializedUser().getName());
				bundle.putString("email", serializedUser().getEmail());
				bundle.putString("url", serializedUser().getPhotoUrl());
				bundle.putString("uid", serializedUser().getUid());
				bundle.putString("phone", serializedUser().getPhoneNum());
				bundle.putString("provider", serializedUser().getProviderID());

				dialogFragment = new FullScreenDialogFragment.Builder(getContext())
						.setTitle(R.string.dialog_title_edit)
						.setConfirmButton(R.string.dialog_positive_button_done)
						.setOnConfirmListener(UserFragment.this)
						.setOnDiscardListener(UserFragment.this)
						.setContent(completeProfile.class, bundle)
						.build();
				dialogFragment.show(getActivity().getSupportFragmentManager(), "tg");
			}
		});

		if (serializedUser() != null){

			if (TextUtils.isEmpty(getArguments().getString("url"))){
				Picasso.get().load(R.drawable.vector_avatar).into(user_image);
				toggle_Comp.setVisibility(View.VISIBLE);
				Toast.makeText(getContext(), "no image", Toast.LENGTH_SHORT).show();
			}else {
				toggle_Comp.setVisibility(View.GONE);
				Picasso.get().load(serializedUser().getPhotoUrl()).into(user_image);
			}

			if (TextUtils.isEmpty(serializedUser().getName())){
				toggle_Comp.setVisibility(View.VISIBLE);
				Toast.makeText(getContext(), "no username", Toast.LENGTH_SHORT).show();
			}else {
				toggle_Comp.setVisibility(View.GONE);
				name.setText(serializedUser().getName());
			}


			if (!TextUtils.isEmpty(serializedUser().getPhoneNum())) {
				toggle_Comp.setVisibility(View.GONE);
				phone.setText(getArguments().getString("phone"));
			}else {
				toggle_Comp.setVisibility(View.VISIBLE);
				phone.setText("");

			}

			email.setText(serializedUser().getEmail());

		}
	}

	public User serializedUser(){
		User user = new User();
		user.setName(getArguments().getString("uname"));
		user.setEmail(getArguments().getString("email"));
		user.setPhotoUrl(getArguments().getString("url"));
		user.setUid(getArguments().getString("uid"));
		user.setPhoneNum(getArguments().getString("phone"));
		user.setProviderID(getArguments().getString("provider"));

		return user;
	}

	@Override
	public void onConfirm(@Nullable Bundle result) {

	}

	@Override
	public void onDiscard() {

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

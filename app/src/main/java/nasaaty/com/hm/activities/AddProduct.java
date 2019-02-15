package nasaaty.com.hm.activities;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.yalantis.ucrop.UCrop;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.utils.PermissionUtils;
import nasaaty.com.hm.utils.StorageRepository;
import nasaaty.com.hm.viewmodels.ProductVModel;

public class AddProduct extends AppCompatActivity {

	private static final int GALLERY_REQUEST_CODE = 100;
	EditText label, desc, price;
	ImageView imageView;
	private HahaDB hahaDB;
	private FirebaseAuth auth;
	private ProductVModel vModel;
	private PermissionUtils permissionUtils;
	private Uri image;
	private android.support.v7.widget.Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_product);
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Add Product");
		permissionUtils = new PermissionUtils(this);

		bindViews();
		hahaDB = HahaDB.getInstance(this);
		auth = FirebaseAuth.getInstance();
		vModel = ViewModelProviders.of(this).get(ProductVModel.class);
	}

	private void bindViews() {
		label = findViewById(R.id.label);
		desc = findViewById(R.id.desc);
		price = findViewById(R.id.price);
		imageView = findViewById(R.id.product_image);
	}

	public void addP(View view) {
		String l = label.getText().toString();
		String d = desc.getText().toString();
		int p = Integer.valueOf(price.getText().toString());

		Product product = new Product();
		product.setLabel(l);
		product.setPrice(p);
		product.setDescription(d);
		product.setOwner(auth.getCurrentUser().getUid());

		vModel.insertNew(product, image);
	}

	private void pickFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
				.setType("image/*")
				.addCategory(Intent.CATEGORY_OPENABLE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			String[] mimeTypes = {"image/jpeg", "image/png"};
			intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
		}

		startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), GALLERY_REQUEST_CODE);
	}

	public void pickImage(View view) {
		Permissions.check(this/*context*/, Manifest.permission.READ_EXTERNAL_STORAGE, null, new PermissionHandler() {
			@Override
			public void onGranted() {
				pickFromGallery();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GALLERY_REQUEST_CODE){
			switch (resultCode){
				case RESULT_OK:
					if (data != null) {
						image = data.getData();
						imageView.setImageURI(image);
					}
					else
						Toast.makeText(this, "no image found", Toast.LENGTH_SHORT).show();
					break;
				case RESULT_CANCELED:
					Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();
					break;
				default:
					Toast.makeText(this, "unknown error occured, try again", Toast.LENGTH_SHORT).show();


			}
		}
	}
}

package nasaaty.com.hm.activities;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.adapters.GalleryAdapter;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.utils.PermissionUtils;
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

	private String imageEncoded;
	private List<String> imageEncodedList;
	private List<Uri> uriList;
	private GridView gvGallery;
	private GalleryAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_product);
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Add Product");
		permissionUtils = new PermissionUtils(this);
		imageEncodedList = new ArrayList<>();
		uriList = new ArrayList<>();

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
		gvGallery = findViewById(R.id.gv);
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

		if (uriList != null){
			
			vModel.insertNew(product, uriList);
		}else {
			Toast.makeText(this, "add product images", Toast.LENGTH_SHORT).show();
		}
	}

	private void pickFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
				.setType("image/*")
				.addCategory(Intent.CATEGORY_OPENABLE)
				.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

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
		try {
			// When an Image is picked
			if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK
					&& null != data) {
				// Get the Image from data

				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				if(data.getData()!=null){

					Uri mImageUri=data.getData();
					uriList.add(mImageUri);

					// Get the cursor
					Cursor cursor = getContentResolver().query(mImageUri,
							filePathColumn, null, null, null);
					// Move to first row
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					imageEncoded  = cursor.getString(columnIndex);
					cursor.close();

					ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
					mArrayUri.add(mImageUri);
					adapter = new GalleryAdapter(getApplicationContext(),mArrayUri);
					gvGallery.setAdapter(adapter);
					gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
					ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
							.getLayoutParams();
					mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

				} else {
					if (data.getClipData() != null) {
						ClipData mClipData = data.getClipData();
						for (int i = 0; i < mClipData.getItemCount(); i++) {

							ClipData.Item item = mClipData.getItemAt(i);
							Uri uri = item.getUri();
							uriList.add(uri);
							// Get the cursor
							Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
							// Move to first row
							cursor.moveToFirst();

							int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
							imageEncoded  = cursor.getString(columnIndex);
							imageEncodedList.add(imageEncoded);
							cursor.close();

							adapter = new GalleryAdapter(getApplicationContext(),uriList);
							gvGallery.setAdapter(adapter);
							gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
							ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
									.getLayoutParams();
							mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

						}
						Log.v("LOG_TAG", "Selected Images" + uriList.size());
					}
				}
			} else {
				Toast.makeText(this, "You haven't picked Image",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, "Something went wrong "+e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}

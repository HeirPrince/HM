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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.adapters.GalleryAdapter;
import nasaaty.com.hm.model.ImageFile;
import nasaaty.com.hm.model.Product;
import nasaaty.com.hm.room.HahaDB;
import nasaaty.com.hm.utils.PermissionUtils;
import nasaaty.com.hm.viewmodels.ProductVModel;

public class AddProduct extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

	private static final int GALLERY_REQUEST_CODE = 100;
	EditText label, desc, price;
	ImageView imageView, defIMage;
	Spinner cats;
	private HahaDB hahaDB;
	private FirebaseAuth auth;
	private ProductVModel vModel;
	private PermissionUtils permissionUtils;
	private Uri def_image;
	private android.support.v7.widget.Toolbar toolbar;

	private String imageEncoded;
	private List<String> imageEncodedList;
	private List<Uri> uriList;
	private List<ImageFile> fileList;
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
		fileList = new ArrayList<>();

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
		defIMage = findViewById(R.id.def_image);
		gvGallery = findViewById(R.id.gv);
		cats = findViewById(R.id.cat_spinner);

		//setup category spinner
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.testItems, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cats.setAdapter(adapter);
		cats.setOnItemSelectedListener(this);
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
		product.setCategory(cats.getSelectedItem().toString());
		product.setNumRatings(0);
		product.setAvgRatings(0.0);
		product.setTimeStamp(getTimeStamp());

		vModel.insertNew(product, uriList, def_image, new ProductVModel.onUploadDone() {
			@Override
			public void done(Boolean ok) {
				if (ok) {
					finish();
					startActivity(new Intent(AddProduct.this, Home.class));
				}else {
					Toast.makeText(AddProduct.this, "upload failed", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private String getTimeStamp() {
		return String.valueOf(System.currentTimeMillis() / 1000);
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

	private void pickDefFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
				.setType("image/*")
				.addCategory(Intent.CATEGORY_OPENABLE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			String[] mimeTypes = {"image/jpeg", "image/png"};
			intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
		}

		startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), 001);
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

					Uri mImageUri = data.getData();
					uriList.add(mImageUri);

					// Get the cursor
					Cursor cursor = getContentResolver().query(mImageUri,
							filePathColumn, null, null, null);
					// Move to first row
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					imageEncoded  = cursor.getString(columnIndex);
					cursor.close();

					ArrayList<Uri> mArrayUri = new ArrayList<>();
					mArrayUri.add(mImageUri);
					ImageFile file = new ImageFile();
					file.setDefault(false);
					file.setFile(mImageUri);
					fileList.add(file);

					adapter = new GalleryAdapter(getApplicationContext(),fileList);
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
							ImageFile imageFile = new ImageFile();
							imageFile.setFile(uri);
							imageFile.setDefault(false);
							fileList.add(imageFile);
							// Get the cursor
							Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
							// Move to first row
							cursor.moveToFirst();

							int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
							imageEncoded  = cursor.getString(columnIndex);
							imageEncodedList.add(imageEncoded);
							cursor.close();

							adapter = new GalleryAdapter(getApplicationContext(),fileList);
							gvGallery.setAdapter(adapter);
							gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
							ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
									.getLayoutParams();
							mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

						}
						Log.v("LOG_TAG", "Selected Images" + uriList.size());
					}
				}
			}else if (requestCode == 001 && resultCode == RESULT_OK & null != data){
				def_image = data.getData();
				Picasso.get().load(def_image).into(defIMage);
			}
			else {
				Toast.makeText(this, "You haven't picked Image",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, "Something went wrong "+e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
		Toast.makeText(this, adapterView.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {

	}

	public void pickDefImage(View view) {
		Permissions.check(this/*context*/, Manifest.permission.READ_EXTERNAL_STORAGE, null, new PermissionHandler() {
			@Override
			public void onGranted() {
				pickDefFromGallery();
			}
		});
	}
}

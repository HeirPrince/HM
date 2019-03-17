package nasaaty.com.hm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import nasaaty.com.hm.R;
import nasaaty.com.hm.model.ImageFile;

public class GalleryAdapter extends BaseAdapter {

	private Context context;
	private int pos;
	private LayoutInflater inflater;
	private ImageView ivGallery;
	List<ImageFile> mUris;
	private View del;
	private TextView isDef;
	
	public GalleryAdapter(Context applicationContext, List<ImageFile> mArrayUri) {
		this.context = applicationContext;
		this.mUris = mArrayUri;

	}

	@Override
	public int getCount() {
		return mUris.size();
	}

	@Override
	public Object getItem(int i) {
		return mUris.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(final int i, View view, ViewGroup viewGroup) {
		pos = i;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.layout_gallery_item, viewGroup, false);

		ivGallery = itemView.findViewById(R.id.ivGallery);
		del = itemView.findViewById(R.id.delete);
		isDef = itemView.findViewById(R.id.isdef);
		isDef.setVisibility(View.GONE);

		final int current = i;
		final ImageFile file = mUris.get(current);
		file.setDefault(false);

//		showDefault(current, mUris);

		ivGallery.setImageURI(file.getFile());

		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				file.setDefault(true);
				if (file.getDefault())
					isDef.setVisibility(View.VISIBLE);
				else
					isDef.setVisibility(View.GONE);
			}
		});

		// TODO: 2/19/2019 find a way of implementing delete properly.
		del.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mUris.remove(pos);
				notifyDataSetChanged();
			}
		});

		return itemView;
	}

	private void showDefault(int current, List<ImageFile> imageFiles) {

		for (int i=0; i<= imageFiles.size(); i++){
			if (i == current){
				ImageFile default_file = imageFiles.get(current);
				default_file.setDefault(true);
				isDef.setVisibility(View.VISIBLE);
			}else {
				ImageFile default_file = imageFiles.get(current);
				default_file.setDefault(false);
				isDef.setVisibility(View.GONE);
			}

		}
	}


}

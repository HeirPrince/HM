package nasaaty.com.hm.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import nasaaty.com.hm.R;

public class GalleryAdapter extends BaseAdapter {

	private Context context;
	private int pos;
	private LayoutInflater inflater;
	private ImageView ivGallery;
	private View del;
	List<Uri> mUris;
	
	public GalleryAdapter(Context applicationContext, List<Uri> mArrayUri) {
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
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		pos = i;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.layout_gallery_item, viewGroup, false);

		ivGallery = itemView.findViewById(R.id.ivGallery);
		del = itemView.findViewById(R.id.delete);

		ivGallery.setImageURI(mUris.get(pos));

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


}

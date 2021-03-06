package nasaaty.com.hm.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ListSpacingDecoration extends RecyclerView.ItemDecoration {

	private int mBottomOffset;

	public ListSpacingDecoration(int bottomOffset) {
		mBottomOffset = bottomOffset;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		int dataSize = state.getItemCount();
		int position = parent.getChildAdapterPosition(view);
		if (dataSize > 0 && position == dataSize - 1) {
			outRect.set(0, 0, 0, mBottomOffset);
		} else {
			outRect.set(0, 0, 0, 0);
		}

	}

}

package nasaaty.com.hm.CallBacks;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import nasaaty.com.hm.adapters.OrderListAdapter;

public class SwipeControl extends ItemTouchHelper.SimpleCallback {

	private SwipeListener listener;

	public SwipeControl(int dragDirs, int swipeDirs, SwipeListener listener) {
		super(dragDirs, swipeDirs);
		this.listener = listener;
	}

	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		return true;
	}

	@Override
	public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
		if (viewHolder != null){
			final View foregroundView = ((OrderListAdapter.OrderVHolder)viewHolder).foregroundView;

			getDefaultUIUtil().onSelected(foregroundView);
		}
	}

	@Override
	public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

		final View foregroundView = ((OrderListAdapter.OrderVHolder)viewHolder).foregroundView;

		getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);

	}

	@Override
	public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		super.clearView(recyclerView, viewHolder);
		final View foregroundView = ((OrderListAdapter.OrderVHolder)viewHolder).foregroundView;
		getDefaultUIUtil().clearView(foregroundView);
	}

	@Override
	public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
		final View foregroundView = ((OrderListAdapter.OrderVHolder)viewHolder).foregroundView;

		getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
	}

	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
		listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
	}

	@Override
	public int convertToAbsoluteDirection(int flags, int layoutDirection) {
		return super.convertToAbsoluteDirection(flags, layoutDirection);
	}

	public interface SwipeListener{
		void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
	}
}

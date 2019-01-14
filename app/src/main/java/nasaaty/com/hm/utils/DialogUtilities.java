package nasaaty.com.hm.utils;

import android.content.Context;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeDialogBuilder;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeNoticeDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import nasaaty.com.hm.R;

public class DialogUtilities {
	private static Context ctx;

	AwesomeProgressDialog progressDialog;
	static AwesomeErrorDialog errorDialog;
	AwesomeInfoDialog infoDialog;
	AwesomeNoticeDialog noticeDialog;
	AwesomeSuccessDialog successDialog;
	AwesomeWarningDialog warningDialog;

	public DialogUtilities(Context ctx) {
		this.ctx = ctx;
	}

	public void hide(){

	}

	public void showErrorDialog(String title, String message){
		new AwesomeErrorDialog(ctx).setTitle(title)
				.setMessage(message)
				.setColoredCircle(R.color.dialogNoticeBackgroundColor)
				.setDialogIconAndColor(R.drawable.ic_notice, R.color.white)
				.setCancelable(true)
				.setButtonBackgroundColor(R.color.dialogErrorBackgroundColor)
				.setButtonText(ctx.getResources().getString(R.string.dialog_ok_button))
		.setErrorButtonClick(new Closure() {
			@Override
			public void exec() {
				//click
			}
		})
		.show();
	}

	public void showProgressDialog(String title, String message){
		new AwesomeInfoDialog(ctx)
				.setTitle(title)
				.setMessage(message)
				.setColoredCircle(R.color.dialogInfoBackgroundColor)
				.setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
				.setCancelable(true)
				.show();
	}

	public void showWarningDialog(String title, String message){
		new AwesomeWarningDialog(ctx)
				.setTitle(title)
				.setMessage(message)
				.setColoredCircle(R.color.dialogNoticeBackgroundColor)
				.setDialogIconAndColor(R.drawable.ic_notice, R.color.white)
				.setCancelable(true)
				.setButtonText(ctx.getResources().getString(R.string.dialog_ok_button))
				.setButtonBackgroundColor(R.color.dialogNoticeBackgroundColor)
				.setButtonText(ctx.getResources().getString(R.string.dialog_ok_button))
				.setWarningButtonClick(new Closure() {
					@Override
					public void exec() {
						// click
					}
				})
				.show();
	}

	public void showNoticeDialog(String title, String message){
		new AwesomeNoticeDialog(ctx)
				.setTitle(title)
				.setMessage(message)
				.setColoredCircle(R.color.dialogNoticeBackgroundColor)
				.setDialogIconAndColor(R.drawable.ic_notice, R.color.white)
				.setCancelable(true)
				.setButtonText(ctx.getResources().getString(R.string.dialog_ok_button))
				.setButtonBackgroundColor(R.color.dialogNoticeBackgroundColor)
				.setButtonText(ctx.getResources().getString(R.string.dialog_ok_button))
				.setNoticeButtonClick(new Closure() {
					@Override
					public void exec() {
						// click
					}
				})
				.show();
	}

	public void showSuccessDialog(String title, String message){
		new AwesomeSuccessDialog(ctx)
				.setTitle(title)
				.setMessage(message)
				.setColoredCircle(R.color.dialogSuccessBackgroundColor)
				.setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
				.setCancelable(true)
				.setPositiveButtonText(ctx.getResources().getString(R.string.dialog_yes_button))
				.setPositiveButtonbackgroundColor(R.color.dialogSuccessBackgroundColor)
				.setPositiveButtonTextColor(R.color.white)
				.setNegativeButtonText(ctx.getResources().getString(R.string.dialog_no_button))
				.setNegativeButtonbackgroundColor(R.color.dialogSuccessBackgroundColor)
				.setNegativeButtonTextColor(R.color.white)
				.setPositiveButtonClick(new Closure() {
					@Override
					public void exec() {
						//click
					}
				})
				.setNegativeButtonClick(new Closure() {
					@Override
					public void exec() {
						//click
					}
				})
				.show();
	}

	public void showInfoDialog(String title, String message) {
		new AwesomeInfoDialog(ctx)
				.setTitle(title)
				.setMessage(message)
				.setColoredCircle(R.color.dialogInfoBackgroundColor)
				.setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
				.setCancelable(true)
				.setPositiveButtonText(ctx.getResources().getString(R.string.dialog_yes_button))
				.setPositiveButtonbackgroundColor(R.color.dialogInfoBackgroundColor)
				.setPositiveButtonTextColor(R.color.white)
				.setNeutralButtonText(ctx.getResources().getString(R.string.dialog_neutral_button))
				.setNeutralButtonbackgroundColor(R.color.dialogInfoBackgroundColor)
				.setNeutralButtonTextColor(R.color.white)
				.setNegativeButtonText(ctx.getResources().getString(R.string.dialog_no_button))
				.setNegativeButtonbackgroundColor(R.color.dialogInfoBackgroundColor)
				.setNegativeButtonTextColor(R.color.white)
				.setPositiveButtonClick(new Closure() {
					@Override
					public void exec() {
						//click
					}
				})
				.setNeutralButtonClick(new Closure() {
					@Override
					public void exec() {
						//click
					}
				})
				.setNegativeButtonClick(new Closure() {
					@Override
					public void exec() {
						//click
					}
				})
				.show();
	}
}

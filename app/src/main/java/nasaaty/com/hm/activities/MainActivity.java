package nasaaty.com.hm.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import nasaaty.com.hm.R;

public class MainActivity extends AppIntro {

	private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;

	//TODO finish intro activity(missing: images, colors, transformers)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SliderPage sliderPage = new SliderPage();
		sliderPage.setTitle("title");
		sliderPage.setDescription("description");
		sliderPage.setImageDrawable(R.drawable.men_shoe1);
		sliderPage.setBgColor(getResources().getColor(R.color.white));
		addSlide(AppIntroFragment.newInstance(sliderPage));

		SliderPage sliderPage2 = new SliderPage();
		sliderPage.setTitle("title");
		sliderPage.setDescription("description");
		sliderPage.setImageDrawable(R.drawable.men_shoe1);
		sliderPage.setBgColor(getResources().getColor(R.color.white));
		addSlide(AppIntroFragment.newInstance(sliderPage2));

		SliderPage sliderPage3 = new SliderPage();
		sliderPage.setTitle("title");
		sliderPage.setDescription("description");
		sliderPage.setImageDrawable(R.drawable.men_shoe1);
		sliderPage.setBgColor(getResources().getColor(R.color.white));
		addSlide(AppIntroFragment.newInstance(sliderPage3));

		SliderPage sliderPage4 = new SliderPage();
		sliderPage.setTitle("title");
		sliderPage.setDescription("description");
		sliderPage.setImageDrawable(R.drawable.men_shoe1);
		sliderPage.setBgColor(getResources().getColor(R.color.white));
		addSlide(AppIntroFragment.newInstance(sliderPage4));

//		setVibrate(true);
//		setVibrateIntensity(60);


	}

	public void signIn(View view) {

	}

	@Override
	public void onSkipPressed(Fragment currentFragment) {
		super.onSkipPressed(currentFragment);
		// Do something when users tap on Skip button.
	}

	@Override
	public void onDonePressed(Fragment currentFragment) {
		super.onDonePressed(currentFragment);

//		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//			// Camera permission has not been granted.
//
//			requestCameraPermission();
//		}else {
//
//		}

		finish();
		startActivity(new Intent(MainActivity.this, SignIn.class));

	}

	private void requestCameraPermission() {
		// BEGIN_INCLUDE(camera_permission_request)
		if (ActivityCompat.shouldShowRequestPermissionRationale(this,
				Manifest.permission.CAMERA)) {
			// Provide an additional rationale to the user if the permission was not granted
			// and the user would benefit from additional context for the use of the permission.
			// For example if the user has previously denied the permission.
			Log.i("tag",
					"Displaying camera permission rationale to provide additional context.");

			ActivityCompat.requestPermissions(MainActivity.this,
					new String[]{Manifest.permission.CAMERA},
					MY_PERMISSIONS_REQUEST_CAMERA);

		} else {

			// Camera permission has not been granted yet. Request it directly.
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
					MY_PERMISSIONS_REQUEST_CAMERA);
		}
		// END_INCLUDE(camera_permission_request)
	}

	@Override
	public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
		super.onSlideChanged(oldFragment, newFragment);
		// Do something when the slide changes.
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA){
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
				Toast.makeText(this, "yeeeeeeeees", Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(this, "noooooooooo", Toast.LENGTH_SHORT).show();
			}
		}
	}
}

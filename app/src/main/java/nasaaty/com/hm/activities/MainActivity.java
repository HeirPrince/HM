package nasaaty.com.hm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import nasaaty.com.hm.R;

public class MainActivity extends AppIntro {

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
		finish();
		startActivity(new Intent(MainActivity.this, SignIn.class));
	}

	@Override
	public void onSkipPressed(Fragment currentFragment) {
		super.onSkipPressed(currentFragment);
		// Do something when users tap on Skip button.
	}

	@Override
	public void onDonePressed(Fragment currentFragment) {
		super.onDonePressed(currentFragment);
		// Do something when users tap on Done button.
		finish();
		startActivity(new Intent(MainActivity.this, SignIn.class));
	}

	@Override
	public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
		super.onSlideChanged(oldFragment, newFragment);
		// Do something when the slide changes.
	}
}

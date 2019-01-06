package nasaaty.com.hm.utils;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.ParallaxPage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

import nasaaty.com.hm.R;

public class WelcomeScreen extends WelcomeActivity {

	@Override
	protected WelcomeConfiguration configuration() {
		return new WelcomeConfiguration.Builder(this)
				.defaultBackgroundColor(R.color.screen_1)
				.bottomLayout(WelcomeConfiguration.BottomLayout.STANDARD)
				.page(new TitlePage(R.drawable.shoe,
						"Keci")
				)
				.page(new BasicPage(R.drawable.men_shoe1,
						"Header",
						"More text.")
						.background(R.color.screen_2)
				)
				.page(new BasicPage(R.drawable.shoe,
						"Header",
						"More text.")
						.background(R.color.screen_3)
				)
				.page(new BasicPage(R.drawable.briefcase,
						"Header",
						"More text.")
						.background(R.color.screen_4)
				)
				.swipeToDismiss(true)
				.build();
	}
}

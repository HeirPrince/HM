package nasaaty.com.hm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.stephentuso.welcome.WelcomeHelper;

import nasaaty.com.hm.activities.SignIn;
import nasaaty.com.hm.utils.WelcomeScreen;

public class MainActivity extends AppCompatActivity {

	WelcomeHelper welcomeScreen;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		welcomeScreen = new WelcomeHelper(this, WelcomeScreen.class);
		welcomeScreen.show(savedInstanceState);
		welcomeScreen.forceShow();
	}

	public void singnIn(View view) {
		finish();
		startActivity(new Intent(MainActivity.this, SignIn.class));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		welcomeScreen.onSaveInstanceState(outState);
	}
}

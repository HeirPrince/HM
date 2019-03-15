package nasaaty.com.hm.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import nasaaty.com.hm.R;

public class ReviewP extends AppCompatActivity {

	Toolbar toolbar;
	TextView info;
	AppCompatRatingBar ratingBar;
	EditText rev;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review_p);
		bindViews();
		setSupportActionBar(toolbar);
	}

	private void bindViews() {
		info = findViewById(R.id.info);
		ratingBar = findViewById(R.id.stars);
		rev = findViewById(R.id.rev);
		toolbar = findViewById(R.id.toolbar);
	}
}

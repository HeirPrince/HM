package nasaaty.com.hm.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.transferwise.sequencelayout.SequenceLayout;
import com.transferwise.sequencelayout.SequenceStep;

import nasaaty.com.hm.R;
import nasaaty.com.hm.fragments.onDelivery;

public class Track_Order extends AppCompatActivity implements View.OnClickListener {

	android.support.v7.widget.Toolbar toolbar;
	SequenceLayout sequenceLayout;
	SequenceStep first, second, third, fourth, fifth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_order);

		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Track Order");

		sequenceLayout = findViewById(R.id.seq_layout);
		first = findViewById(R.id.first);
		second = findViewById(R.id.second);
		third = findViewById(R.id.third);
		fourth = findViewById(R.id.fourth);
		fifth = findViewById(R.id.fifth);

		third.setActive(true);

		first.setOnClickListener(this);
		second.setOnClickListener(this);
		third.setOnClickListener(this);
		fourth.setOnClickListener(this);
		fifth.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){

			case R.id.first:
				Toast.makeText(this, "first clicked", Toast.LENGTH_SHORT).show();
				break;

			case R.id.second:
				Toast.makeText(this, "second clicked", Toast.LENGTH_SHORT).show();
				break;

			case R.id.third:
				Toast.makeText(this, "third clicked", Toast.LENGTH_SHORT).show();
				break;

			case R.id.fourth:
				Toast.makeText(this, "fourth clicked", Toast.LENGTH_SHORT).show();
				break;

			case R.id.fifth:
				Toast.makeText(this, "fifth clicked", Toast.LENGTH_SHORT).show();
				break;

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.track_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.pdeliver:
				onDelivery on = new onDelivery();
				android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

				on.show(ft, onDelivery.TAG);
				break;
		}
		return true;
	}
}

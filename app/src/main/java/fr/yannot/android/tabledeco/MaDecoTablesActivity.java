package fr.yannot.android.tabledeco;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.yannot.fr.tabledeco.R;

public class MaDecoTablesActivity extends Activity {

	Button buttonVoirTableMN90;
	Button buttonPlongeeSimple;
	Button buttonPlongeeSuc;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		buttonVoirTableMN90 = (Button) findViewById(R.id.buttonVoirTableMN90);
		buttonPlongeeSimple = (Button) findViewById(R.id.buttonPlongeeSimple);
		buttonPlongeeSuc = (Button) findViewById(R.id.buttonPlongeeSuc);

		buttonVoirTableMN90.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(), ViewTableActivity.class);
				startActivityForResult(myIntent, 0);
			}
		});

		buttonPlongeeSimple.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(), PlongeeSimpleActivity.class);
				startActivityForResult(myIntent, 0);
			}
		});

		buttonPlongeeSuc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(), PlongeeSuccessiveActivity.class);
				startActivityForResult(myIntent, 0);
			}
		});

	}
}
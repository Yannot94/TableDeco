package fr.yannot.android.tabledeco;

import fr.yannot.android.tabledeco.dao.LigneMN90;
import fr.yannot.android.tabledeco.dao.MN90DataAccess;
import fr.yannot.android.tabledeco.dao.PaliersPlongeeSuc;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.yannot.fr.tabledeco.R;

public class PlongeeSuccessiveActivity extends Activity {

	EditText editProf1;
	EditText editTemps1;
	EditText editProf2;
	EditText editTemps2;
	EditText editInterval;
	Button buttonCalculer;
	TextView textResult;

	MN90DataAccess mn90DataAccess;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plongeesuccessive);

		mn90DataAccess = new MN90DataAccess(getApplicationContext());

		editProf1 = (EditText) findViewById(R.id.psuc_prof1);
		editTemps1 = (EditText) findViewById(R.id.psuc_temps1);
		editProf2 = (EditText) findViewById(R.id.psuc_prof2);
		editTemps2 = (EditText) findViewById(R.id.psuc_temps2);
		editInterval = (EditText) findViewById(R.id.psuc_interval);
		textResult = (TextView)  findViewById(R.id.psuc_result);

		buttonCalculer = (Button) findViewById(R.id.psuc_calculer);

		buttonCalculer.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				calculer();
			}
		});

	}

	private void calculer() {

		int prof1 = Integer.parseInt(editProf1.getText().toString());
		int temps1 = Integer.parseInt(editTemps1.getText().toString());
		int prof2 = Integer.parseInt(editProf2.getText().toString());
		int temps2 = Integer.parseInt(editTemps2.getText().toString());
		int interval = Integer.parseInt(editInterval.getText().toString());

		PaliersPlongeeSuc paliersPlongeeSuc = mn90DataAccess.getPaliersPlongeeSuc(prof1, temps1, interval, prof2,
				temps2);

		LigneMN90 p1 = paliersPlongeeSuc.getPlongee1();
		LigneMN90 p2 = paliersPlongeeSuc.getPlongee2();
		
		String result = "Plong�e 1 : " + p1.getPaliers() + ", GPS : " + p1.getGps() +"\n";
		result += "Azote r�siduel (" + paliersPlongeeSuc.getAzote() + "), Majoration ("
				+ paliersPlongeeSuc.getMajoration() + ")\n";
		result += "Plong�e 2 : " + p2.getPaliers() + ", GPS : " + p2.getGps();
		
		textResult.setText(result);
			
		System.out.println("Plong�e 1 : " + p1.getPaliers() + ", GPS : " + p1.getGps());
		System.out.println("AZote r�siduel (" + paliersPlongeeSuc.getAzote() + "), Majoration ("
				+ paliersPlongeeSuc.getMajoration() + ")");
		System.out.println("Plong�e 2 : " + p2.getPaliers() + ", GPS : " + p2.getGps());

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editProf1.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(editTemps1.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(editProf2.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(editTemps2.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(editInterval.getWindowToken(), 0);
	}

}

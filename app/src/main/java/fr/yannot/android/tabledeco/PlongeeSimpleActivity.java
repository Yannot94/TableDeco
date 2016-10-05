package fr.yannot.android.tabledeco;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.yannot.fr.tabledeco.R;

import fr.yannot.android.tabledeco.dao.LigneMN90;
import fr.yannot.android.tabledeco.dao.MN90DataAccess;

public class PlongeeSimpleActivity extends Activity {

	Button buttonCalculer;
	EditText editProfondeur;
	EditText editTemps;
	TextView textViewDTR;
	TextView textViewGPS;
	TextView textViewListPaliers;

	MN90DataAccess mn90DataAccess;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plongeesimple);

		mn90DataAccess = new MN90DataAccess(getApplicationContext());

		buttonCalculer = (Button) findViewById(R.id.calculer);
		editProfondeur = (EditText) findViewById(R.id.editTextProfondeur);
		editTemps = (EditText) findViewById(R.id.editTextTemps);
		textViewDTR = (TextView) findViewById(R.id.textViewDTRResult);
		textViewGPS = (TextView) findViewById(R.id.textViewGPSResult);
		textViewListPaliers = (TextView) findViewById(R.id.plongeeSimplelistPaliers);

		// On attribue un écouteur d'événement à tous les boutons
		buttonCalculer.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				calculer();
			}
		});
	}

	private void calculer() {

		int profondeur = Integer.parseInt(editProfondeur.getText().toString());
		int temps = Integer.parseInt(editTemps.getText().toString());

		System.out.println(editProfondeur.getText().toString() + " || " + editTemps.getText().toString());

		LigneMN90 ligneMN90 = mn90DataAccess.getLigneMN90(profondeur, temps);

		if (ligneMN90 != null) {
			textViewDTR.setText(String.valueOf(ligneMN90.getDtr()) + " "
					+ getResources().getString(R.string.TimeUnitName));
			textViewGPS.setText(String.valueOf(ligneMN90.getGps()));

			String palier;
			if (ligneMN90.getPaliers().isEmpty()) {
				palier = "Pas de palier\n";
			} else {
				palier = "Paliers : \n";
				Set<Integer> paliers = ligneMN90.getPaliers().keySet();
				List<Integer> paliersList = new ArrayList<Integer>();

				for (Integer prof : paliers) {
					paliersList.add(prof);
				}
				java.util.Collections.sort(paliersList);

				for (Integer prof : paliersList) {
					palier += "\t" + ligneMN90.getPaliers().get(prof) + " minutes à " + prof + " métres\n";
				}
			}

			textViewListPaliers.setText(palier);

			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editTemps.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(editProfondeur.getWindowToken(), 0);
		} else {
			textViewDTR.setText("");
			textViewGPS.setText("");
			textViewListPaliers.setText("");
			Toast.makeText(this, "Pas d'information !", Toast.LENGTH_LONG).show();
		}
	}
}

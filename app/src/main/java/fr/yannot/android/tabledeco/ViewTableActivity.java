package fr.yannot.android.tabledeco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.yannot.fr.tabledeco.R;

import fr.yannot.android.tabledeco.dao.LigneMN90;
import fr.yannot.android.tabledeco.dao.MN90DataAccess;

public class ViewTableActivity extends Activity {

	MN90DataAccess mn90DataAccess;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.viewtable);

		mn90DataAccess = new MN90DataAccess(getApplicationContext());
		List<Map<String, Integer>> listItem = new ArrayList<Map<String, Integer>>();

		for (LigneMN90 ligne : mn90DataAccess.getTableMN90()) {

			Map<String, Integer> item = new HashMap<String, Integer>();
			item.put("profondeur", ligne.getProfondeur());
			item.put("temps", ligne.getTemps());

			listItem.add(item);

		}

		SimpleAdapter adapter = new SimpleAdapter(this.getBaseContext(), listItem, R.layout.itemlist, new String[] {
				"profondeur", "temps" }, new int[] { R.id.textViewProfondeur, R.id.textViewTemps });

		final ListView listView = (ListView) findViewById(R.id.listview);

		listView.setAdapter(adapter);

		// Enfin on met un �couteur d'�v�nement sur notre listView
		listView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				// on r�cup�re la HashMap contenant les infos de notre item
				// (titre, description, img)
				HashMap<String, Integer> map = (HashMap<String, Integer>) listView.getItemAtPosition(position);

				// on recupere les info de la table
				LigneMN90 ligneMN90 = mn90DataAccess.getLigneMN90(map.get("profondeur"), map.get("temps"));

				// on cr�er une boite de dialogue
				AlertDialog.Builder adb = new AlertDialog.Builder(ViewTableActivity.this);
				// on attribut un titre � notre boite de dialogue
				adb.setTitle(map.get("temps") + " minutes � " + map.get("profondeur") + " métres");

				String message = "";

				if (ligneMN90.getPaliers().isEmpty()) {
					message = "Pas de palier\n";
				} else {
					Set<Integer> paliers = ligneMN90.getPaliers().keySet();
					List<Integer> paliersList = new ArrayList<Integer>();

					for (Integer prof : paliers) {
						paliersList.add(prof);
					}
					java.util.Collections.sort(paliersList);

					for (Integer prof : paliersList) {
						message += ligneMN90.getPaliers().get(prof) + " minutes à " + prof + " métres\n";
					}
				}

				message += "\nDTR : " + ligneMN90.getDtr() + " minutes, GPS : " + ligneMN90.getGps();

				adb.setMessage(message);
				// on indique que l'on veut le bouton ok � notre boite de
				// dialogue
				adb.setPositiveButton("Ok", null);
				// on affiche la boite de dialogue
				adb.show();
			}
		});
	}

}

package fr.yannot.android.tabledeco.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.yannot.fr.tabledeco.R;
//import fr.yannot.android.tabledeco.R;

public class MN90DataAccess {

	private static int PROFONDEUR = 0;
	private static int TEMPS = 1;
	private static int DTR = 2;
	private static int GPS = 3;
	private static int paliers = 4;

	private List<LigneMN90> tableMN90 = new ArrayList<LigneMN90>();
	private Map<String, Map<Integer, Float>> mn90Azote = new HashMap<String, Map<Integer, Float>>();
	private Map<Float, Map<Integer, Integer>> mn90Majoration = new HashMap<Float, Map<Integer, Integer>>();

	public MN90DataAccess(Context context) {

		loadTableMN90(context);

		loadAzoteMN90(context);

		loadMajorationMN90(context);

	}

	public LigneMN90 getLigneMN90(int profondeur, int temps) {

		for (LigneMN90 ligneMN90 : tableMN90) {
			if (profondeur <= ligneMN90.getProfondeur()) {

				if (temps <= ligneMN90.getTemps()) {
					return ligneMN90;
				}
			}

		}
		return null;
	}

	public PaliersPlongeeSuc getPaliersPlongeeSuc(int prof1, int temps1, int interval, int prof2, int temps2) {

		PaliersPlongeeSuc paliersPlongeeSuc = new PaliersPlongeeSuc();

		paliersPlongeeSuc.setPlongee1(getLigneMN90(prof1, temps1));

		paliersPlongeeSuc.setInterval(interval);

		if (interval < 15) {
			// plongee consecutive

			int profondeur = (prof1 > prof2) ? prof1 : prof2;
			int temps = temps1 + temps2;

			paliersPlongeeSuc.setPlongee2(getLigneMN90(profondeur, temps));
		} else {
			// plong�e successive

			// calcul de l'azote r�siduel
			String gps = paliersPlongeeSuc.getPlongee1().getGps();
			float azote = getAzoteResiduel(gps, interval);
			System.out.println("Azote : " + azote);

			if (azote == -1) {
				// on repasse sur un plong�e simple
				paliersPlongeeSuc.setPlongee2(getLigneMN90(prof2, temps2));
				paliersPlongeeSuc.setAzote(0);
				paliersPlongeeSuc.setMajoration(0);
			} else {
				// calcule de la majoration

				int majoration = getMajoration(azote, prof2);
				System.out.println("Majoration : " + majoration);

				paliersPlongeeSuc.setPlongee2(getLigneMN90(prof2, temps2 + majoration));
				paliersPlongeeSuc.setAzote(azote);
				paliersPlongeeSuc.setMajoration(majoration);

			}

		}

		return paliersPlongeeSuc;

	}

	private float getAzoteResiduel(String gps, int interval) {
		
		Map<Integer, Float> ligneGPS = mn90Azote.get(gps);
		
		if(ligneGPS == null) {
			System.out.println("GPS (" + gps + ") n'a retourn� aucune ligne");
		} else {
			System.out.println("Ligne GPS : " + ligneGPS);
		}
		
		
		float azote = -1;

		Set<Integer> intervals = ligneGPS.keySet();
		List<Integer> intervalsList = new ArrayList<Integer>();

		for (Integer inter : intervals) {
			intervalsList.add(inter);
		}
		java.util.Collections.sort(intervalsList);
		java.util.Collections.reverse(intervalsList);
		System.out.println("Liste interval : " + intervalsList);
		for (Integer inter : intervalsList) {
			if (inter <= interval) {
				System.out.println("Interval (" + interval + "), choisie (" + inter + ")");
				azote = ligneGPS.get(inter);
				break;
			}
		}

		return azote;
	}

	private int getMajoration(float azote, int profondeur) {
		int majoration = -1;
		Map<Integer, Integer> ligneAzote = null;

		Set<Float> azotesTables = mn90Majoration.keySet();
		List<Float> azotesList = new ArrayList<Float>();

		for (Float a : azotesTables) {
			azotesList.add(a);
		}
		java.util.Collections.sort(azotesList);

		for (Float a : azotesList) {
			if (a >= azote) {
				ligneAzote = mn90Majoration.get(a);
				break;
			}
		}

		Set<Integer> profondeurs = ligneAzote.keySet();
		List<Integer> profondeursList = new ArrayList<Integer>();

		for (Integer prof : profondeurs) {
			profondeursList.add(prof);
		}
		java.util.Collections.sort(profondeursList);

		for (Integer prof : profondeursList) {
			if (prof >= profondeur) {
				majoration = ligneAzote.get(prof);
				break;
			}
		}

		return majoration;
	}

	public List<LigneMN90> getTableMN90() {
		return tableMN90;
	}

	private void loadTableMN90(Context context) {
		// chargement table mn90 simple
		try {
			InputStream inputStream = context.getResources().openRawResource(R.raw.mn90);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			String chaine;

			while ((chaine = bufferedReader.readLine()) != null) {
				String[] ligne = chaine.split(";");

				LigneMN90 ligneMN90 = new LigneMN90();
				ligneMN90.setProfondeur(Integer.parseInt(ligne[PROFONDEUR]));
				ligneMN90.setTemps(Integer.parseInt(ligne[TEMPS]));
				ligneMN90.setDtr(Integer.parseInt(ligne[DTR]));
				ligneMN90.setGps(ligne[GPS]);

				int i = paliers;
				int p = 0;
				while (i < ligne.length && ligne[i] != "") {
					p = p + 3;
					ligneMN90.getPaliers().put(p, Integer.parseInt(ligne[i]));
					i++;
				}

				tableMN90.add(ligneMN90);
			}
			bufferedReader.close();
		} catch (Exception e) {
			System.out.println("Probleme lors du chargement de la table MN90 ! " + e);
		}
	}

	private void loadAzoteMN90(Context context) {
		// chargement azote r�siduel
		try {
			InputStream inputStream = context.getResources().openRawResource(R.raw.mn90_azote);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			String chaine;

			chaine = bufferedReader.readLine();
			String[] interval = chaine.split(";");

			while ((chaine = bufferedReader.readLine()) != null) {
				String[] ligne = chaine.split(";");

				Map<Integer, Float> ligneAzote = new HashMap<Integer, Float>();
				int i = 1;
				while (i < ligne.length && ligne[i] != "") {
					//System.out.println(interval[i] + " => " + ligne[i]);
					ligneAzote.put(Integer.valueOf(interval[i]), Float.valueOf(ligne[i]));
					i++;
				}
				//System.out.println(ligne[0] + "===>" + ligneAzote);
				mn90Azote.put(ligne[0], ligneAzote);

			}
			bufferedReader.close();
		} catch (Exception e) {
			System.out.println("Probleme lors du chargement du tableau d'azote r�siduel ! " + e);
		}
	}

	private void loadMajorationMN90(Context context) {
		// chargement majoration
		try {
			InputStream inputStream = context.getResources().openRawResource(R.raw.mn90_majoration);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			String chaine;

			chaine = bufferedReader.readLine();
			String[] profondeur = chaine.split(";");

			while ((chaine = bufferedReader.readLine()) != null) {
				String[] ligne = chaine.split(";");

				Map<Integer, Integer> ligneAzote = new HashMap<Integer, Integer>();
				int i = 1;
				while (i < ligne.length && ligne[i] != "") {
					ligneAzote.put(Integer.valueOf(profondeur[i]), Integer.valueOf(ligne[i]));
					i++;
				}
				mn90Majoration.put(Float.valueOf(ligne[0]), ligneAzote);

			}
			bufferedReader.close();
		} catch (Exception e) {
			System.out.println("Probleme lors du chargement du tableau majoration ! " + e);
		}
	}
}

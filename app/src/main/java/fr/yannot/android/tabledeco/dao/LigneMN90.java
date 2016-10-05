package fr.yannot.android.tabledeco.dao;

import java.util.HashMap;
import java.util.Map;

public class LigneMN90 {

	private int profondeur_;
	private int temps_;
	//Paliers <profondeur, temps>
	private Map<Integer, Integer> paliers_ = new HashMap<Integer, Integer>();
	private int dtr_;
	private String gps_;

	public int getProfondeur() {
		return profondeur_;
	}

	public void setProfondeur(int profondeur) {
		this.profondeur_ = profondeur;
	}

	public int getTemps() {
		return temps_;
	}

	public void setTemps(int temps) {
		this.temps_ = temps;
	}

	public  Map<Integer, Integer> getPaliers() {
		return paliers_;
	}
		
	public int getDtr() {
		return dtr_;
	}

	public void setDtr(int dtr) {
		this.dtr_ = dtr;
	}

	public String getGps() {
		return gps_;
	}

	public void setGps(String gps) {
		this.gps_ = gps;
	}
}

package igralica.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import static igralica.controller.PocetnaStranaKontroler.korisnik;

@SuppressWarnings("serial")
public class Kljuc implements Serializable {

	public enum StatusKljuca {
		DOSTUPAN, AKTIVAN, ISKORISCEN
	}

	public enum TrajanjeKljuca {
		SAT, DAN, SEDMICA, NEOGRANICENO
	}

	private StatusKljuca statusKLjuca;
	private TrajanjeKljuca trajanjeKljuca;
	private LocalDateTime vrijemeAktiviranjaKljuca;
	private LocalDateTime vrijemeDeaktiviranjaKljuca;
	private String tipIgre;
	private String imeVlasnikaKljuca;

	public Kljuc() {
		super();
	}

	public Kljuc(StatusKljuca statusKLjuca, TrajanjeKljuca trajanjeKljuca, String tipIgre) {
		super();
		this.statusKLjuca = statusKLjuca;
		this.trajanjeKljuca = trajanjeKljuca;
		this.tipIgre = tipIgre;
		imeVlasnikaKljuca = "";
	}

	/*
	 * Aktivacija kljuca za konkretnog korisnika
	 */
	public void aktivirajKljuc(){
		statusKLjuca = StatusKljuca.AKTIVAN;
		setVrijemeAktiviranjaKljuca();
		setVrijemeDeaktiviranjaKljuca();
		imeVlasnikaKljuca = korisnik.getKorisnickoIme();
	}

	public StatusKljuca getStatusKLjuca() {
		return statusKLjuca;
	}

	public void setStatusKLjuca(StatusKljuca statusKLjuca) {
		this.statusKLjuca = statusKLjuca;
	}

	public LocalDateTime getVrijemeAktiviranjaKljuca() {
		return vrijemeAktiviranjaKljuca;
	}

	public void setVrijemeAktiviranjaKljuca(LocalDateTime vrijemeAktiviranjaKljuca) {
		this.vrijemeAktiviranjaKljuca = vrijemeAktiviranjaKljuca;
	}

	/*
	 * Postavlja trenutno vrijeme kao vrijeme aktiviranja kljuca
	 */
	public void setVrijemeAktiviranjaKljuca() {
		this.vrijemeAktiviranjaKljuca = LocalDateTime.now();
	}

	public LocalDateTime getVrijemeDeaktiviranjaKljuca() {
		return vrijemeDeaktiviranjaKljuca;
	}

	public void setVrijemeDeaktiviranjaKljuca(LocalDateTime vrijemeDeaktiviranjaKljuca) {
		this.vrijemeDeaktiviranjaKljuca = vrijemeDeaktiviranjaKljuca;
	}

	/*
	 * Postavlja vrijeme deaktiviranja kljuca u zavisnosti od njegovoga trajanja
	 */
	public void setVrijemeDeaktiviranjaKljuca() {
		if (trajanjeKljuca.equals(TrajanjeKljuca.SAT))
			vrijemeDeaktiviranjaKljuca = vrijemeAktiviranjaKljuca.plusHours(1);
		if (trajanjeKljuca.equals(TrajanjeKljuca.DAN))
			vrijemeDeaktiviranjaKljuca = vrijemeAktiviranjaKljuca.plusDays(1);
		if (trajanjeKljuca.equals(TrajanjeKljuca.SEDMICA))
			vrijemeDeaktiviranjaKljuca = vrijemeAktiviranjaKljuca.plusWeeks(1);
		if (trajanjeKljuca.equals(TrajanjeKljuca.NEOGRANICENO))
			vrijemeDeaktiviranjaKljuca = LocalDateTime.MAX;
	}

	public TrajanjeKljuca getTrajanjeKljuca() {
		return trajanjeKljuca;
	}

	public void setTrajanjeKljuca(TrajanjeKljuca trajanjeKljuca) {
		this.trajanjeKljuca = trajanjeKljuca;
	}

	public String getTipIgre() {
		return tipIgre;
	}

	public void setTipIgre(String tipIgre) {
		this.tipIgre = tipIgre;
	}

	public String getImeVlasnikaKljuca() {
		return imeVlasnikaKljuca;
	}

	public void setImeVlasnikaKljuca(String imeVlasnikaKljuca) {
		this.imeVlasnikaKljuca = imeVlasnikaKljuca;
	}
}

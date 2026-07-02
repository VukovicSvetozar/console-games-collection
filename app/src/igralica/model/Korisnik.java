package igralica.model;

import java.io.File;

public class Korisnik {

	private String ime;
	private String prezime;
	private String korisnickoIme;
	private int brojPoenaNaProfilu;
	private String lozinka;
	private File fotografija;
	private Igra trenutnaIgra;

	public Korisnik() {
		super();
	}

	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public String getKorisnickoIme() {
		return korisnickoIme;
	}

	public void setKorisnickoIme(String korisnickoIme) {
		this.korisnickoIme = korisnickoIme;
	}

	public int getBrojPoenaNaProfilu() {
		return brojPoenaNaProfilu;
	}

	public void setBrojPoenaNaProfilu(int brojPoenaNaProfilu) {
		this.brojPoenaNaProfilu = brojPoenaNaProfilu;
	}

	public String getLozinka() {
		return lozinka;
	}

	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}

	public File getFotografija() {
		return fotografija;
	}

	public void setFotografija(File fotografija) {
		this.fotografija = fotografija;
	}

	public Igra getTrenutnaIgra() {
		return trenutnaIgra;
	}

	public void setTrenutnaIgra(Igra trenutnaIgra) {
		this.trenutnaIgra = trenutnaIgra;
	}

}

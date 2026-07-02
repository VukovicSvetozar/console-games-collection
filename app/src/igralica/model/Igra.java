package igralica.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Igra implements Serializable {

	private String tipIgre;
	private String imeIgraca;
	private String datumIgranja;
	private int brojOsvojenihPoena;
	private int pozicijaURangListi;

	public Igra() {
		super();
	}

	public Igra(String tipIgre, String imeIgraca, String datumIgranja, int brojOsvojenihPoena) {
		super();
		this.tipIgre = tipIgre;
		this.imeIgraca = imeIgraca;
		this.datumIgranja = datumIgranja;
		this.brojOsvojenihPoena = brojOsvojenihPoena;
	}

	public String getTipIgre() {
		return tipIgre;
	}

	public void setTipIgre(String tipIgre) {
		this.tipIgre = tipIgre;
	}

	public String getImeIgraca() {
		return imeIgraca;
	}

	public void setImeIgraca(String imeIgraca) {
		this.imeIgraca = imeIgraca;
	}

	public String getDatumIgranja() {
		return datumIgranja;
	}

	public void setDatumIgranja(String datumIgranja) {
		this.datumIgranja = datumIgranja;
	}

	public int getBrojOsvojenihPoena() {
		return brojOsvojenihPoena;
	}

	public void setBrojOsvojenihPoena(int brojOsvojenihPoena) {
		this.brojOsvojenihPoena = brojOsvojenihPoena;
	}

	public int getPozicijaURangListi() {
		return pozicijaURangListi;
	}

	public void setPozicijaURangListi(int pozicijaURangListi) {
		this.pozicijaURangListi = pozicijaURangListi;
	}

}

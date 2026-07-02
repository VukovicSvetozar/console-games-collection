package igralica.utility;

import static igralica.dialogs.RegistracijaKorisnikaDijalog.listaKorisnickihImena;

public class ValidacijaUnosa {

	public static boolean praznoPolje(String unos) {
		if (unos == null) {
			return true;
		}
		return unos.trim().length() == 0;
	}

	public static boolean vecPostoji(String korisnickoIme) {
		for (String imeUListi : listaKorisnickihImena)
			if (imeUListi.equals(korisnickoIme))
				return true;
		return false;
	}

	public static boolean duzinaLozinke(String lozinka) {
		if (lozinka == null) {
			return true;
		}
		return lozinka.trim().length() < 5;
	}

	public static boolean nepodudaranjeLozinki(String lozinka, String ponovljenaLozinka) {
		return !lozinka.equals(ponovljenaLozinka);
	}
}

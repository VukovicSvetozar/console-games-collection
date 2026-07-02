package igralica.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.logging.Level;

import igralica.dialogs.ObavjestenjaDijalog;
import igralica.model.Kljuc;
import igralica.model.Kljuc.StatusKljuca;
import igralica.model.Kljuc.TrajanjeKljuca;

public class KreiranjeKjuca implements Putanje {

	private static HashMap<String, Kljuc> mapaKljuceva = new HashMap<String, Kljuc>();

	public static void main(String[] args) {

		mapaKljuceva.put("1111-1111-1111-1111", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.SAT, "Pogodi broj"));
		mapaKljuceva.put("1111-1111-1111-1112", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.SAT, "Pogodi broj"));
		mapaKljuceva.put("1111-1111-1111-1113", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.DAN, "Pogodi broj"));
		mapaKljuceva.put("1111-1111-1111-1114", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.DAN, "Pogodi broj"));
		mapaKljuceva.put("1111-1111-1111-1115", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.SEDMICA, "Pogodi broj"));
		mapaKljuceva.put("1111-1111-1111-1116", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.SEDMICA, "Pogodi broj"));
		mapaKljuceva.put("1111-1111-1111-1117",	new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.NEOGRANICENO, "Pogodi broj"));
		mapaKljuceva.put("1111-1111-1111-1118",	new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.NEOGRANICENO, "Pogodi broj"));
		mapaKljuceva.put("2222-2222-2222-2221", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.SAT, "Kviz"));
		mapaKljuceva.put("2222-2222-2222-2222", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.SAT, "Kviz"));
		mapaKljuceva.put("2222-2222-2222-2223", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.DAN, "Kviz"));
		mapaKljuceva.put("2222-2222-2222-2224", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.DAN, "Kviz"));
		mapaKljuceva.put("2222-2222-2222-2225", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.SEDMICA, "Kviz"));
		mapaKljuceva.put("2222-2222-2222-2226", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.SEDMICA, "Kviz"));
		mapaKljuceva.put("2222-2222-2222-2227", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.NEOGRANICENO, "Kviz"));
		mapaKljuceva.put("2222-2222-2222-2228", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.NEOGRANICENO, "Kviz"));
		mapaKljuceva.put("3333-3333-3333-3331", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.SAT, "Loto"));
		mapaKljuceva.put("3333-3333-3333-3332", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.SAT, "Loto"));
		mapaKljuceva.put("3333-3333-3333-3333", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.DAN, "Loto"));
		mapaKljuceva.put("3333-3333-3333-3334", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.DAN, "Loto"));
		mapaKljuceva.put("3333-3333-3333-3335", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.SEDMICA, "Loto"));
		mapaKljuceva.put("3333-3333-3333-3336", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.SEDMICA, "Loto"));
		mapaKljuceva.put("3333-3333-3333-3337", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.NEOGRANICENO, "Loto"));
		mapaKljuceva.put("3333-3333-3333-3338", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.NEOGRANICENO, "Loto"));
		mapaKljuceva.put("4444-4444-4444-1111",	new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.NEOGRANICENO, "Pogodi broj"));
		mapaKljuceva.put("4444-4444-4444-2222", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.NEOGRANICENO, "Kviz"));
		mapaKljuceva.put("4444-4444-4444-3333", new Kljuc(StatusKljuca.DOSTUPAN, TrajanjeKljuca.NEOGRANICENO, "Loto"));

		sacuvajKljuceve(mapaKljuceva, PUTANJA_DO_LISTE_KLJUCEVA);
	}

	/*
	 * Serijalizacija kljuceva
	 */
	private static boolean sacuvajKljuceve(HashMap<String, Kljuc> mapaKljuceva, String putanja) {
		File putanjaFile = new File(putanja);
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(putanjaFile))) {
			oos.writeObject(new HashMap<String, Kljuc>(mapaKljuceva));
			oos.close();
			return true;
		} catch (IOException ex) {
			FileLogger.log(Level.SEVERE, null, ex);
			ObavjestenjaDijalog.showErrorDialog("Greska", "Greska tokom serijalizacije kljuceva.",
					"Nije moguce sacuvati kljuceve na sljedecoj putanji: \n" + putanjaFile.getAbsolutePath());

		}
		return false;
	}
}

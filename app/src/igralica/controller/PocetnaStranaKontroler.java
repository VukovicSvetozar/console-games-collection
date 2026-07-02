package igralica.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import igralica.dialogs.ObavjestenjaDijalog;
import igralica.dialogs.RegistracijaKorisnikaDijalog;
import igralica.model.Korisnik;
import igralica.utility.FileLogger;
import igralica.utility.FxmlLoader;
import igralica.utility.Putanje;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class PocetnaStranaKontroler implements Putanje{

	@FXML
	private Label lblStatus;

	@FXML
	private TextField txtKorisnickoIme;

	@FXML
	private PasswordField txtLozinka;

	@FXML
	private Button btnPrijava;

	@FXML
	private Button btnRegistracija;

	public static Korisnik korisnik = new Korisnik();

	private static final int BROJ_ITERACIJA = 10000;
	private static final int DUZINA_KLJUCA = 256;

	@FXML
	void prijava(ActionEvent event){
		try {
			if (prijavaNaSistem(txtKorisnickoIme.getText(), new String(txtLozinka.getText()))) {
				FxmlLoader.load(getClass(), "/igralica/view/GlavnaStrana.fxml", "Glavna Strana");
			} else {
				lblStatus.setText("Pokušajte ponovo");

				String upozorenje = "Uneseno korisnicko ime ili sifra nisu korektni!";
				ObavjestenjaDijalog.showWarningDialog("Upozorenje", "Niste se uspjesno prijavili!", upozorenje);
			}
		} catch (Exception ex) {
			FileLogger.log(Level.SEVERE, null, ex);
		}
	}

	@FXML
	void registracija(ActionEvent event) {
		dodajKorisnika(new Korisnik());
	}

	private boolean prijavaNaSistem(String korisnickoIme, String unesenaLozinka) {
		try (BufferedReader in = new BufferedReader(new FileReader(PUTANJA_DO_LISTE_KORISNIKA))) {
			String linijaDatoteke;
			String[] podaci;
			while ((linijaDatoteke = in.readLine()) != null) {
				podaci = linijaDatoteke.split("#");
				if (korisnickoIme.equals(podaci[0])) {
					String salt = podaci[3];
					String zasticenaLozinka = podaci[4];
					boolean podudaranje = verifikacijaKorisnickeLozinka(unesenaLozinka, zasticenaLozinka, salt);
					if (podudaranje) {
						korisnik.setKorisnickoIme(podaci[0]);
						korisnik.setIme(podaci[1]);
						korisnik.setPrezime(podaci[2]);
						korisnik.setFotografija(new File(podaci[5]));
						return true;
					}
				}
			}
		} catch (IOException ex) {
			String porukaOGresci = "IO greska se javlja tokom citanja iz stream-a.!";
			ObavjestenjaDijalog.showErrorDialog("Greska", "Doslo je do greske!", porukaOGresci);
		}

		return false;
	}

	private boolean verifikacijaKorisnickeLozinka(String unesenaLozinka, String zasticenaLozinka, String salt) {
		boolean povratnaVrijednost = false;
		byte[] hesVrijednost = hes(unesenaLozinka.toCharArray(), salt.getBytes());
		String novaZasticenaLozinka = Base64.getEncoder().encodeToString(hesVrijednost);
		povratnaVrijednost = novaZasticenaLozinka.equalsIgnoreCase(zasticenaLozinka);

		return povratnaVrijednost;
	}

	private static byte[] hes(char[] lozinka, byte[] salt) {
		PBEKeySpec specifikacija = new PBEKeySpec(lozinka, salt, BROJ_ITERACIJA, DUZINA_KLJUCA);
		Arrays.fill(lozinka, Character.MIN_VALUE);
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			return skf.generateSecret(specifikacija).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new AssertionError("Greska tokom hesovanja lozinke: " + e.getMessage(), e);
		} finally {
			specifikacija.clearPassword();
		}
	}

	private void dodajKorisnika(Korisnik korisnik) {
		RegistracijaKorisnikaDijalog dialog = new RegistracijaKorisnikaDijalog("Dodaj", korisnik);
		dialog.kreirajIPrikazi();
	}

}

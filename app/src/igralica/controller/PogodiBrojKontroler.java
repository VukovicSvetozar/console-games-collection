package igralica.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import igralica.dialogs.ObavjestenjaDijalog;
import igralica.model.Igra;

import static igralica.controller.GlavnaStranaKontroler.lblBrojBodovaNaProfilu;
import static igralica.controller.GlavnaStranaKontroler.listaOdigranihIgara;
import static igralica.controller.GlavnaStranaKontroler.tblRangLista;
import static igralica.controller.PocetnaStranaKontroler.korisnik;

public class PogodiBrojKontroler implements Initializable {

	@FXML
	private TextField tfUneseniBroj;

	@FXML
	private Label lblPokusaj;

	@FXML
	private Label lblObavjestenje;

	@FXML
	private Button btnPostavi;

	@FXML
	private Button btnObrisi;

	@FXML
	private Button btnIzadji;

	private static boolean pokreni = false;
	private static boolean pogodio = false;
	private static boolean kraj = false;
	private TextField aktivnoPolje;
	private static ArrayList<Integer> postavljeniBrojevi = new ArrayList<Integer>();
	private static int indeksUnesenogBroja;
	private static int zamisljeniBroj;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		btnPostavi.setDisable(false);
		btnObrisi.setDisable(false);
		btnIzadji.setDisable(true);

		zamisliBroj();

		tfUneseniBroj.setText("");
		tfUneseniBroj.setEditable(true);

		tfUneseniBroj.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					aktivnoPolje = tfUneseniBroj;
				}
			}
		});
	}

	@FXML
	void btn0(ActionEvent event) {
		dodajBroj(0);
	}

	@FXML
	void btn1(ActionEvent event) {
		dodajBroj(1);
	}

	@FXML
	void btn2(ActionEvent event) {
		dodajBroj(2);
	}

	@FXML
	void btn3(ActionEvent event) {
		dodajBroj(3);
	}

	@FXML
	void btn4(ActionEvent event) {
		dodajBroj(4);
	}

	@FXML
	void btn5(ActionEvent event) {
		dodajBroj(5);
	}

	@FXML
	void btn6(ActionEvent event) {
		dodajBroj(6);
	}

	@FXML
	void btn7(ActionEvent event) {
		dodajBroj(7);
	}

	@FXML
	void btn8(ActionEvent event) {
		dodajBroj(8);
	}

	@FXML
	void btn9(ActionEvent event) {
		dodajBroj(9);
	}


	@FXML
	void postavi(ActionEvent event) {
		if (!pokreni) {
			if (!daLiJeBroj(tfUneseniBroj.getText())) {
				lblObavjestenje.setText("Ulaz mora biti broj!");
			} else {
				postavljeniBrojevi.add(indeksUnesenogBroja, Integer.parseInt(tfUneseniBroj.getText()));
				if (postavljeniBrojevi.get(indeksUnesenogBroja) == 0) {
					lblObavjestenje.setText("Broj 0 je van opsega!");
				} else {
					indeksUnesenogBroja++;
					tfUneseniBroj.setEditable(false);
					pokreni = true;
					provjeriBroj();
				}
			}
		}
	}

	@FXML
	void obrisi(ActionEvent event) {
		if (!pokreni) {
			if (tfUneseniBroj != null) {
				if (tfUneseniBroj.getText().length() > 0) {
					tfUneseniBroj.setText(tfUneseniBroj.getText().substring(0, tfUneseniBroj.getText().length() - 1));
				}
			}
		}
	}

	@FXML
	void izadji(ActionEvent event) {
		izracunajBodoveNaProfilu();
		tblRangLista.refresh();
		GlavnaStranaKontroler.osvjezi();

		indeksUnesenogBroja = 0;
		postavljeniBrojevi.clear();
		pogodio = false;
		kraj = false;
		pokreni = false;

		final Node source = (Node) event.getSource();
		final Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}

    private void zamisliBroj() {
		Random rand = new Random();
		zamisljeniBroj = rand.nextInt(100) + 1;
	}

	private void dodajBroj(int broj) {
		if (!pokreni) {
			if (aktivnoPolje != null) {
				String s = aktivnoPolje.getText();
				if (s.length() == 2) {
					if (s.startsWith("0")) {
						aktivnoPolje.setText(s.substring(1, s.length()) + broj);
					}
				} else {
					aktivnoPolje.setText(s + broj);
				}
			}
		}
	}

	private boolean daLiJeBroj(String s) {
		if (s.length() > 1) {
			if (s.charAt(0) == '0')
				s = s.substring(1);
		}
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	private void provjeriBroj() {
		int brojZaProvjeru = postavljeniBrojevi.get(indeksUnesenogBroja - 1);
		lblPokusaj.setText(indeksUnesenogBroja + ". pokusaj");
		pokreni = false;

		if (zamisljeniBroj < brojZaProvjeru) {
			lblObavjestenje.setText("X < " + brojZaProvjeru);
		} else if (zamisljeniBroj > brojZaProvjeru) {
			lblObavjestenje.setText("X > " + brojZaProvjeru);
		} else {
			if (indeksUnesenogBroja == 5 && daLiDaVaram()) {
				varajAkoMozes();
			} else {
				lblObavjestenje.setText("Pogodili ste broj!");
				pogodio = true;
				kraj = true;
			}
		}

		if (indeksUnesenogBroja == 5)
			kraj = true;

		tfUneseniBroj.setText("");
		if (kraj) {
			btnPostavi.setDisable(true);
			btnObrisi.setDisable(true);
			btnIzadji.setDisable(false);
			lblObavjestenje.setText("Trazeni broj: " + zamisljeniBroj);
			tfUneseniBroj.setText("Kraj");
			tfUneseniBroj.setEditable(false);
			if (pogodio)
				ObavjestenjaDijalog.showWarningDialog("Obavjestenje", "Pogodili ste broj!",
						"Osvojili ste: " + (100 / indeksUnesenogBroja) + " poena.");
			else
				ObavjestenjaDijalog.showWarningDialog("Obavjestenje", "Niste pogodili broj!",
						"Osvojili ste: " + 0 + " poena.");
		}
	}

	private boolean daLiDaVaram() {
		int brojOdigranihIgara = 0;
		int brojPobjeda = 0;

		for (Igra igra : listaOdigranihIgara) {
			if (igra.getImeIgraca().equals(korisnik.getKorisnickoIme()))
				brojOdigranihIgara++;
			if (igra.getBrojOsvojenihPoena() != 0)
				brojPobjeda++;
		}
		double procenatUspjesnosti = 0.0;
		if (brojOdigranihIgara > 0)
			procenatUspjesnosti = (brojPobjeda * 100.0) / brojOdigranihIgara;
		if (brojOdigranihIgara < 4 || procenatUspjesnosti < 40.0)
			return false;

		return true;
	}

	private void varajAkoMozes() {
		if ((!postavljeniBrojevi.contains(zamisljeniBroj - 1)) && (zamisljeniBroj != 1)) {
			zamisljeniBroj -= 1;
			lblObavjestenje.setText("Niste uspjeli! Trazeni broj je: " + zamisljeniBroj);
		} else if ((!postavljeniBrojevi.contains(zamisljeniBroj + 1)) && (zamisljeniBroj != 100)) {
			zamisljeniBroj += 1;
			lblObavjestenje.setText("Niste uspjeli! Trazeni broj je: " + zamisljeniBroj);
		} else {
			lblObavjestenje.setText("Pogodili ste broj!");
			pogodio = true;
		}
	}

	private void izracunajBodoveNaProfilu() {
		int brojOsvojenihPoena = 0;
		if (pogodio)
			brojOsvojenihPoena = 100 / indeksUnesenogBroja;
		korisnik.getTrenutnaIgra().setBrojOsvojenihPoena(brojOsvojenihPoena);
		korisnik.setBrojPoenaNaProfilu(korisnik.getBrojPoenaNaProfilu() + brojOsvojenihPoena);
		lblBrojBodovaNaProfilu.setText(Integer.toString(korisnik.getBrojPoenaNaProfilu()));
	}

}

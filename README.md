# ETFBL_IP Web Shop

*Sistem sastavljen od četiri međusobno povezane aplikacije — web shop, administratorska aplikacija, aplikacija za korisničku podršku i zajednički REST backend — rađen kao projektni zadatak iz predmeta Internet programiranje.*

Sistem simulira malu web prodavnicu (oglasnik) po uzoru na sajtove tipa OLX/Kupujem-prodajem: korisnici objavljuju ponude u proizvoljnim kategorijama (vozila, nekretnine, računari...), pretražuju i kupuju ponude drugih korisnika, dok administrator upravlja kategorijama i korisnicima, a operater korisničke podrške odgovara na poruke.

---

## 🧩 Arhitektura sistema

| Modul | Uloga | Tehnologije |
|---|---|---|
| **WebShopAppFrontend** | Javno dostupna prodavnica (kupci i prodavci) | Angular, Angular Material |
| **WebShopAppBackend** | Jedinstveni REST API koji koriste sva tri klijenta ispod | Spring Boot, Spring Security (JWT), Spring Data JPA |
| **AdministratorskaAplikacija** | Upravljanje kategorijama, specifikacijama i korisnicima | JSP (Model 2 / servlet + JSP) |
| **KorisnickaPodrska** | Pregled i odgovaranje na poruke korisnika | JSP (Model 2 / servlet + JSP) |

Sve četiri aplikacije dijele **istu MySQL bazu** i **isti Spring Boot backend** — administratorska aplikacija i aplikacija za podršku ne pristupaju bazi direktno za autentifikaciju, već i one šalju zahtjev ka backend-u (`/api/autentifikacija/token-administrator`) i dobijaju JWT token, isto kao Angular klijent.

---

## 🛍️ Web shop aplikacija (Angular + Spring Boot)

- **Registracija i aktivacija:** korisnik unosi ime, prezime, grad, korisničko ime, mail, lozinku i (opciono) jedan od 37 ponuđenih avatara. Na mail se šalje nasumičan 4-cifreni PIN; nalog postaje aktivan tek unosom tačnog PIN-a na stranici za aktivaciju. Ako se neaktivan korisnik pokuša prijaviti, PIN se ponovo generiše i šalje.
- **Prijava:** JWT autentifikacija (access + refresh token), token se čuva na klijentu i automatski dodaje na zaštićene pozive (`token.interceptor.ts`).
- **Pregled i pretraga ponuda:** ponude su prikazane kao kartice (naslov, slika, cijena) uz straničenje (Angular Material paginator). Moguće je filtrirati po kategoriji, stanju (nov/polovan), cijeni, datumu objave, korisničkom imenu i lokaciji, kao i sortirati rezultate.
- **Kategorije i specifični atributi:** svaka kategorija ima sopstveni skup dodatnih atributa (npr. za nekretnine: vrsta, sprat, broj kvadrata), koje definiše administrator; forma za objavljivanje ponude se dinamički prilagođava odabranoj kategoriji.
- **Detalji ponude i pitanja:** klikom na karticu otvara se stranica sa svim informacijama, slikama i pitanjima/odgovorima koje vidi svako ko otvori ponudu (javna konverzacija, ne privatna poruka).
- **Kupovina:** kupac bira način plaćanja — broj kartice ili naziv kurirske službe (plaćanje pouzećem) — nikad oba. Ne može se kupiti sopstvena ponuda, niti ponuda koja je već kupljena.
- **Moje ponude:** svaki korisnik može objaviti novu ponudu (sa slikama, jedna oznaka kao naslovna/poster slika), pregledati svoje aktivne i završene ponude, obrisati svoju ponudu, kao i pregledati svoju istoriju kupovina.
- **Profil:** korisnik može izmijeniti sve svoje podatke osim korisničkog imena.
- **Korisnička podrška:** prijavljeni korisnik može poslati poruku podršci direktno iz aplikacije.

## 🛠️ Administratorska aplikacija (JSP)

Zasebna prijava (nalog koji se ne kreira kroz aplikaciju, već direktno u bazi). Nakon prijave, dostupne su tri sekcije:

- **Kategorije** — CRUD nad kategorijama ponuda i njihovim specifičnim atributima.
- **Korisnici** — CRUD nad korisničkim nalozima web shop aplikacije.
- **Statistika** — pregled logova backend aplikacije, sa filtriranjem po nivou logovanja (INFO/DEBUG/WARN/ERROR...) i vremenskom periodu.

Implementirana je servlet-centričnim **Model 2** pristupom: jedan servlet (`AdministratorController`) prima sve akcije, priprema podatke u sesijskim beans-ovima i prosljeđuje ih odgovarajućoj JSP stranici.

## 💬 Aplikacija za korisničku podršku (JSP)

Takođe zasebna prijava, potpuno odvojena od administratorskog naloga (drugi korisnik, druga uloga — nalog se, kao i administratorski, kreira direktno u bazi). Operater vidi tabelu svih poruka korisnika; otvaranjem poruke njen status prelazi u "pročitana"; na poruku se odgovara slanjem mail-a korisniku; poruke se mogu pretraživati po sadržaju.

## 🔐 Autentifikacija, autorizacija i uloge

- Lozinke se heširaju **BCrypt** algoritmom (i za korisnike web shop-a i za administratorske/podrška naloge).
- Pristup je zaštićen **JWT** tokenima (`io.jsonwebtoken`), sa filterom koji presreće svaki zahtjev (`JwtAuthenticationFilter`) i bez server-side sesije (stateless).
- Modelovano je pet uloga: `GOST`, `REGISTROVANI` (registrovan, ali nije aktivirao nalog), `VERIFIKOVANI` (aktivan korisnik web shop-a), `ADMINISTRATOR` i `PODRSKA`.
- Administratorski i podrška nalog čuvaju se u istoj tabeli (`administrator`), razlikuju se preko atributa uloge — u skladu sa zahtjevom da se ti nalozi ne otvaraju kroz samu aplikaciju, već unosom direktno u bazu.

## 🗄️ Model podataka (pregled entiteta)

| Entitet | Opis |
|---|---|
| `Korisnik` | Nalog korisnika web shop-a (lični podaci, avatar, lozinka, PIN, uloga) |
| `Administrator` | Administratorski / podrška nalog (razlikovan poljem uloge) |
| `Kategorija` | Kategorija ponuda (npr. nekretnine, vozila...) |
| `Specifikacija` | Atribut specifičan za kategoriju (npr. "sprat" za nekretnine) |
| `Proizvod` | Ponuda — naslov, opis, cijena, stanje, lokacija, status (`AKTIVAN`/`ZAVRSEN`), kategorija, vlasnik |
| `ProizvodSpecifikacija` | Vrijednost konkretnog atributa za konkretnu ponudu |
| `ProizvodKomentar` | Pitanje/komentar na ponudi, vezan za korisnika i ponudu |
| `Kupovina` | Zabilježena kupovina — kupac, prodavac, ponuda, način plaćanja |
| `Placanje` | Podaci o plaćanju (broj kartice ili naziv kurirske službe) |
| `PorukaKorisnickePodrske` | Poruka poslata podršci, sa statusom pročitanosti i eventualnim odgovorom |

---

## 💻 Tehnologije po modulu

| Modul | Ključne tehnologije |
|---|---|
| Frontend | Angular, Angular Material, RxJS, TypeScript |
| Backend | Spring Boot 3, Spring Data JPA (Hibernate), Spring Security, JWT (`jjwt`), Spring Mail, ModelMapper, MySQL |
| Administratorska aplikacija | Jakarta Servlet/JSP (Model 2), Gson, BCrypt |
| Korisnička podrška | Jakarta Servlet/JSP (Model 2) |
| Baza podataka | MySQL (šema se generiše automatski preko Hibernate `ddl-auto=update`, bez procedura, funkcija i okidača) |

---

## 🚀 Kako pokrenuti projekat lokalno

### Preduslovi

- Java 17 i Maven (ili korišćenje priloženog `mvnw`)
- Node.js i Angular CLI
- MySQL server
- Servletski kontejner (npr. Apache Tomcat) za dvije JSP aplikacije

### 1. Baza podataka

Kreirajte praznu MySQL bazu (npr. `webshopapp`). Šema (tabele) će se automatski generisati pri prvom pokretanju backend-a.

### 2. Backend (WebShopAppBackend/webshop)

U `src/main/resources/application.properties` podesite **svoje** vrijednosti (ne koristite vrijednosti iz repozitorijuma — vidi napomenu ispod):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/webshopapp
spring.datasource.username=<vaš_korisnik>
spring.datasource.password=<vaša_lozinka>

spring.mail.username=<vaš_mail>
spring.mail.password=<vaša_app_lozinka_za_mail>

application.security.jwt.secret-key=<vaš_sopstveni_tajni_ključ>
```

Pokrenite: `./mvnw spring-boot:run` (backend se podiže na portu `8888`).

Nakon prvog pokretanja, ručno unesite red(ove) u tabelu `administrator` za administratorski i za podrška nalog (uz BCrypt heš lozinke) — kroz aplikaciju se ovi nalozi ne kreiraju, u skladu sa zadatkom.

### 3. Frontend (WebShopAppFrontend/web-shop-app)

```bash
npm install
ng serve
```

Aplikacija je dostupna na `http://localhost:4200` i poziva backend na `http://localhost:8888`.

### 4. Administratorska aplikacija i aplikacija za podršku

Build-ujte oba Maven projekta (`AdministratorskaAplikacija`, `KorisnickaPodrska`) u `.war` i deploy-ujte ih na Tomcat (podrazumijevano na `http://localhost:8080/...`). Obje aplikacije komuniciraju sa istim backend-om na portu `8888`.

---

## ⚠️ Napomena o bezbjednosti

`application.properties` i `ConnectionPool.properties` u ovom repozitorijumu sadrže **stvarne, upisane kredencijale** (lozinku za MySQL, mail nalog i app-lozinku za slanje mail-a, tajni ključ za JWT). Pošto je repozitorijum javan, ove vrijednosti treba smatrati kompromitovanim:

- promijenite lozinku MySQL `root` naloga i mail app-lozinku što prije,
- generišite nov JWT tajni ključ,
- ubuduće ovakve vrijednosti čuvajte kroz promjenljive okruženja (environment variables) ili lokalni fajl dodat u `.gitignore`, a u repozitorijumu ostavite samo primjer (npr. `application-example.properties`) sa praznim/izmišljenim vrijednostima.

---

## 📁 Struktura projekta

```
web-shop-app/
├── WebShopAppFrontend/web-shop-app/   → Angular aplikacija (kupci i prodavci)
├── WebShopAppBackend/webshop/         → Spring Boot REST API (zajednički za sve klijente)
├── AdministratorskaAplikacija/        → JSP aplikacija za administratora
├── KorisnickaPodrska/                 → JSP aplikacija za korisničku podršku
└── Tekst projekta/Zadatak.pdf         → tekst projektnog zadatka
```

---

## 👤 Autor

ETFBL_IP Web Shop je razvijen kao projektni zadatak iz predmeta **Internet programiranje** (decembar 2022), na **Elektrotehničkom fakultetu Univerziteta u Banjoj Luci**, autora **Svetozara Vukovića**.

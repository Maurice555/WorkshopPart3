package com.workshop3.model;

import javax.enterprise.context.SessionScoped;
import javax.inject.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import com.workshop3.service.KlantService;

import java.util.*;

@Named
@SessionScoped
@Entity
@Table(name = "Klant")
public class Klant implements java.io.Serializable {
	
	@Transient
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@Column(name = "voornaam")
	private String voornaam;
	
	@Column(name = "tussenvoegsel")
	private String tussenvoegsel;
	
	@Column(name = "achternaam")
	private String achternaam;
	
	@NotNull
	@Column(name = "email", unique = true)
	private String email;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "adresId")
	private Adres adres;
	
	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "bezorgAdres",
			joinColumns = @JoinColumn(name = "klantId"),
			inverseJoinColumns = @JoinColumn(name = "adresId"))
	private Set<Adres> bezorgAdressen;

	@OneToMany(mappedBy = "klant", cascade = CascadeType.MERGE)
	private Set<Account> accounts;
	
	@OneToMany(mappedBy = "klant", cascade = CascadeType.MERGE)
	private Set<Bestelling> bestellingen;
	
	public Klant() {}
	
	public Klant(String mail){
		this(null, null, mail);
	}
	
	public Klant(String voor, String achter, String mail){
		this(voor, null, achter, mail);
	}
	
	public Klant(String voor, String tussen, String achter, String mail){
		setVoornaam(voor);
		this.tussenvoegsel = tussen;
		setAchternaam(achter);
		setEmail(mail);
		this.adres = null;
		this.bestellingen = new HashSet<Bestelling>();
		this.accounts = new HashSet<Account>();
	}
	
	public long getId() {return this.id;}
	
	public void setId(long id) {this.id = id;}

	public String getVoornaam() {return this.voornaam;}
	
	public void setVoornaam(String voornaam) {this.voornaam = KlantService.firstCapital(voornaam);}

	public String getTussenvoegsel() {return this.tussenvoegsel != null ? this.tussenvoegsel + " " : "";}
	
	public void setTussenvoegsel(String tussenvoegsel) {this.tussenvoegsel = tussenvoegsel.isEmpty() ? null : tussenvoegsel;}

	public String getAchternaam() {return this.achternaam;}
	
	public void setAchternaam(String achternaam) {this.achternaam = KlantService.firstCapital(achternaam);}
	
	public String getEmail() {return this.email;}
	
	public void setEmail(String email) {this.email = email.toLowerCase();}
	
	public Adres getAdres() {return this.adres;}
	
	public void setAdres(Adres adres) {this.adres = adres;}
	
	public Set<Adres> getBezorgAdressen() {return this.bezorgAdressen;}

	public void setBezorgAdressen(Set<Adres> bezorgAdressen) {this.bezorgAdressen = bezorgAdressen;}

	public Set<Bestelling> getBestellingen() {return this.bestellingen;}
	
	public void setBestellingen(Set<Bestelling> bestellingen) {this.bestellingen = bestellingen;}

	public Set<Account> getAccounts() {return this.accounts;}

	public void setAccounts(Set<Account> accounts) {this.accounts = accounts;}
	
	
	public boolean hasAccount() {
		return this.accounts != null && !this.accounts.isEmpty();
	}
	
	public boolean hasAdres() {
		return this.adres != null;
	}
	
	public boolean hasValidEmail() {
		return KlantService.isValidEmail(this.email);
	}
		

	@Override
	public String toString(){
		return "klantnummer: " + this.id + "\n" + 
				this.voornaam + " " + getTussenvoegsel() + this.achternaam + "\n" + 
				this.email;
	}
	
	
	
	
	public static long getSerialversionuid() {return serialVersionUID;}
	
	
}

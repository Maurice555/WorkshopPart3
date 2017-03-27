package com.workshop3.model;

import java.util.*;

import javax.enterprise.context.SessionScoped;
import javax.inject.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;

import static com.workshop3.service.KlantService.*;
@XmlRootElement

@Named
@SessionScoped
@Entity
@Table(name = "Klant")
public class Klant implements EntityIface {
	
	@Transient
	private static final long serialVersionUID = 1L;
	
	@XmlID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@XmlElement
	@Column(name = "voornaam")
	private String voornaam;
	
	@XmlElement
	@Column(name = "tussenvoegsel")
	private String tussenvoegsel;
	
	@XmlElement
	@Column(name = "achternaam")
	private String achternaam;
	
	@XmlElement
	@NotNull
	@Column(name = "email", unique = true)
	private String email;

	@XmlElement
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
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
		setTussenvoegsel(tussen);
		setAchternaam(achter);
		setEmail(mail);
		this.bestellingen = new HashSet<Bestelling>();
		this.accounts = new HashSet<Account>();
		this.bezorgAdressen = new HashSet<Adres>();
	}
	
	@Override
	public long getId() {return this.id;}
	
	@Override
	public void setId(long id) {this.id = id;}

	public String getVoornaam() {return this.voornaam;}
	
	public void setVoornaam(String voornaam) {this.voornaam = firstCapital(voornaam.trim());}

	public String getTussenvoegsel() {return this.tussenvoegsel != null ? this.tussenvoegsel + " " : "";}
	
	public void setTussenvoegsel(String tussenvoegsel) {this.tussenvoegsel = tussenvoegsel.trim();}

	public String getAchternaam() {return this.achternaam;}
	
	public void setAchternaam(String achternaam) {this.achternaam = firstCapital(achternaam.trim());}
	
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
	
	
//	@Override
//	public String[] uniqueValue() {
//		return new String[] {getEmail()};
//	}
	
	@Override
	public Map<String, String> identifyingProps() {
		Map<String, String> props = new HashMap<String, String>();
		props.put("email", getEmail());
		return props;
	}
	
	@Override
	public String toString(){
		return "Klantnummer: " + this.id + "\n" + 
				this.voornaam + " " + getTussenvoegsel() + this.achternaam + "\n" + 
				this.email;
	}
	
	
	
	
	public static long getSerialversionuid() {return serialVersionUID;}
	
	
}

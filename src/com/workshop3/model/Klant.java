package com.workshop3.model;

import javax.enterprise.context.SessionScoped;
import javax.inject.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import com.workshop3.model.Adres.AdresType;
import com.workshop3.service.KlantService;

import java.util.*;

@Named
@SessionScoped
@Entity
@Table(name="Klant")
public class Klant implements java.io.Serializable {
	
	@Transient
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private long id;
	
	@Column(name="voornaam")
	private String voornaam;
	
	@Column(name="tussenvoegsel")
	private String tussenvoegsel;
	
	@Column(name="achternaam")
	private String achternaam;
	
	@NotNull
	@Column(name="email", unique = true)
	private String email;


//	@ElementCollection(fetch=FetchType.LAZY)
//	@CollectionTable(name = "klantHasAdres", joinColumns=@JoinColumn(name = "klantId"))
//	@JoinColumn(name = "adresId", referencedColumnName = "id")
//	@MapKeyColumn(name = "adresType")
	
	@ManyToMany(cascade=CascadeType.MERGE)
	@JoinTable(name="klantHasAdres", 
			joinColumns=@JoinColumn(name="klantId"),
			inverseJoinColumns=@JoinColumn(name="adresId"))
	@MapKeyEnumerated(EnumType.STRING)
	@MapKeyColumn(name = "adresType")
	private Map<AdresType, Adres> adressen;

	@OneToMany(mappedBy = "klant", fetch=FetchType.EAGER, cascade=CascadeType.MERGE)
	private List<Bestelling> bestellingen;
	
	public Klant() {this(null);}
	
	public Klant(String mail){
		this(null, null, mail);
	}
	
	public Klant(String voor, String achter, String mail){
		this(voor, null, achter, mail);
	}
	
	public Klant(String voor, String tussen, String achter, String mail){
		this.voornaam = voor;
		this.tussenvoegsel = tussen;
		this.achternaam = achter;
		this.email = mail;
		this.adressen = new HashMap<AdresType, Adres>();
		this.bestellingen = new ArrayList<Bestelling>();
	}
	
	public long getId() {return this.id;}
	
	public void setId(long id) {this.id = id;}

	public String getVoornaam() {return this.voornaam;}
	
	public void setVoornaam(String voornaam) {this.voornaam = KlantService.firstCapital(voornaam);}

	public String getTussenvoegsel() {return this.tussenvoegsel != null ? this.tussenvoegsel + " " : "";}
	
	public void setTussenvoegsel(String tussenvoegsel) {this.tussenvoegsel = tussenvoegsel;}

	public String getAchternaam() {return this.achternaam;}
	
	public void setAchternaam(String achternaam) {this.achternaam = KlantService.firstCapital(achternaam);}
	
	public String getEmail() {return this.email;}
	
	public void setEmail(String email) {this.email = email.toLowerCase();}
	
	public Map<AdresType, Adres> getAdressen() {
		return this.adressen;
	}
	public void setAdressen(Map<AdresType, Adres> adressen) {
		this.adressen = adressen;
	}

	public List<Bestelling> getBestellingen() {
		return this.bestellingen;
	}
	public void setBestellingen(List<Bestelling> bestellingen) {
		this.bestellingen = bestellingen;
	}

	@Override
	public String toString(){
		return "klantnummer: " + this.id + "\n" + 
				this.voornaam + " " + getTussenvoegsel() + this.achternaam + "\n" + 
				this.email;
	}

	
	public static long getSerialversionuid() {return serialVersionUID;}
	
	
}

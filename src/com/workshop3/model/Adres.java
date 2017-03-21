package com.workshop3.model;

import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.inject.*;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

import static com.workshop3.service.KlantService.*;
@XmlRootElement

@Named
@SessionScoped
@Entity
@Table(name="Adres", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"postcode", "huisnummer", "toevoeging"}, 
				name = "uniek_adres")})
public class Adres implements EntityIface {
	
	@Transient
	private static final long serialVersionUID = 2L;
	
	@XmlID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@XmlElement
	@Column(name = "straatnaam")
	private String straatnaam;
	
	@XmlElement
	@Column(name = "huisnummer")
	private int huisnummer;
	
	@XmlElement
	@Column(name = "toevoeging")
	private String toevoeging;
	
	@XmlElement
	@Column(name = "postcode")
	private String postcode;
	
	@XmlElement
	@Column(name = "woonplaats")
	private String woonplaats;
	
	@XmlIDREF
	@OneToMany(mappedBy = "adres")	
	private Set<Klant> bewoners;
	
	@XmlIDREF
	@ManyToMany
	@JoinTable(name = "bezorgAdres",
			joinColumns = @JoinColumn(name = "adresId"),
			inverseJoinColumns = @JoinColumn(name = "klantId"))
	private Set<Klant> bezorgers;
	
	public Adres() {}
	
	public Adres(String straat, int nummer, String postcode, String plaats) {
		this(straat, nummer, null, postcode, plaats);
	}
	
	public Adres(String straat, int nummer, String toevoeging, String postcode, String plaats) {
		setStraatnaam(straat);
		this.huisnummer = nummer;
		setToevoeging(toevoeging);
		setPostcode(postcode);
		setWoonplaats(plaats);
	}
	
	@Override
	public long getId() {return this.id;}
	
	@Override
	public void setId(long id) {this.id = id;}
	
	public String getStraatnaam() {return this.straatnaam;}
	
	public void setStraatnaam(String straatnaam) {this.straatnaam = firstCapital(straatnaam);}
	
	public int getHuisnummer() {return this.huisnummer;}
	
	public void setHuisnummer(int huisnummer) {this.huisnummer = huisnummer;}
	
	public String getToevoeging() {return this.toevoeging;}
	
	public void setToevoeging(String toevoeging) {this.toevoeging = toevoeging == null ? "" : trimUpCase(toevoeging);}
	
	public String getPostcode() {return this.postcode;}
	
	public void setPostcode(String postcode) {this.postcode = trimUpCase(postcode);}
	
	public String getWoonplaats() {return this.woonplaats;}
	
	public void setWoonplaats(String plaats) {this.woonplaats = firstCapital(plaats);}
		
	public Set<Klant> getBewoners() {return this.bewoners;}

	public void setBewoners(Set<Klant> bewoners) {this.bewoners = bewoners;}

	public Set<Klant> getBezorgers() {return this.bezorgers;}

	public void setBezorgers(Set<Klant> bezorgers) {this.bezorgers = bezorgers;}
	

	@Override
	public String[] uniqueValue() {
		return new String[] {getPostcode(), getHuisnummer() + "", getToevoeging()};
		
	}
	
	@Override
	public String toString() {
		return "Adresnummer " + this.id + "\n " + 
				this.straatnaam + " " + this.huisnummer + getToevoeging() + "\n " + 
				this.postcode + " " + this.woonplaats;
	}

	
	
	public static long getSerialversionuid() {return serialVersionUID;}

	
}

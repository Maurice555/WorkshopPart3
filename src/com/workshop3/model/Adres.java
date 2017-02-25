package com.workshop3.model;

import java.util.List;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.inject.*;
import javax.persistence.*;

import com.workshop3.service.KlantService;

@Named
@SessionScoped
@Entity
@Table(name="Adres", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"postcode", "huisnummer", "toevoeging"}, 
				name = "uniek_adres")})
public class Adres implements java.io.Serializable {
	
	@Transient
	private static final long serialVersionUID = 2L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private long id;
	
	@Column(name="straatnaam")
	private String straatnaam;
	
	@Column(name="huisnummer")
	private int huisnummer;
	
	@Column(name="toevoeging")
	private String toevoeging;
	
	@Column(name="postcode")
	private String postcode;
	
	@Column(name="woonplaats")
	private String woonplaats;
	
	public enum AdresType {
		AllesInEen, Bezorg, Post;
	}
	
	@ManyToMany(cascade=CascadeType.MERGE)
	@JoinTable(name="klantHasAdres", 
			joinColumns=@JoinColumn(name="adresId", referencedColumnName="id"),
			inverseJoinColumns=@JoinColumn(name="klantId", referencedColumnName="id")
	)
	private List<Klant> bewoners;
	
	public Adres() {}
	
	public Adres(String straat, int nummer, String postcode, String plaats) {
		this(straat, nummer, null, postcode, plaats);
	}
	
	public Adres(String straat, int nummer, String toevoeging, String postcode, String plaats) {
		this.straatnaam = straat;
		this.huisnummer = nummer;
		this.toevoeging = toevoeging;
		this.postcode = postcode;
		this.woonplaats = plaats;
	}
	
	public long getId() {return this.id;}
	
	public void setId(long id) {this.id = id;}
	
	public String getStraatnaam() {return this.straatnaam;}
	
	public void setStraatnaam(String straatnaam) {this.straatnaam = KlantService.firstCapital(straatnaam);}
	
	public int getHuisnummer() {return this.huisnummer;}
	
	public void setHuisnummer(int huisnummer) {this.huisnummer = huisnummer;}
	
	public String getToevoeging() {return this.toevoeging == null ? "" : this.toevoeging;}
	
	public void setToevoeging(String toevoeging) {this.toevoeging = toevoeging;}
	
	public String getPostcode() {return this.postcode;}
	
	public void setPostcode(String postcode) {this.postcode = postcode;}
	
	public String getWoonplaats() {return this.woonplaats;}
	
	public void setWoonplaats(String plaats) {this.woonplaats = KlantService.firstCapital(plaats);}
		
	public List<Klant> getBewoners() {return this.bewoners;}
	
	public void setBewoners(List<Klant> bewoners) {this.bewoners = bewoners;}

	
	@Override
	public String toString() {
		return "Adresnummer " + this.id + "\n " + 
				this.straatnaam + " " + this.huisnummer + " " + this.toevoeging + "\n " + 
				this.postcode + " " + this.woonplaats;
	}

	
	
	public static long getSerialversionuid() {return serialVersionUID;}

	
}

package com.workshop3.model;

import java.math.BigDecimal;
import java.text.NumberFormat;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.*;

@Named
@SessionScoped
@Entity
@Table(name = "Artikel")
public class Artikel implements EntityIface {
	
	@Transient
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@Column(name = "naam")
	private String naam;

	@Column(name = "omschrijving")
	private String omschrijving;

	@Column(name = "prijs", columnDefinition = "DECIMAL(30,20)")
	private BigDecimal prijs;

	public Artikel() {}
	
	public Artikel(String naam, String omschrijving, BigDecimal prijs) {
		this.naam = naam;
		this.omschrijving = omschrijving;
		this.prijs = prijs;
	}
	
	@Override
	public long getId() {return this.id;}

	@Override
	public void setId(long id) {this.id = id;}

	public String getNaam() {return this.naam;}

	public void setNaam(String naam) {this.naam = naam;}

	public String getOmschrijving() {return this.omschrijving;}

	public void setOmschrijving(String omschrijving) {this.omschrijving = omschrijving;}

	public BigDecimal getPrijs() {return this.prijs;}

	public void setPrijs(BigDecimal prijs) {this.prijs = prijs;}
	
	
	@Override
	public String toString() {
		return "Artikelnummer: " + getId() + " " + getNaam() + " " 
				+ getOmschrijving() + " " + NumberFormat.getCurrencyInstance().format(getPrijs());
	}

	@Override
	public boolean equals(Object a) {
		if (a instanceof Artikel) {
			return ((Artikel) a).getId() == this.id;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int)this.id;
	}
	
	
	public static long getSerialversionuid() {return serialVersionUID;}
	
	
	

}

package com.workshop3.model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.*;

@Named
@SessionScoped
@Entity
@Table(name = "Bestelling")
public class Bestelling implements java.io.Serializable {
	
	@Transient
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "klantId")
	private Klant klant;
	
	@Column(name = "datum")
	private Date datum;
	
	@ElementCollection
	@CollectionTable(name = "bestellingHasArtikel",
			joinColumns=@JoinColumn(name = "bestellingId"))
	@Column(name = "artikelAantal")
	@MapKeyJoinColumn(name = "artikelId", referencedColumnName = "id")
	private Map<Artikel, Integer> artikelen;
	
	public Bestelling() {
		this(null);
	}
	
	public Bestelling(Klant k) {
		this.klant = k;
		this.datum = new Date();
		this.artikelen = new HashMap();
	}

	public long getId() {return this.id;}

	public void setId(long id) {this.id = id;}

	public Klant getKlant() {return this.klant;}

	public void setKlant(Klant klant) {this.klant = klant;}

	public Date getDatum() {return this.datum;}

	public void setDatum(Date datum) {this.datum = datum;}

	public Map<Artikel, Integer> getArtikelen() {
		return this.artikelen;
	}
	public void setArtikelen(Map<Artikel, Integer> artikelen) {
		this.artikelen = artikelen;
	}
	
	public BigDecimal totaalPrijs() {
		BigDecimal total = new BigDecimal(0.0);
		for (Map.Entry<Artikel, Integer> entry : getArtikelen().entrySet()) {
			BigDecimal artikelTimesAantal = entry.getKey().getPrijs()
					.multiply(new BigDecimal(entry.getValue()));
			
			total = total.add(artikelTimesAantal);
		}
		return total;
	}
	
	@Override
	public String toString() {
		return "Bestellingnummer#: " + getId() + " niet null " + 
				NumberFormat.getCurrencyInstance().format(totaalPrijs());
	}

	
	
	public static long getSerialversionuid() {return serialVersionUID;}
	
	
	
	
}


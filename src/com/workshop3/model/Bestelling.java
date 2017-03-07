package com.workshop3.model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.*;

@Named
@SessionScoped
@Entity
@Table(name = "Bestelling")
public class Bestelling extends EntityTemplate {
	
	@Transient
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "klantId", referencedColumnName = "id")
	private Klant klant;
	
	@Column(name = "datum")
	private Date datum;
	
	@ElementCollection
	@CollectionTable(name = "bestellingHasStatus",
			joinColumns = @JoinColumn(name = "bestellingId"))
	@Column(name = "datum")
	@MapKeyColumn(name = "status")
	private Map<Integer, Date> stati;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "bestellingHasArtikel",
			joinColumns = @JoinColumn(name = "bestellingId"))
	@Column(name = "artikelAantal")
	@MapKeyJoinColumn(name = "artikelId", referencedColumnName = "id")
	private Map<Artikel, Integer> artikelen;
	
	public Bestelling() {
		this(null);
	}
	
	public Bestelling(Klant k) {
		this.klant = k;
		this.stati = new HashMap<Integer, Date>();
		this.datum = new Date();
		this.artikelen = new HashMap<Artikel, Integer>();
	}

	
	@Override
	public long getId() {return this.id;}

	@Override
	public void setId(long id) {this.id = id;}

	public Klant getKlant() {return this.klant;}

	public void setKlant(Klant klant) {this.klant = klant;}

	public Date getDatum() {return this.datum;}

	public void setDatum(Date datum) {this.datum = datum;}

	public Map<Artikel, Integer> getArtikelen() {return this.artikelen;}
	
	public void setArtikelen(Map<Artikel, Integer> artikelen) {this.artikelen = artikelen;}
	
	public Map<Integer, Date> getStati() {return this.stati;}

	public void setStati(Map<Integer, Date> stati) {this.stati = stati;}
	

	public void addArtikel(Artikel artikel, int aantal) {
		int totalAantal = aantal;
		if (this.artikelen.containsKey(artikel)) {
			totalAantal += this.artikelen.get(artikel);
		}
		this.artikelen.put(artikel, totalAantal);
	}
	
	public void removeArtikel(Artikel artikel, int aantal) {
		if (this.artikelen.containsKey(artikel)) {
			int newAantal = this.artikelen.get(artikel) - aantal;
			if (newAantal > 0) {
				this.artikelen.put(artikel, newAantal);
			} else {
				this.artikelen.remove(artikel);
			}
		}
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
		return "Bestellingnummer#: " + getId() + " " + getArtikelen() + " " + 
				NumberFormat.getCurrencyInstance().format(totaalPrijs());
	}

	
	public static long getSerialversionuid() {return serialVersionUID;}

	
	
	
}


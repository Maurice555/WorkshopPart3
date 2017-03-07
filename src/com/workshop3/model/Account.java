package com.workshop3.model;

import java.util.Date;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.*;

@Named
@SessionScoped
@Entity
@Table(name = "Account")
public class Account extends EntityTemplate {

	@Transient
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "datum")
	private Date datum;
	
	@ManyToOne
	@JoinColumn(name = "klantId")
	private Klant klant;
	
	@Column(name = "naam", unique = true)
	private String login;
	
	@Column(name = "password")
	private String pass;
	
	public Account() {}
	
	public Account(Klant klant) {
		this.klant = klant;
		this.datum = new Date();
	}
	

	public long getId() {return this.id;}

	public void setId(long id) {this.id = id;}

	public Klant getKlant() {return this.klant;}

	public void setKlant(Klant klant) {this.klant = klant;}

	public String getLogin() {return this.login;}

	public void setLogin(String login) {this.login = login;}

	public String getPass() {return this.pass;}

	public void setPass(String pass) {this.pass = pass;}
	
	public Date getDatum() {return this.datum;}

	public void setDatum(Date datum) {this.datum = datum;}

	
	@Override
	public String toString() {
		return "Accountnummer: " + this.id + " " + getKlant() + " " + this.login;
	}
	
	
	

	public static long getSerialversionuid() {return serialVersionUID;}
	
	

}

package com.workshop3.dao.mysql;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.*;
import javax.inject.*;
import javax.persistence.*;
import javax.transaction.Transactional;

import org.eclipse.persistence.exceptions.DatabaseException;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.workshop3.model.Adres;

@Named
@Transactional(rollbackOn = {MySQLIntegrityConstraintViolationException.class, DatabaseException.class})
@ConversationScoped
public class AdresDAO extends DAO<Adres> {
	
	private static final long serialVersionUID = 101L;
	
	@PersistenceContext(unitName = "CustomER")
	private EntityManager em;
	
	public AdresDAO() { super(Adres.class); }
	
	
	@Override 
	public void update(Adres a) throws RollbackException {}// Adressen mogen niet geupdate
	
	
	public List<Adres> findByStraatAndHuisnummer(String straat, int huisnummer, String plaats) {
		List<Adres> adresList = new ArrayList<Adres>();
		for (Adres a : findByStraat(straat, plaats)) {
			if(a.getHuisnummer() == huisnummer) {
				adresList.add(a);
			}
		}
		return adresList;
	}
	
	public List<Adres> findByStraat(String straat, String plaats) {
		return this.em.createNativeQuery
				("select * from Adres where straatnaam = '" + straat + "' and woonplaats = '" + plaats + "'", Adres.class)
				.getResultList();
	}

	public Adres findByPostcodeAndHuisnummer(String postcode, int huisnummer, String toevoeging) {
		Adres adres = null;
		for (Adres a : findByPostcodeAndHuisnummer(postcode, huisnummer)) {
			if (a.getToevoeging().equalsIgnoreCase(toevoeging)) {
				adres = a;
			}
		}
		return adres;
	}
	
	public List<Adres> findByPostcodeAndHuisnummer(String postcode, int huisnummer) {
		return this.em.createNativeQuery
				("select * from Adres where postcode = '" + postcode + "' and huisnummer = " + huisnummer, Adres.class)
				.getResultList();
	}
	
	
	
	public static long getSerialversionuid() {return serialVersionUID;}
	
	
	
}

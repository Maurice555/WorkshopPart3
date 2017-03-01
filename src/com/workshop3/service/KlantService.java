package com.workshop3.service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.*;
import javax.inject.*;
import javax.transaction.Transactional;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.workshop3.dao.mysql.*;
import com.workshop3.model.Account;
import com.workshop3.model.Adres;
import com.workshop3.model.Klant;
import com.workshop3.view.KlantView;

@Named
@SessionScoped
public class KlantService implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private KlantDAO klantDAO;
	
	@Inject
	private AdresDAO adresDAO;
	
	@Inject
	private AccountDAO accountDAO;
	
	private BestellingService bestelService;
	
	private KlantView klantView;

	public KlantService() {}

	
	public KlantDAO getKlantDAO() {return this.klantDAO;}
	
	public void setKlantDAO(KlantDAO klantDAO) {this.klantDAO = klantDAO;}

	public AdresDAO getAdresDAO() {return this.adresDAO;}

	public void setAdresDAO(AdresDAO adresDAO) {this.adresDAO = adresDAO;}
	
	public BestellingService getBestelService() {return this.bestelService;}
	
	@Inject
	public void setBestelService(BestellingService bestelService) {this.bestelService = bestelService;}

	public KlantView getKlantView() {return this.klantView;}
	
	@Inject
	public void setKlantView(KlantView klantView) {this.klantView = klantView;}


	public static String firstCapital(String s){
		return s.replace(s.substring(0, 1), s.substring(0, 1).toUpperCase());
	}
	
	public static boolean isValidEmail(String mail) {
		return mail.matches("([(\\w)+\\.-]+)[\\w^_]+@[\\w-\\.]+[\\w^_]+(\\.([\\w^0-9_]){2,4}){1,2}");
	}
/* 
WErkt NieT;	
	public boolean isKnownEmail(String mail) {
		return this.klantDAO.get(mail.toLowerCase()) != null;
	}
*/	
	
	public Klant klant() {
		return getKlantView().getKlant();
	}
	
	public long addOrUpdate(Klant k) {
		String mail = k.getEmail().toLowerCase(); 
		if (isValidEmail(mail)) {
			k.setEmail(mail);
			long id = k.getId();
			if (id > 0) {
				update(k, id);
				return id;
			}
			return add(k);
		} 
		return -1;
	}
	
	public void addBestellingToNewKlant(long bestelID) {
		getKlantView().setKlant(new Klant());
		klant().getBestellingen().add(getBestelService().get(bestelID));
	}

	public void update(Klant k, long id) {
		get(id);
		this.klantDAO.update(k);
	}
	
	public long add(Klant k) {
		try {
			this.klantDAO.save(k);
			return k.getId();
		} catch (SQLIntegrityConstraintViolationException ex) {
			return k.getId();
		}
	}
	
	public Klant get(long id) {
		return this.klantDAO.get(id);
	}
	
	public Klant get(String mail) {
		if (isValidEmail(mail))
			return this.klantDAO.get(mail.toLowerCase());
		
		return null;
	}
	
	public List<Klant> getTable(){
		return this.klantDAO.getAll();
	}
			
	public Klant del(long id) {
		Klant k = get(id);
		this.klantDAO.delete(id);
		return k;
	}

	public long add(Adres adres) {
		try {
			this.adresDAO.save(adres);
			return adres.getId();
		} catch (SQLIntegrityConstraintViolationException ex) {
			return adres.getId();
		}
	}
	
	public Adres getAdres(long adresId) {
		return this.adresDAO.get(adresId);
	}
	
	public Set<Account> getAccounts(long id) {
		return this.klantDAO.get(id).getAccounts();
	}
	
	public Account login(String login) {
		return this.accountDAO.get(login);
	}
	
	
	public Account getAccount(long accountID) {
		return this.accountDAO.get(accountID);
	}
	
	public long add(Account account) {
		try {
			this.accountDAO.save(account);
			return account.getId();
		} catch (SQLIntegrityConstraintViolationException e) {
			return account.getId();
		}
	}
	
	public void update(Account account, long id) {
		getAccount(id);
		this.accountDAO.update(account);
	}



	
	public static long getSerialversionuid() {return serialVersionUID;}


	
	
	
}

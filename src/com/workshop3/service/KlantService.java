package com.workshop3.service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

import javax.enterprise.context.*;
import javax.inject.*;
import javax.transaction.*;

import com.workshop3.dao.mysql.*;
import com.workshop3.model.*;
import com.workshop3.view.KlantView;

@Named
@SessionScoped
public class KlantService extends AbstractEntityService<Klant> {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private KlantDAO klantDAO;
	
	@Inject
	private AdresDAO adresDAO;
	
	@Inject
	private AccountDAO accountDAO;
	
	private BestellingService bestelService;
	
	private KlantView klantView;

	private static final String mailReedsInGebruik = "Dit e-mailadres is reeds in gebruik";

	private static final int duplicateKey = 1062;

	public KlantService() { super(new KlantDAO()); }

	
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
/* Werkt niet
	public boolean isKnownEmail(String mail) {
		return this.klantDAO.getEm().contains(this.klantDAO.get(mail));
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
	
	public void addBestellingToNewKlant(long bestelID) { // OF hier gewoon een Bestelling afleveren
		getKlantView().setKlant(new Klant());
		klant().getBestellingen().add(getBestelService().get(bestelID));
	}
	public Klant get(String mail) {
		if (isValidEmail(mail)) {
			return this.klantDAO.get(mail.toLowerCase());
		}
		return null;
	}

	public long add(Adres adres) {
		try {
			this.adresDAO.save(adres);
			return adres.getId();
/*
		} catch (SQLIntegrityConstraintViolationException e) {
			if (e.getErrorCode() == duplicateKey) {
				return 9;
			}
/*Anders zo... werkt ook niet

		} catch (ServletException se) { // Uncatchable due to serverbug: https://java.net/jira/si/jira.issueviews:issue-html/GLASSFISH-21172/GLASSFISH-21172.html
			if (se..getRootCause() instanceof DatabaseException) { https://java.net/jira/browse/GLASSFISH-21172
				DatabaseException de = (DatabaseException) se.getRootCause();
				if (de.getDatabaseErrorCode() == duplicateKey) {
					return 24; // testing 124
				} else if (de.getInternalException() instanceof MySQLIntegrityConstraintViolationException) {
					return 21;
				}
			}

		} catch (RollbackException re) { // Kan ook al niet
			re.getCause(); // ServerBug	
*/	
		} catch (TransactionalException te) { // Is wat we catchen
			if (te.getCause() instanceof RollbackException) { 
				// Aanname is hier dat het om de Constraint is gegaan...
				return this.adresDAO.findByPostcodeAndHuisnummer
						(adres.getPostcode(), adres.getHuisnummer(), adres.getToevoeging())
						.getId();
			}
		}
		return 0;
	}
	
	public Adres getAdres(long adresId) {
		return this.adresDAO.get(adresId);
	}
	
	public Set<Account> getAccounts(long id) {
		return this.klantDAO.get(id).getAccounts();
	}
	
	public Account login(String login, String pass) {
		if (this.accountDAO.get(login).getPass().equals(pass)) {
			Account act = this.accountDAO.get(login);
			getKlantView().setAccount(act);
			return act;
		}
		return getAccount(-1);
	}
	
	public Account getAccount(long accountID) {
		return this.accountDAO.get(accountID);
	}
	
	public long add(Account account) {
		try {
			this.accountDAO.save(account);
			return account.getId();
		} catch (TransactionalException te) {
			return rollbackCheck(te);
		}
	}
	
	public void update(Account account, long id) {
		getAccount(id);
		try {
			this.accountDAO.update(account);
		} catch (TransactionalException e) {
			rollbackCheck(e);
		}
	}


	
	
	public static long getSerialversionuid() {return serialVersionUID;}


	
	
	
}

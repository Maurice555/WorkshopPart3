package com.workshop3.dao.mysql;

import javax.enterprise.context.ConversationScoped;
import javax.persistence.*;

import com.workshop3.model.Account;

@ConversationScoped
public class AccountDAO extends DAO<Account> {
	
	private static final long serialVersionUID = 1L;
	
	@PersistenceContext(unitName = "CustomER")
	private EntityManager em;
	
	public AccountDAO() { super(Account.class); }

	
//	@Override
//	public Account getUnique(String[] uniqueValues) {
//		return get(uniqueValues[0]);
//	}
	
	public Account get(String login) {
		return (Account) this.em.createNativeQuery(
				"select * from Account where naam = '" + login + "'", Account.class)
				.getSingleResult();
	}

}

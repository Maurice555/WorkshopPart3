package com.workshop3.dao.mysql;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;
import javax.persistence.*;
import javax.transaction.Transactional;

import com.workshop3.model.Account;

@Named
@Transactional
@ConversationScoped
public class AccountDAO extends DAO<Account>{
	
	private static final long serialVersionUID = 1L;
	
	@PersistenceContext(unitName = "CustomER")
	private EntityManager em;
	
	public AccountDAO() { super(Account.class); }

	public Account get(String login) {
		return (Account) this.em.createNativeQuery(
				"select * from Account where naam = '" + login + "'", Account.class)
				.getSingleResult();
	}

}

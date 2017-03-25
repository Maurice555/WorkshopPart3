package com.workshop3.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.TransactionalException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.workshop3.dao.DAOIface;
import com.workshop3.model.EntityIface;

@Dependent
public abstract class AbstractEntityService<E extends EntityIface> implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	protected static final String ID = "Id={id : [\\d]+}";
	
	@Inject
	private DAOIface<E> entityDAO;
	
	public AbstractEntityService(DAOIface<E> dao) {
		this.entityDAO = dao;
	}
	
	
	public DAOIface<E> getDAO() {return this.entityDAO;}
	
	public void setDAO(DAOIface<E> dao) {this.entityDAO = dao;}
	
	
	@GET @Path(ID)
	@Produces(MediaType.APPLICATION_JSON)
	public E get(@PathParam("id") long id) {
		return this.entityDAO.get(id);
	}
	
	public E getUnique(String[] uniqueValues) {
		return this.entityDAO.getUnique(uniqueValues);
	}
	
	public Set<E> get(Map<String, String> keyValues) {
		return new HashSet<E>(this.entityDAO.get(keyValues));
	}
	
	protected static final int txExc = -2;
	protected static final int saveExc = 0;
		
	public long add(E e) {
		try {
			this.entityDAO.save(e);
			return e.getId();
		} catch (SQLException sqlexc) {
			if (isDuplicateKeyError(sqlexc)) {
				return getUnique(e.uniqueValue()).getId();
			}
		} catch (TransactionalException txexc) {
			if (isDuplicateKeyError(isSQLCauseForRollback(txexc))) {
				return getUnique(e.uniqueValue()).getId();
			}
			return txExc;
		}
		return saveExc;
	}
	
	public long update(E e) {
		try {
			this.entityDAO.update(e);
			return e.getId();
		} catch (SQLException sqlexc) {
			if (isDuplicateKeyError(sqlexc))
				return getUnique(e.uniqueValue()).getId();
		} catch (TransactionalException txexc) {
			if (isDuplicateKeyError(isSQLCauseForRollback(txexc))) {
				return getUnique(e.uniqueValue()).getId();
			}
			return txExc;
		}
		return saveExc;
	}

	public E delete(long id) {
		E e = get(id);
		this.entityDAO.delete(id);
		return e;
	}
	
	@GET @Path("fetch")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<E> fetch() {
		return new HashSet<E>(this.entityDAO.getAll());
	}
	
// utilities - from URIstring to ParamValues Hier misschien een aparte klasse voor schrijven RestUtil oid..
	protected static Map<String, String> getParamValuePairs(String uriQuery) {
		Map<String, String> uriParams = new HashMap<String, String>();
		if (uriQuery.matches("[;]")) { return uriParams; }
		for (String s : uriQuery.split("&")) {
			String[] keyParamPair = s.split("=");
			switch (keyParamPair.length) {
				case 1:
					uriParams.put(keyParamPair[0], ""); break;
				default:
					uriParams.put(keyParamPair[0], keyParamPair[1]); break;				
			}
		}
		return uriParams;
	}

	private static final String END = "Till2Limited";
	private static final String BEGIN = "Frumd0Sig";
	private static final String DATE = "TimpleTor";
// Parsen
	protected static Map<String, Object> getQuantities(Map<String, String> paramValues) {
		Map<String, Object> queryProps = new HashMap<String, Object>();
		
		for (Map.Entry<String, String> entry : paramValues.entrySet()) {
			String param = entry.getKey(), value = entry.getValue();
			
			if (value.matches("(P|p)([\\d]+[YMWD])+")) {
				queryProps.put("period1-2", Period.parse(value));
			} else if (value.matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
				LocalDate d = LocalDate.parse(value);
				if (param.matches("(van(af)?|begin|from).*")) { queryProps.put(BEGIN, d); } 
				else if (param.matches("(until|tot|e(i)?nd).*")) { queryProps.put(END, d); }
				else { queryProps.put(DATE, d); }
			} else {
				try {
					queryProps.put(param, Long.parseLong(value));
				} catch (NumberFormatException nfexc) {
					queryProps.put(param, value);
				}
			}
		}
		return queryProps;
	}
// more utilities - coupling date (begin, end etc.) to period.. DAO uses (LocalDate start, Period p)	
	protected static Period getPeriod(Map<String, Object> quantities) {
		if (quantities.containsKey(END)) { return getBeginDate(quantities).until((LocalDate) quantities.get(END)); }
		quantities.values().removeIf((Object o) -> ( ! (o instanceof Period)));
		if (quantities.values().iterator().hasNext()) { return (Period) quantities.values().iterator().next(); }
		return Period.ofDays(1); 
	}

	protected static LocalDate getBeginDate(Map<String, Object> quantities) {
		if (quantities.containsKey(BEGIN)) { return (LocalDate) quantities.get(BEGIN); }
		if (quantities.containsKey(DATE)) { return (LocalDate) quantities.get(DATE); }
		return LocalDate.now().minus(getPeriod(quantities));
	}
	
// Een hack voor die gewrapte exceptions	
	protected static SQLException isSQLCauseForRollback(TransactionalException te) {
		Throwable cause = te.getCause();
		while (cause.getCause() != null) {
			cause = cause.getCause();
		}
		if (cause instanceof SQLException) {
			return (SQLException) cause; 
		}
		return null;
	}
	
	protected static final int duplicateKey = 1062;

	protected static boolean isDuplicateKeyError(SQLException sqlexc) {
		return sqlexc != null && sqlexc.getErrorCode() == duplicateKey;
	}
		

	
	
	public static long getSerialversionuid() {return serialVersionUID;}


		
}

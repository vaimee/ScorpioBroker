package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.CSourceDAOSPARQL;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.EntityInfoDAOSPARQL;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.EntityStorageReaderDAOSPARQL;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.HistoryDAOSPARQL;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.StorageReaderDAOSPARQL;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.StorageWriterDAOSPARQL;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.sparql.SubscriptionInfoDAOSPARQL;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.sql.StorageWriterDAOSQL;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.sql.SubscriptionInfoDAOSQL;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.sql.CSourceDAOSQL;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.sql.EntityInfoDAOSQL;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.sql.EntityStorageReaderDAOSQL;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.sql.HistoryDAOSQL;
import eu.neclab.ngsildbroker.commons.storage.dasibreaker.sql.StorageReaderDAOSQL;


@Component
public class QueryLanguageFactory {
	
	private final static QueryLanguage entityHandlerType = QueryLanguage.SQL;

	@Autowired
	private  ApplicationContext context;
	
	private static QueryLanguageFactory instance;
	public static QueryLanguageFactory getInstance() {
		return instance;
	}
	
	public QueryLanguageFactory() {
		instance = this;
	}

	//----------------------------------------------------------------------------------------------------StorageReaderDAO
	public  IStorageReaderDao getStorageReaderDao() {
		return getStorageReaderDao(entityHandlerType);
	}
	
	public  IStorageReaderDao getStorageReaderDao(QueryLanguage entityHandlerType) {
		if(entityHandlerType==QueryLanguage.SPARQL) {
			StorageReaderDAOSPARQL srDAO = new StorageReaderDAOSPARQL();
			srDAO.init();
			return srDAO;
		}else {//default SQL
			AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
			StorageReaderDAOSQL storageReaderDAOSQL = new StorageReaderDAOSQL();
			factory.autowireBean( storageReaderDAOSQL );
			factory.initializeBean( storageReaderDAOSQL, "StorageReaderDAOSLQ" );
			return storageReaderDAOSQL;
		}
		
	}
	
	//----------------------------------------------------------------------------------------------------StorageWriterDAO
	public  IStorageWriterDAO getStorageWriterDao() {
		return getStorageWriterDao(entityHandlerType);
	}
	
	public  IStorageWriterDAO getStorageWriterDao(QueryLanguage entityHandlerType) {
		
		if(entityHandlerType==QueryLanguage.SPARQL) {
			return new StorageWriterDAOSPARQL();
		}else {//default SQL
			AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
			StorageWriterDAOSQL storageWriterDAOSQL = new StorageWriterDAOSQL();
			factory.autowireBean( storageWriterDAOSQL );
			factory.initializeBean( storageWriterDAOSQL, "StorageWriterDAOSQL" );
			return storageWriterDAOSQL;
		}
		
	}
	
	//----------------------------------------------------------------------------------------------------EntityStorageReaderDAO
	public  IEntityStorageReaderDAO getEntityStorageReaderDAO() {
		return getEntityStorageReaderDAO(entityHandlerType);
	}
	
	public  IEntityStorageReaderDAO getEntityStorageReaderDAO(QueryLanguage entityHandlerType) {
		if(entityHandlerType==QueryLanguage.SPARQL) {
			return new EntityStorageReaderDAOSPARQL();
		}else {//default SQL
			AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
			EntityStorageReaderDAOSQL entityStorageReaderDAOSQL = new EntityStorageReaderDAOSQL();
			factory.autowireBean( entityStorageReaderDAOSQL );
			factory.initializeBean( entityStorageReaderDAOSQL, "EntityStorageReaderDAOSQL" );
			return entityStorageReaderDAOSQL;
		}
		
	}
	
	//----------------------------------------------------------------------------------------------------CSourceDAO
	public  ICSourceDAO getCSourceDAO() {
		return getCSourceDAO(entityHandlerType);
	}
	
	public  ICSourceDAO getCSourceDAO(QueryLanguage entityHandlerType) {
		if(entityHandlerType==QueryLanguage.SPARQL) {
			return new CSourceDAOSPARQL();
		}else {//default SQL
			AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
			CSourceDAOSQL cSourceDAOSQL = new CSourceDAOSQL();
			factory.autowireBean( cSourceDAOSQL );
			factory.initializeBean( cSourceDAOSQL, "CSourceDAOSQL" );
			return cSourceDAOSQL;
		}
		
	}
	
	//----------------------------------------------------------------------------------------------------HistoryDAO
		public  IHistoryDAO getHistoryDAO() {
			return getHistoryDAO(entityHandlerType);
		}
		
		public  IHistoryDAO getHistoryDAO(QueryLanguage entityHandlerType) {
			if(entityHandlerType==QueryLanguage.SPARQL) {
				return new HistoryDAOSPARQL();
			}else {//default SQL
				AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
				HistoryDAOSQL historyDAOSQL = new HistoryDAOSQL();
				factory.autowireBean( historyDAOSQL );
				factory.initializeBean( historyDAOSQL, "HistoryDAOSQL" );
				return historyDAOSQL;
			}
			
		}
		
		//----------------------------------------------------------------------------------------------------EntityInfoDAO
		public  IEntityInfoDAO getEntityInfoDAO() {
			return getEntityInfoDAO(entityHandlerType);
		}
		
		public  IEntityInfoDAO getEntityInfoDAO(QueryLanguage entityHandlerType) {
			if(entityHandlerType==QueryLanguage.SPARQL) {
				return new EntityInfoDAOSPARQL();
			}else {//default SQL
				AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
				EntityInfoDAOSQL entityInfoDAOSQL = new EntityInfoDAOSQL();
				factory.autowireBean( entityInfoDAOSQL );
				factory.initializeBean( entityInfoDAOSQL, "EntityInfoDAOSQL" );
				return entityInfoDAOSQL;
			}
			
		}
		
		//----------------------------------------------------------------------------------------------------SubscriptionInfoDAO
		public  ISubscriptionInfoDAO getSubscriptionInfoDAO() {
			return getSubscriptionInfoDAO(entityHandlerType);
		}
		
		public  ISubscriptionInfoDAO getSubscriptionInfoDAO(QueryLanguage entityHandlerType) {
			if(entityHandlerType==QueryLanguage.SPARQL) {
				return new SubscriptionInfoDAOSPARQL();
			}else {//default SQL
				AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
				SubscriptionInfoDAOSQL subscriptionInfoDAOSQL = new SubscriptionInfoDAOSQL();
				factory.autowireBean( subscriptionInfoDAOSQL );
				factory.initializeBean( subscriptionInfoDAOSQL, "SubscriptionInfoDAOSQL" );
				return subscriptionInfoDAOSQL;
			}
			
		}
}

package eu.neclab.ngsildbroker.entityhandler.services.dasibreaker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.neclab.ngsildbroker.commons.ngsiqueries.ParamsResolver;
import eu.neclab.ngsildbroker.commons.tools.HttpUtils;
import eu.neclab.ngsildbroker.entityhandler.services.EntityService;

public class StorageWriterDAOFactory {

	private final static QueryLanguage entityHandlerType = QueryLanguage.SPARQL;
	
	public static IStorageWriterDAO get(JdbcTemplate writerJdbcTemplate, TransactionTemplate writerTransactionTemplate,
			JdbcTemplate writerJdbcTemplateWithTransaction) {
		return get(entityHandlerType,writerJdbcTemplate,  writerTransactionTemplate,
				 writerJdbcTemplateWithTransaction);
		
	}
	
	public static IStorageWriterDAO get(QueryLanguage entityHandlerType,JdbcTemplate writerJdbcTemplate, TransactionTemplate writerTransactionTemplate,
			JdbcTemplate writerJdbcTemplateWithTransaction) {
		if(entityHandlerType==QueryLanguage.SPARQL) {
			return new StorageWriterDAOSPARQL();
		}else {//default SQL
			return new StorageWriterDAOSQL( writerJdbcTemplate,  writerTransactionTemplate,
					 writerJdbcTemplateWithTransaction);
		}
		
	}
}

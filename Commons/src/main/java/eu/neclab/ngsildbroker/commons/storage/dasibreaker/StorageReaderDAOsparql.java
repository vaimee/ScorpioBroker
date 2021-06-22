package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.util.ReflectionUtils;
import eu.neclab.ngsildbroker.commons.constants.DBConstants;
import eu.neclab.ngsildbroker.commons.constants.NGSIConstants;
import eu.neclab.ngsildbroker.commons.datatypes.GeoqueryRel;
import eu.neclab.ngsildbroker.commons.datatypes.QueryParams;
import eu.neclab.ngsildbroker.commons.enums.ErrorType;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;

public class StorageReaderDAOsparql implements IStorageReaderDao {

	private final static Logger logger = LogManager.getLogger(StorageReaderDAOsparql.class);


	@PostConstruct
	public void init() {
	}

	
	public List<String> query(QueryParams qp) {
			return null;
	}

	public String getListAsJsonArray(List<String> s) {
		return "[" + String.join(",", s) + "]";
	}

	public List<String> getLocalTypes() {
		return null;
	}
	
	public List<String> getAllTypes() {

		return null;
	}
	
	public String typesAndAttributeQuery(QueryParams qp) throws ResponseException {

		return null;
	}


	public String translateNgsildQueryToSql(QueryParams qp) throws ResponseException {
		throw new ResponseException("Not implemented yet.");
	}
	
	public String translateNgsildGeoqueryToPostgisQuery(GeoqueryRel georel, String geometry, String coordinates,
			String geoproperty, String dbColumn) throws ResponseException {

		throw new ResponseException("Not implemented yet.");
	}

	public String translateNgsildGeoqueryToPostgisQuery(GeoqueryRel georel, String geometry, String coordinates,
			String geoproperty) throws ResponseException {
		return this.translateNgsildGeoqueryToPostgisQuery(georel, geometry, coordinates, geoproperty, null);
	}

}

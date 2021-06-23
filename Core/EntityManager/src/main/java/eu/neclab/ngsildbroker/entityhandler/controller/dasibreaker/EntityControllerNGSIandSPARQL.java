package eu.neclab.ngsildbroker.entityhandler.controller.dasibreaker;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.time.format.DateTimeParseException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParseException;

import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;
import eu.neclab.ngsildbroker.commons.ngsiqueries.ParamsResolver;
import eu.neclab.ngsildbroker.commons.tools.HttpUtils;
import eu.neclab.ngsildbroker.entityhandler.services.EntityService;

public class EntityControllerNGSIandSPARQL implements IEntityHandler {

	private IEntityHandler ecSparql;
	private IEntityHandler ecNgsi;
	
	public EntityControllerNGSIandSPARQL(EntityService entityService, ObjectMapper objectMapper, ParamsResolver paramsResolver,
			HttpUtils httpUtils) {
		ecSparql= EntityHandlerFactory.get(EntityHandlerType.SPARQL,entityService,objectMapper,paramsResolver,httpUtils);
		ecNgsi= EntityHandlerFactory.get(EntityHandlerType.NGSI,entityService,objectMapper,paramsResolver,httpUtils);
	}
	
	@Override
	public ResponseEntity<byte[]> createEntity(HttpServletRequest request, String payload) 
			throws ResponseException, MalformedURLException, UnsupportedEncodingException , Exception {
		ResponseEntity<byte[]> ris;
		//we need grant ACID i think this try-catch is not enough
		try {
			 ris = ecNgsi.createEntity(request, payload);
			 ecSparql.createEntity(request, payload);
		} catch (ResponseException exception) {
			throw exception;
		} catch (DateTimeParseException exception) {
			throw exception;
		} catch (JsonParseException exception) {
			throw exception;
		} catch (Exception exception) {
			throw exception;
		}
		return ris;
	}

	@Override
	public ResponseEntity<byte[]> updateEntity(HttpServletRequest request, String payload)
			throws ResponseException, MalformedURLException, UnsupportedEncodingException , Exception {
		ResponseEntity<byte[]> ris;
		//we need grant ACID i think this try-catch is not enough
		try {
			 ris = ecNgsi.updateEntity(request, payload);
			 ecSparql.updateEntity(request, payload);
		} catch (ResponseException exception) {
			throw exception;
		} catch (DateTimeParseException exception) {
			throw exception;
		} catch (JsonParseException exception) {
			throw exception;
		} catch (Exception exception) {
			throw exception;
		}
		return ris;
	}

	@Override
	public ResponseEntity<byte[]> appendEntity(HttpServletRequest request, String payload, String options)
			throws ResponseException, MalformedURLException, UnsupportedEncodingException , Exception {
		ResponseEntity<byte[]> ris;
		//we need grant ACID i think this try-catch is not enough
		try {
			 ris = ecNgsi.appendEntity(request, payload,options);
			 ecSparql.appendEntity(request, payload,options);
		} catch (ResponseException exception) {
			throw exception;
		} catch (DateTimeParseException exception) {
			throw exception;
		} catch (JsonParseException exception) {
			throw exception;
		} catch (Exception exception) {
			throw exception;
		}
		return ris;
	}

	@Override
	public ResponseEntity<byte[]> partialUpdateEntity(HttpServletRequest request, String payload) 
			throws ResponseException, MalformedURLException, UnsupportedEncodingException , Exception {
		ResponseEntity<byte[]> ris;
		//we need grant ACID i think this try-catch is not enough
		try {
			 ris = ecNgsi.partialUpdateEntity(request, payload);
			 ecSparql.partialUpdateEntity(request, payload);
		} catch (ResponseException exception) {
			throw exception;
		} catch (DateTimeParseException exception) {
			throw exception;
		} catch (JsonParseException exception) {
			throw exception;
		} catch (Exception exception) {
			throw exception;
		}
		return ris;
	}

	@Override
	public ResponseEntity<byte[]> deleteAttribute(HttpServletRequest request, String datasetId, String deleteAll)
			throws ResponseException, MalformedURLException, UnsupportedEncodingException , Exception {
		ResponseEntity<byte[]> ris;
		//we need grant ACID i think this try-catch is not enough
		try {
			 ris = ecNgsi.deleteAttribute(request, datasetId,deleteAll);
			 ecSparql.deleteAttribute(request,  datasetId,deleteAll);
		} catch (ResponseException exception) {
			throw exception;
		} catch (DateTimeParseException exception) {
			throw exception;
		} catch (JsonParseException exception) {
			throw exception;
		} catch (Exception exception) {
			throw exception;
		}
		return ris;
	}

	@Override
	public ResponseEntity<byte[]> deleteEntity(HttpServletRequest request)
			throws ResponseException, MalformedURLException, UnsupportedEncodingException , Exception {
		ResponseEntity<byte[]> ris;
		//we need grant ACID i think this try-catch is not enough
		try {
			 ris = ecNgsi.deleteEntity(request);
			 ecSparql.deleteEntity(request);
		} catch (ResponseException exception) {
			throw exception;
		} catch (DateTimeParseException exception) {
			throw exception;
		} catch (JsonParseException exception) {
			throw exception;
		} catch (Exception exception) {
			throw exception;
		}
		return ris;
	}

}

package eu.neclab.ngsildbroker.entityhandler.controller;


import java.time.format.DateTimeParseException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonParseException;

import eu.neclab.ngsildbroker.commons.datatypes.RestResponse;
import eu.neclab.ngsildbroker.commons.enums.ErrorType;
import eu.neclab.ngsildbroker.commons.exceptions.ResponseException;
import eu.neclab.ngsildbroker.commons.ldcontext.ContextResolverBasic;
import eu.neclab.ngsildbroker.commons.tools.HttpUtils;
import eu.neclab.ngsildbroker.entityhandler.controller.dasibreaker.EntityHandlerFactory;
import eu.neclab.ngsildbroker.entityhandler.controller.dasibreaker.IEntityHandler;


import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDateTime;
import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.RdfNQuad;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.neclab.ngsildbroker.commons.constants.AppConstants;
import eu.neclab.ngsildbroker.commons.datatypes.AppendResult;
import eu.neclab.ngsildbroker.commons.datatypes.UpdateResult;
import eu.neclab.ngsildbroker.commons.ngsiqueries.ParamsResolver;
import eu.neclab.ngsildbroker.entityhandler.config.EntityProducerChannel;
import eu.neclab.ngsildbroker.entityhandler.services.EntityService;
import eu.neclab.ngsildbroker.entityhandler.validationutil.Validator;
/**
 * 
 * @version 1.0
 * @date 10-Jul-2018
 */
@RestController
@RequestMapping("/ngsi-ld/v1/entities")
public class EntityController  implements IEntityHandler{

	private final static Logger logger = LoggerFactory.getLogger(EntityController.class);

	private IEntityHandler realEntityController;
	
	

	@Autowired
	EntityService entityService;
	@Autowired
	ObjectMapper objectMapper;

//	@Autowired
//	@Qualifier("emops")
//	KafkaOps kafkaOps;

	@Autowired
	@Qualifier("emconRes")
	ContextResolverBasic contextResolver;

	@Autowired
	@Qualifier("emparamsres")
	ParamsResolver paramsResolver;

	@SuppressWarnings("unused")
	// TODO check to remove ... never used
	private EntityProducerChannel producerChannel;

	@Autowired
	public EntityController(EntityProducerChannel producerChannel) {
		this.producerChannel = producerChannel;
	}

	private HttpUtils httpUtils;
	
	
	
	
	@PostConstruct
	private void setup() {
		this.httpUtils = HttpUtils.getInstance(contextResolver);
		realEntityController= EntityHandlerFactory.get(entityService,objectMapper,paramsResolver,httpUtils);
	}
	

	public EntityController() {
	}

	/**
	 * Method(POST) for "/ngsi-ld/v1/entities/" rest endpoint.
	 * 
	 * @param payload jsonld message
	 * @return ResponseEntity object
	 */
	@PostMapping
	public ResponseEntity<byte[]> createEntity(HttpServletRequest request,
			@RequestBody(required = false) String payload) {
		try {
			return realEntityController.createEntity(request, payload);
		} catch (ResponseException exception) {
			logger.error("Exception :: ", exception);
			exception.printStackTrace();
			return ResponseEntity.status(exception.getHttpStatus()).body(new RestResponse(exception).toJsonBytes());
		} catch (DateTimeParseException exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new RestResponse(ErrorType.BadRequestData, "Failed to parse provided datetime field.")
							.toJsonBytes());
		} catch (JsonParseException exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new RestResponse(ErrorType.BadRequestData, "There is an error in the provided json document")
							.toJsonBytes());
		} catch (Exception exception) {
			logger.error("Exception :: ", exception);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new RestResponse(ErrorType.InternalError, exception.getLocalizedMessage()).toJsonBytes());
		}
	}

	/**
	 * Method(PATCH) for "/ngsi-ld/v1/entities/{entityId}/attrs" rest endpoint.
	 * 
	 * @param entityId
	 * @param payload  json ld message
	 * @return ResponseEntity object
	 */
	@PatchMapping("/**/attrs")
	public ResponseEntity<byte[]> updateEntity(HttpServletRequest request, @RequestBody String payload) {
		try {
			return realEntityController.updateEntity(request, payload);
		} catch (ResponseException responseException) {
			logger.error("Exception :: ", responseException);
			return ResponseEntity.status(responseException.getHttpStatus())
					.body(new RestResponse(responseException).toJsonBytes());
		} catch (DateTimeParseException exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new RestResponse(ErrorType.BadRequestData, "Failed to parse provided datetime field.")
							.toJsonBytes());
		} catch (JsonParseException exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new RestResponse(ErrorType.BadRequestData, "There is an error in the provided json document")
							.toJsonBytes());
		} catch (Exception e) {
			logger.error("Exception :: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new RestResponse(ErrorType.InternalError, e.getLocalizedMessage()).toJsonBytes());
		}
	}

	/**
	 * Method(POST) for "/ngsi-ld/v1/entities/{entityId}/attrs" rest endpoint.
	 * 
	 * @param entityId
	 * @param payload  jsonld message
	 * @return ResponseEntity object
	 */
	@PostMapping("/**/attrs")
	public ResponseEntity<byte[]> appendEntity(HttpServletRequest request, @RequestBody String payload,
			@RequestParam(required = false, name = "options") String options) {
		try {
			return realEntityController.appendEntity(request, payload, options);
		} catch (ResponseException responseException) {
			logger.error("Exception :: ", responseException);
			return ResponseEntity.status(responseException.getHttpStatus())
					.body(new RestResponse(responseException).toJsonBytes());
		} catch (DateTimeParseException exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new RestResponse(ErrorType.BadRequestData, "Failed to parse provided datetime field.")
							.toJsonBytes());
		} catch (JsonParseException exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new RestResponse(ErrorType.BadRequestData, "There is an error in the provided json document")
							.toJsonBytes());
		} catch (Exception exception) {
			logger.error("Exception :: ", exception);
			exception.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new RestResponse(ErrorType.InternalError, exception.getLocalizedMessage()).toJsonBytes());
		}
	}

	/**
	 * Method(PATCH) for "/ngsi-ld/v1/entities/{entityId}/attrs/{attrId}" rest
	 * endpoint.
	 * 
	 * @param entityId
	 * @param attrId
	 * @param payload
	 * @return
	 */
	@PatchMapping("/**/attrs/**")
	public ResponseEntity<byte[]> partialUpdateEntity(HttpServletRequest request, @RequestBody String payload) {
		try {
			return realEntityController.partialUpdateEntity(request, payload);
		} catch (ResponseException responseException) {
			logger.error("Exception :: ", responseException);
			return ResponseEntity.status(responseException.getHttpStatus())
					.body(new RestResponse(responseException).toJsonBytes());
		} catch (DateTimeParseException exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new RestResponse(ErrorType.BadRequestData, "Failed to parse provided datetime field.")
							.toJsonBytes());
		} catch (JsonParseException exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new RestResponse(ErrorType.BadRequestData, "There is an error in the provided json document")
							.toJsonBytes());
		} catch (Exception exception) {
			logger.error("Exception :: ", exception);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new RestResponse(ErrorType.InternalError, exception.getLocalizedMessage()).toJsonBytes());
		}
	}

	/**
	 * Method(DELETE) for "/ngsi-ld/v1/entities/{entityId}/attrs/{attrId}" rest
	 * endpoint.
	 * 
	 * @param entityId
	 * @param attrId
	 * @return
	 */
	@DeleteMapping("/**")
	public ResponseEntity<byte[]> deleteAttribute(HttpServletRequest request,
			@RequestParam(value = "datasetId", required = false) String datasetId,
			@RequestParam(value = "deleteAll", required = false) String deleteAll) {
		try {
			return realEntityController.deleteAttribute(request, datasetId, deleteAll);
		} catch (ResponseException responseException) {
			logger.error("Exception :: ", responseException);
			return ResponseEntity.status(responseException.getHttpStatus())
					.body(new RestResponse(responseException).toJsonBytes());
		} catch (DateTimeParseException exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new RestResponse(ErrorType.BadRequestData, "Failed to parse provided datetime field.")
							.toJsonBytes());
		} catch (JsonParseException exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new RestResponse(ErrorType.BadRequestData, "There is an error in the provided json document")
							.toJsonBytes());
		} catch (Exception exception) {
			logger.error("Exception :: ", exception);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new RestResponse(ErrorType.InternalError, exception.getLocalizedMessage()).toJsonBytes());
		}
	}

	/**
	 * Method(DELETE) for "/ngsi-ld/v1/entities/{entityId}" rest endpoint.
	 * 
	 * @param entityId
	 * @return
	 */
	public ResponseEntity<byte[]> deleteEntity(HttpServletRequest request) {
		try {
			return realEntityController.deleteEntity(request);
		} catch (ResponseException responseException) {
			logger.error("Exception :: ", responseException);
			return ResponseEntity.status(responseException.getHttpStatus())
					.body(new RestResponse(responseException).toJsonBytes());
		} catch (DateTimeParseException exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new RestResponse(ErrorType.BadRequestData, "Failed to parse provided datetime field.")
							.toJsonBytes());
		} catch (JsonParseException exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new RestResponse(ErrorType.BadRequestData, "There is an error in the provided json document")
							.toJsonBytes());
		} catch (Exception exception) {
			logger.error("Exception :: ", exception);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new RestResponse(ErrorType.InternalError, exception.getLocalizedMessage()).toJsonBytes());
		}
	}
}

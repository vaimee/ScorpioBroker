package eu.neclab.ngsildbroker.entityhandler.controller.dasibreaker;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.neclab.ngsildbroker.commons.ngsiqueries.ParamsResolver;
import eu.neclab.ngsildbroker.commons.tools.HttpUtils;
import eu.neclab.ngsildbroker.entityhandler.services.EntityService;

public class EntityHandlerFactory {

	private final static EntityHandlerType entityHandlerType = EntityHandlerType.NGSI_SPARQL;
	public static IEntityHandler get(EntityService entityService, ObjectMapper objectMapper, ParamsResolver paramsResolver,
			HttpUtils httpUtils) {
		return get(entityHandlerType,entityService,objectMapper,paramsResolver,httpUtils);
		
	}
	
	public static IEntityHandler get(EntityHandlerType type,
			EntityService entityService, ObjectMapper objectMapper, ParamsResolver paramsResolver,
			HttpUtils httpUtils) {
		if(type==EntityHandlerType.SPARQL) {
			return new EntityControllerSPARQL(httpUtils);
		}else if(type==EntityHandlerType.NGSI_SPARQL) {
			return new EntityControllerNGSIandSPARQL(entityService,objectMapper,paramsResolver,httpUtils);
		}else {//default NGSI
			return new EntityControllerNGSI(entityService,objectMapper,paramsResolver,httpUtils);
		}
		
	}
}

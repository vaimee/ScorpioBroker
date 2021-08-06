package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import com.google.gson.*;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTerm;

public class ConverterJRDF implements IConverterJRDF {

	private int _blankNodeIndex;
	public ConverterJRDF() {
		_blankNodeIndex=-1;
	}
	private int getNextBN() {
		_blankNodeIndex++;
		return _blankNodeIndex;
	}
	public String JSONtoRDF(String json) throws Exception {
		System.out.println("\n--------------------JSON converter:\n"+json);
		String rdf = "";
		JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
		Set<String> keys = jsonObject.keySet();
		for (String key : keys) {
			rdf+=JSONtoRDF(key,"<"+SPARQLConstant.root+">",jsonObject.get(key)); 
		}
		return rdf;
	}
	private String JSONtoRDF(String entryKey,String subject,JsonElement element) throws Exception{
		String rdf = "";
		String resolvedKey;
		if(entryKey.trim().toLowerCase().compareTo("@type")==0 ||
				entryKey.trim().toLowerCase().compareTo("type")==0) {
			resolvedKey=" "+SPARQLConstant.rdfType+" ";
		}else if(entryKey.trim().toLowerCase().compareTo("@id")==0 ||
				entryKey.trim().toLowerCase().compareTo("id")==0) {
			resolvedKey=" "+SPARQLConstant.rdfId+" ";
		}else if(entryKey.trim().toLowerCase().compareTo("@context")==0) {
			resolvedKey=" "+SPARQLConstant.context+" ";
		}else if(entryKey.trim().toLowerCase().compareTo("@value")==0) {
			resolvedKey=" "+SPARQLConstant.rdfValue+" ";
		}else if(entryKey.startsWith(SPARQLConstant.arrayPos)) {
			resolvedKey=" "+entryKey+" ";
		}else if(entryKey.startsWith("https://")||entryKey.startsWith("http://")){
			resolvedKey =" <"+ entryKey+"> ";
		}else {
			resolvedKey =" <"+SPARQLConstant.NGSI_GRAPH_PREFIX + entryKey+"> ";
		}
		if (element instanceof JsonObject) {
			String bn_name1 = SPARQLConstant.balnkNode+getNextBN();
			String bn_name2 = SPARQLConstant.balnkNode+getNextBN();
			rdf+=subject+resolvedKey+bn_name1+".\n";
			rdf+=bn_name1+" <"+SPARQLConstant.isJsonObject+"> "+bn_name2+".\n";
			JsonObject jo=((JsonObject)element);
			Set<String> keys = jo.keySet();
			for (String key : keys) {
				rdf+=JSONtoRDF(key,bn_name2,jo.get(key));
			}
	    }else if(element instanceof JsonArray) {
			String bn_name1 = SPARQLConstant.balnkNode+getNextBN();
			String bn_name2 = SPARQLConstant.balnkNode+getNextBN();
			rdf+=subject+resolvedKey+bn_name1+".\n";
			rdf+=bn_name1+" <"+SPARQLConstant.isJsonArray+"> "+bn_name2+".\n";
			JsonArray jo=((JsonArray)element);
			for (int x=0;x<jo.size();x++) {
				rdf+=JSONtoRDF(SPARQLConstant.arrayPos+x,bn_name2,jo.get(x));
			}
	    }else if(element instanceof JsonPrimitive) {
			JsonPrimitive jo=((JsonPrimitive)element);
			if(jo.isNumber()) {
				String literal = jo.getAsString()+"^xsd:double";
				rdf+=subject+resolvedKey+"\""+literal+"\".\n";
			}else if(jo.isBoolean()) {
				String literal = jo.getAsString()+"^xsd:boolean";
				rdf+=subject+resolvedKey+"\""+literal+"\".\n";
			}else {
				String value = jo.getAsString();
				if(value.contains("\"")) {
					rdf+=subject+resolvedKey+"'"+jo.getAsString()+"'.\n";
				}else {
					rdf+=subject+resolvedKey+"\""+jo.getAsString()+"\".\n";
				}
				
			}
	    }else {
	    	throw new Exception("Unhandled json element: " + element.toString());
	    }
		return rdf;
	}
	public List<String> RDFtoJson(List<Bindings> binings) throws Exception {
		return RDFtoJson(binings,"s","p","o","e");
	}

	public List<String> RDFtoJson(List<Bindings> binings,String s,String p,String o, String e) throws Exception {
		HashMap<String,EntityContainer> ecmap =new HashMap<String,EntityContainer> ();
		for (Bindings bind : binings) {
			String entityGraph = bind.getRDFTerm(e).getValue();
			EntityContainer ec;
			if(ecmap.containsKey(entityGraph)) {
				ec= ecmap.get(entityGraph);
			}else {
				ec= new EntityContainer();
				ecmap.put(entityGraph,ec);
			}
			String subject = bind.getRDFTerm(s).getValue();
			ConvertingField field = new ConvertingField(
					bind.getRDFTerm(p).getValue(),
					bind.getRDFTerm(o)
				);
			if(subject.compareTo(SPARQLConstant.root)==0) {
				ec.getRootField().add(field);
			}else {
				if(ec.getMap().containsKey(subject)){
					ec.getMap().get(subject).add(field);
				}else {
					ArrayList<ConvertingField> fieldList = new ArrayList<ConvertingField>();
					fieldList.add(field);
					ec.getMap().put(subject, fieldList);
				}
			}
		
		}
		List<String> ris = new ArrayList<String>();
		for (String key : ecmap.keySet()) {
			EntityContainer ec=ecmap.get(key);
			JsonObject json  =new JsonObject();
			for (int x =0; x<ec.getRootField().size();x++) {
				resolve(ec.getRootField().get(x),ec.getMap(),json);
			}
			System.out.println("OUTPUT CONVERTER-->\n"+json.toString() );
			ris.add(json.toString());
		}
		return ris;
	}
	
	private void resolve(ConvertingField field,HashMap<String,ArrayList<ConvertingField>> map,JsonObject acc) throws Exception {
		if(field.isJsonObject()){
			ArrayList<ConvertingField> bNodes = map.get(field.getAsBlankNodeString());
			if(field.fromArrayElement()) {
				//if the field is from an array, no need to create
				//JsonObject parent to pass to the next resolve invocation
				//or we will have an additional "JsonObjectKey" key on our json
				for (int x =0; x<bNodes.size();x++) {
					resolve(bNodes.get(x),map,acc);
				}
			}else {
				JsonObject element = new JsonObject();
				for (int x =0; x<bNodes.size();x++) {
					resolve(bNodes.get(x),map,element);
				}
				acc.add(field.resolveKey(), element);
			}
		}else if(field.isJsonArray()){
			ArrayList<ConvertingField> bNodes = map.get(field.getAsBlankNodeString());
			JsonArray element = new JsonArray();
			for (int x =0; x<bNodes.size();x++) {
				resolve(bNodes.get(x),map,element);
			}
			acc.add(field.resolveKey(), element);
		}else {
			JsonElement element;
			if(field.isBlankNode()) {
				List<ConvertingField> bn =map.get(field.getAsBlankNodeString());
				if(bn.size()!=1) {
					throw new Exception("RDF not well formed for be a JSON representation: more than one blanknode for JsonArray or JsonObject");
				}else {
					if(!bn.get(0).isBlankNode()) {
						throw new Exception("RDF not well formed for be a JSON representation: the blanknode as object need to refer to another blanknode that is JsonArray or JsonObject");
					}
					ConvertingField nextCF = bn.get(0);
					nextCF.setKey(field.resolveKey());
					resolve(nextCF,map,acc);
				}
			}else if(field.valueIsBoolean()) {
				element= new JsonPrimitive(field.getValueAsBoolean());
				acc.add(field.resolveKey(), element);
			}else if(field.valueIsNumber()) {
				element= new JsonPrimitive(field.getValueAsNumber());
				acc.add(field.resolveKey(), element);
			}else {
				element= new JsonPrimitive(field.getValueAsString());
				acc.add(field.resolveKey(), element);
			}
		}
	}
	private void resolve(ConvertingField field,HashMap<String,ArrayList<ConvertingField>> map,JsonArray acc) throws Exception {
		if(field.isJsonArrayElement()) {
			//we can force to have the right order on the array -->getArrayPos
			if(field.isBlankNode()) {
				List<ConvertingField> bn =map.get(field.getAsBlankNodeString());
				if(bn.size()!=1) {
					throw new Exception("RDF not well formed for be a JSON representation: more than one blanknode for JsonArray or JsonObject");
				}else {
					ConvertingField bncf =bn.get(0);//un element array can refer at just one blanknode
					if(!bncf.isBlankNode()) {
						throw new Exception("RDF not well formed for be a JSON representation: the blanknode as object need to refer to another blanknode that is JsonArray or JsonObject");
					}
					if(bncf.isJsonArray()) {
						ArrayList<ConvertingField> bNodes = map.get(field.getAsBlankNodeString());
						JsonArray element = new JsonArray();
						for (int x =0; x<bNodes.size();x++) {
							ConvertingField cf =bNodes.get(x);
							cf.setFromArrayElement(true);
							resolve(cf,map,element);
						}
						acc.add(element);
					}else if(bncf.isJsonObject()) {
						ArrayList<ConvertingField> bNodes = map.get(field.getAsBlankNodeString());
						JsonObject element = new JsonObject();
						for (int x =0; x<bNodes.size();x++) {
							ConvertingField cf =bNodes.get(x);
							cf.setFromArrayElement(true);
							resolve(cf,map,element);
						}
						acc.add(element);
					}else {
						throw new Exception("RDF not well formed for be a JSON representation: exspected blanknode found: "+bncf.toString() );
					}
				}
				
			}else if(field.valueIsBoolean()) {
				JsonElement element= new JsonPrimitive(field.getValueAsBoolean());
				acc.add( element);
			}else if(field.valueIsNumber()) {
				JsonElement element= new JsonPrimitive(field.getValueAsNumber());
				acc.add(element);
			}else {
				JsonElement element= new JsonPrimitive(field.getValueAsString());
				acc.add( element);
			}
		}else {
			throw new Exception("RDF not well formed for be a JSON representation: JsonArray need to be composed onloy by JsonArray element");
		}
	}
	private class ConvertingField{
		private RDFTerm  _object;
		private String _predicate;
		private String _key;
		private boolean _fromArrayElement=false;
		public ConvertingField(String predicate, RDFTerm object) {
			this._object = object;
			if(predicate.startsWith(SPARQLConstant.RDF_PREFIX_START)) {
				this._predicate = SPARQLConstant.RDF_PREFIX_SUB+predicate.substring(SPARQLConstant.RDF_PREFIX_START.length());
			}else {
				this._predicate = predicate;
			}
		}
		public boolean isType() {
			return  _predicate.compareTo(SPARQLConstant.rdfType)==0 ;
//			||_predicate.compareTo(SPARQLConstant.rdfTypeWithPrefix)==0;
		}
		public boolean isContext() {
			return _predicate.compareTo(SPARQLConstant.context)==0 ;
//					||  _predicate.compareTo(SPARQLConstant.contextWithPrefix)==0 ;
		}
		public boolean isValue() {
			return _predicate.compareTo(SPARQLConstant.rdfValue)==0 ;
//					|| _predicate.compareTo(SPARQLConstant.rdfValuetWithPrefix)==0 ;
			
		}
		public boolean isJsonArrayElement() {
			return _predicate.startsWith(SPARQLConstant.arrayPos);
//			|| _predicate.startsWith(SPARQLConstant.arrayPosWithPrefix);
		}
//		public int getArrayPos() { //not used yet
//			return Integer.parseInt(_predicate.substring(SPARQLConstant.arrayPos.length()));
//			//arrayPosWithPrefix
//		}
		public boolean isId() {
			return _predicate.compareTo(SPARQLConstant.rdfId)==0 ;
//			|| _predicate.compareTo(SPARQLConstant.rdfIdWithPrefix)==0;
		}
		public boolean isJsonObject() {
			return _predicate.compareTo(SPARQLConstant.isJsonObject)==0;
		}
		public boolean isJsonArray() {
			return _predicate.compareTo(SPARQLConstant.isJsonArray)==0;
		}
		public String getValueAsString() {
			return _object.getValue(); //"\""+_object.getValue()+"\"";
		}
		public boolean valueIsBoolean() {
			return _object.getValue().endsWith("^xsd:boolean");
		}
		public boolean valueIsNumber() {
			return _object.getValue().endsWith("^xsd:double");
		}
		public double getValueAsNumber() {
			String o =_object.getValue();
			return Double.parseDouble(o.substring(0, o.length()-11));
		}
		public boolean getValueAsBoolean() {
			String o =_object.getValue();
			return(o.substring(0, o.length()-12).toLowerCase().compareTo("true")==0);
		}
		public boolean isBlankNode() {
			return _object.isBNode();
		}
		public String getAsBlankNodeString() {
			return _object.getValue();
		}
		public String resolveKey() {
			String toResolve;
			if(_key==null) {
				toResolve = _predicate;
			}else {
				toResolve= _key;
			}
			if(this.isId()){
				return "@id"; //"@id";
			}else if(this.isType()) {
				return "@type";//"@type";
			}else if(this.isContext()) {
				return "@context";
			}else if(this.isValue()) {
				return "@value";
			}else if(toResolve.startsWith(SPARQLConstant.NGSI_GRAPH_PREFIX)) {
				return toResolve.substring(SPARQLConstant.NGSI_GRAPH_PREFIX.length());
			}else {
				return toResolve;
			}
		}
		public void setKey(String key) {
			_key=key;
		}
		@Override
		public String toString() {
			return _predicate + " - " + _object;
		}
		public boolean fromArrayElement() {
			return _fromArrayElement;
		}
		public void setFromArrayElement(boolean fromArrayElement) {
			this._fromArrayElement = fromArrayElement;
		}
		
	}
	private class EntityContainer{
		private ArrayList<ConvertingField> rootField = new ArrayList<ConvertingField>();
		private HashMap<String,ArrayList<ConvertingField>> map = new HashMap<String,ArrayList<ConvertingField>>();
		public ArrayList<ConvertingField> getRootField() {
			return rootField;
		}
		public HashMap<String, ArrayList<ConvertingField>> getMap() {
			return map;
		}
		
	}
}

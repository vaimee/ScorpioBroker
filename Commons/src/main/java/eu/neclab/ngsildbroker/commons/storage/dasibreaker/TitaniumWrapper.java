package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.JsonLdOptions;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.document.RdfDocument;
import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.RdfLiteral;
import com.apicatalog.rdf.RdfNQuad;

import eu.neclab.ngsildbroker.commons.datatypes.QueryParams;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import it.unibo.arces.wot.sepa.commons.sparql.BindingsResults;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTerm;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermLiteral;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

public class TitaniumWrapper implements IConverterJRDF {
	private static HashMap<String,String> contextMap = new HashMap<String,String> ();
	private String _contextFrame=null;
	private String _typeFrame = null;
	public TitaniumWrapper() {
	}
	
	public String JSONtoRDF(String json) throws Exception {
//		System.out.println("\n--------------------JSON converter:\n"+json);
		Reader targetReader = new StringReader(json);
		Document document = JsonDocument.of(targetReader);
		RdfDataset rdfdataset = JsonLd.toRdf(document).get();
		return rdfDatasetToTripleString(rdfdataset);
	}
	
	public List<String> RDFtoJson(List<Bindings> binings) throws Exception {
		return RDFtoJson(binings,null,"s","p","o","e","type");
	}
	@Override
	public List<String> RDFtoJson(List<Bindings> binings, String filterBy) throws Exception {
		return RDFtoJson(binings,filterBy,"s","p","o","e","type");
	}
	public List<String> RDFtoJson(List<Bindings> binings,String s,String p,String o, String e,String t) throws Exception {
		return RDFtoJson(binings,null,s,p,o,e,t);
	}
	


	public List<String> RDFtoJson(List<Bindings> binings,String filterBy,String s,String p,String o, String e,String t) throws Exception {
		List<String> ris = new ArrayList<String>();
		HashMap<String,List<String>> nquads = new HashMap<String,List<String>>();
		HashMap<String,String> piggyTipes = new HashMap<String,String>();
		HashMap<String,String> bnodes = new HashMap<String,String>();
		int bnodesIndex = 0;
		long startTime1 = System.nanoTime();
		for (Bindings bind : binings) {
			String entityGraph = bind.getRDFTerm(e).getValue();
			String subject = bind.getRDFTerm(s).getValue();
			String predicate = bind.getRDFTerm(p).getValue();
			String object = bind.getRDFTerm(o).getValue();
			if(bind.getVariables().contains(t)) {
				piggyTipes.put(entityGraph, bind.getRDFTerm(t).getValue());
			}
			if(bind.getRDFTerm(s).isURI()) {
				subject="<"+subject+">";
			}else if(bind.getRDFTerm(s).isBNode()) {
				if(bnodes.containsKey(subject)) {
					subject=bnodes.get(subject);
				}else {
					//blazegraph use NOT standard bnodes name (EXAMPLE: t123)
					String standardBnode = "_:b"+bnodesIndex;
					bnodesIndex++;
					bnodes.put(subject,standardBnode);
					subject=standardBnode;
				}
			}
			
			if(bind.getRDFTerm(p).isURI()) {
				predicate="<"+predicate+">";
			}
			if(bind.getRDFTerm(o).isURI()) {
				object="<"+object+">";
			}else if(bind.getRDFTerm(o).isLiteral()) {
//				object="\""+sanitizeLiteral(object)+"\""; 
//				object="'"+object+"'";
				//Titanium didn't allow single quotes as literal delimiter
				//and didn't work if the literal contains double quotes
//				if(object.contains("\"")) {
//					object="\""+object.replace("\"", "'")+"\"";
//				}else {
//					object="\""+object+"\"";
//				}

				RDFTermLiteral literal = (RDFTermLiteral)bind.getRDFTerm(o);
				/*
				the following format, is the N-Quads standard
				
				//				if(literal.getDatatype()!=null) {
				//					object+="^^<"+literal.getDatatype()+">";
				//				}
				 * 
				but with that format, Titanium will convert RDF to json as this:
				
						  "value": Object {
					           "@value": "12",
					           "type": "http://www.w3.org/2001/XMLSchema#integer",
				          }
				          
				and we expect just {"value" :12}
				
				that format is valid only for ngsi data-type NOT for XSD
				*/
				
				
//				if(containsDoubleQuotesEncoding(object)) {
//					object=decodeDoubleQuotes(object);
//				}else {
					String dataType= literal.getDatatype();
					object="\""+object+"\"";
					if(dataType!=null ){//&& dataType.startsWith(SPARQLConstant.NGSI_PREFIX_START)){
						object+="^^<"+dataType+">";
					}
//				}
				
				
			}else if(bind.getRDFTerm(o).isBNode()) {
				if(bnodes.containsKey(object)) {
					object=bnodes.get(object);
				}else {
					//blazegraph use NOT standard bnodes name (EXAMPLE: t123)
					String standardBnode = "_:b"+bnodesIndex;
					bnodesIndex++;
					bnodes.put(object,standardBnode);
					object=standardBnode;
				}
			}
			String triple = subject+" "+ predicate+ " " + object; //triple
//			String triple = subject+" "+ predicate+ " " + object + "<g>"; //n-quads
			if(nquads.containsKey(entityGraph)) {
				nquads.get(entityGraph).add(triple);
			}else {
				List<String> triples = new ArrayList<String>();
				triples.add(triple);
				nquads.put(entityGraph,triples);
			}
		}
		System.out.println("RDFtoJson triple builder: "  +(System.nanoTime() - startTime1)/1000000 +"ms");
		startTime1 = System.nanoTime();
		for (String key : nquads.keySet()) {
			String rdf_triples= "";
			for(String triple :nquads.get(key)) {
				rdf_triples+=triple+".\n";
			}	

			long startTime = System.nanoTime();
			String piggyType = null;
			if(piggyTipes.containsKey(key)) {
				piggyTipes.get(key);
			}
			String jsonStr = nQuadsToJson(rdf_triples,piggyType,filterBy,true);
			System.out.println("nQuadsToJson: "  +(System.nanoTime() - startTime)/1000000 +"ms");
			
			if(containsDoubleQuotesEncoding(jsonStr)) {
				ris.add(decodeDoubleQuotes(jsonStr));
			}else {
				ris.add(jsonStr);
			}
		}
		System.out.println("RDFtoJson nQuadsToJson for["+nquads.keySet().size()+"]: "  +(System.nanoTime() - startTime1)/1000000 +"ms");
		return ris;
	}
	
	/*
	 * nQuads				-->	RDF quads of a query sparql 
	 * 							(with the appropriate structure to convert them into json-ld)
	 * filterBy				-->	is the Attrs filter of json-ld fields,
	 * 							we will extract only that specific fields
	 * 							(id and type are special field and will be keep)
	 * forceToJsonObject	-->	In same case we need just one Entity and so in the upper level
	 * 							we need a JsonObject, not a JsonArray
	 * 							(i think that is always necessary as "true", there is no case that needs "forceToJsonObject=false")
	 */
	public String nQuadsToJson(String nQuads,String type,String filterBy,boolean forceToJsonObject) throws JsonLdError {
		Reader targetReader = new StringReader(nQuads);
		//read N-Quads or turtle
		RdfDocument doc=(RdfDocument) RdfDocument.of(targetReader);

		JsonLdOptions options = new JsonLdOptions();
		options.setUseNativeTypes(true);
		String frameStr = this.generateFrame(type);
		if(frameStr!=null) {
			//covert to json-ld
			Document notFramed = JsonDocument.of(JsonLd.fromRdf(doc).options(options).get());
			//if there is the frame, we will frame the jsonld
			Reader targetReaderFrame = new StringReader(frameStr);
			Document frame = JsonDocument.of(targetReaderFrame);
			
			long startTime = System.nanoTime();
			JsonObject jo = JsonLd.frame(notFramed, frame).get();
			System.out.println("JsonLd.frame: "  +(System.nanoTime() - startTime)/1000000 +"ms");
			//here forceToJsonObject is useless,
			//the result is already an JsonObject
			if(filterBy==null) {
				return jo.toString();
			}else {
				return filter(jo,filterBy).toString();
			}
		}else {
			//convert to json-ld and didn't frame it
			JsonArray ja = JsonLd.fromRdf(doc).options(options).get();
			if(filterBy==null) {
				if(forceToJsonObject) {
					if(ja.size()>0) {
						return ja.get(0).toString();
					}else {
						return "";
					}
				}else {
					return ja.toString();
				}
			}else {
				JsonArrayBuilder ris =  Json.createArrayBuilder();
				for (int x = 0;x<ja.size();x++) {
					if(forceToJsonObject) {
						return filter(ja.getJsonObject(x),filterBy).toString();
					}else {
						ris.add(filter(ja.getJsonObject(x),filterBy));
					}
				}
				if(forceToJsonObject) {
					//if we are here, it means that
					//we not enter in the upper "for" because ja.size()<1
					//there is not any entity
					return "";
				}
				return ris.toString();
			}
		}
	}

//	public JsonObject frame() {
//		return JsonLd.frame(notFramed, frame).get();
//	}
	
	protected JsonObject filter(JsonObject obj,String filterBy) {
		JsonObjectBuilder ris =  Json.createObjectBuilder();
		boolean idDone = false;
		boolean typeDone = false;
		if(filterBy.indexOf(",")>-1) {
			for (String  attr : filterBy.split(",")) {
				if(attr.compareTo("@id")==0) {
					idDone=true;
				}else if (attr.compareTo("@type")==0){
					typeDone=true;
				}
				JsonValue jv = obj.get(attr);
				if(jv.getValueType()==ValueType.OBJECT ) {
					ris.add(attr,jv.asJsonObject());
				}else if (jv.getValueType()==ValueType.ARRAY) {
					ris.add(attr,jv.asJsonArray());
				}else {
					ris.add(attr,jv);
				}
			}
		}else {
			if(filterBy.compareTo("@id")==0) {
				idDone=true;
			}else if (filterBy.compareTo("@type")==0){
				typeDone=true;
			}
			JsonValue jv = obj.get(filterBy);
			if(jv.getValueType()==ValueType.OBJECT ) {
				ris.add(filterBy,jv.asJsonObject());
			}else if (jv.getValueType()==ValueType.ARRAY) {
				ris.add(filterBy,jv.asJsonArray());
			}else {
				ris.add(filterBy,jv);
			}
		}
		if(!idDone) {
			ris.add("@id",obj.get("@id"));
		}
		if(!typeDone) {
			ris.add("@type",obj.get("@type"));
		}
		return ris.build();
	}
	
	public List<String> compact(List<String> jsonLDs,String contexts) throws JsonLdError{
		List<String> ris = new ArrayList<String>();
		Reader targetReaderContext = new StringReader(contexts);
		Document context = JsonDocument.of(targetReaderContext);
		for (String jsonld : jsonLDs) {//for each json-ld INSTANCE
			Reader targetReader = new StringReader(jsonld);
			Document jsonldDoc = JsonDocument.of(targetReader);
			ris.add(JsonLd.compact(jsonldDoc,context).compactToRelative(false).get().toString());
			
		}
		return ris;
	}
	
	protected String rdfDatasetToTripleString(RdfDataset rdf) {
		String turtle = "";
		for ( RdfNQuad iterable_element : rdf.toList()) {
			String s = iterable_element.getSubject().getValue();
			if(!iterable_element.getSubject().isBlankNode()) {
				s= "<"+s +">";
			}
			String p= "<"+iterable_element.getPredicate().getValue()+">";
			String o =  iterable_element.getObject().getValue();
			if(iterable_element.getObject().isLiteral()) {
				if(o.contains("\"")){
					o = "\""+encodeDoubleQuotes(o)+"\""; 
					//no data-type, so string as default
				}else {
					o = "\""+o+"\""; 
					//For literals with XSD  and ngsi data-type, 
					//the type will be preserved
					 String dataType = ((RdfLiteral)iterable_element.getObject()).getDatatype();
					 o+="^^<"+dataType+">";
				}
				
			
			}else if(!iterable_element.getObject().isBlankNode()) {
				o = "<"+o+">";
			}
			turtle+=s+" "+ p+ " " + o +".\n";
		}
		return turtle;
	}

	
	public void setFrame(String type, String context) {
		if(type!=null && type.trim().length()>0) {
			
			//------------------TYPE
			if(!type.contains(",")) {
				if(type.contains("#")) {
					this._typeFrame="\""+type.split("#")[1]+"\"";
				}else {
					this._typeFrame="\""+type+"\"";
				}
			}else {
				this._typeFrame=null;
			}
			//------------------CONTEXT
			if(context!=null && context.trim().length()>0) {
				String sanitizzeURL = context.trim();
				if(sanitizzeURL.startsWith("\"")) {
					sanitizzeURL=sanitizzeURL.substring(1,sanitizzeURL.length()-1);
				}
				String downloadedContext="";
				if(contextMap.containsKey(sanitizzeURL)) {
					downloadedContext=contextMap.get(sanitizzeURL);
				}else {
					//----------------------------------------------RESOLVING URI
				    try {
				    	HttpURLConnection connection = (HttpURLConnection) new URL(sanitizzeURL).openConnection();
				    	InputStream inputStream = connection.getInputStream();
				        downloadedContext = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
				        	        .lines()
				        	        .collect(Collectors.joining("\n")).trim();
				        inputStream.close();
				        if(downloadedContext.startsWith("{")) {
							downloadedContext=downloadedContext.substring(1,downloadedContext.length()-2);
						}
						contextMap.put(sanitizzeURL, downloadedContext);
			             
			        }
			        catch (MalformedURLException e) {
			            System.out.println("Malformed URL: " + e.getMessage());
			        }
			        catch (IOException e) {
			            System.out.println("I/O Error: " + e.getMessage());
			        }
				}

				
				if(downloadedContext.length()==0) {
					//if we can't resolve for some reason the context here
					//we forward the URI resolve to Titanium 
					//(warning: Titanium will not cache the context)
					this._contextFrame= "\"@context\":" +context;
					//maybe is ok to use sanitizzeURL too?
					
				}else {
					this._contextFrame= downloadedContext;
				}
				
			}else {
				this._contextFrame=null;
			}
		}else {
			this._typeFrame=null;
		}
		

		
	}
	
	protected String generateFrame() {
		return generateFrame(null);
	}
	protected String generateFrame(String useThisType) {
		if( this._typeFrame==null && useThisType==null) {
			return null;
		}else {
			String typeToUse = this._typeFrame;
			if(useThisType!=null) {
				typeToUse = useThisType;
			}
			if(this._contextFrame==null ) {
				//try to frame only with the type is not a good thing
				return "{\"@type\":"
						+typeToUse
					+"}";
			}else {
				return "{ " 
							+this._contextFrame 
						+",\n\"@type\":"
							+typeToUse
						+"}";
			}
			
		} 
	}
	
	
	/*
	 * Resolving Titanium double quotes problem
	 */
	protected String encodingFlag = "DoubleQuotesEncoding_";
	protected String encodeDoubleQuotes(String str) {
//		return str.replace("\"", "\\\"");
		byte[] bytesEncoded = Base64.encodeBase64(str.getBytes());
		return encodingFlag+ new String(bytesEncoded);
	}
	protected String decodeDoubleQuotes(String str) {
		String temp  ="";
		String sub[] =str.split("\""+encodingFlag);
		temp+=sub[0];
		for(int x =1;x<sub.length;x++) {
			String sub2[] = sub[x].split("\"",2);
			byte[] valueDecoded = Base64.decodeBase64(sub2[0]);
			temp+="\""+new String(valueDecoded).replace("\"", "\\\"")+"\""+sub2[1];
		}
		return temp;
	}
	protected boolean containsDoubleQuotesEncoding(String str) {
		return str.contains(encodingFlag);
	}

	
	public List<String> getJsonLD(QueryParams qp,BindingsResults binds) throws Exception{
		//and setFrame need to be added to its interface
		if(qp instanceof QueryParamsWithContext) {
			String context = ((QueryParamsWithContext)qp).getContext();
			String type = qp.getType();
			if(type==null && binds.getBindings().size()>0) {
				RDFTerm t = binds.getBindings().get(0).getRDFTerm("type");
				if(t!=null) {
					type=t.getValue();
				}
			}
			this.setFrame(type, context);
		}
		List<String> list;
		if(qp.getAttrs()!=null &&  qp.getAttrs().length()>0) {
			// in that case (QUERY_PARAMETER_ATTRS)
			// we need filter jsons
			list=this.RDFtoJson(binds.getBindings(),qp.getAttrs());
		}else {
			list=this.RDFtoJson(binds.getBindings());
		}
		return list;
	}

	 
}

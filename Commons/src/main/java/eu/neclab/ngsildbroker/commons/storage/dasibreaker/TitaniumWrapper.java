package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.IOUtils;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.document.RdfDocument;
import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.RdfLiteral;
import com.apicatalog.rdf.RdfNQuad;

import eu.neclab.ngsildbroker.commons.constants.NGSIConstants;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermLiteral;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;

public class TitaniumWrapper implements IConverterJRDF {
	private static HashMap<String,Document> contextMap = new HashMap<String,Document> ();
	private String _frame=null;
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
		return RDFtoJson(binings,"s","p","o","e");
	}

	
	public List<String> RDFtoJson(List<Bindings> binings,String s,String p,String o, String e) throws Exception {
		List<String> ris = new ArrayList<String>();
		HashMap<String,List<String>> nquads = new HashMap<String,List<String>>();
		HashMap<String,String> bnodes = new HashMap<String,String>();
		int bnodesIndex = 0;
		for (Bindings bind : binings) {
			String entityGraph = bind.getRDFTerm(e).getValue();
			String subject = bind.getRDFTerm(s).getValue();
			String predicate = bind.getRDFTerm(p).getValue();
			String object = bind.getRDFTerm(o).getValue();
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
				if(object.contains("\"")) {
					object="\"null\""; //DOUBLE QUOTE NOT ALLOWED FOR NOW
				}else {
					String dataType= literal.getDatatype();
					object="\""+object+"\"";
					if(dataType!=null ){//&& dataType.startsWith(SPARQLConstant.NGSI_PREFIX_START)){
						object+="^^<"+dataType+">";
					}
				}
				
				
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
		for (String key : nquads.keySet()) {
			String rdf_triples= "";
			for(String triple :nquads.get(key)) {
				rdf_triples+=triple+".\n";
			}	
			ris.add(nQuadsToJson(rdf_triples));
		}
		return ris;
	}
	
	
	public String nQuadsToJson(String nQuads) throws JsonLdError {
		Reader targetReader = new StringReader(nQuads);
		//read N-Quads or turtle
		RdfDocument doc=(RdfDocument) RdfDocument.of(targetReader);
		if(_frame!=null) {
			//covert to json-ld
			Document notFramed = JsonDocument.of(JsonLd.fromRdf(doc).get());
			//if there is the frame, we will frame the jsonld
			Reader targetReaderFrame = new StringReader(_frame);
			Document frame = JsonDocument.of(targetReaderFrame);
			return JsonLd.frame(notFramed, frame).get().toString();
		}else {
			//convert to json-ld and didn't frame it
			return JsonLd.fromRdf(doc).get().toString();
		}
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

				o = "\""+o+"\""; //"'"+o+"'";
				
				//For literals with XSD  and ngsi data-type, 
				//the type will be preserved
				 String dataType = ((RdfLiteral)iterable_element.getObject()).getDatatype();
				 o+="^^<"+dataType+">";
//				 String clennedDataType="";
//				 if(dataType.startsWith(SPARQLConstant.XSD_PREFIX_START)) {
//					 clennedDataType = dataType.substring(SPARQLConstant.XSD_PREFIX_START.length());
//					 o+="^^"+SPARQLConstant.XSD_PREFIX_SUB+clennedDataType+ " ";
//				 }else if(dataType.startsWith(SPARQLConstant.NGSI_PREFIX_START)){
//					 clennedDataType = dataType.substring(SPARQLConstant.NGSI_PREFIX_START.length());
//					 o+="^^"+SPARQLConstant.NGSI_PREFIX_SUB+clennedDataType+ " ";
//				 }
				 
				 //DEPRECATEd
//				 else if(dataType.compareTo(NGSIConstants.NGSI_LD_DATE_TIME)==0) {
//					 clennedDataType = "xsd:dateTime";
//				 }else if(dataType.compareTo(NGSIConstants.NGSI_LD_DATE)==0) {
//					 clennedDataType = "xsd:date";
//				 }else if(dataType.compareTo(NGSIConstants.NGSI_LD_TIME)==0) {
//					 clennedDataType = "xsd:time";
//				 }
				
			}else if(!iterable_element.getObject().isBlankNode()) {
				o = "<"+o+">";
			}
			turtle+=s+" "+ p+ " " + o +".\n";
		}
		return turtle;
	}

	public void setFrame(String frame) {
		this._frame=frame;
	}
	public void setFrame(String type, String context) {
		if(type!=null 
				&& type.trim().length()>0 
				&& context!=null 
				&& context.trim().length()>0
		) {
			String cleannedType = type;
			if(type.contains("#")) {
				cleannedType=type.split("#")[1];
			}
			this._frame=
					"{ \"@context\":" 
							+context +",\n"
					+"\"@type\":\""+
							cleannedType
					+"\"}";
		}else {
			this._frame=null;
		}
		

		
	}
	
	 
}

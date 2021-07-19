package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.UUID;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.RdfDocument;
import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.RdfNQuad;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import it.unibo.arces.wot.sepa.commons.sparql.BindingsResults;
import jakarta.json.JsonArray;

public class ConverterJSONLDSPARQL {

	
	

	private HashMap<String,String> _blankNodeHasMap;

	public ConverterJSONLDSPARQL(){
		_blankNodeHasMap = new HashMap<String,String>();
	}
	
	//-------------------------------------------------------------------WIP
	private String tempJSONLDtoRDF(String jsonld,String key) {
		return "<http://ngsild/"+key+"><http://temp>'"+jsonld+"'";
	}
	public static String tempRDFtoJSONLD(BindingsResults rdf) throws SEPABindingsException {
		for (Bindings bind : rdf.getBindings()) {
			if(bind.getRDFTerm("p").getValue().compareTo("http://temp")==0) {
				return bind.getRDFTerm("o").getValue();
			}
		}
		return "";
	}
	//-------------------------------------------------------------------WIP
	
	protected String jsonldToTriple(String jsonld,String key) throws JsonLdError {
		return tempJSONLDtoRDF(jsonld,key);//------------------------WIP
//		Reader targetReader = new StringReader(jsonld);
//		Document document = JsonDocument.of(targetReader);
//		RdfDataset rdf = JsonLd.toRdf(document).get();
//		return rdfDatasetToTripleString(rdf,key);
	}
	protected String rdfDatasetToTripleString(RdfDataset rdf, String key) {
		String turtle = "";
		for ( RdfNQuad iterable_element : rdf.toList()) {
			String s = iterable_element.getSubject().getValue();
			if(iterable_element.getSubject().isBlankNode()) {
				s= "<"+resolveBlankNode(s,key) +">";
			}else {
				s = "<"+s+">";
			}
			String p= "<"+iterable_element.getPredicate().getValue()+">";
			String o =  iterable_element.getObject().getValue();
			if(iterable_element.getObject().isBlankNode()) {
				o= "<"+resolveBlankNode(o,key) +">";
			}else if(iterable_element.getObject().isLiteral()) {
				o = "'"+o+"'";
			}else {
				o = "<"+o+">";
			}
			
			//----------------------
//			if(p.compareTo("rdf:type")==0) {
//				p="<rdf:type>";
//			}
			turtle+=s+" "+ p+ " " + o +".\n";
		}
		return turtle;
	}
	protected String resolveBlankNode(String blankNode,String key) {
		String uniqueBlankNode;
		if(_blankNodeHasMap.containsKey(blankNode)){
			uniqueBlankNode = _blankNodeHasMap.get(blankNode);
		}else {
			uniqueBlankNode = genBlankNode(key);
			_blankNodeHasMap.put(blankNode,uniqueBlankNode);
		}
		return uniqueBlankNode;
	}
	
	
	protected JsonArray rdfToJsonLd(String rdf) throws JsonLdError {
		Reader targetReader = new StringReader(rdf);
		Document document = RdfDocument.of(targetReader);
		return JsonLd.fromRdf(document).get();
	}
	
	protected String genBlankNode(String key) {
//			return  "_:"+key+"_"+UUID.randomUUID().toString();
			return "http://blanknode/"+ key+"_"+UUID.randomUUID().toString();
	}
	

	public HashMap<String, String> getBlankNodeHasMap() {
		return _blankNodeHasMap;
	}

	private String bindingsResultsToJsonld(BindingsResults bindings) throws JsonLdError, SEPABindingsException {
		String triples = "";
		if(bindings.getBindings().size()==0) {
			return "";
		}
		for (Bindings binding : bindings.getBindings()) {
			triples+="<" +binding.getValue("s") + ">";
			triples+=" <" +binding.getValue("p") + ">";
			if(binding.isLiteral("o")) {
				triples+="'" +binding.getValue("o") + "'";
			}else {
				triples+=" <" +binding.getValue("o") + ">";
			}
			triples+=".\n";
		} 
		Reader targetReader = new StringReader(triples);
		
		RdfDocument document = (RdfDocument) RdfDocument.of(targetReader);
		return JsonLd.fromRdf(document).get().toString();
	}
	

}

package eu.neclab.ngsildbroker.commons.storage.dasibreaker;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.IOUtils;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.document.RdfDocument;
import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.RdfNQuad;

import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;

public class TitaniumWrapper implements IConverterJRDF {
	private static HashMap<String,Document> contextMap = new HashMap<String,Document> ();
	
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

	private String sanitizeLiteral(String literal) {
//		System.out.println("Sanitizze: "+literal.replaceAll("\"", "\\\""));
		return literal.replaceAll("\"", "\\\"");
//		char doubleQuote = 34;
//		String ris = "";
//		for(int x=0;x<literal.length();x++) {
//			if(literal.charAt(x)==doubleQuote) {
//				ris+="\"";
//			}else {
//				ris+=literal.charAt(x);
//			}
//		}
//		return ris;
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
				object="\""+object+"\"";
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
//			else {
//				System.out.println("WARNING ?o unknow type for: "+ object);
//			}
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
			System.out.println("\n-\n-\n-rdf_triples: \n"+rdf_triples);
			Reader targetReader = new StringReader(rdf_triples);
			//read N-Quads or turtle
			RdfDocument doc=(RdfDocument) RdfDocument.of(targetReader);
			//covert to json-ld
			Document notFramed = JsonDocument.of(JsonLd.fromRdf(doc).get());
			Reader targetReaderFrame = new StringReader(getFrame(notFramed));
			Document frame = JsonDocument.of(targetReaderFrame);
			ris.add(JsonLd.frame(notFramed, frame).get().toString());
//			ris.add(JsonLd.fromRdf(doc).get().toString());
		}
		return ris;
	}
	
	private String getFrame(Document notFramed) {
		String frame ="{\"@context\":[\""+
						notFramed.getContextUrl()
				+"\"],"
				+"\"type\":\""+
						notFramed.getContentType()
				+"\"}";
		return frame;
	}
	public List<String> compact(List<String> jsonLDs,String contexts) throws JsonLdError{
		List<String> ris = new ArrayList<String>();
		Reader targetReaderContext = new StringReader(contexts);
		Document context = JsonDocument.of(targetReaderContext);
		for (String jsonld : jsonLDs) {//-------------------------------for each json-ld INSTANCE
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
				o = "'"+o+"'";
			}else if(!iterable_element.getObject().isBlankNode()) {
				o = "<"+o+">";
			}
			turtle+=s+" "+ p+ " " + o +".\n";
		}
		return turtle;
	}
	
}

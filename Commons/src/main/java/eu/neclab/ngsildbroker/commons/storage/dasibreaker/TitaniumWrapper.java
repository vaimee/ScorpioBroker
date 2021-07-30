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
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.document.RdfDocument;
import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.RdfNQuad;

import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import jakarta.json.JsonArray;

public class TitaniumWrapper {
	private static HashMap<String,Document> contextMap = new HashMap<String,Document> ();
	
	public TitaniumWrapper() {
	}
	public String JSONtoRDF(String json) throws Exception {
		System.out.println("\n--------------------JSON converter:\n"+json);
		Reader targetReader = new StringReader(json);
		Document document = JsonDocument.of(targetReader);
//		List<Document> contexts = new ArrayList<Document>();
//        Consumer<JsonStructure> consumer = (JsonStructure x) -> {
//        	if(!x.asJsonObject().isNull("@context")) {
//        		JsonValue c = x.asJsonObject().get("@context");
//        		if(c instanceof JsonArray ) {
//        			JsonArray cja = (JsonArray)c;
//        			for (int i=0;i<cja.size();i++) {
//        				try {
//							contexts.add(resolveContextUri(
//									cja.get(i).toString()));
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//        		}else {
//        			try {
//						contexts.add(resolveContextUri(c.toString()));
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//        		}
//        	}
//        };
//		document.getJsonContent().ifPresent(consumer);
//		for (Document context : contexts) {
//			JsonLd.compact(document,context);
//		}
		RdfDataset rdfdataset = JsonLd.toRdf(document).get();
		return rdfDatasetToTripleString(rdfdataset);
	}
	
	public List<String> RDFtoJson(List<Bindings> binings) throws Exception {
		return RDFtoJson(binings,"s","p","o","e");
	}

	public List<String> RDFtoJson(List<Bindings> binings,String s,String p,String o, String e) throws Exception {
		List<String> ris = new ArrayList<String>();
		HashMap<String,List<String>> nquads = new HashMap<String,List<String>>();
		for (Bindings bind : binings) {
			String entityGraph = bind.getRDFTerm(e).getValue();
			String subject = bind.getRDFTerm(s).getValue();
			String predicate = bind.getRDFTerm(p).getValue();
			String object = bind.getRDFTerm(o).getValue();
			if(bind.getRDFTerm(s).isURI()) {
				subject="<"+subject+">";
			}
			if(bind.getRDFTerm(p).isURI()) {
				predicate="<"+predicate+">";
			}
			if(bind.getRDFTerm(o).isURI()) {
				object="<"+object+">";
			}else if(bind.getRDFTerm(o).isLiteral()) {
				object="\""+object+"\"";
			}
			String triple = subject+" "+ predicate+ " " + object;
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
			Reader targetReader = new StringReader(rdf_triples);
			JsonArray json_converted = JsonLd.fromRdf(RdfDocument.of(targetReader)).get();
			ris.add(json_converted.toString());
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
	
	protected Document resolveContextUri(String uri) throws Exception {
		if(contextMap.containsKey(uri)) {
			return contextMap.get(uri);
		}else {
			String jsonld_context =URLReader(new URL(uri.replace("\"","")));
			Reader targetReader = new StringReader(jsonld_context);
			Document context = JsonDocument.of(targetReader);
			contextMap.put(uri, context);
			return context;
		}
	}
	private  String URLReader(URL url) throws IOException {
	    try (InputStream in = url.openStream()) {
	        return IOUtils.toString(in, Charset.defaultCharset());
	    }
	}
//	private String getText(String url) throws Exception {
//	        URL website = new URL(url.replace("\"",""));
//	        URLConnection connection = website.openConnection();
//	        BufferedReader in = new BufferedReader(
//	                                new InputStreamReader(
//	                                    connection.getInputStream()));
//
//	        StringBuilder response = new StringBuilder();
//	        String inputLine;
//
//	        while ((inputLine = in.readLine()) != null) 
//	            response.append(inputLine);
//
//	        in.close();
//
//	        return response.toString();
//	    }
}


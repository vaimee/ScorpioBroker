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

public class TitaniumWrapperManualBN implements IConverterJRDF {
	private static HashMap<String,Document> contextMap = new HashMap<String,Document> ();
	
	public TitaniumWrapperManualBN() {
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
//		HashMap<String,String> bnodes = new HashMap<String,String>();
//		int bnodesIndex = 0;
		for (Bindings bind : binings) {
			String entityGraph = bind.getRDFTerm(e).getValue();
			String subject = bind.getRDFTerm(s).getValue();
			String predicate = bind.getRDFTerm(p).getValue();
			String object = bind.getRDFTerm(o).getValue();
			if(bind.getRDFTerm(s).isURI()) {
				subject="<"+subject+">";
			}else if(bind.getRDFTerm(s).isBNode()) {
				subject="_:b"+subject+"";
			}
			
			if(bind.getRDFTerm(p).isURI()) {
				predicate="<"+predicate+">";
			}
			if(bind.getRDFTerm(o).isURI()) {
				object="<"+object+">";
			}else if(bind.getRDFTerm(o).isLiteral()) {
				object="\""+object+"\"";
			}else if(bind.getRDFTerm(o).isBNode()) {
				object="_:b"+object+"";
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
			Reader targetReader = new StringReader(rdf_triples);
			//read N-Quads or turtle
			RdfDocument doc=(RdfDocument) RdfDocument.of(targetReader);
			//covert to json-ld
			JsonArray json_converted = JsonLd.fromRdf(doc).get();
			//resolve blank node and get json as string
			String jsonLd = resolveJsonBlankNode(json_converted);
			ris.add(jsonLd);
		}
		return ris;
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
	
	protected String resolveJsonBlankNode(JsonArray json) {
		JsonBlankNodeResolver jsnr = new JsonBlankNodeResolver();
		for (JsonValue x : json) {
			 JsonObject jo = x.asJsonObject();
				if(jo.containsKey("@id")) {
					String b_name = jo.get("@id").toString();
//					System.out.println(b_name); //ok
					if(b_name.matches("^\"_:b.+\"$")) {
						jsnr.put(b_name, jo);
					}else {
						if(!jsnr.hasRoot()) {
							jsnr.putRoot(jo);
						}else {
							throw new RuntimeException("Invalid JsonArray for resolveJsonBlankNode: there is more than one root element.");
						}
					}
				}else {
					throw new RuntimeException("Invalid JsonArray for resolveJsonBlankNode: a root key is not @id.");
				}
		}
		if(jsnr.hasRoot()) {
			return jsnr.iterateOnBNodes();
		}else {
			throw new RuntimeException("Invalid JsonArray for resolveJsonBlankNode: no root element found.");
		}
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
	
	protected Document resolveContext(String context) throws Exception {
			Reader targetReader = new StringReader(context);
			Document c = JsonDocument.of(targetReader);
			return c;
	}
	
	//---------------------deprecate
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
	//---------------------deprecate
	private  String URLReader(URL url) throws IOException {
	    try (InputStream in = url.openStream()) {
	        return IOUtils.toString(in, Charset.defaultCharset());
	    }
	}
	
	//----------------------------------------------------------------------------------------Internal obj for support
	private class JsonBlankNodeResolver{
		private static final int maxIter = 1000;
		private JsonObject _root=null; 
		private HashMap<String,String> _b_node;
//		private HashMap<String,Boolean> _finished;
		public JsonBlankNodeResolver() {
			_b_node= new HashMap<String,String>();
//			_finished= new HashMap<String,Boolean>();
			_root=null; 
		}
		public void put(String b_name, JsonObject b_node) {
//			b_node.remove("@id");//don't work
			JsonObjectBuilder builder =Json.createObjectBuilder();
			for (String key : b_node.keySet()) {
				if(key!="@id") {
					builder.add(key, b_node.get(key));
				}
			}
			String clean = builder.build().toString();
//			System.out.println("-->:"+clean); //ok
			_b_node.put(b_name,clean);
//			_finished.put(b_name,false);
		}
		public void putRoot(JsonObject root) {
			_root=root;
		}
		public boolean hasRoot() {
			return _root!=null;
		}
//		public boolean hasBNode(String b_name) {
//			return _finished.containsKey(b_name);
//		}
		
		public String iterateOnBNodes() {
			String ris ="";
			int count =_b_node.keySet().size();
			HashMap<String,String> finished = new HashMap<String,String>();
			HashMap<String,String> toMod;
			ArrayList<String> toRemove;
			int avoidDeadLoop = 0;
			while(finished.keySet().size()!=count && avoidDeadLoop<maxIter) {
				avoidDeadLoop++;
//				Set<String> keys =  _b_node.keySet();
				toMod = new HashMap<String,String>();
				toRemove = new ArrayList<String>();
				for (String n_name : _b_node.keySet()) {
					String temp = _b_node.get(n_name);
					Boolean thisNodeIsOK=true;
					Boolean thisNodeIsModded=false;
					for (String n_name_finished : finished.keySet()) {
						String match = "{\"@id\":"+n_name_finished+"}";
						if(temp.contains(match)) {
							temp=temp.replace(match, finished.get(n_name_finished));
							thisNodeIsModded=true;
						}
			
					}
					for (String n_name_not_finished : _b_node.keySet()) {
						String match = "{\"@id\":"+n_name_not_finished+"}";
						if(temp.contains(match)) {
							thisNodeIsOK=false;
						}
					}
					if(thisNodeIsOK) {
						finished.put(n_name, temp);
//						_b_node.remove(n_name); //ConcurrentModificationException
						toRemove.add(n_name);
					}else if(thisNodeIsModded){
						toMod.put(n_name, temp);
//						_b_node.put(n_name, temp); //ConcurrentModificationException
					}
				}
				//for ConcurrentModificationException
				//already try with Iterator (don't work) 
				//so need that stuff:
				for (String needRemove : toRemove) {
					_b_node.remove(needRemove);
				}
				for (String needMod : toMod.keySet()) {
					_b_node.put(needMod, toMod.get(needMod));
				}
			}
			if(finished.keySet().size()!=count) {
				throw new RuntimeException("JsonBlankNodeResolver.iterateOnBNodes Avoid dead loop, max iter number reaced.");
			}else {
				ris = _root.asJsonObject().toString();
				for (String n_name_finished : finished.keySet()) {
					String match = "{\"@id\":"+n_name_finished+"}";
					if(ris.contains(match)) {
						ris=ris.replace(match, finished.get(n_name_finished));
					}
				}
			}
			return ris;
		}
		 
	}
}

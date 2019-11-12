package it.univaq.odws.maven.rest.music;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class MusicImpl implements Music {
	private static final String BANDS_INFO_RDF_RESOURCE = "bands_info_cleaned.rdf";
	private static final String BANDS_WORKS_RDF_RESOURCE = "bands_works_cleaned.rdf";
	
	private Dataset loadDataset() {
		Dataset dataset = RDFDataMgr.loadDataset(BANDS_INFO_RDF_RESOURCE);
		RDFDataMgr.read(dataset, BANDS_WORKS_RDF_RESOURCE);
		dataset.begin(ReadWrite.READ);
		return dataset;
	}
	

	private List<String> prefixes = Arrays.asList( 
			"PREFIX cd: <http://www.best.groups/cd#>",
			"PREFIX cdWorks: <http://www.best.groups/cdWorks#>"
			);

	private static final String bandsByGenre = "SELECT ?bandname ?genrename ?activeYearsStartYear ?activeYearsEndYear ?noOfMembers ?hometown ?numworks WHERE { \n" +  
												   "?band  cd:bandname ?bandname .\n" +
												   "?band  cd:genrename ?genrename .\n" +
												   "?band  cd:activeYearsStartYear ?activeYearsStartYear .\n" +
												   "?band  cd:activeYearsEndYear ?activeYearsEndYear .\n" +
												   "?band  cd:noOfMembers ?noOfMembers .\n" +
												   "?band  cd:hometown ?hometown .\n" +
												   "?bandworks cdWorks:bandname ?bandname .\n" +
												   "?bandworks cdWorks:numworks ?numworks .\n" +
												   "filter regex(?genrename,\"X\")}";
	
	private static final String bandsByHometown = 	"SELECT ?bandname ?genrename ?hometown  WHERE { \n" +  
													"?band  cd:bandname ?bandname .\n" +
													"?band  cd:genrename ?genrename .\n" +
													"?band  cd:activeYearsStartYear ?activeYearsStartYear .\n" +
													"?band  cd:activeYearsEndYear ?activeYearsEndYear .\n" +
													"?band  cd:noOfMembers ?noOfMembers .\n" +
													"?band  cd:hometown ?hometown .\n" +
													"?bandworks cdWorks:bandname ?bandname .\n" +
													"?bandworks cdWorks:numworks ?numworks .\n" +
													"filter regex(?hometown,\"X\")}";
	
	private static final String bandsActive = 		"SELECT ?bandname ?genrename ?activeYearsStartYear ?activeYearsEndYear ?noOfMembers ?hometown ?numworks  WHERE { \n"+
											 	    "?band  cd:bandname ?bandname .\n" +
												    "?band  cd:genrename ?genrename .\n" +
												    "?band  cd:activeYearsStartYear ?activeYearsStartYear .\n" +
												    "OPTIONAL{?band  cd:activeYearsEndYear ?activeYearsEndYear} .\n" +
												    "FILTER (?activeYearsEndYear = '') .\n" +
												    "?band  cd:noOfMembers ?noOfMembers .\n" +
												    "?band  cd:hometown ?hometown .\n" +
												    "?bandworks cdWorks:bandname ?bandname .\n" +
												    "?bandworks cdWorks:numworks ?numworks .\n" +
												    "filter regex(?genrename,\"X\")}";
	
	private static final String numberOfAlbums = "SELECT ?activeYearsEndYear (count(distinct ?numworks) "+
												"AS ?count WHERE { \n" +
													"?bandworks cdWorks:numworks ?numworks .\n"+
													"?band  cd:activeYearsEndYear ?activeYearsEndYear .\n" +
													"BIND(year(?activeYearsEndYear) AS ?year)"+
													" GROUP BY ?year "+
													"ORDER BY desc(?count)";
	
	private static final String bandsByMembres = "SELECT ?bandname ?genrename ?activeYearsStartYear ?activeYearsEndYear ?noOfMembers ?hometown ?numworks WHERE { \n" +  
												 "?band  cd:bandname ?bandname .\n" +
												 "?band  cd:genrename ?genrename .\n" +
												 "?band  cd:activeYearsStartYear ?activeYearsStartYear .\n" +
												 "?band  cd:activeYearsEndYear ?activeYearsEndYear .\n" +
												 "?band  cd:noOfMembers ?noOfMembers .\n" +
												 "?band  cd:hometown ?hometown .\n" +
												 "?bandworks cdWorks:bandname ?bandname .\n" +
												 "?bandworks cdWorks:numworks ?numworks .\n" +
												 "filter regex(?noOfMembres,\"X\")}";
													
													

														
	//put query result in an HashMap
	public List<Map<String, String>> retrieveQueryResult(ResultSet r){
		List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		List<String> vars= r.getResultVars();
		while (r.hasNext()) {
			Map<String, String> result = new HashMap<String, String>();
			QuerySolution res = r.next();


			for (String var : vars) {
				if(res.contains(var)){
					String valueString = res.get(var).toString();
					result.put(var, valueString);
				}
			}
			results.add(result);
		}
		System.out.println(results.toString());
		return results;
	}

	//composition of the string to be composed
	public String showResultFromMap (Map<Integer,ArrayList<RDFNode>> resultMap) {
		String resultString = new String();

		for (Map.Entry<Integer, ArrayList<RDFNode>> r : resultMap.entrySet()) {
			resultString = resultString.concat("----------------------------------------------------------------------------------------------------------------------------------------\n");
			for (RDFNode node: r.getValue()) {
				resultString = resultString.concat(node.toString()+"\n");	
			}
			resultString = resultString.concat("----------------------------------------------------------------------------------------------------------------------------------------\n\n");

		}

		return resultString;

	}


	private String jacksonConvert(List<Map<String,String>> resultMap) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();

		String json = objectMapper.writeValueAsString(resultMap);

		System.out.println(json);
		return json;
	}

	public String callService(String service) throws JsonProcessingException {
		Dataset dataset = loadDataset();
		StringBuilder query = new StringBuilder();

		for (String prefix: this.prefixes) {
			query.append(prefix).append(System.lineSeparator());
		}

		query.append(service);

		QueryExecution qexec = QueryExecutionFactory.create(query.toString(),dataset);    

		ResultSet r = qexec.execSelect();

		List<Map<String,String>> resultMap = retrieveQueryResult(r);

		return jacksonConvert(resultMap); 
	}


	@Override
	public String getbandsByGenre(String genre) throws JsonProcessingException {
		return callService(MusicImpl.bandsByGenre.replace("X", genre));
	}

	@Override
	public String getNumberBandsByGenre(String genre) throws JsonProcessingException {
		return callServiceCounter(MusicImpl.bandsByGenre.replace("X", genre));
	}

	@Override
	public String getbandsByHometown(String hometown) throws JsonProcessingException {
		return callService(MusicImpl.bandsByHometown.replace("X", hometown));
	}

	@Override
	public String getNumberBandsByHometown(String hometown) throws JsonProcessingException {
		return callServiceCounter(MusicImpl.bandsByHometown.replace("X", hometown));
	}
	
	public String getbandsActive(String genre) throws JsonProcessingException {
		return callService(MusicImpl.bandsActive.replace("X", genre));
	}
	
	public String callServiceCounter(String service) throws JsonProcessingException {
		Dataset dataset = loadDataset();
		StringBuilder query = new StringBuilder();

		for (String prefix: this.prefixes) {
			query.append(prefix).append(System.lineSeparator());
		}

		query.append(service);

		QueryExecution qexec = QueryExecutionFactory.create(query.toString(),dataset);    

		ResultSet r = qexec.execSelect();

		List<Map<String,String>> resultMap = retrieveQueryResult(r);

		return "[{\"count\":"+resultMap.size()+"}]"; 
	}

	


}

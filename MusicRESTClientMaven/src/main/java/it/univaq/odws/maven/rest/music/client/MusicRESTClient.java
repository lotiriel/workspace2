package it.univaq.odws.maven.rest.music.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MusicRESTClient {
	
	private static final String endpoint = "http://localhost:8080/MusicRESTServiceMavenRDF/rest/music/";

	private static final ArrayList<String> queries = new ArrayList<String>() {{
		add("genre/rock|Select bands of a given genre");
		add("bandsByGenre/rock|Count bands of a given genre");
		add("hometown/Hertford|Select bands which a given hometown");
		add("bandsByHometown/Hertford|Count bands which a given hometown");
		add("bandsActive/1998|Select bands of a given genre which are still in activity");
		add("bandsByMembers/rock|Select bands given the number of their members");	
		add("bandname/Dover|Select bands with a given name");
		add("activeYearsStartYear/1995|Select bands given the year of establishment");
		add("genre/rock|Select bands of a given genre most Active");

	}};
	

	public static void main(String[] args) {	
		System.out.println("Hello! Here a list of the available queries with their relative code \n");
		for (int i=0;i<queries.size();i++) {
			System.out.println("Query code "+(i+1)+" - "+queries.get(i).split("\\|")[1]);
		}
		
		while(true) {
			
			System.out.println("Enter the code of the query that you want to test (between 1 and "+queries.size()+"): ");
			Scanner scanner = new Scanner(System.in);
			int qNumber = scanner.nextInt();
			if (qNumber < 1 || qNumber > queries.size()) {
				System.out.println("Invalid code!");
				continue;
			}
			
			System.out.println("Enter the query parameter: ");
			scanner = new Scanner(System.in);	
			
			String parameter = scanner.nextLine();
			
			if (qNumber ==8) {
				parameter = '"' + parameter + '"';
			}	
			
			String final_query = queries.get(qNumber-1).split("/")[0] + "/" + parameter;
			
			System.out.println("Executing Query: "+queries.get(qNumber-1).split("\\|")[1]+": " + parameter + "\n");
			callendpoint(endpoint + final_query, MediaType.APPLICATION_JSON);
			
		}
	}

	public static void callendpoint(String endpoint, String responseType) {
		WebClient client = WebClient.create(endpoint);
		Response response = client.accept(responseType).get();
		String resultString = getResultString(response);
		if(!resultString.isEmpty()) {
			List<HashMap<String, String>> resultJSON = getJSON(resultString);
			for(HashMap<String, String> m : resultJSON) {
				System.out.println(m.toString());
			}
		}
		client.close();
		response.close();
	}

	public static String getResultString(Response response) {
		try {
			String value = IOUtils.toString((InputStream) response.getEntity());
			return value;
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static List<HashMap<String, String>> getJSON(String value) 
	{
		// per accedere direttamente ai field del JSON
		ObjectMapper mapper = new ObjectMapper();
		List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		try {
			data = mapper.readValue(value,  new ArrayList<HashMap<String, String>>(){}.getClass());
			return data;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

}

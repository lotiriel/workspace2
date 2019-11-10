package it.univaq.odws.maven.rest.music;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

@Path("/music")
public interface Music {
	
	@GET
	@Path("/genre/{genre}")
	@Produces({MediaType.APPLICATION_JSON})
	String getbandsByGenre (@PathParam("genre")String genre) throws JsonProcessingException;
	
	@GET
	@Path("/bandsByGenre/{genre}")
	@Produces({MediaType.APPLICATION_JSON})
	String getNumberBandsByGenre (@PathParam("genre")String genre) throws JsonProcessingException;
	
	@GET
	@Path("/hometown/{hometown}")
	@Produces({MediaType.APPLICATION_JSON})
	String getbandsByHometown (@PathParam("hometown")String hometown) throws JsonProcessingException;	
	
	@GET
	@Path("/bandsByHometown/{hometown}")
	@Produces({MediaType.APPLICATION_JSON})
	String getNumberBandsByHometown (@PathParam("hometown")String hometown) throws JsonProcessingException;
		
}

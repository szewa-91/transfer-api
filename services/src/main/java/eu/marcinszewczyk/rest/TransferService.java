package eu.marcinszewczyk.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/hello")
public class TransferService {

	@GET
	public Response transactions() {
		return Response.ok("Ok").build();
	}
}

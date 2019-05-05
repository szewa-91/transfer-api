package eu.marcinszewczyk.rest;

import eu.marcinszewczyk.model.Transaction;
import eu.marcinszewczyk.services.TransactionsService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transactions")
public class TransactionResource {
	private TransactionsService transactionsService = new TransactionsService();

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTransaction(@PathParam("id") Long id) {
		return Response.ok(transactionsService.getTransaction(id)).build();
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response postTransaction(Transaction transaction) {
		System.out.println(transaction);
		return Response.ok().build();
	}
}

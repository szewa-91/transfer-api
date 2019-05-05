package eu.marcinszewczyk.rest;

import eu.marcinszewczyk.model.Transaction;
import eu.marcinszewczyk.services.ServiceProvider;
import eu.marcinszewczyk.services.TransactionsService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transactions")
public class TransactionResource {
	private TransactionsService transactionsService;

	public TransactionResource() {
		transactionsService = ServiceProvider.getTransactionsService();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllTransactions() {
		return Response.ok(transactionsService.getAllTransactions()).build();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTransaction(@PathParam("id") Long id) {
		return Response.ok(transactionsService.getTransaction(id)).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response postTransaction(Transaction transaction) {
		return Response.ok(transactionsService.saveTransaction(transaction)).build();
	}
}

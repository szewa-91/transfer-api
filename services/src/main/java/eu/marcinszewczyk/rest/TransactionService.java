package eu.marcinszewczyk.rest;

import eu.marcinszewczyk.model.Transaction;
import eu.marcinszewczyk.model.TransactionDirection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

@Path("/transactions")
public class TransactionService {
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response hello(@PathParam("id") Long id) {
		Transaction transaction = new Transaction();
		transaction.setId(id);
		transaction.setAccountNumber("1234567");
		transaction.setDirection(TransactionDirection.PAY);
		transaction.setAmount(BigDecimal.valueOf(200000.20));
		return Response.ok(transaction).build();
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response helloUsingJson(Transaction transaction) {
		System.out.println(transaction);
		return Response.ok().build();
	}
}

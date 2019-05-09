package eu.marcinszewczyk.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.marcinszewczyk.model.Transaction;
import eu.marcinszewczyk.services.TransactionsService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static java.lang.Long.parseLong;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class TransactionResource extends HttpServlet {
    private TransactionsService transactionsService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public TransactionResource(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
        List<String> pathParts = getPaths(request);

        try {
            if (pathParts.isEmpty()) {
                respondWithObject(response, HttpServletResponse.SC_OK, transactionsService.getAllTransactions());

            } else if (pathParts.size() == 1) {
                long id = parseLong(pathParts.get(0));
                respondWithObject(response, HttpServletResponse.SC_OK, transactionsService.getTransaction(id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
        Transaction transaction = objectMapper.readValue(request.getReader(), Transaction.class);

        try {
            respondWithObject(response, HttpServletResponse.SC_OK,
                    transactionsService.executeTransaction(transaction));
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void respondWithObject(HttpServletResponse response, int status, Object object) throws IOException {
        response.getWriter().println(objectMapper.writeValueAsString(object));
        response.setContentType("application/json");
        response.setStatus(status);
    }

    private List<String> getPaths(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        List<String> pathParts = pathInfo != null ? asList(pathInfo.split("/")) : emptyList();
        return pathParts.stream().filter(s -> !s.isBlank()).collect(toList());
    }
}

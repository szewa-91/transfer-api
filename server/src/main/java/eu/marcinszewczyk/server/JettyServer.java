package eu.marcinszewczyk.server;

import eu.marcinszewczyk.rest.TransactionResource;
import eu.marcinszewczyk.services.ServiceProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

public class JettyServer {
    private ServiceProvider serviceProvider;
    private Server server;

    public JettyServer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public void start() {
        server = new Server(9090);
        configureRest(server);

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureRest(Server server) {
        ServletContextHandler handler =
                new ServletContextHandler(NO_SESSIONS);
        handler.setContextPath("/");

        TransactionResource transactionResource = new TransactionResource(serviceProvider.getTransactionsService());
        handler.addServlet(new ServletHolder(transactionResource), "/transactions/*");

        server.setHandler(handler);
    }

    public void stop() throws Exception {
        server.stop();
    }
}
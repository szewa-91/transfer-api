package eu.marcinszewczyk.server;

import eu.marcinszewczyk.rest.TransferResource;
import eu.marcinszewczyk.services.ServiceProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

public class JettyServer {
    private final ServiceProvider serviceProvider;
    private final int port;
    private Server server;

    public JettyServer(int port, ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
        this.port = port;
    }

    public void start() {
        server = new Server(port);
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

        TransferResource transferResource = new TransferResource(serviceProvider.getTransferService());
        handler.addServlet(new ServletHolder(transferResource), "/transfers/*");

        server.setHandler(handler);
    }

    public void stop() throws Exception {
        server.stop();
    }
}
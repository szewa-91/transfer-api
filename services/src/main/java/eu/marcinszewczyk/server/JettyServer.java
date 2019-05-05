package eu.marcinszewczyk.server;

import eu.marcinszewczyk.rest.TransferService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

public class JettyServer {

    public static void main(String... args) {
        new JettyServer().start();
    }

    private void start() {
        Server server = new Server(9090);
        configureRest(server);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }
    }

    private void configureRest(Server server) {
        ServletContextHandler handler =
                new ServletContextHandler(NO_SESSIONS);
        handler.setContextPath("/");

        ServletHolder serHol = handler.addServlet(ServletContainer.class, "/rest/*");
        serHol.setInitOrder(1);
        serHol.setInitParameter("jersey.config.server.provider.classnames",
                TransferService.class.getCanonicalName() );

        server.setHandler(handler);
    }
}
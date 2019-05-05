package eu.marcinszewczyk;

import eu.marcinszewczyk.server.JettyServer;
import eu.marcinszewczyk.services.ServiceProvider;

public class App {
    public static void main(String... args) {
        ServiceProvider serviceProvider = new ServiceProvider();
        new JettyServer(serviceProvider).start();
    }
}

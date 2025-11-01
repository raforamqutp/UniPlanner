package utp.UNIplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class UnIplannerApplication {

    public static void main(String[] args) {
        System.setProperty("server.port", "8420"); // Fuerza el puerto 8420
        ConfigurableApplicationContext context = SpringApplication.run(UnIplannerApplication.class, args);

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI("http://localhost:8420"));
            }
        } catch (Exception e) {
            System.err.println("No se pudo abrir el navegador: " + e.getMessage());
        }
    }
}

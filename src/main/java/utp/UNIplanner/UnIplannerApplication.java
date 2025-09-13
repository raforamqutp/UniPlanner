package utp.UNIplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class UnIplannerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(UnIplannerApplication.class, args);

        // Intenta abrir el navegador directo al programa
        openBrowser("http://localhost:8080/cursos");
    }

    private static void openBrowser(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e) {
                System.err.println("No se pudo abrir el navegador automáticamente: " + e.getMessage());
            }
        } else {
            System.err.println("El entorno no soporta abrir el navegador automáticamente.");
        }
    }
}

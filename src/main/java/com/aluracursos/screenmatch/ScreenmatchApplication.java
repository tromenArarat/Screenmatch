package com.aluracursos.screenmatch;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporada;
import com.aluracursos.screenmatch.principal.EjemploStreams;
import com.aluracursos.screenmatch.principal.Principal;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ScreenmatchApplication.class, args);
    }

    /*
    La interfaz CommandLineRunner es un recurso poderoso dentro del universo de
    Spring Framework, ampliamente utilizado en el desarrollo de aplicaciones Java.
    Permite ejecutar alguna acción justo después de la inicialización de nuestra aplicación.
    Puede ser muy útil, por ejemplo, si queremos cargar algunos datos en
    nuestra base de datos justo al iniciar nuestra aplicación.
    También puede utilizarse para iniciar recursos, como conexiones de red,
    y para verificar la integridad de ciertos componentes o
    servicios con los que la aplicación va a interactuar.
    */
    @Override
    public void run(String... args) throws Exception {
        Principal principal = new Principal();
        principal.muestraElMenu();
        //EjemploStreams ejemploStreams = new EjemploStreams();
        //ejemploStreams.muestraEjemplo();
    }
}

package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporada;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();


    private final String URL_BASE = "https://www.omdbapi.com/?t=";

    private ConvierteDatos conversor = new ConvierteDatos();

    public void muestraElMenu() {
        String apikeyQueryParam = "&apikey=" + "TU_API_KEY";
        System.out.println(apikeyQueryParam);
        System.out.println("Por favor escribe el nombre de la serie que deseas buscar:");
        // Buscar los datos generales de las series
        var nombreSerie = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + apikeyQueryParam);
        var datos = conversor.obtenerDatos(json, DatosSerie.class);

        // Buscar los datos de todas las temporadas
        List<DatosTemporada> temporadas = new ArrayList<>();

        // Handle null case for totalDeTemporadas()
        int totalTemporadas = datos.totalDeTemporadas() != null ? datos.totalDeTemporadas().intValue() : 1;

        for (int i = 1; i <= totalTemporadas; i++) {
            json = consumoAPI.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + "&Season=" + i + apikeyQueryParam);
            var datosTemporadas = conversor.obtenerDatos(json, DatosTemporada.class);

            // Ensure that episodios() is not null
            List<DatosEpisodio> episodios = datosTemporadas.episodios() != null ? datosTemporadas.episodios() : Collections.emptyList();

            // Now you can use stream safely
            episodios.stream()
                    .forEach(System.out::println);

            temporadas.add(datosTemporadas);
        }

        // Convertir todas las info a una lista del tipo DatosEpisodio
        List<DatosEpisodio> datosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios() != null ? t.episodios().stream() : Stream.empty())
                .collect(Collectors.toList());

        //Obtener los top 5 episodios
        System.out.println("Top 5 episodios");
        datosEpisodios.stream()
                .filter(e -> e != null && !"N/A".equalsIgnoreCase(e.evaluacion()))
                .sorted(Comparator.comparingDouble(e -> Double.parseDouble(e.evaluacion())))
                .map(e -> e.titulo().toUpperCase())
                .limit(5)
                .forEach(System.out::println);

        // Convirtiendo los datos a una lista del tipo Episodio
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios() != null ? t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)) : Stream.empty())
                .collect(Collectors.toList());

        // Búsqueda de episodios a partir de x año
        System.out.println("Por favor indica el año a partir del cual deseas ver los episodios");
        var fecha = teclado.nextInt();
        teclado.nextLine();

        LocalDate fechaBusqueda = LocalDate.of(fecha, 1, 1);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodios.stream()
                .filter(e -> e != null && e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
                .forEach(e -> {
                    System.out.println(
                            "Temporada " + e.getTemporada() +
                                    " Episodio " + e.getTitulo() +
                                    " Fecha de lanzamiento " + e.getFechaDeLanzamiento().format(dtf)
                    );
                });

        // Busca episodios por parte del título
        System.out.println("Por favor ingresa lo que recuerdes del episodio que buscas");
        var parteTitulo = teclado.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e != null && e.getTitulo().toUpperCase().contains(parteTitulo.toUpperCase()))
                .findFirst();
        if (episodioBuscado.isPresent()) {
            System.out.println("Episodio encontrado");
            System.out.println("Los datos son: " + episodioBuscado.get());
        } else {
            System.out.println("Episodio no encontrado");
        }

        Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
                .filter(e -> e != null && e.getEvaluacion() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getEvaluacion)));
        System.out.println(evaluacionesPorTemporada);

        // Arma estadísticas sobre una evaluación
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e != null && e.getEvaluacion() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
        System.out.println("Media de las evaluaciones " + est.getAverage());
    }
}

package com.aluracursos.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosTemporada(
        @JsonAlias("Season")
        Integer numero,
        @JsonAlias("Episodes")
        List<DatosEpisodio> episodios
) {
        public static DatosTemporada create(Integer numero, List<DatosEpisodio> episodios) {
                return new DatosTemporada(numero, episodios != null ? episodios : Collections.emptyList());
        }
}


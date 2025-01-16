package com.literaturachallenger.challengerLiteratura.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.literaturachallenger.challengerLiteratura.model.Libro;
import com.literaturachallenger.challengerLiteratura.modelo.Autor;
import com.literaturachallenger.challengerLiteratura.repository.AutorRepository;
import com.literaturachallenger.challengerLiteratura.repository.LibroRepository;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    private static final Logger logger = LoggerFactory.getLogger(LibroService.class);

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public LibroService(LibroRepository libroRepository, AutorRepository autorRepository, CloseableHttpClient httpClient, ObjectMapper objectMapper) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public List<Libro> obtenerLibrosPorTitulo(String titulo) throws IOException, URISyntaxException {
        return obtenerLibrosPorParametro("search", titulo);
    }

    public List<Libro> obtenerLibrosPorIdioma(String idioma) throws IOException, URISyntaxException {
        return obtenerLibrosPorParametro("topic", idioma);
    }

    private List<Libro> obtenerLibrosPorParametro(String parametro, String valor) throws IOException, URISyntaxException {
        List<Libro> libros = new ArrayList<>();
        URI uri;
        try {
            uri = new URIBuilder("https://gutendex.com/books")
                    .addParameter(parametro, valor)
                    .build();
        } catch (URISyntaxException e) {
            logger.error("Error al construir la URI: ", e);
            return Collections.emptyList();
        }

        HttpGet httpGet = new HttpGet(uri);
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            int statusCode = response.getCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try {
                        String json = EntityUtils.toString(entity);
                        JsonNode rootNode = objectMapper.readTree(json);
                        JsonNode resultsNode = rootNode.path("results");

                        if (resultsNode.isArray()) {
                            for (JsonNode libroNode : resultsNode) {
                                try {
                                    Libro libro = parseLibro(libroNode.toString());
                                    libros.add(libro);
                                } catch (JsonProcessingException e) {
                                    logger.error("Error procesando libro individualmente: ", e);
                                }
                            }
                        } else {
                            logger.warn("La respuesta de Gutendex no contiene resultados.");
                        }
                    } catch (ParseException | JsonProcessingException e) {
                        logger.error("Error al procesar la respuesta: ", e);
                    }
                } else {
                    logger.warn("La respuesta no contiene una entidad.");
                }
            } else {
                logger.error("Error al obtener libros: Código de estado {} para {}", statusCode, uri);
            }
        } catch (IOException e) {
            logger.error("Error de IO: ", e);
        }
        return libros;
    }

    @Transactional
    public void guardarLibro(Libro libro) {
        Optional<Autor> autorOptional = autorRepository.findByNombre(libro.getAutor().getNombre());
        Autor autor;
        if (autorOptional.isPresent()) {
            autor = autorOptional.get();
        } else {
            autor = autorRepository.save(libro.getAutor());
        }
        libro.setAutor(autor);
        libroRepository.save(libro);
    }

    @Transactional
    public Libro parseLibro(String json) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(json);

        Libro libro = new Libro();
        libro.setTitulo(rootNode.path("title").asText("Título desconocido"));

        JsonNode authorsNode = rootNode.path("authors");
        if (authorsNode.isArray() && !authorsNode.isEmpty()) {
            JsonNode firstAuthor = authorsNode.get(0);
            String nombre = firstAuthor.path("name").asText("Autor desconocido");
            Integer anioNacimiento = parseYear(firstAuthor.path("birth_year"));
            Integer anioFallecimiento = parseYear(firstAuthor.path("death_year"));

            Optional<Autor> autorOptional = autorRepository.findByNombre(nombre);
            Autor autor;
            if (autorOptional.isPresent()) {
                autor = autorOptional.get();
            } else {
                autor = new Autor(nombre, anioNacimiento, anioFallecimiento);
                autor = autorRepository.save(autor);
            }
            libro.setAutor(autor);

        } else {
            libro.setAutor(new Autor("Autor desconocido", null, null));
        }

        JsonNode languagesNode = rootNode.path("languages");
        String idioma = "Desconocido";

        if (languagesNode.isArray() && !languagesNode.isEmpty()) {
            idioma = languagesNode.get(0).asText();
        }
        libro.setIdioma(idioma);

        JsonNode downloadsNode = rootNode.path("download_count");
        int descargas = downloadsNode.asInt(0);

        libro.setDescargas(descargas);

        return libro;
    }

    private Integer parseYear(JsonNode node) {
        if (node != null && !node.isMissingNode()) {
            try {
                return node.asInt();
            } catch (NumberFormatException e) {
                logger.error("Error al parsear año: {}", node.asText(), e);
            }
        }
        return null;
    }

    public long contarLibrosPorIdioma(String idioma) {
        return libroRepository.countByIdioma(idioma);
    }

    public void mostrarEstadisticasPorIdioma(String idioma1, String idioma2) {
        long cantidadIdioma1 = contarLibrosPorIdioma(idioma1);
        long cantidadIdioma2 = contarLibrosPorIdioma(idioma2);

        System.out.println("Cantidad de libros en " + idioma1 + ": " + cantidadIdioma1);
        System.out.println("Cantidad de libros en " + idioma2 + ": " + cantidadIdioma2);
    }

    public long contarLibrosPorIdiomaConStreams(String idioma) {
        List<Libro> libros = libroRepository.findByIdioma(idioma);
        return libros.stream().count();
    }

    public void mostrarEstadisticasPorIdiomaConStreams(String idioma1, String idioma2) {
        long cantidadIdioma1 = contarLibrosPorIdiomaConStreams(idioma1);
        long cantidadIdioma2 = contarLibrosPorIdiomaConStreams(idioma2);
    }

    public List<Autor> obtenerAutoresVivosEnAnio(Integer anio) {
        return autorRepository.findByAnioNacimientoLessThanEqualAndAnioFallecimientoGreaterThanEqualOrAnioNacimientoLessThanEqualAndAnioFallecimientoIsNull(anio, anio, anio);
    }
}
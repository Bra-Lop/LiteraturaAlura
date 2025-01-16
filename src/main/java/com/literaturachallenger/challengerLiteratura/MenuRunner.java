package com.literaturachallenger.challengerLiteratura;

import com.literaturachallenger.challengerLiteratura.model.Libro;
import com.literaturachallenger.challengerLiteratura.modelo.Autor;
import com.literaturachallenger.challengerLiteratura.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@Component
public class MenuRunner implements CommandLineRunner {

    @Autowired
    private LibroService libroService;
    private List<Libro> librosBuscados = new ArrayList<>();
    private Scanner scanner;
    private final ApplicationContext context;

    public MenuRunner(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(String... args) throws Exception {
        scanner = new Scanner(System.in);
        mostrarMenu();
        scanner.close();
    }
    public void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("--- Menú ---");
            System.out.println("1. Buscar libros por título");
            System.out.println("2. Insertar libro");
            System.out.println("3. Mostrar libros buscados");
            System.out.println("4. Listar autores vivos en determinado año");
            System.out.println("5. Estadísticas por idioma");
            System.out.println("0. Salir");
            System.out.print("Ingrese una opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Consume el carácter de nueva línea (SOLUCIÓN)
                ejecutarOpcion(opcion, scanner);
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Ingrese un número.");
                scanner.next(); // Limpiar el buffer
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Saliendo...");
        scanner.close();
    }

    private int obtenerEnteroUsuario() {
        while (!scanner.hasNextInt()) {
            System.out.println("Entrada no válida. Por favor, ingrese un número entero");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private void ejecutarOpcion(int opcion, Scanner scanner) throws IOException, URISyntaxException {
        switch (opcion) {
            case 1:
                buscarLibros();
                break;
            case 2:
                insertarLibro();
                break;
            case 3:
                mostrarLibrosBuscados();
                break;
            case 4:
                listarAutoresVivos();
                break;
            case 5:
                mostrarEstadisticasPorIdioma();
                break;
            case 0:
                System.out.println("Saliendo...");
                SpringApplication.exit(context, () -> 0); // Cierre controlado de Spring
                break;
            default:
                System.out.println("Opción inválida. Intente de nuevo.");
        }
    }

    private void buscarLibros() throws IOException, URISyntaxException {
        System.out.print("Ingrese el título a buscar: ");
        String titulo = scanner.nextLine();
        librosBuscados = libroService.obtenerLibrosPorTitulo(titulo);

        System.out.println("Número de libros encontrados: " + librosBuscados.size());
        for (Libro libro : librosBuscados) {
            System.out.println("Libro: " + libro.getTitulo());
            if (libro.getAutor() != null) {
                Autor autor = libro.getAutor();
                System.out.println("  Autor: " + autor.getNombre());
                System.out.println("  Nacimiento: " + autor.getAnioNacimiento());
                System.out.println("  Fallecimiento: " + autor.getAnioFallecimiento());
            } else {
                System.out.println("  Sin autor.");
            }
        }
    }

    private void insertarLibro() {
        System.out.println("Ingrese el título del Libro:");
        String titulo = scanner.nextLine();

        System.out.println("Ingrese el nombre del Autor:");
        String nombreAutor = scanner.nextLine();

        System.out.println("Ingrese el año de nacimiento del Autor (o deje vacío si no se conoce):");
        String anioNacimientoStr = scanner.nextLine();
        Integer anioNacimiento = null;
        if (!anioNacimientoStr.isEmpty()) {
            try {
                anioNacimiento = Integer.parseInt(anioNacimientoStr);
            } catch (NumberFormatException e) {
                System.err.println("Año de nacimiento inválido. Se guardará como desconocido.");
            }
        }

        System.out.println("Ingrese el año de fallecimiento del Autor (o deje vacío si no se conoce):");
        String anioFallecimientoStr = scanner.nextLine();
        Integer anioFallecimiento = null;
        if (!anioFallecimientoStr.isEmpty()) {
            try {
                anioFallecimiento = Integer.parseInt(anioFallecimientoStr);
            } catch (NumberFormatException e) {
                System.err.println("Año de fallecimiento inválido. Se guardará como desconocido.");
            }
        }

        Autor autor = new Autor(nombreAutor, anioNacimiento, anioFallecimiento);

        System.out.println("Ingrese el idioma del Libro:");
        String idioma = scanner.nextLine();

        System.out.println("Ingrese el número de descargas del Libro:");
        int descargas = obtenerEnteroUsuario();
        scanner.nextLine(); // Consumir el newline

        Libro nuevoLibro = new Libro();
        nuevoLibro.setTitulo(titulo);
        nuevoLibro.setAutor(autor);
        nuevoLibro.setIdioma(idioma);
        nuevoLibro.setDescargas(descargas);

        libroService.guardarLibro(nuevoLibro);
        System.out.println("Libro insertado correctamente.");
    }

    private void mostrarLibrosBuscados() {
        System.out.println("\nLibros Buscados:");
        if (librosBuscados.isEmpty()) {
            System.out.println("No se encontraron libros.");
            return;
        }
        for (Libro libro : librosBuscados) {
            System.out.println(libro);
        }
    }

    private void mostrarEstadisticasPorIdioma() throws IOException, URISyntaxException {
        System.out.println("Ingrese el primer idioma para las estadísticas:");
        String idioma1 = scanner.nextLine();
        System.out.println("Ingrese el segundo idioma para las estadísticas:");
        String idioma2 = scanner.nextLine();

        List<Libro> librosIdioma1 = libroService.obtenerLibrosPorIdioma(idioma1);
        List<Libro> librosIdioma2 = libroService.obtenerLibrosPorIdioma(idioma2);

        System.out.println("Cantidad de libros en " + idioma1 + ": " + librosIdioma1.size());
        System.out.println("Cantidad de libros en " + idioma2 + ": " + librosIdioma2.size());;
    }

    private void listarAutoresVivos() {
        System.out.println("Ingrese el año para buscar autores vivos:");
        try {
            int anioABuscar = obtenerEnteroUsuario();
            scanner.nextLine();

            List<Autor> autoresVivos = libroService.obtenerAutoresVivosEnAnio(anioABuscar); // Llamar al método del servicio

            System.out.println("\nAutores vivos en el año " + anioABuscar + ":");
            if (autoresVivos.isEmpty()) {
                System.out.println("No se encontraron autores vivos en ese año.");
            } else {
                for (Autor autor : autoresVivos) {
                    System.out.println(autor);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Año inválido. Por favor, ingrese un número entero.");
        }
    }
}
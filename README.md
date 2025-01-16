¡Claro que sí! Con la información que me has proporcionado a lo largo de esta conversación, puedo crear un README más completo y preciso para tu proyecto challengerLiteratura.

Markdown

# challengerLiteratura

Este proyecto es una API REST desarrollada con Spring Boot que interactúa con la API de Gutendex (gutendex.com) para gestionar información detallada sobre libros y autores. Permite buscar libros por título, extraer datos relevantes de los autores (como fechas de nacimiento y fallecimiento) y ofrece estadísticas sobre la cantidad de libros por idioma. Además, persiste la información en una base de datos PostgreSQL.

## Tabla de Contenidos

*   [Descripción](#descripción)
*   [Funcionalidades](#funcionalidades)
*   [Tecnologías Utilizadas](#tecnologías-utilizadas)
*   [Configuración](#configuración)
*   [Ejecución](#ejecución)
*   [Estructura de Datos](#estructura-de-datos)
*   [Ejemplos de Uso](#ejemplos-de-uso)
*   [Manejo de Errores](#manejo-de-errores)
*   [Contribución](#contribución)
*   [Licencia](#licencia)

## Descripción

`challengerLiteratura` facilita la creación de una biblioteca digital utilizando la información de Gutendex. Se centra en la extracción y almacenamiento de datos de autores (nombre, año de nacimiento y fallecimiento) y de sus libros (título, idioma, número de descargas). La aplicación evita la duplicidad de autores en la base de datos y proporciona estadísticas sobre la distribución de libros por idioma.

## Funcionalidades

*   **Búsqueda de libros:**
    *   Por título (usando la API de Gutendex).
*   **Extracción de datos de autores:**
    *   Nombre.
    *   Año de nacimiento.
    *   Año de fallecimiento.
*   **Información sobre libros:**
    *   Título.
    *   Idioma(s).
    *   Número de descargas.
*   **Persistencia de datos:** Almacenamiento en una base de datos PostgreSQL.
*   **Gestión de autores:** Se asegura de que no haya autores duplicados en la base de datos.
*   **Estadísticas por idioma:** Conteo de libros por idioma, con una implementación que utiliza Java Streams.
*   **Consulta de autores vivos en un año específico:** Permite obtener una lista de autores que estuvieron vivos en un año dado.
*   **Menú interactivo por consola:** Facilita la interacción con las funcionalidades de la aplicación.

## Tecnologías Utilizadas

*   **Java:** Lenguaje de programación.
*   **Spring Boot:** Framework para el desarrollo de aplicaciones Java basadas en Spring.
*   **Spring Data JPA:** Facilita el acceso a la base de datos.
*   **Hibernate:** ORM (Object-Relational Mapping) para la persistencia de datos.
*   **PostgreSQL:** Base de datos relacional.
*   **Apache HttpClient:** Cliente HTTP para interactuar con la API de Gutendex.
*   **Jackson ObjectMapper:** Librería para el manejo de JSON.
*   **Maven:** Gestor de dependencias.
*   **SLF4j y Logback:** Librerías para el registro de logs.

## Configuración

1.  **Requisitos previos:**
    *   Java JDK 17 o superior.
    *   Maven.
    *   PostgreSQL instalado y configurado.

2.  **Configuración de la base de datos:**
    *   Crea una base de datos en PostgreSQL.
    *   Configura las credenciales de la base de datos en el archivo `application.properties` (o `application.yml`) dentro de `src/main/resources`. Ejemplo:

    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/nombre_de_la_base_de_datos
    spring.datasource.username=usuario
    spring.datasource.password=contraseña
    ```

3.  **Clonar el repositorio:**

    ```bash
    git clone [https://es.wikipedia.org/wiki/Repositorio_%28contenido_digital%29](https://es.wikipedia.org/wiki/Repositorio_%28contenido_digital%29)
    ```

## Ejecución

1.  Navega al directorio del proyecto:

    ```bash
    cd challengerLiteratura
    ```

2.  Compila y ejecuta la aplicación con Maven:

    ```bash
    mvn spring-boot:run
    ```

La aplicación se iniciará y mostrará un menú interactivo en la consola.

## Estructura de Datos

*   **Autor:**
    *   `nombre` (String): Nombre del autor.
    *   `anioNacimiento` (Integer): Año de nacimiento.
    *   `anioFallecimiento` (Integer): Año de fallecimiento.
*   **Libro:**
    *   `titulo` (String): Título del libro.
    *   `idioma` (String): Idioma del libro.
    *   `descargas` (Integer): Número de descargas.
    *   `autor` (Autor): Referencia al autor del libro.

package com.literaturachallenger.challengerLiteratura.repository;

import com.literaturachallenger.challengerLiteratura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    long countByIdioma(String idioma);
    List<Libro> findByIdioma(String idioma);
    Optional<Libro> findByTitulo(String titulo);
}
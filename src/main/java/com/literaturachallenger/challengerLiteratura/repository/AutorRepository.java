package com.literaturachallenger.challengerLiteratura.repository;

import com.literaturachallenger.challengerLiteratura.modelo.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombre(String nombre);
    List<Autor> findByAnioNacimientoLessThanEqualAndAnioFallecimientoGreaterThanEqualOrAnioNacimientoLessThanEqualAndAnioFallecimientoIsNull(Integer anio, Integer anio2, Integer anio3);
}
package org.crue.hercules.sgi.eti.repository;

import java.util.Optional;

import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.repository.custom.CustomPeticionEvaluacionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link PeticionEvaluacion}.
 */

@Repository
public interface PeticionEvaluacionRepository extends JpaRepository<PeticionEvaluacion, Long>,
    JpaSpecificationExecutor<PeticionEvaluacion>, CustomPeticionEvaluacionRepository {

  /**
   * Recupera la última petición evaluación del año recibido por parámetro.
   * 
   * @param anio año a buscar en el código de la petición evaluación.
   * @return petición evaluación.
   */
  PeticionEvaluacion findFirstByCodigoStartingWithOrderByCodigoDesc(String anio);

  /**
   * Devuelve la petición asociada al id que se encuentra activa.
   * 
   * @param id Identificador {@link PeticionEvaluacion}
   * @return {@link PeticionEvaluacion}
   */
  Optional<PeticionEvaluacion> findByIdAndActivoTrue(Long id);

}
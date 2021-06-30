package org.crue.hercules.sgi.eti.repository.custom;

import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link PeticionEvaluacion}.
 */
@Component
public interface CustomPeticionEvaluacionRepository {

  /**
   * Obtiene las peticiones de evaluación filtradas por memoria en las que el
   * usuario es creador de las peticiones de evaluación o responsable de memoria
   * 
   * @param specsMem           Specification {@link Memoria}
   * @param specsPet           Specification {@link PeticionEvaluacion}
   * @param pageable           paginación
   * @param personaRefConsulta usuario
   * @return las entidades {@link PeticionEvaluacion} paginadas y filtradas
   */
  Page<PeticionEvaluacionWithIsEliminable> findAllPeticionEvaluacionMemoria(Specification<Memoria> specsMem,
      Specification<PeticionEvaluacion> specsPet, Pageable pageable, String personaRefConsulta);

}

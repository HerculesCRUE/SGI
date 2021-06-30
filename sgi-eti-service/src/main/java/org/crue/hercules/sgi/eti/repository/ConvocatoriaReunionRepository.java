package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.repository.custom.CustomConvocatoriaReunionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ConvocatoriaReunion}.
 */

@Repository
public interface ConvocatoriaReunionRepository extends JpaRepository<ConvocatoriaReunion, Long>,
    JpaSpecificationExecutor<ConvocatoriaReunion>, CustomConvocatoriaReunionRepository {

  /**
   * Recupera la ultima convocatoria del comite
   * 
   * @param idComite Identificador del comite
   * @return la convocatoria con el numero de acta más alto
   */
  ConvocatoriaReunion findFirstByComiteIdOrderByNumeroActaDesc(Long idComite);

}
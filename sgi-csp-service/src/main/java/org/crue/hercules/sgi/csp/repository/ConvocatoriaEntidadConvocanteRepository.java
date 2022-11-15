package org.crue.hercules.sgi.csp.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.Programa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ConvocatoriaEntidadConvocante}.
 */
@Repository
public interface ConvocatoriaEntidadConvocanteRepository extends JpaRepository<ConvocatoriaEntidadConvocante, Long>,
    JpaSpecificationExecutor<ConvocatoriaEntidadConvocante> {

  /**
   * Busca un {@link ConvocatoriaEntidadConvocante} por su {@link Convocatoria},
   * entidadRef y {@link Programa}.
   * 
   * @param convocatoriaId Id de la {@link Convocatoria}
   * @param entidadRef     Id de la Entidad
   * @param programaId     Id del {@link Programa} de la {@link Convocatoria}
   * @return una {@link ConvocatoriaEntidadConvocante}
   */
  Optional<ConvocatoriaEntidadConvocante> findByConvocatoriaIdAndEntidadRefAndProgramaId(
      Long convocatoriaId, String entidadRef, Long programaId);

  /**
   * Recupera aquellas {@link ConvocatoriaEntidadConvocante} de la
   * {@link Convocatoria}.
   * 
   * @param idConvocatoria Id de la {@link Convocatoria}
   * @return listado de {@link ConvocatoriaEntidadConvocante}
   */
  List<ConvocatoriaEntidadConvocante> findByConvocatoriaId(Long idConvocatoria);

}

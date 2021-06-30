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
   * Busca un {@link ConvocatoriaEntidadConvocante} por su {@link Convocatoria} y
   * entidadRef.
   * 
   * @param convocatoriaId Id de la {@link Convocatoria}
   * @param entidadRef     Id de la Entidad
   * @return una {@link ConvocatoriaEntidadConvocante}
   */
  Optional<ConvocatoriaEntidadConvocante> findByConvocatoriaIdAndEntidadRef(Long convocatoriaId, String entidadRef);

  /**
   * Recupera aquellas {@link ConvocatoriaEntidadConvocante} de la
   * {@link Convocatoria} que tengan {@link Programa} asociado.
   * 
   * @param idConvocatoria Id de la {@link Convocatoria}
   * @return listado de {@link ConvocatoriaEntidadConvocante}
   */
  List<ConvocatoriaEntidadConvocante> findByProgramaIsNotNullAndConvocatoriaId(Long idConvocatoria);

}

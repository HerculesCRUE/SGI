package org.crue.hercules.sgi.csp.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaEntidadGestoraRepository
    extends JpaRepository<ConvocatoriaEntidadGestora, Long>, JpaSpecificationExecutor<ConvocatoriaEntidadGestora> {

  /**
   * Busca un {@link ConvocatoriaEntidadGestora} por su {@link Convocatoria} y
   * entidadRef.
   * 
   * @param convocatoriaId Id de la {@link Convocatoria}
   * @param entidadRef     Id de la Entidad
   * @return una {@link ConvocatoriaEntidadGestora}
   */
  Optional<ConvocatoriaEntidadGestora> findByConvocatoriaIdAndEntidadRef(Long convocatoriaId, String entidadRef);

  /**
   * Busca todas las {@link ConvocatoriaEntidadGestora} por su
   * {@link Convocatoria}
   * 
   * @param convocatoriaId Id de la {@link Convocatoria}
   * @return lista de {@link ConvocatoriaEntidadGestora}
   */
  List<ConvocatoriaEntidadGestora> findAllByConvocatoriaId(Long convocatoriaId);
}

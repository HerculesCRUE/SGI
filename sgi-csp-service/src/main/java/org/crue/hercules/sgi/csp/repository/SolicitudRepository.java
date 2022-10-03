package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long>, JpaSpecificationExecutor<Solicitud> {
  /**
   * Comprueba si hay alguna {@link Solicitud} asociada a la convocatoria
   * 
   * @param convocatoriaId id de la {@link Convocatoria}
   * @return true si existe, false si no existe.
   */
  boolean existsByConvocatoriaIdAndActivoTrue(Long convocatoriaId);

  /**
   * Obtiene el listado de solicitudes activas relacionadas con una
   * {@link Convocatoria}
   * 
   * @param convocatoriaId id de la {@link Convocatoria}
   * @return Listado de {@link Solicitud}
   */
  List<Solicitud> findByConvocatoriaIdAndActivoIsTrue(Long convocatoriaId);
}

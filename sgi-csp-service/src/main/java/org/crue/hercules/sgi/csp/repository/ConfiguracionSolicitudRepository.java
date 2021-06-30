package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConfiguracionSolicitudRepository
    extends JpaRepository<ConfiguracionSolicitud, Long>, JpaSpecificationExecutor<ConfiguracionSolicitud> {

  /**
   * Busca un {@link ConfiguracionSolicitud} por su {@link Convocatoria}.
   * 
   * @param convocatoriaId Id de la {@link Convocatoria}
   * @return una {@link ConfiguracionSolicitud}
   */
  Optional<ConfiguracionSolicitud> findByConvocatoriaId(Long convocatoriaId);

  /**
   * Recupera una lista paginada de configuraciones solicitud a partir del id de
   * la fase de presentación de solicitud.
   * 
   * @param idFase   Identificador {@link ConvocatoriaFase}
   * @param pageable datos de la paginación
   * @return lista paginada
   */
  Page<ConfiguracionSolicitud> findByFasePresentacionSolicitudesId(Long idFase, Pageable pageable);
}

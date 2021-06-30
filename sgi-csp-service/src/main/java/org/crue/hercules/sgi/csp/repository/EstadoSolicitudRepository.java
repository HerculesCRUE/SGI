package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EstadoSolicitudRepository
    extends JpaRepository<EstadoSolicitud, Long>, JpaSpecificationExecutor<EstadoSolicitud> {

  /**
   * Obtiene las {@link EstadoSolicitud} para una {@link Solicitud}.
   *
   * @param idSolicitud el id de la {@link Solicitud}.
   * @param paging      la información de la paginación.
   * @return la lista de entidades {@link EstadoSolicitud} de la {@link Solicitud}
   *         paginadas.
   */
  Page<EstadoSolicitud> findAllBySolicitudId(Long idSolicitud, Pageable paging);

}

package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudHitoRepository
    extends JpaRepository<SolicitudHito, Long>, JpaSpecificationExecutor<SolicitudHito> {

  /**
   * Busca un {@link SolicitudHito} por su {@link Solicitud}, {@link TipoHito} y
   * fecha.
   * 
   * @param solicitudId Id de la Solicitud de la {@link SolicitudHito}
   * @param fecha       fecha de la {@link SolicitudHito}
   * @param tipoHitoId  Id de la {@link TipoHito}
   * @return un {@link SolicitudHito}
   */
  Optional<SolicitudHito> findBySolicitudIdAndFechaAndTipoHitoId(Long solicitudId, Instant fecha, Long tipoHitoId);

}

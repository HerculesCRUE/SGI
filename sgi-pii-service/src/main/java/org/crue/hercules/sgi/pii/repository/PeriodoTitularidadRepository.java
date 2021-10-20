package org.crue.hercules.sgi.pii.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.pii.dto.SolicitudProteccionOutput.Invencion;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link PeriodoTitularidad}.
 */
@Repository
public interface PeriodoTitularidadRepository
    extends JpaRepository<PeriodoTitularidad, Long>, JpaSpecificationExecutor<PeriodoTitularidad> {

  /**
   * Obtiene la entidad {@link PeriodoTitularidad} que se encuentre activa.
   *
   * @param invencionId id de la {@link Invencion} a la que pertencen los
   *                    {@link PeriodoTitularidad}.
   * @return el {@link PeriodoTitularidad} activo
   */
  Optional<PeriodoTitularidad> findByInvencionIdAndFechaFinIsNull(Long invencionId);

  /**
   * Obtiene las entidades {@link PeriodoTitularidad} asociadas a la
   * {@link Invencion} pasada por parámetros.
   * 
   * @param invencionId id de la {@link Invencion}
   * @return listado de {@link PeriodoTitularidad}
   */
  List<PeriodoTitularidad> findAllByInvencionId(Long invencionId);

  /**
   * Obtiene la entidad {@link PeriodoTitularidad} que se finalize luego de la
   * fecha pasada por parametros.
   * 
   * @param invencionId id de la {@link Invencion}
   * @param date
   * @return el {@link PeriodoTitularidad}
   */
  Optional<PeriodoTitularidad> findByInvencionIdAndFechaFinGreaterThanEqual(Long invencionId, Instant date);

}

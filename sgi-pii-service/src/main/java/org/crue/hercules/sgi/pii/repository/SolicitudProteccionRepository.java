package org.crue.hercules.sgi.pii.repository;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.repository.custom.CustomSolicitudProteccionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProteccionRepository
    extends JpaRepository<SolicitudProteccion, Long>, JpaSpecificationExecutor<SolicitudProteccion>,
    CustomSolicitudProteccionRepository {

  Page<SolicitudProteccion> findByInvencionId(Long invencionId, Pageable paging);

  /**
   * Obtiene una lista de objetos de tipo @link{SolicitudProteccion} cuya
   * fechaFinPrioridadPresFasNacRec (Fecha Fin Prioridad / Presentación / Fases
   * Nacionales / Reguionales)
   * esté entre las fechas recibidas por parámetro y no sean de tipo PCT, esto es
   * cuando la propiedad extension internacional de la tabla via proteccion es
   * true
   * 
   * @param fechaFinPrioridadFrom fecha desde
   * @param fechaFinPrioridadTo   fecha hasta
   * @return Lista de @link{SolicitudProteccion}
   */
  List<SolicitudProteccion> findByfechaFinPriorPresFasNacRecBetweenAndViaProteccionExtensionInternacionalTrue(
      Instant fechaFinPrioridadFrom,
      Instant fechaFinPrioridadTo);

  /**
   * Obtiene una lista de objetos de tipo @link{SolicitudProteccion} cuya
   * fechaFinPrioridadPresFasNacRec (Fecha Fin Prioridad / Presentación / Fases
   * Nacionales / Reguionales)
   * esté entre las fechas recibidas por parámetro y sean de tipo PCT, esto es
   * cuando la propiedad extension internacional de la tabla via proteccion es
   * true
   * 
   * @param fechaFinPrioridadFrom fecha desde
   * @param fechaFinPrioridadTo   fecha hasta
   * @return Lista de @link{SolicitudProteccion}
   */
  List<SolicitudProteccion> findByfechaFinPriorPresFasNacRecBetweenAndViaProteccionExtensionInternacionalFalse(
      Instant fechaFinPrioridadFrom,
      Instant fechaFinPrioridadTo);
}

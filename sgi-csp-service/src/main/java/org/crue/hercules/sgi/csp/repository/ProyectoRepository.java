package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.custom.CustomProyectoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoRepository
    extends JpaRepository<Proyecto, Long>, JpaSpecificationExecutor<Proyecto>, CustomProyectoRepository {

  /**
   * Comprueba si el rango de fechas proporcinado está dentro del periodo del
   * {@link Proyecto}.
   * 
   * @param proyectoId  id del {@link Proyecto}.
   * @param fechaInicio fecha de inicio del rango de fechas a comprobar.
   * @param fechaFin    fecha de fin del rango de fechas a comprobar.
   * @return true si está dentro de rango, false si no lo está.
   */
  boolean existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(Long proyectoId, Instant fechaInicio,
      Instant fechaFin);

  /**
   * Comprueba si existe alguna {@link Solicitud} asociada a algún
   * {@link Proyecto}.
   * 
   * @param solicitudId id del {@link Solicitud}.
   * @return true si existe, false si no existe.
   */
  boolean existsBySolicitudId(Long solicitudId);

}

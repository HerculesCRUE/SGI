package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Instant;
import java.util.List;

public interface ProyectoPeriodoSeguimientoRepository
    extends JpaRepository<ProyectoPeriodoSeguimiento, Long>, JpaSpecificationExecutor<ProyectoPeriodoSeguimiento> {

  /**
   * Recupera un listado de ProyectoPeriodoSeguimiento ordenado por su fecha de
   * inicio direccion ascendente
   * 
   * @param proyectoId Id de {@link Proyecto}
   * @return proyectoPeriodosSeguimiento List
   */
  List<ProyectoPeriodoSeguimiento> findByProyectoIdOrderByFechaInicio(Long proyectoId);

  List<ProyectoPeriodoSeguimiento> findByFechaFinPresentacionBetweenAndProyectoActivoTrue(Instant dateFrom,
      Instant dateTo);

  List<ProyectoPeriodoSeguimiento> findByFechaInicioPresentacionBetweenAndProyectoActivoTrue(Instant dateFrom,
      Instant dateTo);
}

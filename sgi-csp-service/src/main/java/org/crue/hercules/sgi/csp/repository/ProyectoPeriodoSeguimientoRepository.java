package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoPeriodoSeguimientoRepository
    extends JpaRepository<ProyectoPeriodoSeguimiento, Long>, JpaSpecificationExecutor<ProyectoPeriodoSeguimiento> {

  /**
   * Recupera un listado de ProyectoPeriodoSeguimiento ordenado por su fecha de inicio direccion ascendente
   * @param proyectoId Id de {@link Proyecto}
   * @return proyectoPeriodosSeguimiento List
   */
  List<ProyectoPeriodoSeguimiento> findByProyectoIdOrderByFechaInicio(Long proyectoId);

}

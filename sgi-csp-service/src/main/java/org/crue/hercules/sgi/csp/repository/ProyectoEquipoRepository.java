package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoEquipoRepository
    extends JpaRepository<ProyectoEquipo, Long>, JpaSpecificationExecutor<ProyectoEquipo> {

  /**
   * Devuelve un listado de {@link ProyectoEquipo} asociados a un
   * {@link Proyecto}.
   * 
   * @param proyectoId Identificador de {@link Proyecto}.
   * @return listado de {@link ProyectoEquipo}.
   */
  List<ProyectoEquipo> findAllByProyectoId(Long proyectoId);

  /**
   * Devuelve un listado de {@link ProyectoEquipo} asociados a un {@link Proyecto}
   * y una fecha de fin
   * 
   * @param proyectoId Identificador de {@link Proyecto}.
   * @param fechaFin   Fecha de fin del miembro de equipo
   * @return listado de {@link ProyectoEquipo}.
   */
  List<ProyectoEquipo> findAllByProyectoIdAndFechaFin(Long proyectoId, Instant fechaFin);

  /**
   * Devuelve un listado de {@link ProyectoEquipo} asociados a un {@link Proyecto}
   * y una fecha de fin mayor a la indicada
   * 
   * @param proyectoId Identificador de {@link Proyecto}.
   * @param fechaFin   Fecha de fin del miembro de equipo
   * @return listado de {@link ProyectoEquipo}.
   */
  List<ProyectoEquipo> findAllByProyectoIdAndFechaFinGreaterThan(Long proyectoId, Instant fechaFin);

}

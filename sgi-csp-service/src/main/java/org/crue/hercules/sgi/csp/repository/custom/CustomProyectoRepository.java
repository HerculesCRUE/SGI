package org.crue.hercules.sgi.csp.repository.custom;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.dto.ProyectoDto;
import org.crue.hercules.sgi.csp.dto.ProyectoPresupuestoTotales;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Proyecto}.
 */
@Component
public interface CustomProyectoRepository {

  /**
   * Obtiene el {@link ModeloEjecucion} asignada al {@link Proyecto}.
   *
   * @param id Id de la {@link Proyecto}.
   * @return {@link ModeloEjecucion} asignado
   */
  Optional<ModeloEjecucion> getModeloEjecucion(Long id);

  /**
   * Indica si en el {@link Proyecto} se permiten {@link ProyectoPaqueteTrabajo}.
   *
   * @param id Id de la {@link Proyecto}.
   * @return true si se permiten {@link ProyectoPaqueteTrabajo}, false si no se
   *         permiten {@link ProyectoPaqueteTrabajo}
   */
  Optional<Boolean> getPermitePaquetesTrabajo(Long id);

  /**
   * Obtiene el {@link ProyectoPresupuestoTotales} de la {@link Proyecto}.
   * 
   * @param proyectoId Id de la {@link Proyecto}.
   * @return {@link ProyectoPresupuestoTotales}.
   */
  ProyectoPresupuestoTotales getTotales(Long proyectoId);

  /**
   * * Obtiene los ids de proyectos que cumplen con la specification recibida.
   * 
   * @param specification condiciones que deben cumplir.
   * @return lista de ids de {@link Proyecto}.
   */
  List<Long> findIds(Specification<Proyecto> specification);

  /**
   * Devuelve una lista de {@link ProyectoDto} que se incorporarán a la baremación
   * de producción científica
   * 
   * @param fechaInicioBaremacion fecha inicio de baremación
   * @param fechaFinBaremacion    fecha fin de baremación
   * 
   * @return Lista de {@link ProyectoDto}
   */
  List<ProyectoDto> findProyectosProduccionCientifica(Instant fechaInicioBaremacion,
      Instant fechaFinBaremacion);
}

package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Instant;
import java.util.List;

public interface ProyectoSocioPeriodoPagoRepository
    extends JpaRepository<ProyectoSocioPeriodoPago, Long>, JpaSpecificationExecutor<ProyectoSocioPeriodoPago> {

  /**
   * Recupera todos los {@link ProyectoSocioPeriodoPago} asociados al
   * {@link ProyectoSocio} cuyo id se recibe por parámetro.
   * 
   * @param proyectoSocioId Identificador de {@link ProyectoSocio}
   * @return listado de {@link ProyectoSocioPeriodoPago}
   */
  List<ProyectoSocioPeriodoPago> findAllByProyectoSocioId(Long proyectoSocioId);

  /**
   * Elimina todos los {@link ProyectoSocioPeriodoPago} asociados a un
   * {@link ProyectoSocio}.
   * 
   * @param id Identificador de {@link ProyectoSocio}.
   */
  void deleteByProyectoSocioId(Long id);

  boolean existsByProyectoSocioId(Long proyectoSocioId);

  List<ProyectoSocioPeriodoPago> findByFechaPrevistaPagoBetweenAndFechaPagoIsNullAndProyectoSocioProyectoActivoTrue(
      Instant dateFrom, Instant dateTo);
}

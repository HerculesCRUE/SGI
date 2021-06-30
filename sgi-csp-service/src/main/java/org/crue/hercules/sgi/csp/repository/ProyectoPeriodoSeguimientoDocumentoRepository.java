package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoPeriodoSeguimientoDocumentoRepository
    extends JpaRepository<ProyectoPeriodoSeguimientoDocumento, Long>,
    JpaSpecificationExecutor<ProyectoPeriodoSeguimientoDocumento> {

  /**
   * Comprueba la existencia del {@link ProyectoPeriodoSeguimientoDocumento} por
   * id de {@link ProyectoPeriodoSeguimiento}
   *
   * @param id el id de la entidad {@link ProyectoPeriodoSeguimiento}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsByProyectoPeriodoSeguimientoId(Long id);

  /**
   * Se eliminan todos los {@link ProyectoPeriodoSeguimientoDocumento} asociados
   * al id de {@link ProyectoPeriodoSeguimiento} recibido por parámetro.
   * 
   * @param id el id de la entidad {@link ProyectoPeriodoSeguimiento}
   */
  void deleteByProyectoPeriodoSeguimientoId(Long id);

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoPeriodoSeguimientoDocumento}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsByProyectoPeriodoSeguimientoProyectoId(Long proyectoId);
}

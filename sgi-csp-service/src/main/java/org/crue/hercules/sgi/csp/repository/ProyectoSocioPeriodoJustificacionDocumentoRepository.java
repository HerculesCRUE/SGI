package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoSocioPeriodoJustificacionDocumentoRepository
    extends JpaRepository<ProyectoSocioPeriodoJustificacionDocumento, Long>,
    JpaSpecificationExecutor<ProyectoSocioPeriodoJustificacionDocumento> {

  /**
   * Recupera todos los {@link ProyectoSocioPeriodoJustificacionDocumento}
   * asociados a un {@link ProyectoSocioPeriodoJustificacion}.
   * 
   * @param proyectoSocioPeriodoJustificacionId Identificador de
   *                                            {@link ProyectoSocioPeriodoJustificacion}.
   * @return listado de {@link ProyectoSocioPeriodoJustificacionDocumento}
   */
  List<ProyectoSocioPeriodoJustificacionDocumento> findAllByProyectoSocioPeriodoJustificacionId(
      Long proyectoSocioPeriodoJustificacionId);

  /**
   * Elimina los {@link ProyectoSocioPeriodoJustificacionDocumento} asociados a
   * los {@link ProyectoSocioPeriodoJustificacion}.
   * 
   * @param periodoJustificacionId Lista de identificadores de
   *                               {@link ProyectoSocioPeriodoJustificacion}.
   */
  void deleteByProyectoSocioPeriodoJustificacionIdIn(List<Long> periodoJustificacionId);

  /**
   * Elimina todos los {@link ProyectoSocioPeriodoJustificacionDocumento}
   * asociados a un {@link ProyectoSocio}.
   * 
   * @param id Identificador de {@link ProyectoSocio}.
   */
  void deleteByProyectoSocioPeriodoJustificacionProyectoSocioId(Long id);

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoSocioPeriodoJustificacionDocumento}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsByProyectoSocioPeriodoJustificacionProyectoSocioProyectoId(Long proyectoId);
}

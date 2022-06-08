package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion.Estado;
import org.crue.hercules.sgi.csp.repository.custom.CustomAutorizacionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AutorizacionRepository
    extends JpaRepository<Autorizacion, Long>, JpaSpecificationExecutor<Autorizacion>, CustomAutorizacionRepository {

  /**
   * Comprueba si existe el {@link Autorizacion} con el solicitanteRef
   * 
   * @param id             id del {@link Autorizacion}.
   * @param solicitanteRef identificador del solicitante.
   * @return <code>true</code> Si existe, <code>false</code> en cualquier otro
   *         caso.
   */
  boolean existsByIdAndSolicitanteRef(Long id, String solicitanteRef);

  /**
   * Comprueba si existe el {@link Autorizacion} con el estado indicado
   * 
   * @param id     id del {@link Autorizacion}.
   * @param estado estado de la {@link Autorizacion}
   * @return <code>true</code> Si existe, <code>false</code> en cualquier otro
   *         caso.
   */
  boolean existsByIdAndEstadoEstado(Long id, Estado estado);

}

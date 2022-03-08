package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EstadoAutorizacionRepository
    extends JpaRepository<EstadoAutorizacion, Long>, JpaSpecificationExecutor<EstadoAutorizacion> {

  /**
   * Obtiene los {@link EstadoAutorizacion} para una {@link Autorizacion}.
   *
   * @param autorizacionId el id de la {@link Autorizacion}.
   * @param paging         la información de la paginación.
   * @return la lista de entidades {@link EstadoAutorizacion} de la
   *         {@link Autorizacion}
   *         paginadas.
   */
  Page<EstadoAutorizacion> findAllByAutorizacionId(Long autorizacionId, Pageable paging);

  /**
   * Obtiene el primer estado de la Autorizaicon
   * 
   * @param autorizacionId id de la Autorizacion
   * @return El primer estado de la autorizacion
   */
  List<EstadoAutorizacion> findFirstByAutorizacionIdOrderByIdAsc(Long autorizacionId);

  void deleteByAutorizacionId(Long autorizacionId);

}
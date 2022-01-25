package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.crue.hercules.sgi.csp.repository.NotificacionProyectoExternoCVNRepository;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de
 * {@link NotificacionProyectoExternoCVN}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class NotificacionProyectoExternoCVNService {

  private final NotificacionProyectoExternoCVNRepository repository;

  public NotificacionProyectoExternoCVNService(
      NotificacionProyectoExternoCVNRepository notificacionProyectoExternoCVNRepository) {
    this.repository = notificacionProyectoExternoCVNRepository;
  }

  /**
   * Obtener todas las entidades {@link NotificacionProyectoExternoCVN} paginadas
   * y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link NotificacionProyectoExternoCVN}
   *         paginadas y/o filtradas.
   */
  public Page<NotificacionProyectoExternoCVN> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<NotificacionProyectoExternoCVN> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<NotificacionProyectoExternoCVN> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Comprueba si existen datos vinculados a {@link Autorizacion} de
   * {@link NotificacionProyectoExternoCVN}
   *
   * @param autorizacionId Id del {@link Autorizacion}.
   * @return si existe o no el Autorizacion
   */
  public boolean existsByAutorizacionId(Long autorizacionId) {
    return repository.existsByAutorizacionId(autorizacionId);
  }
}

package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.exceptions.CertificadoAutorizacionNotFoundException;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.CertificadoAutorizacion;
import org.crue.hercules.sgi.csp.repository.CertificadoAutorizacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.CertificadoAutorizacionSpecifications;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link CertificadoAutorizacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class CertificadoAutorizacionService {

  private final CertificadoAutorizacionRepository repository;
  private final CertificadoAutorizacionComService certificadoAutorizacionComService;

  public CertificadoAutorizacionService(
      final CertificadoAutorizacionRepository repository,
      final CertificadoAutorizacionComService certificadoAutorizacionComService) {

    this.repository = repository;
    this.certificadoAutorizacionComService = certificadoAutorizacionComService;
  }

  /**
   * Guarda la entidad {@link CertificadoAutorizacion}.
   * 
   * @param certificadoAutorizacion la entidad {@link CertificadoAutorizacion} a
   *                                guardar.
   * @return la entidad {@link CertificadoAutorizacion} persistida.
   */
  @Transactional
  public CertificadoAutorizacion create(CertificadoAutorizacion certificadoAutorizacion) {
    log.debug("create(Autorizacion autorizacion) - start");

    Assert.isNull(certificadoAutorizacion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(CertificadoAutorizacion.class)).build());

    CertificadoAutorizacion returnValue = repository.save(certificadoAutorizacion);

    if (certificadoAutorizacion.getVisible().booleanValue()) {
      this.certificadoAutorizacionComService
          .enviarComunicadoAddModificarCertificadoAutorizacionParticipacionProyectoExterno(returnValue);
    }

    log.debug("create(Autorizacion autorizacion) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link CertificadoAutorizacion}.
   *
   * @param certificadoActualizar autorizacionActualizar
   *                              {@link CertificadoAutorizacion} con
   *                              los datos actualizados.
   * @return {@link CertificadoAutorizacion} actualizado.
   */
  @Transactional
  public CertificadoAutorizacion update(CertificadoAutorizacion certificadoActualizar) {
    log.debug("update(Autorizacion autorizacionActualizar- start");
    return repository.findById(certificadoActualizar.getId()).map(data -> {

      data.setDocumentoRef(certificadoActualizar.getDocumentoRef());
      data.setVisible(certificadoActualizar.getVisible());
      data.setNombre(certificadoActualizar.getNombre());

      CertificadoAutorizacion returnValue = repository.save(data);

      if (certificadoActualizar.getVisible().booleanValue()) {
        this.certificadoAutorizacionComService
            .enviarComunicadoAddModificarCertificadoAutorizacionParticipacionProyectoExterno(returnValue);
      }
      log.debug("update(Autorizacion autorizacionActualizar - end");
      return returnValue;
    }).orElseThrow(() -> new CertificadoAutorizacionNotFoundException(certificadoActualizar.getId()));
  }

  /**
   * Elimina la {@link CertificadoAutorizacion}.
   *
   * @param id Id del {@link CertificadoAutorizacion}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(CertificadoAutorizacion.class)).build());

    if (!repository.existsById(id)) {
      throw new CertificadoAutorizacionNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene los {@link CertificadoAutorizacion} para una {@link Autorizacion}.
   *
   * @param autorizacionId el id de la {@link Autorizacion}.
   * @param paging         la información de la paginación.
   * @return la lista de entidades {@link CertificadoAutorizacion} de la
   *         {@link Autorizacion}
   *         paginadas.
   */
  public Page<CertificadoAutorizacion> findAllByAutorizacion(Long autorizacionId, Pageable paging) {
    log.debug("findAllByAutorizacion(Long autorizacionId, Pageable paging) - start");

    Page<CertificadoAutorizacion> returnValue = repository.findAllByAutorizacionId(autorizacionId, paging);
    log.debug("findAllByAutorizacion(Long autorizacionId, Pageable paging) - end");
    return returnValue;
  }

  public boolean hasCertificadoAutorizacionVisible(Long autorizacionId) {
    log.debug(
        "hasCertificadoAutorizacionVisible(Long autorizacionId)- start");

    Specification<CertificadoAutorizacion> specs = CertificadoAutorizacionSpecifications
        .byAutorizacionId(
            autorizacionId)
        .and(CertificadoAutorizacionSpecifications.visibles());

    boolean returnValue = repository.count(specs) > 0;
    log.debug(
        "hasCertificadoAutorizacionVisible(Long autorizacionId) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link CertificadoAutorizacion} visible de la
   * {@link Autorizacion}
   * 
   * @param autorizacionId id de la autorizacion
   * @return {@link CertificadoAutorizacion} visible de la {@link Autorizacion}
   */
  public CertificadoAutorizacion findCertificadoAutorizacionVisible(Long autorizacionId) {
    log.debug("findCertificadoAutorizacionVisible(Long autorizacionId)- start");

    Specification<CertificadoAutorizacion> specs = CertificadoAutorizacionSpecifications
        .byAutorizacionId(
            autorizacionId)
        .and(CertificadoAutorizacionSpecifications.visibles());

    CertificadoAutorizacion returnValue = repository.findOne(specs).orElse(null);
    log.debug("findCertificadoAutorizacionVisible(Long autorizacionId) - end");
    return returnValue;

  }

}

package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.SolicitudRrhhRequisitoCategoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudRrhh;
import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoCategoria;
import org.crue.hercules.sgi.csp.repository.SolicitudRrhhRequisitoCategoriaRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudRrhhRequisitoCategoriaSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión de {@link SolicitudRrhhRequisitoCategoria}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class SolicitudRrhhRequisitoCategoriaService {

  private final SolicitudRrhhRequisitoCategoriaRepository repository;
  private final SolicitudAuthorityHelper authorityHelper;

  /**
   * Guarda la entidad {@link SolicitudRrhhRequisitoCategoria}.
   * 
   * @param requisitoCategoria la entidad {@link SolicitudRrhhRequisitoCategoria}
   *                           a guardar.
   * @return la entidad {@link SolicitudRrhhRequisitoCategoria} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public SolicitudRrhhRequisitoCategoria create(@Valid SolicitudRrhhRequisitoCategoria requisitoCategoria) {
    log.debug("create(SolicitudRrhhRequisitoCategoria requisitoCategoria) - start");

    AssertHelper.idIsNull(requisitoCategoria.getId(), SolicitudRrhh.class);
    authorityHelper.checkUserHasAuthorityModifySolicitud(requisitoCategoria.getSolicitudRrhhId());

    SolicitudRrhhRequisitoCategoria returnValue = repository.save(requisitoCategoria);

    log.debug("create(SolicitudRrhhRequisitoCategoria requisitoCategoria) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link SolicitudRrhhRequisitoCategoria}.
   *
   * @param id Id del {@link SolicitudRrhhRequisitoCategoria}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, SolicitudRrhhRequisitoCategoria.class);

    Optional<SolicitudRrhhRequisitoCategoria> requisitoCategoria = repository.findById(id);

    if (requisitoCategoria.isPresent()) {
      authorityHelper.checkUserHasAuthorityModifySolicitud(requisitoCategoria.get().getSolicitudRrhhId());
    } else {
      throw new SolicitudRrhhRequisitoCategoriaNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtener todas las entidades {@link SolicitudRrhhRequisitoCategoria} paginadas
   * y/o filtradas de la {@link SolicitudRrhh}.
   *
   * @param solicitudId Identificador de la entidad {@link SolicitudRrhh}.
   * @param paging      la información de la paginación.
   * @param query       la información del filtro.
   * @return la lista de entidades {@link SolicitudRrhhRequisitoCategoria}
   *         paginadas y/o filtradas.
   */
  public Page<SolicitudRrhhRequisitoCategoria> findAllBySolicitud(Long solicitudId, String query, Pageable paging) {
    log.debug("findAll(Long solicitudId, String query, Pageable paging) - start");
    AssertHelper.idNotNull(solicitudId, Solicitud.class);
    authorityHelper.checkUserHasAuthorityViewSolicitud(solicitudId);

    Specification<SolicitudRrhhRequisitoCategoria> specs = SolicitudRrhhRequisitoCategoriaSpecifications
        .bySolicitudRrhhId(solicitudId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudRrhhRequisitoCategoria> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(Long solicitudId, String query, Pageable paging) - end");
    return returnValue;
  }

}

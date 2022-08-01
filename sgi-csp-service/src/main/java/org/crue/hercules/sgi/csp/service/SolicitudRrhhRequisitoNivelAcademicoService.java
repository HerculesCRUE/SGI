package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.SolicitudRrhhRequisitoNivelAcademicoNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudRrhh;
import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoNivelAcademico;
import org.crue.hercules.sgi.csp.repository.SolicitudRrhhRequisitoNivelAcademicoRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudRrhhRequisitoNivelAcademicoSpecifications;
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
 * Service para la gestión de {@link SolicitudRrhhRequisitoNivelAcademico}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class SolicitudRrhhRequisitoNivelAcademicoService {

  private final SolicitudRrhhRequisitoNivelAcademicoRepository repository;
  private final SolicitudAuthorityHelper authorityHelper;

  /**
   * Guarda la entidad {@link SolicitudRrhhRequisitoNivelAcademico}.
   * 
   * @param requisitoNivelAcademico la entidad
   *                                {@link SolicitudRrhhRequisitoNivelAcademico}
   *                                a guardar.
   * @return la entidad {@link SolicitudRrhhRequisitoNivelAcademico} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public SolicitudRrhhRequisitoNivelAcademico create(
      @Valid SolicitudRrhhRequisitoNivelAcademico requisitoNivelAcademico) {
    log.debug("create(SolicitudRrhhRequisitoNivelAcademico requisitoNivelAcademico) - start");

    AssertHelper.idIsNull(requisitoNivelAcademico.getId(), SolicitudRrhh.class);
    authorityHelper.checkUserHasAuthorityModifySolicitud(requisitoNivelAcademico.getSolicitudRrhhId());

    SolicitudRrhhRequisitoNivelAcademico returnValue = repository.save(requisitoNivelAcademico);

    log.debug("create(SolicitudRrhhRequisitoNivelAcademico requisitoNivelAcademico) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link SolicitudRrhhRequisitoNivelAcademico}.
   *
   * @param id Id del {@link SolicitudRrhhRequisitoNivelAcademico}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, SolicitudRrhhRequisitoNivelAcademico.class);

    Optional<SolicitudRrhhRequisitoNivelAcademico> requisitoNivelAcademico = repository.findById(id);

    if (requisitoNivelAcademico.isPresent()) {
      authorityHelper.checkUserHasAuthorityModifySolicitud(requisitoNivelAcademico.get().getSolicitudRrhhId());
    } else {
      throw new SolicitudRrhhRequisitoNivelAcademicoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtener todas las entidades {@link SolicitudRrhhRequisitoNivelAcademico}
   * paginadas
   * y/o filtradas de la {@link SolicitudRrhh}.
   *
   * @param solicitudId Identificador de la entidad {@link SolicitudRrhh}.
   * @param paging      la información de la paginación.
   * @param query       la información del filtro.
   * @return la lista de entidades {@link SolicitudRrhhRequisitoNivelAcademico}
   *         paginadas y/o filtradas.
   */
  public Page<SolicitudRrhhRequisitoNivelAcademico> findAllBySolicitud(Long solicitudId, String query,
      Pageable paging) {
    log.debug("findAll(Long solicitudId, String query, Pageable paging) - start");
    AssertHelper.idNotNull(solicitudId, Solicitud.class);
    authorityHelper.checkUserHasAuthorityViewSolicitud(solicitudId);

    Specification<SolicitudRrhhRequisitoNivelAcademico> specs = SolicitudRrhhRequisitoNivelAcademicoSpecifications
        .bySolicitudRrhhId(solicitudId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudRrhhRequisitoNivelAcademico> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(Long solicitudId, String query, Pageable paging) - end");
    return returnValue;
  }

}

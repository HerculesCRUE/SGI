package org.crue.hercules.sgi.eer.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.eer.exceptions.EmpresaComposicionSociedadNotFoundException;
import org.crue.hercules.sgi.eer.exceptions.EmpresaComposicionSociedadParticipacionException;
import org.crue.hercules.sgi.eer.exceptions.EmpresaNotFoundException;
import org.crue.hercules.sgi.eer.model.BaseEntity;
import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad;
import org.crue.hercules.sgi.eer.repository.EmpresaComposicionSociedadRepository;
import org.crue.hercules.sgi.eer.repository.EmpresaRepository;
import org.crue.hercules.sgi.eer.repository.specification.EmpresaComposicionSociedadSpecifications;
import org.crue.hercules.sgi.eer.util.AssertHelper;
import org.crue.hercules.sgi.eer.util.EmpresaAuthorityHelper;
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
 * Service para la gestión de {@link EmpresaComposicionSociedad}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class EmpresaComposicionSociedadService {

  private Integer participacion;

  private final EmpresaComposicionSociedadRepository repository;
  private final EmpresaRepository empresaRepository;
  private final Validator validator;
  private final EmpresaAuthorityHelper authorityHelper;

  /**
   * Obtiene una entidad {@link EmpresaComposicionSociedad} por id.
   * 
   * @param id Identificador de la entidad {@link EmpresaComposicionSociedad}.
   * @return la entidad {@link EmpresaComposicionSociedad}.
   */
  public EmpresaComposicionSociedad findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, EmpresaComposicionSociedad.class);
    final EmpresaComposicionSociedad returnValue = repository.findById(id)
        .orElseThrow(() -> new EmpresaComposicionSociedadNotFoundException(id));

    authorityHelper.checkUserHasAuthorityViewEmpresa(returnValue.getEmpresaId());

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link EmpresaComposicionSociedad} paginadas y/o
   * filtradas del
   * {@link Empresa}.
   *
   * @param empresaId Identificador de la entidad {@link Empresa}.
   * @param paging    la información de la paginación.
   * @param query     la información del filtro.
   * @return la lista de entidades {@link EmpresaComposicionSociedad} paginadas
   *         y/o
   *         filtradas.
   */
  public Page<EmpresaComposicionSociedad> findAllByEmpresa(Long empresaId, String query, Pageable paging) {
    log.debug("findAll(Long empresaId, String query, Pageable paging) - start");
    AssertHelper.idNotNull(empresaId, Empresa.class);
    authorityHelper.checkUserHasAuthorityViewEmpresa(empresaId);

    Specification<EmpresaComposicionSociedad> specs = EmpresaComposicionSociedadSpecifications.byEmpresaId(empresaId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<EmpresaComposicionSociedad> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(Long empresaId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link EmpresaComposicionSociedad} de la
   * {@link Empresa} con el
   * listado empresaComposicionSociedades añadiendo, editando o eliminando los
   * elementos segun
   * proceda.
   *
   * @param empresaId                    Id de la {@link Empresa}.
   * @param empresaComposicionSociedades lista con los nuevos
   *                                     {@link EmpresaComposicionSociedad} a
   *                                     guardar.
   * @return la entidad {@link EmpresaComposicionSociedad} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public List<EmpresaComposicionSociedad> update(Long empresaId,
      @Valid List<EmpresaComposicionSociedad> empresaComposicionSociedades) {
    log.debug("update(Long empresaId, List<EmpresaComposicionSociedad> empresaComposicionSociedades) - start");

    AssertHelper.idNotNull(empresaId, Empresa.class);
    authorityHelper.checkUserHasAuthorityViewEmpresa(empresaId);

    if (!empresaRepository.existsById(empresaId)) {
      throw new EmpresaNotFoundException(empresaId);
    }

    List<EmpresaComposicionSociedad> empresaComposicionSociedadesBD = repository.findAllByEmpresaId(empresaId);

    // Empresa composicion sociedades eliminados
    List<EmpresaComposicionSociedad> empresaComposicionSociedadesEliminar = empresaComposicionSociedadesBD.stream()
        .filter(
            empresaComposicionSociedad -> empresaComposicionSociedades.stream().map(EmpresaComposicionSociedad::getId)
                .noneMatch(id -> Objects.equals(id, empresaComposicionSociedad.getId())))
        .collect(Collectors.toList());

    if (!empresaComposicionSociedadesEliminar.isEmpty()) {
      repository.deleteAll(empresaComposicionSociedadesEliminar);
    }

    this.validateEmpresaComposicionSociedad(empresaComposicionSociedades);

    List<EmpresaComposicionSociedad> returnValue = repository.saveAll(empresaComposicionSociedades);
    log.debug("update(Long empresaId, List<EmpresaComposicionSociedad> empresaComposicionSociedades) - END");

    return returnValue;
  }

  private void validateEmpresaComposicionSociedad(List<EmpresaComposicionSociedad> empresaComposicionSociedades) {
    if (!empresaComposicionSociedades.isEmpty()) {
      participacion = 0;
      empresaComposicionSociedades.forEach(sociedad -> {
        if (sociedad.getParticipacion().intValue() > 0 && sociedad.getParticipacion().intValue() <= 100) {
          participacion += sociedad.getParticipacion().intValue();
        } else {
          throw new EmpresaComposicionSociedadParticipacionException();
        }
      });

      if (participacion > 100) {
        throw new EmpresaComposicionSociedadParticipacionException();
      }
    }
  }

}

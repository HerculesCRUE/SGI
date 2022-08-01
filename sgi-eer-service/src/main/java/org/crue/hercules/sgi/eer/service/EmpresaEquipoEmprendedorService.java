package org.crue.hercules.sgi.eer.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.eer.exceptions.EmpresaEquipoEmprendedorNotFoundException;
import org.crue.hercules.sgi.eer.exceptions.EmpresaEquipoEmprendedorUniqueException;
import org.crue.hercules.sgi.eer.exceptions.EmpresaNotFoundException;
import org.crue.hercules.sgi.eer.model.BaseEntity;
import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaEquipoEmprendedor;
import org.crue.hercules.sgi.eer.repository.EmpresaEquipoEmprendedorRepository;
import org.crue.hercules.sgi.eer.repository.EmpresaRepository;
import org.crue.hercules.sgi.eer.repository.specification.EmpresaEquipoEmprendedorSpecifications;
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
 * Service para la gestión de {@link EmpresaEquipoEmprendedor}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class EmpresaEquipoEmprendedorService {

  private final EmpresaEquipoEmprendedorRepository repository;
  private final EmpresaRepository empresaRepository;
  private final Validator validator;
  private final EmpresaAuthorityHelper authorityHelper;

  /**
   * Obtiene una entidad {@link EmpresaEquipoEmprendedor} por id.
   * 
   * @param id Identificador de la entidad {@link EmpresaEquipoEmprendedor}.
   * @return la entidad {@link EmpresaEquipoEmprendedor}.
   */
  public EmpresaEquipoEmprendedor findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, EmpresaEquipoEmprendedor.class);
    final EmpresaEquipoEmprendedor returnValue = repository.findById(id)
        .orElseThrow(() -> new EmpresaEquipoEmprendedorNotFoundException(id));

    authorityHelper.checkUserHasAuthorityViewEmpresa(returnValue.getEmpresaId());

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link EmpresaEquipoEmprendedor} paginadas y/o
   * filtradas del
   * {@link Empresa}.
   *
   * @param empresaId Identificador de la entidad {@link Empresa}.
   * @param paging    la información de la paginación.
   * @param query     la información del filtro.
   * @return la lista de entidades {@link EmpresaEquipoEmprendedor} paginadas y/o
   *         filtradas.
   */
  public Page<EmpresaEquipoEmprendedor> findAllByEmpresa(Long empresaId, String query, Pageable paging) {
    log.debug("findAll(Long empresaId, String query, Pageable paging) - start");
    AssertHelper.idNotNull(empresaId, Empresa.class);
    authorityHelper.checkUserHasAuthorityViewEmpresa(empresaId);

    Specification<EmpresaEquipoEmprendedor> specs = EmpresaEquipoEmprendedorSpecifications.byEmpresaId(empresaId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<EmpresaEquipoEmprendedor> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(Long empresaId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link EmpresaEquipoEmprendedor} de la
   * {@link Empresa} con el
   * listado empresaEquipoEmprendedores añadiendo, editando o eliminando los
   * elementos segun
   * proceda.
   *
   * @param empresaId                  Id de la {@link Empresa}.
   * @param empresaEquipoEmprendedores lista con los nuevos
   *                                   {@link EmpresaEquipoEmprendedor} a guardar.
   * @return la entidad {@link EmpresaEquipoEmprendedor} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public List<EmpresaEquipoEmprendedor> update(Long empresaId,
      @Valid List<EmpresaEquipoEmprendedor> empresaEquipoEmprendedores) {
    log.debug("update(Long empresaId, List<EmpresaEquipoEmprendedor> empresaEquipoEmprendedores) - start");

    AssertHelper.idNotNull(empresaId, Empresa.class);
    authorityHelper.checkUserHasAuthorityViewEmpresa(empresaId);

    if (!empresaRepository.existsById(empresaId)) {
      throw new EmpresaNotFoundException(empresaId);
    }

    List<EmpresaEquipoEmprendedor> empresaEquipoEmprendedoresBD = repository.findAllByEmpresaId(empresaId);

    // Miembros del equipo eliminados
    List<EmpresaEquipoEmprendedor> empresaEquipoEmprendedoresEliminar = empresaEquipoEmprendedoresBD.stream()
        .filter(empresaEquipoEmprendedor -> empresaEquipoEmprendedores.stream().map(EmpresaEquipoEmprendedor::getId)
            .noneMatch(id -> Objects.equals(id, empresaEquipoEmprendedor.getId())))
        .collect(Collectors.toList());

    if (!empresaEquipoEmprendedoresEliminar.isEmpty()) {
      empresaEquipoEmprendedoresEliminar.forEach(empresaEquipoEmprendedorEliminar -> {
        Set<ConstraintViolation<EmpresaEquipoEmprendedor>> resultValidateEliminar = validator.validate(
            empresaEquipoEmprendedorEliminar,
            EmpresaEquipoEmprendedor.OnDelete.class);
        if (!resultValidateEliminar.isEmpty()) {
          throw new ConstraintViolationException(resultValidateEliminar);
        }
      });

      repository.deleteAll(empresaEquipoEmprendedoresEliminar);
    }

    this.validateEmpresaEquipoEmprendedor(empresaEquipoEmprendedores);

    List<EmpresaEquipoEmprendedor> returnValue = repository.saveAll(empresaEquipoEmprendedores);
    log.debug("update(Long empresaId, List<EmpresaEquipoEmprendedor> empresaEquipoEmprendedores) - END");

    return returnValue;
  }

  private void validateEmpresaEquipoEmprendedor(List<EmpresaEquipoEmprendedor> empresaEquipoEmprendedores) {

    List<String> personasRef = empresaEquipoEmprendedores.stream().map(EmpresaEquipoEmprendedor::getMiembroEquipoRef)
        .distinct()
        .collect(Collectors.toList());

    for (String personaRef : personasRef) {
      EmpresaEquipoEmprendedor empresaEquipoEmprendedorAnterior = null;

      List<EmpresaEquipoEmprendedor> miembrosMiembroEquipoRef = empresaEquipoEmprendedores.stream()
          .filter(solProyecEquip -> solProyecEquip.getMiembroEquipoRef().equals(personaRef))
          .collect(Collectors.toList());

      for (EmpresaEquipoEmprendedor empresaEquipoEmprendedor : miembrosMiembroEquipoRef) {
        if (empresaEquipoEmprendedorAnterior != null
            && empresaEquipoEmprendedorAnterior.getMiembroEquipoRef()
                .equals(empresaEquipoEmprendedor.getMiembroEquipoRef())) {
          throw new EmpresaEquipoEmprendedorUniqueException();
        }

        empresaEquipoEmprendedorAnterior = empresaEquipoEmprendedor;
      }

    }
  }

}

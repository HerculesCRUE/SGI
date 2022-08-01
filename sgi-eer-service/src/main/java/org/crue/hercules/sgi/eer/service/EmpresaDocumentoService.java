package org.crue.hercules.sgi.eer.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.eer.exceptions.EmpresaDocumentoNotFoundException;
import org.crue.hercules.sgi.eer.model.BaseEntity;
import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaDocumento;
import org.crue.hercules.sgi.eer.repository.EmpresaDocumentoRepository;
import org.crue.hercules.sgi.eer.repository.specification.EmpresaDocumentoSpecifications;
import org.crue.hercules.sgi.eer.util.AssertHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión de {@link EmpresaDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmpresaDocumentoService {

  private final Validator validator;
  private final EmpresaDocumentoRepository repository;

  /**
   * Guarda la entidad {@link EmpresaDocumento}.
   * 
   * @param empresaDocumento la entidad {@link EmpresaDocumento} a guardar.
   * @return la entidad {@link EmpresaDocumento} persistida.
   */
  @Transactional
  public EmpresaDocumento create(EmpresaDocumento empresaDocumento) {
    log.debug("create(EmpresaDocumento empresaDocumento) - start");

    AssertHelper.idIsNull(empresaDocumento.getId(), EmpresaDocumento.class);

    // TipoDocumento puede ser null
    if (empresaDocumento.getTipoDocumento() != null) {
      Set<ConstraintViolation<EmpresaDocumento>> result = validator.validate(empresaDocumento,
          BaseEntity.Create.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }
    }

    EmpresaDocumento newEmpresaDocumento = repository.saveAndFlush(empresaDocumento);
    repository.refresh(newEmpresaDocumento);

    log.debug("create(EmpresaDocumento empresaDocumento) - end");
    return newEmpresaDocumento;
  }

  /**
   * Actualiza los datos del {@link EmpresaDocumento}.
   *
   * @param empresaDocumentoActualizar {@link EmpresaDocumento} con los datos
   *                                   actualizados.
   * @return {@link EmpresaDocumento} actualizado.
   */
  @Transactional
  public EmpresaDocumento update(@Valid EmpresaDocumento empresaDocumentoActualizar) {
    log.debug("update(EmpresaDocumento empresaDocumentoActualizar) - start");

    AssertHelper.idNotNull(empresaDocumentoActualizar.getId(), EmpresaDocumento.class);

    return repository.findById(empresaDocumentoActualizar.getId()).map(empresaDocumentoExistente -> {

      if (empresaDocumentoExistente.getTipoDocumento() != null &&
          empresaDocumentoActualizar.getTipoDocumento() != null &&
          !empresaDocumentoExistente.getTipoDocumento().getId()
              .equals(empresaDocumentoActualizar.getTipoDocumento().getId())) {
        // Si estamos modificando el TipoDocumento invocar validaciones asociadas a
        // OnActualizarTipoDocumento
        Set<ConstraintViolation<EmpresaDocumento>> result = validator.validate(empresaDocumentoActualizar,
            EmpresaDocumento.OnActualizarTipoDocumento.class);
        if (!result.isEmpty()) {
          throw new ConstraintViolationException(result);
        }
      }

      empresaDocumentoExistente.setComentarios(empresaDocumentoActualizar.getComentarios());
      empresaDocumentoExistente.setDocumentoRef(empresaDocumentoActualizar.getDocumentoRef());
      empresaDocumentoExistente.setEmpresaId(empresaDocumentoActualizar.getEmpresaId());
      empresaDocumentoExistente.setNombre(empresaDocumentoActualizar.getNombre());
      empresaDocumentoExistente.setTipoDocumento(empresaDocumentoActualizar.getTipoDocumento());

      EmpresaDocumento returnValue = repository.saveAndFlush(empresaDocumentoExistente);
      repository.refresh(returnValue);
      log.debug("update(EmpresaDocumento empresaDocumentoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new EmpresaDocumentoNotFoundException(empresaDocumentoActualizar.getId()));
  }

  /**
   * Elimina la {@link EmpresaDocumento}.
   *
   * @param id Id del {@link EmpresaDocumento}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    AssertHelper.idNotNull(id, EmpresaDocumento.class);

    if (!repository.existsById(id)) {
      throw new EmpresaDocumentoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene una entidad {@link EmpresaDocumento} por id.
   * 
   * @param id Identificador de la entidad {@link EmpresaDocumento}.
   * @return la entidad {@link EmpresaDocumento}.
   */
  public EmpresaDocumento findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, EmpresaDocumento.class);
    final EmpresaDocumento returnValue = repository.findById(id)
        .orElseThrow(() -> new EmpresaDocumentoNotFoundException(id));

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link EmpresaDocumento} que pertenecen a la
   * entidad {@link Empresa} con el id indicado paginadas y/o filtradas.
   * 
   * @param empresaId id de la entidad {@link Empresa}
   * @param query     la información del filtro.
   * @param paging    la información de la paginación.
   * @return la lista de entidades {@link EmpresaDocumento} que pertenecen a la
   *         entidad {@link Empresa} con el id indicado paginadas y/o filtradas.
   */
  public Page<EmpresaDocumento> findAllByEmpresaId(Long empresaId, String query, Pageable paging) {
    log.debug("findAllByEmpresaId(Long empresaId, String query, Pageable paging) - start");

    Specification<EmpresaDocumento> specs = EmpresaDocumentoSpecifications.byEmpresaId(empresaId)
        .and(SgiRSQLJPASupport.toSpecification(query));
    Page<EmpresaDocumento> returnValue = repository.findAll(specs, paging);

    log.debug("findAllByEmpresaId(Long empresaId, String query, Pageable paging) - end");
    return returnValue;
  }
}

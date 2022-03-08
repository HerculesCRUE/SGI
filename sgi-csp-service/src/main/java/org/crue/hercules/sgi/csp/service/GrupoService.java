package org.crue.hercules.sgi.csp.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.GrupoNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion;
import org.crue.hercules.sgi.csp.model.GrupoTipo;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
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
 * Service para la gestión de {@link Grupo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class GrupoService {

  private final GrupoRepository repository;
  private final GrupoTipoService grupoTipoService;
  private final GrupoEspecialInvestigacionService grupoEspecialInvestigacionService;
  private final Validator validator;

  /**
   * Guarda la entidad {@link Grupo}.
   * 
   * @param grupo la entidad {@link Grupo} a guardar.
   * @return la entidad {@link Grupo} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public Grupo create(@Valid Grupo grupo) {
    log.debug("create(Grupo grupo) - start");

    AssertHelper.idIsNull(grupo.getId(), Grupo.class);

    GrupoTipo grupoTipo = null;
    if (grupo.getTipo() != null) {
      grupoTipo = GrupoTipo.builder()
          .tipo(grupo.getTipo().getTipo())
          .fechaInicio(grupo.getFechaInicio())
          .build();
    }

    GrupoEspecialInvestigacion grupoEspecialInvestigacion = GrupoEspecialInvestigacion.builder()
        .especialInvestigacion(grupo.getEspecialInvestigacion().getEspecialInvestigacion())
        .fechaInicio(grupo.getFechaInicio())
        .build();

    // Elimina el GrupoTipo y GrupoEspecialInvestigacion para crear el grupo
    grupo.setTipo(null);
    grupo.setEspecialInvestigacion(null);

    Grupo newGrupo = repository.save(grupo);

    // Crea el GrupoTipo y GrupoEspecialInvestigacion
    if (grupoTipo != null) {
      grupoTipo.setGrupoId(newGrupo.getId());
      newGrupo.setTipo(grupoTipoService.create(grupoTipo));
    }

    grupoEspecialInvestigacion.setGrupoId(newGrupo.getId());
    newGrupo.setEspecialInvestigacion(grupoEspecialInvestigacionService.create(grupoEspecialInvestigacion));

    // Actualiza el grupo con los GrupoTipo y GrupoEspecialInvestigacion creados
    Grupo returnValue = repository.save(newGrupo);

    log.debug("create(Grupo grupo) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link Grupo}.
   *
   * @param grupoActualizar {@link Grupo} con los datos actualizados.
   * @return {@link Grupo} actualizado.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public Grupo update(@Valid Grupo grupoActualizar) {
    log.debug("update(Grupo grupoActualizar) - start");

    AssertHelper.idNotNull(grupoActualizar.getId(), Grupo.class);

    return repository.findById(grupoActualizar.getId()).map(data -> {
      data.setNombre(grupoActualizar.getNombre());
      data.setCodigo(grupoActualizar.getCodigo());
      data.setProyectoSgeRef(grupoActualizar.getProyectoSgeRef());
      data.setFechaInicio(grupoActualizar.getFechaInicio());
      data.setFechaFin(grupoActualizar.getFechaFin());

      // TODO llamar a los servicios para actualizar tipo y especial

      Grupo returnValue = repository.save(data);

      log.debug("update(Grupo grupoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new GrupoNotFoundException(grupoActualizar.getId()));
  }

  /**
   * Obtiene una entidad {@link Grupo} por id.
   * 
   * @param id Identificador de la entidad {@link Grupo}.
   * @return la entidad {@link Grupo}.
   */
  public Grupo findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, Grupo.class);
    final Grupo returnValue = repository.findById(id).orElseThrow(() -> new GrupoNotFoundException(id));

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link Grupo} por id.
   *
   * @param id el id de la entidad {@link Grupo}.
   * @return <code>true</code> si existe y <code>false</code> en caso contrario.
   */
  public boolean existsById(Long id) {
    log.debug("existsById(Long id)  - start");

    AssertHelper.idNotNull(id, Grupo.class);
    final boolean exists = repository.existsById(id);

    log.debug("existsById(Long id)  - end");
    return exists;
  }

  /**
   * Obtener todas las entidades {@link Grupo} paginadas y/o filtradas.
   *
   * @param paging la información de la paginación.
   * @param query  la información del filtro.
   * @return la lista de entidades {@link Grupo} paginadas y/o
   *         filtradas.
   */
  public Page<Grupo> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Specification<Grupo> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<Grupo> returnValue = repository.findAll(specs, paging);

    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link Grupo} activas paginadas y/o filtradas.
   *
   * @param paging la información de la paginación.
   * @param query  la información del filtro.
   * @return la lista de entidades {@link Grupo} activas paginadas y/o
   *         filtradas.
   */
  public Page<Grupo> findActivos(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Specification<Grupo> specs = GrupoSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));
    Page<Grupo> returnValue = repository.findAll(specs, paging);

    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link Grupo}.
   *
   * @param id Id del {@link Grupo}.
   * @return la entidad {@link Grupo} persistida.
   */
  @Transactional
  public Grupo desactivar(Long id) {
    log.debug("desactivar(Long id) - start");

    AssertHelper.idNotNull(id, Grupo.class);

    return repository.findById(id).map(grupo -> {
      if (Boolean.FALSE.equals(grupo.getActivo())) {
        // Si no esta activo no se hace nada
        return grupo;
      }

      grupo.setActivo(false);

      Grupo returnValue = repository.save(grupo);
      log.debug("desactivar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new GrupoNotFoundException(id));
  }

  /**
   * Activa el {@link Grupo}.
   *
   * @param id Id del {@link Grupo}.
   * @return la entidad {@link Grupo} persistida.
   */
  @Transactional
  public Grupo activar(Long id) {
    log.debug("activar(Long id) - start");

    AssertHelper.idNotNull(id, Grupo.class);

    return repository.findById(id).map(grupo -> {
      if (Boolean.TRUE.equals(grupo.getActivo())) {
        // Si esta activo no se hace nada
        return grupo;
      }

      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<Grupo>> result = validator.validate(grupo, BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      grupo.setActivo(true);

      Grupo returnValue = repository.save(grupo);
      log.debug("activar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new GrupoNotFoundException(id));
  }

  /**
   * Devuelve el siguiente codigo para el {@link Grupo} del departamento indicado.
   *
   * @param departamentoRef departamento para el que se quiere obtener el codigo.
   * @return el siguiente codigo para el {@link Grupo}.
   */
  public String getNextCodigo(String departamentoRef) {
    log.debug("getNextCodigo(String departamentoRef) - start");

    Specification<Grupo> specs = GrupoSpecifications.byDepartamentoOrigenRef(departamentoRef);
    long numGruposDepartamento = repository.count(specs);

    String codigo;
    do {
      codigo = generateCodigo(departamentoRef, ++numGruposDepartamento);
    } while (isDuplicatedCodigo(codigo));

    log.debug("getNextCodigo(String departamentoRef) - end");
    return codigo;
  }

  /**
   * Comprueba si ya existe un grupo activo (o otro grupo si se indica un grupoId)
   * con el codigo indicado
   * 
   * @param grupoId Identificador del {@link Grupo}
   * @param codigo  codigo que se quiere validar
   * @return <code>true</code> si ya existe otro grupo con el mismo codigo,
   *         <code>false</code> en caso contrario
   */
  public boolean isDuplicatedCodigo(Long grupoId, String codigo) {
    log.debug("isDuplicatedCodigo(Long grupoId, String codigo) - start");
    Specification<Grupo> specs = GrupoSpecifications.byCodigo(codigo).and(GrupoSpecifications.activos());

    if (grupoId != null) {
      specs = specs.and(GrupoSpecifications.byIdNotEqual(grupoId));
    }

    log.debug("isDuplicatedCodigo(Long grupoId, String codigo) - end");
    return repository.count(specs) > 0;
  }

  /**
   * Comprueba si ya existe un grupo con el
   * codigo indicado
   * 
   * @param codigo codigo que se quiere validar
   * @return <code>true</code> si ya existe otro grupo con el mismo codigo,
   *         <code>false</code> en caso contrario
   */
  private boolean isDuplicatedCodigo(String codigo) {
    return isDuplicatedCodigo(null, codigo);
  }

  /**
   * Genera el codigo para el grupo concatenando el departamentoRef y el
   * numGrupoDepartamento
   * 
   * @param departamentoRef
   * @param numGrupoDepartamento
   * @return el codigo generado
   */
  private String generateCodigo(String departamentoRef, long numGrupoDepartamento) {
    return departamentoRef + "-" + numGrupoDepartamento;
  }

}

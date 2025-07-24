package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ProrrogaDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoProrrogaNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumento;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.repository.ModeloTipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ProrrogaDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoProrrogaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProrrogaDocumentoSpecifications;
import org.crue.hercules.sgi.csp.service.ProrrogaDocumentoService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ProrrogaDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)

public class ProrrogaDocumentoServiceImpl implements ProrrogaDocumentoService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_FIELD_DOCUMENTO_REF = "documentoRef";
  private static final String MSG_FIELD_VISIBLE = "visible";
  private static final String MSG_MODEL_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.model.ModeloEjecucion.message";
  private static final String MSG_MODEL_TIPO_DOCUMENTO = "org.crue.hercules.sgi.csp.model.TipoDocumento.message";
  private static final String MSG_MODEL_MODELO_TIPO_DOCUMENTO = "org.crue.hercules.sgi.csp.model.ModeloTipoDocumento.message";
  private static final String MSG_ENTITY_INACTIVO = "org.springframework.util.Assert.inactivo.message";

  private final ProrrogaDocumentoRepository repository;
  private final ProyectoProrrogaRepository proyectoProrrogaRepository;
  private final ModeloTipoDocumentoRepository modeloTipoDocumentoRepository;

  public ProrrogaDocumentoServiceImpl(ProrrogaDocumentoRepository prorrogaDocumentoRepository,
      ProyectoProrrogaRepository proyectoProrrogaRepository,
      ModeloTipoDocumentoRepository modeloTipoDocumentoRepository) {
    this.repository = prorrogaDocumentoRepository;
    this.proyectoProrrogaRepository = proyectoProrrogaRepository;
    this.modeloTipoDocumentoRepository = modeloTipoDocumentoRepository;
  }

  /**
   * Guardar un nuevo {@link ProrrogaDocumento}.
   *
   * @param prorrogaDocumento la entidad {@link ProrrogaDocumento} a guardar.
   * @return la entidad {@link ProrrogaDocumento} persistida.
   */
  @Override
  @Transactional
  public ProrrogaDocumento create(ProrrogaDocumento prorrogaDocumento) {
    log.debug("create(ProrrogaDocumento prorrogaDocumento) - start");

    AssertHelper.idIsNull(prorrogaDocumento.getId(), ProrrogaDocumento.class);

    validarRequeridosProrrogaDocumento(prorrogaDocumento);
    validarProrrogaDcoumento(prorrogaDocumento);

    ProrrogaDocumento returnValue = repository.save(prorrogaDocumento);

    log.debug("create(ProrrogaDocumento prorrogaDocumento) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ProrrogaDocumento}.
   *
   * @param prorrogaDocumentoActualizar la entidad {@link ProrrogaDocumento} a
   *                                    actualizar.
   * @return la entidad {@link ProrrogaDocumento} persistida.
   */
  @Override
  @Transactional
  public ProrrogaDocumento update(ProrrogaDocumento prorrogaDocumentoActualizar) {
    log.debug("update(ProrrogaDocumento prorrogaDocumentoActualizar) - start");

    AssertHelper.idNotNull(prorrogaDocumentoActualizar.getId(), ProrrogaDocumento.class);

    validarRequeridosProrrogaDocumento(prorrogaDocumentoActualizar);

    return repository.findById(prorrogaDocumentoActualizar.getId()).map(prorrogaDocumento -> {

      validarProrrogaDcoumento(prorrogaDocumentoActualizar);

      prorrogaDocumento.setNombre(prorrogaDocumentoActualizar.getNombre());
      prorrogaDocumento.setDocumentoRef(prorrogaDocumentoActualizar.getDocumentoRef());
      prorrogaDocumento.setTipoDocumento(prorrogaDocumentoActualizar.getTipoDocumento());
      prorrogaDocumento.setComentario(prorrogaDocumentoActualizar.getComentario());
      prorrogaDocumento.setVisible(prorrogaDocumentoActualizar.getVisible());

      ProrrogaDocumento returnValue = repository.save(prorrogaDocumento);
      log.debug("update(ProrrogaDocumento prorrogaDocumentoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ProrrogaDocumentoNotFoundException(prorrogaDocumentoActualizar.getId()));
  }

  /**
   * Elimina el {@link ProrrogaDocumento}.
   *
   * @param id Id del {@link ProrrogaDocumento}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, ProrrogaDocumento.class);
    if (!repository.existsById(id)) {
      throw new ProrrogaDocumentoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ProrrogaDocumento} por su id.
   *
   * @param id el id de la entidad {@link ProrrogaDocumento}.
   * @return la entidad {@link ProrrogaDocumento}.
   */
  @Override
  public ProrrogaDocumento findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProrrogaDocumento returnValue = repository.findById(id)
        .orElseThrow(() -> new ProrrogaDocumentoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link ProrrogaDocumento} para una
   * {@link ProyectoProrroga} paginadas y/o filtradas.
   * 
   * @param idProrroga id de {@link ProyectoProrroga}
   * @param query      la información del filtro.
   * @param paging     la información de la paginación.
   * @return la lista de entidades {@link ProrrogaDocumento} paginadas y/o
   *         filtradas.
   */
  @Override
  public Page<ProrrogaDocumento> findAllByProyectoProrroga(Long idProrroga, String query, Pageable paging) {
    log.debug("findAllByProyectoProrroga(Long idProrroga, String query, Pageable pageable) - start");
    Specification<ProrrogaDocumento> specs = ProrrogaDocumentoSpecifications.byProyectoProrrogaId(idProrroga)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProrrogaDocumento> returnValue = repository.findAll(specs, paging);
    log.debug("findAllByProyectoProrroga(Long idProrroga, String query, Pageable pageable) - end");
    return returnValue;

  }

  /**
   * Obtener todas las entidades {@link ProrrogaDocumento}.
   * 
   * @param idProyecto id del {@link Proyecto}
   * 
   * @return la lista de entidades {@link ProrrogaDocumento} .
   */
  @Override
  public List<ProrrogaDocumento> findAllByProyecto(Long idProyecto) {
    log.debug("findAllByProyecto(Long idProyecto) - start");

    Specification<ProrrogaDocumento> specByProyecto = ProrrogaDocumentoSpecifications.byProyectoId(idProyecto);
    Specification<ProrrogaDocumento> specs = Specification.where(specByProyecto);

    List<ProrrogaDocumento> returnValue = repository.findAll(specs);
    log.debug("findAllByProyecto(Long idProyecto) - end");
    return returnValue;
  }

  /**
   * Comprueba, valida y tranforma los datos de la {@link ProrrogaDocumento} antes
   * de utilizados para crear o modificar la entidad
   *
   * @param datosProrrogaDocumento
   */
  private void validarProrrogaDcoumento(ProrrogaDocumento datosProrrogaDocumento) {
    log.debug(
        "validarProrrogaDcoumento(ProrrogaDocumento prorrogaDocumento) - start");

    // Se comprueba la existencia del proyecto
    Long proyectoProrrogaId = datosProrrogaDocumento.getProyectoProrrogaId();
    if (!proyectoProrrogaRepository.existsById(proyectoProrrogaId)) {
      throw new ProyectoProrrogaNotFoundException(proyectoProrrogaId);
    }

    if (datosProrrogaDocumento.getTipoDocumento() != null) {
      // TipoDoc Activo sin fase asociada y asociados al modelo ejecucion del proyectp

      Optional<ModeloEjecucion> modeloEjecucion = proyectoProrrogaRepository.getModeloEjecucion(proyectoProrrogaId);
      AssertHelper.entityNotExists(modeloEjecucion.isPresent(), ProyectoProrroga.class, ModeloEjecucion.class);
      Assert.isTrue(modeloEjecucion.get().getActivo(),
          () -> ProblemMessage.builder()
              .key(MSG_ENTITY_INACTIVO)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_EJECUCION))
              .parameter(MSG_KEY_FIELD, modeloEjecucion.get().getNombre())
              .build());

      Optional<ModeloTipoDocumento> modeloTipoDocumento = modeloTipoDocumentoRepository
          .findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(modeloEjecucion.get().getId(), null,
              datosProrrogaDocumento.getTipoDocumento().getId());

      AssertHelper.entityNotExists(modeloTipoDocumento.isPresent(), ModeloEjecucion.class, TipoDocumento.class);

      Assert.isTrue(modeloTipoDocumento.get().getTipoDocumento().getActivo(),
          () -> ProblemMessage.builder()
              .key(MSG_ENTITY_INACTIVO)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_DOCUMENTO))
              .parameter(MSG_KEY_FIELD, modeloTipoDocumento.get().getTipoDocumento().getNombre())
              .build());

      Assert.isTrue(modeloTipoDocumento.get().getActivo(),
          () -> ProblemMessage.builder()
              .key(MSG_ENTITY_INACTIVO)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_DOCUMENTO))
              .parameter(MSG_KEY_FIELD, modeloTipoDocumento.get().getTipoDocumento().getNombre())
              .build());

    }

    log.debug("validarProrrogaDcoumento(ProrrogaDocumento prorrogaDocumento) - end");
  }

  /**
   * valida los datos requeridos de la {@link ProrrogaDocumento}
   *
   * @param datosProrrogaDocumento
   */
  private void validarRequeridosProrrogaDocumento(ProrrogaDocumento datosProrrogaDocumento) {
    log.debug("validarRequeridosProrrogaDocumento(ProrrogaDocumento datosProrrogaDocumento) - start");

    AssertHelper.idNotNull(datosProrrogaDocumento.getProyectoProrrogaId(), ProyectoProrroga.class);
    AssertHelper.fieldNotBlank(!CollectionUtils.isEmpty(datosProrrogaDocumento.getNombre()), ProrrogaDocumento.class,
        AssertHelper.MESSAGE_KEY_NAME);
    AssertHelper.fieldNotNull(datosProrrogaDocumento.getDocumentoRef(), ProrrogaDocumento.class,
        MSG_FIELD_DOCUMENTO_REF);
    AssertHelper.fieldNotNull(datosProrrogaDocumento.getVisible(), ProrrogaDocumento.class, MSG_FIELD_VISIBLE);

    log.debug("validarRequeridosProrrogaDocumento(ProrrogaDocumento datosProrrogaDocumento) - end");
  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProrrogaDocumento}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsByProyecto(Long proyectoId) {
    return repository.existsByProyectoProrrogaProyectoId(proyectoId);
  }

}

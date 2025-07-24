package org.crue.hercules.sgi.csp.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ProyectoDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento;
import org.crue.hercules.sgi.csp.repository.ModeloTipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoDocumentoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoDocumentoService;
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
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ProyectoDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoDocumentoServiceImpl implements ProyectoDocumentoService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_MODELO = "modelo";
  private static final String MSG_KEY_MSG = "msg";
  private static final String MSG_FIELD_VISIBLE = "visible";
  private static final String MSG_FIELD_DOCUMENTO_REF = "documentoRef";
  private static final String MSG_MODEL_MODELO_TIPO_DOCUMENTO = "org.crue.hercules.sgi.csp.model.ModeloTipoDocumento.message";
  private static final String MSG_MODEL_MODELO_TIPO_FASE = "org.crue.hercules.sgi.csp.model.ModeloTipoFase.message";
  private static final String MSG_MODEL_TIPO_DOCUMENTO = "org.crue.hercules.sgi.csp.model.TipoDocumento.message";
  private static final String MSG_MODEL_TIPO_FASE = "org.crue.hercules.sgi.csp.model.TipoFase.message";
  private static final String MSG_ENTITY_INACTIVO = "org.springframework.util.Assert.inactivo.message";
  private static final String MSG_MODELO_EJECUCION_INACTIVO = "org.springframework.util.Assert.entity.modeloEjecucion.inactivo.message";
  private static final String MSG_PROYECTO_ASSIGN_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.exceptions.AssignModeloEjecucion.message";
  private static final String MSG_PROYECTO_SIN_MODELO_ASIGNADO = "org.crue.hercules.sgi.csp.model.Proyecto.sinModeloEjecucion.message";
  private static final String MSG_NO_DISPONIBLE_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.model.ModeloEjecucion.noDisponible.message";

  private final ProyectoDocumentoRepository repository;
  private final ProyectoRepository proyectoRepository;
  private final ModeloTipoFaseRepository modeloTipoFaseRepository;
  private final ModeloTipoDocumentoRepository modeloTipoDocumentoRepository;

  /**
   * {@link ProyectoDocumentoServiceImpl}.
   * 
   * @param proyectoDocumentoRepository   {@link ProyectoDocumentoRepository}.
   * @param proyectoRepository            {@link ProyectoRepository}.
   * @param modeloTipoFaseRepository      {@link ModeloTipoFaseRepository}.
   * @param modeloTipoDocumentoRepository {@link ModeloTipoDocumentoRepository}.
   */
  public ProyectoDocumentoServiceImpl(ProyectoDocumentoRepository proyectoDocumentoRepository,
      ProyectoRepository proyectoRepository, ModeloTipoFaseRepository modeloTipoFaseRepository,
      ModeloTipoDocumentoRepository modeloTipoDocumentoRepository) {
    this.repository = proyectoDocumentoRepository;
    this.proyectoRepository = proyectoRepository;
    this.modeloTipoFaseRepository = modeloTipoFaseRepository;
    this.modeloTipoDocumentoRepository = modeloTipoDocumentoRepository;
  }

  /**
   * Guardar un nuevo {@link ProyectoDocumento}.
   *
   * @param proyectoDocumento la entidad {@link ProyectoDocumento} a guardar.
   * @return la entidad {@link ProyectoDocumento} persistida.
   */
  @Override
  @Transactional
  public ProyectoDocumento create(ProyectoDocumento proyectoDocumento) {
    log.debug("create(ProyectoDocumento proyectoDocumento) - start");

    AssertHelper.idIsNull(proyectoDocumento.getId(), ProyectoDocumento.class);

    validarRequeridosProyectoDocumento(proyectoDocumento);
    validarProyectoDocumento(proyectoDocumento, null);

    ProyectoDocumento returnValue = repository.save(proyectoDocumento);

    log.debug("create(ProyectoDocumento proyectoDocumento) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ProyectoDocumento}.
   *
   * @param proyectoDocumentoActualizar la entidad {@link ProyectoDocumento} a
   *                                    actualizar.
   * @return la entidad {@link ProyectoDocumento} persistida.
   */
  @Override
  @Transactional
  public ProyectoDocumento update(ProyectoDocumento proyectoDocumentoActualizar) {
    log.debug("update(ProyectoDocumento proyectoDocumentoActualizar) - start");

    AssertHelper.idNotNull(proyectoDocumentoActualizar.getId(), ProyectoDocumento.class);

    validarRequeridosProyectoDocumento(proyectoDocumentoActualizar);

    return repository.findById(proyectoDocumentoActualizar.getId()).map(proyectoDocumento -> {

      proyectoDocumentoActualizar.setProyectoId(proyectoDocumento.getProyectoId());
      validarProyectoDocumento(proyectoDocumentoActualizar, proyectoDocumento);

      proyectoDocumento.setTipoFase(proyectoDocumentoActualizar.getTipoFase());
      proyectoDocumento.setTipoDocumento(proyectoDocumentoActualizar.getTipoDocumento());
      proyectoDocumento.setNombre(proyectoDocumentoActualizar.getNombre());
      proyectoDocumento.setVisible(proyectoDocumentoActualizar.getVisible());
      proyectoDocumento.setComentario(proyectoDocumentoActualizar.getComentario());
      proyectoDocumento.setDocumentoRef(proyectoDocumentoActualizar.getDocumentoRef());

      ProyectoDocumento returnValue = repository.save(proyectoDocumento);
      log.debug("update(ProyectoDocumento proyectoDocumentoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoDocumentoNotFoundException(proyectoDocumentoActualizar.getId()));
  }

  /**
   * Elimina el {@link ProyectoDocumento}.
   *
   * @param id Id del {@link ProyectoDocumento}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, ProyectoDocumento.class);
    if (!repository.existsById(id)) {
      throw new ProyectoDocumentoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las {@link ProyectoDocumento} para una {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoDocumento} de la
   *         {@link Proyecto} paginadas.
   */
  public Page<ProyectoDocumento> findAllByProyectoId(Long proyectoId, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - start");
    Specification<ProyectoDocumento> specs = ProyectoDocumentoSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoDocumento> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * valida los datos requeridos de la {@link ProyectoDocumento}
   *
   * @param datosProyectoDocumento
   */
  private void validarRequeridosProyectoDocumento(ProyectoDocumento datosProyectoDocumento) {
    log.debug("validarRequeridosProyectoDocumento(ProyectoDocumento datosProyectoDocumento) - start");

    /** Obligatorios */
    AssertHelper.idNotNull(datosProyectoDocumento.getProyectoId(), Proyecto.class);

    AssertHelper.fieldNotBlank(!StringUtils.isEmpty(datosProyectoDocumento.getNombre()),
        ProyectoDocumento.class, AssertHelper.MESSAGE_KEY_NAME);

    AssertHelper.fieldNotNull(datosProyectoDocumento.getVisible() != null, ProyectoDocumento.class, MSG_FIELD_VISIBLE);

    AssertHelper.fieldNotBlank(!StringUtils.isEmpty(datosProyectoDocumento.getDocumentoRef()), ProyectoDocumento.class,
        MSG_FIELD_DOCUMENTO_REF);

    log.debug("validarRequeridosProyectoDocumento(ProyectoDocumento datosProyectoDocumento) - end");
  }

  /**
   * Comprueba, valida y tranforma los datos de la {@link ProyectoDocumento} antes
   * de utilizados para crear o modificar la entidad
   *
   * @param datosProyectoDocumento
   * @param datosOriginales
   */
  private void validarProyectoDocumento(ProyectoDocumento datosProyectoDocumento, ProyectoDocumento datosOriginales) {
    log.debug(
        "validarProyectoDcoumento(ProyectoDocumento proyectoDocumento, ProyectoDocumento datosOriginales) - start");

    Proyecto proyecto = proyectoRepository.findById(datosProyectoDocumento.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(datosProyectoDocumento.getProyectoId()));

    // Se recupera el Id de ModeloEjecucion para las siguientes validaciones
    Long modeloEjecucionId = (proyecto.getModeloEjecucion() != null && proyecto.getModeloEjecucion().getId() != null)
        ? proyecto.getModeloEjecucion().getId()
        : null;

    /**
     * El TipoFase no es obligatorio, pero si tiene valor y existe un TipoDocumento,
     * es necesario recuperar el ModeloTipoFase para validar el TipoDocumento
     * asignado
     */

    ModeloTipoFase proyectoDocumentoModeloTipoFase = null;
    if (datosProyectoDocumento.getTipoFase() != null && datosProyectoDocumento.getTipoFase().getId() != null) {

      Optional<ModeloTipoFase> modeloTipoFase = modeloTipoFaseRepository
          .findByModeloEjecucionIdAndTipoFaseId(modeloEjecucionId, datosProyectoDocumento.getTipoFase().getId());

      // TipoFase está asignado al ModeloEjecucion
      Assert.isTrue(modeloTipoFase.isPresent(),
          () -> ProblemMessage.builder()
              .key(MSG_PROYECTO_ASSIGN_MODELO_EJECUCION)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FASE))
              .parameter(MSG_KEY_MSG, ApplicationContextSupport.getMessage(MSG_NO_DISPONIBLE_MODELO_EJECUCION))
              .parameter(MSG_KEY_MODELO, ((modeloEjecucionId != null) ? proyecto.getModeloEjecucion().getNombre()
                  : ApplicationContextSupport.getMessage(MSG_PROYECTO_SIN_MODELO_ASIGNADO)))
              .build());

      // Comprobar solamente si estamos creando o se ha modificado el TipoFase
      if (datosOriginales == null || datosOriginales.getTipoFase() == null
          || (!Objects.equals(modeloTipoFase.get().getTipoFase().getId(), datosOriginales.getTipoFase().getId()))) {

        // La asignación al ModeloEjecucion está activa
        Assert.isTrue(modeloTipoFase.get().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_MODELO_EJECUCION_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_FASE))
                .parameter(MSG_KEY_FIELD, modeloTipoFase.get().getTipoFase().getNombre())
                .parameter(MSG_KEY_MODELO, modeloTipoFase.get().getModeloEjecucion().getNombre())
                .build());

        // El TipoFase está activo
        Assert.isTrue(modeloTipoFase.get().getTipoFase().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_ENTITY_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FASE))
                .parameter(MSG_KEY_FIELD, modeloTipoFase.get().getTipoFase().getNombre())
                .build());
      }
      proyectoDocumentoModeloTipoFase = modeloTipoFase.get();

    } else {
      datosProyectoDocumento.setTipoFase(null);
    }

    /**
     * El TipoDocumento no es obligatorio, si hay TipoFase asignado hay que
     * validarlo con el ModeloTipoFase
     */
    if (datosProyectoDocumento.getTipoDocumento() != null
        && datosProyectoDocumento.getTipoDocumento().getId() != null) {

      // TipoDocumento
      Optional<ModeloTipoDocumento> modeloTipoDocumento = modeloTipoDocumentoRepository
          .findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(modeloEjecucionId,
              proyectoDocumentoModeloTipoFase == null ? null : proyectoDocumentoModeloTipoFase.getId(),
              datosProyectoDocumento.getTipoDocumento().getId());

      // Está asignado al ModeloEjecucion y ModeloTipoFase
      Assert.isTrue(modeloTipoDocumento.isPresent(),
          () -> ProblemMessage.builder()
              .key(MSG_PROYECTO_ASSIGN_MODELO_EJECUCION)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_DOCUMENTO))
              .parameter(MSG_KEY_MSG, ApplicationContextSupport.getMessage(MSG_NO_DISPONIBLE_MODELO_EJECUCION))
              .parameter(MSG_KEY_MODELO, ((modeloEjecucionId != null) ? proyecto.getModeloEjecucion().getNombre()
                  : ApplicationContextSupport.getMessage(MSG_PROYECTO_SIN_MODELO_ASIGNADO)))
              .build());

      // Comprobar solamente si estamos creando o se ha modificado el documento
      if (datosOriginales == null || datosOriginales.getTipoDocumento() == null
          || (!Objects.equals(modeloTipoDocumento.get().getTipoDocumento().getId(),
              datosOriginales.getTipoDocumento().getId()))) {

        // La asignación al ModeloEjecucion está activa
        Assert.isTrue(modeloTipoDocumento.get().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_MODELO_EJECUCION_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_DOCUMENTO))
                .parameter(MSG_KEY_FIELD, modeloTipoDocumento.get().getTipoDocumento().getNombre())
                .parameter(MSG_KEY_MODELO, modeloTipoDocumento.get().getModeloEjecucion().getNombre())
                .build());

        // El TipoDocumento está activo
        Assert.isTrue(modeloTipoDocumento.get().getTipoDocumento().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_ENTITY_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_DOCUMENTO))
                .parameter(MSG_KEY_FIELD, modeloTipoDocumento.get().getTipoDocumento().getNombre())
                .build());

      }
      datosProyectoDocumento.setTipoDocumento(modeloTipoDocumento.get().getTipoDocumento());

    } else {
      datosProyectoDocumento.setTipoDocumento(null);
    }

    log.debug("validarProyectoDcoumento(ProyectoDocumento proyectoDocumento, ProyectoDocumento datosOriginales) - end");
  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoDocumento}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return si existe o no el proyecto
   */
  @Override
  public boolean existsByProyecto(Long proyectoId) {
    return repository.existsByProyectoId(proyectoId);
  }
}

package org.crue.hercules.sgi.csp.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaDocumentoSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaDocumentoService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ConvocatoriaDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)

public class ConvocatoriaDocumentoServiceImpl implements ConvocatoriaDocumentoService {

  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_DOCUMENTO_REF = "convocatoriaDocumento.documentoRef";
  private static final String MSG_KEY_PUBLICO = "convocatoriaDocumento.publico";
  private static final String MSG_KEY_MSG = "msg";
  private static final String MSG_KEY_MODELO = "modelo";
  private static final String MSG_MODEL_MODELO_TIPO_FASE = "org.crue.hercules.sgi.csp.model.ModeloTipoFase.message";
  private static final String MSG_MODEL_TIPO_FASE = "org.crue.hercules.sgi.csp.model.TipoFase.message";
  private static final String MSG_ENTITY_INACTIVO = "org.springframework.util.Assert.inactivo.message";
  private static final String MSG_MODEL_MODELO_TIPO_DOCUMENTO = "org.crue.hercules.sgi.csp.model.ModeloTipoDocumento.message";
  private static final String MSG_MODEL_TIPO_DOCUMENTO = "org.crue.hercules.sgi.csp.model.TipoDocumento.message";
  private static final String MSG_CONVOCATORIA_ASSIGN_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.exceptions.AssignModeloEjecucion.message";
  private static final String MSG_CONVOCATORIA_SIN_MODELO_ASIGNADO = "org.crue.hercules.sgi.csp.model.Convocatoria.sinModeloEjecucion.message";
  private static final String MSG_NO_DISPONIBLE_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.model.ModeloEjecucion.noDisponible.message";

  private final ConvocatoriaDocumentoRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ModeloTipoFaseRepository modeloTipoFaseRepository;
  private final ModeloTipoDocumentoRepository modeloTipoDocumentoRepository;
  private final ConvocatoriaAuthorityHelper authorityHelper;

  public ConvocatoriaDocumentoServiceImpl(ConvocatoriaDocumentoRepository convocatoriaDocumentoRepository,
      ConvocatoriaRepository convocatoriaRepository, ModeloTipoFaseRepository modeloTipoFaseRepository,
      ModeloTipoDocumentoRepository modeloTipoDocumentoRepository,
      ConvocatoriaAuthorityHelper authorityHelper) {
    this.repository = convocatoriaDocumentoRepository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.modeloTipoFaseRepository = modeloTipoFaseRepository;
    this.modeloTipoDocumentoRepository = modeloTipoDocumentoRepository;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Guardar un nuevo {@link ConvocatoriaDocumento}.
   *
   * @param convocatoriaDocumento la entidad {@link ConvocatoriaDocumento} a
   *                              guardar.
   * @return la entidad {@link ConvocatoriaDocumento} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaDocumento create(ConvocatoriaDocumento convocatoriaDocumento) {
    log.debug("create(ConvocatoriaDocumento convocatoriaDocumento) - start");

    AssertHelper.idIsNull(convocatoriaDocumento.getId(), ConvocatoriaDocumento.class);

    validarRequeridosConvocatoriaDocumento(convocatoriaDocumento);
    validarConvocatoriaDcoumento(convocatoriaDocumento, null);

    ConvocatoriaDocumento returnValue = repository.save(convocatoriaDocumento);

    log.debug("create(ConvocatoriaDocumento convocatoriaDocumento) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ConvocatoriaDocumento}.
   *
   * @param convocatoriaDocumentoActualizar la entidad
   *                                        {@link ConvocatoriaDocumento} a
   *                                        actualizar.
   * @return la entidad {@link ConvocatoriaDocumento} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaDocumento update(ConvocatoriaDocumento convocatoriaDocumentoActualizar) {
    log.debug("update(ConvocatoriaDocumento convocatoriaDocumentoActualizar) - start");

    AssertHelper.idNotNull(convocatoriaDocumentoActualizar.getId(), ConvocatoriaDocumento.class);

    validarRequeridosConvocatoriaDocumento(convocatoriaDocumentoActualizar);

    return repository.findById(convocatoriaDocumentoActualizar.getId()).map(convocatoriaDocumento -> {

      convocatoriaDocumentoActualizar.setConvocatoriaId(convocatoriaDocumento.getConvocatoriaId());
      validarConvocatoriaDcoumento(convocatoriaDocumentoActualizar, convocatoriaDocumento);

      convocatoriaDocumento.setTipoFase(convocatoriaDocumentoActualizar.getTipoFase());
      convocatoriaDocumento.setTipoDocumento(convocatoriaDocumentoActualizar.getTipoDocumento());
      convocatoriaDocumento.setNombre(convocatoriaDocumentoActualizar.getNombre());
      convocatoriaDocumento.setPublico(convocatoriaDocumentoActualizar.getPublico());
      convocatoriaDocumento.setObservaciones(convocatoriaDocumentoActualizar.getObservaciones());
      convocatoriaDocumento.setDocumentoRef(convocatoriaDocumentoActualizar.getDocumentoRef());

      ConvocatoriaDocumento returnValue = repository.save(convocatoriaDocumento);
      log.debug("update(ConvocatoriaDocumento convocatoriaDocumentoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaDocumentoNotFoundException(convocatoriaDocumentoActualizar.getId()));
  }

  /**
   * Elimina el {@link ConvocatoriaDocumento}.
   *
   * @param id Id del {@link ConvocatoriaDocumento}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, ConvocatoriaDocumento.class);

    if (!repository.existsById(id)) {
      throw new ConvocatoriaDocumentoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaDocumento} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaDocumento}.
   * @return la entidad {@link ConvocatoriaDocumento}.
   */
  @Override
  public ConvocatoriaDocumento findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaDocumento returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaDocumentoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link ConvocatoriaDocumento} para una
   * {@link Convocatoria} paginadas y/o filtradas.
   * 
   * @param convocatoriaId id de {@link Convocatoria}
   * @param query          la información del filtro.
   * @param paging         la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaDocumento} paginadas y/o
   *         filtradas.
   */
  @Override
  public Page<ConvocatoriaDocumento> findAllByConvocatoria(Long convocatoriaId, String query, Pageable paging) {
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - start");

    authorityHelper.checkUserHasAuthorityViewConvocatoria(convocatoriaId);

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaId)
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaId));

    Specification<ConvocatoriaDocumento> specs = ConvocatoriaDocumentoSpecifications.byConvocatoriaId(
        convocatoriaId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    if (!authorityHelper.hasAuthorityViewUnidadGestion(convocatoria)) {
      specs = specs.and(ConvocatoriaDocumentoSpecifications.onlyPublicos());
    }

    Page<ConvocatoriaDocumento> returnValue = repository.findAll(specs, paging);
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - end");
    return returnValue;

  }

  /**
   * Comprueba, valida y tranforma los datos de la {@link ConvocatoriaDocumento}
   * antes de utilizados para crear o modificar la entidad
   *
   * @param datosConvocatoriaDocumento
   * @param datosOriginales
   */
  private void validarConvocatoriaDcoumento(ConvocatoriaDocumento datosConvocatoriaDocumento,
      ConvocatoriaDocumento datosOriginales) {
    log.debug(
        "validarConvocatoriaDcoumento(ConvocatoriaDocumento convocatoriaDocumento, ConvocatoriaDocumento datosOriginales) - start");

    Convocatoria convocatoria = convocatoriaRepository.findById(datosConvocatoriaDocumento.getConvocatoriaId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(datosConvocatoriaDocumento.getConvocatoriaId()));

    // Se recupera el Id de ModeloEjecucion para las siguientes validaciones
    Long modeloEjecucionId = (convocatoria.getModeloEjecucion() != null
        && convocatoria.getModeloEjecucion().getId() != null) ? convocatoria.getModeloEjecucion().getId() : null;

    /**
     * El TipoFase no es obligatorio, pero si tiene valor y existe un TipoDocumento,
     * es necesario recuperar el ModeloTipoFase para validar el TipoDocumento
     * asignado
     */

    ModeloTipoFase convocatoriaDocumentoModeloTipoFase = null;
    if (datosConvocatoriaDocumento.getTipoFase() != null && datosConvocatoriaDocumento.getTipoFase().getId() != null) {

      Optional<ModeloTipoFase> modeloTipoFase = modeloTipoFaseRepository
          .findByModeloEjecucionIdAndTipoFaseId(modeloEjecucionId, datosConvocatoriaDocumento.getTipoFase().getId());

      // TipoFase está asignado al ModeloEjecucion
      Assert.isTrue(modeloTipoFase.isPresent(),
          () -> ProblemMessage.builder()
              .key(MSG_CONVOCATORIA_ASSIGN_MODELO_EJECUCION)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FASE))
              .parameter(MSG_KEY_MSG, ApplicationContextSupport.getMessage(MSG_NO_DISPONIBLE_MODELO_EJECUCION))
              .parameter(MSG_KEY_MODELO, ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                  : ApplicationContextSupport.getMessage(MSG_CONVOCATORIA_SIN_MODELO_ASIGNADO)))
              .build());

      // Comprobar solamente si estamos creando o se ha modificado el TipoFase
      if (datosOriginales == null || datosOriginales.getTipoFase() == null
          || (!Objects.equals(modeloTipoFase.get().getTipoFase().getId(), datosOriginales.getTipoFase().getId()))) {

        // La asignación al ModeloEjecucion está activa
        Assert.isTrue(modeloTipoFase.get().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_ENTITY_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_FASE))
                .parameter(MSG_KEY_FIELD, modeloTipoFase.get().getTipoFase().getNombre())
                .build());

        // El TipoFase está activo
        Assert.isTrue(modeloTipoFase.get().getTipoFase().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_ENTITY_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FASE))
                .parameter(MSG_KEY_FIELD, modeloTipoFase.get().getTipoFase().getNombre())
                .build());
      }
      convocatoriaDocumentoModeloTipoFase = modeloTipoFase.get();

    } else {
      datosConvocatoriaDocumento.setTipoFase(null);
    }

    /**
     * El TipoDocumento no es obligatorio, si hay TipoFase asignado hay que
     * validarlo con el ModeloTipoFase
     */
    if (datosConvocatoriaDocumento.getTipoDocumento() != null
        && datosConvocatoriaDocumento.getTipoDocumento().getId() != null) {

      // TipoDocumento
      Optional<ModeloTipoDocumento> modeloTipoDocumento = modeloTipoDocumentoRepository
          .findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(modeloEjecucionId,
              convocatoriaDocumentoModeloTipoFase == null ? null : convocatoriaDocumentoModeloTipoFase.getId(),
              datosConvocatoriaDocumento.getTipoDocumento().getId());

      // Está asignado al ModeloEjecucion y ModeloTipoFase
      Assert.isTrue(modeloTipoDocumento.isPresent(),
          () -> ProblemMessage.builder()
              .key(MSG_CONVOCATORIA_ASSIGN_MODELO_EJECUCION)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_DOCUMENTO))
              .parameter(MSG_KEY_MSG, ApplicationContextSupport.getMessage(MSG_NO_DISPONIBLE_MODELO_EJECUCION))
              .parameter(MSG_KEY_MODELO, ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                  : ApplicationContextSupport.getMessage(MSG_CONVOCATORIA_SIN_MODELO_ASIGNADO)))
              .build());

      // Comprobar solamente si estamos creando o se ha modificado el documento
      if (datosOriginales == null || datosOriginales.getTipoDocumento() == null
          || (!Objects.equals(modeloTipoDocumento.get().getTipoDocumento().getId(),
              datosOriginales.getTipoDocumento().getId()))) {

        // La asignación al ModeloEjecucion está activa
        Assert.isTrue(modeloTipoDocumento.get().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_ENTITY_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_DOCUMENTO))
                .parameter(MSG_KEY_FIELD, modeloTipoDocumento.get().getTipoDocumento().getNombre())
                .build());

        // El TipoDocumento está activo
        Assert.isTrue(modeloTipoDocumento.get().getTipoDocumento().getActivo(),
            () -> ProblemMessage.builder()
                .key(MSG_ENTITY_INACTIVO)
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_DOCUMENTO))
                .parameter(MSG_KEY_FIELD, modeloTipoDocumento.get().getTipoDocumento().getNombre())
                .build());

      }
      datosConvocatoriaDocumento.setTipoDocumento(modeloTipoDocumento.get().getTipoDocumento());

    } else {
      datosConvocatoriaDocumento.setTipoDocumento(null);
    }

    log.debug(
        "validarConvocatoriaDcoumento(ConvocatoriaDocumento convocatoriaDocumento, ConvocatoriaDocumento datosOriginales) - end");
  }

  /**
   * valida los datos requeridos de la {@link ConvocatoriaDocumento}
   *
   * @param datosConvocatoriaDocumento
   */
  private void validarRequeridosConvocatoriaDocumento(ConvocatoriaDocumento datosConvocatoriaDocumento) {
    log.debug("validarRequeridosConvocatoriaDocumento(ConvocatoriaDocumento datosConvocatoriaDocumento) - start");

    /** Obligatorios */
    AssertHelper.idNotNull(datosConvocatoriaDocumento.getConvocatoriaId(), Convocatoria.class);
    AssertHelper.fieldNotNull(datosConvocatoriaDocumento.getNombre(), ConvocatoriaDocumento.class,
        AssertHelper.MESSAGE_KEY_NAME);
    AssertHelper.fieldNotNull(datosConvocatoriaDocumento.getPublico(), ConvocatoriaDocumento.class, MSG_KEY_PUBLICO);
    AssertHelper.fieldNotNull(datosConvocatoriaDocumento.getDocumentoRef(), ConvocatoriaDocumento.class,
        MSG_KEY_DOCUMENTO_REF);

    log.debug("validarRequeridosConvocatoriaDocumento(ConvocatoriaDocumento datosConvocatoriaDocumento) - end");
  }

  @Override
  public boolean existsByConvocatoriaId(Long convocatoriaId) {
    return repository.existsByConvocatoriaId(convocatoriaId);
  }

}

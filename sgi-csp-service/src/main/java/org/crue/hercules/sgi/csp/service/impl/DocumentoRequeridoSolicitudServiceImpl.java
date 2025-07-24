package org.crue.hercules.sgi.csp.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput.TipoDocumento;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.DocumentoRequeridoSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.DocumentoRequeridoSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.specification.DocumentoRequeridoSolicitudSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.csp.service.DocumentoRequeridoSolicitudService;
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
 * Service Implementation para gestion {@link DocumentoRequeridoSolicitud}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class DocumentoRequeridoSolicitudServiceImpl implements DocumentoRequeridoSolicitudService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_MODELO = "modelo";
  private static final String MSG_KEY_MSG = "msg";
  private static final String MSG_KEY_ACTION = "action";
  private static final String MSG_FIELD_ACTION_CREAR = "action.crear";
  private static final String MSG_FIELD_ACTION_MODIFICAR = "action.modificar";
  private static final String MSG_FIELD_ACTION_ELIMINAR = "action.eliminar";
  private static final String MSG_DOCUMENTO_REQUERIDO = "documentoRequerido";
  private static final String MSG_MODEL_MODELO_TIPO_FASE = "org.crue.hercules.sgi.csp.model.ModeloTipoFase.message";
  private static final String MSG_MODEL_TIPO_FASE = "org.crue.hercules.sgi.csp.model.TipoFase.message";
  private static final String MSG_MODEL_DOCUMENTO_REQUERIDO_SOLICITUD = "org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud.message";
  private static final String MSG_MODEL_MODELO_TIPO_DOCUMENTO = "org.crue.hercules.sgi.csp.model.ModeloTipoDocumento.message";
  private static final String MSG_MODEL_TIPO_DOCUMENTO = "org.crue.hercules.sgi.csp.model.TipoDocumento.message";
  private static final String MSG_ENTITY_INACTIVO = "org.springframework.util.Assert.inactivo.message";
  private static final String MSG_MODELO_EJECUCION_INACTIVO = "org.springframework.util.Assert.entity.modeloEjecucion.inactivo.message";
  private static final String MSG_CONVOCATORIA_ASSIGN_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.exceptions.AssignModeloEjecucion.message";
  private static final String MSG_CONVOCATORIA_SIN_MODELO_ASIGNADO = "org.crue.hercules.sgi.csp.model.Convocatoria.sinModeloEjecucion.message";
  private static final String MSG_NO_DISPONIBLE_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.model.ModeloEjecucion.noDisponible.message";
  private static final String MSG_PROBLEM_ACCION_DENEGADA_PERMISOS = "org.springframework.util.Assert.accion.denegada.permisos.message";

  private final DocumentoRequeridoSolicitudRepository repository;
  private final ConfiguracionSolicitudRepository configuracionSolicitudRepository;
  private final ModeloTipoFaseRepository modeloTipoFaseRepository;
  private final ModeloTipoDocumentoRepository modeloTipoDocumentoRepository;
  private final ConvocatoriaService convocatoriaService;
  private final ConvocatoriaRepository convocatoriaRepository;

  public DocumentoRequeridoSolicitudServiceImpl(DocumentoRequeridoSolicitudRepository repository,
      ConfiguracionSolicitudRepository configuracionSolicitudRepository,
      ModeloTipoFaseRepository modeloTipoFaseRepository, ModeloTipoDocumentoRepository modeloTipoDocumentoRepository,
      ConvocatoriaService convocatoriaService, ConvocatoriaRepository convocatoriaRepository) {
    this.repository = repository;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
    this.modeloTipoFaseRepository = modeloTipoFaseRepository;
    this.modeloTipoDocumentoRepository = modeloTipoDocumentoRepository;
    this.convocatoriaService = convocatoriaService;
    this.convocatoriaRepository = convocatoriaRepository;
  }

  /**
   * Guarda la entidad {@link DocumentoRequeridoSolicitud}.
   * 
   * @param documentoRequeridoSolicitud la entidad
   *                                    {@link DocumentoRequeridoSolicitud} a
   *                                    guardar.
   * @return DocumentoRequeridoSolicitud la entidad
   *         {@link DocumentoRequeridoSolicitud} persistida.
   */
  @Override
  @Transactional
  public DocumentoRequeridoSolicitud create(DocumentoRequeridoSolicitud documentoRequeridoSolicitud) {
    log.debug("create(DocumentoRequeridoSolicitud documentoRequeridoSolicitud) - start");

    AssertHelper.idIsNull(documentoRequeridoSolicitud.getId(), DocumentoRequeridoSolicitud.class);

    validarDocumentoRequeridoSolicitud(documentoRequeridoSolicitud, null, new String[] {
        ConvocatoriaAuthorityHelper.CSP_CON_C,
        ConvocatoriaAuthorityHelper.CSP_CON_E
    });

    DocumentoRequeridoSolicitud returnValue = repository.save(documentoRequeridoSolicitud);

    log.debug("create(DocumentoRequeridoSolicitud documentoRequeridoSolicitud) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link DocumentoRequeridoSolicitud}
   * 
   * @param documentoRequeridoSolicitud {@link DocumentoRequeridoSolicitud} a
   *                                    actualizar.
   * @return DocumentoRequeridoSolicitud {@link DocumentoRequeridoSolicitud}
   *         actualizado.
   */
  @Override
  @Transactional
  public DocumentoRequeridoSolicitud update(DocumentoRequeridoSolicitud documentoRequeridoSolicitud) {
    log.debug("update(DocumentoRequeridoSolicitud documentoRequeridoSolicitud) - start");

    AssertHelper.idNotNull(documentoRequeridoSolicitud.getId(), DocumentoRequeridoSolicitud.class);

    return repository.findById(documentoRequeridoSolicitud.getId()).map(datosOriginales -> {

      validarDocumentoRequeridoSolicitud(documentoRequeridoSolicitud, datosOriginales,
          new String[] { ConvocatoriaAuthorityHelper.CSP_CON_E });

      datosOriginales.setTipoDocumento(documentoRequeridoSolicitud.getTipoDocumento());
      datosOriginales.setObservaciones(documentoRequeridoSolicitud.getObservaciones());

      DocumentoRequeridoSolicitud returnValue = repository.save(datosOriginales);

      log.debug("update(DocumentoRequeridoSolicitud documentoRequeridoSolicitud) - end");
      return returnValue;
    }).orElseThrow(() -> new DocumentoRequeridoSolicitudNotFoundException(documentoRequeridoSolicitud.getId()));
  }

  /**
   * Elimina la {@link DocumentoRequeridoSolicitud}.
   *
   * @param id Id del {@link DocumentoRequeridoSolicitud}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, DocumentoRequeridoSolicitud.class);

    Optional<DocumentoRequeridoSolicitud> documentoRequeridoSolicitud = repository.findById(id);
    if (documentoRequeridoSolicitud.isPresent()) {
      ConfiguracionSolicitud configuracionSolicitud = configuracionSolicitudRepository
          .findById(documentoRequeridoSolicitud.get().getConfiguracionSolicitudId())
          .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(
              documentoRequeridoSolicitud.get().getConfiguracionSolicitudId()));
      Assert.isTrue(
          convocatoriaService.isRegistradaConSolicitudesOProyectos(configuracionSolicitud.getConvocatoriaId(), null,
              new String[] { ConvocatoriaAuthorityHelper.CSP_CON_E }),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_ACCION_DENEGADA_PERMISOS)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_DOCUMENTO_REQUERIDO_SOLICITUD))
              .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_ELIMINAR))
              .build());

    } else {
      throw new DocumentoRequeridoSolicitudNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene una entidad {@link DocumentoRequeridoSolicitud} por el id.
   * 
   * @param id Identificador de {@link DocumentoRequeridoSolicitud}.
   * @return DocumentoRequeridoSolicitud la entidad
   *         {@link DocumentoRequeridoSolicitud}.
   */
  @Override
  public DocumentoRequeridoSolicitud findById(Long id) {
    log.debug("findById(Long id) - start");
    final DocumentoRequeridoSolicitud returnValue = repository.findById(id)
        .orElseThrow(() -> new DocumentoRequeridoSolicitudNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link DocumentoRequeridoSolicitud} para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link DocumentoRequeridoSolicitud} de la
   *         {@link Convocatoria} paginadas.
   */
  public Page<DocumentoRequeridoSolicitud> findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) {
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - start");
    Specification<DocumentoRequeridoSolicitud> specs = DocumentoRequeridoSolicitudSpecifications
        .byConvocatoriaId(convocatoriaId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<DocumentoRequeridoSolicitud> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Comprueba, valida y tranforma los datos de la
   * {@link DocumentoRequeridoSolicitud} antes de utilizados para crear o
   * modificar la entidad
   *
   * @param datosDocumentoRequeridoSolicitud
   * @param datosOriginales
   * @param authorities
   */
  private void validarDocumentoRequeridoSolicitud(DocumentoRequeridoSolicitud documentoRequeridoSolicitud,
      DocumentoRequeridoSolicitud datosOriginales, String[] authorities) {
    log.debug(
        "validarDocumentoRequeridoSolicitud(DocumentoRequeridoSolicitud datosDocumentoRequeridoSolicitud, DocumentoRequeridoSolicitud datosOriginales) - start");

    /** Obligatoria Configuración Solicitud */
    AssertHelper.idNotNull(documentoRequeridoSolicitud.getConfiguracionSolicitudId(), ConfiguracionSolicitud.class);

    /** Obligatorio TipoDocumento */
    AssertHelper.entityNotNull(documentoRequeridoSolicitud.getTipoDocumento(), DocumentoRequeridoSolicitud.class,
        TipoDocumento.class);
    AssertHelper.idNotNull(documentoRequeridoSolicitud.getTipoDocumento().getId(), TipoDocumento.class);

    /** Se recupera la Configuración Solicitud para la convocatoria */
    ConfiguracionSolicitud configuracionSolicitud = configuracionSolicitudRepository
        .findById(documentoRequeridoSolicitud.getConfiguracionSolicitudId())
        .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(
            documentoRequeridoSolicitud.getConfiguracionSolicitudId()));

    // comprobar si convocatoria es modificable
    Assert.isTrue(
        convocatoriaService.isRegistradaConSolicitudesOProyectos(configuracionSolicitud.getConvocatoriaId(), null,
            authorities),
        () -> ProblemMessage.builder()
            .key(MSG_PROBLEM_ACCION_DENEGADA_PERMISOS)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_DOCUMENTO_REQUERIDO_SOLICITUD))
            .parameter(MSG_KEY_ACTION,
                ((datosOriginales != null) ? ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_MODIFICAR)
                    : ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_CREAR)))
            .build());

    // Se recupera el Id de ModeloEjecucion para las siguientes validaciones
    Convocatoria convocatoria = convocatoriaRepository.findById(configuracionSolicitud.getConvocatoriaId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(configuracionSolicitud.getConvocatoriaId()));
    Long modeloEjecucionId = (convocatoria.getModeloEjecucion() != null
        && convocatoria.getModeloEjecucion().getId() != null) ? convocatoria.getModeloEjecucion().getId() : null;
    /**
     * La fase no es obligatoria en la configuración, pero si lo es para añadir un
     * documento requerido, es necesario recuperar el ModeloTipoFase para validar el
     * TipoDocumento asignado
     */

    TipoFase configuracionTipoFase = (configuracionSolicitud.getFasePresentacionSolicitudes() != null
        && configuracionSolicitud.getFasePresentacionSolicitudes().getId() != null)
            ? configuracionSolicitud.getFasePresentacionSolicitudes().getTipoFase()
            : null;
    ModeloTipoFase configuracionModeloTipoFase = null;

    Assert.isTrue(!(configuracionTipoFase == null || configuracionTipoFase.getId() == null),
        ApplicationContextSupport.getMessage(MSG_DOCUMENTO_REQUERIDO));

    Optional<ModeloTipoFase> modeloTipoFase = modeloTipoFaseRepository
        .findByModeloEjecucionIdAndTipoFaseId(modeloEjecucionId, configuracionTipoFase.getId());

    // TipoFase está asignado al ModeloEjecucion
    Assert.isTrue(modeloTipoFase.isPresent(),
        () -> ProblemMessage.builder()
            .key(MSG_CONVOCATORIA_ASSIGN_MODELO_EJECUCION)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FASE))
            .parameter(MSG_KEY_MSG, ApplicationContextSupport.getMessage(MSG_NO_DISPONIBLE_MODELO_EJECUCION))
            .parameter(MSG_KEY_MODELO, ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                : ApplicationContextSupport.getMessage(MSG_CONVOCATORIA_SIN_MODELO_ASIGNADO)))
            .build());

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

    configuracionModeloTipoFase = modeloTipoFase.get();

    /**
     * TipoDocumento es obligatorio, y además debe estar asociado al modelo de
     * ejecución de la convocatoria y a la Fase del plazo de presentación de
     * solicitudes de la configuración.
     */

    // TipoDocumento
    Optional<ModeloTipoDocumento> modeloTipoDocumento = modeloTipoDocumentoRepository
        .findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(modeloEjecucionId,
            configuracionModeloTipoFase.getId(), documentoRequeridoSolicitud.getTipoDocumento().getId());

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
    if (datosOriginales == null
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

      documentoRequeridoSolicitud.setTipoDocumento(modeloTipoDocumento.get().getTipoDocumento());

    }

    log.debug(
        "validarDocumentoRequeridoSolicitud(DocumentoRequeridoSolicitud datosDocumentoRequeridoSolicitud, DocumentoRequeridoSolicitud datosOriginales) - end");
  }

}

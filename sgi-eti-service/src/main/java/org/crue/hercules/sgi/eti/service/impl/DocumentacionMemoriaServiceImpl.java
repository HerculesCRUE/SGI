package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.exceptions.DocumentacionMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.TipoDocumentoNotFoundException;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.repository.DocumentacionMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.FormularioRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.eti.repository.specification.DocumentacionMemoriaSpecifications;
import org.crue.hercules.sgi.eti.service.DocumentacionMemoriaService;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.framework.util.AssertHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link DocumentacionMemoria}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class DocumentacionMemoriaServiceImpl implements DocumentacionMemoriaService {
  private static final String MSG_LA_MEMORIA_NO_SE_ENCUENTRA_EN_UN_ESTADO_ADECUADO = "documentacionMemoria.estado.invalido";
  private static final String MSG_LA_RETROSPECTIVA_NO_SE_ENCUENTRA_EN_UN_ESTADO_ADECUADO = "documentacionMemoria.estado.invalido.retrospectiva";

  /** Documentacion memoria repository */
  private final DocumentacionMemoriaRepository documentacionMemoriaRepository;

  /** Memoria repository */
  private final MemoriaRepository memoriaRepository;

  /** Tipo Memoria repository */
  private final TipoDocumentoRepository tipoDocumentoRepository;

  /** Tipo Memoria repository */
  private final FormularioRepository formularioRepository;

  public DocumentacionMemoriaServiceImpl(DocumentacionMemoriaRepository documentacionMemoriaRepository,
      MemoriaRepository memoriaRepository, TipoDocumentoRepository tipoDocumentoRepository,
      FormularioRepository formularioRepository) {
    this.documentacionMemoriaRepository = documentacionMemoriaRepository;
    this.memoriaRepository = memoriaRepository;
    this.tipoDocumentoRepository = tipoDocumentoRepository;
    this.formularioRepository = formularioRepository;
  }

  /**
   * Crea {@link DocumentacionMemoria} inicial de una memoria (aquella
   * documentación que no es de seguimiento anual, final o retrospectiva).
   *
   * @param idMemoria            Id de la {@link Memoria}
   * @param documentacionMemoria la entidad {@link DocumentacionMemoria} a
   *                             guardar.
   * @return la entidad {@link DocumentacionMemoria} persistida.
   */
  @Override
  @Transactional
  public DocumentacionMemoria createDocumentacionInicial(Long idMemoria, DocumentacionMemoria documentacionMemoria) {
    log.debug("Petición a create DocumentacionMemoria : {} - start", documentacionMemoria);
    AssertHelper.idIsNull(documentacionMemoria.getId(), DocumentacionMemoria.class);
    AssertHelper.idNotNull(idMemoria, Memoria.class);

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {
      TipoEstadoMemoria.Tipo estado = TipoEstadoMemoria.Tipo.fromId(memoria.getEstadoActual().getId());

      Assert.isTrue((estado.getId() >= TipoEstadoMemoria.Tipo.EN_EVALUACION.getId()),
          ApplicationContextSupport
              .getMessage(MSG_LA_MEMORIA_NO_SE_ENCUENTRA_EN_UN_ESTADO_ADECUADO));

      documentacionMemoria.setMemoria(memoria);
      return documentacionMemoriaRepository.save(documentacionMemoria);
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

  }

  /**
   * Crea {@link DocumentacionMemoria} inicial de una memoria (aquella
   * documentación que no es de seguimiento anual, final o retrospectiva).
   *
   * @param idMemoria            Id de la {@link Memoria}
   * @param documentacionMemoria la entidad {@link DocumentacionMemoria} a
   *                             guardar.
   * @return la entidad {@link DocumentacionMemoria} persistida.
   */
  @Override
  @Transactional
  public DocumentacionMemoria createDocumentacionInicialInvestigador(Long idMemoria,
      DocumentacionMemoria documentacionMemoria) {
    log.debug("createDocumentacionInicialInvestigador({}, {}) - start", idMemoria, documentacionMemoria);
    AssertHelper.idIsNull(documentacionMemoria.getId(), DocumentacionMemoria.class);
    AssertHelper.idNotNull(idMemoria, Memoria.class);

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {
      TipoEstadoMemoria.Tipo estado = TipoEstadoMemoria.Tipo.fromId(memoria.getEstadoActual().getId());

      Assert.isTrue(
          (estado == TipoEstadoMemoria.Tipo.EN_ELABORACION || estado == TipoEstadoMemoria.Tipo.COMPLETADA
              || estado == TipoEstadoMemoria.Tipo.FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS
              || estado == TipoEstadoMemoria.Tipo.PENDIENTE_CORRECCIONES
              || estado == TipoEstadoMemoria.Tipo.NO_PROCEDE_EVALUAR
              || estado.getId() >= TipoEstadoMemoria.Tipo.FIN_EVALUACION.getId()),
          ApplicationContextSupport
              .getMessage(MSG_LA_MEMORIA_NO_SE_ENCUENTRA_EN_UN_ESTADO_ADECUADO));

      documentacionMemoria.setMemoria(memoria);
      return documentacionMemoriaRepository.save(documentacionMemoria);
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

  }

  /**
   * Obtiene una entidad {@link DocumentacionMemoria} por id.
   *
   * @param id el id de la entidad {@link DocumentacionMemoria}.
   * @return la entidad {@link DocumentacionMemoria}.
   * @throws DocumentacionMemoriaNotFoundException Si no existe ningún
   *                                               {@link DocumentacionMemoria}
   *                                               con ese id.
   */

  public DocumentacionMemoria findById(final Long id) throws DocumentacionMemoriaNotFoundException {
    log.debug("Petición a get DocumentacionMemoria : {}  - start", id);
    final DocumentacionMemoria documentacionMemoria = documentacionMemoriaRepository.findById(id)
        .orElseThrow(() -> new DocumentacionMemoriaNotFoundException(id));
    log.debug("Petición a get DocumentacionMemoria : {}  - end", id);
    return documentacionMemoria;

  }

  /**
   * Obtener todas las entidades paginadas {@link DocumentacionMemoria} para una
   * determinada {@link Memoria}.
   *
   * @param idMemoria      Id de {@link Memoria}.
   * @param tipoEvaluacion Id de {@link TipoEvaluacion.Tipo}
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  @Override
  public Page<DocumentacionMemoria> findByMemoriaIdAndTipoEvaluacion(Long idMemoria, TipoEvaluacion.Tipo tipoEvaluacion,
      Pageable pageable) {
    log.debug(
        "findByMemoriaIdAndTipoEvaluacion(Long idMemoria, TipoEvaluacion.Tipo tipoEvaluacion, Pageable pageable) - start");
    AssertHelper.idNotNull(idMemoria, Memoria.class);
    AssertHelper.entityNotNull(tipoEvaluacion, DocumentacionMemoria.class, TipoEvaluacion.class);

    Memoria memoria = memoriaRepository.findById(idMemoria).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

    Page<DocumentacionMemoria> returnValue = null;

    // TipoEvaluación Retrospectiva muestra la documentación de tipo Retrospectiva
    if (tipoEvaluacion == TipoEvaluacion.Tipo.RETROSPECTIVA) {
      returnValue = documentacionMemoriaRepository.findByMemoriaIdAndTipoDocumentoFormularioId(idMemoria,
          memoria.getFormularioRetrospectiva() != null ? memoria.getFormularioRetrospectiva().getId() : null, pageable);
    }
    // TipoEvaluación Memoria muestra todas la documentación que no sea de tipo
    // Retrospectiva, Seguimiento Anual o Seguimiento Final
    if (tipoEvaluacion == TipoEvaluacion.Tipo.MEMORIA) {
      returnValue = documentacionMemoriaRepository.findByMemoriaIdAndTipoDocumentoFormularioId(idMemoria,
          memoria.getFormulario().getId(), pageable);
    }
    // TipoEvaluación Seguimiento Anual muestra la documentación de tipo Seguimiento
    // Anual
    if (tipoEvaluacion == TipoEvaluacion.Tipo.SEGUIMIENTO_ANUAL) {
      returnValue = documentacionMemoriaRepository.findByMemoriaIdAndTipoDocumentoFormularioId(idMemoria,
          memoria.getFormularioSeguimientoAnual().getId(), pageable);
    }
    // TipoEvaluación Seguimiento Final muestra la documentación de tipo Seguimiento
    // Final
    if (tipoEvaluacion == TipoEvaluacion.Tipo.SEGUIMIENTO_FINAL) {
      returnValue = documentacionMemoriaRepository.findByMemoriaIdAndTipoDocumentoFormularioId(idMemoria,
          memoria.getFormularioSeguimientoFinal().getId(), pageable);
    }

    log.debug(
        "findByMemoriaIdAndTipoEvaluacion(Long idMemoria, TipoEvaluacion.Tipo tipoEvaluacion, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} asociadas al
   * {@link Formulario} de la {@link Memoria}.
   * 
   * @param idMemoria Id de {@link Memoria}.
   * @param pageable  la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  @Override
  public Page<DocumentacionMemoria> findDocumentacionMemoria(Long idMemoria, Pageable pageable) {
    log.debug("findDocumentacionMemoria(Long idMemoria, Pageable pageable) - start");
    AssertHelper.idNotNull(idMemoria, Memoria.class);

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Specification<DocumentacionMemoria> specMemoriaId = DocumentacionMemoriaSpecifications.memoriaId(idMemoria);

      Specification<DocumentacionMemoria> specTipoDocumentoNotIn = DocumentacionMemoriaSpecifications
          .tipoDocumentoFormularioId(memoria.getFormulario().getId());

      Specification<DocumentacionMemoria> specs = Specification.where(specMemoriaId).and(specTipoDocumentoNotIn);

      Page<DocumentacionMemoria> returnValue = documentacionMemoriaRepository.findAll(specs, pageable);

      log.debug("findDocumentacionMemoria(Long idMemoria, Pageable pageable) - end");
      return returnValue;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

  }

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} asociadas al
   * {@link Formulario} de la {@link Memoria} del tipo Seguimiento Anual.
   * 
   * @param idMemoria Id de {@link Memoria}.
   * @param pageable  la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  @Override
  public Page<DocumentacionMemoria> findDocumentacionSeguimientoAnual(Long idMemoria, Pageable pageable) {
    log.debug("findDocumentacionSeguimientoAnual(Long idMemoria, Pageable pageable) - start");
    AssertHelper.idNotNull(idMemoria, Memoria.class);

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Specification<DocumentacionMemoria> specMemoriaId = DocumentacionMemoriaSpecifications.memoriaId(idMemoria);

      // Aquellos que no son del tipo 1: Seguimiento Anual
      Specification<DocumentacionMemoria> specTipoDocumento = DocumentacionMemoriaSpecifications
          .tipoDocumentoFormularioId(memoria.getFormularioSeguimientoAnual().getId());

      Specification<DocumentacionMemoria> specs = Specification.where(specMemoriaId).and(specTipoDocumento);

      Page<DocumentacionMemoria> returnValue = documentacionMemoriaRepository.findAll(specs, pageable);

      log.debug("findDocumentacionSeguimientoAnual(Long idMemoria, Pageable pageable) - end");
      return returnValue;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

  }

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} asociadas al
   * {@link Formulario} de la {@link Memoria} del tipo Seguimiento Final.
   * 
   * @param idMemoria Id de {@link Memoria}.
   * @param pageable  la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  @Override
  public Page<DocumentacionMemoria> findDocumentacionSeguimientoFinal(Long idMemoria, Pageable pageable) {
    log.debug("findDocumentacionSeguimientoFinal(Long idMemoria, Pageable pageable) - start");
    AssertHelper.idNotNull(idMemoria, Memoria.class);

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Specification<DocumentacionMemoria> specMemoriaId = DocumentacionMemoriaSpecifications.memoriaId(idMemoria);

      Specification<DocumentacionMemoria> specTipoDocumento = DocumentacionMemoriaSpecifications
          .tipoDocumentoFormularioId(memoria.getFormularioSeguimientoFinal().getId());

      Specification<DocumentacionMemoria> specs = Specification.where(specMemoriaId).and(specTipoDocumento);

      Page<DocumentacionMemoria> returnValue = documentacionMemoriaRepository.findAll(specs, pageable);

      log.debug("findDocumentacionSeguimientoFinal(Long idMemoria, Pageable pageable) - end");
      return returnValue;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

  }

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} asociadas al
   * {@link Formulario} de la {@link Memoria} del tipo Retrospectiva.
   * 
   * @param idMemoria Id de {@link Memoria}.
   * @param pageable  la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  @Override
  public Page<DocumentacionMemoria> findDocumentacionRetrospectiva(Long idMemoria, Pageable pageable) {
    log.debug("findDocumentacionRetrospectiva(Long idMemoria, Pageable pageable) - start");
    AssertHelper.idNotNull(idMemoria, Memoria.class);

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {
      Page<DocumentacionMemoria> returnValue = Page.empty(pageable);
      if (memoria.getFormularioRetrospectiva() != null && memoria.getFormularioRetrospectiva().getId() != null) {

        Specification<DocumentacionMemoria> specMemoriaId = DocumentacionMemoriaSpecifications.memoriaId(idMemoria);

        Specification<DocumentacionMemoria> specTipoDocumento = DocumentacionMemoriaSpecifications
            .tipoDocumentoFormularioId(memoria.getFormularioRetrospectiva().getId());

        Specification<DocumentacionMemoria> specs = Specification.where(specMemoriaId).and(specTipoDocumento);

        returnValue = documentacionMemoriaRepository.findAll(specs, pageable);
      }

      log.debug("findDocumentacionRetrospectiva(Long idMemoria, Pageable pageable) - end");
      return returnValue;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

  }

  /**
   * Crea un {@link DocumentacionMemoria} del tipo seguimiento anual.
   * 
   * @param idMemoria            Id de {@link Memoria}
   * @param documentacionMemoria {@link DocumentacionMemoria} a crear
   * @return {@link DocumentacionMemoria} creada
   */
  @Transactional
  @Override
  public DocumentacionMemoria createSeguimientoAnual(Long idMemoria, @Valid DocumentacionMemoria documentacionMemoria) {
    log.debug("Petición a create DocumentacionMemoria seguimiento anual : {} - start", documentacionMemoria);
    AssertHelper.idIsNull(documentacionMemoria.getId(), DocumentacionMemoria.class);
    AssertHelper.idNotNull(idMemoria, Memoria.class);

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {
      TipoEstadoMemoria.Tipo estado = TipoEstadoMemoria.Tipo.fromId(memoria.getEstadoActual().getId());
      Assert.isTrue(
          (estado == TipoEstadoMemoria.Tipo.FIN_EVALUACION
              || estado == TipoEstadoMemoria.Tipo.COMPLETADA_SEGUIMIENTO_ANUAL),
          ApplicationContextSupport
              .getMessage(MSG_LA_MEMORIA_NO_SE_ENCUENTRA_EN_UN_ESTADO_ADECUADO));

      documentacionMemoria.setMemoria(memoria);
      List<TipoDocumento> tiposDocumento = tipoDocumentoRepository
          .findByFormularioIdAndActivoTrue(memoria.getFormularioSeguimientoAnual().getId());
      if (tiposDocumento.size() != 1) {
        throw new TipoDocumentoNotFoundException(null);
      }
      documentacionMemoria.setTipoDocumento(tiposDocumento.get(0));
      return documentacionMemoriaRepository.save(documentacionMemoria);
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));
  }

  /**
   * Crea un {@link DocumentacionMemoria} del tipo seguimiento final.
   * 
   * @param idMemoria            Id de {@link Memoria}
   * @param documentacionMemoria {@link DocumentacionMemoria} a crear
   * @return {@link DocumentacionMemoria} creada
   */
  @Transactional
  @Override
  public DocumentacionMemoria createSeguimientoFinal(Long idMemoria, @Valid DocumentacionMemoria documentacionMemoria) {
    log.debug("Petición a create DocumentacionMemoria seguimiento final : {} - start", documentacionMemoria);
    AssertHelper.idIsNull(documentacionMemoria.getId(), DocumentacionMemoria.class);
    AssertHelper.idNotNull(idMemoria, Memoria.class);

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {
      TipoEstadoMemoria.Tipo estado = TipoEstadoMemoria.Tipo.fromId(memoria.getEstadoActual().getId());
      Assert.isTrue(
          (estado == TipoEstadoMemoria.Tipo.FIN_EVALUACION_SEGUIMIENTO_ANUAL
              || estado == TipoEstadoMemoria.Tipo.COMPLETADA_SEGUIMIENTO_FINAL
              || estado == TipoEstadoMemoria.Tipo.EN_ACLARACION_SEGUIMIENTO_FINAL),
          ApplicationContextSupport
              .getMessage(MSG_LA_MEMORIA_NO_SE_ENCUENTRA_EN_UN_ESTADO_ADECUADO));

      documentacionMemoria.setMemoria(memoria);

      List<TipoDocumento> tiposDocumento = tipoDocumentoRepository
          .findByFormularioIdAndActivoTrue(memoria.getFormularioSeguimientoFinal().getId());
      if (tiposDocumento.size() != 1) {
        throw new TipoDocumentoNotFoundException(null);
      }
      documentacionMemoria.setTipoDocumento(tiposDocumento.get(0));
      return documentacionMemoriaRepository.save(documentacionMemoria);

    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));
  }

  /**
   * Crea un {@link DocumentacionMemoria} del tipo retrospectiva.
   * 
   * @param idMemoria            Id de {@link Memoria}
   * @param documentacionMemoria {@link DocumentacionMemoria} a crear
   * @return {@link DocumentacionMemoria} creada
   */
  @Transactional
  @Override
  public DocumentacionMemoria createRetrospectiva(Long idMemoria, @Valid DocumentacionMemoria documentacionMemoria) {
    log.debug("Petición a create DocumentacionMemoria retrospectiva : {} - start", documentacionMemoria);
    AssertHelper.idIsNull(documentacionMemoria.getId(), DocumentacionMemoria.class);
    AssertHelper.idNotNull(idMemoria, Memoria.class);

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      AssertHelper.entityNotNull(memoria.getRetrospectiva(), Memoria.class, Retrospectiva.class);

      EstadoRetrospectiva.Tipo estado = EstadoRetrospectiva.Tipo
          .fromId(memoria.getRetrospectiva().getEstadoRetrospectiva().getId());
      Assert.isTrue((estado == EstadoRetrospectiva.Tipo.PENDIENTE || estado == EstadoRetrospectiva.Tipo.COMPLETADA),
          ApplicationContextSupport
              .getMessage(MSG_LA_RETROSPECTIVA_NO_SE_ENCUENTRA_EN_UN_ESTADO_ADECUADO));

      documentacionMemoria.setMemoria(memoria);
      List<TipoDocumento> tiposDocumento = tipoDocumentoRepository
          .findByFormularioIdAndActivoTrue(memoria.getFormularioRetrospectiva().getId());
      if (tiposDocumento.size() != 1) {
        throw new TipoDocumentoNotFoundException(null);
      }
      documentacionMemoria.setTipoDocumento(tiposDocumento.get(0));
      return documentacionMemoriaRepository.save(documentacionMemoria);

    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));
  }

  /**
   * Elimina {@link DocumentacionMemoria} del tipo seguimiento anual.
   * 
   * @param idMemoria              Id {@link Memoria}
   * @param idDocumentacionMemoria Id {@link DocumentacionMemoria}
   */
  @Transactional
  @Override
  public void deleteDocumentacionSeguimientoAnual(Long idMemoria, Long idDocumentacionMemoria) {
    log.debug("deleteDocumentacionSeguimientoAnual(Long idMemoria, Long idDocumentacionMemoria) -- start");

    AssertHelper.idNotNull(idDocumentacionMemoria, DocumentacionMemoria.class);

    AssertHelper.idNotNull(idMemoria, Memoria.class);

    Memoria memoria = memoriaRepository.findByIdAndActivoTrue(idMemoria)
        .orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

    TipoEstadoMemoria.Tipo estado = TipoEstadoMemoria.Tipo.fromId(memoria.getEstadoActual().getId());
    Assert.isTrue(
        (estado == TipoEstadoMemoria.Tipo.FIN_EVALUACION
            || estado == TipoEstadoMemoria.Tipo.COMPLETADA_SEGUIMIENTO_ANUAL),
        ApplicationContextSupport
            .getMessage(MSG_LA_MEMORIA_NO_SE_ENCUENTRA_EN_UN_ESTADO_ADECUADO));

    DocumentacionMemoria documentacionMemoria = documentacionMemoriaRepository
        .findByIdAndMemoriaIdAndTipoDocumentoFormularioIdAndMemoriaActivoTrue(idDocumentacionMemoria, idMemoria,
            memoria.getFormularioSeguimientoAnual().getId())
        .orElseThrow(() -> new DocumentacionMemoriaNotFoundException(idDocumentacionMemoria));

    documentacionMemoriaRepository.delete(documentacionMemoria);
  }

  /**
   * Elimina {@link DocumentacionMemoria} del tipo seguimiento final.
   * 
   * @param idMemoria              Id {@link Memoria}
   * @param idDocumentacionMemoria Id {@link DocumentacionMemoria}
   */
  @Transactional
  @Override
  public void deleteDocumentacionSeguimientoFinal(Long idMemoria, Long idDocumentacionMemoria) {
    log.debug("deleteDocumentacionSeguimientoFinal(Long idMemoria, Long idDocumentacionMemoria) -- start");

    AssertHelper.idNotNull(idDocumentacionMemoria, DocumentacionMemoria.class);
    AssertHelper.idNotNull(idMemoria, Memoria.class);

    Memoria memoria = memoriaRepository.findByIdAndActivoTrue(idMemoria)
        .orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

    TipoEstadoMemoria.Tipo estado = TipoEstadoMemoria.Tipo.fromId(memoria.getEstadoActual().getId());
    Assert.isTrue(
        (estado == TipoEstadoMemoria.Tipo.FIN_EVALUACION_SEGUIMIENTO_ANUAL
            || estado == TipoEstadoMemoria.Tipo.COMPLETADA_SEGUIMIENTO_FINAL
            || estado == TipoEstadoMemoria.Tipo.EN_ACLARACION_SEGUIMIENTO_FINAL),
        ApplicationContextSupport
            .getMessage(MSG_LA_MEMORIA_NO_SE_ENCUENTRA_EN_UN_ESTADO_ADECUADO));

    DocumentacionMemoria documentacionMemoria = documentacionMemoriaRepository
        .findByIdAndMemoriaIdAndTipoDocumentoFormularioIdAndMemoriaActivoTrue(idDocumentacionMemoria, idMemoria,
            memoria.getFormularioSeguimientoFinal().getId())
        .orElseThrow(() -> new DocumentacionMemoriaNotFoundException(idDocumentacionMemoria));

    documentacionMemoriaRepository.delete(documentacionMemoria);
  }

  /**
   * Elimina {@link DocumentacionMemoria} del tipo retrospectiva.
   * 
   * @param idMemoria              Id {@link Memoria}
   * @param idDocumentacionMemoria Id {@link DocumentacionMemoria}
   */
  @Transactional
  @Override
  public void deleteDocumentacionRetrospectiva(Long idMemoria, Long idDocumentacionMemoria) {
    log.debug("deleteDocumentacionRetrospectiva(Long idMemoria, Long idDocumentacionMemoria) -- start");

    AssertHelper.idNotNull(idDocumentacionMemoria, DocumentacionMemoria.class);
    AssertHelper.idNotNull(idMemoria, Memoria.class);

    Memoria memoria = memoriaRepository.findByIdAndActivoTrue(idMemoria)
        .orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

    AssertHelper.entityNotNull(memoria.getRetrospectiva(), DocumentacionMemoria.class, Retrospectiva.class);

    EstadoRetrospectiva.Tipo estado = EstadoRetrospectiva.Tipo
        .fromId(memoria.getRetrospectiva().getEstadoRetrospectiva().getId());
    Assert.isTrue((estado == EstadoRetrospectiva.Tipo.PENDIENTE || estado == EstadoRetrospectiva.Tipo.COMPLETADA),
        ApplicationContextSupport
            .getMessage(MSG_LA_RETROSPECTIVA_NO_SE_ENCUENTRA_EN_UN_ESTADO_ADECUADO));

    DocumentacionMemoria documentacionMemoria = documentacionMemoriaRepository
        .findByIdAndMemoriaIdAndTipoDocumentoFormularioIdAndMemoriaActivoTrue(idDocumentacionMemoria, idMemoria,
            memoria.getFormularioRetrospectiva().getId())
        .orElseThrow(() -> new DocumentacionMemoriaNotFoundException(idDocumentacionMemoria));

    documentacionMemoriaRepository.delete(documentacionMemoria);

  }

  /**
   * Elimina {@link DocumentacionMemoria} inicial.
   * 
   * @param idMemoria              Id {@link Memoria}
   * @param idDocumentacionMemoria Id {@link DocumentacionMemoria}
   * @param authentication         Authentication
   */
  @Transactional
  @Override
  public void deleteDocumentacionInicial(Long idMemoria, Long idDocumentacionMemoria, Authentication authentication) {
    log.debug("deleteDocumentacionInicial(Long idMemoria, Long idDocumentacionMemoria) -- start");

    AssertHelper.idNotNull(idDocumentacionMemoria, DocumentacionMemoria.class);
    AssertHelper.idNotNull(idMemoria, Memoria.class);

    Boolean isGestor = authentication.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().startsWith("ETI-MEM-EDOC"));

    Memoria memoria = memoriaRepository.findByIdAndActivoTrue(idMemoria)
        .orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

    TipoEstadoMemoria.Tipo estado = TipoEstadoMemoria.Tipo.fromId(memoria.getEstadoActual().getId());
    if (isGestor.booleanValue()) {
      Assert.isTrue((estado.getId() >= TipoEstadoMemoria.Tipo.EN_EVALUACION.getId()),
          ApplicationContextSupport
              .getMessage(MSG_LA_MEMORIA_NO_SE_ENCUENTRA_EN_UN_ESTADO_ADECUADO));
    } else {
      Assert.isTrue(
          (estado == TipoEstadoMemoria.Tipo.EN_ELABORACION || estado == TipoEstadoMemoria.Tipo.COMPLETADA
              || estado == TipoEstadoMemoria.Tipo.FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS
              || estado == TipoEstadoMemoria.Tipo.PENDIENTE_CORRECCIONES
              || estado == TipoEstadoMemoria.Tipo.NO_PROCEDE_EVALUAR
              || estado.getId() >= TipoEstadoMemoria.Tipo.FIN_EVALUACION.getId()),
          ApplicationContextSupport
              .getMessage(MSG_LA_MEMORIA_NO_SE_ENCUENTRA_EN_UN_ESTADO_ADECUADO));
    }

    DocumentacionMemoria documentacionMemoria = documentacionMemoriaRepository
        .findByIdAndMemoriaIdAndTipoDocumentoFormularioIdAndMemoriaActivoTrue(idDocumentacionMemoria, idMemoria,
            memoria.getFormulario().getId())
        .orElseThrow(() -> new DocumentacionMemoriaNotFoundException(idDocumentacionMemoria));

    documentacionMemoriaRepository.delete(documentacionMemoria);
  }

}

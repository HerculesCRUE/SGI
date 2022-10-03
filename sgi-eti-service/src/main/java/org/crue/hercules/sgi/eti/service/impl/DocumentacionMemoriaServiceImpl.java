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
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.repository.DocumentacionMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.FormularioRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.eti.repository.specification.DocumentacionMemoriaSpecifications;
import org.crue.hercules.sgi.eti.service.DocumentacionMemoriaService;
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

  private static final String MSG_EL_ID_DE_LA_MEMORIA_NO_PUEDE_SER_NULO_PARA_MOSTRAR_SU_DOCUMENTACION = "El id de la memoria no puede ser nulo para mostrar su documentación";
  private static final String MSG_LA_MEMORIA_NO_SE_ENCUENTRA_EN_UN_ESTADO_ADECUADO_PARA_ANADIR_DOCUMENTACION = "La memoria no se encuentra en un estado adecuado para añadir documentación.";
  private static final String MSG_DOCUMENTACION_MEMORIA_ID_TIENE_QUE_SER_NULL_PARA_CREAR_UN_NUEVO_DOCUMENTACION_MEMORIA = "DocumentacionMemoria id tiene que ser null para crear un nuevo DocumentacionMemoria";

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
  public DocumentacionMemoria createDocumentacionInicial(Long idMemoria, DocumentacionMemoria documentacionMemoria,
      Authentication authentication) {
    log.debug("Petición a create DocumentacionMemoria : {} - start", documentacionMemoria);
    Assert.isNull(documentacionMemoria.getId(),
        MSG_DOCUMENTACION_MEMORIA_ID_TIENE_QUE_SER_NULL_PARA_CREAR_UN_NUEVO_DOCUMENTACION_MEMORIA);

    Assert.notNull(idMemoria,
        "El identificador de la memoria no puede ser null para crear un nuevo documento asociado a esta");

    Boolean isGestor = authentication.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().startsWith("ETI-MEM-EDOC"));

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {
      TipoEstadoMemoria.Tipo estado = TipoEstadoMemoria.Tipo.fromId(memoria.getEstadoActual().getId());
      if (isGestor.booleanValue()) {
        Assert.isTrue((estado.getId() >= TipoEstadoMemoria.Tipo.EN_EVALUACION.getId()),
            MSG_LA_MEMORIA_NO_SE_ENCUENTRA_EN_UN_ESTADO_ADECUADO_PARA_ANADIR_DOCUMENTACION);
      } else {
        Assert.isTrue(
            (estado == TipoEstadoMemoria.Tipo.EN_ELABORACION || estado == TipoEstadoMemoria.Tipo.COMPLETADA
                || estado == TipoEstadoMemoria.Tipo.FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS
                || estado == TipoEstadoMemoria.Tipo.PENDIENTE_CORRECCIONES
                || estado == TipoEstadoMemoria.Tipo.NO_PROCEDE_EVALUAR
                || estado.getId() >= TipoEstadoMemoria.Tipo.FIN_EVALUACION.getId()),
            MSG_LA_MEMORIA_NO_SE_ENCUENTRA_EN_UN_ESTADO_ADECUADO_PARA_ANADIR_DOCUMENTACION);
      }

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
    Assert.isTrue(idMemoria != null, MSG_EL_ID_DE_LA_MEMORIA_NO_PUEDE_SER_NULO_PARA_MOSTRAR_SU_DOCUMENTACION);
    Assert.isTrue(tipoEvaluacion != null,
        "El id del tipo de evaluación no puede ser nulo para mostrar su documentacion");

    Page<DocumentacionMemoria> returnValue = null;

    // TipoEvaluación Retrospectiva muestra la documentación de tipo Retrospectiva
    if (tipoEvaluacion == TipoEvaluacion.Tipo.RETROSPECTIVA) {
      returnValue = documentacionMemoriaRepository.findByMemoriaIdAndTipoDocumentoFormularioId(idMemoria,
          Formulario.Tipo.RETROSPECTIVA.getId(), pageable);
    }
    // TipoEvaluación Memoria muestra todas la documentación que no sea de tipo
    // Retrospectiva, Seguimiento Anual o Seguimiento Final
    if (tipoEvaluacion == TipoEvaluacion.Tipo.MEMORIA) {
      Formulario formulario = formularioRepository.findByMemoriaId(idMemoria);
      returnValue = documentacionMemoriaRepository.findByMemoriaIdAndTipoDocumentoFormularioId(idMemoria,
          formulario.getId(), pageable);
    }
    // TipoEvaluación Seguimiento Anual muestra la documentación de tipo Seguimiento
    // Anual
    if (tipoEvaluacion == TipoEvaluacion.Tipo.SEGUIMIENTO_ANUAL) {
      returnValue = documentacionMemoriaRepository.findByMemoriaIdAndTipoDocumentoFormularioId(idMemoria,
          Formulario.Tipo.SEGUIMIENTO_ANUAL.getId(), pageable);
    }
    // TipoEvaluación Seguimiento Final muestra la documentación de tipo Seguimiento
    // Final
    if (tipoEvaluacion == TipoEvaluacion.Tipo.SEGUIMIENTO_FINAL) {
      returnValue = documentacionMemoriaRepository.findByMemoriaIdAndTipoDocumentoFormularioId(idMemoria,
          Formulario.Tipo.SEGUIMIENTO_FINAL.getId(), pageable);
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
    Assert.isTrue(idMemoria != null, MSG_EL_ID_DE_LA_MEMORIA_NO_PUEDE_SER_NULO_PARA_MOSTRAR_SU_DOCUMENTACION);

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Specification<DocumentacionMemoria> specMemoriaId = DocumentacionMemoriaSpecifications.memoriaId(idMemoria);

      Formulario formulario = formularioRepository.findByMemoriaId(idMemoria);
      Specification<DocumentacionMemoria> specTipoDocumentoNotIn = DocumentacionMemoriaSpecifications
          .tipoDocumentoFormularioId(formulario.getId());

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
    Assert.isTrue(idMemoria != null, MSG_EL_ID_DE_LA_MEMORIA_NO_PUEDE_SER_NULO_PARA_MOSTRAR_SU_DOCUMENTACION);

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Specification<DocumentacionMemoria> specMemoriaId = DocumentacionMemoriaSpecifications.memoriaId(idMemoria);

      // Aquellos que no son del tipo 1: Seguimiento Anual
      Specification<DocumentacionMemoria> specTipoDocumento = DocumentacionMemoriaSpecifications.tipoDocumento(1L);

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
    Assert.isTrue(idMemoria != null, MSG_EL_ID_DE_LA_MEMORIA_NO_PUEDE_SER_NULO_PARA_MOSTRAR_SU_DOCUMENTACION);

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Specification<DocumentacionMemoria> specMemoriaId = DocumentacionMemoriaSpecifications.memoriaId(idMemoria);

      Specification<DocumentacionMemoria> specTipoDocumento = DocumentacionMemoriaSpecifications
          .tipoDocumentoFormularioId(Formulario.Tipo.SEGUIMIENTO_FINAL.getId());

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
    Assert.isTrue(idMemoria != null, MSG_EL_ID_DE_LA_MEMORIA_NO_PUEDE_SER_NULO_PARA_MOSTRAR_SU_DOCUMENTACION);

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Specification<DocumentacionMemoria> specMemoriaId = DocumentacionMemoriaSpecifications.memoriaId(idMemoria);

      Specification<DocumentacionMemoria> specTipoDocumento = DocumentacionMemoriaSpecifications
          .tipoDocumentoFormularioId(Formulario.Tipo.RETROSPECTIVA.getId());

      Specification<DocumentacionMemoria> specs = Specification.where(specMemoriaId).and(specTipoDocumento);

      Page<DocumentacionMemoria> returnValue = documentacionMemoriaRepository.findAll(specs, pageable);

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
    Assert.isNull(documentacionMemoria.getId(),
        MSG_DOCUMENTACION_MEMORIA_ID_TIENE_QUE_SER_NULL_PARA_CREAR_UN_NUEVO_DOCUMENTACION_MEMORIA);

    Assert.notNull(idMemoria,
        "El identificador de la memoria no puede ser null para crear un nuevo documento de tipo seguimiento anual asociado a esta");

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {
      TipoEstadoMemoria.Tipo estado = TipoEstadoMemoria.Tipo.fromId(memoria.getEstadoActual().getId());
      Assert.isTrue(
          (estado == TipoEstadoMemoria.Tipo.FIN_EVALUACION
              || estado == TipoEstadoMemoria.Tipo.COMPLETADA_SEGUIMIENTO_ANUAL),
          "La memoria no se encuentra en un estado adecuado para añadir documentación de seguimiento anual");

      documentacionMemoria.setMemoria(memoria);
      List<TipoDocumento> tiposDocumento = tipoDocumentoRepository
          .findByFormularioIdAndActivoTrue(Formulario.Tipo.SEGUIMIENTO_ANUAL.getId());
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
    Assert.isNull(documentacionMemoria.getId(),
        MSG_DOCUMENTACION_MEMORIA_ID_TIENE_QUE_SER_NULL_PARA_CREAR_UN_NUEVO_DOCUMENTACION_MEMORIA);

    Assert.notNull(idMemoria,
        "El identificador de la memoria no puede ser null para crear un nuevo documento de tipo seguimiento final asociado a esta");

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {
      TipoEstadoMemoria.Tipo estado = TipoEstadoMemoria.Tipo.fromId(memoria.getEstadoActual().getId());
      Assert.isTrue(
          (estado == TipoEstadoMemoria.Tipo.FIN_EVALUACION_SEGUIMIENTO_ANUAL
              || estado == TipoEstadoMemoria.Tipo.COMPLETADA_SEGUIMIENTO_FINAL
              || estado == TipoEstadoMemoria.Tipo.EN_ACLARACION_SEGUIMIENTO_FINAL),
          "La memoria no se encuentra en un estado adecuado para añadir documentación de seguimiento final");

      documentacionMemoria.setMemoria(memoria);

      List<TipoDocumento> tiposDocumento = tipoDocumentoRepository
          .findByFormularioIdAndActivoTrue(Formulario.Tipo.SEGUIMIENTO_FINAL.getId());
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
    Assert.isNull(documentacionMemoria.getId(),
        MSG_DOCUMENTACION_MEMORIA_ID_TIENE_QUE_SER_NULL_PARA_CREAR_UN_NUEVO_DOCUMENTACION_MEMORIA);

    Assert.notNull(idMemoria,
        "El identificador de la memoria no puede ser null para crear un nuevo documento de tipo retrospectiva asociado a esta");

    return memoriaRepository.findByIdAndActivoTrue(idMemoria).map(memoria -> {

      Assert.notNull(memoria.getRetrospectiva(),
          "La retrospectiva no puede ser null para crear documentación de tipo retrospectiva");

      EstadoRetrospectiva.Tipo estado = EstadoRetrospectiva.Tipo
          .fromId(memoria.getRetrospectiva().getEstadoRetrospectiva().getId());
      Assert.isTrue((estado == EstadoRetrospectiva.Tipo.PENDIENTE || estado == EstadoRetrospectiva.Tipo.COMPLETADA),
          "La retrospectiva no se encuentra en un estado adecuado para crear documentación de tipo retrospectiva");

      documentacionMemoria.setMemoria(memoria);
      List<TipoDocumento> tiposDocumento = tipoDocumentoRepository
          .findByFormularioIdAndActivoTrue(Formulario.Tipo.RETROSPECTIVA.getId());
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

    Assert.notNull(idDocumentacionMemoria,
        "DocumentacionMemoria id tiene no puede ser null para eliminar uun documento de tipo seguimiento anual");

    Assert.notNull(idMemoria,
        "El identificador de la memoria no puede ser null para eliminar un documento de tipo seguimiento anual asociado a esta");

    Memoria memoria = memoriaRepository.findByIdAndActivoTrue(idMemoria)
        .orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

    TipoEstadoMemoria.Tipo estado = TipoEstadoMemoria.Tipo.fromId(memoria.getEstadoActual().getId());
    Assert.isTrue(
        (estado == TipoEstadoMemoria.Tipo.FIN_EVALUACION
            || estado == TipoEstadoMemoria.Tipo.COMPLETADA_SEGUIMIENTO_ANUAL),
        "La memoria no se encuentra en un estado adecuado para eliminar documentación de seguimiento anual");

    DocumentacionMemoria documentacionMemoria = documentacionMemoriaRepository
        .findByIdAndMemoriaIdAndTipoDocumentoFormularioIdAndMemoriaActivoTrue(idDocumentacionMemoria, idMemoria,
            Formulario.Tipo.SEGUIMIENTO_ANUAL.getId())
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

    Assert.notNull(idDocumentacionMemoria,
        "DocumentacionMemoria id tiene no puede ser null para eliminar uun documento de tipo seguimiento final");

    Assert.notNull(idMemoria,
        "El identificador de la memoria no puede ser null para eliminar un documento de tipo seguimiento final asociado a esta");

    Memoria memoria = memoriaRepository.findByIdAndActivoTrue(idMemoria)
        .orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

    TipoEstadoMemoria.Tipo estado = TipoEstadoMemoria.Tipo.fromId(memoria.getEstadoActual().getId());
    Assert.isTrue(
        (estado == TipoEstadoMemoria.Tipo.FIN_EVALUACION_SEGUIMIENTO_ANUAL
            || estado == TipoEstadoMemoria.Tipo.COMPLETADA_SEGUIMIENTO_FINAL
            || estado == TipoEstadoMemoria.Tipo.EN_ACLARACION_SEGUIMIENTO_FINAL),
        "La memoria no se encuentra en un estado adecuado para eliminar documentación de seguimiento final");

    DocumentacionMemoria documentacionMemoria = documentacionMemoriaRepository
        .findByIdAndMemoriaIdAndTipoDocumentoFormularioIdAndMemoriaActivoTrue(idDocumentacionMemoria, idMemoria,
            Formulario.Tipo.SEGUIMIENTO_FINAL.getId())
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

    Assert.notNull(idDocumentacionMemoria,
        "DocumentacionMemoria id tiene no puede ser null para eliminar uun documento de tipo retrospectiva");

    Assert.notNull(idMemoria,
        "El identificador de la memoria no puede ser null para eliminar un documento de tipo retrospectiva asociado a esta");

    Memoria memoria = memoriaRepository.findByIdAndActivoTrue(idMemoria)
        .orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

    Assert.notNull(memoria.getRetrospectiva(),
        "La retrospectiva no puede ser null para eliminar un documento de tipo retrospectiva");

    EstadoRetrospectiva.Tipo estado = EstadoRetrospectiva.Tipo
        .fromId(memoria.getRetrospectiva().getEstadoRetrospectiva().getId());
    Assert.isTrue((estado == EstadoRetrospectiva.Tipo.PENDIENTE || estado == EstadoRetrospectiva.Tipo.COMPLETADA),
        "La retrospectiva no se encuentra en un estado adecuado para eliminar documentación de retrospectiva");

    DocumentacionMemoria documentacionMemoria = documentacionMemoriaRepository
        .findByIdAndMemoriaIdAndTipoDocumentoFormularioIdAndMemoriaActivoTrue(idDocumentacionMemoria, idMemoria,
            Formulario.Tipo.RETROSPECTIVA.getId())
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

    Assert.notNull(idDocumentacionMemoria,
        "DocumentacionMemoria id tiene no puede ser null para eliminar uun documento de tipo inicial");

    Assert.notNull(idMemoria,
        "El identificador de la memoria no puede ser null para eliminar un documento de tipo inicial asociado a esta");

    Boolean isGestor = authentication.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().startsWith("ETI-MEM-EDOC"));

    Memoria memoria = memoriaRepository.findByIdAndActivoTrue(idMemoria)
        .orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

    TipoEstadoMemoria.Tipo estado = TipoEstadoMemoria.Tipo.fromId(memoria.getEstadoActual().getId());
    if (isGestor.booleanValue()) {
      Assert.isTrue((estado.getId() >= TipoEstadoMemoria.Tipo.EN_EVALUACION.getId()),
          MSG_LA_MEMORIA_NO_SE_ENCUENTRA_EN_UN_ESTADO_ADECUADO_PARA_ANADIR_DOCUMENTACION);
    } else {
      Assert.isTrue(
          (estado == TipoEstadoMemoria.Tipo.EN_ELABORACION || estado == TipoEstadoMemoria.Tipo.COMPLETADA
              || estado == TipoEstadoMemoria.Tipo.FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS
              || estado == TipoEstadoMemoria.Tipo.PENDIENTE_CORRECCIONES
              || estado == TipoEstadoMemoria.Tipo.NO_PROCEDE_EVALUAR
              || estado.getId() >= TipoEstadoMemoria.Tipo.FIN_EVALUACION.getId()),
          "La memoria no se encuentra en un estado adecuado para eliminar documentación inicial");
    }

    Formulario formulario = formularioRepository.findByMemoriaId(idMemoria);
    DocumentacionMemoria documentacionMemoria = documentacionMemoriaRepository
        .findByIdAndMemoriaIdAndTipoDocumentoFormularioIdAndMemoriaActivoTrue(idDocumentacionMemoria, idMemoria,
            formulario.getId())
        .orElseThrow(() -> new DocumentacionMemoriaNotFoundException(idDocumentacionMemoria));

    documentacionMemoriaRepository.delete(documentacionMemoria);
  }

}

package org.crue.hercules.sgi.eti.service;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

/**
 * Service Interface para gestionar {@link DocumentacionMemoria}.
 */
public interface DocumentacionMemoriaService {

  /**
   * Crea {@link DocumentacionMemoria} inicial de una memoria (aquella
   * documentación que no es de seguimiento anual, final o retrospectiva).
   * 
   * @param idMemoria            Id de la {@link Memoria}
   * @param DocumentacionMemoria la entidad {@link DocumentacionMemoria} a
   *                             guardar.
   * @return la entidad {@link DocumentacionMemoria} persistida.
   */
  DocumentacionMemoria createDocumentacionInicial(Long idMemoria, DocumentacionMemoria DocumentacionMemoria);

  /**
   * Crea {@link DocumentacionMemoria} inicial de una memoria (aquella
   * documentación que no es de seguimiento anual, final o retrospectiva).
   * 
   * @param idMemoria            Id de la {@link Memoria}
   * @param DocumentacionMemoria la entidad {@link DocumentacionMemoria} a
   *                             guardar.
   * @return la entidad {@link DocumentacionMemoria} persistida.
   */
  DocumentacionMemoria createDocumentacionInicialInvestigador(Long idMemoria,
      DocumentacionMemoria DocumentacionMemoria);

  /**
   * Obtiene {@link DocumentacionMemoria} por id.
   *
   * @param id el id de la entidad {@link DocumentacionMemoria}.
   * @return la entidad {@link DocumentacionMemoria}.
   */
  DocumentacionMemoria findById(Long id);

  /**
   * Obtener todas las entidades paginadas {@link DocumentacionMemoria} por
   * {@link TipoEvaluacion} para una determinada {@link Memoria}.
   *
   * @param idMemoria      Id de {@link Memoria}.
   * @param tipoEvaluacion Id de {@link TipoEvaluacion.Tipo}
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  Page<DocumentacionMemoria> findByMemoriaIdAndTipoEvaluacion(Long idMemoria, TipoEvaluacion.Tipo tipoEvaluacion,
      Pageable pageable);

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} asociadas al
   * {@link Formulario} de la {@link Memoria}.
   * 
   * @param idMemoria Id de {@link Memoria}.
   * @param pageable  la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  Page<DocumentacionMemoria> findDocumentacionMemoria(Long idMemoria, Pageable pageable);

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} asociadas al
   * {@link Formulario} de la {@link Memoria} del tipo Seguimiento Anual.
   * 
   * @param id       Id de {@link Memoria}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  Page<DocumentacionMemoria> findDocumentacionSeguimientoAnual(Long id, Pageable pageable);

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} asociadas al
   * {@link Formulario} de la {@link Memoria} del tipo Seguimiento Final.
   * 
   * @param id       Id de {@link Memoria}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  Page<DocumentacionMemoria> findDocumentacionSeguimientoFinal(Long id, Pageable pageable);

  /**
   * Obtiene todas las entidades {@link DocumentacionMemoria} asociadas al
   * {@link Formulario} de la {@link Memoria} del tipo Retrospectiva.
   * 
   * @param id       Id de {@link Memoria}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  Page<DocumentacionMemoria> findDocumentacionRetrospectiva(Long id, Pageable pageable);

  /**
   * Crea un {@link DocumentacionMemoria} del tipo seguimiento anual.
   * 
   * @param idMemoria            Id de {@link Memoria}
   * @param documentacionMemoria {@link DocumentacionMemoria} a crear
   * @return {@link DocumentacionMemoria} creada
   */
  DocumentacionMemoria createSeguimientoAnual(Long idMemoria, @Valid DocumentacionMemoria documentacionMemoria);

  /**
   * Crea un {@link DocumentacionMemoria} del tipo seguimiento final.
   * 
   * @param idMemoria            Id de {@link Memoria}
   * @param documentacionMemoria {@link DocumentacionMemoria} a crear
   * @return {@link DocumentacionMemoria} creada
   */
  DocumentacionMemoria createSeguimientoFinal(Long idMemoria, @Valid DocumentacionMemoria documentacionMemoria);

  /**
   * Crea un {@link DocumentacionMemoria} del tipo retrospectiva.
   * 
   * @param idMemoria            Id de {@link Memoria}
   * @param documentacionMemoria {@link DocumentacionMemoria} a crear
   * @return {@link DocumentacionMemoria} creada
   */
  DocumentacionMemoria createRetrospectiva(Long idMemoria, @Valid DocumentacionMemoria documentacionMemoria);

  /**
   * Elimina {@link DocumentacionMemoria} del tipo seguimiento anual.
   * 
   * @param idMemoria              Id {@link Memoria}
   * @param idDocumentacionMemoria Id {@link DocumentacionMemoria}
   */
  void deleteDocumentacionSeguimientoAnual(Long idMemoria, Long idDocumentacionMemoria);

  /**
   * Elimina {@link DocumentacionMemoria} del tipo seguimiento final.
   * 
   * @param idMemoria              Id {@link Memoria}
   * @param idDocumentacionMemoria Id {@link DocumentacionMemoria}
   */
  void deleteDocumentacionSeguimientoFinal(Long idMemoria, Long idDocumentacionMemoria);

  /**
   * Elimina {@link DocumentacionMemoria} del tipo retrospectiva.
   * 
   * @param idMemoria              Id {@link Memoria}
   * @param idDocumentacionMemoria Id {@link DocumentacionMemoria}
   */
  void deleteDocumentacionRetrospectiva(Long idMemoria, Long idDocumentacionMemoria);

  /**
   * Elimina {@link DocumentacionMemoria} inicial.
   * 
   * @param idMemoria              Id {@link Memoria}
   * @param idDocumentacionMemoria Id {@link DocumentacionMemoria}
   * @param authentication         Authentication
   */
  void deleteDocumentacionInicial(Long idMemoria, Long idDocumentacionMemoria, Authentication authentication);
}
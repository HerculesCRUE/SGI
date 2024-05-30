package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.DocumentacionConvocatoriaReunion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

/**
 * Service Interface para gestionar {@link DocumentacionConvocatoriaReunion}.
 */
public interface DocumentacionConvocatoriaReunionService {

  /**
   * Crea {@link DocumentacionConvocatoriaReunion} de una convocatoria de reunión
   * 
   * @param idConvocatoriaReunion            Id de la {@link ConvocatoriaReunion}
   * @param documentacionConvocatoriaReunion la entidad
   *                                         {@link DocumentacionConvocatoriaReunion}
   *                                         a
   *                                         guardar.
   * @return la entidad {@link DocumentacionConvocatoriaReunion} persistida.
   */
  DocumentacionConvocatoriaReunion createDocumentacion(Long idConvocatoriaReunion,
      DocumentacionConvocatoriaReunion documentacionConvocatoriaReunion);

  /**
   * Actualiza {@link DocumentacionConvocatoriaReunion} de una convocatoria de
   * reunión
   * 
   * @param idConvocatoriaReunion              Id de la
   *                                           {@link ConvocatoriaReunion}
   * @param idDocumentacionConvocatoriaReunion id de la
   *                                           {@link DocumentacionConvocatoriaReunion}
   * @param documentacionConvocatoriaReunion   la entidad
   *                                           {@link DocumentacionConvocatoriaReunion}
   *                                           a guardar.
   * @return la entidad {@link DocumentacionConvocatoriaReunion} persistida.
   */
  DocumentacionConvocatoriaReunion updateDocumentacion(Long idConvocatoriaReunion,
      Long idDocumentacionConvocatoriaReunion,
      DocumentacionConvocatoriaReunion documentacionConvocatoriaReunion);

  /**
   * Obtiene {@link DocumentacionConvocatoriaReunion} por id.
   *
   * @param id el id de la entidad {@link DocumentacionConvocatoriaReunion}.
   * @return la entidad {@link DocumentacionConvocatoriaReunion}.
   */
  DocumentacionConvocatoriaReunion findById(Long id);

  /**
   * Obtiene todas las entidades {@link DocumentacionConvocatoriaReunion}
   * 
   * @param idConvocatoriaReunion Id de {@link ConvocatoriaReunion}.
   * @param pageable              la información de la paginación.
   * @return la lista de entidades {@link DocumentacionConvocatoriaReunion}
   *         paginadas.
   */
  Page<DocumentacionConvocatoriaReunion> findDocumentacionConvocatoriaReunion(Long idConvocatoriaReunion,
      Pageable pageable);

  /**
   * Elimina {@link DocumentacionConvocatoriaReunion} inicial.
   * 
   * @param idConvocatoriaReunion              Id {@link ConvocatoriaReunion}
   * @param idDocumentacionConvocatoriaReunion Id
   *                                           {@link DocumentacionConvocatoriaReunion}
   * @param authentication                     Authentication
   */
  void deleteDocumentacion(Long idConvocatoriaReunion, Long idDocumentacionConvocatoriaReunion,
      Authentication authentication);
}
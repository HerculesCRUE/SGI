package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.ConvocatoriaReunionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.DocumentacionConvocatoriaReunionNotFoundException;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.DocumentacionConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.repository.ConvocatoriaReunionRepository;
import org.crue.hercules.sgi.eti.repository.DocumentacionConvocatoriaReunionRepository;
import org.crue.hercules.sgi.eti.service.DocumentacionConvocatoriaReunionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de
 * {@link DocumentacionConvocatoriaReunion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class DocumentacionConvocatoriaReunionServiceImpl implements DocumentacionConvocatoriaReunionService {

  private static final String MSG_EL_ID_DE_LA_CONVOCATORIA_REUNION_NO_PUEDE_SER_NULO_PARA_MOSTRAR_SU_DOCUMENTACION = "El id de la convocatoriaReunion no puede ser nulo para mostrar su documentación";
  private static final String MSG_DOCUMENTACION_CONVOCATORIA_REUNION_ID_TIENE_QUE_SER_NULL_PARA_CREAR_UN_NUEVO_DOCUMENTACION_CONVOCATORIA_REUNION = "DocumentacionConvocatoriaReunion id tiene que ser null para crear un nuevo DocumentacionConvocatoriaReunion";
  private static final String MSG_DOCUMENTACION_CONVOCATORIA_REUNION_ID_NO_TIENE_QUE_SER_NULL_PARA_ACTUALIZAR_UN_NUEVO_DOCUMENTACION_CONVOCATORIA_REUNION = "DocumentacionConvocatoriaReunion id no tiene que ser null para actualizar un nuevo DocumentacionConvocatoriaReunion";

  /** Documentacion convocatoriaReunion repository */
  private final DocumentacionConvocatoriaReunionRepository documentacionConvocatoriaReunionRepository;

  /** ConvocatoriaReunion repository */
  private final ConvocatoriaReunionRepository convocatoriaReunionRepository;

  public DocumentacionConvocatoriaReunionServiceImpl(
      DocumentacionConvocatoriaReunionRepository documentacionConvocatoriaReunionRepository,
      ConvocatoriaReunionRepository convocatoriaReunionRepository) {
    this.documentacionConvocatoriaReunionRepository = documentacionConvocatoriaReunionRepository;
    this.convocatoriaReunionRepository = convocatoriaReunionRepository;
  }

  /**
   * Crea {@link DocumentacionConvocatoriaReunion} inicial de una
   * convocatoriaReunion (aquella
   * documentación que no es de seguimiento anual, final o retrospectiva).
   *
   * @param idConvocatoriaReunion            Id de la {@link ConvocatoriaReunion}
   * @param documentacionConvocatoriaReunion la entidad
   *                                         {@link DocumentacionConvocatoriaReunion}
   *                                         a
   *                                         guardar.
   * @return la entidad {@link DocumentacionConvocatoriaReunion} persistida.
   */
  @Override
  @Transactional
  public DocumentacionConvocatoriaReunion createDocumentacion(Long idConvocatoriaReunion,
      DocumentacionConvocatoriaReunion documentacionConvocatoriaReunion) {
    log.debug("Petición a create DocumentacionConvocatoriaReunion : {} - start", documentacionConvocatoriaReunion);
    Assert.isNull(documentacionConvocatoriaReunion.getId(),
        MSG_DOCUMENTACION_CONVOCATORIA_REUNION_ID_TIENE_QUE_SER_NULL_PARA_CREAR_UN_NUEVO_DOCUMENTACION_CONVOCATORIA_REUNION);

    Assert.notNull(idConvocatoriaReunion,
        "El identificador de la convocatoriaReunion no puede ser null para crear un nuevo documento asociado a esta");

    return convocatoriaReunionRepository.findByIdAndActivoTrue(idConvocatoriaReunion).map(convocatoriaReunion -> {
      documentacionConvocatoriaReunion.setConvocatoriaReunionId(idConvocatoriaReunion);
      return documentacionConvocatoriaReunionRepository.save(documentacionConvocatoriaReunion);
    }).orElseThrow(() -> new ConvocatoriaReunionNotFoundException(idConvocatoriaReunion));

  }

  /**
   * Actualiza {@link DocumentacionConvocatoriaReunion} inicial de una
   * convocatoriaReunion (aquella
   * documentación que no es de seguimiento anual, final o retrospectiva).
   *
   * @param idConvocatoriaReunion                  Id de la
   *                                               {@link ConvocatoriaReunion}
   * @param idDocumentacionConvocatoriaReunion     id de la
   *                                               {@link DocumentacionConvocatoriaReunion}
   * @param documentacionConvocatoriaReunionUpdate la entidad
   *                                               {@link DocumentacionConvocatoriaReunion}
   *                                               a actualizar.
   * @return la entidad {@link DocumentacionConvocatoriaReunion} persistida.
   */
  @Override
  @Transactional
  public DocumentacionConvocatoriaReunion updateDocumentacion(Long idConvocatoriaReunion,
      Long idDocumentacionConvocatoriaReunion,
      DocumentacionConvocatoriaReunion documentacionConvocatoriaReunionUpdate) {
    log.debug("Petición a update DocumentacionConvocatoriaReunion : {} - start",
        documentacionConvocatoriaReunionUpdate);
    Assert.notNull(documentacionConvocatoriaReunionUpdate.getId(),
        MSG_DOCUMENTACION_CONVOCATORIA_REUNION_ID_NO_TIENE_QUE_SER_NULL_PARA_ACTUALIZAR_UN_NUEVO_DOCUMENTACION_CONVOCATORIA_REUNION);

    Assert.notNull(idConvocatoriaReunion,
        "El identificador de la convocatoriaReunion no puede ser null para crear un nuevo documento asociado a esta");

    return documentacionConvocatoriaReunionRepository.findByIdAndConvocatoriaReunionIdAndConvocatoriaReunionActivoTrue(
        idDocumentacionConvocatoriaReunion, idConvocatoriaReunion).map(documentacionConvocatoriaReunion -> {
          documentacionConvocatoriaReunion.setNombre(documentacionConvocatoriaReunionUpdate.getNombre());
          return documentacionConvocatoriaReunionRepository.save(documentacionConvocatoriaReunion);
        }).orElseThrow(() -> new DocumentacionConvocatoriaReunionNotFoundException(idConvocatoriaReunion));

  }

  /**
   * Obtiene una entidad {@link DocumentacionConvocatoriaReunion} por id.
   *
   * @param id el id de la entidad {@link DocumentacionConvocatoriaReunion}.
   * @return la entidad {@link DocumentacionConvocatoriaReunion}.
   * @throws DocumentacionConvocatoriaReunionNotFoundException Si no existe ningún
   *                                                           {@link DocumentacionConvocatoriaReunion}
   *                                                           con ese id.
   */

  public DocumentacionConvocatoriaReunion findById(final Long id)
      throws DocumentacionConvocatoriaReunionNotFoundException {
    log.debug("Petición a get DocumentacionConvocatoriaReunion : {}  - start", id);
    final DocumentacionConvocatoriaReunion documentacionConvocatoriaReunion = documentacionConvocatoriaReunionRepository
        .findById(id)
        .orElseThrow(() -> new DocumentacionConvocatoriaReunionNotFoundException(id));
    log.debug("Petición a get DocumentacionConvocatoriaReunion : {}  - end", id);
    return documentacionConvocatoriaReunion;

  }

  /**
   * Obtiene todas las entidades {@link DocumentacionConvocatoriaReunion}
   * asociadas al
   * {@link Formulario} de la {@link ConvocatoriaReunion}.
   * 
   * @param idConvocatoriaReunion Id de {@link ConvocatoriaReunion}.
   * @param pageable              la información de la paginación.
   * @return la lista de entidades {@link DocumentacionConvocatoriaReunion}
   *         paginadas.
   */
  @Override
  public Page<DocumentacionConvocatoriaReunion> findDocumentacionConvocatoriaReunion(Long idConvocatoriaReunion,
      Pageable pageable) {
    log.debug("findDocumentacionConvocatoriaReunion(Long idConvocatoriaReunion, Pageable pageable) - start");
    Assert.isTrue(idConvocatoriaReunion != null,
        MSG_EL_ID_DE_LA_CONVOCATORIA_REUNION_NO_PUEDE_SER_NULO_PARA_MOSTRAR_SU_DOCUMENTACION);

    return convocatoriaReunionRepository.findByIdAndActivoTrue(idConvocatoriaReunion).map(convocatoriaReunion -> {

      Page<DocumentacionConvocatoriaReunion> returnValue = documentacionConvocatoriaReunionRepository
          .findByConvocatoriaReunionId(idConvocatoriaReunion, pageable);
      log.debug("findDocumentacionConvocatoriaReunion(Long idConvocatoriaReunion, Pageable pageable) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaReunionNotFoundException(idConvocatoriaReunion));

  }

  /**
   * Elimina {@link DocumentacionConvocatoriaReunion} inicial.
   * 
   * @param idConvocatoriaReunion              Id {@link ConvocatoriaReunion}
   * @param idDocumentacionConvocatoriaReunion Id
   *                                           {@link DocumentacionConvocatoriaReunion}
   * @param authentication                     Authentication
   */
  @Transactional
  @Override
  public void deleteDocumentacion(Long idConvocatoriaReunion, Long idDocumentacionConvocatoriaReunion,
      Authentication authentication) {
    log.debug("deleteDocumentacion(Long idConvocatoriaReunion, Long idDocumentacionConvocatoriaReunion) -- start");

    Assert.notNull(idDocumentacionConvocatoriaReunion,
        "DocumentacionConvocatoriaReunion id tiene no puede ser null para eliminar uun documento de tipo inicial");

    Assert.notNull(idConvocatoriaReunion,
        "El identificador de la convocatoria de reunión no puede ser null para eliminar un documento de tipo inicial asociado a esta");

    ConvocatoriaReunion convocatoriaReunion = convocatoriaReunionRepository.findByIdAndActivoTrue(idConvocatoriaReunion)
        .orElseThrow(() -> new ConvocatoriaReunionNotFoundException(idConvocatoriaReunion));

    DocumentacionConvocatoriaReunion documentacionConvocatoriaReunion = documentacionConvocatoriaReunionRepository
        .findByIdAndConvocatoriaReunionIdAndConvocatoriaReunionActivoTrue(
            idDocumentacionConvocatoriaReunion, idConvocatoriaReunion)
        .orElseThrow(() -> new DocumentacionConvocatoriaReunionNotFoundException(idDocumentacionConvocatoriaReunion));

    documentacionConvocatoriaReunionRepository.delete(documentacionConvocatoriaReunion);
  }

}

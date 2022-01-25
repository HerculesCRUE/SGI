package org.crue.hercules.sgi.csp.service.impl;

import java.util.Optional;

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
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
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

    Assert.isNull(documentoRequeridoSolicitud.getId(), "Id tiene que ser null para crear DocumentoRequeridoSolicitud");

    validarDocumentoRequeridoSolicitud(documentoRequeridoSolicitud, null, new String[] { "CSP-CON-E", "CSP-CON-C" });

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

    Assert.notNull(documentoRequeridoSolicitud.getId(),
        "DocumentoRequeridoSolicitud id no puede ser null para actualizar un DocumentoRequeridoSolicitud");

    return repository.findById(documentoRequeridoSolicitud.getId()).map(datosOriginales -> {

      validarDocumentoRequeridoSolicitud(documentoRequeridoSolicitud, datosOriginales, new String[] { "CSP-CON-E" });

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

    Assert.notNull(id, "DocumentoRequeridoSolicitud id no puede ser null para eliminar un DocumentoRequeridoSolicitud");
    repository.findById(id).map(convocatoriaAreaTematica -> {

      // comprobar si convocatoria es modificable
      ConfiguracionSolicitud configuracionSolicitud = configuracionSolicitudRepository
          .findById(convocatoriaAreaTematica.getConfiguracionSolicitudId())
          .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(
              convocatoriaAreaTematica.getConfiguracionSolicitudId()));
      Assert.isTrue(
          convocatoriaService.isRegistradaConSolicitudesOProyectos(configuracionSolicitud.getConvocatoriaId(), null,
              new String[] { "CSP-CON-E" }),
          "No se puede eliminar DocumentoRequeridoSolicitud. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");

      return convocatoriaAreaTematica;
    }).orElseThrow(() -> new DocumentoRequeridoSolicitudNotFoundException(id));

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
    Assert.isTrue(documentoRequeridoSolicitud.getConfiguracionSolicitudId() != null,
        "ConfiguracionSolicitud no puede ser null en la DocumentoRequeridoSolicitud");

    /** Obligatorio TipoDocumento */
    Assert.isTrue(
        documentoRequeridoSolicitud.getTipoDocumento() != null
            && documentoRequeridoSolicitud.getTipoDocumento().getId() != null,
        "TipoDocumento no puede ser null en la DocumentoRequeridoSolicitud");

    /** Se recupera la Configuración Solicitud para la convocatoria */
    ConfiguracionSolicitud configuracionSolicitud = configuracionSolicitudRepository
        .findById(documentoRequeridoSolicitud.getConfiguracionSolicitudId())
        .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(
            documentoRequeridoSolicitud.getConfiguracionSolicitudId()));

    // comprobar si convocatoria es modificable
    Assert.isTrue(
        convocatoriaService.isRegistradaConSolicitudesOProyectos(configuracionSolicitud.getConvocatoriaId(), null,
            authorities),
        "No se puede " + ((datosOriginales != null) ? "modificar" : "crear")
            + " DocumentoRequeridoSolicitud. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");

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
        "Solo se pueden añadir documentos asociados a la Fase del plazo de presentación de solicitudes en la configuración de la convocatoria");

    Optional<ModeloTipoFase> modeloTipoFase = modeloTipoFaseRepository
        .findByModeloEjecucionIdAndTipoFaseId(modeloEjecucionId, configuracionTipoFase.getId());

    // TipoFase está asignado al ModeloEjecucion
    Assert.isTrue(modeloTipoFase.isPresent(),
        "TipoFase '" + configuracionTipoFase.getNombre() + "' no disponible para el ModeloEjecucion '"
            + ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                : "Convocatoria sin modelo asignado")
            + "'");

    // La asignación al ModeloEjecucion está activa
    Assert.isTrue(modeloTipoFase.get().getActivo(), "ModeloTipoFase '" + modeloTipoFase.get().getTipoFase().getNombre()
        + "' no está activo para el ModeloEjecucion '" + modeloTipoFase.get().getModeloEjecucion().getNombre() + "'");

    // El TipoFase está activo
    Assert.isTrue(modeloTipoFase.get().getTipoFase().getActivo(),
        "TipoFase '" + modeloTipoFase.get().getTipoFase().getNombre() + "' no está activo");

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
        "TipoDocumento '" + documentoRequeridoSolicitud.getTipoDocumento().getNombre()
            + "' no disponible para el ModeloEjecucion '"
            + ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                : "Convocatoria sin modelo asignado")
            + "' y TipoFase '" + configuracionModeloTipoFase.getTipoFase().getNombre() + "'");

    // Comprobar solamente si estamos creando o se ha modificado el documento
    if (datosOriginales == null
        || (modeloTipoDocumento.get().getTipoDocumento().getId() != datosOriginales.getTipoDocumento().getId())) {

      // La asignación al ModeloEjecucion está activa
      Assert.isTrue(modeloTipoDocumento.get().getActivo(),
          "ModeloTipoDocumento '" + modeloTipoDocumento.get().getTipoDocumento().getNombre()
              + "' no está activo para el ModeloEjecucion '"
              + modeloTipoDocumento.get().getModeloEjecucion().getNombre() + "'");

      // El TipoDocumento está activo
      Assert.isTrue(modeloTipoDocumento.get().getTipoDocumento().getActivo(),
          "TipoDocumento '" + modeloTipoDocumento.get().getTipoDocumento().getNombre() + "' no está activo");

      documentoRequeridoSolicitud.setTipoDocumento(modeloTipoDocumento.get().getTipoDocumento());

    }

    log.debug(
        "validarDocumentoRequeridoSolicitud(DocumentoRequeridoSolicitud datosDocumentoRequeridoSolicitud, DocumentoRequeridoSolicitud datosOriginales) - end");
  }

}

package org.crue.hercules.sgi.csp.service.impl;

import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessConvocatoriaException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria.Estado;
import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaDocumentoSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaDocumentoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ConvocatoriaDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)

public class ConvocatoriaDocumentoServiceImpl implements ConvocatoriaDocumentoService {

  private final ConvocatoriaDocumentoRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ModeloTipoFaseRepository modeloTipoFaseRepository;
  private final ModeloTipoDocumentoRepository modeloTipoDocumentoRepository;
  private final ConfiguracionSolicitudRepository configuracionSolicitudRepository;

  public ConvocatoriaDocumentoServiceImpl(ConvocatoriaDocumentoRepository convocatoriaDocumentoRepository,
      ConvocatoriaRepository convocatoriaRepository, ModeloTipoFaseRepository modeloTipoFaseRepository,
      ModeloTipoDocumentoRepository modeloTipoDocumentoRepository,
      ConfiguracionSolicitudRepository configuracionSolicitudRepository) {
    this.repository = convocatoriaDocumentoRepository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.modeloTipoFaseRepository = modeloTipoFaseRepository;
    this.modeloTipoDocumentoRepository = modeloTipoDocumentoRepository;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
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

    Assert.isNull(convocatoriaDocumento.getId(),
        "ConvocatoriaDocumento id tiene que ser null para crear un nuevo ConvocatoriaDocumento");

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

    Assert.notNull(convocatoriaDocumentoActualizar.getId(),
        "ConvocatoriaDocumento id no puede ser null para actualizar un ConvocatoriaDocumento");

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

    Assert.notNull(id, "ConvocatoriaDocumento id no puede ser null para eliminar un ConvocatoriaDocumento");
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
    log.debug("findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable) - start");

    Convocatoria convocatoria = convocatoriaRepository.findById(
        convocatoriaId)
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaId));
    if (hasAuthorityViewInvestigador()) {
      ConfiguracionSolicitud configuracionSolicitud = configuracionSolicitudRepository
          .findByConvocatoriaId(convocatoriaId)
          .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(convocatoriaId));
      if (!convocatoria.getEstado().equals(Estado.REGISTRADA)
          || Boolean.FALSE.equals(configuracionSolicitud.getTramitacionSGI())) {
        throw new UserNotAuthorizedToAccessConvocatoriaException();
      }
    }

    Specification<ConvocatoriaDocumento> specs = ConvocatoriaDocumentoSpecifications.byConvocatoriaId(
        convocatoriaId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaDocumento> returnValue = repository.findAll(specs, paging);
    log.debug("findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable) - end");
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
          "TipoFase '" + datosConvocatoriaDocumento.getTipoFase().getNombre()
              + "' no disponible para el ModeloEjecucion '"
              + ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                  : "Convocatoria sin modelo asignado")
              + "'");

      // Comprobar solamente si estamos creando o se ha modificado el TipoFase
      if (datosOriginales == null || datosOriginales.getTipoFase() == null
          || (modeloTipoFase.get().getTipoFase().getId() != datosOriginales.getTipoFase().getId())) {

        // La asignación al ModeloEjecucion está activa
        Assert.isTrue(modeloTipoFase.get().getActivo(),
            "ModeloTipoFase '" + modeloTipoFase.get().getTipoFase().getNombre()
                + "' no está activo para el ModeloEjecucion '" + modeloTipoFase.get().getModeloEjecucion().getNombre()
                + "'");

        // El TipoFase está activo
        Assert.isTrue(modeloTipoFase.get().getTipoFase().getActivo(),
            "TipoFase '" + modeloTipoFase.get().getTipoFase().getNombre() + "' no está activo");
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
          "TipoDocumento '" + datosConvocatoriaDocumento.getTipoDocumento().getNombre()
              + "' no disponible para el ModeloEjecucion '"
              + ((modeloEjecucionId != null) ? convocatoria.getModeloEjecucion().getNombre()
                  : "Convocatoria sin modelo asignado")
              + "' y TipoFase '"
              + ((convocatoriaDocumentoModeloTipoFase != null)
                  ? convocatoriaDocumentoModeloTipoFase.getTipoFase().getNombre()
                  : "Sin Asignar")
              + "'");

      // Comprobar solamente si estamos creando o se ha modificado el documento
      if (datosOriginales == null || datosOriginales.getTipoDocumento() == null
          || (modeloTipoDocumento.get().getTipoDocumento().getId() != datosOriginales.getTipoDocumento().getId())) {

        // La asignación al ModeloEjecucion está activa
        Assert.isTrue(modeloTipoDocumento.get().getActivo(),
            "ModeloTipoDocumento '" + modeloTipoDocumento.get().getTipoDocumento().getNombre()
                + "' no está activo para el ModeloEjecucion '"
                + modeloTipoDocumento.get().getModeloEjecucion().getNombre() + "'");

        // El TipoDocumento está activo
        Assert.isTrue(modeloTipoDocumento.get().getTipoDocumento().getActivo(),
            "TipoDocumento '" + modeloTipoDocumento.get().getTipoDocumento().getNombre() + "' no está activo");

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
    Assert.isTrue(datosConvocatoriaDocumento.getConvocatoriaId() != null,
        "Id Convocatoria no puede ser null en ConvocatoriaDocumento");

    Assert.isTrue(StringUtils.isNotBlank(datosConvocatoriaDocumento.getNombre()),
        "Es necesario indicar el nombre del documento");

    Assert.isTrue(datosConvocatoriaDocumento.getPublico() != null, "Es necesario indicar si el documento es público");

    Assert.isTrue(datosConvocatoriaDocumento.getDocumentoRef() != null,
        "Es necesario indicar la referencia al documento");

    log.debug("validarRequeridosConvocatoriaDocumento(ConvocatoriaDocumento datosConvocatoriaDocumento) - end");
  }

  @Override
  public boolean existsByConvocatoriaId(Long convocatoriaId) {
    return repository.existsByConvocatoriaId(convocatoriaId);
  }

  private boolean hasAuthorityViewInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-CON-INV-V");
  }
}

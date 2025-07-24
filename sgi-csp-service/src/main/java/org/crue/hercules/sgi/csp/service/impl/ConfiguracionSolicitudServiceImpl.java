package org.crue.hercules.sgi.csp.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaFaseNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaFaseRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.DocumentoRequeridoSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.DocumentoRequeridoSolicitudSpecifications;
import org.crue.hercules.sgi.csp.service.ConfiguracionSolicitudService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ConfiguracionSolicitud}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConfiguracionSolicitudServiceImpl implements ConfiguracionSolicitudService {
  private static final String MSG_FIELD_TRAMITACION_SGI = "configuracionSolicitud.tramitacionSgi";
  private static final String MSG_FIELD_PLAZO_PRESENTACION = "configuracionSolicitud.plazoPresentacion";

  private final ConfiguracionSolicitudRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ConvocatoriaFaseRepository convocatoriaFaseRepository;
  private final DocumentoRequeridoSolicitudRepository documentoRequeridoSolicitudRepository;

  public ConfiguracionSolicitudServiceImpl(ConfiguracionSolicitudRepository repository,
      ConvocatoriaRepository convocatoriaRepository, ConvocatoriaFaseRepository convocatoriaFaseRepository,
      DocumentoRequeridoSolicitudRepository documentoRequeridoSolicitudRepository) {
    this.repository = repository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.convocatoriaFaseRepository = convocatoriaFaseRepository;
    this.documentoRequeridoSolicitudRepository = documentoRequeridoSolicitudRepository;
  }

  /**
   * Guarda la entidad {@link ConfiguracionSolicitud}.
   * 
   * @param configuracionSolicitud la entidad {@link ConfiguracionSolicitud} a
   *                               guardar.
   * @return ConfiguracionSolicitud la entidad {@link ConfiguracionSolicitud}
   *         persistida.
   */
  @Override
  @Transactional
  public ConfiguracionSolicitud create(ConfiguracionSolicitud configuracionSolicitud) {
    log.debug("create(ConfiguracionSolicitud configuracionSolicitud) - start");

    AssertHelper.idIsNull(configuracionSolicitud.getId(), ConfiguracionSolicitud.class);
    AssertHelper.idNotNull(configuracionSolicitud.getConvocatoriaId(), Convocatoria.class);
    AssertHelper.entityExists(!repository.findByConvocatoriaId(configuracionSolicitud.getConvocatoriaId())
        .isPresent(), Convocatoria.class, ConfiguracionSolicitud.class);

    configuracionSolicitud.setConvocatoriaId(configuracionSolicitud.getConvocatoriaId());

    // validar y establecer los datos
    validarConfiguracionSolicitud(configuracionSolicitud, null);

    ConfiguracionSolicitud returnValue = repository.save(configuracionSolicitud);

    log.debug("create(ConfiguracionSolicitud configuracionSolicitud) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link ConfiguracionSolicitud} por
   * {@link Convocatoria}
   * 
   * @param configuracionSolicitud {@link ConfiguracionSolicitud} con los datos
   *                               actualizados.
   * @param convocatoriaId         Identificador de la {@link Convocatoria}
   * @return ConfiguracionSolicitud {@link ConfiguracionSolicitud} actualizado.
   */
  @Override
  @Transactional
  public ConfiguracionSolicitud update(ConfiguracionSolicitud configuracionSolicitud, Long convocatoriaId) {
    log.debug("update(ConfiguracionSolicitud configuracionSolicitud) - start");

    AssertHelper.idNotNull(convocatoriaId, Convocatoria.class);

    return repository.findByConvocatoriaId(configuracionSolicitud.getConvocatoriaId()).map(data -> {

      // validar y establecer los datos
      validarConfiguracionSolicitud(configuracionSolicitud, data);

      data.setTramitacionSGI(configuracionSolicitud.getTramitacionSGI());
      data.setFasePresentacionSolicitudes(configuracionSolicitud.getFasePresentacionSolicitudes());
      data.setImporteMaximoSolicitud(configuracionSolicitud.getImporteMaximoSolicitud());

      ConfiguracionSolicitud returnValue = repository.save(configuracionSolicitud);

      log.debug("update(ConfiguracionSolicitud configuracionSolicitud) - end");
      return returnValue;
    }).orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(configuracionSolicitud.getId()));
  }

  /**
   * Obtiene una entidad {@link ConfiguracionSolicitud} por el id de la
   * {@link Convocatoria}.
   * 
   * @param id Identificador de la entidad {@link Convocatoria}.
   * @return ConfiguracionSolicitud la entidad {@link ConfiguracionSolicitud}.
   */
  @Override
  public ConfiguracionSolicitud findByConvocatoriaId(Long id) {
    log.debug("findByConvocatoriaId(Long id) - start");

    if (convocatoriaRepository.existsById(id)) {
      final Optional<ConfiguracionSolicitud> returnValue = repository.findByConvocatoriaId(id);
      log.debug("findByConvocatoriaId(Long id) - end");
      return (returnValue.isPresent()) ? returnValue.get() : null;
    } else {
      throw new ConvocatoriaNotFoundException(id);
    }
  }

  /**
   * Comprueba, valida y tranforma los datos de la {@link ConfiguracionSolicitud}
   * antes de utilizados para crear o modificar la entidad
   * 
   * @param datosConfiguracionSolicitud
   */
  private void validarConfiguracionSolicitud(ConfiguracionSolicitud datosConfiguracionSolicitud,
      ConfiguracionSolicitud datosOriginales) {
    log.debug(
        "validarConfiguracionSolicitud(ConfiguracionSolicitud datosConfiguracionSolicitud, , ConfiguracionSolicitud datosOriginales) - start");

    // obligatorio para pasar al estado Registrada
    Convocatoria convocatoria = convocatoriaRepository.findById(datosConfiguracionSolicitud.getConvocatoriaId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(datosConfiguracionSolicitud.getConvocatoriaId()));
    if (convocatoria.getEstado() == Convocatoria.Estado.REGISTRADA) {
      AssertHelper.fieldNotNull(datosConfiguracionSolicitud.getTramitacionSGI(), ConfiguracionSolicitud.class,
          MSG_FIELD_TRAMITACION_SGI);
    }

    AssertHelper.fieldNotNull(
        !(datosConfiguracionSolicitud.getFasePresentacionSolicitudes() == null
            && datosConfiguracionSolicitud.getTramitacionSGI() == Boolean.TRUE),
        ConfiguracionSolicitud.class, MSG_FIELD_PLAZO_PRESENTACION);

    // Si ya hay documentos requeridos solicitud, no se puede cambiar la fase
    if (datosOriginales != null && datosOriginales.getFasePresentacionSolicitudes() != null
        && (datosConfiguracionSolicitud.getFasePresentacionSolicitudes() == null
            || (datosConfiguracionSolicitud.getFasePresentacionSolicitudes() != null
                && !Objects.equals(datosOriginales.getFasePresentacionSolicitudes().getId(), datosConfiguracionSolicitud
                    .getFasePresentacionSolicitudes().getId())))) {

      Specification<DocumentoRequeridoSolicitud> specByConvocatoria = DocumentoRequeridoSolicitudSpecifications
          .byConvocatoriaId(datosOriginales.getConvocatoriaId());
      AssertHelper.entityExists(
          documentoRequeridoSolicitudRepository.findAll(specByConvocatoria, Pageable.unpaged()).isEmpty(),
          ConfiguracionSolicitud.class, DocumentoRequeridoSolicitud.class);
    }

    // Con Convocatoria-Fase seleccionada
    if (datosConfiguracionSolicitud.getFasePresentacionSolicitudes() != null) {
      datosConfiguracionSolicitud.setFasePresentacionSolicitudes(
          convocatoriaFaseRepository.findById(datosConfiguracionSolicitud.getFasePresentacionSolicitudes().getId())
              .orElseThrow(() -> new ConvocatoriaFaseNotFoundException(
                  datosConfiguracionSolicitud.getFasePresentacionSolicitudes().getId())));
    }

    log.debug(
        "validarConfiguracionSolicitud(ConfiguracionSolicitud datosConfiguracionSolicitud, , ConfiguracionSolicitud datosOriginales) - end");
  }

}

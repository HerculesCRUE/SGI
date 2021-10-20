package org.crue.hercules.sgi.csp.service.impl;

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
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ConfiguracionSolicitud}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConfiguracionSolicitudServiceImpl implements ConfiguracionSolicitudService {

  private final ConfiguracionSolicitudRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ConvocatoriaFaseRepository convocatoriaFaseRepository;
  private final DocumentoRequeridoSolicitudRepository documentoRequeridoSolicitudRepository;

  public ConfiguracionSolicitudServiceImpl(ConfiguracionSolicitudRepository repository,
      ConvocatoriaRepository convocatoriaRepository, ConvocatoriaFaseRepository convocatoriaFaseRepository,
      DocumentoRequeridoSolicitudRepository documentoRequeridoSolicitudRepository,
      ConvocatoriaService convocatoriaService) {
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

    Assert.isNull(configuracionSolicitud.getId(), "Id tiene que ser null para crear la ConfiguracionSolicitud");

    Assert.isTrue(configuracionSolicitud.getConvocatoriaId() != null,
        "Convocatoria no puede ser null en ConfiguracionSolicitud");

    Assert.isTrue(!repository.findByConvocatoriaId(configuracionSolicitud.getConvocatoriaId()).isPresent(),
        "Ya existe ConfiguracionSolicitud para la convocatoria " + configuracionSolicitud.getConvocatoriaId());

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

    Assert.notNull(convocatoriaId, "Convocatoria no puede ser null en ConfiguracionSolicitud");

    return repository.findByConvocatoriaId(configuracionSolicitud.getConvocatoriaId()).map((data) -> {

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
      Assert.notNull(datosConfiguracionSolicitud.getTramitacionSGI(),
          "Habilitar presentacion SGI no puede ser null para crear ConfiguracionSolicitud cuando la convocatoria está registrada");
    }

    Assert.isTrue(
        !(datosConfiguracionSolicitud.getFasePresentacionSolicitudes() == null
            && datosConfiguracionSolicitud.getTramitacionSGI() == Boolean.TRUE),
        "Plazo presentación solicitudes no puede ser null cuando se establece presentacion SGI");

    // Si ya hay documentos requeridos solicitud, no se puede cambiar la fase
    if (datosOriginales != null && datosOriginales.getFasePresentacionSolicitudes() != null
        && (datosConfiguracionSolicitud.getFasePresentacionSolicitudes() == null || (datosConfiguracionSolicitud != null
            && datosConfiguracionSolicitud.getFasePresentacionSolicitudes() != null
            && datosOriginales.getFasePresentacionSolicitudes().getId() != datosConfiguracionSolicitud
                .getFasePresentacionSolicitudes().getId()))) {

      Specification<DocumentoRequeridoSolicitud> specByConvocatoria = DocumentoRequeridoSolicitudSpecifications
          .byConvocatoriaId(datosOriginales.getConvocatoriaId());
      Assert.isTrue(documentoRequeridoSolicitudRepository.findAll(specByConvocatoria, Pageable.unpaged()).isEmpty(),
          "Si ya existen documentos requeridos solicitud asociados a la configuración, no se puede cambiar la fase");
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

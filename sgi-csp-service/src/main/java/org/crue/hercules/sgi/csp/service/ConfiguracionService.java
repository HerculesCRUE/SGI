package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.dto.ConfigParamOutput;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionNotFoundException;
import org.crue.hercules.sgi.csp.model.Configuracion;
import org.crue.hercules.sgi.csp.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Configuracion}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ConfiguracionService {

  private final ConfiguracionRepository repository;

  /**
   * Obtiene la entidad {@link Configuracion}.
   *
   * @return la entidad {@link Configuracion}.
   * @throws ConfiguracionNotFoundException Si no existe ninguna
   *                                        {@link Configuracion} con ese id.
   */
  public Configuracion findConfiguracion() {
    log.debug("findConfiguracion()  - start");
    final Configuracion configuracion = repository.findFirstByOrderByIdAsc()
        .orElseThrow(() -> new ConfiguracionNotFoundException(null));
    log.debug("findConfiguracion()  - end");
    return configuracion;

  }

  /**
   * Actualiza los datos de la entidad {@link Configuracion}.
   * 
   * @param configuracionActualizar {@link Configuracion} con los datos
   *                                actualizados.
   * @return La {@link Configuracion} actualizada.
   * @throws ConfiguracionNotFoundException Si no existe ningún
   *                                        {@link Configuracion} con ese id.
   * @throws IllegalArgumentException       Si la {@link Configuracion} no tiene
   *                                        id.
   */

  @Transactional
  public Configuracion update(final Configuracion configuracionActualizar) {
    log.debug("update(Configuracion configuracionActualizar) - start");

    Assert.notNull(configuracionActualizar.getId(),
        "Configuracion id no puede ser null para actualizar una configuracion");

    return repository.findById(configuracionActualizar.getId()).map(configuracion -> {
      configuracion.setFormatoPartidaPresupuestaria(configuracionActualizar.getFormatoPartidaPresupuestaria());
      configuracion
          .setFormatoIdentificadorJustificacion(configuracionActualizar.getFormatoIdentificadorJustificacion());

      Configuracion returnValue = repository.save(configuracion);
      log.debug("update(Configuracion configuracionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConfiguracionNotFoundException(configuracionActualizar.getId()));
  }

  /**
   * Get a {@link ConfigParamOutput}
   * 
   * @param name the name of the {@link ConfigParamOutput} to get
   * @return the {@link ConfigParamOutput} with the provided name
   */
  public ConfigParamOutput get(String name) {
    log.debug("get(String name) - start");
    AssertHelper.fieldNotNull(name, Configuracion.class, AssertHelper.MESSAGE_KEY_NAME);
    Configuracion.Param param = Configuracion.Param.fromKey(name);
    String value = this.findConfiguracion().getParamValue(param).toString();
    ConfigParamOutput returnValue = ConfigParamOutput.builder()
        .name(param.getKey())
        .description(param.getDescription())
        .value(value)
        .build();
    log.debug("get(String name) - end");
    return returnValue;
  }

  /**
   * Updates the value of an existing {@link ConfigParamOutput}
   * 
   * @param name  the name of the {@link ConfigParamOutput} to update
   * @param value the new value
   * @return Config the updated {@link ConfigParamOutput}
   */
  @Transactional
  public ConfigParamOutput updateValue(String name, String value) {
    log.debug("updateValue(String name, String value) - start");

    AssertHelper.fieldNotNull(name, Configuracion.class, AssertHelper.MESSAGE_KEY_NAME);

    return repository.findFirstByOrderByIdAsc().map(data -> {
      Configuracion.Param param = Configuracion.Param.fromKey(name);
      data.updateParamValue(param, value);
      repository.save(data);

      ConfigParamOutput returnValue = ConfigParamOutput.builder()
          .name(param.getKey())
          .description(param.getDescription())
          .value(data.getParamValue(param).toString())
          .build();

      log.debug("updateValue(String name, String value) - end");
      return returnValue;
    }).orElseThrow(() -> new ConfiguracionNotFoundException(null));
  }

}

package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.ConfiguracionNotFoundException;
import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.eti.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.eti.service.ConfiguracionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Configuracion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConfiguracionServiceImpl implements ConfiguracionService {
  private final ConfiguracionRepository configuracionRepository;

  public ConfiguracionServiceImpl(ConfiguracionRepository configuracionRepository) {
    this.configuracionRepository = configuracionRepository;
  }

  /**
   * Obtiene una entidad {@link Configuracion}.
   *
   * @return la entidad {@link Configuracion}.
   * @throws ConfiguracionNotFoundException Si no existe ningún
   *                                        {@link Configuracion} con ese id.
   */
  public Configuracion findConfiguracion() {
    log.debug("Petición a get Configuracion : {}  - start");
    final Configuracion configuracion = configuracionRepository.findFirstByOrderByIdAsc()
        .orElseThrow(() -> new ConfiguracionNotFoundException(null));
    log.debug("Petición a get Configuracion : {}  - end");
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

    return configuracionRepository.findById(configuracionActualizar.getId()).map(configuracion -> {
      configuracion
          .setDiasArchivadaPendienteCorrecciones(configuracionActualizar.getDiasArchivadaPendienteCorrecciones());
      configuracion.setDiasLimiteEvaluador(configuracionActualizar.getDiasLimiteEvaluador());
      configuracion.setMesesArchivadaInactivo(configuracionActualizar.getMesesArchivadaInactivo());
      configuracion.setMesesAvisoProyectoCEEA(configuracionActualizar.getMesesAvisoProyectoCEEA());
      configuracion.setMesesAvisoProyectoCEISH(configuracionActualizar.getMesesAvisoProyectoCEISH());
      configuracion.setMesesAvisoProyectoCEIAB(configuracionActualizar.getMesesAvisoProyectoCEIAB());

      Configuracion returnValue = configuracionRepository.save(configuracion);
      log.debug("update(Configuracion configuracionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConfiguracionNotFoundException(configuracionActualizar.getId()));
  }
}

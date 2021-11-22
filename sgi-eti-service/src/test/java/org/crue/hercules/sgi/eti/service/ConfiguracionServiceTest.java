package org.crue.hercules.sgi.eti.service;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ConfiguracionNotFoundException;
import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.eti.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.eti.service.impl.ConfiguracionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;

/**
 * ConfiguracionServiceTest
 */
public class ConfiguracionServiceTest extends BaseServiceTest {

  @Mock
  private ConfiguracionRepository configuracionRepository;

  private ConfiguracionService configuracionService;

  @BeforeEach
  public void setUp() throws Exception {
    configuracionService = new ConfiguracionServiceImpl(configuracionRepository);
  }

  @Test
  public void finConfiguracion_ReturnsConfiguracion() {
    BDDMockito.given(configuracionRepository.findFirstByOrderByIdAsc())
        .willReturn(Optional.of(generarMockConfiguracion()));

    Configuracion configuracion = configuracionService.findConfiguracion();

    Assertions.assertThat(configuracion.getId()).isEqualTo(1L);
    Assertions.assertThat(configuracion.getDiasLimiteEvaluador()).isEqualTo(3);
  }

  @Test
  public void update_ReturnsConfiguracion() {
    // given: Una nueva configuracion con el servicio actualizado
    Configuracion configuracionServicioActualizado = generarMockConfiguracionActualizada();

    Configuracion configuracion = generarMockConfiguracion();

    BDDMockito.given(configuracionRepository.findById(1L)).willReturn(Optional.of(configuracion));
    BDDMockito.given(configuracionRepository.save(configuracion)).willReturn(configuracionServicioActualizado);

    // when: Actualizamos la configuracion
    Configuracion configuracionActualizado = configuracionService.update(configuracion);

    // then: La configuracion se actualiza correctamente.
    Assertions.assertThat(configuracionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(configuracionActualizado.getDiasLimiteEvaluador()).isEqualTo(10);
  }

  @Test
  public void update_ThrowsConfiguracionNotFoundException() {
    // given: Una nueva configuracion a actualizar
    Configuracion configuracion = generarMockConfiguracion();

    // then: Lanza una excepcion porque la configuracion no existe
    Assertions.assertThatThrownBy(() -> configuracionService.update(configuracion))
        .isInstanceOf(ConfiguracionNotFoundException.class);

  }

  /**
   * Función que devuelve un objeto Configuracion
   * 
   * @return el objeto Configuracion
   */

  public Configuracion generarMockConfiguracion() {

    Configuracion configuracion = new Configuracion();

    configuracion.setId(1L);
    configuracion.setMesesArchivadaPendienteCorrecciones(20);
    configuracion.setDiasLimiteEvaluador(3);
    configuracion.setDiasArchivadaInactivo(2);

    return configuracion;
  }

  /**
   * Función que devuelve un objeto Configuracion
   * 
   * @return el objeto Configuracion
   */

  public Configuracion generarMockConfiguracionActualizada() {

    Configuracion configuracion = new Configuracion();

    configuracion.setId(1L);
    configuracion.setMesesArchivadaPendienteCorrecciones(20);
    configuracion.setDiasLimiteEvaluador(10);
    configuracion.setDiasArchivadaInactivo(2);

    return configuracion;
  }

}
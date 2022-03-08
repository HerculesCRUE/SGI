package org.crue.hercules.sgi.csp.service;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoAmortizacion;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoAmortizacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.validation.Validator;

class ProyectoPeriodoAmortizacionServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoPeriodoAmortizacionRepository proyectoPeriodoAmortizacionRepository;
  @Mock
  private Validator validator;

  private ProyectoPeriodoAmortizacionService proyectoPeriodoAmortizacionService;

  @BeforeEach
  public void setup(){
    this.proyectoPeriodoAmortizacionService = new ProyectoPeriodoAmortizacionService(this.validator, this.proyectoPeriodoAmortizacionRepository);
  }

  @Test
  void create_WithProyectoPeriodoAmortizacionIdNotNull_ThrowsIllegalArgumentException() {

    ProyectoPeriodoAmortizacion proyectoPeriodoAmortizacion = buildMockProyectoPeriodoAmortizacion(1L);

    Assertions.assertThatThrownBy(() -> this.proyectoPeriodoAmortizacionService.create(proyectoPeriodoAmortizacion)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_WithProyectoPeriodoAmortizacionIdNull_ThrowsIllegalArgumentException() {
    ProyectoPeriodoAmortizacion proyectoPeriodoAmortizacion = buildMockProyectoPeriodoAmortizacion(null);

    Assertions.assertThatThrownBy(() -> this.proyectoPeriodoAmortizacionService.update(proyectoPeriodoAmortizacion)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void delete_WithProyectoPeriodoAmortizacionIdNull_ThrowsIllegalArgumentException() {
    Assertions.assertThatThrownBy(() -> this.proyectoPeriodoAmortizacionService.delete(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void findById_WithProyectoPeriodoAmortizacionIdNull_ThrowsIllegalArgumentException() {
    Assertions.assertThatThrownBy(() -> this.proyectoPeriodoAmortizacionService.findById(null)).isInstanceOf(IllegalArgumentException.class);
  }

  private ProyectoPeriodoAmortizacion buildMockProyectoPeriodoAmortizacion(Long id) {
    return ProyectoPeriodoAmortizacion.builder()
    .id(id)
    .build();
  }
}

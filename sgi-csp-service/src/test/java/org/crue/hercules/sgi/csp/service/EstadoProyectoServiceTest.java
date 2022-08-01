package org.crue.hercules.sgi.csp.service;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.repository.EstadoProyectoRepository;
import org.crue.hercules.sgi.csp.service.impl.EstadoProyectoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;

class EstadoProyectoServiceTest extends BaseServiceTest {

  @Mock
  private EstadoProyectoRepository repository;

  private EstadoProyectoService service;

  @BeforeEach
  void setup() {
    this.service = new EstadoProyectoServiceImpl(repository);
  }

  @Test
  void create_ReturnsEstadoProyecto() {
    // @formatter=off
    EstadoProyecto estado = EstadoProyecto
        .builder()
        .proyectoId(1L)
        .build();
    // @formatter=on

    BDDMockito.given(this.repository.save(ArgumentMatchers.<EstadoProyecto>any())).willReturn(estado);

    EstadoProyecto newEstado = this.service.create(estado);

    Assertions.assertThat(newEstado).isNotNull();
  }

  @Test
  void create_WithIdNotNull_ThrowsAssertException() {
    // @formatter=off
    EstadoProyecto estado = EstadoProyecto
        .builder()
        .id(1L)
        .proyectoId(1L)
        .build();
    // @formatter=on

    Assertions.assertThatThrownBy(() -> this.service.create(estado)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void create_WithProyectoIdNull_ThrowsAssertException() {
    // @formatter=off
    EstadoProyecto estado = EstadoProyecto
        .builder()
        .build();
    // @formatter=on

    Assertions.assertThatThrownBy(() -> this.service.create(estado)).isInstanceOf(IllegalArgumentException.class);
  }
}
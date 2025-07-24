package org.crue.hercules.sgi.csp.service;

import static org.mockito.ArgumentMatchers.anyLong;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.exceptions.AlreadyInEstadoAutorizacionException;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.crue.hercules.sgi.csp.repository.AutorizacionRepository;
import org.crue.hercules.sgi.csp.repository.EstadoAutorizacionRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiRepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;

class AutorizacionServiceTest extends BaseServiceTest {

  @Mock
  private AutorizacionRepository autorizacionRepository;
  @Mock
  private EstadoAutorizacionRepository estadoAutorizacionRepository;
  @Mock
  private SgiApiRepService reportService;
  @Mock
  private SgdocService sgdocService;
  @Mock
  private SgiConfigProperties sgiConfigProperties;
  @Mock
  private AutorizacionComService autorizacionComService;

  private AutorizacionService autorizacionService;

  @BeforeEach
  void setup() {
    this.autorizacionService = new AutorizacionService(
        autorizacionRepository,
        estadoAutorizacionRepository,
        reportService,
        sgdocService,
        sgiConfigProperties,
        autorizacionComService);
  }

  @Test
  void create_WithAutorizacionIdNotNull_ThrowsIllegalArgumentException() {
    Autorizacion autorizacion = this.buildMockAutorizacion(1L, null);

    Assertions.assertThatThrownBy(() -> this.autorizacionService.create(autorizacion))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_WithAutorizacionIdNull_ThrowsIllegalArgumentException() {
    Autorizacion autorizacion = this.buildMockAutorizacion(null, null);

    Assertions.assertThatThrownBy(() -> this.autorizacionService.update(autorizacion))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void delete_WithAutorizacionIdNull_ThrowsIllegalArgumentException() {
    Assertions.assertThatThrownBy(() -> this.autorizacionService.delete(null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @WithMockUser(authorities = { "CSP-AUT-E" })
  @Test
  void cambiarEstado_WithSameEstado_ThrowsAlreadyInEstadoAutorizacionException() {
    Long autorizacionId = 1L;
    Long newEstadoId = 1L;
    EstadoAutorizacion newEstado = this.buildMockEstadoAutorizacion(newEstadoId, autorizacionId,
        EstadoAutorizacion.Estado.AUTORIZADA);
    Autorizacion autorizacion = this.buildMockAutorizacion(autorizacionId, newEstado);

    BDDMockito.given(this.autorizacionRepository.findById(anyLong())).willReturn(Optional.of(autorizacion));

    Assertions.assertThatThrownBy(() -> this.autorizacionService.cambiarEstado(autorizacionId, newEstado))
        .isInstanceOf(AlreadyInEstadoAutorizacionException.class);
  }

  @Test
  void presentable_WithAutorizacionIdNull_ThrowsIllegalArgumentException() {
    Assertions.assertThatThrownBy(() -> this.autorizacionService.presentable(null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  private EstadoAutorizacion buildMockEstadoAutorizacion(Long id, Long autorizacionId,
      EstadoAutorizacion.Estado estado) {
    return EstadoAutorizacion.builder()
        .id(id)
        .autorizacionId(autorizacionId)
        .estado(estado)
        .build();
  }

  private Autorizacion buildMockAutorizacion(Long id, EstadoAutorizacion estado) {
    return Autorizacion.builder()
        .id(id)
        .estado(estado)
        .build();
  }
}
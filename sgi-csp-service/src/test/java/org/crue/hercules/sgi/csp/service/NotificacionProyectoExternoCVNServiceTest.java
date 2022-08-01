package org.crue.hercules.sgi.csp.service;

import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.NotificacionCVNEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.crue.hercules.sgi.csp.repository.NotificacionCVNEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.NotificacionProyectoExternoCVNRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class NotificacionProyectoExternoCVNServiceTest extends BaseServiceTest {

  @Mock
  private NotificacionProyectoExternoCVNRepository notificacionProyectoExternoCVNRepository;
  @Mock
  private NotificacionCVNEntidadFinanciadoraRepository notificacionCVNEntidadFinanciadoraRepository;
  @Mock
  private NotificacionProyectoExternoCVNComService notificacionProyectoExternoCVNComService;

  private NotificacionProyectoExternoCVNService notificacionProyectoExternoCVNService;

  @BeforeEach
  void setup() {
    this.notificacionProyectoExternoCVNService = new NotificacionProyectoExternoCVNService(
        notificacionProyectoExternoCVNRepository, notificacionCVNEntidadFinanciadoraRepository,
        notificacionProyectoExternoCVNComService);
  }

  @Test
  void create_WithIdIsNotNull_ThrowsIllegalArgumentException() {
    NotificacionProyectoExternoCVN notificacionProyectoExternoCVN = this.buildMockNotificacionProyectoExternoCVN(1L,
        "54333666", "00842223");
    List<NotificacionCVNEntidadFinanciadora> notificacionesEntidadFinanciadoras = new LinkedList<>();

    Assertions.assertThatThrownBy(() -> this.notificacionProyectoExternoCVNService
        .create(notificacionProyectoExternoCVN, notificacionesEntidadFinanciadoras))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void asociarAutorizacion_WithIdNull_ThrowsIllegalArgumentException() {
    NotificacionProyectoExternoCVN notificacionProyectoExternoCVN = this.buildMockNotificacionProyectoExternoCVN(null,
        "54333666", "00842223");

    Assertions
        .assertThatThrownBy(
            () -> this.notificacionProyectoExternoCVNService.asociarAutorizacion(notificacionProyectoExternoCVN))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void asociarProyecto_WithIdNull_ThrowsIllegalArgumentException() {
    NotificacionProyectoExternoCVN notificacionProyectoExternoCVN = this.buildMockNotificacionProyectoExternoCVN(null,
        "54333666", "00842223");

    Assertions
        .assertThatThrownBy(
            () -> this.notificacionProyectoExternoCVNService.asociarProyecto(notificacionProyectoExternoCVN))
        .isInstanceOf(IllegalArgumentException.class);
  }

  private NotificacionProyectoExternoCVN buildMockNotificacionProyectoExternoCVN(Long id, String responsableRef,
      String entidadParticipacionRef) {
    return NotificacionProyectoExternoCVN.builder()
        .id(id)
        .responsableRef(responsableRef)
        .entidadParticipacionRef(entidadParticipacionRef)
        .build();
  }
}
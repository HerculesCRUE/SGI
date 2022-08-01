package org.crue.hercules.sgi.csp.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoResponsableEconomicoMaxMonthException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoResponsableEconomicoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoResponsableEconomicoOverlapRangeException;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoResponsableEconomicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;

class SolicitudProyectoResponsableEconomicoServiceTest extends BaseServiceTest {

  @Mock
  private Validator validator;
  @Mock
  private SolicitudProyectoResponsableEconomicoRepository repository;
  @Mock
  private SolicitudProyectoRepository solicitudProyectoRepository;

  private SolicitudProyectoResponsableEconomicoService service;

  @BeforeEach
  void setup() {
    this.service = new SolicitudProyectoResponsableEconomicoService(this.validator, this.repository,
        this.solicitudProyectoRepository);
  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-INV-ER" })
  @Test
  void updateSolicitudProyectoResponsableEconomicos_ThrowsSolicitudProyectoResponsableEconomicoNotFoundException() {
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = SolicitudProyecto.builder()
        .id(solicitudProyectoId)
        .build();
    List<SolicitudProyectoResponsableEconomico> responsablesToUpdate = Arrays
        .asList(this.buildMockSolicitudProyectoResponsableEconomico(1L, solicitudProyectoId, 1));
    List<SolicitudProyectoResponsableEconomico> responsablesToUpdateDB = Arrays
        .asList(this.buildMockSolicitudProyectoResponsableEconomico(2L, solicitudProyectoId, 1));

    BDDMockito.given(this.solicitudProyectoRepository.findById(anyLong())).willReturn(Optional.of(solicitudProyecto));

    BDDMockito.given(repository.findAllBySolicitudProyectoId(solicitudProyectoId)).willReturn(responsablesToUpdateDB);

    Assertions
        .assertThatThrownBy(
            () -> this.service.updateSolicitudProyectoResponsableEconomicos(solicitudProyectoId, responsablesToUpdate))
        .isInstanceOf(SolicitudProyectoResponsableEconomicoNotFoundException.class);
  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-INV-ER" })
  @Test
  void updateSolicitudProyectoResponsableEconomicos_WithSolicitudProyectoIdNull_VerifySolicitudProyectoResponsableEconomicoDeletion_AndThrowsIllegalArgumentException() {
    Long solicitudProyectoId = null;
    SolicitudProyecto solicitudProyecto = SolicitudProyecto.builder()
        .id(solicitudProyectoId)
        .build();
    List<SolicitudProyectoResponsableEconomico> responsablesToUpdate = Arrays
        .asList(this.buildMockSolicitudProyectoResponsableEconomico(1L, solicitudProyectoId, 1));
    //@formatter:off
    List<SolicitudProyectoResponsableEconomico> responsablesToUpdateDB = Arrays.asList(this.buildMockSolicitudProyectoResponsableEconomico(1L, solicitudProyectoId, 1),
                                                                                       this.buildMockSolicitudProyectoResponsableEconomico(2L, solicitudProyectoId, 1));
  
    //@formatter:on 

    BDDMockito.given(this.solicitudProyectoRepository.findById(ArgumentMatchers.any()))
        .willReturn(Optional.of(solicitudProyecto));

    BDDMockito.given(repository.findAllBySolicitudProyectoId(solicitudProyectoId)).willReturn(responsablesToUpdateDB);

    Assertions
        .assertThatThrownBy(
            () -> this.service.updateSolicitudProyectoResponsableEconomicos(solicitudProyectoId, responsablesToUpdate))
        .isInstanceOf(IllegalArgumentException.class);

    verify(this.repository, times(1)).deleteAll(ArgumentMatchers.<List<SolicitudProyectoResponsableEconomico>>any());

  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-INV-ER" })
  @Test
  void updateSolicitudProyectoResponsableEconomicos_WithPersonaRefNull_VerifySolicitudProyectoResponsableEconomicoDeletion_AndThrowsIllegalArgumentException() {
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = SolicitudProyecto.builder()
        .id(solicitudProyectoId)
        .build();
    List<SolicitudProyectoResponsableEconomico> responsablesToUpdate = Arrays
        .asList(this.buildMockSolicitudProyectoResponsableEconomico(1L, solicitudProyectoId, 1));
    //@formatter:off
    List<SolicitudProyectoResponsableEconomico> responsablesToUpdateDB = Arrays.asList(this.buildMockSolicitudProyectoResponsableEconomico(1L, solicitudProyectoId, 1),
                                                                                       this.buildMockSolicitudProyectoResponsableEconomico(2L, solicitudProyectoId, 1));
  
    //@formatter:on 

    BDDMockito.given(this.solicitudProyectoRepository.findById(ArgumentMatchers.any()))
        .willReturn(Optional.of(solicitudProyecto));

    BDDMockito.given(repository.findAllBySolicitudProyectoId(solicitudProyectoId)).willReturn(responsablesToUpdateDB);

    Assertions
        .assertThatThrownBy(
            () -> this.service.updateSolicitudProyectoResponsableEconomicos(solicitudProyectoId, responsablesToUpdate))
        .isInstanceOf(IllegalArgumentException.class);

    verify(this.repository, times(1)).deleteAll(ArgumentMatchers.<List<SolicitudProyectoResponsableEconomico>>any());

  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-INV-ER" })
  @Test
  void updateSolicitudProyectoResponsableEconomicos_ThrowsSolicitudProyectoResponsableEconomicoMaxMonthException() {
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = SolicitudProyecto.builder()
        .id(solicitudProyectoId)
        .duracion(1)
        .build();
    SolicitudProyectoResponsableEconomico responsableEconomicoAnterior = this
        .buildMockSolicitudProyectoResponsableEconomico(1L, solicitudProyectoId, 1);

    List<SolicitudProyectoResponsableEconomico> responsablesToUpdate = Arrays.asList(responsableEconomicoAnterior,
        this.buildMockSolicitudProyectoResponsableEconomico(2L, solicitudProyectoId, 2));
    responsablesToUpdate.get(0).setPersonaRef("user");
    responsablesToUpdate.get(1).setPersonaRef("user");
    //@formatter:off
    List<SolicitudProyectoResponsableEconomico> responsablesToUpdateDB = Arrays.asList(this.buildMockSolicitudProyectoResponsableEconomico(1L, solicitudProyectoId, 1),
                                                                                       this.buildMockSolicitudProyectoResponsableEconomico(2L, solicitudProyectoId, 2));
  
    //@formatter:on 

    BDDMockito.given(this.solicitudProyectoRepository.findById(ArgumentMatchers.any()))
        .willReturn(Optional.of(solicitudProyecto));

    BDDMockito.given(repository.findAllBySolicitudProyectoId(solicitudProyectoId)).willReturn(responsablesToUpdateDB);

    Assertions
        .assertThatThrownBy(
            () -> this.service.updateSolicitudProyectoResponsableEconomicos(solicitudProyectoId, responsablesToUpdate))
        .isInstanceOf(SolicitudProyectoResponsableEconomicoMaxMonthException.class);

  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E", "CSP-SOL-INV-ER" })
  @Test
  void updateSolicitudProyectoResponsableEconomicos_ThrowsSolicitudProyectoResponsableEconomicoOverlapRangeException() {
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = SolicitudProyecto.builder()
        .id(solicitudProyectoId)
        .build();
    SolicitudProyectoResponsableEconomico responsableEconomicoAnterior = this
        .buildMockSolicitudProyectoResponsableEconomico(1L, solicitudProyectoId, 1);

    List<SolicitudProyectoResponsableEconomico> responsablesToUpdate = Arrays.asList(responsableEconomicoAnterior,
        this.buildMockSolicitudProyectoResponsableEconomico(2L, solicitudProyectoId, 1));
    responsablesToUpdate.get(0).setPersonaRef("user");
    responsablesToUpdate.get(1).setPersonaRef("user");
    //@formatter:off
    List<SolicitudProyectoResponsableEconomico> responsablesToUpdateDB = Arrays.asList(this.buildMockSolicitudProyectoResponsableEconomico(1L, solicitudProyectoId, 1),
                                                                                       this.buildMockSolicitudProyectoResponsableEconomico(2L, solicitudProyectoId, 2));
  
    //@formatter:on 

    BDDMockito.given(this.solicitudProyectoRepository.findById(ArgumentMatchers.any()))
        .willReturn(Optional.of(solicitudProyecto));

    BDDMockito.given(repository.findAllBySolicitudProyectoId(solicitudProyectoId)).willReturn(responsablesToUpdateDB);

    Assertions
        .assertThatThrownBy(
            () -> this.service.updateSolicitudProyectoResponsableEconomicos(solicitudProyectoId, responsablesToUpdate))
        .isInstanceOf(SolicitudProyectoResponsableEconomicoOverlapRangeException.class);

  }

  private SolicitudProyectoResponsableEconomico buildMockSolicitudProyectoResponsableEconomico(Long id,
      Long solicitudProyectoId, int mesInicio) {
    return SolicitudProyectoResponsableEconomico.builder()
        .id(id)
        .solicitudProyectoId(solicitudProyectoId)
        .mesInicio(mesInicio)
        .build();
  }
}
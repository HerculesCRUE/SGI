package org.crue.hercules.sgi.csp.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoResponsableEconomicoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoResponsableEconomicoProjectRangeException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoResponsableEconomicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;

class ProyectoResponsableEconomicoServiceTest extends BaseServiceTest {

  @Mock
  private Validator validator;
  @Mock
  private ProyectoResponsableEconomicoRepository proyectoResponsableEconomicoRepository;
  @Mock
  private ProyectoRepository proyectoRepository;

  ProyectoResponsableEconomicoService proyectoResponsableEconomicoService;

  @BeforeEach
  void setup() {
    this.proyectoResponsableEconomicoService = new ProyectoResponsableEconomicoService(validator,
        proyectoResponsableEconomicoRepository, proyectoRepository);
  }

  @Test
  void updateProyectoResponsableEconomicos_WithResponsableEconomicoProyectoIdNull_ThrowsIllegalArgumentException() {
    Long proyectoId = 1L;
    List<ProyectoResponsableEconomico> responsablesEconomicos = Arrays.asList(
        buildMockProyectoResponsableEconomico(1L, null, "556699", Instant.now().plusSeconds(3600000), Instant.now()));
    List<ProyectoResponsableEconomico> responsablesEconomicosBD = Arrays.asList(buildMockProyectoResponsableEconomico(
        1L, proyectoId, "556699", Instant.now(), Instant.now().plusSeconds(3600000)));

    BDDMockito.given(this.proyectoRepository.findById(proyectoId)).willReturn(Optional.of(Proyecto.builder()
        .id(proyectoId)
        .fechaInicio(Instant.now().minusSeconds(3600))
        .fechaFin(Instant.now().minusSeconds(3600))
        .build()));

    BDDMockito.given(this.proyectoResponsableEconomicoRepository.findByProyectoId(proyectoId))
        .willReturn(responsablesEconomicosBD);

    Assertions
        .assertThatThrownBy(() -> this.proyectoResponsableEconomicoService
            .updateProyectoResponsableEconomicos(proyectoId, responsablesEconomicos))
        .isInstanceOf(IllegalArgumentException.class);

  }

  @Test
  void updateProyectoResponsableEconomicos_WithResponsableEconomicoPersonaRefNull_ThrowsIllegalArgumentException() {
    Long proyectoId = 1L;
    List<ProyectoResponsableEconomico> responsablesEconomicos = Arrays.asList(
        buildMockProyectoResponsableEconomico(1L, proyectoId, null, Instant.now().plusSeconds(3600000), Instant.now()));
    List<ProyectoResponsableEconomico> responsablesEconomicosBD = Arrays.asList(buildMockProyectoResponsableEconomico(
        1L, proyectoId, "556699", Instant.now(), Instant.now().plusSeconds(3600000)));

    BDDMockito.given(this.proyectoRepository.findById(proyectoId)).willReturn(Optional.of(Proyecto.builder()
        .id(proyectoId)
        .fechaInicio(Instant.now().minusSeconds(3600))
        .fechaFin(Instant.now().minusSeconds(3600))
        .build()));

    BDDMockito.given(this.proyectoResponsableEconomicoRepository.findByProyectoId(proyectoId))
        .willReturn(responsablesEconomicosBD);

    Assertions
        .assertThatThrownBy(() -> this.proyectoResponsableEconomicoService
            .updateProyectoResponsableEconomicos(proyectoId, responsablesEconomicos))
        .isInstanceOf(IllegalArgumentException.class);

  }

  @Test
  void updateProyectoResponsableEconomicos_WithFechaFinBeforeFechaInicio_ThrowsProyectoResponsableEconomicoProjectRangeException() {
    Long proyectoId = 1L;
    List<ProyectoResponsableEconomico> responsablesEconomicos = Arrays.asList(buildMockProyectoResponsableEconomico(1L,
        proyectoId, "556699", Instant.now(), Instant.now().plusSeconds(3600000)));
    List<ProyectoResponsableEconomico> responsablesEconomicosBD = Arrays.asList(buildMockProyectoResponsableEconomico(
        1L, proyectoId, "556699", Instant.now(), Instant.now().plusSeconds(3600000)));

    BDDMockito.given(this.proyectoRepository.findById(proyectoId)).willReturn(Optional.of(Proyecto.builder()
        .id(proyectoId)
        .fechaInicio(Instant.now().minusSeconds(3600))
        .fechaFin(Instant.now().minusSeconds(3600))
        .build()));

    BDDMockito.given(this.proyectoResponsableEconomicoRepository.findByProyectoId(proyectoId))
        .willReturn(responsablesEconomicosBD);

    Assertions
        .assertThatThrownBy(() -> this.proyectoResponsableEconomicoService
            .updateProyectoResponsableEconomicos(proyectoId, responsablesEconomicos))
        .isInstanceOf(ProyectoResponsableEconomicoProjectRangeException.class);

  }

  @WithMockUser(username = "user", authorities = { "CSP-PRO-E", "CSP-PRO-INV-ER" })
  @Test
  void updateProyectoResponsableEconomicos_ThrowsProyectoResponsableEconomicoNotFoundException() {
    Long proyectoId = 1L;
    Proyecto proyecto = Proyecto.builder()
        .id(proyectoId)
        .build();
    List<ProyectoResponsableEconomico> responsablesToUpdate = Arrays
        .asList(this.buildMockProyectoResponsableEconomico(1L, proyectoId, "user", null, null));
    List<ProyectoResponsableEconomico> responsablesToUpdateDB = Arrays
        .asList(this.buildMockProyectoResponsableEconomico(2L, proyectoId, "user", null, null));

    BDDMockito.given(this.proyectoRepository.findById(anyLong())).willReturn(Optional.of(proyecto));

    BDDMockito.given(this.proyectoResponsableEconomicoRepository.findByProyectoId(proyectoId))
        .willReturn(responsablesToUpdateDB);

    Assertions
        .assertThatThrownBy(() -> this.proyectoResponsableEconomicoService
            .updateProyectoResponsableEconomicos(proyectoId, responsablesToUpdate))
        .isInstanceOf(ProyectoResponsableEconomicoNotFoundException.class);
  }

  @WithMockUser(username = "user", authorities = { "CSP-PRO-E", "CSP-PRO-INV-ER" })
  @Test
  void updateProyectoResponsableEconomicos_WithProyectoIdNull_VerifyProyectoResponsableEconomicoDeletion_AndThrowsIllegalArgumentException() {
    Long solicitudProyectoId = null;
    Proyecto proyecto = Proyecto.builder()
        .id(solicitudProyectoId)
        .build();
    List<ProyectoResponsableEconomico> responsablesToUpdate = Arrays
        .asList(this.buildMockProyectoResponsableEconomico(1L, solicitudProyectoId, "user", null, null));
    //@formatter:off
    List<ProyectoResponsableEconomico> responsablesToUpdateDB = Arrays.asList(this.buildMockProyectoResponsableEconomico(1L, solicitudProyectoId, "user", null, null),
                                                                                       this.buildMockProyectoResponsableEconomico(2L, solicitudProyectoId, "user", null, null));
  
    //@formatter:on 

    BDDMockito.given(this.proyectoRepository.findById(ArgumentMatchers.any())).willReturn(Optional.of(proyecto));

    BDDMockito.given(this.proyectoResponsableEconomicoRepository.findByProyectoId(solicitudProyectoId))
        .willReturn(responsablesToUpdateDB);

    Assertions
        .assertThatThrownBy(() -> this.proyectoResponsableEconomicoService
            .updateProyectoResponsableEconomicos(solicitudProyectoId, responsablesToUpdate))
        .isInstanceOf(IllegalArgumentException.class);

    verify(this.proyectoResponsableEconomicoRepository, times(1))
        .deleteAll(ArgumentMatchers.<List<ProyectoResponsableEconomico>>any());

  }

  private ProyectoResponsableEconomico buildMockProyectoResponsableEconomico(Long id, Long proyectoId,
      String personaRef, Instant fechaFin, Instant fechaInicio) {
    return ProyectoResponsableEconomico.builder()
        .fechaFin(fechaFin)
        .fechaInicio(fechaInicio)
        .id(id)
        .proyectoId(proyectoId)
        .personaRef(personaRef)
        .build();
  }
}
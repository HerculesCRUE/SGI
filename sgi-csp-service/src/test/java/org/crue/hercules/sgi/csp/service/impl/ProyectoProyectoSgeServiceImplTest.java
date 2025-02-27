package org.crue.hercules.sgi.csp.service.impl;

import static org.mockito.ArgumentMatchers.anyLong;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.exceptions.ProyectoProyectoSgeNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessProyectoException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.repository.GastoProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoProyectoSgeRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoResponsableEconomicoRepository;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.crue.hercules.sgi.csp.service.ConfiguracionService;
import org.crue.hercules.sgi.csp.service.ProyectoAnualidadService;
import org.crue.hercules.sgi.csp.service.ProyectoProyectoSgeService;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;

class ProyectoProyectoSgeServiceImplTest extends BaseServiceTest {

  @Mock
  private ProyectoProyectoSgeRepository repository;
  @Mock
  private ProyectoEquipoRepository proyectoEquipoRepository;
  @Mock
  private ProyectoResponsableEconomicoRepository proyectoResponsableEconomicoRepository;
  @Mock
  private SgiConfigProperties sgiConfigProperties;
  @Mock
  private ProyectoRepository proyectoRepository;
  @Mock
  private ConfiguracionService configuracionService;
  @Mock
  private ProyectoAnualidadService proyectoAnualidadService;
  @Mock
  private GastoProyectoRepository gastoProyectoRepository;

  private ProyectoHelper proyectoHelper;
  private ProyectoProyectoSgeService service;

  @BeforeEach
  void setup() {
    this.proyectoHelper = new ProyectoHelper(proyectoRepository, proyectoEquipoRepository,
        proyectoResponsableEconomicoRepository);
    this.service = new ProyectoProyectoSgeServiceImpl(repository, proyectoRepository, this.proyectoHelper,
        sgiConfigProperties, configuracionService, proyectoAnualidadService, gastoProyectoRepository);
  }

  @Test
  void delete_ReturnProyectoProyectoSgeNotFoundException() {
    Long id = 1L;

    BDDMockito.given(this.repository.existsById(id)).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(() -> this.service.delete(id))
        .isInstanceOf(ProyectoProyectoSgeNotFoundException.class);
  }

  @Test
  void existsById_ReturnsTrue() {
    Long id = 1L;

    BDDMockito.given(this.repository.existsById(id)).willReturn(Boolean.TRUE);

    boolean exists = this.service.existsById(id);

    Assertions.assertThat(exists).isTrue();
  }

  @WithMockUser(authorities = { "CSP-EJEC-V_1" }, username = "user")
  @Test
  void findById_ReturnsProyectoProyectoSge() {
    // @formatter=off
    Long proyectoSgeId = 1L;
    ProyectoProyectoSge proyectoSge = ProyectoProyectoSge
        .builder()
        .id(proyectoSgeId)
        .build();
    BDDMockito
        .given(this.repository.findByIdAndProyectoUnidadGestionRefIn(anyLong(), ArgumentMatchers.<List<String>>any()))
        .willReturn(Optional.of(proyectoSge));

    // @formatter=on
    ProyectoProyectoSge result = this.service.findById(proyectoSgeId);

    Assertions.assertThat(result).isNotNull();
  }

  @WithMockUser(authorities = { "CSP-EJEC-V_1" }, username = "user")
  @Test
  void findById_ThrowsProyectoProyectoSgeNotFoundException() {
    Long proyectoSgeId = 1L;

    Assertions.assertThatThrownBy(() -> this.service.findById(proyectoSgeId))
        .isInstanceOf(ProyectoProyectoSgeNotFoundException.class);
  }

  @WithMockUser(authorities = { "CSP-EJEC-V_1" }, username = "user")
  @Test
  void findAllByProyecto_ThrowsUserNotAuthorizedToAccessProyectoException() {
    Long proyectoId = 1L;
    String query = StringUtils.EMPTY;
    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(Proyecto.builder().id(proyectoId).build()));

    Throwable thrown = Assertions.catchThrowable(() -> this.service.findAllByProyecto(proyectoId, query,
        PageRequest.of(0, 10)));
    Assertions.assertThat(thrown).isInstanceOf(UserNotAuthorizedToAccessProyectoException.class);
  }

  @WithMockUser(authorities = { "CSP-EJEC-V_1", "CSP-PRO-INV-VR" }, username = "user")
  @Test
  void findAll_WithUnidadesGestion_ReturnsProyectoProyectoSgePage() {

    String query = StringUtils.EMPTY;
    Pageable pageable = PageRequest.of(0, 10);

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoProyectoSge>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(new LinkedList<>()));

    Page<ProyectoProyectoSge> result = this.service.findAll(query, pageable);

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getContent()).isEmpty();
  }
}
package org.crue.hercules.sgi.csp.service.impl;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessProyectoException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoIVARepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoResponsableEconomicoRepository;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.crue.hercules.sgi.csp.service.ProyectoIVAService;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

class ProyectoIVAServiceImplTest extends BaseServiceTest {

  @Mock
  private ProyectoIVARepository repository;
  @Mock
  private ProyectoEquipoRepository proyectoEquipoRepository;
  @Mock
  private ProyectoResponsableEconomicoRepository proyectoResponsableEconomicoRepository;
  @Mock
  private ProyectoRepository proyectoRepository;

  private ProyectoHelper proyectoHelper;
  private ProyectoIVAService service;

  @BeforeEach
  void setup() {
    this.proyectoHelper = new ProyectoHelper(proyectoRepository, proyectoEquipoRepository,
        proyectoResponsableEconomicoRepository);
    this.service = new ProyectoIVAServiceImpl(repository, this.proyectoHelper);
  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  @Test
  void findAllByProyectoIdOrderByIdDesc_ThrowsUserNotAuthorizedToAccessProyectoException() {
    Long proyectoId = 1L;
    Pageable pageable = PageRequest.of(0, 10);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(Proyecto.builder().id(proyectoId).build()));

    Assertions.assertThatThrownBy(() -> this.service.findAllByProyectoIdOrderByIdDesc(proyectoId, pageable))
        .isInstanceOf(UserNotAuthorizedToAccessProyectoException.class);
  }

}
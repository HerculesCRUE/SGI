package org.crue.hercules.sgi.csp.service;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessProyectoException;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoPalabraClave;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoPalabraClaveRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoResponsableEconomicoRepository;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;

class ProyectoPalabraClaveServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoPalabraClaveRepository repository;
  @Mock
  private ProyectoEquipoRepository proyectoEquipoRepository;
  @Mock
  private ProyectoResponsableEconomicoRepository proyectoResponsableEconomicoRepository;

  private ProyectoHelper proyectoHelper;
  private ProyectoPalabraClaveService service;

  @BeforeEach
  public void setup() {
    this.proyectoHelper = new ProyectoHelper(proyectoEquipoRepository, proyectoResponsableEconomicoRepository);
    this.service = new ProyectoPalabraClaveService(this.repository, this.proyectoHelper);
  }

  @WithMockUser(username = "user", authorities = { "CSP-PRO-E", "CSP-PRO-INV-VR" })
  @Test
  void findByProyectoId_ThrowsUserNotAuthorizedToAccessProyectoException() {
    Long proyectoId = 1L;
    Pageable paging = PageRequest.of(0, 2);

    BDDMockito.given(this.proyectoEquipoRepository.count(ArgumentMatchers.<Specification<ProyectoEquipo>>any()))
        .willReturn(0L);

    Assertions.assertThatThrownBy(() -> this.service.findByProyectoId(proyectoId, "", paging))
        .isInstanceOf(UserNotAuthorizedToAccessProyectoException.class);
  }

  @Test
  void updatePalabrasClave_ThrowsIllegalArgumentException() {
    Long proyectoId = 1L;
    List<ProyectoPalabraClave> palabrasClave = Arrays.asList(ProyectoPalabraClave.builder()
        .proyectoId(2L)
        .build());

    Assertions.assertThatThrownBy(() -> this.service.updatePalabrasClave(proyectoId, palabrasClave))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
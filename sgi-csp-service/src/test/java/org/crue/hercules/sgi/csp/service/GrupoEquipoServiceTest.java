package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo.Dedicacion;
import org.crue.hercules.sgi.csp.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.csp.repository.GrupoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;
import org.crue.hercules.sgi.csp.repository.RolProyectoRepository;
import org.crue.hercules.sgi.csp.util.GrupoAuthorityHelper;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * GrupoEquipoServiceTest
 */
@Import({ GrupoEquipoService.class, ApplicationContextSupport.class })
class GrupoEquipoServiceTest extends BaseServiceTest {
  @MockBean
  private GrupoEquipoRepository grupoEquipoRepository;

  @MockBean
  private SgiConfigProperties sgiConfigProperties;

  @MockBean
  private GrupoRepository grupoRepository;

  @Mock
  private Validator validator;

  @MockBean
  private RolProyectoRepository rolProyectoRepository;

  @MockBean
  private GrupoAuthorityHelper authorityHelper;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @MockBean
  private ConfiguracionRepository configuracionRepository;

  @Autowired
  private GrupoEquipoService grupoEquipoService;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void findAll_ReturnsPage() {
    // given: Una lista con 37 GrupoEquipo
    String personaRef = "personaRef-";
    List<GrupoEquipo> gruposEquipo = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      gruposEquipo.add(generarMockGrupoEquipo(i, i, personaRef + String.format("%03d", i)));
    }

    BDDMockito.given(
        grupoEquipoRepository.findAll(ArgumentMatchers.<Specification<GrupoEquipo>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<GrupoEquipo>>() {
          @Override
          public Page<GrupoEquipo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > gruposEquipo.size() ? gruposEquipo.size() : toIndex;
            List<GrupoEquipo> content = gruposEquipo.subList(fromIndex, toIndex);
            Page<GrupoEquipo> page = new PageImpl<>(content, pageable, gruposEquipo.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<GrupoEquipo> page = grupoEquipoService.findAll(null, paging);

    // then: Devuelve la pagina 3 con los GrupoEquipo del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      GrupoEquipo grupoEquipo = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(grupoEquipo.getPersonaRef()).isEqualTo(personaRef + String.format("%03d", i));
    }
  }

  private GrupoEquipo generarMockGrupoEquipo(Long id, Long grupoId, String personaRef) {
    return generarMockGrupoEquipo(id, grupoId, Dedicacion.COMPLETA, new BigDecimal("100"), personaRef,
        Instant.now().plus(id, ChronoUnit.DAYS));
  }

  private GrupoEquipo generarMockGrupoEquipo(Long id, Long grupoId, Dedicacion dedicacion, BigDecimal participacion,
      String personaRef, Instant fechaInicio) {
    return GrupoEquipo.builder()
        .id(id)
        .grupoId(grupoId)
        .dedicacion(dedicacion)
        .participacion(participacion)
        .personaRef(personaRef)
        .fechaInicio(fechaInicio)
        .build();
  }
}

package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.ProyectoAnualidadGastosTotales;
import org.crue.hercules.sgi.csp.repository.AnualidadGastoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoAnualidadRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

/**
 * ProyectoAnualidadServiceTest
 */
@Import({ ProyectoAnualidadService.class, ApplicationContextSupport.class })
public class ProyectoAnualidadServiceTest extends BaseServiceTest {

  @MockBean
  private ProyectoAnualidadRepository proyectoAnualidadRepository;
  @MockBean
  private ProyectoRepository proyectoRepository;
  @MockBean
  private AnualidadGastoRepository anualidadGastoRepository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @Autowired
  private ProyectoAnualidadService proyectoAnualidadService;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void getTotalImportesAnualidadGasto_WithIdNull_ThrowsIllegalArgumentException() {
    // given: id null
    Long id = null;
    // when: Buscamos por id null
    // then: Lanza IllegalArgumentException porque el id no puede ser null
    Assertions.assertThatThrownBy(() -> proyectoAnualidadService.getTotalImportesProyectoAnualidad(id))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void getTotalImportesAnualidadGasto_ReturnsProyectoAnualidadGastosTotales() {
    // given: id existente
    Long id = 1234L;
    BigDecimal importeConcendidoAnualidadCostesDirectos = new BigDecimal("400");
    BigDecimal importeConcendidoAnualidadCostesIndirectos = new BigDecimal("200");
    BDDMockito.given(proyectoAnualidadRepository.getTotalImportesProyectoAnualidad(ArgumentMatchers.anyLong()))
        .willReturn(
            generarMockProyectoAnualidadGastosTotales(
                importeConcendidoAnualidadCostesDirectos,
                importeConcendidoAnualidadCostesIndirectos));
    // when: Buscamos por un id que existe
    ProyectoAnualidadGastosTotales proyectoAnualidadGastosTotales = proyectoAnualidadService
        .getTotalImportesProyectoAnualidad(id);

    // then: Devuelve el ProyectoAnualidadGastosTotales para dicho id
    Assertions.assertThat(proyectoAnualidadGastosTotales).isNotNull();
    Assertions.assertThat(proyectoAnualidadGastosTotales.getImporteConcendidoAnualidadCostesDirectos())
        .as("getImporteConcendidoAnualidadCostesDirectos()").isEqualTo(importeConcendidoAnualidadCostesDirectos);
    Assertions.assertThat(proyectoAnualidadGastosTotales.getImporteConcendidoAnualidadCostesIndirectos())
        .as("getImporteConcendidoAnualidadCostesIndirectos()").isEqualTo(importeConcendidoAnualidadCostesIndirectos);
  }

  private ProyectoAnualidadGastosTotales generarMockProyectoAnualidadGastosTotales(
      BigDecimal importeConcendidoAnualidadCostesDirectos, BigDecimal importeConcendidoAnualidadCostesIndirectos) {
    return ProyectoAnualidadGastosTotales.builder()
        .importeConcendidoAnualidadCostesDirectos(importeConcendidoAnualidadCostesDirectos)
        .importeConcendidoAnualidadCostesIndirectos(importeConcendidoAnualidadCostesIndirectos)
        .build();
  }
}

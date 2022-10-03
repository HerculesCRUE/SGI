package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.GastoRequerimientoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.GastoRequerimientoJustificacion;
import org.crue.hercules.sgi.csp.repository.GastoRequerimientoJustificacionRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
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
 * GastoRequerimientoJustificacionServiceTest
 */
@Import({ GastoRequerimientoJustificacionService.class, ApplicationContextSupport.class })
public class GastoRequerimientoJustificacionServiceTest extends BaseServiceTest {

  @MockBean
  private GastoRequerimientoJustificacionRepository gastoRequerimientoJustificacionRepository;

  @Autowired
  private GastoRequerimientoJustificacionService gastoRequerimientoJustificacionService;

  @Test
  void findAllByRequerimientoJustificacionId_WithPaging_ReturnsPage() {
    // given: One hundred GastoRequerimientoJustificacion
    Long requerimientoJustificacionId = 1234L;
    List<GastoRequerimientoJustificacion> gastoRequerimientoJustificacionList = new ArrayList<>();
    for (long i = 1; i <= 100; i++) {
      gastoRequerimientoJustificacionList
          .add(generarMockGastoRequerimientoJustificacion(i, requerimientoJustificacionId));
    }

    BDDMockito.given(
        gastoRequerimientoJustificacionRepository.findAll(
            ArgumentMatchers.<Specification<GastoRequerimientoJustificacion>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<GastoRequerimientoJustificacion>>() {
          @Override
          public Page<GastoRequerimientoJustificacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<GastoRequerimientoJustificacion> content = gastoRequerimientoJustificacionList
                .subList(fromIndex, toIndex);
            Page<GastoRequerimientoJustificacion> page = new PageImpl<>(content, pageable,
                gastoRequerimientoJustificacionList.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<GastoRequerimientoJustificacion> page = gastoRequerimientoJustificacionService
        .findAllByRequerimientoJustificacionId(
            requerimientoJustificacionId,
            null, paging);

    // then: A Page with ten GastoRequerimientoJustificacion are returned
    // containing incidencia='Incidencia-031' to
    // 'Incidencia-040'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      GastoRequerimientoJustificacion gastoRequerimientoJustificacion = page.getContent().get(i);
      Assertions.assertThat(gastoRequerimientoJustificacion.getIncidencia())
          .isEqualTo("Incidencia-" + String.format("%03d", j));
    }
  }

  @Test
  void create_idNotNull_ThrowsIllegalArgumentException() {
    // given: Un GastoRequerimientoJustificacion con un id no null
    GastoRequerimientoJustificacion gastoRequerimientoJustificacionToCreate = generarMockGastoRequerimientoJustificacion(
        1234L, 1L);

    // when: Creamos un GastoRequerimientoJustificacion con id no null
    // then: Lanza IllegalArgumentException porque el id debe ser null
    Assertions
        .assertThatThrownBy(
            () -> gastoRequerimientoJustificacionService.create(gastoRequerimientoJustificacionToCreate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void create_requerimientoJustificacionIdIsNull_ThrowsIllegalArgumentException() {
    // given: Un GastoRequerimientoJustificacion con un requerimientoJusitficacionId
    // null
    GastoRequerimientoJustificacion gastoRequerimientoJustificacionToCreate = generarMockGastoRequerimientoJustificacion(
        null, null);

    // when: Creamos un GastoRequerimientoJustificacion con
    // requerimientoJusitficacionId null
    // then: Lanza IllegalArgumentException porque el requerimientoJusitficacionId
    // no debe ser null
    Assertions
        .assertThatThrownBy(
            () -> gastoRequerimientoJustificacionService.create(gastoRequerimientoJustificacionToCreate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void create_ReturnsGastoRequerimientoJustificacion() {
    // given: Un GastoRequerimientoJustificacion con un id null
    Long id = 1L;
    GastoRequerimientoJustificacion gastoRequerimientoJustificacionToCreate = generarMockGastoRequerimientoJustificacion(
        null, 1L);
    GastoRequerimientoJustificacion gastoRequerimientoJustificacionCreated = generarMockGastoRequerimientoJustificacion(
        id,
        1L);

    // when: Creamos un GastoRequerimientoJustificacion con id null
    BDDMockito
        .given(gastoRequerimientoJustificacionRepository.save(ArgumentMatchers.<GastoRequerimientoJustificacion>any()))
        .willReturn(gastoRequerimientoJustificacionCreated);

    // then: GastoRequerimientoJustificacion creado correctamente
    GastoRequerimientoJustificacion gastoRequerimientoJustificacion = gastoRequerimientoJustificacionService
        .create(gastoRequerimientoJustificacionToCreate);
    Assertions.assertThat(gastoRequerimientoJustificacion).isNotNull();
    Assertions.assertThat(gastoRequerimientoJustificacion.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(gastoRequerimientoJustificacion.getAlegacion()).as("getAlegacion()").isEqualTo(
        gastoRequerimientoJustificacionToCreate.getAlegacion());
  }

  @Test
  void update_idIsNull_ThrowsIllegalArgumentException() {
    // given: Un GastoRequerimientoJustificacion con un id null
    GastoRequerimientoJustificacion gastoRequerimientoJustificacionToUpdate = generarMockGastoRequerimientoJustificacion(
        null, null);

    // when: Actualizamos un GastoRequerimientoJustificacion con id null
    // then: Lanza IllegalArgumentException porque el id no debe ser null
    Assertions
        .assertThatThrownBy(
            () -> gastoRequerimientoJustificacionService.update(gastoRequerimientoJustificacionToUpdate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_ThrowsGastoRequerimientoJustificacionNotFoundException() {
    // given: Un GastoRequerimientoJustificacion con un id no existente
    GastoRequerimientoJustificacion gastoRequerimientoJustificacionToUpdate = generarMockGastoRequerimientoJustificacion(
        999L, null);

    // when: Actualizamos un GastoRequerimientoJustificacion con id null
    BDDMockito.given(gastoRequerimientoJustificacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());
    // then: Lanza IllegalArgumentException porque el id no debe ser null
    Assertions
        .assertThatThrownBy(
            () -> gastoRequerimientoJustificacionService.update(gastoRequerimientoJustificacionToUpdate))
        .isInstanceOf(GastoRequerimientoJustificacionNotFoundException.class);
  }

  @Test
  void deleteById_WithIdNull_ThrowsIllegalArgumentException() {
    // given: id null
    Long id = null;
    // when: Eliminamos por id null
    // then: Lanza IllegalArgumentException porque el id no puede ser null
    Assertions.assertThatThrownBy(() -> gastoRequerimientoJustificacionService.deleteById(id))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void deleteByRequerimientoJustificacionId_WithIdNull_ThrowsIllegalArgumentException() {
    // given: id null
    Long requerimientoJustificacionId = null;
    // when: Eliminamos por requerimientoJustificacionId null
    // then: Lanza IllegalArgumentException porque el id no puede ser null
    Assertions
        .assertThatThrownBy(() -> gastoRequerimientoJustificacionService
            .deleteByRequerimientoJustificacionId(requerimientoJustificacionId))
        .isInstanceOf(IllegalArgumentException.class);
  }

  private GastoRequerimientoJustificacion generarMockGastoRequerimientoJustificacion(Long id,
      Long requerimientoJustificacionId) {
    String suffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockGastoRequerimientoJustificacion(id, Boolean.TRUE, "Alegacion-" + suffix,
        "gasto-ref-" + suffix, "11/1111",
        null, null, null,
        "Incidencia-" + suffix, requerimientoJustificacionId);
  }

  private GastoRequerimientoJustificacion generarMockGastoRequerimientoJustificacion(Long id, Boolean aceptado,
      String alegacion, String gastoRef, String identificadorJustificacion, BigDecimal importeAceptado,
      BigDecimal importeAlegado, BigDecimal importeRechazado,
      String incidencia, Long requerimientoJustificacionId) {
    return GastoRequerimientoJustificacion.builder()
        .id(id)
        .aceptado(aceptado)
        .alegacion(alegacion)
        .gastoRef(gastoRef)
        .identificadorJustificacion(identificadorJustificacion)
        .importeAceptado(importeAceptado)
        .importeAlegado(importeAlegado)
        .importeRechazado(importeRechazado)
        .incidencia(incidencia)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }
}

package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.IncidenciaDocumentacionRequerimiento;
import org.crue.hercules.sgi.csp.repository.IncidenciaDocumentacionRequerimientoRepository;
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
 * IncidenciaDocumentacionRequerimientoServiceTest
 */
@Import({ IncidenciaDocumentacionRequerimientoService.class, ApplicationContextSupport.class })
class IncidenciaDocumentacionRequerimientoServiceTest extends BaseServiceTest {

  @MockBean
  private IncidenciaDocumentacionRequerimientoRepository incidenciaDocumentacionRequerimientoRepository;

  @Autowired
  private IncidenciaDocumentacionRequerimientoService incidenciaDocumentacionRequerimientoService;

  @Test
  void findAllByRequerimientoJustificacionId_WithPaging_ReturnsPage() {
    // given: One hundred IncidenciaDocumentacionRequerimiento
    Long requerimientoJustificacionId = 1234L;
    List<IncidenciaDocumentacionRequerimiento> incidenciaDocumentacionRequerimientoList = new ArrayList<>();
    for (long i = 1; i <= 100; i++) {
      incidenciaDocumentacionRequerimientoList
          .add(generarMockIncidenciaDocumentacionRequerimiento(i, requerimientoJustificacionId));
    }

    BDDMockito.given(
        incidenciaDocumentacionRequerimientoRepository.findAll(
            ArgumentMatchers.<Specification<IncidenciaDocumentacionRequerimiento>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<IncidenciaDocumentacionRequerimiento>>() {
          @Override
          public Page<IncidenciaDocumentacionRequerimiento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<IncidenciaDocumentacionRequerimiento> content = incidenciaDocumentacionRequerimientoList
                .subList(fromIndex, toIndex);
            Page<IncidenciaDocumentacionRequerimiento> page = new PageImpl<>(content, pageable,
                incidenciaDocumentacionRequerimientoList.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<IncidenciaDocumentacionRequerimiento> page = incidenciaDocumentacionRequerimientoService
        .findAllByRequerimientoJustificacionId(
            requerimientoJustificacionId,
            null, paging);

    // then: A Page with ten IncidenciaDocumentacionRequerimiento are returned
    // containing nombreDocumento='IncidenciaDocumentacionRequerimiento-031' to
    // 'IncidenciaDocumentacionRequerimiento-040'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento = page.getContent().get(i);
      Assertions.assertThat(incidenciaDocumentacionRequerimiento.getNombreDocumento())
          .isEqualTo("IncidenciaDocumentacionRequerimiento-" + String.format("%03d", j));
    }
  }

  @Test
  void deleteById_WithIdNull_ThrowsIllegalArgumentException() {
    // given: id null
    Long id = null;
    // when: Eliminamos por id null
    // then: Lanza IllegalArgumentException porque el id no puede ser null
    Assertions.assertThatThrownBy(() -> incidenciaDocumentacionRequerimientoService.deleteById(id))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void deleteByRequerimientoJustificacionId_WithIdNull_ThrowsIllegalArgumentException() {
    // given: id null
    Long requerimientoJustificacionId = null;
    // when: Eliminamos por requerimientoJustificacionId null
    // then: Lanza IllegalArgumentException porque el id no puede ser null
    Assertions
        .assertThatThrownBy(() -> incidenciaDocumentacionRequerimientoService
            .deleteByRequerimientoJustificacionId(requerimientoJustificacionId))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void create_idNotNull_ThrowsIllegalArgumentException() {
    // given: Un IncidenciaDocumentacionRequerimiento con un id no null
    IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimientoToCreate = generarMockIncidenciaDocumentacionRequerimiento(
        1234L, 1L);

    // when: Creamos un IncidenciaDocumentacionRequerimiento con id no null
    // then: Lanza IllegalArgumentException porque el id debe ser null
    Assertions
        .assertThatThrownBy(
            () -> incidenciaDocumentacionRequerimientoService.create(incidenciaDocumentacionRequerimientoToCreate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void create_ReturnsIncidenciaDocumentacionRequerimiento() {
    // given: Un IncidenciaDocumentacionRequerimiento con un id null
    Long id = 1L;
    Long requerimientoJustificacionId = 1234L;
    IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimientoToCreate = generarMockIncidenciaDocumentacionRequerimiento(
        null, requerimientoJustificacionId);
    IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimientoCreated = generarMockIncidenciaDocumentacionRequerimiento(
        id, requerimientoJustificacionId);

    // when: Creamos un IncidenciaDocumentacionRequerimiento con id null
    BDDMockito
        .given(incidenciaDocumentacionRequerimientoRepository
            .save(ArgumentMatchers.<IncidenciaDocumentacionRequerimiento>any()))
        .willReturn(incidenciaDocumentacionRequerimientoCreated);

    // then: IncidenciaDocumentacionRequerimiento creado correctamente
    IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento = incidenciaDocumentacionRequerimientoService
        .create(incidenciaDocumentacionRequerimientoToCreate);
    Assertions.assertThat(incidenciaDocumentacionRequerimiento).isNotNull();
    Assertions.assertThat(incidenciaDocumentacionRequerimiento.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(incidenciaDocumentacionRequerimiento.getRequerimientoJustificacionId())
        .as("getRequerimientoJustificacionId()").isEqualTo(
            requerimientoJustificacionId);
  }

  @Test
  void update_idIsNull_ThrowsIllegalArgumentException() {
    // given: Un IncidenciaDocumentacionRequerimiento con un id null
    IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimientoToUpdate = generarMockIncidenciaDocumentacionRequerimiento(
        null, 1L);

    // when: Actualizamos un IncidenciaDocumentacionRequerimiento con id null
    // then: Lanza IllegalArgumentException porque el id no debe ser null
    Assertions
        .assertThatThrownBy(
            () -> incidenciaDocumentacionRequerimientoService
                .update(incidenciaDocumentacionRequerimientoToUpdate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_ReturnsRequerimientoJustificacion() {
    // given: Un IncidenciaDocumentacionRequerimiento con datos correctos
    Long id = 123L;
    Long requerimientoJustificacionId = 1234L;
    String incidenciaToUpdate = "Incidencia-123";
    IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimientoToUpdate = generarMockIncidenciaDocumentacionRequerimiento(
        id, requerimientoJustificacionId);
    IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimientoOnDB = generarMockIncidenciaDocumentacionRequerimiento(
        id, requerimientoJustificacionId);
    incidenciaDocumentacionRequerimientoToUpdate.setIncidencia(incidenciaToUpdate);

    BDDMockito.given(incidenciaDocumentacionRequerimientoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(incidenciaDocumentacionRequerimientoOnDB));
    BDDMockito
        .given(incidenciaDocumentacionRequerimientoRepository
            .save(ArgumentMatchers.<IncidenciaDocumentacionRequerimiento>any()))
        .willReturn(incidenciaDocumentacionRequerimientoToUpdate);

    // when: Actualizamos un IncidenciaDocumentacionRequerimiento con datos
    // correctos
    // then: Se actualiza correctamente
    IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento = incidenciaDocumentacionRequerimientoService
        .update(incidenciaDocumentacionRequerimientoToUpdate);
    Assertions.assertThat(incidenciaDocumentacionRequerimiento).isNotNull();
    Assertions.assertThat(incidenciaDocumentacionRequerimiento.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(incidenciaDocumentacionRequerimiento.getRequerimientoJustificacionId())
        .as("getRequerimientoJustificacionId()").isEqualTo(requerimientoJustificacionId);
    Assertions.assertThat(incidenciaDocumentacionRequerimiento.getIncidencia()).as("getIncidencia()")
        .isEqualTo(incidenciaToUpdate);
  }

  @Test
  void updateAlegacion_idIsNull_ThrowsIllegalArgumentException() {
    // given: Un IncidenciaDocumentacionRequerimiento con un id null
    IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimientoToUpdate = generarMockIncidenciaDocumentacionRequerimiento(
        null, 1L);

    // when: Actualizamos un IncidenciaDocumentacionRequerimiento con id null
    // then: Lanza IllegalArgumentException porque el id no debe ser null
    Assertions
        .assertThatThrownBy(
            () -> incidenciaDocumentacionRequerimientoService
                .updateAlegacion(incidenciaDocumentacionRequerimientoToUpdate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void updateAlegacion_ReturnsRequerimientoJustificacion() {
    // given: Un IncidenciaDocumentacionRequerimiento con datos correctos
    Long id = 123L;
    Long requerimientoJustificacionId = 1234L;
    String alegacionToUpdate = "Alegacion-123";
    IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimientoToUpdate = generarMockIncidenciaDocumentacionRequerimiento(
        id, requerimientoJustificacionId);
    IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimientoOnDB = generarMockIncidenciaDocumentacionRequerimiento(
        id, requerimientoJustificacionId);
    incidenciaDocumentacionRequerimientoToUpdate.setAlegacion(alegacionToUpdate);

    BDDMockito.given(incidenciaDocumentacionRequerimientoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(incidenciaDocumentacionRequerimientoOnDB));
    BDDMockito
        .given(incidenciaDocumentacionRequerimientoRepository
            .save(ArgumentMatchers.<IncidenciaDocumentacionRequerimiento>any()))
        .willReturn(incidenciaDocumentacionRequerimientoToUpdate);

    // when: Actualizamos un IncidenciaDocumentacionRequerimiento con datos
    // correctos
    // then: Se actualiza correctamente
    IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento = incidenciaDocumentacionRequerimientoService
        .update(incidenciaDocumentacionRequerimientoToUpdate);
    Assertions.assertThat(incidenciaDocumentacionRequerimiento).isNotNull();
    Assertions.assertThat(incidenciaDocumentacionRequerimiento.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(incidenciaDocumentacionRequerimiento.getRequerimientoJustificacionId())
        .as("getRequerimientoJustificacionId()").isEqualTo(requerimientoJustificacionId);
    Assertions.assertThat(incidenciaDocumentacionRequerimiento.getAlegacion()).as("getAlegacion()")
        .isEqualTo(alegacionToUpdate);
  }

  private IncidenciaDocumentacionRequerimiento generarMockIncidenciaDocumentacionRequerimiento(Long id,
      Long requerimientoJustificacionId) {
    String suffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockIncidenciaDocumentacionRequerimiento(id, "Alegacion-" + suffix, "Incidencia-" + suffix,
        "IncidenciaDocumentacionRequerimiento-" + suffix, requerimientoJustificacionId);
  }

  private IncidenciaDocumentacionRequerimiento generarMockIncidenciaDocumentacionRequerimiento(Long id,
      String alegacion,
      String incidencia, String nombreDocumento, Long requerimientoJustificacionId) {
    return IncidenciaDocumentacionRequerimiento.builder()
        .id(id)
        .alegacion(alegacion)
        .incidencia(incidencia)
        .nombreDocumento(nombreDocumento)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }
}

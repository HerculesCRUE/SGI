package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.RequerimientoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.AlegacionRequerimiento;
import org.crue.hercules.sgi.csp.repository.AlegacionRequerimientoRepository;
import org.crue.hercules.sgi.csp.repository.RequerimientoJustificacionRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

/**
 * AlegacionRequerimientoServiceTest
 */
@Import({ AlegacionRequerimientoService.class, ApplicationContextSupport.class })
public class AlegacionRequerimientoServiceTest extends BaseServiceTest {

  @MockBean
  private AlegacionRequerimientoRepository alegacionRequerimientoRepository;

  @MockBean
  private RequerimientoJustificacionRepository requerimientoJustificacionRepository;

  @Autowired
  private AlegacionRequerimientoService alegacionRequerimientoService;

  @Test
  void findByRequerimientoJustificacionId_WithIdNull_ThrowsIllegalArgumentException() {
    // given: id null
    Long requerimientoJustificacionId = null;
    // when: Buscamos por requerimientoJustificacionId null
    // then: Lanza IllegalArgumentException porque el id no puede ser null
    Assertions
        .assertThatThrownBy(() -> alegacionRequerimientoService
            .findByRequerimientoJustificacionId(requerimientoJustificacionId))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void findByRequerimientoJustificacionId_WithIdNotFound_ThrowsRequerimientoJustificacionNotFoundException() {
    // given: id null
    Long requerimientoJustificacionId = 9999L;
    // when: Buscamos por requerimientoJustificacionId no existente
    BDDMockito.given(requerimientoJustificacionRepository.existsById(ArgumentMatchers.anyLong()))
        .willReturn(Boolean.FALSE);
    // then: Lanza RequerimientoJustificacionNotFoundException porque el id no
    // existe
    Assertions
        .assertThatThrownBy(() -> alegacionRequerimientoService
            .findByRequerimientoJustificacionId(requerimientoJustificacionId))
        .isInstanceOf(RequerimientoJustificacionNotFoundException.class);
  }

  @Test
  void findByRequerimientoJustificacionId_ReturnsAlegacionRequerimiento() {
    // given: id null
    Long requerimientoJustificacionId = 1L;
    // when: Buscamos por requerimientoJustificacionId existente con una alegacion
    // relacionada
    BDDMockito.given(requerimientoJustificacionRepository.existsById(ArgumentMatchers.anyLong()))
        .willReturn(Boolean.TRUE);
    BDDMockito.given(alegacionRequerimientoRepository.findByRequerimientoJustificacionId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockAlegacionRequerimiento(1L, requerimientoJustificacionId)));
    // then: Devuelve la alegacion relacionada con el requerimientoJustificacionId
    AlegacionRequerimiento alegacionRequerimiento = alegacionRequerimientoService
        .findByRequerimientoJustificacionId(requerimientoJustificacionId);
    Assertions.assertThat(alegacionRequerimiento).isNotNull();
    Assertions.assertThat(alegacionRequerimiento.getRequerimientoJustificacionId())
        .as("getRequerimientoJustificacionId()").isEqualTo(requerimientoJustificacionId);
  }

  @Test
  void deleteByRequerimientoJustificacionId_WithIdNull_ThrowsIllegalArgumentException() {
    // given: id null
    Long requerimientoJustificacionId = null;
    // when: Eliminamos por requerimientoJustificacionId null
    // then: Lanza IllegalArgumentException porque el id no puede ser null
    Assertions
        .assertThatThrownBy(() -> alegacionRequerimientoService
            .deleteByRequerimientoJustificacionId(requerimientoJustificacionId))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void create_idNotNull_ThrowsIllegalArgumentException() {
    // given: Un AlegacionRequerimiento con un id no null
    AlegacionRequerimiento alegacionRequerimientoToCreate = generarMockAlegacionRequerimiento(
        1234L, 1L);

    // when: Creamos un AlegacionRequerimiento con id no null
    // then: Lanza IllegalArgumentException porque el id debe ser null
    Assertions
        .assertThatThrownBy(
            () -> alegacionRequerimientoService.create(alegacionRequerimientoToCreate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void create_ReturnsAlegacionRequerimiento() {
    // given: Un AlegacionRequerimiento con un id null
    Long id = 1L;
    Long requerimientoJustificacionId = 1234L;
    AlegacionRequerimiento alegacionRequerimientoToCreate = generarMockAlegacionRequerimiento(
        null, requerimientoJustificacionId);
    AlegacionRequerimiento alegacionRequerimientotoCreated = generarMockAlegacionRequerimiento(
        id, requerimientoJustificacionId);

    // when: Creamos un AlegacionRequerimiento con id null
    BDDMockito
        .given(alegacionRequerimientoRepository
            .save(ArgumentMatchers.<AlegacionRequerimiento>any()))
        .willReturn(alegacionRequerimientotoCreated);

    // then: AlegacionRequerimiento creado correctamente
    AlegacionRequerimiento alegacionRequerimiento = alegacionRequerimientoService
        .create(alegacionRequerimientoToCreate);
    Assertions.assertThat(alegacionRequerimiento).isNotNull();
    Assertions.assertThat(alegacionRequerimiento.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(alegacionRequerimiento.getRequerimientoJustificacionId())
        .as("getRequerimientoJustificacionId()").isEqualTo(
            requerimientoJustificacionId);
  }

  @Test
  void update_idIsNull_ThrowsIllegalArgumentException() {
    // given: Un AlegacionRequerimiento con un id null
    AlegacionRequerimiento AlegacionRequerimientoToUpdate = generarMockAlegacionRequerimiento(
        null, 1L);

    // when: Actualizamos un AlegacionRequerimiento con id null
    // then: Lanza IllegalArgumentException porque el id no debe ser null
    Assertions
        .assertThatThrownBy(
            () -> alegacionRequerimientoService
                .update(AlegacionRequerimientoToUpdate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_ReturnsRequerimientoJustificacion() {
    // given: Un AlegacionRequerimiento con datos correctos
    Long id = 123L;
    Long requerimientoJustificacionId = 1234L;
    String justificanteReintegroToUpdate = "Incidencia-123";
    AlegacionRequerimiento alegacionRequerimientoToUpdate = generarMockAlegacionRequerimiento(
        id, requerimientoJustificacionId);
    AlegacionRequerimiento alegacionRequerimientoOnDB = generarMockAlegacionRequerimiento(
        id, requerimientoJustificacionId);
    alegacionRequerimientoToUpdate.setJustificanteReintegro(justificanteReintegroToUpdate);

    BDDMockito.given(alegacionRequerimientoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(alegacionRequerimientoOnDB));
    BDDMockito
        .given(alegacionRequerimientoRepository
            .save(ArgumentMatchers.<AlegacionRequerimiento>any()))
        .willReturn(alegacionRequerimientoToUpdate);

    // when: Actualizamos un AlegacionRequerimiento con datos
    // correctos
    // then: Se actualiza correctamente
    AlegacionRequerimiento alegacionRequerimiento = alegacionRequerimientoService
        .update(alegacionRequerimientoToUpdate);
    Assertions.assertThat(alegacionRequerimiento).isNotNull();
    Assertions.assertThat(alegacionRequerimiento.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(alegacionRequerimiento.getRequerimientoJustificacionId())
        .as("getRequerimientoJustificacionId()").isEqualTo(requerimientoJustificacionId);
    Assertions.assertThat(alegacionRequerimiento.getJustificanteReintegro()).as("getJustificanteReintegro()")
        .isEqualTo(justificanteReintegroToUpdate);
  }

  private AlegacionRequerimiento generarMockAlegacionRequerimiento(Long id,
      Long requerimientoJustificacionId) {
    String suffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockAlegacionRequerimiento(id, "Justificante-" + suffix, "Observacion-" + suffix,
        requerimientoJustificacionId);
  }

  private AlegacionRequerimiento generarMockAlegacionRequerimiento(Long id,
      String justificanteReintegro, String observaciones, Long requerimientoJustificacionId) {
    return AlegacionRequerimiento.builder()
        .id(id)
        .justificanteReintegro(justificanteReintegro)
        .observaciones(observaciones)
        .requerimientoJustificacionId(requerimientoJustificacionId)
        .build();
  }
}

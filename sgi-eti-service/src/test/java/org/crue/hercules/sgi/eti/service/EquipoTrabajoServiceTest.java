package org.crue.hercules.sgi.eti.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.EquipoTrabajoWithIsEliminable;
import org.crue.hercules.sgi.eti.exceptions.EquipoTrabajoNotFoundException;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.repository.EquipoTrabajoRepository;
import org.crue.hercules.sgi.eti.service.impl.EquipoTrabajoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * EquipoTrabajoServiceTest
 */
public class EquipoTrabajoServiceTest extends BaseServiceTest {

  @Mock
  private EquipoTrabajoRepository equipoTrabajoRepository;

  private EquipoTrabajoService equipoTrabajoService;

  @BeforeEach
  public void setUp() throws Exception {
    equipoTrabajoService = new EquipoTrabajoServiceImpl(equipoTrabajoRepository);
  }

  @Test
  public void find_WithId_ReturnsEquipoTrabajo() {
    BDDMockito.given(equipoTrabajoRepository.findById(1L)).willReturn(
        Optional.of(generarMockEquipoTrabajo(1L, generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"))));

    EquipoTrabajo equipoTrabajo = equipoTrabajoService.findById(1L);

    Assertions.assertThat(equipoTrabajo.getId()).isEqualTo(1L);
    Assertions.assertThat(equipoTrabajo.getPersonaRef()).isEqualTo("user-001");
    Assertions.assertThat(equipoTrabajo.getPeticionEvaluacion().getTitulo()).isEqualTo("PeticionEvaluacion1");

  }

  @Test
  public void find_NotFound_ThrowsEquipoTrabajoNotFoundException() throws Exception {
    BDDMockito.given(equipoTrabajoRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> equipoTrabajoService.findById(1L))
        .isInstanceOf(EquipoTrabajoNotFoundException.class);
  }

  @Test
  public void create_ReturnsEquipoTrabajo() {
    // given: Un nuevo EquipoTrabajo
    EquipoTrabajo equipoTrabajoNew = generarMockEquipoTrabajo(null,
        generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"));

    EquipoTrabajo equipoTrabajo = generarMockEquipoTrabajo(1L,
        generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"));

    BDDMockito.given(equipoTrabajoRepository.save(equipoTrabajoNew)).willReturn(equipoTrabajo);

    // when: Creamos el EquipoTrabajo
    EquipoTrabajo equipoTrabajoCreado = equipoTrabajoService.create(equipoTrabajoNew);

    // then: El EquipoTrabajo se crea correctamente
    Assertions.assertThat(equipoTrabajoCreado).isNotNull();
    Assertions.assertThat(equipoTrabajoCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(equipoTrabajo.getPersonaRef()).isEqualTo("user-001");
    Assertions.assertThat(equipoTrabajo.getPeticionEvaluacion().getTitulo()).isEqualTo("PeticionEvaluacion1");
  }

  @Test
  public void create_EquipoTrabajoWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo EquipoTrabajo que ya tiene id
    EquipoTrabajo equipoTrabajoNew = generarMockEquipoTrabajo(1L,
        generarMockPeticionEvaluacion(1L, "PeticionEvaluacion1"));
    // when: Creamos el EquipoTrabajo
    // then: Lanza una excepcion porque el EquipoTrabajo ya tiene id
    Assertions.assertThatThrownBy(() -> equipoTrabajoService.create(equipoTrabajoNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> equipoTrabajoService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsEquipoTrabajoNotFoundException() {
    // given: Id no existe
    BDDMockito.given(equipoTrabajoRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> equipoTrabajoService.delete(1L))
        // then: Lanza EquipoTrabajoNotFoundException
        .isInstanceOf(EquipoTrabajoNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesEquipoTrabajo() {
    // given: Id existente
    BDDMockito.given(equipoTrabajoRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(equipoTrabajoRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> equipoTrabajoService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullEquipoTrabajoList() {
    // given: One hundred EquipoTrabajo
    List<EquipoTrabajo> equipoTrabajos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      equipoTrabajos.add(generarMockEquipoTrabajo(Long.valueOf(i),
          generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i))));
    }

    BDDMockito.given(equipoTrabajoRepository.findAll(ArgumentMatchers.<Specification<EquipoTrabajo>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(equipoTrabajos));

    // when: find unlimited
    Page<EquipoTrabajo> page = equipoTrabajoService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred EquipoTrabajos
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAllByPeticionEvaluacionId_ReturnsList() {
    // given: 10 EquipoTrabajos por PeticionEvaluacion
    List<EquipoTrabajoWithIsEliminable> equipoTrabajos = new ArrayList<>();
    for (int i = 1, j = 1; i <= 10; i++, j++) {
      equipoTrabajos.add(generarMockEquipoTrabajoWithIsEliminable(Long.valueOf(i * 10 + j - 10),
          generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i))));
    }

    BDDMockito.given(equipoTrabajoRepository.findAllByPeticionEvaluacionId(ArgumentMatchers.<Long>any()))
        .willReturn(equipoTrabajos);

    List<EquipoTrabajoWithIsEliminable> result = equipoTrabajoService.findAllByPeticionEvaluacionId(1L);

    // then: A List with ten EquipoTrabajos are returned containing
    Assertions.assertThat(result.size()).isEqualTo(10);
    for (int i = 0, j = 1; i < 10 && j <= 10; i++, j++) {
      EquipoTrabajoWithIsEliminable equipoTrabajo = result.get(i);
      Assertions.assertThat(equipoTrabajo.getPeticionEvaluacion().getTitulo())
          .as("equipoTrabajo[" + j + "].peticionEvaluacion.titulo")
          .isEqualTo("PeticionEvaluacion" + String.format("%03d", j));
    }
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred EquipoTrabajos
    List<EquipoTrabajo> equipoTrabajos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      equipoTrabajos.add(generarMockEquipoTrabajo(Long.valueOf(i),
          generarMockPeticionEvaluacion(Long.valueOf(i), "PeticionEvaluacion" + String.format("%03d", i))));
    }

    BDDMockito.given(equipoTrabajoRepository.findAll(ArgumentMatchers.<Specification<EquipoTrabajo>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<EquipoTrabajo>>() {
          @Override
          public Page<EquipoTrabajo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<EquipoTrabajo> content = equipoTrabajos.subList(fromIndex, toIndex);
            Page<EquipoTrabajo> page = new PageImpl<>(content, pageable, equipoTrabajos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<EquipoTrabajo> page = equipoTrabajoService.findAll(null, paging);

    // then: A Page with ten EquipoTrabajos are returned containing
    // descripcion='EquipoTrabajo031' to 'EquipoTrabajo040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      EquipoTrabajo equipoTrabajo = page.getContent().get(i);
      Assertions.assertThat(equipoTrabajo.getPeticionEvaluacion().getTitulo())
          .isEqualTo("PeticionEvaluacion" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param id     id del PeticionEvaluacion
   * @param titulo el título de PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */

  public PeticionEvaluacion generarMockPeticionEvaluacion(Long id, String titulo) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo" + id);
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico" + id);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos("Objetivos" + id);
    peticionEvaluacion.setResumen("Resumen" + id);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria" + id);
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(titulo);
    peticionEvaluacion.setPersonaRef("user-00" + id);
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
  }

  /**
   * Función que devuelve un objeto EquipoTrabajo
   * 
   * @param id                 id del EquipoTrabajo
   * @param peticionEvaluacion la PeticionEvaluacion del EquipoTrabajo
   * @return el objeto EquipoTrabajo
   */
  public EquipoTrabajo generarMockEquipoTrabajo(Long id, PeticionEvaluacion peticionEvaluacion) {

    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(id);
    equipoTrabajo.setPeticionEvaluacion(peticionEvaluacion);
    equipoTrabajo.setPersonaRef("user-00" + id);

    return equipoTrabajo;
  }

  /**
   * Función que devuelve un objeto EquipoTrabajoWithIsEliminable
   * 
   * @param id                 id del EquipoTrabajo
   * @param peticionEvaluacion la PeticionEvaluacion del EquipoTrabajo
   * @return el objeto EquipoTrabajo
   */
  public EquipoTrabajoWithIsEliminable generarMockEquipoTrabajoWithIsEliminable(Long id,
      PeticionEvaluacion peticionEvaluacion) {

    EquipoTrabajoWithIsEliminable equipoTrabajo = new EquipoTrabajoWithIsEliminable();
    equipoTrabajo.setId(id);
    equipoTrabajo.setPeticionEvaluacion(peticionEvaluacion);
    equipoTrabajo.setPersonaRef("user-00" + id);
    equipoTrabajo.setEliminable(true);

    return equipoTrabajo;
  }

}
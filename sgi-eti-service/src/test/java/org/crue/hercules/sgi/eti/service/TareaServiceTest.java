package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.TareaWithIsEliminable;
import org.crue.hercules.sgi.eti.exceptions.TareaNotFoundException;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.crue.hercules.sgi.eti.repository.TareaRepository;
import org.crue.hercules.sgi.eti.service.impl.TareaServiceImpl;
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
 * TareaServiceTest
 */
public class TareaServiceTest extends BaseServiceTest {

  @Mock
  private TareaRepository tareaRepository;

  private TareaService tareaService;

  @BeforeEach
  public void setUp() throws Exception {
    tareaService = new TareaServiceImpl(tareaRepository);
  }

  @Test
  public void find_WithId_ReturnsTarea() {
    BDDMockito.given(tareaRepository.findById(1L)).willReturn(Optional.of(generarMockTarea(1L, "Tarea1")));

    Tarea tarea = tareaService.findById(1L);

    Assertions.assertThat(tarea.getId()).isEqualTo(1L);

    Assertions.assertThat(tarea.getTarea()).isEqualTo("Tarea1");

  }

  @Test
  public void find_NotFound_ThrowsTareaNotFoundException() throws Exception {
    BDDMockito.given(tareaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> tareaService.findById(1L)).isInstanceOf(TareaNotFoundException.class);
  }

  @Test
  public void create_ReturnsTarea() {
    // given: Una nueva Tarea
    Tarea tareaNew = generarMockTarea(null, "TareaNew");

    Tarea tarea = generarMockTarea(1L, "TareaNew");

    BDDMockito.given(tareaRepository.save(tareaNew)).willReturn(tarea);

    // when: Creamos la tarea
    Tarea tareaCreada = tareaService.create(tareaNew);

    // then: la tarea se crea correctamente
    Assertions.assertThat(tareaCreada).isNotNull();
    Assertions.assertThat(tareaCreada.getId()).isEqualTo(1L);
    Assertions.assertThat(tareaCreada.getTarea()).isEqualTo("TareaNew");
  }

  @Test
  public void create_TareaWithId_ThrowsIllegalArgumentException() {
    // given: Una nueva tarea que ya tiene id
    Tarea tareaNew = generarMockTarea(1L, "TareaNew");
    // when: Creamos la tarea
    // then: Lanza una excepcion porque la tarea ya tiene id
    Assertions.assertThatThrownBy(() -> tareaService.create(tareaNew)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsTarea() {
    // given: Una nueva tarea con la descripcion actualizada
    Tarea tareaDescripcionActualizada = generarMockTarea(1L, "Tarea1 actualizada");

    Tarea tarea = generarMockTarea(1L, "Tarea1");

    BDDMockito.given(tareaRepository.findById(1L)).willReturn(Optional.of(tarea));
    BDDMockito.given(tareaRepository.save(tareaDescripcionActualizada)).willReturn(tareaDescripcionActualizada);

    // when: Actualizamos la tarea
    Tarea tareaActualizada = tareaService.update(tareaDescripcionActualizada);

    // then: La tarea se actualiza correctamente.
    Assertions.assertThat(tareaActualizada.getId()).isEqualTo(1L);
    Assertions.assertThat(tareaActualizada.getTarea()).isEqualTo("Tarea1 actualizada");
  }

  @Test
  public void update_ThrowsTareanNotFoundException() {
    // given: Una nueva tarea a actualizar
    Tarea tareaDescripcionActualizada = generarMockTarea(1L, "Tarea1 actualizada");

    // then: Lanza una excepcion porque la tarea no existe
    Assertions.assertThatThrownBy(() -> tareaService.update(tareaDescripcionActualizada))
        .isInstanceOf(TareaNotFoundException.class);
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: Una tarea que venga sin id
    Tarea tareaDescripcionActualizada = generarMockTarea(null, "Tarea1 actualizada");

    Assertions.assertThatThrownBy(
        // when: update tarea
        () -> tareaService.update(tareaDescripcionActualizada))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> tareaService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsTareaNotFoundException() {
    // given: Id no existe
    BDDMockito.given(tareaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> tareaService.delete(1L))
        // then: Lanza TareaNotFoundException
        .isInstanceOf(TareaNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesTarea() {
    // given: Id existente
    BDDMockito.given(tareaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.given(tareaRepository.existsByIdAndMemoriaEstadoActualIdIn(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Long>anyList())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(tareaRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> tareaService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void deleteByEquipoTrabajo_WithExistingId_DeletesTarea() {
    // given: Id existente
    BDDMockito.doNothing().when(tareaRepository).deleteByEquipoTrabajoId(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> tareaService.deleteByEquipoTrabajo(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullTareaList() {
    // given: One hundred tareas
    List<Tarea> tareas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tareas.add(generarMockTarea(Long.valueOf(i), "Tarea" + String.format("%03d", i)));
    }

    BDDMockito
        .given(tareaRepository.findAll(ArgumentMatchers.<Specification<Tarea>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tareas));

    // when: find unlimited
    Page<Tarea> page = tareaService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred tareas
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred tareas
    List<Tarea> tareas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tareas.add(generarMockTarea(Long.valueOf(i), "Tarea" + String.format("%03d", i)));
    }

    BDDMockito
        .given(tareaRepository.findAll(ArgumentMatchers.<Specification<Tarea>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Tarea>>() {
          @Override
          public Page<Tarea> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Tarea> content = tareas.subList(fromIndex, toIndex);
            Page<Tarea> page = new PageImpl<>(content, pageable, tareas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Tarea> page = tareaService.findAll(null, paging);

    // then: A Page with ten tareas are returned containing descripcion='Tarea031'
    // to 'Tarea040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Tarea tarea = page.getContent().get(i);
      Assertions.assertThat(tarea.getTarea()).isEqualTo("Tarea" + String.format("%03d", j));
    }
  }

  @Test
  public void findAllByPeticionEvaluacionId_WithPaging_ReturnsPage() {
    // given: One hundred tareas
    List<TareaWithIsEliminable> tareas = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      tareas.add(generarMockTareaWithIsEliminable(Long.valueOf(i), "Tarea" + String.format("%03d", i)));
    }

    BDDMockito.given(tareaRepository.findAllByPeticionEvaluacionId(ArgumentMatchers.<Long>any())).willReturn(tareas);

    List<TareaWithIsEliminable> result = tareaService.findAllByPeticionEvaluacionId(1L);

    // then: A list with tareas
    Assertions.assertThat(result.size()).isEqualTo(10);
    for (int i = 0, j = 1; i < 10; i++, j++) {
      TareaWithIsEliminable tarea = result.get(i);
      Assertions.assertThat(tarea.getTarea()).isEqualTo("Tarea" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto Tarea
   * 
   * @param id          id de la tarea
   * @param descripcion descripcion de la tarea
   * @return el objeto Tarea
   */
  public Tarea generarMockTarea(Long id, String descripcion) {
    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(100L);

    Memoria memoria = new Memoria();
    memoria.setId(200L);

    FormacionEspecifica formacionEspecifica = new FormacionEspecifica();
    formacionEspecifica.setId(300L);

    TipoTarea tipoTarea = new TipoTarea();
    tipoTarea.setId(1L);
    tipoTarea.setNombre("Eutanasia");
    tipoTarea.setActivo(Boolean.TRUE);

    Tarea tarea = new Tarea();
    tarea.setId(id);
    tarea.setEquipoTrabajo(equipoTrabajo);
    tarea.setMemoria(memoria);
    tarea.setTarea(descripcion);
    tarea.setFormacion("Formacion" + id);
    tarea.setFormacionEspecifica(formacionEspecifica);
    tarea.setOrganismo("Organismo" + id);
    tarea.setAnio(2020);
    tarea.setTipoTarea(tipoTarea);

    return tarea;
  }

  /**
   * Función que devuelve un objeto TareaWithIsEliminable
   * 
   * @param id          id de la tarea
   * @param descripcion descripcion de la tarea
   * @return el objeto TareaWithIsEliminable
   */
  public TareaWithIsEliminable generarMockTareaWithIsEliminable(Long id, String descripcion) {
    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setId(100L);

    Memoria memoria = new Memoria();
    memoria.setId(200L);

    FormacionEspecifica formacionEspecifica = new FormacionEspecifica();
    formacionEspecifica.setId(300L);

    TipoTarea tipoTarea = new TipoTarea();
    tipoTarea.setId(1L);
    tipoTarea.setNombre("Eutanasia");
    tipoTarea.setActivo(Boolean.TRUE);

    TareaWithIsEliminable tarea = new TareaWithIsEliminable();
    tarea.setId(id);
    tarea.setEquipoTrabajo(equipoTrabajo);
    tarea.setMemoria(memoria);
    tarea.setTarea(descripcion);
    tarea.setFormacion("Formacion" + id);
    tarea.setFormacionEspecifica(formacionEspecifica);
    tarea.setOrganismo("Organismo" + id);
    tarea.setEliminable(true);

    return tarea;
  }

}

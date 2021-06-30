package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.repository.ComiteRepository;
import org.crue.hercules.sgi.eti.service.impl.ComiteServiceImpl;
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
 * ComiteServiceTest
 */
public class ComiteServiceTest extends BaseServiceTest {
  @Mock
  private ComiteRepository comiteRepository;

  @Mock
  private ComiteService comiteService;

  @BeforeEach
  public void setUp() throws Exception {
    comiteService = new ComiteServiceImpl(comiteRepository);
  }

  @Test
  public void findAll_ReturnsComiteList() {

    // given: dos comites
    Formulario formulario1 = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", formulario1, Boolean.TRUE);
    Formulario formulario2 = new Formulario(1L, "M20", "Descripcion");
    Comite comite2 = new Comite(2L, "Comite2", formulario2, Boolean.TRUE);

    List<Comite> comiteResponseList = new ArrayList<Comite>();
    comiteResponseList.add(comite);
    comiteResponseList.add(comite2);

    BDDMockito
        .given(
            comiteRepository.findAll(ArgumentMatchers.<Specification<Comite>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(comiteResponseList));

    // when: busqueda de todos los elementos
    Page<Comite> page = comiteService.findAll(null, Pageable.unpaged());

    // then: recuperamos los comités
    Assertions.assertThat(page.getContent().size()).isEqualTo(2);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(2);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(2);
  }

  @Test
  public void findAll_ReturnsEmptyList() throws Exception {

    List<Comite> comiteResponseList = new ArrayList<Comite>();

    // given: Una lista vacía
    BDDMockito.given(comiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(comiteResponseList));

    // when: Se realiza la búsqueda de comites
    Page<Comite> comiteList = comiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any());

    // then: Recuperamos la lista vacía
    Assertions.assertThat(comiteList);

  }

  @Test
  public void findById_WithId_ReturnsComite() throws ComiteNotFoundException {
    // given: El id de un comité

    BDDMockito.given(comiteRepository.findById(1L))
        .willReturn(Optional.of(new Comite(1L, "Comite1", new Formulario(1L, "M10", "Descripcion"), Boolean.TRUE)));

    // when: Buscamos por id
    Comite comite = comiteService.findById(1L);

    // then: Recuperamos el comité
    Assertions.assertThat(comite.getId()).isEqualTo(1L);
    Assertions.assertThat(comite.getComite()).isEqualTo("Comite1");

  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {

    // given: No existe el id
    BDDMockito.given(comiteRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: Búsqueda por un id que no existe
        () -> comiteService.findById(1L))
        // then: Excepción ComiteNotFoundException
        .isInstanceOf(ComiteNotFoundException.class);
  }

  @Test
  public void create_ReturnsComite() {

    // given: Un comité
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(null, "Comite1", formulario, Boolean.TRUE);

    BDDMockito.given(comiteRepository.save(comite)).will((InvocationOnMock invocation) -> {
      Comite comiteCreada = invocation.getArgument(0);
      comiteCreada.setId(1L);
      return comiteCreada;
    });
    // when: Creamos el comité
    Comite comiteCreada = comiteService.create(comite);

    // then: El comité se crea correctamente.
    Assertions.assertThat(comiteCreada.getId()).isEqualTo(1L);
    Assertions.assertThat(comiteCreada.getComite()).isEqualTo("Comite1");

  }

  @Test
  public void create_ComiteWithId_ThrowsIllegalArgumentException() {

    // given: Un nuevo comité que ya tiene id
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", formulario, Boolean.TRUE);

    // then: Lanza una excepcion porque el comité ya tiene id
    Assertions.assertThatThrownBy(() -> comiteService.create(comite)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsComite() throws ComiteNotFoundException {

    // given: Un nuevo comité con el servicio actualizado
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comiteServicioActualizado = new Comite(1L, "Comite1 Actualizado", formulario, Boolean.TRUE);

    Comite comite = new Comite(1L, "Comite1", formulario, Boolean.TRUE);

    BDDMockito.given(comiteRepository.findById(1L)).willReturn(Optional.of(comite));
    BDDMockito.given(comiteRepository.save(comite)).willReturn(comiteServicioActualizado);

    // when: Actualizamos el comité
    Comite comiteActualizado = comiteService.update(comite);

    // then: El comité se actualiza correctamente.
    Assertions.assertThat(comiteActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(comiteActualizado.getComite()).isEqualTo("Comite1 Actualizado");

  }

  @Test
  public void update_ThrowsComiteNotFoundException() {

    // given: Un nuevo comité a actualizar
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", formulario, Boolean.TRUE);

    // then: Lanza una excepcion porque el comité no existe
    Assertions.assertThatThrownBy(() -> comiteService.update(comite)).isInstanceOf(ComiteNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un Comite que venga sin id
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(null, "Comite", formulario, Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: update Comite
        () -> comiteService.update(comite))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> comiteService.deleteById(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsComiteNotFoundException() {
    // given: Id no existe
    BDDMockito.given(comiteRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> comiteService.deleteById(1L))
        // then: Lanza ComiteNotFoundException
        .isInstanceOf(ComiteNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesComite() {
    // given: Id existente
    BDDMockito.given(comiteRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(comiteRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> comiteService.deleteById(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void deleteAll_DeleteAllComite() {
    // given: One hundred Comites
    List<Comite> comites = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      comites.add(generarMockComite(Long.valueOf(i), String.format("Comite%03d", i), Boolean.TRUE));
    }

    BDDMockito.doNothing().when(comiteRepository).deleteAll();

    Assertions.assertThatCode(
        // when: Delete all
        () -> comiteService.deleteAll())
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {

    // given: Cien Comite
    List<Comite> comiteList = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      Formulario formulario = new Formulario(Long.valueOf(i), "M" + i, "Descripcion");
      comiteList.add(new Comite(Long.valueOf(i), "Comite" + String.format("%03d", i), formulario, Boolean.TRUE));
    }

    BDDMockito
        .given(
            comiteRepository.findAll(ArgumentMatchers.<Specification<Comite>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Comite>>() {
          @Override
          public Page<Comite> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Comite> content = comiteList.subList(fromIndex, toIndex);
            Page<Comite> page = new PageImpl<>(content, pageable, comiteList.size());
            return page;
          }
        });

    // when: Se obtiene page=3 con pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Comite> page = comiteService.findAll(null, paging);

    // then: Se retorna una Page con diez Comite que contienen
    // Comite='Comite031' to
    // 'Comite040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Comite comite = page.getContent().get(i);
      Assertions.assertThat(comite.getComite()).isEqualTo("Comite" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto comité.
   * 
   * @param id     identificador del comité.
   * @param comite comité.
   * @param activo indicador de activo.
   */
  private Comite generarMockComite(Long id, String comite, Boolean activo) {
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    return new Comite(id, comite, formulario, activo);

  }
}
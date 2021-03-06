package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.CargoComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.repository.CargoComiteRepository;
import org.crue.hercules.sgi.eti.service.impl.CargoComiteServiceImpl;
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
 * CargoComiteServiceTest
 */
public class CargoComiteServiceTest extends BaseServiceTest {

  @Mock
  private CargoComiteRepository cargoComiteRepository;

  private CargoComiteService cargoComiteService;

  @BeforeEach
  public void setUp() throws Exception {
    cargoComiteService = new CargoComiteServiceImpl(cargoComiteRepository);
  }

  @Test
  public void find_WithId_ReturnsCargoComite() {
    BDDMockito.given(cargoComiteRepository.findById(1L))
        .willReturn(Optional.of(generarMockCargoComite(1L, "CargoComite1")));

    CargoComite cargoComite = cargoComiteService.findById(1L);

    Assertions.assertThat(cargoComite.getId()).isEqualTo(1L);

    Assertions.assertThat(cargoComite.getNombre()).isEqualTo("CargoComite1");

  }

  @Test
  public void find_NotFound_ThrowsCargoComiteNotFoundException() throws Exception {
    BDDMockito.given(cargoComiteRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> cargoComiteService.findById(1L))
        .isInstanceOf(CargoComiteNotFoundException.class);
  }

  @Test
  public void create_ReturnsCargoComite() {
    // given: Un nuevo CargoComite
    CargoComite cargoComiteNew = generarMockCargoComite(1L, "CargoComiteNew");

    CargoComite cargoComite = generarMockCargoComite(1L, "CargoComiteNew");

    BDDMockito.given(cargoComiteRepository.save(cargoComiteNew)).willReturn(cargoComite);

    // when: Creamos el cargo comit??
    CargoComite cargoComiteCreado = cargoComiteService.create(cargoComiteNew);

    // then: El cargo comit?? se crea correctamente
    Assertions.assertThat(cargoComiteCreado).isNotNull();
    Assertions.assertThat(cargoComiteCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(cargoComiteCreado.getNombre()).isEqualTo("CargoComiteNew");
  }

  @Test
  public void create_CargoComiteWithoutId_ThrowsIllegalArgumentException() {
    // given: Un nuevo cargo comit?? que no tiene id
    CargoComite cargoComiteNew = generarMockCargoComite(null, "CargoComiteNew");
    // when: Creamos el cargo comit??
    // then: Lanza una excepcion porque el cargo comit?? no tiene id
    Assertions.assertThatThrownBy(() -> cargoComiteService.create(cargoComiteNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsCargoComite() {
    // given: Un nuevo cargo comit?? actualizado
    CargoComite cargoComiteActualizado = generarMockCargoComite(1L, "CargoComite1 actualizada");

    CargoComite cargoComite = generarMockCargoComite(1L, "CargoComite1");

    BDDMockito.given(cargoComiteRepository.findById(1L)).willReturn(Optional.of(cargoComite));
    BDDMockito.given(cargoComiteRepository.save(cargoComite)).willReturn(cargoComiteActualizado);

    // when: Actualizamos cargo comit??
    CargoComite cargoComiteActualizadoBBDD = cargoComiteService.update(cargoComite);

    // then: El cargo comit?? se actualiza correctamente.
    Assertions.assertThat(cargoComiteActualizadoBBDD.getId()).isEqualTo(1L);
    Assertions.assertThat(cargoComiteActualizadoBBDD.getNombre()).isEqualTo("CargoComite1 actualizada");

  }

  @Test
  public void update_ThrowsCargoComiteNotFoundException() {
    // given: Un nuevo cargo comit?? a actualizar
    CargoComite cargoComite = generarMockCargoComite(1L, "CargoComite");

    // then: Lanza una excepcion porque el cargo comit?? no existe
    Assertions.assertThatThrownBy(() -> cargoComiteService.update(cargoComite))
        .isInstanceOf(CargoComiteNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un cargo comit?? que venga sin id
    CargoComite cargoComite = generarMockCargoComite(null, "CargoComite");

    Assertions.assertThatThrownBy(
        // when: update cargo comit??
        () -> cargoComiteService.update(cargoComite))
        // then: Lanza una excepci??n, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> cargoComiteService.delete(null))
        // then: Lanza una excepci??n
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsCargoComiteNotFoundException() {
    // given: Id no existe
    BDDMockito.given(cargoComiteRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> cargoComiteService.delete(1L))
        // then: Lanza CargoComiteNotFoundException
        .isInstanceOf(CargoComiteNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesCargoComite() {
    // given: Id existente
    BDDMockito.given(cargoComiteRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(cargoComiteRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> cargoComiteService.delete(1L))
        // then: No se lanza ninguna excepci??n
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullCargoComiteList() {
    // given: One hundred cargo comit??
    List<CargoComite> cargoComites = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      cargoComites.add(generarMockCargoComite(Long.valueOf(i), "CargoComite" + String.format("%03d", i)));
    }

    BDDMockito.given(cargoComiteRepository.findAll(ArgumentMatchers.<Specification<CargoComite>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(cargoComites));

    // when: find unlimited
    Page<CargoComite> page = cargoComiteService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred cargo comites
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred CargoComites
    List<CargoComite> cargoComites = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      cargoComites.add(generarMockCargoComite(Long.valueOf(i), "CargoComite" + String.format("%03d", i)));
    }

    BDDMockito.given(cargoComiteRepository.findAll(ArgumentMatchers.<Specification<CargoComite>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<CargoComite>>() {
          @Override
          public Page<CargoComite> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<CargoComite> content = cargoComites.subList(fromIndex, toIndex);
            Page<CargoComite> page = new PageImpl<>(content, pageable, cargoComites.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<CargoComite> page = cargoComiteService.findAll(null, paging);

    // then: A Page with ten CargoComites are returned containing
    // nombre='CargoComite031' to 'CargoComite040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      CargoComite cargoComite = page.getContent().get(i);
      Assertions.assertThat(cargoComite.getNombre()).isEqualTo("CargoComite" + String.format("%03d", j));
    }
  }

  /**
   * Funci??n que devuelve un objeto CargoComite
   * 
   * @param id     id del cargoComite
   * @param nombre nombre del cargoComite
   * @return el objeto CargoComite
   */

  public CargoComite generarMockCargoComite(Long id, String nombre) {

    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(id);
    cargoComite.setNombre(nombre);
    cargoComite.setActivo(Boolean.TRUE);

    return cargoComite;
  }
}
package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoFaseNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.repository.TipoFaseRepository;
import org.crue.hercules.sgi.csp.service.impl.TipoFaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * TipoFaseServiceTest
 */
public class TipoFaseServiceTest extends BaseServiceTest {

  @Mock
  private TipoFaseRepository tipoFaseRepository;

  private TipoFaseService tipoFaseService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoFaseService = new TipoFaseServiceImpl(tipoFaseRepository);
  }

  @Test
  public void findById_WithId_ReturnsTipoFase() {
    BDDMockito.given(tipoFaseRepository.findById(1L)).willReturn(Optional.of(generarMockTipoFase(1L, "TipoFase1")));

    TipoFase tipoFase = tipoFaseService.findById(1L);
    Assertions.assertThat(tipoFase.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoFase.getActivo()).isEqualTo(Boolean.TRUE);
    Assertions.assertThat(tipoFase.getNombre()).isEqualTo("TipoFase1");

  }

  @Test
  public void find_NotFound_ThrowsTipoFaseNotFoundException() throws Exception {
    BDDMockito.given(tipoFaseRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> tipoFaseService.findById(1L)).isInstanceOf(TipoFaseNotFoundException.class);
  }

  @Test
  public void create_ReturnsTipoFase() {
    // given: Un nuevo TipoFase
    TipoFase tipoFase = generarMockTipoFase(null);

    BDDMockito.given(tipoFaseRepository.findByNombreAndActivoIsTrue(tipoFase.getNombre())).willReturn(Optional.empty());

    BDDMockito.given(tipoFaseRepository.save(tipoFase)).will((InvocationOnMock invocation) -> {
      TipoFase tipoFaseCreado = invocation.getArgument(0);
      tipoFaseCreado.setId(1L);
      return tipoFaseCreado;
    });

    // when: Creamos el TipoFase
    TipoFase tipoFaseCreado = tipoFaseService.create(tipoFase);

    // then: El TipoFase se crea correctamente
    Assertions.assertThat(tipoFaseCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoFaseCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoFaseCreado.getNombre()).as("getNombre").isEqualTo(tipoFase.getNombre());
    Assertions.assertThat(tipoFaseCreado.getActivo()).as("getActivo").isEqualTo(tipoFase.getActivo());

  }

  @Test
  public void update_ReturnsTipoFase() {
    // given: Un nuevo tipo Fase con el servicio actualizado
    TipoFase tipoFaseServicioActualizado = generarMockTipoFase(1L, "TipoFase1 actualizada");

    TipoFase tipoFase = generarMockTipoFase(1L, "TipoFase1");

    BDDMockito.given(tipoFaseRepository.findById(1L)).willReturn(Optional.of(tipoFase));
    BDDMockito.given(tipoFaseRepository.save(tipoFase)).willReturn(tipoFaseServicioActualizado);

    // when: Actualizamos el tipo Fase
    TipoFase tipoFaseActualizado = tipoFaseService.update(tipoFase);

    // then: El tipo Fase se actualiza correctamente.
    Assertions.assertThat(tipoFaseActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoFaseActualizado.getNombre()).isEqualTo("TipoFase1 actualizada");

  }

  @Test
  public void update_ThrowsTipoFaseNotFoundException() {
    // given: Un nuevo tipo Fase a actualizar
    TipoFase tipoFase = generarMockTipoFase(1L, "TipoFase");

    // then: Lanza una excepcion porque el tipo Fase no existe
    Assertions.assertThatThrownBy(() -> tipoFaseService.update(tipoFase)).isInstanceOf(TipoFaseNotFoundException.class);

  }

  @Test
  public void enable_ReturnsTipoFase() {
    // given: Un nuevo TipoFase inactivo
    TipoFase tipoFase = generarMockTipoFase(1L);
    tipoFase.setActivo(Boolean.FALSE);

    BDDMockito.given(tipoFaseRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(tipoFase));
    BDDMockito.given(tipoFaseRepository.save(ArgumentMatchers.<TipoFase>any())).willAnswer(new Answer<TipoFase>() {
      @Override
      public TipoFase answer(InvocationOnMock invocation) throws Throwable {
        TipoFase givenData = invocation.getArgument(0, TipoFase.class);
        givenData.setActivo(Boolean.TRUE);
        return givenData;
      }
    });

    // when: activamos el TipoFase
    TipoFase tipoFaseActualizado = tipoFaseService.enable(tipoFase.getId());

    // then: El TipoFase se activa correctamente.
    Assertions.assertThat(tipoFaseActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoFaseActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoFaseActualizado.getNombre()).as("getNombre()").isEqualTo(tipoFase.getNombre());
    Assertions.assertThat(tipoFaseActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(tipoFase.getDescripcion());
    Assertions.assertThat(tipoFaseActualizado.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);

  }

  @Test
  public void enable_WithIdNotExist_ThrowsTipoFinanciacionNotFoundException() {
    // given: Un id de un TipoFase que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(tipoFaseRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el TipoFase
    // then: Lanza una excepcion porque el TipoFase no existe
    Assertions.assertThatThrownBy(() -> tipoFaseService.enable(idNoExiste))
        .isInstanceOf(TipoFaseNotFoundException.class);
  }

  @Test
  public void enable_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un TipoFase inactivo con nombre existente
    TipoFase tipoFaseExistente = generarMockTipoFase(2L);
    TipoFase tipoFase = generarMockTipoFase(1L);
    tipoFase.setActivo(Boolean.FALSE);

    BDDMockito.given(tipoFaseRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(tipoFase));
    BDDMockito.given(tipoFaseRepository.findByNombreAndActivoIsTrue(ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(tipoFaseExistente));

    // when: activamos el TipoFase
    // then: Lanza una excepcion porque el TipoFase no existe
    Assertions.assertThatThrownBy(() -> tipoFaseService.enable(tipoFase.getId()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un TipoFase activo con el nombre '%s'", tipoFase.getNombre());

  }

  @Test
  public void disable_ReturnsTipoFase() {
    // given: Un nuevo TipoFase activo
    TipoFase tipoFase = generarMockTipoFase(1L);

    BDDMockito.given(tipoFaseRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(tipoFase));
    BDDMockito.given(tipoFaseRepository.save(ArgumentMatchers.<TipoFase>any())).willAnswer(new Answer<TipoFase>() {
      @Override
      public TipoFase answer(InvocationOnMock invocation) throws Throwable {
        TipoFase givenData = invocation.getArgument(0, TipoFase.class);
        givenData.setActivo(Boolean.FALSE);
        return givenData;
      }
    });

    // when: Desactivamos el TipoFase
    TipoFase tipoFaseActualizado = tipoFaseService.disable(tipoFase.getId());

    // then: El TipoFase se desactiva correctamente.
    Assertions.assertThat(tipoFaseActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoFaseActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoFaseActualizado.getNombre()).as("getNombre()").isEqualTo(tipoFase.getNombre());
    Assertions.assertThat(tipoFaseActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(tipoFase.getDescripcion());
    Assertions.assertThat(tipoFaseActualizado.getActivo()).as("getActivo()").isEqualTo(false);

  }

  @Test
  public void disable_WithIdNotExist_ThrowsTipoFaseNotFoundException() {
    // given: Un id de un TipoFase que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(tipoFaseRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el TipoFase
    // then: Lanza una excepcion porque el TipoFase no existe
    Assertions.assertThatThrownBy(() -> tipoFaseService.disable(idNoExiste))
        .isInstanceOf(TipoFaseNotFoundException.class);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred TipoFases
    List<TipoFase> tipoFaseList = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoFaseList.add(generarMockTipoFase(Long.valueOf(i), "TipoFase" + String.format("%03d", i)));
    }

    BDDMockito.given(
        tipoFaseRepository.findAll(ArgumentMatchers.<Specification<TipoFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoFase>>() {
          @Override
          public Page<TipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoFase> content = tipoFaseList.subList(fromIndex, toIndex);
            Page<TipoFase> page = new PageImpl<>(content, pageable, tipoFaseList.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoFase> page = tipoFaseService.findAll(null, paging);

    // then: A Page with ten TipoFasees are returned containing
    // descripcion='TipoFase031' to 'TipoFase040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoFase tipoFase = page.getContent().get(i);
      Assertions.assertThat(tipoFase.getNombre()).isEqualTo("TipoFase" + String.format("%03d", j));
    }
  }

  @Test
  public void findAllTodos_WithPaging_ReturnsPage() {
    // given: One hundred TipoFases
    List<TipoFase> tipoFaseList = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoFaseList.add(generarMockTipoFase(Long.valueOf(i), "TipoFase" + String.format("%03d", i)));
    }

    BDDMockito.given(
        tipoFaseRepository.findAll(ArgumentMatchers.<Specification<TipoFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoFase>>() {
          @Override
          public Page<TipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoFase> content = tipoFaseList.subList(fromIndex, toIndex);
            Page<TipoFase> page = new PageImpl<>(content, pageable, tipoFaseList.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoFase> page = tipoFaseService.findAllTodos(null, paging);

    // then: A Page with ten TipoFasees are returned containing
    // descripcion='TipoFase031' to 'TipoFase040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoFase tipoFase = page.getContent().get(i);
      Assertions.assertThat(tipoFase.getNombre()).isEqualTo("TipoFase" + String.format("%03d", j));
    }
  }

  @Test
  public void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: a TipoFase with duplicated nombre

    TipoFase givenData = generarMockTipoFase(1L);
    TipoFase newTFase = new TipoFase();
    BeanUtils.copyProperties(givenData, newTFase);
    newTFase.setId(null);

    BDDMockito.given(tipoFaseRepository.findByNombreAndActivoIsTrue(ArgumentMatchers.anyString()))
        .willReturn(Optional.of(givenData));

    Assertions.assertThatThrownBy(
        // when: create TipoFase
        () -> tipoFaseService.create(newTFase))
        // then: throw exception as Nombre already exists
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un TipoFase activo con el nombre '%s'", newTFase.getNombre());
  }

  @Test
  public void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoFase con un nombre que ya existe
    TipoFase tipoFaseUpdated = generarMockTipoFase(1L, "nombreRepetido");
    TipoFase tipoFase = generarMockTipoFase(2L, "nombreRepetido");

    BDDMockito.given(tipoFaseRepository.findByNombreAndActivoIsTrue(tipoFaseUpdated.getNombre()))
        .willReturn(Optional.of(tipoFase));

    // when: Actualizamos el TipoFase
    // then: Lanza una excepcion porque ya existe otro TipoFase con ese nombre
    Assertions.assertThatThrownBy(() -> tipoFaseService.update(tipoFaseUpdated))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un TipoFase activo con el nombre '%s'", tipoFaseUpdated.getNombre());
  }

  /**
   * Función que devuelve un objeto TipoFase
   * 
   * @param id id del TipoFase
   * @return el objeto TipoFase
   */
  public TipoFase generarMockTipoFase(Long id) {
    return generarMockTipoFase(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoFase
   * 
   * @param id id del TipoFase
   * @return el objeto TipoFase
   */
  public TipoFase generarMockTipoFase(Long id, String nombre) {
    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id);
    tipoFase.setNombre(nombre);
    tipoFase.setDescripcion("descripcion-" + id);
    tipoFase.setActivo(Boolean.TRUE);
    return tipoFase;
  }

}

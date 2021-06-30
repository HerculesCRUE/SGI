package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.repository.TipoHitoRepository;
import org.crue.hercules.sgi.csp.service.impl.TipoHitoServiceImpl;
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
 * TipoHitoServiceTest
 */
public class TipoHitoServiceTest extends BaseServiceTest {

  @Mock
  private TipoHitoRepository tipoHitoRepository;

  private TipoHitoService tipoHitoService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoHitoService = new TipoHitoServiceImpl(tipoHitoRepository);
  }

  @Test
  public void find_WithId_ReturnsTipoHito() {
    BDDMockito.given(tipoHitoRepository.findById(1L)).willReturn(Optional.of(generarMockTipoHito(1L, "TipoHito1")));

    TipoHito tipoHito = tipoHitoService.findById(1L);
    Assertions.assertThat(tipoHito.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoHito.getActivo()).isEqualTo(Boolean.TRUE);
    Assertions.assertThat(tipoHito.getNombre()).isEqualTo("TipoHito1");

  }

  @Test
  public void find_NotFound_ThrowsTipoHitoNotFoundException() throws Exception {
    BDDMockito.given(tipoHitoRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> tipoHitoService.findById(1L)).isInstanceOf(TipoHitoNotFoundException.class);
  }

  @Test
  public void create_ReturnsTipoHito() {
    // given: Un nuevo TipoHito
    TipoHito tipoHito = generarMockTipoHito(null);

    BDDMockito.given(tipoHitoRepository.findByNombreAndActivoIsTrue(tipoHito.getNombre())).willReturn(Optional.empty());

    BDDMockito.given(tipoHitoRepository.save(tipoHito)).will((InvocationOnMock invocation) -> {
      TipoHito tipoHitoCreado = invocation.getArgument(0);
      tipoHitoCreado.setId(1L);
      return tipoHitoCreado;
    });

    // when: Creamos el TipoHito
    TipoHito tipoHitoCreado = tipoHitoService.create(tipoHito);

    // then: El TipoHito se crea correctamente
    Assertions.assertThat(tipoHitoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoHitoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoHitoCreado.getNombre()).as("getNombre").isEqualTo(tipoHito.getNombre());
    Assertions.assertThat(tipoHitoCreado.getActivo()).as("getActivo").isEqualTo(tipoHito.getActivo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoHito que ya tiene id
    TipoHito tipoHitoNew = generarMockTipoHito(1L);

    // when: Creamos el TipoHito
    // then: Lanza una excepcion porque el TipoHito ya tiene id
    Assertions.assertThatThrownBy(() -> tipoHitoService.create(tipoHitoNew))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoHito id tiene que ser null para crear un nuevo tipoHito");
  }

  @Test
  public void update_ReturnsTipoHito() {
    // given: Un nuevo tipo Hito con el servicio actualizado
    TipoHito tipoHitoServicioActualizado = generarMockTipoHito(1L, "TipoHito1 actualizada");

    TipoHito tipoHito = generarMockTipoHito(1L, "TipoHito1");

    BDDMockito.given(tipoHitoRepository.findById(1L)).willReturn(Optional.of(tipoHito));
    BDDMockito.given(tipoHitoRepository.save(tipoHito)).willReturn(tipoHitoServicioActualizado);

    // when: Actualizamos el tipo Hito
    TipoHito tipoHitoActualizado = tipoHitoService.update(tipoHito);

    // then: El tipo Hito se actualiza correctamente.
    Assertions.assertThat(tipoHitoActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoHitoActualizado.getNombre()).isEqualTo("TipoHito1 actualizada");

  }

  @Test
  public void update_ThrowsTipoHitoNotFoundException() {
    // given: Un nuevo tipo Hito a actualizar
    TipoHito tipoHito = generarMockTipoHito(1L, "TipoHito");

    // then: Lanza una excepcion porque el tipo Hito no existe
    Assertions.assertThatThrownBy(() -> tipoHitoService.update(tipoHito)).isInstanceOf(TipoHitoNotFoundException.class);

  }

  @Test
  public void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: a TipoHito with duplicated nombre

    TipoHito givenData = generarMockTipoHito(1L);
    TipoHito newTHito = new TipoHito();
    BeanUtils.copyProperties(givenData, newTHito);
    newTHito.setId(null);

    BDDMockito.given(tipoHitoRepository.findByNombreAndActivoIsTrue(ArgumentMatchers.anyString()))
        .willReturn(Optional.of(givenData));

    Assertions.assertThatThrownBy(
        // when: create TipoHito
        () -> tipoHitoService.create(newTHito))
        // then: throw exception as Nombre already exists
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un TipoHito activo con el nombre '%s'", newTHito.getNombre());
  }

  @Test
  public void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoHito con un nombre que ya existe
    TipoHito tipoHitoUpdated = generarMockTipoHito(1L, "nombreRepetido");
    TipoHito tipoHito = generarMockTipoHito(2L, "nombreRepetido");

    BDDMockito.given(tipoHitoRepository.findByNombreAndActivoIsTrue(tipoHitoUpdated.getNombre()))
        .willReturn(Optional.of(tipoHito));

    // when: Actualizamos el TipoHito
    // then: Lanza una excepcion porque ya existe otro TipoHito con ese nombre
    Assertions.assertThatThrownBy(() -> tipoHitoService.update(tipoHitoUpdated))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un TipoHito activo con el nombre '%s'", tipoHito.getNombre());
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred TipoHitos
    List<TipoHito> tipoHitoList = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoHitoList.add(generarMockTipoHito(Long.valueOf(i), "TipoHito" + String.format("%03d", i)));
    }

    BDDMockito.given(
        tipoHitoRepository.findAll(ArgumentMatchers.<Specification<TipoHito>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoHito>>() {
          @Override
          public Page<TipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoHito> content = tipoHitoList.subList(fromIndex, toIndex);
            Page<TipoHito> page = new PageImpl<>(content, pageable, tipoHitoList.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoHito> page = tipoHitoService.findAll(null, paging);

    // then: A Page with ten TipoHitoes are returned containing
    // descripcion='TipoHito031' to 'TipoHito040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoHito tipoHito = page.getContent().get(i);
      Assertions.assertThat(tipoHito.getNombre()).isEqualTo("TipoHito" + String.format("%03d", j));
    }
  }

  @Test
  public void findAllTodos_WithPaging_ReturnsPage() {
    // given: One hundred TipoHitos
    List<TipoHito> tipoHitoList = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoHitoList.add(generarMockTipoHito(Long.valueOf(i), "TipoHito" + String.format("%03d", i)));
    }

    BDDMockito.given(
        tipoHitoRepository.findAll(ArgumentMatchers.<Specification<TipoHito>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoHito>>() {
          @Override
          public Page<TipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoHito> content = tipoHitoList.subList(fromIndex, toIndex);
            Page<TipoHito> page = new PageImpl<>(content, pageable, tipoHitoList.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoHito> page = tipoHitoService.findAllTodos(null, paging);

    // then: A Page with ten TipoHitoes are returned containing
    // descripcion='TipoHito031' to 'TipoHito040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoHito tipoHito = page.getContent().get(i);
      Assertions.assertThat(tipoHito.getNombre()).isEqualTo("TipoHito" + String.format("%03d", j));
    }
  }

  @Test
  public void enable_ReturnsTipoHito() {
    // given: Un nuevo TipoHito inactivo
    TipoHito tipoHito = generarMockTipoHito(1L);
    tipoHito.setActivo(Boolean.FALSE);

    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(tipoHito));
    BDDMockito.given(tipoHitoRepository.save(ArgumentMatchers.<TipoHito>any())).willAnswer(new Answer<TipoHito>() {
      @Override
      public TipoHito answer(InvocationOnMock invocation) throws Throwable {
        TipoHito givenData = invocation.getArgument(0, TipoHito.class);
        givenData.setActivo(Boolean.TRUE);
        return givenData;
      }
    });

    // when: activamos el TipoHito
    TipoHito tipoHitoActualizado = tipoHitoService.enable(tipoHito.getId());

    // then: El TipoHito se activa correctamente.
    Assertions.assertThat(tipoHitoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoHitoActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoHitoActualizado.getNombre()).as("getNombre()").isEqualTo(tipoHito.getNombre());
    Assertions.assertThat(tipoHitoActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(tipoHito.getDescripcion());
    Assertions.assertThat(tipoHitoActualizado.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);

  }

  @Test
  public void enable_WithIdNotExist_ThrowsTipoFinanciacionNotFoundException() {
    // given: Un id de un TipoHito que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el TipoHito
    // then: Lanza una excepcion porque el TipoHito no existe
    Assertions.assertThatThrownBy(() -> tipoHitoService.enable(idNoExiste))
        .isInstanceOf(TipoHitoNotFoundException.class);
  }

  @Test
  public void enable_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un TipoHito inactivo con nombre existente
    TipoHito tipoHitoExistente = generarMockTipoHito(2L);
    TipoHito tipoHito = generarMockTipoHito(1L);
    tipoHito.setActivo(Boolean.FALSE);

    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(tipoHito));
    BDDMockito.given(tipoHitoRepository.findByNombreAndActivoIsTrue(ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(tipoHitoExistente));

    // when: activamos el TipoHito
    // then: Lanza una excepcion porque el TipoHito no existe
    Assertions.assertThatThrownBy(() -> tipoHitoService.enable(tipoHito.getId()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un TipoHito activo con el nombre '%s'", tipoHito.getNombre());

  }

  @Test
  public void disable_ReturnsTipoHito() {
    // given: Un nuevo TipoHito activo
    TipoHito tipoHito = generarMockTipoHito(1L);

    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(tipoHito));
    BDDMockito.given(tipoHitoRepository.save(ArgumentMatchers.<TipoHito>any())).willAnswer(new Answer<TipoHito>() {
      @Override
      public TipoHito answer(InvocationOnMock invocation) throws Throwable {
        TipoHito givenData = invocation.getArgument(0, TipoHito.class);
        givenData.setActivo(Boolean.FALSE);
        return givenData;
      }
    });

    // when: Desactivamos el TipoHito
    TipoHito tipoHitoActualizado = tipoHitoService.disable(tipoHito.getId());

    // then: El TipoHito se desactiva correctamente.
    Assertions.assertThat(tipoHitoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoHitoActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoHitoActualizado.getNombre()).as("getNombre()").isEqualTo(tipoHito.getNombre());
    Assertions.assertThat(tipoHitoActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(tipoHito.getDescripcion());
    Assertions.assertThat(tipoHitoActualizado.getActivo()).as("getActivo()").isEqualTo(false);

  }

  @Test
  public void disable_WithIdNotExist_ThrowsTipoHitoNotFoundException() {
    // given: Un id de un TipoHito que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el TipoHito
    // then: Lanza una excepcion porque el TipoHito no existe
    Assertions.assertThatThrownBy(() -> tipoHitoService.disable(idNoExiste))
        .isInstanceOf(TipoHitoNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto TipoHito
   * 
   * @param id id del TipoHito
   * @return el objeto TipoHito
   */
  public TipoHito generarMockTipoHito(Long id) {
    return generarMockTipoHito(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoHito
   * 
   * @param id id del TipoHito
   * @return el objeto TipoHito
   */
  public TipoHito generarMockTipoHito(Long id, String nombre) {
    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id);
    tipoHito.setNombre(nombre);
    tipoHito.setDescripcion("descripcion-" + id);
    tipoHito.setActivo(Boolean.TRUE);
    return tipoHito;
  }

}

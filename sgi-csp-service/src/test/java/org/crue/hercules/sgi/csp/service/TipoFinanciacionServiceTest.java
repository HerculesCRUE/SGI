package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.repository.TipoFinanciacionRepository;
import org.crue.hercules.sgi.csp.service.impl.TipoFinanciacionServiceImpl;
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
 * TipoFinanciacionServiceTest
 */
public class TipoFinanciacionServiceTest extends BaseServiceTest {

  @Mock
  private TipoFinanciacionRepository repository;

  private TipoFinanciacionService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new TipoFinanciacionServiceImpl(repository);
  }

  @Test
  public void findById_WithId_ReturnsTipoFinanciacion() {
    BDDMockito.given(repository.findById(1L))
        .willReturn(Optional.of(generarMockTipoFinanciacion(1L, "TipoFinanciacion1")));

    TipoFinanciacion tipoFinanciacion = service.findById(1L);
    Assertions.assertThat(tipoFinanciacion.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoFinanciacion.getActivo()).isEqualTo(Boolean.TRUE);
    Assertions.assertThat(tipoFinanciacion.getNombre()).isEqualTo("TipoFinanciacion1");

  }

  @Test
  public void findById_WithIdNotExist_ThrowsTipoFinanciacionNotFoundException() throws Exception {
    BDDMockito.given(repository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> service.findById(1L)).isInstanceOf(TipoFinanciacionNotFoundException.class);
  }

  @Test
  public void create_ReturnsTipoFinanciacion() {
    // given: Un nuevo TipoFinanciacion
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(null);

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(tipoFinanciacion.getNombre())).willReturn(Optional.empty());

    BDDMockito.given(repository.save(tipoFinanciacion)).will((InvocationOnMock invocation) -> {
      TipoFinanciacion tipoFinanciacionCreado = invocation.getArgument(0);
      tipoFinanciacionCreado.setId(1L);
      return tipoFinanciacionCreado;
    });

    // when: Creamos el TipoFinanciacion
    TipoFinanciacion tipoFinanciacionCreado = service.create(tipoFinanciacion);

    // then: El TipoFinanciacion se crea correctamente
    Assertions.assertThat(tipoFinanciacionCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoFinanciacionCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoFinanciacionCreado.getNombre()).as("getNombre").isEqualTo(tipoFinanciacion.getNombre());
    Assertions.assertThat(tipoFinanciacionCreado.getActivo()).as("getActivo").isEqualTo(tipoFinanciacion.getActivo());

  }

  @Test
  public void update_ReturnsTipoFinanciacion() {
    // given: Un nuevo tipo Financiacion con el servicio actualizado
    TipoFinanciacion tipoFinanciacionServicioActualizado = generarMockTipoFinanciacion(1L,
        "TipoFinanciacion1 actualizada");

    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L, "TipoFinanciacion1");

    BDDMockito.given(repository.findById(1L)).willReturn(Optional.of(tipoFinanciacion));
    BDDMockito.given(repository.save(tipoFinanciacion)).willReturn(tipoFinanciacionServicioActualizado);

    // when: Actualizamos el tipo Financiacion
    TipoFinanciacion tipoFinanciacionActualizado = service.update(tipoFinanciacionServicioActualizado);

    // then: El tipo Financiacion se actualiza correctamente.
    Assertions.assertThat(tipoFinanciacionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoFinanciacionActualizado.getNombre()).isEqualTo("TipoFinanciacion1 actualizada");

  }

  @Test
  public void update_ThrowsTipoFinanciacionNotFoundException() {
    // given: Un nuevo tipo Financiacion a actualizar
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L, "TipoFinanciacion");

    // then: Lanza una excepcion porque el tipo Financiacion no existe
    Assertions.assertThatThrownBy(() -> service.update(tipoFinanciacion))
        .isInstanceOf(TipoFinanciacionNotFoundException.class);

  }

  @Test
  public void enable_ReturnsTipoFinanciacion() {
    // given: Un nuevo TipoFinanciacio inactivo
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);
    tipoFinanciacion.setActivo(false);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(tipoFinanciacion));
    BDDMockito.given(repository.findByNombreAndActivoIsTrue(tipoFinanciacion.getNombre())).willReturn(Optional.empty());
    BDDMockito.given(repository.save(ArgumentMatchers.<TipoFinanciacion>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Activamos el TipoFinanciacion
    TipoFinanciacion tipoFinanciacionActualizado = service.enable(tipoFinanciacion.getId());

    // then: El TipoFinanciacion se activa correctamente.
    Assertions.assertThat(tipoFinanciacionActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoFinanciacionActualizado.getId()).as("getId()").isEqualTo(tipoFinanciacion.getId());
    Assertions.assertThat(tipoFinanciacionActualizado.getNombre()).as("getNombre()")
        .isEqualTo(tipoFinanciacion.getNombre());
    Assertions.assertThat(tipoFinanciacionActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(tipoFinanciacion.getDescripcion());
    Assertions.assertThat(tipoFinanciacionActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void enable_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un TipoFinanciacion inactivo con un nombre que ya existe activo
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L, "nombreRepetido");
    tipoFinanciacion.setActivo(false);
    TipoFinanciacion tipoFinanciacionRepetido = generarMockTipoFinanciacion(2L, "nombreRepetido");

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(tipoFinanciacion.getNombre()))
        .willReturn(Optional.of(tipoFinanciacionRepetido));

    // when: Activamos el TipoFinanciacion
    // then: Lanza una excepcion porque hay otro TipoFinanciacion con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(tipoFinanciacion)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un TipoFinanciacion con el nombre %s", tipoFinanciacion.getNombre());
  }

  @Test
  public void enable_WithIdNotExist_ThrowsTipoFinanciacionNotFoundException() {
    // given: Un id de un TipoFinanciacion que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el TipoFinanciacion
    // then: Lanza una excepcion porque el TipoFinanciacion no existe
    Assertions.assertThatThrownBy(() -> service.enable(idNoExiste))
        .isInstanceOf(TipoFinanciacionNotFoundException.class);
  }

  @Test
  public void disable_ReturnsTipoFinanciacion() {
    // given: Un nuevo TipoFinanciacion activo
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(tipoFinanciacion));
    BDDMockito.given(repository.save(ArgumentMatchers.<TipoFinanciacion>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el TipoFinanciacion
    TipoFinanciacion tipoFinanciacionActualizado = service.disable(tipoFinanciacion.getId());

    // then: El TipoFinanciacion se desactiva correctamente.
    Assertions.assertThat(tipoFinanciacionActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoFinanciacionActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoFinanciacionActualizado.getNombre()).as("getNombre()")
        .isEqualTo(tipoFinanciacion.getNombre());
    Assertions.assertThat(tipoFinanciacionActualizado.getActivo()).as("getActivo()").isEqualTo(false);

  }

  @Test
  public void disable_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: disable sin id
        () -> service.disable(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred TipoFinanciacions
    List<TipoFinanciacion> tipoFinanciacionList = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoFinanciacionList
          .add(generarMockTipoFinanciacion(Long.valueOf(i), "TipoFinanciacion" + String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<TipoFinanciacion>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoFinanciacion>>() {
          @Override
          public Page<TipoFinanciacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoFinanciacion> content = tipoFinanciacionList.subList(fromIndex, toIndex);
            Page<TipoFinanciacion> page = new PageImpl<>(content, pageable, tipoFinanciacionList.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoFinanciacion> page = service.findAll(null, paging);

    // then: A Page with ten TipoFinanciaciones are returned containing
    // descripcion='TipoFinanciacion031' to 'TipoFinanciacion040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoFinanciacion tipoFinanciacion = page.getContent().get(i);
      Assertions.assertThat(tipoFinanciacion.getNombre()).isEqualTo("TipoFinanciacion" + String.format("%03d", j));
    }
  }

  @Test
  public void findAllTodos_WithPaging_ReturnsPage() {
    // given: One hundred TipoFinanciacions
    List<TipoFinanciacion> tipoFinanciacionList = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoFinanciacionList
          .add(generarMockTipoFinanciacion(Long.valueOf(i), "TipoFinanciacion" + String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<TipoFinanciacion>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoFinanciacion>>() {
          @Override
          public Page<TipoFinanciacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoFinanciacion> content = tipoFinanciacionList.subList(fromIndex, toIndex);
            Page<TipoFinanciacion> page = new PageImpl<>(content, pageable, tipoFinanciacionList.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoFinanciacion> page = service.findAllTodos(null, paging);

    // then: A Page with ten TipoFinanciaciones are returned containing
    // descripcion='TipoFinanciacion031' to 'TipoFinanciacion040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoFinanciacion tipoFinanciacion = page.getContent().get(i);
      Assertions.assertThat(tipoFinanciacion.getNombre()).isEqualTo("TipoFinanciacion" + String.format("%03d", j));
    }
  }

  @Test
  public void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: a TipoFinanciacion with duplicated nombre

    TipoFinanciacion givenData = generarMockTipoFinanciacion(1L);
    TipoFinanciacion newTFinanciacion = new TipoFinanciacion();
    BeanUtils.copyProperties(givenData, newTFinanciacion);
    newTFinanciacion.setId(null);

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(ArgumentMatchers.anyString()))
        .willReturn(Optional.of(givenData));

    Assertions.assertThatThrownBy(
        // when: create TipoFinanciacion
        () -> service.create(newTFinanciacion))
        // then: throw exception as Nombre already exists
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoFinanciacion con un nombre que ya existe
    TipoFinanciacion tipoFinanciacionUpdated = generarMockTipoFinanciacion(1L, "nombreRepetido");
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(2L, "nombreRepetido");

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(tipoFinanciacionUpdated.getNombre()))
        .willReturn(Optional.of(tipoFinanciacion));

    // when: Actualizamos el TipoFinanciacion
    // then: Lanza una excepcion porque ya existe otro TipoFinanciacion con ese
    // nombre
    Assertions.assertThatThrownBy(() -> service.update(tipoFinanciacionUpdated))
        .isInstanceOf(IllegalArgumentException.class);
  }

  /**
   * Función que devuelve un objeto TipoFinanciacion
   * 
   * @param id id del TipoFinanciacion
   * @return el objeto TipoFinanciacion
   */
  public TipoFinanciacion generarMockTipoFinanciacion(Long id) {
    return generarMockTipoFinanciacion(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoFinanciacion
   * 
   * @param id     id del TipoFinanciacion
   * @param nombre nombre del TipoFinanciacion
   * @return el objeto TipoFinanciacion
   */
  public TipoFinanciacion generarMockTipoFinanciacion(Long id, String nombre) {

    TipoFinanciacion tipoFinanciacion = new TipoFinanciacion();
    tipoFinanciacion.setId(id);
    tipoFinanciacion.setActivo(true);
    tipoFinanciacion.setNombre(nombre);
    tipoFinanciacion.setDescripcion("descripcion-" + 1);

    return tipoFinanciacion;
  }

}

package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.model.TipoFinanciacionDescripcion;
import org.crue.hercules.sgi.csp.model.TipoFinanciacionNombre;
import org.crue.hercules.sgi.csp.repository.TipoFinanciacionRepository;
import org.crue.hercules.sgi.csp.service.impl.TipoFinanciacionServiceImpl;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * TipoFinanciacionServiceTest
 */
@Import({ TipoFinanciacionServiceImpl.class })
class TipoFinanciacionServiceTest extends BaseServiceTest {

  @MockBean
  private TipoFinanciacionRepository repository;

  @Autowired
  private TipoFinanciacionService service;

  @Test
  void findById_WithId_ReturnsTipoFinanciacion() {
    BDDMockito.given(repository.findById(1L))
        .willReturn(Optional.of(generarMockTipoFinanciacion(1L, "TipoFinanciacion1")));

    TipoFinanciacion tipoFinanciacion = service.findById(1L);
    Assertions.assertThat(tipoFinanciacion.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoFinanciacion.getActivo()).isEqualTo(Boolean.TRUE);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoFinanciacion.getNombre(), Language.ES))
        .isEqualTo("TipoFinanciacion1");

  }

  @Test
  void findById_WithIdNotExist_ThrowsTipoFinanciacionNotFoundException() throws Exception {
    BDDMockito.given(repository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> service.findById(1L)).isInstanceOf(TipoFinanciacionNotFoundException.class);
  }

  @Test
  void create_ReturnsTipoFinanciacion() {
    // given: Un nuevo TipoFinanciacion
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(null);

    TipoFinanciacionNombre nombre = tipoFinanciacion.getNombre().iterator().next();

    BDDMockito.given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(nombre.getLang(),
        nombre.getValue()))
        .willReturn(Optional.empty());

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
    Assertions.assertThat(tipoFinanciacionCreado.getNombre())
        .as("getNombre").isEqualTo(tipoFinanciacion.getNombre());
    Assertions.assertThat(tipoFinanciacionCreado.getActivo()).as("getActivo").isEqualTo(tipoFinanciacion.getActivo());

  }

  @Test
  void update_ReturnsTipoFinanciacion() {
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
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoFinanciacionActualizado.getNombre(), Language.ES))
        .isEqualTo("TipoFinanciacion1 actualizada");

  }

  @Test
  void update_ThrowsTipoFinanciacionNotFoundException() {
    // given: Un nuevo tipo Financiacion a actualizar
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L, "TipoFinanciacion");

    // then: Lanza una excepcion porque el tipo Financiacion no existe
    Assertions.assertThatThrownBy(() -> service.update(tipoFinanciacion))
        .isInstanceOf(TipoFinanciacionNotFoundException.class);

  }

  @Test
  void enable_ReturnsTipoFinanciacion() {
    // given: Un nuevo TipoFinanciacio inactivo
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L);
    tipoFinanciacion.setActivo(false);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(tipoFinanciacion));
    TipoFinanciacionNombre nombre = tipoFinanciacion.getNombre().iterator().next();
    BDDMockito.given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(
        nombre.getLang(), nombre.getValue())).willReturn(Optional.empty());
    BDDMockito.given(repository.save(ArgumentMatchers.<TipoFinanciacion>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Activamos el TipoFinanciacion
    TipoFinanciacion tipoFinanciacionActualizado = service.enable(tipoFinanciacion.getId());

    // then: El TipoFinanciacion se activa correctamente.
    Assertions.assertThat(tipoFinanciacionActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoFinanciacionActualizado.getId()).as("getId()").isEqualTo(tipoFinanciacion.getId());
    Assertions.assertThat(tipoFinanciacionActualizado.getNombre())
        .as("getNombre()")
        .isEqualTo(tipoFinanciacion.getNombre());
    Assertions.assertThat(tipoFinanciacionActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(tipoFinanciacion.getDescripcion());
    Assertions.assertThat(tipoFinanciacionActualizado.getActivo()).as("getActivo()").isTrue();
  }

  @Test
  void enable_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un TipoFinanciacion inactivo con un nombre que ya existe activo
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(1L, "nombreRepetido");
    tipoFinanciacion.setActivo(false);
    TipoFinanciacion tipoFinanciacionRepetido = generarMockTipoFinanciacion(2L, "nombreRepetido");
    TipoFinanciacionNombre nombre = tipoFinanciacion.getNombre().iterator().next();

    BDDMockito.given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(nombre.getLang(), nombre.getValue()))
        .willReturn(Optional.of(tipoFinanciacionRepetido));

    // when: Activamos el TipoFinanciacion
    // then: Lanza una excepcion porque hay otro TipoFinanciacion con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(tipoFinanciacion))
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessage("update.tipoFinanciacion.Nombre: Ya existe un tipo de financiacion con el nombre 'nombreRepetido'",
            tipoFinanciacion.getNombre());
  }

  @Test
  void enable_WithIdNotExist_ThrowsTipoFinanciacionNotFoundException() {
    // given: Un id de un TipoFinanciacion que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el TipoFinanciacion
    // then: Lanza una excepcion porque el TipoFinanciacion no existe
    Assertions.assertThatThrownBy(() -> service.enable(idNoExiste))
        .isInstanceOf(TipoFinanciacionNotFoundException.class);
  }

  @Test
  void disable_ReturnsTipoFinanciacion() {
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
    Assertions.assertThat(tipoFinanciacionActualizado.getNombre())
        .as("getNombre()")
        .isEqualTo(tipoFinanciacion.getNombre());
    Assertions.assertThat(tipoFinanciacionActualizado.getActivo()).as("getActivo()").isFalse();

  }

  @Test
  void disable_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: disable sin id
        () -> service.disable(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void findAll_WithPaging_ReturnsPage() {
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
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page).hasSize(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoFinanciacion tipoFinanciacion = page.getContent().get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(tipoFinanciacion.getNombre(), Language.ES))
          .isEqualTo("TipoFinanciacion" + String.format("%03d", j));
    }
  }

  @Test
  void findAllTodos_WithPaging_ReturnsPage() {
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
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page).hasSize(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoFinanciacion tipoFinanciacion = page.getContent().get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(tipoFinanciacion.getNombre(), Language.ES))
          .isEqualTo("TipoFinanciacion" + String.format("%03d", j));
    }
  }

  @Test
  void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: a TipoFinanciacion with duplicated nombre

    TipoFinanciacion givenData = generarMockTipoFinanciacion(1L);
    TipoFinanciacion newTFinanciacion = new TipoFinanciacion();
    BeanUtils.copyProperties(givenData, newTFinanciacion);
    newTFinanciacion.setId(null);

    BDDMockito.given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.any(Language.class),
        ArgumentMatchers
            .anyString()))
        .willReturn(Optional.of(givenData));

    Assertions.assertThatThrownBy(
        // when: create TipoFinanciacion
        () -> service.create(newTFinanciacion))
        // then: throw exception as Nombre already exists
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoFinanciacion con un nombre que ya existe
    TipoFinanciacion tipoFinanciacionUpdated = generarMockTipoFinanciacion(1L, "nombreRepetido");
    TipoFinanciacion tipoFinanciacion = generarMockTipoFinanciacion(2L, "nombreRepetido");
    TipoFinanciacionNombre nombre = tipoFinanciacion.getNombre().iterator().next();

    BDDMockito.given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(nombre.getLang(), nombre.getValue()))
        .willReturn(Optional.of(tipoFinanciacion));

    // when: Actualizamos el TipoFinanciacion
    // then: Lanza una excepcion porque ya existe otro TipoFinanciacion con ese
    // nombre
    Assertions.assertThatThrownBy(() -> service.update(tipoFinanciacionUpdated))
        .isInstanceOf(ConstraintViolationException.class);
  }

  /**
   * Función que devuelve un objeto TipoFinanciacion
   * 
   * @param id id del TipoFinanciacion
   * @return el objeto TipoFinanciacion
   */
  TipoFinanciacion generarMockTipoFinanciacion(Long id) {
    return generarMockTipoFinanciacion(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoFinanciacion
   * 
   * @param id     id del TipoFinanciacion
   * @param nombre nombre del TipoFinanciacion
   * @return el objeto TipoFinanciacion
   */
  TipoFinanciacion generarMockTipoFinanciacion(Long id, String nombre) {

    Set<TipoFinanciacionNombre> tipoFinanciacionNombre = new HashSet<>();
    tipoFinanciacionNombre.add(new TipoFinanciacionNombre(Language.ES, nombre));

    Set<TipoFinanciacionDescripcion> tipoFinanciacionDescripcion = new HashSet<>();
    tipoFinanciacionDescripcion.add(new TipoFinanciacionDescripcion(Language.ES, "descripcion-" + 1));

    TipoFinanciacion tipoFinanciacion = new TipoFinanciacion();
    tipoFinanciacion.setId(id);
    tipoFinanciacion.setActivo(true);
    tipoFinanciacion.setNombre(tipoFinanciacionNombre);
    tipoFinanciacion.setDescripcion(tipoFinanciacionDescripcion);

    return tipoFinanciacion;
  }

}

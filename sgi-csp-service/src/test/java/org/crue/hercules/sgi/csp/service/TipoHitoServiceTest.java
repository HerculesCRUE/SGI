package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.model.TipoHitoDescripcion;
import org.crue.hercules.sgi.csp.model.TipoHitoNombre;
import org.crue.hercules.sgi.csp.repository.TipoHitoRepository;
import org.crue.hercules.sgi.csp.service.impl.TipoHitoServiceImpl;
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
 * TipoHitoServiceTest
 */
@Import({ TipoHitoServiceImpl.class })
class TipoHitoServiceTest extends BaseServiceTest {

  @MockBean
  private TipoHitoRepository tipoHitoRepository;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private TipoHitoService tipoHitoService;

  @Test
  void find_WithId_ReturnsTipoHito() {
    BDDMockito.given(tipoHitoRepository.findById(1L)).willReturn(Optional.of(generarMockTipoHito(1L, "TipoHito1")));

    TipoHito tipoHito = tipoHitoService.findById(1L);
    Assertions.assertThat(tipoHito.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoHito.getActivo()).isEqualTo(Boolean.TRUE);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoHito.getNombre(), Language.ES)).isEqualTo("TipoHito1");

  }

  @Test
  void find_NotFound_ThrowsTipoHitoNotFoundException() throws Exception {
    BDDMockito.given(tipoHitoRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> tipoHitoService.findById(1L)).isInstanceOf(TipoHitoNotFoundException.class);
  }

  @Test
  void create_ReturnsTipoHito() {
    // given: Un nuevo TipoHito
    TipoHito tipoHito = generarMockTipoHito(null);

    BDDMockito.given(tipoHitoRepository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
        ArgumentMatchers.<String>any())).willReturn(Optional.empty());

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
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoHitoCreado.getNombre(), Language.ES)).as("getNombre")
        .isEqualTo(I18nHelper.getValueForLanguage(tipoHito.getNombre(), Language.ES));
    Assertions.assertThat(tipoHitoCreado.getActivo()).as("getActivo").isEqualTo(tipoHito.getActivo());
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoHito que ya tiene id
    TipoHito tipoHitoNew = generarMockTipoHito(1L);

    // when: Creamos el TipoHito
    // then: Lanza una excepcion porque el TipoHito ya tiene id
    Assertions.assertThatThrownBy(() -> tipoHitoService.create(tipoHitoNew))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Tipo Hito debe ser nulo");
  }

  @Test
  void update_ReturnsTipoHito() {
    // given: Un nuevo tipo Hito con el servicio actualizado
    TipoHito tipoHitoServicioActualizado = generarMockTipoHito(1L, "TipoHito1 actualizada");

    TipoHito tipoHito = generarMockTipoHito(1L, "TipoHito1");

    BDDMockito.given(tipoHitoRepository.findById(1L)).willReturn(Optional.of(tipoHito));
    BDDMockito.given(tipoHitoRepository.save(tipoHito)).willReturn(tipoHitoServicioActualizado);

    // when: Actualizamos el tipo Hito
    TipoHito tipoHitoActualizado = tipoHitoService.update(tipoHito);

    // then: El tipo Hito se actualiza correctamente.
    Assertions.assertThat(tipoHitoActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoHitoActualizado.getNombre(), Language.ES))
        .isEqualTo("TipoHito1 actualizada");

  }

  @Test
  void update_ThrowsTipoHitoNotFoundException() {
    // given: Un nuevo tipo Hito a actualizar
    TipoHito tipoHito = generarMockTipoHito(1L, "TipoHito");

    // then: Lanza una excepcion porque el tipo Hito no existe
    Assertions.assertThatThrownBy(() -> tipoHitoService.update(tipoHito)).isInstanceOf(TipoHitoNotFoundException.class);

  }

  @Test
  void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: a TipoHito with duplicated nombre

    TipoHito givenData = generarMockTipoHito(1L);
    TipoHito newTHito = new TipoHito();
    BeanUtils.copyProperties(givenData, newTHito);
    newTHito.setId(null);

    BDDMockito.given(tipoHitoRepository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
        ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(givenData));

    Assertions.assertThatThrownBy(
        // when: create TipoHito
        () -> tipoHitoService.create(newTHito))
        // then: throw exception as Nombre already exists
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un tipo de hito con el nombre '%s'",
            I18nHelper.getValueForLanguage(newTHito.getNombre(), Language.ES));
  }

  @Test
  void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoHito con un nombre que ya existe
    TipoHito tipoHitoUpdated = generarMockTipoHito(1L, "nombreRepetido");
    TipoHito tipoHito = generarMockTipoHito(2L, "nombreRepetido");

    BDDMockito.given(tipoHitoRepository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
        ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(tipoHito));

    // when: Actualizamos el TipoHito
    // then: Lanza una excepcion porque ya existe otro TipoHito con ese nombre
    Assertions.assertThatThrownBy(() -> tipoHitoService.update(tipoHitoUpdated))
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un tipo de hito con el nombre '%s'",
            I18nHelper.getValueForLanguage(tipoHito.getNombre(), Language.ES));
  }

  @Test
  void findAll_WithPaging_ReturnsPage() {
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
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page).hasSize(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoHito tipoHito = page.getContent().get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(tipoHito.getNombre(), Language.ES))
          .isEqualTo("TipoHito" + String.format("%03d", j));
    }
  }

  @Test
  void findAllTodos_WithPaging_ReturnsPage() {
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
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page).hasSize(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoHito tipoHito = page.getContent().get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(tipoHito.getNombre(), Language.ES))
          .isEqualTo("TipoHito" + String.format("%03d", j));
    }
  }

  @Test
  void enable_ReturnsTipoHito() {
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
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoHitoActualizado.getNombre(), Language.ES))
        .as("getNombre()").isEqualTo(I18nHelper.getValueForLanguage(tipoHito.getNombre(), Language.ES));
    Assertions.assertThat(tipoHitoActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(tipoHito.getDescripcion());
    Assertions.assertThat(tipoHitoActualizado.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);

  }

  @Test
  void enable_WithIdNotExist_ThrowsTipoFinanciacionNotFoundException() {
    // given: Un id de un TipoHito que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el TipoHito
    // then: Lanza una excepcion porque el TipoHito no existe
    Assertions.assertThatThrownBy(() -> tipoHitoService.enable(idNoExiste))
        .isInstanceOf(TipoHitoNotFoundException.class);
  }

  @Test
  void enable_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un TipoHito inactivo con nombre existente
    TipoHito tipoHitoExistente = generarMockTipoHito(2L);
    TipoHito tipoHito = generarMockTipoHito(1L);
    tipoHito.setActivo(Boolean.FALSE);

    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(tipoHito));
    BDDMockito.given(tipoHitoRepository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
        ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(tipoHitoExistente));

    // when: activamos el TipoHito
    // then: Lanza una excepcion porque el TipoHito no existe
    Assertions.assertThatThrownBy(() -> tipoHitoService.enable(tipoHito.getId()))
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un tipo de hito con el nombre '%s'",
            I18nHelper.getValueForLanguage(tipoHito.getNombre(), Language.ES));

  }

  @Test
  void disable_ReturnsTipoHito() {
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
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoHitoActualizado.getNombre(), Language.ES))
        .as("getNombre()").isEqualTo(I18nHelper.getValueForLanguage(tipoHito.getNombre(), Language.ES));
    Assertions.assertThat(tipoHitoActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(tipoHito.getDescripcion());
    Assertions.assertThat(tipoHitoActualizado.getActivo()).as("getActivo()").isFalse();

  }

  @Test
  void disable_WithIdNotExist_ThrowsTipoHitoNotFoundException() {
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
  TipoHito generarMockTipoHito(Long id) {
    return generarMockTipoHito(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoHito
   * 
   * @param id id del TipoHito
   * @return el objeto TipoHito
   */
  TipoHito generarMockTipoHito(Long id, String nombre) {
    Set<TipoHitoNombre> nombreTipoHito = new HashSet<>();
    nombreTipoHito.add(new TipoHitoNombre(Language.ES, nombre));

    Set<TipoHitoDescripcion> descripcionTipoHito = new HashSet<>();
    descripcionTipoHito.add(new TipoHitoDescripcion(Language.ES, "descripcion-" + id));

    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id);
    tipoHito.setNombre(nombreTipoHito);
    tipoHito.setDescripcion(descripcionTipoHito);
    tipoHito.setActivo(Boolean.TRUE);
    return tipoHito;
  }

}

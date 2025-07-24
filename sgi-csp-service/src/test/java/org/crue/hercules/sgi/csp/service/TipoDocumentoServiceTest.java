package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumentoDescripcion;
import org.crue.hercules.sgi.csp.model.TipoDocumentoNombre;
import org.crue.hercules.sgi.csp.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.csp.service.impl.TipoDocumentoServiceImpl;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * TipoDocumentoServiceTest
 */
@Import({ TipoDocumentoServiceImpl.class })
class TipoDocumentoServiceTest extends BaseServiceTest {

  @MockBean
  private TipoDocumentoRepository tipoDocumentoRepository;

  @Autowired
  private TipoDocumentoService tipoDocumentoService;

  @Test
  void create_ReturnsTipoDocumento() {
    // given: Un nuevo TipoDocumento
    TipoDocumento tipoDocumento = generarMockTipoDocumento(null);

    BDDMockito
        .given(tipoDocumentoRepository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
            ArgumentMatchers.<String>any()))
        .willReturn(Optional.empty());

    BDDMockito.given(tipoDocumentoRepository.save(tipoDocumento)).will((InvocationOnMock invocation) -> {
      TipoDocumento tipoDocumentoCreado = invocation.getArgument(0);
      tipoDocumentoCreado.setId(1L);
      return tipoDocumentoCreado;
    });

    // when: Creamos el TipoDocumento
    TipoDocumento tipoDocumentoCreado = tipoDocumentoService.create(tipoDocumento);

    // then: El TipoDocumento se crea correctamente
    Assertions.assertThat(tipoDocumentoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoDocumentoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoDocumentoCreado.getNombre(), Language.ES)).as("getNombre")
        .isEqualTo(I18nHelper.getValueForLanguage(tipoDocumento.getNombre(), Language.ES));
    Assertions.assertThat(tipoDocumentoCreado.getActivo()).as("getActivo").isEqualTo(tipoDocumento.getActivo());
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoDocumento que ya tiene id
    TipoDocumento tipoDocumentoNew = generarMockTipoDocumento(1L);

    // when: Creamos el TipoDocumento
    // then: Lanza una excepcion porque el TipoDocumento ya tiene id
    Assertions.assertThatThrownBy(() -> tipoDocumentoService.create(tipoDocumentoNew))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Tipo Documento debe ser nulo");
  }

  @Test
  void create_WithNombreRepetido_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoDocumento con un nombre que ya existe
    TipoDocumento tipoDocumentoNew = generarMockTipoDocumento(null, "nombreRepetido");
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L, "nombreRepetido");

    BDDMockito
        .given(tipoDocumentoRepository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
            ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(tipoDocumento));

    // when: Creamos el TipoDocumento
    // then: Lanza una excepcion porque hay otro TipoDocumento con ese nombre
    Assertions.assertThatThrownBy(() -> tipoDocumentoService.create(tipoDocumentoNew))
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un tipo de documento con el nombre '%s'",
            I18nHelper.getValueForLanguage(tipoDocumentoNew.getNombre(), Language.ES));
  }

  @Test
  void update_ReturnsTipoDocumento() {
    // given: Un nuevo TipoDocumento con el nombre actualizado
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    TipoDocumento tipoDocumentoNombreActualizado = generarMockTipoDocumento(1L, "NombreActualizado");

    BDDMockito
        .given(tipoDocumentoRepository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
            ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(tipoDocumento));

    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(tipoDocumento));
    BDDMockito.given(tipoDocumentoRepository.save(ArgumentMatchers.<TipoDocumento>any()))
        .willReturn(tipoDocumentoNombreActualizado);

    // when: Actualizamos el TipoDocumento
    TipoDocumento tipoDocumentoActualizado = tipoDocumentoService.update(tipoDocumentoNombreActualizado);

    // then: El TipoDocumento se actualiza correctamente.
    Assertions.assertThat(tipoDocumentoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoDocumentoActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoDocumentoActualizado.getNombre(), Language.ES))
        .as("getNombre()")
        .isEqualTo(I18nHelper.getValueForLanguage(tipoDocumentoActualizado.getNombre(), Language.ES));

  }

  @Test
  void update_WithIdNotExist_ThrowsTipoDocumentoNotFoundException() {
    // given: Un TipoDocumento a actualizar con un id que no existe
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L, "TipoDocumento");

    // when: Actualizamos el TipoDocumento
    // then: Lanza una excepcion porque el TipoDocumento no existe
    Assertions.assertThatThrownBy(() -> tipoDocumentoService.update(tipoDocumento))
        .isInstanceOf(TipoDocumentoNotFoundException.class);
  }

  @Test
  void enable_ReturnsTipoDocumento() {
    // given: Un nuevo TipoDocumento inactivo
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    tipoDocumento.setActivo(Boolean.FALSE);

    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(tipoDocumento));
    BDDMockito.given(tipoDocumentoRepository.save(ArgumentMatchers.<TipoDocumento>any()))
        .willAnswer(new Answer<TipoDocumento>() {
          @Override
          public TipoDocumento answer(InvocationOnMock invocation) throws Throwable {
            TipoDocumento givenData = invocation.getArgument(0, TipoDocumento.class);
            givenData.setActivo(Boolean.TRUE);
            return givenData;
          }
        });

    // when: activamos el TipoDocumento
    TipoDocumento tipoDocumentoActualizado = tipoDocumentoService.enable(tipoDocumento.getId());

    // then: El TipoDocumento se activa correctamente.
    Assertions.assertThat(tipoDocumentoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoDocumentoActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoDocumentoActualizado.getNombre(), Language.ES))
        .as("getNombre()").isEqualTo(I18nHelper.getValueForLanguage(tipoDocumento.getNombre(), Language.ES));
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoDocumentoActualizado.getDescripcion(), Language.ES))
        .as("getDescripcion()")
        .isEqualTo(I18nHelper.getValueForLanguage(tipoDocumento.getDescripcion(), Language.ES));
    Assertions.assertThat(tipoDocumentoActualizado.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);

  }

  @Test
  void enable_WithIdNotExist_ThrowsTipoFinanciacionNotFoundException() {
    // given: Un id de un TipoDocumento que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el TipoDocumento
    // then: Lanza una excepcion porque el TipoDocumento no existe
    Assertions.assertThatThrownBy(() -> tipoDocumentoService.enable(idNoExiste))
        .isInstanceOf(TipoDocumentoNotFoundException.class);
  }

  @Test
  void enable_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un TipoDocumento inactivo con nombre existente
    TipoDocumento tipoDocumentoExistente = generarMockTipoDocumento(2L);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    tipoDocumento.setActivo(Boolean.FALSE);

    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(tipoDocumento));
    BDDMockito
        .given(tipoDocumentoRepository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
            ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(tipoDocumentoExistente));

    // when: activamos el TipoDocumento
    // then: Lanza una excepcion porque el TipoDocumento no existe
    Assertions.assertThatThrownBy(() -> tipoDocumentoService.enable(tipoDocumento.getId()))
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un tipo de documento con el nombre '%s'",
            I18nHelper.getValueForLanguage(tipoDocumento.getNombre(), Language.ES));

  }

  @Test
  void disable_ReturnsTipoDocumento() {
    // given: Un nuevo TipoDocumento activo
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);

    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(tipoDocumento));
    BDDMockito.given(tipoDocumentoRepository.save(ArgumentMatchers.<TipoDocumento>any()))
        .willAnswer(new Answer<TipoDocumento>() {
          @Override
          public TipoDocumento answer(InvocationOnMock invocation) throws Throwable {
            TipoDocumento givenData = invocation.getArgument(0, TipoDocumento.class);
            givenData.setActivo(Boolean.FALSE);
            return givenData;
          }
        });

    // when: Desactivamos el TipoDocumento
    TipoDocumento tipoDocumentoActualizado = tipoDocumentoService.disable(tipoDocumento.getId());

    // then: El TipoDocumento se desactiva correctamente.
    Assertions.assertThat(tipoDocumentoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoDocumentoActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoDocumentoActualizado.getNombre(), Language.ES))
        .as("getNombre()").isEqualTo(I18nHelper.getValueForLanguage(tipoDocumento.getNombre(), Language.ES));
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoDocumentoActualizado.getDescripcion(), Language.ES))
        .as("getDescripcion()")
        .isEqualTo(I18nHelper.getValueForLanguage(tipoDocumento.getDescripcion(), Language.ES));
    Assertions.assertThat(tipoDocumentoActualizado.getActivo()).as("getActivo()").isFalse();

  }

  @Test
  void disable_WithIdNotExist_ThrowsTipoDocumentoNotFoundException() {
    // given: Un id de un TipoDocumento que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(tipoDocumentoRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el TipoDocumento
    // then: Lanza una excepcion porque el TipoDocumento no existe
    Assertions.assertThatThrownBy(() -> tipoDocumentoService.disable(idNoExiste))
        .isInstanceOf(TipoDocumentoNotFoundException.class);
  }

  @Test
  void update_WithNombreRepetido_ThrowsIllegalArgumentException() {
    // given: Un nuevo TipoDocumento con un nombre que ya existe
    TipoDocumento tipoDocumentoUpdated = generarMockTipoDocumento(1L, "nombreRepetido");
    TipoDocumento tipoDocumento = generarMockTipoDocumento(2L, "nombreRepetido");

    BDDMockito
        .given(tipoDocumentoRepository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
            ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(tipoDocumento));

    // when: Actualizamos el TipoDocumento
    // then: Lanza una excepcion porque ya existe otro TipoDocumento con ese nombre
    Assertions.assertThatThrownBy(() -> tipoDocumentoService.update(tipoDocumentoUpdated))
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un tipo de documento con el nombre '%s'",
            I18nHelper.getValueForLanguage(tipoDocumentoUpdated.getNombre(), Language.ES));
  }

  @Test
  void findAll_ReturnsPage() {
    // given: Una lista con 37 TipoDocumento
    List<TipoDocumento> tiposDocumento = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposDocumento.add(generarMockTipoDocumento(i, "TipoDocumento" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoDocumentoRepository.findAll(ArgumentMatchers.<Specification<TipoDocumento>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposDocumento.size() ? tiposDocumento.size() : toIndex;
            List<TipoDocumento> content = tiposDocumento.subList(fromIndex, toIndex);
            Page<TipoDocumento> page = new PageImpl<>(content, pageable, tiposDocumento.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoDocumento> page = tipoDocumentoService.findAll(null, paging);

    // then: Devuelve la pagina 3 con los TipoDocumento del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      TipoDocumento tipoDocumento = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(I18nHelper.getValueForLanguage(tipoDocumento.getNombre(), Language.ES))
          .isEqualTo("TipoDocumento" + String.format("%03d", i));
    }
  }

  @Test
  void findAllTodos_ReturnsPage() {
    // given: Una lista con 37 TipoDocumento
    List<TipoDocumento> tiposDocumento = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposDocumento.add(generarMockTipoDocumento(i, "TipoDocumento" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoDocumentoRepository.findAll(ArgumentMatchers.<Specification<TipoDocumento>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposDocumento.size() ? tiposDocumento.size() : toIndex;
            List<TipoDocumento> content = tiposDocumento.subList(fromIndex, toIndex);
            Page<TipoDocumento> page = new PageImpl<>(content, pageable, tiposDocumento.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoDocumento> page = tipoDocumentoService.findAllTodos(null, paging);

    // then: Devuelve la pagina 3 con los TipoDocumento del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      TipoDocumento tipoDocumento = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(I18nHelper.getValueForLanguage(tipoDocumento.getNombre(), Language.ES))
          .isEqualTo("TipoDocumento" + String.format("%03d", i));
    }
  }

  @Test
  void findById_ReturnsTipoDocumento() {
    // given: Un TipoDocumento con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(tipoDocumentoRepository.findById(idBuscado))
        .willReturn(Optional.of(generarMockTipoDocumento(idBuscado)));

    // when: Buscamos el TipoDocumento por su id
    TipoDocumento tipoDocumento = tipoDocumentoService.findById(idBuscado);

    // then: el TipoDocumento
    Assertions.assertThat(tipoDocumento).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoDocumento.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoDocumento.getNombre(), Language.ES)).as("getNombre()")
        .isEqualTo("nombre-1");
    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoDocumento.getDescripcion(), Language.ES))
        .as("getDescripcion()").isEqualTo("descripcion-1");
    Assertions.assertThat(tipoDocumento.getActivo()).as("getActivo()").isTrue();

  }

  @Test
  void findById_WithIdNotExist_ThrowsTipoDocumentoNotFoundException() throws Exception {
    // given: Ningun TipoDocumento con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(tipoDocumentoRepository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el TipoDocumento por su id
    // then: lanza un TipoDocumentoNotFoundException
    Assertions.assertThatThrownBy(() -> tipoDocumentoService.findById(idBuscado))
        .isInstanceOf(TipoDocumentoNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id) {
    return generarMockTipoDocumento(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id, String nombre) {
    Set<TipoDocumentoNombre> nombreTipoDocumento = new HashSet<>();
    nombreTipoDocumento.add(new TipoDocumentoNombre(Language.ES, nombre));

    Set<TipoDocumentoDescripcion> descripcionTipoDocumento = new HashSet<>();
    descripcionTipoDocumento.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-" + id));

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nombreTipoDocumento);
    tipoDocumento.setDescripcion(descripcionTipoDocumento);
    tipoDocumento.setActivo(Boolean.TRUE);

    return tipoDocumento;
  }

}
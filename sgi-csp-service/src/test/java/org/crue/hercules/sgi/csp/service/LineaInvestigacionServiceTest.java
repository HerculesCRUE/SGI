package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.validation.ValidationException;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.LineaInvestigacionNotFoundException;
import org.crue.hercules.sgi.csp.model.LineaInvestigacion;
import org.crue.hercules.sgi.csp.repository.LineaInvestigacionRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * LineaInvestigacionServiceTest
 */
@Import({ LineaInvestigacionService.class, ApplicationContextSupport.class })
class LineaInvestigacionServiceTest extends BaseServiceTest {

  @MockBean
  private LineaInvestigacionRepository lineaInvestigacionRepository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @Autowired
  private LineaInvestigacionService lineaInvestigacionService;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void findById_WithId_ReturnsLineaInvestigacion() {
    BDDMockito.given(lineaInvestigacionRepository.findById(1L))
        .willReturn(Optional.of(generarMockLineaInvestigacion(1L, "LineaInvestigacion1")));

    LineaInvestigacion lineaInvestigacion = lineaInvestigacionService.findById(1L);
    Assertions.assertThat(lineaInvestigacion.getId()).isEqualTo(1L);
    Assertions.assertThat(lineaInvestigacion.getActivo()).isEqualTo(Boolean.TRUE);
    Assertions.assertThat(lineaInvestigacion.getNombre()).isEqualTo("LineaInvestigacion1");

  }

  @Test
  void find_NotFound_ThrowsLineaInvestigacionNotFoundException() throws Exception {
    BDDMockito.given(lineaInvestigacionRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> lineaInvestigacionService.findById(1L))
        .isInstanceOf(LineaInvestigacionNotFoundException.class);
  }

  @Test
  void create_ReturnsLineaInvestigacion() {
    // given: Un nuevo LineaInvestigacion
    LineaInvestigacion lineaInvestigacion = generarMockLineaInvestigacion(null);

    mockActivableIsActivo(LineaInvestigacion.class, null);

    BDDMockito.given(lineaInvestigacionRepository.save(lineaInvestigacion)).will((InvocationOnMock invocation) -> {
      LineaInvestigacion lineaInvestigacionCreado = invocation.getArgument(0);
      lineaInvestigacionCreado.setId(1L);
      return lineaInvestigacionCreado;
    });

    // when: Creamos el LineaInvestigacion
    LineaInvestigacion lineaInvestigacionCreado = lineaInvestigacionService.create(lineaInvestigacion);

    // then: El LineaInvestigacion se crea correctamente
    Assertions.assertThat(lineaInvestigacionCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(lineaInvestigacionCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(lineaInvestigacionCreado.getNombre()).as("getNombre")
        .isEqualTo(lineaInvestigacion.getNombre());
    Assertions.assertThat(lineaInvestigacionCreado.getActivo()).as("getActivo")
        .isEqualTo(lineaInvestigacion.getActivo());

  }

  @Test
  void update_ReturnsLineaInvestigacion() {
    // given: Un nuevo tipo Fase con el servicio actualizado
    LineaInvestigacion lineaInvestigacionServicioActualizado = generarMockLineaInvestigacion(1L,
        "LineaInvestigacion1 actualizada");

    LineaInvestigacion lineaInvestigacion = generarMockLineaInvestigacion(1L, "LineaInvestigacion1");

    mockActivableIsActivo(LineaInvestigacion.class, lineaInvestigacion);

    BDDMockito.given(lineaInvestigacionRepository.findById(1L)).willReturn(Optional.of(lineaInvestigacion));
    BDDMockito.given(lineaInvestigacionRepository.save(lineaInvestigacion))
        .willReturn(lineaInvestigacionServicioActualizado);

    // when: Actualizamos el tipo Fase
    LineaInvestigacion lineaInvestigacionActualizado = lineaInvestigacionService.update(lineaInvestigacion);

    // then: El tipo Fase se actualiza correctamente.
    Assertions.assertThat(lineaInvestigacionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(lineaInvestigacionActualizado.getNombre()).isEqualTo("LineaInvestigacion1 actualizada");

  }

  @Test
  void update_NoActivaLineaInvestigacion_ThrowsValidationException() {
    // given: Un LineaInvestigacion actualizado
    Long id = 1L;
    LineaInvestigacion fuenteFinanciacion = generarMockLineaInvestigacion(id);

    mockActivableIsActivo(LineaInvestigacion.class, null);
    BDDMockito.given(entityManager.find(LineaInvestigacion.class, id)).willReturn(null);

    // when: Actualizamos el LineaInvestigacion
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> lineaInvestigacionService.update(fuenteFinanciacion))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void enable_ReturnsLineaInvestigacion() {
    // given: Un nuevo LineaInvestigacion inactivo
    LineaInvestigacion lineaInvestigacion = generarMockLineaInvestigacion(1L);
    lineaInvestigacion.setActivo(Boolean.FALSE);

    mockActivableIsActivo(LineaInvestigacion.class, null);

    BDDMockito.given(lineaInvestigacionRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(lineaInvestigacion));
    BDDMockito.given(lineaInvestigacionRepository.findByNombreAndActivoIsTrue(lineaInvestigacion.getNombre()))
        .willReturn(Optional.empty());
    BDDMockito.given(lineaInvestigacionRepository.save(ArgumentMatchers.<LineaInvestigacion>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: activamos el LineaInvestigacion
    LineaInvestigacion lineaInvestigacionActualizado = lineaInvestigacionService.enable(lineaInvestigacion.getId());

    // then: El LineaInvestigacion se activa correctamente.
    Assertions.assertThat(lineaInvestigacionActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(lineaInvestigacionActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(lineaInvestigacionActualizado.getNombre()).as("getNombre()")
        .isEqualTo(lineaInvestigacion.getNombre());
    Assertions.assertThat(lineaInvestigacionActualizado.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);

  }

  @Test
  void enable_WithIdNotExist_ThrowsTipoFinanciacionNotFoundException() {
    // given: Un id de un LineaInvestigacion que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(lineaInvestigacionRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el LineaInvestigacion
    // then: Lanza una excepcion porque el LineaInvestigacion no existe
    Assertions.assertThatThrownBy(() -> lineaInvestigacionService.enable(idNoExiste))
        .isInstanceOf(LineaInvestigacionNotFoundException.class);
  }

  @Test
  void enable_WithDuplicatedNombre_ThrowsValidationException() {
    // given: Un LineaInvestigacion inactivo con nombre existente
    LineaInvestigacion lineaInvestigacionExistente = generarMockLineaInvestigacion(2L);
    LineaInvestigacion lineaInvestigacion = generarMockLineaInvestigacion(1L);
    lineaInvestigacion.setActivo(Boolean.FALSE);

    mockActivableIsActivo(LineaInvestigacion.class, null);
    BDDMockito.given(lineaInvestigacionRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(lineaInvestigacion));
    BDDMockito.given(lineaInvestigacionRepository.findByNombreAndActivoIsTrue(ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(lineaInvestigacionExistente));

    // when: activamos el LineaInvestigacion
    // then: Lanza una excepcion porque el LineaInvestigacion no existe
    Assertions.assertThatThrownBy(() -> lineaInvestigacionService.enable(1L))
        .isInstanceOf(ValidationException.class);

  }

  @Test
  void disable_ReturnsLineaInvestigacion() {
    // given: Un nuevo LineaInvestigacion activo
    LineaInvestigacion lineaInvestigacion = generarMockLineaInvestigacion(1L);

    BDDMockito.given(lineaInvestigacionRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(lineaInvestigacion));
    BDDMockito.given(lineaInvestigacionRepository.save(ArgumentMatchers.<LineaInvestigacion>any()))
        .willAnswer(new Answer<LineaInvestigacion>() {
          @Override
          public LineaInvestigacion answer(InvocationOnMock invocation) throws Throwable {
            LineaInvestigacion givenData = invocation.getArgument(0, LineaInvestigacion.class);
            givenData.setActivo(Boolean.FALSE);
            return givenData;
          }
        });

    // when: Desactivamos el LineaInvestigacion
    LineaInvestigacion lineaInvestigacionActualizado = lineaInvestigacionService.disable(lineaInvestigacion.getId());

    // then: El LineaInvestigacion se desactiva correctamente.
    Assertions.assertThat(lineaInvestigacionActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(lineaInvestigacionActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(lineaInvestigacionActualizado.getNombre()).as("getNombre()")
        .isEqualTo(lineaInvestigacion.getNombre());
    Assertions.assertThat(lineaInvestigacionActualizado.getActivo()).as("getActivo()").isFalse();

  }

  @Test
  void disable_WithIdNotExist_ThrowsLineaInvestigacionNotFoundException() {
    // given: Un id de un LineaInvestigacion que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(lineaInvestigacionRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el LineaInvestigacion
    // then: Lanza una excepcion porque el LineaInvestigacion no existe
    Assertions.assertThatThrownBy(() -> lineaInvestigacionService.disable(idNoExiste))
        .isInstanceOf(LineaInvestigacionNotFoundException.class);
  }

  @Test
  void findAll_WithPaging_ReturnsPage() {
    // given: One hundred LineaInvestigacions
    List<LineaInvestigacion> lineaInvestigacionList = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      lineaInvestigacionList
          .add(generarMockLineaInvestigacion(Long.valueOf(i), "LineaInvestigacion" + String.format("%03d", i)));
    }

    BDDMockito.given(
        lineaInvestigacionRepository.findAll(ArgumentMatchers.<Specification<LineaInvestigacion>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<LineaInvestigacion>>() {
          @Override
          public Page<LineaInvestigacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<LineaInvestigacion> content = lineaInvestigacionList.subList(fromIndex, toIndex);
            Page<LineaInvestigacion> page = new PageImpl<>(content, pageable, lineaInvestigacionList.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<LineaInvestigacion> page = lineaInvestigacionService.findAll(null, paging);

    // then: A Page with ten LineaInvestigaciones are returned containing
    // descripcion='LineaInvestigacion031' to 'LineaInvestigacion040'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page).hasSize(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      LineaInvestigacion lineaInvestigacion = page.getContent().get(i);
      Assertions.assertThat(lineaInvestigacion.getNombre()).isEqualTo("LineaInvestigacion" + String.format("%03d", j));
    }
  }

  @Test
  void findAllTodos_WithPaging_ReturnsPage() {
    // given: One hundred LineaInvestigacions
    List<LineaInvestigacion> lineaInvestigacionList = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      lineaInvestigacionList
          .add(generarMockLineaInvestigacion(Long.valueOf(i), "LineaInvestigacion" + String.format("%03d", i)));
    }

    BDDMockito.given(
        lineaInvestigacionRepository.findAll(ArgumentMatchers.<Specification<LineaInvestigacion>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<LineaInvestigacion>>() {
          @Override
          public Page<LineaInvestigacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<LineaInvestigacion> content = lineaInvestigacionList.subList(fromIndex, toIndex);
            Page<LineaInvestigacion> page = new PageImpl<>(content, pageable, lineaInvestigacionList.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<LineaInvestigacion> page = lineaInvestigacionService.findAllTodos(null, paging);

    // then: A Page with ten LineaInvestigaciones are returned containing
    // descripcion='LineaInvestigacion031' to 'LineaInvestigacion040'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page).hasSize(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      LineaInvestigacion lineaInvestigacion = page.getContent().get(i);
      Assertions.assertThat(lineaInvestigacion.getNombre()).isEqualTo("LineaInvestigacion" + String.format("%03d", j));
    }
  }

  @Test
  void create_WithDuplicatedNombre_ThrowsValidationException() {
    // given: a LineaInvestigacion with duplicated nombre

    LineaInvestigacion lineaInvestigacion = generarMockLineaInvestigacion(1L);
    LineaInvestigacion newLineaInvestigacion = new LineaInvestigacion();
    BeanUtils.copyProperties(lineaInvestigacion, newLineaInvestigacion);
    newLineaInvestigacion.setId(null);

    mockActivableIsActivo(LineaInvestigacion.class, lineaInvestigacion);
    BDDMockito.given(lineaInvestigacionRepository.findByNombreAndActivoIsTrue(
        newLineaInvestigacion.getNombre()))
        .willReturn(Optional.of(lineaInvestigacion));

    Assertions.assertThatThrownBy(
        // when: create LineaInvestigacion
        () -> lineaInvestigacionService.create(
            newLineaInvestigacion))
        // then: throw exception as Nombre already exists
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void update_WithDuplicatedNombre_ThrowsValidationException() {
    // given: Un nuevo LineaInvestigacion con un nombre que ya existe
    LineaInvestigacion lineaInvestigacionUpdated = generarMockLineaInvestigacion(1L, "nombreRepetido");
    LineaInvestigacion lineaInvestigacion = generarMockLineaInvestigacion(2L, "nombreRepetido");

    mockActivableIsActivo(LineaInvestigacion.class, lineaInvestigacion);

    BDDMockito.given(lineaInvestigacionRepository.findByNombreAndActivoIsTrue(lineaInvestigacionUpdated.getNombre()))
        .willReturn(Optional.of(lineaInvestigacion));

    // when: Actualizamos el LineaInvestigacion
    // then: Lanza una excepcion porque ya existe otro LineaInvestigacion con ese
    // nombre
    Assertions.assertThatThrownBy(() -> lineaInvestigacionService.update(lineaInvestigacionUpdated))
        .isInstanceOf(ValidationException.class);
  }

  /**
   * Función que devuelve un objeto LineaInvestigacion
   * 
   * @param id id del LineaInvestigacion
   * @return el objeto LineaInvestigacion
   */
  LineaInvestigacion generarMockLineaInvestigacion(Long id) {
    return generarMockLineaInvestigacion(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto LineaInvestigacion
   * 
   * @param id id del LineaInvestigacion
   * @return el objeto LineaInvestigacion
   */
  LineaInvestigacion generarMockLineaInvestigacion(Long id, String nombre) {
    LineaInvestigacion lineaInvestigacion = new LineaInvestigacion();
    lineaInvestigacion.setId(id);
    lineaInvestigacion.setNombre(nombre);
    lineaInvestigacion.setActivo(Boolean.TRUE);
    return lineaInvestigacion;
  }

  private <T> void mockActivableIsActivo(Class<T> clazz, T object) {
    BDDMockito.given(persistenceUnitUtil.getIdentifier(ArgumentMatchers.any(clazz)))
        .willAnswer((InvocationOnMock invocation) -> {
          Object arg0 = invocation.getArgument(0);
          if (arg0 == null) {
            return null;
          }
          BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(arg0);
          Object id = wrapper.getPropertyValue("id");
          return id;
        });
    BDDMockito.given(entityManager.find(ArgumentMatchers.eq(clazz), ArgumentMatchers.anyLong())).willReturn(object);
  }

}

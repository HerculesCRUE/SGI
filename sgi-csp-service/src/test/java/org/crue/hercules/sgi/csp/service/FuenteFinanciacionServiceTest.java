package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.FuenteFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
import org.crue.hercules.sgi.csp.repository.FuenteFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.TipoAmbitoGeograficoRepository;
import org.crue.hercules.sgi.csp.repository.TipoOrigenFuenteFinanciacionRepository;
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
 * FuenteFinanciacionServiceTest
 */
@Import({ FuenteFinanciacionService.class })
public class FuenteFinanciacionServiceTest extends BaseServiceTest {

  @MockBean
  private FuenteFinanciacionRepository repository;

  @MockBean
  private TipoAmbitoGeograficoRepository tipoAmbitoGeograficoRepository;

  @MockBean
  private TipoOrigenFuenteFinanciacionRepository tipoOrigenFuenteFinanciacionRepository;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private FuenteFinanciacionService service;

  @Test
  public void create_ReturnsFuenteFinanciacion() {
    // given: Un nuevo FuenteFinanciacion
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(null);

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(fuenteFinanciacion.getNombre()))
        .willReturn(Optional.empty());

    BDDMockito.given(tipoAmbitoGeograficoRepository.existsByIdAndActivoIsTrue(ArgumentMatchers.anyLong()))
        .willReturn(true);
    BDDMockito.given(tipoAmbitoGeograficoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(fuenteFinanciacion.getTipoAmbitoGeografico()));

    BDDMockito.given(tipoOrigenFuenteFinanciacionRepository.existsByIdAndActivoIsTrue(ArgumentMatchers.anyLong()))
        .willReturn(true);
    BDDMockito.given(tipoOrigenFuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(fuenteFinanciacion.getTipoOrigenFuenteFinanciacion()));

    BDDMockito.given(repository.save(fuenteFinanciacion)).will((InvocationOnMock invocation) -> {
      FuenteFinanciacion fuenteFinanciacionCreado = invocation.getArgument(0);
      fuenteFinanciacionCreado.setId(1L);
      return fuenteFinanciacionCreado;
    });

    // when: Creamos el FuenteFinanciacion
    FuenteFinanciacion fuenteFinanciacionCreado = service.create(fuenteFinanciacion);

    // then: El FuenteFinanciacion se crea correctamente
    Assertions.assertThat(fuenteFinanciacionCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(fuenteFinanciacionCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(fuenteFinanciacionCreado.getNombre()).as("getNombre")
        .isEqualTo(fuenteFinanciacion.getNombre());
    Assertions.assertThat(fuenteFinanciacionCreado.getActivo()).as("getActivo")
        .isEqualTo(fuenteFinanciacion.getActivo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo FuenteFinanciacion que ya tiene id
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(1L);

    BDDMockito.given(tipoAmbitoGeograficoRepository.existsByIdAndActivoIsTrue(ArgumentMatchers.anyLong()))
        .willReturn(true);
    BDDMockito.given(tipoOrigenFuenteFinanciacionRepository.existsByIdAndActivoIsTrue(ArgumentMatchers.anyLong()))
        .willReturn(true);

    // when: Creamos el FuenteFinanciacion
    // then: Lanza una excepcion porque el FuenteFinanciacion ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(fuenteFinanciacion))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithoutTipoAmbitoGeograficoId_ThrowsConstraintViolationException() {
    // given: Un nuevo FuenteFinanciacion
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(null);
    fuenteFinanciacion.getTipoAmbitoGeografico().setId(null);

    // when: Creamos el FuenteFinanciacion
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(fuenteFinanciacion))
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  public void create_WithoutTipoOrigenFuenteFinanciacionId_ThrowsConstraintViolationException() {
    // given: Un nuevo FuenteFinanciacion
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(null);
    fuenteFinanciacion.getTipoOrigenFuenteFinanciacion().setId(null);

    // when: Creamos el FuenteFinanciacion
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(fuenteFinanciacion))
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  public void create_WithDuplicatedNombre_ThrowsConstraintViolationException() {
    // given: Un nuevo FuenteFinanciacion con un nombre que ya existe
    FuenteFinanciacion fuenteFinanciacionNew = generarMockFuenteFinanciacion(null, "nombreRepetido");
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(1L, "nombreRepetido");

    BDDMockito.given(tipoAmbitoGeograficoRepository.existsByIdAndActivoIsTrue(ArgumentMatchers.anyLong()))
        .willReturn(true);
    BDDMockito.given(tipoOrigenFuenteFinanciacionRepository.existsByIdAndActivoIsTrue(ArgumentMatchers.anyLong()))
        .willReturn(true);
    BDDMockito.given(repository.findByNombreAndActivoIsTrue(fuenteFinanciacionNew.getNombre()))
        .willReturn(Optional.of(fuenteFinanciacion));

    // when: Creamos el FuenteFinanciacion
    // then: Lanza una excepcion porque hay otro FuenteFinanciacion con ese nombre
    Assertions.assertThatThrownBy(() -> service.create(fuenteFinanciacionNew))
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  public void create_WithNoActivoTipoAmbitoGeografico_ThrowsConstraintViolationException() {
    // given: Un nuevo FuenteFinanciacion
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(null);

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(fuenteFinanciacion.getNombre()))
        .willReturn(Optional.empty());

    BDDMockito.given(tipoAmbitoGeograficoRepository.existsByIdAndActivoIsTrue(ArgumentMatchers.anyLong()))
        .willReturn(false);
    BDDMockito.given(tipoOrigenFuenteFinanciacionRepository.existsByIdAndActivoIsTrue(ArgumentMatchers.anyLong()))
        .willReturn(true);

    // when: Creamos el FuenteFinanciacion
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(fuenteFinanciacion))
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  public void create_WithNoActivoTipoOrigenFuenteFinanciacion_ThrowsConstraintViolationException() {
    // given: Un nuevo FuenteFinanciacion
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(null);

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(fuenteFinanciacion.getNombre()))
        .willReturn(Optional.empty());

    BDDMockito.given(tipoAmbitoGeograficoRepository.existsByIdAndActivoIsTrue(ArgumentMatchers.anyLong()))
        .willReturn(true);
    BDDMockito.given(tipoOrigenFuenteFinanciacionRepository.existsByIdAndActivoIsTrue(ArgumentMatchers.anyLong()))
        .willReturn(false);

    // when: Creamos el FuenteFinanciacion
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(fuenteFinanciacion))
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  public void update_ReturnsFuenteFinanciacion() {
    // given: Un nuevo FuenteFinanciacion con el nombre actualizado
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(1L);
    FuenteFinanciacion fuenteFinanciacionNombreActualizado = generarMockFuenteFinanciacion(1L, "NombreActualizado");

    BDDMockito.given(repository.existsByIdAndActivoIsTrue(fuenteFinanciacion.getId())).willReturn(true);
    BDDMockito.given(repository.findByNombreAndActivoIsTrue(fuenteFinanciacionNombreActualizado.getNombre()))
        .willReturn(Optional.empty());

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(fuenteFinanciacion));
    BDDMockito.given(repository.save(ArgumentMatchers.<FuenteFinanciacion>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el FuenteFinanciacion
    FuenteFinanciacion fuenteFinanciacionActualizado = service.update(fuenteFinanciacionNombreActualizado);

    // then: El FuenteFinanciacion se actualiza correctamente.
    Assertions.assertThat(fuenteFinanciacionActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(fuenteFinanciacionActualizado.getId()).as("getId()").isEqualTo(fuenteFinanciacion.getId());
    Assertions.assertThat(fuenteFinanciacionActualizado.getNombre()).as("getNombre()")
        .isEqualTo(fuenteFinanciacionNombreActualizado.getNombre());
    Assertions.assertThat(fuenteFinanciacionActualizado.getActivo()).as("getActivo()")
        .isEqualTo(fuenteFinanciacion.getActivo());
  }

  @Test
  public void update_NoActivo_ThrowsConstraintViolationException() {
    // given: Un FuenteFinanciacion no activo
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(1L, "FuenteFinanciacion");

    BDDMockito.given(repository.existsByIdAndActivoIsTrue(fuenteFinanciacion.getId())).willReturn(false);
    BDDMockito.given(repository.findByNombreAndActivoIsTrue(fuenteFinanciacion.getNombre()))
        .willReturn(Optional.empty());

    // when: Actualizamos el FuenteFinanciacion
    // then: Lanza una excepcion porque el FuenteFinanciacion no existe
    Assertions.assertThatThrownBy(() -> service.update(fuenteFinanciacion))
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  public void update_WithDuplicatedNombre_ThrowsConstraintViolationException() {
    // given: Un FuenteFinanciacion actualizado con un nombre que ya existe
    FuenteFinanciacion fuenteFinanciacionActualizado = generarMockFuenteFinanciacion(1L, "nombreRepetido");
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(2L, "nombreRepetido");

    BDDMockito.given(repository.existsByIdAndActivoIsTrue(fuenteFinanciacion.getId())).willReturn(true);
    BDDMockito.given(repository.findByNombreAndActivoIsTrue(fuenteFinanciacionActualizado.getNombre()))
        .willReturn(Optional.of(fuenteFinanciacion));

    // when: Actualizamos el FuenteFinanciacion
    // then: Lanza una excepcion porque hay otro FuenteFinanciacion con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(fuenteFinanciacionActualizado))
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  public void update_WithoutTipoAmbitoGeografico_ThrowsIllegalArgumentExceptionException() {
    // given: Un FuenteFinanciacion actualizado
    FuenteFinanciacion fuenteFinanciacionExistente = generarMockFuenteFinanciacion(1L);
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(1L);
    fuenteFinanciacion.getTipoAmbitoGeografico().setId(null);

    BDDMockito.given(repository.findById(fuenteFinanciacionExistente.getId()))
        .willReturn(Optional.of(fuenteFinanciacionExistente));
    BDDMockito.given(repository.existsByIdAndActivoIsTrue(fuenteFinanciacion.getId())).willReturn(true);

    // when: Actualizamos el FuenteFinanciacion
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(fuenteFinanciacion))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithoutTipoOrigenFuenteFinanciacion_ThrowsIllegalArgumentExceptionException() {
    // given: Un FuenteFinanciacion actualizado
    FuenteFinanciacion fuenteFinanciacionExistente = generarMockFuenteFinanciacion(1L);
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(1L);
    fuenteFinanciacion.getTipoOrigenFuenteFinanciacion().setId(null);

    BDDMockito.given(repository.findById(fuenteFinanciacionExistente.getId()))
        .willReturn(Optional.of(fuenteFinanciacionExistente));
    BDDMockito.given(repository.existsByIdAndActivoIsTrue(fuenteFinanciacion.getId())).willReturn(true);

    // when: Actualizamos el FuenteFinanciacion
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(fuenteFinanciacion))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithNoActivoTipoAmbitoGeografico_ThrowsConstraintViolationException() {
    // given: Un FuenteFinanciacion actualizado
    FuenteFinanciacion fuenteFinanciacionExistente = generarMockFuenteFinanciacion(1L);
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(1L);
    fuenteFinanciacion.getTipoAmbitoGeografico().setId(2L);

    BDDMockito.given(tipoAmbitoGeograficoRepository.existsByIdAndActivoIsTrue(ArgumentMatchers.anyLong()))
        .willReturn(false);
    BDDMockito.given(repository.findById(fuenteFinanciacionExistente.getId()))
        .willReturn(Optional.of(fuenteFinanciacionExistente));
    BDDMockito.given(repository.existsByIdAndActivoIsTrue(fuenteFinanciacion.getId())).willReturn(true);

    // when: Actualizamos el FuenteFinanciacion
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(fuenteFinanciacion))
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  public void update_NoActivaFuenteFinanciacion_ThrowsConstraintViolationException() {
    // given: Un FuenteFinanciacion actualizado
    Long id = 1L;
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(id);

    BDDMockito.given(repository.existsByIdAndActivoIsTrue(id)).willReturn(false);

    // when: Actualizamos el FuenteFinanciacion
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(fuenteFinanciacion))
        .isInstanceOf(ConstraintViolationException.class).hasMessageContaining("The Financial Source is not active");
  }

  @Test
  public void enable_ReturnsFuenteFinanciacion() {
    // given: Un FuenteFinanciacion inactivo existente
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(1L);
    fuenteFinanciacion.setActivo(false);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(fuenteFinanciacion));
    BDDMockito.given(repository.findByNombreAndActivoIsTrue(fuenteFinanciacion.getNombre()))
        .willReturn(Optional.empty());
    BDDMockito.given(repository.save(ArgumentMatchers.<FuenteFinanciacion>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Activamos el FuenteFinanciacion
    FuenteFinanciacion fuenteFinanciacionActualizado = service.activar(fuenteFinanciacion.getId());

    // then: El FuenteFinanciacion se activa correctamente.
    Assertions.assertThat(fuenteFinanciacionActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(fuenteFinanciacionActualizado.getId()).as("getId()").isEqualTo(fuenteFinanciacion.getId());
    Assertions.assertThat(fuenteFinanciacionActualizado.getNombre()).as("getNombre()")
        .isEqualTo(fuenteFinanciacion.getNombre());
    Assertions.assertThat(fuenteFinanciacionActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(fuenteFinanciacion.getDescripcion());
    Assertions.assertThat(fuenteFinanciacionActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void enable_WithDuplicatedNombre_ThrowsConstraintViolationException() {
    // given: Un FuenteFinanciacion inactivo con un nombre que ya existe activo
    Long id = 1L;
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(id, "nombreRepetido");
    fuenteFinanciacion.setActivo(false);
    FuenteFinanciacion fuenteFinanciacionRepetido = generarMockFuenteFinanciacion(2L, "nombreRepetido");

    BDDMockito.given(repository.findById(fuenteFinanciacion.getId())).willReturn(Optional.of(fuenteFinanciacion));

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(fuenteFinanciacion.getNombre()))
        .willReturn(Optional.of(fuenteFinanciacionRepetido));

    // when: Activamos el FuenteFinanciacion
    // then: Lanza una excepcion porque hay otro FuenteFinanciacion con ese nombre
    Assertions.assertThatThrownBy(() -> service.activar(id)).isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("A Financial Source with name 'nombreRepetido' already exists");
  }

  @Test
  public void enable_WithIdNotExist_ThrowsFuenteFinanciacionNotFoundException() {
    // given: Un id de un FuenteFinanciacion que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el FuenteFinanciacion
    // then: Lanza una excepcion porque el FuenteFinanciacion no existe
    Assertions.assertThatThrownBy(() -> service.activar(idNoExiste))
        .isInstanceOf(FuenteFinanciacionNotFoundException.class);
  }

  @Test
  public void disable_ReturnsFuenteFinanciacion() {
    // given: Un nuevo FuenteFinanciacion activo
    FuenteFinanciacion fuenteFinanciacion = generarMockFuenteFinanciacion(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(fuenteFinanciacion));
    BDDMockito.given(repository.save(ArgumentMatchers.<FuenteFinanciacion>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el FuenteFinanciacion
    FuenteFinanciacion fuenteFinanciacionActualizado = service.desactivar(fuenteFinanciacion.getId());

    // then: El FuenteFinanciacion se desactiva correctamente.
    Assertions.assertThat(fuenteFinanciacionActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(fuenteFinanciacionActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(fuenteFinanciacionActualizado.getNombre()).as("getNombre()")
        .isEqualTo(fuenteFinanciacion.getNombre());
    Assertions.assertThat(fuenteFinanciacionActualizado.getActivo()).as("getActivo()").isEqualTo(false);

  }

  @Test
  public void disable_WithIdNotExist_ThrowsFuenteFinanciacionNotFoundException() {
    // given: Un id de un FuenteFinanciacion que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el FuenteFinanciacion
    // then: Lanza una excepcion porque el FuenteFinanciacion no existe
    Assertions.assertThatThrownBy(() -> service.desactivar(idNoExiste))
        .isInstanceOf(FuenteFinanciacionNotFoundException.class);
  }

  @Test
  public void findActivos_ReturnsPage() {
    // given: Una lista con 37 FuenteFinanciacion
    List<FuenteFinanciacion> fuenteFinanciaciones = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      fuenteFinanciaciones.add(generarMockFuenteFinanciacion(i, "FuenteFinanciacion" + String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<FuenteFinanciacion>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<FuenteFinanciacion>>() {
          @Override
          public Page<FuenteFinanciacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > fuenteFinanciaciones.size() ? fuenteFinanciaciones.size() : toIndex;
            List<FuenteFinanciacion> content = fuenteFinanciaciones.subList(fromIndex, toIndex);
            Page<FuenteFinanciacion> page = new PageImpl<>(content, pageable, fuenteFinanciaciones.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<FuenteFinanciacion> page = service.findActivos(null, paging);

    // then: Devuelve la pagina 3 con los FuenteFinanciacion del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      FuenteFinanciacion fuenteFinanciacion = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(fuenteFinanciacion.getNombre()).isEqualTo("FuenteFinanciacion" + String.format("%03d", i));
    }
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 FuenteFinanciacion
    List<FuenteFinanciacion> fuenteFinanciaciones = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      fuenteFinanciaciones.add(generarMockFuenteFinanciacion(i, "FuenteFinanciacion" + String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<FuenteFinanciacion>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<FuenteFinanciacion>>() {
          @Override
          public Page<FuenteFinanciacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > fuenteFinanciaciones.size() ? fuenteFinanciaciones.size() : toIndex;
            List<FuenteFinanciacion> content = fuenteFinanciaciones.subList(fromIndex, toIndex);
            Page<FuenteFinanciacion> page = new PageImpl<>(content, pageable, fuenteFinanciaciones.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<FuenteFinanciacion> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los FuenteFinanciacion del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      FuenteFinanciacion fuenteFinanciacion = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(fuenteFinanciacion.getNombre()).isEqualTo("FuenteFinanciacion" + String.format("%03d", i));
    }
  }

  @Test
  public void findById_ReturnsFuenteFinanciacion() {
    // given: Un FuenteFinanciacion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockFuenteFinanciacion(idBuscado)));

    // when: Buscamos el FuenteFinanciacion por su id
    FuenteFinanciacion fuenteFinanciacion = service.findById(idBuscado);

    // then: el FuenteFinanciacion
    Assertions.assertThat(fuenteFinanciacion).as("isNotNull()").isNotNull();
    Assertions.assertThat(fuenteFinanciacion.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(fuenteFinanciacion.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(fuenteFinanciacion.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsFuenteFinanciacionNotFoundException() throws Exception {
    // given: Ningun FuenteFinanciacion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el FuenteFinanciacion por su id
    // then: lanza un FuenteFinanciacionNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(FuenteFinanciacionNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto FuenteFinanciacion
   * 
   * @param id id del FuenteFinanciacion
   * @return el objeto FuenteFinanciacion
   */
  private FuenteFinanciacion generarMockFuenteFinanciacion(Long id) {
    return generarMockFuenteFinanciacion(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto FuenteFinanciacion
   * 
   * @param id     id del FuenteFinanciacion
   * @param nombre nombre del FuenteFinanciacion
   * @return el objeto FuenteFinanciacion
   */
  private FuenteFinanciacion generarMockFuenteFinanciacion(Long id, String nombre) {
    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(1L);
    tipoAmbitoGeografico.setActivo(true);

    TipoOrigenFuenteFinanciacion tipoOrigenFuenteFinanciacion = new TipoOrigenFuenteFinanciacion();
    tipoOrigenFuenteFinanciacion.setId(1L);
    tipoOrigenFuenteFinanciacion.setActivo(true);

    FuenteFinanciacion fuenteFinanciacion = new FuenteFinanciacion();
    fuenteFinanciacion.setId(id);
    fuenteFinanciacion.setNombre(nombre);
    fuenteFinanciacion.setDescripcion("descripcion-" + id);
    fuenteFinanciacion.setFondoEstructural(true);
    fuenteFinanciacion.setTipoAmbitoGeografico(tipoAmbitoGeografico);
    fuenteFinanciacion.setTipoOrigenFuenteFinanciacion(tipoOrigenFuenteFinanciacion);
    fuenteFinanciacion.setActivo(true);

    return fuenteFinanciacion;
  }

}

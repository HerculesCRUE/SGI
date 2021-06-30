package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.AreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.repository.AreaTematicaRepository;
import org.crue.hercules.sgi.csp.service.impl.AreaTematicaServiceImpl;
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
 * AreaTematicaServiceTest
 */
public class AreaTematicaServiceTest extends BaseServiceTest {

  @Mock
  private AreaTematicaRepository repository;

  private AreaTematicaService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new AreaTematicaServiceImpl(repository);
  }

  @Test
  public void create_ReturnsAreaTematica() {
    // given: Un nuevo AreaTematica (grupo)
    AreaTematica areaTematica = generarMockAreaTematica(null, "nombre-1", "descripcion-1", null);

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<AreaTematica>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<AreaTematica> page = new PageImpl<>(new ArrayList<AreaTematica>(), pageable, 0);
          return page;
        });

    BDDMockito.given(repository.save(areaTematica)).will((InvocationOnMock invocation) -> {
      AreaTematica areaTematicaCreado = invocation.getArgument(0);
      areaTematicaCreado.setId(1L);
      return areaTematicaCreado;
    });

    // when: Creamos el AreaTematica
    AreaTematica areaTematicaCreado = service.create(areaTematica);

    // then: El AreaTematica (grupo) se crea correctamente
    Assertions.assertThat(areaTematicaCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(areaTematicaCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(areaTematicaCreado.getNombre()).as("getNombre").isEqualTo(areaTematica.getNombre());
    Assertions.assertThat(areaTematicaCreado.getDescripcion()).as("getDescripcion")
        .isEqualTo(areaTematica.getDescripcion());
    Assertions.assertThat(areaTematicaCreado.getActivo()).as("getActivo").isEqualTo(areaTematica.getActivo());
  }

  @Test
  public void create_WithPadre_ReturnsAreaTematica() {
    // given: Un nuevo AreaTematica
    AreaTematica areaTematica = generarMockAreaTematica(null, "A-002", "descripcion-2", 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockAreaTematica(1L)));
    BDDMockito.given(repository.findByPadreIdInAndActivoIsTrue(ArgumentMatchers.<Long>anyList()))
        .willReturn(new ArrayList<>());

    BDDMockito.given(repository.save(areaTematica)).will((InvocationOnMock invocation) -> {
      AreaTematica areaTematicaCreado = invocation.getArgument(0);
      areaTematicaCreado.setId(1L);
      return areaTematicaCreado;
    });

    // when: Creamos el AreaTematica
    AreaTematica areaTematicaCreado = service.create(areaTematica);

    // then: El AreaTematica se crea correctamente
    Assertions.assertThat(areaTematicaCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(areaTematicaCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(areaTematicaCreado.getNombre()).as("getNombre").isEqualTo(areaTematica.getNombre());
    Assertions.assertThat(areaTematicaCreado.getDescripcion()).as("getDescripcion")
        .isEqualTo(areaTematica.getDescripcion());
    Assertions.assertThat(areaTematicaCreado.getActivo()).as("getActivo").isEqualTo(areaTematica.getActivo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo AreaTematica que ya tiene id
    AreaTematica areaTematica = generarMockAreaTematica(1L);

    // when: Creamos el AreaTematica
    // then: Lanza una excepcion porque el AreaTematica ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(areaTematica)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("AreaTematica id tiene que ser null para crear un nuevo AreaTematica");
  }

  @Test
  public void create_WithNoExistingPadre_ThrowsAreaTematicaNotFoundException() {
    // given: Un nuevo AreaTematica con un padre que no existe
    AreaTematica areaTematica = generarMockAreaTematica(null, "A-002", "descripcion-2", 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Creamos el AreaTematica
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(areaTematica)).isInstanceOf(AreaTematicaNotFoundException.class);
  }

  @Test
  public void create_GrupoWithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo AreaTematica (grupo) con un nombre que ya existe en otro
    // grupo
    AreaTematica areaTematicaNew = generarMockAreaTematica(null, "nombreRepetido", "descripcion-2", null);
    AreaTematica areaTematica = generarMockAreaTematica(1L, "nombreRepetido", "descripcion-1", null);

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<AreaTematica>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<AreaTematica> page = new PageImpl<>(Arrays.asList(areaTematica), pageable, 0);
          return page;
        });

    // when: Creamos el AreaTematica
    // then: Lanza una excepcion porque hay otro grupo AreaTematica con ese nombre
    Assertions.assertThatThrownBy(() -> service.create(areaTematicaNew)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un grupo con el mismo nombre");
  }

  @Test
  public void create_AreaTematicaWithPadreDisabled_ThrowsIllegalArgumentException() {
    // given: nueva AreaTematica cuyo padre está desactivado
    // nombre(back) ==> abreviatura(front)
    AreaTematica areaTematicaNew = generarMockAreaTematica(null, "A-002", "descripcion-2", 1L);
    AreaTematica areaTematica = generarMockAreaTematica(1L, "nombrePadre", "descripcionPadre", null);
    areaTematica.setActivo(Boolean.FALSE);

    BDDMockito.given(repository.findById(1L)).willReturn(Optional.of(areaTematica));

    // when: Creamos el AreaTematica
    // then: Lanza una excepcion porque el padre no está activo
    Assertions.assertThatThrownBy(() -> service.create(areaTematicaNew)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("AreaTematica padre '%s' está desactivada", areaTematica.getNombre());
  }

  @Test
  public void create_AreaTematicaWithNombreLengthGreaterThan5_ThrowsIllegalArgumentException() {
    // given: Un nuevo AreaTematica cuyo nombre supera 5 caracteres
    // nombre(back) ==> abreviatura(front)
    AreaTematica areaTematicaNew = generarMockAreaTematica(null, "nombre", "descripcion-1", 1L);
    AreaTematica areaTematica = generarMockAreaTematica(1L, "nombrePadre", "descripcionPadre", null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(areaTematica));

    // when: Creamos el AreaTematica
    // then: Lanza una excepcion porque excede la longitud máxima permitida
    Assertions.assertThatThrownBy(() -> service.create(areaTematicaNew)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Se ha superado la longitud máxima permitida para la abreviatura de AreaTematica (5)");
  }

  @Test
  public void create_AreaTematicaWithDescripcionLengthGreaterThan50_ThrowsIllegalArgumentException() {
    // given: Un nuevo AreaTematica cuya descripcion supera 50 caracteres
    // descripcion(back) ==> nombre(front)
    AreaTematica areaTematicaNew = generarMockAreaTematica(null, "A-001",
        "descripcion que supera la longitud máxima permitida para el nombre de AreaTematica (50)", 1L);
    AreaTematica areaTematica = generarMockAreaTematica(1L, "nombrePadre", "descripcionPadre", null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(areaTematica));

    // when: Creamos el AreaTematica
    // then: Lanza una excepcion porque excede la longitud máxima permitida
    Assertions.assertThatThrownBy(() -> service.create(areaTematicaNew)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Se ha superado la longitud máxima permitida para el nombre de AreaTematica (50)");
  }

  @Test
  public void create_AreaTematicaWithoutDescripcion_ThrowsIllegalArgumentException() {
    // given: Un nuevo AreaTematica sin descripción
    // descripcion(back) ==> nombre(front)
    AreaTematica areaTematicaNew = generarMockAreaTematica(null, "A-001", null, 1L);
    AreaTematica areaTematica = generarMockAreaTematica(1L, "nombrePadre", "descripcionPadre", null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(areaTematica));

    // when: Creamos el AreaTematica
    // then: Lanza una excepcion porque descripcion es obligatoria
    Assertions.assertThatThrownBy(() -> service.create(areaTematicaNew)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El nombre de AreaTematica es un campo obligatorio");
  }

  @Test
  public void create_AreaTematicaWithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo AreaTematica con un nombre que ya existe
    AreaTematica areaTematicaNew = generarMockAreaTematica(null, "A-002", "descripcion-3", 1L);
    AreaTematica areaTematica = generarMockAreaTematica(1L, "nombreRepetidoPadre", "descripcion-1", null);
    AreaTematica areaTematicaHijo = generarMockAreaTematica(2L, "A-002", "descripcion-2", 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(areaTematica));
    BDDMockito.given(repository.findByPadreIdInAndActivoIsTrue(ArgumentMatchers.<Long>anyList()))
        .willReturn(Arrays.asList(areaTematicaHijo));

    // when: Creamos el AreaTematica
    // then: Lanza una excepcion porque hay otro AreaTematica con ese nombre
    Assertions.assertThatThrownBy(() -> service.create(areaTematicaNew)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un AreaTematica con la misma abreviatura en el grupo");
  }

  @Test
  public void create_AreaTematicaWithDuplicatedDescripcion_ThrowsIllegalArgumentException() {
    // given: Un nuevo AreaTematica con una descripcion que ya existe
    AreaTematica areaTematicaNew = generarMockAreaTematica(null, "A-003", "descripcionRepetida", 1L);
    AreaTematica areaTematica = generarMockAreaTematica(1L, "nombrePadre", "descripcionPadre", null);
    AreaTematica areaTematicaHijo = generarMockAreaTematica(2L, "A-002", "descripcionRepetida", 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(areaTematica));
    // se llama dos veces, primero para el nombre y luego la descripción
    BDDMockito.given(repository.findByPadreIdInAndActivoIsTrue(ArgumentMatchers.<Long>anyList()))
        .willReturn(new ArrayList<>()).willReturn(Arrays.asList(areaTematicaHijo));

    // when: Creamos el AreaTematica
    // then: Lanza una excepcion porque hay otro AreaTematica con ese nombre
    Assertions.assertThatThrownBy(() -> service.create(areaTematicaNew)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un AreaTematica con el mismo nombre en el grupo");
  }

  @Test
  public void update_ReturnsAreaTematica() {
    // given: Un nuevo AreaTematica con el nombre actualizado
    AreaTematica areaTematica = generarMockAreaTematica(1L);
    AreaTematica areaTematicaNombreActualizado = generarMockAreaTematica(1L, "NombreActualizado",
        "DescripcionActualizada", null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(areaTematica));

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<AreaTematica>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<AreaTematica> page = new PageImpl<>(new ArrayList<AreaTematica>(), pageable, 0);
          return page;
        });

    BDDMockito.given(repository.save(ArgumentMatchers.<AreaTematica>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el AreaTematica
    AreaTematica areaTematicaActualizado = service.update(areaTematicaNombreActualizado);

    // then: El AreaTematica se actualiza correctamente.
    Assertions.assertThat(areaTematicaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(areaTematicaActualizado.getId()).as("getId()").isEqualTo(areaTematica.getId());
    Assertions.assertThat(areaTematicaActualizado.getNombre()).as("getNombre()")
        .isEqualTo(areaTematicaNombreActualizado.getNombre());
    Assertions.assertThat(areaTematicaActualizado.getDescripcion()).as("getDescripcion")
        .isEqualTo(areaTematica.getDescripcion());
    Assertions.assertThat(areaTematicaActualizado.getActivo()).as("getActivo()").isEqualTo(areaTematica.getActivo());
  }

  @Test
  public void update_WithPadre_ReturnsAreaTematica() {
    // given: Un nuevo AreaTematica con el nombre actualizado
    AreaTematica areaTematica = generarMockAreaTematica(2L, "A-002", "Descripcion", 1L);
    AreaTematica areaTematicaNombreActualizado = generarMockAreaTematica(2L, "B-222", "DescripcionActualizada", 1L);

    BDDMockito.given(repository.findById(2L)).willReturn(Optional.of(areaTematica));
    BDDMockito.given(repository.findById(1L)).willReturn(Optional.of(generarMockAreaTematica(1L)));
    BDDMockito.given(repository.findByPadreIdInAndActivoIsTrue(ArgumentMatchers.<Long>anyList()))
        .willReturn(new ArrayList<>());

    BDDMockito.given(repository.save(ArgumentMatchers.<AreaTematica>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el AreaTematica
    AreaTematica areaTematicaActualizado = service.update(areaTematicaNombreActualizado);

    // then: El AreaTematica se actualiza correctamente.
    Assertions.assertThat(areaTematicaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(areaTematicaActualizado.getId()).as("getId()").isEqualTo(areaTematica.getId());
    Assertions.assertThat(areaTematicaActualizado.getNombre()).as("getNombre()")
        .isEqualTo(areaTematicaNombreActualizado.getNombre());
    Assertions.assertThat(areaTematicaActualizado.getDescripcion()).as("getDescripcion")
        .isEqualTo(areaTematica.getDescripcion());
    Assertions.assertThat(areaTematicaActualizado.getActivo()).as("getActivo()").isEqualTo(areaTematica.getActivo());
  }

  @Test
  public void update_GrupoWithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un AreaTematica (grupo) actualizado con un nombre que ya existe
    AreaTematica areaTematicaActualizado = generarMockAreaTematica(1L, "nombreRepetido", "Descripcion-1", null);
    AreaTematica areaTematica = generarMockAreaTematica(2L, "nombreRepetido", "Descripcion-2", null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(areaTematicaActualizado));
    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<AreaTematica>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<AreaTematica> page = new PageImpl<>(Arrays.asList(areaTematica), pageable, 0);
          return page;
        });

    // when: Actualizamos el AreaTematica
    // then: Lanza una excepcion porque hay otro grupo AreaTematica con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(areaTematicaActualizado))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe un grupo con el mismo nombre");
  }

  @Test
  public void update_AreaTematicaWithPadreDisabled_ThrowsIllegalArgumentException() {
    // given: AreaTematica modificada cuyo padre está desactivado
    // nombre(back) ==> abreviatura(front)
    AreaTematica areaTematicaOriginal = generarMockAreaTematica(3L, "A-003", "descripcion-3", 1L);
    AreaTematica areaTematicaPadreActualizado = generarMockAreaTematica(2L, "nombrePadre2", "descripcionPadre2", null);
    AreaTematica areaTematicaActualizado = generarMockAreaTematica(3L, "A-003", "descripcion-3", 2L);
    areaTematicaPadreActualizado.setActivo(Boolean.FALSE);

    BDDMockito.given(repository.findById(2L)).willReturn(Optional.of(areaTematicaPadreActualizado));
    BDDMockito.given(repository.findById(3L)).willReturn(Optional.of(areaTematicaOriginal));

    // when: Actualizamos el AreaTematica
    // then: Lanza una excepcion porque el padre no está activo
    Assertions.assertThatThrownBy(() -> service.update(areaTematicaActualizado))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("AreaTematica padre '%s' está desactivada", areaTematicaPadreActualizado.getNombre());
  }

  @Test
  public void update_AreaTematicaWithNombreLengthGreaterThan5_ThrowsIllegalArgumentException() {
    // given: AreaTematica modificada cuyo nombre supera 5 caracteres
    // nombre(back) ==> abreviatura(front)
    AreaTematica areaTematicaActualizado = generarMockAreaTematica(3L, "nombre", "descripcion-1", 1L);
    AreaTematica areaTematica = generarMockAreaTematica(1L, "nombrePadre", "descripcionPadre", null);

    BDDMockito.given(repository.findById(3L)).willReturn(Optional.of(areaTematicaActualizado));
    BDDMockito.given(repository.findById(1L)).willReturn(Optional.of(areaTematica));

    // when: Actualizamos el AreaTematica
    // then: Lanza una excepcion porque excede la longitud máxima permitida
    Assertions.assertThatThrownBy(() -> service.update(areaTematicaActualizado))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Se ha superado la longitud máxima permitida para la abreviatura de AreaTematica (5)");
  }

  @Test
  public void update_AreaTematicaWithDescripcionLengthGreaterThan50_ThrowsIllegalArgumentException() {
    // given: AreaTematica actualizada cuya descripcion supera 50 caracteres
    // descripcion(back) ==> nombre(front)
    AreaTematica areaTematicaActualizado = generarMockAreaTematica(3L, "A-001",
        "descripcion que supera la longitud máxima permitida para el nombre de AreaTematica (50)", 1L);
    AreaTematica areaTematica = generarMockAreaTematica(1L, "nombrePadre", "descripcionPadre", null);

    BDDMockito.given(repository.findById(3L)).willReturn(Optional.of(areaTematicaActualizado));
    BDDMockito.given(repository.findById(1L)).willReturn(Optional.of(areaTematica));

    // when: Actualizamos el AreaTematica
    // then: Lanza una excepcion porque excede la longitud máxima permitida
    Assertions.assertThatThrownBy(() -> service.update(areaTematicaActualizado))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Se ha superado la longitud máxima permitida para el nombre de AreaTematica (50)");
  }

  @Test
  public void update_AreaTematicaWithoutDescripcion_ThrowsIllegalArgumentException() {
    // given: AreaTematica actualizada sin descripción
    // descripcion(back) ==> nombre(front)
    AreaTematica areaTematicaActualizado = generarMockAreaTematica(3L, "A-001", null, 1L);
    AreaTematica areaTematica = generarMockAreaTematica(1L, "nombrePadre", "descripcionPadre", null);

    BDDMockito.given(repository.findById(3L)).willReturn(Optional.of(areaTematicaActualizado));
    BDDMockito.given(repository.findById(1L)).willReturn(Optional.of(areaTematica));

    // when: Actualizamos el AreaTematica
    // then: Lanza una excepcion porque descripcion es obligatoria
    Assertions.assertThatThrownBy(() -> service.update(areaTematicaActualizado))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El nombre de AreaTematica es un campo obligatorio");
  }

  @Test
  public void update_AreaTematicaWithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un AreaTematica actualizado con un nombre que ya existe
    AreaTematica areaTematicaActualizado = generarMockAreaTematica(3L, "A-002", "Descripcion-3", 1L);
    AreaTematica areaTematica = generarMockAreaTematica(1L, "nombreRepetidoPadre", "DescripcionPadre", null);
    AreaTematica areaTematicaHijo = generarMockAreaTematica(2L, "A-002", "Descripcion-2", 1L);

    BDDMockito.given(repository.findById(3L)).willReturn(Optional.of(areaTematicaActualizado));
    BDDMockito.given(repository.findById(1L)).willReturn(Optional.of(areaTematica));

    BDDMockito.given(repository.findByPadreIdInAndActivoIsTrue(ArgumentMatchers.<Long>anyList()))
        .willReturn(Arrays.asList(areaTematicaHijo));

    // when: Actualizamos el AreaTematica
    // then: Lanza una excepcion porque hay otro AreaTematica con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(areaTematicaActualizado))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un AreaTematica con la misma abreviatura en el grupo");
  }

  @Test
  public void update_AreaTematicaWithDuplicatedDescripcion_ThrowsIllegalArgumentException() {
    // given: Un AreaTematica actualizado con una descripcion que ya existe
    AreaTematica areaTematicaActualizado = generarMockAreaTematica(3L, "A-003", "DescripcionRepetida", 1L);
    AreaTematica areaTematica = generarMockAreaTematica(1L, "nombrePadre", "DescripcionPadre", null);
    AreaTematica areaTematicaHijo = generarMockAreaTematica(2L, "A-002", "DescripcionRepetida", 1L);

    BDDMockito.given(repository.findById(3L)).willReturn(Optional.of(areaTematicaActualizado));
    BDDMockito.given(repository.findById(1L)).willReturn(Optional.of(areaTematica));

    BDDMockito.given(repository.findByPadreIdInAndActivoIsTrue(ArgumentMatchers.<Long>anyList()))
        .willReturn(new ArrayList<>()).willReturn(Arrays.asList(areaTematicaHijo));

    // when: Actualizamos el AreaTematica
    // then: Lanza una excepcion porque hay otro AreaTematica con esa descripcion
    Assertions.assertThatThrownBy(() -> service.update(areaTematicaActualizado))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un AreaTematica con el mismo nombre en el grupo");
  }

  @Test
  public void update_WithNoExistingPadre_ThrowsAreaTematicaNotFoundException() {
    // given: Un AreaTematica actualizado con un padre que no existe
    AreaTematica areaTematica = generarMockAreaTematica(2L, "A-002", "descripcion-2", 1L);

    BDDMockito.given(repository.findById(1L)).willReturn(Optional.empty());

    // when: Actualizamos el AreaTematica
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(areaTematica)).isInstanceOf(AreaTematicaNotFoundException.class);
  }

  @Test
  public void update_WithIdNotExist_ThrowsAreaTematicaNotFoundException() {
    // given: Un AreaTematica actualizado con un id que no existe
    AreaTematica areaTematica = generarMockAreaTematica(1L, "AreaTematica", "Descripcion", null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Actualizamos el AreaTematica
    // then: Lanza una excepcion porque el AreaTematica no existe
    Assertions.assertThatThrownBy(() -> service.update(areaTematica)).isInstanceOf(AreaTematicaNotFoundException.class);
  }

  @Test
  public void enable_ReturnsAreaTematica() {
    // given: Un nuevo AreaTematica inactivo
    AreaTematica areaTematica = generarMockAreaTematica(1L);
    areaTematica.setActivo(false);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(areaTematica));
    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<AreaTematica>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<AreaTematica> page = new PageImpl<>(new ArrayList<>(), pageable, 0);
          return page;
        });
    BDDMockito.given(repository.save(ArgumentMatchers.<AreaTematica>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Activamos el AreaTematica
    AreaTematica areaTematicaActualizado = service.enable(areaTematica.getId());

    // then: El FuenteFinanciacion se activa correctamente.
    Assertions.assertThat(areaTematicaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(areaTematicaActualizado.getId()).as("getId()").isEqualTo(areaTematica.getId());
    Assertions.assertThat(areaTematicaActualizado.getNombre()).as("getNombre()").isEqualTo(areaTematica.getNombre());
    Assertions.assertThat(areaTematicaActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(areaTematica.getDescripcion());
    Assertions.assertThat(areaTematicaActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void enable_GrupoWithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un AreaTematica inactivo con un nombre que ya existe activo
    AreaTematica areaTematica = generarMockAreaTematica(1L, "nombreRepetido", "descripcion-1", null);
    areaTematica.setActivo(false);
    AreaTematica areaTematicaRepetido = generarMockAreaTematica(2L, "nombreRepetido", "descripcion-2", null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(areaTematica));
    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<AreaTematica>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<AreaTematica> page = new PageImpl<>(Arrays.asList(areaTematicaRepetido), pageable, 0);
          return page;
        });

    // when: Activamos el AreaTematica
    // then: Lanza una excepcion porque hay otro AreaTematica con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(areaTematica)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un grupo con el mismo nombre");
  }

  @Test
  public void enable_WithIdNotExist_ThrowsTipoFinanciacionNotFoundException() {
    // given: Un id de un AreaTematica que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el AreaTematica
    // then: Lanza una excepcion porque el AreaTematica no existe
    Assertions.assertThatThrownBy(() -> service.enable(idNoExiste)).isInstanceOf(AreaTematicaNotFoundException.class);
  }

  @Test
  public void enable_WithPadreNotNull_ThrowsIllegalArgumentException() {
    // given: Un id de un AreaTematica desactivado que tiene padre (no es grupo)
    AreaTematica areaTematica = generarMockAreaTematica(2L, "A-002", "descripcion-002", 1L);
    areaTematica.setActivo(Boolean.FALSE);
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(areaTematica));
    // when: activamos el AreaTematica
    // then: Lanza una excepcion porque el AreaTematica tiene padre (no es grupo)
    Assertions.assertThatThrownBy(() -> service.enable(areaTematica.getId()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Solo se puede reactivar si es un grupo (AreaTematica sin padre)");
  }

  @Test
  public void disable_ReturnsAreaTematica() {
    // given: Un nuevo AreaTematica activo
    AreaTematica areaTematica = generarMockAreaTematica(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(areaTematica));

    BDDMockito.given(repository.save(ArgumentMatchers.<AreaTematica>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el AreaTematica
    AreaTematica areaTematicaActualizado = service.disable(areaTematica.getId());

    // then: El AreaTematica se desactiva correctamente
    Assertions.assertThat(areaTematicaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(areaTematicaActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(areaTematicaActualizado.getNombre()).as("getNombre()").isEqualTo(areaTematica.getNombre());
    Assertions.assertThat(areaTematicaActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(areaTematica.getDescripcion());
    Assertions.assertThat(areaTematicaActualizado.getActivo()).as("getActivo()").isEqualTo(false);
  }

  @Test
  public void disable_WithIdNotExist_ThrowsAreaTematicaNotFoundException() {
    // given: Un id de un AreaTematica que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el AreaTematica
    // then: Lanza una excepcion porque el AreaTematica no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste)).isInstanceOf(AreaTematicaNotFoundException.class);
  }

  @Test
  public void findById_ReturnsAreaTematica() {
    // given: Un AreaTematica con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockAreaTematica(idBuscado)));

    // when: Buscamos el AreaTematica por su id
    AreaTematica areaTematica = service.findById(idBuscado);

    // then: el AreaTematica
    Assertions.assertThat(areaTematica).as("isNotNull()").isNotNull();
    Assertions.assertThat(areaTematica.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(areaTematica.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(areaTematica.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsAreaTematicaNotFoundException() throws Exception {
    // given: Ningun AreaTematica con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el AreaTematica por su id
    // then: lanza un AreaTematicaNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado)).isInstanceOf(AreaTematicaNotFoundException.class);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 AreaTematica
    List<AreaTematica> areaTematicas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      areaTematicas.add(generarMockAreaTematica(i));
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<AreaTematica>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<AreaTematica>>() {
          @Override
          public Page<AreaTematica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > areaTematicas.size() ? areaTematicas.size() : toIndex;
            List<AreaTematica> content = areaTematicas.subList(fromIndex, toIndex);
            Page<AreaTematica> page = new PageImpl<>(content, pageable, areaTematicas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<AreaTematica> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los AreaTematica del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      AreaTematica areaTematica = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(areaTematica.getNombre()).isEqualTo("nombre-" + i);
    }
  }

  @Test
  public void findAllGrupo_ReturnsPage() {
    // given: Una lista con 37 AreaTematica sin padre (grupoes)
    List<AreaTematica> areaTematicas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      areaTematicas.add(generarMockAreaTematica(i));
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<AreaTematica>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<AreaTematica>>() {
          @Override
          public Page<AreaTematica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > areaTematicas.size() ? areaTematicas.size() : toIndex;
            List<AreaTematica> content = areaTematicas.subList(fromIndex, toIndex);
            Page<AreaTematica> page = new PageImpl<>(content, pageable, areaTematicas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<AreaTematica> page = service.findAllGrupo(null, paging);

    // then: Devuelve la pagina 3 con los AreaTematica del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      AreaTematica areaTematica = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(areaTematica.getNombre()).isEqualTo("nombre-" + i);
    }
  }

  @Test
  public void findAllTodosGrupo_ReturnsPage() {
    // given: Una lista con 37 AreaTematica sin padre (grupoes)
    List<AreaTematica> areaTematicas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      areaTematicas.add(generarMockAreaTematica(i));
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<AreaTematica>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<AreaTematica>>() {
          @Override
          public Page<AreaTematica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > areaTematicas.size() ? areaTematicas.size() : toIndex;
            List<AreaTematica> content = areaTematicas.subList(fromIndex, toIndex);
            Page<AreaTematica> page = new PageImpl<>(content, pageable, areaTematicas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<AreaTematica> page = service.findAllTodosGrupo(null, paging);

    // then: Devuelve la pagina 3 con los AreaTematica del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      AreaTematica areaTematica = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(areaTematica.getNombre()).isEqualTo("nombre-" + i);
    }
  }

  @Test
  public void findAllHijosAreaTematica_ReturnsPage() {
    // given: Una lista con 37 AreaTematica para un AreaTematica
    Long idPadre = 1L;
    List<AreaTematica> areaTematicas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      areaTematicas.add(generarMockAreaTematica(i));
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<AreaTematica>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<AreaTematica>>() {
          @Override
          public Page<AreaTematica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > areaTematicas.size() ? areaTematicas.size() : toIndex;
            List<AreaTematica> content = areaTematicas.subList(fromIndex, toIndex);
            Page<AreaTematica> page = new PageImpl<>(content, pageable, areaTematicas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<AreaTematica> page = service.findAllHijosAreaTematica(idPadre, null, paging);

    // then: Devuelve la pagina 3 con los TipoEnlace del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      AreaTematica areaTematica = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(areaTematica.getNombre()).isEqualTo("nombre-" + i);
    }
  }

  /**
   * Función que devuelve un objeto AreaTematica
   * 
   * @param id id del AreaTematica
   * @return el objeto AreaTematica
   */
  private AreaTematica generarMockAreaTematica(Long id) {
    return generarMockAreaTematica(id, "nombre-" + id, "descripcion-" + id, null);
  }

  /**
   * Función que devuelve un objeto AreaTematica
   * 
   * @param id                  id del AreaTematica
   * @param nombre              nombre del AreaTematica
   * @param idAreaTematicaPadre id del AreaTematica padre
   * @return el objeto AreaTematica
   */
  private AreaTematica generarMockAreaTematica(Long id, String nombre, String descripcion, Long idAreaTematicaPadre) {
    AreaTematica areaTematica = new AreaTematica();
    areaTematica.setId(id);
    areaTematica.setNombre(nombre);
    areaTematica.setDescripcion(descripcion);

    if (idAreaTematicaPadre != null) {
      areaTematica.setPadre(generarMockAreaTematica(idAreaTematicaPadre));
    }
    areaTematica.setActivo(true);

    return areaTematica;
  }

}

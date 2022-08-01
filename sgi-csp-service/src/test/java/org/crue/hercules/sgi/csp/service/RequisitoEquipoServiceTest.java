package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.RequisitoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoEquipoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;

/**
 * RequisitoEquipoServiceTest
 */

class RequisitoEquipoServiceTest extends BaseServiceTest {

  @Mock
  private RequisitoEquipoRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;

  private RequisitoEquipoService service;

  @BeforeEach
  void setUp() throws Exception {
    service = new RequisitoEquipoService(repository, convocatoriaRepository);
  }

  @Test
  void create_ReturnsRequisitoEquipo() {
    // given: Un nuevo RequisitoEquipo
    Long convocatoriaId = 1L;
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(convocatoriaId);

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(true);

    BDDMockito.given(repository.save(requisitoEquipo)).will((InvocationOnMock invocation) -> {
      RequisitoEquipo requisitoEquipoCreado = invocation.getArgument(0);
      requisitoEquipoCreado.setId(1L);
      return requisitoEquipoCreado;
    });

    // when: Creamos el RequisitoEquipo
    RequisitoEquipo requisitoEquipoCreado = service.create(requisitoEquipo);

    // then: El RequisitoEquipo se crea correctamente
    Assertions.assertThat(requisitoEquipoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(requisitoEquipoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(requisitoEquipoCreado.getEdadMaxima()).as("getEdadMaxima()")
        .isEqualTo(requisitoEquipo.getEdadMaxima());
    Assertions.assertThat(requisitoEquipoCreado.getSexoRef()).as("getSexoRef()")
        .isEqualTo(requisitoEquipo.getSexoRef());
  }

  @Test
  void create_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: Un nuevo RequisitoEquipo sin convocatoria
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(null);

    // when: Creamos el RequisitoEquipo
    // then: Lanza una excepcion porque la convocatoria es null
    Assertions.assertThatThrownBy(() -> service.create(requisitoEquipo)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("The Identifier from Team Requirement can't be null");
  }

  @Test
  void create_WithDuplicatedConvocatoria_ThrowsIllegalArgumentException() {
    // given: Un nuevo RequisitoEquipo con convocatoria ya asignada
    Long convocatoriaId = 1L;
    RequisitoEquipo requisitoEquipoExistente = generarMockRequisitoEquipo(convocatoriaId);

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(true);

    // when: Creamos el RequisitoEquipo
    // then: Lanza una excepcion porque la convocatoria ya tiene un RequisitoEquipo
    Assertions.assertThatThrownBy(() -> service.create(requisitoEquipoExistente))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("There is already a Team Requirement related with the Call");
  }

  @Test
  void update_ReturnsRequisitoEquipo() {
    // given: Un nuevo RequisitoEquipo con el sexo actualizado
    Long convocatoriaId = 1L;
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(convocatoriaId);
    RequisitoEquipo requisitoEquipoActualizado = generarMockRequisitoEquipo(convocatoriaId);
    requisitoEquipoActualizado.setEdadMaxima(45);

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(true);
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(requisitoEquipo));
    BDDMockito.given(repository.save(ArgumentMatchers.<RequisitoEquipo>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el RequisitoEquipo
    requisitoEquipoActualizado = service.update(requisitoEquipoActualizado, requisitoEquipo.getId());

    // then: El RequisitoEquipo se actualiza correctamente.
    Assertions.assertThat(requisitoEquipoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(requisitoEquipoActualizado.getId()).as("getId()").isEqualTo(requisitoEquipo.getId());
    Assertions.assertThat(requisitoEquipoActualizado.getEdadMaxima()).as("getEdadMaxima()")
        .isEqualTo(requisitoEquipo.getEdadMaxima());
    Assertions.assertThat(requisitoEquipoActualizado.getSexoRef()).as("getSexoRef()")
        .isEqualTo(requisitoEquipo.getSexoRef());
  }

  @Test
  void update_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: Un RequisitoEquipo actualizado con una convocatoria que no existe
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(null);

    // when: Actualizamos el RequisitoEquipo
    // then: Lanza una excepcion porque el RequisitoEquipo no existe
    Assertions.assertThatThrownBy(() -> service.update(requisitoEquipo, null))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("The Identifier from Team Requirement can't be null");
  }

  @Test
  void update_WithConvocatoriaNotExist_ThrowsNotFoundException() {
    // given: Un RequisitoEquipo actualizado con una convocatoria que no existe
    Long convocatoriaId = 1L;
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(convocatoriaId);

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(false);

    // when: Actualizamos el RequisitoEquipo
    // then: Lanza una excepcion porque la Convocatoria no existe
    Assertions.assertThatThrownBy(() -> service.update(requisitoEquipo, requisitoEquipo.getId()))
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  void update_NotExist_ThrowsNotFoundException() {
    // given: Un RequisitoEquipo actualizado con una convocatoria que no existe
    Long convocatoriaId = 1L;
    RequisitoEquipo requisitoEquipo = generarMockRequisitoEquipo(convocatoriaId);

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(true);
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el RequisitoEquipo
    // then: Lanza una excepcion porque la Convocatoria no existe
    Assertions.assertThatThrownBy(() -> service.update(requisitoEquipo, requisitoEquipo.getId()))
        .isInstanceOf(RequisitoEquipoNotFoundException.class);
  }

  @Test
  void findByConvocatoriaId_ReturnsRequisitoEquipo() {
    // given: Un RequisitoEquipo con el id convocatoria buscado
    Long idBuscado = 1L;

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockRequisitoEquipo(idBuscado)));

    // when: Buscamos el RequisitoEquipo por id Convocatoria
    RequisitoEquipo requisitoEquipo = service.findByConvocatoriaId(idBuscado);

    // then: el RequisitoEquipo
    Assertions.assertThat(requisitoEquipo).as("isNotNull()").isNotNull();
    Assertions.assertThat(requisitoEquipo.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(requisitoEquipo.getEdadMaxima()).as("getEdadMaxima()").isEqualTo(50);
    Assertions.assertThat(requisitoEquipo.getSexoRef()).as("getSexoRef()").isEqualTo("sexo-ref");
  }

  @Test
  void findByConvocatoriaId_WithIdNotExist_ThrowsConvocatoriaNotFoundException() throws Exception {
    // given: Ninguna convocatoria con el id Convocatoria buscado
    Long idBuscado = 1L;

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Buscamos id de Convocatoria
    // then: lanza un ConvocatoriaNotFoundException
    Assertions.assertThatThrownBy(() -> service.findByConvocatoriaId(idBuscado))
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  private Convocatoria generarMockConvocatoria(Long convocatoriaId) {
    return Convocatoria.builder().id(convocatoriaId).activo(Boolean.TRUE).codigo("codigo" + convocatoriaId).build();
  }

  /**
   * Función que devuelve un objeto RequisitoEquipo
   * 
   * @param id id del RequisitoEquipo
   * @return el objeto RequisitoEquipo
   */
  private RequisitoEquipo generarMockRequisitoEquipo(Long id) {
    RequisitoEquipo requisitoEquipo = new RequisitoEquipo();
    requisitoEquipo.setId(id);
    requisitoEquipo.setEdadMaxima(50);
    requisitoEquipo.setSexoRef("sexo-ref");
    return requisitoEquipo;
  }

}

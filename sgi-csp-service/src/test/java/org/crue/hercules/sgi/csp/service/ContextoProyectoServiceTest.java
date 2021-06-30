package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ContextoProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.ContextoProyecto;
import org.crue.hercules.sgi.csp.repository.ContextoProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.service.impl.ContextoProyectoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;

/**
 * ContextoProyectoServiceTest
 */
public class ContextoProyectoServiceTest extends BaseServiceTest {

  @Mock
  private ContextoProyectoRepository repository;
  @Mock
  private ProyectoRepository proyectoRepository;

  private ContextoProyectoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ContextoProyectoServiceImpl(repository, proyectoRepository);
  }

  @Test
  public void create_ReturnsContextoProyecto() {
    // given: Un nuevo ContextoProyecto
    ContextoProyecto contextoProyecto = generarMockContextoProyecto(null);

    BDDMockito.given(repository.save(contextoProyecto)).will((InvocationOnMock invocation) -> {
      ContextoProyecto contextoProyectoCreado = invocation.getArgument(0);
      contextoProyectoCreado.setId(1L);
      return contextoProyectoCreado;
    });

    // when: Creamos el ContextoProyecto
    ContextoProyecto contextoProyectoCreado = service.create(contextoProyecto);

    // then: El ContextoProyecto se crea correctamente
    Assertions.assertThat(contextoProyectoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(contextoProyectoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(contextoProyectoCreado.getIntereses()).as("getIntereses()")
        .isEqualTo(contextoProyecto.getIntereses());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ContextoProyecto que ya tiene id
    ContextoProyecto contextoProyecto = generarMockContextoProyecto(1L);

    // when: Creamos el ContextoProyecto
    // then: Lanza una excepcion porque el ContextoProyecto ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(contextoProyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear ContextoProyecto");
  }

  @Test
  public void create_WithoutProyecto_ThrowsIllegalArgumentException() {
    // given: Un nuevo ContextoProyecto sin proyecto
    ContextoProyecto contextoProyecto = generarMockContextoProyecto(null);
    contextoProyecto.setProyectoId(null);

    // when: Creamos el ContextoProyecto
    // then: Lanza una excepcion porque la proyecto es null
    Assertions.assertThatThrownBy(() -> service.create(contextoProyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Proyecto no puede ser null para crear ContextoProyecto");
  }

  @Test
  public void create_WithDuplicatedProyecto_ThrowsIllegalArgumentException() {
    // given: Un nuevo ContextoProyecto con proyecto ya asignada
    Long proyectoId = 1L;
    ContextoProyecto contextoProyecto = generarMockContextoProyecto(null);

    BDDMockito.given(repository.existsByProyectoId(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);

    // when: Creamos el ContextoProyecto
    // then: Lanza una excepcion porque la proyecto ya tiene un ContextoProyecto
    Assertions.assertThatThrownBy(() -> service.create(contextoProyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe ContextoProyecto para el proyecto " + proyectoId);
  }

  @Test
  public void update_ReturnsContextoProyecto() {
    // given: Un nuevo ContextoProyecto con el sexo actualizado
    ContextoProyecto contextoProyecto = generarMockContextoProyecto(1L);
    ContextoProyecto contextoProyectoActualizado = generarMockContextoProyecto(1L);
    contextoProyectoActualizado.setIntereses("Intereses");

    BDDMockito.given(repository.findByProyectoId(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(contextoProyecto));
    BDDMockito.given(repository.save(ArgumentMatchers.<ContextoProyecto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ContextoProyecto
    contextoProyectoActualizado = service.update(contextoProyectoActualizado, 1L);

    // then: El ContextoProyecto se actualiza correctamente.
    Assertions.assertThat(contextoProyectoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(contextoProyectoActualizado.getId()).as("getId()").isEqualTo(contextoProyecto.getId());
    Assertions.assertThat(contextoProyectoActualizado.getIntereses()).as("getIntereses()")
        .isEqualTo(contextoProyecto.getIntereses());
  }

  @Test
  public void update_WithIdNotExist_ThrowsContextoProyectoNotFoundException() {
    // given: Un ContextoProyecto actualizado con un id que no existe
    ContextoProyecto contextoProyecto = generarMockContextoProyecto(1L);

    BDDMockito.given(repository.findByProyectoId(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(contextoProyecto));

    // when: Actualizamos el ContextoProyecto
    // then: Lanza una excepcion porque el ContextoProyecto no existe
    Assertions.assertThatThrownBy(() -> service.update(contextoProyecto, 1L))
        .isInstanceOf(ContextoProyectoNotFoundException.class);
  }

  @Test
  public void findByProyectoId_ReturnsContextoProyecto() {
    // given: Un ContextoProyecto con el id buscado
    Long idBuscado = 1L;

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findByProyectoId(idBuscado))
        .willReturn(Optional.of(generarMockContextoProyecto(idBuscado)));

    // when: Buscamos el ContextoProyecto por su id
    ContextoProyecto contextoProyecto = service.findByProyecto(idBuscado);

    // then: el ContextoProyecto
    Assertions.assertThat(contextoProyecto).as("isNotNull()").isNotNull();
    Assertions.assertThat(contextoProyecto.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(contextoProyecto.getIntereses()).as("getIntereses()").isEqualTo("intereses");

  }

  @Test
  public void findByProyectoId_WithIdNotExist_ThrowsProyectoNotFoundExceptionException() throws Exception {
    // given: Ninguna Proyecto con el id buscado
    Long idBuscado = 1L;

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Buscamos el ContextoProyecto por id Proyecto
    // then: lanza un ProyectoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findByProyecto(idBuscado))
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto ContextoProyecto
   * 
   * @param id id del ContextoProyecto
   * @return el objeto ContextoProyecto
   */
  private ContextoProyecto generarMockContextoProyecto(Long id) {
    ContextoProyecto contextoProyecto = new ContextoProyecto();
    contextoProyecto.setId(id);
    contextoProyecto.setProyectoId(id != null ? id : 1L);
    contextoProyecto.setIntereses("intereses");
    return contextoProyecto;
  }

}

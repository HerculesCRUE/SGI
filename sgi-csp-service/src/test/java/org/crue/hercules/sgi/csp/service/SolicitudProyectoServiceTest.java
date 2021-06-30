package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto.TipoPresupuesto;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudProyectoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SolicitudProyectoServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class SolicitudProyectoServiceTest {

  @Mock
  private SolicitudProyectoRepository repository;

  @Mock
  private SolicitudRepository solicitudRepository;
  @Mock
  private SolicitudService solicitudService;

  private SolicitudProyectoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new SolicitudProyectoServiceImpl(repository, solicitudRepository, solicitudService);
  }

  @Test
  public void create__ReturnsSolicitudProyecto() {
    // given: Un nuevo SolicitudProyecto
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.save(ArgumentMatchers.<SolicitudProyecto>any())).will((InvocationOnMock invocation) -> {
      SolicitudProyecto solicitudProyectoCreado = invocation.getArgument(0);
      if (solicitudProyectoCreado.getId() == null) {
        solicitudProyectoCreado.setId(1L);
      }

      return solicitudProyectoCreado;
    });

    // when: Creamos el SolicitudProyecto
    SolicitudProyecto solicitudProyectoCreado = service.create(solicitudProyecto);

    // then: El SolicitudProyecto se crea correctamente
    Assertions.assertThat(solicitudProyectoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(solicitudProyectoCreado.getTitulo()).as("getTitulo()")
        .isEqualTo(solicitudProyecto.getTitulo());
    Assertions.assertThat(solicitudProyectoCreado.getColaborativo()).as("getColaborativo()")
        .isEqualTo(solicitudProyecto.getColaborativo());
  }

  @Test
  public void create_WithoutId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyecto que ya tiene id
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(null);

    // when: Creamos el SolicitudProyecto
    // then: Lanza una excepcion porque el SolicitudProyecto ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El id no puede ser null para realizar la acción sobre SolicitudProyecto");
  }

  @Test
  public void create_WithoutTitulo_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyecto que no tiene titulo
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(1L);

    solicitudProyecto.setTitulo(null);

    // when: Creamos el SolicitudProyecto
    // then: Lanza una excepcion porque no tiene titulo
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El título no puede ser null para realizar la acción sobre SolicitudProyecto");
  }

  @Test
  public void create_WithoutColaborativo_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyecto que no tiene colaborativo
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(1L);

    solicitudProyecto.setColaborativo(null);

    // when: Creamos el SolicitudProyecto
    // then: Lanza una excepcion porque no tiene colaborativo
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Colaborativo no puede ser null para realizar la acción sobre SolicitudProyecto");
  }

  @Test
  public void create_WithNoExistingSolicitud_ThrowsSolicitudNotFoundException() {
    // given: Un nuevo SolicitudProyecto que tiene una solicitud que no existe
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Creamos el SolicitudProyecto
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyecto))
        .isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  public void update_ReturnsSolicitudProyecto() {
    // given: Un nuevo SolicitudProyecto con el titulo actualizado
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(3L);

    SolicitudProyecto solicitudProyectoActualizado = generarSolicitudProyecto(3L);

    solicitudProyectoActualizado.setTitulo("titulo-actualizado");

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitudProyecto));

    BDDMockito.given(repository.save(ArgumentMatchers.<SolicitudProyecto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyecto
    SolicitudProyecto solicitudProyectoActualizada = service.update(solicitudProyectoActualizado);

    // then: El SolicitudProyecto se actualiza correctamente.
    Assertions.assertThat(solicitudProyectoActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoActualizada.getId()).as("getId()").isEqualTo(solicitudProyecto.getId());
    Assertions.assertThat(solicitudProyectoActualizada.getTitulo()).as("getTitulo()")
        .isEqualTo(solicitudProyecto.getTitulo());

  }

  @Test
  public void update_WithSolicitudNotExist_ThrowsSolicitudNotFoundException() {
    // given: Un SolicitudProyecto actualizado con un programa que no existe
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Actualizamos el SolicitudProyecto
    // then: Lanza una excepcion porque la solicitud asociada no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyecto))
        .isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  public void update_WithIdNotExist_ThrowsSolicitudProyectoNotFoundException() {
    // given: Un SolicitudProyecto actualizado con un id que no existe
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyecto
    // then: Lanza una excepcion porque el SolicitudProyecto no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyecto))
        .isInstanceOf(SolicitudProyectoNotFoundException.class);
  }

  @Test
  public void update_WithoutTitulo_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyecto que no tiene titulo
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(1L);

    solicitudProyecto.setTitulo(null);

    // when: Actualizamos el SolicitudProyecto
    // then: Lanza una excepcion porque no tiene titulo
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El título no puede ser null para realizar la acción sobre SolicitudProyecto");
  }

  @Test
  public void update_WithoutColaborativo_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyecto que no tiene colaborativo
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(1L);

    solicitudProyecto.setColaborativo(null);

    // when: Actualizamos el SolicitudProyecto
    // then: Lanza una excepcion porque no tiene colaborativo
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyecto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Colaborativo no puede ser null para realizar la acción sobre SolicitudProyecto");
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing SolicitudProyecto
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(SolicitudProyectoNotFoundException.class);
  }

  @Test
  public void findById_ReturnsSolicitudProyecto() {
    // given: Un SolicitudProyecto con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarSolicitudProyecto(idBuscado)));

    // when: Buscamos el SolicitudProyecto por su id
    SolicitudProyecto solicitudProyecto = service.findById(idBuscado);

    // then: el SolicitudProyecto
    Assertions.assertThat(solicitudProyecto).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyecto.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsSolicitudProyectoNotFoundException() throws Exception {
    // given: Ningun SolicitudProyecto con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SolicitudProyecto por su id
    // then: lanza un SolicitudProyectoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(SolicitudProyectoNotFoundException.class);
  }

  @Test
  public void findBySolicitudId_ReturnsSolicitudProyecto() {
    // given: Un Solicitud con el id buscado
    Long idSolicitud = 1L;

    BDDMockito.given(solicitudRepository.existsById(idSolicitud)).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findById(idSolicitud)).willReturn(Optional.of(generarSolicitudProyecto(idSolicitud)));

    // when: Buscamos el SolicitudProyecto por solicitud id
    SolicitudProyecto solicitudProyecto = service.findBySolicitud(idSolicitud);

    // then: el SolicitudProyecto
    Assertions.assertThat(solicitudProyecto).as("isNotNull()").isNotNull();
  }

  @Test
  public void findBySolicitudId_WithSolicitudIdNotExist_ThrowsSolicitudProyectoNotFoundException() throws Exception {
    // given: Ningun SolicitudProyecto con el solicitud id buscado
    Long idSolicitud = 1L;

    // when: Buscamos el SolicitudProyecto por solicitud id
    // then: lanza un SolicitudProyectoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findBySolicitud(idSolicitud))
        .isInstanceOf(SolicitudNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto SolicitudProyecto
   * 
   * @param solicitudProyectoId
   * @param solicitudId
   * @return el objeto SolicitudProyecto
   */
  private SolicitudProyecto generarSolicitudProyecto(Long solicitudProyectoId) {

    SolicitudProyecto solicitudProyecto = SolicitudProyecto.builder().id(solicitudProyectoId)
        .titulo("titulo-" + solicitudProyectoId).acronimo("acronimo-" + solicitudProyectoId).colaborativo(Boolean.TRUE)
        .tipoPresupuesto(TipoPresupuesto.GLOBAL).build();

    return solicitudProyecto;
  }

}

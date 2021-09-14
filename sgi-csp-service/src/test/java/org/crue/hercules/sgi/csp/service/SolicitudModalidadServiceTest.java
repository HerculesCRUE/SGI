package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProgramaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudModalidadNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadConvocanteRepository;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudModalidadRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudModalidadServiceImpl;
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
 * SolicitudModalidadServiceTest
 */
public class SolicitudModalidadServiceTest extends BaseServiceTest {

  @Mock
  private SolicitudModalidadRepository repository;

  @Mock
  private SolicitudRepository solicitudRepository;

  @Mock
  private ProgramaRepository programaRepository;

  @Mock
  private ConvocatoriaEntidadConvocanteRepository convocatoriaEntidadConvocanteRepository;

  @Mock
  private SolicitudService solicitudService;

  private SolicitudModalidadService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new SolicitudModalidadServiceImpl(repository, solicitudRepository, programaRepository,
        convocatoriaEntidadConvocanteRepository, solicitudService);
  }

  @Test
  public void create_WithConvocatoria_ReturnsSolicitudModalidad() {
    // given: Un nuevo SolicitudModalidad
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId);
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(null);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(programaRepository.findById(2L)).willReturn(Optional.of(solicitudModalidad.getPrograma()));
    BDDMockito.given(programaRepository.findById(1L))
        .willReturn(Optional.of(solicitudModalidad.getPrograma().getPadre()));
    BDDMockito.given(convocatoriaEntidadConvocanteRepository
        .findByConvocatoriaIdAndEntidadRef(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockConvocatoriaEntidadConvocante(1L)));

    BDDMockito.given(repository.save(ArgumentMatchers.<SolicitudModalidad>any()))
        .will((InvocationOnMock invocation) -> {
          SolicitudModalidad solicitudModalidadCreado = invocation.getArgument(0);
          if (solicitudModalidadCreado.getId() == null) {
            solicitudModalidadCreado.setId(1L);
          }

          return solicitudModalidadCreado;
        });

    // when: Creamos el SolicitudModalidad
    SolicitudModalidad solicitudModalidadCreado = service.create(solicitudModalidad);

    // then: El SolicitudModalidad se crea correctamente
    Assertions.assertThat(solicitudModalidadCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudModalidadCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(solicitudModalidadCreado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(solicitudModalidad.getEntidadRef());
    Assertions.assertThat(solicitudModalidadCreado.getPrograma().getId()).as("getPrograma().getId()")
        .isEqualTo(solicitudModalidad.getPrograma().getId());
    Assertions.assertThat(solicitudModalidadCreado.getSolicitudId()).as("getSolicitudId()")
        .isEqualTo(solicitudModalidad.getSolicitudId());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudModalidad que ya tiene id
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(1L);

    // when: Creamos el SolicitudModalidad
    // then: Lanza una excepcion porque el SolicitudModalidad ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(solicitudModalidad)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("SolicitudModalidad id tiene que ser null para crear una SolicitudModalidad");
  }

  @Test
  public void create_WithoutSolicitudId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudModalidad que no tiene solicitud
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(null);
    solicitudModalidad.setSolicitudId(null);

    // when: Creamos el SolicitudModalidad
    // then: Lanza una excepcion porque no tiene solicitud
    Assertions.assertThatThrownBy(() -> service.create(solicitudModalidad)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Solicitud id no puede ser null para crear una SolicitudModalidad");
  }

  @Test
  public void create_WithoutProgramaId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudModalidad que no tiene programa
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(null);
    solicitudModalidad.getPrograma().setId(null);

    // when: Creamos el SolicitudModalidad
    // then: Lanza una excepcion porque no tiene programa
    Assertions.assertThatThrownBy(() -> service.create(solicitudModalidad)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Programa id no puede ser null para crear una SolicitudModalidad");
  }

  @Test
  public void create_WithNoExistingSolicitud_ThrowsSolicitudNotFoundException() {
    // given: Un nuevo SolicitudModalidad que tiene una solicitud que no existe
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(null);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Creamos el SolicitudModalidad
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(solicitudModalidad))
        .isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  public void create_WithNoExistingPrograma_ThrowsProgramaNotFoundException() {
    // given: Un nuevo SolicitudModalidad que tiene un programa que no existe
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId);
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(null);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(programaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Creamos el SolicitudModalidad
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(solicitudModalidad))
        .isInstanceOf(ProgramaNotFoundException.class);
  }

  @Test
  public void create_WithNoValidEntidadRef_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudModalidad que tiene una entidadRef y la convocatoria
    // no tiene una convocatoriaEntidadConvocante con ese entidadRef
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId);
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(null);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(programaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudModalidad.getPrograma()));
    BDDMockito.given(convocatoriaEntidadConvocanteRepository
        .findByConvocatoriaIdAndEntidadRef(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.empty());

    // when: Creamos el SolicitudModalidad
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(solicitudModalidad)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "No existe ninguna ConvocatoriaEntidadConvocante con el entidadRef para la convocatoria seleccionada");
  }

  @Test
  public void create_WithNoValidPrograma_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudModalidad y la modalidad seleccionada no pertenece
    // al arbol del programa de la convocatoria
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId);
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(null);

    Programa padre = new Programa();
    padre.setId(3L);
    solicitudModalidad.getPrograma().setPadre(padre);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(programaRepository.findById(2L)).willReturn(Optional.of(solicitudModalidad.getPrograma()));
    BDDMockito.given(convocatoriaEntidadConvocanteRepository
        .findByConvocatoriaIdAndEntidadRef(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockConvocatoriaEntidadConvocante(4L)));

    // when: Creamos el SolicitudModalidad
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(solicitudModalidad)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La modalidad seleccionada no pertenece al arbol del programa de la convocatoria");
  }

  @Test
  public void update_ReturnsSolicitudModalidad() {
    // given: Un nuevo SolicitudModalidad con las observaciones actualizadas
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId);
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(3L);
    SolicitudModalidad solicitudModalidadProgramaActualizado = generarMockSolicitudModalidad(3L);
    solicitudModalidadProgramaActualizado.getPrograma().setId(4L);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(programaRepository.findById(4L))
        .willReturn(Optional.of(solicitudModalidadProgramaActualizado.getPrograma()));
    BDDMockito.given(programaRepository.findById(1L))
        .willReturn(Optional.of(solicitudModalidadProgramaActualizado.getPrograma().getPadre()));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitudModalidad));
    BDDMockito.given(convocatoriaEntidadConvocanteRepository
        .findByConvocatoriaIdAndEntidadRef(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockConvocatoriaEntidadConvocante(1L)));

    BDDMockito.given(repository.save(ArgumentMatchers.<SolicitudModalidad>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudModalidad
    SolicitudModalidad solicitudModalidadActualizada = service.update(solicitudModalidadProgramaActualizado);

    // then: El SolicitudModalidad se actualiza correctamente.
    Assertions.assertThat(solicitudModalidadActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudModalidadActualizada.getId()).as("getId()").isEqualTo(solicitudModalidad.getId());
    Assertions.assertThat(solicitudModalidadActualizada.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(solicitudModalidad.getEntidadRef());
    Assertions.assertThat(solicitudModalidadActualizada.getPrograma().getId()).as("getPrograma().getId()")
        .isEqualTo(solicitudModalidadProgramaActualizado.getPrograma().getId());
    Assertions.assertThat(solicitudModalidadActualizada.getSolicitudId()).as("getSolicitudId()")
        .isEqualTo(solicitudModalidad.getSolicitudId());
  }

  @Test
  public void update_WithProgramaNotExist_ThrowsProgramaNotFoundException() {
    // given: Un SolicitudModalidad actualizado con un programa que no existe
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(1L);

    BDDMockito.given(programaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Actualizamos el SolicitudModalidad
    // then: Lanza una excepcion porque el SolicitudModalidad no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitudModalidad))
        .isInstanceOf(ProgramaNotFoundException.class);
  }

  @Test
  public void update_WithIdNotExist_ThrowsSolicitudModalidadNotFoundException() {
    // given: Un SolicitudModalidad actualizado con un id que no existe
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(1L);

    BDDMockito.given(programaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudModalidad.getPrograma()));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudModalidad
    // then: Lanza una excepcion porque el SolicitudModalidad no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitudModalidad))
        .isInstanceOf(SolicitudModalidadNotFoundException.class);
  }

  @Test
  public void update_WithNoValidPrograma_ThrowsIllegalArgumentException() {
    // given: Un SolicitudModalidad actualizadoy la modalidad seleccionada no
    // pertenece al arbol del programa de la convocatoria
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId);
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(1L);

    Programa padre = new Programa();
    padre.setId(3L);
    solicitudModalidad.getPrograma().setPadre(padre);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(programaRepository.findById(2L)).willReturn(Optional.of(solicitudModalidad.getPrograma()));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitudModalidad));
    BDDMockito.given(convocatoriaEntidadConvocanteRepository
        .findByConvocatoriaIdAndEntidadRef(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockConvocatoriaEntidadConvocante(4L)));
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudModalidad
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(solicitudModalidad)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La modalidad seleccionada no pertenece al arbol del programa de la convocatoria");
  }

  @Test
  public void update_WithProgramaNodoRaiz_ThrowsIllegalArgumentException() {
    // given: Un SolicitudModalidad actualizado y la modalidad seleccionada es el
    // nodo raiz
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId);
    SolicitudModalidad solicitudModalidad = generarMockSolicitudModalidad(1L);
    solicitudModalidad.setPrograma(solicitudModalidad.getPrograma().getPadre());

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(programaRepository.findById(1L)).willReturn(Optional.of(solicitudModalidad.getPrograma()));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitudModalidad));
    BDDMockito.given(convocatoriaEntidadConvocanteRepository
        .findByConvocatoriaIdAndEntidadRef(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.of(generarMockConvocatoriaEntidadConvocante(1L)));
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudModalidad
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(solicitudModalidad)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La modalidad seleccionada es el nodo raiz del arbol");
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing SolicitudModalidad
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
        .isInstanceOf(SolicitudModalidadNotFoundException.class);
  }

  @Test
  public void findById_ReturnsSoliciud() {
    // given: Un SolicitudModalidad con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockSolicitudModalidad(idBuscado)));

    // when: Buscamos el SolicitudModalidad por su id
    SolicitudModalidad solicitudModalidad = service.findById(idBuscado);

    // then: el SolicitudModalidad
    Assertions.assertThat(solicitudModalidad).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudModalidad.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsProgramaNotFoundException() throws Exception {
    // given: Ningun SolicitudModalidad con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SolicitudModalidad por su id
    // then: lanza un SolicitudModalidadNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(SolicitudModalidadNotFoundException.class);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 SolicitudModalidad
    Long solicitudId = 1L;
    List<SolicitudModalidad> solicitudModalidadModalidades = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudModalidadModalidades.add(generarMockSolicitudModalidad(i));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<SolicitudModalidad>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<SolicitudModalidad>>() {
          @Override
          public Page<SolicitudModalidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > solicitudModalidadModalidades.size() ? solicitudModalidadModalidades.size() : toIndex;
            List<SolicitudModalidad> content = solicitudModalidadModalidades.subList(fromIndex, toIndex);
            Page<SolicitudModalidad> page = new PageImpl<>(content, pageable, solicitudModalidadModalidades.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<SolicitudModalidad> page = service.findAllBySolicitud(solicitudId, null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      SolicitudModalidad solicitudModalidad = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitudModalidad.getId()).isEqualTo(i);
    }
  }

  private Solicitud generarMockSolicitud(Long solicitudId) {
    Solicitud solicitud = new Solicitud();
    solicitud.setId(solicitudId);
    solicitud.setConvocatoriaId(1L);
    solicitud.setUnidadGestionRef("2");

    return solicitud;
  }

  /**
   * Función que devuelve un objeto SolicitudModalidad
   * 
   * @param id id del SolicitudModalidad
   * @return el objeto SolicitudModalidad
   */
  private SolicitudModalidad generarMockSolicitudModalidad(Long id) {
    Programa programa = new Programa();
    programa.setId(1L);

    Programa modalidad = new Programa();
    modalidad.setId(2L);
    modalidad.setPadre(programa);

    SolicitudModalidad solicitudModalidad = new SolicitudModalidad();
    solicitudModalidad.setId(id);
    solicitudModalidad.setEntidadRef("entidadRef");
    solicitudModalidad.setSolicitudId(1L);
    solicitudModalidad.setPrograma(modalidad);

    return solicitudModalidad;
  }

  /**
   * Función que devuelve un objeto ConvocatoriaEntidadConvocante
   * 
   * @param id id del ConvocatoriaEntidadConvocante
   * @return el objeto ConvocatoriaEntidadConvocante
   */
  private ConvocatoriaEntidadConvocante generarMockConvocatoriaEntidadConvocante(Long id) {
    Programa programa = new Programa();
    programa.setId(id == null ? 1 : id);
    programa.setActivo(true);

    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = new ConvocatoriaEntidadConvocante();
    convocatoriaEntidadConvocante.setId(id);
    convocatoriaEntidadConvocante.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaEntidadConvocante.setEntidadRef("entidad-" + (id == null ? 1 : id));
    convocatoriaEntidadConvocante.setPrograma(programa);

    return convocatoriaEntidadConvocante;
  }

}

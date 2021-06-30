package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioEquipoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudProyectoSocioEquipoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * SolicitudProyectoSocioEquipoServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class SolicitudProyectoSocioEquipoServiceTest {

  @Mock
  private SolicitudProyectoSocioEquipoRepository repository;

  @Mock
  private SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;

  @Mock
  private SolicitudService solicitudService;

  @Mock
  private SolicitudProyectoRepository solicitudProyectoRepository;

  private SolicitudProyectoSocioEquipoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new SolicitudProyectoSocioEquipoServiceImpl(repository, solicitudService,
        solicitudProyectoSocioRepository, solicitudProyectoRepository);
  }

  @Test
  public void update_ReturnsSolicitudProyectoSocioEquipo() {
    // given: una lista con uno de los SolicitudProyectoSocioEquipo actualizado,
    // otro nuevo y sin el otros existente
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);

    List<SolicitudProyectoSocioEquipo> solicitudProyecotEquipoSocioJustificiacionExistentes = new ArrayList<>();
    solicitudProyecotEquipoSocioJustificiacionExistentes.add(generarSolicitudProyectoSocioEquipo(2L, 1L));
    solicitudProyecotEquipoSocioJustificiacionExistentes.add(generarSolicitudProyectoSocioEquipo(4L, 1L));
    solicitudProyecotEquipoSocioJustificiacionExistentes.add(generarSolicitudProyectoSocioEquipo(5L, 1L));

    SolicitudProyectoSocioEquipo newSolicitudProyectoSocioEquipo = generarSolicitudProyectoSocioEquipo(null, 1L);
    newSolicitudProyectoSocioEquipo.setMesInicio(1);
    newSolicitudProyectoSocioEquipo.setMesFin(3);
    SolicitudProyectoSocioEquipo updatedSolicitudProyectoSocioEquipo = generarSolicitudProyectoSocioEquipo(4L, 1L);
    updatedSolicitudProyectoSocioEquipo.setMesInicio(5);
    updatedSolicitudProyectoSocioEquipo.setMesFin(7);

    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocioActualizar = new ArrayList<>();
    solicitudProyectoEquipoSocioActualizar.add(newSolicitudProyectoSocioEquipo);
    solicitudProyectoEquipoSocioActualizar.add(updatedSolicitudProyectoSocioEquipo);

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyecotEquipoSocioJustificiacionExistentes);
    BDDMockito.doNothing().when(repository).deleteAll(ArgumentMatchers.<SolicitudProyectoSocioEquipo>anyList());
    BDDMockito.given(repository.saveAll(ArgumentMatchers.<SolicitudProyectoSocioEquipo>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocios = invocation.getArgument(0);
          return solicitudProyectoEquipoSocios.stream().map(solicitudProyectoSocioEquipo -> {
            if (solicitudProyectoSocioEquipo.getId() == null) {
              solicitudProyectoSocioEquipo.setId(6L);
            }
            solicitudProyectoSocioEquipo.setSolicitudProyectoSocioId(solicitudProyectoSocioId);
            return solicitudProyectoSocioEquipo;
          }).collect(Collectors.toList());
        });
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: updatedSolicitudProyectoSocioEquipo
    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocioActualizados = service
        .update(solicitudProyectoSocioId, solicitudProyectoEquipoSocioActualizar);

    // then: Se crea el nuevo ConvocatoriaPeriodoJustificacion, se actualiza el
    // existe y se elimina el otro
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(0).getId()).as("get(0).getId()").isEqualTo(6L);
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(0).getSolicitudProyectoSocioId())
        .as("get(0).getSolicitudProyectoSocioId()").isEqualTo(solicitudProyectoSocioId);
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(0).getMesInicio()).as("get(0).getMesInicio()")
        .isEqualTo(newSolicitudProyectoSocioEquipo.getMesInicio());
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(0).getMesFin()).as("get(0).getMesFin()")
        .isEqualTo(newSolicitudProyectoSocioEquipo.getMesFin());
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(0).getPersonaRef()).as("get(0).getPersonaRef()")
        .isEqualTo(newSolicitudProyectoSocioEquipo.getPersonaRef());
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(0).getRolProyecto().getId())
        .as("get(0).getRolProyecto().getId()").isEqualTo(newSolicitudProyectoSocioEquipo.getRolProyecto().getId());

    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(1).getId()).as("get(1).getId()")
        .isEqualTo(updatedSolicitudProyectoSocioEquipo.getId());
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(1).getSolicitudProyectoSocioId())
        .as("get(0).getSolicitudProyectoSocioId()").isEqualTo(solicitudProyectoSocioId);
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(1).getMesInicio()).as("get(1).getMesInicio()")
        .isEqualTo(updatedSolicitudProyectoSocioEquipo.getMesInicio());
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(1).getMesFin()).as("get(1).getMesFin()")
        .isEqualTo(updatedSolicitudProyectoSocioEquipo.getMesFin());
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(1).getPersonaRef()).as("get(1).getPersonaRef()")
        .isEqualTo(updatedSolicitudProyectoSocioEquipo.getPersonaRef());
    Assertions.assertThat(solicitudProyectoEquipoSocioActualizados.get(1).getRolProyecto().getId())
        .as("get(0).getRolProyecto().getId()").isEqualTo(updatedSolicitudProyectoSocioEquipo.getRolProyecto().getId());

    Mockito.verify(repository, Mockito.times(1)).deleteAll(ArgumentMatchers.<SolicitudProyectoSocioEquipo>anyList());
    Mockito.verify(repository, Mockito.times(1)).saveAll(ArgumentMatchers.<SolicitudProyectoSocioEquipo>anyList());

  }

  @Test
  public void update_WithoutSolicitudProyectoSocio_ThrowsSolicitudProyectoSocioNotFoundException() {
    // given: Un nuevo SolicitudProyectoSocioEquipo que no tiene
    // SolicitudProyectoSocio

    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoSocioEquipo newSolicitudProyectoSocioEquipo = generarSolicitudProyectoSocioEquipo(null, 1L);

    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocioActualizar = new ArrayList<>();

    newSolicitudProyectoSocioEquipo.setRolProyecto(null);
    solicitudProyectoEquipoSocioActualizar.add(newSolicitudProyectoSocioEquipo);

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    // when: Actualizamos el SolicitudProyectoSocioEquipo
    // then: Lanza una excepcion porque no tiene solicitud proyecto socio
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId, solicitudProyectoEquipoSocioActualizar))
        .isInstanceOf(SolicitudProyectoSocioNotFoundException.class);
  }

  @Test
  public void update_WithoSolicitudProyectoSocioChange_ThrowsIllegalArgumentException() {
    // given: Se actualiza la solicitud proyecto socio
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);
    SolicitudProyectoSocioEquipo updateSolicitudProyectoSocioEquipo = generarSolicitudProyectoSocioEquipo(2L, 1L);

    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocioActualizar = new ArrayList<>();

    updateSolicitudProyectoSocioEquipo.setRolProyecto(null);
    solicitudProyectoEquipoSocioActualizar.add(updateSolicitudProyectoSocioEquipo);

    List<SolicitudProyectoSocioEquipo> solicitudProyecotEquipoSocioJustificiacionExistentes = new ArrayList<>();
    SolicitudProyectoSocioEquipo solicitudProyectoSocioEquipo = generarSolicitudProyectoSocioEquipo(2L, 1L);
    solicitudProyectoSocioEquipo.setSolicitudProyectoSocioId(3L);
    solicitudProyecotEquipoSocioJustificiacionExistentes.add(solicitudProyectoSocioEquipo);

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyecotEquipoSocioJustificiacionExistentes);
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocioEquipo
    // then: Lanza una excepcion porque se ha modificado la solicitud proyecto socio
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId, solicitudProyectoEquipoSocioActualizar))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("No se puede modificar la solicitud proyecto socio del SolicitudProyectoSocioEquipo");
  }

  @Test
  public void update_WithoutRolProyectoId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoSocioEquipo que no tiene rol proyecto
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);
    SolicitudProyectoSocioEquipo newSolicitudProyectoSocioEquipo = generarSolicitudProyectoSocioEquipo(null, 1L);

    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocioActualizar = new ArrayList<>();

    newSolicitudProyectoSocioEquipo.setRolProyecto(null);
    solicitudProyectoEquipoSocioActualizar.add(newSolicitudProyectoSocioEquipo);

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocioEquipo
    // then: Lanza una excepcion porque no tiene solicitud
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId, solicitudProyectoEquipoSocioActualizar))
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "El rol de participación no puede ser null para realizar la acción sobre SolicitudProyectoSocioEquipo");
  }

  @Test
  public void update_WithoutPersonaRef_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoSocioEquipo que no tiene persona ref
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);
    SolicitudProyectoSocioEquipo newSolicitudProyectoSocioEquipo = generarSolicitudProyectoSocioEquipo(null, 1L);

    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocioActualizar = new ArrayList<>();

    newSolicitudProyectoSocioEquipo.setPersonaRef(null);
    solicitudProyectoEquipoSocioActualizar.add(newSolicitudProyectoSocioEquipo);

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocioEquipo
    // then: Lanza una excepcion porque no tiene persona ref
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId, solicitudProyectoEquipoSocioActualizar))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La persona ref no puede ser null para realizar la acción sobre SolicitudProyectoSocioEquipo");
  }

  @Test
  public void update_WithRangosMesesSolapados_ThrowsIllegalArgumentException() {
    // given: una lista con uno de los SolicitudProyectoSocioEquipo actualizado,
    // otro nuevo y sin el otros existente
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);

    List<SolicitudProyectoSocioEquipo> solicitudProyecotEquipoSocioJustificiacionExistentes = new ArrayList<>();
    solicitudProyecotEquipoSocioJustificiacionExistentes.add(generarSolicitudProyectoSocioEquipo(4L, 1L));

    SolicitudProyectoSocioEquipo newSolicitudProyectoSocioEquipo = generarSolicitudProyectoSocioEquipo(null, 1L);
    SolicitudProyectoSocioEquipo updatedSolicitudProyectoSocioEquipo = generarSolicitudProyectoSocioEquipo(4L, 1L);

    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocioActualizar = new ArrayList<>();
    newSolicitudProyectoSocioEquipo.setMesInicio(1);
    newSolicitudProyectoSocioEquipo.setMesFin(2);
    solicitudProyectoEquipoSocioActualizar.add(newSolicitudProyectoSocioEquipo);
    solicitudProyectoEquipoSocioActualizar.add(updatedSolicitudProyectoSocioEquipo);

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyecotEquipoSocioJustificiacionExistentes);
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocioEquipo
    // then: Lanza una excepcion porque existen otros solicitudproyectosocioequipo
    // dentro del mismo rango de meses
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId, solicitudProyectoEquipoSocioActualizar))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");

  }

  @Test
  public void findById_ReturnsSolicitudProyectoSocioEquipo() {
    // given: Un SolicitudProyectoSocioEquipo con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarSolicitudProyectoSocioEquipo(idBuscado, 1L)));

    // when: Buscamos el SolicitudProyectoSocioEquipo por su id
    SolicitudProyectoSocioEquipo solicitudProyectoSocioEquipo = service.findById(idBuscado);

    // then: el SolicitudProyectoSocioEquipo
    Assertions.assertThat(solicitudProyectoSocioEquipo).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoSocioEquipo.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsSolicitudProyectoSocioEquipoNotFoundException() throws Exception {
    // given: Ningun SolicitudProyectoSocioEquipo con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SolicitudProyectoSocioEquipo por su id
    // then: lanza un SolicitudProyectoSocioEquipoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(SolicitudProyectoSocioEquipoNotFoundException.class);
  }

  @Test
  public void findBySolicitudProyectoSocioId_ReturnsSolicitudProyectoSocioEquipo() {
    // given: Una lista con 37 SolicitudProyectoSocio
    Long solicitudId = 1L;
    List<SolicitudProyectoSocioEquipo> solicitudProyectoSocioEquipo = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoSocioEquipo.add(generarSolicitudProyectoSocioEquipo(i, i));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoSocioEquipo>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<SolicitudProyectoSocioEquipo>>() {
          @Override
          public Page<SolicitudProyectoSocioEquipo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > solicitudProyectoSocioEquipo.size() ? solicitudProyectoSocioEquipo.size() : toIndex;
            List<SolicitudProyectoSocioEquipo> content = solicitudProyectoSocioEquipo.subList(fromIndex, toIndex);
            Page<SolicitudProyectoSocioEquipo> page = new PageImpl<>(content, pageable,
                solicitudProyectoSocioEquipo.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<SolicitudProyectoSocioEquipo> page = service.findAllBySolicitudProyectoSocio(solicitudId, null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoSocioEquipo solicitudProyectoEquipoSocioRecuperado = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitudProyectoEquipoSocioRecuperado.getId()).isEqualTo(i);
    }

  }

  private SolicitudProyecto generarMockSolicitudProyecto(Long solicitudProyectoId) {
    return SolicitudProyecto.builder().id(solicitudProyectoId).build();
  }

  private SolicitudProyectoSocio generarMockSolicitudProyectoSocio(Long solicitudProyectoSocioId,
      Long solicitudProyectoId) {
    return SolicitudProyectoSocio.builder().id(solicitudProyectoSocioId).solicitudProyectoId(solicitudProyectoId)
        .build();
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoSocioEquipo
   * 
   * @param solicitudProyectoEquipoSocioId
   * @param entidadesRelacionadasId
   * @return el objeto SolicitudProyectoSocioEquipo
   */
  private SolicitudProyectoSocioEquipo generarSolicitudProyectoSocioEquipo(Long solicitudProyectoEquipoSocioId,
      Long entidadesRelacionadasId) {

    SolicitudProyectoSocioEquipo solicitudProyectoSocioEquipo = SolicitudProyectoSocioEquipo.builder()
        .id(solicitudProyectoEquipoSocioId).solicitudProyectoSocioId(entidadesRelacionadasId)
        .rolProyecto(RolProyecto.builder().id(entidadesRelacionadasId).build()).personaRef("user-001").mesInicio(1)
        .mesFin(3).build();

    return solicitudProyectoSocioEquipo;
  }

}

package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioPeriodoJustificacionDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionDocumentoRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoSocioPeriodoJustificacionDocumentoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ProyectoSocioPeriodoJustificacionDocumentoServiceTest
 */
class ProyectoSocioPeriodoJustificacionDocumentoServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoSocioPeriodoJustificacionDocumentoRepository repository;
  @Mock
  private ProyectoSocioPeriodoJustificacionRepository proyectoSocioRepository;

  private ProyectoSocioPeriodoJustificacionDocumentoService service;

  @BeforeEach
  void setUp() throws Exception {
    service = new ProyectoSocioPeriodoJustificacionDocumentoServiceImpl(repository, proyectoSocioRepository);
  }

  @Test
  void update_ReturnsProyectoSocioPeriodoJustificacionDocumentoList() {
    // given: una lista con uno de los ProyectoSocioPeriodoJustificacionDocumento
    // actualizado,
    // otro nuevo y sin el otros existente
    Long proyectoSocioId = 1L;
    Long proyectoSocioPeriodoJustificacionId = 1L;

    List<ProyectoSocioPeriodoJustificacionDocumento> peridosJustificiacionExistentes = new ArrayList<>();
    peridosJustificiacionExistentes
        .add(generarMockProyectoSocioPeriodoJustificacionDocumento(2L, proyectoSocioPeriodoJustificacionId));

    ProyectoSocioPeriodoJustificacionDocumento updateProyectoSocioPeriodoJustificacionDocumento1 = generarMockProyectoSocioPeriodoJustificacionDocumento(
        4L, proyectoSocioPeriodoJustificacionId);

    peridosJustificiacionExistentes.add(updateProyectoSocioPeriodoJustificacionDocumento1);

    ProyectoSocioPeriodoJustificacionDocumento updateProyectoSocioPeriodoJustificacionDocumento2 = generarMockProyectoSocioPeriodoJustificacionDocumento(
        5L, proyectoSocioPeriodoJustificacionId);

    peridosJustificiacionExistentes.add(updateProyectoSocioPeriodoJustificacionDocumento2);
    peridosJustificiacionExistentes.add(updateProyectoSocioPeriodoJustificacionDocumento2);

    ProyectoSocioPeriodoJustificacionDocumento newProyectoSocioPeriodoJustificacionDocumento = generarMockProyectoSocioPeriodoJustificacionDocumento(
        null, proyectoSocioPeriodoJustificacionId);

    ProyectoSocioPeriodoJustificacionDocumento updatedProyectoSocioPeriodoJustificacionDocumento = generarMockProyectoSocioPeriodoJustificacionDocumento(
        4L, proyectoSocioPeriodoJustificacionId);

    List<ProyectoSocioPeriodoJustificacionDocumento> peridosJustificiacionActualizar = new ArrayList<>();
    peridosJustificiacionActualizar.add(newProyectoSocioPeriodoJustificacionDocumento);
    peridosJustificiacionActualizar.add(updatedProyectoSocioPeriodoJustificacionDocumento);

    BDDMockito.given(proyectoSocioRepository.existsById(ArgumentMatchers.anyLong()))
        .willReturn(true);

    BDDMockito.given(repository.findAllByProyectoSocioPeriodoJustificacionId(ArgumentMatchers.anyLong()))
        .willReturn(peridosJustificiacionExistentes);

    BDDMockito.doNothing().when(repository)
        .deleteAll(ArgumentMatchers.<ProyectoSocioPeriodoJustificacionDocumento>anyList());

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ProyectoSocioPeriodoJustificacionDocumento>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<ProyectoSocioPeriodoJustificacionDocumento> periodoJustificaciones = invocation.getArgument(0);

          return periodoJustificaciones.stream().map(periodoJustificacion -> {
            if (periodoJustificacion.getId() == null) {
              periodoJustificacion.setId(6L);
            }
            periodoJustificacion.setProyectoSocioPeriodoJustificacionId(proyectoSocioId);
            return periodoJustificacion;
          }).collect(Collectors.toList());
        });

    // when: update
    List<ProyectoSocioPeriodoJustificacionDocumento> periodosJustificacionActualizados = service.update(proyectoSocioId,
        peridosJustificiacionActualizar);

    // then: Se crea el nuevo ProyectoSocioPeriodoJustificacionDocumento, se
    // actualiza el
    // existe y se elimina el otro
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getId()).as("get(0).getId()").isEqualTo(6L);
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getProyectoSocioPeriodoJustificacionId())
        .as("get(0).getProyectoSocioPeriodoJustificacionId()").isEqualTo(proyectoSocioPeriodoJustificacionId);
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getComentario()).as("get(0).getComentario()")
        .isEqualTo(newProyectoSocioPeriodoJustificacionDocumento.getComentario());
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getDocumentoRef()).as("get(0).getDocumentoRef()")
        .isEqualTo(newProyectoSocioPeriodoJustificacionDocumento.getDocumentoRef());
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getTipoDocumento().getId())
        .as("get(0).getTipoDocumento().getId()")
        .isEqualTo(newProyectoSocioPeriodoJustificacionDocumento.getTipoDocumento().getId());
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getNombre()).as("get(0).getNombre()")
        .isEqualTo(newProyectoSocioPeriodoJustificacionDocumento.getNombre());
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getVisible()).as("get(0).getVisible()")
        .isEqualTo(newProyectoSocioPeriodoJustificacionDocumento.getVisible());

    Assertions.assertThat(periodosJustificacionActualizados.get(1).getId()).as("get(1).getId()").isEqualTo(4L);
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getProyectoSocioPeriodoJustificacionId())
        .as("get(1).getProyectoSocioPeriodoJustificacionId()").isEqualTo(proyectoSocioPeriodoJustificacionId);
    Assertions.assertThat(periodosJustificacionActualizados.get(0).getComentario()).as("get(1).getComentario()")
        .isEqualTo(updatedProyectoSocioPeriodoJustificacionDocumento.getComentario());
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getDocumentoRef()).as("get(1).getDocumentoRef()")
        .isEqualTo(updatedProyectoSocioPeriodoJustificacionDocumento.getDocumentoRef());
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getTipoDocumento().getId())
        .as("get(1).getTipoDocumento().getId()")
        .isEqualTo(updatedProyectoSocioPeriodoJustificacionDocumento.getTipoDocumento().getId());
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getNombre()).as("get(1).getNombre()")
        .isEqualTo(updatedProyectoSocioPeriodoJustificacionDocumento.getNombre());
    Assertions.assertThat(periodosJustificacionActualizados.get(1).getVisible()).as("get(1).getVisible()")
        .isEqualTo(updatedProyectoSocioPeriodoJustificacionDocumento.getVisible());

    Mockito.verify(repository, Mockito.times(1))
        .deleteAll(ArgumentMatchers.<ProyectoSocioPeriodoJustificacionDocumento>anyList());
    Mockito.verify(repository, Mockito.times(1))
        .saveAll(ArgumentMatchers.<ProyectoSocioPeriodoJustificacionDocumento>anyList());
  }

  @Test
  void update_WithNoExistingProyectoSocioPeriodoJustificacion_ThrowsProyectoSocioPeriodoJustificacionNotFoundException() {
    // given: a ProyectoSocioPeriodoJustificacionEntidadGestora with non existing
    // ProyectoSocioPeriodoJustificacion
    Long proyectoSocioId = 1L;
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacionDocumento(
        1L, proyectoSocioPeriodoJustificacionId);

    BDDMockito.given(proyectoSocioRepository.existsById(ArgumentMatchers.anyLong()))
        .willReturn(false);

    Assertions.assertThatThrownBy(
        // when: update ProyectoSocioPeriodoJustificacionEntidadGestora
        () -> service.update(proyectoSocioId, Arrays.asList(proyectoSocioPeriodoJustificacion)))
        // then: throw exception as ProyectoSocioPeriodoJustificacion is not found
        .isInstanceOf(ProyectoSocioPeriodoJustificacionNotFoundException.class);
  }

  @Test
  void update_WithIdNotExist_ThrowsProyectoSocioPeriodoJustificacionDocumentoNotFoundException() {
    // given: Un ProyectoSocioPeriodoJustificacionDocumento a actualizar con un id
    // que no existe
    Long proyectoSocioId = 1L;
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacionDocumento = generarMockProyectoSocioPeriodoJustificacionDocumento(
        1L, proyectoSocioPeriodoJustificacionId);

    BDDMockito.given(proyectoSocioRepository.existsById(ArgumentMatchers.anyLong()))
        .willReturn(true);

    BDDMockito.given(repository.findAllByProyectoSocioPeriodoJustificacionId(ArgumentMatchers.anyLong()))
        .willReturn(new ArrayList<>());

    // when:update
    // then: Lanza una excepcion porque el
    // ProyectoSocioPeriodoJustificacionDocumento no
    // existe
    Assertions
        .assertThatThrownBy(
            () -> service.update(proyectoSocioId, Arrays.asList(proyectoSocioPeriodoJustificacionDocumento)))
        .isInstanceOf(ProyectoSocioPeriodoJustificacionDocumentoNotFoundException.class);
  }

  @Test
  void update_WithProyectoSocioPeriodoJustificacionChange_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacionDocumento with proyecto socio
    // modificado
    Long proyectoSocioId = 1L;
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacionDocumento = generarMockProyectoSocioPeriodoJustificacionDocumento(
        1L, proyectoSocioPeriodoJustificacionId);

    proyectoSocioPeriodoJustificacionDocumento.setProyectoSocioPeriodoJustificacionId(3L);

    BDDMockito.given(proyectoSocioRepository.existsById(ArgumentMatchers.anyLong()))
        .willReturn(true);

    BDDMockito.given(repository.findAllByProyectoSocioPeriodoJustificacionId(ArgumentMatchers.anyLong())).willReturn(
        Arrays.asList(generarMockProyectoSocioPeriodoJustificacionDocumento(1L, proyectoSocioPeriodoJustificacionId)));

    Assertions.assertThatThrownBy(
        // when: update
        () -> service.update(proyectoSocioId, Arrays.asList(proyectoSocioPeriodoJustificacionDocumento)))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("No se puede modificar el proyecto socio del ProyectoSocioPeriodoJustificacionDocumento");
  }

  @Test
  void findAllByProyectoSocioPeriodoJustificacion_ReturnsPage() {
    // given: Una lista con 37 ProyectoSocioPeriodoJustificacionEntidadGestora para
    // la ProyectoSocioPeriodoJustificacion
    Long proyectoSocioId = 1L;
    Long proyectoSocioPeriodoJustificacionId = 1L;
    List<ProyectoSocioPeriodoJustificacionDocumento> proyectoSociosEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoSociosEntidadesConvocantes
          .add(generarMockProyectoSocioPeriodoJustificacionDocumento(i, proyectoSocioPeriodoJustificacionId));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<ProyectoSocioPeriodoJustificacionDocumento>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectoSociosEntidadesConvocantes.size() ? proyectoSociosEntidadesConvocantes.size()
              : toIndex;
          List<ProyectoSocioPeriodoJustificacionDocumento> content = proyectoSociosEntidadesConvocantes
              .subList(fromIndex, toIndex);
          Page<ProyectoSocioPeriodoJustificacionDocumento> pageResponse = new PageImpl<>(content, pageable,
              proyectoSociosEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoSocioPeriodoJustificacionDocumento> page = service
        .findAllByProyectoSocioPeriodoJustificacion(proyectoSocioId, null, paging);

    // then: Devuelve la pagina 3 con los ProyectoSocioPeriodoJustificacionDocumento
    // del 31
    // al
    // 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoSocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacion = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyectoSocioPeriodoJustificacion.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(proyectoSocioPeriodoJustificacion.getNombre()).isEqualTo("nombre-" + i);
    }
  }

  @Test
  void findById_ReturnsProyectoSocioPeriodoJustificacionDocumento() {
    // given: Un ProyectoSocioPeriodoJustificacionDocumento con el id buscado
    Long idBuscado = 1L;
    Long proyectoSocioPeriodoJustificacionId = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional
        .of(generarMockProyectoSocioPeriodoJustificacionDocumento(idBuscado, proyectoSocioPeriodoJustificacionId)));

    // when: Buscamos el ProyectoSocioPeriodoJustificacionDocumento por su id
    ProyectoSocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacion = service.findById(idBuscado);

    // then: el ProyectoSocioPeriodoJustificacionDocumento
    Assertions.assertThat(proyectoSocioPeriodoJustificacion).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getComentario()).as("getComentario()")
        .isEqualTo("comentario");
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getVisible()).as("getVisible()").isTrue();

  }

  @Test
  void findById_WithIdNotExist_ThrowsProyectoSocioPeriodoJustificacionDocumentoNotFoundException()
      throws Exception {
    // given: Ningun ProyectoSocioPeriodoJustificacionDocumento con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ProyectoSocioPeriodoJustificacionDocumento por su id
    // then: lanza un ProyectoSocioPeriodoJustificacionDocumentoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ProyectoSocioPeriodoJustificacionDocumentoNotFoundException.class);
  }

  ProyectoSocioPeriodoJustificacion generarMockProyectoSocioPeriodoJustificacion(
      Long proyectoSocioPeriodoJustificacionId) {
    return ProyectoSocioPeriodoJustificacion.builder().id(proyectoSocioPeriodoJustificacionId).build();
  }

  /**
   * Función que devuelve un objeto ProyectoSocioPeriodoJustificacionDocumento.
   * 
   * @param id identificador
   * @return el objeto ProyectoSocioPeriodoJustificacionDocumento
   */
  private ProyectoSocioPeriodoJustificacionDocumento generarMockProyectoSocioPeriodoJustificacionDocumento(Long id,
      Long proyectoSocioPeriodoJustificacionId) {
    TipoDocumento tipoDocumento = TipoDocumento.builder().nombre("tipo1").activo(Boolean.TRUE).build();
    ProyectoSocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacionDocumento = ProyectoSocioPeriodoJustificacionDocumento
        .builder().id(id).nombre("nombre-" + id).comentario("comentario").documentoRef("001")
        .proyectoSocioPeriodoJustificacionId(proyectoSocioPeriodoJustificacionId).tipoDocumento(tipoDocumento)
        .visible(Boolean.TRUE).build();

    return proyectoSocioPeriodoJustificacionDocumento;
  }

}

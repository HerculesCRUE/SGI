package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioPeriodoPagoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoPagoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoSocioPeriodoPagoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ProyectoSocioPeriodoPagoServiceTest
 */
public class ProyectoSocioPeriodoPagoServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoSocioPeriodoPagoRepository repository;

  @Mock
  private ProyectoSocioRepository proyectoSocioRepository;

  private ProyectoSocioPeriodoPagoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ProyectoSocioPeriodoPagoServiceImpl(repository, proyectoSocioRepository);
  }

  @Test
  public void update_ReturnsProyectoSocioPeriodoPago() {
    // given: una lista con uno de los ProyectoSocioPeriodoPago actualizado,
    // otro nuevo y sin el otros existente
    Long proyectoSocioId = 1L;
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(proyectoSocioId);

    List<ProyectoSocioPeriodoPago> proyectoSocioPeriodoPagoExistentes = new ArrayList<>();
    proyectoSocioPeriodoPagoExistentes.add(generarMockProyectoSocioPeriodoPago(2L));
    proyectoSocioPeriodoPagoExistentes.add(generarMockProyectoSocioPeriodoPago(4L));
    proyectoSocioPeriodoPagoExistentes.add(generarMockProyectoSocioPeriodoPago(5L));

    ProyectoSocioPeriodoPago newProyectoSocioPeriodoPago = generarMockProyectoSocioPeriodoPago(null);
    ProyectoSocioPeriodoPago updatedProyectoSocioPeriodoPago = generarMockProyectoSocioPeriodoPago(4L);

    List<ProyectoSocioPeriodoPago> proyectoSocioPeriodoPagoActualizar = new ArrayList<>();
    proyectoSocioPeriodoPagoActualizar.add(newProyectoSocioPeriodoPago);
    proyectoSocioPeriodoPagoActualizar.add(updatedProyectoSocioPeriodoPago);

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocio));

    BDDMockito.given(repository.findAllByProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(proyectoSocioPeriodoPagoExistentes);

    BDDMockito.doNothing().when(repository).deleteAll(ArgumentMatchers.<ProyectoSocioPeriodoPago>anyList());

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ProyectoSocioPeriodoPago>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<ProyectoSocioPeriodoPago> periodosPagos = invocation.getArgument(0);
          return periodosPagos.stream().map(periodoPago -> {
            if (periodoPago.getId() == null) {
              periodoPago.setId(6L);
            }
            periodoPago.setProyectoSocioId(proyectoSocioId);
            return periodoPago;
          }).collect(Collectors.toList());
        });

    // when: se actualiza proyectoSocioPeriodoPagoActualizar
    List<ProyectoSocioPeriodoPago> periodosPagoActualizados = service.update(proyectoSocioId,
        proyectoSocioPeriodoPagoActualizar);

    // then: El ProyectoSocioPeriodoPago se actualiza correctamente.

    // then: Se crea el nuevo ProyectoSocioPeriodoPago, se actualiza el
    // existe y se elimina el otro
    Assertions.assertThat(periodosPagoActualizados.get(0).getId()).as("get(0).getId()").isEqualTo(6L);
    Assertions.assertThat(periodosPagoActualizados.get(0).getProyectoSocioId()).as("get(0).getProyectoSocioId()")
        .isEqualTo(proyectoSocioId);
    Assertions.assertThat(periodosPagoActualizados.get(0).getImporte()).as("get(0).getImporte()")
        .isEqualTo(newProyectoSocioPeriodoPago.getImporte());
    Assertions.assertThat(periodosPagoActualizados.get(0).getNumPeriodo()).as("get(0).getNumPeriodo()")
        .isEqualTo(newProyectoSocioPeriodoPago.getNumPeriodo());
    Assertions.assertThat(periodosPagoActualizados.get(0).getFechaPago()).as("get(0).getFechaPago()")
        .isEqualTo(newProyectoSocioPeriodoPago.getFechaPago());
    Assertions.assertThat(periodosPagoActualizados.get(0).getFechaPrevistaPago()).as("get(0).getFechaPrevistaPago()")
        .isEqualTo(newProyectoSocioPeriodoPago.getFechaPrevistaPago());

    Assertions.assertThat(periodosPagoActualizados.get(1).getId()).as("get(1).getId()")
        .isEqualTo(updatedProyectoSocioPeriodoPago.getId());
    Assertions.assertThat(periodosPagoActualizados.get(1).getProyectoSocioId())
        .as("get(1).getSolicitudProyectoSocioId()").isEqualTo(proyectoSocioId);
    Assertions.assertThat(periodosPagoActualizados.get(1).getImporte()).as("get(1).getImporte()")
        .isEqualTo(updatedProyectoSocioPeriodoPago.getImporte());
    Assertions.assertThat(periodosPagoActualizados.get(1).getNumPeriodo()).as("get(1).getNumPeriodo()")
        .isEqualTo(updatedProyectoSocioPeriodoPago.getNumPeriodo());
    Assertions.assertThat(periodosPagoActualizados.get(1).getFechaPago()).as("get(1).getFechaPago()")
        .isEqualTo(updatedProyectoSocioPeriodoPago.getFechaPago());
    Assertions.assertThat(periodosPagoActualizados.get(0).getFechaPrevistaPago()).as("get(1).getFechaPrevistaPago()")
        .isEqualTo(newProyectoSocioPeriodoPago.getFechaPrevistaPago());

    Mockito.verify(repository, Mockito.times(1)).deleteAll(ArgumentMatchers.<ProyectoSocioPeriodoPago>anyList());
    Mockito.verify(repository, Mockito.times(1)).saveAll(ArgumentMatchers.<ProyectoSocioPeriodoPago>anyList());

  }

  @Test
  public void update_WithtudProyectoSocioNotExist_ThrowsProyectoSocioNotFoundException() {
    // given: a ProyectoSocioPeriodoPago with non existing
    // ProyectoSocio
    Long proyectoSocioId = 1L;
    ProyectoSocioPeriodoPago proyectoSocioPeriodoPago = generarMockProyectoSocioPeriodoPago(1L);

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ProyectoSocioPeriodoPago
        () -> service.update(proyectoSocioId, Arrays.asList(proyectoSocioPeriodoPago)))
        // then: throw exception as ProyectoSocio is not found
        .isInstanceOf(ProyectoSocioNotFoundException.class);
  }

  @Test
  public void update_WithIdNotExist_ThrowsProyectoSocioPeriodoPagoNotFoundException() {
    // given: Un ProyectoSocioPeriodoPago actualizado con un id que no existe
    Long solicitudProyectoSocioId = 1L;
    ProyectoSocioPeriodoPago proyectoPeriodoPago = generarMockProyectoSocioPeriodoPago(1L);
    Long proyectoSocioId = 1L;
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(proyectoSocioId);

    List<ProyectoSocioPeriodoPago> proyectoPeriodoPagoExistentes = new ArrayList<>();
    proyectoPeriodoPagoExistentes.add(generarMockProyectoSocioPeriodoPago(3L));

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocio));

    BDDMockito.given(repository.findAllByProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(proyectoPeriodoPagoExistentes);

    // when: Actualizamos el ProyectoSocioPeriodoPago
    // then: Lanza una excepcion porque el ProyectoSocioPeriodoPago no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoSocioId, Arrays.asList(proyectoPeriodoPago)))
        .isInstanceOf(ProyectoSocioPeriodoPagoNotFoundException.class);
  }

  @Test
  public void update_WithProyectoSocioChange_ThrowsIllegalArgumentException() {
    // given:Se actualiza ProyectoSocio
    Long proyectoSocioId = 1L;
    ProyectoSocioPeriodoPago proyectoPeriodoPago = generarMockProyectoSocioPeriodoPago(1L);
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(proyectoSocioId);

    proyectoPeriodoPago.setProyectoSocioId(2L);

    List<ProyectoSocioPeriodoPago> proyectoPeriodoPagoExistentes = new ArrayList<>();
    proyectoPeriodoPagoExistentes.add(generarMockProyectoSocioPeriodoPago(1L));

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocio));

    BDDMockito.given(repository.findAllByProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(proyectoPeriodoPagoExistentes);

    // when: Actualizamos el ProyectoSocio del ProyectoSocioPeriodoPago
    // then: Lanza una excepcion porque no se puede modificar el campo
    // ProyectoSocio
    Assertions.assertThatThrownBy(() -> service.update(proyectoSocioId, Arrays.asList(proyectoPeriodoPago)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("No se puede modificar el proyecto socio del ProyectoSocioPeriodoPago");
  }

  @Test
  public void findById_ReturnsProyectoSocioPeriodoPago() {
    // given: Un ProyectoSocioPeriodoPago con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockProyectoSocioPeriodoPago(idBuscado)));

    // when: Buscamos el ProyectoSocioPeriodoPago por su id
    ProyectoSocioPeriodoPago proyectoPeriodoPago = service.findById(idBuscado);

    // then: el ProyectoSocioPeriodoPago
    Assertions.assertThat(proyectoPeriodoPago).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoPeriodoPago.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsProyectoSocioPeriodoPagoNotFoundException() throws Exception {
    // given: Ningun ProyectoSocioPeriodoPago con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ProyectoSocioPeriodoPago por su id
    // then: lanza un ProyectoSocioPeriodoPagoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ProyectoSocioPeriodoPagoNotFoundException.class);
  }

  @Test
  public void findAllByProyectoSocio_ReturnsPage() {
    // given: Una lista con 37 ProyectoSocioPeriodoPago
    Long solicitudId = 1L;
    List<ProyectoSocioPeriodoPago> proyectoPeriodoPago = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoPeriodoPago.add(generarMockProyectoSocioPeriodoPago(i));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoSocioPeriodoPago>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ProyectoSocioPeriodoPago>>() {
          @Override
          public Page<ProyectoSocioPeriodoPago> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectoPeriodoPago.size() ? proyectoPeriodoPago.size() : toIndex;
            List<ProyectoSocioPeriodoPago> content = proyectoPeriodoPago.subList(fromIndex, toIndex);
            Page<ProyectoSocioPeriodoPago> page = new PageImpl<>(content, pageable, proyectoPeriodoPago.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoSocioPeriodoPago> page = service.findAllByProyectoSocio(solicitudId, null, paging);

    // then: Devuelve la pagina 3 con los ProyectoSocioPeriodoPAgo del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoSocioPeriodoPago proyectoPeriodoPagoRecuperado = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyectoPeriodoPagoRecuperado.getId()).isEqualTo(i);
    }
  }

  private ProyectoSocio generarMockProyectoSocio(Long id) {
    // @formatter:off
    RolSocio rolSocio = RolSocio.builder()
        .id(id).abreviatura("001")
        .nombre("nombre-001")
        .descripcion("descripcion-001")
        .coordinador(Boolean.FALSE)
        .activo(Boolean.TRUE)
        .build();

    ProyectoSocio proyectoSocio = ProyectoSocio.builder()
        .id(id)
        .proyectoId(id)
        .empresaRef("empresa-0041")
        .rolSocio(rolSocio)
        .build();
    // @formatter:on

    return proyectoSocio;
  }

  /**
   * Función que devuelve un objeto ProyectoSocioPeriodoPago
   * 
   * @param id id del ProyectoSocioPeriodoPago
   * 
   * @return el objeto ProyectoSocioPeriodoPago
   */
  private ProyectoSocioPeriodoPago generarMockProyectoSocioPeriodoPago(Long id) {

    ProyectoSocioPeriodoPago proyectoSocioPeriodoPago = new ProyectoSocioPeriodoPago(id, id, 1, new BigDecimal(3500),
        Instant.parse("2021-04-10T00:00:00Z"), null);

    return proyectoSocioPeriodoPago;
  }

}

package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoProrrogaNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.repository.ProrrogaDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoProrrogaRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoResponsableEconomicoRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoProrrogaServiceImpl;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * ProyectoProrrogaServiceTest
 */

class ProyectoProrrogaServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoProrrogaRepository repository;
  @Mock
  private ProyectoRepository proyectoRepository;
  @Mock
  private ProrrogaDocumentoRepository prorrogaDocumentoRepository;
  @Mock
  private ProyectoEquipoRepository proyectoEquipoRepository;
  @Mock
  private ProyectoResponsableEconomicoRepository proyectoResponsableEconomicoRepository;
  @Mock
  private ProyectoHelper proyectoHelper;

  private ProyectoProrrogaService service;

  @BeforeEach
  void setUp() throws Exception {
    service = new ProyectoProrrogaServiceImpl(repository, proyectoRepository, prorrogaDocumentoRepository,
        proyectoEquipoRepository, proyectoHelper);
  }

  @Test
  void create_ReturnsProyectoProrroga() {
    // given: Un nuevo ProyectoProrroga
    ProyectoProrroga proyectoProrrogaAnterior = generarMockProyectoProrroga(1L, 1L);
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(2L, 1L);
    proyectoProrroga.setFechaConcesion(proyectoProrrogaAnterior.getFechaConcesion().plus(Period.ofDays(1)));
    proyectoProrroga.setId(null);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyecto(1L)));
    BDDMockito.given(repository.findFirstByProyectoIdOrderByFechaConcesionDesc(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoProrrogaAnterior));
    BDDMockito.given(repository.getProyecto(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyecto(1L)));

    BDDMockito.given(repository.save(proyectoProrroga)).will((InvocationOnMock invocation) -> {
      ProyectoProrroga proyectoProrrogaCreado = invocation.getArgument(0);
      proyectoProrrogaCreado.setId(1L);
      return proyectoProrrogaCreado;
    });

    // when: Creamos el ProyectoProrroga
    ProyectoProrroga proyectoProrrogaCreado = service.create(proyectoProrroga);

    // then: El ProyectoProrroga se crea correctamente
    Assertions.assertThat(proyectoProrrogaCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoProrrogaCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoProrrogaCreado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoProrroga.getProyectoId());
    Assertions.assertThat(proyectoProrrogaCreado.getNumProrroga()).as("getNumProrroga()")
        .isEqualTo(proyectoProrroga.getNumProrroga());
    Assertions.assertThat(proyectoProrrogaCreado.getFechaConcesion()).as("getFechaConcesion()")
        .isEqualTo(proyectoProrroga.getFechaConcesion());
    Assertions.assertThat(proyectoProrrogaCreado.getTipo()).as("getTipo()").isEqualTo(proyectoProrroga.getTipo());
    Assertions.assertThat(proyectoProrrogaCreado.getFechaFin()).as("getFechaFin()")
        .isEqualTo(proyectoProrroga.getFechaFin());
    Assertions.assertThat(proyectoProrrogaCreado.getImporte()).as("getImporte()")
        .isEqualTo(proyectoProrroga.getImporte());
    Assertions.assertThat(proyectoProrrogaCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyectoProrroga.getObservaciones());
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ProyectoProrroga que ya tiene id
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    // when: Creamos el ProyectoProrroga
    // then: Lanza una excepcion porque el ProyectoProrroga ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(proyectoProrroga)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ProyectoProrroga id tiene que ser null para crear un nuevo ProyectoProrroga");
  }

  @Test
  void create_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoProrroga without ProyectoId
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setId(null);
    proyectoProrroga.setProyectoId(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoProrroga
        () -> service.create(proyectoProrroga))
        // then: throw exception as ProyectoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Proyecto no puede ser null para realizar la acción sobre ProyectoProrroga");
  }

  @Test
  void create_WithoutNumProrroga_ThrowsIllegalArgumentException() {
    // given: a ProyectoProrroga without NumProrroga
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setId(null);
    proyectoProrroga.setNumProrroga(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoProrroga
        () -> service.create(proyectoProrroga))
        // then: throw exception as NumProrroga is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Número de prórroga no puede ser null para realizar la acción sobre ProyectoProrroga");
  }

  @Test
  void create_WithoutTipoProrroga_ThrowsIllegalArgumentException() {
    // given: a ProyectoProrroga without TipoProrroga
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setId(null);
    proyectoProrroga.setTipo(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoProrroga
        () -> service.create(proyectoProrroga))
        // then: throw exception as TipoProrroga is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tipo prórroga no puede ser null para realizar la acción sobre ProyectoProrroga");
  }

  @Test
  void create_WithoutFechaConcesion_ThrowsIllegalArgumentException() {
    // given: a ProyectoProrroga without FechaConcesion
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setId(null);
    proyectoProrroga.setFechaConcesion(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoProrroga
        () -> service.create(proyectoProrroga))
        // then: throw exception as FechaConcesion is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Fecha concesión no puede ser null para realizar la acción sobre ProyectoProrroga");
  }

  @Test
  void create_WithTipoTiempoAndWithoutFechaFin_ThrowsIllegalArgumentException() {
    // given: a ProyectoProrroga tipo Tiempo without FechaFin
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setId(null);
    proyectoProrroga.setTipo(ProyectoProrroga.Tipo.TIEMPO_IMPORTE);
    proyectoProrroga.setFechaFin(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoProrroga
        () -> service.create(proyectoProrroga))
        // then: throw exception as FechaFin is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Nueva fecha fin proyecto no puede ser null para  para realizar la acción sobre ProyectoProrroga");
  }

  @Test
  void create_WithTipoImporteAndWithoutImporte_ThrowsIllegalArgumentException() {
    // given: a ProyectoProrroga tipo Tiempo without Importe
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setId(null);
    proyectoProrroga.setTipo(ProyectoProrroga.Tipo.TIEMPO_IMPORTE);
    proyectoProrroga.setImporte(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoProrroga
        () -> service.create(proyectoProrroga))
        // then: throw exception as Importe is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Importe debe tener un valor para realizar la acción sobre ProyectoProrroga");
  }

  @Test
  void create_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoProrroga with non existing Proyecto
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setId(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoProrroga
        () -> service.create(proyectoProrroga))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  void create_WithFechaConcesionBeforePreviousProrroga_ThrowsIllegalArgumentException() {
    // given: a ProyectoProrroga with fechaConcesion anterior a la última prórroga
    ProyectoProrroga proyectoProrrogaAnterior = generarMockProyectoProrroga(1L, 1L);
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(2L, 1L);
    proyectoProrroga.setFechaConcesion(proyectoProrrogaAnterior.getFechaConcesion().minus(Period.ofDays(1)));
    proyectoProrroga.setId(null);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyecto(1L)));
    BDDMockito.given(repository.findFirstByProyectoIdOrderByFechaConcesionDesc(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoProrrogaAnterior));

    Assertions.assertThatThrownBy(
        // when: create ProyectoProrroga
        () -> service.create(proyectoProrroga))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Fecha de concesión debe ser posterior a la de la última prórroga");
  }

  @Test
  void create_FechaFinBeforeProyectoFechaFin_ThrowsIllegalArgumentException() {
    // given: Fecha Fin anterior a la de inicio del proyecto
    Proyecto proyecto = generarMockProyecto(1L);
    ProyectoProrroga proyectoProrrogaAnterior = generarMockProyectoProrroga(1L, 1L);
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(2L, 1L);
    proyectoProrroga.setFechaConcesion(proyectoProrrogaAnterior.getFechaConcesion().plus(Period.ofDays(1)));
    proyectoProrroga.setFechaFin(proyecto.getFechaInicio().minus(Period.ofDays(1)));
    proyectoProrroga.setId(null);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyecto(1L)));

    Assertions.assertThatThrownBy(
        // when: create ProyectoProrroga
        () -> service.create(proyectoProrroga))
        // then: IllegalArgumentException is thrown
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Fecha de fin debe ser posterior a la fecha de fin del proyecto");
  }

  @Test
  void update_ReturnsProyectoProrroga() {
    // given: Un nuevo ProyectoProrroga con el tipoProrroga actualizado
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    ProyectoProrroga proyectoProrrogaAnterior = generarMockProyectoProrroga(2L, 1L);
    proyectoProrrogaAnterior.setFechaConcesion(proyectoProrroga.getFechaConcesion().minus(Period.ofDays(1)));
    ProyectoProrroga proyectoProrrogaActualizado = generarMockProyectoProrroga(1L, 1L);
    proyectoProrrogaActualizado.setFechaFin(proyectoProrrogaActualizado.getFechaFin().plus(Period.ofDays(1)));
    proyectoProrrogaActualizado.setObservaciones("observaciones-modificada");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoProrroga));

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyecto(1L)));
    BDDMockito.given(repository.findFirstByProyectoIdOrderByFechaConcesionDesc(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoProrroga));
    BDDMockito.given(repository.findFirstByIdNotAndProyectoIdOrderByFechaConcesionDesc(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoProrrogaAnterior));
    BDDMockito.given(repository.getProyecto(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyecto(1L)));

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoProrroga>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ProyectoProrroga
    ProyectoProrroga updated = service.update(proyectoProrrogaActualizado);

    // then: El ProyectoProrroga se actualiza correctamente.
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(proyectoProrroga.getId());
    Assertions.assertThat(updated.getProyectoId()).as("getProyectoId()").isEqualTo(proyectoProrroga.getProyectoId());
    Assertions.assertThat(updated.getNumProrroga()).as("getNumProrroga()").isEqualTo(proyectoProrroga.getNumProrroga());
    Assertions.assertThat(updated.getFechaConcesion()).as("getFechaConcesion()")
        .isEqualTo(proyectoProrroga.getFechaConcesion());
    Assertions.assertThat(updated.getTipo()).as("getTipo()").isEqualTo(proyectoProrroga.getTipo());
    Assertions.assertThat(updated.getFechaFin()).as("getFechaFin()").isEqualTo(proyectoProrroga.getFechaFin());
    Assertions.assertThat(updated.getImporte()).as("getImporte()").isEqualTo(proyectoProrroga.getImporte());
    Assertions.assertThat(updated.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyectoProrrogaActualizado.getObservaciones());
  }

  @Test
  void update_WithIdNotExist_ThrowsProyectoProrrogaNotFoundException() {
    // given: Un ProyectoProrroga a actualizar con un id que no existe
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el ProyectoProrroga
    // then: Lanza una excepcion porque el ProyectoProrroga no existe
    Assertions.assertThatThrownBy(() -> service.update(proyectoProrroga))
        .isInstanceOf(ProyectoProrrogaNotFoundException.class);
  }

  @Test
  void update_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoProrroga without ProyectoId
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setObservaciones("observaciones-modificada");
    proyectoProrroga.setProyectoId(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoProrroga
        () -> service.update(proyectoProrroga))
        // then: throw exception as ProyectoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Proyecto no puede ser null para realizar la acción sobre ProyectoProrroga");
  }

  @Test
  void update_WithoutNumProrroga_ThrowsIllegalArgumentException() {
    // given: a ProyectoProrroga without NumProrroga
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setObservaciones("observaciones-modificada");
    proyectoProrroga.setNumProrroga(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoProrroga
        () -> service.update(proyectoProrroga))
        // then: throw exception as NumProrroga is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Número de prórroga no puede ser null para realizar la acción sobre ProyectoProrroga");
  }

  @Test
  void update_WithoutTipoProrroga_ThrowsIllegalArgumentException() {
    // given: a ProyectoProrroga without TipoProrroga
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setObservaciones("observaciones-modificada");
    proyectoProrroga.setTipo(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoProrroga
        () -> service.update(proyectoProrroga))
        // then: throw exception as TipoProrroga is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tipo prórroga no puede ser null para realizar la acción sobre ProyectoProrroga");
  }

  @Test
  void update_WithoutFechaConcesion_ThrowsIllegalArgumentException() {
    // given: a ProyectoProrroga without FechaConcesion
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setObservaciones("observaciones-modificada");
    proyectoProrroga.setFechaConcesion(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoProrroga
        () -> service.update(proyectoProrroga))
        // then: throw exception as FechaConcesion is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Fecha concesión no puede ser null para realizar la acción sobre ProyectoProrroga");
  }

  @Test
  void update_WithTipoTiempoAndWithoutFechaFin_ThrowsIllegalArgumentException() {
    // given: a ProyectoProrroga without FechaFin
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setObservaciones("observaciones-modificada");
    proyectoProrroga.setTipo(ProyectoProrroga.Tipo.TIEMPO_IMPORTE);
    proyectoProrroga.setFechaFin(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoProrroga
        () -> service.update(proyectoProrroga))
        // then: throw exception as FechaFin is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Nueva fecha fin proyecto no puede ser null para  para realizar la acción sobre ProyectoProrroga");
  }

  @Test
  void update_WithTipoImporteAndWithoutImporte_ThrowsIllegalArgumentException() {
    // given: a ProyectoProrroga without Importe
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setObservaciones("observaciones-modificada");
    proyectoProrroga.setTipo(ProyectoProrroga.Tipo.TIEMPO_IMPORTE);
    proyectoProrroga.setImporte(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoProrroga
        () -> service.update(proyectoProrroga))
        // then: throw exception as FechaFin is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Importe debe tener un valor para realizar la acción sobre ProyectoProrroga");
  }

  @Test
  void update_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoProrroga with non existing Proyecto
    ProyectoProrroga proyectoProrrogaOriginal = generarMockProyectoProrroga(1L, 1L);
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setObservaciones("observaciones-modificada");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoProrrogaOriginal));

    Assertions.assertThatThrownBy(
        // when: update ProyectoProrroga
        () -> service.update(proyectoProrroga))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  void update_NotUltimaProrroga_ThrowsIllegalArgumentException() throws Exception {
    // given: Not ultimo ProyectoProrroga
    ProyectoProrroga proyectoProrrogaOriginal = generarMockProyectoProrroga(1L, 1L);
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(1L, 1L);
    proyectoProrroga.setObservaciones("observaciones-modificada");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoProrrogaOriginal));
    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyecto(1L)));
    BDDMockito.given(repository.findFirstByProyectoIdOrderByFechaConcesionDesc(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyectoProrroga(2L, 1L)));

    Assertions.assertThatThrownBy(
        // when: update ProyectoProrroga
        () -> service.update(proyectoProrroga))
        // then: IllegalArgumentException is thrown
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Sólo se permite modificar la última prórroga");
  }

  @Test
  void update_FechaConcesionNotBeforePreviousProrroga_ThrowsIllegalArgumentException() throws Exception {
    // given: Fecha de concesión anterior a la de la última prorroga
    ProyectoProrroga proyectoProrrogaAnterior = generarMockProyectoProrroga(1L, 1L);
    ProyectoProrroga proyectoProrrogaOriginal = generarMockProyectoProrroga(2L, 1L);
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(2L, 1L);
    proyectoProrroga.setFechaConcesion(proyectoProrrogaAnterior.getFechaConcesion().minus(Period.ofDays(1)));
    proyectoProrroga.setObservaciones("observaciones-modificada");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoProrrogaOriginal));
    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyecto(1L)));
    BDDMockito.given(repository.findFirstByProyectoIdOrderByFechaConcesionDesc(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoProrrogaOriginal));
    BDDMockito.given(repository.findFirstByIdNotAndProyectoIdOrderByFechaConcesionDesc(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoProrrogaAnterior));

    Assertions.assertThatThrownBy(
        // when: update ProyectoProrroga
        () -> service.update(proyectoProrroga))
        // then: IllegalArgumentException is thrown
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Fecha de concesión debe ser posterior a la de la última prórroga");
  }

  @Test
  void update_FechaFinBeforeProyectoFechaFin_ThrowsIllegalArgumentException() throws Exception {
    // given: Fecha Fin anterior a la de inicio del proyecto
    Proyecto proyecto = generarMockProyecto(1L);
    ProyectoProrroga proyectoProrrogaOriginal = generarMockProyectoProrroga(2L, 1L);
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(2L, 1L);
    proyectoProrroga.setFechaFin(proyecto.getFechaInicio().minus(Period.ofDays(1)));
    proyectoProrroga.setObservaciones("observaciones-modificada");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoProrrogaOriginal));
    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyecto(1L)));

    Assertions.assertThatThrownBy(
        // when: update ProyectoProrroga
        () -> service.update(proyectoProrroga))
        // then: IllegalArgumentException is thrown
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Fecha de fin debe ser posterior a la fecha de fin del proyecto");
  }

  @Test
  void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing ProyectoProrroga
    Long id = 1L;
    ProyectoProrroga proyectoProrroga = generarMockProyectoProrroga(id, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoProrroga));
    BDDMockito.given(repository.findFirstByProyectoIdOrderByFechaConcesionDesc(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoProrroga));

    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.<Long>any());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoProrrogaNotFoundException.class);
  }

  @Test
  void delete_NotUltimaProrroga_ThrowsIllegalArgumentException() throws Exception {
    // given: Not ultimo ProyectoProrroga
    Long id = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyectoProrroga(id, 1L)));
    BDDMockito.given(repository.findFirstByProyectoIdOrderByFechaConcesionDesc(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockProyectoProrroga(2L, 1L)));

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: IllegalArgumentException is thrown
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Sólo se permite eliminar la última prórroga");
  }

  @Test
  void existsById_WithExistingId_ReturnsTRUE() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);

    // when: exists by id
    boolean responseData = service.existsById(id);

    // then: returns TRUE
    Assertions.assertThat(responseData).isTrue();
  }

  @Test
  void existsById_WithNoExistingId_ReturnsFALSE() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    // when: exists by id
    boolean responseData = service.existsById(id);

    // then: returns TRUE
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  void findById_ReturnsProyectoProrroga() {
    // given: Un ProyectoProrroga con el id buscado
    Long idBuscado = 1L;
    Long idProyecto = 1L;

    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockProyectoProrroga(idBuscado, idProyecto)));

    // when: Buscamos el ProyectoProrroga por su id
    ProyectoProrroga proyectoProrroga = service.findById(idBuscado);

    // then: el ProyectoProrroga
    Assertions.assertThat(proyectoProrroga).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoProrroga.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  void findById_WithIdNotExist_ThrowsProyectoProrrogaNotFoundException() throws Exception {
    // given: Ningun ProyectoProrroga con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ProyectoProrroga por su id
    // then: lanza un ProyectoProrrogaNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ProyectoProrrogaNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  void findAllByProyecto_ReturnsPage() {
    // given: Una lista con 37 ProyectoProrroga para la Proyecto
    Long proyectoId = 1L;
    List<ProyectoProrroga> proyectosEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectosEntidadesConvocantes.add(generarMockProyectoProrroga(Long.valueOf(i), proyectoId));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ProyectoProrroga>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectosEntidadesConvocantes.size() ? proyectosEntidadesConvocantes.size() : toIndex;
          List<ProyectoProrroga> content = proyectosEntidadesConvocantes.subList(fromIndex, toIndex);
          Page<ProyectoProrroga> pageResponse = new PageImpl<>(content, pageable, proyectosEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoProrroga> page = service.findAllByProyecto(proyectoId, null, paging);

    // then: Devuelve la pagina 3 con los ProyectoProrroga del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoProrroga proyectoProrroga = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyectoProrroga.getId()).isEqualTo(Long.valueOf(i));
    }
  }

  private Proyecto generarMockProyecto(Long proyectoId) {
    // @formatter:off
    return Proyecto.builder()
        .id(proyectoId)
        .fechaInicio(Instant.parse("2020-01-01T00:00:00Z"))
        .fechaFin(Instant.parse("2021-01-01T23:59:59Z"))
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto ProyectoProrroga
   * 
   * @param id         id del ProyectoProrroga
   * @param proyectoId id del Proyecto
   * @return el objeto ProyectoProrroga
   */
  private ProyectoProrroga generarMockProyectoProrroga(Long id, Long proyectoId) {

    // @formatter:off
    return ProyectoProrroga.builder()
        .id(id)
        .proyectoId(proyectoId)
        .numProrroga(1)
        .fechaConcesion(Instant.parse("2020-01-01T00:00:00Z"))
        .tipo(ProyectoProrroga.Tipo.TIEMPO_IMPORTE)
        .fechaFin(Instant.parse("2021-12-01T23:59:59Z"))
        .importe(BigDecimal.valueOf(123.45))
        .observaciones("observaciones-proyecto-prorroga-" + (id == null ? "" : String.format("%03d", id)))
        .build();
    // @formatter:on
  }
}

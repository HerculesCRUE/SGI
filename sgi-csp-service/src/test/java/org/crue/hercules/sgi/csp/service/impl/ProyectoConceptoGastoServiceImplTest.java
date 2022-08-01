package org.crue.hercules.sgi.csp.service.impl;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.repository.ConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.crue.hercules.sgi.csp.service.ProyectoConceptoGastoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;

class ProyectoConceptoGastoServiceImplTest extends BaseServiceTest {

  @Mock
  private ProyectoConceptoGastoRepository repository;
  @Mock
  private ProyectoRepository proyectoRepository;
  @Mock
  private ConceptoGastoRepository conceptoGastoRepository;
  @Mock
  private ProyectoConceptoGastoCodigoEcRepository proyectoConceptoGastoCodigoEcRepository;
  @Mock
  private ConvocatoriaConceptoGastoCodigoEcRepository convocatoriaConceptoGastoCodigoEcRepository;

  private ProyectoConceptoGastoService service;

  @BeforeEach
  void setup() {
    // @formatter=off
    this.service = new ProyectoConceptoGastoServiceImpl(repository,
        proyectoRepository,
        conceptoGastoRepository,
        proyectoConceptoGastoCodigoEcRepository,
        convocatoriaConceptoGastoCodigoEcRepository);
    // @formatter=on
  }

  @Test
  void create_ThrowsConceptoGastoNotFoundException() {
    ConceptoGasto conceptoGasto = this.buildMockConceptoGasto(1L, "testing");
    ProyectoConceptoGasto proyectoConceptoGasto = this.buildMockProyectoConceptoGasto(null, 1L, conceptoGasto, null,
        null);

    Assertions.assertThatThrownBy(() -> this.service.create(proyectoConceptoGasto)).isInstanceOf(
        ConceptoGastoNotFoundException.class);
  }

  @Test
  void create_WithFechaInicioAfterFechaFin_ThrowsIllegalArgumentException() {
    Long proyectoId = 1L;
    Proyecto proyecto = this.buildMockProyecto(proyectoId, Instant.now(), Instant.now().plusSeconds(36000000));
    ConceptoGasto conceptoGasto = this.buildMockConceptoGasto(null, "testing");
    ProyectoConceptoGasto proyectoConceptoGasto = this.buildMockProyectoConceptoGasto(null, proyectoId, conceptoGasto,
        Instant.now().plusSeconds(46000000), null);

    BDDMockito.given(this.proyectoRepository.findById(proyectoConceptoGasto.getProyectoId()))
        .willReturn(Optional.of(proyecto));

    Assertions.assertThatThrownBy(() -> this.service.create(proyectoConceptoGasto)).isInstanceOf(
        IllegalArgumentException.class)
        .hasMessage("La fecha de inicio no puede ser posterior a la fecha de fin del proyecto");
  }

  @Test
  void update_ThrowsConceptoGastoNotFoundException() {
    ConceptoGasto conceptoGasto = this.buildMockConceptoGasto(1L, "testing");
    ProyectoConceptoGasto proyectoConceptoGasto = this.buildMockProyectoConceptoGasto(1L, 1L, conceptoGasto, null,
        null);

    Assertions.assertThatThrownBy(() -> this.service.update(proyectoConceptoGasto)).isInstanceOf(
        ConceptoGastoNotFoundException.class);
  }

  @Test
  void update_WithFechaInicioAfterFechaFin_ThrowsIllegalArgumentException() {
    Long proyectoId = 1L;
    Proyecto proyecto = this.buildMockProyecto(proyectoId, Instant.now(), Instant.now().plusSeconds(36000000));
    ConceptoGasto conceptoGasto = this.buildMockConceptoGasto(null, "testing");
    ProyectoConceptoGasto proyectoConceptoGasto = this.buildMockProyectoConceptoGasto(1L, proyectoId, conceptoGasto,
        Instant.now().plusSeconds(46000000), null);

    BDDMockito.given(this.proyectoRepository.findById(proyectoConceptoGasto.getProyectoId()))
        .willReturn(Optional.of(proyecto));

    Assertions.assertThatThrownBy(() -> this.service.update(proyectoConceptoGasto)).isInstanceOf(
        IllegalArgumentException.class)
        .hasMessage("La fecha de inicio no puede ser posterior a la fecha de fin del proyecto");
  }

  @Test
  void delete_VerificationSuccess() {
    List<ProyectoConceptoGastoCodigoEc> codigosEconomicos = Arrays
        .asList(this.buildMockProyectoConceptoGastoCodigoEc(1L, null, null));

    BDDMockito.given(this.repository.existsById(1L)).willReturn(true);

    BDDMockito.given(proyectoConceptoGastoCodigoEcRepository.findAllByProyectoConceptoGastoId(anyLong()))
        .willReturn(codigosEconomicos);

    this.service.delete(1L);

    verify(this.proyectoConceptoGastoCodigoEcRepository, times(1)).deleteById(1L);
    verify(this.repository, times(1)).deleteById(1L);
  }

  @Test
  void hasDifferencesCodigosEcConvocatoria_ReturnsTrue() {
    ProyectoConceptoGasto proyectoConceptoGasto = this.buildMockProyectoConceptoGasto(1L, 1L, null,
        Instant.now().plusSeconds(46000000), null);
    List<ProyectoConceptoGastoCodigoEc> conceptosGastoProyecto = Arrays
        .asList(this.buildMockProyectoConceptoGastoCodigoEc(1L, Instant.now(), Instant.now().plusSeconds(54333000)));
    conceptosGastoProyecto.get(0).setConvocatoriaConceptoGastoCodigoEcId(1L);
    conceptosGastoProyecto.get(0).setCodigoEconomicoRef("AA.BBBB");
    proyectoConceptoGasto.setConvocatoriaConceptoGastoId(1L);

    ConvocatoriaConceptoGastoCodigoEc convocatoriaCodigoEc = buildMockConvocatoriaConceptoGastoCodigoEc(1L,
        Instant.now(), Instant.now().plusSeconds(36000000));
    convocatoriaCodigoEc.setCodigoEconomicoRef("BB.DDDD");
    List<ConvocatoriaConceptoGastoCodigoEc> conceptosGastoConvocatoria = Arrays.asList(convocatoriaCodigoEc);

    BDDMockito.given(this.repository.findById(anyLong())).willReturn(Optional.of(proyectoConceptoGasto));
    BDDMockito.given(this.proyectoConceptoGastoCodigoEcRepository.findAllByProyectoConceptoGastoId(anyLong()))
        .willReturn(conceptosGastoProyecto);
    BDDMockito.given(this.convocatoriaConceptoGastoCodigoEcRepository.findAllByConvocatoriaConceptoGastoId(anyLong()))
        .willReturn(conceptosGastoConvocatoria);

    boolean response = this.service.hasDifferencesCodigosEcConvocatoria(1L);

    Assertions.assertThat(response).isTrue();

  }

  private ProyectoConceptoGasto buildMockProyectoConceptoGasto(Long id, Long proyectoId, ConceptoGasto conceptoGasto,
      Instant fechaInicio, Instant fechaFin) {
    return ProyectoConceptoGasto.builder()
        .id(id)
        .proyectoId(proyectoId)
        .conceptoGasto(conceptoGasto)
        .fechaInicio(fechaInicio)
        .fechaFin(fechaFin)
        .build();
  }

  private ConceptoGasto buildMockConceptoGasto(Long id, String nombre) {
    return ConceptoGasto.builder()
        .id(id)
        .nombre(nombre)
        .build();
  }

  private Proyecto buildMockProyecto(Long id, Instant fechaInicio, Instant fechaFin) {
    return Proyecto.builder()
        .id(id)
        .fechaInicio(fechaInicio)
        .fechaFin(fechaFin)
        .build();
  }

  private ProyectoConceptoGastoCodigoEc buildMockProyectoConceptoGastoCodigoEc(Long id, Instant fechaInicio,
      Instant fechaFin) {
    return ProyectoConceptoGastoCodigoEc.builder()
        .id(id)
        .fechaInicio(fechaInicio)
        .fechaFin(fechaFin)
        .build();
  }

  private ConvocatoriaConceptoGastoCodigoEc buildMockConvocatoriaConceptoGastoCodigoEc(Long id, Instant fechaInicio,
      Instant fechaFin) {
    return ConvocatoriaConceptoGastoCodigoEc.builder()
        .id(id)
        .fechaInicio(fechaInicio)
        .fechaFin(fechaFin)
        .build();
  }
}
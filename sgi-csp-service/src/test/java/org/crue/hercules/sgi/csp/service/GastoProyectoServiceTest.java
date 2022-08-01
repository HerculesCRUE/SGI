package org.crue.hercules.sgi.csp.service;

import static org.mockito.ArgumentMatchers.anyLong;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.EstadoGastoProyecto;
import org.crue.hercules.sgi.csp.model.EstadoGastoProyecto.TipoEstadoGasto;
import org.crue.hercules.sgi.csp.model.GastoProyecto;
import org.crue.hercules.sgi.csp.repository.EstadoGastoProyectoRepository;
import org.crue.hercules.sgi.csp.repository.GastoProyectoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;

class GastoProyectoServiceTest extends BaseServiceTest {

  @Mock
  private GastoProyectoRepository gastoProyectoRepository;
  @Mock
  private EstadoGastoProyectoRepository estadoGastoProyectoRepository;

  private GastoProyectoService gastoProyectoService;

  @BeforeEach
  void setup() {
    this.gastoProyectoService = new GastoProyectoService(this.gastoProyectoRepository,
        this.estadoGastoProyectoRepository);
  }

  @Test
  void update_ReturnsGastoProyecto() {
    EstadoGastoProyecto estadoOriginal = buildModkEstadoGastoProyecto("estado original", TipoEstadoGasto.BLOQUEADO);
    EstadoGastoProyecto estadoChanged = buildModkEstadoGastoProyecto("estado cambiado", TipoEstadoGasto.VALIDADO);

    GastoProyecto gastoProyectoOriginal = buildMockGastoProyecto(1L, null, estadoOriginal,
        Instant.parse("2022-02-22T11:11:00.000Z"), new BigDecimal(666), "testing proyecto gasto actualizado");
    GastoProyecto gastoProyectoChanges = buildMockGastoProyecto(1L, buildMockConceptoGasto(), estadoChanged,
        Instant.parse("2022-03-22T11:11:00.000Z"), new BigDecimal(666), "testing proyecto gasto actualizado");

    BDDMockito.given(this.gastoProyectoRepository.findById(anyLong())).willReturn(Optional.of(gastoProyectoOriginal));
    BDDMockito.given(this.estadoGastoProyectoRepository.save(ArgumentMatchers.<EstadoGastoProyecto>any()))
        .willReturn(estadoChanged);
    BDDMockito.given(this.gastoProyectoRepository.save(ArgumentMatchers.<GastoProyecto>any()))
        .willReturn(gastoProyectoChanges);

    GastoProyecto finalGastoProyecto = this.gastoProyectoService.update(gastoProyectoChanges);

    Assertions.assertThat(finalGastoProyecto).isNotNull();
    Assertions.assertThat(finalGastoProyecto.getEstado()).isEqualTo(estadoChanged);
    Assertions.assertThat(finalGastoProyecto.getConceptoGasto()).isEqualTo(gastoProyectoChanges.getConceptoGasto());
    Assertions.assertThat(finalGastoProyecto.getFechaCongreso()).isEqualTo(gastoProyectoChanges.getFechaCongreso());
    Assertions.assertThat(finalGastoProyecto.getImporteInscripcion())
        .isEqualTo(gastoProyectoChanges.getImporteInscripcion());
    Assertions.assertThat(finalGastoProyecto.getObservaciones()).isEqualTo(gastoProyectoChanges.getObservaciones());
  }

  private GastoProyecto buildMockGastoProyecto(Long id, ConceptoGasto conceptoGasto, EstadoGastoProyecto estado,
      Instant fechaCongreso, BigDecimal importeInscripcion, String observaciones) {

    return GastoProyecto.builder()
        .id(id)
        .conceptoGasto(conceptoGasto)
        .estado(estado)
        .fechaCongreso(fechaCongreso)
        .importeInscripcion(importeInscripcion)
        .observaciones(observaciones)
        .build();
  }

  private ConceptoGasto buildMockConceptoGasto() {
    return ConceptoGasto.builder()
        .activo(Boolean.TRUE)
        .id(1L)
        .descripcion("Testing concepto gasto")
        .build();
  }

  private EstadoGastoProyecto buildModkEstadoGastoProyecto(String comentario, TipoEstadoGasto tipoEstado) {
    return EstadoGastoProyecto.builder()
        .comentario(comentario)
        .estado(tipoEstado)
        .build();
  }

}
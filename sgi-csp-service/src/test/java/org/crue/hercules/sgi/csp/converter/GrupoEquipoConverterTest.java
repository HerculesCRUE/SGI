package org.crue.hercules.sgi.csp.converter;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoInput;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoOutput;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo.Dedicacion;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.Instant;

class GrupoEquipoConverterTest extends BaseServiceTest {

  @Mock
  private ModelMapper modelMapper;

  private GrupoEquipoConverter grupoEquipoConverter;

  @BeforeEach
  void setup() {
    this.grupoEquipoConverter = new GrupoEquipoConverter(this.modelMapper);
  }

  @Test
  void convert_WithoutId_ReturnsGrupoEquipo() {
    final Long grupoId = 1L;
    final Dedicacion dedicacion = Dedicacion.COMPLETA;
    final Instant fechaInicio = Instant.now();
    final Instant fechaFin = Instant.now().plusSeconds(3600000);
    final BigDecimal participacion = new BigDecimal(100);
    final String personaRef = "48999343";
    final Long rolId = 1L;

    GrupoEquipoInput input = this.buildMockGrupoEquipoInput(grupoId, dedicacion, fechaInicio, fechaFin, participacion,
        personaRef, rolId);
    GrupoEquipo grupoEquipo = this.buildMockGrupoEquipo(1L, grupoId, dedicacion, fechaInicio, fechaFin, participacion,
        personaRef, RolProyecto.builder().id(1L).build());

    BDDMockito.given(this.modelMapper.map(input, GrupoEquipo.class)).willReturn(grupoEquipo);

    GrupoEquipo result = this.grupoEquipoConverter.convert(input);

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getId()).isNull();
  }

  @Test
  void convert_ReturnsGrupoEquipoOutput() {
    final Long grupoId = 1L;
    final Dedicacion dedicacion = Dedicacion.COMPLETA;
    final Instant fechaInicio = Instant.now();
    final Instant fechaFin = Instant.now().plusSeconds(3600000);
    final BigDecimal participacion = new BigDecimal(100);
    final String personaRef = "48999343";
    final Long id = 2L;

    GrupoEquipo grupoEquipo = this.buildMockGrupoEquipo(id, grupoId, dedicacion, fechaInicio, fechaFin, participacion,
        personaRef, RolProyecto.builder().id(1L).build());

    GrupoEquipoOutput output = buildMockGrupoEquipoOutput(id, dedicacion, fechaInicio, fechaFin, participacion,
        personaRef);

    BDDMockito.given(modelMapper.map(grupoEquipo, GrupoEquipoOutput.class)).willReturn(output);

    GrupoEquipoOutput result = this.grupoEquipoConverter.convert(grupoEquipo);

    Assertions.assertThat(result).isNotNull().isEqualTo(output);

  }

  private GrupoEquipoInput buildMockGrupoEquipoInput(Long grupoId, Dedicacion dedicacion, Instant fechaInicio,
      Instant fechaFin, BigDecimal participacion, String personaRef, Long rolId) {
    return GrupoEquipoInput.builder()
        .dedicacion(dedicacion)
        .fechaInicio(fechaInicio)
        .fechaFin(fechaFin)
        .grupoId(grupoId)
        .participacion(participacion)
        .personaRef(personaRef)
        .rolId(rolId)
        .build();
  }

  private GrupoEquipo buildMockGrupoEquipo(Long id, Long grupoId, Dedicacion dedicacion, Instant fechaInicio,
      Instant fechaFin, BigDecimal participacion, String personaRef, RolProyecto rol) {
    return GrupoEquipo.builder()
        .id(id)
        .dedicacion(dedicacion)
        .fechaInicio(fechaInicio)
        .fechaFin(fechaFin)
        .grupoId(grupoId)
        .participacion(participacion)
        .personaRef(personaRef)
        .rol(rol)
        .build();
  }

  private GrupoEquipoOutput buildMockGrupoEquipoOutput(Long id, Dedicacion dedicacion,
      Instant fechaInicio,
      Instant fechaFin, BigDecimal participacion, String personaRef) {
    return GrupoEquipoOutput.builder()
        .id(id)
        .dedicacion(dedicacion)
        .fechaInicio(fechaInicio)
        .fechaFin(fechaFin)
        .participacion(participacion)
        .personaRef(personaRef)
        .build();
  }
}
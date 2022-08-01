package org.crue.hercules.sgi.csp.converter;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.GrupoInput;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;

import java.time.Instant;

class GrupoConverterTest extends BaseServiceTest {

  @Mock
  private ModelMapper modelMapper;

  private GrupoConverter grupoConverter;

  @BeforeEach
  void setup() {
    this.grupoConverter = new GrupoConverter(modelMapper);
  }

  @Test
  void convert_WithoutId_ReturnsGrupo() {
    final String codigo = "001";
    final String departamentoOrigenRef = "ref-001";
    final Instant fechaInicio = Instant.now();
    final Instant fechaFin = Instant.now().plusSeconds(360000);
    final String nombre = "Testing 1";
    final GrupoEspecialInvestigacion especialInvestigacion = this.buildMockGrupoEspecialInvestigacion(1L);

    GrupoInput input = buildMockGrupoInput(codigo, departamentoOrigenRef, true, fechaInicio, fechaFin,
        nombre);
    Grupo grupo = buildMockGrupo(null, codigo, departamentoOrigenRef, especialInvestigacion, fechaInicio, fechaFin,
        nombre);
    BDDMockito.given(this.modelMapper.map(input, Grupo.class)).willReturn(grupo);

    Grupo result = this.grupoConverter.convert(input);

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getId()).isNull();
  }

  private GrupoInput buildMockGrupoInput(String codigo, String departamentoOrigenRef, boolean especialInvestigacion,
      Instant fechaInicio, Instant fechaFin, String nombre) {
    return GrupoInput
        .builder()
        .codigo(codigo)
        .departamentoOrigenRef(departamentoOrigenRef)
        .especialInvestigacion(especialInvestigacion)
        .fechaFin(fechaFin)
        .fechaInicio(fechaInicio)
        .nombre(nombre)
        .build();
  }

  private Grupo buildMockGrupo(Long id, String codigo, String departamentoOrigenRef,
      GrupoEspecialInvestigacion especialInvestigacion,
      Instant fechaInicio, Instant fechaFin, String nombre) {
    return Grupo
        .builder()
        .id(id)
        .codigo(codigo)
        .departamentoOrigenRef(departamentoOrigenRef)
        .especialInvestigacion(especialInvestigacion)
        .fechaFin(fechaFin)
        .fechaInicio(fechaInicio)
        .nombre(nombre)
        .build();
  }

  private GrupoEspecialInvestigacion buildMockGrupoEspecialInvestigacion(Long id) {
    return GrupoEspecialInvestigacion.builder()
        .id(id)
        .build();
  }
}
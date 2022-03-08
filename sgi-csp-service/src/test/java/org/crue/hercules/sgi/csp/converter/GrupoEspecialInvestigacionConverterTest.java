package org.crue.hercules.sgi.csp.converter;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.GrupoEspecialInvestigacionOutput;
import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.Instant;
import java.util.Arrays;

class GrupoEspecialInvestigacionConverterTest extends BaseServiceTest {

  @Mock
  private ModelMapper modelMapper;

  private GrupoEspecialInvestigacionConverter grupoEspecialInvestigacionConverter;

  @BeforeEach
  public void setup() {
    this.grupoEspecialInvestigacionConverter = new GrupoEspecialInvestigacionConverter(modelMapper);
  }

  @Test
  void convert_ReturnsGrupoEspecialInvestigacionOutput() {
    final Long id = 1L;
    final boolean especialInvestigacion = true;
    final Long grupoId = 1L;
    final Instant fechaInicio = Instant.now();
    final Instant fechaFin = Instant.now().plusSeconds(3600000);

    GrupoEspecialInvestigacion entity = this.buildMockGrupoEspecialInvestigacion(id,
        especialInvestigacion, grupoId, fechaInicio,
        fechaFin);
    GrupoEspecialInvestigacionOutput output = this.buildMockGrupoEspecialInvestigacionOutput(id, especialInvestigacion,
        fechaInicio, fechaFin);

    BDDMockito.given(modelMapper.map(entity, GrupoEspecialInvestigacionOutput.class)).willReturn(output);

    GrupoEspecialInvestigacionOutput result = this.grupoEspecialInvestigacionConverter.convert(entity);

    Assertions.assertThat(result).isNotNull().isEqualTo(output);
  }

  @Test
  void convert_ReturnsGrupoEspecialInvestigacionOutputPage() {
    final Long id = 1L;
    final boolean especialInvestigacion = true;
    final Long grupoId = 1L;
    final Instant fechaInicio = Instant.now();
    final Instant fechaFin = Instant.now().plusSeconds(3600000);

    GrupoEspecialInvestigacion entity = this.buildMockGrupoEspecialInvestigacion(id,
        especialInvestigacion, grupoId, fechaInicio,
        fechaFin);
    Page<GrupoEspecialInvestigacion> entitiesPage = new PageImpl<GrupoEspecialInvestigacion>(Arrays.asList(entity));

    GrupoEspecialInvestigacionOutput output = this.buildMockGrupoEspecialInvestigacionOutput(id, especialInvestigacion,
        fechaInicio, fechaFin);

    BDDMockito.given(modelMapper.map(entity, GrupoEspecialInvestigacionOutput.class)).willReturn(output);

    Page<GrupoEspecialInvestigacionOutput> result = this.grupoEspecialInvestigacionConverter.convert(entitiesPage);

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getContent()).isNotNull();
  }

  private GrupoEspecialInvestigacion buildMockGrupoEspecialInvestigacion(Long id, boolean especialInvestigacion,
      Long grupoId, Instant fechaInicio, Instant fechaFin) {
    return GrupoEspecialInvestigacion.builder()
        .id(id)
        .especialInvestigacion(especialInvestigacion)
        .grupoId(grupoId)
        .fechaInicio(fechaInicio)
        .fechaFin(fechaFin)
        .build();
  }

  private GrupoEspecialInvestigacionOutput buildMockGrupoEspecialInvestigacionOutput(Long id,
      boolean especialInvestigacion,
      Instant fechaInicio, Instant fechaFin) {
    return GrupoEspecialInvestigacionOutput.builder()
        .id(id)
        .especialInvestigacion(especialInvestigacion)
        .fechaInicio(fechaInicio)
        .fechaFin(fechaFin)
        .build();
  }
}
package org.crue.hercules.sgi.csp.converter;

import java.time.Instant;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.GrupoTipoOutput;
import org.crue.hercules.sgi.csp.model.GrupoTipo;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

class GrupoTipoConverterTest extends BaseServiceTest {

  @Mock
  private ModelMapper modelMapper;

  private GrupoTipoConverter grupoTipoConverter;

  @BeforeEach
  void setup() {
    this.grupoTipoConverter = new GrupoTipoConverter(this.modelMapper);
  }

  @Test
  void convert_ReturnsGrupoTipoOutputPage() {
    final Long id = 1L;
    final Long grupoId = 1L;
    final Instant fechaInicio = Instant.now();
    final Instant fechaFin = Instant.now().plusSeconds(3600000);

    final GrupoTipo entity = this.buildMockGrupoTipo(id, grupoId, fechaInicio, fechaFin);
    final GrupoTipoOutput output = this.buildMockGrupoTipoOutput(id, fechaInicio, fechaFin);

    final Page<GrupoTipo> inputPage = new PageImpl<>(Arrays.asList(entity));

    BDDMockito.given(this.modelMapper.map(entity, GrupoTipoOutput.class)).willReturn(output);

    Page<GrupoTipoOutput> result = this.grupoTipoConverter.convert(inputPage);

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getContent()).isNotEmpty();
    Assertions.assertThat(result.getContent().get(0).getId()).isEqualTo(id);
    Assertions.assertThat(result.getContent().get(0).getFechaInicio()).isEqualTo(fechaInicio);
    Assertions.assertThat(result.getContent().get(0).getFechaFin()).isEqualTo(fechaFin);

  }

  private GrupoTipo buildMockGrupoTipo(Long id, Long grupoId, Instant fechaInicio, Instant fechaFin) {
    return GrupoTipo.builder()
        .id(id)
        .grupoId(grupoId)
        .fechaInicio(fechaInicio)
        .fechaFin(fechaFin)
        .build();
  }

  private GrupoTipoOutput buildMockGrupoTipoOutput(Long id, Instant fechaInicio, Instant fechaFin) {
    return GrupoTipoOutput.builder()
        .id(id)
        .fechaInicio(fechaInicio)
        .fechaFin(fechaFin)
        .build();
  }
}
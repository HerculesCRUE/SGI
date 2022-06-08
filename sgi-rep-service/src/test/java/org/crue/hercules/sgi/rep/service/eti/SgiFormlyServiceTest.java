package org.crue.hercules.sgi.rep.service.eti;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoOutput;
import org.crue.hercules.sgi.rep.dto.eti.RespuestaDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * SgiFormlyServiceTest
 */
class SgiFormlyServiceTest extends BaseReportEtiServiceTest {

  private SgiFormlyService sgiFormlyService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @BeforeEach
  public void setUp() throws Exception {
    sgiFormlyService = new SgiFormlyService(sgiConfigProperties);
  }

  @Test
  void getEmptyApartadoOutput_ReturnsException() throws Exception {
    // given: data ApartadoOutput
    ApartadoOutput apartadoOutput = ApartadoOutput.builder().build();

    Assertions.assertThatThrownBy(() -> sgiFormlyService.parseApartadoAndRespuestaAndComentarios(
        apartadoOutput))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @CsvSource({
      "'apartadoEquipoInvestigador.json','respuestaEquipoInvestigador.json'",
      "'apartado3_6_1.json','respuesta3_6_1.json'",
      "'apartado3_4_1.json','respuesta3_4_1.json'",
      "'apartado3_4_4.json','respuesta3_4_4.json'",
      "'apartado1_1.json','respuesta1_1.json'"
  })
  void getApartadoOutput_ReturnsApartadoOutput(String apartadoFileName, String respuestaFileName) throws Exception {
    // given: data ApartadoOutput
    RespuestaDto respuesta = generarMockRespuesta(3L);
    respuesta.setValor(getJsonFromResources("eti/" + respuestaFileName));

    ApartadoOutput apartadoOutput = ApartadoOutput.builder()
        .mostrarContenidoApartado(Boolean.TRUE)
        .titulo("titulo")
        .nombre("nombre")
        .orden(1)
        .esquema(getJsonFromResources("eti/" + apartadoFileName))
        .respuesta(respuesta)
        .comentarios(generarMockComentarios())
        .build();

    sgiFormlyService.parseApartadoAndRespuestaAndComentarios(apartadoOutput);

    // given: apartadoOutput parsed
    assertNotNull(apartadoOutput);
  }

}

package org.crue.hercules.sgi.rep.service.eti;

import java.util.HashSet;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

/**
 * ApartadoServiceTest
 */
class ApartadoServiceTest extends BaseReportEtiServiceTest {

  private ApartadoService apartadoService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    apartadoService = new ApartadoService(restApiProperties, restTemplate);
  }

  @Test
  void findByPadreId_ReturnsException() throws Exception {
    Long idPadre = 1L;

    Assertions.assertThatThrownBy(() -> apartadoService.findByPadreId(idPadre))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void findByBloqueId_ReturnsException() throws Exception {
    Long idBloque = 1L;

    Assertions.assertThatThrownBy(() -> apartadoService.findByBloqueId(idBloque))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void findTreeApartadosById_ReturnsNullPointerException() throws Exception {
    Assertions.assertThatThrownBy(() -> apartadoService.findTreeApartadosById(null, null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void findTreeApartadosById_ReturnsException() throws Exception {
    ApartadoDto apartado = ApartadoDto.builder().id(1L).padre(ApartadoDto.builder().id(11L).build()).build();
    Set<Long> apartados = new HashSet<>();
    apartados.add(1L);

    Assertions.assertThatThrownBy(() -> apartadoService.findTreeApartadosById(apartados, apartado))
        .isInstanceOf(GetDataReportException.class);
  }

}

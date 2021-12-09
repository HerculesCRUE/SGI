package org.crue.hercules.sgi.rep.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoDto;
import org.crue.hercules.sgi.rep.dto.eti.RespuestaDto;
import org.crue.hercules.sgi.rep.model.BaseEntity;
import org.junit.jupiter.api.Test;

/**
 * DtoTest
 */
class DtoTest extends BaseReportServiceTest {

  @Test
  void getDynamicReport_ReturnsJson() throws Exception {
    SgiDynamicReportDto report = generarMockSgiDynamicReport(OutputType.PDF);

    String jsonSgiDynamicReportDto = report.toJson();

    assertNotNull(jsonSgiDynamicReportDto);
  }

  @Test
  void getRespuesta_ReturnsJson() throws Exception {
    RespuestaDto dto = RespuestaDto.builder().build();
    ObjectNode node = JsonNodeFactory.instance.objectNode();
    dto.setEsquemaRaw(node);

    assertNotNull(dto.getValor());
  }

  @Test
  void getApartado_ReturnsJson() throws Exception {
    ApartadoDto dto = ApartadoDto.builder().build();
    ObjectNode node = JsonNodeFactory.instance.objectNode();
    dto.setEsquemaRaw(node);

    assertNotNull(dto.getEsquema());
  }

  @Test
  void getEntity_ReturnsInstance() throws Exception {
    BaseEntity entity = new BaseEntity();

    assertNotNull(entity);
  }
}

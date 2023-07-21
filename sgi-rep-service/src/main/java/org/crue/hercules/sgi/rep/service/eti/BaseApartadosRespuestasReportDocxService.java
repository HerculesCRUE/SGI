package org.crue.hercules.sgi.rep.service.eti;

import java.util.Map;

import javax.swing.table.TableModel;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.ElementOutput;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio base de generación de informes relacionado con apartados y
 * respuestas de ética
 */
@Service
@Slf4j
@Validated
public class BaseApartadosRespuestasReportDocxService extends BaseApartadosRespuestasReportService {

  protected BaseApartadosRespuestasReportDocxService(SgiConfigProperties sgiConfigProperties,
      SgiApiConfService sgiApiConfService, BloqueService bloqueService, ApartadoService apartadoService,
      SgiFormlyService sgiFormlyService, RespuestaService respuestaService) {
    super(sgiConfigProperties, sgiApiConfService, bloqueService, apartadoService, sgiFormlyService, respuestaService);
  }

  @Override
  void parseElementTypeFromTableModel(Map<String, TableModel> hmTableModel, ElementOutput elemento) {
    throw new UnsupportedOperationException("Unimplemented method 'parseElementTypeFromTableModel'");
  }
}
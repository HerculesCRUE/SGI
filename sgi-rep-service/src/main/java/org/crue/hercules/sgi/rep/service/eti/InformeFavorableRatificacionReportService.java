package org.crue.hercules.sgi.rep.service.eti;

import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableRatificacion;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Servicio de generación de informe favorable ratificación de ética
 */
@Service
@Validated
public class InformeFavorableRatificacionReportService extends InformeEvaluacionEvaluadorBaseReportService {

  public InformeFavorableRatificacionReportService(SgiConfigProperties sgiConfigProperties,
      SgiApiConfService sgiApiConfService, SgiApiSgpService personaService, EvaluacionService evaluacionService) {

    super(sgiConfigProperties, sgiApiConfService, personaService, evaluacionService, null);
  }

  protected XWPFDocument getDocument(EvaluacionDto evaluacion, HashMap<String, Object> dataReport, InputStream path) {

    addDataPersona(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef(), dataReport);
    dataReport.put("memoriaRef", evaluacion.getMemoria().getNumReferencia());

    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    Instant fechaEvaluacion = evaluacion.getConvocatoriaReunion().getFechaEvaluacion();
    dataReport.put("fechaEvaluacion", formatInstantToString(fechaEvaluacion, pattern));

    addDataEvaluacion(evaluacion, dataReport);

    return compileReportData(path, dataReport);
  }

  public byte[] getReportInformeFavorableRatificacion(ReportInformeFavorableRatificacion sgiReport, Long idEvaluacion) {
    getReportFromIdEvaluacion(sgiReport, idEvaluacion);
    return sgiReport.getContent();
  }

}
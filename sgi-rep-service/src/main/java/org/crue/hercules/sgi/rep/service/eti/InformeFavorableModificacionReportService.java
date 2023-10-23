package org.crue.hercules.sgi.rep.service.eti;

import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.ConvocatoriaReunionDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableModificacion;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Servicio de generación de informe favorable modificación de ética
 */
@Service
@Validated
public class InformeFavorableModificacionReportService extends InformeEvaluacionEvaluadorBaseReportService {

  private final EvaluacionService evaluacionService;
  private final ConvocatoriaReunionService convocatoriaReunionService;

  public InformeFavorableModificacionReportService(SgiConfigProperties sgiConfigProperties,
      SgiApiConfService sgiApiConfService, PersonaService personaService, EvaluacionService evaluacionService,
      ConvocatoriaReunionService convocatoriaReunionService) {

    super(sgiConfigProperties, sgiApiConfService, personaService, evaluacionService, null);
    this.convocatoriaReunionService = convocatoriaReunionService;
    this.evaluacionService = evaluacionService;
  }

  protected XWPFDocument getDocument(EvaluacionDto evaluacion, HashMap<String, Object> dataReport, InputStream path) {

    addDataPersona(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef(), dataReport);

    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);

    try {
      ConvocatoriaReunionDto convocatoriaUltimaEvaluacion = convocatoriaReunionService
          .findConvocatoriaUltimaEvaluacionTipoMemoria(evaluacion.getId(), evaluacion.getDictamen().getId());

      dataReport.put(
          "fechaDictamenMemOriginal",
          formatInstantToString(convocatoriaUltimaEvaluacion.getFechaEvaluacion(), pattern));

      dataReport.put("numeroActaMemOriginal",
          convocatoriaUltimaEvaluacion.getNumeroActa() + "/" + convocatoriaUltimaEvaluacion.getAnio());
    } catch (Exception e) {
      dataReport.put("fechaDictamenMemOriginal", getErrorMessage(e));
      dataReport.put("numeroActaMemOriginal", getErrorMessage(e));

    }

    try {
      Instant fechaEnvioSecretaria = evaluacionService.findFirstFechaEnvioSecretariaByIdEvaluacion(evaluacion.getId());
      dataReport.put("fechaEnvioSecretaria", formatInstantToString(fechaEnvioSecretaria, pattern));
    } catch (Exception e) {
      dataReport.put("fechaEnvioSecretaria", getErrorMessage(e));
    }

    dataReport.put("fechaDictamen",
        formatInstantToString(evaluacion.getConvocatoriaReunion().getFechaEvaluacion(), pattern));

    dataReport
        .put("numeroActa",
            evaluacion.getConvocatoriaReunion().getNumeroActa() + "/" + evaluacion.getConvocatoriaReunion().getAnio());

    dataReport.put("referenciaMemoria", evaluacion.getMemoria().getNumReferencia());

    addDataEvaluacion(evaluacion, dataReport);

    return compileReportData(path, dataReport);
  }

  public byte[] getReportInformeFavorableModificacion(ReportInformeFavorableModificacion sgiReport, Long idEvaluacion) {
    getReportFromEvaluacionId(sgiReport, idEvaluacion);
    return sgiReport.getContent();
  }

}
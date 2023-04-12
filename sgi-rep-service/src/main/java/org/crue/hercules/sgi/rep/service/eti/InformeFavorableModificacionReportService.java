package org.crue.hercules.sgi.rep.service.eti;

import java.time.Instant;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.ConvocatoriaReunionDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableModificacion;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Servicio de generación de informe favorable modificación de ética
 */
@Service
@Validated
public class InformeFavorableModificacionReportService extends InformeEvaluacionBaseReportService {

  private final EvaluacionService evaluacionService;
  private final ConvocatoriaReunionService convocatoriaReunionService;

  public InformeFavorableModificacionReportService(SgiConfigProperties sgiConfigProperties,
      SgiApiConfService sgiApiConfService, SgiApiSgpService personaService, EvaluacionService evaluacionService,
      ConvocatoriaReunionService convocatoriaReunionService) {

    super(sgiConfigProperties, sgiApiConfService, personaService, evaluacionService);
    this.convocatoriaReunionService = convocatoriaReunionService;
    this.evaluacionService = evaluacionService;
  }

  protected DefaultTableModel getTableModelGeneral(EvaluacionDto evaluacion) {

    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();
    Vector<Object> elementsRow = new Vector<>();

    addColumnAndRowDataInvestigador(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef(), columnsData,
        elementsRow);

    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);

    columnsData.add("fechaDictamenMemOriginal");
    columnsData.add("numeroActaMemOriginal");
    try {

      ConvocatoriaReunionDto convocatoriaUltimaEvaluacion = convocatoriaReunionService
          .findConvocatoriaUltimaEvaluacionTipoMemoria(evaluacion.getId(), evaluacion.getDictamen().getId());

      elementsRow.add(formatInstantToString(convocatoriaUltimaEvaluacion.getFechaEvaluacion(), pattern));

      elementsRow.add(convocatoriaUltimaEvaluacion.getNumeroActa() + "/" + convocatoriaUltimaEvaluacion.getAnio());
    } catch (Exception e) {
      elementsRow.add(getErrorMessageToReport(e));
      elementsRow.add(getErrorMessageToReport(e));

    }

    columnsData.add("fechaEnvioSecretaria");
    try {
      Instant fechaEnvioSecretaria = evaluacionService.findFirstFechaEnvioSecretariaByIdEvaluacion(evaluacion.getId());
      elementsRow.add(formatInstantToString(fechaEnvioSecretaria, pattern));
    } catch (Exception e) {
      elementsRow.add(getErrorMessageToReport(e));
    }

    columnsData.add("fechaDictamen");
    elementsRow.add(formatInstantToString(evaluacion.getConvocatoriaReunion().getFechaEvaluacion(), pattern));

    columnsData.add("numeroActa");
    elementsRow
        .add(evaluacion.getConvocatoriaReunion().getNumeroActa() + "/" + evaluacion.getConvocatoriaReunion().getAnio());

    columnsData.add("referenciaMemoria");
    elementsRow.add(evaluacion.getMemoria().getNumReferencia());

    fillCommonFieldsEvaluacion(evaluacion, columnsData, elementsRow);

    columnsData.add("resourcesBaseURL");
    elementsRow.add(getRepResourcesBaseURL());

    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  public byte[] getReportInformeFavorableModificacion(ReportInformeFavorableModificacion sgiReport, Long idEvaluacion) {
    getReportFromIdEvaluacion(sgiReport, idEvaluacion, "informeFavorableModificacion");
    return sgiReport.getContent();
  }

}
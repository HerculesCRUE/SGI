package org.crue.hercules.sgi.rep.service.eti;

import java.time.Instant;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

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
public class InformeFavorableRatificacionReportService extends InformeEvaluacionBaseReportService {

  public InformeFavorableRatificacionReportService(SgiConfigProperties sgiConfigProperties,
      SgiApiConfService sgiApiConfService, SgiApiSgpService personaService, EvaluacionService evaluacionService) {

    super(sgiConfigProperties, sgiApiConfService, personaService, evaluacionService);
  }

  protected DefaultTableModel getTableModelGeneral(EvaluacionDto evaluacion) {

    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();
    Vector<Object> elementsRow = new Vector<>();

    addColumnAndRowDataInvestigador(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef(), columnsData,
        elementsRow);

    columnsData.add("nombreArticulo");
    elementsRow.add(evaluacion.getMemoria().getComite().getArticulo());

    columnsData.add("memoriaRef");
    elementsRow.add(evaluacion.getMemoria().getNumReferencia());

    columnsData.add("fechaEvaluacion");
    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    Instant fechaEvaluacion = evaluacion.getConvocatoriaReunion().getFechaEvaluacion();
    elementsRow.add(formatInstantToString(fechaEvaluacion, pattern));

    fillCommonFieldsEvaluacion(evaluacion, columnsData, elementsRow);

    columnsData.add("resourcesBaseURL");
    elementsRow.add(getRepResourcesBaseURL());

    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  public byte[] getReportInformeFavorableRatificacion(ReportInformeFavorableRatificacion sgiReport, Long idEvaluacion) {
    getReportFromIdEvaluacion(sgiReport, idEvaluacion, "informeFavorableRatificacion");
    return sgiReport.getContent();
  }

}
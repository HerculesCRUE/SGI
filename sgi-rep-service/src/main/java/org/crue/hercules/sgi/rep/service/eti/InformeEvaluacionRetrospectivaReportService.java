package org.crue.hercules.sgi.rep.service.eti;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto.Genero;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluadorDto;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionReportInput;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeEvaluacionRetrospectiva;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Servicio de generación de informe evaluación retrospectiva de ética
 */
@Service
@Validated
public class InformeEvaluacionRetrospectivaReportService extends InformeEvaluacionBaseReportService {

  private final EvaluacionService evaluacionService;
  private final SgiApiSgpService personaService;

  public InformeEvaluacionRetrospectivaReportService(SgiConfigProperties sgiConfigProperties,
      SgiApiConfService sgiApiConfService,
      SgiApiSgpService personaService, EvaluacionService evaluacionService) {

    super(sgiConfigProperties, sgiApiConfService, personaService, evaluacionService);
    this.evaluacionService = evaluacionService;
    this.personaService = personaService;
  }

  protected DefaultTableModel getTableModelGeneral(EvaluacionDto evaluacion) {

    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();
    Vector<Object> elementsRow = new Vector<>();

    addColumnAndRowDataInvestigador(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef(), columnsData,
        elementsRow);

    columnsData.add("tituloProyecto");
    elementsRow.add(evaluacion.getMemoria().getPeticionEvaluacion().getTitulo());

    columnsData.add("lugar");
    elementsRow.add(evaluacion.getConvocatoriaReunion().getNumeroActa());

    columnsData.add("codigoOrgano");
    elementsRow.add(evaluacion.getMemoria().getCodOrganoCompetente());

    columnsData.add("nombreInvestigacion");
    elementsRow.add(evaluacion.getMemoria().getComite().getNombreInvestigacion());

    columnsData.add("comite");
    elementsRow.add(evaluacion.getMemoria().getComite().getComite());

    columnsData.add("nombreSecretario");
    try {
      EvaluadorDto secretario = evaluacionService.findSecretarioEvaluacion(evaluacion.getId());
      if (ObjectUtils.isNotEmpty(secretario)) {
        PersonaDto persona = personaService.findById(secretario.getPersonaRef());
        elementsRow.add(persona.getNombre() + " " + persona.getApellidos());
      } else {
        elementsRow.add(" - ");
      }
    } catch (Exception e) {
      elementsRow.add(getErrorMessageToReport(e));
    }

    columnsData.add("nombrePresidente");
    columnsData.add("articuloPresidente");
    try {
      String idPresidente = evaluacionService.findIdPresidenteByIdEvaluacion(evaluacion.getId());
      addRowDataInvestigador(idPresidente, elementsRow);
    } catch (Exception e) {
      elementsRow.add(getErrorMessageToReport(e));
    }

    columnsData.add("del");
    columnsData.add("el");
    if (evaluacion.getMemoria().getComite().getGenero().equals(Genero.F)) {
      String i18nDela = ApplicationContextSupport.getMessage("common.dela");
      elementsRow.add(i18nDela);
      String i18nLa = ApplicationContextSupport.getMessage("common.la");
      elementsRow.add(i18nLa);
    } else {
      String i18nDel = ApplicationContextSupport.getMessage("common.del");
      elementsRow.add(i18nDel);
      String i18nEl = ApplicationContextSupport.getMessage("common.el");
      elementsRow.add(i18nEl);
    }

    columnsData.add("resourcesBaseURL");
    elementsRow.add(getRepResourcesBaseURL());

    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  public byte[] getReportInformeEvaluacionRetrospectiva(ReportInformeEvaluacionRetrospectiva sgiReport,
      InformeEvaluacionReportInput input) {
    getReportFromIdEvaluacion(sgiReport, input.getIdEvaluacion(), "informeEvaluacionRetrospectiva");
    return sgiReport.getContent();
  }

}
package org.crue.hercules.sgi.rep.service.eti;

import java.util.Collection;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto.Genero;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.SgiReportService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación base de informe evaluación
 */
@Service
@Slf4j
@Validated
public abstract class InformeEvaluacionBaseReportService extends SgiReportService {

  private final EvaluacionService evaluacionService;
  private final PersonaService personaService;

  protected InformeEvaluacionBaseReportService(SgiConfigProperties sgiConfigProperties, PersonaService personaService,
      EvaluacionService evaluacionService) {

    super(sgiConfigProperties);
    this.personaService = personaService;
    this.evaluacionService = evaluacionService;
  }

  protected MasterReport getReportFromIdEvaluacion(SgiReportDto sgiReport, Long idEvaluacion, String reportSuffix) {
    try {

      final MasterReport report = getReportDefinition(sgiReport.getPath());

      EvaluacionDto evaluacion = evaluacionService.findById(idEvaluacion);

      String queryGeneral = QUERY_TYPE + SEPARATOR_KEY + NAME_GENERAL_TABLE_MODEL + SEPARATOR_KEY + reportSuffix;
      DefaultTableModel tableModelGeneral = getTableModelGeneral(evaluacion);

      TableDataFactory dataFactory = new TableDataFactory();
      dataFactory.addTable(queryGeneral, tableModelGeneral);
      report.setDataFactory(dataFactory);

      sgiReport.setContent(generateReportOutput(sgiReport.getOutputReportType(), report));

      return report;

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
  }

  protected void addColumnAndRowDataInvestigador(String personaRef, List<Object> columnsData,
      List<Object> elementsRow) {
    columnsData.add("nombreInvestigador");
    addRowDataInvestigador(personaRef, elementsRow);
  }

  protected void addRowDataInvestigador(String personaRef, List<Object> elementsRow) {
    try {
      PersonaDto persona = personaService.findById(personaRef);
      elementsRow.add(persona.getNombre() + " " + persona.getApellidos());
    } catch (Exception e) {
      elementsRow.add(getErrorMessageToReport(e));
    }
  }

  protected void fillCommonFieldsEvaluacion(EvaluacionDto evaluacion, Collection<Object> columnsData,
      Collection<Object> elementsRow) {
    columnsData.add("tituloProyecto");
    elementsRow.add(evaluacion.getMemoria().getPeticionEvaluacion().getTitulo());

    columnsData.add("comite");
    elementsRow.add(evaluacion.getMemoria().getComite().getComite());

    columnsData.add("nombreSecretario");
    elementsRow.add(evaluacion.getMemoria().getComite().getNombreSecretario());

    columnsData.add("nombreInvestigacion");
    elementsRow.add(evaluacion.getMemoria().getComite().getNombreInvestigacion());

    columnsData.add("del");
    columnsData.add("el");
    columnsData.add("este");
    if (evaluacion.getMemoria().getComite().getGenero().equals(Genero.F)) {
      String i18nDela = ApplicationContextSupport.getMessage("common.dela");
      elementsRow.add(i18nDela);
      String i18nLa = ApplicationContextSupport.getMessage("common.la");
      elementsRow.add(StringUtils.capitalize(i18nLa));
      String i18nEsta = ApplicationContextSupport.getMessage("common.esta");
      elementsRow.add(i18nEsta);
    } else {
      String i18nDel = ApplicationContextSupport.getMessage("common.del");
      elementsRow.add(i18nDel);
      String i18nEl = ApplicationContextSupport.getMessage("common.el");
      elementsRow.add(StringUtils.capitalize(i18nEl));
      String i18nEste = ApplicationContextSupport.getMessage("common.este");
      elementsRow.add(i18nEste);
    }

  }

  protected abstract DefaultTableModel getTableModelGeneral(EvaluacionDto evaluacion);

}
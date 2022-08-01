package org.crue.hercules.sgi.rep.service.eti;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableMemoria;
import org.crue.hercules.sgi.rep.dto.eti.TareaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe favorable memoria de ética
 */
@Service
@Slf4j
@Validated
public class InformeFavorableMemoriaReportService extends InformeEvaluacionBaseReportService {
  private final EvaluacionService evaluacionService;
  private final PeticionEvaluacionService peticionEvaluacionService;

  public InformeFavorableMemoriaReportService(SgiConfigProperties sgiConfigProperties, SgiApiSgpService personaService,
      EvaluacionService evaluacionService, PeticionEvaluacionService peticionEvaluacionService) {

    super(sgiConfigProperties, personaService, evaluacionService);
    this.evaluacionService = evaluacionService;

    this.peticionEvaluacionService = peticionEvaluacionService;
  }

  protected DefaultTableModel getTableModelGeneral(EvaluacionDto evaluacion) {

    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();
    Vector<Object> elementsRow = new Vector<>();

    columnsData.add("codigoMemoria");
    elementsRow.add(evaluacion.getMemoria().getNumReferencia());

    addColumnAndRowDataInvestigador(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef(), columnsData,
        elementsRow);

    columnsData.add("fechaDictamen");
    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("EEEE dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    elementsRow.add(formatInstantToString(evaluacion.getFechaDictamen(), pattern));

    columnsData.add("numeroActa");
    String i18nActa = ApplicationContextSupport.getMessage("acta");
    String codigoActa = "(" + i18nActa + evaluacion.getConvocatoriaReunion().getNumeroActa() + "/"
        + formatInstantToString(evaluacion.getConvocatoriaReunion()
            .getFechaEvaluacion(), "YYYY")
        + "/" + evaluacion.getConvocatoriaReunion().getComite().getComite() + ")";
    elementsRow.add(codigoActa);

    fillCommonFieldsEvaluacion(evaluacion, columnsData, elementsRow);
    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  private DefaultTableModel getTableModelEquipoInvestigador(EvaluacionDto evaluacion) {
    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();

    List<TareaDto> tareas = peticionEvaluacionService
        .findTareasEquipoTrabajo(evaluacion.getMemoria().getPeticionEvaluacion().getId());

    columnsData.add("nombreInvestigador");
    List<String> personas = new ArrayList<>();
    tareas.forEach(tarea -> {
      Vector<Object> elementsRow = new Vector<>();
      if (!personas.contains(tarea.getEquipoTrabajo().getPersonaRef())) {
        personas.add(tarea.getEquipoTrabajo().getPersonaRef());

        addRowDataInvestigador(tarea.getEquipoTrabajo().getPersonaRef(), elementsRow);

        rowsData.add(elementsRow);
      }
    });

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  public byte[] getReportInformeFavorableMemoria(ReportInformeFavorableMemoria sgiReport, Long idEvaluacion) {
    try {

      final MasterReport report = getReportDefinition(sgiReport.getPath());

      EvaluacionDto evaluacion = evaluacionService.findById(idEvaluacion);

      String queryGeneral = QUERY_TYPE + SEPARATOR_KEY + NAME_GENERAL_TABLE_MODEL + SEPARATOR_KEY
          + "informeFavorableMemoria";
      DefaultTableModel tableModelGeneral = getTableModelGeneral(evaluacion);

      TableDataFactory dataFactory = new TableDataFactory();
      dataFactory.addTable(queryGeneral, tableModelGeneral);
      report.setDataFactory(dataFactory);

      String queryEquipoInvestigador = QUERY_TYPE + SEPARATOR_KEY + "equipoInvestigador";
      DefaultTableModel tableModeEquipoInvestigador = getTableModelEquipoInvestigador(evaluacion);
      TableDataFactory dataFactorySubReportEquipoInvestigador = new TableDataFactory();
      dataFactorySubReportEquipoInvestigador.addTable(queryEquipoInvestigador, tableModeEquipoInvestigador);
      Band bandEquipoInvestigador = (Band) report.getItemBand().getElement(1);
      SubReport subreportEquipoInvestigador = (SubReport) bandEquipoInvestigador.getElement(0);
      subreportEquipoInvestigador.setDataFactory(dataFactorySubReportEquipoInvestigador);

      sgiReport.setContent(generateReportOutput(sgiReport.getOutputType(), report));

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return sgiReport.getContent();
  }

}
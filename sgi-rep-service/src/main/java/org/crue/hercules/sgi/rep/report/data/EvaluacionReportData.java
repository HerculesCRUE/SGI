package org.crue.hercules.sgi.rep.report.data;

import java.util.HashMap;
import java.util.Map;

import org.crue.hercules.sgi.rep.dto.eti.ConfiguracionDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.enums.Dictamen;
import org.crue.hercules.sgi.rep.enums.EstadoRetrospectiva;
import org.crue.hercules.sgi.rep.enums.Genero;
import org.crue.hercules.sgi.rep.enums.Informe;
import org.crue.hercules.sgi.rep.enums.TipoEstadoMemoria;
import org.crue.hercules.sgi.rep.enums.TipoEvaluacion;
import org.crue.hercules.sgi.rep.report.SgiReportHelper;
import org.crue.hercules.sgi.rep.report.data.objects.ConfiguracionObject;
import org.crue.hercules.sgi.rep.report.data.objects.EvaluacionObject;
import org.crue.hercules.sgi.rep.report.data.objects.PersonaObject;

import com.deepoove.poi.data.DocxRenderData;
import com.deepoove.poi.data.PictureRenderData;

public class EvaluacionReportData implements ReportData {

  private static final Informe INFORME = Informe.ETI_EVALUACION;

  private final Map<String, Object> dataReport = new HashMap<>();

  public void setHeaderLogo(PictureRenderData value) {
    this.dataReport.put("headerImg", value);
  }

  public void setEvaluacion(EvaluacionDto value) {
    EvaluacionObject evaluacionObject = new EvaluacionObject(value);

    this.dataReport.put("evaluacion", evaluacionObject);

    // Added to direct access nested objects
    this.dataReport.put("convocatoria", evaluacionObject.getConvocatoria());
    this.dataReport.put("memoria", evaluacionObject.getMemoria());
    this.dataReport.put("peticionEvaluacion", evaluacionObject.getMemoria().getPeticionEvaluacion());

    // Valores retenidos por compatibilidad
    this.dataReport.put("referenciaMemoria", evaluacionObject.getMemoria().getNumReferencia());
    this.dataReport.put("version", evaluacionObject.getVersion());
    this.dataReport.put("titulo", evaluacionObject.getMemoria().getPeticionEvaluacion().getTitulo());
    this.dataReport.put("fechaDictamen",
        SgiReportHelper.formatDate(evaluacionObject.getConvocatoria().getFechaEvaluacion(),
            String.format("dd '%s' MMMM '%s' yyyy", SgiReportHelper.getMessage("common.de"),
                SgiReportHelper.getMessage("common.de"))));
    this.dataReport.put("numeroActa", evaluacionObject.getConvocatoria().getNumeroActa());
    this.dataReport.put("idComite", evaluacionObject.getMemoria().getComite().getId());
    this.dataReport.put("comite", evaluacionObject.getMemoria().getComite().getCodigo());
    this.dataReport.put("nombreInvestigacion", evaluacionObject.getMemoria().getComite().getNombreInvestigacion());

    this.dataReport.put("retrospectiva",
        evaluacionObject.getMemoria().getEstadoActual().getTipo() != TipoEstadoMemoria.EN_EVALUACION_SEGUIMIENTO_ANUAL
            && evaluacionObject.getMemoria().getEstadoActual()
                .getTipo() != TipoEstadoMemoria.EN_EVALUACION_SEGUIMIENTO_FINAL
            && evaluacionObject.getMemoria().getRetrospectiva() != null
            && evaluacionObject.getMemoria().getRetrospectiva().getEstadoRetrospectiva()
                .getEstado() != EstadoRetrospectiva.PENDIENTE
            && evaluacionObject.getTipo() == TipoEvaluacion.RETROSPECTIVA);

    this.dataReport.put("seguimientoAnual", evaluacionObject.getTipo() == TipoEvaluacion.SEGUIMIENTO_ANUAL);
    this.dataReport.put("seguimientoFinal", evaluacionObject.getTipo() == TipoEvaluacion.SEGUIMIENTO_FINAL);

    this.dataReport.put("preposicionComite",
        evaluacionObject.getMemoria().getComite().getGenero() == Genero.M
            ? SgiReportHelper.getMessage("common.del")
            : SgiReportHelper.getMessage("common.dela"));
    this.dataReport.put("comisionComite",
        evaluacionObject.getMemoria().getComite().getGenero() == Genero.M
            ? SgiReportHelper.getMessage("comite.comision.masculino")
            : SgiReportHelper.getMessage("comite.comision.femenino"));

    this.dataReport.put("idDictamenPendienteCorrecciones", Dictamen.PDTE_CORRECCIONES.getId());
    this.dataReport.put("idDictamenNoProcedeEvaluar", Dictamen.NO_PROCEDE_EVALUAR);
    this.dataReport.put("idDictamenPendienteRevisionMinima", Dictamen.FAVORABLE_PDTE_REV_MINIMA);
    this.dataReport.put("idDictamen", value.getDictamen().getId());
    this.dataReport.put("dictamen", evaluacionObject.getDictamen().toString());
    this.dataReport.put("comentarioNoProcedeEvaluar", evaluacionObject.getComentario());

    this.dataReport.put("actividad", evaluacionObject.getMemoria().getPeticionEvaluacion().getActividad().toString());
    this.dataReport.put("fieldDelActividad",
        evaluacionObject.getMemoria().getPeticionEvaluacion().getActividad().getGenero() == Genero.M
            ? SgiReportHelper.getMessage("common.del")
            : SgiReportHelper.getMessage("common.dela"));
  }

  public void setInvestigador(PersonaDto value) {
    PersonaObject personaObject = new PersonaObject(value);
    this.dataReport.put("investigador", personaObject);

    // Valores retenidos por compatibilidad
    this.dataReport.put("nombreInvestigador", value.getNombre() + " " + value.getApellidos());
    this.dataReport.put("emailInvestigador", personaObject.getEmail());
  }

  public void setConfiguracion(ConfiguracionDto value) {
    ConfiguracionObject configuracionObject = new ConfiguracionObject(value);
    this.dataReport.put("configuracion", configuracionObject);

    // Valores retenidos por compatibilidad
    this.dataReport.put("mesesArchivadaPendienteCorrecciones",
        configuracionObject.getMesesArchivadaPendienteCorrecciones());
    this.dataReport.put("diasArchivadaPendienteCorrecciones", configuracionObject.getDiasArchivadaInactivo());
  }

  public void setBloqueApartados(DocxRenderData value) {
    this.dataReport.put("bloqueApartados", value);
  }

  public Map<String, Object> getData() {
    return this.dataReport;
  }

  public Informe getInforme() {
    return INFORME;
  }
}

package org.crue.hercules.sgi.rep.report.data;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.crue.hercules.sgi.rep.dto.eti.ConvocatoriaReunionDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.enums.Genero;
import org.crue.hercules.sgi.rep.enums.Informe;
import org.crue.hercules.sgi.rep.report.SgiReportHelper;
import org.crue.hercules.sgi.rep.report.data.objects.ConvocatoriaReunionObject;
import org.crue.hercules.sgi.rep.report.data.objects.EvaluacionObject;
import org.crue.hercules.sgi.rep.report.data.objects.PersonaObject;

import com.deepoove.poi.data.PictureRenderData;

public class EvaluacionFavorableModificacionReportData implements ReportData {

  private static final Informe INFORME = Informe.ETI_EVALUACION_FAVORABLE_MODIFICACION;

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
    this.dataReport.put("fechaDictamen",
        SgiReportHelper.formatDate(evaluacionObject.getConvocatoria().getFechaEvaluacion(),
            String.format("dd '%s' MMMM '%s' yyyy", SgiReportHelper.getMessage("common.de"),
                SgiReportHelper.getMessage("common.de"))));
    this.dataReport.put("numeroActa", evaluacionObject.getConvocatoria().getCodigoActa());
    this.dataReport.put("referenciaMemoria", evaluacionObject.getMemoria().getNumReferencia());

    this.dataReport.put("tituloProyecto", evaluacionObject.getMemoria().getPeticionEvaluacion().getTitulo());
    this.dataReport.put("referenciaProyecto", evaluacionObject.getMemoria().getPeticionEvaluacion().getCodigo());
    this.dataReport.put("comite", evaluacionObject.getMemoria().getComite().getCodigo());
    this.dataReport.put("nombreInvestigacion", evaluacionObject.getMemoria().getComite().getNombreInvestigacion());
    this.dataReport.put("del",
        evaluacionObject.getMemoria().getComite().getGenero() == Genero.M
            ? SgiReportHelper.getMessage("common.del")
            : SgiReportHelper.getMessage("common.dela"));
    this.dataReport.put("el",
        evaluacionObject.getMemoria().getComite().getGenero() == Genero.M
            ? SgiReportHelper.getMessage("common.el")
            : SgiReportHelper.getMessage("common.la"));

    switch (evaluacionObject.getMemoria().getPeticionEvaluacion().getTipoActividad().getTipo()) {
      case PROYECTO_INVESTIGACION, PRACTICA_DOCENTE:
        this.dataReport.put("actividad",
            evaluacionObject.getMemoria().getPeticionEvaluacion().getTipoActividad().getNombre());
        this.dataReport.put("fieldAlActividad",
            evaluacionObject.getMemoria().getPeticionEvaluacion().getTipoActividad().getTipo()
                .getGenero() == Genero.M ? SgiReportHelper.getMessage("common.al")
                    : SgiReportHelper.getMessage("common.a.la"));
        break;
      case INVESTIGACION_TUTELADA:
        this.dataReport.put("actividad",
            evaluacionObject.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada().getNombre());
        this.dataReport.put("fieldAlActividad",
            evaluacionObject.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada().getTipo()
                .getGenero() == Genero.M ? SgiReportHelper.getMessage("common.al")
                    : SgiReportHelper.getMessage("common.a.la"));
        break;
      default:
        break;
    }
  }

  public void setInvestigador(PersonaDto value) {
    PersonaObject personaObject = new PersonaObject(value);
    this.dataReport.put("investigador", personaObject);

    // Valores retenidos por compatibilidad
    this.dataReport.put("nombreInvestigador", value.getNombre() + " " + value.getApellidos());
    this.dataReport.put("fieldElInvestigador",
        personaObject.getGenero() == Genero.M ? SgiReportHelper.getMessage("common.el")
            : SgiReportHelper.getMessage("common.la"));
    this.dataReport.put("fieldInvestigador",
        personaObject.getGenero() == Genero.M
            ? SgiReportHelper.getMessage("field.investigador.masculino")
            : SgiReportHelper.getMessage("field.investigador.femenino"));
  }

  public void setPresidente(PersonaDto value) {
    PersonaObject personaObject = new PersonaObject(value);
    this.dataReport.put("presidente", personaObject);

    // Valores retenidos por compatibilidad
    this.dataReport.put("fieldDelPresidente",
        personaObject.getGenero() == Genero.M ? SgiReportHelper.getMessage("common.del")
            : SgiReportHelper.getMessage("common.dela"));
    this.dataReport.put("fieldPresidente",
        personaObject.getGenero() == Genero.M
            ? SgiReportHelper.getMessage("field.presidente.masculino")
            : SgiReportHelper.getMessage("field.presidente.femenino"));
  }

  public void setSecretario(PersonaDto value) {
    PersonaObject personaObject = new PersonaObject(value);
    this.dataReport.put("secretario", personaObject);

    // Valores retenidos por compatibilidad
    this.dataReport.put("nombreSecretario", value.getNombre() + " " + value.getApellidos());
    this.dataReport.put("fieldSecretario",
        personaObject.getGenero() == Genero.M
            ? SgiReportHelper.getMessage("field.secretario.masculino")
            : SgiReportHelper.getMessage("field.secretario.femenino"));
  }

  public void setConvocatoriaUltimaEvaluacion(ConvocatoriaReunionDto value) {
    ConvocatoriaReunionObject convocatoriaObject = new ConvocatoriaReunionObject(value);
    this.dataReport.put("convocatoriaUltimaEvaluacion", convocatoriaObject);

    // Valores retenidos por compatibilidad
    this.dataReport.put("fechaDictamenMemOriginal", SgiReportHelper.formatDate(convocatoriaObject.getFechaEvaluacion(),
        String.format("dd '%s' MMMM '%s' yyyy", SgiReportHelper.getMessage("common.de"),
            SgiReportHelper.getMessage("common.de"))));
    this.dataReport.put("numeroActaMemOriginal", convocatoriaObject.getCodigoActa());
  }

  public void setFechaPrimerEnvioSecretaria(Instant value) {
    this.dataReport.put("fechaPrimerEnvioSecretaria", value);

    // Valores retenidos por compatibilidad
    this.dataReport.put("fechaEnvioSecretaria",
        SgiReportHelper.formatDate(value,
            String.format("dd '%s' MMMM '%s' yyyy", SgiReportHelper.getMessage("common.de"),
                SgiReportHelper.getMessage("common.de"))));
  }

  public Map<String, Object> getData() {
    return this.dataReport;
  }

  public Informe getInforme() {
    return INFORME;
  }
}

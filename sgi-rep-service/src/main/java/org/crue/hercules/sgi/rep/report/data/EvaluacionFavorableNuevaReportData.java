package org.crue.hercules.sgi.rep.report.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.enums.Genero;
import org.crue.hercules.sgi.rep.enums.Informe;
import org.crue.hercules.sgi.rep.report.SgiReportHelper;
import org.crue.hercules.sgi.rep.report.data.objects.EvaluacionObject;
import org.crue.hercules.sgi.rep.report.data.objects.PersonaObject;

import com.deepoove.poi.data.PictureRenderData;

public class EvaluacionFavorableNuevaReportData implements ReportData {

  private static final Informe INFORME = Informe.ETI_EVALUACION_FAVORABLE_NUEVA;

  private final Map<String, Object> dataReport = new HashMap<>();
  PersonaObject tutorObject = null;

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
    this.dataReport.put("codigoMemoria", evaluacionObject.getMemoria().getNumReferencia());

    this.dataReport.put("tituloProyecto", evaluacionObject.getMemoria().getPeticionEvaluacion().getTitulo());
    this.dataReport.put("comite", evaluacionObject.getMemoria().getComite().getCodigo());
    this.dataReport.put("nombreInvestigacion", evaluacionObject.getMemoria().getComite().getNombreInvestigacion());
    this.dataReport.put("del",
        evaluacionObject.getMemoria().getComite().getGenero() == Genero.M
            ? SgiReportHelper.getMessage("common.del")
            : SgiReportHelper.getMessage("common.dela"));
    this.dataReport.put("este",
        evaluacionObject.getMemoria().getComite().getGenero() == Genero.M
            ? SgiReportHelper.getMessage("common.este")
            : SgiReportHelper.getMessage("common.esta"));

    this.dataReport.put("actividad", evaluacionObject.getMemoria().getPeticionEvaluacion().getActividad().toString());
    this.dataReport.put("fieldDelActividad",
        evaluacionObject.getMemoria().getPeticionEvaluacion().getActividad().getGenero() == Genero.M
            ? SgiReportHelper.getMessage("common.del")
            : SgiReportHelper.getMessage("common.dela"));
    this.dataReport.put("fieldDichoActividad",
        evaluacionObject.getMemoria().getPeticionEvaluacion().getActividad().getGenero() == Genero.M
            ? SgiReportHelper.getMessage("common.dicho")
            : SgiReportHelper.getMessage("common.dicha"));
    this.dataReport.put("fieldRealizadoActividad",
        evaluacionObject.getMemoria().getPeticionEvaluacion().getActividad().getGenero() == Genero.M
            ? SgiReportHelper.getMessage("common.realizado")
            : SgiReportHelper.getMessage("common.realizada"));
  }

  public void setTutor(PersonaDto value) {
    this.tutorObject = new PersonaObject(value);
    this.dataReport.put("tutor", tutorObject);
  }

  /**
   * Tiene que ser invocado después de establecer la evaluación y el tutor
   * 
   * @param value
   */
  public void setEquipo(List<PersonaDto> value) {
    this.dataReport.put("equipoTrabajo", value.stream().map(PersonaObject::new).toList());

    // Valores retenidos por compatibilidad
    this.dataReport.put("equipo", value.stream().map(persona -> {
      PersonaObject personaObject = new PersonaObject(persona);
      Map<String, String> properties = new HashMap<>();
      properties.put("nombre", personaObject.getNombre());
      if (tutorObject != null && personaObject.getId().equals(tutorObject.getId())) {
        properties.put("apellidos",
            (personaObject.getGenero() == Genero.M ? personaObject.getApellidos() + " " + "(Director "
                : "Directora ") + this.dataReport.get("fieldDelActividad") + " " + this.dataReport.get("actividad")
                + ")");
      } else {
        properties.put("apellidos", personaObject.getApellidos());
      }
      return properties;
    }).toList());
  }

  public void setInvestigador(PersonaDto value) {
    PersonaObject personaObject = new PersonaObject(value);
    this.dataReport.put("investigador", personaObject);

    // Valores retenidos por compatibilidad
    this.dataReport.put("nombreInvestigador", value.getNombre() + " " + value.getApellidos());
    this.dataReport.put("fieldDelInvestigador",
        personaObject.getGenero() == Genero.M ? SgiReportHelper.getMessage("common.del")
            : SgiReportHelper.getMessage("common.dela"));
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

  public Map<String, Object> getData() {
    return this.dataReport;
  }

  public Informe getInforme() {
    return INFORME;
  }
}

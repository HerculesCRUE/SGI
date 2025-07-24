package org.crue.hercules.sgi.rep.report.data;

import java.util.HashMap;
import java.util.Map;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.dto.csp.AutorizacionDto;
import org.crue.hercules.sgi.rep.dto.csp.ConvocatoriaDto;
import org.crue.hercules.sgi.rep.dto.sgemp.EmpresaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.VinculacionDto;
import org.crue.hercules.sgi.rep.enums.Genero;
import org.crue.hercules.sgi.rep.enums.Informe;
import org.crue.hercules.sgi.rep.report.SgiReportHelper;
import org.crue.hercules.sgi.rep.report.data.objects.AutorizacionObject;
import org.crue.hercules.sgi.rep.report.data.objects.ConvocatoriaObject;
import org.crue.hercules.sgi.rep.report.data.objects.PersonaObject;

import com.deepoove.poi.data.PictureRenderData;

public class AutorizacionProyectoExternoReportData implements ReportData {

  private static final Informe INFORME = Informe.CSP_AUTORIZACION_PROYECTO_EXTERNO;

  private final Map<String, Object> dataReport = new HashMap<>();

  public void setHeaderLogo(PictureRenderData value) {
    this.dataReport.put("headerImg", value);
  }

  public void setAutorizacion(AutorizacionDto value) {
    AutorizacionObject autorizacionObject = new AutorizacionObject(value);
    this.dataReport.put("autorizacion", autorizacionObject);

    // Values to retain compatibility
    this.dataReport.put("datosConvocatoria", autorizacionObject.getDatosConvocatoria());
    this.dataReport.put("horasDedicacion", autorizacionObject.getHorasDedicacion());
    this.dataReport.put("tituloProyecto", autorizacionObject.getTituloProyecto());
    this.dataReport.put("universidad", autorizacionObject.getDatosEntidad());
    this.dataReport.put("investigador", autorizacionObject.getDatosResponsable());
    this.dataReport.put("fieldCapitalizeInvestigador",
        ApplicationContextSupport.getMessage("field.capitalize.investigador.masculinoFemenino"));
    this.dataReport.put("fechaActual", SgiReportHelper.formatDateNow("SHORT"));
  }

  public void setConvocatoria(ConvocatoriaDto value) {
    ConvocatoriaObject convocatoriaObject = new ConvocatoriaObject(value);
    this.dataReport.put("convocatoria", convocatoriaObject);

    // Values to retain compatibility
    this.dataReport.put("datosConvocatoria", convocatoriaObject.getTitulo());
  }

  public void setSolicitante(PersonaDto value) {
    PersonaObject solicitanteObject = new PersonaObject(value);
    this.dataReport.put("solicitante", solicitanteObject);

    // Values to retain compatibility
    this.dataReport.put("solicitanteNombre", solicitanteObject.getNombre() + " " + solicitanteObject.getApellidos());
    this.dataReport.put("solicitanteNif", solicitanteObject.getNumeroDocumento());
    this.dataReport.put("isSolicitanteMasculino", solicitanteObject.getGenero() == Genero.M);

  }

  public void setSolicitanteVinculacion(VinculacionDto value) {
    this.dataReport.put("solicitanteVinculacion", value);

    // Values to retain compatibility
    if (value != null) {
      this.dataReport.put("solicitanteCatProfesional",
          value.getCategoriaProfesional() != null ? value.getCategoriaProfesional().getNombre() : '-');
      this.dataReport.put("solicitanteDepartamento",
          value.getDepartamento() != null ? value.getDepartamento().getNombre() : "-");
      this.dataReport.put("solicitanteCentro", value.getCentro() != null ? value.getCentro().getNombre() : "-");
    }
  }

  public void setEntidad(EmpresaDto value) {
    this.dataReport.put("entidad", value);

    // Values to retain compatibility
    this.dataReport.put("universidad", value.getNombre());
  }

  public void setInvestigador(PersonaDto value) {
    PersonaObject responsableObject = new PersonaObject(value);
    this.dataReport.put("responsable", responsableObject);

    // Values to retain compatibility
    dataReport.put("investigador", responsableObject.getNombre() + " " + responsableObject.getApellidos());
    dataReport.put("fieldCapitalizeInvestigador",
        (responsableObject.getGenero() == Genero.M
            ? ApplicationContextSupport.getMessage("field.capitalize.investigador.masculino")
            : ApplicationContextSupport.getMessage("field.capitalize.investigador.femenino")));
  }

  public Map<String, Object> getData() {
    return this.dataReport;
  }

  public Informe getInforme() {
    return INFORME;
  }
}

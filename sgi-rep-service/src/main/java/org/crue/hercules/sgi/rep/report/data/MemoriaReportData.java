package org.crue.hercules.sgi.rep.report.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.crue.hercules.sgi.rep.dto.eti.MemoriaDto;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaPeticionEvaluacionDto;
import org.crue.hercules.sgi.rep.dto.sgp.DatosContactoDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.VinculacionDto;
import org.crue.hercules.sgi.rep.report.data.objects.DatosContactoObject;
import org.crue.hercules.sgi.rep.report.data.objects.MemoriaObject;
import org.crue.hercules.sgi.rep.report.data.objects.MemoriaPeticionEvaluacionObject;

import com.deepoove.poi.data.PictureRenderData;

public class MemoriaReportData {

  private Map<String, Object> dataReport = new HashMap<>();

  public MemoriaReportData(Map<String, Object> formularioData) {
    this.dataReport = formularioData;
  }

  public void setHeaderLogo(PictureRenderData value) {
    this.dataReport.put("headerImg", value);
  }

  public void setFormularioId(Long value) {
    this.dataReport.put("formularioId", value);
  }

  public void setMemoria(MemoriaDto value) {
    this.dataReport.put("memoria", new MemoriaObject(value));
  }

  public void setMemoriasPeticionEvaluacion(List<MemoriaPeticionEvaluacionDto> value) {
    this.dataReport.put("memoriasPeticionEvaluacion",
        value != null ? value.stream().map(MemoriaPeticionEvaluacionObject::new).toList() : null);
  }

  public void setSolicitante(PersonaDto value) {
    this.dataReport.put("solicitante", value);
  }

  public void setSolicitanteDatosContacto(DatosContactoDto value) {
    DatosContactoObject datosContactoObject = new DatosContactoObject(value);
    this.dataReport.put("solicitanteDatosContacto", datosContactoObject);
  }

  public void setSolicitanteVinculacion(VinculacionDto value) {
    this.dataReport.put("solicitanteVinculacion", value);
  }

  public void setTutor(PersonaDto value) {
    this.dataReport.put("tutor", value);
  }

  public void setZoneId(TimeZone value) {
    this.dataReport.put("zoneId", value.toZoneId().getId());
  }

  public Map<String, Object> getDataReport() {
    return this.dataReport;
  }

}
package org.crue.hercules.sgi.rep.service.eti;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeFavorableMemoria;
import org.crue.hercules.sgi.rep.dto.eti.TareaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Servicio de generación de informe favorable memoria de ética
 */
@Service
@Validated
public class InformeFavorableMemoriaReportService extends InformeEvaluacionEvaluadorBaseReportService {
  private final PeticionEvaluacionService peticionEvaluacionService;
  private final PersonaService personaService;

  public InformeFavorableMemoriaReportService(SgiConfigProperties sgiConfigProperties,
      SgiApiConfService sgiApiConfService, PersonaService personaService,
      EvaluacionService evaluacionService, PeticionEvaluacionService peticionEvaluacionService) {

    super(sgiConfigProperties, sgiApiConfService, personaService, evaluacionService, null);
    this.peticionEvaluacionService = peticionEvaluacionService;
    this.personaService = personaService;
  }

  protected XWPFDocument getDocument(EvaluacionDto evaluacion, HashMap<String, Object> dataReport, InputStream path) {

    dataReport.put("codigoMemoria", evaluacion.getMemoria().getNumReferencia());

    addDataPersona(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef(),
        dataReport);

    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("EEEE dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    dataReport.put("fechaDictamen",
        formatInstantToString(evaluacion.getConvocatoriaReunion().getFechaEvaluacion(), pattern));

    String i18nActa = ApplicationContextSupport.getMessage("acta");
    String codigoActa = "(" + i18nActa + evaluacion.getConvocatoriaReunion().getNumeroActa() + "/"
        + formatInstantToString(evaluacion.getConvocatoriaReunion()
            .getFechaEvaluacion(), "YYYY")
        + "/" + evaluacion.getConvocatoriaReunion().getComite().getComite() + ")";
    dataReport.put("numeroActa", codigoActa);

    addDataEvaluacion(evaluacion, dataReport);

    addDataEquipoTrabajo(evaluacion, dataReport);

    return compileReportData(path, dataReport);
  }

  private void addDataEquipoTrabajo(EvaluacionDto evaluacion, HashMap<String, Object> dataReport) {
    List<TareaDto> tareas = peticionEvaluacionService
        .getTareasEquipoTrabajo(evaluacion.getMemoria().getPeticionEvaluacion().getId());
    List<PersonaDto> personas = new ArrayList<>();
    tareas.stream().filter(tarea -> tarea.getMemoria().getId().equals(evaluacion.getMemoria().getId()))
        .forEach(tarea -> {
          if (!personas.stream().map(persona -> persona.getId().toString()).collect(Collectors.toList())
              .contains(tarea.getEquipoTrabajo().getPersonaRef())) {
            PersonaDto persona = personaService.findById(tarea.getEquipoTrabajo().getPersonaRef());
            if (tarea.getEquipoTrabajo().getPersonaRef()
                .equals(evaluacion.getMemoria().getPeticionEvaluacion().getTutorRef())) {
              String textoDirectorMasculino = "(Director " + dataReport.get("fieldDelActividad") + " "
                  + dataReport.get("actividad") + ")";
              String textoDirectorFemenino = "(Directora " + dataReport.get("fieldDelActividad") + " "
                  + dataReport.get("actividad") + ")";
              String textoDirector = (persona.getSexo().getId().equals("V") ? textoDirectorMasculino
                  : textoDirectorFemenino);
              persona.setApellidos(persona.getApellidos() + " " + textoDirector);
            }
            personas.add(persona);

          }
        });

    if (ObjectUtils.isNotEmpty(evaluacion.getMemoria().getPeticionEvaluacion().getTutorRef())) {
      PersonaDto director = personaService.findById(evaluacion.getMemoria().getPeticionEvaluacion().getTutorRef());
      if (personas.stream().noneMatch(persona -> persona.getNumeroDocumento().equals(director.getNumeroDocumento()))) {
        String textoDirectorMasculino = "(Director " + dataReport.get("fieldDelActividad") + " "
            + dataReport.get("actividad") + ")";
        String textoDirectorFemenino = "(Directora " + dataReport.get("fieldDelActividad") + " "
            + dataReport.get("actividad") + ")";
        String textoDirector = (director.getSexo().getId().equals("V") ? textoDirectorMasculino
            : textoDirectorFemenino);
        director.setApellidos(director.getApellidos() + " " + textoDirector);
        personas.add(director);
      }
    }

    dataReport.put("equipo", personas);
  }

  public byte[] getReportInformeFavorableMemoria(ReportInformeFavorableMemoria sgiReport, Long idEvaluacion) {
    getReportFromEvaluacionId(sgiReport, idEvaluacion);
    return sgiReport.getContent();
  }

}
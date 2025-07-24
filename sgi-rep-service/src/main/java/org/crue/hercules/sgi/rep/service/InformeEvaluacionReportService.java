package org.crue.hercules.sgi.rep.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportInput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.FormularioDto;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionEvaluadorReportOutput;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.enums.Dictamen;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.report.data.BloqueApartadoReportData;
import org.crue.hercules.sgi.rep.report.data.EvaluacionReportData;
import org.crue.hercules.sgi.rep.service.eti.BaseApartadosRespuestasReportService;
import org.crue.hercules.sgi.rep.service.eti.ConfiguracionService;
import org.crue.hercules.sgi.rep.service.eti.EvaluacionService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.stereotype.Service;

import com.deepoove.poi.data.DocxRenderData;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe de evaluación de ética
 */
@Service
@Slf4j
public class InformeEvaluacionReportService extends SgiDocxReportService {

  private static final Long TIPO_COMENTARIO_GESTOR = 1L;

  private final PersonaService personaService;
  private final EvaluacionService evaluacionService;
  private final ConfiguracionService configuracionService;
  private final BaseApartadosRespuestasReportService baseApartadosRespuestasService;

  public InformeEvaluacionReportService(
      SgiApiConfService sgiApiConfService, PersonaService personaService,
      EvaluacionService evaluacionService,
      ConfiguracionService configuracionService,
      BaseApartadosRespuestasReportService baseApartadosRespuestasService) {

    super(sgiApiConfService);
    this.personaService = personaService;
    this.evaluacionService = evaluacionService;
    this.configuracionService = configuracionService;
    this.baseApartadosRespuestasService = baseApartadosRespuestasService;
  }

  public byte[] getReport(Long idEvaluacion) {
    try {
      EvaluacionReportData reportData = new EvaluacionReportData();
      EvaluacionDto evaluacion = evaluacionService.findById(idEvaluacion);

      reportData.setHeaderLogo(getHeaderLogo());
      reportData.setEvaluacion(evaluacion);
      reportData
          .setInvestigador(personaService.findById(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef()));
      reportData.setConfiguracion(configuracionService.findConfiguracion());
      reportData.setBloqueApartados(
          buildBloqueApartadosSubreport(evaluacion.getDictamen().getId(),
              getInformeEvaluacion(evaluacion.getId())));

      return buildPDF(reportData);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }
  }

  private DocxRenderData buildBloqueApartadosSubreport(Long idDictamen,
      InformeEvaluacionEvaluadorReportOutput informeEvaluacionEvaluadorReportOutput) {
    BloqueApartadoReportData reportData = new BloqueApartadoReportData();
    reportData.setIdDictamen(idDictamen);
    reportData.setIdDictamenNoProcedeEvaluar(Dictamen.NO_PROCEDE_EVALUAR.getId());
    if (informeEvaluacionEvaluadorReportOutput != null
        && ObjectUtils.isNotEmpty(informeEvaluacionEvaluadorReportOutput.getBloques())
        && !informeEvaluacionEvaluadorReportOutput.getBloques().isEmpty()) {
      if (informeEvaluacionEvaluadorReportOutput.getBloques().stream().findAny().isPresent()
          && informeEvaluacionEvaluadorReportOutput.getBloques().stream().findAny().get().getApartados().stream()
              .findAny().isPresent()) {
        reportData.setNumComentarios(
            informeEvaluacionEvaluadorReportOutput.getBloques().stream().findAny().get().getApartados().stream()
                .findAny().get().getNumeroComentariosTotalesInforme());
      }
      reportData.setBloques(informeEvaluacionEvaluadorReportOutput.getBloques());
    } else {
      return null;
    }
    return buildSubReport(reportData);
  }

  private InformeEvaluacionEvaluadorReportOutput getInformeEvaluacion(Long idEvaluacion) {
    log.debug("getInformeEvaluacion(idEvaluacion)- start");

    InformeEvaluacionEvaluadorReportOutput informeEvaluacionEvaluadorReportOutput = new InformeEvaluacionEvaluadorReportOutput();

    informeEvaluacionEvaluadorReportOutput.setBloques(new ArrayList<>());

    EvaluacionDto evaluacion = evaluacionService.findById(idEvaluacion);
    informeEvaluacionEvaluadorReportOutput.setEvaluacion(evaluacion);

    Integer numComentarios = null;

    List<ComentarioDto> comentarios = null;

    comentarios = evaluacionService.findByEvaluacionIdGestor(idEvaluacion);
    numComentarios = evaluacionService.countByEvaluacionIdAndTipoComentarioId(evaluacion.getId(),
        TIPO_COMENTARIO_GESTOR);

    if (null != comentarios && !comentarios.isEmpty()) {
      final Set<Long> apartados = new HashSet<>();
      comentarios.forEach(
          c -> {
            baseApartadosRespuestasService.getApartadoService().findTreeApartadosById(apartados, c.getApartado());
            PersonaDto personaCreated = personaService.findById(c.getCreatedBy());
            c.setPersonaCreated(personaCreated.getNombre() + " " + personaCreated.getApellidos());
            c.setSexoPersonaCreated(personaCreated.getSexo().getId());
          });

      Long idFormulario = 0L;

      Optional<FormularioDto> formulario = comentarios.stream()
          .map(c -> c.getApartado().getBloque().getFormulario())
          .filter(f -> f != null)
          .findFirst();

      if (formulario.isPresent()) {
        idFormulario = formulario.get().getId();
      }

      // @formatter:off
        BloquesReportInput etiBloquesReportInput = BloquesReportInput.builder()
        .idMemoria(idEvaluacion)
        .idFormulario(idFormulario)
        .mostrarRespuestas(false)
        .mostrarContenidoApartado(false)
        .comentarios(comentarios)
        .apartados(apartados)
        .numeroComentarios(numComentarios)
        .build();
        // @formatter:on

      BloquesReportOutput reportOutput = baseApartadosRespuestasService
          .getDataFromApartadosAndRespuestas(etiBloquesReportInput);

      final int orden = informeEvaluacionEvaluadorReportOutput.getBloques().size();
      for (BloqueOutput bloque : reportOutput.getBloques()) {
        bloque.setOrden(bloque.getOrden() + orden);
      }

      informeEvaluacionEvaluadorReportOutput.getBloques().addAll(reportOutput.getBloques());
    } else {

      BloquesReportInput etiBloquesReportInput = BloquesReportInput.builder()
          .idMemoria(idEvaluacion)
          .idFormulario(0L)
          .mostrarRespuestas(false)
          .mostrarContenidoApartado(false)
          .comentarios(null)
          .apartados(null)
          .numeroComentarios(numComentarios)
          .build();

      BloquesReportOutput reportOutput = baseApartadosRespuestasService
          .getDataFromApartadosAndRespuestas(etiBloquesReportInput);

      if (informeEvaluacionEvaluadorReportOutput.getBloques().isEmpty() && ObjectUtils.isNotEmpty(reportOutput)) {
        informeEvaluacionEvaluadorReportOutput.setBloques(reportOutput.getBloques());
      } else if (ObjectUtils.isNotEmpty(reportOutput)) {
        informeEvaluacionEvaluadorReportOutput.getBloques().addAll(reportOutput.getBloques());
      }

    }

    return informeEvaluacionEvaluadorReportOutput;
  }
}
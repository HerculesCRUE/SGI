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
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.enums.Dictamen;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.report.data.BloqueApartadoFichaEvaluadorReportData;
import org.crue.hercules.sgi.rep.report.data.FichaEvaluadorReportData;
import org.crue.hercules.sgi.rep.service.eti.BaseApartadosRespuestasReportService;
import org.crue.hercules.sgi.rep.service.eti.EvaluacionService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.stereotype.Service;

import com.deepoove.poi.data.DocxRenderData;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe de evaluador de ética
 */
@Service
@Slf4j
public class InformeEvaluadorReportService extends SgiDocxReportService {

  private static final Long TIPO_COMENTARIO_EVALUADOR = 2L;

  private final PersonaService personaService;
  private final EvaluacionService evaluacionService;
  private final BaseApartadosRespuestasReportService baseApartadosRespuestasService;

  public InformeEvaluadorReportService(SgiApiConfService sgiApiConfService,
      PersonaService personaService, EvaluacionService evaluacionService,
      BaseApartadosRespuestasReportService baseApartadosRespuestasService) {

    super(sgiApiConfService);
    this.personaService = personaService;
    this.evaluacionService = evaluacionService;
    this.baseApartadosRespuestasService = baseApartadosRespuestasService;
  }

  public byte[] getReport(Long idEvaluacion) {
    try {
      FichaEvaluadorReportData reportData = new FichaEvaluadorReportData();
      EvaluacionDto evaluacion = evaluacionService.findById(idEvaluacion);

      reportData.setHeaderLogo(getHeaderLogo());
      reportData.setEvaluacion(evaluacion);
      reportData
          .setSolicitante(personaService.findById(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef()));
      reportData.setBloqueApartados(
          buildBloqueApartadosSubReport(
              (evaluacion.getDictamen() != null ? evaluacion.getDictamen().getId()
                  : null),
              getInformeEvaluadorEvaluacion(evaluacion)));

      return buildPDF(reportData);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }
  }

  private DocxRenderData buildBloqueApartadosSubReport(Long idDictamen,
      BloquesReportOutput informeEvaluacionEvaluadorReportOutput) {
    BloqueApartadoFichaEvaluadorReportData reportData = new BloqueApartadoFichaEvaluadorReportData();
    reportData.setIdDictamen(idDictamen);
    reportData.setIdDictamenNoProcedeEvaluar(Dictamen.NO_PROCEDE_EVALUAR.getId());
    if (ObjectUtils.isNotEmpty(informeEvaluacionEvaluadorReportOutput)
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

  private BloquesReportOutput getInformeEvaluadorEvaluacion(EvaluacionDto evaluacion) {
    log.debug("getInformeEvaluacion(idEvaluacion)- start");

    BloquesReportOutput informeEvaluacionEvaluadorReportOutput = new BloquesReportOutput();

    informeEvaluacionEvaluadorReportOutput.setBloques(new ArrayList<>());

    try {
      informeEvaluacionEvaluadorReportOutput.setEvaluacion(evaluacion);

      Integer numComentarios = null;

      List<ComentarioDto> comentarios = null;

      comentarios = evaluacionService.findByEvaluacionEvaluadorEstadoCerrado(evaluacion.getId());
      numComentarios = evaluacionService.countByEvaluacionIdAndTipoComentarioIdAndEstadoCerrado(evaluacion.getId(),
          TIPO_COMENTARIO_EVALUADOR);

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
        .idMemoria(evaluacion.getId())
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
            .idMemoria(evaluacion.getId())
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

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }

    return informeEvaluacionEvaluadorReportOutput;
  }

}
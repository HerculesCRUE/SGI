package org.crue.hercules.sgi.rep.service.eti;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoDefinicionDto;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoDto;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloqueDto;
import org.crue.hercules.sgi.rep.dto.eti.BloqueNombreDto;
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportInput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.dto.eti.RespuestaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.util.SgiReportContextHolder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio base de generación de informes relacionado con apartados y
 * respuestas de ética
 */
@Service
@Slf4j
public class BaseApartadosRespuestasReportService {

  private final BloqueService bloqueService;
  private final ApartadoService apartadoService;
  private final SgiFormlyService sgiFormlyService;
  private final RespuestaService respuestaService;

  public BaseApartadosRespuestasReportService(
      BloqueService bloqueService,
      ApartadoService apartadoService, SgiFormlyService sgiFormlyService, RespuestaService respuestaService) {
    this.bloqueService = bloqueService;
    this.apartadoService = apartadoService;
    this.sgiFormlyService = sgiFormlyService;
    this.respuestaService = respuestaService;
  }

  public ApartadoService getApartadoService() {
    return this.apartadoService;
  }

  /**
   * Obtenemos los datos relacionados con los apartados y/o sus correspondientes
   * respuestas y/o comentarios
   * 
   * @param input BloquesReportInput
   * @return BloquesReportOutput
   */
  public BloquesReportOutput getDataFromApartadosAndRespuestas(@Valid BloquesReportInput input) {
    log.debug("getDataFromApartadosAndRespuestas(EtiBloquesReportInput) - start");

    BloquesReportOutput bloquesReportOutput = new BloquesReportOutput();
    bloquesReportOutput.setBloques(new ArrayList<>());

    try {
      List<BloqueDto> bloques = new ArrayList<>();
      if (input.getIdFormulario() > 0) {
        bloques.addAll(bloqueService.findByFormularioId(input.getIdFormulario()));
      }
      if (!CollectionUtils.isEmpty(input.getComentarios())) {
        bloques.add(bloqueService.getBloqueComentariosGenerales());
      }

      final int tamBloques = bloquesReportOutput.getBloques().size();

      for (BloqueDto bloque : bloques) {

        boolean parseBloque = true;
        if (null != input.getBloques()) {
          parseBloque = input.getBloques().contains(bloque.getId());
        }

        if (parseBloque) {
          parseBloque(input, bloquesReportOutput, bloque, tamBloques);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new GetDataReportException(e);
    }
    return bloquesReportOutput;
  }

  private void parseBloque(BloquesReportInput input, BloquesReportOutput bloquesReportOutput, BloqueDto bloque,
      int tamBloques) {

    Optional<BloqueNombreDto> bloqueNombre = bloque.getNombre().stream()
        .filter(n -> n.getLang().equalsIgnoreCase(SgiReportContextHolder.getLanguage().getCode())).findFirst();
    String nombre = bloqueNombre.get().getValue();
    if (bloque.getFormulario() != null) {
      nombre = bloque.getOrden() + ". " + nombre;
    }

    // @formatter:off
    BloqueOutput bloqueOutput = BloqueOutput.builder()
      .id(bloque.getId())
      .nombre(nombre)
      .orden(tamBloques + bloque.getOrden())
      .apartados(new ArrayList<>())
      .build();
    // @formatter:on
    List<ApartadoDto> apartados = apartadoService.findByBloqueId(bloque.getId());

    for (ApartadoDto apartado : apartados) {
      boolean parseApartado = true;
      if (null != input.getApartados()) {
        parseApartado = input.getApartados().contains(apartado.getId());
      }

      if (parseApartado) {
        ApartadoOutput apartadoOutput = parseApartadoAndHijos(input, apartado);
        bloqueOutput.getApartados().add(apartadoOutput);
      }
    }

    if (null != bloqueOutput.getApartados() && !bloqueOutput.getApartados().isEmpty()) {
      bloquesReportOutput.getBloques().add(bloqueOutput);
    }
  }

  private ApartadoOutput parseApartadoAndHijos(BloquesReportInput input, ApartadoDto apartado) {
    ApartadoOutput apartadoOutput = parseApartadoOutput(input, apartado);
    apartadoOutput.setApartadosHijos(findApartadosHijosAndRespuestas(input, apartado.getId()));
    return apartadoOutput;
  }

  private List<ApartadoOutput> findApartadosHijosAndRespuestas(BloquesReportInput input, Long idPadre) {
    List<ApartadoOutput> apartadosHijosResult = new ArrayList<>();

    List<ApartadoDto> apartados = apartadoService.findByPadreId(idPadre);

    if (CollectionUtils.isNotEmpty(apartados)) {
      for (ApartadoDto apartado : apartados) {
        boolean parseApartado = true;
        if (null != input.getApartados()) {
          parseApartado = input.getApartados().contains(apartado.getId());
        }

        if (parseApartado) {
          ApartadoOutput apartadoOutput = parseApartadoAndHijos(input, apartado);
          apartadosHijosResult.add(apartadoOutput);
        }
      }
    }
    return apartadosHijosResult;
  }

  private ApartadoOutput parseApartadoOutput(BloquesReportInput input, ApartadoDto apartado) {
    ApartadoOutput apartadoOutput = null;

    List<ComentarioDto> comentarios = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(input.getComentarios())) {
      comentarios = input.getComentarios().stream()
          .filter(c -> c.getApartado().getId().compareTo(apartado.getId()) == 0).collect(Collectors.toList());
    }

    RespuestaDto respuestaAnteriorDto = null;
    if (ObjectUtils.isNotEmpty(input.getIdMemoriaOriginal())) {
      respuestaAnteriorDto = respuestaService.findByMemoriaIdAndApartadoId(input.getIdMemoriaOriginal(),
          apartado.getId());
    }

    RespuestaDto respuestaDto = null;
    if (null != input.getMostrarRespuestas() && input.getMostrarRespuestas()) {
      respuestaDto = respuestaService.findByMemoriaIdAndApartadoId(input.getIdMemoria(), apartado.getId());
    }

    Optional<ApartadoDefinicionDto> apartadoDefinicion = apartado.getDefinicion().stream()
        .filter(d -> d.getLang().equalsIgnoreCase(SgiReportContextHolder.getLanguage().getCode())).findFirst();

    // @formatter:off
    apartadoOutput = ApartadoOutput.builder()
      .id(apartado.getId())
      .nombre(apartadoDefinicion.get().getNombre())
      .orden(apartado.getOrden())
      .esquema(apartadoDefinicion.get().getEsquema())
      .respuesta(respuestaDto)
      .comentarios(comentarios)
      .mostrarContenidoApartado(input.getMostrarContenidoApartado())
      .modificado(this.isRespuestaModificada(respuestaDto, respuestaAnteriorDto))
      .numeroComentariosTotalesInforme(input.getNumeroComentarios())
      .build();
    // @formatter:on

    sgiFormlyService.parseApartadoAndRespuestaAndComentarios(apartadoOutput);

    return apartadoOutput;
  }

  private boolean isRespuestaModificada(RespuestaDto respuestaDto, RespuestaDto respuestaAnteriorDto) {
    return ObjectUtils.isNotEmpty(respuestaDto) && ObjectUtils.isNotEmpty(respuestaAnteriorDto)
        && !respuestaDto.getValor()
            .equals(respuestaAnteriorDto.getValor());
  }

}
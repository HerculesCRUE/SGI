package org.crue.hercules.sgi.rep.service.eti;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.table.DefaultTableModel;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoDto;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloqueDto;
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportInput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.dto.eti.ElementOutput;
import org.crue.hercules.sgi.rep.dto.eti.RespuestaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.SgiReportExcelService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio base de generación de informes relacionado con apartados y
 * respuestas de ética
 */
@Service
@Slf4j
public class BaseApartadosRespuestasReportService {

  private static final Long DICTAMEN_NO_PROCEDE_EVALUAR = 4L;

  private final BloqueService bloqueService;
  private final ApartadoService apartadoService;
  private final SgiFormlyService sgiFormlyService;
  private final RespuestaService respuestaService;
  private final SgiReportExcelService sgiExcelService;

  public BaseApartadosRespuestasReportService(SgiConfigProperties sgiConfigProperties,
      SgiApiConfService sgiApiConfService, BloqueService bloqueService,
      ApartadoService apartadoService, SgiFormlyService sgiFormlyService, RespuestaService respuestaService,
      SgiReportExcelService sgiExcelService) {
    this.bloqueService = bloqueService;
    this.apartadoService = apartadoService;
    this.sgiFormlyService = sgiFormlyService;
    this.respuestaService = respuestaService;
    this.sgiExcelService = sgiExcelService;
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
      throw new GetDataReportException();
    }
    return bloquesReportOutput;
  }

  private void parseBloque(BloquesReportInput input, BloquesReportOutput bloquesReportOutput, BloqueDto bloque,
      int tamBloques) {

    String nombre = bloque.getNombre();
    if (bloque.getFormulario() != null) {
      nombre = bloque.getOrden() + ". " + bloque.getNombre();
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

  public List<ApartadoOutput> findApartadosHijosAndRespuestas(BloquesReportInput input, Long idPadre) {
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

  public ApartadoOutput parseApartadoOutput(BloquesReportInput input, ApartadoDto apartado) {
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

    // @formatter:off
    apartadoOutput = ApartadoOutput.builder()
      .id(apartado.getId())
      .nombre(apartado.getNombre())
      .orden(apartado.getOrden())
      .esquema(apartado.getEsquema())
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

  public ApartadoOutput getApartadoOutputFromElementRow(int index, List<ApartadoOutput> jerarquiaApartados) {
    ApartadoOutput apartadoOutput = null;
    if (jerarquiaApartados.size() >= index + 1 && null != jerarquiaApartados.get(index)) {
      apartadoOutput = jerarquiaApartados.get(index);
      if (null == apartadoOutput.getOrden()) {
        apartadoOutput.setOrden(0);
      }
      if (null == apartadoOutput.getId()) {
        apartadoOutput.setId(0L);
      }
      if (null == apartadoOutput.getTitulo()) {
        apartadoOutput.setTitulo("");
      }
    } else {
      apartadoOutput = ApartadoOutput.builder().id(0L).titulo("").orden(0).build();
    }

    return apartadoOutput;
  }

  /**
   * Obtiene el DefaultTableModel de un campo de tipo table-crud
   * 
   * @param elemento ElementOutput
   * @return DefaultTableModel
   */
  public DefaultTableModel parseTableCrudTableModel(ElementOutput elemento) {
    DefaultTableModel tableModel = new DefaultTableModel();
    try {
      ObjectMapper mapper = new ObjectMapper();
      List<List<ElementOutput>> elementsTableCrud = mapper.readValue(elemento.getContent(),
          new TypeReference<List<List<ElementOutput>>>() {
          });

      Vector<Object> columns = new Vector<>();
      Vector<Vector<Object>> elements = new Vector<>();
      for (int i = 0; i < elementsTableCrud.size(); i++) {
        List<ElementOutput> rowElementTableCrud = elementsTableCrud.get(i);

        Vector<Object> elementsRow = new Vector<>();
        for (ElementOutput elementTableCrud : rowElementTableCrud) {
          if (i == 0) {
            String columnName = null != elementTableCrud.getNombre() ? elementTableCrud.getNombre() : "";
            columns.add(columnName);
          }
          String content = null != elementTableCrud.getContent() ? elementTableCrud.getContent() : "";
          elementsRow.add(content);
        }
        elements.add(elementsRow);
      }

      tableModel.setDataVector(elements, columns);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return tableModel;
  }

  public ApartadoOutput generateApartadoOutputBasic(int apartadoIndex, String apartadoTitle) {
    // @formatter:off
    return ApartadoOutput.builder()
      .id(Long.valueOf(apartadoIndex))
      .nombre(apartadoTitle)
      .titulo(apartadoTitle)
      .orden(apartadoIndex)
      .elementos(new ArrayList<>())
      .apartadosHijos(new ArrayList<>())
      .build();
    // @formatter:on
  }

  public ElementOutput generateTemplateElementOutput(String question, String answer) {
    // @formatter:off
    return ElementOutput.builder()
      .nombre("")
      .tipo(SgiFormlyService.TEMPLATE_PROPERTY)
      .content(SgiFormlyService.P_HTML + question + SgiFormlyService.I_HTML + answer + SgiFormlyService.I_CLOSE_HTML +  SgiFormlyService.P_CLOSE_HTML )
      .build();
    // @formatter:on
  }
}
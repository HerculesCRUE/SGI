package org.crue.hercules.sgi.eti.service;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.dto.ApartadoOutput;
import org.crue.hercules.sgi.eti.dto.BloqueOutput;
import org.crue.hercules.sgi.eti.dto.ElementOutput;
import org.crue.hercules.sgi.eti.dto.EtiMXXReportOutput;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.dto.RespuestaOutput;
import org.crue.hercules.sgi.eti.dto.RespuestaOutput.TipoDocumentoDto;
import org.crue.hercules.sgi.eti.exceptions.rep.GetDataReportMxxException;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Apartado_;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Bloque_;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Respuesta;
import org.crue.hercules.sgi.eti.repository.FormularioRepository;
import org.crue.hercules.sgi.eti.util.I18nUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar informes.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class EtiReportService {

  private static final String DATOS_SOLICITANTE_TYPE = "datosSolicitanteType";
  private static final String NO_REF_CEID = "NoRefCEID: ";

  private final PeticionEvaluacionService peticionEvaluacionService;
  private final MemoriaService memoriaService;
  private final BloqueService bloqueService;
  private final ApartadoService apartadoService;
  private final RespuestaService respuestaService;

  private final FormularioRepository formularioRepository;

  private final SgiFormlyService sgiFormlyService;
  private final SgiConfigProperties sgiConfigProperties;

  public EtiReportService(MemoriaService memoriaService, PeticionEvaluacionService peticionEvaluacionService,
      BloqueService bloqueService, ApartadoService apartadoService, SgiFormlyService sgiFormlyService,
      RespuestaService respuestaService, FormularioRepository formularioRepository,
      SgiConfigProperties sgiConfigProperties) {
    this.memoriaService = memoriaService;
    this.peticionEvaluacionService = peticionEvaluacionService;
    this.bloqueService = bloqueService;
    this.apartadoService = apartadoService;
    this.sgiFormlyService = sgiFormlyService;
    this.respuestaService = respuestaService;
    this.formularioRepository = formularioRepository;
    this.sgiConfigProperties = sgiConfigProperties;
  }

  /**
   * Obtener la estructura de datos necesario para los informes M10, M20 y M30
   * 
   * @param idMemoria
   * @return EtiMXXReportOutput
   */
  public EtiMXXReportOutput getDataReportMXX(Long idMemoria) {

    log.debug("getDataReportMXX(idMemoria) - start");
    Assert.notNull(idMemoria, "El id de la memoria no puede ser null");

    EtiMXXReportOutput etiReportOutput = new EtiMXXReportOutput();
    try {

      etiReportOutput.setBloques(new ArrayList<>());

      Memoria memoria = memoriaService.findById(idMemoria);
      PeticionEvaluacion peticionEvaluacion = peticionEvaluacionService
          .findById(memoria.getPeticionEvaluacion().getId());
      memoria.setPeticionEvaluacion(peticionEvaluacion);
      Formulario formulario = formularioRepository.findByMemoriaId(idMemoria);
      memoria.getComite().setFormulario(formulario);

      // TODO internacionalizar
      StringBuilder titulo = new StringBuilder();
      titulo.append(formulario.getNombre());

      titulo.append(" - Memoria para el ");
      titulo.append(memoria.getComite().getComite());
      titulo.append(": ");
      // TODO meter titulo del informe
      // titulo.append(memoria.getComite().getTitulo());
      etiReportOutput.setTitulo(titulo.toString());

      List<MemoriaPeticionEvaluacion> memorias = memoriaService
          .findMemoriaByPeticionEvaluacionMaxVersion(memoria.getPeticionEvaluacion().getId());

      // Obtenemos los datos relacionados ccon la petición de evaluación
      getDataFromPeticionEvaluacion(memoria, memorias, etiReportOutput);

      // Obtenemos los datos relacionados con los apartados y sus correspondientes
      // respuestas
      getDataFromApartadosAndRespuestas(formulario.getId(), idMemoria, etiReportOutput);

    } catch (Exception e) {
      log.error(e.getMessage());
      throw new GetDataReportMxxException();
    }

    log.debug("getDataReportMXX(idMemoria) - end");

    return etiReportOutput;
  }

  /**
   * Obtenemos los datos relacionados ccon la petición de evaluación
   * 
   * @param memoria         Memoria
   * @param memorias        List<MemoriaPeticionEvaluacion>
   * @param etiReportOutput
   */
  private void getDataFromPeticionEvaluacion(Memoria memoria, List<MemoriaPeticionEvaluacion> memorias,
      EtiMXXReportOutput etiReportOutput) {
    PeticionEvaluacion peticionEvaluacion = memoria.getPeticionEvaluacion();

    int bloqueIndex = etiReportOutput.getBloques().size() + 1;
    // @formatter:off
    BloqueOutput bloqueOutput = BloqueOutput.builder()
      .id(Long.valueOf(bloqueIndex))
      .nombre(NO_REF_CEID + memoria.getNumReferencia())
      .orden(bloqueIndex)
      .apartados(new ArrayList<>())
      .build();
    // @formatter:on

    // TODO Internacionalizar labels
    getApartadoDatosSolicitante(bloqueOutput, peticionEvaluacion.getPersonaRef());

    getApartadoTituloProyecto(bloqueOutput, peticionEvaluacion.getTitulo());
    getApartadoTipoActividad(bloqueOutput, peticionEvaluacion.getTipoActividad().getNombre());
    getApartadoFinanciacion(bloqueOutput, peticionEvaluacion);
    getApartadoFechasClave(bloqueOutput, peticionEvaluacion);
    getApartadoResumen(bloqueOutput, peticionEvaluacion.getResumen());
    getApartadoMemorias(bloqueOutput, memorias);

    etiReportOutput.getBloques().add(bloqueOutput);
  }

  private void getApartadoDatosSolicitante(BloqueOutput bloqueOutput, String personaRef) {
    int apartadoIndex = bloqueOutput.getApartados().size() + 1;
    String apartadoTitle = "Datos del solicitante";
    ApartadoOutput apartadoOutput = generateApartadoOutputBasic(apartadoIndex, apartadoTitle);

    // @formatter:off
    ElementOutput dataSolicitanteElement = ElementOutput.builder()
      .nombre("")
      .tipo(DATOS_SOLICITANTE_TYPE)
      .content(personaRef)
      .build();
    // @formatter:on

    apartadoOutput.getElementos().add(dataSolicitanteElement);

    bloqueOutput.getApartados().add(apartadoOutput);
  }

  private void getApartadoTituloProyecto(BloqueOutput bloqueOutput, String titulo) {

    int apartadoIndex = bloqueOutput.getApartados().size() + 1;
    String apartadoTitle = "Título del proyecto";
    ApartadoOutput apartadoOutput = generateApartadoOutputBasic(apartadoIndex, apartadoTitle);

    String question = "";
    String answer = null != titulo ? titulo : "";
    ElementOutput tituloElementOutput = generateTemplateElementOutput(question, answer);

    apartadoOutput.getElementos().add(tituloElementOutput);
    bloqueOutput.getApartados().add(apartadoOutput);
  }

  private void getApartadoTipoActividad(BloqueOutput bloqueOutput, String tipoActividad) {
    int apartadoIndex = bloqueOutput.getApartados().size() + 1;
    String apartadoTitle = "Tipo de actividad";
    ApartadoOutput apartadoOutput = generateApartadoOutputBasic(apartadoIndex, apartadoTitle);

    String question = "";
    String answer = null != tipoActividad ? tipoActividad : "";
    ElementOutput tipoActividadElementOutput = generateTemplateElementOutput(question, answer);

    apartadoOutput.getElementos().add(tipoActividadElementOutput);
    bloqueOutput.getApartados().add(apartadoOutput);
  }

  private void getApartadoFinanciacion(BloqueOutput bloqueOutput, PeticionEvaluacion peticionEvaluacion) {
    int apartadoIndex = bloqueOutput.getApartados().size() + 1;

    String apartadoTitle = "Financiación";
    ApartadoOutput apartadoOutput = generateApartadoOutputBasic(apartadoIndex, apartadoTitle);

    String question = "¿Se dispone de financiación para la realización del proyecto?: ";
    String disponeFinanciacion = peticionEvaluacion.getExisteFinanciacion() ? "Sí" : "No";
    ElementOutput disponeFinanciacionElementOutput = generateTemplateElementOutput(question, disponeFinanciacion);
    apartadoOutput.getElementos().add(disponeFinanciacionElementOutput);

    question = "Indicar la fuente de financiación: ";
    String fuenteFinanciacion = null != peticionEvaluacion.getFuenteFinanciacion()
        ? peticionEvaluacion.getFuenteFinanciacion()
        : "";
    ElementOutput fuenteFinanciacionElementOutput = generateTemplateElementOutput(question, fuenteFinanciacion);
    apartadoOutput.getElementos().add(fuenteFinanciacionElementOutput);

    question = "Estado de la financiación: ";
    String estadoFinanciacion = null != peticionEvaluacion.getEstadoFinanciacion()
        ? EstadoFinanciacionI18n.getI18nMessageFromValorSocialEnum(peticionEvaluacion.getEstadoFinanciacion().name())
        : "";
    ElementOutput estadoElementOutput = generateTemplateElementOutput(question, estadoFinanciacion);
    apartadoOutput.getElementos().add(estadoElementOutput);

    bloqueOutput.getApartados().add(apartadoOutput);
  }

  private void getApartadoFechasClave(BloqueOutput bloqueOutput, PeticionEvaluacion peticionEvaluacion) {
    int apartadoIndex = bloqueOutput.getApartados().size() + 1;

    String apartadoTitle = "Fechas clave del proyecto";
    ApartadoOutput apartadoOutput = generateApartadoOutputBasic(apartadoIndex, apartadoTitle);

    String question = "Fecha prevista inicio del proyecto: ";
    String fechaInicio = formatInstantToString(peticionEvaluacion.getFechaInicio());
    ElementOutput fechaInicioElementOutput = generateTemplateElementOutput(question, fechaInicio);
    apartadoOutput.getElementos().add(fechaInicioElementOutput);

    question = "Fecha prevista fin del proyecto: ";
    String fechaFin = formatInstantToString(peticionEvaluacion.getFechaFin());
    ElementOutput fechaFinElementOutput = generateTemplateElementOutput(question, fechaFin);
    apartadoOutput.getElementos().add(fechaFinElementOutput);

    bloqueOutput.getApartados().add(apartadoOutput);
  }

  private void getApartadoResumen(BloqueOutput bloqueOutput, String resumen) {
    int apartadoIndex = bloqueOutput.getApartados().size() + 1;
    String apartadoTitle = "Resumen del proyecto";
    ApartadoOutput apartadoOutput = generateApartadoOutputBasic(apartadoIndex, apartadoTitle);

    String question = "";
    String answer = null != resumen ? resumen : "";
    ElementOutput tipoActividadElementOutput = generateTemplateElementOutput(question, answer);

    apartadoOutput.getElementos().add(tipoActividadElementOutput);
    bloqueOutput.getApartados().add(apartadoOutput);
  }

  private void getApartadoMemorias(BloqueOutput bloqueOutput, List<MemoriaPeticionEvaluacion> memorias) {
    int apartadoIndex = bloqueOutput.getApartados().size() + 1;
    String apartadoTitle = "Memorias del proyecto";
    ApartadoOutput apartadoOutput = generateApartadoOutputBasic(apartadoIndex, apartadoTitle);

    List<List<ElementOutput>> elementsTableCrud = new ArrayList<>();

    memorias.forEach(memoriaPeticionEvaluacion -> {
      List<ElementOutput> rowElementsTableCrud = new ArrayList<>();
      // @formatter:off
      ElementOutput elementOutputTableCrud = ElementOutput.builder()
        .nombre("Comité")
        .content(memoriaPeticionEvaluacion.getComite().getComite())
        .build();
      rowElementsTableCrud.add(elementOutputTableCrud);

      elementOutputTableCrud = ElementOutput.builder()
        .nombre("NoRefCEID")
        .content(memoriaPeticionEvaluacion.getNumReferencia())
        .build();
      rowElementsTableCrud.add(elementOutputTableCrud);

      elementOutputTableCrud = ElementOutput.builder()
        .nombre("Nombre")
        .content(memoriaPeticionEvaluacion.getTitulo())
        .build();
      rowElementsTableCrud.add(elementOutputTableCrud);

      elementOutputTableCrud = ElementOutput.builder()
        .nombre("Estado")
        .content(memoriaPeticionEvaluacion.getEstadoActual().getNombre())
        .build();
      // @formatter:on
      rowElementsTableCrud.add(elementOutputTableCrud);

      elementsTableCrud.add(rowElementsTableCrud);
    });

    String contentTableCrud = "";

    if (!elementsTableCrud.isEmpty()) {
      try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, elementsTableCrud);
        contentTableCrud = new String(out.toByteArray());
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    }

    // @formatter:off
    ElementOutput elementOutput = ElementOutput.builder()
      .nombre("memoriasProyecto")
      .tipo(SgiFormlyService.TABLE_CRUD_TYPE)
      .content(contentTableCrud)
      .build();
    // @formatter:on

    apartadoOutput.getElementos().add(elementOutput);
    bloqueOutput.getApartados().add(apartadoOutput);
  }

  private String formatInstantToString(Instant instantDate) {
    String result = "";

    // TODO revisar parseo de fecha con timezone porque no coge la correcta
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        .withZone(sgiConfigProperties.getTimeZone().toZoneId());
    result = formatter.format(instantDate);

    return result;
  }

  private ApartadoOutput generateApartadoOutputBasic(int apartadoIndex, String apartadoTitle) {
    // @formatter:off
    ApartadoOutput apartadoOutput = ApartadoOutput.builder()
        .id(Long.valueOf(apartadoIndex))
        .nombre(apartadoTitle)
        .titulo(apartadoTitle)
        .orden(apartadoIndex)
        .elementos(new ArrayList<>())
        .apartadosHijos(new ArrayList<>())
        .build();
    // @formatter:on
    return apartadoOutput;
  }

  private ElementOutput generateTemplateElementOutput(String question, String answer) {
    // @formatter:off
    ElementOutput elementOutput = ElementOutput.builder()
      .nombre("")
      .tipo(SgiFormlyService.TEMPLATE_PROPERTY)
      .content(SgiFormlyService.P_HTML + question + SgiFormlyService.I_HTML + answer + SgiFormlyService.I_CLOSE_HTML +  SgiFormlyService.P_CLOSE_HTML )
      .build();
    // @formatter:on
    return elementOutput;
  }

  /**
   * Obtenemos los datos relacionados con los apartados y sus correspondientes
   * respuestas
   * 
   * @param idFormulario
   * @param idMemoria
   * @param etiReportOutput
   */
  private void getDataFromApartadosAndRespuestas(Long idFormulario, Long idMemoria,
      EtiMXXReportOutput etiReportOutput) {

    // Obtenemos todos los bloques del formulario
    Pageable pageableBloque = PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, Bloque_.ORDEN));
    Page<Bloque> bloqueResult = bloqueService.findByFormularioId(idFormulario, pageableBloque);

    final int tamBloques = etiReportOutput.getBloques().size();

    bloqueResult.getContent().forEach(bloque -> {
      int bloqueIndex = etiReportOutput.getBloques().size() + 1;
      // @formatter:off
      BloqueOutput bloqueOutput = BloqueOutput.builder()
        .id(Long.valueOf(bloqueIndex))
        .nombre(bloque.getNombre())
        .orden(tamBloques + bloque.getOrden())
        .apartados(new ArrayList<>())
        .build();
      // @formatter:on

      // Por cada bloque obtenemos sus apartados que no tienen padre y sus respuestas
      Pageable pageableApartado = PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, Apartado_.ORDEN));
      Page<Apartado> apartadoResult = apartadoService.findByBloqueId(bloque.getId(), pageableApartado);

      apartadoResult.getContent().forEach(apartado -> {

        ApartadoOutput apartadoOutput = parseApartadoOutput(idMemoria, apartado);
        apartadoOutput.setApartadosHijos(findApartadosHijosAndRespuestas(apartado.getId(), idMemoria));

        bloqueOutput.getApartados().add(apartadoOutput);

      });

      etiReportOutput.getBloques().add(bloqueOutput);
    });
  }

  private List<ApartadoOutput> findApartadosHijosAndRespuestas(Long idPadre, Long idMemoria) {
    List<ApartadoOutput> apartadosHijosResult = new ArrayList<>();

    Pageable pageableApartado = PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, Apartado_.ORDEN));
    Page<Apartado> apartadoResult = apartadoService.findByPadreId(idPadre, pageableApartado);

    if (!apartadoResult.getContent().isEmpty()) {
      for (Apartado apartado : apartadoResult.getContent()) {
        ApartadoOutput apartadoOutput = parseApartadoOutput(idMemoria, apartado);
        List<ApartadoOutput> apartadosHijos = findApartadosHijosAndRespuestas(apartado.getId(), idMemoria);
        apartadoOutput.setApartadosHijos(apartadosHijos);
        apartadosHijosResult.add(apartadoOutput);
      }
    }
    return apartadosHijosResult;
  }

  private ApartadoOutput parseApartadoOutput(Long idMemoria, Apartado apartado) {
    RespuestaOutput respuestaOutput = findByMemoriaIdAndApartadoId(idMemoria, apartado);

    // @formatter:off
    ApartadoOutput apartadoOutput = ApartadoOutput.builder()
        .id(apartado.getId())
        .nombre(apartado.getNombre())
        .orden(apartado.getOrden())
        .esquema(apartado.getEsquema())
        .respuesta(respuestaOutput).build();
    // @formatter:on

    // Parseamos el formly para el report correspondiente
    sgiFormlyService.parseApartadoAndRespuesta(apartadoOutput);

    return apartadoOutput;
  }

  private RespuestaOutput findByMemoriaIdAndApartadoId(Long idMemoria, Apartado apartado) {
    RespuestaOutput respuestaOutput = null;

    // Obtenemos la respuesta del apartado
    Respuesta respuesta = respuestaService.findByMemoriaIdAndApartadoId(idMemoria, apartado.getId());

    if (null != respuesta) {
      // @formatter:off
      TipoDocumentoDto tipoDocumentoDto = null != respuesta.getTipoDocumento() ? 
        RespuestaOutput.TipoDocumentoDto.builder()
          .id(respuesta.getTipoDocumento().getId())
          .nombre(respuesta.getTipoDocumento().getNombre())
          .build()
        : null;
      respuestaOutput = RespuestaOutput.builder()
        .id(respuesta.getId())
        .valor(respuesta.getValor())
        .tipoDocumento(tipoDocumentoDto)
        .build();
      // @formatter:on
    }
    return respuestaOutput;
  }

  /** Estado financiacion */
  public enum EstadoFinanciacionI18n {
    /** Solicitado */
    SOLICITADO("enum.estado-financiacion.SOLICITADO"),
    /** Concedido */
    CONCEDIDO("enum.estado-financiacion.CONCEDIDO"),
    /** Denegado */
    DENEGADO("enum.estado-financiacion.DENEGADO");

    private final String i18nMessage;

    private EstadoFinanciacionI18n(String i18nMessage) {
      this.i18nMessage = i18nMessage;
    }

    public String getI18nMessage() {
      return this.i18nMessage;
    }

    public static String getI18nMessageFromValorSocialEnum(final String estadoFinanciacion) {
      String message = "";
      if (StringUtils.hasText(estadoFinanciacion)) {
        EstadoFinanciacionI18n estadoFinanciacionBusq = Stream.of(EstadoFinanciacionI18n.values())
            .filter(tvs -> estadoFinanciacion.equals(tvs.name())).findFirst().orElse(null);
        if (null != estadoFinanciacionBusq) {
          message = I18nUtil.toLocale(estadoFinanciacionBusq.i18nMessage);
        }
      }
      return message;
    }
  }
}
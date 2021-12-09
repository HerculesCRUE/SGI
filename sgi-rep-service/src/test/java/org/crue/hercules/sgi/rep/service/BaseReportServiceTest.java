package org.crue.hercules.sgi.rep.service;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;
import org.crue.hercules.sgi.rep.dto.eti.ActaDto;
import org.crue.hercules.sgi.rep.dto.eti.ActaDto.TipoEstadoActaDto;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoDto;
import org.crue.hercules.sgi.rep.dto.eti.BloqueDto;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto.Genero;
import org.crue.hercules.sgi.rep.dto.eti.ConfiguracionDto;
import org.crue.hercules.sgi.rep.dto.eti.ConvocatoriaReunionDto;
import org.crue.hercules.sgi.rep.dto.eti.DictamenDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.FormularioDto;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaDto;
import org.crue.hercules.sgi.rep.dto.eti.PeticionEvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.PeticionEvaluacionDto.EstadoFinanciacion;
import org.crue.hercules.sgi.rep.dto.eti.RespuestaDto;
import org.crue.hercules.sgi.rep.dto.eti.TipoActividadDto;
import org.crue.hercules.sgi.rep.dto.eti.TipoConvocatoriaReunionDto;
import org.crue.hercules.sgi.rep.dto.eti.TipoEvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.TipoInvestigacionTuteladaDto;
import org.crue.hercules.sgi.rep.dto.eti.TipoMemoriaDto;
import org.crue.hercules.sgi.rep.dto.sgp.DatosContactoDto;
import org.crue.hercules.sgi.rep.dto.sgp.EmailDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.dto.sgp.DatosContactoDto.ComunidadAutonomaDto;
import org.crue.hercules.sgi.rep.dto.sgp.DatosContactoDto.PaisDto;
import org.crue.hercules.sgi.rep.dto.sgp.DatosContactoDto.ProvinciaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.AreaConocimientoDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.DepartamentoDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.SexoDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.TipoDocumentoDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.VinculacionDto;

/**
 * BaseReportServiceTest
 */
abstract class BaseReportServiceTest extends BaseServiceTest {

  protected EvaluacionDto generarMockEvaluacion(Long idEvaluacion) {
    return EvaluacionDto.builder()
        .id(idEvaluacion)
        .memoria(generarMockMemoria(1L, 1L))
        .convocatoriaReunion(generarMockConvocatoriaReunion(idEvaluacion))
        .tipoEvaluacion(TipoEvaluacionDto.builder().id(1L).nombre("nombre").activo(Boolean.TRUE).build())
        .dictamen(DictamenDto.builder().id(1L).nombre("nombre").activo(Boolean.TRUE).build())
        .fechaDictamen(Instant.now())
        .version(1)
        .activo(Boolean.TRUE)
        .build();
  }

  protected ConvocatoriaReunionDto generarMockConvocatoriaReunion(Long idConvocatoriaReunion) {
    ConvocatoriaReunionDto convocatoriaReunion = ConvocatoriaReunionDto.builder()
        .id(idConvocatoriaReunion)
        .lugar("lugar")
        .comite(generarMockComite(1L, "CEI"))
        .ordenDia("ordenDia")
        .anio(2021)
        .horaInicio(10)
        .minutoInicio(0)
        .horaInicioSegunda(12)
        .minutoInicioSegunda(0)
        .tipoConvocatoriaReunion(
            TipoConvocatoriaReunionDto.builder().id(1L).nombre("nombre").activo(Boolean.TRUE).build())
        .fechaEvaluacion(Instant.now())
        .fechaLimite(Instant.now())
        .fechaEnvio(Instant.now())
        .numeroActa(1L)
        .activo(Boolean.TRUE)
        .build();
    return convocatoriaReunion;
  }

  protected ConfiguracionDto generarMockConfiguracion() {
    return ConfiguracionDto.builder()
        .diasArchivadaInactivo(3)
        .mesesArchivadaPendienteCorrecciones(6)
        .diasLimiteEvaluador(31)
        .build();
  }

  protected ComiteDto generarMockComite(Long idComite, String comite) {
    return ComiteDto.builder()
        .id(idComite)
        .comite(comite)
        .nombreDecreto("nombreDecreto")
        .nombreInvestigacion("nombreInvestigacion")
        .nombreSecretario("nombreSecretario")
        .genero(Genero.M)
        .formulario(generarMockFormulario(1L))
        .activo(Boolean.TRUE)
        .build();
  }

  protected PeticionEvaluacionDto generarMockPeticionEvaluacion(Long idPeticionEvaluacion) {
    return PeticionEvaluacionDto.builder().id(
        idPeticionEvaluacion)
        .tipoActividad(TipoActividadDto.builder().id(1L).nombre("nombreTipoActividad").build())
        .fuenteFinanciacion("fuenteFinanciacion")
        .estadoFinanciacion(EstadoFinanciacion.CONCEDIDO)
        .tipoInvestigacionTutelada(TipoInvestigacionTuteladaDto.builder().nombre("nombre").build())
        .build();
  }

  protected MemoriaDto generarMockMemoria(Long idMemoria, Long idFormulario) {
    return MemoriaDto.builder()
        .id(idMemoria)
        .numReferencia("numReferencia")
        .peticionEvaluacion(generarMockPeticionEvaluacion(1L))
        .comite(generarMockComite(1L, "CEI"))
        .titulo("titulo")
        .tipoMemoria(TipoMemoriaDto.builder().id(1L).nombre("nombre").activo(Boolean.TRUE).build())
        .fechaEnvioSecretaria(Instant.now())
        .version(1)
        .activo(Boolean.TRUE)
        .build();
  }

  protected PersonaDto generarMockPersona(String numeroDocumento) {
    return PersonaDto.builder()
        .nombre("nombre")
        .apellidos("apellidos")
        .numeroDocumento(numeroDocumento)
        .tipoDocumento(TipoDocumentoDto.builder().nombre("NIF").build())
        .sexo(SexoDto.builder().nombre("V").build())
        .emails(generarMockEmails())
        .build();
  }

  protected VinculacionDto generarMockVinculacion() {
    return VinculacionDto.builder()
        .areaConocimiento(AreaConocimientoDto.builder().nombre("Area conocimiento").build())
        .departamento(DepartamentoDto.builder().nombre("Departamento").build())
        .build();
  }

  protected DatosContactoDto generarMockDatosContacto() {
    return DatosContactoDto.builder()
        .ciudadContacto("ciudadContacto")
        .comAutonomaContacto(ComunidadAutonomaDto.builder().nombre("ASTURIAS").build())
        .provinciaContacto(ProvinciaDto.builder().nombre("ASTURIAS").build())
        .paisContacto(PaisDto.builder().nombre("ESPAÑA").build())
        .emails(generarMockEmails())
        .telefonos(Arrays.asList("234234", "34554654"))
        .build();
  }

  protected List<EmailDto> generarMockEmails() {
    List<EmailDto> emails = new ArrayList<>();
    emails.add(generarMockEmail("p@es.com", Boolean.TRUE));
    emails.add(generarMockEmail("p2@es.com", Boolean.FALSE));
    return emails;
  }

  protected EmailDto generarMockEmail(String email, Boolean principal) {
    return EmailDto.builder()
        .email(email)
        .principal(principal)
        .build();
  }

  protected ActaDto generarMockActa(Long id, Integer numero) {
    return ActaDto.builder()
        .id(id)
        .horaInicio(10)
        .horaFin(15)
        .minutoInicio(10)
        .minutoFin(10)
        .resumen("resumen" + numero)
        .numero(numero)
        .inactiva(Boolean.TRUE)
        .activo(Boolean.TRUE)
        .estadoActual(TipoEstadoActaDto.builder().id(1L).nombre("En elaboración").build())
        .convocatoriaReunion(generarMockConvocatoriaReunion(1L))
        .build();
  }

  protected FormularioDto generarMockFormulario(Long idFormulario) {
    return FormularioDto.builder()
        .id(idFormulario)
        .nombre("nombre")
        .build();
  }

  protected BloqueDto generarMockBloque(Long idBloque, Long idFormulario) {
    return BloqueDto.builder()
        .id(idBloque)
        .orden(1)
        .formulario(generarMockFormulario(idFormulario))
        .build();
  }

  protected ApartadoDto generarMockApartado(Long idApartado, Long idBloque, Long idFormulario) {
    return ApartadoDto.builder()
        .id(idApartado)
        .nombre("nombre")
        .orden(1)
        .bloque(generarMockBloque(idBloque, idFormulario))
        .build();
  }

  protected RespuestaDto generarMockRespuesta(Long idApartado) {
    return RespuestaDto.builder()
        .id(1L)
        .apartado(generarMockApartado(idApartado, 1L, 1L))
        .valor("valor")
        .build();
  }

  protected List<BloqueDto> generarMockBloques() {
    List<BloqueDto> bloques = new ArrayList<>();
    bloques.add(generarMockBloque(1L, 1L));
    bloques.add(generarMockBloque(2L, 1L));
    return bloques;
  }

  protected List<ApartadoDto> generarMockApartados(List<Long> idsApartados, Long idBloque) {
    List<ApartadoDto> apartados = new ArrayList<>();
    idsApartados.forEach(idApartado -> apartados.add(generarMockApartado(idApartado, idBloque, 1L)));
    return apartados;
  }

  protected List<ComentarioDto> generarMockComentarios() {
    List<ComentarioDto> comentarios = new ArrayList<>();
    comentarios.add(ComentarioDto.builder()
        .id(1L)
        .apartado(generarMockApartado(1L, 1L, 1L))
        .build());
    return comentarios;
  }

  protected SgiDynamicReportDto generarMockSgiDynamicReport(OutputType outputType) {
    // @formatter:off
    List<SgiDynamicReportDto.SgiFilterReportDto> filters = new ArrayList<>();
    filters.add(SgiDynamicReportDto.SgiFilterReportDto.builder().name("Filtro1").filter("Valor de filtro 1").build());
    filters.add(SgiDynamicReportDto.SgiFilterReportDto.builder().name("Filtro2").filter("Valor de filtro 2").build());

    List<SgiDynamicReportDto.SgiColumReportDto> columns = new ArrayList<>();
    columns.add(SgiDynamicReportDto.SgiColumReportDto.builder().title("Nombre").name("nombre").type(SgiDynamicReportDto.ColumnType.STRING).build());
    columns.add(SgiDynamicReportDto.SgiColumReportDto.builder().title("Nombre").name("fecha").type(SgiDynamicReportDto.ColumnType.STRING).build());

    List<SgiDynamicReportDto.SgiRowReportDto> rows = new ArrayList<>();
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre1", "12/10/2021")).build());
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre2", "22/10/2021")).build());    
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre3", "12/10/2021")).build());
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre4", "22/10/2021")).build());    
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre5", "12/10/2021")).build());
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre6", "22/10/2021")).build());

    SgiDynamicReportDto report = SgiDynamicReportDto.builder()
      .name("informeDinamico")
      .outputType(outputType)
      .title("Listado de prueba de informe dinámico")
      .filters(filters)
      .columns(columns)
      .rows(rows)
      .build();
    // @formatter:on
    return report;
  }

  protected SgiReportDto generarMockSgiReport(OutputType outputType) {
    return SgiReportDto.builder()
        .path("report/eti/mxx.prpt")
        .name("informeDinamico")
        .outputType(outputType)
        .dataModel(generarMockDataModel())
        .parameters(generarMockDataReportParameters())
        .build();
  }

  private Map<String, Object> generarMockDataReportParameters() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("1", "1");
    parameters.put("2", "2");
    return parameters;
  }

  protected Map<String, TableModel> generarMockDataModel() {
    Map<String, TableModel> dataModel = new HashMap<>();
    dataModel.put("Query1", new DefaultTableModel());
    return dataModel;
  }

  protected ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    return objectMapper;
  }

  protected SgiDynamicReportDto getSgiDynamicReportFromJson(String outputJsonPath) throws Exception {
    ObjectMapper objectMapper = getObjectMapper();
    return objectMapper.readValue(getJsonFromResources(outputJsonPath), SgiDynamicReportDto.class);
  }

  protected String getJsonFromResources(String outputJsonPath) throws Exception {
    String jsonValue = null;

    try (InputStream jsonApartadoInputStream = this.getClass().getClassLoader().getResourceAsStream(outputJsonPath)) {
      try (Scanner scanner = new Scanner(jsonApartadoInputStream, "UTF-8").useDelimiter("\\Z")) {
        jsonValue = scanner.next();
      }
    } catch (Exception e) {
      throw e;
    }
    return jsonValue;
  }

}

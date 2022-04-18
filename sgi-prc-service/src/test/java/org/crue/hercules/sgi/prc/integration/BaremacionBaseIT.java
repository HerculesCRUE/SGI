package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.controller.BaremacionController;
import org.crue.hercules.sgi.prc.dto.csp.GrupoDto;
import org.crue.hercules.sgi.prc.dto.csp.GrupoEquipoDto;
import org.crue.hercules.sgi.prc.dto.sgp.DatosContactoDto;
import org.crue.hercules.sgi.prc.dto.sgp.DatosContactoDto.ComunidadAutonomaDto;
import org.crue.hercules.sgi.prc.dto.sgp.DatosContactoDto.PaisDto;
import org.crue.hercules.sgi.prc.dto.sgp.DatosContactoDto.ProvinciaDto;
import org.crue.hercules.sgi.prc.dto.sgp.EmailDto;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto.AreaConocimientoDto;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto.DepartamentoDto;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto.SexoDto;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto.TipoDocumentoDto;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto.VinculacionDto;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EditorialPrestigio;
import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.repository.ConvocatoriaBaremacionRepository;
import org.crue.hercules.sgi.prc.repository.EditorialPrestigioRepository;
import org.crue.hercules.sgi.prc.repository.MapeoTiposRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionItemInvestigadorRepository;
import org.crue.hercules.sgi.prc.service.ConvocatoriaBaremacionService;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import lombok.Getter;

/**
 * Base IT de baremaciones
 */
public class BaremacionBaseIT extends ProduccionCientificaBaseIT {

  protected static final String PATH_PARAMETER_ID = "/{id}";
  protected static final String CONTROLLER_BASE_PATH = BaremacionController.MAPPING;

  @Autowired
  private EditorialPrestigioRepository editorialPrestigioRepository;

  @Autowired
  @Getter
  private PuntuacionBaremoItemRepository puntuacionBaremoItemRepository;

  @Autowired
  @Getter
  private PuntuacionItemInvestigadorRepository puntuacionItemInvestigadorRepository;

  @Autowired
  @Getter
  private PuntuacionGrupoInvestigadorRepository puntuacionGrupoInvestigadorRepository;

  @Autowired
  @Getter
  private PuntuacionGrupoRepository puntuacionGrupoRepository;

  @Autowired
  @Getter
  private ConvocatoriaBaremacionService convocatoriaBaremacionService;

  @Autowired
  @Getter
  private ConvocatoriaBaremacionRepository convocatoriaBaremacionRepository;

  @Autowired
  @Getter
  private MapeoTiposRepository mapeoTiposRepository;

  protected HttpEntity<Void> buildRequestBaremacion(HttpHeaders headers, Void entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "PRC-CON-BAR")));

    HttpEntity<Void> request = new HttpEntity<>(entity, headers);
    return request;
  }

  protected void baremacionWithoutPuntuaciones(Long idBaremacion) throws Exception {
    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = puntuacionBaremoItemRepository.findAll();

    Assertions.assertThat(puntuacionBaremoItems).as("numPuntuaciones").isEmpty();
  }

  protected void baremacionWithOnePuntuacion(Long baremacionId, Long baremoId, String puntos) throws Exception {
    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequestBaremacion(null, null),
        Void.class, baremacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = puntuacionBaremoItemRepository.findAll();

    int numPuntuaciones = puntuacionBaremoItems.size();
    Assertions.assertThat(numPuntuaciones).as("numPuntuaciones").isEqualTo(1);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(baremoId);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal(puntos));
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

  protected VinculacionDto generarMockVinculacion(String areaRef, String areaRefPadreId) {
    return VinculacionDto.builder()
        .areaConocimiento(generarMockAreaConocimiento(areaRef, areaRefPadreId))
        .departamento(generarMockDepartamento())
        .build();
  }

  private DepartamentoDto generarMockDepartamento() {
    return DepartamentoDto.builder()
        .id("E011")
        .nombre("DERECHO PRIVADO")
        .build();
  }

  protected AreaConocimientoDto generarMockAreaConocimiento(String id, String padreId) {
    return AreaConocimientoDto.builder()
        .id(id)
        .nombre("AREA_" + id)
        .padreId(padreId)
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

  protected void mockPersonaAndAreaConocimientoAndGrupoInvestigacion(String personaRef, String areaRef,
      String areaRefRaiz) {
    BDDMockito.given(getSgiApiSgpService().findPersonaById(personaRef))
        .willReturn((Optional.of(generarMockPersona(personaRef))));

    BDDMockito.given(getSgiApiSgpService().findVinculacionByPersonaId(personaRef))
        .willReturn((Optional.of(generarMockVinculacion(areaRef, areaRefRaiz))));

    BDDMockito.given(getSgiApiSgoService().findAllAreasConocimiento("id==" + areaRef))
        .willReturn((Arrays.asList(generarMockAreaConocimiento(areaRef, areaRefRaiz))));

    BDDMockito.given(getSgiApiSgoService().findAllAreasConocimiento("id==" + areaRefRaiz))
        .willReturn((Arrays.asList(generarMockAreaConocimiento(areaRefRaiz, null))));

    Boolean booleanReturn = Boolean.TRUE;

    BDDMockito.given(getSgiApiCspService().isGrupoBaremable(
        ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).willReturn(booleanReturn);

    BDDMockito.given(getSgiApiCspService().findAllGruposByAnio(ArgumentMatchers.anyInt()))
        .willReturn(Arrays.asList(generarMockGrupo(1L)));

    BDDMockito.given(getSgiApiCspService().isPersonaBaremable(
        ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).willReturn(booleanReturn);

    BDDMockito.given(getSgiApiCspService().findAllGruposEquipoByGrupoIdAndAnio(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyInt()))
        .willReturn(Arrays.asList(generarMockGrupoEquipo(personaRef, new BigDecimal("60.00"))));
  }

  protected GrupoEquipoDto generarMockGrupoEquipo(String personaRef, BigDecimal participacion) {
    return GrupoEquipoDto.builder()
        .personaRef(personaRef)
        .participacion(participacion)
        .build();
  }

  protected GrupoDto generarMockGrupo(Long grupoRef) {
    return GrupoDto.builder()
        .id(grupoRef)
        .nombre("nombreGrupo")
        .fechaInicio(Instant.now())
        .fechaFin(Instant.now())
        .build();
  }

  protected Autor updateAutorPersonaRef(Long autorId, String personaRef, Integer orden) {
    return getAutorRepository().findById(autorId).map(autor -> {
      autor.setPersonaRef(personaRef);
      autor.setOrden(orden);
      return getAutorRepository().save(autor);
    }).orElse(null);
  }

  protected EditorialPrestigio updateEditorialPrestigio(Long editorialId, String nombre) {
    return editorialPrestigioRepository.findById(editorialId).map(editorial -> {
      editorial.setNombre(nombre);
      return editorialPrestigioRepository.save(editorial);
    }).orElse(null);
  }

  protected ValorCampo getValorCampoByCodigoCVN(List<CampoProduccionCientifica> campos, CodigoCVN codigoCVN) {
    return campos.stream().filter(campo -> campo.getCodigoCVN().equals(codigoCVN)).findFirst()
        .map(campo -> getValorCampoRepository().findAllByCampoProduccionCientificaId(campo.getId()).get(0))
        .orElse(null);
  }

  protected List<ValorCampo> getValoresCamposByCodigoCVN(List<CampoProduccionCientifica> campos, CodigoCVN codigoCVN) {
    return campos.stream().filter(campo -> campo.getCodigoCVN().equals(codigoCVN)).findFirst()
        .map(campo -> getValorCampoRepository().findAllByCampoProduccionCientificaId(campo.getId()))
        .orElse(null);
  }

  protected void checkPuntuacionBaremoItem(List<PuntuacionBaremoItem> puntuacionBaremoItems, Long baremoId,
      BigDecimal puntuacionBaremo) {
    String assertMessage = String.format("puntuacionBaremoItem[%d]=%s", baremoId, puntuacionBaremo.toString());
    Assertions.assertThat(hasPuntuacionBaremoItem(puntuacionBaremoItems, baremoId, puntuacionBaremo))
        .as(assertMessage).isTrue();
  }

  protected boolean hasPuntuacionBaremoItem(List<PuntuacionBaremoItem> puntuacionBaremoItems, Long baremoId,
      BigDecimal puntuacionBaremo) {
    return puntuacionBaremoItems.stream().anyMatch(
        pbi -> pbi.getBaremoId().compareTo(baremoId) == 0 && pbi.getPuntos().compareTo(puntuacionBaremo) == 0);
  }

  protected void checkPuntuacionItemInvestigador(List<PuntuacionItemInvestigador> puntuaciones, String personaRef,
      BigDecimal puntuacionBaremo) {
    String assertMessage = String.format("PuntuacionItemInvestigador[%s]=%s", personaRef, puntuacionBaremo.toString());
    Assertions.assertThat(hasPuntuacionItemInvestigador(puntuaciones, personaRef, puntuacionBaremo))
        .as(assertMessage).isTrue();
  }

  protected boolean hasPuntuacionItemInvestigador(List<PuntuacionItemInvestigador> puntuaciones,
      String personaRef, BigDecimal puntuacion) {
    return puntuaciones.stream().anyMatch(
        pbi -> pbi.getPersonaRef().compareTo(personaRef) == 0 && pbi.getPuntos().compareTo(puntuacion) == 0);
  }

  protected void checkPuntuacionGrupoInvestigador(List<PuntuacionGrupoInvestigador> puntuaciones,
      BigDecimal puntuacionBaremo) {
    String assertMessage = String.format("PuntuacionGrupoInvestigador=%s", puntuacionBaremo.toString());
    Assertions.assertThat(hasPuntuacionGrupoInvestigador(puntuaciones, puntuacionBaremo))
        .as(assertMessage).isTrue();
  }

  protected boolean hasPuntuacionGrupoInvestigador(List<PuntuacionGrupoInvestigador> puntuaciones,
      BigDecimal puntuacion) {
    return puntuaciones.stream().anyMatch(pbi -> pbi.getPuntos().compareTo(puntuacion) == 0);
  }

}

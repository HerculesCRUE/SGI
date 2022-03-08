package org.crue.hercules.sgi.prc.integration;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.controller.ProduccionCientificaApiController;
import org.crue.hercules.sgi.prc.converter.ProduccionCientificaConverter;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiCreateInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiCreateInput.TipoEstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AcreditacionInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AutorInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.CampoProduccionCientificaInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.IndiceImpactoInput;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.Acreditacion;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.IndiceImpacto.TipoRanking;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.Proyecto;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.repository.AcreditacionRepository;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.EstadoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.ProyectoRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;

/**
 * Base IT de PRC
 */
public class ProduccionCientificaBaseIT extends BaseIT {

  protected static final String PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF = "/{produccionCientificaRef}";
  protected static final String CONTROLLER_BASE_PATH_API = ProduccionCientificaApiController.MAPPING;

  protected static final String PRODUCCION_CIENTIFICA_REF_VALUE = "publicacion-ref-";

  @Autowired
  @Getter
  private ProduccionCientificaRepository produccionCientificaRepository;

  @Autowired
  @Getter
  private EstadoProduccionCientificaRepository estadoProduccionCientificaRepository;

  @Autowired
  @Getter
  private CampoProduccionCientificaRepository campoProduccionCientificaRepository;

  @Autowired
  @Getter
  private ValorCampoRepository valorCampoRepository;

  @Autowired
  @Getter
  private IndiceImpactoRepository indiceImpactoRepository;

  @Autowired
  @Getter
  private AutorRepository autorRepository;

  @Autowired
  @Getter
  private AcreditacionRepository acreditacionRepository;

  @Autowired
  @Getter
  private ProyectoRepository proyectoRepository;

  @Autowired
  @Getter
  private ProduccionCientificaConverter produccionCientificaConverter;

  protected void updateValorCampoByCodigoCVNAndProduccionCientificaId(Long produccionCientificaId, CodigoCVN codigoCVN,
      String valor) {
    campoProduccionCientificaRepository
        .findByCodigoCVNAndProduccionCientificaId(codigoCVN, produccionCientificaId).map(campo -> {
          valorCampoRepository.findAllByCampoProduccionCientificaId(campo.getId()).stream().forEach(valorCampo -> {
            valorCampo.setValor(valor);
            valorCampoRepository.save(valorCampo);
          });
          return campo;

        }).orElse(null);
  }

  protected void updateEstadoProduccionCientifica(Long produccionCientificaId, TipoEstadoProduccion tipoEstado) {
    produccionCientificaRepository.findById(produccionCientificaId).map(produccionCientifica -> {
      EstadoProduccionCientifica estado = estadoProduccionCientificaRepository
          .findById(produccionCientifica.getEstado().getId()).get();
      estado.setEstado(tipoEstado);
      estadoProduccionCientificaRepository.save(estado);
      return produccionCientifica;
    }).orElse(null);
  }

  protected void updateTipoFuenteIndiceImpacto(Long produccionCientificaId, TipoFuenteImpacto tipoFuente) {
    indiceImpactoRepository.findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(tipoFuente);
      indiceImpactoRepository.save(entity);
    });
  }

  protected void checkValueByCodigoCVN(Long produccionCientificaId, CodigoCVN codigoCVN, String value) {
    CampoProduccionCientifica campoFecha = campoProduccionCientificaRepository
        .findByCodigoCVNAndProduccionCientificaId(codigoCVN, produccionCientificaId).get();

    List<ValorCampo> valorFecha = valorCampoRepository.findAllByCampoProduccionCientificaId(campoFecha.getId());
    Assertions.assertThat(valorFecha.get(0).getValor()).as("valorFormat").isEqualTo(value);
  }

  protected ProduccionCientificaApiCreateInput getProduccionCientificaApiCreateInputFromJson(String outputJsonPath) {
    ProduccionCientificaApiCreateInput input = null;

    try (InputStream jsonApartadoInputStream = this.getClass().getClassLoader().getResourceAsStream(outputJsonPath)) {
      try (Scanner scanner = new Scanner(jsonApartadoInputStream, "UTF-8").useDelimiter("\\Z")) {
        String outputJson = scanner.next();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        input = objectMapper.readValue(outputJson, ProduccionCientificaApiCreateInput.class);

      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return input;
  }

  protected ProduccionCientificaApiInput createApiInputFromProduccionCientificaById(Long produccionCientificaId) {
    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    List<CampoProduccionCientificaInput> camposInput = campos.stream().map(campo -> {
      List<String> valores = getValorCampoRepository().findAllByCampoProduccionCientificaId(campo.getId()).stream()
          .map(valorCampo -> valorCampo.getValor()).collect(Collectors.toList());
      CampoProduccionCientificaInput campoInput = CampoProduccionCientificaInput.builder()
          .codigoCVN(campo.getCodigoCVN().getInternValue())
          .valores(valores)
          .build();
      return campoInput;
    }).collect(Collectors.toList());

    List<Autor> autores = getAutorRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    List<AutorInput> autoresInput = autores.stream()
        .map(entity -> getProduccionCientificaConverter().convertAutor(entity))
        .collect(Collectors.toList());

    List<IndiceImpacto> indicesImpacto = getIndiceImpactoRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    List<IndiceImpactoInput> indicesImpactoInput = indicesImpacto.stream()
        .map(entity -> getProduccionCientificaConverter().convertIndiceImpacto(entity))
        .collect(Collectors.toList());

    List<Acreditacion> acreditaciones = getAcreditacionRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    List<AcreditacionInput> acreditacionesInput = acreditaciones.stream()
        .map(entity -> getProduccionCientificaConverter().convertAcreditacion(entity))
        .collect(Collectors.toList());

    List<Proyecto> proyectos = getProyectoRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    List<Long> proyectosInput = proyectos.stream()
        .map(entity -> entity.getProyectoRef())
        .collect(Collectors.toList());

    ProduccionCientificaApiInput produccionCientificaApiInput = ProduccionCientificaApiInput.builder()
        .campos(camposInput)
        .autores(autoresInput)
        .indicesImpacto(indicesImpactoInput)
        .acreditaciones(acreditacionesInput)
        .proyectos(proyectosInput)
        .build();
    return produccionCientificaApiInput;
  }

  protected ProduccionCientificaApiCreateInput generarMockProduccionCientificaApiInput() {
    ProduccionCientificaApiCreateInput produccionCientifica = new ProduccionCientificaApiCreateInput();
    produccionCientifica.setIdRef(PRODUCCION_CIENTIFICA_REF_VALUE + "001");
    produccionCientifica.setEpigrafeCVN(EpigrafeCVN.E060_010_010_000.getInternValue());
    produccionCientifica.setEstado(TipoEstadoProduccionCientifica.PENDIENTE);

    produccionCientifica.setCampos(new ArrayList<>());
    String codigoCVN1 = CampoProduccionCientifica.CodigoCVN.E060_010_010_010.getInternValue();
    produccionCientifica.getCampos().add(addValores(codigoCVN1));
    String codigoCVN2 = CampoProduccionCientifica.CodigoCVN.E060_010_010_030.getInternValue();
    produccionCientifica.getCampos().add(addValores(codigoCVN2));
    String codigoCVN3 = CampoProduccionCientifica.CodigoCVN.E060_010_010_090.getInternValue();
    produccionCientifica.getCampos().add(addValores(codigoCVN3));

    produccionCientifica.setAutores(new ArrayList<>());
    AutorInput autor = new AutorInput();
    autor.setFirma("firma");
    autor.setOrcidId("orcidId");
    autor.setOrden(1);
    autor.setIp(Boolean.FALSE);
    produccionCientifica.getAutores().add(autor);
    autor = new AutorInput();
    autor.setPersonaRef("personaRef");
    autor.setOrcidId("orcidId");
    autor.setOrden(1);
    autor.setIp(Boolean.FALSE);
    produccionCientifica.getAutores().add(autor);
    autor = new AutorInput();
    autor.setNombre("nombre");
    autor.setApellidos("apellidos");
    autor.setOrcidId("orcidId");
    autor.setOrden(1);
    autor.setIp(Boolean.FALSE);
    produccionCientifica.getAutores().add(autor);

    produccionCientifica.setIndicesImpacto(new ArrayList<>());
    IndiceImpactoInput indiceImpacto = new IndiceImpactoInput();
    indiceImpacto.setRanking(TipoRanking.CLASE1);
    indiceImpacto.setFuenteImpacto(TipoFuenteImpacto.BCI.getInternValue());
    indiceImpacto.setAnio(2022);
    indiceImpacto.setNumeroRevistas(BigDecimal.ZERO);
    indiceImpacto.setPosicionPublicacion(new BigDecimal(1));
    indiceImpacto.setOtraFuenteImpacto("otraFuenteImpacto");
    indiceImpacto.setRevista25(Boolean.TRUE);
    produccionCientifica.getIndicesImpacto().add(indiceImpacto);
    indiceImpacto = new IndiceImpactoInput();
    indiceImpacto.setRanking(TipoRanking.CLASE2);
    indiceImpacto.setFuenteImpacto(TipoFuenteImpacto.CORE.getInternValue());
    indiceImpacto.setAnio(2022);
    indiceImpacto.setNumeroRevistas(BigDecimal.ZERO);
    indiceImpacto.setPosicionPublicacion(new BigDecimal(3));
    indiceImpacto.setOtraFuenteImpacto("otraFuenteImpacto2");
    indiceImpacto.setRevista25(Boolean.TRUE);
    produccionCientifica.getIndicesImpacto().add(indiceImpacto);
    indiceImpacto = new IndiceImpactoInput();
    indiceImpacto.setRanking(TipoRanking.CLASE3);
    indiceImpacto.setFuenteImpacto(TipoFuenteImpacto.DIALNET.getInternValue());
    indiceImpacto.setAnio(2022);
    indiceImpacto.setNumeroRevistas(BigDecimal.ZERO);
    indiceImpacto.setPosicionPublicacion(new BigDecimal(2));
    indiceImpacto.setOtraFuenteImpacto("otraFuenteImpacto3");
    indiceImpacto.setRevista25(Boolean.FALSE);
    produccionCientifica.getIndicesImpacto().add(indiceImpacto);

    produccionCientifica.setAcreditaciones(new ArrayList<>());
    produccionCientifica.getAcreditaciones().add(AcreditacionInput.builder().url("url").build());
    produccionCientifica.getAcreditaciones().add(AcreditacionInput.builder().documentoRef("documentoRef").build());
    produccionCientifica.getAcreditaciones().add(AcreditacionInput.builder().documentoRef("documentoRef2").build());

    produccionCientifica.setProyectos(new ArrayList<>());
    produccionCientifica.getProyectos().add(1L);
    produccionCientifica.getProyectos().add(2L);
    produccionCientifica.getProyectos().add(3L);

    return produccionCientifica;
  }

  protected CampoProduccionCientificaInput addValores(String codigoCVN1) {
    CampoProduccionCientificaInput campo = new CampoProduccionCientificaInput();
    campo.setCodigoCVN(codigoCVN1);
    campo.setValores(Stream.of(codigoCVN1 + "_0", codigoCVN1 + "_1").collect(Collectors.toList()));
    return campo;
  }

}

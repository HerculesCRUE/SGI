package org.crue.hercules.sgi.prc.service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.converter.ProduccionCientificaConverter;
import org.crue.hercules.sgi.prc.dto.EpigrafeCVNOutput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiCreateInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiFullOutput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AcreditacionInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AutorInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.CampoProduccionCientificaInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.IndiceImpactoInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiOutput;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.exceptions.ProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.model.Acreditacion;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoFuente;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo.TipoFormato;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.Proyecto;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.repository.AcreditacionRepository;
import org.crue.hercules.sgi.prc.repository.AutorGrupoRepository;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.ConfiguracionBaremoRepository;
import org.crue.hercules.sgi.prc.repository.ConfiguracionCampoRepository;
import org.crue.hercules.sgi.prc.repository.ConvocatoriaBaremacionRepository;
import org.crue.hercules.sgi.prc.repository.EstadoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.ProyectoRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.crue.hercules.sgi.prc.repository.predicate.ProduccionCientificaPredicateResolverApi;
import org.crue.hercules.sgi.prc.repository.specification.ConfiguracionBaremoSpecifications;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaFieldFormatUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar el API de {@link ProduccionCientifica} procedente
 * del CVN.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ProduccionCientificaApiService {

  private final Validator validator;

  private final ProduccionCientificaRepository produccionCientificaRepository;
  private final EstadoProduccionCientificaRepository estadoProduccionCientificaRepository;
  private final CampoProduccionCientificaRepository campoProduccionCientificaRepository;
  private final ValorCampoRepository valorCampoRepository;
  private final AutorRepository autorRepository;
  private final AutorGrupoRepository autorGrupoRepository;
  private final AcreditacionRepository acreditacionRepository;
  private final IndiceImpactoRepository indiceImpactoRepository;
  private final ProyectoRepository proyectoRepository;
  private final ConfiguracionCampoRepository configuracionCampoRepository;
  private final ConfiguracionBaremoRepository configuracionBaremoRepository;
  private final ConvocatoriaBaremacionRepository convocatoriaBaremacionRepository;
  private final BaremoRepository baremoRepository;
  private final SgiApiCspService sgiApiCspService;
  private final ProduccionCientificaConverter produccionCientificaConverter;
  private final SgiConfigProperties sgiConfigProperties;
  private final ComunicadosService comunicadosService;

  /**
   * Guardar un nuevo {@link ProduccionCientifica} y sus entidades relacionadas
   *
   * @param produccionCientificaApiInput la entidad
   *                                     {@link ProduccionCientificaApiCreateInput}
   *                                     a guardar.
   * @return la entidad {@link ProduccionCientificaApiFullOutput} persistida con
   *         sus entidades relacionadas.
   */
  @Transactional
  public ProduccionCientificaApiFullOutput create(ProduccionCientificaApiCreateInput produccionCientificaApiInput) {

    log.debug("create(ProduccionCientificaApiCreateInput produccionCientificaApiInput) - start");

    ProduccionCientifica produccionCientifica = ProduccionCientifica.builder()
        .epigrafeCVN(EpigrafeCVN.getByCode(produccionCientificaApiInput.getEpigrafeCVN()))
        .produccionCientificaRef(produccionCientificaApiInput.getIdRef())
        .build();

    validateProduccionCientifica(produccionCientifica, BaseEntity.Create.class);

    ProduccionCientifica produccionCientificaCreate = produccionCientificaRepository.save(produccionCientifica);

    EstadoProduccionCientifica estadoProduccionCientifica = addEstadoProduccionCientificaFromCreate(
        produccionCientificaCreate.getId(),
        produccionCientificaApiInput);

    produccionCientificaCreate.setEstado(estadoProduccionCientifica);

    validateProduccionCientifica(produccionCientifica, BaseEntity.Update.class);
    ProduccionCientifica produccionCientificaUpdate = produccionCientificaRepository.save(produccionCientificaCreate);

    List<CampoProduccionCientificaInput> campos = addCamposProduccionCientifica(produccionCientificaCreate.getId(),
        produccionCientificaApiInput.getCampos());
    List<AutorInput> autores = addAutores(produccionCientificaCreate, produccionCientificaApiInput.getAutores());
    List<IndiceImpactoInput> indicesImpacto = addIndicesImpacto(produccionCientificaCreate.getId(),
        produccionCientificaApiInput.getIndicesImpacto());
    List<AcreditacionInput> acreditaciones = addAcreditaciones(produccionCientificaCreate.getId(),
        produccionCientificaApiInput.getAcreditaciones());
    List<Long> proyectos = addProyectos(produccionCientificaCreate.getId(),
        produccionCientificaApiInput.getProyectos());

    ProduccionCientificaApiFullOutput output = produccionCientificaConverter.convert(produccionCientificaUpdate);
    output.setEstado(produccionCientifica.getEstado().getEstado());
    output.setCampos(campos);
    output.setAutores(autores);
    output.setIndicesImpacto(indicesImpacto);
    output.setAcreditaciones(acreditaciones);
    output.setProyectos(proyectos);

    if (!estadoProduccionCientifica.getEstado().equals(TipoEstadoProduccion.VALIDADO)) {
      enviarComunicadoValidacionItem(produccionCientifica.getEpigrafeCVN(), campos, autores);
    }

    log.debug("create(ProduccionCientificaApiInput produccionCientificaApiInput) - end");
    return output;
  }

  private void enviarComunicadoValidacionItem(EpigrafeCVN epigrafeCVN, List<CampoProduccionCientificaInput> campos,
      List<AutorInput> autores) {
    log.debug(
        "enviarComunicadoValidacionItem(EpigrafeCVN epigrafeCVN, List<CampoProduccionCientificaInput> campos) - start");

    String titulo = null;
    Instant fecha = null;
    switch (epigrafeCVN) {
      case E060_010_010_000:
        titulo = getFirstValorCampo(campos, CodigoCVN.E060_010_010_030);
        fecha = getFirstValorCampoAsInstant(campos, CodigoCVN.E060_010_010_140);
        break;
      case E060_010_020_000:
        titulo = getFirstValorCampo(campos, CodigoCVN.E060_010_020_030);
        fecha = getFirstValorCampoAsInstant(campos, CodigoCVN.E060_010_020_190);
        break;
      case E050_020_030_000:
        titulo = getFirstValorCampo(campos, CodigoCVN.E050_020_030_020);
        fecha = getFirstValorCampoAsInstant(campos, CodigoCVN.E050_020_030_120);
        break;
      case E060_030_030_000:
        titulo = getFirstValorCampo(campos, CodigoCVN.E060_030_030_010);
        fecha = getFirstValorCampoAsInstant(campos, CodigoCVN.E060_030_030_140);
        break;
      case E030_040_000_000:
        titulo = getFirstValorCampo(campos, CodigoCVN.E030_040_000_030);
        fecha = getFirstValorCampoAsInstant(campos, CodigoCVN.E030_040_000_140);
        break;
      case E060_020_030_000:
        titulo = getFirstValorCampo(campos, CodigoCVN.E060_020_030_010);
        fecha = getFirstValorCampoAsInstant(campos, CodigoCVN.E060_020_030_160);
        break;
      default:
        titulo = "";
        fecha = null;
        break;
    }

    List<String> personaRefs = autores.stream().map(AutorInput::getPersonaRef).filter(StringUtils::hasText).distinct()
        .collect(Collectors.toList());
    comunicadosService.enviarComunicadoValidarItem(epigrafeCVN.getDescription(), titulo, fecha, personaRefs);

    log.debug(
        "enviarComunicadoValidacionItem(EpigrafeCVN epigrafeCVN, List<CampoProduccionCientificaInput> campos) - end");
  }

  private String getFirstValorCampo(List<CampoProduccionCientificaInput> campos, CodigoCVN codigoCVN) {
    return campos.stream()
        .filter(c -> c.getCodigoCVN().equals(codigoCVN.getCode())).map(c -> c.getValores()
            .get(0))
        .findFirst().orElse("");
  }

  private Instant getFirstValorCampoAsInstant(List<CampoProduccionCientificaInput> campos, CodigoCVN codigoCVN) {
    String valor = getFirstValorCampo(campos, codigoCVN);
    if (!StringUtils.hasText(valor)) {
      return null;
    }

    if (valor.matches("^(\\d{4})-(\\d{2})-(\\d{2})$")) {
      valor = valor + "T00:00:00Z";
    }

    return Instant.parse(valor);
  }

  private void validateProduccionCientifica(ProduccionCientifica produccionCientifica, Class<?> classGroup) {
    Set<ConstraintViolation<ProduccionCientifica>> result = validator.validate(produccionCientifica, classGroup);
    if (!result.isEmpty()) {
      throw new ConstraintViolationException(result);
    }
  }

  /**
   * Actualiza un {@link ProduccionCientifica} y sus entidades relacionadas
   *
   * @param produccionCientificaApiInput la entidad {@link ProduccionCientifica} a
   *                                     modificar.
   * @param produccionCientificaRef      produccionCientificaRef
   * @return la entidad {@link ProduccionCientifica} persistida con sus entidades
   *         relacionadas.
   */
  @Transactional
  public ProduccionCientificaApiFullOutput update(ProduccionCientificaApiInput produccionCientificaApiInput,
      String produccionCientificaRef) {

    log.debug("update(produccionCientificaApiInput, produccionCientificaRef) - start");

    ProduccionCientifica produccionCientificaUpdate = produccionCientificaRepository
        .findByProduccionCientificaRefAndConvocatoriaBaremacionIdIsNull(produccionCientificaRef)
        .orElseThrow(() -> new ProduccionCientificaNotFoundException(produccionCientificaRef));

    validateProduccionCientificaUpdate(produccionCientificaUpdate);

    Long produccionCientificaId = produccionCientificaUpdate.getId();
    boolean isEstadoActualizado = updateEstadoToPendiente(produccionCientificaApiInput, produccionCientificaUpdate);

    List<CampoProduccionCientificaInput> campos = updateCampos(produccionCientificaApiInput.getCampos(),
        produccionCientificaId);

    List<AutorInput> autores = new ArrayList<>();
    if (produccionCientificaUpdate.getEstado().getEstado().equals(TipoEstadoProduccion.PENDIENTE)) {
      autores = updateAutores(produccionCientificaApiInput.getAutores(), produccionCientificaUpdate);
    }

    List<IndiceImpactoInput> indicesImpacto = updateIndicesImpacto(produccionCientificaApiInput.getIndicesImpacto(),
        produccionCientificaId);

    List<AcreditacionInput> acreditaciones = updateAcreditaciones(produccionCientificaApiInput.getAcreditaciones(),
        produccionCientificaId);

    List<Long> proyectos = updateProyectos(produccionCientificaApiInput.getProyectos(),
        produccionCientificaId);

    ProduccionCientificaApiFullOutput output = produccionCientificaConverter.convert(produccionCientificaUpdate);
    output.setEstado(produccionCientificaUpdate.getEstado().getEstado());
    output.setCampos(campos);
    output.setAutores(autores);
    output.setIndicesImpacto(indicesImpacto);
    output.setAcreditaciones(acreditaciones);
    output.setProyectos(proyectos);

    if (isEstadoActualizado) {
      List<CampoProduccionCientificaInput> camposComnunicado = produccionCientificaApiInput.getCampos().stream()
          .map(campo -> {
            ConfiguracionCampo configuracionCampo = configuracionCampoRepository
                .findByCodigoCVN(CodigoCVN.getByCode(campo.getCodigoCVN())).orElse(null);
            TipoFormato tipoFormato = null != configuracionCampo ? configuracionCampo.getTipoFormato() : null;
            campo.setValores(campo.getValores().stream().map(valor -> formatValorByTipoFormato(valor, tipoFormato))
                .collect(Collectors.toList()));
            return campo;
          }).collect(Collectors.toList());

      enviarComunicadoValidacionItem(produccionCientificaUpdate.getEpigrafeCVN(),
          camposComnunicado, produccionCientificaApiInput.getAutores());
    }

    log.debug("update(produccionCientificaApiInput, produccionCientificaRef) - end");
    return output;
  }

  private void validateProduccionCientificaUpdate(ProduccionCientifica produccionCientificaUpdate) {
    Assert.isTrue(
        (produccionCientificaUpdate.getEstado().getEstado().equals(TipoEstadoProduccion.VALIDADO) ||
            produccionCientificaUpdate.getEstado().getEstado().equals(TipoEstadoProduccion.RECHAZADO)),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder()
            .key("org.crue.hercules.sgi.prc.exceptions.EstadoNotValidProduccionCientificaException.message").build());
  }

  /**
   * Actualiza el estado del item a pendiente si cambia el valor de uno de los
   * campos que necesitan validación adicional, el listado de autores o si estado
   * actual del item es RECHAZADO
   * 
   * @param produccionCientificaApiInput item actualizado
   * @param produccionCientificaUpdate   item actual
   * @return <code>true</code> si se actualizo el estado, <code>false</code> en
   *         otro caso
   */
  private boolean updateEstadoToPendiente(ProduccionCientificaApiInput produccionCientificaApiInput,
      ProduccionCientifica produccionCientificaUpdate) {
    Long produccionCientificaId = produccionCientificaUpdate.getId();

    boolean addEstadoPendiente = produccionCientificaUpdate.getEstado().getEstado()
        .equals(TipoEstadoProduccion.RECHAZADO);

    if (produccionCientificaUpdate.getEstado().getEstado().equals(TipoEstadoProduccion.VALIDADO)) {
      addEstadoPendiente = checkCamposValidacionAdicional(produccionCientificaApiInput, produccionCientificaId)
          || checkAutoresValidacionAdicional(produccionCientificaApiInput, produccionCientificaId);
    }

    if (addEstadoPendiente) {
      TipoEstadoProduccion tipoEstado = EstadoProduccionCientifica.TipoEstadoProduccion.PENDIENTE;
      EstadoProduccionCientifica estadoProduccionCientificaNew = addEstado(produccionCientificaId, tipoEstado);
      produccionCientificaUpdate.setEstado(estadoProduccionCientificaNew);
      produccionCientificaRepository.save(produccionCientificaUpdate);
    }

    return addEstadoPendiente;
  }

  private boolean checkCamposValidacionAdicional(ProduccionCientificaApiInput produccionCientificaApiInput,
      Long produccionCientificaId) {
    return ListUtils.emptyIfNull(produccionCientificaApiInput.getCampos()).stream()
        .anyMatch(campoInput -> campoProduccionCientificaRepository
            .findByCodigoCVNAndProduccionCientificaId(
                CodigoCVN.getByCode(campoInput.getCodigoCVN()), produccionCientificaId)
            .map(campoSearch -> hasValidacionAdicional(campoSearch.getCodigoCVN())
                && !valoresCampoEqualsValoresCampoInput(campoSearch, campoInput))
            .orElse(false));
  }

  private boolean valoresCampoEqualsValoresCampoInput(CampoProduccionCientifica campo,
      CampoProduccionCientificaInput campoInput) {

    List<ValorCampo> valores = valorCampoRepository.findAllByCampoProduccionCientificaId(campo.getId());

    if (valores.size() != campoInput.getValores().size()) {
      return false;
    }

    ConfiguracionCampo configuracionCampo = configuracionCampoRepository.findByCodigoCVN(campo.getCodigoCVN())
        .orElse(null);
    TipoFormato tipoFormato = null != configuracionCampo ? configuracionCampo.getTipoFormato() : null;

    for (int i = 0; i < valores.size(); i++) {
      ValorCampo valorCampo = valores.get(i);
      String valorString = campoInput.getValores().get(i);
      if (!valorCampoEqualsValorInput(valorCampo, valorString, tipoFormato)) {
        return false;
      }
    }
    return true;
  }

  private boolean checkAutoresValidacionAdicional(ProduccionCientificaApiInput produccionCientificaApiInput,
      Long produccionCientificaId) {
    return !ListUtils.emptyIfNull(produccionCientificaApiInput.getAutores()).stream()
        .filter(autorInput -> autorRepository.findAllByProduccionCientificaId(produccionCientificaId).stream()
            .noneMatch(autor -> autorEqualsAutorInput(autor, autorInput)))
        .collect(Collectors.toList()).isEmpty();
  }

  private boolean autorEqualsAutorInput(Autor autor, AutorInput autorInput) {
    return Objects.equals(ObjectUtils.defaultIfNull(autor.getFirma(), ""), autorInput.getFirma()) &&
        Objects.equals(ObjectUtils.defaultIfNull(autor.getPersonaRef(), ""), autorInput.getPersonaRef()) &&
        Objects.equals(ObjectUtils.defaultIfNull(autor.getNombre(), ""), autorInput.getNombre()) &&
        Objects.equals(ObjectUtils.defaultIfNull(autor.getApellidos(), ""), autorInput.getApellidos());
  }

  private boolean valorCampoEqualsValorInput(ValorCampo valorCampo, String valorCampoUpdate, TipoFormato tipoFormato) {
    String valorCampoUpdateFormated = formatValorByTipoFormato(valorCampoUpdate, tipoFormato);
    return Objects.equals(valorCampoUpdateFormated, valorCampo.getValor());
  }

  private String formatValorByTipoFormato(final String valor, TipoFormato tipoFormato) {
    if (null != tipoFormato) {
      switch (tipoFormato) {
        case FECHA:
          return ProduccionCientificaFieldFormatUtil.formatDate(valor, sgiConfigProperties.getTimeZone());
        case NUMERO:
          return ProduccionCientificaFieldFormatUtil.formatNumber(valor);
        default:
          return valor;
      }
    }
    return valor;
  }

  private boolean hasValidacionAdicional(CodigoCVN codigoCVN) {
    return configuracionCampoRepository.findByCodigoCVN(codigoCVN)
        .map(ConfiguracionCampo::getValidacionAdicional)
        .orElse(false);
  }

  private EstadoProduccionCientifica addEstado(Long produccionCientificaId, TipoEstadoProduccion tipoEstado) {
    EstadoProduccionCientifica estadoProduccionCientifica = EstadoProduccionCientifica.builder()
        .produccionCientificaId(produccionCientificaId)
        .estado(tipoEstado)
        .build();
    estadoProduccionCientifica.setFecha(Instant.now());
    estadoProduccionCientifica = estadoProduccionCientificaRepository.save(estadoProduccionCientifica);
    return estadoProduccionCientifica;
  }

  private List<CampoProduccionCientificaInput> updateCampos(List<CampoProduccionCientificaInput> campos,
      Long produccionCientificaId) {
    List<CampoProduccionCientificaInput> camposInputUpdate = new ArrayList<>();
    List<CampoProduccionCientificaInput> camposInputAdd = new ArrayList<>();

    ListUtils.emptyIfNull(campos).stream().forEach(campoInput -> {
      CampoProduccionCientifica campo = campoProduccionCientificaRepository
          .findByCodigoCVNAndProduccionCientificaId(
              CodigoCVN.getByCode(campoInput.getCodigoCVN()), produccionCientificaId)
          .orElse(null);

      if (null != campo) {
        if (!valoresCampoEqualsValoresCampoInput(campo, campoInput)) {
          camposInputUpdate.add(updateCampo(campo, campoInput));
        }
      } else {
        camposInputAdd.add(campoInput);
      }
    });

    List<CampoProduccionCientificaInput> camposSave = addCamposProduccionCientifica(produccionCientificaId,
        camposInputAdd);
    return Stream.concat(camposInputUpdate.stream(), camposSave.stream()).collect(Collectors.toList());
  }

  private CampoProduccionCientificaInput updateCampo(CampoProduccionCientifica campo,
      CampoProduccionCientificaInput campoInput) {

    CampoProduccionCientificaInput campoUpdate = new CampoProduccionCientificaInput();
    valorCampoRepository.deleteInBulkByCampoProduccionCientificaId(campo.getId());
    List<String> valoresSave = addValoresCampos(campo, campoInput.getValores());

    campoUpdate.setCodigoCVN(campo.getCodigoCVN().getCode());
    campoUpdate.setValores(valoresSave);
    return campoUpdate;
  }

  private void deleteCamposAndValores(Long produccionCientificaId) {
    List<CampoProduccionCientifica> entitiesToDelete = campoProduccionCientificaRepository
        .findAllByProduccionCientificaId(produccionCientificaId);

    entitiesToDelete.stream()
        .forEach(entity -> valorCampoRepository.deleteInBulkByCampoProduccionCientificaId(entity.getId()));

    campoProduccionCientificaRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
  }

  private List<AutorInput> updateAutores(List<AutorInput> autores, ProduccionCientifica produccionCientificaUpdate) {
    deleteAutoresAndGrupos(produccionCientificaUpdate.getId());

    return addAutores(produccionCientificaUpdate, autores);
  }

  private void deleteAutoresAndGrupos(Long produccionCientificaId) {
    List<Autor> entitiesToDelete = autorRepository
        .findAllByProduccionCientificaId(produccionCientificaId);

    entitiesToDelete.stream()
        .forEach(entity -> autorGrupoRepository.deleteInBulkByAutorId(entity.getId()));

    autorRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
  }

  private List<AcreditacionInput> updateAcreditaciones(List<AcreditacionInput> acreditaciones,
      Long produccionCientificaId) {
    acreditacionRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);

    return addAcreditaciones(produccionCientificaId, acreditaciones);
  }

  private List<IndiceImpactoInput> updateIndicesImpacto(List<IndiceImpactoInput> indicesImpacto,
      Long produccionCientificaId) {
    indiceImpactoRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);

    return addIndicesImpacto(produccionCientificaId, indicesImpacto);
  }

  private List<Long> updateProyectos(List<Long> proyectos, Long produccionCientificaId) {
    proyectoRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);

    return addProyectos(produccionCientificaId, proyectos);
  }

  private EstadoProduccionCientifica addEstadoProduccionCientificaFromCreate(Long idProduccionCientifica,
      ProduccionCientificaApiCreateInput produccionCientificaApiInput) {

    TipoEstadoProduccion tipoEstado = null != produccionCientificaApiInput.getEstado()
        && produccionCientificaApiInput.getEstado().equals(TipoEstadoProduccion.VALIDADO)
            ? EstadoProduccionCientifica.TipoEstadoProduccion.VALIDADO
            : EstadoProduccionCientifica.TipoEstadoProduccion.PENDIENTE;

    return addEstado(idProduccionCientifica, tipoEstado);
  }

  private List<CampoProduccionCientificaInput> addCamposProduccionCientifica(Long idProduccionCientifica,
      List<CampoProduccionCientificaInput> camposInput) {
    List<CampoProduccionCientificaInput> campos = new ArrayList<>();

    ListUtils.emptyIfNull(camposInput).stream().forEach(campo -> {
      CampoProduccionCientifica campoProduccionCientifica = CampoProduccionCientifica.builder()
          .produccionCientificaId(idProduccionCientifica)
          .codigoCVN(CodigoCVN.getByCode(campo.getCodigoCVN()))
          .build();

      campoProduccionCientifica = campoProduccionCientificaRepository.save(campoProduccionCientifica);

      List<String> valores = addValoresCampos(campoProduccionCientifica, campo.getValores());

      CampoProduccionCientificaInput campoInput = new CampoProduccionCientificaInput();
      campoInput.setCodigoCVN(campoProduccionCientifica.getCodigoCVN().getCode());
      campoInput.setValores(valores);
      campos.add(campoInput);
    });

    return campos;
  }

  private List<String> addValoresCampos(CampoProduccionCientifica campo, List<String> valores) {
    List<String> valoresCamposSave = new ArrayList<>();

    ConfiguracionCampo configuracionCampo = configuracionCampoRepository.findByCodigoCVN(campo.getCodigoCVN())
        .orElse(null);
    TipoFormato tipoFormato = null != configuracionCampo ? configuracionCampo.getTipoFormato() : null;

    AtomicInteger fieldIndex = new AtomicInteger();
    ListUtils.emptyIfNull(valores).stream().forEach(valor -> {
      ValorCampo valorCampoNew = ValorCampo.builder()
          .campoProduccionCientificaId(campo.getId())
          .orden(fieldIndex.getAndIncrement() + 1)
          .valor(formatValorByTipoFormato(valor, tipoFormato))
          .build();

      Set<ConstraintViolation<ValorCampo>> result = validator.validate(valorCampoNew, BaseEntity.Create.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      valorCampoNew = valorCampoRepository.save(valorCampoNew);
      valoresCamposSave.add(valorCampoNew.getValor());
    });

    return valoresCamposSave;
  }

  private List<ValorCampo> findValoresByCampoProduccionCientificaId(CodigoCVN codigoCVN,
      Long produccionCientificaId) {
    return campoProduccionCientificaRepository
        .findByCodigoCVNAndProduccionCientificaId(codigoCVN, produccionCientificaId)
        .map(campo -> valorCampoRepository.findAllByCampoProduccionCientificaId(campo.getId()))
        .orElse(new ArrayList<>());
  }

  private List<AutorInput> addAutores(ProduccionCientifica produccionCientifica, List<AutorInput> autores) {
    List<AutorInput> autoresSave = new ArrayList<>();

    ListUtils.emptyIfNull(autores).stream().forEach(autor -> {
      String personaRef = autor.getPersonaRef();

      Autor autorNew = Autor.builder()
          .produccionCientificaId(produccionCientifica.getId())
          .firma(autor.getFirma())
          .personaRef(personaRef)
          .nombre(autor.getNombre())
          .apellidos(autor.getApellidos())
          .orcidId(autor.getOrcidId())
          .orden(autor.getOrden())
          .ip(Objects.isNull(autor.getIp()) ? Boolean.FALSE : autor.getIp())
          .build();

      Set<ConstraintViolation<Autor>> result = validator.validate(autorNew, BaseEntity.Create.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      autorNew = autorRepository.save(autorNew);
      autoresSave.add(produccionCientificaConverter.convertAutor(autorNew));
      Long autorId = autorNew.getId();

      saveGruposAutorBaremable(produccionCientifica, personaRef, autorId);
    });

    return autoresSave;
  }

  private void saveGruposAutorBaremable(ProduccionCientifica produccionCientifica, String personaRef, Long autorId) {
    if (StringUtils.hasText(personaRef)) {

      Integer anio = getAnioIsAutorBaremable(produccionCientifica);

      if (null != anio) {
        sgiApiCspService.findGrupoEquipoByPersonaRefAndFechaBaremacion(personaRef, anio).stream().forEach(
            grupoId -> saveAutorGrupo(autorId, grupoId, produccionCientifica.getEstado().getEstado()));
      }
    }
  }

  private AutorGrupo saveAutorGrupo(Long autorId, Long grupoRef, TipoEstadoProduccion tipoEstado) {
    AutorGrupo autorGrupoNew = AutorGrupo.builder()
        .autorId(autorId)
        .grupoRef(grupoRef)
        .estado(tipoEstado)
        .build();
    return autorGrupoRepository.save(autorGrupoNew);
  }

  private Integer getAnioIsAutorBaremable(ProduccionCientifica produccionCientifica) {
    return configuracionCampoRepository
        .findByEpigrafeCVNAndFechaReferenciaInicioIsTrue(produccionCientifica.getEpigrafeCVN())
        .map(configuracionCampo -> {
          Integer anioCampo = null;
          List<ValorCampo> valores = findValoresByCampoProduccionCientificaId(configuracionCampo.getCodigoCVN(),
              produccionCientifica.getId());
          if (!CollectionUtils.isEmpty(valores)) {

            TimeZone timeZone = sgiConfigProperties.getTimeZone();

            ZonedDateTime fechaInicio = Instant.parse(valores.get(0).getValor()).atZone(timeZone.toZoneId());
            ZonedDateTime fechaActual = Instant.now().atZone(timeZone.toZoneId());

            if (fechaInicio.compareTo(fechaActual) < 0) {
              anioCampo = fechaInicio.getYear();
            } else {
              anioCampo = fechaActual.getYear();
            }
          }
          return anioCampo;
        }).orElse(null);
  }

  private List<IndiceImpactoInput> addIndicesImpacto(Long idProduccionCientifica,
      List<IndiceImpactoInput> indicesImpacto) {
    List<IndiceImpactoInput> indicesImpactoSave = new ArrayList<>();

    ListUtils.emptyIfNull(indicesImpacto).stream().forEach(indiceImpacto -> {
      IndiceImpacto indiceImpactoNew = IndiceImpacto.builder()
          .produccionCientificaId(idProduccionCientifica)
          .posicionPublicacion(indiceImpacto.getPosicionPublicacion())
          .indice(indiceImpacto.getIndice())
          .fuenteImpacto(TipoFuenteImpacto.getByCode(indiceImpacto.getFuenteImpacto()))
          .ranking(indiceImpacto.getRanking())
          .anio(indiceImpacto.getAnio())
          .otraFuenteImpacto(indiceImpacto.getOtraFuenteImpacto())
          .numeroRevistas(indiceImpacto.getNumeroRevistas())
          .revista25(
              Objects.isNull(indiceImpacto.getRevista25()) ? Boolean.FALSE : indiceImpacto.getRevista25())
          .build();

      Set<ConstraintViolation<IndiceImpacto>> result = validator.validate(indiceImpactoNew, BaseEntity.Create.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      indiceImpactoNew = indiceImpactoRepository.save(indiceImpactoNew);
      indicesImpactoSave.add(produccionCientificaConverter.convertIndiceImpacto(indiceImpactoNew));
    });

    return indicesImpactoSave;
  }

  private List<AcreditacionInput> addAcreditaciones(Long idProduccionCientifica,
      List<AcreditacionInput> acreditaciones) {
    List<AcreditacionInput> acreditacionesSave = new ArrayList<>();

    ListUtils.emptyIfNull(acreditaciones).stream().forEach(acreditacion -> {
      Acreditacion acreditacionNew = Acreditacion.builder()
          .produccionCientificaId(idProduccionCientifica)
          .documentoRef(acreditacion.getDocumentoRef())
          .url(acreditacion.getUrl())
          .build();

      Set<ConstraintViolation<Acreditacion>> result = validator.validate(acreditacionNew, BaseEntity.Create.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      acreditacionNew = acreditacionRepository.save(acreditacionNew);
      acreditacionesSave.add(produccionCientificaConverter.convertAcreditacion(acreditacionNew));
    });

    return acreditacionesSave;
  }

  private List<Long> addProyectos(Long idProduccionCientifica, List<Long> proyectos) {
    ListUtils.emptyIfNull(proyectos).stream().forEach(proyecto -> {

      Proyecto proyectoNew = Proyecto.builder()
          .produccionCientificaId(idProduccionCientifica)
          .proyectoRef(proyecto)
          .build();

      Set<ConstraintViolation<Proyecto>> result = validator.validate(proyectoNew, BaseEntity.Create.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      proyectoNew = proyectoRepository.save(proyectoNew);
    });

    return proyectos;
  }

  @Transactional
  public void delete(String produccionCientificaRef) {
    ProduccionCientifica produccionCientifica = produccionCientificaRepository
        .findByProduccionCientificaRefAndConvocatoriaBaremacionIdIsNull(produccionCientificaRef)
        .orElseThrow(() -> new ProduccionCientificaNotFoundException(produccionCientificaRef));

    validationDelete(produccionCientifica);

    Long produccionCientificaId = produccionCientifica.getId();

    deleteCamposAndValores(produccionCientificaId);
    deleteAutoresAndGrupos(produccionCientificaId);
    acreditacionRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    indiceImpactoRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    proyectoRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    produccionCientificaRepository.updateEstadoNull(produccionCientificaId);
    estadoProduccionCientificaRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    produccionCientificaRepository.deleteById(produccionCientificaId);
  }

  private void validationDelete(ProduccionCientifica produccionCientifica) {

    Specification<ConfiguracionBaremo> specs = ConfiguracionBaremoSpecifications
        .byEpigrafeCVN(produccionCientifica.getEpigrafeCVN())
        .and(
            ConfiguracionBaremoSpecifications.tipoFuenteIn(Arrays.asList(TipoFuente.CVN, TipoFuente.CVN_OTRO_SISTEMA)));

    Assert.isTrue(
        !configuracionBaremoRepository.findAll(specs, PageRequest.of(0, 1)).isEmpty(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(
            "org.crue.hercules.sgi.prc.exceptions.EpigrafeCVNNotFromCVNProduccionCientificaDeleteException.message")
            .build());
  }

  public List<ProduccionCientificaApiOutput> findByEstadoValidadoOrRechazadoByFechaModificacion(String query) {
    return produccionCientificaConverter.convertProduccionCientificaEstadoResumen(
        produccionCientificaRepository.findByEstadoValidadoOrRechazadoByFechaModificacion(
            SgiRSQLJPASupport.toSpecification(query, ProduccionCientificaPredicateResolverApi.getInstance())));
  }

  /**
   * Listado con los códigos de los apartados del CVN que forman parte de la
   * Producción científica y que necesitan validación. Se enviarán los epígrafes
   * marcados en el SGI de la última convocatoria creada.
   * 
   * Por cada epígrafe se enviarán los campos dinámicos del CVN que se tienen que
   * enviar a PRC. Será un subconjunto de los de la Fecyt.
   *
   * @return lista de {@link EpigrafeCVNOutput}.
   */
  public List<EpigrafeCVNOutput> findListadoEpigrafes() {
    log.debug("findListadoEpigrafes - start");
    Long convocatoriaBaremacionId = convocatoriaBaremacionRepository.findIdByMaxAnio();

    List<EpigrafeCVNOutput> result = baremoRepository
        .findDistinctEpigrafesCVNByConvocatoriaBaremacionId(convocatoriaBaremacionId).stream()
        .map(epigrafeCVN -> {
          List<String> codigosCVN = configuracionCampoRepository.findByEpigrafeCVNOrderByCodigoCVN(epigrafeCVN).stream()
              .map(configuracionCampo -> configuracionCampo.getCodigoCVN().getCode()).collect(Collectors.toList());

          return EpigrafeCVNOutput.builder().epigrafeCVN(epigrafeCVN.getCode()).codigosCVN(codigosCVN).build();
        }).collect(Collectors.toList());

    log.debug("findListadoEpigrafes - end");

    return result;

  }

}

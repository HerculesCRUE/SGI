package org.crue.hercules.sgi.prc.service;

import java.time.Instant;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.Acreditacion;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.Proyecto;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.repository.AcreditacionRepository;
import org.crue.hercules.sgi.prc.repository.AutorGrupoRepository;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.EstadoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.ProyectoRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.PuntuacionItemInvestigadorRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la clonación o eliminación de una {@link ProduccionCientifica}
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class ProduccionCientificaBuilderService {

  private final ProduccionCientificaRepository produccionCientificaRepository;
  private final EstadoProduccionCientificaRepository estadoProduccionCientificaRepository;
  private final PuntuacionGrupoInvestigadorRepository puntuacionGrupoInvestigadorRepository;
  private final PuntuacionBaremoItemRepository puntuacionBaremoItemRepository;
  private final PuntuacionItemInvestigadorRepository puntuacionItemInvestigadorRepository;
  private final CampoProduccionCientificaRepository campoProduccionCientificaRepository;
  private final ValorCampoRepository valorCampoRepository;
  private final IndiceImpactoRepository indiceImpactoRepository;
  private final AcreditacionRepository acreditacionRepository;
  private final AutorRepository autorRepository;
  private final AutorGrupoRepository autorGrupoRepository;
  private final ProyectoRepository proyectoRepository;

  @Transactional
  public ProduccionCientifica cloneProduccionCientificaAndRelations(Long convocatoriaBaremacionId,
      ProduccionCientifica produccionCientifica) {

    log.debug(
        "cloneProduccionCientificaAndRelations(convocatoriaBaremacionId, produccionCientifica) - start");

    ProduccionCientifica produccionCientificaCreate = cloneProduccionCientificaAndEstado(convocatoriaBaremacionId,
        produccionCientifica);

    Long produccionCientificaId = produccionCientifica.getId();
    Long produccionCientificaIdNew = produccionCientificaCreate.getId();

    campoProduccionCientificaRepository.findAllByProduccionCientificaId(produccionCientificaId).stream()
        .forEach(campo -> cloneCamposAndValores(campo, produccionCientificaIdNew));

    autorRepository.findAllByProduccionCientificaId(produccionCientificaId).stream()
        .forEach(autor -> cloneAutoresAndGrupos(autor, produccionCientificaIdNew));

    proyectoRepository.findAllByProduccionCientificaId(produccionCientificaId).stream()
        .forEach(proyecto -> addProyecto(proyecto, produccionCientificaIdNew));

    acreditacionRepository.findAllByProduccionCientificaId(produccionCientificaId).stream()
        .forEach(acreditacion -> addAcreditacion(acreditacion, produccionCientificaIdNew));

    indiceImpactoRepository.findAllByProduccionCientificaId(produccionCientificaId).stream()
        .forEach(indiceImpacto -> addIndiceImpacto(indiceImpacto, produccionCientificaIdNew));

    log.debug("cloneProduccionCientificaAndRelations(convocatoriaBaremacionId, produccionCientifica) - end");

    return produccionCientificaCreate;
  }

  private ProduccionCientifica cloneProduccionCientificaAndEstado(Long convocatoriaBaremacionId,
      ProduccionCientifica produccionCientifica) {
    ProduccionCientifica produccionCientificaCreate = ProduccionCientifica.builder()
        .epigrafeCVN(produccionCientifica.getEpigrafeCVN())
        .produccionCientificaRef(produccionCientifica.getProduccionCientificaRef())
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .build();

    produccionCientificaCreate = produccionCientificaRepository.save(produccionCientificaCreate);

    Long produccionCientificaId = produccionCientificaCreate.getId();

    EstadoProduccionCientifica estadoProduccionCientifica = addEstado(produccionCientificaId,
        produccionCientifica.getEstado().getEstado());

    produccionCientificaCreate.setEstado(estadoProduccionCientifica);
    produccionCientificaRepository.save(produccionCientificaCreate);
    return produccionCientificaCreate;
  }

  private CampoProduccionCientifica cloneCamposAndValores(CampoProduccionCientifica campo,
      Long produccionCientificaId) {
    CampoProduccionCientifica campoProduccionCientificaCreate = CampoProduccionCientifica.builder()
        .codigoCVN(campo.getCodigoCVN())
        .produccionCientificaId(produccionCientificaId)
        .build();

    campoProduccionCientificaCreate = campoProduccionCientificaRepository.save(campoProduccionCientificaCreate);

    Long campoProduccionCientificaCreateId = campoProduccionCientificaCreate.getId();
    valorCampoRepository.findAllByCampoProduccionCientificaId(campo.getId()).stream()
        .forEach(valorCampo -> addValorCampo(campoProduccionCientificaCreateId, valorCampo.getValor(),
            valorCampo.getOrden()));

    return campoProduccionCientificaCreate;
  }

  public Long addProduccionCientificaAndEstado(ProduccionCientifica produccionCientifica,
      TipoEstadoProduccion tipoEstadoProduccion) {
    ProduccionCientifica produccionCientificaCreate = produccionCientificaRepository.save(produccionCientifica);

    EstadoProduccionCientifica estadoProduccionCientifica = addEstado(
        produccionCientificaCreate.getId(), tipoEstadoProduccion);

    produccionCientificaCreate.setEstado(estadoProduccionCientifica);

    return produccionCientificaRepository.save(produccionCientificaCreate).getId();
  }

  public EstadoProduccionCientifica addEstado(Long produccionCientificaId, TipoEstadoProduccion tipoEstado) {
    EstadoProduccionCientifica estadoProduccionCientifica = EstadoProduccionCientifica.builder()
        .produccionCientificaId(produccionCientificaId)
        .estado(tipoEstado)
        .build();
    estadoProduccionCientifica.setFecha(Instant.now());
    return estadoProduccionCientificaRepository.save(estadoProduccionCientifica);
  }

  public CampoProduccionCientifica addCampoProduccionCientifica(Long produccionCientificaId, CodigoCVN codigoCVN) {
    CampoProduccionCientifica campoProduccionCientifica = CampoProduccionCientifica.builder()
        .produccionCientificaId(produccionCientificaId)
        .codigoCVN(codigoCVN)
        .build();

    return campoProduccionCientificaRepository.save(campoProduccionCientifica);
  }

  public CampoProduccionCientifica addCampoProduccionCientificaAndValor(Long produccionCientificaId,
      CodigoCVN codigoCVN, String valor, Integer orden) {
    CampoProduccionCientifica campoProduccionCientifica = addCampoProduccionCientifica(produccionCientificaId,
        codigoCVN);
    addValorCampo(campoProduccionCientifica.getId(), valor, orden);
    return campoProduccionCientifica;
  }

  public CampoProduccionCientifica addCampoProduccionCientificaAndValor(Long produccionCientificaId,
      CodigoCVN codigoCVN, String valor) {
    return addCampoProduccionCientificaAndValor(produccionCientificaId, codigoCVN, valor, 1);
  }

  public void addValorCampo(Long campoProduccionCientificaId, String valor, Integer orden) {
    ValorCampo valorCampoCreate = ValorCampo.builder()
        .campoProduccionCientificaId(campoProduccionCientificaId)
        .orden(orden)
        .valor(valor)
        .build();

    valorCampoRepository.save(valorCampoCreate);
  }

  private Autor cloneAutoresAndGrupos(Autor autor, Long produccionCientificaId) {

    Autor autorCreate = Autor.builder()
        .produccionCientificaId(produccionCientificaId)
        .firma(autor.getFirma())
        .personaRef(autor.getPersonaRef())
        .nombre(autor.getNombre())
        .apellidos(autor.getApellidos())
        .orcidId(autor.getOrcidId())
        .orden(autor.getOrden())
        .fechaInicio(autor.getFechaInicio())
        .fechaFin(autor.getFechaFin())
        .ip(ObjectUtils.defaultIfNull(autor.getIp(), Boolean.FALSE))
        .build();

    autorCreate = autorRepository.save(autorCreate);
    Long autorCreateId = autorCreate.getId();

    autorGrupoRepository.findAllByAutorId(autor.getId()).stream()
        .forEach(autorGrupo -> addAutorGrupo(autorGrupo, autorCreateId));

    return autorCreate;
  }

  public Autor addAutor(Autor autorNew) {
    return autorRepository.save(autorNew);
  }

  public Autor addAutorByPersonaRefAndIp(Long produccionCientificaId, String personaRef, Boolean ip) {
    Autor autorNew = Autor.builder()
        .produccionCientificaId(produccionCientificaId)
        .personaRef(personaRef)
        .ip(ObjectUtils.defaultIfNull(ip, Boolean.FALSE))
        .build();

    return autorRepository.save(autorNew);
  }

  public AutorGrupo addAutorGrupo(AutorGrupo autorGrupo, Long autorId) {
    AutorGrupo autorGrupoCreate = AutorGrupo.builder()
        .autorId(autorId)
        .estado(autorGrupo.getEstado())
        .grupoRef(autorGrupo.getGrupoRef())
        .build();

    return autorGrupoRepository.save(autorGrupoCreate);
  }

  public Proyecto addProyecto(Proyecto proyecto, Long produccionCientificaId) {
    Proyecto proyectoCreate = Proyecto.builder()
        .proyectoRef(proyecto.getProyectoRef())
        .produccionCientificaId(produccionCientificaId)
        .build();

    return proyectoRepository.save(proyectoCreate);
  }

  public Acreditacion addAcreditacion(Acreditacion acreditacion, Long produccionCientificaId) {
    Acreditacion acreditacionCreate = Acreditacion.builder()
        .url(acreditacion.getUrl())
        .documentoRef(acreditacion.getDocumentoRef())
        .produccionCientificaId(produccionCientificaId)
        .build();

    return acreditacionRepository.save(acreditacionCreate);
  }

  public IndiceImpacto addIndiceImpacto(IndiceImpacto indiceImpacto, Long produccionCientificaId) {
    IndiceImpacto indiceImpactoCreate = IndiceImpacto.builder()
        .produccionCientificaId(produccionCientificaId)
        .posicionPublicacion(indiceImpacto.getPosicionPublicacion())
        .fuenteImpacto(indiceImpacto.getFuenteImpacto())
        .ranking(indiceImpacto.getRanking())
        .anio(indiceImpacto.getAnio())
        .otraFuenteImpacto(indiceImpacto.getOtraFuenteImpacto())
        .numeroRevistas(indiceImpacto.getNumeroRevistas())
        .revista25(indiceImpacto.getRevista25())
        .build();

    return indiceImpactoRepository.save(indiceImpactoCreate);
  }

  @Transactional
  public void deleteProduccionCientifica(ProduccionCientifica produccionCientifica) {
    log.debug("deleteProduccionCientifica(produccionCientifica) - start");

    Long produccionCientificaId = produccionCientifica.getId();
    List<CampoProduccionCientifica> camposToDelete = campoProduccionCientificaRepository
        .findAllByProduccionCientificaId(produccionCientificaId);
    camposToDelete.stream()
        .forEach(entity -> valorCampoRepository.deleteInBulkByCampoProduccionCientificaId(entity.getId()));
    campoProduccionCientificaRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);

    List<Autor> autoresToDelete = autorRepository
        .findAllByProduccionCientificaId(produccionCientificaId);
    autoresToDelete.stream()
        .forEach(entity -> autorGrupoRepository.deleteInBulkByAutorId(entity.getId()));
    autorRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);

    puntuacionGrupoInvestigadorRepository.updatePuntuacionItemInvestigadorNull(produccionCientificaId);

    puntuacionItemInvestigadorRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    puntuacionBaremoItemRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    puntuacionItemInvestigadorRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    acreditacionRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    indiceImpactoRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    proyectoRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    produccionCientificaRepository.updateEstadoNull(produccionCientificaId);
    estadoProduccionCientificaRepository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    produccionCientificaRepository.deleteById(produccionCientificaId);

    log.debug("deleteProduccionCientifica(produccionCientifica) - end");
  }

}

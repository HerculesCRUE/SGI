package org.crue.hercules.sgi.prc.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.prc.dto.EpigrafeCVNOutput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiCreateInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiFullOutput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiOutput;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.service.ProduccionCientificaApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProduccionCientificaApiController
 */
@RestController
@RequestMapping(ProduccionCientificaApiController.MAPPING)
@Slf4j
@RequiredArgsConstructor
public class ProduccionCientificaApiController {
  public static final String MAPPING = "/producciones-cientificas-api";

  private final ProduccionCientificaApiService service;

  /**
   * Crea un nuevo {@link ProduccionCientifica}.
   * 
   * @param produccionCientifica {@link ProduccionCientificaApiInput} que se
   *                             quiere crear.
   * @return Nuevo {@link ProduccionCientificaApiFullOutput} creado.
   */
  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ProduccionCientificaApiFullOutput> create(
      @Valid @RequestBody ProduccionCientificaApiCreateInput produccionCientifica) {

    log.debug("create(ProduccionCientificaApiCreateInput produccionCientifica) - start");
    ProduccionCientificaApiFullOutput returnValue = service.create(produccionCientifica);
    log.debug("create(ProduccionCientificaApiCreateInput produccionCientifica) - end");

    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ProduccionCientifica} con produccionCientificaRef
   * indicado.
   *
   * @param produccionCientifica    {@link ProduccionCientificaApiInput} a
   *                                actualizar.
   * @param produccionCientificaRef produccionCientificaRef
   *                                {@link ProduccionCientifica} a actualizar.
   * @return {@link ProduccionCientificaApiFullOutput} actualizado.
   */
  @PutMapping("/{produccionCientificaRef}")
  @PreAuthorize("isAuthenticated()")
  public ProduccionCientificaApiFullOutput update(
      @Valid @RequestBody ProduccionCientificaApiInput produccionCientifica,
      @PathVariable String produccionCientificaRef) {
    log.debug("update(ProduccionCientifica produccionCientifica, String  produccionCientificaRef) - start");
    ProduccionCientificaApiFullOutput returnValue = service.update(produccionCientifica, produccionCientificaRef);
    log.debug("update(ProduccionCientifica produccionCientifica, String  produccionCientificaRef) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link ProduccionCientifica} con produccionCientificaRef
   * indicado.
   *
   * @param produccionCientificaRef produccionCientificaRef de
   *                                {@link ProduccionCientifica}.
   */
  @DeleteMapping("/{produccionCientificaRef}")
  @PreAuthorize("isAuthenticated()")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteByProduccionCientificaRef(@PathVariable String produccionCientificaRef) {
    log.debug("deleteByProduccionCientificaRef(String produccionCientificaRef) -  start");

    service.delete(produccionCientificaRef);

    log.debug("deleteByProduccionCientificaRef(String produccionCientificaRef) -  end");
  }

  /**
   * Devuelve el identificador CVN y el estado (Validado O Rechazado) de
   * aquellos items almacenados en producción científica que han cambiado al
   * estado Validado o Rechazado en una fecha igual o superior a la fecha de
   * estado pasada por parámetro
   *
   * @param query filtro de búsqueda fechaEstado
   * @return lista de {@link ProduccionCientificaApiOutput}.
   */
  @GetMapping("/estado")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<ProduccionCientificaApiOutput>> findByEstadoValidadoOrRechazadoByFechaModificacion(
      @RequestParam(name = "q", required = true) String query) {
    log.debug("findByEstadoValidadoOrRechazadoByFechaModificacion(String query) - start");

    List<ProduccionCientificaApiOutput> returnValue = service.findByEstadoValidadoOrRechazadoByFechaModificacion(query);

    if (returnValue.isEmpty()) {
      log.debug("findByEstadoValidadoOrRechazadoByFechaModificacion(String query) -  end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findByEstadoValidadoOrRechazadoByFechaModificacion(String query) -  end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
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
  @GetMapping("/epigrafes")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<EpigrafeCVNOutput>> findListadoEpigrafes() {
    log.debug("findListadoEpigrafes() - start");

    List<EpigrafeCVNOutput> returnValue = service.findListadoEpigrafes();

    if (returnValue.isEmpty()) {
      log.debug("findListadoEpigrafes() - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findListadoEpigrafes() - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

}
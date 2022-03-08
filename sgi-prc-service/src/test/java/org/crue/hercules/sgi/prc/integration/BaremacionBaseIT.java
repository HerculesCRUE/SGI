package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.controller.BaremacionController;
import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem;
import org.crue.hercules.sgi.prc.repository.PuntuacionBaremoItemRepository;
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
  @Getter
  private PuntuacionBaremoItemRepository puntuacionBaremoItemRepository;

  protected HttpEntity<Void> buildRequest(HttpHeaders headers,
      Void entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s",
            tokenBuilder.buildToken("user", "AUTH")));

    HttpEntity<Void> request = new HttpEntity<>(entity, headers);
    return request;
  }

  protected void baremacionWithoutPuntuaciones(Long idBaremacion) throws Exception {
    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequest(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = puntuacionBaremoItemRepository.findAll();

    Assertions.assertThat(puntuacionBaremoItems.size()).as("numPuntuaciones").isEqualTo(0);
  }

  protected void baremacionWithOnePuntuacion(Long idBaremacion, String baremoId, String puntos) throws Exception {
    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequest(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = puntuacionBaremoItemRepository.findAll();

    Assertions.assertThat(puntuacionBaremoItems.size()).as("numPuntuaciones").isEqualTo(1);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(Long.valueOf(
        baremoId));
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos")
        .isEqualTo(new BigDecimal(puntos));
  }
}

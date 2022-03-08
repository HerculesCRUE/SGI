package org.crue.hercules.sgi.prc.repository;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica.EpigrafeCVN;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProduccionCientificaRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProduccionCientificaRepository repository;

  @Test
  public void findByProduccionCientificaRef_ReturnsProduccionCientifica() throws Exception {
    String idRefSearch = "id-ref-1";
    EpigrafeCVN epigrafeCVNSearch = EpigrafeCVN.E060_010_010_000;

    // given: 2 ProduccionCientifica de los que 1 coincide con el idRef buscado
    ProduccionCientifica produccionCientifica = ProduccionCientifica.builder()
        .produccionCientificaRef(idRefSearch)
        .epigrafeCVN(epigrafeCVNSearch)
        .build();
    produccionCientifica = repository.save(produccionCientifica);

    // when: se busca el ProduccionCientifica idRef
    ProduccionCientifica produccionCientificaSearched = repository
        .findByProduccionCientificaRefAndConvocatoriaBaremacionIdIsNull(idRefSearch).get();

    // then: Se recupera el ProduccionCientifica con el idRef buscado
    Assertions.assertThat(produccionCientificaSearched.getId()).as("getId").isNotNull();
    Assertions.assertThat(produccionCientificaSearched.getProduccionCientificaRef()).as("getProduccionCientificaRef")
        .isEqualTo(produccionCientifica.getProduccionCientificaRef());
    Assertions.assertThat(produccionCientificaSearched.getEpigrafeCVN()).as("getEpigrafeCVN")
        .isEqualTo(produccionCientifica.getEpigrafeCVN());
  }
}

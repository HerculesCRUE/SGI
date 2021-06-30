package org.crue.hercules.sgi.eti.repository;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Formly;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class FormlyRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private FormlyRepository repository;

  @Test
  public void findFirstByNombreOrderByVersionDesc_ReturnsLatestWithGivenName() throws Exception {
    // given: two Formly, with three versions each
    final int N_DOCS = 2;
    final int N_VERS = 3;
    for (int i = 1; i <= N_DOCS; i++) {
      for (int j = 1; j <= N_VERS; j++) {
        Formly formly = entityManager
            .persistAndFlush(Formly.builder().nombre("FRM" + i).esquema("{}").version(j).build());
        formly = entityManager.persistAndFlush(formly);
      }
    }

    // when: find latest formly version named 'FRM1'
    Formly dataFound = repository.findFirstByNombreOrderByVersionDesc("FRM1").get();

    // then: latest formly version is returned
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getNombre()).isEqualTo("FRM1");
    Assertions.assertThat(dataFound.getVersion()).isEqualTo(N_VERS);
  }
}

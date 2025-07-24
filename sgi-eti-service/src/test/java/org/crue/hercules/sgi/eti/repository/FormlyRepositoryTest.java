package org.crue.hercules.sgi.eti.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Formly;
import org.crue.hercules.sgi.eti.model.FormlyDefinicion;
import org.crue.hercules.sgi.framework.i18n.Language;
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
    final int N_VERS = 3;
    for (int j = 1; j <= N_VERS; j++) {
      Formly formly = entityManager
          .persistAndFlush(Formly.builder().version(j).nombre("FRM" + j).build());
      formly = entityManager.persistAndFlush(formly);

      FormlyDefinicion formlyNombre = entityManager
          .persistAndFlush(
              FormlyDefinicion.builder().formlyId(formly.getId()).esquema("{}")
                  .lang(Language.ES).build());
      formlyNombre = entityManager.persistAndFlush(formlyNombre);
    }

    // when: find latest formly version named 'FRM1'
    Optional<Formly> dataFound = repository.findFirstByNombreOrderByVersionDesc("FRM3");

    // then: latest formly version is returned
    Assertions.assertThat(dataFound).isPresent();
    Assertions.assertThat(dataFound.get().getVersion()).isEqualTo(N_VERS);
  }
}

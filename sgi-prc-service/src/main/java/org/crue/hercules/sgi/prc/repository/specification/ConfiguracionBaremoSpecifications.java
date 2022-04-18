package org.crue.hercules.sgi.prc.repository.specification;

import java.util.List;

import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoFuente;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo_;
import org.springframework.data.jpa.domain.Specification;

public class ConfiguracionBaremoSpecifications {

  private ConfiguracionBaremoSpecifications() {
  }

  /**
   * {@link ConfiguracionBaremo} con el {@link EpigrafeCVN} indicado.
   * 
   * @param epigrafeCVN {@link EpigrafeCVN}.
   * @return specification para obtener {@link ConfiguracionBaremo} con el
   *         {@link EpigrafeCVN} indicado.
   */
  public static Specification<ConfiguracionBaremo> byEpigrafeCVN(EpigrafeCVN epigrafeCVN) {
    return (root, query, cb) -> cb.equal(root.get(ConfiguracionBaremo_.epigrafeCVN), epigrafeCVN);
  }

  /**
   * Lista de {@link ConfiguracionBaremo} de los {@link TipoFuente} indicados.
   * 
   * @param tiposFuente Lista de {@link TipoFuente}.
   * @return specification para obtener los {@link ConfiguracionBaremo} de los
   *         {@link TipoFuente} indicados.
   */
  public static Specification<ConfiguracionBaremo> tipoFuenteIn(List<TipoFuente> tiposFuente) {
    return (root, query, cb) -> root.get(ConfiguracionBaremo_.tipoFuente).in(tiposFuente);
  }
}

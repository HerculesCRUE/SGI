package org.crue.hercules.sgi.prc.repository.specification;

import java.util.List;

import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoFuente;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoNodo;
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

  /**
   * {@link ConfiguracionBaremo} con el id y el {@link TipoNodo} indicados.
   * 
   * @param id       {@link ConfiguracionBaremo} id.
   * @param tipoNodo {@link TipoNodo}
   * @return specification para obtener {@link ConfiguracionBaremo} con el
   *         id y el {@link TipoNodo} indicados.
   */
  public static Specification<ConfiguracionBaremo> byIdAndTipoNodo(Long id, TipoNodo tipoNodo) {
    return (root, query, cb) -> cb.and(
        cb.equal(root.get(ConfiguracionBaremo_.id), id),
        cb.equal(root.get(ConfiguracionBaremo_.tipoNodo), tipoNodo));
  }

  /**
   * {@link ConfiguracionBaremo} con el id y sin el {@link TipoNodo} indicados.
   * 
   * @param id       {@link ConfiguracionBaremo} id.
   * @param tipoNodo {@link TipoNodo}
   * @return specification para obtener {@link ConfiguracionBaremo} con el
   *         id y sin el {@link TipoNodo} indicados.
   */
  public static Specification<ConfiguracionBaremo> byIdAndTipoNodoNotEqual(Long id, TipoNodo tipoNodo) {
    return (root, query, cb) -> cb.and(
        cb.equal(root.get(ConfiguracionBaremo_.id), id),
        cb.notEqual(root.get(ConfiguracionBaremo_.tipoNodo), tipoNodo));
  }

  /**
   * {@link ConfiguracionBaremo} con el id indicado y uno de los {@link TipoNodo}
   * indicados.
   * 
   * @param id       {@link ConfiguracionBaremo} id.
   * @param tipoNodo {@link TipoNodo}
   * @return specification para obtener {@link ConfiguracionBaremo} con el
   *         id indicado y uno de los {@link TipoNodo} indicados.
   */
  public static Specification<ConfiguracionBaremo> byIdAndTipoNodoIn(Long id, List<TipoNodo> tipoNodo) {
    return (root, query, cb) -> cb.and(
        cb.equal(root.get(ConfiguracionBaremo_.id), id),
        root.get(ConfiguracionBaremo_.tipoNodo).in(tipoNodo));
  }

  /**
   * {@link ConfiguracionBaremo} activas.
   * 
   * @return specification para obtener las {@link ConfiguracionBaremo} activos.
   */
  public static Specification<ConfiguracionBaremo> activos() {
    return (root, query, cb) -> cb.equal(root.get(Activable_.activo), Boolean.TRUE);
  }
}

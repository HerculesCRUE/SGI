package org.crue.hercules.sgi.prc.repository.specification;

import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.Baremo_;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo_;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.springframework.data.jpa.domain.Specification;

public class BaremoSpecifications {

  private BaremoSpecifications() {
  }

  /**
   * {@link Baremo} de la {@link ConvocatoriaBaremacion} con el id indicado.
   * 
   * @param id identificador del {@link ConvocatoriaBaremacion}.
   * @return specification para obtener los {@link Baremo} de la
   *         {@link ConvocatoriaBaremacion} con el id indicado.
   */
  public static Specification<Baremo> byConvocatoriaBaremacionId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(Baremo_.convocatoriaBaremacionId), id);
  }

  /**
   * {@link Baremo} de la {@link ConfiguracionBaremo} con el
   * {@link EpigrafeCVN} indicado.
   * 
   * @param epigrafeCVN {@link EpigrafeCVN}.
   * @return specification para obtener los {@link Baremo} de la
   *         {@link ConfiguracionBaremo} con el {@link EpigrafeCVN} indicado.
   */
  public static Specification<Baremo> byConfiguracionBaremoEpigrafeCVN(EpigrafeCVN epigrafeCVN) {
    return (root, query, cb) -> cb.equal(root.get(Baremo_.configuracionBaremo).get(ConfiguracionBaremo_.epigrafeCVN),
        epigrafeCVN);
  }

  /**
   * {@link Baremo} de la {@link ConfiguracionBaremo} con el tipoBaremo indicado
   * 
   * @param tipoBaremo {@link TipoBaremo}.
   * 
   * @return specification para obtener los {@link Baremo} de la
   *         {@link ConfiguracionBaremo} con el tipoBaremo indicado.
   */
  public static Specification<Baremo> byConfiguracionBaremoTipoBaremo(TipoBaremo tipoBaremo) {
    return (root, query, cb) -> cb.equal(root.get(Baremo_.configuracionBaremo).get(ConfiguracionBaremo_.tipoBaremo),
        tipoBaremo);
  }

  /**
   * {@link Baremo} de la {@link ConfiguracionBaremo} activa.
   * 
   * @return specification para obtener los {@link Baremo} de la
   *         {@link ConfiguracionBaremo} activa.
   */
  public static Specification<Baremo> byConfiguracionBaremoActivoIsTrue() {
    return (root, query, cb) -> cb.isTrue(root.get(Baremo_.configuracionBaremo).get(Activable_.activo));
  }

}

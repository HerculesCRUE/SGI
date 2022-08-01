package org.crue.hercules.sgi.eer.repository.specification;

import org.crue.hercules.sgi.eer.model.TipoDocumento;
import org.crue.hercules.sgi.eer.model.TipoDocumento_;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.springframework.data.jpa.domain.Specification;

public class TipoDocumentoSpecifications {

  private TipoDocumentoSpecifications() {
  }

  /**
   * {@link TipoDocumento} activos.
   * 
   * @return specification para obtener los {@link TipoDocumento} activos.
   */
  public static Specification<TipoDocumento> activos() {
    return (root, query, cb) -> cb.isTrue(root.get(Activable_.activo));
  }

  /**
   * {@link TipoDocumento} con el padreId indicado.
   * 
   * @param padreId Identificador del {@link TipoDocumento} padre.
   * @return specification para obtener los {@link TipoDocumento} con el padreId
   *         indicado.
   */
  public static Specification<TipoDocumento> byPadreId(Long padreId) {
    return (root, query, cb) -> cb.equal(root.get(TipoDocumento_.padre), padreId);
  }

  /**
   * {@link TipoDocumento} con el padreId null.
   * 
   * @return specification para obtener los {@link TipoDocumento} con el padreId
   *         null.
   */
  public static Specification<TipoDocumento> byPadreIdisNull() {
    return (root, query, cb) -> cb.isNull(root.get(TipoDocumento_.padre));
  }
}

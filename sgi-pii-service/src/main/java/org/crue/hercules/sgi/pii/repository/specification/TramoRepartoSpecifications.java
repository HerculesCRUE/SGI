package org.crue.hercules.sgi.pii.repository.specification;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.pii.model.TramoReparto;
import org.crue.hercules.sgi.pii.model.TramoReparto.Tipo;
import org.crue.hercules.sgi.pii.model.TramoReparto_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TramoRepartoSpecifications {

  /**
   * {@link TramoReparto} con el {@link TramoReparto.Tipo} especificado
   * 
   * @param tipo {@link TramoReparto.Tipo} especificado.
   * @return Specification para obtener los {@link TramoReparto} con el
   *         {@link TramoReparto.Tipo} especificado.
   */
  public static Specification<TramoReparto> withTipo(Tipo tipo) {
    return (root, query, cb) -> cb.equal(root.get(TramoReparto_.tipo), tipo);
  }

  /**
   * {@link TramoReparto} con tramos solapados al tramo no final indicado por
   * parámetros
   * 
   * @param desde inicio del tramo.
   * @param hasta fin del tramo.
   * @return specification para obtener los {@link TramoReparto} con tramos
   *         solapados.
   */
  public static Specification<TramoReparto> overlappedTramoRepartoNotFinal(Integer desde, Integer hasta) {
    return (root, query, cb) -> cb.and(cb.and(cb.or(cb.between(root.get(TramoReparto_.desde), desde, hasta),
        cb.between(root.get(TramoReparto_.hasta), desde, hasta),
        cb.and(cb.lessThanOrEqualTo(root.get(TramoReparto_.desde), desde),
            cb.greaterThanOrEqualTo(root.get(TramoReparto_.hasta), hasta)))));
  }

  /**
   * {@link TramoReparto} con tramos solapados al tramo final indicado por
   * parámetros
   * 
   * @param desdeTramoFinal inicio del tramo final.
   * @return specification para obtener los {@link TramoReparto} con tramos
   *         solapados.
   */
  public static Specification<TramoReparto> overlappedTramoRepartoFinal(Integer desdeTramoFinal) {
    return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(TramoReparto_.hasta), desdeTramoFinal);
  }

  /**
   * {@link TramoReparto} con un valor en hasta igual al valor esperado
   * 
   * @param expectedHasta hasta esperado para evitar saltos entre tramos.
   * @return specification para obtener los {@link TramoReparto} con hasta igual
   *         al valor esperado.
   */
  public static Specification<TramoReparto> noGapBetweenTramoReparto(Integer expectedHasta) {
    return (root, query, cb) -> cb.equal(root.get(TramoReparto_.hasta), expectedHasta);
  }

  /**
   * Devuelve el {@link TramoReparto} con el id indicado si tiene el campo Hasta
   * máximo
   * 
   * @param id del {@link TramoReparto} a comprobar.
   * @return specification para obtener el {@link TramoReparto} con el id indicado
   *         si tiene el campo Hasta máximo
   */
  public static Specification<TramoReparto> hasTramoRepartoGreatestDesde(Long id) {
    return (root, query, cb) -> {
      Subquery<Integer> subquery = query.subquery(Integer.class);
      Root<TramoReparto> rootSubquery = subquery.from(TramoReparto.class);
      subquery.select(cb.max(rootSubquery.get(TramoReparto_.desde)));

      return cb.and(cb.equal(root.get(TramoReparto_.desde), subquery), cb.equal(root.get(TramoReparto_.id), id));
    };
  }
}

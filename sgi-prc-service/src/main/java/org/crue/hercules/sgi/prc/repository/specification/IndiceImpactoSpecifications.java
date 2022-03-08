package org.crue.hercules.sgi.prc.repository.specification;

import java.util.List;

import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.IndiceImpacto_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.springframework.data.jpa.domain.Specification;

public class IndiceImpactoSpecifications {

  private IndiceImpactoSpecifications() {
  }

  /**
   * {@link IndiceImpacto} de la {@link ProduccionCientifica} con el id indicado.
   * 
   * @param id identificador de la {@link ProduccionCientifica}.
   * @return specification para obtener las {@link IndiceImpacto} de
   *         la {@link ProduccionCientifica} con el id indicado.
   */
  public static Specification<IndiceImpacto> byProduccionCientificaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(IndiceImpacto_.produccionCientificaId), id);
    };
  }

  /**
   * {@link IndiceImpacto} del anio indicado
   * 
   * @param anio identificador del {@link ProduccionCientifica}.
   * @return specification para obtener los {@link IndiceImpacto} de anio
   *         indicado.
   */
  public static Specification<IndiceImpacto> byAnio(Integer anio) {
    return (root, query, cb) -> {
      return cb.equal(root.get(IndiceImpacto_.anio), anio);
    };
  }

  /**
   * {@link IndiceImpacto} con anio null
   * 
   * @return specification para obtener los {@link IndiceImpacto} de anio
   *         indicado.
   */
  public static Specification<IndiceImpacto> byAnioIsNull() {
    return (root, query, cb) -> {
      return cb.isNull(root.get(IndiceImpacto_.anio));
    };
  }

  /**
   * Lista de {@link IndiceImpacto} de los {@link TipoFuenteImpacto} indicados.
   * 
   * @param tiposFuenteImpacto Lista de {@link TipoFuenteImpacto}.
   * @return specification para obtener los {@link IndiceImpacto} de los
   *         {@link TipoFuenteImpacto} indicados.
   */
  public static Specification<IndiceImpacto> tipoFuenteImpactoIn(List<TipoFuenteImpacto> tiposFuenteImpacto) {
    return (root, query, cb) -> {
      return root.get(IndiceImpacto_.fuenteImpacto).in(tiposFuenteImpacto);
    };
  }

  /**
   * Lista de {@link IndiceImpacto} que no contengan los {@link TipoFuenteImpacto}
   * indicados.
   * 
   * @param tiposFuenteImpacto Lista de {@link TipoFuenteImpacto}.
   * @return specification para obtener los {@link IndiceImpacto} que no contengan
   *         los {@link TipoFuenteImpacto} indicados.
   */
  public static Specification<IndiceImpacto> tipoFuenteImpactoNotIn(List<TipoFuenteImpacto> tiposFuenteImpacto) {
    return (root, query, cb) -> {
      return cb.not(root.get(IndiceImpacto_.fuenteImpacto).in(tiposFuenteImpacto));
    };
  }

}

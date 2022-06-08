package org.crue.hercules.sgi.prc.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.crue.hercules.sgi.prc.model.AutorGrupo_;
import org.crue.hercules.sgi.prc.model.Autor_;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica_;
import org.springframework.data.jpa.domain.Specification;

public class ProduccionCientificaSpecifications {

  private ProduccionCientificaSpecifications() {
  }

  /**
   * {@link ProduccionCientifica} de la {@link ConvocatoriaBaremacion} con
   * el id indicado.
   * 
   * @param convocatoriaBaremacionId identificador del
   *                                 {@link ConvocatoriaBaremacion}.
   * @return specification para obtener los {@link ProduccionCientifica} de
   *         la {@link ConvocatoriaBaremacion} con el id indicado.
   */
  public static Specification<ProduccionCientifica> byConvocatoriaBaremacionId(Long convocatoriaBaremacionId) {
    return (root, query, cb) -> cb.equal(root.get(ProduccionCientifica_.convocatoriaBaremacionId),
        convocatoriaBaremacionId);
  }

  /**
   * {@link ProduccionCientifica} de la {@link ConvocatoriaBaremacion} con
   * el id null.
   * 
   * @return specification para obtener los {@link ProduccionCientifica} de
   *         la {@link ConvocatoriaBaremacion} con el id null.
   */
  public static Specification<ProduccionCientifica> byConvocatoriaBaremacionIsNull() {
    return (root, query, cb) -> cb.isNull(root.get(ProduccionCientifica_.convocatoriaBaremacionId));
  }

  /**
   * {@link ProduccionCientifica} con produccionCientificaRef que empieza por el
   * prefijo indicado
   * 
   * @param prefix prefijo.
   * @return specification para obtener los {@link ProduccionCientifica} con
   *         produccionCientificaRef que empieza por el prefijo indicado
   */
  public static Specification<ProduccionCientifica> byProduccionCientificaRefStartWith(String prefix) {
    return (root, query, cb) -> cb.like(root.get(ProduccionCientifica_.produccionCientificaRef), prefix + "%");
  }

  /**
   * {@link ProduccionCientifica} con el {@link EpigrafeCVN} indicado.
   * 
   * @param epigrafeCVN {@link EpigrafeCVN}.
   * @return specification para obtener los {@link ProduccionCientifica} con
   *         el {@link EpigrafeCVN} indicado.
   */
  public static Specification<ProduccionCientifica> byEpigrafeCVN(EpigrafeCVN epigrafeCVN) {
    return (root, query, cb) -> cb.equal(root.get(ProduccionCientifica_.epigrafeCVN), epigrafeCVN);
  }

  /**
   * Lista de {@link ProduccionCientifica} que no contenga los
   * {@link EpigrafeCVN} indicados.
   * 
   * @param epigrafes Lista de {@link EpigrafeCVN}.
   * @return specification para obtener los {@link ProduccionCientifica} que
   *         contenga los {@link EpigrafeCVN} indicados.
   */
  public static Specification<ProduccionCientifica> byEpigrafeCVNIn(List<EpigrafeCVN> epigrafes) {
    return (root, query, cb) -> root.get(ProduccionCientifica_.epigrafeCVN).in(epigrafes);
  }

  /**
   * Lista de {@link ProduccionCientifica} que contenga los grupoRef indicados.
   * Si la lista de grupoRef es null o está vacía, devuelve un filtro que se
   * resuelve siempre a false.
   * 
   * @param gruposRef Lista de grupoRef.
   * @return specification para obtener los {@link ProduccionCientifica} que
   *         contenga los grupoRef indicados.
   */
  public static Specification<ProduccionCientifica> byExistsSubqueryInGrupoRef(List<Long> gruposRef) {
    return (root, query, cb) -> {
      if (gruposRef == null || gruposRef.isEmpty()) {
        return cb.and(cb.isTrue(cb.literal(false))); // always false = no filtering
      }

      List<Predicate> predicatesSubquery = new ArrayList<>();

      Subquery<Long> queryAutorGrupo = query.subquery(Long.class);
      Root<AutorGrupo> subqRoot = queryAutorGrupo.from(AutorGrupo.class);

      Join<AutorGrupo, Autor> joinAutor = subqRoot.join(AutorGrupo_.autor);

      predicatesSubquery
          .add(cb.and(cb.equal(joinAutor.get(Autor_.produccionCientificaId), root.get(ProduccionCientifica_.id))));
      predicatesSubquery
          .add(cb.and(subqRoot.get(AutorGrupo_.grupoRef).in(gruposRef)));

      queryAutorGrupo.select(subqRoot.get(AutorGrupo_.id))
          .where(predicatesSubquery.toArray(new Predicate[] {}));

      return cb.and(cb.exists(queryAutorGrupo));
    };
  }

  /**
   * {@link ProduccionCientifica} por id.
   * 
   * @param id de la {@link ProduccionCientifica}.
   * @return specification para obtener la {@link ProduccionCientifica} por id.
   */
  public static Specification<ProduccionCientifica> byId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(ProduccionCientifica_.id), id);
  }

  /**
   * Lista de {@link ProduccionCientifica} con estado no final (diferente de
   * RECHAZADO o VALIDADO).
   * 
   * @return specification para obtener las {@link ProduccionCientifica} con
   *         estado no final (diferente de RECHAZADO o VALIDADO).
   */
  public static Specification<ProduccionCientifica> isInEstadoEditable() {
    return (root, query, cb) -> {
      Join<ProduccionCientifica, EstadoProduccionCientifica> joinEstado = root.join(ProduccionCientifica_.estado);
      return cb.and(
          cb.notEqual(joinEstado.get(EstadoProduccionCientifica_.estado), TipoEstadoProduccion.RECHAZADO),
          cb.notEqual(joinEstado.get(EstadoProduccionCientifica_.estado), TipoEstadoProduccion.VALIDADO));
    };
  }

  /**
   * Lista de {@link ProduccionCientifica} con los {@link AutorGrupo} que
   * pertenezcan a uno de los grupos recibidos como parámetro y que estén en
   * el estado recibido por parámetro.
   * 
   * @param estado    estado del {@link AutorGrupo}
   * @param gruposRef Lista de grupoRef.
   * @return specification para obtener los {@link ProduccionCientifica} que
   *         pertenezcan a uno de los grupos recibidos como parámetro y que estén
   *         en el estado recibido por parámetro.
   */
  public static Specification<ProduccionCientifica> byAutorGrupoEstadoAndAutorGrupoInGrupoRef(
      TipoEstadoProduccion estado, List<Long> gruposRef) {
    return (root, query, cb) -> {
      Join<ProduccionCientifica, Autor> joinAutor = root.join(ProduccionCientifica_.autores);
      Join<Autor, AutorGrupo> joinAutorGrupo = joinAutor.join(Autor_.autoresGrupo);

      return cb.and(
          cb.equal(joinAutorGrupo.get(AutorGrupo_.estado), estado),
          joinAutorGrupo.get(AutorGrupo_.grupoRef).in(gruposRef));
    };
  }
}

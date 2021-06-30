package org.crue.hercules.sgi.eti.repository.predicate;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Acta_;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion_;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluacion_;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion_;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa_;
import org.crue.hercules.sgi.eti.repository.custom.CustomMemoriaRepositoryImpl.FechasMemoria;
import org.crue.hercules.sgi.eti.util.Constantes;

public class MemoriaPredicateBuilder {
  List<Predicate> predicates;

  MemoriaPredicateBuilder() {
    this.predicates = new LinkedList<>();
  }

  public static MemoriaPredicateBuilder builder() {
    return new MemoriaPredicateBuilder();
  }

  public MemoriaPredicateBuilder filterWithMemoryIdPredicate(final Root<Evaluacion> root, final CriteriaBuilder cb,
      Long memoriaId) {
    this.predicates.add(cb.equal(root.get(Evaluacion_.memoria).get(Memoria_.id), memoriaId));
    return this;
  }

  private Predicate filterWithTipoConvocatoriaReunion(final CriteriaBuilder cb,
      Root<ConvocatoriaReunion> fechaEvaluacionQueryRoot, Long tipoConvocatoriaReunion) {
    return cb.equal(
        fechaEvaluacionQueryRoot.get(ConvocatoriaReunion_.tipoConvocatoriaReunion).get(TipoConvocatoriaReunion_.id),
        tipoConvocatoriaReunion);
  }

  public MemoriaPredicateBuilder filterWithAnyTipoConvocatoriaReunion(final CriteriaBuilder cb,
      Root<Evaluacion> fechaEvaluacionQueryRoot, Long tipoConvocatoriaReunion) {
    this.predicates.add(cb.equal(fechaEvaluacionQueryRoot.get(Evaluacion_.convocatoriaReunion)
        .get(ConvocatoriaReunion_.tipoConvocatoriaReunion).get(TipoConvocatoriaReunion_.id), tipoConvocatoriaReunion));
    return this;
  }

  public MemoriaPredicateBuilder filterWithAnyTipoConvocatoriaReunionNotJoined(final CriteriaBuilder cb,
      Root<ConvocatoriaReunion> root, Long tipoConvocatoriaReunion) {
    this.predicates
        .add(cb.equal(root.get(ConvocatoriaReunion_.tipoConvocatoriaReunion).get(TipoConvocatoriaReunion_.id),
            tipoConvocatoriaReunion));
    return this;
  }

  public MemoriaPredicateBuilder filterWithTipoConvocatoriaReunionOrdinarioOrExtraordinario(final CriteriaBuilder cb,
      final Root<ConvocatoriaReunion> root) {
    this.predicates
        .add(cb.or(filterWithTipoConvocatoriaReunion(cb, root, Constantes.TIPO_CONVOCATORIA_REUNION_ORDINARIA),
            filterWithTipoConvocatoriaReunion(cb, root, Constantes.TIPO_CONVOCATORIA_REUNION_EXTRAORDINARIA)));
    return this;
  }

  public MemoriaPredicateBuilder filterWithTipoEvaluacionEqualsTo(final CriteriaBuilder cb, final Root<Evaluacion> root,
      Long tipoEvaluacion) {
    this.predicates.add(cb.equal(root.get(Evaluacion_.tipoEvaluacion), tipoEvaluacion));
    return this;
  }

  public MemoriaPredicateBuilder filterWithComite(final Root<Evaluacion> root, final CriteriaBuilder cb,
      Comite comite) {
    this.predicates.add(cb.equal(root.get(Evaluacion_.memoria).get(Memoria_.comite), comite));
    return this;
  }

  public MemoriaPredicateBuilder filterWithComiteConvocatoriaReunion(final Root<ConvocatoriaReunion> root,
      final CriteriaBuilder cb, Comite comite) {
    this.predicates.add(cb.equal(root.get(ConvocatoriaReunion_.comite), comite));
    return this;
  }

  public MemoriaPredicateBuilder filterWithFechaLimiteConvocatoriaReunionGreatestThanNow(CriteriaBuilder cb,
      Root<Evaluacion> root) {
    this.predicates.add(
        cb.greaterThan(root.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.fechaLimite), Instant.now()));
    return this;
  }

  public MemoriaPredicateBuilder filterWithFechaLimiteConvocatoriaReunionGreatestThanNowConvocatoriaReunion(
      CriteriaBuilder cb, Root<ConvocatoriaReunion> root) {
    this.predicates.add(cb.greaterThan(root.get(ConvocatoriaReunion_.fechaLimite), Instant.now()));
    return this;
  }

  public MemoriaPredicateBuilder filterWithActasNotInConvocatoriasFinalizadas(final CriteriaBuilder cb,
      Root<Evaluacion> root, final CriteriaQuery<FechasMemoria> cq) {

    Subquery<Long> actaQuery = cq.subquery(Long.class);
    Root<Acta> actaRoot = actaQuery.from(Acta.class);

    actaQuery.select(actaRoot.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.id)).where(
        cb.equal(actaRoot.get(Acta_.estadoActual).get(TipoEstadoActa_.id), Constantes.TIPO_ESTADO_ACTA_FINALIZADA));

    this.predicates.add(cb.not(root.get(Evaluacion_.convocatoriaReunion).get(ConvocatoriaReunion_.id).in(actaQuery)));

    return this;
  }

  public MemoriaPredicateBuilder filterWithActasNotInConvocatoriasFinalizadasConvocatoriaReunion(
      final CriteriaBuilder cb, Root<ConvocatoriaReunion> root, final CriteriaQuery<FechasMemoria> cq) {

    Subquery<Long> actaQuery = cq.subquery(Long.class);
    Root<Acta> actaRoot = actaQuery.from(Acta.class);

    actaQuery.select(actaRoot.get(Acta_.convocatoriaReunion).get(ConvocatoriaReunion_.id)).where(
        cb.equal(actaRoot.get(Acta_.estadoActual).get(TipoEstadoActa_.id), Constantes.TIPO_ESTADO_ACTA_FINALIZADA));

    this.predicates.add(cb.not(root.get(ConvocatoriaReunion_.id).in(actaQuery)));

    return this;
  }

  public MemoriaPredicateBuilder filterWithMemoriaActiva(CriteriaBuilder cb, Root<Evaluacion> root) {
    this.predicates.add(cb.equal(root.get(Evaluacion_.memoria).get(Memoria_.activo), Boolean.TRUE));
    return this;
  }

  public MemoriaPredicateBuilder filterWithEvaluacionActiva(CriteriaBuilder cb, Root<Evaluacion> root) {
    this.predicates.add(cb.equal(root.get(Evaluacion_.activo), Boolean.TRUE));
    return this;
  }

  public MemoriaPredicateBuilder filterWithLastVersion(final CriteriaBuilder cb, Root<Evaluacion> root, Long memoriaId,
      CriteriaQuery<FechasMemoria> cq) {

    Subquery<Integer> maxVersionQuery = cq.subquery(Integer.class);
    Root<Evaluacion> subqueryRoot = maxVersionQuery.from(Evaluacion.class);
    maxVersionQuery.select(cb.greatest(subqueryRoot.get(Evaluacion_.version)))
        .where(cb.equal(subqueryRoot.get(Evaluacion_.memoria).get(Memoria_.id), memoriaId));
    this.predicates.add(cb.equal(root.get(Evaluacion_.version), maxVersionQuery));

    return this;
  }

  public MemoriaPredicateBuilder addCustomPredicate(final Predicate predicate) {

    this.predicates.add(predicate);

    return this;
  }

  public Predicate[] build() {
    return predicates.toArray(new Predicate[] {});
  }

}

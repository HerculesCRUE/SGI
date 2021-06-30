package org.crue.hercules.sgi.eti.repository.specification;

import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva_;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion_;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.Retrospectiva_;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria_;
import org.springframework.data.jpa.domain.Specification;

public class MemoriaSpecifications {

  public static Specification<Memoria> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Memoria_.activo), Boolean.TRUE);
    };
  }

  public static Specification<Memoria> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Memoria_.activo), Boolean.FALSE);
    };
  }

  public static Specification<Memoria> estadoActualIn(List<Long> estados) {
    return (root, query, cb) -> {
      if (estados != null && !estados.isEmpty()) {
        return root.get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id).in(estados);
      } else {
        return cb.and();
      }
    };
  }

  public static Specification<Memoria> estadoRetrospectivaIn(List<Long> estados) {
    return (root, query, cb) -> {
      if (estados != null && !estados.isEmpty()) {
        Join<Memoria, Retrospectiva> joinMemoriaRetrospectiva = root.join(Memoria_.retrospectiva, JoinType.LEFT);
        return joinMemoriaRetrospectiva.get(Retrospectiva_.estadoRetrospectiva).get(EstadoRetrospectiva_.id)
            .in(estados);
      } else {
        return cb.and();
      }
    };
  }

  public static Specification<Memoria> byPersonaRef(String personaRef) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Memoria_.personaRef), personaRef);
    };
  }

  public static Specification<Memoria> byPersonaRefPeticionEvaluacion(String personaRef) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.personaRef), personaRef);
    };
  }

  public static Specification<Memoria> byPeticionEvaluacion(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id), id);
    };
  }

  public static Specification<Memoria> byPeticionesEvaluacion(List<Long> ids) {
    return (root, query, cb) -> {
      if (ids != null && !ids.isEmpty()) {
        return root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id).in(ids);
      } else {
        return cb.and();
      }
    };
  }

  public static Specification<Memoria> byPeticionEvaluacionActivo() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.activo), Boolean.TRUE);
    };
  }
}

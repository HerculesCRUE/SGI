package org.crue.hercules.sgi.eti.repository.specification;

import java.time.Instant;

import org.crue.hercules.sgi.eti.model.CargoComite_;
import org.crue.hercules.sgi.eti.model.Comite_;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Evaluador_;
import org.springframework.data.jpa.domain.Specification;

public class EvaluadorSpecifications {

  public static final String PRESIDENTE = "presidente";
  public static final String SECRETARIO = "secretario";

  public static Specification<Evaluador> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Evaluador_.activo), Boolean.TRUE);
    };
  }

  public static Specification<Evaluador> inactivos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Evaluador_.activo), Boolean.FALSE);
    };
  }

  public static Specification<Evaluador> presidentes() {
    return (root, query, cb) -> {
      return cb.equal(cb.lower(root.get(Evaluador_.cargoComite).get(CargoComite_.nombre)), PRESIDENTE);
    };
  }

  public static Specification<Evaluador> byPersonaRef(String personaRef) {
    return (root, query, cb) -> {
      return cb.equal(cb.lower(root.get(Evaluador_.personaRef)), personaRef);
    };
  }

  public static Specification<Evaluador> inFechas(Instant fechaAlta, Instant fechaBaja) {
    return (root, query, cb) -> {
      if (fechaBaja != null) {
        return cb.or(
            cb.and(cb.lessThan(root.get(Evaluador_.fechaAlta), fechaAlta),
                cb.greaterThan(root.get(Evaluador_.fechaBaja), fechaBaja)),
            cb.or(cb.between(root.get(Evaluador_.fechaAlta), fechaAlta, fechaBaja),
                cb.between(root.get(Evaluador_.fechaBaja), fechaAlta, fechaBaja)));
      } else {
        return cb.greaterThan(root.get(Evaluador_.fechaBaja), fechaAlta);
      }
    };
  }

  public static Specification<Evaluador> byFechaBajaNull() {
    return (root, query, cb) -> {
      return cb.isNull(root.get(Evaluador_.fechaBaja));
    };
  }

  public static Specification<Evaluador> byComite(String comite) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Evaluador_.comite).get(Comite_.comite), comite);
    };
  }

  public static Specification<Evaluador> secretarios() {
    return (root, query, cb) -> {
      return cb.equal(cb.lower(root.get(Evaluador_.cargoComite).get(CargoComite_.nombre)), SECRETARIO);
    };
  }

  public static Specification<Evaluador> between(Instant fecha) {
    return (root, query, cb) -> {
      return cb.and(cb.lessThan(root.get(Evaluador_.fechaAlta), fecha),
          cb.greaterThan(root.get(Evaluador_.fechaBaja), fecha));
    };
  }
}

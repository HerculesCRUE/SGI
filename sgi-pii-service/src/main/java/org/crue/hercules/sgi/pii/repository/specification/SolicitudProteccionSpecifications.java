package org.crue.hercules.sgi.pii.repository.specification;

import javax.persistence.criteria.Predicate;

import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.crue.hercules.sgi.pii.model.Invencion_;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion_;
import org.crue.hercules.sgi.pii.model.ViaProteccion_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SolicitudProteccionSpecifications {

  public static Specification<SolicitudProteccion> solicitudesByViaProteccion(Long solicitudProteccionId,
      Long idInvencion, Long idViaProteccion) {
    return (root, query, cb) -> {
      Predicate queryPredicate = cb.and(
          cb.equal(root.get(SolicitudProteccion_.viaProteccion).get(ViaProteccion_.id), idViaProteccion),
          cb.equal(root.get(SolicitudProteccion_.invencion).get(Invencion_.id), idInvencion),
          cb.equal(root.get(Activable_.activo), Boolean.TRUE));
      if (solicitudProteccionId != null) {
        return cb.and(queryPredicate, cb.notEqual(root.get(SolicitudProteccion_.id), solicitudProteccionId));
      }
      return queryPredicate;
    };
  }

  public static Specification<SolicitudProteccion> solicitudesByViaProteccionPaisEspecifico(Long solicitudProteccionId,
      Long idInvencion, Long idViaProteccion, String pais) {
    return (root, query, cb) -> cb.and(cb.equal(root.get(SolicitudProteccion_.paisProteccionRef), pais),
        SolicitudProteccionSpecifications
            .solicitudesByViaProteccion(solicitudProteccionId, idInvencion, idViaProteccion)
            .toPredicate(root, query, cb));
  }

  public static Specification<SolicitudProteccion> byInvencionId(Long invencionId) {
    return (root, query, cb) -> cb
        .and(cb.equal(root.get(SolicitudProteccion_.invencion).get(Invencion_.id), invencionId));
  }

}
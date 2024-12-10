package org.crue.hercules.sgi.csp.repository.specification;

import java.util.List;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.Grupo_;
import org.crue.hercules.sgi.csp.model.SolicitanteExterno;
import org.crue.hercules.sgi.csp.model.SolicitanteExterno_;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudRrhh;
import org.crue.hercules.sgi.csp.model.SolicitudRrhh_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class SolicitudSpecifications {

  public static final String PUBLIC_ID_SPLIT_DELIMITER = "@";

  /**
   * {@link Solicitud} activos.
   * 
   * @return specification para obtener las {@link Solicitud} activas.
   */
  public static Specification<Solicitud> activos() {
    return (root, query, cb) -> cb.isTrue(root.get(Solicitud_.activo));
  }

  /**
   * {@link Solicitud} con Activo a False
   * 
   * @return specification para obtener las {@link Solicitud} no activas
   */
  public static Specification<Solicitud> notActivos() {
    return (root, query, cb) -> cb.isFalse(root.get(Solicitud_.activo));
  }

  /**
   * {@link Solicitud} con un unidadGestionRef incluido en la lista.
   * 
   * @param unidadGestionRefs lista de unidadGestionRefs
   * @return specification para obtener los {@link Convocatoria} cuyo
   *         unidadGestionRef se encuentre entre los recibidos.
   */
  public static Specification<Solicitud> unidadGestionRefIn(List<String> unidadGestionRefs) {
    return (root, query, cb) -> root.get(Solicitud_.unidadGestionRef).in(unidadGestionRefs);
  }

  /**
   * {@link Solicitud} en las que la persona es el solicitante.
   * 
   * @param personaRef referencia de la persona
   * @return specification para obtener las {@link Solicitud} en las que la
   *         persona es el solicitante.
   */
  public static Specification<Solicitud> bySolicitante(String personaRef) {
    return (root, query, cb) -> cb.equal(root.get(Solicitud_.solicitanteRef), personaRef);
  }

  /**
   * {@link Solicitud} por id
   * 
   * @param id identificador de la {@link Solicitud}
   * @return specification para obtener las {@link Solicitud} por id.
   */
  public static Specification<Solicitud> byId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(Solicitud_.id), id);
  }

  /**
   * {@link Solicitud} del {@link Grupo} con el id indicado.
   * 
   * @param grupoId identificador de la {@link Grupo}.
   * @return specification para obtener las {@link Convocatoria} de
   *         la {@link Grupo} con el id indicado.
   */
  public static Specification<Solicitud> byGrupoId(Long grupoId) {
    return (root, query, cb) -> {
      Subquery<Long> queryGrupo = query.subquery(Long.class);
      Root<Grupo> queryGrupoRoot = queryGrupo.from(Grupo.class);
      queryGrupo.select(queryGrupoRoot.get(Grupo_.solicitud).get(Solicitud_.id))
          .where(cb.equal(queryGrupoRoot.get(Grupo_.id), grupoId));
      return root.get(Solicitud_.id).in(queryGrupo);
    };
  }

  /**
   * {@link Solicitud} en las que la persona es el tutor.
   * 
   * @param personaRef referencia de la persona
   * @return specification para obtener las {@link Solicitud} en las que la
   *         persona es el tutor.
   */
  public static Specification<Solicitud> byTutor(String personaRef) {
    return (root, query, cb) -> {
      Subquery<Long> queryTutor = query.subquery(Long.class);
      Root<SolicitudRrhh> queryTutorRoot = queryTutor.from(SolicitudRrhh.class);
      queryTutor.select(queryTutorRoot.get(SolicitudRrhh_.solicitud).get(Solicitud_.id))
          .where(cb.equal(queryTutorRoot.get(SolicitudRrhh_.tutorRef), personaRef));
      return root.get(Solicitud_.id).in(queryTutor);
    };
  }

  /**
   * {@link Solicitud} en las que la persona es el solicitante o el tutor.
   * 
   * @param personaRef referencia de la persona
   * @return specification para obtener las {@link Solicitud} en las que la
   *         persona es el solicitante o el tutor.
   */
  public static Specification<Solicitud> bySolicitanteOrTutor(String personaRef) {
    return bySolicitante(personaRef).or(byTutor(personaRef));
  }

  /**
   * {@link Solicitud} por id
   * 
   * @param publicId identificador de la {@link Solicitud}
   * @return specification para obtener las {@link Solicitud} por id.
   */
  public static Specification<Solicitud> byPublicId(String publicId) {
    String[] publicIdArray = publicId.split(PUBLIC_ID_SPLIT_DELIMITER);
    String codigoRegistroInterno = publicIdArray[0];
    String numeroDocumentoSolicitante = publicIdArray[1];

    return (root, query, cb) -> {
      Subquery<Long> querySolicitanteExterno = query.subquery(Long.class);
      Root<SolicitanteExterno> querySolicitanteExternoRoot = querySolicitanteExterno.from(SolicitanteExterno.class);
      querySolicitanteExterno.select(querySolicitanteExternoRoot.get(SolicitanteExterno_.solicitudId))
          .where(cb.equal(querySolicitanteExternoRoot.get(SolicitanteExterno_.numeroDocumento),
              numeroDocumentoSolicitante));

      return cb.and(
          root.get(Solicitud_.id).in(querySolicitanteExterno),
          cb.equal(root.get(Solicitud_.codigoRegistroInterno), codigoRegistroInterno));
    };
  }

}

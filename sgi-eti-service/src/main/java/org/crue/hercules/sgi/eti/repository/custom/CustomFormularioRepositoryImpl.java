package org.crue.hercules.sgi.eti.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.eti.model.Comite_;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomFormularioRepositoryImpl implements CustomFormularioRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Formulario findByMemoriaId(Long idMemoria) {
    log.debug("findByMemoriaId(Long idMemoria) - start");
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Formulario> cq = cb.createQuery(Formulario.class);
    Root<Memoria> root = cq.from(Memoria.class);

    cq.select(root.get(Memoria_.comite).get(Comite_.formulario));
    cq.where(cb.equal(root.get(Memoria_.id), idMemoria));

    TypedQuery<Formulario> typedQuery = entityManager.createQuery(cq);

    Formulario response = typedQuery.getSingleResult();
    log.debug("findByMemoriaId(Long idMemoria) - end");
    return response;
  }
}

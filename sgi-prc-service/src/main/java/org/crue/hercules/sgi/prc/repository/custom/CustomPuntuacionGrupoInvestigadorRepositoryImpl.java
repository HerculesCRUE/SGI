package org.crue.hercules.sgi.prc.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador_;
import org.crue.hercules.sgi.prc.repository.PuntuacionGrupoInvestigadorRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para
 * {@link PuntuacionGrupoInvestigadorRepository}.
 */
@Slf4j
@Component
public class CustomPuntuacionGrupoInvestigadorRepositoryImpl implements CustomPuntuacionGrupoInvestigadorRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByPuntuacionGrupoId(Long puntuacionGrupoId) {
    log.debug("deleteInBulkByAutorId(long autorId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<PuntuacionGrupoInvestigador> query = cb.createCriteriaDelete(PuntuacionGrupoInvestigador.class);

    // Define FROM PuntuacionGrupoInvestigador clause
    Root<PuntuacionGrupoInvestigador> root = query.from(PuntuacionGrupoInvestigador.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(PuntuacionGrupoInvestigador_.puntuacionGrupoId), puntuacionGrupoId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByAutorId(long autorId) - end");
    return returnValue;
  }
}

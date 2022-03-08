package org.crue.hercules.sgi.prc.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador_;
import org.crue.hercules.sgi.prc.repository.PuntuacionItemInvestigadorRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link PuntuacionItemInvestigadorRepository}.
 */
@Slf4j
@Component
public class CustomPuntuacionItemInvestigadorRepositoryImpl implements CustomPuntuacionItemInvestigadorRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public int deleteInBulkByProduccionCientificaId(long produccionCientificaId) {
    log.debug("deleteInBulkByProduccionCientificaId(long produccionCientificaId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<PuntuacionItemInvestigador> query = cb.createCriteriaDelete(PuntuacionItemInvestigador.class);

    // Define FROM PuntuacionItemInvestigador clause
    Root<PuntuacionItemInvestigador> root = query.from(PuntuacionItemInvestigador.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(PuntuacionItemInvestigador_.produccionCientificaId), produccionCientificaId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByProduccionCientificaId(long produccionCientificaId) - end");
    return returnValue;
  }
}

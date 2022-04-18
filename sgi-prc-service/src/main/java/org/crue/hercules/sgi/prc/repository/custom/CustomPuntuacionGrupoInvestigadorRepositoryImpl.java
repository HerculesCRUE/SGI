package org.crue.hercules.sgi.prc.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionGrupoInvestigador_;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador;
import org.crue.hercules.sgi.prc.model.PuntuacionItemInvestigador_;
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

  /**
   * Modifica el campo puntuacionItemInvestigador a null de
   * {@link PuntuacionGrupoInvestigador}
   * 
   * @param produccionCientificaId el identificador de la
   *                               {@link PuntuacionItemInvestigador}
   * @return el número de registros eliminados
   */
  @Override
  public int updatePuntuacionItemInvestigadorNull(Long produccionCientificaId) {
    log.debug("updatePuntuacionItemInvestigadorNull(produccionCientificaId) : {} - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaUpdate<PuntuacionGrupoInvestigador> update = cb.createCriteriaUpdate(PuntuacionGrupoInvestigador.class);

    // Define FROM IndiceImpacto clause
    Root<PuntuacionGrupoInvestigador> root = update.from(PuntuacionGrupoInvestigador.class);

    Subquery<PuntuacionGrupoInvestigador> subquery = update.subquery(PuntuacionGrupoInvestigador.class);
    Root<PuntuacionGrupoInvestigador> rootSubquery = subquery.from(PuntuacionGrupoInvestigador.class);
    subquery.select(rootSubquery);
    /* below are narrowing criteria, based on root2 */
    Join<PuntuacionGrupoInvestigador, PuntuacionItemInvestigador> join = rootSubquery
        .join(PuntuacionGrupoInvestigador_.puntuacionItemInvestigador);
    subquery.where(cb.equal(join.get(PuntuacionItemInvestigador_.produccionCientificaId), produccionCientificaId));

    update.set(PuntuacionGrupoInvestigador_.PUNTUACION_ITEM_INVESTIGADOR, null);

    // Set WHERE restrictions
    update.where(root.in(subquery));

    // Execute query
    int result = entityManager.createQuery(update).executeUpdate();

    log.debug("updatePuntuacionItemInvestigadorNull(produccionCientificaId) : {} - start");

    return result;
  }
}

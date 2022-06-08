package org.crue.hercules.sgi.prc.repository.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.Baremo_;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo_;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link CustomBaremoRepository}.
 */
@Slf4j
@Component
public class CustomBaremoRepositoryImpl implements CustomBaremoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Retorna una lista de {@link EpigrafeCVN} cuyo tipo de fuente es CVN o
   * CVN_OTRO_SISTEMA de la convocatoria de baremación del último año
   * 
   * @param convocatoriaBaremacionId Id de {@link ConvocatoriaBaremacion} del
   *                                 último año
   * @return Lista de {@link EpigrafeCVN}
   */
  @Override
  public List<EpigrafeCVN> findDistinctEpigrafesCVNByConvocatoriaBaremacionId(Long convocatoriaBaremacionId) {
    List<EpigrafeCVN> result = null;
    log.debug("findDistinctEpigrafesCVNByConvocatoriaBaremacionId(convocatoriaBaremacionId) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<EpigrafeCVN> cq = cb.createQuery(EpigrafeCVN.class);
    Root<Baremo> root = cq.from(Baremo.class);
    Join<Baremo, ConfiguracionBaremo> joinConfiguracionBaremo = root.join(Baremo_.configuracionBaremo);

    List<Predicate> listPredicates = new ArrayList<>();

    listPredicates.add(cb.equal(root.get(Baremo_.convocatoriaBaremacionId), (convocatoriaBaremacionId)));

    listPredicates.add(cb.isTrue(joinConfiguracionBaremo.get(Activable_.activo)));

    listPredicates.add(cb.and(joinConfiguracionBaremo.get(ConfiguracionBaremo_.tipoFuente)
        .in(Arrays.asList(ConfiguracionBaremo.TipoFuente.CVN, ConfiguracionBaremo.TipoFuente.CVN_OTRO_SISTEMA))));

    cq.select(joinConfiguracionBaremo.get(ConfiguracionBaremo_.epigrafeCVN)).distinct(true)
        .where(listPredicates.toArray(new Predicate[] {}));

    // Order
    cq.orderBy(cb.asc(root.get(Baremo_.configuracionBaremo).get(ConfiguracionBaremo_.EPIGRAFE_CV_N)));

    result = entityManager.createQuery(cq).getResultList();

    log.debug("findDistinctEpigrafesCVNByConvocatoriaBaremacionId(convocatoriaBaremacionId) - end");
    return result;
  }

  @Override
  public int deleteInBulkByConvocatoriaBaremacionId(long convocatoriaBaremacionId) {
    log.debug("deleteInBulkByConvocatoriaBaremacionId(long convocatoriaBaremacionId) - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaDelete<Baremo> query = cb.createCriteriaDelete(Baremo.class);

    // Define FROM Baremo clause
    Root<Baremo> root = query.from(Baremo.class);

    // Set WHERE restrictions
    query.where(cb.equal(root.get(Baremo_.convocatoriaBaremacionId), convocatoriaBaremacionId));

    // Execute query
    int returnValue = entityManager.createQuery(query).executeUpdate();

    log.debug("deleteInBulkByConvocatoriaBaremacionId(long convocatoriaBaremacionId) - end");
    return returnValue;
  }
}

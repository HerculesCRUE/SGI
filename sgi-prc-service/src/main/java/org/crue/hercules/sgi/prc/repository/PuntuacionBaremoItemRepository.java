package org.crue.hercules.sgi.prc.repository;

import java.util.List;

import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem;
import org.crue.hercules.sgi.prc.repository.custom.CustomPuntuacionBaremoItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link PuntuacionBaremoItem}.
 */

@Repository
public interface PuntuacionBaremoItemRepository
    extends JpaRepository<PuntuacionBaremoItem, Long>, JpaSpecificationExecutor<PuntuacionBaremoItem>,
    CustomPuntuacionBaremoItemRepository {

  List<PuntuacionBaremoItem> findByBaremoConvocatoriaBaremacionId(Long convocatoriaBaremacionId);

}

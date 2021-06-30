package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConvocatoriaConceptoGastoCodigoEcRepository
    extends JpaRepository<ConvocatoriaConceptoGastoCodigoEc, Long>,
    JpaSpecificationExecutor<ConvocatoriaConceptoGastoCodigoEc> {

  /**
   * Se obtienen los códigos económicos de una {@link ConvocatoriaConceptoGasto}
   * 
   * @param idConvocatoriaConceptoGasto identificador
   *                                    {@link ConvocatoriaConceptoGasto}
   * @return listado {@link ConvocatoriaConceptoGastoCodigoEc}
   */
  List<ConvocatoriaConceptoGastoCodigoEc> findAllByConvocatoriaConceptoGastoId(Long idConvocatoriaConceptoGasto);

  /**
   * Comprueba la existencia del {@link ConvocatoriaConceptoGastoCodigoEc} por id
   * de {@link ConvocatoriaConceptoGasto}
   *
   * @param id el id de la entidad {@link ConvocatoriaConceptoGasto}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsByConvocatoriaConceptoGastoId(Long id);

}

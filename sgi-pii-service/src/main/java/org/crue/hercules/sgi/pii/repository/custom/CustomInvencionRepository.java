package org.crue.hercules.sgi.pii.repository.custom;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.pii.dto.InvencionDto;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.springframework.data.jpa.domain.Specification;

public interface CustomInvencionRepository {

  /**
   * Devuelve una lista de {@link InvencionDto} que se incorporarán a la
   * baremación
   * de producción científica
   * 
   * @param fechaInicioBaremacion fecha inicio de baremación
   * @param fechaFinBaremacion    fecha fin de baremación
   * @param universidadId         id universidad
   * 
   * @return Lista de {@link InvencionDto}
   */
  List<InvencionDto> findInvencionesProduccionCientifica(Instant fechaInicioBaremacion,
      Instant fechaFinBaremacion, String universidadId);

  /**
   * Obtiene los ids de {@link Invencion} que cumplen con la specification
   * recibida.
   * 
   * @param specification condiciones que deben cumplir.
   * @return lista de ids de {@link Invencion}.
   */
  List<Long> findIds(Specification<Invencion> specification);

}

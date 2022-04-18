package org.crue.hercules.sgi.prc.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.repository.custom.CustomActividadRepository;
import org.crue.hercules.sgi.prc.repository.custom.CustomComiteEditorialRepository;
import org.crue.hercules.sgi.prc.repository.custom.CustomCongresoRepository;
import org.crue.hercules.sgi.prc.repository.custom.CustomDireccionTesisRepository;
import org.crue.hercules.sgi.prc.repository.custom.CustomObraArtisticaRepository;
import org.crue.hercules.sgi.prc.repository.custom.CustomProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.custom.CustomPublicacionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ProduccionCientifica}.
 */

@Repository
public interface ProduccionCientificaRepository
    extends JpaRepository<ProduccionCientifica, Long>, JpaSpecificationExecutor<ProduccionCientifica>,
    CustomProduccionCientificaRepository, CustomPublicacionRepository, CustomComiteEditorialRepository,
    CustomCongresoRepository, CustomObraArtisticaRepository, CustomActividadRepository, CustomDireccionTesisRepository {

  /**
   * Obtiene la entidad {@link ProduccionCientifica} con el
   * produccionCientificaRef indicado y ConvocatoriaBaremacionId es nulo
   *
   * @param produccionCientificaRef el produccionCientificaRef de
   *                                {@link ProduccionCientifica}.
   * @return el {@link ProduccionCientifica} con el produccionCientificaRef
   *         indicado
   */
  Optional<ProduccionCientifica> findByProduccionCientificaRefAndConvocatoriaBaremacionIdIsNull(
      String produccionCientificaRef);

  /**
   * Obtiene la entidad {@link ProduccionCientifica} con el
   * produccionCientificaRef indicado y ConvocatoriaBaremacionId
   *
   * @param produccionCientificaRef  el produccionCientificaRef de
   *                                 {@link ProduccionCientifica}.
   * @param convocatoriaBaremacionId Id de {@link ConvocatoriaBaremacion}
   * 
   * @return el {@link ProduccionCientifica} con el produccionCientificaRef
   *         indicado
   */
  Optional<ProduccionCientifica> findByProduccionCientificaRefAndConvocatoriaBaremacionId(
      String produccionCientificaRef, Long convocatoriaBaremacionId);

  /**
   * Obtiene las entidades {@link ProduccionCientifica} con el
   * ConvocatoriaBaremacionId indicado
   * 
   * @param convocatoriaBaremacionId Id de {@link ConvocatoriaBaremacion}
   * @return lista de {@link ProduccionCientifica}
   */
  List<ProduccionCientifica> findByConvocatoriaBaremacionId(Long convocatoriaBaremacionId);

  /**
   * Obtiene las {@link ProduccionCientifica} con el {@link EpigrafeCVN}
   * y ConvocatoriaBaremacionId indicado
   * 
   * @param epigrafeCVN              Id de {@link EpigrafeCVN}
   * @param convocatoriaBaremacionId Id de
   *                                 {@link ConvocatoriaBaremacion}
   * @return lista de {@link ProduccionCientifica}
   */
  List<ProduccionCientifica> findByEpigrafeCVNAndConvocatoriaBaremacionId(EpigrafeCVN epigrafeCVN,
      Long convocatoriaBaremacionId);

  /**
   * Obtiene las {@link ProduccionCientifica} con el {@link EpigrafeCVN}
   * y ConvocatoriaBaremacionId es nulo
   * 
   * @param epigrafeCVN Id de {@link EpigrafeCVN}
   * @return lista de {@link ProduccionCientifica}
   */
  List<ProduccionCientifica> findByEpigrafeCVNAndConvocatoriaBaremacionIdIsNull(EpigrafeCVN epigrafeCVN);

}

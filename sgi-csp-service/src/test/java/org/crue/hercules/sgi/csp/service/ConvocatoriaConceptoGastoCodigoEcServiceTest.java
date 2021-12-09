package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.time.Period;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaConceptoGastoCodigoEcNotFoundException;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaConceptoGastoCodigoEcServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;

/**
 * ConvocatoriaConceptoGastoCodigoEcServiceTest
 */
public class ConvocatoriaConceptoGastoCodigoEcServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaConceptoGastoCodigoEcRepository repository;
  @Mock
  private ConvocatoriaConceptoGastoRepository convocatoriaConceptoGastoRepository;
  @Mock
  private ConvocatoriaService convocatoriaService;
  @Mock
  ConvocatoriaRepository convocatoriaRepository;
  @Mock
  ConfiguracionSolicitudRepository configuracionSolicitudRepository;

  private ConvocatoriaConceptoGastoCodigoEcService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaConceptoGastoCodigoEcServiceImpl(repository, convocatoriaConceptoGastoRepository,
        convocatoriaService, convocatoriaRepository, configuracionSolicitudRepository);
  }

  @Test
  public void findById_ReturnsConvocatoriaConceptoGastoCodigoEc() {
    // given: Un ConvocatoriaConceptoGastoCodigoEc con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockConvocatoriaConceptoGastoCodigoEc(idBuscado)));

    // when: Buscamos el ConvocatoriaConceptoGastoCodigoEc por su id
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = service.findById(idBuscado);

    // then: el ConvocatoriaConceptoGastoCodigoEc
    Assertions.assertThat(convocatoriaConceptoGastoCodigoEc).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaConceptoGastoCodigoEc.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(convocatoriaConceptoGastoCodigoEc.getCodigoEconomicoRef()).as("getCodigoEconomicoRef()")
        .isEqualTo("Cod-1");
    Assertions.assertThat(convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGastoId())
        .as("getConvocatoriaConceptoGastoId()").isEqualTo(1L);

  }

  @Test
  public void findById_WithIdNotExist_ThrowsConvocatoriaConceptoGastoCodigoEcNotFoundException() throws Exception {
    // given: Ningun ConvocatoriaConceptoGastoCodigoEc con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ConvocatoriaConceptoGastoCodigoEc por su id
    // then: lanza un ConvocatoriaConceptoGastoCodigoEcNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ConvocatoriaConceptoGastoCodigoEcNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaConceptoGastoCodigoEc
   * 
   * @param id     id del ConvocatoriaConceptoGastoCodigoEc
   * @param nombre nombre del ConvocatoriaConceptoGastoCodigoEc
   * @return el objeto ConvocatoriaConceptoGastoCodigoEc
   */
  private ConvocatoriaConceptoGastoCodigoEc generarMockConvocatoriaConceptoGastoCodigoEc(Long id) {
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = new ConvocatoriaConceptoGastoCodigoEc();
    convocatoriaConceptoGastoCodigoEc.setId(id);
    convocatoriaConceptoGastoCodigoEc.setCodigoEconomicoRef("Cod-" + (id == null ? 1 : id));
    convocatoriaConceptoGastoCodigoEc.setConvocatoriaConceptoGastoId(id == null ? 1 : id);
    convocatoriaConceptoGastoCodigoEc.setFechaInicio(Instant.now().minus(Period.ofDays(1)));
    convocatoriaConceptoGastoCodigoEc.setFechaFin(Instant.now());

    return convocatoriaConceptoGastoCodigoEc;
  }

}

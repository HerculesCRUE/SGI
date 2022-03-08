package org.crue.hercules.sgi.prc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.ConfiguracionCampoNotFoundException;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo.TipoFormato;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica.EpigrafeCVN;
import org.crue.hercules.sgi.prc.repository.ConfiguracionCampoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ConfiguracionCampoServiceTest
 */
@Import({ ConfiguracionCampoService.class, ApplicationContextSupport.class })
public class ConfiguracionCampoServiceTest extends BaseServiceTest {

  private static final CodigoCVN DEFAULT_DATA_CODIGO_CVN = CodigoCVN.COLECTIVA;
  private static final TipoFormato DEFAULT_DATA_TIPO_FORMATO = TipoFormato.TEXTO;
  private static final Boolean DEFAULT_DATA_FECHA_REFERENCIA_INICIO = Boolean.TRUE;
  private static final Boolean DEFAULT_DATA_FECHA_REFERENCIA_FIN = Boolean.TRUE;
  private static final EpigrafeCVN DEFAULT_DATA_EPIGRAFE_CVN = EpigrafeCVN.E030_040_000_000;
  private static final Boolean DEFAULT_DATA_VALIDACION_ADICIONAL = Boolean.FALSE;

  @MockBean
  private ConfiguracionCampoRepository repository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private ConfiguracionCampoService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 ConfiguracionCampo
    List<ConfiguracionCampo> configuracionesCampo = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      configuracionesCampo.add(generarMockConfiguracionCampo(i));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ConfiguracionCampo>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConfiguracionCampo>>() {
          @Override
          public Page<ConfiguracionCampo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > configuracionesCampo.size() ? configuracionesCampo.size() : toIndex;
            List<ConfiguracionCampo> content = configuracionesCampo.subList(fromIndex, toIndex);
            Page<ConfiguracionCampo> page = new PageImpl<>(content, pageable,
                configuracionesCampo.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConfiguracionCampo> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los ConfiguracionCampo del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConfiguracionCampo configuracionCampo = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(configuracionCampo.getCodigoCVN()).as("getCodigoCVN")
          .isEqualTo(DEFAULT_DATA_CODIGO_CVN);
    }
  }

  @Test
  public void findById_ReturnsConfiguracionCampo() {
    // given: ConfiguracionCampo con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockConfiguracionCampo(idBuscado)));

    // when: Buscamos ConfiguracionCampo por su id
    ConfiguracionCampo configuracionCampo = service.findById(idBuscado);

    // then: el ConfiguracionCampo
    Assertions.assertThat(configuracionCampo).as("isNotNull()").isNotNull();
    Assertions.assertThat(configuracionCampo.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(configuracionCampo.getCodigoCVN()).as("getCodigoCVN")
        .isEqualTo(DEFAULT_DATA_CODIGO_CVN);
    Assertions.assertThat(configuracionCampo.getEpigrafeCVN()).as("getEpigrafeCVN")
        .isEqualTo(DEFAULT_DATA_EPIGRAFE_CVN);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsConfiguracionCampoNotFoundException() {
    // given: Ningun ConfiguracionCampo con el id buscado
    Long idBuscado = 33L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Buscamos el ConfiguracionCampo por su id
    // then: Lanza un ConfiguracionCampoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ConfiguracionCampoNotFoundException.class);
  }

  private ConfiguracionCampo generarMockConfiguracionCampo(Long id) {
    return this.generarMockConfiguracionCampo(
        id, DEFAULT_DATA_CODIGO_CVN, DEFAULT_DATA_EPIGRAFE_CVN,
        DEFAULT_DATA_FECHA_REFERENCIA_FIN, DEFAULT_DATA_FECHA_REFERENCIA_INICIO,
        DEFAULT_DATA_TIPO_FORMATO, DEFAULT_DATA_VALIDACION_ADICIONAL);
  }

  private ConfiguracionCampo generarMockConfiguracionCampo(
      Long id, CodigoCVN codigoCVN, EpigrafeCVN epigrafeCVN,
      Boolean fechaReferenciaFin, Boolean fechaReferenciaInicio,
      TipoFormato tipoFormato, Boolean validacionAdicional) {
    return ConfiguracionCampo.builder()
        .id(id)
        .codigoCVN(codigoCVN)
        .epigrafeCVN(epigrafeCVN)
        .fechaReferenciaFin(fechaReferenciaFin)
        .fechaReferenciaInicio(fechaReferenciaInicio)
        .tipoFormato(tipoFormato)
        .validacionAdicional(validacionAdicional)
        .build();
  }
}

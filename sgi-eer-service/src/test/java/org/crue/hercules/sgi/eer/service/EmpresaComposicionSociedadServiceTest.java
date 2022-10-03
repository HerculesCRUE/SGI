package org.crue.hercules.sgi.eer.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eer.exceptions.EmpresaComposicionSociedadNotFoundException;
import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad;
import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad.TipoAportacion;
import org.crue.hercules.sgi.eer.repository.EmpresaComposicionSociedadRepository;
import org.crue.hercules.sgi.eer.repository.EmpresaRepository;
import org.crue.hercules.sgi.eer.util.EmpresaAuthorityHelper;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

/**
 * EmpresaComposicionSociedadServiceTest
 */
@Import({ EmpresaComposicionSociedadService.class, ApplicationContextSupport.class })
class EmpresaComposicionSociedadServiceTest extends BaseServiceTest {

  @MockBean
  private EmpresaComposicionSociedadRepository empresaEquipoRepository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @Autowired
  private EmpresaComposicionSociedadService empresaEquipoService;

  @MockBean
  private EmpresaRepository empresaRepository;

  @MockBean
  private EmpresaAuthorityHelper authorityHelper;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void findById_WithId_ReturnsEmpresaComposicionSociedad() {
    BDDMockito.given(empresaEquipoRepository.findById(1L))
        .willReturn(Optional.of(generarMockEmpresaComposicionSociedad(1L)));

    EmpresaComposicionSociedad empresa = empresaEquipoService.findById(1L);
    Assertions.assertThat(empresa.getId()).isEqualTo(1L);
    Assertions.assertThat(empresa.getMiembroSociedadEmpresaRef()).isEqualTo("miembroSociedadEmpresaRef");

  }

  @Test
  void find_NotFound_ThrowsEmpresaComposicionSociedadNotFoundException() throws Exception {
    BDDMockito.given(empresaEquipoRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> empresaEquipoService.findById(1L))
        .isInstanceOf(EmpresaComposicionSociedadNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto EmpresaComposicionSociedad
   * 
   * @param id id dla EmpresaComposicionSociedad
   * @return el objeto EmpresaComposicionSociedad
   */
  public EmpresaComposicionSociedad generarMockEmpresaComposicionSociedad(Long id) {
    EmpresaComposicionSociedad empresa = new EmpresaComposicionSociedad();
    empresa.setId(id);
    empresa.setEmpresaId(1L);
    empresa.setMiembroSociedadEmpresaRef("miembroSociedadEmpresaRef");
    empresa.setFechaInicio(Instant.now());
    empresa.setParticipacion(new BigDecimal(30));
    empresa.setTipoAportacion(TipoAportacion.DINERARIA);
    return empresa;
  }

}

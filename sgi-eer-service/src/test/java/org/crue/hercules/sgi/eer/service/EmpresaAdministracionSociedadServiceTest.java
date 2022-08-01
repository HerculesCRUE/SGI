package org.crue.hercules.sgi.eer.service;

import java.time.Instant;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eer.exceptions.EmpresaAdministracionSociedadNotFoundException;
import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad;
import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad.TipoAdministracion;
import org.crue.hercules.sgi.eer.repository.EmpresaAdministracionSociedadRepository;
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
 * EmpresaAdministracionSociedadServiceTest
 */
@Import({ EmpresaAdministracionSociedadService.class, ApplicationContextSupport.class })
public class EmpresaAdministracionSociedadServiceTest extends BaseServiceTest {

  @MockBean
  private EmpresaAdministracionSociedadRepository empresaAdministracionSociedadRepository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @Autowired
  private EmpresaAdministracionSociedadService empresaEquipoService;

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
  public void findById_WithId_ReturnsEmpresaAdministracionSociedad() {
    BDDMockito.given(empresaAdministracionSociedadRepository.findById(1L))
        .willReturn(Optional.of(generarMockEmpresaAdministracionSociedad(1L)));

    EmpresaAdministracionSociedad empresa = empresaEquipoService.findById(1L);
    Assertions.assertThat(empresa.getId()).isEqualTo(1L);
    Assertions.assertThat(empresa.getMiembroEquipoAdministracionRef()).isEqualTo("miembroEquipoAdministracionRef");

  }

  @Test
  public void find_NotFound_ThrowsEmpresaAdministracionSociedadNotFoundException() throws Exception {
    BDDMockito.given(empresaAdministracionSociedadRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> empresaEquipoService.findById(1L))
        .isInstanceOf(EmpresaAdministracionSociedadNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto EmpresaAdministracionSociedad
   * 
   * @param id id dla EmpresaAdministracionSociedad
   * @return el objeto EmpresaAdministracionSociedad
   */
  public EmpresaAdministracionSociedad generarMockEmpresaAdministracionSociedad(Long id) {
    EmpresaAdministracionSociedad empresa = new EmpresaAdministracionSociedad();
    empresa.setId(id);
    empresa.setEmpresaId(1L);
    empresa.setMiembroEquipoAdministracionRef("miembroEquipoAdministracionRef");
    empresa.setFechaInicio(Instant.now());
    empresa.setTipoAdministracion(TipoAdministracion.ADMINISTRADOR_MANCOMUNADO);
    return empresa;
  }

}

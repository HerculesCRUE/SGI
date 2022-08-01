package org.crue.hercules.sgi.eer.service;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eer.exceptions.EmpresaEquipoEmprendedorNotFoundException;
import org.crue.hercules.sgi.eer.model.EmpresaEquipoEmprendedor;
import org.crue.hercules.sgi.eer.repository.EmpresaEquipoEmprendedorRepository;
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
 * EmpresaEquipoEmprendedorServiceTest
 */
@Import({ EmpresaEquipoEmprendedorService.class, ApplicationContextSupport.class })
public class EmpresaEquipoEmprendedorServiceTest extends BaseServiceTest {

  @MockBean
  private EmpresaEquipoEmprendedorRepository empresaEquipoRepository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @Autowired
  private EmpresaEquipoEmprendedorService empresaEquipoService;

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
  public void findById_WithId_ReturnsEmpresaEquipoEmprendedor() {
    BDDMockito.given(empresaEquipoRepository.findById(1L))
        .willReturn(Optional.of(generarMockEmpresaEquipoEmprendedor(1L)));

    EmpresaEquipoEmprendedor empresa = empresaEquipoService.findById(1L);
    Assertions.assertThat(empresa.getId()).isEqualTo(1L);
    Assertions.assertThat(empresa.getMiembroEquipoRef()).isEqualTo("miembroEquipoRef");

  }

  @Test
  public void find_NotFound_ThrowsEmpresaEquipoEmprendedorNotFoundException() throws Exception {
    BDDMockito.given(empresaEquipoRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> empresaEquipoService.findById(1L))
        .isInstanceOf(EmpresaEquipoEmprendedorNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto EmpresaEquipoEmprendedor
   * 
   * @param id id dla EmpresaEquipoEmprendedor
   * @return el objeto EmpresaEquipoEmprendedor
   */
  public EmpresaEquipoEmprendedor generarMockEmpresaEquipoEmprendedor(Long id) {
    EmpresaEquipoEmprendedor empresa = new EmpresaEquipoEmprendedor();
    empresa.setId(id);
    empresa.setEmpresaId(1L);
    empresa.setMiembroEquipoRef("miembroEquipoRef");
    return empresa;
  }

}

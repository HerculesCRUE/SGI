package org.crue.hercules.sgi.eer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.validation.ValidationException;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eer.exceptions.EmpresaNotFoundException;
import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.repository.EmpresaRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * EmpresaServiceTest
 */
@Import({ EmpresaService.class, ApplicationContextSupport.class })
class EmpresaServiceTest extends BaseServiceTest {

  @MockBean
  private EmpresaRepository empresaRepository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @Autowired
  private EmpresaService empresaService;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void findById_WithId_ReturnsEmpresa() {
    BDDMockito.given(empresaRepository.findById(1L))
        .willReturn(Optional.of(generarMockEmpresa(1L, "Empresa1")));

    Empresa empresa = empresaService.findById(1L);
    Assertions.assertThat(empresa.getId()).isEqualTo(1L);
    Assertions.assertThat(empresa.getActivo()).isEqualTo(Boolean.TRUE);
    Assertions.assertThat(empresa.getNombreRazonSocial()).isEqualTo("Empresa1");

  }

  @Test
  void find_NotFound_ThrowsEmpresaNotFoundException() throws Exception {
    BDDMockito.given(empresaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> empresaService.findById(1L))
        .isInstanceOf(EmpresaNotFoundException.class);
  }

  @Test
  void create_ReturnsEmpresa() {
    // given: Un nuevo Empresa
    Empresa empresa = generarMockEmpresa(null);

    mockActivableIsActivo(Empresa.class, null);

    BDDMockito.given(empresaRepository.save(empresa)).will((InvocationOnMock invocation) -> {
      Empresa empresaCreado = invocation.getArgument(0);
      empresaCreado.setId(1L);
      return empresaCreado;
    });

    // when: Creamos la Empresa
    Empresa empresaCreado = empresaService.create(empresa);

    // then: La Empresa se crea correctamente
    Assertions.assertThat(empresaCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(empresaCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(empresaCreado.getNombreRazonSocial()).as("getNombreRazonSocial")
        .isEqualTo(empresa.getNombreRazonSocial());
    Assertions.assertThat(empresaCreado.getActivo()).as("getActivo")
        .isEqualTo(empresa.getActivo());

  }

  @Test
  void update_ReturnsEmpresa() {
    // given: Un nuevo tipo Fase con el servicio actualizado
    Empresa empresaServicioActualizada = generarMockEmpresa(1L,
        "Empresa1 actualizada");

    Empresa empresa = generarMockEmpresa(1L, "Empresa1");

    mockActivableIsActivo(Empresa.class, empresa);

    BDDMockito.given(empresaRepository.findById(1L)).willReturn(Optional.of(empresa));
    BDDMockito.given(empresaRepository.save(empresa))
        .willReturn(empresaServicioActualizada);

    // when: Actualizamos el tipo Fase
    Empresa empresaActualizada = empresaService.update(empresa);

    // then: El tipo Fase se actualiza correctamente.
    Assertions.assertThat(empresaActualizada.getId()).isEqualTo(1L);
    Assertions.assertThat(empresaActualizada.getNombreRazonSocial()).isEqualTo("Empresa1 actualizada");

  }

  @Test
  void update_NoActivaEmpresa_ThrowsValidationException() {
    // given: Un Empresa actualizado
    Long id = 1L;
    Empresa fuenteFinanciacion = generarMockEmpresa(id);

    mockActivableIsActivo(Empresa.class, null);
    BDDMockito.given(entityManager.find(Empresa.class, id)).willReturn(null);

    // when: Actualizamos la Empresa
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> empresaService.update(fuenteFinanciacion))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void disable_ReturnsEmpresa() {
    // given: Un nuevo Empresa activo
    Empresa empresa = generarMockEmpresa(1L);

    BDDMockito.given(empresaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(empresa));
    BDDMockito.given(empresaRepository.save(ArgumentMatchers.<Empresa>any()))
        .willAnswer(new Answer<Empresa>() {
          @Override
          public Empresa answer(InvocationOnMock invocation) throws Throwable {
            Empresa givenData = invocation.getArgument(0, Empresa.class);
            givenData.setActivo(Boolean.FALSE);
            return givenData;
          }
        });

    // when: Desactivamos la Empresa
    Empresa empresaActualizada = empresaService.desactivar(empresa.getId());

    // then: La Empresa se desactiva correctamente.
    Assertions.assertThat(empresaActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(empresaActualizada.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(empresaActualizada.getNombreRazonSocial()).as("getNombreRazonSocial()")
        .isEqualTo(empresa.getNombreRazonSocial());
    Assertions.assertThat(empresaActualizada.getActivo()).as("getActivo()").isFalse();

  }

  @Test
  void disable_WithIdNotExist_ThrowsEmpresaNotFoundException() {
    // given: Un id de un Empresa que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(empresaRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos la Empresa
    // then: Lanza una excepcion porque la Empresa no existe
    Assertions.assertThatThrownBy(() -> empresaService.desactivar(idNoExiste))
        .isInstanceOf(EmpresaNotFoundException.class);
  }

  @Test
  void findActivos_WithPaging_ReturnsPage() {
    // given: One hundred Empresas
    List<Empresa> empresaList = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      empresaList
          .add(generarMockEmpresa(Long.valueOf(i), "Empresa" + String.format("%03d", i)));
    }

    BDDMockito.given(
        empresaRepository.findAll(ArgumentMatchers.<Specification<Empresa>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Empresa>>() {
          @Override
          public Page<Empresa> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Empresa> content = empresaList.subList(fromIndex, toIndex);
            Page<Empresa> page = new PageImpl<>(content, pageable, empresaList.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Empresa> page = empresaService.findActivos(null, paging);

    // then: A Page with ten Empresaes are returned containing
    // descripcion='Empresa031' to 'Empresa040'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Empresa empresa = page.getContent().get(i);
      Assertions.assertThat(empresa.getNombreRazonSocial()).isEqualTo("Empresa" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto Empresa
   * 
   * @param id id dla Empresa
   * @return el objeto Empresa
   */
  public Empresa generarMockEmpresa(Long id) {
    return generarMockEmpresa(id, "nombreRazonSocial-" + id);
  }

  /**
   * Función que devuelve un objeto Empresa
   * 
   * @param id id dla Empresa
   * @return el objeto Empresa
   */
  public Empresa generarMockEmpresa(Long id, String nombreRazonSocial) {
    Empresa empresa = new Empresa();
    empresa.setId(id);
    empresa.setNombreRazonSocial(nombreRazonSocial);
    empresa.setActivo(Boolean.TRUE);
    return empresa;
  }

  private <T> void mockActivableIsActivo(Class<T> clazz, T object) {
    BDDMockito.given(persistenceUnitUtil.getIdentifier(ArgumentMatchers.any(clazz)))
        .willAnswer((InvocationOnMock invocation) -> {
          Object arg0 = invocation.getArgument(0);
          if (arg0 == null) {
            return null;
          }
          BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(arg0);
          Object id = wrapper.getPropertyValue("id");
          return id;
        });
    BDDMockito.given(entityManager.find(ArgumentMatchers.eq(clazz), ArgumentMatchers.anyLong())).willReturn(object);
  }

}

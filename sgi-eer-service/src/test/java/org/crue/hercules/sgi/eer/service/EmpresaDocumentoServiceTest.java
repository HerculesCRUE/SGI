package org.crue.hercules.sgi.eer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.validation.ValidationException;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eer.exceptions.EmpresaDocumentoNotFoundException;
import org.crue.hercules.sgi.eer.model.EmpresaDocumento;
import org.crue.hercules.sgi.eer.model.TipoDocumento;
import org.crue.hercules.sgi.eer.repository.EmpresaDocumentoRepository;
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
 * EmpresaDocumentoServiceTest
 */
@Import({ EmpresaDocumentoService.class, ApplicationContextSupport.class })
public class EmpresaDocumentoServiceTest extends BaseServiceTest {
  private static final String NOMBRE_PREFIX = "Documento ";

  @MockBean
  private EmpresaDocumentoRepository repository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @Autowired
  private EmpresaDocumentoService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  public void create_ReturnsEmpresaDocumento() {
    // given: Una nueva EmpresaDocumento
    EmpresaDocumento empresaDocumentoToCreate = generateEmpresaDocumentoMock(null, 1L, Boolean.TRUE);

    mockActivableIsActivo(TipoDocumento.class, empresaDocumentoToCreate.getTipoDocumento());
    BDDMockito.given(repository.saveAndFlush(ArgumentMatchers.<EmpresaDocumento>any()))
        .will((InvocationOnMock invocation) -> {
          EmpresaDocumento empresaDocumentoCreated = invocation.getArgument(0);
          empresaDocumentoCreated.setId(1L);
          return empresaDocumentoCreated;
        });

    // when: Creamos la EmpresaDocumento
    EmpresaDocumento empresaDocumentoCreated = service.create(empresaDocumentoToCreate);

    // then: La EmpresaDocumento se crea correctamente
    Assertions.assertThat(empresaDocumentoCreated).as("isNotNull()").isNotNull();
    Assertions.assertThat(empresaDocumentoCreated.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(empresaDocumentoCreated.getNombre()).as("getNombre()")
        .isEqualTo(empresaDocumentoToCreate.getNombre());
  }

  @Test
  public void create_WithNoActivoTipoDocumento_ThrowsValidationException() {
    // given: Una nueva EmpresaDocumento con TipoDocumento no activo
    EmpresaDocumento empresaDocumentoToCreate = generateEmpresaDocumentoMock(null, 1L, Boolean.FALSE);

    mockActivableIsActivo(TipoDocumento.class, empresaDocumentoToCreate.getTipoDocumento());
    BDDMockito.given(repository.save(ArgumentMatchers.<EmpresaDocumento>any())).will((InvocationOnMock invocation) -> {
      EmpresaDocumento empresaDocumentoCreated = invocation.getArgument(0);
      empresaDocumentoCreated.setId(1L);
      return empresaDocumentoCreated;
    });

    Assertions.assertThatThrownBy(
        // when: Creamos la EmpresaDocumento
        () -> service.create(empresaDocumentoToCreate))
        // then: Lanza excepcion porque el TipoDocumento no esta activo
        .isInstanceOf(ValidationException.class);
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Una nueva EmpresaDocumento
    EmpresaDocumento empresaDocumentoToCreate = generateEmpresaDocumentoMock(1L);

    Assertions.assertThatThrownBy(
        // when: Creamos la EmpresaDocumento
        () -> service.create(empresaDocumentoToCreate))
        // then: Lanza excepcion porque el id no puede venir cubierto
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsEmpresaDocumento() {
    // given: Una EmpresaDocumento a actualizar
    String nombreToUpdate = "Documento actualizado";
    EmpresaDocumento empresaDocumentoToUpdate = generateEmpresaDocumentoMock(1L, nombreToUpdate);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      Long idToFind = invocation.getArgument(0);
      return Optional.of(generateEmpresaDocumentoMock(idToFind));
    });
    BDDMockito.given(repository.saveAndFlush(ArgumentMatchers.<EmpresaDocumento>any()))
        .will((InvocationOnMock invocation) -> {
          EmpresaDocumento empresaDocumentoUpdated = invocation.getArgument(0);
          empresaDocumentoUpdated.setNombre(nombreToUpdate);
          return empresaDocumentoUpdated;
        });

    // when: Actualizamos la EmpresaDocumento
    EmpresaDocumento empresaDocumentoUpdated = service.update(empresaDocumentoToUpdate);

    // then: EmpresaDocumento actualizada correctamente
    Assertions.assertThat(empresaDocumentoUpdated).as("isNotNull()").isNotNull();
    Assertions.assertThat(empresaDocumentoUpdated.getId()).as("getId()").isEqualTo(empresaDocumentoToUpdate.getId());
    Assertions.assertThat(empresaDocumentoUpdated.getNombre()).as("getNombre()")
        .isEqualTo(empresaDocumentoToUpdate.getNombre());
  }

  @Test
  public void update_ToNoActivoTipoDocumento_ThrowsValidationException() {
    // given: Una EmpresaDocumento a actualizar a un TipoDocumento no activo
    EmpresaDocumento empresaDocumentoToUpdate = generateEmpresaDocumentoMock(1L, 2L, Boolean.FALSE);

    mockActivableIsActivo(TipoDocumento.class, empresaDocumentoToUpdate.getTipoDocumento());
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      Long idToFind = invocation.getArgument(0);
      return Optional.of(generateEmpresaDocumentoMock(idToFind, 3L, Boolean.TRUE));
    });

    Assertions.assertThatThrownBy(
        // when: Actualizamos la EmpresaDocumento
        () -> service.update(empresaDocumentoToUpdate))
        // then: Lanza excepcion porque el TipoDocumento a actualizar no esta activo
        .isInstanceOf(ValidationException.class);
  }

  @Test
  public void update_WithNotExistingId_ThrowsEmpresaDocumentoNotFoundException() {
    // given: Una EmpresaDocumento a actualizar con id no existente
    EmpresaDocumento empresaDocumentoToUpdate = generateEmpresaDocumentoMock(99L);

    Assertions.assertThatThrownBy(
        // when: Actualizamos la EmpresaDocumento
        () -> service.update(empresaDocumentoToUpdate))
        // then: Lanza excepcion porque no existe el id
        .isInstanceOf(EmpresaDocumentoNotFoundException.class);
  }

  @Test
  public void update_WithIdNull_ThrowsIllegalArgumentException() {
    // given: Una EmpresaDocumento sin id
    EmpresaDocumento empresaDocumentoToUpdate = generateEmpresaDocumentoMock(null);

    Assertions.assertThatThrownBy(
        // when: Actualizamos la EmpresaDocumento
        () -> service.update(empresaDocumentoToUpdate))
        // then: Lanza excepcion porque el id no puede ser null
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_ThrowsIllegalArgumentException() {
    // given: Un empresaDocumentoId null
    Long empresaDocumentoId = null;

    Assertions.assertThatThrownBy(
        // when: Buscamos por el id
        () -> service.delete(empresaDocumentoId))
        // then: Lanza excepcion porque el id no puede ser null
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithNotExistingId_ThrowsEmpresaDocumentoNotFoundException() {
    // given: Un empresaDocumentoId que no existe
    Long empresaDocumentoId = 99L;

    Assertions.assertThatThrownBy(
        // when: Buscamos por el id
        () -> service.delete(empresaDocumentoId))
        // then: Lanza excepcion porque el id no existe
        .isInstanceOf(EmpresaDocumentoNotFoundException.class);
  }

  @Test
  public void findById_ReturnsEmpresaDocumento() {
    // given: Un empresaDocumentoId existente
    Long empresaDocumentoId = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      Long idToFind = invocation.getArgument(0);
      return Optional.of(generateEmpresaDocumentoMock(idToFind));
    });

    // when: Buscamos por el id
    EmpresaDocumento empresaDocumento = service.findById(empresaDocumentoId);

    // then: Obtenemos una EmpresaDocumento con el id indicado
    Assertions.assertThat(empresaDocumento).as("isNotNull()").isNotNull();
    Assertions.assertThat(empresaDocumento.getId()).as("getId()").isEqualTo(empresaDocumentoId);
  }

  @Test
  public void findById_WithIdNull_ThrowsIllegalArgumentException() {
    // given: Un empresaDocumentoId null
    Long empresaDocumentoId = null;

    Assertions.assertThatThrownBy(
        // when: Buscamos por el id
        () -> service.findById(empresaDocumentoId))
        // then: Lanza excepcion porque el id no puede ser null
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void findById_WithNotExistingId_ThrowsEmpresaDocumentoNotFoundException() {
    // given: Un empresaDocumentoId no existente
    Long empresaDocumentoId = 1L;

    Assertions.assertThatThrownBy(
        // when: Buscamos por el id
        () -> service.findById(empresaDocumentoId))
        // then: Lanza excepcion porque no existe el id
        .isInstanceOf(EmpresaDocumentoNotFoundException.class);
  }

  @Test
  public void findAllByEmpresaId_ReturnsPage() {
    // given: Una lista con 37 EmpresaDocumento que pertenecen a una Empresa
    Long empresaId = 1L;
    List<EmpresaDocumento> empresaDocumentos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      empresaDocumentos
          .add(generateEmpresaDocumentoMock(i, empresaId, NOMBRE_PREFIX + String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<EmpresaDocumento>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<EmpresaDocumento>>() {
          @Override
          public Page<EmpresaDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > empresaDocumentos.size() ? empresaDocumentos.size() : toIndex;
            List<EmpresaDocumento> content = empresaDocumentos.subList(fromIndex, toIndex);
            Page<EmpresaDocumento> page = new PageImpl<>(content, pageable, empresaDocumentos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<EmpresaDocumento> page = service.findAllByEmpresaId(empresaId, null, paging);

    // then: Devuelve la pagina 3 con los EmpresaDocumento del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      EmpresaDocumento empresaDocumento = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(empresaDocumento.getEmpresaId()).as("getEmpresaId()").isEqualTo(empresaId);
      Assertions.assertThat(empresaDocumento.getNombre())
          .isEqualTo(NOMBRE_PREFIX + String.format("%03d", i));
    }
  }

  private EmpresaDocumento generateEmpresaDocumentoMock(Long id) {
    return generateEmpresaDocumentoMock(id, 1L,
        generateTipoDocumentoMock(id), "Documento", "Comentario", "documento-ref-1");
  }

  private EmpresaDocumento generateEmpresaDocumentoMock(Long id, String nombre) {
    return generateEmpresaDocumentoMock(id, 1L, generateTipoDocumentoMock(id), nombre, "Comentario", "documento-ref-1");
  }

  private EmpresaDocumento generateEmpresaDocumentoMock(Long id, Long empresaId, String nombre) {
    return generateEmpresaDocumentoMock(id, empresaId, generateTipoDocumentoMock(id), nombre, "Comentario",
        "documento-ref-1");
  }

  private EmpresaDocumento generateEmpresaDocumentoMock(Long id, Long tipoDocumentoId, Boolean isTipoDocumentoActivo) {
    return generateEmpresaDocumentoMock(id, 1L,
        generateTipoDocumentoMock(tipoDocumentoId, isTipoDocumentoActivo), "Documento", "Comentario",
        "documento-ref-1");
  }

  private EmpresaDocumento generateEmpresaDocumentoMock(Long id, Long empresaId, TipoDocumento tipoDocumento,
      String nombre,
      String comentarios, String documentoRef) {
    return EmpresaDocumento.builder()
        .comentarios(comentarios)
        .documentoRef(documentoRef)
        .empresaId(empresaId)
        .id(id)
        .nombre(nombre)
        .tipoDocumento(tipoDocumento)
        .build();
  }

  private TipoDocumento generateTipoDocumentoMock(Long id) {
    return generateTipoDocumentoMock(id, Boolean.TRUE, "Nombre", "Descripcion", null);
  }

  private TipoDocumento generateTipoDocumentoMock(Long id, Boolean activo) {
    return generateTipoDocumentoMock(id, activo, "Nombre", "Descripcion", null);
  }

  private TipoDocumento generateTipoDocumentoMock(Long id, Boolean activo, String nombre, String descripcion,
      TipoDocumento padre) {
    return TipoDocumento.builder()
        .activo(activo)
        .descripcion(descripcion)
        .id(id)
        .nombre(nombre)
        .padre(padre)
        .build();
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

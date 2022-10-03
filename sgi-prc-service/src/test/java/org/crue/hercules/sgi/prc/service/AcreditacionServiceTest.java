package org.crue.hercules.sgi.prc.service;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.AcreditacionNotFoundException;
import org.crue.hercules.sgi.prc.model.Acreditacion;
import org.crue.hercules.sgi.prc.repository.AcreditacionRepository;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaAuthorityHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

/**
 * AcreditacionServiceTest
 */
@Import({ AcreditacionService.class, ApplicationContextSupport.class })
class AcreditacionServiceTest extends BaseServiceTest {

  private static final String DEFAULT_DATA_DOCUMENTO_REF = "Documento-ref-default";
  private static final Long DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID = 1L;
  private static final String DEFAULT_DATA_URL = "http://url-por-defecto.com";

  @MockBean
  private AcreditacionRepository repository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @MockBean
  private ProduccionCientificaAuthorityHelper authorityHelper;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private AcreditacionService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void create_ReturnsAcreditacion() {
    // given: Un nuevo Acreditacion
    Acreditacion acreditacion = generarMockAcreditacion(null);

    BDDMockito.given(repository.save(acreditacion)).will((InvocationOnMock invocation) -> {
      Acreditacion acreditacionCreada = invocation.getArgument(0);
      acreditacionCreada.setId(1L);
      return acreditacionCreada;
    });

    // when: Creamos Acreditacion
    Acreditacion acreditacionCreada = service.create(acreditacion);

    // then: Acreditacion se crea correctamente
    Assertions.assertThat(acreditacionCreada).as("isNotNull()").isNotNull();
    Assertions.assertThat(acreditacionCreada.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(acreditacionCreada.getDocumentoRef()).as("getDocumentoRef")
        .isEqualTo(acreditacion.getDocumentoRef());
    Assertions.assertThat(acreditacionCreada.getProduccionCientificaId()).as("getProduccionCientificaId")
        .isEqualTo(acreditacion.getProduccionCientificaId());
    Assertions.assertThat(acreditacionCreada.getUrl()).as("getUrl")
        .isEqualTo(acreditacion.getUrl());
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: Acreditacion que ya tiene id
    Acreditacion acreditacion = generarMockAcreditacion(1L);

    // when: Creamos Acreditacion
    // then: Lanza una excepcion porque Acreditacion ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(acreditacion))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_ReturnsAcreditacion() {
    // given: Un nuevo Acreditacion
    Acreditacion acreditacion = generarMockAcreditacion(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(acreditacion));
    BDDMockito.given(repository.save(acreditacion)).will((InvocationOnMock invocation) -> {
      return invocation.getArgument(0);
    });

    // when: Creamos Acreditacion
    Acreditacion acreditacionActualizada = service.update(acreditacion);

    // then: Acreditacion se crea correctamente
    Assertions.assertThat(acreditacionActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(acreditacionActualizada.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(acreditacionActualizada.getDocumentoRef()).as("getDocumentoRef")
        .isEqualTo(acreditacion.getDocumentoRef());
    Assertions.assertThat(acreditacionActualizada.getProduccionCientificaId()).as("getProduccionCientificaId")
        .isEqualTo(acreditacion.getProduccionCientificaId());
    Assertions.assertThat(acreditacionActualizada.getUrl()).as("getUrl")
        .isEqualTo(acreditacion.getUrl());
  }

  @Test
  void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: Acreditacion sin id
    Acreditacion acreditacion = generarMockAcreditacion(null);

    // when: Actualizamos Acreditacion
    // then: Lanza una excepcion porque Acreditacion no tiene id
    Assertions.assertThatThrownBy(() -> service.update(acreditacion))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_WithNotExistingAcreditacion_ThrowsAcreditacionNotFoundException() {
    // given: Acreditacion sin id
    Acreditacion acreditacion = generarMockAcreditacion(33L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos Acreditacion
    // then: Lanza una excepcion porque Acreditacion no existe
    Assertions.assertThatThrownBy(() -> service.update(acreditacion))
        .isInstanceOf(AcreditacionNotFoundException.class);
  }

  @Test
  void findById_ReturnsFuenteFinanciacion() {
    // given: Acreditacion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockAcreditacion(idBuscado)));

    // when: Buscamos Acreditacion por su id
    Acreditacion acreditacion = service.findById(idBuscado);

    // then: el Acreditacion
    Assertions.assertThat(acreditacion).as("isNotNull()").isNotNull();
    Assertions.assertThat(acreditacion.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(acreditacion.getDocumentoRef()).as("getDocumentoRef")
        .isEqualTo(DEFAULT_DATA_DOCUMENTO_REF);
    Assertions.assertThat(acreditacion.getProduccionCientificaId()).as("getProduccionCientificaId")
        .isEqualTo(DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID);
    Assertions.assertThat(acreditacion.getUrl()).as("getUrl")
        .isEqualTo(DEFAULT_DATA_URL);
  }

  @Test
  void findById_WithIdNotExist_ThrowsAcreditacionNotFoundException() {
    // given: Ningun Acreditacion con el id buscado
    Long idBuscado = 33L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Buscamos el Acreditacion por su id
    // then: Lanza un AcreditacionNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(AcreditacionNotFoundException.class);
  }

  @Test
  void delete_ReturnsVoid() {
    // given: id null
    Long idToDelete = 1L;

    // when: Eliminamos Acreditacion con el id indicado
    // then: Elimina Acreditacion
    service.delete(idToDelete);

    Assertions.assertThat(idToDelete).as("idToDelete").isEqualTo(1L);
  }

  @Test
  void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: id null
    Long idToDelete = null;

    // when: Eliminamos Acreditacion
    // then: Lanza una excepcion porque el id es null
    Assertions.assertThatThrownBy(() -> service.delete(idToDelete))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void deleteInBulkByProduccionCientificaId_ReturnsInt() {
    // give: produccionCientificaId
    Long produccionCientificaId = 1L;

    // when: Eliminamos Acreditacion con produccionCientificaId
    Integer numeroRegistrosToDelete = 5;
    BDDMockito.given(repository.deleteInBulkByProduccionCientificaId(produccionCientificaId))
        .willReturn(numeroRegistrosToDelete);

    // then: Número registros eliminados igual al esperado
    Integer numeroRegistrosDeleted = service.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(numeroRegistrosDeleted).as("numeroRegistrosDeleted").isEqualTo(numeroRegistrosToDelete);
  }

  private Acreditacion generarMockAcreditacion(Long id) {
    return this.generarMockAcreditacion(id, DEFAULT_DATA_DOCUMENTO_REF, DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID,
        DEFAULT_DATA_URL);
  }

  private Acreditacion generarMockAcreditacion(
      Long id, String documentoRef, Long produccionCientificaId, String url) {
    return Acreditacion.builder()
        .id(id)
        .documentoRef(documentoRef)
        .produccionCientificaId(produccionCientificaId)
        .url(url)
        .build();
  }
}

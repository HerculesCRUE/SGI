package org.crue.hercules.sgi.prc.service;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.AliasEnumeradoNotFoundException;
import org.crue.hercules.sgi.prc.model.AliasEnumerado;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.repository.AliasEnumeradoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

/**
 * AliasEnumeradoServiceTest
 */
@Import({ AliasEnumeradoService.class, ApplicationContextSupport.class })
public class AliasEnumeradoServiceTest extends BaseServiceTest {

  private static final CodigoCVN DEFAULT_DATA_CVN_CODE = CodigoCVN.COLECTIVA;
  private static final String DEFAULT_DATA_PREFIJO_ENUMERADO = "Colectiva";

  @MockBean
  private AliasEnumeradoRepository repository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private AliasEnumeradoService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  public void create_ReturnsAliasEnumerado() {
    // given: Un nuevo AliasEnumerado
    AliasEnumerado aliasEnumerado = generarMockAliasEnumerado(null);

    BDDMockito.given(repository.save(aliasEnumerado)).will((InvocationOnMock invocation) -> {
      AliasEnumerado aliasEnumeradoCreado = invocation.getArgument(0);
      aliasEnumeradoCreado.setId(1L);
      return aliasEnumeradoCreado;
    });

    // when: Creamos AliasEnumerado
    AliasEnumerado aliasEnumeradoCreado = service.create(aliasEnumerado);

    // then: AliasEnumerado se crea correctamente
    Assertions.assertThat(aliasEnumeradoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(aliasEnumeradoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(aliasEnumeradoCreado.getCodigoCVN()).as("getCodigoCVN")
        .isEqualTo(aliasEnumerado.getCodigoCVN());
    Assertions.assertThat(aliasEnumeradoCreado.getPrefijoEnumerado()).as("getPrefijoEnumerado")
        .isEqualTo(aliasEnumerado.getPrefijoEnumerado());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: AliasEnumerado que ya tiene id
    AliasEnumerado aliasEnumerado = generarMockAliasEnumerado(1L);

    // when: Creamos AliasEnumerado
    // then: Lanza una excepcion porque AliasEnumerado ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(aliasEnumerado))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsAliasEnumerado() {
    // given: Un nuevo AliasEnumerado
    AliasEnumerado aliasEnumerado = generarMockAliasEnumerado(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(aliasEnumerado));
    BDDMockito.given(repository.save(aliasEnumerado)).will((InvocationOnMock invocation) -> {
      return invocation.getArgument(0);
    });

    // when: Creamos AliasEnumerado
    AliasEnumerado aliasEnumeradoActualizado = service.update(aliasEnumerado);

    // then: AliasEnumerado se crea correctamente
    Assertions.assertThat(aliasEnumeradoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(aliasEnumeradoActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(aliasEnumeradoActualizado.getCodigoCVN()).as("getCodigoCVN")
        .isEqualTo(aliasEnumerado.getCodigoCVN());
    Assertions.assertThat(aliasEnumeradoActualizado.getPrefijoEnumerado()).as("getPrefijoEnumerado")
        .isEqualTo(aliasEnumerado.getPrefijoEnumerado());
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: AliasEnumerado sin id
    AliasEnumerado aliasEnumerado = generarMockAliasEnumerado(null);

    // when: Actualizamos AliasEnumerado
    // then: Lanza una excepcion porque AliasEnumerado no tiene id
    Assertions.assertThatThrownBy(() -> service.update(aliasEnumerado))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithNotExistingAliasEnumerado_ThrowsAliasEnumeradoNotFoundException() {
    // given: AliasEnumerado sin id
    AliasEnumerado aliasEnumerado = generarMockAliasEnumerado(33L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos AliasEnumerado
    // then: Lanza una excepcion porque AliasEnumerado no existe
    Assertions.assertThatThrownBy(() -> service.update(aliasEnumerado))
        .isInstanceOf(AliasEnumeradoNotFoundException.class);
  }

  @Test
  public void findById_ReturnsAliasEnumerado() {
    // given: AliasEnumerado con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockAliasEnumerado(idBuscado)));

    // when: Buscamos AliasEnumerado por su id
    AliasEnumerado aliasEnumerado = service.findById(idBuscado);

    // then: AliasEnumerado
    Assertions.assertThat(aliasEnumerado).as("isNotNull()").isNotNull();
    Assertions.assertThat(aliasEnumerado.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(aliasEnumerado.getCodigoCVN()).as("getCodigoCVN")
        .isEqualTo(DEFAULT_DATA_CVN_CODE);
    Assertions.assertThat(aliasEnumerado.getPrefijoEnumerado()).as("getPrefijoEnumerado")
        .isEqualTo(DEFAULT_DATA_PREFIJO_ENUMERADO);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsAliasEnumeradoNotFoundException() {
    // given: Ningun AliasEnumerado con el id buscado
    Long idBuscado = 33L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Buscamos el AliasEnumerado por su id
    // then: Lanza un AliasEnumeradoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(AliasEnumeradoNotFoundException.class);
  }

  @Test
  public void delete_ReturnsVoid() {
    // given: id null
    Long idToDelete = 1L;

    // when: Eliminamos AliasEnumerado con el id indicado
    // then: Elimina AliasEnumerado
    service.delete(idToDelete);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: id null
    Long idToDelete = null;

    // when: Eliminamos AliasEnumerado
    // then: Lanza una excepcion porque el id es null
    Assertions.assertThatThrownBy(() -> service.delete(idToDelete))
        .isInstanceOf(IllegalArgumentException.class);
  }

  private AliasEnumerado generarMockAliasEnumerado(Long id) {
    return this.generarMockAliasEnumerado(id, DEFAULT_DATA_CVN_CODE, DEFAULT_DATA_PREFIJO_ENUMERADO);
  }

  private AliasEnumerado generarMockAliasEnumerado(Long id, CodigoCVN codigoCVN, String prefijoEnumerado) {
    return AliasEnumerado.builder()
        .id(id)
        .codigoCVN(codigoCVN)
        .prefijoEnumerado(prefijoEnumerado)
        .build();
  }
}

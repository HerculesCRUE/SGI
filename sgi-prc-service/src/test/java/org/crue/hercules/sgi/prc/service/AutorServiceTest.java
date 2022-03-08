package org.crue.hercules.sgi.prc.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.AutorNotFoundException;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
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
 * AutorServiceTest
 */
@Import({ AutorService.class, ApplicationContextSupport.class })
public class AutorServiceTest extends BaseServiceTest {

  private static final String PERSONA_REF_PREFIX = "Persona-ref-";
  private static final String DEFAULT_DATA_FIRMA = "Default-firma";
  private static final String DEFAULT_DATA_PERSONA_REF = PERSONA_REF_PREFIX + "default";
  private static final String DEFAULT_DATA_NOMBRE = "Nombre Default";
  private static final String DEFAULT_DATA_APELLIDOS = "Apellidos Default";
  private static final Integer DEFAULT_DATA_ORDEN = 1;
  private static final String DEFAULT_DATA_ORCID_ID = "Orcid-id-default";
  private static final Instant DEFAULT_DATA_FECHA_INICIO = Instant.now();
  private static final Instant DEFAULT_DATA_FECHA_FIN = Instant.now().plus(1, ChronoUnit.DAYS);
  private static final Boolean DEFAULT_DATA_IP = Boolean.TRUE;
  private static final Long DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID = 1L;

  @MockBean
  private AutorRepository repository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private AutorService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  public void create_ReturnsAutor() {
    // given: Un nuevo Autor
    Autor autor = generarMockAutor(null);

    BDDMockito.given(repository.save(autor)).will((InvocationOnMock invocation) -> {
      Autor autorCreado = invocation.getArgument(0);
      autorCreado.setId(1L);
      return autorCreado;
    });

    // when: Creamos Autor
    Autor autorCreado = service.create(autor);

    // then: Autor se crea correctamente
    Assertions.assertThat(autorCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(autorCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(autorCreado.getApellidos()).as("getApellidos")
        .isEqualTo(autor.getApellidos());
    Assertions.assertThat(autorCreado.getFechaFin()).as("getFechaFin")
        .isEqualTo(autor.getFechaFin());
    Assertions.assertThat(autorCreado.getFechaInicio()).as("getFechaInicio")
        .isEqualTo(autor.getFechaInicio());
    Assertions.assertThat(autorCreado.getFirma()).as("getFirma")
        .isEqualTo(autor.getFirma());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Autor que ya tiene id
    Autor autor = generarMockAutor(1L);

    // when: Creamos Autor
    // then: Lanza una excepcion porque Autor ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(autor))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsAutor() {
    // given: Un nuevo Autor
    Autor autor = generarMockAutor(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(autor));
    BDDMockito.given(repository.save(autor)).will((InvocationOnMock invocation) -> {
      return invocation.getArgument(0);
    });

    // when: Creamos Autor
    Autor autorActualizado = service.update(autor);

    // then: Autor se crea correctamente
    Assertions.assertThat(autorActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(autorActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(autorActualizado.getApellidos()).as("getApellidos")
        .isEqualTo(autor.getApellidos());
    Assertions.assertThat(autorActualizado.getFechaFin()).as("getFechaFin")
        .isEqualTo(autor.getFechaFin());
    Assertions.assertThat(autorActualizado.getFechaInicio()).as("getFechaInicio")
        .isEqualTo(autor.getFechaInicio());
    Assertions.assertThat(autorActualizado.getFirma()).as("getFirma")
        .isEqualTo(autor.getFirma());
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: Autor sin id
    Autor autor = generarMockAutor(null);

    // when: Actualizamos Autor
    // then: Lanza una excepcion porque Autor no tiene id
    Assertions.assertThatThrownBy(() -> service.update(autor))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithNotExistingAutor_ThrowsAutorNotFoundException() {
    // given: Autor sin id
    Autor autor = generarMockAutor(33L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos Autor
    // then: Lanza una excepcion porque Autor no existe
    Assertions.assertThatThrownBy(() -> service.update(autor))
        .isInstanceOf(AutorNotFoundException.class);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 Autor
    List<Autor> autores = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      autores.add(generarMockAutor(i, String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<Autor>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Autor>>() {
          @Override
          public Page<Autor> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > autores.size() ? autores.size() : toIndex;
            List<Autor> content = autores.subList(fromIndex, toIndex);
            Page<Autor> page = new PageImpl<>(content, pageable, autores.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Autor> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los Autor del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Autor autor = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(autor.getPersonaRef()).as("getPersonaRef")
          .isEqualTo(PERSONA_REF_PREFIX + String.format("%03d", i));
    }
  }

  @Test
  public void findById_ReturnsAutor() {
    // given: Autor con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockAutor(idBuscado)));

    // when: Buscamos Autor por su id
    Autor autor = service.findById(idBuscado);

    // then: el Autor
    Assertions.assertThat(autor).as("isNotNull()").isNotNull();
    Assertions.assertThat(autor.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(autor.getApellidos()).as("getApellidos")
        .isEqualTo(DEFAULT_DATA_APELLIDOS);
    Assertions.assertThat(autor.getFechaFin()).as("getFechaFin")
        .isEqualTo(DEFAULT_DATA_FECHA_FIN);
    Assertions.assertThat(autor.getFechaInicio()).as("getFechaInicio")
        .isEqualTo(DEFAULT_DATA_FECHA_INICIO);
    Assertions.assertThat(autor.getFirma()).as("getFirma")
        .isEqualTo(DEFAULT_DATA_FIRMA);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsAutorNotFoundException() {
    // given: Ningun Autor con el id buscado
    Long idBuscado = 33L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Buscamos el Autor por su id
    // then: Lanza un AutorNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(AutorNotFoundException.class);
  }

  @Test
  public void delete_ReturnsVoid() {
    // given: id null
    Long idToDelete = 1L;

    // when: Eliminamos Autor con el id indicado
    // then: Elimina Autor
    service.delete(idToDelete);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: id null
    Long idToDelete = null;

    // when: Eliminamos Autor
    // then: Lanza una excepcion porque el id es null
    Assertions.assertThatThrownBy(() -> service.delete(idToDelete))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void findAllByProduccionCientificaId_ReturnsList() {
    // given: Una lista con 7 Autor y un produccionCientificaId
    Long produccionCientificaId = 1L;
    List<Autor> autores = new ArrayList<>();
    for (long i = 1; i <= 7; i++) {
      if (i % 2 == 0) {
        autores.add(generarMockAutor(i, produccionCientificaId, String.format("%03d", i)));
      } else {
        autores.add(generarMockAutor(i, 2L, String.format("%03d", i)));
      }
    }

    // when: Buscamos Autor con produccionCientificaId
    BDDMockito.given(
        repository.findAllByProduccionCientificaId(ArgumentMatchers.<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Long autorIdToFind = invocation.getArgument(0);
          return autores.stream().filter(autor -> autorIdToFind.equals(autor.getProduccionCientificaId()))
              .collect(Collectors.toList());
        });
    List<Autor> autoresBuscados = service.findAllByProduccionCientificaId(produccionCientificaId);
    // then: Cada Autor tiene produccionCientificaId buscado
    Assertions.assertThat(autoresBuscados.size()).as("size()").isEqualTo(3);
    autoresBuscados.stream().forEach(autorBuscado -> {
      Assertions.assertThat(autorBuscado.getProduccionCientificaId()).as("getProduccionCientificaId")
          .isEqualTo(produccionCientificaId);
    });
  }

  @Test
  public void deleteInBulkByProduccionCientificaId_ReturnsInt() {
    // give: produccionCientificaId
    Long produccionCientificaId = 1L;

    // when: Eliminamos Autor con produccionCientificaId
    Integer numeroRegistrosToDelete = 5;
    BDDMockito.given(repository.deleteInBulkByProduccionCientificaId(produccionCientificaId))
        .willReturn(numeroRegistrosToDelete);

    // then: Número registros eliminados igual al esperado
    Integer numeroRegistrosDeleted = service.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(numeroRegistrosDeleted).as("numeroRegistrosDeleted").isEqualTo(numeroRegistrosToDelete);
  }

  private Autor generarMockAutor(Long id, Long produccionCientificaId, String personaRefId) {
    return this.generarMockAutor(
        id, DEFAULT_DATA_APELLIDOS, DEFAULT_DATA_FECHA_FIN, DEFAULT_DATA_FECHA_INICIO,
        DEFAULT_DATA_FIRMA, DEFAULT_DATA_IP, DEFAULT_DATA_NOMBRE, DEFAULT_DATA_ORCID_ID,
        DEFAULT_DATA_ORDEN, PERSONA_REF_PREFIX + personaRefId, produccionCientificaId);
  }

  private Autor generarMockAutor(Long id, String personaRefId) {
    return this.generarMockAutor(
        id, DEFAULT_DATA_APELLIDOS, DEFAULT_DATA_FECHA_FIN, DEFAULT_DATA_FECHA_INICIO,
        DEFAULT_DATA_FIRMA, DEFAULT_DATA_IP, DEFAULT_DATA_NOMBRE, DEFAULT_DATA_ORCID_ID,
        DEFAULT_DATA_ORDEN, PERSONA_REF_PREFIX + personaRefId, DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID);
  }

  private Autor generarMockAutor(Long id) {
    return this.generarMockAutor(
        id, DEFAULT_DATA_APELLIDOS, DEFAULT_DATA_FECHA_FIN, DEFAULT_DATA_FECHA_INICIO,
        DEFAULT_DATA_FIRMA, DEFAULT_DATA_IP, DEFAULT_DATA_NOMBRE, DEFAULT_DATA_ORCID_ID,
        DEFAULT_DATA_ORDEN, DEFAULT_DATA_PERSONA_REF, DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID);
  }

  private Autor generarMockAutor(
      Long id, String apellidos,
      Instant fechaFin, Instant fechaInicio,
      String firma, Boolean ip,
      String nombre, String orcidId,
      Integer orden, String personaRef,
      Long produccionCientificaId) {
    return Autor.builder()
        .id(id)
        .apellidos(apellidos)
        .fechaFin(fechaFin)
        .fechaInicio(fechaInicio)
        .firma(firma)
        .ip(ip)
        .nombre(nombre)
        .orcidId(orcidId)
        .orden(orden)
        .personaRef(personaRef)
        .produccionCientificaId(produccionCientificaId)
        .build();
  }
}

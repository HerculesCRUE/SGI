package org.crue.hercules.sgi.prc.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.AutorGrupoNotFoundException;
import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.repository.AutorGrupoRepository;
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
 * AutorGrupoServiceTest
 */
@Import({ AutorGrupoService.class, ApplicationContextSupport.class })
class AutorGrupoServiceTest extends BaseServiceTest {

  private static final Long DEFAULT_DATA_AUTOR_ID = 1L;
  private static final TipoEstadoProduccion DEFAULT_DATA_ESTADO = TipoEstadoProduccion.PENDIENTE;
  private static final Long DEFAULT_DATA_GRUPO_REF = 1L;

  @MockBean
  private AutorGrupoRepository repository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private AutorGrupoService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void create_ReturnsAutorGrupo() {
    // given: Un nuevo AutorGrupo
    AutorGrupo autorGrupo = generarMockAutorGrupo(null);

    BDDMockito.given(repository.save(autorGrupo)).will((InvocationOnMock invocation) -> {
      AutorGrupo autorGrupoCreado = invocation.getArgument(0);
      autorGrupoCreado.setId(1L);
      return autorGrupoCreado;
    });

    // when: Creamos AutorGrupo
    AutorGrupo autorGrupoCreado = service.create(autorGrupo);

    // then: AutorGrupo se crea correctamente
    Assertions.assertThat(autorGrupoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(autorGrupoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(autorGrupoCreado.getAutorId()).as("getAutorId")
        .isEqualTo(autorGrupo.getAutorId());
    Assertions.assertThat(autorGrupoCreado.getEstado()).as("getEstado")
        .isEqualTo(autorGrupo.getEstado());
    Assertions.assertThat(autorGrupoCreado.getGrupoRef()).as("getGrupoRef")
        .isEqualTo(autorGrupo.getGrupoRef());
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: AutorGrupo que ya tiene id
    AutorGrupo autorGrupo = generarMockAutorGrupo(1L);

    // when: Creamos AutorGrupo
    // then: Lanza una excepcion porque AutorGrupo ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(autorGrupo))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_ReturnsAutorGrupo() {
    // given: Un nuevo AutorGrupo
    AutorGrupo autorGrupo = generarMockAutorGrupo(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(autorGrupo));
    BDDMockito.given(repository.save(autorGrupo)).will((InvocationOnMock invocation) -> {
      return invocation.getArgument(0);
    });

    // when: Creamos AutorGrupo
    AutorGrupo autorGrupoActualizado = service.update(autorGrupo);

    // then: AutorGrupo se crea correctamente
    Assertions.assertThat(autorGrupoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(autorGrupoActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(autorGrupoActualizado.getAutorId()).as("getAutorId")
        .isEqualTo(autorGrupo.getAutorId());
    Assertions.assertThat(autorGrupoActualizado.getEstado()).as("getEstado")
        .isEqualTo(autorGrupo.getEstado());
    Assertions.assertThat(autorGrupoActualizado.getGrupoRef()).as("getGrupoRef")
        .isEqualTo(autorGrupo.getGrupoRef());
  }

  @Test
  void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: AutorGrupo sin id
    AutorGrupo autorGrupo = generarMockAutorGrupo(null);

    // when: Actualizamos AutorGrupo
    // then: Lanza una excepcion porque AutorGrupo no tiene id
    Assertions.assertThatThrownBy(() -> service.update(autorGrupo))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_WithNotExistingAutorGrupo_ThrowsAutorGrupoNotFoundException() {
    // given: AutorGrupo sin id
    AutorGrupo autorGrupo = generarMockAutorGrupo(33L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos AutorGrupo
    // then: Lanza una excepcion porque AutorGrupo no existe
    Assertions.assertThatThrownBy(() -> service.update(autorGrupo))
        .isInstanceOf(AutorGrupoNotFoundException.class);
  }

  @Test
  void findAll_ReturnsPage() {
    // given: Una lista con 37 AutorGrupo
    List<AutorGrupo> autorGrupos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      autorGrupos.add(generarMockAutorGrupo(i, i));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<AutorGrupo>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<AutorGrupo>>() {
          @Override
          public Page<AutorGrupo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > autorGrupos.size() ? autorGrupos.size() : toIndex;
            List<AutorGrupo> content = autorGrupos.subList(fromIndex, toIndex);
            Page<AutorGrupo> page = new PageImpl<>(content, pageable, autorGrupos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<AutorGrupo> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los AutorGrupo del 31 al 37
    int numResultados = page.getContent().size();
    Assertions.assertThat(numResultados).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      AutorGrupo autorGrupo = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(autorGrupo.getGrupoRef()).as("getGrupoRef")
          .isEqualTo(i);
    }
  }

  @Test
  void findById_ReturnsAutorGrupo() {
    // given: AutorGrupo con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockAutorGrupo(idBuscado)));

    // when: Buscamos AutorGrupo por su id
    AutorGrupo autorGrupo = service.findById(idBuscado);

    // then: el AutorGrupo
    Assertions.assertThat(autorGrupo).as("isNotNull()").isNotNull();
    Assertions.assertThat(autorGrupo.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(autorGrupo.getAutorId()).as("getAutorId")
        .isEqualTo(DEFAULT_DATA_AUTOR_ID);
    Assertions.assertThat(autorGrupo.getEstado()).as("getEstado")
        .isEqualTo(DEFAULT_DATA_ESTADO);
    Assertions.assertThat(autorGrupo.getGrupoRef()).as("getGrupoRef")
        .isEqualTo(DEFAULT_DATA_GRUPO_REF);
  }

  @Test
  void findById_WithIdNotExist_ThrowsAutorGrupoNotFoundException() {
    // given: Ningun AutorGrupo con el id buscado
    Long idBuscado = 33L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Buscamos el AutorGrupo por su id
    // then: Lanza un AutorGrupoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(AutorGrupoNotFoundException.class);
  }

  @Test
  void delete_ReturnsVoid() {
    // given: id null
    Long idToDelete = 1L;

    // when: Eliminamos AutorGrupo con el id indicado
    // then: Elimina AutorGrupo
    service.delete(idToDelete);

    Assertions.assertThat(idToDelete).as("idToDelete").isEqualTo(1L);
  }

  @Test
  void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: id null
    Long idToDelete = null;

    // when: Eliminamos AutorGrupo
    // then: Lanza una excepcion porque el id es null
    Assertions.assertThatThrownBy(() -> service.delete(idToDelete))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void findAllByAutorId_ReturnsList() {
    // given: Una lista con 7 AutorGrupo y un autorId
    Long autorId = 1L;
    List<AutorGrupo> autorGrupos = new ArrayList<>();
    for (long i = 1; i <= 7; i++) {
      if (i % 2 == 0) {
        autorGrupos.add(generarMockAutorGrupo(i, autorId, i));
      } else {
        autorGrupos.add(generarMockAutorGrupo(i, 2L, i));
      }
    }

    // when: Buscamos AutorGrupo con autorId
    BDDMockito.given(
        repository.findAllByAutorId(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
          Long autorIdToFind = invocation.getArgument(0);
          return autorGrupos.stream().filter(autorGrupo -> autorIdToFind.equals(autorGrupo.getAutorId()))
              .collect(Collectors.toList());
        });
    List<AutorGrupo> autorGruposBuscados = service.findAllByAutorId(autorId);
    // then: Cada AutorGrupo tiene autorId buscado
    int numResultados = autorGruposBuscados.size();
    Assertions.assertThat(numResultados).as("size()").isEqualTo(3);
    autorGruposBuscados.stream().forEach(autoGrupoBuscado -> {
      Assertions.assertThat(autoGrupoBuscado.getAutorId()).as("getAutorId").isEqualTo(autorId);
    });
  }

  @Test
  void deleteInBulkByAutorId_ReturnsInt() {
    // give: autorId
    Long autorId = 1L;

    // when: Eliminamos AutorGrupo con autorId
    Integer numeroRegistrosToDelete = 5;
    BDDMockito.given(repository.deleteInBulkByAutorId(autorId))
        .willReturn(numeroRegistrosToDelete);

    // then: Número registros eliminados igual al esperado
    Integer numeroRegistrosDeleted = service.deleteInBulkByAutorId(autorId);
    Assertions.assertThat(numeroRegistrosDeleted).as("numeroRegistrosDeleted").isEqualTo(numeroRegistrosToDelete);
  }

  @Test
  void findAllByProduccionCientificaId_ReturnsList() {
    // given: Una lista con 7 AutorGrupo y un produccionCientificaId
    Long produccionCientificaId = 1L;
    List<AutorGrupo> autorGrupos = new ArrayList<>();
    for (long i = 1; i <= 7; i++) {
      autorGrupos.add(generarMockAutorGrupo(i));
    }

    // when: Buscamos AutorGrupo con produccionCientificaId
    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<AutorGrupo>>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          return autorGrupos;
        });
    List<AutorGrupo> autorGruposBuscados = service.findAllByProduccionCientificaId(produccionCientificaId);
    // then: Returns lista de AutorGrupo
    int numResultados = autorGruposBuscados.size();
    Assertions.assertThat(numResultados).as("size()").isEqualTo(7);
  }

  @Test
  void findAllByProduccionCientificaIdAndInGruposRef_ReturnsList() {
    // given: Una lista con 7 AutorGrupo, un produccionCientificaId y una lista de
    // ids de grupos
    Long produccionCientificaId = 1L;
    List<Long> gruposRef = Arrays.asList(new Long[] { 1L, 2L });
    List<AutorGrupo> autorGrupos = new ArrayList<>();
    for (long i = 1; i <= 7; i++) {
      autorGrupos.add(generarMockAutorGrupo(i));
    }

    // when: Buscamos AutorGrupo con produccionCientificaId
    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<AutorGrupo>>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          return autorGrupos;
        });
    List<AutorGrupo> autorGruposBuscados = service
        .findAllByProduccionCientificaIdAndInGruposRef(produccionCientificaId, gruposRef);
    // then: Returns lista de AutorGrupo
    int numResultados = autorGruposBuscados.size();
    Assertions.assertThat(numResultados).as("size()").isEqualTo(7);
  }

  private AutorGrupo generarMockAutorGrupo(Long id, Long autorId, Long grupoRefId) {
    return this.generarMockAutorGrupo(id, autorId, DEFAULT_DATA_ESTADO, grupoRefId);
  }

  private AutorGrupo generarMockAutorGrupo(Long id, Long grupoRefId) {
    return this.generarMockAutorGrupo(id, DEFAULT_DATA_AUTOR_ID, DEFAULT_DATA_ESTADO, grupoRefId);
  }

  private AutorGrupo generarMockAutorGrupo(Long id) {
    return this.generarMockAutorGrupo(id, DEFAULT_DATA_AUTOR_ID, DEFAULT_DATA_ESTADO, DEFAULT_DATA_GRUPO_REF);
  }

  private AutorGrupo generarMockAutorGrupo(
      Long id, Long autorId, TipoEstadoProduccion estado, Long grupoRef) {
    return AutorGrupo.builder()
        .id(id)
        .autorId(autorId)
        .estado(estado)
        .grupoRef(grupoRef)
        .build();
  }
}

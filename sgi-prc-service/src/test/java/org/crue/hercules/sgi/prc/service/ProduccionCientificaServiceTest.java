package org.crue.hercules.sgi.prc.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.dto.PublicacionResumen;
import org.crue.hercules.sgi.prc.exceptions.ProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica.EpigrafeCVN;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
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
 * ProduccionCientificaServiceTest
 */
@Import({ ProduccionCientificaService.class, ApplicationContextSupport.class })
public class ProduccionCientificaServiceTest extends BaseServiceTest {

  @MockBean
  private ProduccionCientificaRepository repository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @MockBean
  private EstadoProduccionCientificaService estadoProduccionCientificaService;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private ProduccionCientificaService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 ProduccionCientifica
    List<ProduccionCientifica> produccionCientificaes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      produccionCientificaes.add(generarMockProduccionCientifica(i, "ProduccionCientifica" + String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ProduccionCientifica>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProduccionCientifica>>() {
          @Override
          public Page<ProduccionCientifica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > produccionCientificaes.size() ? produccionCientificaes.size() : toIndex;
            List<ProduccionCientifica> content = produccionCientificaes.subList(fromIndex, toIndex);
            Page<ProduccionCientifica> page = new PageImpl<>(content, pageable, produccionCientificaes.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProduccionCientifica> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los ProduccionCientifica del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProduccionCientifica produccionCientifica = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(produccionCientifica.getProduccionCientificaRef())
          .isEqualTo("ProduccionCientifica" + String.format("%03d", i));
    }
  }

  @Test
  public void findById_ReturnsProduccionCientifica() {
    // given: Un ProduccionCientifica con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockProduccionCientifica(idBuscado)));

    // when: Buscamos el ProduccionCientifica por su id
    ProduccionCientifica produccionCientifica = service.findById(idBuscado);

    // then: el ProduccionCientifica
    Assertions.assertThat(produccionCientifica).as("isNotNull()").isNotNull();
    Assertions.assertThat(produccionCientifica.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(produccionCientifica.getProduccionCientificaRef()).as("getProduccionCientificaRef()")
        .isEqualTo("id-ref-1");
  }

  @Test
  public void findById_WithIdNotExist_ThrowsProduccionCientificaNotFoundException() throws Exception {
    // given: Ningun ProduccionCientifica con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ProduccionCientifica por su id
    // then: lanza un ProduccionCientificaNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ProduccionCientificaNotFoundException.class);
  }

  @Test
  public void findAllPublicaciones_ReturnsPage() {
    // given: Una lista con 37 PublicacionResumen
    List<PublicacionResumen> publicaciones = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      publicaciones.add(generarMockPublicacionResumen(i, String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAllPublicaciones(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<PublicacionResumen>>() {
          @Override
          public Page<PublicacionResumen> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > publicaciones.size() ? publicaciones.size() : toIndex;
            List<PublicacionResumen> content = publicaciones.subList(fromIndex, toIndex);
            Page<PublicacionResumen> page = new PageImpl<>(content, pageable, publicaciones.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<PublicacionResumen> page = service.findAllPublicaciones(null, paging);

    // then: Devuelve la pagina 3 con los PublicacionResumen del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      PublicacionResumen publicacion = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(publicacion.getProduccionCientificaRef())
          .isEqualTo("ProduccionCientifica" + String.format("%03d", i));
    }
  }

  @Test
  void cambiarEstado_WithIdIsNull_ThrowsIllegalArgumentException() {
    Assertions.assertThatThrownBy(() -> this.service.cambiarEstado(null, null, null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void cambiarEstado_ProduccionCientificaEstadoNotPendiente_ReturnsProduccionCientifica() {
    BDDMockito.given(
        repository.findById(ArgumentMatchers.<Long>any()))
        .willAnswer(new Answer<Optional<ProduccionCientifica>>() {
          @Override
          public Optional<ProduccionCientifica> answer(InvocationOnMock invocation) throws Throwable {
            ProduccionCientifica produccionCientifica = ProduccionCientifica.builder()
                .id(1L)
                .estado(
                    EstadoProduccionCientifica.builder()
                        .id(1L)
                        .estado(TipoEstadoProduccion.RECHAZADO)
                        .build())
                .build();
            return Optional.of(produccionCientifica);
          }
        });
    ProduccionCientifica produccionCientifica = service.cambiarEstado(1L,
        TipoEstadoProduccion.VALIDADO, null);

    Assertions.assertThat(produccionCientifica.getEstado().getEstado()).as("getEstado().getEstado()")
        .isEqualTo(TipoEstadoProduccion.RECHAZADO);
  }

  /**
   * Función que devuelve un objeto ProduccionCientifica
   * 
   * @param id id del ProduccionCientifica
   * @return el objeto ProduccionCientifica
   */
  private ProduccionCientifica generarMockProduccionCientifica(Long id) {
    return generarMockProduccionCientifica(id, "id-ref-" + id, EpigrafeCVN.E060_010_010_000);
  }

  /**
   * Función que devuelve un objeto ProduccionCientifica
   * 
   * @param id    id del ProduccionCientifica
   * @param idRef idRef del ProduccionCientifica
   * @return el objeto ProduccionCientifica
   */
  private ProduccionCientifica generarMockProduccionCientifica(Long id, String idRef) {
    return generarMockProduccionCientifica(id, idRef, EpigrafeCVN.E060_010_010_000);
  }

  /**
   * Función que devuelve un objeto ProduccionCientifica
   * 
   * @param id          id del ProduccionCientifica
   * @param idRef       idRef del ProduccionCientifica
   * @param epigrafeCVN epigrafeCVN del ProduccionCientifica
   * @return el objeto ProduccionCientifica
   */
  private ProduccionCientifica generarMockProduccionCientifica(Long id, String idRef, EpigrafeCVN epigrafeCVN) {
    ProduccionCientifica produccionCientifica = new ProduccionCientifica();
    produccionCientifica.setId(id);
    produccionCientifica.setProduccionCientificaRef(idRef);
    produccionCientifica.setEpigrafeCVN(epigrafeCVN);

    return produccionCientifica;
  }

  private PublicacionResumen generarMockPublicacionResumen(Long id, String idRef) {
    PublicacionResumen publicacion = new PublicacionResumen();
    publicacion.setId(id);
    publicacion.setProduccionCientificaRef("ProduccionCientifica" + idRef);
    publicacion.setEpigrafeCVN(EpigrafeCVN.E060_010_010_000);
    publicacion.setFechaPublicacion(Instant.now());
    publicacion.setTituloPublicacion("Título" + idRef);
    publicacion.setTipoProduccion("Produccion" + idRef);

    return publicacion;
  }

}

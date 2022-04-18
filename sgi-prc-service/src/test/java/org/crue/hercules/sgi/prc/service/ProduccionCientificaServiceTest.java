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
import org.crue.hercules.sgi.prc.dto.ActividadResumen;
import org.crue.hercules.sgi.prc.dto.ComiteEditorialResumen;
import org.crue.hercules.sgi.prc.dto.CongresoResumen;
import org.crue.hercules.sgi.prc.dto.DireccionTesisResumen;
import org.crue.hercules.sgi.prc.dto.ObraArtisticaResumen;
import org.crue.hercules.sgi.prc.dto.PublicacionResumen;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.exceptions.ProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
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
class ProduccionCientificaServiceTest extends BaseServiceTest {

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
  void findAll_ReturnsPage() {
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
  void findById_ReturnsProduccionCientifica() {
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
  void findById_WithIdNotExist_ThrowsProduccionCientificaNotFoundException() throws Exception {
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
  public void findAllComitesEditoriales_ReturnsPage() {
    // given: Una lista con 37 ComiteEditorialResumen
    List<ComiteEditorialResumen> comitesEditoriales = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      comitesEditoriales.add(generarMockComiteEditorialResumen(i, String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAllComitesEditoriales(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ComiteEditorialResumen>>() {
          @Override
          public Page<ComiteEditorialResumen> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > comitesEditoriales.size() ? comitesEditoriales.size() : toIndex;
            List<ComiteEditorialResumen> content = comitesEditoriales.subList(fromIndex, toIndex);
            Page<ComiteEditorialResumen> page = new PageImpl<>(content, pageable, comitesEditoriales.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ComiteEditorialResumen> page = service.findAllComitesEditoriales(null, paging);

    // then: Devuelve la pagina 3 con los ComiteEditorialResumen del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ComiteEditorialResumen comiteEditorial = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(comiteEditorial.getProduccionCientificaRef())
          .isEqualTo("ProduccionCientifica" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllCongresos_ReturnsPage() {
    // given: Una lista con 37 CongresoResumen
    List<CongresoResumen> congresos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      congresos.add(generarMockCongresoResumen(i, String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAllCongresos(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<CongresoResumen>>() {
          @Override
          public Page<CongresoResumen> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > congresos.size() ? congresos.size() : toIndex;
            List<CongresoResumen> content = congresos.subList(fromIndex, toIndex);
            Page<CongresoResumen> page = new PageImpl<>(content, pageable, congresos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<CongresoResumen> page = service.findAllCongresos(null, paging);

    // then: Devuelve la pagina 3 con los CongresoResumen del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      CongresoResumen congreso = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(congreso.getProduccionCientificaRef())
          .isEqualTo("ProduccionCientifica" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllObrasArtisticas_ReturnsPage() {
    // given: Una lista con 37 ObraArtisticaResumen
    List<ObraArtisticaResumen> obrasArtisticas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      obrasArtisticas.add(generarMockObraArtisticaResumen(i, String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAllObrasArtisticas(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ObraArtisticaResumen>>() {
          @Override
          public Page<ObraArtisticaResumen> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > obrasArtisticas.size() ? obrasArtisticas.size() : toIndex;
            List<ObraArtisticaResumen> content = obrasArtisticas.subList(fromIndex, toIndex);
            Page<ObraArtisticaResumen> page = new PageImpl<>(content, pageable, obrasArtisticas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ObraArtisticaResumen> page = service.findAllObrasArtisticas(null, paging);

    // then: Devuelve la pagina 3 con los ObraArtisticaResumen del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ObraArtisticaResumen obraArtistica = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(obraArtistica.getProduccionCientificaRef())
          .isEqualTo("ProduccionCientifica" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllActividades_ReturnsPage() {
    // given: Una lista con 37 ActividadResumen
    List<ActividadResumen> actividades = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      actividades.add(generarMockActividadResumen(i, String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAllActividades(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ActividadResumen>>() {
          @Override
          public Page<ActividadResumen> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > actividades.size() ? actividades.size() : toIndex;
            List<ActividadResumen> content = actividades.subList(fromIndex, toIndex);
            Page<ActividadResumen> page = new PageImpl<>(content, pageable, actividades.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ActividadResumen> page = service.findAllActividades(null, paging);

    // then: Devuelve la pagina 3 con los ActividadResumen del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ActividadResumen actividad = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(actividad.getProduccionCientificaRef())
          .isEqualTo("ProduccionCientifica" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllDireccionesTesis_ReturnsPage() {
    // given: Una lista con 37 DireccionTesisResumen
    List<DireccionTesisResumen> direccionesTesis = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      direccionesTesis.add(generarMockDireccionTesisResumen(i, String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAllDireccionesTesis(ArgumentMatchers.<String>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<DireccionTesisResumen>>() {
          @Override
          public Page<DireccionTesisResumen> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > direccionesTesis.size() ? direccionesTesis.size() : toIndex;
            List<DireccionTesisResumen> content = direccionesTesis.subList(fromIndex, toIndex);
            Page<DireccionTesisResumen> page = new PageImpl<>(content, pageable, direccionesTesis.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<DireccionTesisResumen> page = service.findAllDireccionesTesis(null, paging);

    // then: Devuelve la pagina 3 con los DireccionTesisResumen del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      DireccionTesisResumen direccionTesis = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(direccionTesis.getProduccionCientificaRef())
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

  private ComiteEditorialResumen generarMockComiteEditorialResumen(Long id, String idRef) {
    ComiteEditorialResumen comiteEditorial = new ComiteEditorialResumen();
    comiteEditorial.setId(id);
    comiteEditorial.setProduccionCientificaRef("ProduccionCientifica" + idRef);
    comiteEditorial.setEpigrafeCVN(EpigrafeCVN.E060_030_030_000);
    comiteEditorial.setFechaInicio(Instant.now());
    comiteEditorial.setNombre("Nombre" + idRef);

    return comiteEditorial;
  }

  private CongresoResumen generarMockCongresoResumen(Long id, String idRef) {
    CongresoResumen congreso = new CongresoResumen();
    congreso.setId(id);
    congreso.setProduccionCientificaRef("ProduccionCientifica" + idRef);
    congreso.setEpigrafeCVN(EpigrafeCVN.E060_010_020_000);
    congreso.setFechaCelebracion(Instant.now());
    congreso.setTipoEvento("Tipo-evento" + idRef);
    congreso.setTituloTrabajo("Titulo-trabajo" + idRef);

    return congreso;
  }

  private ObraArtisticaResumen generarMockObraArtisticaResumen(Long id, String idRef) {
    ObraArtisticaResumen obraArtistica = new ObraArtisticaResumen();
    obraArtistica.setId(id);
    obraArtistica.setProduccionCientificaRef("ProduccionCientifica" + idRef);
    obraArtistica.setEpigrafeCVN(EpigrafeCVN.E050_020_030_000);
    obraArtistica.setDescripcion("Descripcion-" + idRef);
    obraArtistica.setFechaInicio(Instant.now());

    return obraArtistica;
  }

  private ActividadResumen generarMockActividadResumen(Long id, String idRef) {
    ActividadResumen actividad = new ActividadResumen();
    actividad.setId(id);
    actividad.setProduccionCientificaRef("ProduccionCientifica" + idRef);
    actividad.setEpigrafeCVN(EpigrafeCVN.E060_020_030_000);
    actividad.setTituloActividad("Título" + idRef);
    actividad.setFechaInicio(Instant.now());

    return actividad;
  }

  private DireccionTesisResumen generarMockDireccionTesisResumen(Long id, String idRef) {
    DireccionTesisResumen actividad = new DireccionTesisResumen();
    actividad.setId(id);
    actividad.setProduccionCientificaRef("ProduccionCientifica" + idRef);
    actividad.setEpigrafeCVN(EpigrafeCVN.E030_040_000_000);
    actividad.setTituloTrabajo("Título" + idRef);
    actividad.setFechaDefensa(Instant.now());

    return actividad;
  }
}

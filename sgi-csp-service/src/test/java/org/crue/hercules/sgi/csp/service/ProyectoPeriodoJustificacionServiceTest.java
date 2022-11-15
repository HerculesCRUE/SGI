package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.validation.ValidationException;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoJustificacionNotDeleteableException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.Configuracion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
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
 * ProyectoPeriodoJustificacionServiceTest
 */
@Import({ ProyectoPeriodoJustificacionService.class, ApplicationContextSupport.class })
class ProyectoPeriodoJustificacionServiceTest extends BaseServiceTest {

  @MockBean
  private ProyectoPeriodoJustificacionRepository repository;
  @MockBean
  private ProyectoRepository proyectoRepository;
  @MockBean
  private ConfiguracionService configuracionService;
  @MockBean
  private RequerimientoJustificacionService requerimientoJustificacionService;
  @MockBean
  private EntityManager entityManager;
  @MockBean
  private EntityManagerFactory entityManagerFactory;
  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private ProyectoPeriodoJustificacionService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void findAllByProyectoSgeRef_ReturnsPage() {
    // given: Una lista con 37 ProyectoPeriodoJustificacion
    String proyectosSgeRef = "1";
    List<ProyectoPeriodoJustificacion> periodosJustificacion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      periodosJustificacion.add(generarMockProyectoPeriodoJustificacion(i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<ProyectoPeriodoJustificacion>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoPeriodoJustificacion>>() {
          @Override
          public Page<ProyectoPeriodoJustificacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > periodosJustificacion.size() ? periodosJustificacion.size() : toIndex;
            List<ProyectoPeriodoJustificacion> content = periodosJustificacion.subList(fromIndex, toIndex);
            Page<ProyectoPeriodoJustificacion> page = new PageImpl<>(content, pageable, periodosJustificacion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoPeriodoJustificacion> page = service.findAllByProyectoSgeRef(proyectosSgeRef, null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().hasSize()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoPeriodoJustificacion proyecto = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyecto.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  @Test
  void deleteById_WithIdNull_ThrowsIllegalArgumentException() {
    // given: id null
    Long id = null;
    // when: Eliminamos por id null
    // then: Lanza IllegalArgumentException porque el id no puede ser null
    Assertions.assertThatThrownBy(() -> service.delete(id))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void delete_WithIdNotExisting_ThrowsProyectoPeriodoJustificacionNotFoundException() {
    // given: id not existing
    Long id = 123L;
    // when: ProyectoPeriodoJustificacion can not be deleted
    BDDMockito
        .given(requerimientoJustificacionService.existsAnyByProyectoPeriodoJustificacionId(ArgumentMatchers.anyLong()))
        .willReturn(Boolean.TRUE);
    // then: Lanza IllegalArgumentException porque el id no puede ser null
    Assertions.assertThatThrownBy(() -> service.delete(id))
        .isInstanceOf(ProyectoPeriodoJustificacionNotFoundException.class);
  }

  @Test
  void delete_WithIdNotDeleteable_ThrowsProyectoPeriodoJustificacionNotDeleteableException() {
    // given: id not deleteable
    Long id = 123L;
    // when: ProyectoPeriodoJustificacion with id exists but can not be deleted
    BDDMockito
        .given(repository.existsById(ArgumentMatchers.anyLong()))
        .willReturn(Boolean.TRUE);
    BDDMockito
        .given(requerimientoJustificacionService.existsAnyByProyectoPeriodoJustificacionId(ArgumentMatchers.anyLong()))
        .willReturn(Boolean.TRUE);
    // then: Lanza IllegalArgumentException porque el id no puede ser null
    Assertions.assertThatThrownBy(() -> service.delete(id))
        .isInstanceOf(ProyectoPeriodoJustificacionNotDeleteableException.class);
  }

  @Test
  void updateIdentificadorJustificacion_WithIdNull_ThrowsIllegalArgumentException() {
    // given: Un ProyectoPeriodoJustificacion con id null
    Long idPeriodoJustificacion = null;
    ProyectoPeriodoJustificacion periodoJustificacion = generarMockProyectoPeriodoJustificacion(idPeriodoJustificacion);
    Assertions.assertThatThrownBy(() -> service.updateIdentificadorJustificacion(periodoJustificacion))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void updateIdentificadorJustificacion_WithRepeatedIdentificadorJustificacion_ThrowsValidationException() {
    // given: Dos ProyectoPeriodoJustificacion sin identificadorJustificacion
    // repetido
    Long idPeriodoJustificacion = 1L;
    Long idPeriodoJustificacionRepeatedIdJustificacion = 2L;
    String previousIdJustificacion = "33/1754";
    String repeatedIdJustificacion = "22/1965";
    ProyectoPeriodoJustificacion periodoJustificacionToUpdate = generarMockProyectoPeriodoJustificacion(
        idPeriodoJustificacion, previousIdJustificacion);
    ProyectoPeriodoJustificacion periodoJustificacionSameIdJustificacion = generarMockProyectoPeriodoJustificacion(
        idPeriodoJustificacionRepeatedIdJustificacion, repeatedIdJustificacion);

    // when: Actualizamos uno de los dos periodos para que tengan el mismo
    // identificadorJustificacion
    ProyectoPeriodoJustificacion periodoJustificacionNewData = generarMockProyectoPeriodoJustificacion(
        idPeriodoJustificacion, repeatedIdJustificacion);
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(periodoJustificacionToUpdate));
    BDDMockito.given(repository.findByIdentificadorJustificacion(ArgumentMatchers.anyString()))
        .willReturn(Optional.of(periodoJustificacionSameIdJustificacion));
    BDDMockito.given(configuracionService.findConfiguracion())
        .willReturn(generarMockConfiguracion());

    // then: Lanza una excepcion porque hay otro ProyectoPeriodoJustificacion con
    // ese identificadorJustificacion
    Assertions.assertThatThrownBy(() -> service.updateIdentificadorJustificacion(periodoJustificacionNewData))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void updateIdentificadorJustificacion_WithIdentificadorJustificacionFormatNotValid_ThrowsValidationException() {
    // given: un ProyectoPeriodoJustificacion
    Long idPeriodoJustificacion = 1L;
    String newIdJustificacionNotValidFormat = "221965";
    ProyectoPeriodoJustificacion periodoJustificacionToUpdate = generarMockProyectoPeriodoJustificacion(
        idPeriodoJustificacion);

    // when: Actualizamos el ProyectoPeriodoJustificacion con un id justificacion
    // con formato incorrecto
    ProyectoPeriodoJustificacion periodoJustificacionNewData = generarMockProyectoPeriodoJustificacion(
        idPeriodoJustificacion, newIdJustificacionNotValidFormat);
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(periodoJustificacionToUpdate));
    BDDMockito.given(configuracionService.findConfiguracion())
        .willReturn(generarMockConfiguracion());

    // then: Lanza una excepcion porque el identificador de justificacion no tiene
    // el formato correcto
    Assertions.assertThatThrownBy(() -> service.updateIdentificadorJustificacion(periodoJustificacionNewData))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void updateIdentificadorJustificacion_ReturnsProyectoPeriodoJustificacion() {
    // given: Un ProyectoPeriodoJustificacion
    Long idPeriodoJustificacion = 1L;
    String newIdJustificacion = "22/1965";
    ProyectoPeriodoJustificacion periodoJustificacionToUpdate = generarMockProyectoPeriodoJustificacion(
        idPeriodoJustificacion);

    // when: Actualizamos el ProyectoPeriodoJustificacion
    ProyectoPeriodoJustificacion periodoJustificacionNewData = generarMockProyectoPeriodoJustificacion(
        idPeriodoJustificacion, newIdJustificacion);
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(periodoJustificacionToUpdate));
    BDDMockito.given(configuracionService.findConfiguracion())
        .willReturn(generarMockConfiguracion());
    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoPeriodoJustificacion>any()))
        .willAnswer(new Answer<ProyectoPeriodoJustificacion>() {
          @Override
          public ProyectoPeriodoJustificacion answer(InvocationOnMock invocation) throws Throwable {
            ProyectoPeriodoJustificacion givenData = invocation.getArgument(0, ProyectoPeriodoJustificacion.class);
            periodoJustificacionToUpdate.setIdentificadorJustificacion(givenData.getIdentificadorJustificacion());
            periodoJustificacionToUpdate
                .setFechaPresentacionJustificacion(givenData.getFechaPresentacionJustificacion());
            return periodoJustificacionToUpdate;
          }
        });

    // then: ProyectoPeriodoJustificacion actualizado correctamente
    ProyectoPeriodoJustificacion proyectoPeriodoJustificacionUpdated = service
        .updateIdentificadorJustificacion(periodoJustificacionNewData);

    Assertions.assertThat(proyectoPeriodoJustificacionUpdated).isNotNull();
    Assertions.assertThat(proyectoPeriodoJustificacionUpdated.getIdentificadorJustificacion())
        .as("getIdentificadorJustificacion()").isEqualTo(newIdJustificacion);
  }

  @Test
  void findByIdentificadorJustificacion_WithIdentificadorJustificacionNull_ThrowsIllegalArgumentException() {
    // given: Un Identificador de justificacion nulo
    String identificadorJustificacion = null;

    // when: Buscamos un ProyectoPeriodoJustificacion con identificadorJustificacion
    // then: Obtenemos una excepcion indicando que el identificadorJustificacion no
    // puede ser nulo
    Assertions.assertThatThrownBy(() -> service.findByIdentificadorJustificacion(identificadorJustificacion))
        .isInstanceOf(IllegalArgumentException.class);
  }

  private ProyectoPeriodoJustificacion generarMockProyectoPeriodoJustificacion(Long id) {
    final String observacionesSuffix = id != null ? String.format("%03d", id) : "001";
    return generarMockProyectoPeriodoJustificacion(id, "XX/AAAA", "observaciones-" + observacionesSuffix);
  }

  private ProyectoPeriodoJustificacion generarMockProyectoPeriodoJustificacion(Long id, String idJustificacion) {
    final String observacionesSuffix = id != null ? String.format("%03d", id) : "001";
    return generarMockProyectoPeriodoJustificacion(id, idJustificacion, "observaciones-" + observacionesSuffix);
  }

  private ProyectoPeriodoJustificacion generarMockProyectoPeriodoJustificacion(Long id, String idJustificacion,
      String observaciones) {
    return ProyectoPeriodoJustificacion.builder()
        .id(id)
        .identificadorJustificacion(idJustificacion)
        .observaciones(observaciones)
        .build();
  }

  private Configuracion generarMockConfiguracion() {
    return Configuracion.builder()
        .formatoIdentificadorJustificacion("^[0-9]{2}\\/[0-9]{4}$")
        .build();
  }
}

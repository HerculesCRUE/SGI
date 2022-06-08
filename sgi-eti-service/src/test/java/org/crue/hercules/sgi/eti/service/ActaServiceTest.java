package org.crue.hercules.sgi.eti.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.ActaWithNumEvaluaciones;
import org.crue.hercules.sgi.eti.exceptions.ActaNotFoundException;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.EstadoActa;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.crue.hercules.sgi.eti.repository.ActaRepository;
import org.crue.hercules.sgi.eti.repository.EstadoActaRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.RetrospectivaRepository;
import org.crue.hercules.sgi.eti.repository.TipoEstadoActaRepository;
import org.crue.hercules.sgi.eti.repository.custom.CustomActaRepository;
import org.crue.hercules.sgi.eti.service.impl.ActaServiceImpl;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiRepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * ActaServiceTest
 */
public class ActaServiceTest extends BaseServiceTest {

  @Mock
  private ActaRepository actaRepository;
  @Mock
  private CustomActaRepository customActaRepository;
  @Mock
  private EstadoActaRepository estadoActaRepository;
  @Mock
  private EvaluacionRepository evaluacionRepository;
  @Mock
  private MemoriaRepository memoriaRepository;
  @Mock
  private TipoEstadoActaRepository tipoEstadoActaRepository;
  @Mock
  private RetrospectivaRepository retrospectivaRepository;
  @Mock
  private RetrospectivaService retrospectivaService;
  @Mock
  private SgiApiRepService reportService;
  @Mock
  private SgdocService sgdocService;
  @Mock
  private ComunicadosService comunicadosService;

  private ActaService actaService;
  private MemoriaService memoriaService;

  @BeforeEach
  public void setUp() throws Exception {
    actaService = new ActaServiceImpl(actaRepository, estadoActaRepository, tipoEstadoActaRepository,
        evaluacionRepository, retrospectivaRepository, memoriaService, retrospectivaService, reportService,
        sgdocService, comunicadosService);
  }

  @Test
  public void find_WithId_ReturnsActa() {
    BDDMockito.given(actaRepository.findById(1L)).willReturn(Optional.of(generarMockActa(1L, 123)));

    Acta acta = actaService.findById(1L);

    Assertions.assertThat(acta.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(acta.getConvocatoriaReunion().getId()).as("convocatoriaReunion.id").isEqualTo(100L);
    Assertions.assertThat(acta.getHoraInicio()).as("horaInicio").isEqualTo(10);
    Assertions.assertThat(acta.getMinutoInicio()).as("minutoInicio").isEqualTo(15);
    Assertions.assertThat(acta.getHoraFin()).as("horaFin").isEqualTo(12);
    Assertions.assertThat(acta.getMinutoFin()).as("minutoFin").isEqualTo(0);
    Assertions.assertThat(acta.getResumen()).as("resumen").isEqualTo("Resumen123");
    Assertions.assertThat(acta.getNumero()).as("numero").isEqualTo(123);
    Assertions.assertThat(acta.getEstadoActual().getId()).as("estadoActual.id").isEqualTo(1);
    Assertions.assertThat(acta.getInactiva()).as("inactiva").isEqualTo(true);
    Assertions.assertThat(acta.getActivo()).as("activo").isEqualTo(true);
  }

  @Test
  public void find_NotFound_ThrowsActaNotFoundException() throws Exception {
    BDDMockito.given(actaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> actaService.findById(1L)).isInstanceOf(ActaNotFoundException.class);
  }

  @Test
  public void create_ReturnsActa() {
    // given: Un nuevo Acta
    Acta actaNew = generarMockActa(null, 123);

    Acta acta = generarMockActa(1L, 123);

    BDDMockito.given(actaRepository.save(actaNew)).willReturn(acta);
    BDDMockito.given(tipoEstadoActaRepository.findById(1L)).willReturn(Optional.of(acta.getEstadoActual()));
    BDDMockito.given(estadoActaRepository.save(ArgumentMatchers.any(EstadoActa.class)))
        .willReturn(new EstadoActa(1L, acta, acta.getEstadoActual(), Instant.now()));

    // when: Creamos el acta
    Acta actaCreado = actaService.create(actaNew);

    // then: el acta se crea correctamente
    Assertions.assertThat(actaCreado).isNotNull();
    Assertions.assertThat(actaCreado.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(actaCreado.getConvocatoriaReunion().getId()).as("convocatoriaReunion.id").isEqualTo(100L);
    Assertions.assertThat(actaCreado.getHoraInicio()).as("horaInicio").isEqualTo(10);
    Assertions.assertThat(actaCreado.getMinutoInicio()).as("minutoInicio").isEqualTo(15);
    Assertions.assertThat(actaCreado.getHoraFin()).as("horaFin").isEqualTo(12);
    Assertions.assertThat(actaCreado.getMinutoFin()).as("minutoFin").isEqualTo(0);
    Assertions.assertThat(actaCreado.getResumen()).as("resumen").isEqualTo("Resumen123");
    Assertions.assertThat(actaCreado.getNumero()).as("numero").isEqualTo(123);
    Assertions.assertThat(actaCreado.getEstadoActual().getId()).as("estadoActual.id").isEqualTo(1);
    Assertions.assertThat(actaCreado.getInactiva()).as("inactiva").isEqualTo(true);
    Assertions.assertThat(actaCreado.getActivo()).as("activo").isEqualTo(true);
  }

  @Test
  public void create_ActaWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo acta que ya tiene id
    Acta actaNew = generarMockActa(1L, 123);
    // when: Creamos el acta
    // then: Lanza una excepcion porque el acta ya tiene id
    Assertions.assertThatThrownBy(() -> actaService.create(actaNew)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsActa() {

    // given: Un nuevo acta con el resumen actualizado
    Acta actaResumenActualizado = generarMockActa(1L, 123);
    actaResumenActualizado.setResumen("Resumen actualizado");

    Acta acta = generarMockActa(1L, 123);

    BDDMockito.given(actaRepository.findById(1L)).willReturn(Optional.of(acta));
    BDDMockito.given(actaRepository.save(actaResumenActualizado)).willReturn(actaResumenActualizado);

    // when: Actualizamos el acta
    Acta actaActualizado = actaService.update(actaResumenActualizado);

    // then: El acta se actualiza correctamente.
    Assertions.assertThat(actaActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(actaActualizado.getResumen()).isEqualTo("Resumen actualizado");

  }

  @Test
  public void update_ThrowsActaNotFoundException() {
    // given: Un acta a actualizar
    Acta actaResumenActualizado = generarMockActa(1L, 123);

    // then: Lanza una excepcion porque el acta no existe
    Assertions.assertThatThrownBy(() -> actaService.update(actaResumenActualizado))
        .isInstanceOf(ActaNotFoundException.class);
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: Un acta que venga sin id
    Acta actaTipoActualizado = generarMockActa(null, 123);

    Assertions.assertThatThrownBy(
        // when: update acta
        () -> actaService.update(actaTipoActualizado))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> actaService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsActaNotFoundException() {
    // given: Id no existe
    BDDMockito.given(actaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> actaService.delete(1L))
        // then: Lanza ActaNotFoundException
        .isInstanceOf(ActaNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesActa() {
    // given: Id existente
    BDDMockito.given(actaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(actaRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> actaService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void findAll_Unlimited_ReturnsFullActaWithNumEvaluacionesList() {
    // given: One hundred actas
    List<ActaWithNumEvaluaciones> actas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      actas.add(generarMockActaWithNumEvaluaciones(Long.valueOf(i), i));
    }

    BDDMockito.given(actaRepository.findAllActaWithNumEvaluaciones(ArgumentMatchers.<Specification<Acta>>any(),
        ArgumentMatchers.<Pageable>any(), ArgumentMatchers.<String>any())).willReturn(new PageImpl<>(actas));

    // when: find unlimited
    Page<ActaWithNumEvaluaciones> page = actaService.findAllActaWithNumEvaluaciones(null, Pageable.unpaged(), null);

    // then: Get a page with one hundred actas
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    Assertions.assertThat(page.getContent()).isEqualTo(actas);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred actas
    List<ActaWithNumEvaluaciones> actas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      actas.add(generarMockActaWithNumEvaluaciones(Long.valueOf(i), i));
    }

    BDDMockito
        .given(actaRepository.findAllActaWithNumEvaluaciones(ArgumentMatchers.<Specification<Acta>>any(),
            ArgumentMatchers.<Pageable>any(), ArgumentMatchers.<String>any()))
        .willAnswer(new Answer<Page<ActaWithNumEvaluaciones>>() {
          @Override
          public Page<ActaWithNumEvaluaciones> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<ActaWithNumEvaluaciones> content = actas.subList(fromIndex, toIndex);
            Page<ActaWithNumEvaluaciones> page = new PageImpl<>(content, pageable, actas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ActaWithNumEvaluaciones> page = actaService.findAllActaWithNumEvaluaciones(null, paging, null);

    // then: A Page with ten actas are returned containing id='31' to '40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      ActaWithNumEvaluaciones acta = page.getContent().get(i);
      Assertions.assertThat(acta.getId()).isEqualTo(j);
    }
  }

  @Test
  public void finishActa_ThrowsActaNotFoundException() {

    BDDMockito.given(actaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> actaService.finishActa(1L))
        // then: Lanza ActaNotFoundException
        .isInstanceOf(ActaNotFoundException.class);

  }

  @Test
  public void finishActa_Success() {

    Acta acta = generarMockActa(1L, 123);

    BDDMockito.given(actaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(acta));

    BDDMockito
        .given(evaluacionRepository.findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong()))
        .willReturn(Collections.emptyList());

    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa(9L, "Finalizada", Boolean.TRUE);

    EstadoActa estadoActa = new EstadoActa(1L, acta, tipoEstadoActa, Instant.parse("2020-08-01T00:00:00Z"));

    BDDMockito.given(estadoActaRepository.save(ArgumentMatchers.any(EstadoActa.class))).willReturn(estadoActa);

    acta.setEstadoActual(tipoEstadoActa);

    // when: Actualizamos el acta
    BDDMockito.given(actaRepository.save(ArgumentMatchers.<Acta>any())).willReturn(acta);

    actaService.finishActa(1L);

    Mockito.verify(actaRepository, Mockito.times(1)).save(ArgumentMatchers.<Acta>any());

  }

  /**
   * Función que devuelve un objeto Acta
   * 
   * @param id     id del acta
   * @param numero numero del acta
   * @return el objeto Acta
   */
  public Acta generarMockActa(Long id, Integer numero) {
    Comite comite = new Comite();
    comite.setId(1L);
    comite.setComite("CEEA");
    comite.setGenero(Genero.M);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);
    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(100L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.parse("2020-08-01T00:00:00Z"));
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);

    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(1L);
    tipoEstadoActa.setNombre("En elaboración");
    tipoEstadoActa.setActivo(Boolean.TRUE);

    Acta acta = new Acta();
    acta.setId(id);
    acta.setConvocatoriaReunion(convocatoriaReunion);
    acta.setHoraInicio(10);
    acta.setMinutoInicio(15);
    acta.setHoraFin(12);
    acta.setMinutoFin(0);
    acta.setResumen("Resumen" + numero);
    acta.setNumero(numero);
    acta.setEstadoActual(tipoEstadoActa);
    acta.setInactiva(true);
    acta.setActivo(true);

    return acta;
  }

  /**
   * Función que devuelve un objeto ActaWithNumEvaluaciones
   * 
   * @param id     id del acta
   * @param numero numero del acta
   * @return el objeto Acta
   */
  public ActaWithNumEvaluaciones generarMockActaWithNumEvaluaciones(Long id, Integer numero) {
    Acta acta = generarMockActa(id, numero);

    ActaWithNumEvaluaciones returnValue = new ActaWithNumEvaluaciones();
    returnValue.setId(acta.getId());
    returnValue.setComite(acta.getConvocatoriaReunion().getComite().getComite());
    returnValue.setFechaEvaluacion(acta.getConvocatoriaReunion().getFechaEvaluacion());
    returnValue.setNumeroActa(acta.getNumero());
    returnValue.setConvocatoria(acta.getConvocatoriaReunion().getTipoConvocatoriaReunion().getNombre());
    returnValue.setNumEvaluaciones(1);
    returnValue.setNumRevisiones(2);
    returnValue.setNumTotal(returnValue.getNumEvaluaciones() + returnValue.getNumRevisiones());
    returnValue.setEstadoActa(acta.getEstadoActual());
    return returnValue;
  }

}

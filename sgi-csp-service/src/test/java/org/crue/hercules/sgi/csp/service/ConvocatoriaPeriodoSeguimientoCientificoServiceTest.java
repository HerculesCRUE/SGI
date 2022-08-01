package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaPeriodoSeguimientoCientificoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.PeriodoLongerThanConvocatoriaException;
import org.crue.hercules.sgi.csp.exceptions.PeriodoWrongOrderException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoSeguimientoCientificoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaPeriodoSeguimientoCientificoServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
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
import org.springframework.security.test.context.support.WithMockUser;

@Import({ ConvocatoriaPeriodoSeguimientoCientificoServiceImpl.class })
class ConvocatoriaPeriodoSeguimientoCientificoServiceTest extends BaseServiceTest {

  @MockBean
  private ConvocatoriaPeriodoSeguimientoCientificoRepository repository;
  @MockBean
  private ConvocatoriaRepository convocatoriaRepository;
  @MockBean
  private ConfiguracionSolicitudRepository configuracionSolicitudRepository;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private ConvocatoriaPeriodoSeguimientoCientificoService service;

  @Test
  void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_ReturnsConvocatoriaPeriodoSeguimientoCientificoList() {
    // given: una lista con uno de los ConvocatoriaPeriodoSeguimientoCientifico
    // actualizado, otro nuevo y sin el otros existente
    Long convocatoriaId = 1L;

    List<ConvocatoriaPeriodoSeguimientoCientifico> peridosJustificiacionExistentes = new ArrayList<>();
    peridosJustificiacionExistentes
        .add(generarMockConvocatoriaPeriodoSeguimientoCientifico(2L, 5, 10, 1L, TipoSeguimiento.PERIODICO));
    peridosJustificiacionExistentes
        .add(generarMockConvocatoriaPeriodoSeguimientoCientifico(4L, 11, 15, 1L, TipoSeguimiento.INTERMEDIO));
    peridosJustificiacionExistentes
        .add(generarMockConvocatoriaPeriodoSeguimientoCientifico(5L, 20, 25, 1L, TipoSeguimiento.FINAL));

    ConvocatoriaPeriodoSeguimientoCientifico newConvocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        null, 1, 10, 1L, TipoSeguimiento.INTERMEDIO);
    ConvocatoriaPeriodoSeguimientoCientifico updatedConvocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        4L, 11, 19, 1L, TipoSeguimiento.FINAL);

    List<ConvocatoriaPeriodoSeguimientoCientifico> peridosJustificiacionActualizar = new ArrayList<>();
    peridosJustificiacionActualizar.add(newConvocatoriaPeriodoSeguimientoCientifico);
    peridosJustificiacionActualizar.add(updatedConvocatoriaPeriodoSeguimientoCientifico);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(convocatoriaId)));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(peridosJustificiacionExistentes);

    BDDMockito.doNothing().when(repository)
        .deleteAll(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>anyList());

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<ConvocatoriaPeriodoSeguimientoCientifico> periodoSeguimientoCientificos = invocation.getArgument(0);
          return periodoSeguimientoCientificos.stream().map(periodoSeguimientoCientifico -> {
            if (periodoSeguimientoCientifico.getId() == null) {
              periodoSeguimientoCientifico.setId(6L);
            }
            periodoSeguimientoCientifico.setConvocatoriaId(convocatoriaId);
            return periodoSeguimientoCientifico;
          }).collect(Collectors.toList());
        });

    // when: updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria
    List<ConvocatoriaPeriodoSeguimientoCientifico> periodosSeguimientoCientificoActualizados = service
        .updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(convocatoriaId, peridosJustificiacionActualizar);

    // then: Se crea el nuevo ConvocatoriaPeriodoSeguimientoCientifico, se actualiza
    // el existe y se elimina el otro
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(0).getId()).as("get(0).getId()").isEqualTo(6L);
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(0).getConvocatoriaId())
        .as("get(0).getConvocatoriaId()").isEqualTo(convocatoriaId);
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(0).getMesInicial()).as("get(0).getMesInicial()")
        .isEqualTo(newConvocatoriaPeriodoSeguimientoCientifico.getMesInicial());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(0).getMesFinal()).as("get(0).getMesFinal()")
        .isEqualTo(newConvocatoriaPeriodoSeguimientoCientifico.getMesFinal());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(0).getFechaInicioPresentacion())
        .as("get(0).getFechaInicioPresentacion()")
        .isEqualTo(newConvocatoriaPeriodoSeguimientoCientifico.getFechaInicioPresentacion());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(0).getFechaFinPresentacion())
        .as("get(0).getFechaFinPresentacion()")
        .isEqualTo(newConvocatoriaPeriodoSeguimientoCientifico.getFechaFinPresentacion());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(0).getNumPeriodo()).as("get(0).getNumPeriodo()")
        .isEqualTo(1);
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(0).getObservaciones())
        .as("get(0).getObservaciones()").isEqualTo(newConvocatoriaPeriodoSeguimientoCientifico.getObservaciones());

    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(1).getId()).as("get(1).getId()")
        .isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getId());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(1).getConvocatoriaId())
        .as("get(1).getConvocatoriaId()").isEqualTo(convocatoriaId);
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(1).getMesInicial()).as("get(1).getMesInicial()")
        .isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getMesInicial());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(1).getMesFinal()).as("get(1).getMesFinal()")
        .isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getMesFinal());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(1).getFechaInicioPresentacion())
        .as("get(1).getFechaInicioPresentacion()")
        .isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getFechaInicioPresentacion());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(1).getFechaFinPresentacion())
        .as("get(1).getFechaFinPresentacion()")
        .isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getFechaFinPresentacion());
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(1).getNumPeriodo()).as("get(1).getNumPeriodo()")
        .isEqualTo(2);
    Assertions.assertThat(periodosSeguimientoCientificoActualizados.get(1).getObservaciones())
        .as("get(1).getObservaciones()").isEqualTo(updatedConvocatoriaPeriodoSeguimientoCientifico.getObservaciones());

    Mockito.verify(repository, Mockito.times(1))
        .deleteAll(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>anyList());
    Mockito.verify(repository, Mockito.times(1))
        .saveAll(ArgumentMatchers.<ConvocatoriaPeriodoSeguimientoCientifico>anyList());
  }

  @Test
  void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_WithNoExistingConvocatoria_ThrowsConvocatoriaNotFoundException() {
    // given: a ConvocatoriaEntidadGestora with non existing Convocatoria
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        1L);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadGestora
        () -> service.updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoSeguimientoCientifico)))
        // then: throw exception as Convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_WithIdNotExist_ThrowsConvocatoriaPeriodoSeguimientoCientificoNotFoundException() {
    // given: Un ConvocatoriaPeriodoSeguimientoCientifico a actualizar con un id que
    // no existe
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        1L);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(convocatoriaId)));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(new ArrayList<>());

    // when:updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria
    // then: Lanza una excepcion porque el ConvocatoriaPeriodoSeguimientoCientifico
    // no existe
    Assertions
        .assertThatThrownBy(() -> service.updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoSeguimientoCientifico)))
        .isInstanceOf(ConvocatoriaPeriodoSeguimientoCientificoNotFoundException.class);
  }

  @Test
  void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_WithMesFinalLowerThanMesInicial_ThrowsConstraintViolationException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with mesFinal lower than
    // mesInicial
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        1L);
    convocatoriaPeriodoSeguimientoCientifico.setMesInicial(convocatoriaPeriodoSeguimientoCientifico.getMesFinal() + 1);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(convocatoriaId)));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(convocatoriaPeriodoSeguimientoCientifico));

    Assertions.assertThatThrownBy(
        // when: updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria
        () -> service.updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoSeguimientoCientifico)))
        // then: throw exception
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("End month must be bigger or equal than initial month");
  }

  @Test
  void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_WithFechaFinBeforeFechaInicio_ThrowsConstraintViolationException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with FechaFinPresentacion
    // before FechaInicioPresentacion
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        1L);
    convocatoriaPeriodoSeguimientoCientifico.setFechaInicioPresentacion(
        convocatoriaPeriodoSeguimientoCientifico.getFechaFinPresentacion().plus(Period.ofDays(1)));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(convocatoriaId)));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(convocatoriaPeriodoSeguimientoCientifico));

    Assertions.assertThatThrownBy(
        // when: updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria
        () -> service.updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoSeguimientoCientifico)))
        // then: throw exception
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("End date must be bigger or equal than initial date");
  }

  @Test
  void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_WithMesFinalGreaterThanDuracionConvocatoria_ThrowsPeriodoLongerThanConvocatoriaException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with mesFinal greater than
    // duracion convocatoria
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        1L);

    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    convocatoria.setDuracion(convocatoriaPeriodoSeguimientoCientifico.getMesFinal() - 1);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(convocatoriaPeriodoSeguimientoCientifico));

    Assertions.assertThatThrownBy(
        // when: updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria
        () -> service.updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoSeguimientoCientifico)))
        // then: throw exception
        .isInstanceOf(PeriodoLongerThanConvocatoriaException.class)
        .hasMessage("The Period goes beyond the duration of the Call");
  }

  @Test
  void updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria_WithMesSolapado_ThrowsPeriodoWrongOrderException() {
    // given: a ConvocatoriaPeriodoSeguimientoCientifico with mesFinal greater than
    // duracion convocatoria
    Long convocatoriaId = 1L;
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico1 = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        1L, 1, 10, 1L, TipoSeguimiento.PERIODICO);
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico2 = generarMockConvocatoriaPeriodoSeguimientoCientifico(
        2L, 8, 15, 1L, TipoSeguimiento.FINAL);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoria(convocatoriaId)));

    BDDMockito.given(repository.findAllByConvocatoriaIdOrderByMesInicial(ArgumentMatchers.anyLong())).willReturn(
        Arrays.asList(convocatoriaPeriodoSeguimientoCientifico1, convocatoriaPeriodoSeguimientoCientifico2));

    Assertions.assertThatThrownBy(
        // when: updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria
        () -> service.updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(convocatoriaId,
            Arrays.asList(convocatoriaPeriodoSeguimientoCientifico1, convocatoriaPeriodoSeguimientoCientifico2)))
        // then: throw exception
        .isInstanceOf(PeriodoWrongOrderException.class).hasMessageContaining(
            "The first Period must start in month 1 and all Periods must be consecutive, with no gaps");
  }

  @Test
  void findById_WithExistingId_ReturnsConvocatoriaPeriodoSeguimientoCientifico() throws Exception {
    // given: existing ConvocatoriaPeriodoSeguimientoCientifico
    // @formatter:off
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
        .builder()
        .id(1L)
        .convocatoriaId(1L)
        .mesInicial(1)
        .mesFinal(2)
        .fechaInicioPresentacion(Instant.parse("2020-02-01T00:00:00Z"))
        .fechaFinPresentacion(Instant.parse("2020-01-01T00:00:00Z"))
        .build();
    // @formatter:on

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaPeriodoSeguimientoCientifico));

    // when: find by id ConvocatoriaPeriodoSeguimientoCientifico
    ConvocatoriaPeriodoSeguimientoCientifico data = service.findById(convocatoriaPeriodoSeguimientoCientifico.getId());

    // then: returns ConvocatoriaPeriodoSeguimientoCientifico
    Assertions.assertThat(data).isNotNull();
    Assertions.assertThat(data.getId()).isNotNull();
    Assertions.assertThat(data.getId()).isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getId());
    Assertions.assertThat(data.getConvocatoriaId())
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getConvocatoriaId());
    Assertions.assertThat(data.getNumPeriodo()).isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getNumPeriodo());
    Assertions.assertThat(data.getMesInicial()).isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getMesInicial());
    Assertions.assertThat(data.getMesFinal()).isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getMesFinal());
    Assertions.assertThat(data.getFechaInicioPresentacion())
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getFechaInicioPresentacion());
    Assertions.assertThat(data.getFechaFinPresentacion())
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getFechaFinPresentacion());
    Assertions.assertThat(data.getObservaciones())
        .isEqualTo(convocatoriaPeriodoSeguimientoCientifico.getObservaciones());

  }

  @Test
  void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaPeriodoSeguimientoCientificoNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void findAllByConvocatoria_WithPaging_ReturnsPage() {
    // given: One hundred ConvocatoriaPeriodoSeguimientoCientifico
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaId, 1L);
    List<ConvocatoriaPeriodoSeguimientoCientifico> listaConvocatoriaPeriodoSeguimientoCientifico = new LinkedList<ConvocatoriaPeriodoSeguimientoCientifico>();

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));
    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(configuracionSolicitud));
    for (int i = 1, j = 2; i <= 100; i++, j += 2) {
      // @formatter:off
      listaConvocatoriaPeriodoSeguimientoCientifico.add(ConvocatoriaPeriodoSeguimientoCientifico
          .builder()
          .id(Long.valueOf(i))
          .convocatoriaId(convocatoriaId)
          .numPeriodo(i - 1)
          .mesInicial((i * 2) - 1)
          .mesFinal(j * 1)
          .observaciones("observaciones-" + i)
          .build());
      // @formatter:on
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaPeriodoSeguimientoCientifico>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ConvocatoriaPeriodoSeguimientoCientifico>>() {
          @Override
          public Page<ConvocatoriaPeriodoSeguimientoCientifico> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<ConvocatoriaPeriodoSeguimientoCientifico> content = listaConvocatoriaPeriodoSeguimientoCientifico
                .subList(fromIndex, toIndex);
            Page<ConvocatoriaPeriodoSeguimientoCientifico> page = new PageImpl<>(content, pageable,
                listaConvocatoriaPeriodoSeguimientoCientifico.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConvocatoriaPeriodoSeguimientoCientifico> page = service.findAllByConvocatoria(convocatoriaId, null, paging);

    // then: A Page with ten ConvocatoriaPeriodoSeguimientoCientifico are returned
    // containing
    // obsrvaciones='observaciones-31' to
    // 'observaciones-40'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page).hasSize(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 31; i < 10; i++) {
      ConvocatoriaPeriodoSeguimientoCientifico item = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(item.getId()).isEqualTo(Long.valueOf(i));
    }
  }

  /**
   * Función que devuelve un objeto ConvocatoriaPeriodoSeguimientoCientifico
   * 
   * @param id id del ConvocatoriaPeriodoSeguimientoCientifico
   * @return el objeto ConvocatoriaPeriodoSeguimientoCientifico
   */
  private ConvocatoriaPeriodoSeguimientoCientifico generarMockConvocatoriaPeriodoSeguimientoCientifico(Long id) {
    return generarMockConvocatoriaPeriodoSeguimientoCientifico(id, 1, 2, id, TipoSeguimiento.FINAL);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaPeriodoSeguimientoCientifico
   * 
   * @param id             id del ConvocatoriaPeriodoSeguimientoCientifico
   * @param mesInicial     Mes inicial
   * @param mesFinal       Mes final
   * @param tipo           Tipo SeguimientoCientifico
   * @param convocatoriaId Id Convocatoria
   * @return el objeto ConvocatoriaPeriodoSeguimientoCientifico
   */
  private ConvocatoriaPeriodoSeguimientoCientifico generarMockConvocatoriaPeriodoSeguimientoCientifico(Long id,
      Integer mesInicial, Integer mesFinal, Long convocatoriaId, TipoSeguimiento tipoSeguimiento) {
    ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = new ConvocatoriaPeriodoSeguimientoCientifico();
    convocatoriaPeriodoSeguimientoCientifico.setId(id);
    convocatoriaPeriodoSeguimientoCientifico.setConvocatoriaId(convocatoriaId == null ? 1 : convocatoriaId);
    convocatoriaPeriodoSeguimientoCientifico.setNumPeriodo(1);
    convocatoriaPeriodoSeguimientoCientifico.setMesInicial(mesInicial);
    convocatoriaPeriodoSeguimientoCientifico.setMesFinal(mesFinal);
    convocatoriaPeriodoSeguimientoCientifico.setFechaInicioPresentacion(Instant.parse("2020-10-10T00:00:00Z"));
    convocatoriaPeriodoSeguimientoCientifico.setFechaFinPresentacion(Instant.parse("2020-11-20T23:59:59Z"));
    convocatoriaPeriodoSeguimientoCientifico.setObservaciones("observaciones-" + id);
    convocatoriaPeriodoSeguimientoCientifico.setTipoSeguimiento(tipoSeguimiento);

    return convocatoriaPeriodoSeguimientoCientifico;
  }

  /**
   * Función que devuelve un objeto Convocatoria
   * 
   * @param id id del Convocatoria
   * @return el objeto Convocatoria
   */
  private Convocatoria generarMockConvocatoria(Long id) {
    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(id == null ? 1 : id);

    return convocatoria;
  }

  /**
   * Genera un objeto ConfiguracionSolicitud
   * 
   * @param configuracionSolicitudId
   * @param convocatoriaId
   * @param convocatoriaFaseId
   * @return
   */
  private ConfiguracionSolicitud generarMockConfiguracionSolicitud(Long configuracionSolicitudId, Long convocatoriaId,
      Long convocatoriaFaseId) {
    // @formatter:off
    TipoFase tipoFase = TipoFase.builder()
        .id(convocatoriaFaseId)
        .nombre("nombre-1")
        .activo(Boolean.TRUE)
        .build();

    ConvocatoriaFase convocatoriaFase = ConvocatoriaFase.builder()
        .id(convocatoriaFaseId)
        .convocatoriaId(convocatoriaId)
        .tipoFase(tipoFase)
        .fechaInicio(Instant.parse("2020-10-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-10-15T00:00:00Z"))
        .observaciones("observaciones")
        .build();

    ConfiguracionSolicitud configuracionSolicitud = ConfiguracionSolicitud.builder()
        .id(configuracionSolicitudId)
        .convocatoriaId(convocatoriaId)
        .tramitacionSGI(Boolean.TRUE)
        .fasePresentacionSolicitudes(convocatoriaFase)
        .importeMaximoSolicitud(BigDecimal.valueOf(12345))
        .build();
    // @formatter:on

    return configuracionSolicitud;
  }

}

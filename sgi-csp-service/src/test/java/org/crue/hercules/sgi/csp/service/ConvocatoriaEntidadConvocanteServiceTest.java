package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.ProgramaNombre;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadConvocanteRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaEntidadConvocanteServiceImpl;
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * ConvocatoriaEntidadConvocanteServiceTest
 */
class ConvocatoriaEntidadConvocanteServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaEntidadConvocanteRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ProgramaRepository programaRepository;
  @Mock
  private ConvocatoriaService convocatoriaService;
  @Mock
  private ConfiguracionSolicitudRepository configuracionSolicitudRepository;
  @Mock
  private SolicitudRepository solicitudRepository;

  private ConvocatoriaAuthorityHelper authorityHelper;
  private ConvocatoriaEntidadConvocanteService service;

  @BeforeEach
  void setUp() throws Exception {
    this.authorityHelper = new ConvocatoriaAuthorityHelper(convocatoriaRepository, configuracionSolicitudRepository);
    service = new ConvocatoriaEntidadConvocanteServiceImpl(repository, convocatoriaRepository, programaRepository,
        convocatoriaService, solicitudRepository, authorityHelper);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void findAllByConvocatoria_ReturnsPage() {
    // given: Una lista con 37 ConvocatoriaEntidadGestora para la Convocatoria
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    List<ConvocatoriaEntidadConvocante> convocatoriasEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesConvocantes.add(generarMockConvocatoriaEntidadConvocante(i, convocatoriaId));
    }
    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaEntidadConvocante>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > convocatoriasEntidadesConvocantes.size() ? convocatoriasEntidadesConvocantes.size()
              : toIndex;
          List<ConvocatoriaEntidadConvocante> content = convocatoriasEntidadesConvocantes.subList(fromIndex, toIndex);
          Page<ConvocatoriaEntidadConvocante> pageResponse = new PageImpl<>(content, pageable,
              convocatoriasEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConvocatoriaEntidadConvocante> page = service.findAllByConvocatoria(convocatoriaId, null, paging);

    // then: Devuelve la pagina 3 con los ConvocatoriaEntidadConvocante del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(convocatoriaEntidadConvocante.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(convocatoriaEntidadConvocante.getEntidadRef()).isEqualTo("entidad-" + i);
    }
  }

  private Convocatoria generarMockConvocatoria(Long convocatoriaId) {
    return Convocatoria.builder()
        .id(convocatoriaId)
        .activo(true)
        .formularioSolicitud(FormularioSolicitud.PROYECTO)
        .build();
  }

  /**
   * Función que devuelve un objeto ConvocatoriaEntidadConvocante
   * 
   * @param id id del ConvocatoriaEntidadConvocante
   * @return el objeto ConvocatoriaEntidadConvocante
   */
  private ConvocatoriaEntidadConvocante generarMockConvocatoriaEntidadConvocante(Long id, Long convocatoriaId) {
    Set<ProgramaNombre> nombrePrograma = new HashSet<>();
    nombrePrograma.add(new ProgramaNombre(Language.ES, "nombrePrograma"));

    Programa programa = new Programa();
    programa.setId(id == null ? 1 : id);
    programa.setNombre(nombrePrograma);
    programa.setActivo(true);

    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = new ConvocatoriaEntidadConvocante();
    convocatoriaEntidadConvocante.setId(id);
    convocatoriaEntidadConvocante.setConvocatoriaId(convocatoriaId);
    convocatoriaEntidadConvocante.setEntidadRef("entidad-" + (id == null ? 1 : id));
    convocatoriaEntidadConvocante.setPrograma(programa);

    return convocatoriaEntidadConvocante;
  }

}

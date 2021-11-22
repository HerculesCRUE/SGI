package org.crue.hercules.sgi.csp.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.FuenteFinanciacionInput;
import org.crue.hercules.sgi.csp.dto.FuenteFinanciacionOutput;
import org.crue.hercules.sgi.csp.exceptions.FuenteFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoAmbitoGeograficoNotFoundException;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
import org.crue.hercules.sgi.csp.service.FuenteFinanciacionService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * FuenteFinanciacionControllerTest
 */
@WebMvcTest(FuenteFinanciacionController.class)
public class FuenteFinanciacionControllerTest extends BaseControllerTest {

  @MockBean
  private FuenteFinanciacionService service;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_ACTIVAR = "/activar";
  private static final String CONTROLLER_BASE_PATH = FuenteFinanciacionController.MAPPING;

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-FNT-C" })
  public void create_ReturnsFuenteFinanciacion() throws Exception {
    // given: new FuenteFinanciacion
    FuenteFinanciacionInput fuenteFinanciacion = generarMockFuenteFinanciacionInput();

    BDDMockito.given(service.create(ArgumentMatchers.<FuenteFinanciacion>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          FuenteFinanciacion newFuenteFinanciacion = new FuenteFinanciacion();
          BeanUtils.copyProperties(invocation.getArgument(0), newFuenteFinanciacion);
          newFuenteFinanciacion.setId(1L);
          return newFuenteFinanciacion;
        });

    // when: create FuenteFinanciacion
    mockMvc
        .perform(MockMvcRequestBuilders.post(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(fuenteFinanciacion)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: new FuenteFinanciacion is created
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(fuenteFinanciacion.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(fuenteFinanciacion.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("fondoEstructural").value(fuenteFinanciacion.getFondoEstructural()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoAmbitoGeografico.id")
            .value(fuenteFinanciacion.getTipoAmbitoGeograficoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoOrigenFuenteFinanciacion.id")
            .value(fuenteFinanciacion.getTipoOrigenFuenteFinanciacionId()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-FNT-E" })
  public void update_ReturnsFuenteFinanciacion() throws Exception {
    // given: Existing TipoAmbitoGeografico to be updated
    Long id = 1L;
    FuenteFinanciacionInput fuenteFinanciacion = generarMockFuenteFinanciacionInput();
    fuenteFinanciacion.setNombre("nuevo-nombre");

    BDDMockito.given(service.update(ArgumentMatchers.<FuenteFinanciacion>any()))
        .willAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update FuenteFinanciacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(fuenteFinanciacion)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: FuenteFinanciacion is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("nombre").value(fuenteFinanciacion.getNombre()))
        .andExpect(MockMvcResultMatchers.jsonPath("descripcion").value(fuenteFinanciacion.getDescripcion()))
        .andExpect(MockMvcResultMatchers.jsonPath("fondoEstructural").value(fuenteFinanciacion.getFondoEstructural()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoAmbitoGeografico.id")
            .value(fuenteFinanciacion.getTipoAmbitoGeograficoId()))
        .andExpect(MockMvcResultMatchers.jsonPath("tipoOrigenFuenteFinanciacion.id")
            .value(fuenteFinanciacion.getTipoOrigenFuenteFinanciacionId()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-FNT-E" })
  public void update_WithNoExistingId_Returns404() throws Exception {
    // given: No existing Id
    Long id = 1L;
    FuenteFinanciacionInput fuenteFinanciacion = generarMockFuenteFinanciacionInput();

    BDDMockito.willThrow(new TipoAmbitoGeograficoNotFoundException(id)).given(service)
        .update(ArgumentMatchers.<FuenteFinanciacion>any());

    // when: update FuenteFinanciacion
    mockMvc
        .perform(MockMvcRequestBuilders.put(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(fuenteFinanciacion)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-FNT-R" })
  public void activar_WithExistingId_ReturnFuenteFinanciacion() throws Exception {
    // given: existing id
    Long id = 1L;

    BDDMockito.given(service.activar(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      Long updatedId = invocation.getArgument(0);
      FuenteFinanciacion fuenteFinanciacion = FuenteFinanciacion.builder().id(updatedId).activo(true).build();
      return fuenteFinanciacion;
    });

    // when: reactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_ACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: FuenteFinanciacion is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-FNT-R" })
  public void activar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new FuenteFinanciacionNotFoundException(id)).given(service)
        .activar(ArgumentMatchers.<Long>any());

    // when: reactivar by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_ACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-FNT-B" })
  public void desactivar_WithExistingId_ReturnFuenteFinanciacion() throws Exception {
    // given: existing id
    Long id = 1L;

    BDDMockito.given(service.desactivar(ArgumentMatchers.<Long>any())).willAnswer((InvocationOnMock invocation) -> {
      Long updatedId = invocation.getArgument(0);
      FuenteFinanciacion fuenteFinanciacion = FuenteFinanciacion.builder().id(updatedId).activo(false).build();
      return fuenteFinanciacion;
    });

    // when: desactivar by id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: FuenteFinanciacion is updated
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-FNT-B" })
  public void desactivar_NoExistingId_Return404() throws Exception {
    // given: non existing id
    Long id = 1L;

    BDDMockito.willThrow(new FuenteFinanciacionNotFoundException(id)).given(service)
        .desactivar(ArgumentMatchers.<Long>any());

    // when: desactivar by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: 404 error
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V", "CSP-CON-E", "CSP-CON-C", "CSP-CON-INV-V" })
  public void findActivos_ReturnsPage() throws Exception {
    // given: Una lista con 37 FuenteFinanciacion
    List<FuenteFinanciacion> fuenteFinanciaciones = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      fuenteFinanciaciones.add(generarMockFuenteFinanciacion(i, "FuenteFinanciacion" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findActivos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > fuenteFinanciaciones.size() ? fuenteFinanciaciones.size() : toIndex;
          List<FuenteFinanciacion> content = fuenteFinanciaciones.subList(fromIndex, toIndex);
          Page<FuenteFinanciacion> pageResponse = new PageImpl<>(content, pageable, fuenteFinanciaciones.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los FuenteFinanciacion del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<FuenteFinanciacionOutput> fuenteFinanciacionesResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<FuenteFinanciacionOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      FuenteFinanciacionOutput fuenteFinanciacion = fuenteFinanciacionesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(fuenteFinanciacion.getNombre()).isEqualTo("FuenteFinanciacion" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-V", "CSP-CON-E", "CSP-CON-C", "CSP-CON-INV-V" })
  public void findActivos_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de FuenteFinanciacion
    List<FuenteFinanciacion> fuenteFinanciaciones = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(service.findActivos(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<FuenteFinanciacion> pageResponse = new PageImpl<>(fuenteFinanciaciones, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH).with(SecurityMockMvcRequestPostProcessors.csrf())
            .header("X-Page", page).header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-FNT-V", "CSP-FNT-C", "CSP-FNT-E", "CSP-FNT-B", "CSP-FNT-R" })
  public void findAll_ReturnsPage() throws Exception {
    // given: Una lista con 37 FuenteFinanciacion
    List<FuenteFinanciacion> fuenteFinanciaciones = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      fuenteFinanciaciones.add(generarMockFuenteFinanciacion(i, "FuenteFinanciacion" + String.format("%03d", i)));
    }

    Integer page = 3;
    Integer pageSize = 10;

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > fuenteFinanciaciones.size() ? fuenteFinanciaciones.size() : toIndex;
          List<FuenteFinanciacion> content = fuenteFinanciaciones.subList(fromIndex, toIndex);
          Page<FuenteFinanciacion> pageResponse = new PageImpl<>(content, pageable, fuenteFinanciaciones.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve la pagina 3 con los FuenteFinanciacion del 31 al 37
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "3"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", "7"))
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", "10"))
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", "37"))
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(7))).andReturn();

    List<FuenteFinanciacionOutput> fuenteFinanciacionesResponse = mapper.readValue(
        requestResult.getResponse().getContentAsString(), new TypeReference<List<FuenteFinanciacionOutput>>() {
        });

    for (int i = 31; i <= 37; i++) {
      FuenteFinanciacionOutput fuenteFinanciacion = fuenteFinanciacionesResponse.get(i - (page * pageSize) - 1);
      Assertions.assertThat(fuenteFinanciacion.getNombre()).isEqualTo("FuenteFinanciacion" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-FNT-V", "CSP-FNT-C", "CSP-FNT-E", "CSP-FNT-B", "CSP-FNT-R" })
  public void findAll_EmptyList_Returns204() throws Exception {
    // given: Una lista vacia de FuenteFinanciacion
    List<FuenteFinanciacion> fuenteFinanciaciones = new ArrayList<>();

    Integer page = 0;
    Integer pageSize = 10;

    BDDMockito.given(service.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<FuenteFinanciacion> pageResponse = new PageImpl<>(fuenteFinanciaciones, pageable, 0);
          return pageResponse;
        });

    // when: Get page=0 with pagesize=10
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + "/todos")
            .with(SecurityMockMvcRequestPostProcessors.csrf()).header("X-Page", page).header("X-Page-Size", pageSize)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Devuelve un 204
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void findById_WithExistingId_ReturnsFuenteFinanciacion() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).willAnswer((InvocationOnMock invocation) -> {
      Long paramId = invocation.getArgument(0);
      FuenteFinanciacion fuenteFinanciacion = FuenteFinanciacion.builder().id(paramId).build();
      return fuenteFinanciacion;
    });

    // when: find by existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested FuenteFinanciacion is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(id));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void findById_WithNoExistingId_Returns404() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(service.findById(ArgumentMatchers.anyLong())).will((InvocationOnMock invocation) -> {
      Long paramId = invocation.getArgument(0);
      throw new FuenteFinanciacionNotFoundException(paramId);
    });

    // when: find by non existing id
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, id)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  private FuenteFinanciacionInput generarMockFuenteFinanciacionInput() {
    FuenteFinanciacionInput fuenteFinanciacion = FuenteFinanciacionInput.builder().nombre("nombre")
        .descripcion("descripcion").fondoEstructural(true).tipoAmbitoGeograficoId(1L).tipoOrigenFuenteFinanciacionId(1L)
        .build();

    return fuenteFinanciacion;
  }

  private FuenteFinanciacion generarMockFuenteFinanciacion(Long id, String nombre) {
    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(1L);

    TipoOrigenFuenteFinanciacion tipoOrigenFuenteFinanciacion = new TipoOrigenFuenteFinanciacion();
    tipoOrigenFuenteFinanciacion.setId(1L);

    FuenteFinanciacion fuenteFinanciacion = new FuenteFinanciacion();
    fuenteFinanciacion.setId(id);
    fuenteFinanciacion.setNombre(nombre);
    fuenteFinanciacion.setDescripcion("descripcion-" + id);
    fuenteFinanciacion.setFondoEstructural(true);
    fuenteFinanciacion.setTipoAmbitoGeografico(tipoAmbitoGeografico);
    fuenteFinanciacion.setTipoOrigenFuenteFinanciacion(tipoOrigenFuenteFinanciacion);
    fuenteFinanciacion.setActivo(true);

    return fuenteFinanciacion;
  }
}

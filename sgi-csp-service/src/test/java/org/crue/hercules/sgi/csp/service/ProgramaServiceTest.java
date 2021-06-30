package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProgramaNotFoundException;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.service.impl.ProgramaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ProgramaServiceTest
 */
public class ProgramaServiceTest extends BaseServiceTest {

  @Mock
  private ProgramaRepository repository;

  private ProgramaService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ProgramaServiceImpl(repository);
  }

  @Test
  public void create_ReturnsPrograma() {
    // given: Un nuevo Programa
    Programa programa = generarMockPrograma(null, "nombre-1", null);

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Programa>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<Programa> page = new PageImpl<>(new ArrayList<Programa>(), pageable, 0);
          return page;
        });

    BDDMockito.given(repository.save(programa)).will((InvocationOnMock invocation) -> {
      Programa programaCreado = invocation.getArgument(0);
      programaCreado.setId(1L);
      return programaCreado;
    });

    // when: Creamos el Programa
    Programa programaCreado = service.create(programa);

    // then: El Programa se crea correctamente
    Assertions.assertThat(programaCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(programaCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(programaCreado.getNombre()).as("getNombre").isEqualTo(programa.getNombre());
    Assertions.assertThat(programaCreado.getDescripcion()).as("getDescripcion").isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programaCreado.getActivo()).as("getActivo").isEqualTo(programa.getActivo());
  }

  @Test
  public void create_WithPadre_ReturnsPrograma() {
    // given: Un nuevo Programa
    Programa programa = generarMockPrograma(null, "nombre-2", 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(generarMockPrograma(1L)));
    BDDMockito.given(repository.findByPadreIdInAndActivoIsTrue(ArgumentMatchers.<Long>anyList()))
        .willReturn(new ArrayList<>());

    BDDMockito.given(repository.save(programa)).will((InvocationOnMock invocation) -> {
      Programa programaCreado = invocation.getArgument(0);
      programaCreado.setId(1L);
      return programaCreado;
    });

    // when: Creamos el Programa
    Programa programaCreado = service.create(programa);

    // then: El Programa se crea correctamente
    Assertions.assertThat(programaCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(programaCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(programaCreado.getNombre()).as("getNombre").isEqualTo(programa.getNombre());
    Assertions.assertThat(programaCreado.getDescripcion()).as("getDescripcion").isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programaCreado.getActivo()).as("getActivo").isEqualTo(programa.getActivo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo Programa que ya tiene id
    Programa programa = generarMockPrograma(1L);

    // when: Creamos el Programa
    // then: Lanza una excepcion porque el Programa ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(programa)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Programa id tiene que ser null para crear un nuevo Programa");
  }

  @Test
  public void create_WithNoExistingPadre_ThrowsProgramaNotFoundException() {
    // given: Un nuevo Programa con un padre que no existe
    Programa programa = generarMockPrograma(null, "nombreRepetido", 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Creamos el Programa
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(programa)).isInstanceOf(ProgramaNotFoundException.class);
  }

  @Test
  public void create_PlanWithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo Programa con un nombre que ya existe
    Programa programaNew = generarMockPrograma(null, "nombreRepetido", null);
    Programa programa = generarMockPrograma(1L, "nombreRepetido", null);

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Programa>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<Programa> page = new PageImpl<>(Arrays.asList(programa), pageable, 0);
          return page;
        });

    // when: Creamos el Programa
    // then: Lanza una excepcion porque hay otro Programa con ese nombre
    Assertions.assertThatThrownBy(() -> service.create(programaNew)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un plan con el mismo nombre");
  }

  @Test
  public void create_ProgramaWithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo Programa con un nombre que ya existe
    Programa programaNew = generarMockPrograma(null, "nombreRepetido", 1L);
    Programa programa = generarMockPrograma(1L, "nombreRepetidoPadre", null);
    Programa programaHijo = generarMockPrograma(2L, "nombreRepetido", 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(programa));
    BDDMockito.given(repository.findByPadreIdInAndActivoIsTrue(ArgumentMatchers.<Long>anyList()))
        .willReturn(Arrays.asList(programaHijo));

    // when: Creamos el Programa
    // then: Lanza una excepcion porque hay otro Programa con ese nombre
    Assertions.assertThatThrownBy(() -> service.create(programaNew)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un programa con el mismo nombre en el plan");
  }

  @Test
  public void update_ReturnsPrograma() {
    // given: Un nuevo Programa con el nombre actualizado
    Programa programa = generarMockPrograma(1L);
    Programa programaNombreActualizado = generarMockPrograma(1L, "NombreActualizado", null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(programa));

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Programa>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<Programa> page = new PageImpl<>(new ArrayList<Programa>(), pageable, 0);
          return page;
        });

    BDDMockito.given(repository.save(ArgumentMatchers.<Programa>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el Programa
    Programa programaActualizado = service.update(programaNombreActualizado);

    // then: El Programa se actualiza correctamente.
    Assertions.assertThat(programaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(programaActualizado.getId()).as("getId()").isEqualTo(programa.getId());
    Assertions.assertThat(programaActualizado.getNombre()).as("getNombre()")
        .isEqualTo(programaNombreActualizado.getNombre());
    Assertions.assertThat(programaActualizado.getDescripcion()).as("getDescripcion")
        .isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programaActualizado.getActivo()).as("getActivo()").isEqualTo(programa.getActivo());
  }

  @Test
  public void update_WithPadre_ReturnsPrograma() {
    // given: Un nuevo Programa con el nombre actualizado
    Programa programa = generarMockPrograma(2L, "Nombre", 1L);
    Programa programaNombreActualizado = generarMockPrograma(2L, "NombreActualizado", 1L);

    BDDMockito.given(repository.findById(2L)).willReturn(Optional.of(programa));
    BDDMockito.given(repository.findById(1L)).willReturn(Optional.of(generarMockPrograma(1L)));
    BDDMockito.given(repository.findByPadreIdInAndActivoIsTrue(ArgumentMatchers.<Long>anyList()))
        .willReturn(new ArrayList<>());

    BDDMockito.given(repository.save(ArgumentMatchers.<Programa>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el Programa
    Programa programaActualizado = service.update(programaNombreActualizado);

    // then: El Programa se actualiza correctamente.
    Assertions.assertThat(programaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(programaActualizado.getId()).as("getId()").isEqualTo(programa.getId());
    Assertions.assertThat(programaActualizado.getNombre()).as("getNombre()")
        .isEqualTo(programaNombreActualizado.getNombre());
    Assertions.assertThat(programaActualizado.getDescripcion()).as("getDescripcion")
        .isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programaActualizado.getActivo()).as("getActivo()").isEqualTo(programa.getActivo());
  }

  @Test
  public void update_PlanWithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un Programa actualizado con un nombre que ya existe
    Programa programaActualizado = generarMockPrograma(1L, "nombreRepetido", null);
    Programa programa = generarMockPrograma(2L, "nombreRepetido", null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(programaActualizado));
    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Programa>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<Programa> page = new PageImpl<>(Arrays.asList(programa), pageable, 0);
          return page;
        });

    // when: Actualizamos el Programa
    // then: Lanza una excepcion porque hay otro Programa con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(programaActualizado))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe un plan con el mismo nombre");
  }

  @Test
  public void update_ProgramaWithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un Programa actualizado con un nombre que ya existe
    Programa programaActualizado = generarMockPrograma(3L, "nombreRepetido", 1L);
    Programa programa = generarMockPrograma(1L, "nombreRepetidoPadre", null);
    Programa programaHijo = generarMockPrograma(2L, "nombreRepetido", 1L);

    BDDMockito.given(repository.findById(3L)).willReturn(Optional.of(programaActualizado));
    BDDMockito.given(repository.findById(1L)).willReturn(Optional.of(programa));

    BDDMockito.given(repository.findByPadreIdInAndActivoIsTrue(ArgumentMatchers.<Long>anyList()))
        .willReturn(Arrays.asList(programaHijo));

    // when: Actualizamos el Programa
    // then: Lanza una excepcion porque hay otro Programa con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(programaActualizado))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un programa con el mismo nombre en el plan");
  }

  @Test
  public void update_WithNoExistingPadre_ThrowsProgramaNotFoundException() {
    // given: Un Programa actualizado con un padre que no existe
    Programa programa = generarMockPrograma(2L, "nombreRepetido", 1L);

    BDDMockito.given(repository.findById(1L)).willReturn(Optional.empty());

    // when: Actualizamos el Programa
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(programa)).isInstanceOf(ProgramaNotFoundException.class);
  }

  @Test
  public void update_WithIdNotExist_ThrowsProgramaNotFoundException() {
    // given: Un Programa actualizado con un id que no existe
    Programa programa = generarMockPrograma(1L, "Programa", null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Actualizamos el Programa
    // then: Lanza una excepcion porque el Programa no existe
    Assertions.assertThatThrownBy(() -> service.update(programa)).isInstanceOf(ProgramaNotFoundException.class);
  }

  @Test
  public void enable_ReturnsPrograma() {
    // given: Un nuevo Programa inactivo
    Programa programa = generarMockPrograma(1L);
    programa.setActivo(false);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(programa));
    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Programa>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<Programa> page = new PageImpl<>(new ArrayList<>(), pageable, 0);
          return page;
        });
    BDDMockito.given(repository.save(ArgumentMatchers.<Programa>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Activamos el Programa
    Programa programaActualizado = service.enable(programa.getId());

    // then: El FuenteFinanciacion se activa correctamente.
    Assertions.assertThat(programaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(programaActualizado.getId()).as("getId()").isEqualTo(programa.getId());
    Assertions.assertThat(programaActualizado.getNombre()).as("getNombre()").isEqualTo(programa.getNombre());
    Assertions.assertThat(programaActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programaActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void enable_PlanWithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un Programa inactivo con un nombre que ya existe activo
    Programa programa = generarMockPrograma(1L, "nombreRepetido", null);
    programa.setActivo(false);
    Programa programaRepetido = generarMockPrograma(2L, "nombreRepetido", null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(programa));
    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Programa>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<Programa> page = new PageImpl<>(Arrays.asList(programaRepetido), pageable, 0);
          return page;
        });

    // when: Activamos el Programa
    // then: Lanza una excepcion porque hay otro Programa con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(programa)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un plan con el mismo nombre");
  }

  @Test
  public void enable_WithIdNotExist_ThrowsTipoFinanciacionNotFoundException() {
    // given: Un id de un Programa que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el Programa
    // then: Lanza una excepcion porque el Programa no existe
    Assertions.assertThatThrownBy(() -> service.enable(idNoExiste)).isInstanceOf(ProgramaNotFoundException.class);
  }

  @Test
  public void disable_ReturnsPrograma() {
    // given: Un nuevo Programa activo
    Programa programa = generarMockPrograma(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(programa));

    BDDMockito.given(repository.save(ArgumentMatchers.<Programa>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el Programa
    Programa programaActualizado = service.disable(programa.getId());

    // then: El Programa se desactiva correctamente
    Assertions.assertThat(programaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(programaActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(programaActualizado.getNombre()).as("getNombre()").isEqualTo(programa.getNombre());
    Assertions.assertThat(programaActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(programa.getDescripcion());
    Assertions.assertThat(programaActualizado.getActivo()).as("getActivo()").isEqualTo(false);
  }

  @Test
  public void disable_WithIdNotExist_ThrowsProgramaNotFoundException() {
    // given: Un id de un Programa que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el Programa
    // then: Lanza una excepcion porque el Programa no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste)).isInstanceOf(ProgramaNotFoundException.class);
  }

  @Test
  public void findById_ReturnsPrograma() {
    // given: Un Programa con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockPrograma(idBuscado)));

    // when: Buscamos el Programa por su id
    Programa programa = service.findById(idBuscado);

    // then: el Programa
    Assertions.assertThat(programa).as("isNotNull()").isNotNull();
    Assertions.assertThat(programa.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(programa.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(programa.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsProgramaNotFoundException() throws Exception {
    // given: Ningun Programa con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el Programa por su id
    // then: lanza un ProgramaNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado)).isInstanceOf(ProgramaNotFoundException.class);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 Programa
    List<Programa> programas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      programas.add(generarMockPrograma(i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Programa>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Programa>>() {
          @Override
          public Page<Programa> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > programas.size() ? programas.size() : toIndex;
            List<Programa> content = programas.subList(fromIndex, toIndex);
            Page<Programa> page = new PageImpl<>(content, pageable, programas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Programa> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Programa programa = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(programa.getNombre()).isEqualTo("nombre-" + i);
    }
  }

  @Test
  public void findAllPlan_ReturnsPage() {
    // given: Una lista con 37 Programa sin padre (planes)
    List<Programa> programas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      programas.add(generarMockPrograma(i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Programa>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Programa>>() {
          @Override
          public Page<Programa> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > programas.size() ? programas.size() : toIndex;
            List<Programa> content = programas.subList(fromIndex, toIndex);
            Page<Programa> page = new PageImpl<>(content, pageable, programas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Programa> page = service.findAllPlan(null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Programa programa = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(programa.getNombre()).isEqualTo("nombre-" + i);
    }
  }

  @Test
  public void findAllTodosPlan_ReturnsPage() {
    // given: Una lista con 37 Programa sin padre (planes)
    List<Programa> programas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      programas.add(generarMockPrograma(i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Programa>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Programa>>() {
          @Override
          public Page<Programa> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > programas.size() ? programas.size() : toIndex;
            List<Programa> content = programas.subList(fromIndex, toIndex);
            Page<Programa> page = new PageImpl<>(content, pageable, programas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Programa> page = service.findAllTodosPlan(null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Programa programa = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(programa.getNombre()).isEqualTo("nombre-" + i);
    }
  }

  @Test
  public void findAllHijosPrograma_ReturnsPage() {
    // given: Una lista con 37 Programa para un Programa
    Long idPadre = 1L;
    List<Programa> programas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      programas.add(generarMockPrograma(i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Programa>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Programa>>() {
          @Override
          public Page<Programa> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > programas.size() ? programas.size() : toIndex;
            List<Programa> content = programas.subList(fromIndex, toIndex);
            Page<Programa> page = new PageImpl<>(content, pageable, programas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Programa> page = service.findAllHijosPrograma(idPadre, null, paging);

    // then: Devuelve la pagina 3 con los TipoEnlace del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Programa programa = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(programa.getNombre()).isEqualTo("nombre-" + i);
    }
  }

  /**
   * Función que devuelve un objeto Programa
   * 
   * @param id id del Programa
   * @return el objeto Programa
   */
  private Programa generarMockPrograma(Long id) {
    return generarMockPrograma(id, "nombre-" + id, null);
  }

  /**
   * Función que devuelve un objeto Programa
   * 
   * @param id              id del Programa
   * @param nombre          nombre del Programa
   * @param idProgramaPadre id del Programa padre
   * @return el objeto Programa
   */
  private Programa generarMockPrograma(Long id, String nombre, Long idProgramaPadre) {
    Programa programa = new Programa();
    programa.setId(id);
    programa.setNombre(nombre);
    programa.setDescripcion("descripcion-" + id);

    if (idProgramaPadre != null) {
      programa.setPadre(generarMockPrograma(idProgramaPadre));
    }
    programa.setActivo(true);

    return programa;
  }

}

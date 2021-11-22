package org.crue.hercules.sgi.eti.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.dto.ConvocatoriaReunionDatosGenerales;
import org.crue.hercules.sgi.eti.exceptions.ConvocatoriaReunionNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.crue.hercules.sgi.eti.repository.ActaRepository;
import org.crue.hercules.sgi.eti.repository.ConvocatoriaReunionRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.service.impl.ConvocatoriaReunionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ConvocatoriaReunionServiceTest
 */
public class ConvocatoriaReunionServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaReunionRepository repository;
  @Mock
  private ActaRepository actaRepository;
  @Mock
  private EvaluacionRepository evaluacionRepository;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  private ConvocatoriaReunionService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaReunionServiceImpl(sgiConfigProperties, repository, actaRepository, evaluacionRepository);
  }

  @Test
  public void create_ReturnsConvocatoriaReunion() {

    // given: Nueva entidad sin Id
    ConvocatoriaReunion response = getMockData(1L, 1L, 1L);
    response.setId(null);

    BDDMockito.given(repository.save(response)).willReturn(response);

    // when: Se crea la entidad
    ConvocatoriaReunion result = service.create(response);

    // then: La entidad se crea correctamente
    Assertions.assertThat(result).isEqualTo(response);
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {

    // given: Nueva entidad con Id
    ConvocatoriaReunion response = getMockData(1L, 1L, 1L);
    // when: Se crea la entidad
    // then: Se produce error porque ya tiene Id
    Assertions.assertThatThrownBy(() -> service.create(response)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithExistingId_ReturnsConvocatoriaReunion() {

    // given: Entidad existente que se va a actualizar
    ConvocatoriaReunion convocatoriaReunion = getMockData(1L, 1L, 1L);
    ConvocatoriaReunion convocatoriaReunionActualizada = getMockData(2L, 1L, 2L);
    convocatoriaReunionActualizada.setId(convocatoriaReunion.getId());

    BDDMockito.given(repository.findById(convocatoriaReunion.getId())).willReturn(Optional.of(convocatoriaReunion));
    BDDMockito.given(repository.save(convocatoriaReunionActualizada)).willReturn(convocatoriaReunionActualizada);

    // when: Se actualiza la entidad
    ConvocatoriaReunion result = service.update(convocatoriaReunionActualizada);

    // then: Los datos se actualizan correctamente
    Assertions.assertThat(result).isEqualTo(convocatoriaReunionActualizada);
  }

  @Test
  public void update_WithNoExistingId_ThrowsNotFoundException() {

    // given: Entidad a actualizar que no existe
    ConvocatoriaReunion convocatoriaReunionActualizada = getMockData(1L, 1L, 1L);

    // when: Se actualiza la entidad
    // then: Se produce error porque no encuentra la entidad a actualizar
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaReunionActualizada))
        .isInstanceOf(ConvocatoriaReunionNotFoundException.class);
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Entidad a actualizar sin Id
    ConvocatoriaReunion convocatoriaReunionActualizada = getMockData(1L, 1L, 1L);
    convocatoriaReunionActualizada.setId(null);

    // when: Se actualiza la entidad
    // then: Se produce error porque no tiene Id
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaReunionActualizada))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesConvocatoriaReunion() {

    // given: Entidad existente
    ConvocatoriaReunion response = getMockData(1L, 1L, 1L);

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(repository).deleteById(response.getId());

    // when: Se elimina la entidad
    service.delete(response.getId());

    // then: La entidad se elimina correctamente
    BDDMockito.given(repository.findById(response.getId())).willReturn(Optional.empty());
    Assertions.assertThatThrownBy(() -> service.findById(response.getId()))
        .isInstanceOf(ConvocatoriaReunionNotFoundException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsNotFoundException() {

    // given: Id de una entidad que no existe
    Long id = 1L;

    // when: Se elimina la entidad
    // then: Se produce error porque no encuentra la entidad a actualizar
    Assertions.assertThatThrownBy(() -> service.delete(id)).isInstanceOf(ConvocatoriaReunionNotFoundException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {

    // given: Id de una entidad vacía
    Long id = null;

    // when: Se elimina la entidad
    // then: Se produce error porque no encuentra la entidad a actualizar
    Assertions.assertThatThrownBy(() -> service.delete(id)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void find_WithExistingId_ReturnsConvocatoriaReunion() {

    // given: Entidad con un determinado Id
    ConvocatoriaReunion response = getMockData(1L, 1L, 1L);
    BDDMockito.given(repository.findById(response.getId())).willReturn(Optional.of(response));

    // when: Se busca la entidad por ese Id
    ConvocatoriaReunion result = service.findById(response.getId());

    // then: Se recupera la entidad con el Id
    Assertions.assertThat(result).isEqualTo(response);
  }

  @Test
  public void find_WithNoExistingId_ThrowsNotFoundException() throws Exception {

    // given: No existe entidad con el id indicado
    Long id = 1L;
    BDDMockito.given(repository.findById(id)).willReturn(Optional.empty());

    // when: Se busca entidad con ese id
    // then: Se produce error porque no encuentra la entidad con ese Id
    Assertions.assertThatThrownBy(() -> service.findById(id)).isInstanceOf(ConvocatoriaReunionNotFoundException.class);
  }

  @Test
  public void findWithNumEvaluacionesActivasNoRevMin_WithExistingId_ReturnsConvocatoriaReunionDatosGenerales() {

    // given: Entidad con un determinado Id
    ConvocatoriaReunionDatosGenerales response = new ConvocatoriaReunionDatosGenerales(getMockData(1L, 1L, 1L), 1L, 1L);
    BDDMockito.given(repository.findByIdWithDatosGenerales(response.getId())).willReturn(Optional.of(response));

    // when: Se busca la entidad por ese Id
    ConvocatoriaReunionDatosGenerales result = service.findByIdWithDatosGenerales(response.getId());

    // then: Se recupera la entidad con el Id
    Assertions.assertThat(result).isEqualTo(response);
  }

  @Test
  public void findWithNumEvaluacionesActivasNoRevMin_WithNoExistingId_ThrowsNotFoundException() throws Exception {

    // given: No existe entidad con el id indicado
    Long id = 1L;
    BDDMockito.given(repository.findByIdWithDatosGenerales(id)).willReturn(Optional.empty());

    // when: Se busca entidad con ese id
    // then: Se produce error porque no encuentra la entidad con ese Id
    Assertions.assertThatThrownBy(() -> service.findByIdWithDatosGenerales(id))
        .isInstanceOf(ConvocatoriaReunionNotFoundException.class);
  }

  @Test
  public void findAll_Unlimited_ReturnsFullConvocatoriaReunionList() {

    // given: Datos existentes
    List<ConvocatoriaReunion> response = new LinkedList<ConvocatoriaReunion>();
    response.add(getMockData(1L, 1L, 1L));
    response.add(getMockData(2L, 1L, 2L));

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaReunion>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(response));

    // when: Se buscan todos las datos
    Page<ConvocatoriaReunion> result = service.findAll(null, Pageable.unpaged());

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getContent()).isEqualTo(response);
    Assertions.assertThat(result.getNumber()).isZero();
    Assertions.assertThat(result.getSize()).isEqualTo(response.size());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findAll_Unlimited_ReturnEmptyPage() {

    // given: No hay datos
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaReunion>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(new LinkedList<ConvocatoriaReunion>()));

    // when: Se buscan todos las datos
    Page<ConvocatoriaReunion> result = service.findAll(null, Pageable.unpaged());

    // then: Se recupera lista vacía
    Assertions.assertThat(result.isEmpty());
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {

    // given: Datos existentes
    List<ConvocatoriaReunion> response = new LinkedList<ConvocatoriaReunion>();
    response.add(getMockData(1L, 1L, 1L));
    response.add(getMockData(2L, 1L, 2L));
    response.add(getMockData(3L, 2L, 3L));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<ConvocatoriaReunion> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaReunion>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<ConvocatoriaReunion> result = service.findAll(null, pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findAll_WithPaging_ReturnEmptyPage() {

    // given: No hay datos
    List<ConvocatoriaReunion> response = new LinkedList<ConvocatoriaReunion>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<ConvocatoriaReunion> pageResponse = new PageImpl<>(response, pageable, response.size());

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaReunion>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<ConvocatoriaReunion> result = service.findAll(null, pageable);

    // then: Se recupera lista de datos paginados vacía
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void findConvocatoriasSinActa() {

    // given: Datos existentes
    List<ConvocatoriaReunion> response = new LinkedList<ConvocatoriaReunion>();
    response.add(getMockData(1L, 1L, 1L));
    response.add(getMockData(2L, 1L, 2L));

    BDDMockito.given(repository.findConvocatoriasReunionSinActa()).willReturn(response);

    // when: Se buscan todos las datos
    List<ConvocatoriaReunion> result = service.findConvocatoriasSinActa();

    // then: Se recuperan todos los datos
    Assertions.assertThat(result).isEqualTo(response);
    Assertions.assertThat(result.size()).isEqualTo(response.size());
  }

  /**
   * Genera un objeto {@link ConvocatoriaReunion}
   * 
   * @param id
   * @param comiteId
   * @param tipoId
   * @return ConvocatoriaReunion
   */
  private ConvocatoriaReunion getMockData(Long id, Long comiteId, Long tipoId) {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(comiteId, "Comite" + comiteId, "nombreSecretario", "nombreInvestigacion", Genero.M,
        "nombreDecreto", "articulo", formulario, Boolean.TRUE);

    String tipo_txt = (tipoId == 1L) ? "Ordinaria" : (tipoId == 2L) ? "Extraordinaria" : "Seguimiento";
    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(tipoId, tipo_txt, Boolean.TRUE);

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final ConvocatoriaReunion data = new ConvocatoriaReunion();
    data.setId(id);
    data.setComite(comite);
    data.setFechaEvaluacion(LocalDate.of(2020, 7, id.intValue()).atStartOfDay(ZoneOffset.UTC).toInstant());
    data.setFechaLimite(LocalDate.of(2020, 8, id.intValue()).atStartOfDay(ZoneOffset.UTC).toInstant());
    data.setLugar("Lugar " + txt);
    data.setOrdenDia("Orden del día convocatoria reunión " + txt);
    data.setAnio(2020);
    data.setNumeroActa(100L);
    data.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    data.setHoraInicio(7 + id.intValue());
    data.setMinutoInicio(30);
    data.setFechaEnvio(Instant.parse("2020-07-13T00:00:00Z"));
    data.setActivo(Boolean.TRUE);

    return data;
  }

}

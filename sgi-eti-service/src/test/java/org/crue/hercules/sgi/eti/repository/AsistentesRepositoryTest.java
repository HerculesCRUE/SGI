package org.crue.hercules.sgi.eti.repository;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Asistentes;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
public class AsistentesRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private AsistentesRepository repository;

  @Test
  public void findAllByConvocatoriaReunionId_WithPaging_ReturnsPage() throws Exception {

    // given: Datos existentes con convocatoriaReunionId = 1

    Formulario formulario = entityManager.persistFlushFind(generarMockFormulario());
    Comite comite = entityManager.persistFlushFind(generarMockComite(formulario));
    CargoComite cargoComite = entityManager.persistAndFlush(generarMockCargoComite());
    TipoConvocatoriaReunion tipoConvocatoriaReunion = entityManager
        .persistFlushFind(generarMockTipoConvocatoriaReunion());
    ConvocatoriaReunion c1 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    ConvocatoriaReunion c2 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    Evaluador evaluador = entityManager.persistAndFlush(generarMockEvaluador(cargoComite, comite));

    List<Asistentes> response = new LinkedList<Asistentes>();
    response.add(entityManager.persist(generarMockAsistentes(evaluador, c1)));
    response.add(entityManager.persist(generarMockAsistentes(evaluador, c1)));
    response.add(entityManager.persist(generarMockAsistentes(evaluador, c1)));
    entityManager.persist(generarMockAsistentes(evaluador, c2));
    entityManager.persist(generarMockAsistentes(evaluador, c2));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<Asistentes> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    // when: Se buscan los datos paginados
    Page<Asistentes> result = repository.findAllByConvocatoriaReunionId(c1.getId(), pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result.getNumber()).isEqualTo(pageResponse.getNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageResponse.getSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(pageResponse.getTotalElements());
    Assertions.assertThat(result.getContent()).isEqualTo(pageResponse.getContent());
  }

  @Test
  public void findAllByConvocatoriaReunionId_WithPaging_ReturnsEmptyPage() throws Exception {

    // given: Sin datos existentes con convocatoriaReunionId = 2

    Formulario formulario = entityManager.persistFlushFind(generarMockFormulario());
    Comite comite = entityManager.persistFlushFind(generarMockComite(formulario));
    CargoComite cargoComite = entityManager.persistAndFlush(generarMockCargoComite());
    TipoConvocatoriaReunion tipoConvocatoriaReunion = entityManager
        .persistFlushFind(generarMockTipoConvocatoriaReunion());
    ConvocatoriaReunion c1 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    ConvocatoriaReunion c2 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    Evaluador evaluador = entityManager.persistAndFlush(generarMockEvaluador(cargoComite, comite));

    List<Asistentes> response = new LinkedList<Asistentes>();
    entityManager.persist(generarMockAsistentes(evaluador, c1));
    entityManager.persist(generarMockAsistentes(evaluador, c1));
    entityManager.persist(generarMockAsistentes(evaluador, c1));
    entityManager.persist(generarMockAsistentes(evaluador, c1));
    entityManager.persist(generarMockAsistentes(evaluador, c1));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<Asistentes> pageResponse = new PageImpl<>(response, pageable, response.size());

    // when: Se buscan los datos paginados
    Page<Asistentes> result = repository.findAllByConvocatoriaReunionId(c2.getId(), pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result.getNumber()).isEqualTo(pageResponse.getNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageResponse.getSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(pageResponse.getTotalElements());
    Assertions.assertThat(result.getContent()).isEmpty();
  }

  /**
   * Función que devuelve un objeto Formulario
   * 
   * @return el objeto Formulario
   */
  public Formulario generarMockFormulario() {
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    return formulario;
  }

  /**
   * Función que devuelve un objeto Comite
   * 
   * @param formulario el formulario
   * @return el objeto Comite
   */
  public Comite generarMockComite(Formulario formulario) {
    return new Comite(null, "Comite1", "nombreInvestigacion", Genero.M, formulario, Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoConvocatoriaReunionluacion
   * 
   * @return el objeto TipoConvocatoriaReunion
   */
  public TipoConvocatoriaReunion generarMockTipoConvocatoriaReunion() {
    return new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto Asistentes
   * 
   * @param comite                  el objeto Comité
   * @param tipoConvocatoriaReunion el objeto TipoConvocatoriaReunion
   * @return el objeto Asistentes
   */
  public ConvocatoriaReunion generarMockConvocatoriaReunion(Comite comite,
      TipoConvocatoriaReunion tipoConvocatoriaReunion) {

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.parse("2020-08-01T00:00:00Z"));
    convocatoriaReunion.setFechaLimite(Instant.now());
    convocatoriaReunion.setVideoconferencia(false);
    convocatoriaReunion.setVideoconferencia(false);
    convocatoriaReunion.setLugar("Lugar");
    convocatoriaReunion.setOrdenDia("Orden del día convocatoria reunión");
    convocatoriaReunion.setAnio(2020);
    convocatoriaReunion.setNumeroActa(100L);
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(Instant.now());
    convocatoriaReunion.setActivo(Boolean.TRUE);

    return convocatoriaReunion;

  }

  /**
   * Función que devuelve un objeto CargoComite
   * 
   * @return el objeto CargoComite
   */
  public CargoComite generarMockCargoComite() {
    return new CargoComite(1L, "CargoComite1", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto Evaluador
   * 
   * @param cargoComite
   * @param comite
   * @return el objeto Evaluador
   */
  private Evaluador generarMockEvaluador(CargoComite cargoComite, Comite comite) {
    return new Evaluador(null, cargoComite, comite, Instant.parse("2020-08-01T00:00:00Z"), null, "Resumen", "user-001",
        Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto Asistentes
   * 
   * @param evaluador
   * @param convocatoriaReunion
   * @return el objeto Asistentes
   */
  private Asistentes generarMockAsistentes(Evaluador evaluador, ConvocatoriaReunion convocatoriaReunion) {
    return new Asistentes(null, evaluador, convocatoriaReunion, Boolean.TRUE, "Motivo1");
  }

}
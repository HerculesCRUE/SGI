package org.crue.hercules.sgi.eti.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ComiteNombre;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ComiteRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ComiteRepository repository;

  @Test
  public void findByIdAndActivoTrue_ReturnsData() throws Exception {

    // given: Datos existentes para el comité activo
    Formulario formulario = entityManager.persistFlushFind(generarMockFormulario());
    Comite comite = entityManager.persistFlushFind(generarMockComite(formulario));

    // when: Se buscan los datos
    Optional<Comite> result = repository.findByIdAndActivoTrue(comite.getId());

    // then: Se recuperan los datos correctamente
    Assertions.assertThat(result.get()).isNotNull();

  }

  /**
   * Función que devuelve un objeto Formulario
   * 
   * @return el objeto Formulario
   */
  private Formulario generarMockFormulario() {
    Formulario formulario = new Formulario();
    formulario.setCodigo("M10/2020/001");
    formulario.setTipo(Formulario.Tipo.MEMORIA);
    return formulario;
  }

  /**
   * Función que devuelve un objeto Comite
   * 
   * @param formulario el formulario
   * @return el objeto Comite
   */
  private Comite generarMockComite(Formulario formulario) {
    Set<ComiteNombre> nombre = new HashSet<>();
    nombre.add(new ComiteNombre(Language.ES, "NombreComite1", ComiteNombre.Genero.M));
    Comite comite = new Comite();
    comite.setCodigo("Comite1");
    comite.setNombre(nombre);
    comite.setFormularioMemoriaId(formulario.getId());
    comite.setFormularioSeguimientoAnualId(formulario.getId());
    comite.setFormularioSeguimientoFinalId(formulario.getId());
    comite.setPrefijoReferencia("M10");
    comite.setRequiereRetrospectiva(Boolean.FALSE);
    comite.setPermitirRatificacion(Boolean.FALSE);
    comite.setPrefijoReferencia("M10");
    comite.setTareaNombreLibre(Boolean.TRUE);
    comite.setTareaExperienciaLibre(Boolean.TRUE);
    comite.setTareaExperienciaDetalle(Boolean.TRUE);
    comite.setMemoriaTituloLibre(Boolean.TRUE);
    comite.setActivo(Boolean.TRUE);

    return comite;
  }

}
package org.crue.hercules.sgi.csp.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.AreaTematicaDescripcion;
import org.crue.hercules.sgi.csp.repository.AreaTematicaRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueDescripcionAreaTematicaActivoValidator
    implements ConstraintValidator<UniqueDescripcionAreaTematicaActivo, AreaTematica> {

  private AreaTematicaRepository repository;
  private String field;

  public UniqueDescripcionAreaTematicaActivoValidator(AreaTematicaRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueDescripcionAreaTematicaActivo constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(AreaTematica value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }

    List<AreaTematica> areasTematicas = null;
    if (value.getPadre() == null) {
      // No se valida la descripcion en las areas tematicas padres
      return true;
    } else {
      AreaTematica areaTematicaPadre = getAreaTematicaPadre(value);
      if (areaTematicaPadre == null) {
        // Si no se encuentra el areaTematicaPadre NO se hace la validacion para que se
        // devuelva el
        // error correcto en la validacion correspondiente
        return true;
      }
      areasTematicas = findAllAreasTematicasHijas(areaTematicaPadre);
    }

    Optional<AreaTematicaDescripcion> descripcionDuplicado = getDescripcionDuplicado(value, areasTematicas);

    if (descripcionDuplicado.isPresent()) {
      addEntityMessageParameter(context, descripcionDuplicado.get());
      return false;
    }

    return true;
  }

  /**
   * Recupera el plan (areaTematica con {@link AreaTematica#padre} igual null) al
   * que
   * pertenece el {@link AreaTematica}
   * 
   * @param areaTematicaId Identificador del {@link AreaTematica}
   * @return el plan al que pertenece el {@link AreaTematica}, si el padre de
   *         alguno
   *         de los {@link AreaTematica}s recuperados hasta obtener el plan no
   *         existe
   *         devuelve <code>null</code>
   */
  private AreaTematica getAreaTematicaPadre(AreaTematica areaTematica) {
    while (areaTematica != null && areaTematica.getPadre() != null) {
      Long areaTematicaPadreId = areaTematica.getPadre().getId();
      areaTematica = repository.findById(areaTematicaPadreId).orElse(null);
    }

    return areaTematica;
  }

  /**
   * Devuelve la lista de areaTematicas que son hijos del areaTematica y
   * recursivamente de
   * todos sus hijos
   * 
   * @param areaTematica un {@link AreaTematica}
   * @return la lista completa de descencientes del areaTematica
   */
  private List<AreaTematica> findAllAreasTematicasHijas(AreaTematica areaTematica) {
    return findAllHijos(Arrays.asList(areaTematica));
  }

  /**
   * Devuelve la lista de areasTematicas que son hijos de alguno de los
   * areasTematicas de la lista y repite la busqueda sobre sus hijos hasta obtener
   * todos los descendientes de la lista de areasTematicas.
   * 
   * @param areasTematicas la lista de {@link AreaTematica}s
   * @return la lista completa de descencientes de los areasTematicas
   */
  private List<AreaTematica> findAllHijos(List<AreaTematica> areasTematicas) {
    if (areasTematicas.isEmpty()) {
      return new ArrayList<>();
    }

    List<AreaTematica> areasTematicasHijos = repository
        .findByPadreIdInAndActivoIsTrue(areasTematicas.stream().map(AreaTematica::getId).collect(Collectors.toList()));
    areasTematicasHijos.addAll(findAllHijos(areasTematicasHijos));

    return areasTematicasHijos;
  }

  /**
   * Comprueba si existe {@link AreaTematica} con la descripcion indicado en la
   * lista
   * de
   * areaTematicas
   *
   * @param areaTematica   El {@link AreaTematica} para el que se hace la
   *                       comprobacion.
   * @param areasTematicas Lista de {@link AreaTematica}s.
   * @return true si existe algun {@link AreaTematica} con esa descripcion.
   */
  private Optional<AreaTematicaDescripcion> getDescripcionDuplicado(AreaTematica areaTematica,
      List<AreaTematica> areasTematicas) {
    return areasTematicas.stream()
        .filter(p -> !p.getId().equals(areaTematica.getId()))
        .map(p -> getAreaTematicaDescripcionRepetido(areaTematica.getDescripcion(), p))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }

  /**
   * Recupera el {@link AreaTematicaDescripcion} repetido si existe en el
   * areaTematica
   * 
   * @param areasTematicasDescripcion Nombre que se comprueba
   * @param areaTematica              {@link AreaTematica} con el que se comprueba
   *                                  si
   *                                  tiene la misma descripcion en alguno de los
   *                                  idiomas
   * @return el {@link AreaTematicaDescripcion} repetido
   */
  private Optional<AreaTematicaDescripcion> getAreaTematicaDescripcionRepetido(
      Set<AreaTematicaDescripcion> areasTematicasDescripcion,
      AreaTematica areaTematica) {
    return areasTematicasDescripcion.stream()
        .filter(descripcionI18n -> areaTematica.getDescripcion().stream()
            .anyMatch(
                areaTematicaDescripcion -> areaTematicaDescripcion.getLang().equals(descripcionI18n.getLang())
                    && areaTematicaDescripcion.getValue().equals(descripcionI18n.getValue())))
        .findFirst();
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context, AreaTematicaDescripcion descripcionI18n) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("descripcion", descripcionI18n.getValue());

    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .enableExpressionLanguage(ExpressionLanguageFeatureLevel.BEAN_PROPERTIES)
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}

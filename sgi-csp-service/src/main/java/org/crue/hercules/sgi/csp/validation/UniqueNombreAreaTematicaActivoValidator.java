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
import org.crue.hercules.sgi.csp.model.AreaTematicaNombre;
import org.crue.hercules.sgi.csp.repository.AreaTematicaRepository;
import org.crue.hercules.sgi.csp.repository.specification.AreaTematicaSpecifications;
import org.crue.hercules.sgi.csp.util.SgiStringUtils;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueNombreAreaTematicaActivoValidator
    implements ConstraintValidator<UniqueNombreAreaTematicaActivo, AreaTematica> {

  private AreaTematicaRepository repository;
  private String field;

  public UniqueNombreAreaTematicaActivoValidator(AreaTematicaRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueNombreAreaTematicaActivo constraintAnnotation) {
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
      areasTematicas = findAllAreasTematicasPadres();
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

    Optional<AreaTematicaNombre> nombreDuplicado = getNombreDuplicado(value, areasTematicas);

    if (nombreDuplicado.isPresent()) {
      addEntityMessageParameter(context, nombreDuplicado.get());
      return false;
    }

    return true;
  }

  /**
   * Devuelve la lista de areasTematicas padres ({@link AreaTematica}s sin
   * {@link AreaTematica#padre})
   * activos
   * 
   * @return la lista de areasTematicas padres
   */
  private List<AreaTematica> findAllAreasTematicasPadres() {
    return repository.findAll(AreaTematicaSpecifications.padres().and(AreaTematicaSpecifications.activos()));
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
   * Comprueba si existe {@link AreaTematica} con el nombre indicado en la lista
   * de
   * areaTematicas
   *
   * @param areaTematica   El {@link AreaTematica} para el que se hace la
   *                       comprobacion.
   * @param areasTematicas Lista de {@link AreaTematica}s.
   * @return true si existe algun {@link AreaTematica} con ese nombre.
   */
  private Optional<AreaTematicaNombre> getNombreDuplicado(AreaTematica areaTematica,
      List<AreaTematica> areasTematicas) {
    return areasTematicas.stream()
        .filter(p -> !p.getId().equals(areaTematica.getId()))
        .map(p -> getAreaTematicaNombreRepetido(areaTematica.getNombre(), p))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }

  /**
   * Recupera el {@link AreaTematicaNombre} repetido si existe en el areaTematica
   * 
   * @param areasTematicasNombres Nombre que se comprueba
   * @param areaTematica          {@link AreaTematica} con el que se comprueba si
   *                              tiene el mismo nombre en alguno de los idiomas
   * @return el {@link AreaTematicaNombre} repetido
   */
  private Optional<AreaTematicaNombre> getAreaTematicaNombreRepetido(Set<AreaTematicaNombre> areasTematicasNombres,
      AreaTematica areaTematica) {
    return areasTematicasNombres.stream()
        .filter(nombreI18n -> areaTematica.getNombre().stream()
            .anyMatch(
                areaTematicaNombre -> areaTematicaNombre.getLang().equals(nombreI18n.getLang())
                    && SgiStringUtils.normalize(areaTematicaNombre.getValue())
                        .equals(SgiStringUtils.normalize(nombreI18n.getValue()))))
        .findFirst();
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context, AreaTematicaNombre nombreI18n) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("nombre", nombreI18n.getValue());

    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .enableExpressionLanguage(ExpressionLanguageFeatureLevel.BEAN_PROPERTIES)
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}

package org.crue.hercules.sgi.csp.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.ProgramaNombre;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProgramaSpecifications;
import org.crue.hercules.sgi.csp.util.SgiStringUtils;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueNombreProgramaActivoValidator
    implements ConstraintValidator<UniqueNombreProgramaActivo, Programa> {

  private ProgramaRepository repository;
  private String field;

  public UniqueNombreProgramaActivoValidator(ProgramaRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueNombreProgramaActivo constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(Programa value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }

    List<Programa> programas = null;
    if (value.getPadre() == null) {
      programas = findAllPlanes();
    } else {
      Programa plan = getPlan(value);
      if (plan == null) {
        // Si no se encuentra el plan NO se hace la validacion para que se devuelva el
        // error correcto en la validacion correspondiente
        return true;
      }
      programas = findAllDescendientes(plan);
    }

    Optional<ProgramaNombre> nombreDuplicado = getNombreDuplicado(value, programas);

    if (nombreDuplicado.isPresent()) {
      addEntityMessageParameter(context, nombreDuplicado.get(), value.getPadre() == null);
      return false;
    }

    return true;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context, ProgramaNombre nombreI18n,
      boolean isPlan) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("nombre", nombreI18n.getValue());

    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();

    String message = context.getDefaultConstraintMessageTemplate();

    if (isPlan) {
      message = "{org.crue.hercules.sgi.csp.validation.UniqueNombrePlanActivo.message}";
    }

    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(message)
        .enableExpressionLanguage(ExpressionLanguageFeatureLevel.BEAN_PROPERTIES)
        .addPropertyNode(ApplicationContextSupport.getMessage(field))
        .addConstraintViolation();
  }

  /**
   * Devuelve la lista de planes ({@link Programa}s sin {@link Programa#padre})
   * activos
   * 
   * @return la lista de planes
   */
  private List<Programa> findAllPlanes() {
    return repository.findAll(ProgramaSpecifications.planes().and(ProgramaSpecifications.activos()));
  }

  /**
   * Recupera el plan (programa con {@link Programa#padre} igual null) al que
   * pertenece el {@link Programa}
   * 
   * @param programaId Identificador del {@link Programa}
   * @return el plan al que pertenece el {@link Programa}, si el padre de alguno
   *         de los {@link Programa}s recuperados hasta obtener el plan no existe
   *         devuelve <code>null</code>
   */
  private Programa getPlan(Programa programa) {
    while (programa != null && programa.getPadre() != null) {
      Long programaPadreId = programa.getPadre().getId();
      programa = repository.findById(programaPadreId).orElse(null);
    }

    return programa;
  }

  /**
   * Devuelve la lista de programas que son hijos del programa y recursivamente de
   * todos sus hijos
   * 
   * @param programa un {@link Programa}
   * @return la lista completa de descencientes del programa
   */
  private List<Programa> findAllDescendientes(Programa programa) {
    return findAllHijos(Arrays.asList(programa));
  }

  /**
   * Devuelve la lista de programas que son hijos de alguno de los programas de la
   * lista y repite la busqueda
   * sobre sus hijos hasta obtener todos los descendientes de la lista de
   * programas.
   * 
   * @param programas la lista de {@link Programa}s
   * @return la lista completa de descencientes de los programas
   */
  private List<Programa> findAllHijos(List<Programa> programas) {
    if (programas.isEmpty()) {
      return new ArrayList<>();
    }

    List<Programa> programasHijos = repository
        .findByPadreIdInAndActivoIsTrue(programas.stream().map(Programa::getId).collect(Collectors.toList()));
    programasHijos.addAll(findAllHijos(programasHijos));

    return programasHijos;
  }

  /**
   * Comprueba si existe {@link Programa} con el nombre indicado en la lista de
   * programas
   *
   * @param programa  El {@link Programa} para el que se hace la comprobacion.
   * @param programas Lista de {@link Programa}s.
   * @return true si existe algun {@link Programa} con ese nombre.
   */
  private Optional<ProgramaNombre> getNombreDuplicado(Programa programa, List<Programa> programas) {
    return programas.stream()
        .filter(p -> !p.getId().equals(programa.getId()))
        .map(p -> getProgramaNombreRepetido(programa.getNombre(), p))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }

  /**
   * Recupera el {@link ProgramaNombre} repetido si existe en el programa
   * 
   * @param programaNombres Nombre que se comprueba
   * @param programa        {@link Programa} con el que se comprueba si tiene el
   *                        mismo
   *                        nombre en alguno de los idiomas
   * @return el {@link ProgramaNombre} repetido
   */
  private Optional<ProgramaNombre> getProgramaNombreRepetido(Set<ProgramaNombre> programaNombres, Programa programa) {
    return programaNombres.stream()
        .filter(nombreI18n -> programa.getNombre().stream()
            .anyMatch(
                programaNombre -> programaNombre.getLang().equals(nombreI18n.getLang())
                    && SgiStringUtils.normalize(programaNombre.getValue())
                        .equals(SgiStringUtils.normalize(nombreI18n.getValue()))))
        .findFirst();
  }

}

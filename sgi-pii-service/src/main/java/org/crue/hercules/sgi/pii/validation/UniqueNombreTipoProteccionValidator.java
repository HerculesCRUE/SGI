package org.crue.hercules.sgi.pii.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.TipoProteccion;
import org.crue.hercules.sgi.pii.model.TipoProteccionNombre;
import org.crue.hercules.sgi.pii.repository.TipoProteccionRepository;
import org.crue.hercules.sgi.pii.repository.specification.TipoProteccionSpecifications;
import org.crue.hercules.sgi.pii.util.SgiStringUtils;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueNombreTipoProteccionValidator
    implements ConstraintValidator<UniqueNombreTipoProteccion, TipoProteccion> {
  private TipoProteccionRepository repository;
  private String field;
  private String subtipoProteccionMesage;

  public UniqueNombreTipoProteccionValidator(TipoProteccionRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueNombreTipoProteccion constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
    subtipoProteccionMesage = constraintAnnotation.subtipoProteccionMessage();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(TipoProteccion value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }

    final boolean isSubtipo = value.getPadre() != null;

    List<TipoProteccion> tipoProtecciones = null;
    if (!isSubtipo) {
      tipoProtecciones = findAllTipos();
    } else {
      TipoProteccion tipoProteccionPadre = getTipoPadre(value);
      if (tipoProteccionPadre == null) {
        return true;
      }
      tipoProtecciones = findAllSubtipos(tipoProteccionPadre);
    }

    Optional<TipoProteccionNombre> returnValue = getNombreDuplicado(value, tipoProtecciones);

    if (returnValue.isPresent()) {
      addEntityMessageParameter(context, returnValue.get(), isSubtipo);
      return false;
    }

    return true;
  }

  /**
   * Devuelve la lista de ({@link TipoProteccion}es sin
   * {@link TipoProteccion#padre}) activos
   * 
   * @return la lista de {@link TipoProteccion}
   */
  private List<TipoProteccion> findAllTipos() {
    return repository.findAll(TipoProteccionSpecifications.noSubtipos().and(TipoProteccionSpecifications.activos()));
  }

  /**
   * Devuelve la lista de tipoProtecciones que son hijos del tipoProteccion y
   * recursivamente de
   * todos sus hijos
   * 
   * @param tipoProteccion un {@link TipoProteccion}
   * @return la lista completa de hijos y descencientes del tipoProteccion
   */
  private List<TipoProteccion> findAllSubtipos(TipoProteccion tipoProteccion) {
    return findAllHijos(Arrays.asList(tipoProteccion));
  }

  /**
   * Devuelve la lista de tipoProtecciones que son hijos de alguno de los
   * tipoProtecciones de la
   * lista y repite la busqueda
   * sobre sus hijos hasta obtener todos los descendientes de la lista de
   * tipoProtecciones.
   * 
   * @param tipoProtecciones la lista de {@link TipoProteccion}es
   * @return la lista completa de descencientes de los tipoProtecciones
   */
  private List<TipoProteccion> findAllHijos(List<TipoProteccion> tipoProtecciones) {
    if (tipoProtecciones.isEmpty()) {
      return new ArrayList<>();
    }

    List<TipoProteccion> tipoProteccionesHijos = repository
        .findByPadreIdInAndActivoIsTrue(tipoProtecciones.stream().map(TipoProteccion::getId).toList());
    tipoProteccionesHijos.addAll(findAllHijos(tipoProteccionesHijos));

    return tipoProteccionesHijos;
  }

  /**
   * Recupera el plan (tipoProteccion con {@link TipoProteccion#padre} igual null)
   * al que pertenece el {@link TipoProteccion}
   * 
   * @param tipoProteccionId Identificador del {@link TipoProteccion}
   * @return el {@link TipoProteccion}, si el padre de alguno
   *         de los {@link TipoProteccion}es recuperados hasta obtener el padre no
   *         existe devuelve <code>null</code>
   */
  private TipoProteccion getTipoPadre(TipoProteccion tipoProteccion) {
    while (tipoProteccion != null && tipoProteccion.getPadre() != null) {
      Long tipoProteccionPadreId = tipoProteccion.getPadre().getId();
      tipoProteccion = repository.findById(tipoProteccionPadreId).orElse(null);
    }

    return tipoProteccion;
  }

  /**
   * Comprueba si existe {@link TipoProteccion} con el nombre indicado en la lista
   * de
   * tipoProtecciones
   *
   * @param tipoProteccion   El {@link TipoProteccion} para el que se hace la
   *                         comprobacion.
   * @param tipoProtecciones Lista de {@link TipoProteccion}es.
   * @return true si existe algun {@link TipoProteccion} con ese nombre.
   */
  private Optional<TipoProteccionNombre> getNombreDuplicado(TipoProteccion tipoProteccion,
      List<TipoProteccion> tipoProtecciones) {
    return tipoProtecciones.stream()
        .filter(p -> !p.getId().equals(tipoProteccion.getId()))
        .map(p -> getTipoProteccionNombreRepetido(tipoProteccion.getNombre(), p))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }

  /**
   * Recupera el {@link TipoProteccionNombre} repetido si existe en el
   * tipoProteccion
   * 
   * @param tipoProteccionNombres Nombre que se comprueba
   * @param tipoProteccion        {@link TipoProteccion} con el que se comprueba
   *                              si tiene el
   *                              mismo
   *                              nombre en alguno de los idiomas
   * @return el {@link TipoProteccionNombre} repetido
   */
  private Optional<TipoProteccionNombre> getTipoProteccionNombreRepetido(
      Set<TipoProteccionNombre> tipoProteccionNombres,
      TipoProteccion tipoProteccion) {
    return tipoProteccionNombres.stream()
        .filter(nombreI18n -> tipoProteccion.getNombre().stream()
            .anyMatch(
                tipoProteccionNombre -> tipoProteccionNombre.getLang().equals(nombreI18n.getLang())
                    && SgiStringUtils.normalize(tipoProteccionNombre.getValue())
                        .equals(SgiStringUtils.normalize(nombreI18n.getValue()))))
        .findFirst();
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context, TipoProteccionNombre nombreI18n,
      Boolean isSubtipo) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("nombre", nombreI18n.getValue());

    final String errorMessage = Boolean.TRUE.equals(isSubtipo)
        ? ApplicationContextSupport.getMessage(this.subtipoProteccionMesage)
        : ApplicationContextSupport.getMessage(TipoProteccion.class);
    hibernateContext.addMessageParameter("entity", errorMessage);
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .enableExpressionLanguage(ExpressionLanguageFeatureLevel.BEAN_PROPERTIES)
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}

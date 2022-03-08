package org.crue.hercules.sgi.csp.validation;

import java.util.List;
import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.CertificadoAutorizacion;
import org.crue.hercules.sgi.csp.model.CertificadoAutorizacion_;
import org.crue.hercules.sgi.csp.repository.CertificadoAutorizacionRepository;
import org.springframework.data.jpa.domain.Specification;

import lombok.RequiredArgsConstructor;

import org.crue.hercules.sgi.csp.repository.specification.CertificadoAutorizacionSpecifications;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueCertificadoAutorizacionVisibleValidator
    implements ConstraintValidator<UniqueCertificadoAutorizacionVisible, CertificadoAutorizacion> {

  private final CertificadoAutorizacionRepository certificadoAutorizacionRepository;

  private String field;

  @Override
  public void initialize(UniqueCertificadoAutorizacionVisible constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  public boolean isValid(CertificadoAutorizacion value, ConstraintValidatorContext context) {

    Specification<CertificadoAutorizacion> specs = CertificadoAutorizacionSpecifications.visibles()
        .and(CertificadoAutorizacionSpecifications.byAutorizacionId(value.getAutorizacionId()));

    specs = specs.and((root, query, cb) -> cb.equal(root.get(CertificadoAutorizacion_.visible), true));

    List<CertificadoAutorizacion> certificadosAutorizacion = this.certificadoAutorizacionRepository.findAll(specs);
    boolean isMoreThanOneVisible = certificadosAutorizacion.stream()
        .anyMatch(certificado -> !Objects.equals(value.getId(), certificado.getId()));

    if (isMoreThanOneVisible) {
      addEntityMessageParameter(context);
    }
    return !isMoreThanOneVisible;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(CertificadoAutorizacion.class));
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }

}

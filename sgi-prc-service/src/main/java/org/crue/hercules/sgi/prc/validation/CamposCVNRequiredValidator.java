package org.crue.hercules.sgi.prc.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.collections4.ListUtils;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiCreateInput;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Validates that the provided collection of campos contains mandatory fields
 */
@Slf4j
public class CamposCVNRequiredValidator
    implements ConstraintValidator<CamposCVNRequired, ProduccionCientificaApiCreateInput> {

  Map<String, List<String>> mapCamposCVNRequired = new HashMap<>();

  @Override
  public void initialize(CamposCVNRequired camposCVNRequired) {
    mapCamposCVNRequired = new HashMap<>();

    mapCamposCVNRequired.put(EpigrafeCVN.E060_010_010_000.getCode(),
        Arrays.asList(CodigoCVN.E060_010_010_030.getCode(),
            CodigoCVN.E060_010_010_010.getCode(),
            CodigoCVN.E060_010_010_140.getCode()));

    mapCamposCVNRequired.put(EpigrafeCVN.E060_010_020_000.getCode(),
        Arrays.asList(CodigoCVN.E060_010_020_010.getCode(),
            CodigoCVN.E060_010_020_030.getCode(),
            CodigoCVN.E060_010_020_190.getCode()));

    mapCamposCVNRequired.put(EpigrafeCVN.E060_020_030_000.getCode(),
        Arrays.asList(CodigoCVN.E060_020_030_010.getCode(),
            CodigoCVN.E060_020_030_160.getCode()));

    mapCamposCVNRequired.put(EpigrafeCVN.E060_030_030_000.getCode(),
        Arrays.asList(CodigoCVN.E060_030_030_010.getCode(),
            CodigoCVN.E060_030_030_140.getCode()));

    mapCamposCVNRequired.put(EpigrafeCVN.E030_040_000_000.getCode(),
        Arrays.asList(CodigoCVN.E030_040_000_030.getCode(),
            CodigoCVN.E030_040_000_140.getCode()));

    mapCamposCVNRequired.put(EpigrafeCVN.E050_020_030_000.getCode(),
        Arrays.asList(CodigoCVN.E050_020_030_010.getCode(),
            CodigoCVN.E050_020_030_120.getCode()));

  }

  /**
   * @param prcCreateinput             prcCreateinput
   * @param constraintValidatorContext context in which the constraint is
   *                                   evaluated
   *
   * @return true if the input collection is null or does not contain duplicate
   *         elements
   */
  @Override
  public boolean isValid(ProduccionCientificaApiCreateInput prcCreateinput,
      ConstraintValidatorContext constraintValidatorContext) {
    String camposNotFilledParameter = "";
    try {
      List<String> camposCVNError = findCamposCVNNotFilled(prcCreateinput);

      if (camposCVNError.isEmpty()) {
        return true;
      }

      camposNotFilledParameter = camposCVNError.stream().collect(Collectors.joining(", "));

    } catch (Exception e) {
      log.debug(e.getMessage());
    } finally {
      addEntityMessageParameter(camposNotFilledParameter, constraintValidatorContext);
    }
    return false;
  }

  private List<String> findCamposCVNNotFilled(ProduccionCientificaApiCreateInput prcCreateinput) {
    List<String> camposCVNError = new ArrayList<>();
    if (mapCamposCVNRequired.containsKey(prcCreateinput.getEpigrafeCVN())) {
      camposCVNError = mapCamposCVNRequired.get(prcCreateinput.getEpigrafeCVN()).stream()
          .filter(campoCVNRequired -> ListUtils.emptyIfNull(prcCreateinput.getCampos()).stream()
              .noneMatch(campoCVN -> campoCVN.getCodigoCVN().equals(campoCVNRequired)))
          .collect(Collectors.toList());
    }
    return camposCVNError;
  }

  private void addEntityMessageParameter(String camposNotFilledParameter,
      ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("camposNotFilled", camposNotFilledParameter);
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode("campos")
        .addConstraintViolation();
  }

}

import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { II18nConfigValue } from '@core/models/cnf/i18n-config-value';
import { SgiBaseConverter } from '@sgi/framework/core';
import { II18nConfigValueRequest } from './i18n-config-value-request';

class I18nConfigValueRequestConverter extends SgiBaseConverter<II18nConfigValueRequest, II18nConfigValue> {
  toTarget(value: II18nConfigValueRequest): II18nConfigValue {
    if (!value) {
      return value as unknown as II18nConfigValue;
    }
    return {
      name: undefined,
      description: value.description,
      value: value.value ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.value) : []
    };
  }

  fromTarget(value: II18nConfigValue): II18nConfigValueRequest {
    if (!value) {
      return value as unknown as II18nConfigValueRequest;
    }
    return {
      description: value.description,
      value: value.value ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.value) : []
    };
  }
}

export const I18N_CONFIG_VALUE_REQUEST_CONVERTER = new I18nConfigValueRequestConverter();

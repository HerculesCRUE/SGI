import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { II18nConfigValue } from '@core/models/cnf/i18n-config-value';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IConfigValueResponse } from './config-value-response';

class I18nConfigValueResponseConverter extends SgiBaseConverter<IConfigValueResponse, II18nConfigValue> {
  toTarget(value: IConfigValueResponse): II18nConfigValue {
    if (!value) {
      return value as unknown as II18nConfigValue;
    }
    return {
      name: value.name,
      description: value.description,
      value: value.value ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(JSON.parse(value.value)) : []
    };
  }

  fromTarget(value: II18nConfigValue): IConfigValueResponse {
    if (!value) {
      return value as unknown as IConfigValueResponse;
    }
    return {
      name: value.name,
      description: value.description,
      value: value.value ? JSON.stringify(I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.value)) : ''
    };
  }
}

export const I18N_CONFIG_VALUE_RESPONSE_CONVERTER = new I18nConfigValueResponseConverter();

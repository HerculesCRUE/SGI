import { IConfigValue } from '@core/models/cnf/config-value';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IConfigValueResponse } from './config-value-response';

class ConfigValueResponseConverter extends SgiBaseConverter<IConfigValueResponse, IConfigValue> {
  toTarget(value: IConfigValueResponse): IConfigValue {
    if (!value) {
      return value as unknown as IConfigValue;
    }
    return {
      name: value.name,
      description: value.description,
      value: value.value
    };
  }

  fromTarget(value: IConfigValue): IConfigValueResponse {
    if (!value) {
      return value as unknown as IConfigValueResponse;
    }
    return {
      name: value.name,
      description: value.description,
      value: value.value
    };
  }
}

export const CONFIG_VALUE_RESPONSE_CONVERTER = new ConfigValueResponseConverter();

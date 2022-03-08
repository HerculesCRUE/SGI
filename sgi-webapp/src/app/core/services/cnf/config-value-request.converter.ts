import { IConfigValue } from '@core/models/cnf/config-value';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IConfigValueRequest } from './config-value-request';

class ConfigValueRequestConverter extends SgiBaseConverter<IConfigValueRequest, IConfigValue> {
  toTarget(value: IConfigValueRequest): IConfigValue {
    if (!value) {
      return value as unknown as IConfigValue;
    }
    return {
      name: undefined,
      description: value.description,
      value: value.value
    };
  }

  fromTarget(value: IConfigValue): IConfigValueRequest {
    if (!value) {
      return value as unknown as IConfigValueRequest;
    }
    return {
      description: value.description,
      value: value.value
    };
  }
}

export const CONFIG_VALUE_REQUEST_CONVERTER = new ConfigValueRequestConverter();

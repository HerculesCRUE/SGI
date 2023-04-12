import { IResourceInfo } from '@core/models/cnf/resource-info';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IResourceInfoResponse } from './resource-info-response';

class ResourceInfoResponseConverter extends SgiBaseConverter<IResourceInfoResponse, IResourceInfo> {
  toTarget(value: IResourceInfoResponse): IResourceInfo {
    if (!value) {
      return value as unknown as IResourceInfo;
    }
    return {
      name: value.name,
      description: value.description
    };
  }

  fromTarget(value: IResourceInfo): IResourceInfoResponse {
    if (!value) {
      return value as unknown as IResourceInfoResponse;
    }
    return {
      name: value.name,
      description: value.description
    };
  }
}

export const RESOURCE_INFO_RESPONSE_CONVERTER = new ResourceInfoResponseConverter();

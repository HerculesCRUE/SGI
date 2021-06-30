import { IRetrospectivaBackend } from '@core/models/eti/backend/retrospectiva-backend';
import { IRetrospectiva } from '@core/models/eti/retrospectiva';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class RetrospectivaConverter extends SgiBaseConverter<IRetrospectivaBackend, IRetrospectiva> {
  toTarget(value: IRetrospectivaBackend): IRetrospectiva {
    if (!value) {
      return value as unknown as IRetrospectiva;
    }
    return {
      id: value.id,
      estadoRetrospectiva: value.estadoRetrospectiva,
      fechaRetrospectiva: LuxonUtils.fromBackend(value.fechaRetrospectiva)
    };
  }

  fromTarget(value: IRetrospectiva): IRetrospectivaBackend {
    if (!value) {
      return value as unknown as IRetrospectivaBackend;
    }
    return {
      id: value.id,
      estadoRetrospectiva: value.estadoRetrospectiva,
      fechaRetrospectiva: LuxonUtils.toBackend(value.fechaRetrospectiva)
    };
  }
}

export const RETROSPECTIVA_CONVERTER = new RetrospectivaConverter();

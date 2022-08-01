import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { INivelAcademico } from '@core/models/sgp/nivel-academico';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRequisitoIPNivelAcademicoRequest } from './requisito-ip-nivel-academico-request';

class RequisitoIPNivelAcademicoRequestConverter extends SgiBaseConverter<IRequisitoIPNivelAcademicoRequest, IRequisitoIPNivelAcademico>{
  toTarget(value: IRequisitoIPNivelAcademicoRequest): IRequisitoIPNivelAcademico {
    if (!value) {
      return value as unknown as IRequisitoIPNivelAcademico;
    }
    return {
      id: value.id,
      requisitoIP: {
        id: value.requisitoIPId
      } as IConvocatoriaRequisitoIP,
      nivelAcademico: {
        id: value.nivelAcademicoRef
      } as INivelAcademico
    };
  }
  fromTarget(value: IRequisitoIPNivelAcademico): IRequisitoIPNivelAcademicoRequest {
    if (!value) {
      return value as unknown as IRequisitoIPNivelAcademicoRequest;
    }
    return {
      id: value.id,
      requisitoIPId: value.requisitoIP?.id,
      nivelAcademicoRef: value.nivelAcademico?.id,
    };
  }
}

export const REQUISITOIP_NIVELACADEMICO_REQUEST_CONVERTER = new RequisitoIPNivelAcademicoRequestConverter();

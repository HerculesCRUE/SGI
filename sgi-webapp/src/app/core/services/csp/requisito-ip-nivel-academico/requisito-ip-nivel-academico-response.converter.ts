import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { INivelAcademico } from '@core/models/sgp/nivel-academico';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRequisitoIPNivelAcademicoResponse } from './requisito-ip-nivel-academico-response';

class RequisitoIPNivelAcademicoResponseConverter extends SgiBaseConverter<IRequisitoIPNivelAcademicoResponse, IRequisitoIPNivelAcademico>{
  toTarget(value: IRequisitoIPNivelAcademicoResponse): IRequisitoIPNivelAcademico {
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
  fromTarget(value: IRequisitoIPNivelAcademico): IRequisitoIPNivelAcademicoResponse {
    if (!value) {
      return value as unknown as IRequisitoIPNivelAcademicoResponse;
    }
    return {
      id: value.id,
      requisitoIPId: value.requisitoIP?.id,
      nivelAcademicoRef: value.nivelAcademico?.id,
    };
  }
}

export const REQUISITOIP_NIVELACADEMICO_RESPONSE_CONVERTER = new RequisitoIPNivelAcademicoResponseConverter();

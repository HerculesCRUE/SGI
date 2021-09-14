import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { INivelAcademico } from '@core/models/sgp/nivel-academico';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRequisitoIPCategoriaProfesionalResponse } from './requisito-ip-categoria-profesional-response';

class RequisitoIPCategoriaProfesionalResponseConverter extends
  SgiBaseConverter<IRequisitoIPCategoriaProfesionalResponse, IRequisitoIPCategoriaProfesional>{
  toTarget(value: IRequisitoIPCategoriaProfesionalResponse): IRequisitoIPCategoriaProfesional {
    if (!value) {
      return value as unknown as IRequisitoIPCategoriaProfesional;
    }
    return {
      id: value.id,
      requisitoIP: {
        id: value.requisitoIPId
      } as IConvocatoriaRequisitoIP,
      categoriaProfesional: {
        id: value.categoriaProfesionalRef
      } as INivelAcademico
    };
  }
  fromTarget(value: IRequisitoIPCategoriaProfesional): IRequisitoIPCategoriaProfesionalResponse {
    if (!value) {
      return value as unknown as IRequisitoIPCategoriaProfesionalResponse;
    }
    return {
      id: value.id,
      requisitoIPId: value.requisitoIP?.id,
      categoriaProfesionalRef: value.categoriaProfesional?.id,
    };
  }
}

export const REQUISITOIP_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER = new RequisitoIPCategoriaProfesionalResponseConverter();

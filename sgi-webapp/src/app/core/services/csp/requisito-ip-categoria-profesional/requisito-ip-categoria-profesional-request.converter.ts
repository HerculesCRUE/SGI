import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { INivelAcademico } from '@core/models/sgp/nivel-academico';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRequisitoIPNivelAcademicoRequest } from '../requisito-ip-nivel-academico/requisito-ip-nivel-academico-request';
import { IRequisitoIPCategoriaProfesionalRequest } from './requisito-ip-categoria-profesional-request';

class RequisitoIPCategoriaProfesionalRequestConverter extends
  SgiBaseConverter<IRequisitoIPCategoriaProfesionalRequest, IRequisitoIPCategoriaProfesional>{
  toTarget(value: IRequisitoIPCategoriaProfesionalRequest): IRequisitoIPCategoriaProfesional {
    if (!value) {
      return value as unknown as IRequisitoIPCategoriaProfesional;
    }
    return {
      id: undefined,
      requisitoIP: {
        id: value.requisitoIPId
      } as IConvocatoriaRequisitoIP,
      categoriaProfesional: {
        id: value.categoriaProfesionalRef
      } as INivelAcademico
    };
  }
  fromTarget(value: IRequisitoIPCategoriaProfesional): IRequisitoIPCategoriaProfesionalRequest {
    if (!value) {
      return value as unknown as IRequisitoIPCategoriaProfesionalRequest;
    }
    return {
      requisitoIPId: value.requisitoIP?.id,
      categoriaProfesionalRef: value.categoriaProfesional?.id,
    };
  }
}

export const REQUISITOIP_CATEGORIA_PROFESIONAL_REQUEST_CONVERTER = new RequisitoIPCategoriaProfesionalRequestConverter();

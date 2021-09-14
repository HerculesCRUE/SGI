import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IRequisitoEquipoCategoriaProfesional } from '@core/models/csp/requisito-equipo-categoria-profesional';
import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRequisitoEquipoCategoriaProfesionalRequest } from './requisito-equipo-categoria-profesional-request';

class RequisitoEquipoCategoriaProfesionalRequestConverter extends
  SgiBaseConverter<IRequisitoEquipoCategoriaProfesionalRequest, IRequisitoEquipoCategoriaProfesional>{
  toTarget(value: IRequisitoEquipoCategoriaProfesionalRequest): IRequisitoEquipoCategoriaProfesional {
    if (!value) {
      return value as unknown as IRequisitoEquipoCategoriaProfesional;
    }
    return {
      id: undefined,
      requisitoEquipo: {
        id: value.requisitoEquipoId
      } as IConvocatoriaRequisitoEquipo,
      categoriaProfesional: {
        id: value.categoriaProfesionalRef
      } as ICategoriaProfesional
    };
  }
  fromTarget(value: IRequisitoEquipoCategoriaProfesional): IRequisitoEquipoCategoriaProfesionalRequest {
    if (!value) {
      return value as unknown as IRequisitoEquipoCategoriaProfesionalRequest;
    }
    return {
      requisitoEquipoId: value.requisitoEquipo?.id,
      categoriaProfesionalRef: value.categoriaProfesional?.id,
    };
  }
}

export const REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_REQUEST_CONVERTER = new RequisitoEquipoCategoriaProfesionalRequestConverter();

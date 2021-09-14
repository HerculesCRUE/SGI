import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IRequisitoEquipoCategoriaProfesional } from '@core/models/csp/requisito-equipo-categoria-profesional';
import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRequisitoEquipoCategoriaProfesionalResponse } from './requisito-equipo-categoria-profesional-response';

class RequisitoEquipoCategoriaProfesionalResponseConverter extends
  SgiBaseConverter<IRequisitoEquipoCategoriaProfesionalResponse, IRequisitoEquipoCategoriaProfesional>{
  toTarget(value: IRequisitoEquipoCategoriaProfesionalResponse): IRequisitoEquipoCategoriaProfesional {
    if (!value) {
      return value as unknown as IRequisitoEquipoCategoriaProfesional;
    }
    return {
      id: value.id,
      requisitoEquipo: {
        id: value.requisitoEquipoId
      } as IConvocatoriaRequisitoEquipo,
      categoriaProfesional: {
        id: value.categoriaProfesionalRef
      } as ICategoriaProfesional
    };
  }
  fromTarget(value: IRequisitoEquipoCategoriaProfesional): IRequisitoEquipoCategoriaProfesionalResponse {
    if (!value) {
      return value as unknown as IRequisitoEquipoCategoriaProfesionalResponse;
    }
    return {
      id: value.id,
      requisitoEquipoId: value.requisitoEquipo?.id,
      categoriaProfesionalRef: value.categoriaProfesional?.id,
    };
  }
}

export const REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER = new RequisitoEquipoCategoriaProfesionalResponseConverter();

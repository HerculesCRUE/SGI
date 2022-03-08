import { IAutor } from '@core/models/prc/autor';
import { IAutorGrupo } from '@core/models/prc/autor-grupo';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IAutorGrupoResponse } from './autor-grupo-response';

class AutorGrupoResponseConverter extends SgiBaseConverter<IAutorGrupoResponse, IAutorGrupo>{
  toTarget(value: IAutorGrupoResponse): IAutorGrupo {
    if (!value) {
      return value as unknown as IAutorGrupo;
    }
    return {
      id: value.id,
      autor: value.autorId ?
        { id: value.autorId } as IAutor : null,
      estado: value.estado,
      grupoRef: value.grupoRef
    };
  }
  fromTarget(value: IAutorGrupo): IAutorGrupoResponse {
    if (!value) {
      return value as unknown as IAutorGrupoResponse;
    }
    return {
      id: value.id,
      autorId: value.autor?.id,
      estado: value.estado,
      grupoRef: value.grupoRef
    };
  }
}

export const AUTOR_GRUPO_RESPONSE_CONVERTER = new AutorGrupoResponseConverter();

import { IRelacionEliminada } from "@core/models/sge/relacion-eliminada";
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRelacionEliminadaRequest } from "./relacion-eliminada-request";

class RelacionEliminadaRequestConverter extends SgiBaseConverter<IRelacionEliminadaRequest, IRelacionEliminada> {

  toTarget(value: IRelacionEliminadaRequest): IRelacionEliminada {
    return !!!value ? value as unknown as IRelacionEliminada :
      {
        entidadSGIId: value.entidadSGIId,
        tipoEntidadSGI: value.tipoEntidadSGI,
      };
  }

  fromTarget(value: IRelacionEliminada): IRelacionEliminadaRequest {
    return !!!value ? value as unknown as IRelacionEliminadaRequest :
      {
        entidadSGIId: value.entidadSGIId,
        tipoEntidadSGI: value.tipoEntidadSGI,
      }
  }
}

export const RELACION_ELIMINADA_REQUEST_CONVERTER = new RelacionEliminadaRequestConverter();

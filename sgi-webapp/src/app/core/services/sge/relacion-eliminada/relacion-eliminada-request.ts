import { TipoEntidadSGI } from "@core/models/sge/relacion-eliminada";

export interface IRelacionEliminadaRequest {
  entidadSGIId: string;
  tipoEntidadSGI: TipoEntidadSGI;
}

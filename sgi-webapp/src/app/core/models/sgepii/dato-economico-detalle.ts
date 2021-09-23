import { IDocumento } from "../sge/documento";

export interface IDatoEconomicoDetalle {
  id: string;
  documentos: IDocumento[];
}

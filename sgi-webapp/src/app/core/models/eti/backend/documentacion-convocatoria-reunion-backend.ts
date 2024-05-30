import { IConvocatoriaReunionBackend } from "./convocatoria-reunion-backend";


export interface IDocumentacionConvocatoriaReunionBackend {
  /** Id */
  id: number;
  /** Convocatoria Reunion */
  convocatoriaReunionId: number;
  /** Nombre */
  nombre: string;
  /** Ref del documento */
  documentoRef: string;
}

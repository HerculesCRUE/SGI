import { IDocumento } from '../sgdoc/documento';
import { IConvocatoriaReunion } from './convocatoria-reunion';

export interface IDocumentacionConvocatoriaReunion {
  /** Id */
  id: number;
  /** ConvocatoriaReunion */
  convocatoriaReunion: IConvocatoriaReunion;
  /** nombre */
  nombre: string;
  /** Ref del documento */
  documento: IDocumento;
}

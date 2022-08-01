import { IDocumento } from '../sgdoc/documento';
import { IRequisitoIPNivelAcademico } from './requisito-ip-nivel-academico';

export interface ISolicitudRrhhRequisitoNivelAcademico {
  id: number;
  solicitudRrhhId: number;
  requisitoIpNivelAcademico: IRequisitoIPNivelAcademico;
  documento: IDocumento;
}

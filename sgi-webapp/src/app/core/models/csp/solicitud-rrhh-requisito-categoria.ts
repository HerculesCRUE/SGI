import { IDocumento } from '../sgdoc/documento';
import { IRequisitoIPCategoriaProfesional } from './requisito-ip-categoria-profesional';

export interface ISolicitudRrhhRequisitoCategoria {
  id: number;
  solicitudRrhhId: number;
  requisitoIpCategoria: IRequisitoIPCategoriaProfesional;
  documento: IDocumento;
}

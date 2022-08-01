import { IEmail } from '../email';
import { ISexo } from '../sexo';
import { ITipoDocumento } from '../tipo-documento';

export interface IPersonaBackend {
  id: string;
  nombre: string;
  apellidos: string;
  sexo: ISexo;
  tipoDocumento: ITipoDocumento;
  numeroDocumento: string;
  empresaRef: string;
  personalPropio: boolean;
  entidadPropiaRef: string;
  emails: IEmail[];
}

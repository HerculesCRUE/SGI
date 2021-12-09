import { IDatosAcademicos } from '../datos-academicos';
import { IEmail } from '../email';
import { ISexo } from '../sexo';
import { ITipoDocumento } from '../tipo-documento';
import { IVinculacion } from '../vinculacion';

export interface IPersonaBackend {
  id: string;
  nombre: string;
  apellidos: string;
  sexo: ISexo;
  tipoDocumento: ITipoDocumento;
  numeroDocumento: string;
  vinculacion: IVinculacion;
  datosAcademicos: IDatosAcademicos;
  empresaRef: string;
  personalPropio: boolean;
  entidadPropiaRef: string;
  emails: IEmail[];
}

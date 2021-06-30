import { IEmpresa } from '../sgemp/empresa';
import { IDatosAcademicos } from './datos-academicos';
import { ISexo } from './sexo';
import { ITipoDocumento } from './tipo-documento';
import { IVinculacion } from './vinculacion';

export interface IPersona {
  id: string;
  nombre: string;
  apellidos: string;
  sexo: ISexo;
  tipoDocumento: ITipoDocumento;
  numeroDocumento: string;
  vinculacion: IVinculacion;
  datosAcademicos: IDatosAcademicos;
  entidad: IEmpresa;
}

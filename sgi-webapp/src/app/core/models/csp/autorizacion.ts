import { IEmpresa } from '../sgemp/empresa';
import { IPersona } from '../sgp/persona';
import { IConvocatoria } from './convocatoria';
import { IEstadoAutorizacion } from './estado-autorizacion';

export interface IAutorizacion {
  id: number;
  observaciones: string;
  responsable: IPersona;
  solicitante: IPersona;
  tituloProyecto: string;
  entidad: IEmpresa;
  horasDedicacion: number;
  datosResponsable: string;
  datosEntidad: string;
  datosConvocatoria: string;
  convocatoria: IConvocatoria;
  estado: IEstadoAutorizacion;
}

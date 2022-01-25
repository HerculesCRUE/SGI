import { DateTime } from 'luxon';
import { IEmpresa } from '../sgemp/empresa';
import { IPersona } from '../sgp/persona';
import { IAutorizacion } from './autorizacion';
import { IProyecto } from './proyecto';
import { ITipoAmbitoGeografico } from './tipo-ambito-geografico';

export interface INotificacionProyectoExternoCVN {
  id: number;
  titulo: string;
  autorizacion: IAutorizacion;
  proyecto: IProyecto;
  ambitoGeografico: ITipoAmbitoGeografico;
  codExterno: string;
  datosEntidadParticipacion: string;
  datosResponsable: string;
  documentoRef: string;
  entidadParticipacion: IEmpresa;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  gradoContribucion: string;
  importeTotal: number;
  nombrePrograma: string;
  porcentajeSubvencion: number;
  proyectoCVNId: string;
  responsable: IPersona;
  urlDocumentoAcreditacion: string;
  solicitante: IPersona;
}

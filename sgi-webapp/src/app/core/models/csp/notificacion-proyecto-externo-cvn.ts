import { DateTime } from 'luxon';
import { IDocumento } from '../sgdoc/documento';
import { IEmpresa } from '../sgemp/empresa';
import { IPersona } from '../sgp/persona';
import { IAutorizacion } from './autorizacion';
import { IProyecto } from './proyecto';

export interface INotificacionProyectoExternoCVN {
  id: number;
  titulo: string;
  autorizacion: IAutorizacion;
  proyecto: IProyecto;
  ambitoGeografico: string;
  codExterno: string;
  datosEntidadParticipacion: string;
  datosResponsable: string;
  documento: IDocumento;
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

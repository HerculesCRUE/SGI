import { ClasificacionCVN } from '@core/enums/clasificacion-cvn';
import { Estado } from '../convocatoria';
import { ITipoAmbitoGeografico } from '../tipo-ambito-geografico';
import { ITipoRegimenConcurrencia } from '../tipo-regimen-concurrencia';
import { IModeloEjecucion, ITipoFinalidad } from '../tipos-configuracion';

export interface IConvocatoriaBackend {
  id: number;
  unidadGestionRef: string;
  modeloEjecucion: IModeloEjecucion;
  codigo: string;
  fechaPublicacion: string;
  fechaProvisional: string;
  fechaConcesion: string;
  titulo: string;
  objeto: string;
  observaciones: string;
  finalidad: ITipoFinalidad;
  regimenConcurrencia: ITipoRegimenConcurrencia;
  estado: Estado;
  duracion: number;
  abiertoPlazoPresentacionSolicitud: boolean;
  ambitoGeografico: ITipoAmbitoGeografico;
  clasificacionCVN: ClasificacionCVN;
  activo: boolean;
}

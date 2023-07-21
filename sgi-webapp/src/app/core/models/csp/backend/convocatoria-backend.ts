import { ClasificacionCVN } from '@core/enums/clasificacion-cvn';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { Estado } from '../convocatoria';
import { ITipoAmbitoGeografico } from '../tipos-configuracion';
import { ITipoRegimenConcurrencia } from '../tipos-configuracion';
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
  formularioSolicitud: FormularioSolicitud;
  estado: Estado;
  duracion: number;
  abiertoPlazoPresentacionSolicitud: boolean;
  ambitoGeografico: ITipoAmbitoGeografico;
  clasificacionCVN: ClasificacionCVN;
  activo: boolean;
  excelencia: boolean;
}

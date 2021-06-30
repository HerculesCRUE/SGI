import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { IEstadoSolicitudBackend } from './estado-solicitud-backend';

export interface ISolicitudBackend {
  /** Id */
  id: number;
  /** Activo */
  activo: boolean;
  /** Codigo externo */
  codigoExterno: string;
  /** Codigo registro interno */
  codigoRegistroInterno: string;
  /** Estado solicitud */
  estado: IEstadoSolicitudBackend;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Convocatoria externa */
  convocatoriaExterna: string;
  /** Creador */
  creadorRef: string;
  /** Solicitante */
  solicitanteRef: string;
  /** Tipo formulario solicitud */
  formularioSolicitud: FormularioSolicitud;
  /** Unidad gestion */
  unidadGestionRef: string;
  /** Observaciones */
  observaciones: string;
}

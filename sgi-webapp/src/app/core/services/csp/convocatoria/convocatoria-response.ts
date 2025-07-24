import { ClasificacionCVN } from '@core/enums/clasificacion-cvn';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { IModeloEjecucionResponse } from '@core/services/csp/modelo-ejecucion/modelo-ejecucion-response';
import { ITipoAmbitoGeograficoResponse } from '@core/services/csp/tipo-ambito-geografico/tipo-ambito-geografico-response';
import { ITipoFinalidadResponse } from '@core/services/csp/tipo-finalidad/tipo-finalidad-response';
import { ITipoRegimenConcurrenciaResponse } from '@core/services/csp/tipo-regimen-concurrencia/tipo-regimen-concurrencia-response';
import { Estado } from '../../../models/csp/convocatoria';

export interface IConvocatoriaResponse {
  id: number;
  unidadGestionRef: string;
  modeloEjecucion: IModeloEjecucionResponse;
  codigo: string;
  fechaPublicacion: string;
  fechaProvisional: string;
  fechaConcesion: string;
  titulo: I18nFieldValueResponse[];
  objeto: I18nFieldValueResponse[];
  observaciones: I18nFieldValueResponse[];
  finalidad: ITipoFinalidadResponse;
  regimenConcurrencia: ITipoRegimenConcurrenciaResponse;
  formularioSolicitud: FormularioSolicitud;
  estado: Estado;
  duracion: number;
  abiertoPlazoPresentacionSolicitud: boolean;
  ambitoGeografico: ITipoAmbitoGeograficoResponse;
  clasificacionCVN: ClasificacionCVN;
  activo: boolean;
  excelencia: boolean;
  codigoInterno: string;
  anio: number;
}

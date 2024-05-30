import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { IColumnDefinition } from '../ejecucion-economica-formulario/desglose-economico.fragment';

export interface IEjecucionPresupuestariaReportOptions extends IReportOptions {
  data: IDatoEconomico[];
  columns: IColumnDefinition[];
  showColumClasificadoAutomaticamente?: boolean;
  showColumnProyectoSgi?: boolean;
}

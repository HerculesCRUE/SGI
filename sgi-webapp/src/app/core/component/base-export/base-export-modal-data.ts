import { SgiRestFindOptions } from "@sgi/framework/http";

export interface IBaseExportModalData {
  findOptions?: SgiRestFindOptions;
  totalRegistrosExportacionExcel?: number;
  limiteRegistrosExportacionExcel?: number;
}

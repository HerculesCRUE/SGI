
export enum OutputReport {
  PDF = 'PDF',
  CSV = 'CSV',
  XLS = 'XLS',
  XLSX = 'XLSX',
  RTF = 'RTF'
}

export const OUTPUT_REPORT_TYPE_EXTENSION_MAP: Map<OutputReport, string> = new Map([
  [OutputReport.PDF, 'pdf'],
  [OutputReport.CSV, 'csv'],
  [OutputReport.XLS, 'xls'],
  [OutputReport.XLSX, 'xlsx'],
  [OutputReport.RTF, 'rtf']
]);

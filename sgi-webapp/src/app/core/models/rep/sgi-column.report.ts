import { FieldOrientationType } from "./sgi-dynamic-report";

export interface ISgiColumnReport {
  title: string;
  name: string;
  type: TypeColumnReportEnum;
  format?: string;
  fieldOrientationType?: FieldOrientationType;
  columns?: ISgiColumnReport[];
}

export enum TypeColumnReportEnum {
  STRING = 'STRING',
  DATE = 'DATE',
  NUMBER = 'NUMBER',
  BOOLEAN = 'BOOLEAN',
  FORMULA = 'FORMULA',
  SUBREPORT = 'SUBREPORT'
}




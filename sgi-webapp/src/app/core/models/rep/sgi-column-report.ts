import { FieldOrientation } from './field-orientation.enum';

export interface ISgiColumnReport {
  title: string;
  name: string;
  type: ColumnType;
  format?: string;
  fieldOrientation?: FieldOrientation;
  columns?: ISgiColumnReport[];
}

export enum ColumnType {
  STRING = 'STRING',
  DATE = 'DATE',
  NUMBER = 'NUMBER',
  BOOLEAN = 'BOOLEAN',
  FORMULA = 'FORMULA',
  SUBREPORT = 'SUBREPORT'
}




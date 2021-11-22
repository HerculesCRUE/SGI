import { FieldOrientationType } from './field-orientation.enum';
import { TypeColumnReportEnum } from './type-column-report-enum';

export interface ISgiColumnReport {
  title: string;
  name: string;
  type: TypeColumnReportEnum;
  format?: string;
  fieldOrientationType?: FieldOrientationType;
  columns?: ISgiColumnReport[];
}




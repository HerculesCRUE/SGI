import { KeyValue } from "@angular/common";
import { Observable } from "rxjs";

export enum ConfigType {
  TEXT = 'text',
  EMAILS = 'emails',
  EMAILS_UO = 'emails-uo',
  FILE = 'file',
  SELECT = 'select',
  CONFIG_GROUP_TITLE = 'config-group-title'
}

export interface IConfigOptions {
  type: ConfigType,
  label: string,
  options?: Observable<KeyValue<string, string>[]>,
  disabled?: boolean,
  info?: string
}

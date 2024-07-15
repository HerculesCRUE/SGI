import { KeyValue } from "@angular/common";
import { Observable } from "rxjs";

export enum ConfigType {
  TEXT = 'text',
  EMAILS = 'emails',
  EMAILS_UO = 'emails-uo',
  FILE = 'file',
  SELECT = 'select',
  SELECT_MULTIPLE = 'select-multiple',
  CONFIG_GROUP_TITLE = 'config-group-title'
}

export enum ConfigModule {
  CNF,
  CSP,
  NONE
}

export interface IConfigOptions {
  type: ConfigType,
  label: string,
  module: ConfigModule,
  options?: Observable<KeyValue<string, string>[]>,
  disabled?: boolean,
  info?: string
  description?: string
}

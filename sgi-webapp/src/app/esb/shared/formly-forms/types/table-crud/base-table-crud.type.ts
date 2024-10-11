import { Directive, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { FieldArrayType, FormlyFieldConfig } from '@ngx-formly/core';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

interface FieldsToRender {
  name: string;
  key: string | number | string[];
  type: string;
  options?: any[] | Observable<any[]>;
  format: string;
  order: number;
}

const BTN_ADD_ENTITY = marker('btn.add.entity');

export enum TYPE_RENDER_COLUMN {
  ONE_ELEMENT = 'oneElement',
  CLASIFICATIONS = 'clasifications',
  DEFAULT = 'default'
}

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class BaseTableCRUDTypeComponent extends FieldArrayType implements OnInit {

  @ViewChild('formTable', { static: true }) table: MatTable<any>;

  HEADER_ACCIONES = 'Acciones';

  TYPE_RENDER_COLUMN = TYPE_RENDER_COLUMN;

  dataSource: MatTableDataSource<FormlyFieldConfig> = new MatTableDataSource();

  fieldsToRender: FieldsToRender[] = [];

  customModel: any[] = [];

  btnAddTitle: string;

  constructor(
    public tableCRUDMatDialog: MatDialog,
    protected readonly translate: TranslateService
  ) {
    super();
  }

  ngOnInit(): void {
    this.field.templateOptions.remove = this.remove.bind(this);
    this.dataSource.data = this.field.fieldGroup;
    this.fieldsToRender = this.buildColumnInfo(this.field.fieldArray);
    this.setupI18N();
  }

  protected setupI18N(): void {
    if (this.to.entity && !this.to.btnAddTitle) {
      this.translate.get(
        this.to.entity,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            BTN_ADD_ENTITY,
            { entity: value }
          );
        })
      ).subscribe((value) => this.btnAddTitle = value);
    } else {
      this.btnAddTitle = this.to.btnAddTitle;
    }
  }

  protected buildColumnInfo(fieldConfig: FormlyFieldConfig): FieldsToRender[] {
    const toRender: FieldsToRender[] = fieldConfig.fieldGroup.map(fieldGroup => {
      return {
        name: fieldGroup.templateOptions.label,
        key: fieldGroup.key,
        type: fieldGroup.type,
        format: fieldGroup.templateOptions.luxonFormat,
        order: +fieldGroup.templateOptions.order,
        options: fieldGroup.templateOptions.options
      };
    });

    if (!this.to.disabled) {
      toRender.push({
        name: this.HEADER_ACCIONES,
        key: 'acciones',
        type: '',
        format: '',
        order: 9999
      });
    }

    return toRender.sort((a, b) => (a.order > b.order ? 1 : -1));
  }

  getOptionSelected(value: string | boolean, options: any[]): string {
    let optionSelected = '';
    if (!options.length) {
      options = [{
        label: value,
        value: value
      }]
    }

    if (value || (typeof value === 'boolean' && value === false)) {
      optionSelected = options.filter(option => option.value === value)[0].label;
    }
    return optionSelected;
  }

  getDisplayColumns(): string[] {
    return this.fieldsToRender.filter(field => field.key).map(field => field.key.toString());
  }

  remove(row: any) {
    super.remove(row);
    this.table.renderRows();
  }

  add(index?: number, model?: any) {
    super.add(index, model);
    this.table.renderRows();
  }

  addItem(): void {
    this.openDialog();
  }

  abstract editItem(rowIndex: number): void;

  abstract removeItem(rowIndex: number): void;

  /**
   * Indica el modo de renderizado de las columnas en función del tipo de componente que lo herede
   */
  abstract getMode(): TYPE_RENDER_COLUMN;

  protected abstract openDialog(editItemModel?: any, editIndex?: number): void;

  abstract isEditEnabled(rowIndex: number): boolean;

  abstract isDeleteEnabled(rowIndex: number): boolean;

}

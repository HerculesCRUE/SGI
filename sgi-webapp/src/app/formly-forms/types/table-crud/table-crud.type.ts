import { Component, Inject, OnInit, Optional, ViewChild } from '@angular/core';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { FieldArrayType, FormlyFieldConfig } from '@ngx-formly/core';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { TableCRUDModalComponent, TableCRUDModalData } from './table-crud-modal/table-crud-modal.component';
interface FieldsToRender {
  name: string;
  key: string | number | string[];
  type: string;
  options?: any[] | Observable<any[]>;
  format: string;
  order: number;
}

const BTN_ADD_ENTITY = marker('btn.add.entity');

@Component({
  templateUrl: './table-crud.type.html',
  styleUrls: ['./table-crud.type.scss']
})
export class TableCRUDTypeComponent extends FieldArrayType implements OnInit {
  HEADER_ACCIONES = 'Acciones';

  @ViewChild('formTable', { static: true }) table: MatTable<any>;

  dataSource: MatTableDataSource<FormlyFieldConfig> = new MatTableDataSource();

  fieldsToRender: FieldsToRender[] = [];

  btnAddTitle: string;

  constructor(
    private tableCRUDMatDialog: MatDialog,
    @Optional() @Inject(MAT_DIALOG_DATA) public data: TableCRUDModalData,
    private readonly translate: TranslateService,
  ) {
    super();
  }

  ngOnInit(): void {
    this.field.templateOptions.remove = this.remove.bind(this);
    this.dataSource.data = this.field.fieldGroup;
    this.fieldsToRender = this.buildColumnInfo(this.field.fieldArray);
    this.setupI18N();
  }

  private setupI18N(): void {
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

  buildColumnInfo(fieldConfig: FormlyFieldConfig): FieldsToRender[] {
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
        order: 99
      });
    }

    return toRender.sort((a, b) => (a.order > b.order ? 1 : -1));
  }

  getOptionSelected(value: string, options: any[]): string {
    let optionSelected = '';
    if (value) {
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

  openDialog(editItemModel?: any, editIndex?: number) {

    const data: TableCRUDModalData = {
      fieldGroup: [...this.field.fieldArray.fieldGroup],
      formModel: editItemModel,
      validateEmptyRow: this.to.validateEmptyRow || true,
      entity: {
        name: this.to.entity,
        gender: this.to.gender,
      }
    };
    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.tableCRUDMatDialog.open(TableCRUDModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (formModel) => {
        if (editItemModel) {
          this.formControl.at(editIndex).patchValue(editItemModel);
          this.table.renderRows();
        } else if (formModel && formModel !== '') {
          this.add(null, formModel);
        }
      }
    );
  }

  addItem(): void {
    this.openDialog();
  }

  editItem(rowIndex: number): void {
    const editFormModel = { ...this.model[rowIndex] };
    this.openDialog(editFormModel, rowIndex);
  }

  removeItem(rowIndex: number): void {
    this.remove(rowIndex);
  }
}

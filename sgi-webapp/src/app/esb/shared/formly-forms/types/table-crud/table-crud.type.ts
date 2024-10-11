import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { BaseTableCRUDTypeComponent, TYPE_RENDER_COLUMN } from './base-table-crud.type';
import { TableCRUDModalComponent, TableCRUDModalData } from './table-crud-modal/table-crud-modal.component';

@Component({
  templateUrl: './base-table-crud.type.html',
  styleUrls: ['./table-crud.type.scss']
})
export class TableCRUDTypeComponent extends BaseTableCRUDTypeComponent implements OnInit {

  constructor(
    public tableCRUDMatDialog: MatDialog,
    protected readonly translate: TranslateService
  ) {
    super(tableCRUDMatDialog, translate);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  getMode(): TYPE_RENDER_COLUMN {
    return TYPE_RENDER_COLUMN.DEFAULT;
  }

  editItem(rowIndex: number): void {
    const editFormModel = { ...this.model[rowIndex] };
    this.openDialog(editFormModel, rowIndex);
  }

  removeItem(rowIndex: number): void {
    this.remove(rowIndex);
  }

  isEditEnabled(rowIndex: number): boolean {
    if (typeof this.to.editEnabled !== 'boolean' && !this.to.editEnabled) {
      return true;
    }

    return eval(this.to.editEnabled);
  }

  isDeleteEnabled(rowIndex: number): boolean {
    if (typeof this.to.deleteEnabled !== 'boolean' && !this.to.deleteEnabled) {
      return true;
    }

    return eval(this.to.deleteEnabled);
  }

  openDialog(editItemModel?: any, editIndex?: number) {
    const data: TableCRUDModalData = {
      fieldGroup: [...this.field.fieldArray.fieldGroup],
      validateEmptyRow: this.to.validateEmptyRow || true,
      formModel: editItemModel,
      formState: this.options?.formState,
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
}

import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { BaseTableCRUDTypeComponent, TYPE_RENDER_COLUMN } from './base-table-crud.type';
import { TableCRUDOneElementModalComponent, TableCRUDOneElementModalData } from './table-crud-one-element-modal/table-crud-one-element-modal.component';

/**
 * @deprecated use `TableCRUDTypeComponent` instead
 * TODO Fix formGroup.valid == false when use this component
 */
@Component({
  templateUrl: './base-table-crud.type.html',
  styleUrls: ['./table-crud-one-element.type.scss']
})
export class TableCRUDOneElementTypeComponent extends BaseTableCRUDTypeComponent implements OnInit {

  constructor(
    public tableCRUDMatDialog: MatDialog,
    protected readonly translate: TranslateService,
  ) {
    super(tableCRUDMatDialog, translate);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  getMode(): TYPE_RENDER_COLUMN {
    return TYPE_RENDER_COLUMN.ONE_ELEMENT;
  }

  editItem(rowIndex: number): void {
    const editFormModel = this.model[rowIndex];
    this.openDialog(editFormModel, rowIndex);
  }

  removeItem(rowIndex: number): void {
    this.remove(rowIndex);
  }

  openDialog(editItemModel: any, editIndex: number) {
    const fieldGroup = this.field?.fieldArray?.fieldGroup[0];

    const modalData: TableCRUDOneElementModalData = {
      itemValue: editItemModel,
      label: fieldGroup.templateOptions.label,
      placeholder: fieldGroup.templateOptions.placeholder,
      requiredMessage: 'El campo es obligatorio',
      entity: {
        name: this.to.entity,
        gender: this.to.gender
      }
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data: modalData,
    };

    const dialogRef = this.tableCRUDMatDialog.open(TableCRUDOneElementModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (formModel) => {
        if (editItemModel) {
          this.formControl.at(editIndex).patchValue(formModel);
          this.model[editIndex] = formModel;
          this.table.renderRows();
        } else if (formModel && formModel !== '') {
          this.add(null, formModel);
        }
      }
    );
  }
}

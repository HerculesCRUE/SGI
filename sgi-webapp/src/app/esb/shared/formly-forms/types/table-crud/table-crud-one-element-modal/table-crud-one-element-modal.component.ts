import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface TableCRUDOneElementModalData {
  itemValue: any;
  label: string;
  placeholder: string;
  requiredMessage: string;
  entity: {
    name: string;
    gender: string;
  };
}

/**
 * @deprecated use `TableCRUDModalComponent` instead
 * TODO Fix formGroup.valid == false when use this component
 */
@Component({
  templateUrl: './table-crud-one-element-modal.component.html',
  styleUrls: ['./table-crud-one-element-modal.component.scss']
})
export class TableCRUDOneElementModalComponent implements OnInit {
  title: string;
  private entity: string;
  private gender: string;

  msgRequired = 'El campo es obligatorio';

  formGroup: FormGroup = new FormGroup({});

  constructor(
    public readonly matDialogRef: MatDialogRef<TableCRUDOneElementModalComponent>,
    private readonly translate: TranslateService,
    @Inject(MAT_DIALOG_DATA) public tableCRUDOneElementModalData: TableCRUDOneElementModalData,
  ) { }

  ngOnInit(): void {
    this.initFormlyData();
    this.setupI18N();
  }

  private initFormlyData() {
    const editValue = this.tableCRUDOneElementModalData.itemValue ? this.tableCRUDOneElementModalData.itemValue as string : '';
    const formGroup = new FormGroup({
      item: new FormControl(editValue)
    });

    this.formGroup = formGroup;

    this.entity = this.tableCRUDOneElementModalData?.entity?.name;
    this.gender = this.tableCRUDOneElementModalData?.entity?.gender;
  }

  /**
   * Checks the formGroup, returns the entered data and closes the modal
   */
  saveOrUpdate(): void {
    this.formGroup.markAllAsTouched();

    if (this.formGroup.valid) {
      this.matDialogRef.close(this.formGroup.controls.item.value);
    }
  }

  private setupI18N(): void {
    if (this.entity) {
      const gender = this.gender === MSG_PARAMS.GENDER.MALE.gender ? MSG_PARAMS.GENDER.MALE : MSG_PARAMS.GENDER.FEMALE;

      if (this.tableCRUDOneElementModalData.itemValue) {
        this.translate.get(
          this.entity,
          MSG_PARAMS.CARDINALIRY.SINGULAR
        ).subscribe((value) => this.title = value);
      } else {
        this.translate.get(
          this.entity,
          MSG_PARAMS.CARDINALIRY.SINGULAR
        ).pipe(
          switchMap((value) => {
            return this.translate.get(
              TITLE_NEW_ENTITY,
              { entity: value, ...gender }
            );
          })
        ).subscribe((value) => this.title = value);
      }
    }
  }
}

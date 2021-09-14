import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { MatSelectChange } from '@angular/material/select';
import { FieldType } from '@ngx-formly/material/form-field';

@Component({
  template: `
    <mat-select
      [id]="id"
      [formControl]="formControl"
      [formlyAttributes]="field"
      [placeholder]="to.placeholder"
      [tabIndex]="to.tabindex"
      [required]="to.required"
      [compareWith]="to.compareWith"
      [multiple]="to.multiple"
      (selectionChange)="change($event)"
      [errorStateMatcher]="errorStateMatcher"
      [aria-labelledby]="_getAriaLabelledby()"
      [disableOptionCentering]="to.disableOptionCentering"
    >
      <ng-container *ngIf="to.options | formlySelectOptions: field | async as selectOptions">
        <ng-container *ngFor="let item of selectOptions">
          <mat-option [value]="item.value" [disabled]="item.disabled">{{ item.label }}</mat-option>
        </ng-container>
      </ng-container>
    </mat-select>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectAttributesTypeComponent extends FieldType implements OnInit {

  defaultOptions = {
    templateOptions: {
      options: [],
      compareWith(o1: any, o2: any) {
        return o1 === o2;
      },
    },
  };

  ngOnInit() {
    if (this.to.propertyMappedToFormState) {
      this.formState[this.to.propertyMappedToFormState] = '';

      // Si hemos añadido los datos iniciales al formState tenemos que añadir tambień la conversión de este campo
      if (this.to.modelEditName && this.formState[this.to.modelEditName]) {
        const editModel = this.formState[this.to.modelEditName];
        this.patchSelectAttributeId(editModel[this.field.key as string], editModel);
      }
    }
  }

  change($event: MatSelectChange) {
    this.patchSelectAttributeId(this.field.formControl.value, this.formState);

    this.to.change?.(this.field, $event);
  }

  private patchSelectAttributeId(fieldId: string, formStateObj: any) {
    if (this.to.dataSelect && this.to.propertyMappedToFormState
      && this.to.dataSelectPropertyValue && this.to.dataSelectPropertyToFormState) {
      const dataSelectFilter = this.to.dataSelect.filter(item => item[this.to.dataSelectPropertyValue] === fieldId);
      if (dataSelectFilter && dataSelectFilter.length > 0 && dataSelectFilter[0]) {
        formStateObj[this.to.propertyMappedToFormState] = dataSelectFilter[0][this.to.dataSelectPropertyToFormState];
      }
    }
  }

  _getAriaLabelledby() {
    if (this.to.attributes?.['aria-labelledby']) {
      return this.to.attributes['aria-labelledby'];
    }

    return this.formField?._labelId;
  }

}

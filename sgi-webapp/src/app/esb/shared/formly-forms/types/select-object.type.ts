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
export class SelectObjectTypeComponent extends FieldType implements OnInit {

  defaultOptions = {
    templateOptions: {
      options: [],
      compareWith(o1: any, o2: any) {
        return o1 === o2;
      },
    },
  };

  // tslint:disable-next-line: variable-name
  private _compareWith = (o1: any, o2: any) => {
    if (o1 && o2) {
      return o1[this.to.modelKeyCompareProp] === o2[this.to.modelKeyCompareProp];
    }
    return o1 === o2;
  }

  ngOnInit() {
    this.to.options = [
      {
        value: this.model[this.to.modelKeyValueProp],
        label: this.model[this.to.modelKeyValueProp][this.to.modelKeyLabelProp]
      }
    ];

    this.to.compareWith = this._compareWith;
  }

  change($event: MatSelectChange) {
    this.to.change?.(this.field, $event);
  }

  _getAriaLabelledby() {
    if (this.to.attributes?.['aria-labelledby']) {
      return this.to.attributes['aria-labelledby'];
    }

    return this.formField?._labelId;
  }

}

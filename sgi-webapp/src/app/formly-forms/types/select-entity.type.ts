import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatSelectChange } from '@angular/material/select';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FieldType } from '@ngx-formly/material/form-field';
import { NGXLogger } from 'ngx-logger';

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
    <ng-container *ngIf="optionsFiltered | formlySelectOptions: field | async as selectOptions">
        <ng-container *ngFor="let item of selectOptions">
          <mat-option [value]="item.value" [disabled]="item.disabled">{{ item.label }}</mat-option>
        </ng-container>
        </ng-container>
    </mat-select>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectEntityTypeComponent extends FieldType implements OnInit {
  defaultOptions = {
    templateOptions: {
      options: [],
      compareWith(o1: any, o2: any) {
        return o1 === o2;
      },
    },
  };

  optionsFiltered: any[];

  change($event: MatSelectChange) {
    this.to.change?.(this.field, $event);
  }

  _getAriaLabelledby() {
    if (this.to.attributes?.['aria-labelledby']) {
      return this.to.attributes['aria-labelledby'];
    }

    return this.formField?._labelId;
  }

  get selectOptions(): any[] {
    return this.field?.templateOptions?.options as any[];
  }

  get propertyBound(): string {
    return this.to.propertyBound;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService
  ) {
    super();
  }

  ngOnInit() {
    if (!!this.propertyBound) {
      const valuePropertyBound = this.getPropertyBoundControl()?.value;
      this.optionsFiltered = this.filterByPropertyBound(valuePropertyBound);
    } else {
      this.optionsFiltered = this.selectOptions;
    }

    if (!!this.propertyBound) {
      this.onChangesPropertyBound();
    }
  }

  private onChangesPropertyBound(): void {
    this.getPropertyBoundControl().valueChanges.subscribe(value => {
      this.field.formControl.setValue(null);
      if (value) {
        this.optionsFiltered = this.filterByPropertyBound(value);
      } else {
        this.optionsFiltered = [];
      }
    });
  }

  private getPropertyBoundControl(): FormControl {
    return this.form.get(this.to.propertyBound) as FormControl;
  }

  private filterByPropertyBound(valuePropertyBound: string): any[] {
    return this.selectOptions.filter(option => option[this.to.propertyBoundProp] === valuePropertyBound);
  }

}

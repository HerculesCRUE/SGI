import { HttpParams } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { MatSelectChange } from '@angular/material/select';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormlyDataSGEService } from '@core/services/sge/formly-data-sge/formly-data-sge.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FieldType } from '@ngx-formly/material/form-field';
import { NGXLogger } from 'ngx-logger';
import { combineLatest, Observable, of, Subject } from 'rxjs';
import { catchError, distinctUntilChanged, map, startWith, switchMap, takeUntil } from 'rxjs/operators';

const MSG_ERROR_INIT = marker('error.load');

interface FormlyServiceConfig {
  readonly dependsOn?: string[];
  readonly labelProp?: string;
  readonly labelPropFn?: string;
  readonly params?: { [key: string]: any };
  readonly service: string;
  readonly valueProp?: string;
}

interface DependentFieldParam {
  field: string;
  value: any;
}

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
      <ng-container *ngIf="options$ | formlySelectOptions: field | async as selectOptions">
        <ng-container *ngFor="let item of selectOptions">
          <mat-option [value]="item.value" [disabled]="item.disabled">
            {{ item.label }}
          </mat-option>
        </ng-container>
      </ng-container>
    </mat-select>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectFormlyDataSgeTypeComponent extends FieldType implements OnInit, OnDestroy {

  defaultOptions = {
    templateOptions: {
      options: [],
      compareWith: (o1: unknown, o2: unknown): boolean => o1 === o2,
    },
  };

  options$: Observable<any[]> = of([]);

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly formlyDataSgeService: FormlyDataSGEService
  ) {
    super();
  }

  ngOnInit(): void {
    this.loadOptions();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  change($event: MatSelectChange): void {
    this.to.change?.(this.field, $event);
  }

  _getAriaLabelledby(): string | number {
    return this.to.attributes?.['aria-labelledby'] ?? this.formField?._labelId;
  }

  private loadOptions(): void {
    const serviceConfig = this.to.serviceConfig as FormlyServiceConfig;

    if (!serviceConfig || !serviceConfig.service) {
      return;
    }

    this.options$ = this.observeDependentFieldsChanges$(serviceConfig).pipe(
      distinctUntilChanged((previous, current) => {
        if (previous.length !== current.length) {
          return false;
        }

        return previous.every(prevParam =>
          current.some(currentParam =>
            currentParam.field === prevParam.field &&
            currentParam.value === prevParam.value
          )
        );
      }),
      switchMap(dependentFieldsParams => {
        if (serviceConfig.dependsOn?.length && dependentFieldsParams.some(dependentFieldParam => !dependentFieldParam.value)) {
          this.formControl.setValue(null);
          return of([]);
        }

        return this.getOptions(serviceConfig, this.buildRequestParams(serviceConfig, dependentFieldsParams));
      }),
      takeUntil(this.destroy$)
    );
  }

  private observeDependentFieldsChanges$(serviceConfig: FormlyServiceConfig): Observable<DependentFieldParam[]> {
    if (!serviceConfig.dependsOn?.length) {
      return of([]);
    }

    return combineLatest(
      serviceConfig.dependsOn.map(field => {
        const fieldFormControl = this.form.get(field);
        if (!fieldFormControl) {
          return of({
            field,
            value: null
          });
        }

        return fieldFormControl.valueChanges.pipe(
          startWith(fieldFormControl.value),
          map(value => {
            return {
              field,
              value
            };
          })
        );
      })
    );
  }

  private buildRequestParams(serviceConfig: FormlyServiceConfig, dependentValues: DependentFieldParam[]): HttpParams {
    let httpParams = new HttpParams();

    if (!!serviceConfig.params) {
      Object.entries(serviceConfig.params).forEach(([key, value]) => {
        if (!!value) {
          httpParams = httpParams.set(key, value);
        }
      });
    }

    if (!!serviceConfig.dependsOn?.length && !!dependentValues?.length) {
      dependentValues.forEach((field) => {
        httpParams = httpParams.set(field.field, field.value);
      });
    }

    return httpParams;
  }

  private getOptions(serviceConfig: FormlyServiceConfig, params: HttpParams): Observable<any[]> {
    return this.formlyDataSgeService.getData(serviceConfig.service, params).pipe(
      map(response => this.convertToSelectOptions(response, serviceConfig)),
      catchError(error => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_INIT);
        return of([]);
      }),
      takeUntil(this.destroy$)
    );
  }

  private convertToSelectOptions(response: any[], serviceConfig: FormlyServiceConfig): any[] {
    const valueProperty = serviceConfig.valueProp;
    const labelProperty = serviceConfig.labelProp;
    const labelPropertyFn = serviceConfig.labelPropFn;

    return response.map(item => ({
      value: valueProperty ? this.getNestedProperty(item, valueProperty) : item,
      label: !!labelPropertyFn ? this.getLabelPropertyFn(item, labelPropertyFn) : this.getNestedProperty(item, labelProperty),
      disabled: false
    }));
  }

  private getNestedProperty(object: any, property: string): any {
    return property.split('.').reduce((currentObject, currentProperty) => currentObject?.[currentProperty], object);
  }

  private getLabelPropertyFn(object: any, labelPropFn: string): string {
    const displayValueFn = new Function(`return (${labelPropFn});`)();
    return displayValueFn(object);
  }

}

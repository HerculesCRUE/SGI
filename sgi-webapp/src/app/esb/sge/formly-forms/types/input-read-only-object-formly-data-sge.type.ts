import { HttpParams } from '@angular/common/http';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
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
    <input
      matInput
      [formControl]="formControl"
      [formlyAttributes]="field"
      [placeholder]="to.placeholder"
      [value]="displayValue"
      readonly
    />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InputReadOnlyObjectFormlyDataSgeTypeComponent extends FieldType implements OnInit, OnDestroy {

  displayValue = '';

  private readonly destroy$ = new Subject<void>();

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private readonly logger: NGXLogger,
    private readonly snackBarService: SnackBarService,
    private readonly formlyDataSgeService: FormlyDataSGEService
  ) {
    super();
  }

  ngOnInit(): void {
    this.loadValue();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadValue(): void {
    const serviceConfig = this.to.serviceConfig as FormlyServiceConfig;
    if (!serviceConfig?.service) {
      return;
    }

    this.observeDependentFieldsChanges$(serviceConfig).pipe(
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
      switchMap(params => {
        if (params.some(p => !p.value)) {
          this.formControl.setValue(null);
          this.displayValue = '';
          return of(null);
        }

        const httpParams = this.buildRequestParams(serviceConfig, params);
        return this.formlyDataSgeService.getData(serviceConfig.service, httpParams).pipe(
          catchError(error => {
            this.logger.error(error);
            this.snackBarService.showError(MSG_ERROR_INIT);
            return of(null);
          }),
          takeUntil(this.destroy$)
        );
      }),
      takeUntil(this.destroy$)
    ).subscribe(value => {
      this.formControl.setValue(serviceConfig.valueProp ? this.getNestedProperty(value, serviceConfig.valueProp) : value);
      this.displayValue = this.getDisplayValue(value, serviceConfig);
      this.changeDetectorRef.markForCheck();
    });
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

  private getDisplayValue(object: any, serviceConfig: FormlyServiceConfig): string {
    if (!object) {
      return '';
    }

    if (!!serviceConfig.labelPropFn) {
      const displayValueFn = new Function(`return (${serviceConfig.labelPropFn});`)();
      return displayValueFn(object);
    }

    return this.getNestedProperty(object, serviceConfig.labelProp) ?? '';
  }

  private getNestedProperty(object: any, property: string): any {
    return property.split('.').reduce((currentObject, currentProperty) => currentObject?.[currentProperty], object);
  }

}

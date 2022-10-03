import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormlyUtils } from '@core/utils/formly-utils';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { TranslateService } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { ACTION_MODAL_MODE, BaseFormlyModalComponent, IFormlyData } from 'src/app/esb/shared/formly-forms/core/base-formly-modal.component';

const EMPRESA_KEY = marker('sgemp.empresa');

export interface IEmpresaFormlyData {
  empresaId: string;
  action: ACTION_MODAL_MODE;
}

@Component({
  templateUrl: './empresa-formly-modal.component.html',
  styleUrls: ['./empresa-formly-modal.component.scss']
})
export class EmpresaFormlyModalComponent extends BaseFormlyModalComponent<IEmpresaFormlyData, IEmpresa> implements OnInit {

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<EmpresaFormlyModalComponent>,
    @Inject(MAT_DIALOG_DATA) public empresaData: IEmpresaFormlyData,
    protected readonly translate: TranslateService,
    private readonly empresaService: EmpresaService
  ) {
    super(matDialogRef, empresaData?.action === ACTION_MODAL_MODE.EDIT, translate);
  }

  protected initializer = (): Observable<void> => this.loadFormlyData(this.empresaData?.action, this.empresaData?.empresaId);

  ngOnInit(): void {
    super.ngOnInit();
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({});
  }

  protected getValue(): IEmpresaFormlyData {
    return this.empresaData;
  }

  protected getKey(): string {
    return EMPRESA_KEY;
  }

  protected getGender() {
    return MSG_PARAMS.GENDER.FEMALE;
  }

  protected loadFormlyData(action: ACTION_MODAL_MODE, id: string): Observable<void> {
    let load$: Observable<IFormlyData> = of({ fields: [], data: {}, model: {} });
    switch (action) {
      case ACTION_MODAL_MODE.EDIT:
        load$ = this.empresaService.getFormlyUpdate().pipe(
          map(fields => {
            return this.initFormlyData(fields);
          }),
          switchMap((formlyData): Observable<IFormlyData> => {
            return this.getFormlyModelById(id, formlyData);
          })
        );
        break;
      case ACTION_MODAL_MODE.NEW:
        load$ = this.empresaService.getFormlyCreate().pipe(
          map(fields => {
            return this.initFormlyData(fields);
          })
        );
        break;
      case ACTION_MODAL_MODE.VIEW:
        load$ = this.empresaService.getFormlyView().pipe(
          map(fields => {
            return this.initFormlyData(fields);
          }),
          switchMap((formlyData) => {
            return this.getFormlyModelById(id, formlyData);
          })
        );
        break;
    }

    return load$.pipe(
      tap(formlyData => {
        this.options.formState.mainModel = formlyData.model;
        this.formlyData = formlyData;
      }),
      switchMap(() => of(void 0))
    );
  }

  private initFormlyData(fields: FormlyFieldConfig[]): IFormlyData {
    return { fields, data: {}, model: {} } as IFormlyData;
  }

  private getFormlyModelById(id: string, formlyData: IFormlyData): Observable<IFormlyData> {
    return this.empresaService.getFormlyModelById(id).pipe(
      map((model) => {
        formlyData.model = model;
        formlyData.model.empresaId = id;
        FormlyUtils.convertJSONToFormly(this.formlyData.model, this.formlyData.fields);
        return formlyData;
      }));
  }

  /**
   * Checks the formGroup, returns the entered data and closes the modal
   */
  protected saveOrUpdate(): Observable<IEmpresa> {
    FormlyUtils.convertFormlyToJSON(this.formlyData.model, this.formlyData.fields);

    return this.empresaData.action === ACTION_MODAL_MODE.NEW
      ? this.createEmpresa(this.formlyData)
      : this.updateEmpresa(this.empresaData.empresaId, this.formlyData);
  }

  private createEmpresa(formlyData: IFormlyData): Observable<IEmpresa> {
    return this.empresaService.createEmpresa(formlyData.model).pipe(
      switchMap(response => this.empresaService.findById(response))
    );
  }

  private updateEmpresa(empresaId: string, formlyData: IFormlyData): Observable<IEmpresa> {
    return this.empresaService.updateEmpresa(empresaId, formlyData.model).pipe(
      switchMap(() => this.empresaService.findById(empresaId))
    );
  }

}

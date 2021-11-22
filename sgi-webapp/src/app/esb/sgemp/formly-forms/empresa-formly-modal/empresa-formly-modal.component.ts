import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormlyUtils } from '@core/utils/formly-utils';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, mergeMap, switchMap } from 'rxjs/operators';
import { ACTION_MODAL_MODE, BaseFormlyModalComponent, IFormlyData } from 'src/app/esb/shared/formly-forms/core/base-formly-modal.component';

const EMPRESA_KEY = marker('sgemp.empresa');
const MSG_ERROR = marker('error.load');

export interface IEmpresaFormlyData {
  empresaId: string;
  action: ACTION_MODAL_MODE;
}

@Component({
  templateUrl: './empresa-formly-modal.component.html',
  styleUrls: ['./empresa-formly-modal.component.scss']
})
export class EmpresaFormlyModalComponent extends BaseFormlyModalComponent implements OnInit {

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<EmpresaFormlyModalComponent>,
    @Inject(MAT_DIALOG_DATA) public empresaData: IEmpresaFormlyData,
    protected readonly translate: TranslateService,
    private readonly empresaService: EmpresaService
  ) {
    super(translate);
    this.subscriptions.push(
      this.loadFormlyData(empresaData?.action, empresaData?.empresaId).subscribe(
        (formlyData) => {
          this.options.formState.mainModel = { ...formlyData.model };
          this.formlyData = formlyData;
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR);
          this.matDialogRef.close();
        }
      )
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  protected getKey(): string {
    return EMPRESA_KEY;
  }

  protected getGender() {
    return MSG_PARAMS.GENDER.FEMALE;
  }

  protected loadFormlyData(action: ACTION_MODAL_MODE, id: string): Observable<IFormlyData> {
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
    return load$;
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
  saveOrUpdate(): void {
    this.formGroup.markAllAsTouched();

    if (this.formGroup.valid) {

      if (this.empresaData.action === ACTION_MODAL_MODE.NEW) {
        FormlyUtils.convertFormlyToJSON(this.formlyData.model, this.formlyData.fields);
        this.subscriptions.push(this.empresaService.createEmpresa(this.formlyData.model).pipe(
          mergeMap(response =>
            //TODO se busca a la empresa según respuesta actual del servicio. Es posible que esta cambie
            this.empresaService.findById(response).pipe(
              map(proyecto => {
                return proyecto;
              })
            )
          )
        ).subscribe(
          (empresaCreada) => {
            this.matDialogRef.close(empresaCreada);
            this.snackBarService.showSuccess(this.textoCrearSuccess);
          },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(this.textoCrearError);
          }
        ));
      } else if (this.empresaData.action === ACTION_MODAL_MODE.EDIT) {
        // TODO verificar que llega en este punto empresaId 
        // y cambiar método para que se le pase empresaId como parámetro
        FormlyUtils.convertFormlyToJSON(this.formlyData.model, this.formlyData.fields);
        this.subscriptions.push(this.empresaService.updateEmpresa(this.empresaData.empresaId, this.formlyData.model).subscribe(
          () => {
            this.matDialogRef.close();
            this.snackBarService.showSuccess(this.textoUpdateSuccess);
          },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(this.textoUpdateError);
          }
        ));
      }
    }
  }
}

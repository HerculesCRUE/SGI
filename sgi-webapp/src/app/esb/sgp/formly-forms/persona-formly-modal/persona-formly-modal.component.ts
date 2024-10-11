import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IPersona } from '@core/models/sgp/persona';
import { ConfigService } from '@core/services/cnf/config.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormlyUtils } from '@core/utils/formly-utils';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { TranslateService } from '@ngx-translate/core';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { ACTION_MODAL_MODE, BaseFormlyModalComponent, IFormlyData } from 'src/app/esb/shared/formly-forms/core/base-formly-modal.component';

const PERSONA_KEY = marker('sgp.persona');
export interface IPersonaFormlyData {
  personaId: string;
  action: ACTION_MODAL_MODE;
}

export interface IPersonaFormlyResponse {
  createdOrUpdated: boolean;
  persona?: IPersona;
}

@Component({
  templateUrl: './persona-formly-modal.component.html',
  styleUrls: ['./persona-formly-modal.component.scss']
})
export class PersonaFormlyModalComponent extends BaseFormlyModalComponent<IPersonaFormlyData, IPersonaFormlyResponse> implements OnInit {
  private sgpModificacion: boolean = true;

  get sgpModificacionDisabled(): boolean {
    return !this.sgpModificacion;
  }

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<PersonaFormlyModalComponent>,
    @Inject(MAT_DIALOG_DATA) public personaData: IPersonaFormlyData,
    protected readonly translate: TranslateService,
    private readonly personaService: PersonaService,
    private readonly empresaService: EmpresaService,
    private readonly areaConocimientoService: AreaConocimientoService,
    private configService: ConfigService
  ) {
    super(matDialogRef, personaData?.action === ACTION_MODAL_MODE.EDIT, translate);
    this.subscriptions.push(this.configService.isModificacionSgpEnabled().subscribe(value => {
      this.sgpModificacion = value;
    }));
  }

  protected initializer = (): Observable<void> => this.loadFormlyData(this.personaData?.action, this.personaData?.personaId);

  protected buildFormGroup(): FormGroup {
    return new FormGroup({});
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  protected getValue(): IPersonaFormlyData {
    return this.personaData;
  }

  protected getKey(): string {
    return PERSONA_KEY;
  }

  protected getGender() {
    return MSG_PARAMS.GENDER.FEMALE;
  }

  protected loadFormlyData(action: ACTION_MODAL_MODE, id: string): Observable<void> {
    let formly$: Observable<FormlyFieldConfig[]>;
    switch (action) {
      case ACTION_MODAL_MODE.EDIT:
        formly$ = this.personaService.getFormlyUpdate();
        break;
      case ACTION_MODAL_MODE.NEW:
        formly$ = this.personaService.getFormlyCreate();
        break;
      case ACTION_MODAL_MODE.VIEW:
        formly$ = this.personaService.getFormlyView();
        break;
      default:
        formly$ = of([]);
    }

    let load$ = formly$.pipe(
      map(fields => {
        this.setDisableFields(fields, action);
        return { fields, data: {}, model: {} } as IFormlyData;
      })
    );

    switch (action) {
      case ACTION_MODAL_MODE.EDIT:
      case ACTION_MODAL_MODE.VIEW:
        load$ = load$.pipe(
          switchMap((formlyData) => {
            return this.fillPersonaFormlyModelById(id, formlyData);
          }),
          switchMap((formlyData) => {
            if (formlyData.model.empresaId || formlyData.model.entidadRef) {
              return this.fillEmpresaFormlyModelById(formlyData);
            } else {
              return of(formlyData);
            }
          }),
          switchMap((formlyData) => {
            if (formlyData.model.areaConocimientoId || formlyData.model.areaConocimientoRef) {
              return this.fillAreaConocimiento(formlyData);
            } else {
              return of(formlyData);
            }
          })
        );
        break;
      case ACTION_MODAL_MODE.NEW:
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

  private fillAreaConocimiento(formlyData: IFormlyData): Observable<IFormlyData> {
    return this.areaConocimientoService.findById(formlyData.model.areaConocimientoId ?? formlyData.model.areaConocimientoRef).pipe(
      map(areaConocimiento => {
        formlyData.model.areaConocimiento = [];
        formlyData.model.areaConocimiento.push({
          niveles: areaConocimiento.padreId,
          nivelSeleccionado: areaConocimiento.nombre
        });

        return formlyData;

      }),
      switchMap((result) => {
        if (result.model.areaConocimiento[0].niveles) {
          return this.areaConocimientoService.findById(result.model.areaConocimiento[0].niveles).pipe(
            map(areaConocimiento => {
              formlyData.model.areaConocimiento[0].niveles = areaConocimiento.nombre;
              return formlyData;

            })
          );
        } else {
          return of(formlyData);
        }
      })
    );
  }

  private fillPersonaFormlyModelById(id: string, formlyData: IFormlyData): Observable<IFormlyData> {
    return this.personaService.getFormlyModelById(id).pipe(
      map((model) => {
        FormlyUtils.convertJSONToFormly(model, formlyData.fields);
        formlyData.model = model;
        return formlyData;
      })
    );
  }

  private fillEmpresaFormlyModelById(formlyData: IFormlyData): Observable<IFormlyData> {
    return this.empresaService.findById(formlyData.model.empresaId ?? formlyData.model.entidadRef).pipe(
      map((empresa) => {
        if (formlyData.model.empresaId) {
          formlyData.model.empresaId = empresa;
        } else {
          formlyData.model.entidadRef = empresa;
        }

        return formlyData;
      })
    );
  }


  private create(formlyData: IFormlyData): Observable<IPersonaFormlyResponse> {
    return this.personaService.createPersona(formlyData.model).pipe(
      switchMap(response => {
        if (!response) {
          return of({ createdOrUpdated: true } as IPersonaFormlyResponse);
        }
        return this.personaService.findById(response).pipe(
          map(persona => {
            return {
              createdOrUpdated: true,
              persona
            }
          })
        );
      })
    );
  }

  private update(personaId: string, formlyData: IFormlyData): Observable<IPersonaFormlyResponse> {
    return this.personaService.updatePersona(personaId, formlyData.model).pipe(
      map(() => {
        return {
          createdOrUpdated: true,
          persona: null
        }
      })
    );
  }

  /**
   * Checks the formGroup, returns the entered data and closes the modal
   */
  protected saveOrUpdate(): Observable<IPersonaFormlyResponse> {
    this.parseFormlyToJSON();

    const obs$: Observable<IPersonaFormlyResponse> = this.personaData.action === ACTION_MODAL_MODE.EDIT
      ? this.update(this.personaData.personaId, this.formlyData)
      : this.create(this.formlyData);

    return obs$.pipe(
      catchError(error => {
        FormlyUtils.convertJSONToFormly(this.formlyData.model, this.formlyData.fields);
        if (this.personaData.action === ACTION_MODAL_MODE.EDIT) {
          this.formlyData.model.areaConocimiento = { ... this.formlyData.model.areaConocimiento };
        }
        return throwError(error);
      })
    );
  }

  private parseFormlyToJSON() {
    FormlyUtils.convertFormlyToJSON(this.formlyData.model, this.formlyData.fields);
    delete this.formlyData.model.areaConocimiento;
    if (this.formlyData.model.empresaId) {
      this.formlyData.model.empresaId = this.formlyData.model.empresaId.id;
    }
    if (this.formlyData.model.entidadRef) {
      this.formlyData.model.entidadRef = this.formlyData.model.entidadRef.id;
    }
  }

  setDisableFields(fields: FormlyFieldConfig[], action: ACTION_MODAL_MODE): void {
    if (this.sgpModificacionDisabled && action == ACTION_MODAL_MODE.EDIT) {
      fields.forEach(field => {
        if (field.fieldGroup) {
          this.setDisableFields(field.fieldGroup, action);
        } else {
          if (field.templateOptions) {
            field.templateOptions.disabled = true;
          }
        }
      });
    }
  }
}

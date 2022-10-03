import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IPersona } from '@core/models/sgp/persona';
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

@Component({
  templateUrl: './persona-formly-modal.component.html',
  styleUrls: ['./persona-formly-modal.component.scss']
})
export class PersonaFormlyModalComponent extends BaseFormlyModalComponent<IPersonaFormlyData, IPersona> implements OnInit {

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<PersonaFormlyModalComponent>,
    @Inject(MAT_DIALOG_DATA) public personaData: IPersonaFormlyData,
    protected readonly translate: TranslateService,
    private readonly personaService: PersonaService,
    private readonly empresaService: EmpresaService,
    private readonly areaConocimientoService: AreaConocimientoService

  ) {
    super(matDialogRef, personaData?.action === ACTION_MODAL_MODE.EDIT, translate);
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
            if (formlyData.model.empresaId) {
              return this.fillEmpresaFormlyModelById(formlyData);
            } else {
              return of(formlyData);
            }
          }),
          switchMap((formlyData) => {
            if (formlyData.model.areaConocimientoId) {
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
    return this.areaConocimientoService.findById(formlyData.model.areaConocimientoId).pipe(
      map(areaConocimiento => {
        formlyData.model.areaConocimiento = [];
        formlyData.model.areaConocimiento.push({
          niveles: areaConocimiento.nombre,
          nivelSeleccionado: areaConocimiento.padreId
        });

        return formlyData;

      }),
      switchMap((result) => {
        if (result.model.areaConocimiento[0].nivelSeleccionado) {
          return this.areaConocimientoService.findById(result.model.areaConocimiento[0].nivelSeleccionado).pipe(
            map(areaConocimiento => {
              formlyData.model.areaConocimiento[0].nivelSeleccionado = areaConocimiento.nombre;
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
    return this.empresaService.findById(formlyData.model.empresaId).pipe(
      map((empresa) => {
        formlyData.model.empresaId = empresa;
        return formlyData;
      })
    );
  }


  private create(formlyData: IFormlyData): Observable<IPersona> {
    return this.personaService.createPersona(formlyData.model).pipe(
      switchMap(response => this.personaService.findById(response))
    );
  }

  private update(personaId: string, formlyData: IFormlyData): Observable<IPersona> {
    return this.personaService.updatePersona(personaId, formlyData.model).pipe(
      switchMap(() => this.personaService.findById(personaId))
    );
  }

  /**
   * Checks the formGroup, returns the entered data and closes the modal
   */
  protected saveOrUpdate(): Observable<IPersona> {
    this.parseFormlyToJSON();

    const obs$: Observable<IPersona> = this.personaData.action === ACTION_MODAL_MODE.EDIT
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
  }
}

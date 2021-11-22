import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormlyUtils } from '@core/utils/formly-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, mergeMap, switchMap } from 'rxjs/operators';
import { ACTION_MODAL_MODE, BaseFormlyModalComponent, IFormlyData } from 'src/app/esb/shared/formly-forms/core/base-formly-modal.component';

const PERSONA_KEY = marker('sgp.persona');
const MSG_ERROR = marker('error.load');

export interface IPersonaFormlyData {
  personaId: string;
  action: ACTION_MODAL_MODE;
}

@Component({
  templateUrl: './persona-formly-modal.component.html',
  styleUrls: ['./persona-formly-modal.component.scss']
})
export class PersonaFormlyModalComponent extends BaseFormlyModalComponent implements OnInit {

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<PersonaFormlyModalComponent>,
    @Inject(MAT_DIALOG_DATA) public personaData: IPersonaFormlyData,
    protected readonly translate: TranslateService,
    private readonly personaService: PersonaService,
    private readonly empresaService: EmpresaService,
    private readonly areaConocimientoService: AreaConocimientoService

  ) {
    super(translate);
    this.subscriptions.push(
      this.loadFormlyData(personaData?.action, personaData?.personaId).subscribe(
        (formlyData) => {
          this.options.formState.mainModel = formlyData.model;
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
    return PERSONA_KEY;
  }

  protected getGender() {
    return MSG_PARAMS.GENDER.FEMALE;
  }

  protected loadFormlyData(action: ACTION_MODAL_MODE, id: string): Observable<IFormlyData> {
    let load$: Observable<IFormlyData> = of({ fields: [], data: {}, model: {} });
    switch (action) {
      case ACTION_MODAL_MODE.EDIT:
        load$ = this.personaService.getFormlyUpdate().pipe(
          map(fields => {
            return { fields, data: {}, model: {} } as IFormlyData;
          }),
          switchMap((formlyData) => {
            return this.findPersonaFormlyModelById(id, formlyData);
          }),
          switchMap((formlyData) => {
            if (formlyData.model.empresaId) {
              return this.findEmpresaFormlyModelById(formlyData);
            } else {
              return of(formlyData);
            }
          }),
          switchMap((formlyData) => {
            if (formlyData.model.areaConocimientoId) {
              return this.findAreaConocimiento(formlyData);
            } else {
              return of(formlyData);
            }
          })
        );
        break;
      case ACTION_MODAL_MODE.NEW:
        load$ = this.personaService.getFormlyCreate().pipe(
          map(fields => {
            return { fields, data: {}, model: {} } as IFormlyData;
          })
        );
        break;
      case ACTION_MODAL_MODE.VIEW:
        load$ = this.personaService.getFormlyView().pipe(
          map(fields => {
            return { fields, data: {}, model: {} } as IFormlyData;
          }),
          switchMap((formlyData) => {
            return this.findPersonaFormlyModelById(id, formlyData);
          }),
          switchMap((formlyData) => {
            if (formlyData.model.empresaId) {
              return this.findEmpresaFormlyModelById(formlyData);
            } else {
              return of(formlyData);
            }
          }),
          switchMap((formlyData) => {
            if (formlyData.model.areaConocimientoId) {
              return this.findAreaConocimiento(formlyData);
            } else {
              return of(formlyData);
            }
          })
        );
        break;
    }
    return load$;
  }

  private findAreaConocimiento(formlyData: IFormlyData): Observable<IFormlyData> {
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

  private findPersonaFormlyModelById(id: string, formlyData: IFormlyData): Observable<IFormlyData> {
    return this.personaService.getFormlyModelById(id).pipe(
      map((model) => {
        FormlyUtils.convertJSONToFormly(model, formlyData.fields);
        formlyData.model = model;
        return formlyData;
      })
    );
  }

  private findEmpresaFormlyModelById(formlyData: IFormlyData): Observable<IFormlyData> {
    return this.empresaService.findById(formlyData.model.empresaId).pipe(
      map((empresa) => {
        formlyData.model.empresaId = empresa;
        return formlyData;
      })
    );
  }

  /**
   * Checks the formGroup, returns the entered data and closes the modal
   */
  saveOrUpdate(): void {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {

      if (this.personaData.action === ACTION_MODAL_MODE.NEW) {
        this.parseFormlyToJSON();
        this.subscriptions.push(this.personaService.createPersona(this.formlyData.model).pipe(
          mergeMap(response =>
            //TODO se busca a la persona según respuesta actual del servicio. Es posible que esta cambie
            this.personaService.findById(response).pipe(
              map(proyecto => {
                return proyecto;
              })
            )
          )
        ).subscribe(
          (respuestaPersona) => {
            this.matDialogRef.close(respuestaPersona);
            this.snackBarService.showSuccess(this.textoCrearSuccess);
          },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(this.textoCrearError);
          }
        ));
      } else if (this.personaData.action === ACTION_MODAL_MODE.EDIT) {
        const areaConocimiento = { ... this.formlyData.model.areaConocimiento };
        this.parseFormlyToJSON();
        this.subscriptions.push(this.personaService.updatePersona(this.personaData.personaId, this.formlyData.model).subscribe(
          () => {
            this.matDialogRef.close();
            this.snackBarService.showSuccess(this.textoUpdateSuccess);
          },
          (error) => {
            this.formlyData.model.areaConocimiento = areaConocimiento;
            this.logger.error(error);
            this.snackBarService.showError(this.textoUpdateError);
          }
        ));
      }
    }
  }

  private parseFormlyToJSON() {
    FormlyUtils.convertFormlyToJSON(this.formlyData.model, this.formlyData.fields);
    delete this.formlyData.model.areaConocimiento;
    if (this.formlyData.model.empresaId) {
      this.formlyData.model.empresaId = this.formlyData.model.empresaId.id;
    }
  }
}

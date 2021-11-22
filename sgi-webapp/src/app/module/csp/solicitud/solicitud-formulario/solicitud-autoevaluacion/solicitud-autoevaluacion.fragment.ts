import { FormGroup } from '@angular/forms';
import { ISolicitud } from '@core/models/csp/solicitud';
import { IChecklist } from '@core/models/eti/checklist';
import { IFormly } from '@core/models/eti/formly';
import { IPersona } from '@core/models/sgp/persona';
import { Fragment, Group } from '@core/services/action-service';
import { ChecklistService } from '@core/services/eti/checklist/checklist.service';
import { FormlyService } from '@core/services/eti/formly/formly.service';
import { FormlyFieldConfig, FormlyFormOptions } from '@ngx-formly/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { delay, filter, map, switchMap, take, tap } from 'rxjs/operators';

export interface FormlyData {
  formGroup: FormGroup;
  formly: IFormly;
  model: {};
  options: FormlyFormOptions;
}

export interface SolicitudProyectoData {
  checklistRef: string;
  readonly: boolean;
}

export class SolicitudAutoevaluacionFragment extends Fragment {

  public readonly data: FormlyData = {
    formGroup: new FormGroup({}),
    formly: { esquema: [] } as IFormly,
    model: {},
    options: {}
  };
  private readonly group = new Group();
  private solicitud: ISolicitud;

  public readonly solicitudProyectoData$: Subject<SolicitudProyectoData> = new BehaviorSubject<SolicitudProyectoData>(undefined);

  constructor(
    solicitud: ISolicitud,
    private readonly formlyService: FormlyService,
    private readonly checklistService: ChecklistService,
    private readonly authService: SgiAuthService
  ) {
    super(null);
    this.solicitud = solicitud;
    this.subscriptions.push(this.initialized$.pipe(delay(100)).subscribe(
      (value) => {
        if (value) {
          this.group.load(this.data.formGroup);
          this.group.initialize();
          if (this.getKey()) {
            this.group.refreshInitialState(true);
          }

          this.subscriptions.push(this.group.status$.subscribe(
            (status) => {
              if (this.isEdit()) {
                this.setChanges(status.changes);
              }
              else {
                this.setChanges(status.complete);
                this.setComplete(status.complete);
              }
              this.setErrors(status.errors);
            }
          ));
        }
      }
    ));
  }

  protected onInitialize(): Observable<any> {
    return this.solicitudProyectoData$.pipe(
      filter(value => value !== undefined),
      take(1),
      switchMap(value => {
        this.data.options.formState = {
          solicitud: this.solicitud
        };
        return of(value);
      }),
      switchMap(value => {
        this.setKey(value.checklistRef);
        if (value.checklistRef) {
          this.refreshInitialState(true);
          return this.checklistService.findById(Number.parseInt(value.checklistRef, 10)).pipe(
            tap((checklist: IChecklist) => {
              this.data.formly = checklist.formly;
              this.data.model = checklist.respuesta;

              if (value.readonly) {
                this.switchToReadonly(this.data.formly.esquema);
              }
            })
          );
        }
        else {
          return this.formlyService.findByNombre('CHECKLIST').pipe(
            tap((formly: IFormly) => {
              this.data.formly = formly;
              if (value.readonly) {
                this.switchToReadonly(this.data.formly.esquema);
              }
            })
          );
        }
      })
    );

  }

  private switchToReadonly(fieldConfig: FormlyFieldConfig[]): void {
    fieldConfig.forEach((field) => {
      if (field.key) {
        if (field.templateOptions) {
          field.templateOptions.disabled = true;
        }
        else {
          field.templateOptions = {
            disabled: true
          };
        }
      }
      if (field.fieldGroup) {
        this.switchToReadonly(field.fieldGroup);
      }
    });
  }

  saveOrUpdate(): Observable<string> {
    if (this.getKey()) {
      return this.checklistService.updateRespuesta(Number.parseInt(this.getKey() as string, 10), this.data.model).pipe(
        map(checklist => checklist.id.toString())
      );
    }
    else {
      return this.checklistService.create({
        id: undefined,
        formly: this.data.formly,
        respuesta: this.data.model,
        fechaCreacion: undefined,
        persona: { id: this.authService.authStatus$.value.userRefId } as IPersona
      }).pipe(
        map(checklist => checklist.id.toString()),
        tap(id => {
          this.setKey(id);
          this.refreshInitialState(true);
        })
      );
    }
  }

  performChecks(markAllTouched?: boolean): void {
    if (this.group.hasChanges()) {
      this.group.forceUpdate(markAllTouched);
    }
  }
}

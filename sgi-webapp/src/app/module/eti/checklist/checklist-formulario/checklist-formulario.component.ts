import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IChecklist } from '@core/models/eti/checklist';
import { IFormly } from '@core/models/eti/formly';
import { IPersona } from '@core/models/sgp/persona';
import { Module } from '@core/module';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { ChecklistService } from '@core/services/eti/checklist/checklist.service';
import { FormlyService } from '@core/services/eti/formly/formly.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { Observable, Subscription } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { PETICION_EVALUACION_ROUTE, PETICION_EVALUACION_ROUTE_NAMES } from '../../peticion-evaluacion/peticion-evaluacion-route-names';

const MSG_CHECKLIST_INVALID_FORM = marker('eti.checklist.invalid');
const MSG_CHECKLIST_DIALOG = marker('eti.checklist.dialog');
const CHECKLIST_KEY = marker('eti.checklist');
const MSG_CREATE_SUCCESS = marker('msg.save.entity.success');
const MSG_UPDATE_SUCCESS = marker('msg.update.entity.success');
const URL_CREAR_SOLICITUD_EVALUACION = Module.INV.path + '/' + PETICION_EVALUACION_ROUTE + '/' + ROUTE_NAMES.NEW + '/' + PETICION_EVALUACION_ROUTE_NAMES.DATOS_GENERALES;

export interface FormlyData {
  formGroup: FormGroup;
  formly: IFormly;
  model: {};
}

export interface SolicitudProyectoData {
  checklistRef: string;
  readonly: boolean;
}

@Component({
  selector: 'sgi-checklist-formulario',
  templateUrl: './checklist-formulario.component.html',
  styleUrls: ['./checklist-formulario.component.scss']
})
export class ChecklistFormularioComponent implements OnInit, OnDestroy {

  public readonly data: FormlyData = {
    formGroup: new FormGroup({}),
    formly: { esquema: [] } as IFormly,
    model: {}
  };

  private subscriptions: Subscription[] = [];
  private idCheckList = null;
  private textoCreateSuccess: string;
  private textoUpdateSuccess: string;
  buttonDisable = false;

  constructor(
    private formlyService: FormlyService,
    private checklistService: ChecklistService,
    private authService: SgiAuthService,
    protected readonly snackBarService: SnackBarService,
    private readonly dialogService: DialogService,
    private readonly translate: TranslateService,
    private router: Router,
  ) {
  }

  ngOnInit(): void {
    this.subscriptions.push(this.checklistService.findByPersonaActual().subscribe((checkList: IChecklist) => {
      if (!checkList) {
        this.subscriptions.push(this.formlyService.findByNombre('CHECKLIST').subscribe((formlyByNombre: IFormly) => {
          this.data.formly = formlyByNombre;
        }));
      } else {
        this.idCheckList = checkList.id;
        this.data.formly = checkList.formly;
        this.data.model = checkList.respuesta;
      }
    }));
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      CHECKLIST_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_CREATE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoCreateSuccess = value);

    this.translate.get(
      CHECKLIST_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoUpdateSuccess = value);
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

  saveOrUpdate(): void {
    if (this.data.formGroup.valid) {
      this.buttonDisable = true;
      if (this.hasAnyTrue()) {
        this.subscriptions.push(this.dialogService.showConfirmation(
          MSG_CHECKLIST_DIALOG
        ).pipe(
          filter(aceptado => aceptado),
          switchMap(() => {
            return this.doSaveOrUpdate();
          })
        ).subscribe((checklist) => {
          this.idCheckList = checklist.id;
          this.snackBarService.showSuccess(this.textoUpdateSuccess);
          this.buttonDisable = false;
          this.router.navigate([URL_CREAR_SOLICITUD_EVALUACION]);
        }));
      } else {
        this.doSaveOrUpdate().subscribe((checklist) => {
          this.idCheckList = checklist.id;
          this.snackBarService.showSuccess(this.textoUpdateSuccess);
          this.buttonDisable = false;
        });
      }

    } else {
      this.snackBarService.showError(MSG_CHECKLIST_INVALID_FORM);
    }
  }

  private doSaveOrUpdate(): Observable<IChecklist> {
    if (this.idCheckList) {
      return this.checklistService.updateRespuesta(Number.parseInt(this.idCheckList, 10), this.data.model);
    }
    else {
      return this.checklistService.create({
        id: undefined,
        formly: this.data.formly,
        respuesta: this.data.model,
        fechaCreacion: undefined,
        persona: { id: this.authService.authStatus$.value.userRefId } as IPersona
      });
    }
  }

  cancel() {
    this.data.model = {};
  }

  private hasAnyTrue() {
    return this.data.formGroup.controls.pregunta_1.value || this.data.formGroup.controls.pregunta_2.value
      || this.data.formGroup.controls.pregunta_3.value || this.data.formGroup.controls.pregunta_4.value
      || this.data.formGroup.controls.pregunta_5.value || this.data.formGroup.controls.pregunta_6.value;
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(x => x.unsubscribe());
  }

}

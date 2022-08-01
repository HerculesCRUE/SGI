import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEmpresaEquipoEmprendedor } from '@core/models/eer/empresa-equipo-emprendedor';
import { TranslateService } from '@ngx-translate/core';
import { merge } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const MIEMBRO_EQUIPO_EMPRESA_EQUIPO_EMPRENDEDOR_MIEMBRO_KEY = marker('eer.empresa-equipo-emprendedor.miembro');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface EmpresaEquipoEmprendedorModalData {
  titleEntity: string;
  selectedEntidades: IEmpresaEquipoEmprendedor[];
  entidad: IEmpresaEquipoEmprendedor;
}

@Component({
  templateUrl: './empresa-equipo-emprendedor-modal.component.html',
  styleUrls: ['./empresa-equipo-emprendedor-modal.component.scss']
})
export class EmpresaEquipoEmprendedorModalComponent extends DialogFormComponent<EmpresaEquipoEmprendedorModalData> implements OnInit {
  TIPO_COLECTIVO = TipoColectivo;

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  textSaveOrUpdate: string;

  msgParamMiembroEntity = {};
  title: string;

  constructor(
    matDialogRef: MatDialogRef<EmpresaEquipoEmprendedorModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EmpresaEquipoEmprendedorModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.entidad);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.textSaveOrUpdate = this.data?.entidad?.id ? MSG_ACEPTAR : MSG_ANADIR;

    this.subscriptions.push(
      merge(
        this.formGroup.get('miembro').valueChanges,
      ).subscribe(() => this.checkContainsPersona())
    );
  }

  private setupI18N(): void {
    this.translate.get(
      MIEMBRO_EQUIPO_EMPRESA_EQUIPO_EMPRENDEDOR_MIEMBRO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMiembroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data?.entidad?.id) {
      this.translate.get(
        this.data.titleEntity,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        this.data.titleEntity,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        miembro: new FormControl(null,
          Validators.required
        )
      }
    );
    return formGroup;
  }

  protected getValue(): EmpresaEquipoEmprendedorModalData {
    this.data.entidad.miembroEquipo = this.formGroup.get('miembro').value;
    return this.data;
  }

  private checkContainsPersona(): void {
    const miembroForm = this.formGroup.get('miembro');

    const miembroEquipo = this.data.selectedEntidades.some(element => element.miembroEquipo.id === miembroForm.value?.id);

    if (miembroEquipo) {
      this.addError(miembroForm, 'contains');
    } else if (miembroForm.errors) {
      this.deleteError(miembroForm, 'contains');
    }
  }

  private deleteError(formControl: AbstractControl, errorName: string): void {
    if (formControl.errors) {
      delete formControl.errors[errorName];
      if (Object.keys(formControl.errors).length === 0) {
        formControl.setErrors(null);
      }
    }
  }

  private addError(formControl: AbstractControl, errorName: string): void {
    if (!formControl.errors) {
      formControl.setErrors({});
    }
    formControl.errors[errorName] = true;
    formControl.markAsTouched({ onlySelf: true });
  }

}

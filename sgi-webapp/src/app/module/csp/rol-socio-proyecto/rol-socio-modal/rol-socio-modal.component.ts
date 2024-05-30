import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { IRolSocio } from '@core/models/csp/rol-socio';
import { RolSocioService } from '@core/services/csp/rol-socio/rol-socio.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const ROL_SOCIO_KEY = marker('csp.rol-socio');
const ROL_SOCIO_NOMBRE_KEY = marker('csp.rol-socio.nombre');
const ROL_SOCIO_ABREVIATURA_KEY = marker('csp.rol-socio.abreviatura');
const ROL_SOCIO_COORDINADOR_KEY = marker('csp.rol-socio.coordinador');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-rol-socio-modal',
  templateUrl: './rol-socio-modal.component.html',
  styleUrls: ['./rol-socio-modal.component.scss']
})
export class RolSocioModalComponent
  extends DialogActionComponent<IRolSocio> implements OnInit, OnDestroy {

  private readonly rolSocio: IRolSocio;
  msgParamNombreEntity = {};
  msgParamAbreviaturaEntity = {};
  msgParamCoordinadorEntity = {};
  title: string;

  constructor(
    matDialogRef: MatDialogRef<RolSocioModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: IRolSocio,
    private readonly rolSocioService: RolSocioService,
    private readonly translate: TranslateService,
    private readonly formBuilder: FormBuilder
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.rolSocio = { ...data };
    } else {
      this.rolSocio = { activo: true } as IRolSocio;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.matDialogRef.updateSize('30vw');
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      ROL_SOCIO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      ROL_SOCIO_ABREVIATURA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe({
      next: (value: string) => {
        this.msgParamAbreviaturaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR };
      }
    });

    this.translate.get(
      ROL_SOCIO_COORDINADOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe({
      next: (value: string) => {
        this.msgParamCoordinadorEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
      }
    });

    if (this.isEdit()) {
      this.translate.get(
        ROL_SOCIO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        ROL_SOCIO_KEY,
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

  protected getValue(): IRolSocio {
    this.rolSocio.nombre = this.formGroup.controls.nombre.value;
    this.rolSocio.abreviatura = this.formGroup.controls.abreviatura.value;
    this.rolSocio.coordinador = this.formGroup.controls.coordinador.value;
    this.rolSocio.descripcion = this.formGroup.controls.descripcion.value;
    return this.rolSocio;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = this.formBuilder.group({
      nombre: [this.rolSocio?.nombre ?? '', Validators.required],
      abreviatura: [this.rolSocio?.abreviatura ?? '', Validators.required],
      coordinador: [this.rolSocio?.coordinador ?? null, Validators.required],
      descripcion: [this.rolSocio?.descripcion ?? '']
    });
    return formGroup;
  }

  protected saveOrUpdate(): Observable<IRolSocio> {
    const tipoRegimenConcurrencia = this.getValue();
    return this.isEdit() ? this.rolSocioService.update(tipoRegimenConcurrencia.id, tipoRegimenConcurrencia) :
      this.rolSocioService.create(tipoRegimenConcurrencia);
  }
}

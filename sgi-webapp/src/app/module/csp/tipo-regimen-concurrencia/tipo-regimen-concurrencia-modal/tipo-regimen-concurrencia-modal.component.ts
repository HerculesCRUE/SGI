import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoRegimenConcurrencia } from '@core/models/csp/tipos-configuracion';
import { TipoRegimenConcurrenciaService } from '@core/services/csp/tipo-regimen-concurrencia/tipo-regimen-concurrencia.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TIPO_REGIMEN_CONCURRENCIA_KEY = marker('csp.tipo-regimen-concurrencia');
const TIPO_REGIMEN_CONCURRENCIA_NOMBRE_KEY = marker('csp.tipo-regimen-concurrencia.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-tipo-regimen-concurrencia-modal',
  templateUrl: './tipo-regimen-concurrencia-modal.component.html',
  styleUrls: ['./tipo-regimen-concurrencia-modal.component.scss']
})
export class TipoRegimenConcurrenciaModalComponent
  extends DialogActionComponent<ITipoRegimenConcurrencia> implements OnInit, OnDestroy {

  private readonly tipoRegimenConcurrencia: ITipoRegimenConcurrencia;
  msgParamNombreEntity = {};
  title: string;

  constructor(
    matDialogRef: MatDialogRef<TipoRegimenConcurrenciaModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ITipoRegimenConcurrencia,
    private readonly tipoRegimenConcurrenciaService: TipoRegimenConcurrenciaService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.tipoRegimenConcurrencia = { ...data };
    } else {
      this.tipoRegimenConcurrencia = { activo: true } as ITipoRegimenConcurrencia;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.matDialogRef.updateSize('30vw');
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_REGIMEN_CONCURRENCIA_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.isEdit()) {
      this.translate.get(
        TIPO_REGIMEN_CONCURRENCIA_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        TIPO_REGIMEN_CONCURRENCIA_KEY,
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

  protected getValue(): ITipoRegimenConcurrencia {
    this.tipoRegimenConcurrencia.nombre = this.formGroup.controls.nombre.value;
    return this.tipoRegimenConcurrencia;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.tipoRegimenConcurrencia?.nombre ?? '', Validators.required),
    });

    return formGroup;
  }

  protected saveOrUpdate(): Observable<ITipoRegimenConcurrencia> {
    const tipoRegimenConcurrencia = this.getValue();
    return this.isEdit() ? this.tipoRegimenConcurrenciaService.update(tipoRegimenConcurrencia.id, tipoRegimenConcurrencia) :
      this.tipoRegimenConcurrenciaService.create(tipoRegimenConcurrencia);
  }
}

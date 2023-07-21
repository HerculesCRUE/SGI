import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipos-configuracion';
import { TipoAmbitoGeograficoService } from '@core/services/csp/tipo-ambito-geografico/tipo-ambito-geografico.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TIPO_AMBITO_GEOGRAFICO = marker('csp.tipo-ambito-geografico');
const TIPO_AMBITO_GREOGRAFICO_NOMBRE = marker('csp.tipo-ambito-geografico.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-tipo-ambito-geografico-modal',
  templateUrl: './tipo-ambito-geografico-modal.component.html',
  styleUrls: ['./tipo-ambito-geografico-modal.component.scss']
})
export class TipoAmbitoGeograficoModalComponent extends DialogActionComponent<ITipoAmbitoGeografico> implements OnInit, OnDestroy {

  private readonly tipoAmbitoGeografico: ITipoAmbitoGeografico;
  title: string;
  msgParamNombreEntity = {};

  constructor(
    matDialogRef: MatDialogRef<TipoAmbitoGeograficoModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ITipoAmbitoGeografico,
    private readonly tipoAmbitoGeograficoService: TipoAmbitoGeograficoService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.tipoAmbitoGeografico = { ...data };
    } else {
      this.tipoAmbitoGeografico = { activo: true } as ITipoAmbitoGeografico;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.matDialogRef.updateSize('30vw');
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_AMBITO_GREOGRAFICO_NOMBRE,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.isEdit()) {
      this.translate.get(
        TIPO_AMBITO_GEOGRAFICO,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        TIPO_AMBITO_GEOGRAFICO,
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

  protected getValue(): ITipoAmbitoGeografico {
    this.tipoAmbitoGeografico.nombre = this.formGroup.controls.nombre.value;
    return this.tipoAmbitoGeografico;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.tipoAmbitoGeografico?.nombre ?? '', Validators.required)
    });

    return formGroup;
  }

  protected saveOrUpdate(): Observable<ITipoAmbitoGeografico> {
    const tipoFase = this.getValue();
    return this.isEdit() ? this.tipoAmbitoGeograficoService.update(tipoFase.id, tipoFase) :
      this.tipoAmbitoGeograficoService.create(tipoFase);
  }
}

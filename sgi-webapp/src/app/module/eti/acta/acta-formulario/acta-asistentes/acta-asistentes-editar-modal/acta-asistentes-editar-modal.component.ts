import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAsistente } from '@core/models/eti/asistente';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const ACTA_ASISTENTE_KEY = marker('eti.acta.asistente');
const ACTA_ASISTENTE_MOTIVO_KEY = marker('eti.acta.asistente.motivo');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  templateUrl: './acta-asistentes-editar-modal.component.html',
  styleUrls: ['./acta-asistentes-editar-modal.component.scss']
})
export class ActaAsistentesEditarModalComponent extends DialogFormComponent<IAsistente> implements OnInit {

  ocultarMotivo = true;

  estados =
    [
      { label: 'Sí', value: true },
      { label: 'No', value: false }
    ];

  msgParamMotivoEntity = {};
  title: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<ActaAsistentesEditarModalComponent>,
    @Inject(MAT_DIALOG_DATA) public asistente: IAsistente,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!asistente.asistencia);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      ACTA_ASISTENTE_MOTIVO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMotivoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.asistente.asistencia) {
      this.translate.get(
        ACTA_ASISTENTE_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

    } else {
      this.translate.get(
        ACTA_ASISTENTE_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      asistente: new FormControl(
        this.asistente?.evaluador?.persona?.nombre + ' ' + this.asistente?.evaluador?.persona?.apellidos,
        [Validators.required]
      ),
      asistencia: new FormControl(!!this.asistente.asistencia, [Validators.required]),
      motivo: new FormControl(this.asistente.motivo, !!!this.asistente.asistencia ? Validators.required : undefined),
    });
    formGroup.controls.asistente.disable();

    this.ocultarMotivo = !!this.asistente.asistencia;

    this.subscriptions.push(formGroup.controls.asistencia.valueChanges.subscribe(
      (value) => {
        if (value) {
          this.ocultarMotivo = true;
          formGroup.controls.motivo.clearValidators();
        }
        else {
          this.ocultarMotivo = false;
          formGroup.controls.motivo.setValidators(Validators.required);
        }
        formGroup.controls.motivo.updateValueAndValidity({ onlySelf: true, emitEvent: false });
      }
    ));

    return formGroup;
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   */
  protected getValue(): IAsistente {
    const asistente = this.asistente;
    if (this.ocultarMotivo) {
      this.formGroup.controls.motivo.setValue('');
      asistente.motivo = '';
    } else {
      asistente.motivo = this.formGroup.controls.motivo.value;
    }
    asistente.asistencia = this.formGroup.controls.asistencia.value;
    return asistente;
  }

}

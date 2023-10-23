import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';

const MEMORIA_INDICAR_SUBSANACION_COMENTARIO = marker('eti.memoria.indicar-subsanacion.comentario');
const MSG_INDICAR_SUBSANACION_CONFIRMACION = marker('confirmacion.eti.memoria.indicar-subsanacion');

export interface IndicarSubsanacionModalComponentData {
  memoriaId: number;
}

interface IComentarioEstadoMemoria {
  comentario: string;
}

@Component({
  templateUrl: './indicar-subsanacion-modal.component.html',
  styleUrls: ['./indicar-subsanacion-modal.component.scss']
})
export class IndicarSubsanacionModalComponent extends DialogActionComponent<IComentarioEstadoMemoria> implements OnInit {

  msgParamComentarioEntity = {};

  constructor(
    matDialogRef: MatDialogRef<IndicarSubsanacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IndicarSubsanacionModalComponentData,
    protected snackBarService: SnackBarService,
    private memoriaService: MemoriaService,
    private confirmDialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, true);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.matDialogRef.updateSize('30vw');
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      MEMORIA_INDICAR_SUBSANACION_COMENTARIO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  protected getValue(): IComentarioEstadoMemoria {
    return {
      comentario: this.formGroup.controls.comentario.value
    };
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      comentario: new FormControl('', [Validators.maxLength(2000), Validators.required])
    });
  }

  protected saveOrUpdate(): Observable<IComentarioEstadoMemoria> {
    const comentarioEstadoMemoria = this.getValue();

    return this.confirmDialogService.showConfirmation(MSG_INDICAR_SUBSANACION_CONFIRMACION).pipe(
      filter(aceptado => !!aceptado),
      switchMap(() => this.memoriaService.indicarSubsanacion(this.data.memoriaId, comentarioEstadoMemoria.comentario)),
      map(() => comentarioEstadoMemoria)
    );
  }

}

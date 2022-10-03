import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ILineaInvestigacion } from '@core/models/csp/linea-investigacion';
import { LineaInvestigacionService } from '@core/services/csp/linea-investigacion/linea-investigacion.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const LINEA_INVESTIGACION_KEY = marker('csp.linea-investigacion');
const LINEA_INVESTIGACION_NOMBRE_KEY = marker('csp.linea-investigacion.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  templateUrl: './linea-investigacion-modal.component.html',
  styleUrls: ['./linea-investigacion-modal.component.scss']
})
export class LineaInvestigacionModalComponent extends DialogActionComponent<ILineaInvestigacion> implements OnInit, OnDestroy {

  private readonly lineaInvestigacion: ILineaInvestigacion;
  title: string;
  msgParamNombreEntity = {};

  constructor(
    matDialogRef: MatDialogRef<LineaInvestigacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ILineaInvestigacion,
    private readonly lineaInvestigacionService: LineaInvestigacionService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.lineaInvestigacion = { ...data };
    } else {
      this.lineaInvestigacion = { activo: true } as ILineaInvestigacion;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      LINEA_INVESTIGACION_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.isEdit()) {
      this.translate.get(
        LINEA_INVESTIGACION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        LINEA_INVESTIGACION_KEY,
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

  protected getValue(): ILineaInvestigacion {
    this.lineaInvestigacion.nombre = this.formGroup.controls.nombre.value;
    return this.lineaInvestigacion;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.lineaInvestigacion?.nombre ?? '', [Validators.maxLength(1000), Validators.required]),
    });

    return formGroup;
  }

  protected saveOrUpdate(): Observable<ILineaInvestigacion> {
    const lineaInvestigacion = this.getValue();
    return this.isEdit() ? this.lineaInvestigacionService.update(lineaInvestigacion.id, lineaInvestigacion) :
      this.lineaInvestigacionService.create(lineaInvestigacion);
  }
}

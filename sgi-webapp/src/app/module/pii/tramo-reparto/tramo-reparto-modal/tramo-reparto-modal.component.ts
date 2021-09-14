import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITramoReparto } from '@core/models/pii/tramo-reparto';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NumberValidator } from '@core/validators/number-validator';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const TRAMO_REPARTO_KEY = marker('pii.tramo-reparto');
const TRAMO_REPARTO_DESDE_KEY = marker('pii.tramo-reparto.desde');
const TRAMO_REPARTO_HASTA_KEY = marker('pii.tramo-reparto.hasta');
const TRAMO_REPARTO_PORCENTAJE_UNIVERSIDAD_KEY = marker('pii.tramo-reparto.porcentaje-universidad');
const TRAMO_REPARTO_PORCENTAJE_INVENTORES_KEY = marker('pii.tramo-reparto.porcentaje-inventores');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

@Component({
  selector: 'sgi-tramo-reparto-modal',
  templateUrl: './tramo-reparto-modal.component.html',
  styleUrls: ['./tramo-reparto-modal.component.scss']
})
export class TramoRepartoModalComponent extends BaseModalComponent<ITramoReparto, TramoRepartoModalComponent> implements OnInit, OnDestroy {
  fxLayoutProperties: FxLayoutProperties;
  tramoReparto: ITramoReparto;

  msgParamDesdeEntity = {};
  msgParamHastaEntity = {};
  msgParamPorcentajeUniversidadEntity = {};
  msgParamPorcentajeInventoresEntity = {};
  title: string;

  textSaveOrUpdate: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<TramoRepartoModalComponent>,
    @Inject(MAT_DIALOG_DATA) tramoReparto: ITramoReparto,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, tramoReparto);
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    if (tramoReparto) {
      this.tramoReparto = { ...tramoReparto };
    } else {
      this.tramoReparto = { activo: true } as ITramoReparto;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  protected getDatosForm(): ITramoReparto {
    this.tramoReparto.desde = this.formGroup.controls.desde.value;
    this.tramoReparto.hasta = this.formGroup.controls.hasta.value;
    this.tramoReparto.porcentajeUniversidad = this.formGroup.controls.porcentajeUniversidad.value;
    this.tramoReparto.porcentajeInventores = this.formGroup.controls.porcentajeInventores.value;

    return this.tramoReparto;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      desde: new FormControl(this.tramoReparto?.desde, [Validators.min(0), Validators.pattern(/^[0-9]*$/)]),
      hasta: new FormControl(this.tramoReparto?.hasta, Validators.pattern(/^[0-9]*$/)),
      porcentajeUniversidad: new FormControl(
        this.tramoReparto?.porcentajeUniversidad,
        [
          Validators.min(0),
          Validators.max(100),
          NumberValidator.maxDecimalPlaces(2)
        ]
      ),
      porcentajeInventores: new FormControl(
        this.tramoReparto?.porcentajeInventores,
        [
          Validators.min(0),
          Validators.max(100),
          NumberValidator.maxDecimalPlaces(2)
        ]
      ),
    },
      [
        NumberValidator.isAfter('desde', 'hasta'),
        NumberValidator.fieldsSumEqualsToValue(100, 'porcentajeUniversidad', 'porcentajeInventores')
      ]);
    return formGroup;
  }

  private setupI18N(): void {
    this.translate.get(
      TRAMO_REPARTO_DESDE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDesdeEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TRAMO_REPARTO_HASTA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamHastaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TRAMO_REPARTO_PORCENTAJE_UNIVERSIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPorcentajeUniversidadEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TRAMO_REPARTO_PORCENTAJE_INVENTORES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPorcentajeInventoresEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.tramoReparto.desde) {
      this.translate.get(
        TRAMO_REPARTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.translate.get(
        TRAMO_REPARTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ANADIR;
    }
  }
}

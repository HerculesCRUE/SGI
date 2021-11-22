import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITramoReparto, Tipo, TIPO_TRAMO_REPARTO_MAP } from '@core/models/pii/tramo-reparto';
import { TramoRepartoService } from '@core/services/pii/tramo-reparto/tramo-reparto.service';
import { NumberValidator } from '@core/validators/number-validator';
import { TramoRepartoValidator } from 'src/app/module/pii/tramo-reparto/validators/tramo-reparto-validator';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TRAMO_REPARTO_KEY = marker('pii.tramo-reparto');
const TRAMO_REPARTO_TIPO_KEY = marker('pii.tramo-reparto.tipo');
const TRAMO_REPARTO_DESDE_KEY = marker('pii.tramo-reparto.desde');
const TRAMO_REPARTO_HASTA_KEY = marker('pii.tramo-reparto.hasta');
const TRAMO_REPARTO_PORCENTAJE_UNIVERSIDAD_KEY = marker('pii.tramo-reparto.porcentaje-universidad');
const TRAMO_REPARTO_PORCENTAJE_INVENTORES_KEY = marker('pii.tramo-reparto.porcentaje-inventores');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ITramoRepartoModalData {
  currentTramoReparto: ITramoReparto;
  tramoRepartoMaxHasta: ITramoReparto;
  hasTramoRepartoInicial: boolean;
  hasTramoRepartoFinal: boolean;
  isTramoRepartoModificable?: boolean;
}

@Component({
  selector: 'sgi-tramo-reparto-modal',
  templateUrl: './tramo-reparto-modal.component.html',
  styleUrls: ['./tramo-reparto-modal.component.scss']
})
export class TramoRepartoModalComponent extends DialogActionComponent<ITramoRepartoModalData, ITramoReparto> implements OnInit, OnDestroy {

  private readonly tramoReparto: ITramoReparto;
  msgParamTipoEntity = {};
  msgParamDesdeEntity = {};
  msgParamHastaEntity = {};
  msgParamPorcentajeUniversidadEntity = {};
  msgParamPorcentajeInventoresEntity = {};
  title: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get TIPO_TRAMO_REPARTO_MAP() {
    return TIPO_TRAMO_REPARTO_MAP;
  }

  get TIPO_TRAMO_REPARTO() {
    return Tipo;
  }

  constructor(
    matDialogRef: MatDialogRef<TramoRepartoModalComponent>,
    @Inject(MAT_DIALOG_DATA) private readonly data: ITramoRepartoModalData,
    private readonly translate: TranslateService,
    private readonly tramoRepartoService: TramoRepartoService
  ) {
    super(matDialogRef, !!data.currentTramoReparto);
    if (this.isEdit()) {
      this.tramoReparto = { ...data.currentTramoReparto };
    } else {
      this.tramoReparto = { activo: true } as ITramoReparto;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected getValue(): ITramoReparto {
    this.tramoReparto.desde = this.formGroup.controls.desde.value;
    this.tramoReparto.hasta = this.formGroup.controls.hasta.value;
    this.tramoReparto.porcentajeUniversidad = this.formGroup.controls.porcentajeUniversidad.value;
    this.tramoReparto.porcentajeInventores = this.formGroup.controls.porcentajeInventores.value;
    this.tramoReparto.tipo = this.formGroup.controls.tipo.value;

    return this.tramoReparto;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tipo: new FormControl(this.tramoReparto?.tipo ?? null,
        [
          TramoRepartoValidator.noDuplicateTipoTramo(Tipo.INICIAL, this.data.hasTramoRepartoInicial),
          TramoRepartoValidator.noDuplicateTipoTramo(Tipo.FINAL, this.data.hasTramoRepartoFinal)
        ]),
      desde: new FormControl(this.tramoReparto?.desde ?? '', [Validators.min(0), Validators.pattern(/^[0-9]*$/)]),
      hasta: new FormControl(this.tramoReparto?.hasta ?? '', Validators.pattern(/^[0-9]*$/)),
      porcentajeUniversidad: new FormControl(
        this.tramoReparto?.porcentajeUniversidad ?? '',
        [
          Validators.min(0),
          Validators.max(100),
          NumberValidator.maxDecimalPlaces(2)
        ]
      ),
      porcentajeInventores: new FormControl(
        this.tramoReparto?.porcentajeInventores ?? '',
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

    if (typeof this.data.isTramoRepartoModificable === 'boolean' && this.data.isTramoRepartoModificable === false) {
      this.configureFormGroupToNoEditableKeyFieldsEntity(formGroup);
    } else {
      this.configureFormGroupControlsByTipo(formGroup, this.tramoReparto.tipo);
    }
    this.initFormGroupSubscribes(formGroup, this.tramoReparto);

    return formGroup;
  }

  protected saveOrUpdate(): Observable<ITramoReparto> {
    const tramoReparto = this.getValue();
    return this.isEdit() ? this.tramoRepartoService.update(tramoReparto.id, tramoReparto) :
      this.tramoRepartoService.create(tramoReparto);
  }

  private configureFormGroupToNoEditableKeyFieldsEntity(formGroup: FormGroup) {
    formGroup.controls.tipo.disable();
    formGroup.controls.desde.disable();
    formGroup.controls.hasta.disable();
  }

  private configureFormGroupControlsByTipo(formGroup: FormGroup, tipo: Tipo): void {
    switch (tipo) {
      case Tipo.INICIAL:
        formGroup.controls.tipo.setValidators(TramoRepartoValidator.noDuplicateTipoTramo(Tipo.FINAL, this.data.hasTramoRepartoFinal));
        formGroup.controls.desde.disable();
        break;
      case Tipo.INTERMEDIO:
        formGroup.controls.desde.disable();
        break;
      case Tipo.FINAL:
        formGroup.controls.tipo.setValidators(TramoRepartoValidator.noDuplicateTipoTramo(Tipo.INICIAL, this.data.hasTramoRepartoInicial));
        formGroup.controls.desde.disable();
        formGroup.controls.hasta.disable();
        break;
      default:
        break;
    }
  }

  private initFormGroupSubscribes(formGroup: FormGroup, tramoReparto: ITramoReparto): void {
    this.subscriptions.push(
      formGroup.controls.tipo.valueChanges.subscribe((tipo: Tipo) =>
        this.onTipoValueChanges(tipo, formGroup)
      )
    );
  }

  private onTipoValueChanges(tipo: Tipo, formGroup: FormGroup): void {
    switch (tipo) {
      case Tipo.INICIAL:
        formGroup.controls.desde.reset(1);
        formGroup.controls.desde.disable();
        formGroup.controls.hasta.reset();
        formGroup.controls.hasta.enable();
        break;
      case Tipo.INTERMEDIO:
        formGroup.controls.desde.reset(this.getTramoRepartoFinalDesde());
        formGroup.controls.desde.disable();
        formGroup.controls.hasta.reset();
        formGroup.controls.hasta.enable();
        break;
      case Tipo.FINAL:
        formGroup.controls.desde.reset(this.getTramoRepartoFinalDesde());
        formGroup.controls.desde.disable();
        formGroup.controls.hasta.reset();
        formGroup.controls.hasta.disable();
        break;
      default:
        break;
    }
  }

  private getTramoRepartoFinalDesde(): number {
    return this.getTramoRepartoMaxHasta() + 1;
  }

  private getTramoRepartoMaxHasta(): number {
    return this.data.tramoRepartoMaxHasta?.hasta ?? 0;
  }

  private setupI18N(): void {
    this.translate.get(
      TRAMO_REPARTO_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

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
    ).subscribe((value) =>
      this.msgParamPorcentajeUniversidadEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TRAMO_REPARTO_PORCENTAJE_INVENTORES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamPorcentajeInventoresEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.isEdit()) {
      this.translate.get(
        TRAMO_REPARTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
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
    }
  }
}

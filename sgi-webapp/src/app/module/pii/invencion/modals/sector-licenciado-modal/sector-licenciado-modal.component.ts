import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { ISectorLicenciado } from '@core/models/pii/sector-licenciado';
import { IPais } from '@core/models/sgo/pais';
import { PaisService } from '@core/services/sgo/pais/pais.service';
import { DateValidator } from '@core/validators/date-validator';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const SECTOR_LICENCIADO_PAIS_KEY = marker('pii.sector-licenciado.pais');
const SECTOR_LICENCIADO_EXCLUSIVIDAD_KEY = marker('pii.sector-licenciado.exclusividad');
const SECTOR_LICENCIADO_FECHA_INICIO_KEY = marker('pii.sector-licenciado.fecha-inicio');
const SECTOR_LICENCIADO_FECHA_FIN_KEY = marker('pii.sector-licenciado.fecha-fin');
const SECTOR_LICENCIADO_SECTOR_KEY = marker('pii.sector-licenciado.sector');
const SECTOR_LICENCIADO_KEY = marker('pii.sector-licenciado');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_SECTOR_LICENCIADO_DUPLICADO = marker('pii.sector-licenciado.duplicado');

export interface ISectorLicenciadoModalData {
  sectorLicenciado: ISectorLicenciado;
  readonly: boolean;
  existingEntities: ISectorLicenciado[];
}

@Component({
  selector: 'sgi-sector-licenciado-modal',
  templateUrl: './sector-licenciado-modal.component.html',
  styleUrls: ['./sector-licenciado-modal.component.scss']
})
export class SectorLicenciadoModalComponent extends DialogFormComponent<ISectorLicenciado> implements OnInit {

  msgParamPaisEntity = {};
  msgParamExclusividadEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamFechaFinEntity = {};
  msgParamSectorEntity = {};

  textSaveOrUpdate: string;
  title: string;
  errorSectorLicenciado: string;

  paises$: Observable<IPais[]>;

  constructor(
    matDialogRef: MatDialogRef<SectorLicenciadoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ISectorLicenciadoModalData,
    private readonly translate: TranslateService,
    paisService: PaisService,
  ) {
    super(matDialogRef, !!data.sectorLicenciado?.pais);
    this.paises$ = paisService.findAll().pipe(map(({ items }) => items));
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      pais: new FormControl(this.data.sectorLicenciado?.pais, Validators.required),
      exclusividad: new FormControl(this.data.sectorLicenciado?.exclusividad, Validators.required),
      fechaInicio: new FormControl(this.data.sectorLicenciado?.fechaInicioLicencia, Validators.required),
      fechaFin: new FormControl(this.data.sectorLicenciado?.fechaFinLicencia, Validators.required),
      sector: new FormControl(this.data.sectorLicenciado?.sectorAplicacion, Validators.required),
    },
      DateValidator.isAfter('fechaInicio', 'fechaFin'));

    if (this.data.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

  protected getValue(): ISectorLicenciado {
    this.data.sectorLicenciado.pais = this.formGroup.controls.pais.value;
    this.data.sectorLicenciado.exclusividad = this.formGroup.controls.exclusividad.value;
    this.data.sectorLicenciado.fechaInicioLicencia = this.formGroup.controls.fechaInicio.value;
    this.data.sectorLicenciado.fechaFinLicencia = this.formGroup.controls.fechaFin.value;
    this.data.sectorLicenciado.sectorAplicacion = this.formGroup.controls.sector.value;

    return this.data.sectorLicenciado;
  }

  private setupI18N(): void {
    this.translate.get(
      SECTOR_LICENCIADO_PAIS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamPaisEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SECTOR_LICENCIADO_EXCLUSIVIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamExclusividadEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SECTOR_LICENCIADO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SECTOR_LICENCIADO_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaFinEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SECTOR_LICENCIADO_SECTOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamSectorEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SECTOR_LICENCIADO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SECTOR_LICENCIADO_DUPLICADO,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.errorSectorLicenciado = value);

    if (this.data.sectorLicenciado?.pais) {
      this.translate.get(
        SECTOR_LICENCIADO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.translate.get(
        SECTOR_LICENCIADO_KEY,
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

  protected getFormStatus(): ISectorLicenciado {
    const sector = {} as ISectorLicenciado;
    sector.pais = this.formGroup.controls.pais.value;
    sector.exclusividad = this.formGroup.controls.exclusividad.value;
    sector.fechaInicioLicencia = this.formGroup.controls.fechaInicio.value;
    sector.fechaFinLicencia = this.formGroup.controls.fechaFin.value;
    sector.sectorAplicacion = this.formGroup.controls.sector.value;

    return sector;
  }

  doAction(): void {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      this.clearProblems();
      if (this.isDuplicateSectorLicenciado(this.getFormStatus())) {
        this.pushProblems(new SgiError(this.errorSectorLicenciado));
      } else {
        this.close(this.getValue());
      }
    }
  }

  private isDuplicateSectorLicenciado(elem: ISectorLicenciado): boolean {
    return this.data.existingEntities
      .some(existentSector =>
        existentSector.pais?.id === elem.pais?.id
        && existentSector.sectorAplicacion?.id === elem.sectorAplicacion?.id
        && existentSector.exclusividad === elem.exclusividad);
  }

}

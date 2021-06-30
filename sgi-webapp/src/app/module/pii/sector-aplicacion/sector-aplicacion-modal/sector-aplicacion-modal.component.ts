import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISectorAplicacion } from '@core/models/pii/sector-aplicacion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const SECTOR_APLICACION_KEY = marker('pii.sector-aplicacion');
const SECTOR_APLICACION_NOMBRE_KEY = marker('pii.sector-aplicacion.nombre');
const SECTOR_APLICACION_DESCRIPCION_KEY = marker('pii.sector-aplicacion.descripcion');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

@Component({
  selector: 'sgi-sector-aplicacion-modal',
  templateUrl: './sector-aplicacion-modal.component.html',
  styleUrls: ['./sector-aplicacion-modal.component.scss']
})
export class SectorAplicacionModalComponent extends BaseModalComponent<ISectorAplicacion, SectorAplicacionModalComponent> implements OnInit, OnDestroy {
  fxLayoutProperties: FxLayoutProperties;
  sectorAplicacion: ISectorAplicacion;

  msgParamNombreEntity = {};
  msgParamDescripcionEntity = {};
  title: string;

  textSaveOrUpdate: string;

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<SectorAplicacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) sectorAplicacion: ISectorAplicacion,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, sectorAplicacion);
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    if (sectorAplicacion) {
      this.sectorAplicacion = { ...sectorAplicacion };
    } else {
      this.sectorAplicacion = { activo: true } as ISectorAplicacion;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      SECTOR_APLICACION_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SECTOR_APLICACION_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.sectorAplicacion.nombre) {

      this.translate.get(
        SECTOR_APLICACION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.translate.get(
        SECTOR_APLICACION_KEY,
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

  protected getDatosForm(): ISectorAplicacion {
    this.sectorAplicacion.nombre = this.formGroup.controls.nombre.value;
    this.sectorAplicacion.descripcion = this.formGroup.controls.descripcion.value;
    return this.sectorAplicacion;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.sectorAplicacion?.nombre),
      descripcion: new FormControl(this.sectorAplicacion?.descripcion),
    });
    return formGroup;
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }
}

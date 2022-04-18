import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISectorAplicacion } from '@core/models/pii/sector-aplicacion';
import { SectorAplicacionService } from '@core/services/pii/sector-aplicacion/sector-aplicacion.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const SECTOR_APLICACION_KEY = marker('pii.sector-aplicacion');
const SECTOR_APLICACION_NOMBRE_KEY = marker('pii.sector-aplicacion.nombre');
const SECTOR_APLICACION_DESCRIPCION_KEY = marker('pii.sector-aplicacion.descripcion');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-sector-aplicacion-modal',
  templateUrl: './sector-aplicacion-modal.component.html',
  styleUrls: ['./sector-aplicacion-modal.component.scss']
})
export class SectorAplicacionModalComponent
  extends DialogActionComponent<ISectorAplicacion> implements OnInit, OnDestroy {

  private readonly sectorAplicacion: ISectorAplicacion;
  msgParamNombreEntity = {};
  msgParamDescripcionEntity = {};
  title: string;

  constructor(
    matDialogRef: MatDialogRef<SectorAplicacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ISectorAplicacion,
    private readonly sectorAplicacionService: SectorAplicacionService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.sectorAplicacion = { ...data };
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

    if (this.isEdit()) {
      this.translate.get(
        SECTOR_APLICACION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
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
    }

  }

  protected getValue(): ISectorAplicacion {
    this.sectorAplicacion.nombre = this.formGroup.controls.nombre.value;
    this.sectorAplicacion.descripcion = this.formGroup.controls.descripcion.value;
    return this.sectorAplicacion;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.sectorAplicacion?.nombre ?? '', [Validators.required, Validators.maxLength(50)]),
      descripcion: new FormControl(this.sectorAplicacion?.descripcion ?? '', [Validators.required, Validators.maxLength(250)]),
    });
    return formGroup;
  }

  protected saveOrUpdate(): Observable<ISectorAplicacion> {
    const sectorAplicacion = this.getValue();
    return this.isEdit() ? this.sectorAplicacionService.update(sectorAplicacion.id, sectorAplicacion) :
      this.sectorAplicacionService.create(sectorAplicacion);
  }

}

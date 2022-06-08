import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { IFuenteFinanciacion } from '@core/models/csp/fuente-financiacion';
import { ITipoOrigenFuenteFinanciacion } from '@core/models/csp/tipo-origen-fuente-financiacion';
import { FuenteFinanciacionService } from '@core/services/csp/fuente-financiacion/fuente-financiacion.service';
import { TipoOrigenFuenteFinanciacionService } from '@core/services/csp/tipo-origen-fuente-financiacion.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const FUENTE_FINANCIACION_KEY = marker('csp.fuente-financiacion');
const FUENTE_FINANCIACION_NOMBRE_KEY = marker('csp.fuente-financiacion.nombre');
const FUENTE_FINANCIACION_AMBITO_GEOGRAFICO_KEY = marker('csp.fuente-financiacion.ambito-geografico');
const FUENTE_FINANCIACION_ORIGEN_KEY = marker('csp.fuente-financiacion.origen');
const FUENTE_FINANCIACION_FONDO_ESTRUCTURAL_KEY = marker('csp.fuente-financiacion.fondo-estructural');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  templateUrl: './fuente-financiacion-modal.component.html',
  styleUrls: ['./fuente-financiacion-modal.component.scss']
})
export class FuenteFinanciacionModalComponent
  extends DialogActionComponent<IFuenteFinanciacion> implements OnInit, OnDestroy {

  private readonly fuenteFinanciacion: IFuenteFinanciacion;
  public readonly origenes: Observable<ITipoOrigenFuenteFinanciacion[]>;

  msgParamAmbitoEntity = {};
  msgParamNombreEntity = {};
  msgParamOrigenEntity = {};
  msgParamFondoEstructuralEntity = {};
  title: string;

  constructor(
    matDialogRef: MatDialogRef<FuenteFinanciacionModalComponent, IFuenteFinanciacion>,
    @Inject(MAT_DIALOG_DATA) data: IFuenteFinanciacion,
    tipoOrigenFuenteFinanciacionService: TipoOrigenFuenteFinanciacionService,
    private readonly fuenteFinanciacionService: FuenteFinanciacionService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);
    this.origenes = tipoOrigenFuenteFinanciacionService.findAll().pipe(map(result => result.items));

    if (this.isEdit()) {
      this.fuenteFinanciacion = { ...data };
    } else {
      this.fuenteFinanciacion = { activo: true } as IFuenteFinanciacion;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.matDialogRef.updateSize('30vw');
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      FUENTE_FINANCIACION_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      FUENTE_FINANCIACION_AMBITO_GEOGRAFICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAmbitoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      FUENTE_FINANCIACION_ORIGEN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamOrigenEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      FUENTE_FINANCIACION_FONDO_ESTRUCTURAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFondoEstructuralEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.isEdit()) {
      this.translate.get(
        FUENTE_FINANCIACION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        FUENTE_FINANCIACION_KEY,
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

  protected getValue(): IFuenteFinanciacion {
    const controls = this.formGroup.controls;
    this.fuenteFinanciacion.nombre = controls.nombre.value;
    this.fuenteFinanciacion.descripcion = controls.descripcion.value;
    this.fuenteFinanciacion.tipoAmbitoGeografico = controls.ambitoGeografico.value;
    this.fuenteFinanciacion.tipoOrigenFuenteFinanciacion = controls.origen.value;
    this.fuenteFinanciacion.fondoEstructural = controls.fondoEstructural.value;
    return this.fuenteFinanciacion;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.fuenteFinanciacion?.nombre ?? ''),
      descripcion: new FormControl(this.fuenteFinanciacion?.descripcion ?? ''),
      ambitoGeografico: new FormControl(this.fuenteFinanciacion?.tipoAmbitoGeografico ?? null),
      origen: new FormControl(this.fuenteFinanciacion?.tipoOrigenFuenteFinanciacion ?? null),
      fondoEstructural: new FormControl(this.fuenteFinanciacion?.fondoEstructural ?? true),
    });
    return formGroup;
  }

  protected saveOrUpdate(): Observable<IFuenteFinanciacion> {
    const fuenteFinanciacion = this.getValue();
    return this.isEdit() ? this.fuenteFinanciacionService.update(fuenteFinanciacion.id, fuenteFinanciacion) :
      this.fuenteFinanciacionService.create(fuenteFinanciacion);
  }
}

import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IProyecto } from '@core/models/csp/proyecto';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ModeloUnidadService } from '@core/services/csp/modelo-unidad.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { DateValidator } from '@core/validators/date-validator';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

const MSG_ACEPTAR = marker('btn.ok');
const MSG_ERROR_INIT = marker('error.load');
const SOLICITUD_PROYECTO_FECHA_INICIO_KEY = marker('csp.solicitud-proyecto.fecha-inicio');
const SOLICITUD_PROYECTO_FECHA_FIN_KEY = marker('csp.solicitud-proyecto.fecha-fin');
const SOLICITUD_PROYECTO_MODELO_EJECUCION_KEY = marker('csp.solicitud-proyecto.modelo-ejecucion');

export interface ISolicitudCrearProyectoModalData {
  solicitud: ISolicitud;
  solicitudProyecto: ISolicitudProyecto;
}

@Component({
  templateUrl: './solicitud-crear-proyecto-modal.component.html',
  styleUrls: ['./solicitud-crear-proyecto-modal.component.scss']
})
export class SolicitudCrearProyectoModalComponent
  extends BaseModalComponent<IProyecto, SolicitudCrearProyectoModalComponent> implements OnInit {
  fxLayoutProperties: FxLayoutProperties;
  textSaveOrUpdate: string;

  private modelosEjecucionFiltered = [] as IModeloEjecucion[];
  modelosEjecucion$: Observable<IModeloEjecucion[]>;

  private convocatoria: IConvocatoria;

  msgParamFechaFinEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamModeloEjecucionEntity = {};

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<SolicitudCrearProyectoModalComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: ISolicitudCrearProyectoModalData,
    private unidadModeloService: ModeloUnidadService,
    private logger: NGXLogger,
    private readonly translate: TranslateService,
    private convocatoriaService: ConvocatoriaService
  ) {
    super(snackBarService, matDialogRef, { solicitudId: data.solicitud?.id } as IProyecto);
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
    this.textSaveOrUpdate = MSG_ACEPTAR;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    if (this.data.solicitud.convocatoriaId) {
      this.subscriptions.push(this.convocatoriaService.findById(this.data.solicitud.convocatoriaId).subscribe(
        (convocatoria => {
          this.formGroup.controls.modeloEjecucion.setValue(convocatoria.modeloEjecucion);
          this.formGroup.controls.modeloEjecucion.disable();
        })
      ));
    }
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROYECTO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROYECTO_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROYECTO_MODELO_EJECUCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamModeloEjecucionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        fechaInicio: new FormControl(null, [Validators.required]),
        fechaFin: new FormControl(null, [Validators.required]),
        modeloEjecucion: new FormControl(null, [Validators.required]
        )
      },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin'),
          DateValidator.isBefore('fechaFin', 'fechaInicio')
        ]
      }
    );

    this.subscriptions.push(
      formGroup.controls.fechaInicio.valueChanges.subscribe((value) => {
        this.getFechaFinProyecto(value);
      })
    );

    if (this.convocatoria?.modeloEjecucion) {
      formGroup.controls.modeloEjecucion.disable();
    }

    return formGroup;
  }

  protected getDatosForm(): IProyecto {
    return {
      fechaInicio: this.formGroup.controls.fechaInicio.value,
      fechaFin: this.formGroup.controls.fechaFin.value,
      modeloEjecucion: this.formGroup.controls.modeloEjecucion.value,
      titulo: this.data.solicitud.titulo,
      solicitudId: this.data.solicitud.id
    } as IProyecto;
  }

  /**
   * Devuelve el nombre de un modelo de ejecución.
   * @param modeloEjecucion modelo de ejecución.
   * @returns nombre de un modelo de ejecución.
   */
  getModeloEjecucion(modeloEjecucion?: IModeloEjecucion): string | undefined {
    return typeof modeloEjecucion === 'string' ? modeloEjecucion : modeloEjecucion?.nombre;
  }

  loadModelosEjecucion(): void {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter(
        'unidadGestionRef',
        SgiRestFilterOperator.EQUALS,
        String(this.data?.solicitud?.unidadGestion?.id)
      )
    };
    const subcription = this.unidadModeloService.findAll(options).subscribe(
      res => {
        this.modelosEjecucionFiltered = res.items.map(item => item.modeloEjecucion);
        this.modelosEjecucion$ = this.formGroup.controls.modeloEjecucion.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filtroModeloEjecucion(value))
          );
      },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_INIT);
      }
    );
    this.subscriptions.push(subcription);
  }

  /**
   * Filtra la lista devuelta por el servicio
   *
   * @param value del input para autocompletar
   */
  private filtroModeloEjecucion(value: string): IModeloEjecucion[] {
    const filterValue = value.toString().toLowerCase();
    return this.modelosEjecucionFiltered.filter(modeloEjecucion => modeloEjecucion.nombre.toLowerCase().includes(filterValue));
  }

  private getFechaFinProyecto(fecha: DateTime): void {
    if (fecha && this.data?.solicitudProyecto?.duracion) {
      const fechaFin = fecha.plus({ months: this.data?.solicitudProyecto?.duracion, seconds: -1 });
      // fechaFin.day = fecha.day - 1;
      this.formGroup.controls.fechaFin.setValue(fechaFin);
    }
  }

}

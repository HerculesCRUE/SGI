import { Component, Inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { MSG_PARAMS } from '@core/i18n';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import { IProyecto } from '@core/models/csp/proyecto';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { NotificacionProyectoExternoCvnService } from '@core/services/csp/notificacion-proyecto-externo-cvn/notificacion-proyecto-externo-cvn.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { DateValidator } from '@core/validators/date-validator';
import { TranslateService } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const PROYECTO_KEY = marker('csp.proyecto');
const NOTIFICACION_CVN_PROYECTO_FECHA_INICIO_KEY = marker('csp.solicitud-proyecto.fecha-inicio');
const NOTIFICACION_CVN_PROYECTO_FECHA_FIN_KEY = marker('csp.solicitud-proyecto.fecha-fin');
const NOTIFICACION_CVN_PROYECTO_MODELO_EJECUCION_KEY = marker('csp.solicitud-proyecto.modelo-ejecucion');
const NOTIFICACION_CVN_PROYECTO_TITULO_KEY = marker('csp.solicitud-proyecto.titulo');
const NOTIFICACION_CVN_PROYECTO_UNIDAD_GESTION_KEY = marker('csp.convocatoria.unidad-gestion');

@Component({
  selector: 'sgi-notificacion-cvn-modal-asociar-proyecto',
  templateUrl: './notificacion-cvn-asociar-proyecto-modal.component.html',
  styleUrls: ['./notificacion-cvn-asociar-proyecto-modal.component.scss']
})
export class NotificacionCvnAsociarProyectoModalComponent extends DialogActionComponent<INotificacionProyectoExternoCVN> {

  proyectos$: Observable<IProyecto[]>;

  msgParamProyectoEntity = {};
  msgParamFechaFinEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamModeloEjecucionEntity = {};
  msgParamTituloEntity = {};
  msgParamUnidadGestionEntity = {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<NotificacionCvnAsociarProyectoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: INotificacionProyectoExternoCVN,
    protected snackBarService: SnackBarService,
    private readonly translate: TranslateService,
    private notificacionProyectoExternoCvnService: NotificacionProyectoExternoCvnService,
    private proyectoService: ProyectoService
  ) {
    super(matDialogRef, true);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      crearProyecto: new FormControl(null),
      proyecto: new FormControl(null),
      titulo: new FormControl(this.data.titulo),
      fechaInicio: new FormControl(this.data.fechaInicio),
      fechaFin: new FormControl(this.data.fechaFin),
      unidadGestion: new FormControl(null),
      modeloEjecucion: new FormControl(null)
    });

    this.setFormValidators(form);

    return form;
  }

  protected getValue(): INotificacionProyectoExternoCVN {
    if (this.formGroup.controls.crearProyecto.value) {
      this.data.proyecto = {
        titulo: this.formGroup.controls.titulo.value,
        fechaInicio: this.formGroup.controls.fechaInicio.value,
        fechaFin: this.formGroup.controls.fechaFin.value,
        unidadGestion: this.formGroup.controls.unidadGestion.value,
        modeloEjecucion: this.formGroup.controls.modeloEjecucion.value,
        activo: true
      } as IProyecto;
    } else {
      this.data.proyecto = this.formGroup.controls.proyecto.value;
    }

    return this.data;
  }

  protected saveOrUpdate(): Observable<INotificacionProyectoExternoCVN> {
    const notificacionProyectoExternoCvn = this.getValue();

    const proyecto$ = notificacionProyectoExternoCvn.proyecto.id ? of(notificacionProyectoExternoCvn.proyecto.id) : this.createProyecto(notificacionProyectoExternoCvn.proyecto);

    return proyecto$.pipe(
      map(proyectoId => {
        notificacionProyectoExternoCvn.proyecto.id = proyectoId;
        return notificacionProyectoExternoCvn;
      }),
      switchMap(result => {
        return this.notificacionProyectoExternoCvnService.asociarProyecto(result.id, result);
      })
    )
  }

  public selectFirstUnidadGEstionIfOnlyOneOption(options: SelectValue<IUnidadGestion>[]): void {
    if (options?.length === 1 && !this.formGroup.controls.unidadGestion.value) {
      this.formGroup.controls.unidadGestion.setValue(options[0].item);
    }
  }

  private createProyecto(proyecto: IProyecto): Observable<number> {
    return this.proyectoService.create(proyecto).pipe(
      map(proyectoCreated => proyectoCreated.id)
    )
  }

  private setFormValidators(form: FormGroup): void {
    this.subscriptions.push(
      form.controls.crearProyecto.valueChanges.subscribe(value => {
        if (value) {
          form.controls.titulo.setValidators([Validators.required, Validators.maxLength(250)]);
          form.controls.fechaInicio.setValidators([Validators.required]);
          form.controls.fechaFin.setValidators([Validators.required]);
          form.controls.unidadGestion.setValidators([Validators.required]);
          form.controls.modeloEjecucion.setValidators([Validators.required]);
          form.setValidators([
            DateValidator.isAfter('fechaInicio', 'fechaFin'),
            DateValidator.isBefore('fechaFin', 'fechaInicio')
          ]);

          form.controls.proyecto.clearValidators();
        } else {
          form.controls.proyecto.setValidators([Validators.required]);

          form.controls.titulo.clearValidators();
          form.controls.fechaInicio.clearValidators();
          form.controls.fechaFin.clearValidators();
          form.controls.unidadGestion.clearValidators();
          form.controls.modeloEjecucion.clearValidators();
          form.clearValidators();
        }

        form.controls.proyecto.updateValueAndValidity({ onlySelf: true });
        form.controls.titulo.updateValueAndValidity({ onlySelf: true });
        form.controls.fechaInicio.updateValueAndValidity({ onlySelf: true });
        form.controls.fechaFin.updateValueAndValidity({ onlySelf: true });
        form.controls.unidadGestion.updateValueAndValidity({ onlySelf: true });
        form.controls.modeloEjecucion.updateValueAndValidity({ onlySelf: true });
      })
    );
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamProyectoEntity = { entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      NOTIFICACION_CVN_PROYECTO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      NOTIFICACION_CVN_PROYECTO_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      NOTIFICACION_CVN_PROYECTO_MODELO_EJECUCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamModeloEjecucionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      NOTIFICACION_CVN_PROYECTO_TITULO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTituloEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      NOTIFICACION_CVN_PROYECTO_UNIDAD_GESTION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamUnidadGestionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

}

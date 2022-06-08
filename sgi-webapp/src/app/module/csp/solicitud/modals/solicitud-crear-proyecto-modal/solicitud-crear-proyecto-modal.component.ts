import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { ESTADO_MAP } from '@core/models/csp/estado-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { DateValidator } from '@core/validators/date-validator';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { merge, Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

const MSG_ACEPTAR = marker('btn.ok');
const SOLICITUD_PROYECTO_FECHA_INICIO_KEY = marker('csp.solicitud-proyecto.fecha-inicio');
const SOLICITUD_PROYECTO_FECHA_FIN_KEY = marker('csp.solicitud-proyecto.fecha-fin');
const SOLICITUD_PROYECTO_MODELO_EJECUCION_KEY = marker('csp.solicitud-proyecto.modelo-ejecucion');
const SOLICITUD_PROYECTO_TITULO_KEY = marker('csp.solicitud-proyecto.titulo');

export interface ISolicitudCrearProyectoModalData {
  solicitud: ISolicitud;
  solicitudProyecto: ISolicitudProyecto;
}

interface IProyectoData extends IProyecto {
  prorrogado: boolean;
  proyectosSGE: string;
}

@Component({
  templateUrl: './solicitud-crear-proyecto-modal.component.html',
  styleUrls: ['./solicitud-crear-proyecto-modal.component.scss']
})

export class SolicitudCrearProyectoModalComponent extends DialogActionComponent<IProyecto> implements OnInit {

  textSaveOrUpdate: string;

  displayedColumns = ['id', 'codigoSGE', 'titulo', 'fechaInicio', 'fechaFin', 'estado'];
  elementosPagina = [5, 10, 25, 100];
  totalElements = 0;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  private convocatoria: IConvocatoria;

  msgParamFechaFinEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamModeloEjecucionEntity = {};
  msgParamTituloEntity = {};
  proyectos$: Observable<IProyectoData[]>;

  constructor(
    matDialogRef: MatDialogRef<SolicitudCrearProyectoModalComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: ISolicitudCrearProyectoModalData,
    private readonly proyectoService: ProyectoService,
    private readonly translate: TranslateService,
    private convocatoriaService: ConvocatoriaService
  ) {
    super(matDialogRef, false);

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

    this.proyectos$ = this.getProyectos();
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

    this.translate.get(
      SOLICITUD_PROYECTO_TITULO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTituloEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        titulo: new FormControl(this.data.solicitud.titulo || null, [Validators.required, Validators.maxLength(250)]),
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

  protected getValue(): IProyecto {
    return {
      fechaInicio: this.formGroup.controls.fechaInicio.value,
      fechaFin: this.formGroup.controls.fechaFin.value,
      modeloEjecucion: this.formGroup.controls.modeloEjecucion.value,
      titulo: this.formGroup.controls.titulo.value,
      solicitudId: this.data.solicitud.id
    } as IProyecto;
  }

  protected getProyectos(): Observable<IProyectoData[]> {
    const filters = new RSQLSgiRestFilter('solicitudId', SgiRestFilterOperator.EQUALS, this.data.solicitud.id.toString())
    const filter: SgiRestFindOptions = {
      filter: filters
    }

    return this.proyectoService.findTodos(filter).pipe(
      map((response) => {
        this.totalElements = response.items.length;
        return response.items as IProyectoData[];
      }),
      switchMap((response) => {
        const requestsProyecto: Observable<IProyectoData>[] = [];
        response.forEach(proyecto => {
          const proyectoData = proyecto as IProyectoData;
          if (proyecto.id) {
            requestsProyecto.push(this.proyectoService.hasProyectoProrrogas(proyecto.id).pipe(
              map(value => {
                proyectoData.prorrogado = value;
                return proyectoData;
              }),
              switchMap(() =>
                this.proyectoService.findAllProyectosSgeProyecto(proyecto.id).pipe(
                  map(value => {
                    proyectoData.proyectosSGE = value.items.map(element => element.proyectoSge.id).join(', ');
                    return proyectoData;
                  }))
              )
            ));
          } else {
            requestsProyecto.push(of(proyectoData));
          }
        });
        return of(response).pipe(
          tap(() => merge(...requestsProyecto).subscribe())
        );
      })
    );
  }

  private getFechaFinProyecto(fecha: DateTime): void {
    if (fecha && this.data?.solicitudProyecto?.duracion) {
      const fechaFin = fecha.plus({ months: this.data?.solicitudProyecto?.duracion, seconds: -1 });
      // fechaFin.day = fecha.day - 1;
      this.formGroup.controls.fechaFin.setValue(fechaFin);
    }
  }

  protected saveOrUpdate(): Observable<IProyecto> {
    return this.proyectoService.crearProyectoBySolicitud(this.data.solicitud.id, this.getValue());
  }

}

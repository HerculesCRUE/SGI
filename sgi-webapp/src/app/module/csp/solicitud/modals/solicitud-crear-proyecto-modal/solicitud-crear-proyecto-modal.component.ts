import { AfterViewInit, Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { MSG_PARAMS } from '@core/i18n';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { ESTADO_MAP } from '@core/models/csp/estado-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { LanguageService } from '@core/services/language.service';
import { DateValidator } from '@core/validators/date-validator';
import { I18nValidators } from '@core/validators/i18n-validator';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { merge, Observable, of } from 'rxjs';
import { delay, map, switchMap, tap } from 'rxjs/operators';

const MSG_ACEPTAR = marker('btn.ok');
const SOLICITUD_PROYECTO_FECHA_INICIO_KEY = marker('csp.solicitud-proyecto.fecha-inicio');
const SOLICITUD_PROYECTO_FECHA_FIN_KEY = marker('csp.solicitud-proyecto.fecha-fin');
const SOLICITUD_PROYECTO_MODELO_EJECUCION_KEY = marker('csp.solicitud-proyecto.modelo-ejecucion');
const SOLICITUD_PROYECTO_TITULO_KEY = marker('csp.solicitud-proyecto.titulo');

export interface ISolicitudCrearProyectoModalData {
  solicitud: ISolicitud;
  solicitudProyecto: ISolicitudProyecto;
  convocatoria: IConvocatoria;
  nombreSolicitante: string;
}

interface IProyectoData extends IProyecto {
  prorrogado: boolean;
  proyectosSGE: string;
}

@Component({
  templateUrl: './solicitud-crear-proyecto-modal.component.html',
  styleUrls: ['./solicitud-crear-proyecto-modal.component.scss']
})

export class SolicitudCrearProyectoModalComponent extends DialogActionComponent<IProyecto> implements OnInit, AfterViewInit, OnDestroy {

  textSaveOrUpdate: string;

  displayedColumns = ['id', 'codigoSGE', 'titulo', 'fechaInicio', 'fechaFin', 'estado'];
  elementosPagina = [5, 10, 25, 100];
  totalElements = 0;

  @ViewChild(MatSort, { static: true }) private sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) private paginator: MatPaginator;

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

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
    private readonly languageService: LanguageService
  ) {
    super(matDialogRef, false);

    this.textSaveOrUpdate = MSG_ACEPTAR;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.proyectos$ = this.getProyectos();

    this.subscriptions.push(
      this.initialized$.pipe(delay(0)).subscribe(() => {
        this.formGroup.updateValueAndValidity();
      })
    );
  }

  ngAfterViewInit(): void {
    super.ngAfterViewInit();
    merge(
      this.paginator?.page,
      this.sort?.sortChange
    ).pipe(
      tap(() => this.proyectos$ = this.getProyectos())
    ).subscribe();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROYECTO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaInicioEntity = {
        entity: value,
        ...MSG_PARAMS.GENDER.FEMALE,
        ...MSG_PARAMS.CARDINALIRY.SINGULAR
      }
    );

    this.translate.get(
      SOLICITUD_PROYECTO_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaFinEntity = {
        entity: value,
        ...MSG_PARAMS.GENDER.FEMALE,
        ...MSG_PARAMS.CARDINALIRY.SINGULAR
      }
    );

    this.translate.get(
      SOLICITUD_PROYECTO_MODELO_EJECUCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamModeloEjecucionEntity = {
        entity: value,
        ...MSG_PARAMS.GENDER.MALE,
        ...MSG_PARAMS.CARDINALIRY.SINGULAR
      }
    );

    this.translate.get(
      SOLICITUD_PROYECTO_TITULO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTituloEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  protected buildFormGroup(): FormGroup {
    let titulo: I18nFieldValue[];
    if (this.data.solicitud.formularioSolicitud === FormularioSolicitud.PROYECTO) {
      titulo = this.data.solicitud.titulo;
    } else if (this.data.solicitud.formularioSolicitud === FormularioSolicitud.RRHH) {

      if (!!this.data?.convocatoria?.titulo) {
        titulo = this.data.convocatoria.titulo;
      } else if (!!this.data?.convocatoria?.anio || !!this.data?.convocatoria?.fechaPublicacion || !!this.data?.nombreSolicitante) {
        titulo = [
          {
            lang: this.languageService.getLanguage(),
            value: ''
          }
        ];
      }

      if (!!titulo) {
        titulo.forEach(t => {
          const camposTitulo: string[] = [];

          camposTitulo.push(t.value);

          if (!!this.data?.convocatoria?.anio) {
            camposTitulo.push(this.data.convocatoria.anio.toString());
          } else if (!!this.data?.convocatoria?.fechaPublicacion) {
            camposTitulo.push(this.data.convocatoria.fechaPublicacion.year.toString());
          }

          if (!!this.data?.nombreSolicitante) {
            camposTitulo.push(this.data.nombreSolicitante);
          }

          t.value = camposTitulo.join(' - ');
        })
      }
    }

    const formGroup = new FormGroup(
      {
        titulo: new FormControl(titulo || [], [I18nValidators.required, I18nValidators.maxLength(250)]),
        fechaInicio: new FormControl(null),
        fechaFin: new FormControl(null),
        modeloEjecucion: new FormControl(this.data.solicitud.modeloEjecucion, [Validators.required])
      },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin'),
          DateValidator.isBefore('fechaFin', 'fechaInicio', false)
        ]
      }
    );

    this.subscriptions.push(
      formGroup.controls.fechaInicio.valueChanges.subscribe((value) => {
        this.getFechaFinProyecto(value);
      })
    );

    if (this.data.solicitud.modeloEjecucion) {
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
      solicitudId: this.data.solicitud.id,
      unidadGestion: this.data.solicitud.unidadGestion
    } as IProyecto;
  }

  protected getProyectos(): Observable<IProyectoData[]> {
    const filters = new RSQLSgiRestFilter('solicitudId', SgiRestFilterOperator.EQUALS, this.data.solicitud.id.toString());
    const options: SgiRestFindOptions = {
      filter: filters,
      ...(this.paginator?.pageIndex && this.paginator?.pageSize && {
        page: {
          index: this.paginator.pageIndex,
          size: this.paginator.pageSize,
        },
      }),
      ...(this.sort?.active && this.sort?.direction && {
        sort: new RSQLSgiRestSort(
          this.resolveSortProperty(this.sort.active),
          SgiRestSortDirection.fromSortDirection(this.sort.direction)
        ),
      }),
    };

    return this.proyectoService.findTodos(options).pipe(
      map((response) => {
        this.totalElements = response.items.length;
        return response.items as IProyectoData[];
      }),
      switchMap((response) => {
        const requestsProyecto: Observable<IProyectoData>[] = [];
        response.forEach(proyecto => {
          if (proyecto.id) {
            requestsProyecto.push(this.proyectoService.hasProyectoProrrogas(proyecto.id).pipe(
              map(value => {
                proyecto.prorrogado = value;
                return proyecto;
              }),
              switchMap(() =>
                this.proyectoService.findAllProyectosSgeProyecto(proyecto.id).pipe(
                  map(value => {
                    proyecto.proyectosSGE = value.items.map(element => element.proyectoSge.id).join(', ');
                    return proyecto;
                  }))
              )
            ));
          } else {
            requestsProyecto.push(of(proyecto));
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
      this.formGroup.controls.fechaFin.setValue(fechaFin);
    }
  }

  protected saveOrUpdate(): Observable<IProyecto> {
    return this.proyectoService.crearProyectoBySolicitud(this.data.solicitud.id, this.getValue());
  }


  private resolveSortProperty(column: string): string {
    if (column === 'titulo') {
      return 'titulo.value';
    }
    return column;
  }
}

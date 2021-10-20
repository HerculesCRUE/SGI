import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { CausaExencion, CAUSA_EXENCION_MAP, IProyecto } from '@core/models/csp/proyecto';
import { IProyectoResponsableEconomico } from '@core/models/csp/proyecto-responsable-economico';
import { Orden } from '@core/models/csp/rol-proyecto';
import { IPersona } from '@core/models/sgp/persona';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SolicitudProyectoService } from '@core/services/csp/solicitud-proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormlyUtils } from '@core/utils/formly-utils';
import { FormlyFieldConfig, FormlyFormOptions } from '@ngx-formly/core';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, Subscription } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const PROYECTO_KEY = marker('sge.proyecto');
const MSG_ERROR = marker('error.load');
const MSG_SAVE_ERROR = marker('error.save.request.entity');
const MSG_SAVE_SUCCESS = marker('msg.save.request.entity.success');

export enum ACTION_MODAL_MODE {
  VIEW = 'view',
  NEW = 'new',
  EDIT = 'edit'
}

export interface IProyectoEconomicoFormlyData {
  proyectoSgiId: number;
}

interface IFormlyData {
  fields: FormlyFieldConfig[];
  data: any;
  model: any;
}

interface IDatosProyecto {
  proyecto: IProyecto;
  numeroDocumentoResponsable: string;
  importeTotalGastos: number;
  importeTotalIngresos: number;
  porIva: number;
  causaExencion: CausaExencion;
  causaExencionDesc?: string;
}
interface IResponsable {
  fechaInicio: DateTime;
  fechaFin: DateTime;
  persona: IPersona;
}

@Component({
  templateUrl: './proyecto-economico-formly-modal.component.html',
  styleUrls: ['./proyecto-economico-formly-modal.component.scss']
})
export class ProyectoEconomicoFormlyModalComponent implements OnInit, OnDestroy {

  ACTION_MODAL_MODE = ACTION_MODAL_MODE;

  title: string;

  private textoCrearSuccess: string;
  private textoCrearError: string;

  private subscriptions: Subscription[] = [];

  formGroup: FormGroup = new FormGroup({});
  formlyData: IFormlyData = {
    fields: [],
    data: {},
    model: {}
  };

  options: FormlyFormOptions = {
    formState: {
      mainModel: {},
    },
  };

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ProyectoEconomicoFormlyModalComponent>,
    @Inject(MAT_DIALOG_DATA) public proyectoData: IProyectoEconomicoFormlyData,
    private readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private readonly proyectoSgeService: ProyectoSgeService,
    private readonly solicitudProyectoService: SolicitudProyectoService,
    private readonly solicitudService: SolicitudService,

  ) {
    this.subscriptions.push(
      this.loadFormlyData(proyectoData?.proyectoSgiId).subscribe(
        (formlyData) => {
          this.options.formState.mainModel = formlyData.data;
          this.formlyData.model = {};
          this.formlyData = formlyData;
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR);
          this.matDialogRef.close();
        }
      )
    );
  }

  ngOnInit(): void {
    this.setupI18N();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }


  private loadFormlyData(id: number): Observable<IFormlyData> {
    let load$: Observable<IFormlyData> = of({ fields: [], data: {}, model: {} });
    load$ = this.proyectoSgeService.getFormlyCreate().pipe(
      map(fields => {
        return {
          fields,
          data: {},
          model: {}
        } as IFormlyData;
      }),
      switchMap((formlyData) => {
        return this.proyectoService.findById(id).pipe(
          map((proyectoEconomico) => {
            formlyData.data.proyecto = proyectoEconomico;
            formlyData.data.fechaInicio = proyectoEconomico.fechaInicio;
            formlyData.data.fechaFin = proyectoEconomico.fechaFin;
            formlyData.data.porIva = proyectoEconomico.iva?.iva;
            formlyData.data.importeTotalGastos = proyectoEconomico.importePresupuesto;
            formlyData.data.causaExencion = proyectoEconomico.causaExencion;

            if (formlyData.data.causaExencion) {
              formlyData.data.causaExencionDesc = this.translate.instant(
                CAUSA_EXENCION_MAP.get(formlyData.data.causaExencion)
              );
            }
            return formlyData;
          })
        );
      }),
      switchMap((formlyData) => {
        return this.findNumeroDocumentoResponsableEcnomicoOrMiembroEquipo(id, formlyData);
      }),
      switchMap((formlyData) => {
        const proyectoEconomico = formlyData.data.proyecto;

        if (!formlyData.data.importeTotalGastos && proyectoEconomico.solicitudId) {
          return this.findImportePresupuestoBySolicitudProyecto(proyectoEconomico, formlyData);
        } else {
          return of(formlyData);
        }
      }),
      switchMap((formlyData) => {
        const proyectoEconomico = formlyData.data.proyecto;

        if (!formlyData.data.importeTotalGastos && proyectoEconomico.solicitudId) {
          return this.findImportePresupuestoBySolicitudProyectoGastos(proyectoEconomico, formlyData);
        } else {
          return of(formlyData);
        }
      }),
      switchMap((formlyData) => {
        formlyData.data.importeTotalIngresos = formlyData.data.importeTotalGastos;
        return of(formlyData);
      })
    );
    return load$;
  }

  private findImportePresupuestoBySolicitudProyecto(proyectoEconomico: any, formlyData: IFormlyData): Observable<IFormlyData> {
    return this.solicitudProyectoService.findById(proyectoEconomico.solicitudId).pipe(
      switchMap((solicitudProyecto) => {
        if (solicitudProyecto.importePresupuestado) {
          formlyData.data.importeTotalGastos = solicitudProyecto.importePresupuestado;
        }
        return of(formlyData);
      })
    );
  }

  private findImportePresupuestoBySolicitudProyectoGastos(proyectoEconomico: any, formlyData: IFormlyData): Observable<IFormlyData> {
    return this.solicitudService.findAllSolicitudProyectoPresupuesto(proyectoEconomico.solicitudId).pipe(
      map(response => {
        return response.items.reduce(
          (total, solicitudProyectoConceptoGasto) => total + solicitudProyectoConceptoGasto.importePresupuestado, 0);
      }),
      switchMap((result) => {
        formlyData.data.importeTotalGastos = result;
        return of(formlyData);
      })
    );
  }

  private findNumeroDocumentoResponsableEcnomicoOrMiembroEquipo(id: number, formlyData: IFormlyData): Observable<IFormlyData> {
    return this.proyectoService.findAllProyectoResponsablesEconomicos(id).pipe(
      map(response => response.items.map(responsable => {
        return {
          fechaInicio: responsable.fechaInicio,
          fechaFin: responsable.fechaFin,
          persona: responsable.persona
        } as IResponsable;
      })),
      map((responsablesEconomicos: IResponsable[]) => {
        return this.getCurrentResponsable(responsablesEconomicos);
      }),
      switchMap((result: IResponsable) => {
        if (!result?.persona) {
          return this.getCurrentMiembroEquipoWithRolOrdenPrimario(id);
        }
        return of(result);
      }),
      switchMap((result) => {
        if (result) {
          formlyData.data.numeroDocumentoResponsable = result.persona.id;
        }
        return of(formlyData);
      })
    );
  }

  private getCurrentResponsable(responsablesEconomicos: IResponsable[]): IResponsable {
    responsablesEconomicos.sort(
      (a, b) => {
        const dateA = a.fechaInicio;
        const dateB = b.fechaInicio;
        return (dateA > dateB) ? 1 : ((dateB > dateA) ? -1 : 0);
      }
    );

    const dateTimeNow = DateTime.now();
    return responsablesEconomicos.find(responsableEconomico => {
      if (!responsableEconomico.fechaInicio) {
        responsableEconomico.fechaInicio = dateTimeNow;
      }
      if (!responsableEconomico.fechaFin) {
        responsableEconomico.fechaFin = dateTimeNow;
      }
      return responsableEconomico.fechaInicio.toMillis() <= dateTimeNow.toMillis() && responsableEconomico.fechaFin >= dateTimeNow;
    }
    );
  }

  /**
   * Checks the formGroup, returns the entered data and closes the modal
   */
  saveOrUpdate(): void {
    this.formGroup.markAllAsTouched();

    if (this.formGroup.valid) {

      this.parseModel();

      this.subscriptions.push(this.proyectoSgeService.createProyecto(this.formlyData.model).subscribe(
        () => {
          this.matDialogRef.close();
          this.snackBarService.showSuccess(this.textoCrearSuccess);
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(this.textoCrearError);
        }
      ));
    }
  }

  private parseModel() {
    FormlyUtils.convertFormlyToJSON(this.formlyData.model, this.formlyData.fields);
    this.formlyData.model.causaExencion = this.formlyData.data.causaExencion;
    this.formlyData.model.modeloEjecucion = {
      id: this.formlyData.model.modeloEjecucion.id,
      nombre: this.formlyData.model.modeloEjecucion.nombre,
    };
    this.formlyData.model.tipoFinalidad = {
      id: this.formlyData.model.tipoFinalidad.id,
      nombre: this.formlyData.model.tipoFinalidad.nombre,
    };
  }

  private setupI18N(): void {

    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.title = value);

    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrearSuccess = value);

    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrearError = value);
  }

  private getCurrentMiembroEquipoWithRolOrdenPrimario(id: number): Observable<IResponsable> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('rolProyecto.rolPrincipal', SgiRestFilterOperator.EQUALS, 'true')
        .and('rolProyecto.orden', SgiRestFilterOperator.EQUALS, Orden.PRIMARIO)
    };
    return this.proyectoService.findAllProyectoEquipo(id, options).pipe(
      map(responseIP => responseIP.items.map(investigadorPrincipal => {
        return {
          fechaInicio: investigadorPrincipal.fechaInicio,
          fechaFin: investigadorPrincipal.fechaFin,
          persona: investigadorPrincipal.persona
        } as IResponsable;
      })),
      map(responsablesEconomicos => {
        return this.getCurrentResponsable(responsablesEconomicos);
      })
    );
  }
}




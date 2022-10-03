import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupo } from '@core/models/csp/grupo';
import { CAUSA_EXENCION_MAP } from '@core/models/csp/proyecto';
import { Orden } from '@core/models/csp/rol-proyecto';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { IPersona } from '@core/models/sgp/persona';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SolicitudProyectoService } from '@core/services/csp/solicitud-proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { FormlyUtils } from '@core/utils/formly-utils';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { ACTION_MODAL_MODE, BaseFormlyModalComponent, IFormlyData } from 'src/app/esb/shared/formly-forms/core/base-formly-modal.component';

const PROYECTO_KEY = marker('sge.proyecto');

export interface IProyectoEconomicoFormlyData {
  proyectoSgiId: number;
  proyectoSge: IProyectoSge;
  action: ACTION_MODAL_MODE;
  grupoInvestigacion: IGrupo;
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
export class ProyectoEconomicoFormlyModalComponent
  extends BaseFormlyModalComponent<IProyectoEconomicoFormlyData, IProyectoSge>
  implements OnInit {

  constructor(
    public readonly matDialogRef: MatDialogRef<ProyectoEconomicoFormlyModalComponent>,
    @Inject(MAT_DIALOG_DATA) public proyectoData: IProyectoEconomicoFormlyData,
    protected readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private readonly proyectoSgeService: ProyectoSgeService,
    private readonly solicitudProyectoService: SolicitudProyectoService,
    private readonly solicitudService: SolicitudService,
    private readonly grupoService: GrupoService

  ) {
    super(matDialogRef, proyectoData?.action === ACTION_MODAL_MODE.EDIT, translate);
  }

  protected initializer = (): Observable<void> => this.loadFormlyData(
    this.proyectoData?.action,
    this.proyectoData?.proyectoSgiId,
    this.proyectoData?.proyectoSge?.id,
    this.proyectoData?.grupoInvestigacion
  )

  ngOnInit(): void {
    super.ngOnInit();
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({});
  }

  protected getValue(): IProyectoEconomicoFormlyData {
    return this.proyectoData;
  }

  protected getKey(): string {
    return PROYECTO_KEY;
  }

  protected getGender() {
    return MSG_PARAMS.GENDER.FEMALE;
  }

  protected loadFormlyData(action: ACTION_MODAL_MODE, proyectoSgiId: number, proyectoSgeId: any, grupo: IGrupo): Observable<void> {
    let formly$: Observable<FormlyFieldConfig[]>;
    switch (action) {
      case ACTION_MODAL_MODE.EDIT:
        formly$ = this.proyectoSgeService.getFormlyUpdate();
        break;
      case ACTION_MODAL_MODE.NEW:
        formly$ = this.proyectoSgeService.getFormlyCreate();
        break;
      default:
        formly$ = of([]);
    }

    let load$: Observable<IFormlyData>;

    if (grupo == null) {
      load$ = this.fillProyectoData(formly$, action, proyectoSgiId, proyectoSgeId);
    } else {
      load$ = this.fillGrupoData(formly$, grupo);
    }

    return load$.pipe(
      tap(formlyData => {
        this.options.formState.mainModel = formlyData.data;
        this.formlyData.model = {};
        this.formlyData = formlyData;
      }),
      switchMap(() => of(void 0))
    );
  }

  protected saveOrUpdate(): Observable<IProyectoSge> {
    delete this.formlyData.model.proyectoSgeId;
    this.parseModel();

    return this.proyectoData.action === ACTION_MODAL_MODE.NEW
      ? this.createProyectoSge(this.formlyData)
      : this.updateProyectoSge(this.proyectoData.proyectoSge, this.formlyData);
  }


  private fillProyectoData(
    load$: Observable<FormlyFieldConfig[]>, action: ACTION_MODAL_MODE, proyectoSgiId: number, proyectoSgeId: any): Observable<IFormlyData> {
    return load$.pipe(
      map(fields => {
        return {
          fields,
          data: {},
          model: {}
        } as IFormlyData;
      }),
      switchMap((formlyData) => {
        return this.proyectoService.findById(proyectoSgiId).pipe(
          map((proyecto) => {
            formlyData.data.proyecto = proyecto;
            formlyData.data.fechaInicio = proyecto.fechaInicio;
            formlyData.data.fechaFin = proyecto.fechaFin;
            formlyData.data.porIva = proyecto.iva?.iva;
            formlyData.data.importeTotalGastos = proyecto.importePresupuesto;
            formlyData.data.causaExencion = proyecto.causaExencion;
            formlyData.data.tipoFinalidad = proyecto.finalidad;
            if (action === ACTION_MODAL_MODE.EDIT) {
              formlyData.data.sgeId = proyectoSgeId;
            }

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
        return this.fillNumeroDocumentoResponsableEcnomicoOrMiembroEquipo(proyectoSgiId, formlyData);
      }),
      switchMap((formlyData) => {
        const proyectoEconomico = formlyData.data.proyecto;

        if (!formlyData.data.importeTotalGastos && proyectoEconomico.solicitudId) {
          return this.fillImportePresupuestoBySolicitudProyecto(proyectoEconomico, formlyData);
        } else {
          return of(formlyData);
        }
      }),
      switchMap((formlyData) => {
        const proyectoEconomico = formlyData.data.proyecto;

        if (!formlyData.data.importeTotalGastos && proyectoEconomico.solicitudId) {
          return this.fillImportePresupuestoBySolicitudProyectoGastos(proyectoEconomico, formlyData);
        } else {
          return of(formlyData);
        }
      }),
      switchMap((formlyData) => {
        formlyData.data.importeTotalIngresos = formlyData.data.importeTotalGastos;
        return of(formlyData);
      })
    );
  }

  private fillGrupoData(
    load$: Observable<FormlyFieldConfig[]>,
    grupo: IGrupo
  ): Observable<IFormlyData> {
    return load$.pipe(
      map(fields => {
        return {
          fields,
          data: {
            fechaInicio: grupo?.fechaInicio,
            fechaFin: grupo?.fechaFin,
            proyecto: {
              id: grupo?.id,
              titulo: grupo?.nombre,
              finalidad: {
                id: 17,
                nombre: 'Grupo de investigación o proyecto de fondos propios'
              },
              fechaInicio: grupo?.fechaInicio,
              fechaFin: grupo?.fechaFin,
              modeloEjecucion: {
                id: 3,
                nombre: 'Recursos propios'
              }
            },
            tipoFinalidad: {
              id: 17,
              nombre: 'Grupo de investigación o proyecto de fondos propios'
            }
          },
          model: {}
        } as IFormlyData;
      }),
      switchMap((formlyData) => {
        return this.fillNumeroDocumentoResponsableEconomicoOrMiembroEquipo(grupo, formlyData);
      }),
    );
  }

  private fillImportePresupuestoBySolicitudProyecto(proyectoEconomico: any, formlyData: IFormlyData): Observable<IFormlyData> {
    return this.solicitudService.findById(proyectoEconomico.solicitudId).pipe(
      switchMap(solicitud => {
        if (!!!solicitud || solicitud.formularioSolicitud !== FormularioSolicitud.PROYECTO) {
          return of(formlyData);
        }

        return this.solicitudProyectoService.findById(proyectoEconomico.solicitudId).pipe(
          switchMap((solicitudProyecto) => {
            if (solicitudProyecto.importePresupuestado) {
              formlyData.data.importeTotalGastos = solicitudProyecto.importePresupuestado;
            }
            return of(formlyData);
          })
        );
      })
    );
  }

  private fillImportePresupuestoBySolicitudProyectoGastos(proyectoEconomico: any, formlyData: IFormlyData): Observable<IFormlyData> {
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

  private fillNumeroDocumentoResponsableEcnomicoOrMiembroEquipo(id: number, formlyData: IFormlyData): Observable<IFormlyData> {
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

  private fillNumeroDocumentoResponsableEconomicoOrMiembroEquipo(grupo: IGrupo, formlyData: IFormlyData): Observable<IFormlyData> {
    return this.grupoService.findResponsablesEconomicos(grupo?.id).pipe(
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
          return this.getCurrentMiembroEquipoWithRolOrdenPrimarioGrupo(grupo?.id);
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

  private parseModel() {
    FormlyUtils.convertFormlyToJSON(this.formlyData.model, this.formlyData.fields);
    this.formlyData.model.causaExencion = this.formlyData.data.causaExencion;
    this.formlyData.model.modeloEjecucion = {
      id: this.formlyData.model.modeloEjecucion?.id,
      nombre: this.formlyData.model.modeloEjecucion?.nombre,
    };
    this.formlyData.model.tipoFinalidad = {
      id: this.formlyData.model.tipoFinalidad?.id,
      nombre: this.formlyData.model.tipoFinalidad?.nombre,
    };
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

  private getCurrentMiembroEquipoWithRolOrdenPrimarioGrupo(idGrupo: number): Observable<IResponsable> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('rol.rolPrincipal', SgiRestFilterOperator.EQUALS, 'true')
        .and('rol.orden', SgiRestFilterOperator.EQUALS, Orden.PRIMARIO)
    };
    return this.grupoService.findMiembrosEquipo(idGrupo, options).pipe(
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

  private createProyectoSge(formlyData: IFormlyData): Observable<IProyectoSge> {
    return this.proyectoSgeService.createProyecto(formlyData.model).pipe(
      switchMap(response => this.proyectoSgeService.findById(response))
    );
  }

  private updateProyectoSge(proyectoSge: IProyectoSge, formlyData: IFormlyData): Observable<IProyectoSge> {
    return this.proyectoSgeService.updateProyecto(proyectoSge.id, formlyData.model).pipe(
      map(() => proyectoSge)
    );
  }

}

import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
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
import { PersonaService } from '@core/services/sgp/persona.service';
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

export interface IProyectoEconomicoFormlyResponse {
  createdOrUpdated: boolean;
  proyectoSge?: IProyectoSge;
}

interface IResponsable {
  fechaInicio: DateTime;
  fechaFin: DateTime;
  persona: IPersona;
}

enum FormlyFields {
  CAUSA_EXENCION = 'causaExencion',
  CODIGO_INTERNO = 'codigoInterno',
  FINALIDAD = 'finalidad',
  IMPORTE_TOTAL_GASTOS = 'importeTotalGastos',
  IMPORTE_TOTAL_INGRESOS = 'importeTotalIngresos',
  IVA_DEDUCIBLE = 'ivaDeducible',
  NUMERO_DOCUMENTO_RESPONSABLE = 'numeroDocumentoResponsable',
  POR_IVA = 'porIva',
  RESPONSABLE_REF = 'responsableRef'
}

@Component({
  templateUrl: './proyecto-economico-formly-modal.component.html',
  styleUrls: ['./proyecto-economico-formly-modal.component.scss']
})
export class ProyectoEconomicoFormlyModalComponent
  extends BaseFormlyModalComponent<IProyectoEconomicoFormlyData, IProyectoEconomicoFormlyResponse>
  implements OnInit {

  formlyFieldKeys: string[];

  constructor(
    public readonly matDialogRef: MatDialogRef<ProyectoEconomicoFormlyModalComponent>,
    @Inject(MAT_DIALOG_DATA) public proyectoData: IProyectoEconomicoFormlyData,
    protected readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private readonly proyectoSgeService: ProyectoSgeService,
    private readonly solicitudProyectoService: SolicitudProyectoService,
    private readonly solicitudService: SolicitudService,
    private readonly grupoService: GrupoService,
    private readonly personaService: PersonaService

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
      case ACTION_MODAL_MODE.SELECT_AND_NOTIFY:
        formly$ = this.proyectoSgeService.getFormlyUpdate();
        break;
      case ACTION_MODAL_MODE.NEW:
        formly$ = this.proyectoSgeService.getFormlyCreate();
        break;
      case ACTION_MODAL_MODE.VIEW:
        formly$ = this.proyectoSgeService.getFormlyView();
        break;
      default:
        formly$ = of([]);
    }

    let load$: Observable<IFormlyData>;
    this.options.formState.isGrupo = !!grupo;
    if (grupo == null) {
      load$ = this.fillProyectoData(formly$, action, proyectoSgiId, proyectoSgeId);
    } else {
      load$ = this.fillGrupoData(formly$, grupo);
    }

    switch (action) {
      case ACTION_MODAL_MODE.EDIT:
      case ACTION_MODAL_MODE.VIEW:
        load$ = load$.pipe(
          switchMap((formlyData) => {
            return this.fillProyectoSgeFormlyModelById(proyectoSgeId, proyectoSgiId, formlyData);
          })
        );
        break;
      case ACTION_MODAL_MODE.NEW:
      case ACTION_MODAL_MODE.SELECT_AND_NOTIFY:
        break;
    }

    return load$.pipe(
      tap(formlyData => {
        this.options.formState.mainModel = formlyData.data;
        this.formlyData = formlyData;
      }),
      switchMap(() => of(void 0))
    );
  }

  protected saveOrUpdate(): Observable<IProyectoEconomicoFormlyResponse> {
    delete this.formlyData.model.proyectoSgeId;
    this.parseModel();

    return this.proyectoData.action === ACTION_MODAL_MODE.NEW
      ? this.createProyectoSge(this.formlyData)
      : this.updateProyectoSge(this.proyectoData.proyectoSge, this.formlyData);
  }

  private fillProyectoSgeFormlyModelById(id: string, proyectoSgiId: number, formlyData: IFormlyData): Observable<IFormlyData> {
    return this.proyectoSgeService.getFormlyModelById(id).pipe(
      map((model) => {
        FormlyUtils.convertJSONToFormly(model, formlyData.fields);
        formlyData.data.proyectoSge = model;
        formlyData.data.proyectoSge.proyectoSGIId = proyectoSgiId;
        return formlyData;
      })
    );
  }

  private fillProyectoData(
    load$: Observable<FormlyFieldConfig[]>,
    action: ACTION_MODAL_MODE,
    proyectoSgiId: number,
    proyectoSgeId: string
  ): Observable<IFormlyData> {
    return load$.pipe(
      map(fields => {
        return {
          fields,
          data: {},
          model: {}
        } as IFormlyData;
      }),
      tap(formlyData => this.fillFormlyFieldKeys(formlyData)),
      switchMap((formlyData) => {
        return this.proyectoService.findById(proyectoSgiId).pipe(
          map((proyecto) => {
            formlyData.data.proyecto = proyecto;

            if (this.formlyContainsField(FormlyFields.CAUSA_EXENCION)) {
              formlyData.data.causaExencion = proyecto.causaExencion;

              if (formlyData.data.causaExencion) {
                formlyData.data.causaExencionDesc = this.translate.instant(
                  CAUSA_EXENCION_MAP.get(formlyData.data.causaExencion)
                );
              }
            }

            if (this.formlyContainsField(FormlyFields.CODIGO_INTERNO)) {
              formlyData.data.codigoInterno = proyecto.codigoInterno;
            }

            if (this.formlyContainsField(FormlyFields.FINALIDAD)) {
              formlyData.data.tipoFinalidad = proyecto.finalidad;
            }

            if (this.formlyContainsField(FormlyFields.IVA_DEDUCIBLE)) {
              formlyData.data.ivaDeducible = proyecto.ivaDeducible;
            }

            if (this.formlyContainsField(FormlyFields.IMPORTE_TOTAL_GASTOS)) {
              formlyData.data.importeTotalGastos = proyecto.importePresupuesto;
            }

            if (this.formlyContainsField(FormlyFields.POR_IVA)) {
              formlyData.data.porIva = proyecto.iva?.iva;
            }

            formlyData.data.sgeId = proyectoSgeId;

            return formlyData;
          })
        );
      }),
      switchMap((formlyData) => this.fillResponsableRef(formlyData, proyectoSgiId)),
      switchMap((formlyData) => this.fillNumeroDocumentoResponsable(formlyData, proyectoSgiId)),
      switchMap((formlyData) => this.fillImporteTotalGastos(formlyData)),
      switchMap((formlyData) => this.fillImporteTotalIngresos(formlyData))
    );
  }

  /**
   * Rellena el campo responsableRef si esta presente en el formly
   */
  private fillResponsableRef(formlyData: IFormlyData, proyectoSgiId: number): Observable<IFormlyData> {
    if (this.formlyContainsField(FormlyFields.RESPONSABLE_REF)) {
      return this.proyectoService.findAllProyectoResponsablesEconomicos(proyectoSgiId).pipe(
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
            return this.getCurrentMiembroEquipoWithRolOrdenPrimario(proyectoSgiId);
          }
          return of(result);
        }),
        switchMap((result) => {
          if (result) {
            return this.personaService.findById(result.persona.id).pipe(
              map(persona => {
                formlyData.data.responsable = {
                  id: persona.id,
                  nombreCompleto: (persona?.nombre ?? '') + ' ' + (persona?.apellidos ?? '')
                };

                return formlyData;
              })
            )

          }
          return of(formlyData);
        })
      );
    }

    return of(formlyData);
  }

  /**
   * Rellena el campo numeroDocumentoResponsable si esta presente en el formly
   */
  private fillNumeroDocumentoResponsable(formlyData: IFormlyData, proyectoSgiId: number): Observable<IFormlyData> {
    if (this.formlyContainsField(FormlyFields.NUMERO_DOCUMENTO_RESPONSABLE)) {
      return this.fillNumeroDocumentoResponsableEcnomicoOrMiembroEquipo(proyectoSgiId, formlyData);
    }

    return of(formlyData);
  }

  /**
   * Rellena el campo importeTotalGastos si esta presente en el formly y no esta ya relleno
   */
  private fillImporteTotalGastos(formlyData: IFormlyData): Observable<IFormlyData> {
    const solicitudId = formlyData.data.proyecto.solicitudId;

    let formlyData$: Observable<IFormlyData>
    if (this.formlyContainsField(FormlyFields.IMPORTE_TOTAL_GASTOS) && !formlyData.data.importeTotalGastos && solicitudId) {
      formlyData$ = this.fillImportePresupuestoBySolicitudProyecto(solicitudId, formlyData);
    } else {
      formlyData$ = of(formlyData);
    }

    return formlyData$.pipe(
      switchMap((formlyData) => {
        if (this.formlyContainsField(FormlyFields.IMPORTE_TOTAL_GASTOS) && !formlyData.data.importeTotalGastos && solicitudId) {
          return this.fillImportePresupuestoBySolicitudProyectoGastos(solicitudId, formlyData);
        }

        return of(formlyData);
      })
    );
  }

  /**
   * Rellena el campo importeTotalIngresos si esta presente en el formly
   */
  private fillImporteTotalIngresos(formlyData: IFormlyData): Observable<IFormlyData> {
    if (this.formlyContainsField(FormlyFields.IMPORTE_TOTAL_GASTOS)) {
      formlyData.data.importeTotalIngresos = formlyData.data.importeTotalGastos;
    }

    return of(formlyData);
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
              finalidad: {},
              fechaInicio: grupo?.fechaInicio,
              fechaFin: grupo?.fechaFin,
              modeloEjecucion: {}
            },
            tipoFinalidad: {}
          },
          model: {}
        } as IFormlyData;
      }),
      switchMap((formlyData) => {
        return this.fillNumeroDocumentoResponsableEconomicoOrMiembroEquipo(grupo, formlyData);
      }),
    );
  }

  /**
   * Rellena el campo importeTotalGastos del formly
   */
  private fillImportePresupuestoBySolicitudProyecto(solicitudId: number, formlyData: IFormlyData): Observable<IFormlyData> {
    return this.solicitudService.findById(solicitudId).pipe(
      switchMap(solicitud => {
        if (!!!solicitud || solicitud.formularioSolicitud !== FormularioSolicitud.PROYECTO) {
          return of(formlyData);
        }

        return this.solicitudProyectoService.findById(solicitudId).pipe(
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

  /**
   * Rellena el campo importeTotalGastos del formly
   */
  private fillImportePresupuestoBySolicitudProyectoGastos(solicitudId: number, formlyData: IFormlyData): Observable<IFormlyData> {
    return this.solicitudService.findAllSolicitudProyectoPresupuesto(solicitudId).pipe(
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

  /**
   * Rellena el campo numeroDocumentoResponsable del formly
   */
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

  /**
   * Rellena el campo numeroDocumentoResponsable del formly
   */
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
    if (this.formlyData.model.modeloEjecucion?.id) {
      this.formlyData.model.modeloEjecucion = {
        id: this.formlyData.model.modeloEjecucion?.id,
        nombre: this.formlyData.model.modeloEjecucion?.nombre,
      };
    } else {
      this.formlyData.model.modeloEjecucion = null;
    }

    if (this.formlyData.model.tipoFinalidad?.id) {
      this.formlyData.model.tipoFinalidad = {
        id: this.formlyData.model.tipoFinalidad?.id,
        nombre: this.formlyData.model.tipoFinalidad?.nombre,
      };
    } else {
      this.formlyData.model.tipoFinalidad = null;
    }
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

  private createProyectoSge(formlyData: IFormlyData): Observable<IProyectoEconomicoFormlyResponse> {
    return this.proyectoSgeService.createProyecto(formlyData.model).pipe(
      switchMap(response => {
        if (!response) {
          return of({ createdOrUpdated: true } as IProyectoEconomicoFormlyResponse);
        }
        return this.proyectoSgeService.findById(response).pipe(
          map(proyectoSge => {
            return {
              createdOrUpdated: true,
              proyectoSge
            }
          })
        );
      })
    );
  }

  private updateProyectoSge(proyectoSge: IProyectoSge, formlyData: IFormlyData): Observable<IProyectoEconomicoFormlyResponse> {
    return this.proyectoSgeService.updateProyecto(proyectoSge.id, formlyData.model).pipe(
      map(() => {
        return {
          createdOrUpdated: true,
          proyectoSge
        }
      })
    );
  }

  private getFormlyFieldKeys(field: any, formlyFieldKeys: string[]): string[] {
    if (field.hasOwnProperty('fieldGroup') && Array.isArray(field.fieldGroup)) {
      field.fieldGroup.forEach((fieldGroupItem: any) => {
        if (fieldGroupItem.hasOwnProperty('fieldGroup') && Array.isArray(fieldGroupItem.fieldGroup)) {
          this.getFormlyFieldKeys(fieldGroupItem, formlyFieldKeys);
        } else if (fieldGroupItem.hasOwnProperty('key')) {
          formlyFieldKeys.push(fieldGroupItem.key);
        }
      });
    }
    return formlyFieldKeys;
  }

  private fillFormlyFieldKeys(formlyData: IFormlyData): void {
    let formlyFields: string[] = [];
    formlyData.fields.forEach(field => this.getFormlyFieldKeys(field, formlyFields));
    this.formlyFieldKeys = formlyFields;
  }

  private formlyContainsField(formlyField: FormlyFields): boolean {
    return this.formlyFieldKeys.some(field => field === formlyField);
  }

}

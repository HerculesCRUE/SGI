import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FORMULARIO_SOLICITUD_MAP, FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { MSG_PARAMS } from '@core/i18n';
import { I18nFieldValueRequest } from '@core/i18n/i18n-field-request';
import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IGrupo } from '@core/models/csp/grupo';
import { CAUSA_EXENCION_MAP, IProyecto } from '@core/models/csp/proyecto';
import { Orden } from '@core/models/csp/rol-proyecto';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { IPersona } from '@core/models/sgp/persona';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { IConvocatoriaResponse } from '@core/services/csp/convocatoria/convocatoria-response';
import { CONVOCATORIA_RESPONSE_CONVERTER } from '@core/services/csp/convocatoria/convocatoria-response.converter';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { IModeloEjecucionResponse } from '@core/services/csp/modelo-ejecucion/modelo-ejecucion-response';
import { MODELO_EJECUCION_RESPONSE_CONVERTER } from '@core/services/csp/modelo-ejecucion/modelo-ejecucion-response.converter';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SolicitudProyectoService } from '@core/services/csp/solicitud-proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { ITipoFinalidadResponse } from '@core/services/csp/tipo-finalidad/tipo-finalidad-response';
import { TIPO_FINALIDAD_RESPONSE_CONVERTER } from '@core/services/csp/tipo-finalidad/tipo-finalidad-response.converter';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { UNIDAD_GESTION_REQUEST_CONVERTER } from '@core/services/csp/unidad-gestion/unidad-gestion-request.converter';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { FormlyUtils } from '@core/utils/formly-utils';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { forkJoin, Observable, of } from 'rxjs';
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

interface IFormlyDataSGI {
  id: number;
  anio: number;
  causaExencion: {
    id: string;
    descripcion: string;
  };
  codigoInterno: string;
  convocatoria: IConvocatoriaResponse;
  fechaFin: DateTime;
  fechaFinDefinitiva: DateTime;
  fechaInicio: DateTime;
  finalidad: ITipoFinalidadResponse;
  importeTotalGastos: number;
  importeTotalIngresos: number;
  iva: number;
  ivaDeducible: boolean;
  modeloEjecucion: IModeloEjecucionResponse;
  numeroDocumentoResponsable: string;
  responsable: {
    id: string;
    nombreCompleto: string;
  },
  sgeId: string;
  tipoFormularioSolicitud: string;
  tipoEntidadSGI: TipoEntidadSGI;
  titulo: I18nFieldValueRequest[] | I18nFieldValueResponse[];
  unidadGestion: IUnidadGestion
}

enum TipoEntidadSGI {
  GRUPO = 'GRUPO',
  PROYECTO = 'PROYECTO',
}

enum FormlyFields {
  CAUSA_EXENCION = 'causaExencion',
  CODIGO_INTERNO = 'codigoInterno',
  CONVOCATORIA_ID = 'convocatoriaId',
  FINALIDAD = 'finalidad',
  IMPORTE_TOTAL_GASTOS = 'importeTotalGastos',
  IMPORTE_TOTAL_INGRESOS = 'importeTotalIngresos',
  IVA_DEDUCIBLE = 'ivaDeducible',
  MODELO_EJECUCION = 'modeloEjecucion',
  NUMERO_DOCUMENTO_RESPONSABLE = 'numeroDocumentoResponsable',
  POR_IVA = 'porIva',
  RESPONSABLE_REF = 'responsableRef',
  TIPO_FINALIDAD = 'tipoFinalidad',
  TIPO_FORMULARIO_SOLICITUD = 'tipoFormularioSolicitud',
  UNIDAD_GESTION = 'unidadGestion',
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
    private readonly convocatoriaService: ConvocatoriaService,
    private readonly proyectoService: ProyectoService,
    private readonly proyectoSgeService: ProyectoSgeService,
    private readonly solicitudProyectoService: SolicitudProyectoService,
    private readonly solicitudService: SolicitudService,
    private readonly grupoService: GrupoService,
    private readonly personaService: PersonaService,
    private readonly unidadGestionService: UnidadGestionService
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

    const tipoEntidadSGI = !!grupo ? TipoEntidadSGI.GRUPO : TipoEntidadSGI.PROYECTO;
    if (tipoEntidadSGI === TipoEntidadSGI.PROYECTO) {
      load$ = this.fillProyectoData(formly$, proyectoSgiId, proyectoSgeId);
    } else {
      load$ = this.fillGrupoData(formly$, grupo);
    }

    switch (action) {
      case ACTION_MODAL_MODE.EDIT:
      case ACTION_MODAL_MODE.VIEW:
      case ACTION_MODAL_MODE.SELECT_AND_NOTIFY:
        load$ = load$.pipe(
          switchMap((formlyData) => {
            return this.fillProyectoSgeFormlyModelById(proyectoSgeId, proyectoSgiId, formlyData);
          })
        );
        break;
      case ACTION_MODAL_MODE.NEW:
        break;
    }

    return load$.pipe(
      tap(formlyData => {
        formlyData.data.formlyDataSGI.tipoEntidadSGI = tipoEntidadSGI;
        this.options.formState.mainModel = formlyData.data;
        this.parseModel();
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
          switchMap((proyecto) => forkJoin({
            convocatoria: this.formlyContainsField(FormlyFields.CONVOCATORIA_ID) ? this.getConvocatoria(proyecto.convocatoriaId) : of(null),
            importeTotalGastos: this.formlyContainsField(FormlyFields.IMPORTE_TOTAL_GASTOS) ? this.getImporteTotalGastos(proyecto) : of(null),
            responsable: this.formlyContainsAnyField([FormlyFields.RESPONSABLE_REF, FormlyFields.NUMERO_DOCUMENTO_RESPONSABLE]) ? this.getResponsable(proyecto.id) : of(null),
            unidadGestion: this.formlyContainsField(FormlyFields.UNIDAD_GESTION) ? this.getUnidadGesion(proyecto.unidadGestion?.id) : of(null),
          }).pipe(
            map(({ convocatoria, importeTotalGastos, responsable, unidadGestion }) => {
              formlyData.data.formlyDataSGI = this.buildProyectoFormlyModel(convocatoria, importeTotalGastos, proyecto, proyectoSgeId, responsable, unidadGestion);
              return formlyData;
            })
          ))
        )
      })
    );
  }

  private buildProyectoFormlyModel(
    convocatoria: IConvocatoria,
    importeTotalGastos: number,
    proyecto: IProyecto,
    proyectoSgeId: string,
    responsable: IPersona,
    unidadGestion: IUnidadGestion,
  ): Partial<IFormlyDataSGI> {
    const proyectoModel: Partial<IFormlyDataSGI> = {
      id: proyecto.id,
      fechaInicio: proyecto.fechaInicio,
      fechaFin: proyecto.fechaFin,
      anio: proyecto.anio,
      fechaFinDefinitiva: proyecto.fechaFinDefinitiva,
      titulo: I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(proyecto.titulo),
      sgeId: proyectoSgeId
    };

    if (this.formlyContainsField(FormlyFields.CAUSA_EXENCION) && !!proyecto.causaExencion) {
      proyectoModel.causaExencion = {
        id: proyecto.causaExencion,
        descripcion: this.translate.instant(CAUSA_EXENCION_MAP.get(proyecto.causaExencion)),
      };
    }

    if (this.formlyContainsField(FormlyFields.CODIGO_INTERNO)) {
      proyectoModel.codigoInterno = proyecto.codigoInterno;
    }

    if (this.formlyContainsField(FormlyFields.CONVOCATORIA_ID)) {
      proyectoModel.convocatoria = CONVOCATORIA_RESPONSE_CONVERTER.fromTarget(convocatoria);
    }

    if (this.formlyContainsField(FormlyFields.TIPO_FORMULARIO_SOLICITUD) && !!convocatoria?.formularioSolicitud) {
      proyectoModel.tipoFormularioSolicitud = this.translate.instant(FORMULARIO_SOLICITUD_MAP.get(convocatoria.formularioSolicitud));
    }

    if (this.formlyContainsAnyField([FormlyFields.FINALIDAD, FormlyFields.TIPO_FINALIDAD])) {
      proyectoModel.finalidad = TIPO_FINALIDAD_RESPONSE_CONVERTER.fromTarget(proyecto.finalidad);
    }

    if (this.formlyContainsField(FormlyFields.IMPORTE_TOTAL_GASTOS)) {
      proyectoModel.importeTotalGastos = importeTotalGastos;
    }

    if (this.formlyContainsField(FormlyFields.IMPORTE_TOTAL_INGRESOS)) {
      proyectoModel.importeTotalIngresos = proyectoModel.importeTotalGastos;
    }

    if (this.formlyContainsField(FormlyFields.IVA_DEDUCIBLE)) {
      proyectoModel.ivaDeducible = proyecto.ivaDeducible;
    }

    if (this.formlyContainsField(FormlyFields.MODELO_EJECUCION)) {
      proyectoModel.modeloEjecucion = MODELO_EJECUCION_RESPONSE_CONVERTER.fromTarget(proyecto.modeloEjecucion);
    }

    if (this.formlyContainsField(FormlyFields.POR_IVA)) {
      proyectoModel.iva = proyecto.iva?.iva;
    }

    if (responsable != null) {
      if (this.formlyContainsField(FormlyFields.RESPONSABLE_REF)) {
        proyectoModel.responsable = {
          id: responsable.id,
          nombreCompleto: (responsable?.nombre ?? '') + ' ' + (responsable?.apellidos ?? '')
        };
      } else if (this.formlyContainsField(FormlyFields.NUMERO_DOCUMENTO_RESPONSABLE)) {
        proyectoModel.numeroDocumentoResponsable = responsable.id;
      }
    }

    if (this.formlyContainsField(FormlyFields.UNIDAD_GESTION)) {
      proyectoModel.unidadGestion = UNIDAD_GESTION_REQUEST_CONVERTER.toTarget(unidadGestion);
    }

    return proyectoModel;
  }

  private getResponsable(proyectoId: number): Observable<IPersona> {
    return this.proyectoService.findAllProyectoResponsablesEconomicos(proyectoId).pipe(
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
      switchMap((responsable: IResponsable) => {
        if (!responsable?.persona) {
          return this.getCurrentMiembroEquipoWithRolOrdenPrimario(proyectoId);
        }
        return of(responsable);
      }),
      switchMap((responsable) => responsable ? this.personaService.findById(responsable.persona.id) : of(null))
    );
  }

  private getConvocatoria(convocatoriaId: number): Observable<IConvocatoria> {
    if (!convocatoriaId) {
      return of(null);
    }

    return this.convocatoriaService.findById(convocatoriaId);
  }

  private getConvocatoriaGrupoBySolicitudId(solicitudId: number): Observable<IConvocatoria> {
    if (!solicitudId) {
      return of(null);
    }

    return this.solicitudService.findById(solicitudId).pipe(
      switchMap((solicitud) => this.getConvocatoria(solicitud?.convocatoriaId))
    );
  }



  private getImporteTotalGastos(proyecto: IProyecto): Observable<number> {
    if (proyecto.importePresupuesto || !proyecto.solicitudId) {
      return of(proyecto.importePresupuesto);
    }

    return this.getImportePresupuestoBySolicitudProyecto(proyecto.solicitudId).pipe(
      switchMap(importeBySolicitudProyect => !importeBySolicitudProyect ? this.getImportePresupuestoBySolicitudProyectoGastos(proyecto.solicitudId) : of(importeBySolicitudProyect))
    );
  }

  private getUnidadGesion(unidadGestionId: number): Observable<IUnidadGestion> {
    if (!unidadGestionId) {
      return of(null);
    }

    return this.unidadGestionService.findById(unidadGestionId);
  }

  private fillGrupoData(
    load$: Observable<FormlyFieldConfig[]>,
    grupo: IGrupo
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
      switchMap((formlyData) => forkJoin({
        convocatoria: this.formlyContainsField(FormlyFields.CONVOCATORIA_ID) ? this.getConvocatoriaGrupoBySolicitudId(grupo.solicitud?.id) : of(null),
        responsable: this.formlyContainsAnyField([FormlyFields.RESPONSABLE_REF, FormlyFields.NUMERO_DOCUMENTO_RESPONSABLE]) ? this.getResponsableGrupo(grupo.id) : of(null),
      }).pipe(
        map(({ convocatoria, responsable }) => {
          formlyData.data.formlyDataSGI = this.buildGrupoFormlyModel(convocatoria, grupo, responsable);
          return formlyData;
        })
      ))
    );
  }

  private buildGrupoFormlyModel(
    convocatoria: IConvocatoria,
    grupo: IGrupo,
    responsable: IPersona,
  ): Partial<IFormlyDataSGI> {
    const grupoModel: Partial<IFormlyDataSGI> = {
      id: grupo.id,
      fechaInicio: grupo.fechaInicio,
      fechaFin: grupo.fechaFin,
      titulo: I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(grupo.nombre),
      sgeId: grupo.proyectoSge?.id
    };

    if (this.formlyContainsField(FormlyFields.CONVOCATORIA_ID)) {
      grupoModel.convocatoria = CONVOCATORIA_RESPONSE_CONVERTER.fromTarget(convocatoria);
    }

    if (this.formlyContainsField(FormlyFields.TIPO_FORMULARIO_SOLICITUD) && !!convocatoria?.formularioSolicitud) {
      grupoModel.tipoFormularioSolicitud = this.translate.instant(FORMULARIO_SOLICITUD_MAP.get(convocatoria.formularioSolicitud));
    }

    if (responsable != null) {
      if (this.formlyContainsField(FormlyFields.RESPONSABLE_REF)) {
        grupoModel.responsable = {
          id: responsable.id,
          nombreCompleto: (responsable?.nombre ?? '') + ' ' + (responsable?.apellidos ?? '')
        };
      } else if (this.formlyContainsField(FormlyFields.NUMERO_DOCUMENTO_RESPONSABLE)) {
        grupoModel.numeroDocumentoResponsable = responsable.id;
      }
    }

    return grupoModel;
  }


  private getImportePresupuestoBySolicitudProyecto(solicitudId: number): Observable<number> {
    return this.solicitudService.findById(solicitudId).pipe(
      switchMap(solicitud => {
        if (!!!solicitud || solicitud.formularioSolicitud !== FormularioSolicitud.PROYECTO) {
          return of(null);
        }

        return this.solicitudProyectoService.findById(solicitudId).pipe(
          map((solicitudProyecto) => solicitudProyecto.importePresupuestado)
        );
      })
    );
  }

  private getImportePresupuestoBySolicitudProyectoGastos(solicitudId: number): Observable<number> {
    return this.solicitudService.findAllSolicitudProyectoPresupuesto(solicitudId).pipe(
      map(response => {
        return response.items.reduce(
          (total, solicitudProyectoConceptoGasto) => total + solicitudProyectoConceptoGasto.importePresupuestado, 0);
      })
    );
  }

  private getResponsableGrupo(grupoId: number): Observable<IPersona> {
    return this.grupoService.findResponsablesEconomicos(grupoId).pipe(
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
          return this.getCurrentMiembroEquipoWithRolOrdenPrimarioGrupo(grupoId);
        }
        return of(result);
      }),
      switchMap((responsable) => responsable ? this.personaService.findById(responsable.persona.id) : of(null))
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

  private formlyContainsAnyField(formlyFields: FormlyFields[]): boolean {
    return this.formlyFieldKeys.some(field => formlyFields.some(formlyField => formlyField == field));
  }

}

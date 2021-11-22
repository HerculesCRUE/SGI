import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { IProyectoContexto } from '@core/models/csp/proyecto-contexto';
import { FormFragment } from '@core/services/action-service';
import { ContextoProyectoService } from '@core/services/csp/contexto-proyecto.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { ProyectoContextoModalData } from '../../modals/proyecto-contexto-modal/proyecto-contexto-modal.component';
import { AreaTematicaListado } from './proyecto-contexto.component';

export interface AreaTematicaProyectoData {
  root: IAreaTematica;
  areaTematicaProyecto: IAreaTematica;
  areasTematicasConvocatoria: IAreaTematica[];
}

export class ProyectoContextoFragment extends FormFragment<IProyectoContexto>{
  proyectoContexto: IProyectoContexto;
  areasTematicas$ = new BehaviorSubject<AreaTematicaProyectoData[]>([]);
  areaTematica: IAreaTematica;
  ocultarTable: boolean;

  constructor(
    key: number,
    private readonly logger: NGXLogger,
    private contextoProyectoService: ContextoProyectoService,
    private convocatoriaService: ConvocatoriaService,
    private convocatoriaId: number,
    private readonly: boolean,
    public isVisor: boolean,
  ) {
    super(key, true);
    this.setComplete(true);
    this.proyectoContexto = {} as IProyectoContexto;
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      objetivos: new FormControl('', [Validators.maxLength(2000)]),
      intereses: new FormControl('', [Validators.maxLength(2000)]),
      resultados_previstos: new FormControl('', [Validators.maxLength(2000)]),
      propiedadResultados: new FormControl(''),
    });

    if (this.readonly) {
      form.disable();
    }
    if (this.isVisor) {
      form.disable();
    }

    return form;
  }

  protected buildPatch(value: IProyectoContexto): { [key: string]: any; } {
    this.proyectoContexto = value;
    const result = {
      proyectoId: value.proyectoId,
      objetivos: value.objetivos,
      intereses: value.intereses,
      resultados_previstos: value.resultadosPrevistos,
      propiedadResultados: value.propiedadResultados
    };
    return result;
  }

  protected initializer(key: number): Observable<IProyectoContexto> {
    return this.contextoProyectoService.findById(key).pipe(
      map(proyectoContexto => {
        return proyectoContexto ?? {} as IProyectoContexto;
      }),
      switchMap(proyectoContexto => {
        if (this.convocatoriaId) {
          return this.convocatoriaService.findAreaTematicas(this.convocatoriaId).pipe(
            map((results) => {
              let nodes;
              if (results.total > 0) {
                nodes = results.items.map(convocatoriaAreaTematica => {
                  const area: AreaTematicaProyectoData = {
                    root: this.getFirstLevelAreaTematica(convocatoriaAreaTematica.areaTematica),
                    areasTematicasConvocatoria: results.items.map(areaConvocatoria => areaConvocatoria.areaTematica),
                    areaTematicaProyecto: proyectoContexto.areaTematica
                  };
                  return area;
                });
              } else if (proyectoContexto.areaTematica) {
                nodes = [{
                  root: this.getFirstLevelAreaTematica(proyectoContexto.areaTematica),
                  areasTematicasConvocatoria: null,
                  areaTematicaProyecto: proyectoContexto.areaTematica
                }];
              }

              this.areasTematicas$.next(nodes);
              return results;
            }),
            switchMap(() => of(proyectoContexto))
          );
        } else {
          return of(proyectoContexto);
        }
      }),
      catchError(error => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  getFirstLevelAreaTematica(areaTematica: IAreaTematica): IAreaTematica {
    if (areaTematica?.padre) {
      return this.getFirstLevelAreaTematica(areaTematica.padre);
    }
    return areaTematica;
  }

  getValue(): IProyectoContexto {
    const form = this.getFormGroup().value;
    this.proyectoContexto.objetivos = form.objetivos;
    this.proyectoContexto.intereses = form.intereses;
    this.proyectoContexto.resultadosPrevistos = form.resultados_previstos;
    this.proyectoContexto.propiedadResultados = form.propiedadResultados;
    this.proyectoContexto.areaTematica = this.areaTematica;
    return this.proyectoContexto;
  }

  saveOrUpdate(): Observable<number> {
    const proyectoContextoDatos = this.getValue();
    const observable$ = this.proyectoContexto.id ? this.update(proyectoContextoDatos) :
      this.create(proyectoContextoDatos);
    return observable$.pipe(
      map(value => {
        this.setChanges(false);
        this.proyectoContexto = value;
        this.refreshInitialState(true);
        return this.proyectoContexto.id;
      })
    );
  }

  private create(proyectoContexto: IProyectoContexto): Observable<IProyectoContexto> {
    proyectoContexto.proyectoId = this.getKey() as number;
    return this.contextoProyectoService.create(proyectoContexto);
  }

  private update(proyectoContexto: IProyectoContexto): Observable<IProyectoContexto> {
    return this.contextoProyectoService.update(proyectoContexto.proyectoId, proyectoContexto);
  }

  updateListadoAreaTematica(result: ProyectoContextoModalData): void {
    this.areaTematica = result?.areaTematicaProyecto;

    if (this.areasTematicas$.value?.length > 0) {
      this.areasTematicas$.value[0].areaTematicaProyecto = result.areaTematicaProyecto;
      this.areasTematicas$.next(this.areasTematicas$.value);
    } else {
      this.areasTematicas$.next([{
        root: result.padre,
        areaTematicaProyecto: result.areaTematicaProyecto,
        areasTematicasConvocatoria: result.areasTematicasConvocatoria
      } as AreaTematicaProyectoData]);
    }
    this.setChanges(true);
  }

  deleteAreaTematicaListado(): void {
    this.proyectoContexto.areaTematica = null;
    if (this.areasTematicas$.value[0].areasTematicasConvocatoria) {
      this.areasTematicas$.value[0].areaTematicaProyecto = null;
      this.areasTematicas$.next(this.areasTematicas$.value);
    } else {
      this.areasTematicas$.next([]);
    }
    this.setChanges(true);
  }
}

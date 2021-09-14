import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { IProyectoContexto } from '@core/models/csp/proyecto-contexto';
import { FormFragment } from '@core/services/action-service';
import { ContextoProyectoService } from '@core/services/csp/contexto-proyecto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

export interface AreaTematicaProyectoData {
  root: IAreaTematica;
  areaTematica: IAreaTematica;
  areaTematicaConvocatoria: IAreaTematica;
}

export class ProyectoContextoFragment extends FormFragment<IProyectoContexto>{

  private proyectoContexto: IProyectoContexto;
  areasTematicas$ = new BehaviorSubject<AreaTematicaProyectoData[]>([]);

  private areaTematica: IAreaTematica;
  ocultarTable: boolean;

  constructor(
    key: number,
    private readonly logger: NGXLogger,
    private contextoProyectoService: ContextoProyectoService,
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
      switchMap((proyectoContexto) => {

        let root: IAreaTematica;
        if (proyectoContexto?.areaTematicaConvocatoria?.id) {
          root = this.getFirstLevelAreaTematica(proyectoContexto.areaTematicaConvocatoria);
        } else if (proyectoContexto?.areaTematica?.id) {
          root = this.getFirstLevelAreaTematica(proyectoContexto.areaTematica);
        }

        const area: AreaTematicaProyectoData = {
          root,
          areaTematica: proyectoContexto.areaTematica,
          areaTematicaConvocatoria: proyectoContexto.areaTematicaConvocatoria
        };

        this.areasTematicas$.next([area]);

        return of(proyectoContexto);
      }),
      catchError(error => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  getFirstLevelAreaTematica(areaTematica: IAreaTematica): IAreaTematica {
    if (areaTematica.padre) {
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

  updateAreaTematica(data: AreaTematicaProyectoData) {
    this.areaTematica = data.areaTematica;
    const wrapper = new StatusWrapper<AreaTematicaProyectoData>(data);
    const current = this.areasTematicas$.value;
    wrapper.setEdited();
    this.areasTematicas$[0] = wrapper.value;
    this.areasTematicas$.next(current);
    this.setChanges(true);
  }

  addAreaTematica(data: AreaTematicaProyectoData) {
    this.areaTematica = data.areaTematica;
    const wrapper = new StatusWrapper<AreaTematicaProyectoData>(data);
    wrapper.setCreated();
    const current = this.areasTematicas$.value;
    current.push(wrapper.value);
    this.areasTematicas$.next(current);
    this.setChanges(true);
    this.setErrors(false);
  }

}

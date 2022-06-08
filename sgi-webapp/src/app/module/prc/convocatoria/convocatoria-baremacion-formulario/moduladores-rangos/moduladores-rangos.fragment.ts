import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { IModulador, Tipo } from '@core/models/prc/modulador';
import { IRango, TipoRango } from '@core/models/prc/rango';
import { IAreaConocimiento } from '@core/models/sgo/area-conocimiento';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaBaremacionService } from '@core/services/prc/convocatoria-baremacion/convocatoria-baremacion.service';
import { ModuladorService } from '@core/services/prc/modulador/modulador.service';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, forkJoin, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export interface ModuladorListado {
  id: number;
  area: IAreaConocimiento;
  valores: FormGroup;
}

export interface ModuladoresFormData {
  moduladoresAutoresArea: IModulador[];
  moduladoresAreas: IModulador[];
}

export class ModuladoresRangosFragment extends FormFragment<ModuladoresFormData> {

  private initialModuladoresAutoresAreas: IModulador[];
  private moduladoresAutoresArea$ = new BehaviorSubject<ModuladorListado[]>([]);

  private initialModuladoresAreas: IModulador[];
  private moduladoresAreas$ = new BehaviorSubject<ModuladorListado[]>([]);

  private rangosCuantiaCostesIndirectos$ = new BehaviorSubject<StatusWrapper<IRango>[]>([]);
  private rangosCuantiaContratos$ = new BehaviorSubject<StatusWrapper<IRango>[]>([]);
  private rangosIngresosLicencia$ = new BehaviorSubject<StatusWrapper<IRango>[]>([]);

  hasCostesIndirectosTipoRango$ = new BehaviorSubject<boolean>(false);

  constructor(
    key: number,
    private convocatoriaBaremacion: IConvocatoriaBaremacion,
    readonly isEditPerm: boolean,
    private readonly convocatoriaBaremacionService: ConvocatoriaBaremacionService,
    private readonly areaConocimientoService: AreaConocimientoService,
    private readonly moduladorService: ModuladorService
  ) {
    super(key, true);
    this.buildFormGroup();
  }

  getModuladores$(tipo: Tipo): Observable<ModuladorListado[]> {
    let moduladores$: BehaviorSubject<ModuladorListado[]>;
    switch (tipo) {
      case Tipo.AREAS:
        moduladores$ = this.moduladoresAreas$;
        break;
      case Tipo.NUMERO_AUTORES:
        moduladores$ = this.moduladoresAutoresArea$;
        break;
      default:
        moduladores$ = new BehaviorSubject<ModuladorListado[]>([]);
    }

    return moduladores$.asObservable();
  }

  getRangos$(tipo: TipoRango): Observable<StatusWrapper<IRango>[]> {
    let rangos$: BehaviorSubject<StatusWrapper<IRango>[]>;
    switch (tipo) {
      case TipoRango.CUANTIA_CONTRATOS:
        rangos$ = this.rangosCuantiaContratos$;
        break;
      case TipoRango.CUANTIA_COSTES_INDIRECTOS:
        rangos$ = this.rangosCuantiaCostesIndirectos$;
        break;
      case TipoRango.LICENCIA:
        rangos$ = this.rangosIngresosLicencia$;
        break;
      default:
        rangos$ = new BehaviorSubject<StatusWrapper<IRango>[]>([]);
    }

    return rangos$.asObservable();
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      moduladoresNumAutores: new FormArray([]),
      moduladoresAreas: new FormArray([])
    });

    if (this.isEdit() && !this.isEditPerm) {
      formGroup.disable();
    }

    return formGroup;
  }


  protected buildPatch(): { [key: string]: any; } {
    return {};
  }

  protected initializer(): Observable<any> {
    const convocatoriaBaremacionId = this.getKey() as number;
    return forkJoin({
      moduladoresAreas: this.initializeModuladoresAreas$(convocatoriaBaremacionId),
      moduladoresAutoresArea: this.initializeModuladoresAutoresArea$(convocatoriaBaremacionId),
      rangosCuantiaCostesIndirectos: this.initializeRangosCuantiaCostesIndirectos$(convocatoriaBaremacionId),
      rangosCuantiaContratos: this.initializeRangosCuantiaContratos$(convocatoriaBaremacionId),
      rangosIngresosLicencia: this.initializeRangosIngresosLicencia$(convocatoriaBaremacionId)
    }).pipe(
      tap(({ moduladoresAreas }) => this.moduladoresAreas$.next(moduladoresAreas)),
      tap(({ moduladoresAutoresArea }) => this.moduladoresAutoresArea$.next(moduladoresAutoresArea)),
      tap(({ rangosCuantiaCostesIndirectos }) => this.rangosCuantiaCostesIndirectos$.next(rangosCuantiaCostesIndirectos)),
      tap(({ rangosCuantiaContratos }) => this.rangosCuantiaContratos$.next(rangosCuantiaContratos)),
      tap(({ rangosIngresosLicencia }) => this.rangosIngresosLicencia$.next(rangosIngresosLicencia))
    );
  }

  getValue(): ModuladoresFormData {
    const moduladoresAreas: IModulador[] = this.moduladoresAreas$.value.map(moduladorValues => {
      return {
        id: moduladorValues.id,
        area: moduladorValues.area,
        valor1: moduladorValues.valores.controls.valor1.value,
        valor2: null,
        valor3: null,
        valor4: null,
        valor5: null,
        convocatoriaBaremacion: this.convocatoriaBaremacion,
        tipo: Tipo.AREAS
      };
    });

    const moduladoresAutoresArea: IModulador[] = this.moduladoresAutoresArea$.value.map(moduladorValues => {
      return {
        id: moduladorValues.id,
        area: moduladorValues.area,
        valor1: moduladorValues.valores.controls.valor1.value,
        valor2: moduladorValues.valores.controls.valor2.value,
        valor3: moduladorValues.valores.controls.valor3.value,
        valor4: moduladorValues.valores.controls.valor4.value,
        valor5: moduladorValues.valores.controls.valor5.value,
        convocatoriaBaremacion: this.convocatoriaBaremacion,
        tipo: Tipo.NUMERO_AUTORES
      };
    });

    return {
      moduladoresAreas,
      moduladoresAutoresArea
    };
  }

  checkHasChanges(modulador: IModulador): boolean {
    const initialModulador = modulador.tipo === Tipo.AREAS
      ? this.initialModuladoresAreas.find(m => modulador.area.id === m?.area.id)
      : this.initialModuladoresAutoresAreas.find(m => modulador.area.id === m?.area.id);

    return initialModulador?.valor1 !== modulador.valor1
      || initialModulador?.valor2 !== modulador.valor2
      || initialModulador?.valor3 !== modulador.valor3
      || initialModulador?.valor4 !== modulador.valor4
      || initialModulador?.valor5 !== modulador.valor5;
  }

  saveOrUpdate(): Observable<string | number | void> {
    return merge(
      this.saveOrUpdateModuladores(),
      this.updateRangos(TipoRango.CUANTIA_CONTRATOS),
      this.updateRangos(TipoRango.CUANTIA_COSTES_INDIRECTOS),
      this.updateRangos(TipoRango.LICENCIA),
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  addRango(element: IRango, tipo: TipoRango) {
    const rangos$ = this.getRangosTipo$(tipo);
    const wrapper = new StatusWrapper<IRango>(element);
    wrapper.setCreated();
    const current = rangos$.value;
    current.push(wrapper);
    rangos$.next(current);
    this.setChanges(true);
  }

  updateRango(wrapper: StatusWrapper<IRango>, tipo: TipoRango): void {
    const rangos$ = this.getRangosTipo$(tipo);
    const current = rangos$.value;
    const index = current.findIndex(value => value.value === wrapper.value);
    if (index >= 0) {
      wrapper.setEdited();
      rangos$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  deleteRango(wrapper: StatusWrapper<IRango>, tipo: TipoRango) {
    const rangos$ = this.getRangosTipo$(tipo);
    const current = rangos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      rangos$.next(current);
      this.setChanges(true);
    }
  }

  private saveOrUpdateModuladores(): Observable<void> {
    const formData = this.getValue();
    const moduladoresAreasUpdated = formData.moduladoresAreas.filter(modulador => this.checkHasChanges(modulador));
    const moduladoresAutoresAreaUpdated = formData.moduladoresAutoresArea.filter(modulador => this.checkHasChanges(modulador));

    const moduladoresUpdated = [...moduladoresAreasUpdated, ...moduladoresAutoresAreaUpdated];

    if (moduladoresUpdated.length === 0) {
      return of(void 0);
    }

    return from(moduladoresUpdated).pipe(
      mergeMap(modulador => !modulador.id ? this.saveModulador(modulador) : this.updateModulador(modulador)),
      takeLast(1)
    );
  }

  private saveModulador(modulador: IModulador): Observable<void> {
    return this.moduladorService.create(modulador).pipe(
      map(moduladorCreated => {
        const initialModuladores = modulador.tipo === Tipo.AREAS ? this.initialModuladoresAreas : this.initialModuladoresAutoresAreas;
        const currentModuladores = modulador.tipo === Tipo.AREAS ? this.moduladoresAreas$.value : this.moduladoresAutoresArea$.value;
        const indexCurrent = currentModuladores.findIndex(m => modulador.area.id === m?.area.id);
        initialModuladores.push(moduladorCreated);
        currentModuladores[indexCurrent].id = moduladorCreated.id;
      })
    );
  }

  private updateModulador(modulador: IModulador): Observable<void> {
    return this.moduladorService.update(modulador.id, modulador).pipe(
      map(moduladorUpdated => {
        const initialModuladores = modulador.tipo === Tipo.AREAS ? this.initialModuladoresAreas : this.initialModuladoresAutoresAreas;
        const indexInitial = initialModuladores.findIndex(m => modulador.area.id === m?.area.id);
        initialModuladores[indexInitial] = moduladorUpdated;

      })
    );
  }

  private updateRangos(tipo: TipoRango): Observable<void> {
    const rangos$ = this.getRangosTipo$(tipo);
    if (!rangos$.value.some((wrapper) => wrapper.touched)) {
      return of(void 0);
    }

    return this.convocatoriaBaremacionService.updateRangos(
      this.getKey() as number,
      rangos$.value.map(wrapper => {
        wrapper.value.convocatoriaBaremacion = this.convocatoriaBaremacion;
        wrapper.value.tipoRango = tipo;
        return wrapper.value;
      }),
      tipo
    ).pipe(
      map(results => {
        rangos$.next(results.map(rango => new StatusWrapper<IRango>(rango)));
      })
    );
  }

  private getRangosTipo$(tipo: TipoRango): BehaviorSubject<StatusWrapper<IRango>[]> {
    let rangos$: BehaviorSubject<StatusWrapper<IRango>[]>;

    switch (tipo) {
      case TipoRango.CUANTIA_CONTRATOS:
        rangos$ = this.rangosCuantiaContratos$;
        break;
      case TipoRango.CUANTIA_COSTES_INDIRECTOS:
        rangos$ = this.rangosCuantiaCostesIndirectos$;
        break;
      case TipoRango.LICENCIA:
        rangos$ = this.rangosIngresosLicencia$;
        break;
      default:
        rangos$ = new BehaviorSubject<StatusWrapper<IRango>[]>([]);
    }
    return rangos$;
  }

  private fillFormGroupModuladorArea(moduladores: IModulador[], areaRef: string, tipo: Tipo): FormGroup {
    const moduladorArea = moduladores.find(modulador => modulador.area.id === areaRef);

    let moduladorAreaFormGroup: FormGroup;

    if (tipo === Tipo.AREAS) {
      moduladorAreaFormGroup = new FormGroup({
        valor1: new FormControl(moduladorArea?.valor1 ?? 1, [Validators.required, Validators.min(0)])
      });
    } else {
      moduladorAreaFormGroup = new FormGroup({
        valor1: new FormControl(moduladorArea?.valor1 ?? 1, [Validators.required, Validators.min(0)]),
        valor2: new FormControl(moduladorArea?.valor2 ?? 1, [Validators.required, Validators.min(0)]),
        valor3: new FormControl(moduladorArea?.valor3 ?? 1, [Validators.required, Validators.min(0)]),
        valor4: new FormControl(moduladorArea?.valor4 ?? 1, [Validators.required, Validators.min(0)]),
        valor5: new FormControl(moduladorArea?.valor5 ?? 1, [Validators.required, Validators.min(0)])
      });
    }

    if (this.isEdit && !this.isEditPerm) {
      moduladorAreaFormGroup.disable();
    } else if (!moduladorArea) {
      this.setChanges(true);
    }

    (this.getFormGroup().controls.moduladoresNumAutores as FormArray).push(moduladorAreaFormGroup);
    return moduladorAreaFormGroup;
  }

  private initializeModuladoresAreas$(convocatoriaBaremacionId: number): Observable<ModuladorListado[]> {
    return forkJoin({
      areasConocimiento: this.areaConocimientoService.findAllRamasConocimiento().pipe(
        map(result => result.items)
      ),
      moduladores: this.convocatoriaBaremacionService.findModuladores(convocatoriaBaremacionId, Tipo.AREAS).pipe(
        map(result => result.items)
      )
    }).pipe(
      tap(({ moduladores }) => this.initialModuladoresAutoresAreas = moduladores),
      map(({ areasConocimiento, moduladores }) =>
        areasConocimiento.map(area => {
          const moduladorAreas: ModuladorListado = {
            id: moduladores.find(modulador => modulador.area.id === area.id)?.id,
            area,
            valores: this.fillFormGroupModuladorArea(moduladores, area.id, Tipo.AREAS)
          };

          return moduladorAreas;
        })
      ),
    );
  }

  private initializeModuladoresAutoresArea$(convocatoriaBaremacionId: number): Observable<ModuladorListado[]> {
    return forkJoin({
      areasConocimiento: this.areaConocimientoService.findAllRamasConocimiento().pipe(
        map(result => result.items)
      ),
      moduladores: this.convocatoriaBaremacionService.findModuladores(convocatoriaBaremacionId, Tipo.NUMERO_AUTORES).pipe(
        map(result => result.items)
      )
    }).pipe(
      tap(({ moduladores }) => this.initialModuladoresAreas = moduladores),
      map(({ areasConocimiento, moduladores }) =>
        areasConocimiento.map(area => {
          const moduladorAreaAutores: ModuladorListado = {
            id: moduladores.find(modulador => modulador.area.id === area.id)?.id,
            area,
            valores: this.fillFormGroupModuladorArea(moduladores, area.id, Tipo.NUMERO_AUTORES)
          };

          return moduladorAreaAutores;
        })
      ),
    );
  }

  private initializeRangosCuantiaCostesIndirectos$(convocatoriaBaremacionId: number): Observable<StatusWrapper<IRango>[]> {
    return this.convocatoriaBaremacionService.findRangosTipo(convocatoriaBaremacionId, TipoRango.CUANTIA_COSTES_INDIRECTOS).pipe(
      map(response => response.items.map(rango => new StatusWrapper<IRango>(rango)))
    );
  }

  private initializeRangosCuantiaContratos$(convocatoriaBaremacionId: number): Observable<StatusWrapper<IRango>[]> {
    return this.convocatoriaBaremacionService.findRangosTipo(convocatoriaBaremacionId, TipoRango.CUANTIA_CONTRATOS).pipe(
      map(response => response.items.map(rango => new StatusWrapper<IRango>(rango)))
    );
  }

  private initializeRangosIngresosLicencia$(convocatoriaBaremacionId: number): Observable<StatusWrapper<IRango>[]> {
    return this.convocatoriaBaremacionService.findRangosTipo(convocatoriaBaremacionId, TipoRango.LICENCIA).pipe(
      map(response => response.items.map(rango => new StatusWrapper<IRango>(rango)))
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const formData = this.getValue();
    const hasTouched = this.rangosCuantiaContratos$.value.some((wrapper) => wrapper.touched)
      || this.rangosCuantiaCostesIndirectos$.value.some((wrapper) => wrapper.touched)
      || this.rangosIngresosLicencia$.value.some((wrapper) => wrapper.touched)
      || formData.moduladoresAreas.some(modulador => this.checkHasChanges(modulador))
      || formData.moduladoresAutoresArea.some(modulador => this.checkHasChanges(modulador));
    return !hasTouched;
  }

}

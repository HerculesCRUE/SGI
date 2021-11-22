import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoEquipo } from '@core/models/csp/proyecto-equipo';
import { IInvencion } from '@core/models/pii/invencion';
import { ISectorLicenciado } from '@core/models/pii/sector-licenciado';
import { IRelacion, TipoEntidad } from '@core/models/rel/relacion';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IPersona } from '@core/models/sgp/persona';
import { Fragment } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SectorLicenciadoService } from '@core/services/pii/sector-licenciado/sector-licenciado.service';
import { RelacionService } from '@core/services/rel/relaciones/relacion.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PaisService } from '@core/services/sgo/pais/pais.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap, takeLast, tap, toArray } from 'rxjs/operators';

export interface IContratoAsociadoTableData {
  contrato: IProyecto;
  entidadesFinanciadoras: IEmpresa[];
  investigadorPrincipal: IPersona;
}

export class InvencionContratosFragment extends Fragment {

  private contratosAsociados$ = new BehaviorSubject<IContratoAsociadoTableData[]>([]);
  private selectedContratoAsociado$ = new BehaviorSubject<IContratoAsociadoTableData>(undefined);
  private sectoresLicenciados$ = new BehaviorSubject<StatusWrapper<ISectorLicenciado>[]>([]);
  private sectoresLicenciadosToDelete: StatusWrapper<ISectorLicenciado>[] = [];

  constructor(
    key: number,
    public candEdit: boolean,
    private sectorLicenciadoService: SectorLicenciadoService,
    private relacionService: RelacionService,
    private proyectoService: ProyectoService,
    private empresaService: EmpresaService,
    private personaService: PersonaService,
    private paisService: PaisService,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): Observable<IContratoAsociadoTableData[]> {
    const id = this.getKey() as number;

    return this.relacionService.findInvencionRelaciones(id).pipe(
      map(relaciones => this.convertRelacionArrayToContratoAsociadoTableDataArray(relaciones)),
      tap(relaciones => this.contratosAsociados$.next(relaciones)),
      mergeMap(relaciones => this.fillContratoAsociadoAdditionalData$(relaciones))
    );
  }

  private convertRelacionArrayToContratoAsociadoTableDataArray(relaciones: IRelacion[]): IContratoAsociadoTableData[] {
    return relaciones.map(relacion => ({
      contrato: { id: this.getContratoIdFromRelacion(relacion) } as IProyecto,
      entidadesFinanciadoras: [],
      investigadorPrincipal: undefined
    }));
  }

  private getContratoIdFromRelacion(relacion: IRelacion): number {
    return relacion.tipoEntidadOrigen === TipoEntidad.PROYECTO ? +relacion.entidadOrigen.id : +relacion.entidadDestino.id;
  }

  private fillContratoAsociadoAdditionalData$(contratosAsociados: IContratoAsociadoTableData[]): Observable<IContratoAsociadoTableData[]> {
    return from(contratosAsociados).pipe(
      mergeMap(contratoAsociado => this.fillContratoData$(contratoAsociado)),
      mergeMap(contratoAsociado => this.fillEntidadesFinanciadorasData$(contratoAsociado)),
      mergeMap(contratoAsociado => this.fillInvestigadorPrincipalData$(contratoAsociado)),
      toArray()
    );
  }

  private fillContratoData$(contratoAsociado: IContratoAsociadoTableData): Observable<IContratoAsociadoTableData> {
    return this.proyectoService.findById(contratoAsociado.contrato.id).pipe(
      map(contrato => {
        contratoAsociado.contrato = contrato;
        return contratoAsociado;
      }),
      catchError(() => of(contratoAsociado))
    );
  }

  private fillEntidadesFinanciadorasData$(contratoAsociado: IContratoAsociadoTableData): Observable<IContratoAsociadoTableData> {
    return this.proyectoService.findEntidadesFinanciadoras(contratoAsociado.contrato.id).pipe(
      map(response => {
        contratoAsociado.entidadesFinanciadoras = response.items.map(item => item.empresa);
        return contratoAsociado;
      }),
      mergeMap(contratoAsociadoWithIdsOfEntidadesFinanciadoras =>
        this.fillEntidadFinanciadoraData$(contratoAsociadoWithIdsOfEntidadesFinanciadoras)),
      catchError(() => of(contratoAsociado))
    );
  }

  private fillEntidadFinanciadoraData$(contratoAsociado: IContratoAsociadoTableData): Observable<IContratoAsociadoTableData> {
    return from(contratoAsociado.entidadesFinanciadoras).pipe(
      mergeMap(entidadFinanciadora => this.empresaService.findById(entidadFinanciadora.id)
      ),
      toArray(),
      map(entidadesFinanciadoras => {
        contratoAsociado.entidadesFinanciadoras = entidadesFinanciadoras;
        return contratoAsociado;
      })
    );
  }

  private fillInvestigadorPrincipalData$(contratoAsociado: IContratoAsociadoTableData): Observable<IContratoAsociadoTableData> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('rolProyecto.rolPrincipal', SgiRestFilterOperator.EQUALS, 'true')
    };
    return this.proyectoService.findAllProyectoEquipo(contratoAsociado.contrato.id, options).pipe(
      map(response => {
        contratoAsociado.investigadorPrincipal = this.getFirstInvestigadorPrincipal(response.items);
        return contratoAsociado;
      }),
      mergeMap(contratoAsociadoWithIdOfInvestigadorPrincipal =>
        this.completeInvestigadorPrincipalData$(contratoAsociadoWithIdOfInvestigadorPrincipal)),
      catchError(() => of(contratoAsociado))
    );
  }

  private getFirstInvestigadorPrincipal([firstElement]: IProyectoEquipo[]): IPersona {
    return firstElement.persona;
  }

  private completeInvestigadorPrincipalData$(
    contratoAsociadoToComplete: IContratoAsociadoTableData): Observable<IContratoAsociadoTableData> {
    if (contratoAsociadoToComplete.investigadorPrincipal.id) {
      return this.personaService.findById(contratoAsociadoToComplete.investigadorPrincipal.id).pipe(
        map(investigadorPrincipal => {
          contratoAsociadoToComplete.investigadorPrincipal = investigadorPrincipal;
          return contratoAsociadoToComplete;
        })
      );
    } else {
      return of(contratoAsociadoToComplete);
    }
  }

  addSectorLicenciado(sectorLicenciado: ISectorLicenciado): void {
    sectorLicenciado.invencion = { id: +this.getKey() } as IInvencion;
    sectorLicenciado.contrato = this.selectedContratoAsociado$.value.contrato;
    const wrapped = new StatusWrapper<ISectorLicenciado>(sectorLicenciado);
    wrapped.setCreated();
    const current = this.sectoresLicenciados$.value;
    current.push(wrapped);
    this.sectoresLicenciados$.next(current);
    this.setChanges(true);
  }

  updateSectorLicenciado(updatedSectorLicenciado: ISectorLicenciado, index: number) {
    if (index >= 0) {
      const current = this.sectoresLicenciados$.value;
      const wrapper = current[index];
      if (!wrapper.created) {
        wrapper.setEdited();
      }
      this.sectoresLicenciados$.next(current);
      this.setChanges(true);
    }
  }

  deleteSectorLicenciado(wrapper: StatusWrapper<ISectorLicenciado>): void {
    const current = this.sectoresLicenciados$.value;
    const index = current.findIndex(value => value === wrapper);
    if (index >= 0) {
      if (!wrapper.created) {
        this.sectoresLicenciadosToDelete.push(current[index]);
      }
      current.splice(index, 1);
      this.sectoresLicenciados$.next(current);
      this.setChanges(this.hasFragmentChangesPending());
    }
  }

  hasFragmentChangesPending() {
    return this.sectoresLicenciadosToDelete.length > 0 || this.sectoresLicenciados$.value.some((value) => value.created || value.edited);
  }

  getContratosAsociados$(): Observable<IContratoAsociadoTableData[]> {
    return this.contratosAsociados$.asObservable().pipe(
      tap(contratosAsociados => this.selectFirstContratoAsociadoByDefault(contratosAsociados))
    );
  }

  private selectFirstContratoAsociadoByDefault([firstElement]: IContratoAsociadoTableData[]): void {
    this.selectedContratoAsociado$.next(firstElement);
  }

  notifySelectedContratoAsociado(contratoAsociado: IContratoAsociadoTableData): void {
    this.selectedContratoAsociado$.next(contratoAsociado);
  }

  getSelectedContratoAsociado$(): Observable<IContratoAsociadoTableData> {
    return this.selectedContratoAsociado$.asObservable().pipe(
      filter(contratoAsociado => !!contratoAsociado?.contrato?.id),
      tap(() => this.resetFragmentPendingChanges()),
      tap(contratoAsociado => this.findSectoresLicenciadosByContratoAsociado(contratoAsociado))
    );
  }

  private resetFragmentPendingChanges() {
    this.sectoresLicenciadosToDelete = [];
  }

  private findSectoresLicenciadosByContratoAsociado(contratoAsociado: IContratoAsociadoTableData): void {
    this.subscriptions.push(
      this.sectorLicenciadoService.findByContratoRef(contratoAsociado.contrato.id.toString())
        .pipe(
          map(sectoresLicenciados => sectoresLicenciados.map(sectorLicenciado => new StatusWrapper(sectorLicenciado))),
          switchMap(wrapperList => this.fillSectorLicenciadoAdditionalData$(wrapperList))
        )
        .subscribe(sectoresLicenciados => {
          this.sectoresLicenciados$.next(sectoresLicenciados);
        })
    );
  }

  private fillSectorLicenciadoAdditionalData$(wrapperList: StatusWrapper<ISectorLicenciado>[]): Observable<StatusWrapper<ISectorLicenciado>[]> {
    return from(wrapperList).pipe(
      mergeMap(wrapper => this.fillPaisData$(wrapper)),
      toArray()
    );
  }

  private fillPaisData$(wrapper: StatusWrapper<ISectorLicenciado>): Observable<StatusWrapper<ISectorLicenciado>> {
    return this.paisService.findById(wrapper.value.pais?.id).pipe(
      map(pais => {
        wrapper.value.pais = pais;
        return wrapper;
      }),
      catchError(() => of(wrapper))
    );
  }

  getSectoresLicenciados$(): Observable<StatusWrapper<ISectorLicenciado>[]> {
    return this.sectoresLicenciados$.asObservable();
  }

  hasEditPerm(): boolean {
    return this.candEdit;
  }

  saveOrUpdate(): Observable<string | number | void> {
    return merge(
      this.deleteSectoresLicenciados(),
      this.updateSectoresLicenciados(),
      this.createSectoresLicenciados()
    ).pipe(
      takeLast(1),
      tap(() => {
        this.setChanges(this.hasFragmentChangesPending());
      })
    );
  }

  private deleteSectoresLicenciados(): Observable<void> {
    if (this.sectoresLicenciadosToDelete.length === 0) {
      return of(void 0);
    }

    return from(this.sectoresLicenciadosToDelete).pipe(
      mergeMap(wrapped =>
        this.deleteSectorLicenciadoById(wrapped))
    );
  }

  private deleteSectorLicenciadoById(wrapped: StatusWrapper<ISectorLicenciado>): Observable<void> {
    return this.sectorLicenciadoService.deleteById(wrapped.value).pipe(
      tap(() =>
        this.sectoresLicenciadosToDelete = this.sectoresLicenciadosToDelete.filter(entidadEliminada =>
          entidadEliminada.value.id !== wrapped.value.id
        )
      )
    );
  }

  private updateSectoresLicenciados(): Observable<void> {
    const current = this.sectoresLicenciados$.value;
    return from(current.filter(wrapper => wrapper.edited)).pipe(
      mergeMap((wrapper => {
        return this.sectorLicenciadoService.update(wrapper.value.id, wrapper.value).pipe(
          map((informePatentabilidadResponse) => this.refreshSectoresLicenciadosData(informePatentabilidadResponse, wrapper, current))
        )
      }))
    );
  }

  private createSectoresLicenciados(): Observable<void> {
    const current = this.sectoresLicenciados$.value;
    return from(current.filter(wrapper => wrapper.created)).pipe(
      mergeMap((wrapper => {
        return this.sectorLicenciadoService.create(wrapper.value).pipe(
          map((informePatentabilidadResponse) => this.refreshSectoresLicenciadosData(informePatentabilidadResponse, wrapper, current))
        )
      }))
    );
  }

  private refreshSectoresLicenciadosData(
    sectorLicenciadoResponse: ISectorLicenciado,
    wrapper: StatusWrapper<ISectorLicenciado>,
    current: StatusWrapper<ISectorLicenciado>[]
  ): void {
    this.copyRelatedAttributes(wrapper.value, sectorLicenciadoResponse);
    current[current.findIndex(c => c === wrapper)] = new StatusWrapper<ISectorLicenciado>(sectorLicenciadoResponse);
    this.sectoresLicenciados$.next(current);
  }

  private copyRelatedAttributes(
    source: ISectorLicenciado,
    target: ISectorLicenciado
  ): void {
    target.invencion = source.invencion;
    target.pais = source.pais;
    target.sectorAplicacion = source.sectorAplicacion;
  }
}

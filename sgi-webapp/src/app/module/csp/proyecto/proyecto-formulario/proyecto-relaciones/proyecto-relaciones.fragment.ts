import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IProyecto } from '@core/models/csp/proyecto';
import { IInvencion } from '@core/models/pii/invencion';
import { IRelacion, TIPO_ENTIDAD_HREF_MAP, TipoEntidad } from '@core/models/rel/relacion';
import { IPersona } from '@core/models/sgp/persona';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { RelacionService } from '@core/services/rel/relaciones/relacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiAuthService } from '@sgi/framework/auth';
import { BehaviorSubject, forkJoin, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast, tap, toArray } from 'rxjs/operators';
import { IProyectoListadoData } from '../../proyecto-listado/proyecto-listado.component';

export interface IProyectoRelacionTableData {
  id: number;
  entidadRelacionada: IConvocatoria | IInvencion | IProyecto;
  entidadRelacionadaHref?: string;
  observaciones: string;
  tipoEntidadRelacionada: TipoEntidad;
  entidadConvocanteRef: string;
  codigosSge: string;
  tipoRelacion: TipoRelacion;
}

export enum TipoRelacion {
  HIJO = 'HIJO',
  PADRE = 'PADRE',
}

export class ProyectoRelacionFragment extends Fragment {
  private proyectoRelacionesTableData$ = new BehaviorSubject<StatusWrapper<IProyectoRelacionTableData>[]>([]);
  private proyectoRelacionesTableDataToDelete: StatusWrapper<IProyectoRelacionTableData>[] = [];

  miembrosEquipoProyecto: IPersona[] = [];

  private get proyectoId(): number {
    return this.getKey() as number;
  }

  constructor(
    key: number,
    private proyecto: IProyecto,
    private readonly: boolean,
    private relacionService: RelacionService,
    private convocatoriaService: ConvocatoriaService,
    private invencionService: InvencionService,
    private proyectoService: ProyectoService,
    private sgiAuthService: SgiAuthService,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void | Observable<any> {
    if (this.proyectoId) {
      return this.relacionService.findProyectoRelaciones(this.proyectoId).pipe(
        switchMap(relaciones => from(relaciones).pipe(
          map(relacion => new StatusWrapper(this.createProyectoRelacionTableDataFromRelacion(relacion))),
          mergeMap(relacionWrapper => this.fillEntidadRelacionada$(relacionWrapper), 100),
          toArray()
        )),
        tap(relacionesWrapped => this.proyectoRelacionesTableData$.next(relacionesWrapped))
      );
    }
  }

  private createProyectoRelacionTableDataFromRelacion(relacion: IRelacion): IProyectoRelacionTableData {
    let data: IProyectoRelacionTableData = {
      id: relacion.id,
      entidadRelacionada: null,
      observaciones: relacion.observaciones,
      tipoEntidadRelacionada: null,
      entidadConvocanteRef: '',
      codigosSge: '',
      tipoRelacion: null
    };

    if (this.isEntidadOrigenRelatedEntity(relacion, this.proyectoId)) {
      data.entidadRelacionada = relacion.entidadOrigen;
      data.tipoEntidadRelacionada = relacion.tipoEntidadOrigen;
    } else {
      data.entidadRelacionada = relacion.entidadDestino;
      data.tipoEntidadRelacionada = relacion.tipoEntidadDestino;
    }

    return data;
  }

  /**
   * Comprueba si la entidad origen de la relacion es la entidad relacionada con el proyecto actual
   * 
   * @param relacion La relacion
   * @param proyectoId Id del proyecto actual
   * @returns True si la entidad origen de la relacion es la entidad relacionada con el proyecto actual, false si la entidad origen es el proyecto actual
   */
  private isEntidadOrigenRelatedEntity(relacion: IRelacion, proyectoId: number): boolean {
    return relacion.entidadOrigen.id !== proyectoId ||
      relacion.entidadOrigen.id === proyectoId && relacion.tipoEntidadOrigen !== TipoEntidad.PROYECTO;
  }

  private fillEntidadRelacionada$(
    wrapper: StatusWrapper<IProyectoRelacionTableData>): Observable<StatusWrapper<IProyectoRelacionTableData>> {
    const tipoEntidad = wrapper.value.tipoEntidadRelacionada;
    switch (tipoEntidad) {
      case TipoEntidad.CONVOCATORIA:
        return this.convocatoriaService.findById(wrapper.value.entidadRelacionada.id).pipe(
          map((convocatoria) => {
            wrapper.value.entidadRelacionada = convocatoria;
            wrapper.value.entidadRelacionadaHref = this.createEntidadRelacionadaHref(convocatoria.id, tipoEntidad);
            return wrapper;
          }),
          catchError(() => of(wrapper))
        );
      case TipoEntidad.INVENCION:
        return this.invencionService.findById(wrapper.value.entidadRelacionada.id).pipe(
          map((invencion) => {
            wrapper.value.entidadRelacionada = invencion;
            wrapper.value.entidadRelacionadaHref = this.createEntidadRelacionadaHref(invencion.id, tipoEntidad);
            return wrapper;
          }),
          catchError(() => of(wrapper))
        );
      case TipoEntidad.PROYECTO:
        return forkJoin({
          proyecto: this.proyectoService.findById(wrapper.value.entidadRelacionada.id),
          proyectosSge: this.proyectoService.findAllProyectosSgeProyecto(wrapper.value.entidadRelacionada.id)
        }).pipe(
          map(({ proyecto, proyectosSge }) => {
            wrapper.value.entidadRelacionada = proyecto;
            wrapper.value.entidadRelacionadaHref = this.createEntidadRelacionadaHref(proyecto.id, tipoEntidad);
            wrapper.value.entidadConvocanteRef = proyecto.codigoExterno;
            wrapper.value.codigosSge = proyectosSge.items.map(element => element.proyectoSge.id).join(', ');
            return wrapper;
          }),
          catchError(() => of(wrapper))
        );

      default:
        return of(wrapper);
    }
  }

  private createEntidadRelacionadaHref(entidadRelacionadaId: number, tipoEntidad: TipoEntidad): string {
    if (tipoEntidad !== TipoEntidad.INVENCION || tipoEntidad === TipoEntidad.INVENCION && this.hasUserInvencionAuth()) {
      return `${TIPO_ENTIDAD_HREF_MAP.get(tipoEntidad)}/${entidadRelacionadaId}`;
    } else {
      return '';
    }
  }

  private hasUserInvencionAuth(): boolean {
    return this.sgiAuthService.hasAnyAuthority(['PII-INV-V', 'PII-INV-E']);
  }

  hasEditPerm(): boolean {
    return !this.readonly;
  }

  getCurrentProyectoAsSelfRelated(): IProyectoRelacionTableData {
    return {
      entidadRelacionada: this.proyecto,
      tipoEntidadRelacionada: TipoEntidad.PROYECTO
    } as IProyectoRelacionTableData;
  }

  getRelacionesProyectoTableData$(): Observable<StatusWrapper<IProyectoRelacionTableData>[]> {
    return this.proyectoRelacionesTableData$.asObservable();
  }

  addRelacion(relacion: IProyectoRelacionTableData): void {
    const wrapped = new StatusWrapper<IProyectoRelacionTableData>(relacion);
    this.updateWrapperEntidadRelacionadaHref(wrapped);
    if (relacion.tipoEntidadRelacionada === TipoEntidad.PROYECTO) {
      wrapped.value.codigosSge = (relacion.entidadRelacionada as IProyectoListadoData)?.proyectosSGE;
      wrapped.value.entidadConvocanteRef = (relacion.entidadRelacionada as IProyecto).codigoExterno;
    }
    wrapped.setCreated();
    const current = this.proyectoRelacionesTableData$.value;
    current.push(wrapped);
    this.proyectoRelacionesTableData$.next(current);
    this.setChanges(true);
  }

  updateRelacion(index: number): void {
    if (index >= 0) {
      const current = this.proyectoRelacionesTableData$.value;
      const wrapper = current[index];
      this.updateWrapperEntidadRelacionadaHref(wrapper);
      if (!wrapper.created) {
        wrapper.setEdited();
      }
      this.proyectoRelacionesTableData$.next(current);
      this.setChanges(true);
    }
  }

  private updateWrapperEntidadRelacionadaHref(wrapper: StatusWrapper<IProyectoRelacionTableData>): void {
    wrapper.value.entidadRelacionadaHref = this.createEntidadRelacionadaHref(
      wrapper.value.entidadRelacionada.id, wrapper.value.tipoEntidadRelacionada);
  }

  deleteRelacion(wrapper: StatusWrapper<IProyectoRelacionTableData>): void {
    const current = this.proyectoRelacionesTableData$.value;
    const index = current.findIndex(value => value === wrapper);
    if (index >= 0) {
      if (!wrapper.created) {
        this.proyectoRelacionesTableDataToDelete.push(current[index]);
      }
      this.removeDeletedRelacionFromArray(index, current);
    }
  }

  private removeDeletedRelacionFromArray(
    index: number, currentProyectoRelacionesTableData: StatusWrapper<IProyectoRelacionTableData>[]): void {
    currentProyectoRelacionesTableData.splice(index, 1);
    this.proyectoRelacionesTableData$.next(currentProyectoRelacionesTableData);
    this.setChanges(this.hasFragmentChangesPending());
  }

  private hasFragmentChangesPending(): boolean {
    return this.proyectoRelacionesTableDataToDelete.length > 0 ||
      this.proyectoRelacionesTableData$.value.some((value) => value.created || value.edited);
  }

  saveOrUpdate(): Observable<string | number | void> {
    return merge(
      this.deleteRelaciones(),
      this.updateRelaciones(),
      this.createRelaciones()
    ).pipe(
      takeLast(1),
      tap(() => {
        this.setChanges(this.hasFragmentChangesPending());
      })
    );
  }

  private deleteRelaciones(): Observable<void> {
    if (this.proyectoRelacionesTableDataToDelete.length === 0) {
      return of(void 0);
    }

    return from(this.proyectoRelacionesTableDataToDelete).pipe(
      mergeMap(wrapped =>
        this.deleteInformePatentabilidadById(wrapped)
      )
    );
  }

  private deleteInformePatentabilidadById(wrapped: StatusWrapper<IProyectoRelacionTableData>): Observable<void> {
    return this.relacionService.delete(wrapped.value.id).pipe(
      tap(() =>
        this.proyectoRelacionesTableDataToDelete = this.proyectoRelacionesTableDataToDelete.filter(entidadEliminada =>
          entidadEliminada.value.id !== wrapped.value.id
        )
      )
    );
  }

  private updateRelaciones(): Observable<void> {
    const current = this.proyectoRelacionesTableData$.value;
    return from(current.filter(wrapper => wrapper.edited)).pipe(
      mergeMap((wrapper => {
        return this.relacionService.update(wrapper.value.id, this.createRelacionFromProyectoRelacion(wrapper.value)).pipe(
          map((relacionResponse) => this.refreshProyectoRelacionesTableData(relacionResponse, wrapper, current)),
        );
      }))
    );
  }

  private createRelaciones(): Observable<void> {
    const current = this.proyectoRelacionesTableData$.value;
    return from(current.filter(wrapper => wrapper.created)).pipe(
      mergeMap((wrapper => {
        return this.relacionService.create(this.createRelacionFromProyectoRelacion(wrapper.value)).pipe(
          map((relacionResponse) => this.refreshProyectoRelacionesTableData(relacionResponse, wrapper, current)),
        );
      }))
    );
  }

  private createRelacionFromProyectoRelacion(proyectoRelacion: IProyectoRelacionTableData): IRelacion {
    return {
      id: proyectoRelacion.id,
      entidadOrigen: proyectoRelacion.tipoEntidadRelacionada !== TipoEntidad.PROYECTO ?
        this.proyecto : this.getEntidadOrigenProyecto(proyectoRelacion),
      entidadDestino: proyectoRelacion.tipoEntidadRelacionada !== TipoEntidad.PROYECTO ?
        proyectoRelacion.entidadRelacionada : this.getEntidadDestinoProyecto(proyectoRelacion),
      tipoEntidadOrigen: TipoEntidad.PROYECTO,
      tipoEntidadDestino: proyectoRelacion.tipoEntidadRelacionada,
      observaciones: proyectoRelacion.observaciones
    };
  }

  private getEntidadOrigenProyecto(proyectoRelacion: IProyectoRelacionTableData): IConvocatoria | IInvencion | IProyecto {
    return proyectoRelacion.tipoRelacion === TipoRelacion.PADRE ? this.proyecto : proyectoRelacion.entidadRelacionada;
  }

  private getEntidadDestinoProyecto(proyectoRelacion: IProyectoRelacionTableData): IConvocatoria | IInvencion | IProyecto {
    return proyectoRelacion.tipoRelacion === TipoRelacion.PADRE ? proyectoRelacion.entidadRelacionada : this.proyecto;
  }

  private refreshProyectoRelacionesTableData(
    relacionResponse: IRelacion,
    wrapper: StatusWrapper<IProyectoRelacionTableData>,
    current: StatusWrapper<IProyectoRelacionTableData>[]
  ): void {
    const proyectoRelacion = this.createProyectoRelacionTableDataFromRelacion(relacionResponse);
    this.copyRelatedAttributes(wrapper.value, proyectoRelacion);
    current[current.findIndex(c => c === wrapper)] = new StatusWrapper<IProyectoRelacionTableData>(proyectoRelacion);
    this.proyectoRelacionesTableData$.next(current);
  }

  private copyRelatedAttributes(
    source: IProyectoRelacionTableData,
    target: IProyectoRelacionTableData
  ): void {
    target.entidadRelacionada = source.entidadRelacionada;
    target.entidadRelacionadaHref = source.entidadRelacionadaHref;
  }

}

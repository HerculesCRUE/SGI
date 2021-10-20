import { IInvencion } from '@core/models/pii/invencion';
import { IPeriodoTitularidad } from '@core/models/pii/periodo-titularidad';
import { IPeriodoTitularidadTitular } from '@core/models/pii/periodo-titularidad-titular';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { Fragment } from '@core/services/action-service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { PeriodoTitularidadService } from '@core/services/pii/invencion/periodo-titularidad/periodo-titularidad.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, combineLatest, concat, forkJoin, from, Observable, of } from 'rxjs';
import { catchError, flatMap, map, mergeMap, reduce, shareReplay, switchMap, takeLast, tap } from 'rxjs/operators';

export interface IPeriodoTitularidadFront extends IPeriodoTitularidad {
  /** Atributo utilizado en el front para definir si se puede eliminar o no */
  hasTramosReparto?: boolean;
}

export class PeriodoTitularidadFragment extends Fragment {

  private periodosTitularidades = new BehaviorSubject<StatusWrapper<IPeriodoTitularidad>[]>([]);
  private periodosTitularidadesShared = this.periodosTitularidades.asObservable().pipe(shareReplay(1));
  private periodosTitularidadSelected = new BehaviorSubject<StatusWrapper<IPeriodoTitularidad>>(null);
  private periodosTitularidadSelectedShared = this.periodosTitularidadSelected.asObservable().pipe(shareReplay(1));
  private periodosTitularidadTitulares = new BehaviorSubject<StatusWrapper<IPeriodoTitularidadTitular>[]>([]);
  private periodosTitularidadTitularesShared = this.periodosTitularidadTitulares.asObservable().pipe(shareReplay(1));
  private periodosTitularidadesToDelete: StatusWrapper<IPeriodoTitularidad>[] = [];
  private checkIsTitularesCompleted = new BehaviorSubject<any>(void 0);
  private deletedTitulares: IPeriodoTitularidadTitular[] = [];

  /**
   * Observable utilizado para verificar que la participación de los {@link IPeriodoTitularidad} esté completa.
   * Esta acción se ejecutará cuando se solicite o siempre cambie el listado de {@link IPeriodoTitularidadTitular}.
   * En caso de no ser completa la participación el {@link Fragment} se marcará con errores. Además se solicitará una verificación
   * del estado del {@link Fragment} para marcar si existen cambios.
   */
  private isTitularesComplete = combineLatest([this.periodosTitularidadTitularesShared, this.checkIsTitularesCompleted]).pipe(
    map(([elem, doCheck]) => (elem.reduce((acc, curr) =>
      acc + curr.value.participacion, 0)) === 100 || !this.periodosTitularidadSelected.value),
    shareReplay(1),
    tap(result => {
      this.setErrors(!result);
      this.validateStatus();
    })
  );

  /**
   * Devuelve el {@link IPeriodoTitularidad} histórico anterior al {@link IPeriodoTitularidad}
   * vigente si existe.
   */
  get previousPeriodoTitularidadHistorico(): StatusWrapper<IPeriodoTitularidad> {
    let previous: StatusWrapper<IPeriodoTitularidad> = null;
    this.periodosTitularidades.getValue().forEach(elem => {
      if (previous == null) {
        previous = elem;
      }
      if (previous.value.fechaFin < elem.value.fechaFin) {
        previous = elem;
      }
    });

    return previous;
  }

  /**
   * Devuelve el {@link IPeriodoTitularidad} vigente actualmente si existe.
   */
  get periodoVigente(): StatusWrapper<IPeriodoTitularidad> {
    return this.periodosTitularidades.getValue().find((elem) => elem.value.fechaFin == null) ?? null;
  }

  /**
   * Devuelve la Fecha Minima posible de ser usada como Inicio de un nuevo {@link IPeriodoTitularidad}.
   */
  get fechaInicioMinimaCreacion(): DateTime {
    const result = this.periodoVigente?.value?.fechaInicio;
    if (!result) {
      return this.fechaInicioMinimaEdicion;
    }

    return result.plus({ days: 1 });
  }

  /**
   * Devuelve la Fecha Minima posible de ser usada como Inicio al editar un {@link IPeriodoTitularidad} activo.
   */
  get fechaInicioMinimaEdicion(): DateTime {
    return this.previousPeriodoTitularidadHistorico?.value?.fechaFin?.plus({ days: 1 });
  }

  /**
   * Listado de los {@link IPeriodoTitularidad}.
   */
  get periodosTitularidad$(): Observable<StatusWrapper<IPeriodoTitularidad>[]> {
    return this.periodosTitularidadesShared;
  }

  /**
   * {@link IPeriodoTitularidad} seleccionado actualmente. Los {@link IPeriodoTitularidadTitular} asociados a este
   * {@link IPeriodoTitularidad} serán cargados cada vez que se seleccione uno nuevo.
   */
  get periodosTitularidadSelected$(): Observable<StatusWrapper<IPeriodoTitularidad>> {
    return this.periodosTitularidadSelectedShared;
  }

  /**
   * Listado de los {@link IPeriodoTitularidadTitular} asociados a el {@link IPeriodoTitularidad} seleccionado actualmente.
   */
  get periodosTitularidadTitular$(): Observable<StatusWrapper<IPeriodoTitularidadTitular>[]> {
    return this.periodosTitularidadTitularesShared;
  }

  /**
   * Devuelve {@link True} si la suma de las participaciones de los {@link IPeriodoTitularidadTitular} suman 100%.
   */
  get isTitularesComplete$(): Observable<boolean> {
    return this.isTitularesComplete;
  }

  get empresasTitularesPeriodo(): IEmpresa[] {
    if (!this.periodosTitularidadSelected.getValue()) {
      return [];
    }
    return this.periodosTitularidadTitulares.getValue().map(elem => elem?.value.titular);
  }

  constructor(
    key: number,
    private readonly logger: NGXLogger,
    private readonly periodoTitularidadService: PeriodoTitularidadService,
    private readonly invencionService: InvencionService,
    private readonly empresaService: EmpresaService
  ) {
    super(key);
    this.setComplete(false);
  }

  /**
   * Devuelve verdadero o falso según sea el caso si una vez consultado el Backend
   * se define que tenemos Repartos de este {@link IPeriodoTitularidad}
   *
   * @returns Si se encontró o no un reparto asociado
   */
  private periodoTitularidadHasRepartos(periodoTitularidad: StatusWrapper<IPeriodoTitularidadFront>): Observable<boolean> {
    return of(false);
  }

  clearPeriodoTitularidadSelection() {
    this.periodosTitularidadSelected.next(null);
    this.periodosTitularidadTitulares.next([]);
  }

  createEmptyPeriodoTitularidad() {
    const emptyElem = new StatusWrapper<IPeriodoTitularidad>({} as IPeriodoTitularidad);
    emptyElem.setCreated();

    return emptyElem;
  }

  createEmptyTitular() {
    const emptyElem = new StatusWrapper<IPeriodoTitularidadTitular>({} as IPeriodoTitularidadTitular);
    emptyElem.setCreated();

    return emptyElem;
  }

  /**
   * Devuelve verdadero si se puede agregar en el estado actual un nuevo {@link IPeriodoTitularidad}
   *
   * @returns Boolean
   */
  canAddTitularidad(): boolean {
    return !this.hasChanges() && this.periodoVigente === this.periodosTitularidadSelected.getValue();
  }

  /**
   *  Elimina el {@link IPeriodoTitularidadTitular} del listado de elementos a guardar.
   *
   * @param elemToDelete Elemento a eliminar
   */
  deletePeriodoTitularidadTitular(elemToDelete: StatusWrapper<IPeriodoTitularidadTitular>) {
    const current = this.periodosTitularidadTitulares.value;
    const index = current.findIndex((value) => value === elemToDelete);
    if (index >= 0) {
      current.splice(index, 1);
      this.periodosTitularidadTitulares.next(current);
      this.deletedTitulares.push(elemToDelete.value);
      this.validateStatus();
    }
  }

  /**
   * Eliminar {@link IPeriodoTitularidad}
   * @param elemToDelete Elemento a eliminar
   *
   */
  deletePeriodoTitularidad(elemToDelete: StatusWrapper<IPeriodoTitularidad>) {
    if ((elemToDelete.value as IPeriodoTitularidadFront)?.hasTramosReparto || this.periodoVigente.value?.id !== elemToDelete.value?.id) {
      return;
    }

    const current = this.periodosTitularidades.value;
    const toDelete = elemToDelete;
    const index = current.findIndex(
      (value) => value === elemToDelete
    );
    if (index === -1) {
      return;
    }
    current.splice(index, 1);
    if (!toDelete.created && toDelete.value?.id) {
      toDelete.setDeleted();
      const currentDeleted = this.periodosTitularidadesToDelete;
      currentDeleted.push(toDelete);
    }
    this.periodosTitularidades.next(current);
    this.clearPeriodoTitularidadSelection();
    this.validateStatus();
  }

  /**
   * Al ser llamado reactiva el {@link IPeriodoTitularidad} inmediatamente anterior.
   * Debe asegurarse que no exista ningún {@link IPeriodoTitularidad} vigente antes
   * de llamar este método pues de ser ese el caso no se ejecutará ningún cambio.
   *
   */
  reactivatePreviousPeriodoTitularidad() {
    if (this.periodoVigente || this.periodosTitularidades.getValue().length === 0) {
      return;
    }
    let toReactivate: StatusWrapper<IPeriodoTitularidad> = this.periodosTitularidades?.value[0];
    for (let i = 1; i < this.periodosTitularidades.getValue().length; i++) {
      if (this.periodosTitularidades.getValue()[i].value.fechaFin > toReactivate.value.fechaFin) {
        toReactivate = this.periodosTitularidades.getValue()[i];
      }
    }
    toReactivate.setEdited();
    toReactivate.value.fechaFin = null;
  }

  /**
   * Método llamado al ser editado un {@link IPeriodoTitularidad}. Al ser modificado por referencia
   * su única función actual es invocar la validación del estado actual.
   *
   * @param editedElem Elemento editado
   */
  editPeriodoTitularidad(editedElem: StatusWrapper<IPeriodoTitularidad>) {
    this.validateStatus();
  }

  /**
   * Función que debe ser llamada al editarse un {@link IPeriodoTitularidadTitular}.
   * Al ser modificado el objeto vía referencia, este método solo se encarga de solicitar
   * que se revise nuevamente si está completada la lista de titulares. A partir de esta solicitud
   * de revisión se activará en caso de ser necesario el mensaje de error al no estar completado el
   * listado de {@link IPeriodoTitularidadTitular}.
   *
   * @param editedElem Elemento editado
   */
  editPeriodoTitularidadTitular(editedElem: StatusWrapper<IPeriodoTitularidadTitular>) {
    this.checkIsTitularesCompleted.next(void 0);
  }

  /**
   * Agregar un nuevo {@link IPeriodoTitularidad}
   *
   * @param addedElem Elemento a agregar
   */
  addPeriodoTitularidad(addedElem: StatusWrapper<IPeriodoTitularidad>) {
    addedElem.value.invencion = { id: this.getKey() } as IInvencion;
    const current = this.periodosTitularidades.value;
    current.push(addedElem);
    if (this.periodoVigente !== addedElem) {
      // Marca como editado el {@link IPeriodoTitularidad} vigente
      this.periodoVigente.setEdited();
      // Desactiva como vigente el actual en favor del recientemente agregado
      this.periodoVigente.value.fechaFin = addedElem.value.fechaFinPrevious;
    }
    this.periodosTitularidades.next(current);
    this.validateStatus();
  }

  /**
   * Función encargada de agregar un {@link IPeriodoTitularidadTitular} al listado. Esta función
   * además solicita un chequeo del estado actual con elobjetivo de marcar si existen cambios pendientes.
   *
   * @param addedElem Elemento agregado
   */
  addPeriodoTitularidadTitular(addedElem: StatusWrapper<IPeriodoTitularidadTitular>) {
    const current = this.periodosTitularidadTitulares.getValue();
    addedElem.value.periodoTitularidad = this.periodoVigente.value;
    if (current.indexOf(addedElem) === -1) {
      current.push(addedElem);
      this.periodosTitularidadTitulares.next(current);
    } else {
      this.checkIsTitularesCompleted.next(void 0);
    }
    this.validateStatus();
  }

  /**
   * Establece el {@link IPeriodoTitularidad} seleccionado, a partir del cuál deberán
   * ser cargado los {@link IPeriodoTitularidadTitular} asociados.
   *
   * @param elem Elemento que pasa a ser seleccionado
   */
  setSelectedPeriodoTitularidad(elem: StatusWrapper<IPeriodoTitularidad>): void {
    if (elem?.created || elem?.value?.id !== this.periodosTitularidadSelected.getValue()?.value?.id) {
      this.periodosTitularidadSelected.next(elem);
    }
  }

  /**
   * Devuelve verdadero si alguno de los {@link IPeriodoTitularidadTitular} presentan cambios
   * @returns Boolean
   */
  public hasChangesTitulares(): boolean {
    return this.periodosTitularidadTitulares.value?.some(elem => elem.edited || elem.created) || this.deletedTitulares.length > 0;
  }

  protected onInitialize(): void | Observable<any> {
    this.subscriptions.push(
      this.invencionService.findPeriodosTitularidadByInvencionId(this.getKey() as number).pipe(
        flatMap(periodosTitularidad => {
          const periodosWrapped: StatusWrapper<IPeriodoTitularidad>[] = [];
          periodosTitularidad.items.forEach(element => {
            periodosWrapped.push(new StatusWrapper<IPeriodoTitularidad>(element));
          });
          return periodosWrapped;
        }),
        map(periodoWrapped => {
          (periodoWrapped.value as IPeriodoTitularidadFront).hasTramosReparto = false;
          if (!periodoWrapped.value.fechaFin) {
            return forkJoin([of(periodoWrapped), this.periodoTitularidadHasRepartos(periodoWrapped)]).pipe(
              map(([periodoConsultado, hasRepartos]) => {
                (periodoWrapped.value as IPeriodoTitularidadFront).hasTramosReparto = hasRepartos;
                return periodoConsultado;
              })
            );
          }
          return of(periodoWrapped);
        }),
        flatMap(elem => elem),
        reduce<StatusWrapper<IPeriodoTitularidad>, StatusWrapper<IPeriodoTitularidad>[]>((all, current) => [...all, current], [])
      ).subscribe((periodosWrapped) => {
        this.periodosTitularidades.next(periodosWrapped ?? []);
      })
    );

    this.subscriptions.push(
      this.periodosTitularidad$.subscribe(() => {
        this.setSelectedPeriodoTitularidad(this.periodoVigente);
      })
    );

    this.subscriptions.push(
      this.periodosTitularidadSelectedShared.pipe(
        switchMap(elem => {
          if (!elem?.value?.id) {
            return of([]);
          }
          return this.periodoTitularidadService.findTitularesByPeriodoTitularidad(elem.value.id).pipe(
            flatMap(requestResult => requestResult.items),
            flatMap(titularidadTitular => this.empresaService.findById(titularidadTitular.titular.id).pipe(
              catchError((err) => {
                this.logger.error(err);
                return of({ id: titularidadTitular.titular.id } as IEmpresa);
              }),
              map(empresa => {
                titularidadTitular.titular = empresa;
                return new StatusWrapper(titularidadTitular);
              }))),
            reduce<StatusWrapper<IPeriodoTitularidadTitular>,
              StatusWrapper<IPeriodoTitularidadTitular>[]>((all, current) => [...all, current], [])
          );
        }),
      ).subscribe(elem => {
        this.deletedTitulares = [];
        this.periodosTitularidadTitulares.next(elem);
      })
    );
  }

  /**
   * Se valida el estado actual y se marca como que hay cambios
   * siempre que al estar seleccionado el {@link IPeriodoTitularidad} vigente se
   * determine que existen {@link IPeriodoTitularidad} o {@link IPeriodoTitularidadTitular} 
   * con cambios pendientes de guardar.
   *
   */
  private validateStatus() {
    this.setChanges(
      this.periodoVigente === this.periodosTitularidadSelected.getValue() &&
      (
        this.periodoVigente?.touched ||
        this.hasChangesTitulares() ||
        this.periodosTitularidadesToDelete.length > 0
      )
    );
  }

  /**
   * Realiza la actualización en lote de los {@link IPeriodoTitularidadTitular} asociados al {@link IPeriodoTitularidad}
   * vigente actualmente. Al finalizar establece como {@link IPeriodoTitularidad} seleccionado el {@link IPeriodoTitularidad}
   * vigente para forzar el la carga de los elementos actualizados del backend.
   *
   * @returns Vacío
   */
  private batchUpdateTitulares(): Observable<void> {
    if (!this.periodoVigente || !this.hasChangesTitulares()) {
      return of(void 0);
    }
    return this.periodoTitularidadService
      .bulkSaveOrUpdatePeriodoTitularidadTitulares(
        this.periodoVigente?.value?.id as number, this.periodosTitularidadTitulares.value.map(elem => elem.value))
      .pipe(
        switchMap(e => {
          this.periodosTitularidadSelected.next(this.periodoVigente);
          return of(void 0);
        })
      );
  }

  /**
   * Realiza la actualización en el servidor del {@link IPeriodoTitularidad} vigente.
   *
   * @returns Vacío
   */
  private updatePeriodoTitularidad(): Observable<void> {
    if (!this.periodoVigente?.edited) {
      return of(void 0);
    }

    return this.periodoTitularidadService
      .update(this.periodoVigente.value.id, this.periodoVigente.value)
      .pipe(
        switchMap(() => of(void 0))
      );
  }

  /**
   * Persiste la eliminación de los {@link IPeriodoTitularidad} correspondientes en el servidor.
   *
   * @returns Vacío
   */
  private deletePeriodosTitularidad(): Observable<void> {
    if (this.deletePeriodoTitularidad.length === 0) {
      return of(void 0);
    }
    return from(this.periodosTitularidadesToDelete).pipe(
      mergeMap((wrapped) => {
        return this.periodoTitularidadService.deleteById(wrapped.value.id);
      }),
      switchMap(() => {
        this.periodosTitularidadesToDelete = [];
        return of(void 0);
      })
    );
  }

  private createPeriodoTitularidad(): Observable<void> {
    if (!this.periodoVigente || !this.periodoVigente.created) {
      return of(void 0);
    }

    return this.periodoTitularidadService
      .create(this.periodoVigente.value)
      .pipe(
        tap(periodoTitularidadCreated => {
          this.periodoVigente.value.id = periodoTitularidadCreated.id;
          this.periodoVigente.value.fechaInicio = periodoTitularidadCreated.fechaInicio;
          this.periodoVigente.value.fechaFin = null;
        }),
        switchMap(e => of(void 0))
      );
  }

  saveOrUpdate(): Observable<string | number | void> {
    return concat(
      this.deletePeriodosTitularidad(),
      this.createPeriodoTitularidad()
        .pipe(
          switchMap(() => this.updatePeriodoTitularidad()),
          switchMap(() => this.batchUpdateTitulares())
        ),
    ).pipe(
      takeLast(1),
      tap(() => {
        this.deletedTitulares = [];
        this.clearPeriodoTitularidadSelection();
        this.periodosTitularidades.next(this.periodosTitularidades.value.map(elem => elem.touched ? new StatusWrapper(elem.value) : elem));
      })
    );
  }

}

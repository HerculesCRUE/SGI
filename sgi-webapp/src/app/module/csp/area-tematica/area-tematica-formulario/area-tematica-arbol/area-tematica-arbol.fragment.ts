import { IAreaTematica } from '@core/models/csp/area-tematica';
import { Fragment } from '@core/services/action-service';
import { AreaTematicaService } from '@core/services/csp/area-tematica.service';
import { LanguageService } from '@core/services/language.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { concatMap, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface INodeArea {
  parent: NodeArea;
  area: StatusWrapper<IAreaTematica>;
  readonly childs: NodeArea[];
}

export class NodeArea implements INodeArea {
  parent: NodeArea;
  area: StatusWrapper<IAreaTematica>;
  // tslint:disable-next-line: variable-name
  _childs: NodeArea[];
  get childs(): NodeArea[] {
    return this._childs;
  }
  // tslint:disable-next-line: variable-name
  _sortChildsWith: (a1: NodeArea, a2: NodeArea) => number;

  constructor(area: StatusWrapper<IAreaTematica>, sortChildsWith: (a1: NodeArea, a2: NodeArea) => number) {
    this.area = area;
    this._childs = [];
    this._sortChildsWith = sortChildsWith;
  }

  addChild(child: NodeArea) {
    child.parent = this;
    child.area.value.padre = this.area.value;
    this._childs.push(child);
    this.sortChildsByName();
  }


  removeChild(child: NodeArea) {
    this._childs = this._childs.filter((area) => area !== child);
  }

  sortChildsByName(): void {
    this._childs = sortNodes(this._childs, this._sortChildsWith);
  }
}

function sortNodes(nodes: NodeArea[], sortWith: (a1: NodeArea, a2: NodeArea) => number): NodeArea[] {
  return nodes.sort(sortWith);
}

export class AreaTematicaArbolFragment extends Fragment {
  areas$ = new BehaviorSubject<NodeArea[]>([]);
  private areasEliminados: IAreaTematica[] = [];

  private sortNodesByAreaNombre: (a1: NodeArea, a2: NodeArea) => number = (a1, a2) => {
    const nombreA = this.languageService.getFieldValue(a1.area?.value?.nombre);
    const nombreB = this.languageService.getFieldValue(a2.area?.value?.nombre);
    return nombreA.localeCompare(nombreB);
  };

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private areaTematicaService: AreaTematicaService,
    private languageService: LanguageService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.areaTematicaService.findAllHijosArea(this.getKey() as number).pipe(
        switchMap(response => {
          return from(response.items).pipe(
            mergeMap((area) => {
              const node = new NodeArea(new StatusWrapper<IAreaTematica>(area), this.sortNodesByAreaNombre);
              return this.getChilds(node).pipe(map(() => node));
            })
          );
        })
      ).subscribe(
        (area) => {
          const current = this.areas$.value;
          current.push(area);
          this.publishNodes(current);
        },
        (error) => {
          this.logger.error(error);
        }
      );
    }
  }

  publishNodes(rootNodes?: NodeArea[]) {
    let nodes = rootNodes ? rootNodes : this.areas$.value;
    nodes = sortNodes(nodes, this.sortNodesByAreaNombre);
    this.areas$.next(nodes);
  }

  createEmptyNode(): NodeArea {
    return new NodeArea(
      new StatusWrapper<IAreaTematica>({
        padre: {} as IAreaTematica
      } as IAreaTematica),
      this.sortNodesByAreaNombre
    );
  }

  private getChilds(parent: NodeArea): Observable<NodeArea[]> {
    return this.areaTematicaService.findAllHijosArea(parent.area.value.id).pipe(
      map((result) => {
        const childs: NodeArea[] = result.items.map(
          (area) => {
            const child = new NodeArea(new StatusWrapper<IAreaTematica>(area), this.sortNodesByAreaNombre);
            child.parent = parent;
            return child;
          });
        return childs;
      }),
      switchMap((nodes) => {
        parent.childs.push(...nodes);
        parent.sortChildsByName();
        if (nodes.length > 0) {
          return from(nodes).pipe(
            mergeMap((node) => {
              return this.getChilds(node);
            })
          );
        }
        else {
          return of([]);
        }
      }),
      takeLast(1)
    );
  }

  public addToDelete(node: NodeArea) {
    if (!node.area.created) {
      this.areasEliminados.push(node.area.value);
    }
    node.childs.forEach((child) => {
      this.addToDelete(child);
    });
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteAreas(),
      this.updateAreas(this.getUpdated(this.areas$.value)),
      this.createAreas(this.getCreated(this.areas$.value))
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete(this.areas$.value)) {
          this.setChanges(false);
        }
      })
    );
  }

  private getUpdated(areas: NodeArea[]): NodeArea[] {
    const updated: NodeArea[] = [];
    areas.forEach((node) => {
      if (node.area.edited) {
        updated.push(node);
      }
      if (node.childs.length) {
        updated.push(...this.getUpdated(node.childs));
      }
    });
    return updated;
  }

  private getCreated(areas: NodeArea[]): NodeArea[] {
    const updated: NodeArea[] = [];
    areas.forEach((node) => {
      if (node.area.created) {
        updated.push(node);
      }
      if (node.childs.length) {
        updated.push(...this.getCreated(node.childs));
      }
    });
    return updated;
  }

  private deleteAreas(): Observable<void> {
    if (this.areasEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.areasEliminados).pipe(
      mergeMap((area) => {
        return this.areaTematicaService.desactivar(area.id)
          .pipe(
            tap(() => {
              this.areasEliminados = this.areasEliminados.filter(deleted =>
                deleted.id !== area.id);
            })
          );
      }));
  }

  private updateAreas(nodes: NodeArea[]): Observable<void> {
    if (nodes.length === 0) {
      return of(void 0);
    }
    return from(nodes).pipe(
      mergeMap((node) => {
        return this.areaTematicaService.update(node.area.value.id, node.area.value).pipe(
          map((updated) => {
            node.area = new StatusWrapper<IAreaTematica>(updated);
          })
        );
      }));
  }

  private createAreas(nodes: NodeArea[]): Observable<void> {
    if (nodes.length === 0) {
      return of(void 0);
    }
    return from(nodes).pipe(
      concatMap(node => {
        // Root nodes must match entity parent
        if (!node.parent) {
          node.area.value.padre.id = this.getKey() as number;
        }
        return this.areaTematicaService.create(node.area.value).pipe(
          map(created => {
            node.area = new StatusWrapper<IAreaTematica>(created);
            node.childs.forEach((child) => {
              child.area.value.padre = created;
            });
          })
        );
      }));
  }

  private isSaveOrUpdateComplete(nodes: NodeArea[]): boolean {
    let pending = this.areasEliminados.length > 0;
    if (pending) {
      return false;
    }
    nodes.forEach((node) => {
      pending = pending || node.area.touched;
      if (pending) {
        return false;
      }
      if (node.childs.length) {
        pending = pending || this.isSaveOrUpdateComplete(node.childs);
        if (pending) {
          return false;
        }
      }
    });
    return true;
  }

}

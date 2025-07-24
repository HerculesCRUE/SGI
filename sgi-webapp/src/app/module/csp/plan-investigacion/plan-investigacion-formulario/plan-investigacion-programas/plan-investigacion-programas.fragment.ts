import { IPrograma } from '@core/models/csp/programa';
import { Fragment } from '@core/services/action-service';
import { ProgramaService } from '@core/services/csp/programa.service';
import { LanguageService } from '@core/services/language.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { concatMap, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface INodePrograma {
  parent: NodePrograma;
  programa: StatusWrapper<IPrograma>;
  readonly childs: NodePrograma[];
}

export class NodePrograma implements INodePrograma {
  parent: NodePrograma;
  programa: StatusWrapper<IPrograma>;
  // tslint:disable-next-line: variable-name
  _childs: NodePrograma[];
  get childs(): NodePrograma[] {
    return this._childs;
  }
  // tslint:disable-next-line: variable-name
  _sortChildsWith: (a1: NodePrograma, a2: NodePrograma) => number

  constructor(programa: StatusWrapper<IPrograma>, sortChildsWith: (a1: NodePrograma, a2: NodePrograma) => number) {
    this.programa = programa;
    this._childs = [];
    this._sortChildsWith = sortChildsWith;
  }

  addChild(child: NodePrograma) {
    child.parent = this;
    child.programa.value.padre = this.programa.value;
    this._childs.push(child);
    this.sortChildsByName();
  }

  removeChild(child: NodePrograma) {
    this._childs = this._childs.filter((programa) => programa !== child);
  }

  sortChildsByName(): void {
    this._childs = sortNodes(this._childs, this._sortChildsWith);
  }
}

function sortNodes(nodes: NodePrograma[], sortWith: (a1: NodePrograma, a2: NodePrograma) => number): NodePrograma[] {
  return nodes.sort(sortWith);
}

export class PlanInvestigacionProgramaFragment extends Fragment {
  programas$ = new BehaviorSubject<NodePrograma[]>([]);
  private programasEliminados: IPrograma[] = [];

  private sortNodesByAreaNombre: (a1: NodePrograma, a2: NodePrograma) => number = (a1, a2) => {
    const nombreA = this.languageService.getFieldValue(a1.programa?.value?.nombre);
    const nombreB = this.languageService.getFieldValue(a2.programa?.value?.nombre);
    return nombreA.localeCompare(nombreB);
  };


  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private programaService: ProgramaService,
    private readonly languageService: LanguageService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.programaService.findAllHijosPrograma(this.getKey() as number).pipe(
        switchMap(response => {
          return from(response.items).pipe(
            mergeMap((programa) => {
              const node = new NodePrograma(new StatusWrapper<IPrograma>(programa), this.sortNodesByAreaNombre);
              return this.getChilds(node).pipe(map(() => node));
            })
          );
        })
      ).subscribe(
        (programa) => {
          const current = this.programas$.value;
          current.push(programa);
          this.publishNodes(current);
        },
        (error) => {
          this.logger.error(error);
        }
      );
    }
  }

  publishNodes(rootNodes?: NodePrograma[]) {
    let nodes = rootNodes ? rootNodes : this.programas$.value;
    nodes = sortNodes(nodes, this.sortNodesByAreaNombre);
    this.programas$.next(nodes);
  }

  createEmptyNode(): NodePrograma {
    return new NodePrograma(
      new StatusWrapper<IPrograma>({
        padre: {} as IPrograma
      } as IPrograma),
      this.sortNodesByAreaNombre
    );
  }

  private getChilds(parent: NodePrograma): Observable<NodePrograma[]> {
    return this.programaService.findAllHijosPrograma(parent.programa.value.id).pipe(
      map((result) => {
        const childs: NodePrograma[] = result.items.map(
          (programa) => {
            const child = new NodePrograma(new StatusWrapper<IPrograma>(programa), this.sortNodesByAreaNombre);
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

  public addToDelete(node: NodePrograma) {
    if (!node.programa.created) {
      this.programasEliminados.push(node.programa.value);
    }
    node.childs.forEach((child) => {
      this.addToDelete(child);
    });
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteProgramas(),
      this.updateProgramas(this.getUpdated(this.programas$.value)),
      this.createProgramas(this.getCreated(this.programas$.value))
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete(this.programas$.value)) {
          this.setChanges(false);
        }
      })
    );
  }

  private getUpdated(programas: NodePrograma[]): NodePrograma[] {
    const updated: NodePrograma[] = [];
    programas.forEach((node) => {
      if (node.programa.edited) {
        updated.push(node);
      }
      if (node.childs.length) {
        updated.push(...this.getUpdated(node.childs));
      }
    });
    return updated;
  }

  private getCreated(programas: NodePrograma[]): NodePrograma[] {
    const updated: NodePrograma[] = [];
    programas.forEach((node) => {
      if (node.programa.created) {
        updated.push(node);
      }
      if (node.childs.length) {
        updated.push(...this.getCreated(node.childs));
      }
    });
    return updated;
  }

  private deleteProgramas(): Observable<void> {
    if (this.programasEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.programasEliminados).pipe(
      mergeMap((programa) => {
        return this.programaService.deactivate(programa.id)
          .pipe(
            tap(() => {
              this.programasEliminados = this.programasEliminados.filter(deleted =>
                deleted.id !== programa.id);
            })
          );
      }));
  }

  private updateProgramas(nodes: NodePrograma[]): Observable<void> {
    if (nodes.length === 0) {
      return of(void 0);
    }
    return from(nodes).pipe(
      mergeMap((node) => {
        return this.programaService.update(node.programa.value.id, node.programa.value).pipe(
          map((updated) => {
            node.programa = new StatusWrapper<IPrograma>(updated);
          })
        );
      }));
  }

  private createProgramas(nodes: NodePrograma[]): Observable<void> {
    if (nodes.length === 0) {
      return of(void 0);
    }
    return from(nodes).pipe(
      concatMap(node => {
        // Root nodes must match entity parent
        if (!node.parent) {
          node.programa.value.padre.id = this.getKey() as number;
        }
        return this.programaService.create(node.programa.value).pipe(
          map(created => {
            node.programa = new StatusWrapper<IPrograma>(created);
            node.childs.forEach((child) => {
              child.programa.value.padre = created;
            });
          })
        );
      }));
  }

  private isSaveOrUpdateComplete(nodes: NodePrograma[]): boolean {
    let pending = this.programasEliminados.length > 0;
    if (pending) {
      return false;
    }
    nodes.forEach((node) => {
      pending = pending || node.programa.touched;
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

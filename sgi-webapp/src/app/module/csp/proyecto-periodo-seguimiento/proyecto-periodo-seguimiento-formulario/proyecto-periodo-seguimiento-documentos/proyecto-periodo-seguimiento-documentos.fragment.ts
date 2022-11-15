import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyectoPeriodoSeguimientoDocumento } from '@core/models/csp/proyecto-periodo-seguimiento-documento';
import { IDocumento } from '@core/models/sgdoc/documento';
import { Fragment } from '@core/services/action-service';
import { ProyectoPeriodoSeguimientoDocumentoService } from '@core/services/csp/proyecto-periodo-seguimiento-documento.service';
import { ProyectoPeriodoSeguimientoService } from '@core/services/csp/proyecto-periodo-seguimiento.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class NodeDocumento {
  parent: NodeDocumento;
  key: string;
  title: string;
  documento?: StatusWrapper<IProyectoPeriodoSeguimientoDocumento>;
  fichero?: IDocumento;
  // tslint:disable-next-line: variable-name
  _level: number;
  // tslint:disable-next-line: variable-name
  _childs: NodeDocumento[];
  get childs(): NodeDocumento[] {
    return this._childs;
  }

  get level(): number {
    return this._level;
  }

  constructor(key: string, title: string, level: number, documento?: StatusWrapper<IProyectoPeriodoSeguimientoDocumento>) {
    this.key = key;
    this.title = title;
    this._level = level;

    if (level === 0 && !title) {
      this.title = marker('label.csp.documento.sin-tipo');
    }
    this.documento = documento;
    this._childs = [];
  }

  addChild(child: NodeDocumento) {
    child.parent = this;
    this._childs.push(child);
    this.sortChildsByTitle();
  }

  removeChild(child: NodeDocumento) {
    this._childs = this._childs.filter((documento) => documento !== child);
  }

  sortChildsByTitle(): void {
    this._childs = sortByTitle(this._childs);
  }
}

function sortByTitle(nodes: NodeDocumento[]): NodeDocumento[] {
  return nodes.sort((a, b) => {
    // Force ordering last for level 0 and key 0
    if ((a.level === 0 || b.level === 0) && (a.key === '0' || b.key === '0')) {
      // A is the last
      if (a.key === '0') {
        return 1;
      }
      // B is the last
      if (b.key === '0') {
        return -1;
      }
      return 0;
    }


    if (a.title < b.title) {
      return -1;
    }
    if (a.title > b.title) {
      return 1;
    }
    return 0;
  });
}

export class ProyectoPeriodoSeguimientoDocumentosFragment extends Fragment {
  documentos$ = new BehaviorSubject<NodeDocumento[]>([]);
  private documentosEliminados: IProyectoPeriodoSeguimientoDocumento[] = [];

  private nodeLookup = new Map<string, NodeDocumento>();

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private periodoSeguimientoService: ProyectoPeriodoSeguimientoService,
    private periodoSeguimientoDocumentoService: ProyectoPeriodoSeguimientoDocumentoService,
    private documentoService: DocumentoService,
    public readonly
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.periodoSeguimientoService.findDocumentos(this.getKey() as number).pipe(
        map(response => {
          // TODO este filtro debería hacerse en el back
          let items = response.items;
          if (this.readonly) {
            items = items.filter(documento => documento.visible);
          }
          return this.buildTree(items);
        })
      ).subscribe(
        (documento) => {
          this.publishNodes(documento);
        },
        (error) => {
          this.logger.error(error);
        }
      );
    }
  }

  private buildTree(documentos: IProyectoPeriodoSeguimientoDocumento[]): NodeDocumento[] {
    const nodes: NodeDocumento[] = [];
    documentos.forEach((documento: IProyectoPeriodoSeguimientoDocumento) => {
      const keyTipoDocumento = `${documento.tipoDocumento ? documento.tipoDocumento?.id : 0}`;
      let tipoDocNode = this.nodeLookup.get(keyTipoDocumento);
      if (!tipoDocNode) {
        tipoDocNode = new NodeDocumento(keyTipoDocumento, documento.tipoDocumento?.nombre, 0);
        this.nodeLookup.set(keyTipoDocumento, tipoDocNode);
        nodes.push(tipoDocNode);
      }
      const docNode = new NodeDocumento('', documento.nombre, 1,
        new StatusWrapper<IProyectoPeriodoSeguimientoDocumento>(documento));
      tipoDocNode.addChild(docNode);
    });
    return nodes;
  }

  publishNodes(rootNodes?: NodeDocumento[]) {
    let nodes = rootNodes ? rootNodes : this.documentos$.value;
    nodes = sortByTitle(nodes);
    this.documentos$.next(nodes);
  }

  public addNode(node: NodeDocumento): NodeDocumento {
    const keyTipoDocumento = `${node.documento.value.tipoDocumento ? node.documento.value.tipoDocumento.id : 0}`;
    let nodeTipoDoc = this.nodeLookup.get(keyTipoDocumento);
    let addToRoot = false;
    if (!nodeTipoDoc) {
      nodeTipoDoc = new NodeDocumento(keyTipoDocumento, node.documento.value.tipoDocumento?.nombre, 0);
      this.nodeLookup.set(keyTipoDocumento, nodeTipoDoc);
      addToRoot = true;
    }
    const nodeDocumento = new NodeDocumento(keyTipoDocumento, node.title, 1, node.documento);
    nodeDocumento.documento.setCreated();
    nodeDocumento.fichero = node.fichero;
    nodeDocumento.documento.value.documentoRef = node.fichero?.documentoRef;
    nodeTipoDoc.addChild(nodeDocumento);
    const current = this.documentos$.value;
    if (addToRoot) {
      current.push(nodeTipoDoc);
    }
    this.publishNodes(current);
    this.setChanges(true);
    return nodeDocumento;
  }

  public updateNode(node: NodeDocumento) {
    if (!node.documento.created) {
      node.documento.setEdited();
    }
    node.documento.value.documentoRef = node.fichero?.documentoRef;
    const keyTipoDocumento = `${node.documento.value.tipoDocumento ? node.documento.value.tipoDocumento.id : 0}`;
    let nodeTipoDoc = this.nodeLookup.get(keyTipoDocumento);
    let addToRoot = false;
    let removedRootNode: NodeDocumento;
    if (!nodeTipoDoc) {
      nodeTipoDoc = new NodeDocumento(keyTipoDocumento, node.documento.value.tipoDocumento?.nombre, 1);
      this.nodeLookup.set(keyTipoDocumento, nodeTipoDoc);
      addToRoot = true;
    }
    // Si el padre ha cambiado limpiamos la rama y establecemos el nuevo padre
    if (nodeTipoDoc !== node.parent) {
      node.parent.removeChild(node);
      removedRootNode = this.removeEmptyParentNodes(node.parent);
      nodeTipoDoc.addChild(node);
    }
    else {
      // Ordenamos los hijos, porque puede haber cambiado el nombre
      node.parent.sortChildsByTitle();
    }
    let current = this.documentos$.value;
    if (removedRootNode) {
      current = current.filter((n) => n !== removedRootNode);
    }
    if (addToRoot) {
      current.push(nodeTipoDoc);
    }
    this.publishNodes(current);
    this.setChanges(true);
  }

  public deleteNode(node: NodeDocumento) {
    let removedRootNode: NodeDocumento;

    if (!node.documento.created) {
      this.documentosEliminados.push(node.documento.value);
    }

    node.parent.removeChild(node);
    removedRootNode = this.removeEmptyParentNodes(node.parent);

    let current = this.documentos$.value;
    if (removedRootNode) {
      current = current.filter((n) => n !== removedRootNode);
    }

    this.publishNodes(current);
    this.setChanges(true);
  }

  private removeEmptyParentNodes(node: NodeDocumento): NodeDocumento {
    let removedNode: NodeDocumento;
    if (node.childs.length === 0) {
      this.nodeLookup.delete(node.key);
      if (!node.parent) {
        removedNode = node;
      }
      else {
        node.parent.removeChild(node);
      }
    }
    if (node.parent) {
      removedNode = this.removeEmptyParentNodes(node.parent);
    }
    return removedNode;
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteDocumentos(),
      this.updateDocumentos(this.getUpdated(this.documentos$.value)),
      this.createDocumentos(this.getCreated(this.documentos$.value))
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete(this.documentos$.value)) {
          this.setChanges(false);
        }
      })
    );
  }

  private getUpdated(documentos: NodeDocumento[]): NodeDocumento[] {
    const updated: NodeDocumento[] = [];
    documentos.forEach((node) => {
      if (node.documento && node.documento.edited) {
        updated.push(node);
      }
      if (node.childs.length) {
        updated.push(...this.getUpdated(node.childs));
      }
    });
    return updated;
  }

  private getCreated(programas: NodeDocumento[]): NodeDocumento[] {
    const updated: NodeDocumento[] = [];
    programas.forEach((node) => {
      if (node.documento && node.documento.created) {
        updated.push(node);
      }
      if (node.childs.length) {
        updated.push(...this.getCreated(node.childs));
      }
    });
    return updated;
  }

  private deleteDocumentos(): Observable<void> {
    if (this.documentosEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.documentosEliminados).pipe(
      mergeMap((documento) => {
        return this.periodoSeguimientoDocumentoService.deleteById(documento.id)
          .pipe(
            tap(() => {
              this.documentosEliminados = this.documentosEliminados
                .filter(deleted => deleted.id !== documento.id);
            }),
            switchMap(() => this.documentoService.eliminarFichero(documento.documentoRef))
          );
      }));
  }

  private updateDocumentos(nodes: NodeDocumento[]): Observable<void> {
    if (nodes.length === 0) {
      return of(void 0);
    }
    return from(nodes).pipe(
      mergeMap((node) => {
        return this.periodoSeguimientoDocumentoService.update(node.documento.value.id, node.documento.value).pipe(
          map((updated) => {
            node.documento = new StatusWrapper<IProyectoPeriodoSeguimientoDocumento>(updated);
          })
        );
      })
    );
  }

  private createDocumentos(nodes: NodeDocumento[]): Observable<void> {
    if (nodes.length === 0) {
      return of(void 0);
    }
    return from(nodes).pipe(
      mergeMap(node => {
        node.documento.value.proyectoPeriodoSeguimientoId = this.getKey() as number;
        return this.periodoSeguimientoDocumentoService.create(node.documento.value).pipe(
          map(created => {
            node.documento = new StatusWrapper<IProyectoPeriodoSeguimientoDocumento>(created);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(nodes: NodeDocumento[]): boolean {
    let pending = this.documentosEliminados.length > 0;
    if (pending) {
      return false;
    }
    nodes.forEach((node) => {
      if (node.documento) {
        pending = node.documento.touched;
        if (pending) {
          return false;
        }
      }
      if (node.childs.length) {
        pending = this.isSaveOrUpdateComplete(node.childs);
        if (pending) {
          return false;
        }
      }
    });
    return true;
  }

}

import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IEmpresaDocumento } from '@core/models/eer/empresa-documento';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { ITipoDocumento } from '@core/models/eer/tipo-documento';
import { Fragment } from '@core/services/action-service';
import { EmpresaDocumentoService } from '@core/services/eer/empresa-documento/empresa-documento.service';
import { EmpresaExplotacionResultadosService } from '@core/services/eer/empresa-explotacion-resultados/empresa-explotacion-resultados.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class NodeDocumento {
  parent: NodeDocumento;
  key: string;
  title: string;
  documento?: StatusWrapper<IEmpresaDocumento>;
  // tslint:disable-next-line: variable-name
  private _level: number;
  tipo: ITipoDocumento;
  // tslint:disable-next-line: variable-name
  private _childs: NodeDocumento[];
  get childs(): NodeDocumento[] {
    return this._childs;
  }

  get level(): number {
    return this._level;
  }

  constructor(key: string, title: string, level: number, documento?: StatusWrapper<IEmpresaDocumento>) {
    this.key = key;
    this.title = title;
    this._level = level;
    if (level === 0 && !title) {
      this.title = marker('label.eer.documentos.sin-tipo');
    }
    else if (level === 1 && !title) {
      this.title = marker('label.eer.documentos.sin-subtipo');
    }
    this.documento = documento;
    this._childs = [];
  }

  addChild(child: NodeDocumento) {
    child.parent = this;
    this._childs.push(child);
    this.sortChildsByTitle();
  }

  sortChildsByTitle(): void {
    this._childs = sortByTitle(this._childs);
  }

  removeChild(child: NodeDocumento) {
    this._childs = this._childs.filter((documento) => documento !== child);
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

    // Force ordering last for level 1 and key ?-0
    if ((a.level === 1 || b.level === 1) && (a.key.endsWith('-0') || b.key.endsWith('-0'))) {
      // A is the last
      if (a.key.endsWith('-0')) {
        return 1;
      }
      // B is the last
      if (b.key.endsWith('-0')) {
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

export class EmpresaExplotacionResultadosDocumentosFragment extends Fragment {
  documentos$ = new BehaviorSubject<NodeDocumento[]>([]);
  private documentosEliminados: IEmpresaDocumento[] = [];
  private documentosRefUnrelated: string[] = [];

  private nodeLookup = new Map<string, NodeDocumento>();
  private empresa: IEmpresaExplotacionResultados;

  get canEdit(): boolean {
    return !this.readonly;
  }

  constructor(
    readonly id: number,
    private readonly readonly: boolean,
    private readonly empresaExplotacionResultadosService: EmpresaExplotacionResultadosService,
    private readonly empresaDocumentoService: EmpresaDocumentoService,
    private readonly documentoService: DocumentoService
  ) {
    super(id);
    this.setComplete(true);
    this.empresa = { id: this.id } as IEmpresaExplotacionResultados;
  }

  protected onInitialize(): void | Observable<any> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.empresaExplotacionResultadosService.findDocumentos(this.empresa, findOptions)
      .pipe(
        map(({ items }) => this.buildTree(items)),
        tap((nodes) => this.publishNodes(nodes))
      );
  }

  buildTree(documentos: IEmpresaDocumento[]): NodeDocumento[] {
    const nodes: NodeDocumento[] = [];
    documentos.forEach((documento) => {
      const keyTipoDocumento = `${documento.tipoDocumento ? documento.tipoDocumento.id : 0}`;
      const keySubtipoDocumento = `${keyTipoDocumento}-${documento.subtipoDocumento ? documento.subtipoDocumento.id : 0}`;
      let tipoDocumentoNode = this.nodeLookup.get(keyTipoDocumento);
      if (!tipoDocumentoNode) {
        tipoDocumentoNode = new NodeDocumento(keyTipoDocumento, documento.tipoDocumento?.nombre, 0);
        this.nodeLookup.set(keyTipoDocumento, tipoDocumentoNode);
        nodes.push(tipoDocumentoNode);
      }
      let subtipoDocumentoNode = this.nodeLookup.get(keySubtipoDocumento);
      if (!subtipoDocumentoNode) {
        subtipoDocumentoNode = new NodeDocumento(keySubtipoDocumento, documento.subtipoDocumento?.nombre, 1);
        tipoDocumentoNode.addChild(subtipoDocumentoNode);
        this.nodeLookup.set(keySubtipoDocumento, subtipoDocumentoNode);
      }
      const nodeDocumento = new NodeDocumento(null, documento.nombre, 2, new StatusWrapper<IEmpresaDocumento>(documento));
      subtipoDocumentoNode.addChild(nodeDocumento);
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
    const keySubtipoDocumento = `${keyTipoDocumento}-${node.documento.value.subtipoDocumento ? node.documento.value.subtipoDocumento.id : 0}`;
    let nodeTipoDocumento = this.nodeLookup.get(keyTipoDocumento);
    let addToRoot = false;
    if (!nodeTipoDocumento) {
      nodeTipoDocumento = new NodeDocumento(keyTipoDocumento, node.documento.value.tipoDocumento?.nombre, 0);
      this.nodeLookup.set(keyTipoDocumento, nodeTipoDocumento);
      addToRoot = true;
    }
    let nodeSubtipoDocumento = this.nodeLookup.get(keySubtipoDocumento);
    if (!nodeSubtipoDocumento) {
      nodeSubtipoDocumento = new NodeDocumento(keySubtipoDocumento, node.documento.value.subtipoDocumento?.nombre, 1);
      nodeTipoDocumento.addChild(nodeSubtipoDocumento);
      this.nodeLookup.set(keySubtipoDocumento, nodeSubtipoDocumento);
    }
    const nodeDocumento = new NodeDocumento(keySubtipoDocumento, node.title, 2, node.documento);
    nodeDocumento.documento.setCreated();
    nodeSubtipoDocumento.addChild(nodeDocumento);
    const current = this.documentos$.value;
    if (addToRoot) {
      current.push(nodeTipoDocumento);
    }
    this.publishNodes(current);
    this.setChanges(true);
    return nodeDocumento;
  }

  /**
   * Actualiza el documento y si se modifica el documento asociado lo anade a la lista de documentos a eliminar
   * y si no esta persistido aun se elimina directamente.
   */
  public updateNode(node: NodeDocumento, previousDocumentoRef: string): void {
    if (!node.documento.created) {
      node.documento.setEdited();
    }

    let deleteDocumento$ = of(void 0);
    if (!!previousDocumentoRef && node.documento.value.documento.documentoRef !== previousDocumentoRef) {
      if (node.documento.created) {
        deleteDocumento$ = this.documentoService.eliminarFichero(previousDocumentoRef);
      } else {
        this.documentosRefUnrelated.push(previousDocumentoRef);
      }
    }

    deleteDocumento$.subscribe(() => {
      const keyTipoDocumento = `${node.documento.value.tipoDocumento ? node.documento.value.tipoDocumento.id : 0}`;
      const keySubtipoDocumento = `${keyTipoDocumento}-${node.documento.value.subtipoDocumento ? node.documento.value.subtipoDocumento.id : 0}`;
      let nodeTipoDocumento = this.nodeLookup.get(keyTipoDocumento);
      let addToRoot = false;
      let removedRootNode: NodeDocumento;
      if (!nodeTipoDocumento) {
        nodeTipoDocumento = new NodeDocumento(keyTipoDocumento, node.documento.value.tipoDocumento?.nombre, 0);
        this.nodeLookup.set(keyTipoDocumento, nodeTipoDocumento);
        addToRoot = true;
      }
      let nodeSubtipoDocumento = this.nodeLookup.get(keySubtipoDocumento);
      if (!nodeSubtipoDocumento) {
        nodeSubtipoDocumento = new NodeDocumento(keySubtipoDocumento, node.documento.value.subtipoDocumento?.nombre, 1);
        nodeTipoDocumento.addChild(nodeSubtipoDocumento);
        this.nodeLookup.set(keySubtipoDocumento, nodeSubtipoDocumento);
      }
      // Si el padre ha cambiado limpiamos la rama y establecemos el nuevo padre
      if (nodeSubtipoDocumento !== node.parent) {
        node.parent.removeChild(node);
        removedRootNode = this.removeEmptyParentNodes(node.parent);
        nodeSubtipoDocumento.addChild(node);
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
        current.push(nodeTipoDocumento);
      }
      this.publishNodes(current);
      this.setChanges(true);
    });
  }

  /**
   * Si el documento ya esta creado lo anade a la lista de elementos a eliminar
   * y si no esta persistido aun se elimina directamente el documento asociado.
   */
  public deleteNode(node: NodeDocumento): void {
    let removedRootNode: NodeDocumento;
    let deleteDocumento$ = of(void 0);

    if (node.documento.created) {
      deleteDocumento$ = this.documentoService.eliminarFichero(node.documento.value.documento.documentoRef);
    } else {
      this.documentosEliminados.push(node.documento.value);
    }

    deleteDocumento$.subscribe(() => {
      node.parent.removeChild(node);
      removedRootNode = this.removeEmptyParentNodes(node.parent);

      let current = this.documentos$.value;
      if (removedRootNode) {
        current = current.filter((n) => n !== removedRootNode);
      }

      this.publishNodes(current);
      this.setChanges(true);
    });
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
      this.createDocumentos(this.getCreated(this.documentos$.value)),
      this.deleteDocumentosUnrelated()
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
      mergeMap((empresaDocumento) => {
        return this.empresaDocumentoService.delete(empresaDocumento.id)
          .pipe(
            tap(() => {
              this.documentosEliminados = this.documentosEliminados.filter(deleted =>
                deleted.id !== empresaDocumento.id);
            }),
            switchMap(() => this.documentoService.eliminarFichero(empresaDocumento.documento.documentoRef))
          );
      }));
  }

  private deleteDocumentosUnrelated(): Observable<void> {
    if (this.documentosRefUnrelated.length === 0) {
      return of(void 0);
    }

    return from(this.documentosRefUnrelated).pipe(
      mergeMap(documentoRef =>
        this.documentoService.eliminarFichero(documentoRef)
          .pipe(
            tap(() =>
              this.documentosRefUnrelated = this.documentosRefUnrelated
                .filter(documentoRefEliminado => documentoRefEliminado !== documentoRef)
            )
          )
      ),
      takeLast(1)
    );
  }

  private updateDocumentos(nodes: NodeDocumento[]): Observable<void> {
    if (nodes.length === 0) {
      return of(void 0);
    }
    return from(nodes).pipe(
      mergeMap((node) => {
        return this.empresaDocumentoService.update(node.documento.value.id, node.documento.value).pipe(
          map((updated) => {
            updated.documento = node.documento.value.documento;
            node.documento = new StatusWrapper<IEmpresaDocumento>(updated);
          })
        );
      }));
  }

  private createDocumentos(nodes: NodeDocumento[]): Observable<void> {
    if (nodes.length === 0) {
      return of(void 0);
    }
    return from(nodes).pipe(
      mergeMap(node => {
        node.documento.value.empresa = this.empresa;
        return this.empresaDocumentoService.create(node.documento.value).pipe(
          map(created => {
            created.documento = node.documento.value.documento;
            node.documento = new StatusWrapper<IEmpresaDocumento>(created);
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

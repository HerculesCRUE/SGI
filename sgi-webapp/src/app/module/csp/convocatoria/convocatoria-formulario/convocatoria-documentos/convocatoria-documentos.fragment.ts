import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConvocatoriaDocumento } from '@core/models/csp/convocatoria-documento';
import { IDocumento } from '@core/models/sgdoc/documento';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaDocumentoService } from '@core/services/csp/convocatoria-documento.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class NodeDocumento {
  parent: NodeDocumento;
  key: string;
  title: string;
  documento?: StatusWrapper<IConvocatoriaDocumento>;
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

  constructor(key: string, title: string, level: number, documento?: StatusWrapper<IConvocatoriaDocumento>) {
    this.key = key;
    this.title = title;
    this._level = level;
    if (level === 0 && !title) {
      this.title = marker('label.csp.documentos.sin-fase');
    }
    else if (level === 1 && !title) {
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

export class ConvocatoriaDocumentosFragment extends Fragment {
  documentos$ = new BehaviorSubject<NodeDocumento[]>([]);
  private documentosEliminados: IConvocatoriaDocumento[] = [];
  private documentosRefUnrelated: string[] = [];

  private nodeLookup = new Map<string, NodeDocumento>();

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private convocatoriaService: ConvocatoriaService,
    private convocatoriaDocumentoService: ConvocatoriaDocumentoService,
    private documentoService: DocumentoService,
    public readonly: boolean,
    public canEdit: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.convocatoriaService.findDocumentos(this.getKey() as number).pipe(
        map(response => {
          return this.buildTree(response.items);
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

  private buildTree(documentos: IConvocatoriaDocumento[]): NodeDocumento[] {
    const nodes: NodeDocumento[] = [];
    documentos.forEach((documento) => {
      const keyTipoFase = `${documento.tipoFase ? documento.tipoFase.id : 0}`;
      const keyTipoDocumento = `${keyTipoFase}-${documento.tipoDocumento ? documento.tipoDocumento.id : 0}`;
      let faseNode = this.nodeLookup.get(keyTipoFase);
      if (!faseNode) {
        faseNode = new NodeDocumento(keyTipoFase, documento.tipoFase?.nombre, 0);
        this.nodeLookup.set(keyTipoFase, faseNode);
        nodes.push(faseNode);
      }
      let tipoDocNode = this.nodeLookup.get(keyTipoDocumento);
      if (!tipoDocNode) {
        tipoDocNode = new NodeDocumento(keyTipoDocumento, documento.tipoDocumento?.nombre, 1);
        faseNode.addChild(tipoDocNode);
        this.nodeLookup.set(keyTipoDocumento, tipoDocNode);
      }
      const docNode = new NodeDocumento(null, documento.nombre, 2, new StatusWrapper<IConvocatoriaDocumento>(documento));
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
    const keyTipoFase = `${node.documento.value.tipoFase ? node.documento.value.tipoFase.id : 0}`;
    const keyTipoDocumento = `${keyTipoFase}-${node.documento.value.tipoDocumento ? node.documento.value.tipoDocumento.id : 0}`;
    let nodeFase = this.nodeLookup.get(keyTipoFase);
    let addToRoot = false;
    if (!nodeFase) {
      nodeFase = new NodeDocumento(keyTipoFase, node.documento.value.tipoFase?.nombre, 0);
      this.nodeLookup.set(keyTipoFase, nodeFase);
      addToRoot = true;
    }
    let nodeTipoDoc = this.nodeLookup.get(keyTipoDocumento);
    if (!nodeTipoDoc) {
      nodeTipoDoc = new NodeDocumento(keyTipoDocumento, node.documento.value.tipoDocumento?.nombre, 1);
      nodeFase.addChild(nodeTipoDoc);
      this.nodeLookup.set(keyTipoDocumento, nodeTipoDoc);
    }
    const nodeDocumento = new NodeDocumento(keyTipoDocumento, node.title, 2, node.documento);
    nodeDocumento.documento.setCreated();
    nodeDocumento.fichero = node.fichero;
    nodeDocumento.documento.value.documentoRef = node.fichero?.documentoRef;
    nodeTipoDoc.addChild(nodeDocumento);
    const current = this.documentos$.value;
    if (addToRoot) {
      current.push(nodeFase);
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
    node.documento.value.documentoRef = node.fichero?.documentoRef;

    let deleteDocumento$ = of(void 0);
    if (!!previousDocumentoRef && node.documento.value.documentoRef !== previousDocumentoRef) {
      if (node.documento.created) {
        deleteDocumento$ = this.documentoService.eliminarFichero(previousDocumentoRef);
      } else {
        this.documentosRefUnrelated.push(previousDocumentoRef);
      }
    }

    deleteDocumento$.subscribe(() => {
      const keyTipoFase = `${node.documento.value.tipoFase ? node.documento.value.tipoFase.id : 0}`;
      const keyTipoDocumento = `${keyTipoFase}-${node.documento.value.tipoDocumento ? node.documento.value.tipoDocumento.id : 0}`;
      let nodeFase = this.nodeLookup.get(keyTipoFase);
      let addToRoot = false;
      let removedRootNode: NodeDocumento;
      if (!nodeFase) {
        nodeFase = new NodeDocumento(keyTipoFase, node.documento.value.tipoFase?.nombre, 0);
        this.nodeLookup.set(keyTipoFase, nodeFase);
        addToRoot = true;
      }
      let nodeTipoDoc = this.nodeLookup.get(keyTipoDocumento);
      if (!nodeTipoDoc) {
        nodeTipoDoc = new NodeDocumento(keyTipoDocumento, node.documento.value.tipoDocumento?.nombre, 1);
        nodeFase.addChild(nodeTipoDoc);
        this.nodeLookup.set(keyTipoDocumento, nodeTipoDoc);
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
        current.push(nodeFase);
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
      deleteDocumento$ = this.documentoService.eliminarFichero(node.documento.value.documentoRef);
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
      mergeMap((documento) => {
        return this.convocatoriaDocumentoService.deleteById(documento.id)
          .pipe(
            tap(() => {
              this.documentosEliminados = this.documentosEliminados
                .filter(deleted => deleted.id !== documento.id);
            }),
            switchMap(() => this.documentoService.eliminarFichero(documento.documentoRef))
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
        return this.convocatoriaDocumentoService.update(node.documento.value.id, node.documento.value).pipe(
          map((updated) => {
            node.documento = new StatusWrapper<IConvocatoriaDocumento>(updated);
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
        node.documento.value.convocatoriaId = this.getKey() as number;
        return this.convocatoriaDocumentoService.create(node.documento.value).pipe(
          map(created => {
            node.documento = new StatusWrapper<IConvocatoriaDocumento>(created);
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

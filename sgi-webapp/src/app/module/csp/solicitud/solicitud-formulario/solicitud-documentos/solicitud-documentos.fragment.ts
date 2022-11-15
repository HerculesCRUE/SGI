import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IDocumentoRequeridoSolicitud } from '@core/models/csp/documento-requerido-solicitud';
import { ISolicitudDocumento } from '@core/models/csp/solicitud-documento';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { IDocumento } from '@core/models/sgdoc/documento';
import { Fragment } from '@core/services/action-service';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { SolicitudDocumentoService } from '@core/services/csp/solicitud-documento.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

const SIN_TIPO_DOCUMENTO = marker('label.csp.documento.sin-tipo');

export class NodeDocumentoSolicitud {
  parent: NodeDocumentoSolicitud;
  key: string;
  title: string;
  required: boolean;
  documento?: StatusWrapper<ISolicitudDocumento>;
  fichero?: IDocumento;
  // tslint:disable-next-line: variable-name
  _level: number;
  // tslint:disable-next-line: variable-name
  _childs: NodeDocumentoSolicitud[];
  get childs(): NodeDocumentoSolicitud[] {
    return this._childs;
  }

  get level(): number {
    return this._level;
  }

  constructor(
    key: string, title: string, level: number, required: boolean,
    documento?: StatusWrapper<ISolicitudDocumento>) {
    this.key = key;
    this.title = title;
    this._level = level;
    if (level === 0 && !title) {
      this.title = SIN_TIPO_DOCUMENTO;
    }
    this.required = required;
    this.documento = documento;
    this._childs = [];
  }

  addChild(child: NodeDocumentoSolicitud) {
    child.parent = this;
    this._childs.push(child);
    this.sortChildsByTitle();
  }

  removeChild(child: NodeDocumentoSolicitud) {
    this._childs = this._childs.filter((documento) => documento !== child);
  }

  sortChildsByTitle(): void {
    this._childs = sortByTitle(this._childs);
  }
}

function sortByTitle(nodes: NodeDocumentoSolicitud[]): NodeDocumentoSolicitud[] {
  return nodes.sort((a, b) => {
    if ((a.level === 0 || b.level === 0)) {
      if (a.key < b.key) {
        return -1;
      }
      if (a.key > b.key) {
        return 1;
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

export class SolicitudDocumentosFragment extends Fragment {
  documentos$ = new BehaviorSubject<NodeDocumentoSolicitud[]>([]);
  private documentosEliminados: ISolicitudDocumento[] = [];
  private documentosRefUnrelated: string[] = [];

  private nodeLookup = new Map<string, NodeDocumentoSolicitud>();

  private tiposDocumentosIdsRequired: string[] = [];

  get hasRequiredDocumentos(): boolean {
    return this.tiposDocumentosIdsRequired.every(tipoRequeridoId =>
      this.nodeLookup.get(tipoRequeridoId).childs.length > 0);
  }

  constructor(
    private readonly logger: NGXLogger,
    private solicitudId: number,
    private convocatoriaId: number,
    private configuracionSolicitudService: ConfiguracionSolicitudService,
    private solicitudService: SolicitudService,
    private solicitudDocumentoService: SolicitudDocumentoService,
    private documentoService: DocumentoService,
    public readonly: boolean,
    public addDocumentosDisabled: boolean
  ) {
    super(solicitudId);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    let convocatoriaDocumentoRequeridoSolicitud$: Observable<IDocumentoRequeridoSolicitud[]>;
    if (this.convocatoriaId) {
      convocatoriaDocumentoRequeridoSolicitud$ =
        this.configuracionSolicitudService.findAllConvocatoriaDocumentoRequeridoSolicitud(this.convocatoriaId).pipe(
          map((result) => result.items),
          tap(documentosRequeridos => this.tiposDocumentosIdsRequired = documentosRequeridos.map(tipo => tipo.tipoDocumento.id.toString()))
        );
    } else {
      convocatoriaDocumentoRequeridoSolicitud$ = of([]);
    }

    const subscription = convocatoriaDocumentoRequeridoSolicitud$.pipe(
      map((documentosRequeridos) => documentosRequeridos.map(
        (documento) => this.createNode(documento.tipoDocumento, [], true))
      ),
      switchMap((nodes) => {
        return this.solicitudService.findDocumentos(this.solicitudId).pipe(
          map((solicitudDocumentos) => nodes.concat(this.buildTree(solicitudDocumentos.items)))
        );
      })
    ).subscribe(
      (nodes) => {
        this.publishNodes(nodes);
      },
      (error) => {
        this.logger.error(error);
      }
    );
    this.subscriptions.push(subscription);
  }

  private createNode(tipoDocumento: ITipoDocumento, nodes: NodeDocumentoSolicitud[], required: boolean): NodeDocumentoSolicitud {
    const keyTipoDocumento = `${tipoDocumento ? tipoDocumento.id : 0}`;
    const tipoDocNode = new NodeDocumentoSolicitud(keyTipoDocumento, tipoDocumento?.nombre, 0, required);
    this.nodeLookup.set(keyTipoDocumento, tipoDocNode);
    nodes.push(tipoDocNode);
    return tipoDocNode;
  }

  private buildTree(documentos: ISolicitudDocumento[]): NodeDocumentoSolicitud[] {
    const nodes: NodeDocumentoSolicitud[] = [];
    documentos.forEach((documento: ISolicitudDocumento) => {
      const keyTipoDocumento = `${documento.tipoDocumento ? documento.tipoDocumento?.id : 0}`;
      let tipoDocNode = this.nodeLookup.get(keyTipoDocumento);
      if (!tipoDocNode) {
        tipoDocNode = this.createNode(documento.tipoDocumento, nodes, false);
      }
      const docNode = new NodeDocumentoSolicitud('', documento.nombre, 1, false,
        new StatusWrapper<ISolicitudDocumento>(documento));
      tipoDocNode.addChild(docNode);
    });
    return nodes;
  }

  publishNodes(rootNodes?: NodeDocumentoSolicitud[]) {
    let nodes = rootNodes ? rootNodes : this.documentos$.value;
    nodes = sortByTitle(nodes);
    this.documentos$.next(nodes);
  }

  public addNode(node: NodeDocumentoSolicitud): NodeDocumentoSolicitud {
    const keyTipoDocumento = `${node.documento.value.tipoDocumento ? node.documento.value.tipoDocumento.id : 0}`;
    let nodeTipoDoc = this.nodeLookup.get(keyTipoDocumento);
    let addToRoot = false;
    if (!nodeTipoDoc) {
      nodeTipoDoc = new NodeDocumentoSolicitud(keyTipoDocumento, node.documento.value.tipoDocumento?.nombre, 0, false);
      this.nodeLookup.set(keyTipoDocumento, nodeTipoDoc);
      addToRoot = true;
    }
    const nodeDocumento = new NodeDocumentoSolicitud(keyTipoDocumento, node.title, 1, false, node.documento);
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

  /**
   * Actualiza el documento y si se modifica el documento asociado lo anade a la lista de documentos a eliminar
   * y si no esta persistido aun se elimina directamente.
   */
  public updateNode(node: NodeDocumentoSolicitud, previousDocumentoRef: string): void {
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
      const keyTipoDocumento = `${node.documento.value.tipoDocumento ? node.documento.value.tipoDocumento.id : 0}`;
      let nodeTipoDoc = this.nodeLookup.get(keyTipoDocumento);
      if (!nodeTipoDoc) {
        nodeTipoDoc = new NodeDocumentoSolicitud(keyTipoDocumento, node.documento.value.tipoDocumento?.nombre, 1, false);
        this.nodeLookup.set(keyTipoDocumento, nodeTipoDoc);
      }
      // Si el padre ha cambiado limpiamos la rama y establecemos el nuevo padre
      if (nodeTipoDoc !== node.parent) {
        node.parent.removeChild(node);
        nodeTipoDoc.addChild(node);
      }
      else {
        // Ordenamos los hijos, porque puede haber cambiado el nombre
        node.parent.sortChildsByTitle();
      }
      const current = this.documentos$.value;
      this.publishNodes(current);
      this.setChanges(true);
    });
  }

  /**
   * Si el documento ya esta creado lo anade a la lista de elementos a eliminar
   * y si no esta persistido aun se elimina directamente el documento asociado.
   */
  deleteNode(node: NodeDocumentoSolicitud): void {
    let deleteDocumento$ = of(void 0);

    if (node.documento.created) {
      deleteDocumento$ = this.documentoService.eliminarFichero(node.documento.value.documentoRef);
    } else {
      this.documentosEliminados.push(node.documento.value);
    }

    deleteDocumento$.subscribe(() => {
      node.parent.removeChild(node);
      let current = this.documentos$.value;
      if (node) {
        current = current.filter((n) => n !== node);
      }
      this.publishNodes(current);
      this.setChanges(true);
    });
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

  private deleteDocumentos(): Observable<void> {
    if (this.documentosEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.documentosEliminados).pipe(
      mergeMap((documento) => {
        return this.solicitudDocumentoService.deleteById(documento.id)
          .pipe(
            tap(() => {
              this.documentosEliminados = this.documentosEliminados
                .filter(deleted => deleted.id !== documento.id);
            }),
            switchMap(() => this.documentoService.eliminarFichero(documento.documentoRef))
          );
      })
    );
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

  private updateDocumentos(nodes: NodeDocumentoSolicitud[]): Observable<void> {
    if (nodes.length === 0) {
      return of(void 0);
    }
    return from(nodes).pipe(
      mergeMap((node) => {
        return this.solicitudDocumentoService.update(node.documento.value.id, node.documento.value).pipe(
          map((updated) => {
            node.documento = new StatusWrapper<ISolicitudDocumento>(updated);
          })
        );
      })
    );
  }

  private getUpdated(nodes: NodeDocumentoSolicitud[]): NodeDocumentoSolicitud[] {
    const updated: NodeDocumentoSolicitud[] = [];
    nodes.forEach((node) => {
      if (node.documento && node.documento.edited) {
        updated.push(node);
      }
      if (node.childs.length) {
        updated.push(...this.getUpdated(node.childs));
      }
    });
    return updated;
  }

  private createDocumentos(nodes: NodeDocumentoSolicitud[]): Observable<void> {
    if (nodes.length === 0) {
      return of(void 0);
    }
    return from(nodes).pipe(
      mergeMap(node => {
        node.documento.value.solicitudId = this.getKey() as number;
        return this.solicitudDocumentoService.create(node.documento.value).pipe(
          map(created => {
            node.documento = new StatusWrapper<ISolicitudDocumento>(created);
          })
        );
      })
    );
  }

  private getCreated(nodes: NodeDocumentoSolicitud[]): NodeDocumentoSolicitud[] {
    const updated: NodeDocumentoSolicitud[] = [];
    nodes.forEach((node) => {
      if (node.documento && node.documento.created) {
        updated.push(node);
      }
      if (node.childs.length) {
        updated.push(...this.getCreated(node.childs));
      }
    });
    return updated;
  }

  private isSaveOrUpdateComplete(nodes: NodeDocumentoSolicitud[]): boolean {
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

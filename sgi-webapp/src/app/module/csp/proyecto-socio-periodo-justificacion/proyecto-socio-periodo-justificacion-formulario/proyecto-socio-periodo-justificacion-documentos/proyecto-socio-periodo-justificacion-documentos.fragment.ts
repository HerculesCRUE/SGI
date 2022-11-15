import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyectoSocioPeriodoJustificacionDocumento } from '@core/models/csp/proyecto-socio-periodo-justificacion-documento';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { IDocumento } from '@core/models/sgdoc/documento';
import { Fragment } from '@core/services/action-service';
import { ProyectoSocioPeriodoJustificacionDocumentoService } from '@core/services/csp/proyecto-socio-periodo-justificacion-documento.service';
import { ProyectoSocioPeriodoJustificacionService } from '@core/services/csp/proyecto-socio-periodo-justificacion.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

const SIN_TIPO_DOCUMENTO = marker('label.csp.documento.sin-tipo');

export class NodeDocumentoProyecto {
  parent: NodeDocumentoProyecto;
  key: string;
  title: string;
  documento?: StatusWrapper<IProyectoSocioPeriodoJustificacionDocumento>;
  fichero?: IDocumento;
  // tslint:disable-next-line: variable-name
  _level: number;
  // tslint:disable-next-line: variable-name
  _childs: NodeDocumentoProyecto[];
  get childs(): NodeDocumentoProyecto[] {
    return this._childs;
  }

  get level(): number {
    return this._level;
  }

  constructor(key: string, title: string, level: number, documento?: StatusWrapper<IProyectoSocioPeriodoJustificacionDocumento>) {
    this.key = key;
    this.title = title;
    this._level = level;
    if (level === 0 && !title) {
      this.title = SIN_TIPO_DOCUMENTO;
    }
    this.documento = documento;
    this._childs = [];
  }

  addChild(child: NodeDocumentoProyecto) {
    child.parent = this;
    this._childs.push(child);
    this.sortChildsByTitle();
  }

  removeChild(child: NodeDocumentoProyecto) {
    this._childs = this._childs.filter((documento) => documento !== child);
  }

  sortChildsByTitle(): void {
    this._childs = sortByTitle(this._childs);
  }
}

function sortByTitle(nodes: NodeDocumentoProyecto[]): NodeDocumentoProyecto[] {
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

export class ProyectoSocioPeriodoJustificacionDocumentosFragment extends Fragment {
  documentos$ = new BehaviorSubject<NodeDocumentoProyecto[]>([]);
  private documentosRefUnrelated: string[] = [];

  private nodeLookup = new Map<string, NodeDocumentoProyecto>();

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private proyectoSocioPeriodoJustificacionService: ProyectoSocioPeriodoJustificacionService,
    private proyectoSocioPeriodoJustificacionDocumentoService: ProyectoSocioPeriodoJustificacionDocumentoService,
    private documentoService: DocumentoService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.proyectoSocioPeriodoJustificacionService.findAllProyectoSocioPeriodoJustificacionDocumento(id).pipe(
          map((result) => result.items),
          map((documentosRequeridos) => this.buildTree(documentosRequeridos))
        ).subscribe(
          (nodes) => {
            this.publishNodes(nodes);
          },
          (error) => {
            this.logger.error(error);
          }
        )
      );
    }
  }

  private createNode(tipoDocumento: ITipoDocumento, nodes: NodeDocumentoProyecto[]): NodeDocumentoProyecto {
    const keyTipoDocumento = `${tipoDocumento ? tipoDocumento.id : 0}`;
    const tipoDocNode = new NodeDocumentoProyecto(keyTipoDocumento, tipoDocumento?.nombre, 0);
    this.nodeLookup.set(keyTipoDocumento, tipoDocNode);
    nodes.push(tipoDocNode);
    return tipoDocNode;
  }

  private buildTree(documentos: IProyectoSocioPeriodoJustificacionDocumento[]): NodeDocumentoProyecto[] {
    const nodes: NodeDocumentoProyecto[] = [];
    this.nodeLookup = new Map<string, NodeDocumentoProyecto>();
    documentos.forEach((documento: IProyectoSocioPeriodoJustificacionDocumento) => {
      const keyTipoDocumento = `${documento.tipoDocumento ? documento.tipoDocumento?.id : 0}`;
      let tipoDocNode = this.nodeLookup.get(keyTipoDocumento);
      if (!tipoDocNode) {
        tipoDocNode = this.createNode(documento.tipoDocumento, nodes);
      }
      const docNode = new NodeDocumentoProyecto('', documento.nombre, 1,
        new StatusWrapper<IProyectoSocioPeriodoJustificacionDocumento>(documento));
      tipoDocNode.addChild(docNode);
    });
    return nodes;
  }

  publishNodes(rootNodes?: NodeDocumentoProyecto[]) {
    let nodes = rootNodes ? rootNodes : this.documentos$.value;
    nodes = sortByTitle(nodes);
    this.documentos$.next(nodes);
  }

  public addNode(node: NodeDocumentoProyecto): NodeDocumentoProyecto {
    const keyTipoDocumento = `${node.documento.value.tipoDocumento ? node.documento.value.tipoDocumento.id : 0}`;
    let nodeTipoDoc = this.nodeLookup.get(keyTipoDocumento);
    let addToRoot = false;
    if (!nodeTipoDoc) {
      nodeTipoDoc = new NodeDocumentoProyecto(keyTipoDocumento, node.documento.value.tipoDocumento?.nombre, 0);
      this.nodeLookup.set(keyTipoDocumento, nodeTipoDoc);
      addToRoot = true;
    }
    const nodeDocumento = new NodeDocumentoProyecto(keyTipoDocumento, node.title, 1, node.documento);
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
  public updateNode(node: NodeDocumentoProyecto, previousDocumentoRef: string): void {
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
        nodeTipoDoc = new NodeDocumentoProyecto(keyTipoDocumento, node.documento.value.tipoDocumento?.nombre, 1);
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
  deleteNode(node: NodeDocumentoProyecto): void {
    this.documentosRefUnrelated.push(node.documento.value.documentoRef);

    node.parent.removeChild(node);
    let current = this.documentos$.value;
    if (node) {
      current = current.filter((n) => n !== node);
    }
    this.publishNodes(current);
    this.setChanges(true);
  }

  private getDocumentos(nodes: NodeDocumentoProyecto[]): IProyectoSocioPeriodoJustificacionDocumento[] {
    const documentos: IProyectoSocioPeriodoJustificacionDocumento[] = [];
    nodes.forEach((node) => {
      if (node.documento) {
        documentos.push(node.documento.value);
      }
      if (node.childs.length) {
        documentos.push(...this.getDocumentos(node.childs));
      }
    });
    return documentos;
  }

  saveOrUpdate(): Observable<void> {
    const documentos = this.getDocumentos(this.documentos$.value);
    documentos.forEach(documento => {
      if (!documento.proyectoSocioPeriodoJustificacionId) {
        documento.proyectoSocioPeriodoJustificacionId = this.getKey() as number;
      }
    });
    const id = this.getKey() as number;
    return this.proyectoSocioPeriodoJustificacionDocumentoService.updateList(id, documentos)
      .pipe(
        switchMap(results => this.deleteDocumentosUnrelated().pipe(map(() => results))),
        map((results) => {
          this.documentos$.next(this.buildTree(results));
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

}

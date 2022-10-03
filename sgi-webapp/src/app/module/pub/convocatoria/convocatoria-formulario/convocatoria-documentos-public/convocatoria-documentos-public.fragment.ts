import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConvocatoriaDocumento } from '@core/models/csp/convocatoria-documento';
import { IDocumento } from '@core/models/sgdoc/documento';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

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

export class ConvocatoriaDocumentosPublicFragment extends Fragment {
  documentos$ = new BehaviorSubject<NodeDocumento[]>([]);

  private nodeLookup = new Map<string, NodeDocumento>();

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private convocatoriaService: ConvocatoriaPublicService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.convocatoriaService.findDocumentos(this.getKey() as number).pipe(
        map(response => {
          // TODO este filtro debería hacerse en el back
          const items = response.items.filter(documento => documento.publico);
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

  saveOrUpdate(): Observable<void> {
    throw new Error('Method not implemented.');
  }

}

import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoDocumento } from '@core/models/csp/proyecto-documento';
import { ITipoDocumento, ITipoFase } from '@core/models/csp/tipos-configuracion';
import { IDocumento } from '@core/models/sgdoc/documento';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoDocumentoService } from '@core/services/csp/proyecto-documento.service';
import { ProyectoPeriodoSeguimientoService } from '@core/services/csp/proyecto-periodo-seguimiento.service';
import { ProyectoProrrogaService } from '@core/services/csp/proyecto-prorroga.service';
import { ProyectoSocioPeriodoJustificacionService } from '@core/services/csp/proyecto-socio-periodo-justificacion.service';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

const SIN_TIPO_FASE = marker('label.csp.documentos.sin-fase');
const SIN_TIPO_DOCUMENTO = marker('label.csp.documentos.sin-tipo-documento');

const PERIODO_JUSTIFICACION_PERIODO_TITLE = marker('label.csp.documentos.periodo-justificacion.periodo');
const PRORROGA_PERIODO_TITLE = marker('label.csp.documentos.prorroga');
const SEGUIMIENTO_PERIODO_TITLE = marker('label.csp.documentos.periodo-seguimiento.periodo');

const PERIODO_JUSTIFICACION_TITLE = marker('label.csp.documentos.periodo-justificacion.socios');
const SEGUIMIENTO_TITLE = marker('label.csp.documentos.periodo-seguimiento');

const DOCUMENTO_CONVOCATORIA_TITLE = marker('label.csp.documentos.convocatoria');
const DOCUMENTO_SOLICITUD_TITLE = marker('label.csp.documentos.solicitud');

enum TIPO_DOCUMENTO {
  PROYECTO = '0',
  SEGUIMIENTO = '1',
  PERIODO_JUSTIFICACION = '2',
  PRORROGA = '3',
  CONVOCATORIA = '4',
  SOLICITUD = '5'
}

interface IDocumentoData {
  id: number;
  nombre: string;
  tipoFase?: ITipoFase;
  tipoDocumento: ITipoDocumento;
  comentario?: string;
  documentoRef: string;
  visible?: boolean;
  proyectoId?: number;
  convocatoriaId?: number;
  observaciones?: string;
  publico?: boolean;
}

export class NodeDocumento {
  parent: NodeDocumento;
  key: string;
  title: string;
  documento?: StatusWrapper<IDocumentoData>;
  fichero?: IDocumento;
  // tslint:disable-next-line: variable-name
  _level: number;
  tipo: TIPO_DOCUMENTO;
  readonly: boolean;
  // tslint:disable-next-line: variable-name
  _childs: NodeDocumento[];
  get childs(): NodeDocumento[] {
    return this._childs;
  }

  get level(): number {
    return this._level;
  }

  constructor(
    key: string, title: string, level: number,
    documento?: StatusWrapper<IDocumentoData>, readonly?: boolean) {
    this.key = key;
    this.title = title;

    this._level = level;
    if (((level === 0 || (level === 1 && key.startsWith('4'))) && !title)) {
      this.title = SIN_TIPO_FASE;
    } else if ((level === 1 || level === 2 || level === 3) && !title) {
      this.title = SIN_TIPO_DOCUMENTO;
    }
    if (documento) {
      this.documento = documento;
    }

    this._childs = [];
    this.readonly = readonly;
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
    if ((a.level === 0 || b.level === 0) && (a.key.startsWith('0') || b.key.startsWith('0'))) {
      // A is the last
      if (!a.key.startsWith('0')) {
        return 1;
      }
      // B is the last
      if (!b.key.startsWith('0')) {
        return -1;
      }

      // A and B are tipo proyecto but different fase
      if (a.key.startsWith('0') && b.key.startsWith('0') && a.key.endsWith('-0')) {
        return 1;
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

export class ProyectoDocumentosFragment extends Fragment {
  documentos$ = new BehaviorSubject<NodeDocumento[]>([]);
  private documentosEliminados: IDocumentoData[] = [];

  private nodeLookup = new Map<string, NodeDocumento>();

  msgParamSeguimientoTitle: string;
  msgParamPeriodoJustificacionTitle: string;
  msgParamProrrogaPeriodoTitle: string;
  msgParamProrrogaTitle: string;
  msgParamConvocatoriaTitle: string;
  msgParamSolicitudTitle: string;

  constructor(
    proyectoId: number,
    private convocatoriaService: ConvocatoriaService,
    private solicitudService: SolicitudService,
    private proyectoService: ProyectoService,
    private proyectoPeriodoSeguimientoService: ProyectoPeriodoSeguimientoService,
    private proyectoSocioService: ProyectoSocioService,
    private proyectoSocioPeriodoJustificacionService: ProyectoSocioPeriodoJustificacionService,
    private proyectoProrrogaService: ProyectoProrrogaService,
    private proyectoDocumentoService: ProyectoDocumentoService,
    private empresaService: EmpresaService,
    private readonly translate: TranslateService,
    public isVisor: boolean
  ) {
    super(proyectoId);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    this.setupI18N();
    this.subscriptions.push(
      merge(
        this.loadProyectoDocumentos(),
        this.loadConvocatoriaDocumentos(),
        this.loadSolicitudDocumentos(),
        this.loadProyectoPeriodoSeguimientoDocumentos(),
        this.loadProyectoSocioPeriodoJustificacionDocumentos(),
        this.loadProyectoProrrogaDocumentos()
      ).subscribe(
        (nodes) => {
          const current = this.documentos$.value;
          this.publishNodes(current.concat(nodes));
        }
      )
    );
  }

  private loadProyectoDocumentos(): Observable<NodeDocumento[]> {
    return this.proyectoService.findAllProyectoDocumentos(this.getKey() as number).pipe(
      map(documentos => {
        const keyTipo = TIPO_DOCUMENTO.PROYECTO;
        const nodes: NodeDocumento[] = [];
        documentos.items.forEach((documento) => {
          const keyTipoFase = `${keyTipo}-${documento.tipoFase ? documento.tipoFase.id : 0}`;
          let faseNode = this.nodeLookup.get(keyTipoFase);
          if (!faseNode) {
            faseNode = new NodeDocumento(keyTipoFase, documento.tipoFase?.nombre, 0);
            this.nodeLookup.set(keyTipoFase, faseNode);
            nodes.push(faseNode);
          }
          const keyTipoDocumento = `${keyTipoFase}-${documento.tipoDocumento ? documento.tipoDocumento.id : 0}`;
          let tipoDocumentoNode = this.nodeLookup.get(keyTipoDocumento);
          if (!tipoDocumentoNode) {
            tipoDocumentoNode = new NodeDocumento(keyTipoDocumento, documento.tipoDocumento?.nombre, 1);
            this.nodeLookup.set(keyTipoDocumento, tipoDocumentoNode);
            faseNode.addChild(tipoDocumentoNode);
          }
          const documentoNode = new NodeDocumento(null, documento.nombre, 2, new StatusWrapper<IDocumentoData>(documento), false);
          tipoDocumentoNode.addChild(documentoNode);
        });
        return nodes;
      })
    );
  }

  private loadConvocatoriaDocumentos(): Observable<NodeDocumento[]> {
    return this.proyectoService.findById(this.getKey() as number).pipe(
      switchMap((proyecto) => {
        if (proyecto.convocatoriaId) {
          return this.convocatoriaService.findDocumentos(proyecto.convocatoriaId).pipe(
            map(response => response.items)
          );
        }
        return of([]);
      }),
      map((response => {
        if (response) {
          response = response.filter(documento => documento.publico);
          const keyTipo = TIPO_DOCUMENTO.CONVOCATORIA;
          const nodes: NodeDocumento[] = [];
          response.forEach((documento) => {
            documento.comentario = documento.observaciones;
            documento.visible = documento.publico;
            let tipoNode = this.nodeLookup.get(keyTipo);
            if (!tipoNode) {
              tipoNode = new NodeDocumento(keyTipo, this.msgParamConvocatoriaTitle, 0);
              this.nodeLookup.set(keyTipo, tipoNode);
              nodes.push(tipoNode);
            }

            const keyTipoFase = `${keyTipo}-${documento.tipoFase ? documento.tipoFase.id : 0}`;
            let faseNode = this.nodeLookup.get(keyTipoFase);
            if (!faseNode) {
              faseNode = new NodeDocumento(keyTipoFase, documento.tipoFase?.nombre, 1);
              this.nodeLookup.set(keyTipoFase, faseNode);
              tipoNode.addChild(faseNode);
            }

            const keyTipoDocumento = `${keyTipoFase}-${documento.tipoDocumento ? documento.tipoDocumento.id : 0}`;
            let tipoDocumentoNode = this.nodeLookup.get(keyTipoDocumento);
            if (!tipoDocumentoNode) {
              tipoDocumentoNode = new NodeDocumento(keyTipoDocumento, documento.tipoDocumento?.nombre, 2);
              this.nodeLookup.set(keyTipoDocumento, tipoDocumentoNode);
              faseNode.addChild(tipoDocumentoNode);
            }

            const documentoNode = new NodeDocumento(null, documento.nombre, 3, new StatusWrapper<IDocumentoData>(documento), false);
            documentoNode.readonly = true;
            tipoDocumentoNode.addChild(documentoNode);
          });
          return nodes;
        }
      })
      ));
  }

  private loadSolicitudDocumentos(): Observable<NodeDocumento[]> {
    return this.proyectoService.findById(this.getKey() as number).pipe(
      switchMap((proyecto) => {
        if (proyecto.solicitudId) {
          return this.solicitudService.findDocumentos(proyecto.solicitudId).pipe(
            map(response => response.items)
          );
        }
        return of([]);
      }),
      map((response => {
        if (response) {
          const keyTipo = TIPO_DOCUMENTO.SOLICITUD;
          const nodes: NodeDocumento[] = [];
          response.forEach((documento) => {
            let tipoNode = this.nodeLookup.get(keyTipo);
            if (!tipoNode) {
              tipoNode = new NodeDocumento(keyTipo, this.msgParamSolicitudTitle, 0);
              this.nodeLookup.set(keyTipo, tipoNode);
              nodes.push(tipoNode);
            }

            const keyTipoDocumento = `${keyTipo}-${documento.tipoDocumento ? documento.tipoDocumento.id : 0}`;
            let tipoDocumentoNode = this.nodeLookup.get(keyTipoDocumento);
            if (!tipoDocumentoNode) {
              tipoDocumentoNode = new NodeDocumento(keyTipoDocumento, documento.tipoDocumento?.nombre, 1);
              this.nodeLookup.set(keyTipoDocumento, tipoDocumentoNode);
              tipoNode.addChild(tipoDocumentoNode);
            }

            const documentoNode = new NodeDocumento(null, documento.nombre, 2, new StatusWrapper<IDocumentoData>(documento), false);
            documentoNode.readonly = true;
            tipoDocumentoNode.addChild(documentoNode);
          });
          return nodes;
        }
      })
      ));
  }

  private loadProyectoPeriodoSeguimientoDocumentos(): Observable<NodeDocumento[]> {
    return this.proyectoService.findAllProyectoPeriodoSeguimientoProyecto(this.getKey() as number).pipe(
      mergeMap(periodos => {
        const nodes: NodeDocumento[] = [];
        return from(periodos.items).pipe(
          mergeMap(periodo => {
            return this.proyectoPeriodoSeguimientoService.findDocumentos(periodo.id).pipe(
              map(documentos => {
                const keyTipo = TIPO_DOCUMENTO.SEGUIMIENTO;
                documentos.items.forEach((documento) => {
                  let tipoNode = this.nodeLookup.get(keyTipo);
                  if (!tipoNode) {
                    tipoNode = new NodeDocumento(keyTipo, SEGUIMIENTO_TITLE, 0);
                    this.nodeLookup.set(keyTipo, tipoNode);
                    nodes.push(tipoNode);
                  }

                  const keyPeriodo = `${keyTipo}-${periodo.id}`;
                  let periodoNode = this.nodeLookup.get(keyPeriodo);
                  if (!periodoNode) {
                    periodoNode = new NodeDocumento(keyPeriodo, this.msgParamPeriodoJustificacionTitle + ' ' + periodo.numPeriodo, 1);
                    this.nodeLookup.set(keyPeriodo, periodoNode);
                    tipoNode.addChild(periodoNode);
                  }

                  const keyTipoDocumento = `${keyPeriodo}-${documento.tipoDocumento ? documento.tipoDocumento.id : 0}`;
                  let tipoDocumentoNode = this.nodeLookup.get(keyTipoDocumento);
                  if (!tipoDocumentoNode) {
                    tipoDocumentoNode = new NodeDocumento(keyTipoDocumento, documento.tipoDocumento?.nombre, 2);
                    this.nodeLookup.set(keyTipoDocumento, tipoDocumentoNode);
                    periodoNode.addChild(tipoDocumentoNode);
                  }

                  const documentoNode = new NodeDocumento(
                    null, documento.nombre, 3, new StatusWrapper<IDocumentoData>(documento as IDocumentoData), true
                  );
                  tipoDocumentoNode.addChild(documentoNode);
                });
                return nodes;
              })
            );
          }),
        );
      }),
      takeLast(1)
    );
  }

  private loadProyectoSocioPeriodoJustificacionDocumentos(): Observable<NodeDocumento[]> {
    return this.proyectoService.findAllProyectoSocioProyecto(this.getKey() as number).pipe(
      mergeMap(socios => {
        const nodes: NodeDocumento[] = [];
        return from(socios.items).pipe(
          mergeMap(socio => {
            return this.empresaService.findById(socio.empresa.id).pipe(
              map(empresa => {
                socio.empresa = empresa;
                return socio;
              })
            );
          }),
          mergeMap(socio => {
            return this.proyectoSocioService.findAllProyectoSocioPeriodoJustificacion(socio.id).pipe(
              mergeMap(periodos => {
                return from(periodos.items).pipe(
                  mergeMap(periodo => {
                    return this.proyectoSocioPeriodoJustificacionService.findAllProyectoSocioPeriodoJustificacionDocumento(periodo.id).pipe(
                      map(documentos => {
                        const keyTipo = TIPO_DOCUMENTO.PERIODO_JUSTIFICACION;
                        documentos.items.forEach((documento) => {
                          let tipoNode = this.nodeLookup.get(keyTipo);
                          if (!tipoNode) {
                            tipoNode = new NodeDocumento(keyTipo, PERIODO_JUSTIFICACION_TITLE, 0);
                            this.nodeLookup.set(keyTipo, tipoNode);
                            nodes.push(tipoNode);
                          }

                          const keySocio = `${keyTipo}-${periodo.proyectoSocioId}`;
                          let socioNode = this.nodeLookup.get(keySocio);
                          if (!socioNode) {
                            socioNode = new NodeDocumento(
                              keySocio, socio.empresa.nombre + ' (' + socio.empresa.numeroIdentificacion + ')', 1
                            );
                            this.nodeLookup.set(keySocio, socioNode);
                            tipoNode.addChild(socioNode);
                          }

                          const keyPeriodo = `${keySocio}-${periodo.numPeriodo}`;
                          let periodoNode = this.nodeLookup.get(keyPeriodo);
                          if (!periodoNode) {
                            periodoNode = new NodeDocumento(
                              keyPeriodo, this.msgParamPeriodoJustificacionTitle + ' ' + periodo.numPeriodo, 2
                            );
                            this.nodeLookup.set(keyPeriodo, periodoNode);
                            socioNode.addChild(periodoNode);
                          }

                          const keyTipoDocumento = `${keyPeriodo}-${documento.tipoDocumento ? documento.tipoDocumento.id : 0}`;
                          let tipoDocumentoNode = this.nodeLookup.get(keyTipoDocumento);
                          if (!tipoDocumentoNode) {
                            tipoDocumentoNode = new NodeDocumento(keyTipoDocumento, documento.tipoDocumento?.nombre, 3);
                            this.nodeLookup.set(keyTipoDocumento, tipoDocumentoNode);
                            periodoNode.addChild(tipoDocumentoNode);
                          }

                          const documentoNode = new NodeDocumento(
                            null, documento.nombre, 4, new StatusWrapper<IDocumentoData>(documento as IDocumentoData), true
                          );
                          tipoDocumentoNode.addChild(documentoNode);
                        });
                        return nodes;
                      })
                    );
                  })
                );
              })
            );
          }),
        );
      }),
      takeLast(1)
    );
  }

  private loadProyectoProrrogaDocumentos(): Observable<NodeDocumento[]> {
    return this.proyectoService.findAllProyectoProrrogaProyecto(this.getKey() as number).pipe(
      mergeMap(prorrogas => {
        const nodes: NodeDocumento[] = [];
        return from(prorrogas.items).pipe(
          mergeMap(prorroga => {
            return this.proyectoProrrogaService.findDocumentos(prorroga.id).pipe(
              map(documentos => {
                const keyTipo = TIPO_DOCUMENTO.PRORROGA;
                documentos.items.forEach((documento) => {
                  let tipoNode = this.nodeLookup.get(keyTipo);
                  if (!tipoNode) {
                    tipoNode = new NodeDocumento(keyTipo, this.msgParamProrrogaTitle, 0);
                    this.nodeLookup.set(keyTipo, tipoNode);
                    nodes.push(tipoNode);
                  }

                  const keyProrroga = `${keyTipo}-${prorroga.numProrroga}`;
                  let prorrogaNode = this.nodeLookup.get(keyProrroga);
                  if (!prorrogaNode) {
                    prorrogaNode = new NodeDocumento(keyProrroga, this.msgParamProrrogaPeriodoTitle + ' ' + prorroga.numProrroga, 1);
                    this.nodeLookup.set(keyProrroga, prorrogaNode);
                    tipoNode.addChild(prorrogaNode);
                  }

                  const keyTipoDocumento = `${keyProrroga}-${documento.tipoDocumento ? documento.tipoDocumento.id : 0}`;
                  let tipoDocumentoNode = this.nodeLookup.get(keyTipoDocumento);
                  if (!tipoDocumentoNode) {
                    tipoDocumentoNode = new NodeDocumento(keyTipoDocumento, documento.tipoDocumento?.nombre, 2);
                    this.nodeLookup.set(keyTipoDocumento, tipoDocumentoNode);
                    prorrogaNode.addChild(tipoDocumentoNode);
                  }

                  const documentoNode = new NodeDocumento(
                    null, documento.nombre, 3, new StatusWrapper<IDocumentoData>(documento as IDocumentoData), true
                  );
                  tipoDocumentoNode.addChild(documentoNode);
                });
                return nodes;
              })
            );
          })
        );
      }),
      takeLast(1)
    );
  }

  private setupI18N(): void {
    this.translate.get(
      PERIODO_JUSTIFICACION_PERIODO_TITLE
    ).subscribe((value) => this.msgParamPeriodoJustificacionTitle = value);
    this.translate.get(
      SEGUIMIENTO_PERIODO_TITLE
    ).subscribe((value) => this.msgParamSeguimientoTitle = value);
    this.translate.get(
      PRORROGA_PERIODO_TITLE,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamProrrogaPeriodoTitle = value);
    this.translate.get(
      PRORROGA_PERIODO_TITLE,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamProrrogaTitle = value);
    this.translate.get(
      DOCUMENTO_CONVOCATORIA_TITLE
    ).subscribe((value) => this.msgParamConvocatoriaTitle = value);
    this.translate.get(
      DOCUMENTO_SOLICITUD_TITLE
    ).subscribe((value) => this.msgParamSolicitudTitle = value);
  }

  publishNodes(rootNodes?: NodeDocumento[]) {
    let nodes = rootNodes ? rootNodes : this.documentos$.value;
    nodes = sortByTitle(nodes);
    this.documentos$.next(nodes);
  }

  public addNode(node: NodeDocumento): NodeDocumento {
    const keyTipoFase = `${TIPO_DOCUMENTO.PROYECTO}-${node.documento.value.tipoFase ? node.documento.value.tipoFase.id : 0}`;
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
    nodeDocumento.tipo = TIPO_DOCUMENTO.PROYECTO;
    nodeTipoDoc.addChild(nodeDocumento);
    const current = this.documentos$.value;
    if (addToRoot) {
      current.push(nodeFase);
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
    node.tipo = TIPO_DOCUMENTO.PROYECTO;

    const keyTipoFase = `${TIPO_DOCUMENTO.PROYECTO}-${node.documento.value.tipoFase ? node.documento.value.tipoFase.id : 0}`;
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
      if (node.documento && node.documento.edited && node.tipo === TIPO_DOCUMENTO.PROYECTO) {
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
      if (node.documento && node.documento.created && node.tipo === TIPO_DOCUMENTO.PROYECTO) {
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
        return this.proyectoDocumentoService.deleteById(documento.id)
          .pipe(
            tap(() => {
              this.documentosEliminados = this.documentosEliminados.filter(deleted =>
                deleted.id !== documento.id);
            })
          );
      })
    );
  }

  private updateDocumentos(nodes: NodeDocumento[]): Observable<void> {
    if (nodes.length === 0) {
      return of(void 0);
    }
    return from(nodes).pipe(
      mergeMap((node) => {
        if (node.tipo === TIPO_DOCUMENTO.PROYECTO) {
          return this.proyectoDocumentoService.update(node.documento.value.id, node.documento.value as IProyectoDocumento).pipe(
            map((updated) => {
              node.documento = new StatusWrapper<IDocumentoData>(updated);
            })
          );
        }
      })
    );
  }

  private createDocumentos(nodes: NodeDocumento[]): Observable<void> {
    if (nodes.length === 0) {
      return of(void 0);
    }
    return from(nodes).pipe(
      mergeMap(node => {
        if (node.tipo === TIPO_DOCUMENTO.PROYECTO) {
          node.documento.value.proyectoId = this.getKey() as number;
          return this.proyectoDocumentoService.create(node.documento.value as IProyectoDocumento).pipe(
            map(created => {
              node.documento = new StatusWrapper<IDocumentoData>(created);
            })
          );
        }
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

import { FlatTreeControl } from '@angular/cdk/tree';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTree, MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAreaConocimiento } from '@core/models/sgo/area-conocimiento';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';

const MSG_ERROR_LOAD = marker('error.load');
interface AreaConocimientoListado {
  niveles: IAreaConocimiento[];
  nivelesTexto: string;
  nivelSeleccionado: IAreaConocimiento;
}

export interface AreaConocimientoDataModal {
  selectedAreasConocimiento: IAreaConocimiento[];
  multiSelect: boolean;
}

class NodeAreaConocimiento {
  disabled: boolean;
  parent: NodeAreaConocimiento;
  areaConocimiento: IAreaConocimiento;
  checked: boolean;
  // tslint:disable-next-line: variable-name
  _childs: NodeAreaConocimiento[];
  // tslint:disable-next-line: variable-name
  _childsLoaded: boolean;
  // tslint:disable-next-line: variable-name
  _level: number;

  get childs(): NodeAreaConocimiento[] {
    return this._childs;
  }

  get level(): number {
    return this._level;
  }

  setChildsLoaded(): void {
    this._childsLoaded = true;
  }

  get childsLoaded() {
    return this._childsLoaded;
  }

  setCheckedAndDisabled() {
    this.checked = true;
    this.disabled = true;
  }

  constructor(areaConocimiento: IAreaConocimiento, level: number) {
    this.areaConocimiento = areaConocimiento;
    this._childs = [];
    this._childsLoaded = false;
    this._level = level;
  }

  addChild(child: NodeAreaConocimiento) {
    child.parent = this;
    this._childs.push(child);
    this.sortChildsByName();
  }

  removeChild(child: NodeAreaConocimiento) {
    this._childs = this._childs.filter((programa) => programa !== child);
  }

  sortChildsByName(): void {
    this._childs = sortByName(this._childs);
  }
}

function sortByName(nodes: NodeAreaConocimiento[]): NodeAreaConocimiento[] {
  return nodes.sort((a, b) => {
    if (a.areaConocimiento.nombre < b.areaConocimiento.nombre) {
      return -1;
    }
    if (a.areaConocimiento.nombre > b.areaConocimiento.nombre) {
      return 1;
    }
    return 0;
  });
}

@Component({
  templateUrl: './area-conocimiento-modal.component.html',
  styleUrls: ['./area-conocimiento-modal.component.scss']
})
export class AreaConocimientoModalComponent
  extends BaseModalComponent<AreaConocimientoListado[], AreaConocimientoModalComponent>
  implements OnInit {

  areasConocimientoTree$ = new BehaviorSubject<NodeAreaConocimiento[]>([]);
  readonly areasConocimiento$ = new BehaviorSubject<IAreaConocimiento[]>([]);
  selectedAreasConocimiento = [] as IAreaConocimiento[];

  @ViewChild(MatTree, { static: true }) private matTree: MatTree<NodeAreaConocimiento>;
  treeControl: FlatTreeControl<NodeAreaConocimiento>;
  private treeFlattener: MatTreeFlattener<NodeAreaConocimiento, NodeAreaConocimiento>;
  dataSource: MatTreeFlatDataSource<NodeAreaConocimiento, NodeAreaConocimiento>;

  private getLevel = (node: NodeAreaConocimiento) => node.level;
  private isExpandable = (node: NodeAreaConocimiento) => node.childs.length > 0;
  private getChildren = (node: NodeAreaConocimiento): NodeAreaConocimiento[] => node.childs;
  private transformer = (node: NodeAreaConocimiento, level: number) => node;

  hasChild = (_: number, node: NodeAreaConocimiento) => node.childs.length > 0;

  constructor(
    private readonly logger: NGXLogger,
    public matDialogRef: MatDialogRef<AreaConocimientoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AreaConocimientoDataModal,
    private areaConocimientoService: AreaConocimientoService,
    protected readonly snackBarService: SnackBarService
  ) {
    super(snackBarService, matDialogRef, null);

    this.treeFlattener = new MatTreeFlattener(this.transformer, this.getLevel, this.isExpandable, this.getChildren);
    this.treeControl = new FlatTreeControl<NodeAreaConocimiento>(this.getLevel, this.isExpandable);
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.loadInitialTree();

    this.subscriptions.push(
      this.areasConocimientoTree$
        .subscribe((areasConocimiento) => {
          this.dataSource.data = areasConocimiento;
        })
    );
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({});
    return formGroup;
  }

  protected getDatosForm(): AreaConocimientoListado[] {
    const selectedAreasConocimientoListado = this.selectedAreasConocimiento.map(areaConocimiento => {
      const areasConocimientoListado: AreaConocimientoListado = {
        niveles: [areaConocimiento],
        nivelesTexto: undefined,
        nivelSeleccionado: areaConocimiento
      };
      return this.fillAreaConocimientoListado(areasConocimientoListado);
    });
    return selectedAreasConocimientoListado;
  }

  /**
   * Actualiza los nodos del arbol que estan seleccionados.
   *
   * @param node nodo del arbol seleccionado o deseleccionado
   * @param $event evento de seleccion
   */
  onCheckNode(node: NodeAreaConocimiento, $event: MatCheckboxChange): void {
    node.checked = $event.checked;
    if ($event.checked) {
      this.selectedAreasConocimiento.push(node.areaConocimiento);
    } else {
      this.selectedAreasConocimiento = this.selectedAreasConocimiento.filter(checkedNode => checkedNode.id !== node.areaConocimiento.id);
    }
  }

  /**
   * Recupera los hijos del nodo expandido si no se han cargado previamente.
   *
   * @param node node del arbol expandido o colapsado.
   */
  onToggleNode(node: NodeAreaConocimiento): void {
    if (this.treeControl.isExpanded(node)) {
      if (!node.childsLoaded) {
        this.areaConocimientoService.findAllHijos(node.areaConocimiento.id)
          .subscribe(
            (areasConocimiento) => {
              this.buildTree(areasConocimiento.items);
              node.setChildsLoaded();
              this.publishNodes();
            },
            (error) => {
              this.logger.error(error);
              this.snackBarService.showError(MSG_ERROR_LOAD);
            }
          );
      }
    }
  }

  /**
   * Crea el arbol con los nodos de primer nivel y sus hijos
   */
  private loadInitialTree() {
    this.subscriptions.push(
      this.areaConocimientoService.findAllRamasConocimiento()
        .pipe(
          map((ramasConocimiento) => ramasConocimiento.items.map(
            (ramaConocimiento) => new NodeAreaConocimiento(ramaConocimiento, 0))
          )
        ).subscribe(
          (nodes) => {
            this.publishNodes(nodes, true);
          },
          (error) => {
            this.logger.error(error);
            this.snackBarService.showError(MSG_ERROR_LOAD);
          }
        )
    );
  }

  /**
   * Crea los nodos correspondientes a la lista de areas de conocimiento en las
   * posiciones del arbol correspondientes (añade nodos como hijos de otros nodos).
   *
   * @param areasConocimiento lista de IAreaConocimiento.
   */
  private buildTree(areasConocimiento: IAreaConocimiento[]): void {
    areasConocimiento.forEach((areaConocimiento: IAreaConocimiento) => {
      const areaConocimientoPadreNode = this.treeControl.dataNodes.find(node => node.areaConocimiento.id === areaConocimiento.padreId);
      const areaConocimientoNode = new NodeAreaConocimiento(areaConocimiento, areaConocimientoPadreNode.level + 1);
      areaConocimientoPadreNode.addChild(areaConocimientoNode);
    });
  }

  /**
   * Actualiza el arbol con la lista de nodos indicada.
   *
   * @param rootNodes (opcional) nodos raiz del arbol, si no se indica se actualiza los nodos actuales del arbol.
   * @param recreateTree (por defecto false) indica si se hace un "reset" del arbol antes de hacer la actualizacion.
   */
  private publishNodes(rootNodes?: NodeAreaConocimiento[], recreateTree = false): void {
    let nodes = rootNodes ? rootNodes : this.areasConocimientoTree$.value;
    nodes = sortByName(nodes);
    this.data.selectedAreasConocimiento?.forEach(areaSeleccionada => {
      this.treeControl.dataNodes.find(node => node.areaConocimiento.id === areaSeleccionada.id)?.setCheckedAndDisabled();
    });
    this.refreshTree(nodes, recreateTree);
    this.areasConocimientoTree$.next(nodes);
  }

  /**
   * Actualiza el arbol con los cambios realizados en la lista de nodos.
   *
   * @param nodes lista de nodos del arbol
   * @param recreate indica si se hace un "reset" del arbol antes de hacer la actualizacion (por defecto false)
   */
  private refreshTree(nodes: NodeAreaConocimiento[], recreate = false): void {
    // There is no way to update the tree without recreating it,
    // because CDK Tree detect changes only when a root nodes changes
    // See: https://github.com/angular/components/issues/11381
    if (recreate) {
      this.matTree.renderNodeChanges([]);
    }

    this.matTree.renderNodeChanges(nodes);
  }

  /**
   * Rellena el objeto AreaConocimientoListado con los niveles.
   *
   * @param areaConocimientoListado un AreaConocimientoListado.
   * @returns el AreaConocimientoListado relleno.
   */
  private fillAreaConocimientoListado(areaConocimientoListado: AreaConocimientoListado): AreaConocimientoListado {
    const lastLevel = areaConocimientoListado.niveles[areaConocimientoListado.niveles.length - 1];
    if (!lastLevel.padreId) {
      areaConocimientoListado.nivelesTexto = areaConocimientoListado.niveles
        .slice(1, areaConocimientoListado.niveles.length)
        .reverse()
        .map(clasificacion => clasificacion.nombre).join(' - ');
      return areaConocimientoListado;
    }

    let areaConocimientoPadre = this.treeControl.dataNodes.find(node => node.areaConocimiento.id === lastLevel.padreId)?.areaConocimiento;
    if (!areaConocimientoPadre) {
      areaConocimientoPadre = this.areasConocimiento$.value.find(area => area.id === lastLevel.padreId);
    }
    areaConocimientoListado.niveles.push(areaConocimientoPadre);

    return this.fillAreaConocimientoListado(areaConocimientoListado);
  }

}

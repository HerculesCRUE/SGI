import { FlatTreeControl } from '@angular/cdk/tree';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTree, MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { ClasificacionService } from '@core/services/sgo/clasificacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';

const MSG_ERROR_LOAD = marker('error.load');

export enum TipoClasificacion {
  SECTORES_INDUSTRIALES = 'SECTORES_INDUSTRIALES',
}

export interface ClasificacionDataModal {
  selectedClasificaciones: IClasificacion[];
  tipoClasificacion?: TipoClasificacion;
}

interface ClasificacionListado {
  clasificacion: IClasificacion;
  niveles: IClasificacion[];
  nivelesTexto: string;
  nivelSeleccionado: IClasificacion;
}

class NodeClasificacion {
  disabled: boolean;
  parent: NodeClasificacion;
  clasificacion: IClasificacion;
  checked: boolean;
  // tslint:disable-next-line: variable-name
  _childs: NodeClasificacion[];
  // tslint:disable-next-line: variable-name
  _childsLoaded: boolean;
  // tslint:disable-next-line: variable-name
  _level: number;

  get childs(): NodeClasificacion[] {
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

  constructor(clasificacion: IClasificacion, level: number) {
    this.clasificacion = clasificacion;
    this._childs = [];
    this._childsLoaded = false;
    this._level = level;
  }

  addChild(child: NodeClasificacion) {
    child.parent = this;
    this._childs.push(child);
    this.sortChildsByName();
  }

  removeChild(child: NodeClasificacion) {
    this._childs = this._childs.filter((programa) => programa !== child);
  }

  sortChildsByName(): void {
    this._childs = sortByName(this._childs);
  }
}

function sortByName(nodes: NodeClasificacion[]): NodeClasificacion[] {
  return nodes.sort((a, b) => {
    if (a.clasificacion.nombre < b.clasificacion.nombre) {
      return -1;
    }
    if (a.clasificacion.nombre > b.clasificacion.nombre) {
      return 1;
    }
    return 0;
  });
}

@Component({
  templateUrl: './clasificacion-modal.component.html',
  styleUrls: ['./clasificacion-modal.component.scss']
})
export class ClasificacionModalComponent
  extends BaseModalComponent<ClasificacionListado[], ClasificacionModalComponent>
  implements OnInit {

  arbolesClasificaciones: Map<string, NodeClasificacion[]> = new Map();
  readonly clasificaciones$ = new BehaviorSubject<IClasificacion[]>([]);
  clasificacionesTree$ = new BehaviorSubject<NodeClasificacion[]>([]);
  selectedClasificaciones = [] as IClasificacion[];

  @ViewChild(MatTree, { static: true }) private matTree: MatTree<NodeClasificacion>;
  treeControl: FlatTreeControl<NodeClasificacion>;
  private treeFlattener: MatTreeFlattener<NodeClasificacion, NodeClasificacion>;
  dataSource: MatTreeFlatDataSource<NodeClasificacion, NodeClasificacion>;

  private getLevel = (node: NodeClasificacion) => node.level;
  private isExpandable = (node: NodeClasificacion) => node.childs.length > 0;
  private getChildren = (node: NodeClasificacion): NodeClasificacion[] => node.childs;
  private transformer = (node: NodeClasificacion, level: number) => node;

  hasChild = (_: number, node: NodeClasificacion) => node.childs.length > 0;

  constructor(
    private readonly logger: NGXLogger,
    public matDialogRef: MatDialogRef<ClasificacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ClasificacionDataModal,
    private clasificacionService: ClasificacionService,
    protected readonly snackBarService: SnackBarService
  ) {
    super(snackBarService, matDialogRef, null);

    this.treeFlattener = new MatTreeFlattener(this.transformer, this.getLevel, this.isExpandable, this.getChildren);
    this.treeControl = new FlatTreeControl<NodeClasificacion>(this.getLevel, this.isExpandable);
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

    this.subscriptions.push(
      this.clasificacionService.findAllPadres(this.data.tipoClasificacion)
        .subscribe(response => this.clasificaciones$.next(response.items))
    );
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.subscriptions.push(
      this.formGroup.controls.clasificacion.valueChanges.subscribe((clasificacion: IClasificacion) => {
        this.loadClasificacionTree(clasificacion?.id);
      })
    );

    this.subscriptions.push(
      this.clasificacionesTree$.subscribe((clasificaciones) => {
        this.dataSource.data = clasificaciones;
      })
    );
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      clasificacion: new FormControl(null, Validators.required)
    });
    return formGroup;
  }

  protected getDatosForm(): ClasificacionListado[] {
    const selectedClasificacionesListado = this.selectedClasificaciones.map(clasificacion => {
      const clasificacionListado: ClasificacionListado = {
        clasificacion: undefined,
        niveles: [clasificacion],
        nivelesTexto: undefined,
        nivelSeleccionado: clasificacion
      };
      return this.fillClasificacionListado(clasificacionListado);
    });
    return selectedClasificacionesListado;
  }

  /**
   * Actualiza los nodos del arbol que estan seleccionados.
   *
   * @param node nodo del arbol seleccionado o deseleccionado
   * @param $event evento de seleccion
   */
  onCheckNode(node: NodeClasificacion, $event: MatCheckboxChange): void {
    node.checked = $event.checked;
    if ($event.checked) {
      this.selectedClasificaciones.push(node.clasificacion);
    } else {
      this.selectedClasificaciones = this.selectedClasificaciones.filter(checkedNode => checkedNode.id !== node.clasificacion.id);
    }
  }

  /**
   * Recupera los hijos del nodo expandido si no se han cargado previamente.
   *
   * @param node node del arbol expandido o colapsado.
   */
  onToggleNode(node: NodeClasificacion): void {
    if (this.treeControl.isExpanded(node)) {
      if (!node.childsLoaded) {
        this.clasificacionService.findAllHijos(node.clasificacion.id)
          .subscribe(
            (clasificacion) => {
              this.buildTree(clasificacion.items);
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
   * Crea el arbol de la clasificacion con los nodos de primer nivel y sus hijos
   *
   * @param clasificacionId identificador de la clasificacion
   */
  private loadClasificacionTree(clasificacionId: string): void {
    if (clasificacionId) {
      const arbolClasificacion = this.arbolesClasificaciones.get(clasificacionId);
      if (arbolClasificacion) {
        this.publishNodes(arbolClasificacion, true);
      } else {
        this.subscriptions.push(
          this.clasificacionService.findAllHijos(clasificacionId)
            .pipe(
              map((ramasConocimiento) => ramasConocimiento.items.map(
                (ramaConocimiento) => new NodeClasificacion(ramaConocimiento, 0))
              )
            ).subscribe(
              (nodes) => {
                this.publishNodes(nodes, true);
                this.arbolesClasificaciones.set(clasificacionId, nodes);
              },
              (error) => {
                this.logger.error(error);
                this.snackBarService.showError(MSG_ERROR_LOAD);
              }
            )
        );
      }
    } else {
      this.publishNodes([], true);
    }
  }

  /**
   * Crea los nodos correspondientes a la lista de clasificaciones de conocimiento en las
   * posiciones del arbol correspondientes (añade nodos como hijos de otros nodos).
   *
   * @param clasificaciones lista de IClasificacion.
   */
  private buildTree(clasificaciones: IClasificacion[]): void {
    clasificaciones.forEach((clasificacion: IClasificacion) => {
      const clasificacionPadreNode = this.treeControl.dataNodes.find(node => node.clasificacion.id === clasificacion.padreId);
      const clasificacionNode = new NodeClasificacion(clasificacion, clasificacionPadreNode.level + 1);
      clasificacionPadreNode.addChild(clasificacionNode);
    });
  }

  /**
   * Actualiza el arbol con la lista de nodos indicada.
   *
   * @param rootNodes (opcional) nodos raiz del arbol, si no se indica se actualiza los nodos actuales del arbol.
   * @param recreateTree (por defecto false) indica si se hace un "reset" del arbol antes de hacer la actualizacion.
   */
  private publishNodes(rootNodes?: NodeClasificacion[], recreateTree = false): void {
    let nodes = rootNodes ? rootNodes : this.clasificacionesTree$.value;
    nodes = sortByName(nodes);
    this.data.selectedClasificaciones?.forEach(clasificacionSeleccionada => {
      this.treeControl.dataNodes.find(node => node.clasificacion.id === clasificacionSeleccionada.id)?.setCheckedAndDisabled();
    });
    this.refreshTree(nodes, recreateTree);
    this.clasificacionesTree$.next(nodes);
  }

  /**
   * Actualiza el arbol con los cambios realizados en la lista de nodos.
   *
   * @param nodes lista de nodos del arbol
   * @param recreate indica si se hace un "reset" del arbol antes de hacer la actualizacion (por defecto false)
   */
  private refreshTree(nodes: NodeClasificacion[], recreate = false): void {
    // There is no way to update the tree without recreating it,
    // because CDK Tree detect changes only when a root nodes changes
    // See: https://github.com/angular/components/issues/11381
    if (recreate) {
      this.matTree.renderNodeChanges([]);
    }

    this.matTree.renderNodeChanges(nodes);
  }

  /**
   * Rellena el objeto clasificacionListado con los niveles y la clasificacion.
   *
   * @param clasificacionListado un ClasificacionListado.
   * @returns el ClasificacionListado relleno.
   */
  private fillClasificacionListado(clasificacionListado: ClasificacionListado): ClasificacionListado {
    const lastLevel = clasificacionListado.niveles[clasificacionListado.niveles.length - 1];
    if (!lastLevel.padreId) {
      clasificacionListado.clasificacion = lastLevel;
      clasificacionListado.nivelesTexto = clasificacionListado.niveles
        .slice(1, clasificacionListado.niveles.length - 1)
        .reverse()
        .map(clasificacion => clasificacion.nombre).join(' - ');
      return clasificacionListado;
    }

    let clasificacionPadre = this.treeControl.dataNodes.find(node => node.clasificacion.id === lastLevel.padreId)?.clasificacion;
    if (!clasificacionPadre) {
      clasificacionPadre = this.clasificaciones$.value.find(clasificacion => clasificacion.id === lastLevel.padreId);
    }
    clasificacionListado.niveles.push(clasificacionPadre);

    return this.fillClasificacionListado(clasificacionListado);
  }

}

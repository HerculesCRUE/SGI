import { SelectionModel } from '@angular/cdk/collections';
import { FlatTreeControl } from '@angular/cdk/tree';
import { Component, ComponentFactoryResolver, OnDestroy, OnInit, Type, ViewChild } from '@angular/core';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IBaremo } from '@core/models/prc/baremo';
import { TipoNodo } from '@core/models/prc/configuracion-baremo';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { BaremoPesoCuantiaComponent } from '../../components/baremo-peso-cuantia/baremo-peso-cuantia.component';
import { BaremoPesoPuntosComponent } from '../../components/baremo-peso-puntos/baremo-peso-puntos.component';
import { BaremoPesoComponent } from '../../components/baremo-peso/baremo-peso.component';
import { BaremoPuntosComponent } from '../../components/baremo-puntos/baremo-puntos.component';
import { BaremoComponent } from '../../components/baremo.component';
import { BaremoDirective } from '../../components/baremo.directive';
import { ConvocatoriaBaremacionActionService } from '../../convocatoria-baremacion.action.service';
import { ConvocatoriaBaremacionBaremosPuntuacionesFragment, NodeConfiguracionBaremo } from './convocatoria-baremacion-baremos-puntuaciones.fragment';

@Component({
  selector: 'sgi-convocatoria-baremacion-baremos-puntuaciones',
  templateUrl: './convocatoria-baremacion-baremos-puntuaciones.component.html',
  styleUrls: ['./convocatoria-baremacion-baremos-puntuaciones.component.scss']
})
export class ConvocatoriaBaremacionBaremosPuntuacionesComponent extends FragmentComponent implements OnInit, OnDestroy {
  @ViewChild(BaremoDirective, { static: true }) baremoHost: BaremoDirective;

  // tslint:disable-next-line: variable-name
  private _treeDisabled = false;

  formPart: ConvocatoriaBaremacionBaremosPuntuacionesFragment;
  private subscriptions: Subscription[] = [];
  treeControl: FlatTreeControl<NodeConfiguracionBaremo>;
  private treeFlattener: MatTreeFlattener<NodeConfiguracionBaremo, NodeConfiguracionBaremo>;
  dataSource: MatTreeFlatDataSource<NodeConfiguracionBaremo, NodeConfiguracionBaremo>;

  private getLevel = (node: NodeConfiguracionBaremo) => node.level;
  private isExpandable = (node: NodeConfiguracionBaremo) =>
    node.configuracionBaremo.tipoNodo === TipoNodo.PESO || node.configuracionBaremo.tipoNodo === TipoNodo.NO_BAREMABLE
  private getChildren = (node: NodeConfiguracionBaremo): NodeConfiguracionBaremo[] => node.childs;
  private transformer = (node: NodeConfiguracionBaremo, level: number) => node;

  isPesoPuntosNode = (_: number, node: NodeConfiguracionBaremo) => node.configuracionBaremo.tipoNodo === TipoNodo.PESO_PUNTOS;
  isPesoCuantiaNode = (_: number, node: NodeConfiguracionBaremo) => node.configuracionBaremo.tipoNodo === TipoNodo.PESO_CUANTIA;
  isPesoNode = (_: number, node: NodeConfiguracionBaremo) => node.configuracionBaremo.tipoNodo === TipoNodo.PESO;
  isNoBaremableNode = (_: number, node: NodeConfiguracionBaremo) => node.configuracionBaremo.tipoNodo === TipoNodo.NO_BAREMABLE;
  isPuntosNode = (_: number, node: NodeConfiguracionBaremo) => node.configuracionBaremo.tipoNodo === TipoNodo.PUNTOS;
  isSinPuntosNode = (_: number, node: NodeConfiguracionBaremo) => node.configuracionBaremo.tipoNodo === TipoNodo.SIN_PUNTOS;
  trackBy = (_: number, node: NodeConfiguracionBaremo) => node.configuracionBaremo.id;

  get isEditPerm(): boolean {
    return this.formPart.isEditPerm;
  }

  get checklistSelection(): SelectionModel<NodeConfiguracionBaremo> {
    return this.formPart.checklistSelection;
  }

  get treeDisabled(): boolean {
    return this._treeDisabled;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private resolver: ComponentFactoryResolver,
    private readonly actionService: ConvocatoriaBaremacionActionService,
    private readonly translateService: TranslateService
  ) {
    super(actionService.FRAGMENT.BAREMOS_PUNTUACIONES, actionService);
    this.formPart = this.fragment as ConvocatoriaBaremacionBaremosPuntuacionesFragment;
    this.treeFlattener = new MatTreeFlattener(this.transformer, this.getLevel, this.isExpandable, this.getChildren);
    this.treeControl = new FlatTreeControl<NodeConfiguracionBaremo>(this.getLevel, this.isExpandable);
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.subscriptions.push(
      this.formPart.getBaremoConfiguracionNodes$().subscribe(nodes => {
        this.dataSource.data = nodes;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /** Toggle the root node selection. Select/deselect all the descendants node */
  rootNodeSelectionToggle = (node: NodeConfiguracionBaremo): void => {
    if (this.isEditPerm) {
      this.checklistSelection.toggle(node);
      const descendants = this.treeControl.getDescendants(node)
        .filter(descendant => !!descendant.baremo || node.configuracionBaremo.tipoNodo === TipoNodo.NO_BAREMABLE);

      this.checklistSelection.isSelected(node)
        ? this.checklistSelection.select(...descendants)
        : this.checklistSelection.deselect(...descendants);
      this.formPart.checklistSelectionChanged(this.checklistSelection.selected);
    }
  }

  /** Toggle the group node selection. Select/deselect all the descendants node */
  groupNodeSelectionToggle(node: NodeConfiguracionBaremo): void {
    if (this.isEditPerm) {
      const descendants = this.treeControl.getDescendants(node).filter(descendant => !!descendant.baremo);
      const allDescendantsSelected = descendants.every(descendant => this.checklistSelection.isSelected(descendant));
      if (allDescendantsSelected) {
        this.checklistSelection.deselect(...descendants);
      } else {
        this.checklistSelection.select(...descendants);
      }
      this.checkAllParentsSelection(node);
      this.formPart.checklistSelectionChanged(this.checklistSelection.selected);
    }
  }

  /** Toggle a leaf node selection. Check all the parents to see if they changed */
  leafNodeSelectionToggle = (node: NodeConfiguracionBaremo): void => {
    if (this.isEditPerm) {
      this.checklistSelection.toggle(node);
      this.checkAllParentsSelection(node);
      this.formPart.checklistSelectionChanged(this.checklistSelection.selected);
    }
  }

  /** Whether all the descendants of the node are selected. */
  descendantsAllSelected(node: NodeConfiguracionBaremo): boolean {
    const descendants = this.treeControl.getDescendants(node).filter(descendant => !!descendant.baremo);
    const descAllSelected =
      descendants.length > 0 &&
      descendants.every(child => {
        return this.checklistSelection.isSelected(child);
      });
    return descAllSelected;
  }

  /** Whether part of the descendants are selected */
  descendantsPartiallySelected(node: NodeConfiguracionBaremo): boolean {
    const descendants = this.treeControl.getDescendants(node).filter(descendant => !!descendant.baremo);
    const result = descendants.some(child => this.checklistSelection.isSelected(child));
    return result && !this.descendantsAllSelected(node);
  }

  /** Checks all the parents when a leaf/group node is selected/unselected */
  checkAllParentsSelection(node: NodeConfiguracionBaremo): void {
    let parent = node.parent;
    while (parent) {
      if (parent.configuracionBaremo.tipoNodo !== TipoNodo.NO_BAREMABLE) {
        this.checkRootNodeSelection(parent);
      }
      parent = parent.parent;
    }
  }

  /** Check root node checked state and change it accordingly */
  checkRootNodeSelection(node: NodeConfiguracionBaremo): void {
    const nodeSelected = this.checklistSelection.isSelected(node);
    const descendants = this.treeControl.getDescendants(node);
    const isAnyDescendantSelected = descendants.some(descendant => this.checklistSelection.isSelected(descendant));
    if (nodeSelected && !isAnyDescendantSelected) {
      this.checklistSelection.deselect(node);
    } else if (!nodeSelected && isAnyDescendantSelected) {
      this.checklistSelection.select(node);
    }
  }

  isCheckboxDisabled(node: NodeConfiguracionBaremo): boolean {
    return !this.isEditPerm || this.treeDisabled || node.disabled;
  }

  isNodeNoBaremableCheckboxDisabled(node: NodeConfiguracionBaremo): boolean {
    return !this.isEditPerm ||
      this.treeDisabled ||
      !this.treeControl.getDescendants(node).some(descendantNode => descendantNode.baremo);
  }

  isRootNodeEnabled(node: NodeConfiguracionBaremo): boolean {
    const parent = node.parent;
    if (parent?.configuracionBaremo.tipoNodo === TipoNodo.NO_BAREMABLE) {
      return this.isRootNodeEnabled(parent);
    } else {
      return !parent.disabled;
    }
  }

  addPesoPuntosNode(node: NodeConfiguracionBaremo): void {
    if (this.isEditPerm) {
      this.loadBaremoComponent(BaremoPesoPuntosComponent, node, this.rootNodeSelectionToggle);
    }
  }

  openPesoPuntosNode(node: NodeConfiguracionBaremo): void {
    this.loadBaremoComponent(BaremoPesoPuntosComponent, node);
  }

  addPesoCuantiaNode(node: NodeConfiguracionBaremo): void {
    if (this.isEditPerm) {
      this.loadBaremoComponent(BaremoPesoCuantiaComponent, node, this.rootNodeSelectionToggle);
    }
  }

  openPesoCuantiaNode(node: NodeConfiguracionBaremo): void {
    this.loadBaremoComponent(BaremoPesoCuantiaComponent, node);
  }

  addPesoNode(node: NodeConfiguracionBaremo): void {
    if (this.isEditPerm) {
      this.loadBaremoComponent(BaremoPesoComponent, node, this.rootNodeSelectionToggle);
    }
  }

  openPesoNode(node: NodeConfiguracionBaremo): void {
    this.loadBaremoComponent(BaremoPesoComponent, node);
  }

  addPuntosNode(node: NodeConfiguracionBaremo): void {
    if (this.isEditPerm) {
      this.loadBaremoComponent(BaremoPuntosComponent, node, this.onPuntosNodeAdded);
    }
  }

  onPuntosNodeAdded = (node: NodeConfiguracionBaremo): void => {
    this.leafNodeSelectionToggle(node);
    this.enableNoBaremableParent(node);
  }

  openPuntosNode(node: NodeConfiguracionBaremo): void {
    this.loadBaremoComponent(BaremoPuntosComponent, node);
  }

  addSinPuntosNode(node: NodeConfiguracionBaremo): void {
    if (this.isEditPerm) {
      node.disabled = false;
      node.baremo = {
        configuracionBaremo: node.configuracionBaremo,
        convocatoriaBaremacion: this.formPart.convocatoriaBaremacion
      } as IBaremo;
      this.leafNodeSelectionToggle(node);
      this.enableNoBaremableParent(node);
    }
  }

  private enableNoBaremableParent(node: NodeConfiguracionBaremo): void {
    const parent = node.parent;
    if (parent?.disabled && parent.configuracionBaremo.tipoNodo === TipoNodo.NO_BAREMABLE) {
      parent.disabled = false;
      this.enableNoBaremableParent(parent);
    }
  }

  getErrorMsg(node: NodeConfiguracionBaremo): string {
    return node.errorsMsg.map(errorMsg => {
      return this.translateService.instant(errorMsg);
    }).join(' | ');
  }

  loadBaremoComponent(
    component: Type<BaremoComponent>, node: NodeConfiguracionBaremo, onBaremoOutput?: (node: NodeConfiguracionBaremo) => void
  ) {
    this.disableTree();
    // Dynamic component loader
    const viewContainerRef = this.baremoHost.viewContainerRef;
    viewContainerRef.clear();
    const componentFactory = this.resolver.resolveComponentFactory(component);
    const componentRef = viewContainerRef.createComponent<BaremoComponent>(componentFactory);
    componentRef.instance.node = node;
    componentRef.instance.readonly = !this.isEditPerm;
    const baremoSubscription = componentRef.instance.baremoOutput.asObservable()
      .subscribe(baremo => {
        this.formPart.editBaremo(node, baremo);
        node.disabled = false;
        if (onBaremoOutput) {
          onBaremoOutput(node);
        }
        this.enableTree();
        viewContainerRef.clear();
      });
    const cancelSubscription = componentRef.instance.cancel.asObservable()
      .subscribe(() => {
        this.enableTree();
        viewContainerRef.clear();
      });
    componentRef.onDestroy(() => {
      if (!baremoSubscription.closed) {
        baremoSubscription.unsubscribe();
      }
      if (!cancelSubscription.closed) {
        cancelSubscription.unsubscribe();
      }
    });
  }

  private disableTree(): void {
    this._treeDisabled = true;
  }

  private enableTree(): void {
    this._treeDisabled = false;
  }
}

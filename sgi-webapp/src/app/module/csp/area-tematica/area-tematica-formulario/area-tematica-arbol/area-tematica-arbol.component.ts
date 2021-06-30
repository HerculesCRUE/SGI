import { NestedTreeControl } from '@angular/cdk/tree';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatTree, MatTreeNestedDataSource } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AreaTematicaActionService } from '../../area-tematica.action.service';
import { AreaTematicaArbolFragment, NodeArea } from './area-tematica-arbol.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const AREA_KEY = marker('csp.area');
const AREA_TEMATICA_KEY = marker('csp.area-tematica');
const AREA_TEMATICA_ARBOL_NOMBRE_KEY = marker('csp.area-tematica-arbol.nombre');
const AREA_TEMATICA_ARBOL_DESCRIPCION_KEY = marker('csp.area-tematica-arbol.descripcion');

enum VIEW_MODE {
  NONE = '',
  VIEW = 'view',
  NEW = 'new',
  EDIT = 'edit'
}

@Component({
  selector: 'sgi-area-tematica-arbol',
  templateUrl: './area-tematica-arbol.component.html',
  styleUrls: ['./area-tematica-arbol.component.scss']
})
export class AreaTematicaArbolComponent extends FragmentComponent implements OnInit, OnDestroy {

  private formPart: AreaTematicaArbolFragment;
  private subscriptions = [] as Subscription[];
  @ViewChild(MatTree, { static: true }) private matTree: MatTree<NodeArea>;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  treeControl = new NestedTreeControl<NodeArea>(node => node.childs);
  dataSource = new MatTreeNestedDataSource<NodeArea>();

  viewingNode: NodeArea;
  viewMode = VIEW_MODE.NONE;
  checkedNode: NodeArea;

  formGroup: FormGroup;

  hasChild = (_: number, node: NodeArea) => node.childs.length > 0;

  msgParamEntity = {};
  msgParamDescripcionEntity = {};
  msgParamNombreEntity = {};
  msgParamAreaTematicaEntity = {};
  textoDelete: string;

  constructor(
    private readonly dialogService: DialogService,
    public actionService: AreaTematicaActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.AREAS_ARBOL, actionService);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.formPart = this.fragment as AreaTematicaArbolFragment;
  }

  ngOnInit() {
    super.ngOnInit();
    this.setupI18N();
    this.formPart.areas$.subscribe((programas) => {
      this.dataSource.data = programas;
    });
    this.formGroup = new FormGroup({
      nombre: new FormControl('', [Validators.required, Validators.maxLength(5)]),
      descripcion: new FormControl('', [Validators.required, Validators.maxLength(50)]),
    });
    this.switchToNone();
  }

  private setupI18N(): void {
    this.translate.get(
      AREA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      AREA_TEMATICA_ARBOL_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      AREA_TEMATICA_ARBOL_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      AREA_TEMATICA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAreaTematicaEntity = { entity: value });


    this.translate.get(
      AREA_TEMATICA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);


  }

  ngOnDestroy() {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  showNodeDetails(node: NodeArea) {
    this.viewingNode = node;
    this.switchToView();
  }

  hideNodeDetails() {
    this.viewMode = VIEW_MODE.NONE;
    this.viewingNode = undefined;
  }

  onCheckNode(node: NodeArea, $event: MatCheckboxChange) {
    if ($event.checked) {
      this.checkedNode = node;
    }
    else {
      this.checkedNode = undefined;
    }
  }

  switchToNew() {
    const newNode = new NodeArea(new StatusWrapper<IAreaTematica>({
      padre: {} as IAreaTematica
    } as IAreaTematica));
    this.viewMode = VIEW_MODE.NEW;
    this.viewingNode = newNode;
    this.loadDetails(this.viewingNode);
  }

  switchToEdit() {
    this.viewMode = VIEW_MODE.EDIT;
    this.loadDetails(this.viewingNode);
  }

  switchToView() {
    this.viewMode = VIEW_MODE.VIEW;
    this.loadDetails(this.viewingNode);
  }

  private switchToNone() {
    this.viewMode = VIEW_MODE.NONE;
    this.viewingNode = undefined;
    this.loadDetails(undefined);
    this.checkedNode = undefined;
  }

  private loadDetails(node: NodeArea) {
    if (this.viewMode === VIEW_MODE.NEW || this.viewMode === VIEW_MODE.EDIT) {
      this.formGroup.get('nombre').enable();
      this.formGroup.get('descripcion').enable();
    }
    else {
      this.formGroup.get('nombre').disable();
      this.formGroup.get('descripcion').disable();
    }
    this.formGroup.reset();
    this.formGroup.get('nombre').patchValue(node?.area?.value?.nombre);
    this.formGroup.get('descripcion').patchValue(node?.area?.value?.descripcion);
  }

  cancelDetail() {
    if (this.viewMode === VIEW_MODE.EDIT) {
      this.switchToView();
    }
    else {
      this.switchToNone();
    }
  }

  private refreshTree(nodes: NodeArea[]) {
    // There is no way to update the tree without recreating it,
    // because CDK Tree detect changes only when a root nodes changes
    // See: https://github.com/angular/components/issues/11381
    this.matTree.renderNodeChanges([]);
    this.matTree.renderNodeChanges(nodes);
  }

  acceptDetail() {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      if (this.viewMode === VIEW_MODE.NEW) {
        this.addNode(this.getDetailNode());
      }
      else if (this.viewMode === VIEW_MODE.EDIT) {
        this.updateNode(this.getDetailNode());
      }
    }
  }

  private getDetailNode(): NodeArea {
    const detail = this.viewingNode;
    detail.area.value.nombre = this.formGroup.get('nombre').value;
    detail.area.value.descripcion = this.formGroup.get('descripcion').value;
    return detail;
  }

  private addNode(node: NodeArea) {
    node.area.setCreated();
    if (this.checkedNode) {
      this.checkedNode.addChild(node);
      this.refreshTree(this.formPart.areas$.value);
    }
    else {
      const current = this.formPart.areas$.value;
      current.push(node);
      this.formPart.publishNodes(current);
    }
    this.formPart.setChanges(true);
    this.switchToNone();
  }

  private updateNode(node: NodeArea) {
    if (!node.area.created) {
      node.area.setEdited();
    }
    if (node.parent) {
      node.parent.sortChildsByName();
    }
    else {
      this.formPart.publishNodes();
    }
    this.refreshTree(this.formPart.areas$.value);
    this.formPart.setChanges(true);
    this.switchToView();
  }

  deleteDetail() {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            if (this.viewingNode.parent) {
              this.viewingNode.parent.removeChild(this.viewingNode);
              this.refreshTree(this.formPart.areas$.value);
            }
            else {
              const updated = this.formPart.areas$.value.filter((programa) => programa !== this.viewingNode);
              this.formPart.publishNodes(updated);
            }
            this.formPart.addToDelete(this.viewingNode);
            this.formPart.setChanges(true);
            this.switchToNone();
          }
        }
      )
    );
  }
}

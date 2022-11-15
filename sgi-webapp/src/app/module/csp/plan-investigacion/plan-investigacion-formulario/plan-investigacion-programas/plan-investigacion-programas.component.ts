import { NestedTreeControl } from '@angular/cdk/tree';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatTree, MatTreeNestedDataSource } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IPrograma } from '@core/models/csp/programa';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { PlanInvestigacionActionService } from '../../plan-investigacion.action.service';
import { NodePrograma, PlanInvestigacionProgramaFragment } from './plan-investigacion-programas.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PLAN_INVESTIGACION_PROGRAMA_KEY = marker('csp.plan-investigacion-programa');
const PLAN_INVESTIGACION_PROGRAMA_NOMBRE_KEY = marker('csp.plan-investigacion-programa.nombre');
const PLAN_INVESTIGACION_PROGRAMA_DESCRIPCION_KEY = marker('csp.plan-investigacion-programa.descripcion');

enum VIEW_MODE {
  NONE = '',
  VIEW = 'view',
  NEW = 'new',
  EDIT = 'edit'
}

@Component({
  selector: 'sgi-plan-investigacion-programas',
  templateUrl: './plan-investigacion-programas.component.html',
  styleUrls: ['./plan-investigacion-programas.component.scss']
})
export class PlanInvestigacionProgramasComponent extends FragmentComponent implements OnInit, OnDestroy {
  private formPart: PlanInvestigacionProgramaFragment;
  private subscriptions = [] as Subscription[];
  @ViewChild(MatTree, { static: true }) private matTree: MatTree<NodePrograma>;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  treeControl = new NestedTreeControl<NodePrograma>(node => node.childs);
  dataSource = new MatTreeNestedDataSource<NodePrograma>();

  viewingNode: NodePrograma;
  viewMode = VIEW_MODE.NONE;
  checkedNode: NodePrograma;

  formGroup: FormGroup;

  msgParamEntity = {};
  msgParamDescripcionEntity = {};
  msgParamNombreEntity = {};
  textoDelete: string;

  hasChild = (_: number, node: NodePrograma) => node.childs.length > 0;

  constructor(
    private readonly dialogService: DialogService,
    public actionService: PlanInvestigacionActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.PROGRAMAS, actionService);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.formPart = this.fragment as PlanInvestigacionProgramaFragment;
  }

  ngOnInit() {
    super.ngOnInit();
    this.setupI18N();
    this.formPart.programas$.subscribe((programas) => {
      this.dataSource.data = programas;
    });
    this.formGroup = new FormGroup({
      nombre: new FormControl('', [Validators.required, Validators.maxLength(200)]),
      descripcion: new FormControl('', [Validators.maxLength(4000)]),
    });
    this.switchToNone();
  }

  private setupI18N(): void {
    this.translate.get(
      PLAN_INVESTIGACION_PROGRAMA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });


    this.translate.get(
      PLAN_INVESTIGACION_PROGRAMA_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      PLAN_INVESTIGACION_PROGRAMA_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      PLAN_INVESTIGACION_PROGRAMA_KEY,
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

  showNodeDetails(node: NodePrograma) {
    this.viewingNode = node;
    this.switchToView();
  }

  hideNodeDetails() {
    this.viewMode = VIEW_MODE.NONE;
    this.viewingNode = undefined;
  }

  onCheckNode(node: NodePrograma, $event: MatCheckboxChange) {
    if ($event.checked) {
      this.checkedNode = node;
    }
    else {
      this.checkedNode = undefined;
    }
  }

  switchToNew() {
    const newNode = new NodePrograma(new StatusWrapper<IPrograma>({
      padre: {} as IPrograma
    } as IPrograma));
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

  private loadDetails(node: NodePrograma) {
    if (this.viewMode === VIEW_MODE.NEW || this.viewMode === VIEW_MODE.EDIT) {
      this.formGroup.get('nombre').enable();
      this.formGroup.get('descripcion').enable();
    }
    else {
      this.formGroup.get('nombre').disable();
      this.formGroup.get('descripcion').disable();
    }
    this.formGroup.reset();
    this.formGroup.get('nombre').patchValue(node?.programa?.value?.nombre);
    this.formGroup.get('descripcion').patchValue(node?.programa?.value?.descripcion);
  }

  cancelDetail() {
    if (this.viewMode === VIEW_MODE.EDIT) {
      this.switchToView();
    }
    else {
      this.switchToNone();
    }
  }

  private refreshTree(nodes: NodePrograma[]) {
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

  private getDetailNode(): NodePrograma {
    const detail = this.viewingNode;
    detail.programa.value.nombre = this.formGroup.get('nombre').value;
    detail.programa.value.descripcion = this.formGroup.get('descripcion').value;
    return detail;
  }

  private addNode(node: NodePrograma) {
    node.programa.setCreated();
    if (this.checkedNode) {
      this.checkedNode.addChild(node);
      this.refreshTree(this.formPart.programas$.value);
    }
    else {
      const current = this.formPart.programas$.value;
      current.push(node);
      this.formPart.publishNodes(current);
    }
    this.formPart.setChanges(true);
    this.switchToNone();
  }

  private updateNode(node: NodePrograma) {
    if (!node.programa.created) {
      node.programa.setEdited();
    }
    if (node.parent) {
      node.parent.sortChildsByName();
    }
    else {
      this.formPart.publishNodes();
    }
    this.refreshTree(this.formPart.programas$.value);
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
              this.refreshTree(this.formPart.programas$.value);
            }
            else {
              const updated = this.formPart.programas$.value.filter((programa) => programa !== this.viewingNode);
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

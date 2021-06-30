import { NestedTreeControl } from '@angular/cdk/tree';
import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IPrograma } from '@core/models/csp/programa';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ProgramaService } from '@core/services/csp/programa.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { ConvocatoriaEntidadConvocanteData } from '../../convocatoria-formulario/convocatoria-entidades-convocantes/convocatoria-entidades-convocantes.fragment';

const MSG_ERROR_FORM_GROUP = marker('error.form-group');
const MSG_FORM_GROUP_WITHOUT_PLAN = marker('msg.csp.convocatoria-entidad-convocante.sin-plan');
const MSG_FORM_GROUP_WITHOUT_PROGRAMA = marker('msg.csp.convocatoria-entidad-convocante.sin-programa');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const CONVOCATORIA_ENTIDAD_CONVOCANTE_KEY = marker('csp.convocatoria-entidad-convocante');
const CONVOCATORIA_ENTIDAD_CONVOCANTE_PLAN_KEY = marker('csp.convocatoria-entidad-convocante.plan');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ConvocatoriaEntidadConvocanteModalData {
  entidadConvocanteData: ConvocatoriaEntidadConvocanteData;
  selectedEmpresas: IEmpresa[];
  readonly: boolean;
}

class NodePrograma {
  parent: NodePrograma;
  programa: StatusWrapper<IPrograma>;
  // tslint:disable-next-line: variable-name
  _childs: NodePrograma[];
  get childs(): NodePrograma[] {
    return this._childs;
  }

  constructor(programa: StatusWrapper<IPrograma>) {
    this.programa = programa;
    this._childs = [];
  }

  addChild(child: NodePrograma) {
    child.parent = this;
    child.programa.value.padre = this.programa.value;
    this._childs.push(child);
    this.sortChildsByName();
  }

  removeChild(child: NodePrograma) {
    this._childs = this._childs.filter((programa) => programa !== child);
  }

  sortChildsByName(): void {
    this._childs = sortByName(this._childs);
  }
}

function sortByName(nodes: NodePrograma[]): NodePrograma[] {
  return nodes.sort((a, b) => {
    if (a.programa.value.nombre < b.programa.value.nombre) {
      return -1;
    }
    if (a.programa.value.nombre > b.programa.value.nombre) {
      return 1;
    }
    return 0;
  });
}

@Component({
  templateUrl: './convocatoria-entidad-convocante-modal.component.html',
  styleUrls: ['./convocatoria-entidad-convocante-modal.component.scss']
})
export class ConvocatoriaEntidadConvocanteModalComponent extends
  BaseModalComponent<ConvocatoriaEntidadConvocanteModalData, ConvocatoriaEntidadConvocanteModalComponent> implements OnInit {
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  programaTree$ = new BehaviorSubject<NodePrograma[]>([]);
  treeControl = new NestedTreeControl<NodePrograma>(node => node.childs);
  dataSource = new MatTreeNestedDataSource<NodePrograma>();
  private nodeMap = new Map<number, NodePrograma>();

  textSaveOrUpdate: string;
  textoTitle: string;

  msgParamEntity = {};
  msgParamPlanEntity = {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  checkedNode: NodePrograma;
  hasChild = (_: number, node: NodePrograma) => node.childs.length > 0;

  constructor(
    private readonly logger: NGXLogger,
    public matDialogRef: MatDialogRef<ConvocatoriaEntidadConvocanteModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConvocatoriaEntidadConvocanteModalData,
    protected snackBarService: SnackBarService,
    private programaService: ProgramaService,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data);

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'row';

    if (!data.entidadConvocanteData) {
      data.entidadConvocanteData = {
        entidadConvocante: new StatusWrapper<IConvocatoriaEntidadConvocante>({} as IConvocatoriaEntidadConvocante),
        empresa: undefined,
        modalidad: undefined,
        plan: undefined,
        programa: undefined,
      };
      this.textSaveOrUpdate = MSG_ANADIR;
    } else {
      this.textSaveOrUpdate = MSG_ACEPTAR;
    }
  }

  ngOnInit() {
    super.ngOnInit();
    this.setupI18N();
    this.subscriptions.push(this.programaTree$.subscribe(
      (programas) => {
        this.dataSource.data = programas;
      }
    ));
    this.subscriptions.push(this.formGroup.get('plan').valueChanges.subscribe(
      (value) => {
        // Reset selected node on first user change
        if (value?.id !== this.data.entidadConvocanteData.plan?.id) {
          this.formGroup.get('programa').setValue(undefined);
          this.checkedNode = undefined;
        }
        this.loadTreePrograma(value?.id);
      })
    );
    this.loadTreePrograma(this.data.entidadConvocanteData?.plan?.id);
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_ENTIDAD_CONVOCANTE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_ENTIDAD_CONVOCANTE_PLAN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPlanEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (this.data.entidadConvocanteData.empresa) {
      this.translate.get(
        CONVOCATORIA_ENTIDAD_CONVOCANTE_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.textoTitle = value);
    } else {
      this.translate.get(
        CONVOCATORIA_ENTIDAD_CONVOCANTE_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.textoTitle = value);
    }
  }

  private loadTreePrograma(programaId: number) {
    if (programaId) {
      this.checkedNode = undefined;
      const subscription = this.programaService.findAllHijosPrograma(programaId).pipe(
        switchMap(response => {
          if (response.items.length === 0) {
            this.programaTree$.next([]);
            this.nodeMap.clear();
          }
          return from(response.items).pipe(
            mergeMap((programa) => {
              const node = new NodePrograma(new StatusWrapper<IPrograma>(programa));
              this.nodeMap.set(node.programa.value.id, node);
              return this.getChilds(node).pipe(map(() => node));
            })
          );
        })
      ).subscribe(
        (programa) => {
          const current = this.programaTree$.value;
          current.push(programa);
          this.publishNodes(current);
        },
        (error) => {
          this.logger.error(error);
        },
        () => {
          this.checkedNode = this.nodeMap.get(this.formGroup.get('programa').value);
          if (this.checkedNode) {
            this.expandNodes(this.checkedNode);
          }
        }
      );
      this.subscriptions.push(subscription);
    }
    else {
      this.programaTree$.next([]);
      this.nodeMap.clear();
    }
  }

  private getChilds(parent: NodePrograma): Observable<NodePrograma[]> {
    return this.programaService.findAllHijosPrograma(parent.programa.value.id).pipe(
      map((result) => {
        const childs: NodePrograma[] = result.items.map(
          (programa) => {
            const child = new NodePrograma(new StatusWrapper<IPrograma>(programa));
            child.parent = parent;
            this.nodeMap.set(child.programa.value.id, child);
            return child;
          });
        return childs;
      }),
      switchMap((nodes) => {
        parent.childs.push(...nodes);
        parent.sortChildsByName();
        if (nodes.length > 0) {
          return from(nodes).pipe(
            mergeMap((node) => {
              return this.getChilds(node);
            })
          );
        }
        return of([]);
      }),
      takeLast(1)
    );
  }

  private expandNodes(node: NodePrograma) {
    if (node && node.parent) {
      this.treeControl.expand(node.parent);
      this.expandNodes(node.parent);
    }
  }

  private publishNodes(rootNodes?: NodePrograma[]) {
    let nodes = rootNodes ? rootNodes : this.programaTree$.value;
    nodes = sortByName(nodes);
    this.programaTree$.next(nodes);
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      empresa: new FormControl(this.data.entidadConvocanteData.empresa, Validators.required),
      plan: new FormControl(this.data.entidadConvocanteData.plan),
      programa: new FormControl(this.data.entidadConvocanteData.entidadConvocante.value.programa?.id)
    });
    if (this.data.readonly) {
      formGroup.disable();
    }
    return formGroup;
  }

  protected getDatosForm(): ConvocatoriaEntidadConvocanteModalData {
    const entidadConvocante = this.data.entidadConvocanteData.entidadConvocante;
    entidadConvocante.value.entidad = this.formGroup.get('empresa').value;
    const plan = this.formGroup.get('plan').value;
    const programa = this.checkedNode?.programa?.value;
    entidadConvocante.value.programa = programa ? programa : plan;
    if (!plan && !programa) {
      entidadConvocante.value.programa = undefined;
    }
    this.data.entidadConvocanteData.empresa = this.formGroup.get('empresa').value;
    this.data.entidadConvocanteData.modalidad = entidadConvocante.value.programa;
    return this.data;
  }

  onCheckNode(node: NodePrograma, $event: MatCheckboxChange): void {
    this.checkedNode = $event.checked ? node : undefined;
    this.formGroup.get('programa').setValue(this.checkedNode?.programa?.value?.id);
  }

  saveOrUpdate(): void {
    if (FormGroupUtil.valid(this.formGroup)) {
      const plan = this.formGroup.get('plan').value;
      const programa = this.checkedNode;
      if (!plan && !programa) {
        this.saveIncompleteFormGroup(MSG_FORM_GROUP_WITHOUT_PLAN);
      } else if (!programa) {
        this.saveIncompleteFormGroup(MSG_FORM_GROUP_WITHOUT_PROGRAMA);
      } else {
        this.matDialogRef.close(this.getDatosForm());
      }
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
  }

  private saveIncompleteFormGroup(message: string): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(message).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.matDialogRef.close(this.getDatosForm());
          }
        }
      )
    );
  }

}

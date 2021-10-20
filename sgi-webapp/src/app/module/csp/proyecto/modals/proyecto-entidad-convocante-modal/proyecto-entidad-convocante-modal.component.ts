import { NestedTreeControl } from '@angular/cdk/tree';
import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IPrograma } from '@core/models/csp/programa';
import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ProgramaService } from '@core/services/csp/programa.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { map, mergeMap, startWith, switchMap, takeLast, tap } from 'rxjs/operators';

const PROYECTO_ENTIDAD_CONVOCANTE_KEY = marker('csp.proyecto-entidad-convocante');
const PROYECTO_ENTIDAD_CONVOCANTE_PLAN_KEY = marker('csp.proyecto-entidad-convocante.programa.plan');
const PROYECTO_ENTIDAD_CONVOCANTE_PROGRAMA_KEY = marker('csp.proyecto-entidad-convocante.programa.programa');
const MSG_CONTINUE_ENTITY_NOTSET_KEY = marker('msg.continue.entity.not-set');
const MSG_ERROR_FORM_GROUP = marker('error.form-group');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ProyectoEntidadConvocanteModalData {
  proyectoEntidadConvocante: IProyectoEntidadConvocante;
  selectedEmpresas: IEmpresa[];
  readonly: boolean;
}

class NodePrograma {
  parent: NodePrograma;
  programa: IPrograma;
  // tslint:disable-next-line: variable-name
  _childs: NodePrograma[];
  get childs(): NodePrograma[] {
    return this._childs;
  }

  constructor(programa: IPrograma) {
    this.programa = programa;
    this._childs = [];
  }

  addChild(child: NodePrograma) {
    child.parent = this;
    child.programa.padre = this.programa;
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
    if (a.programa.nombre < b.programa.nombre) {
      return -1;
    }
    if (a.programa.nombre > b.programa.nombre) {
      return 1;
    }
    return 0;
  });
}

@Component({
  templateUrl: './proyecto-entidad-convocante-modal.component.html',
  styleUrls: ['./proyecto-entidad-convocante-modal.component.scss']
})
export class ProyectoEntidadConvocanteModalComponent extends
  BaseModalComponent<ProyectoEntidadConvocanteModalData, ProyectoEntidadConvocanteModalComponent> implements OnInit {
  fxLayoutProperties: FxLayoutProperties;

  msgParamEntity = {};
  msgParamPlanEntity = {};
  msgParamProgramaEntity = {};
  title: string;

  planes$: BehaviorSubject<IPrograma[]> = new BehaviorSubject<IPrograma[]>([]);

  treeControl = new NestedTreeControl<NodePrograma>(node => node.childs);
  dataSource = new MatTreeNestedDataSource<NodePrograma>();
  private nodeMap = new Map<number, NodePrograma>();

  // indica si estamos en modo creación (true) o actualización (false)
  create: boolean;

  checkedNode: NodePrograma;
  hasChild = (_: number, node: NodePrograma) => node.childs.length > 0;

  constructor(
    private readonly logger: NGXLogger,
    public matDialogRef: MatDialogRef<ProyectoEntidadConvocanteModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoEntidadConvocanteModalData,
    protected snackBarService: SnackBarService,
    private programaService: ProgramaService,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
  ) {
    super(snackBarService, matDialogRef, data);

    this.setupLayout();

    if (!data.proyectoEntidadConvocante) {
      data.proyectoEntidadConvocante = {} as IProyectoEntidadConvocante;
      this.create = true;
    } else {
      this.create = false;
    }
  }

  private setupLayout(): void {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'row';
  }

  ngOnInit() {
    super.ngOnInit();
    this.setupI18N();
    const subcription = this.programaService.findAllPlan().subscribe(
      list => this.planes$.next(list.items)
    );
    this.subscriptions.push(subcription);
  }

  private setupI18N(): void {

    this.translate.get(
      PROYECTO_ENTIDAD_CONVOCANTE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_ENTIDAD_CONVOCANTE_PLAN_KEY
    ).subscribe((value) => this.msgParamPlanEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_ENTIDAD_CONVOCANTE_PROGRAMA_KEY
    ).subscribe((value) => this.msgParamProgramaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (!this.create) {
      this.translate.get(
        PROYECTO_ENTIDAD_CONVOCANTE_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        PROYECTO_ENTIDAD_CONVOCANTE_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }

  private updateProgramas(programa: NodePrograma[]) {
    this.dataSource.data = programa;
  }

  private loadTreePrograma() {
    if (this.data.proyectoEntidadConvocante.programaConvocatoria) {
      const node = new NodePrograma(this.data.proyectoEntidadConvocante.programaConvocatoria);
      this.nodeMap.set(node.programa.id, node);
      const subscription = this.getChilds(node).pipe(map(() => node)).pipe(
        tap(() => {
          this.checkedNode = this.nodeMap.get(this.formGroup.get('programa').value);
          if (this.checkedNode) {
            this.expandNodes(this.checkedNode);
          }
        })
      ).subscribe((nodePrograma) => {
        this.dataSource.data = [nodePrograma];
      });
      this.subscriptions.push(subscription);
    } else {
      const id = this.formGroup.get('plan').value?.id;
      if (id && !isNaN(id)) {
        this.checkedNode = undefined;
        const subscription = this.programaService.findAllHijosPrograma(id).pipe(
          switchMap(response => {
            if (response.items.length === 0) {
              this.updateProgramas([]);
              this.nodeMap.clear();
            }
            return from(response.items).pipe(
              mergeMap((programa) => {
                const node = new NodePrograma(programa);
                this.nodeMap.set(node.programa.id, node);
                return this.getChilds(node).pipe(map(() => node));
              })
            );
          })
        ).subscribe(
          (programa) => {
            const current = this.dataSource.data;
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
        this.updateProgramas([]);
        this.nodeMap.clear();
      }
    }
  }

  private getChilds(parent: NodePrograma): Observable<NodePrograma[]> {
    return this.programaService.findAllHijosPrograma(parent.programa.id).pipe(
      map((result) => {
        const childs: NodePrograma[] = result.items.map(
          (programa) => {
            const child = new NodePrograma(programa);
            child.parent = parent;
            this.nodeMap.set(child.programa.id, child);
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
    let nodes = rootNodes ? rootNodes : this.dataSource.data;
    nodes = sortByName(nodes);
    this.updateProgramas(nodes);
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      entidad: new FormControl({ value: this.data.proyectoEntidadConvocante.entidad, disabled: !this.create }, Validators.required),
      plan: new FormControl(
        {
          value: this.getPlan(this.data.proyectoEntidadConvocante),
          disabled: !(this.create || this.data.proyectoEntidadConvocante.programaConvocatoria == null)
        }, IsEntityValidator.isValid()),
      programa: new FormControl(this.data.proyectoEntidadConvocante.programa?.id)
    });

    if (this.data.readonly) {
      formGroup.disable();
    }
    this.subscriptions.push(
      formGroup.controls.plan.valueChanges.subscribe(value => {
        if (value.id !== this.getPlan(this.data.proyectoEntidadConvocante)?.id) {
          formGroup.controls.programa.setValue(undefined);
          this.checkedNode = undefined;
        }
        this.loadTreePrograma();
      }));
    return formGroup;
  }

  protected getDatosForm(): ProyectoEntidadConvocanteModalData {
    this.data.proyectoEntidadConvocante.entidad = this.formGroup.get('entidad').value;
    if (this.checkedNode) {
      // Se ha seleccionado modalidad
      this.data.proyectoEntidadConvocante.programa = this.checkedNode?.programa;
    } else {
      // No se ha seleccionado modalidad
      // se limpia porque se ha deseleccionado
      this.data.proyectoEntidadConvocante.programa = null;
      if (this.formGroup.get('plan').value?.id) {
        // Pero se ha seleccionado plan
        if (!this.data.proyectoEntidadConvocante.programaConvocatoria) {
          // Si el plan no viene fijado por la Convocatoria
          // el plan se debe guardar como modalidad
          this.data.proyectoEntidadConvocante.programa = this.formGroup.get('plan').value;
        }
      }
    }
    return this.data;
  }

  onCheckNode(node: NodePrograma, $event: MatCheckboxChange): void {
    this.checkedNode = $event.checked ? node : undefined;
    this.formGroup.get('programa').setValue(this.checkedNode?.programa?.id);
  }

  saveOrUpdate(): void {
    if (this.formGroup.valid) {
      const plan = this.formGroup.get('plan').value;
      const programa = this.checkedNode;
      if (!plan && !programa) {
        this.saveIncompleteFormGroup(MSG_CONTINUE_ENTITY_NOTSET_KEY, this.msgParamPlanEntity);
      } else if (!programa) {
        this.saveIncompleteFormGroup(MSG_CONTINUE_ENTITY_NOTSET_KEY, this.msgParamProgramaEntity);
      } else {
        this.matDialogRef.close(this.getDatosForm());
      }
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
  }

  private saveIncompleteFormGroup(message: string, params?: {}): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(message, params).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.matDialogRef.close(this.getDatosForm());
          }
        }
      )
    );
  }

  private getPlan(value: IProyectoEntidadConvocante): IPrograma {
    if (value.programa != null) {
      return this.getTopLevel(value.programa);
    }
    if (value.programaConvocatoria != null) {
      return this.getTopLevel(value.programaConvocatoria);
    }
    return null;
  }

  private getTopLevel(programa: IPrograma): IPrograma {
    if (programa.padre == null) {
      return programa;
    }
    return this.getTopLevel(programa.padre);
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

}

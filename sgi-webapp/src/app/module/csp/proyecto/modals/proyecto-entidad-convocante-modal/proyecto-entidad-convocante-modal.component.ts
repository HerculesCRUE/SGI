import { NestedTreeControl } from '@angular/cdk/tree';
import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IPrograma } from '@core/models/csp/programa';
import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';
import { ProgramaService } from '@core/services/csp/programa.service';
import { DialogService } from '@core/services/dialog.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

const PROYECTO_ENTIDAD_CONVOCANTE_KEY = marker('csp.proyecto-entidad-convocante');
const PROYECTO_ENTIDAD_CONVOCANTE_PLAN_KEY = marker('csp.proyecto-entidad-convocante.programa.plan');
const PROYECTO_ENTIDAD_CONVOCANTE_PROGRAMA_KEY = marker('csp.proyecto-entidad-convocante.programa.programa');
const MSG_CONTINUE_ENTITY_NOTSET_KEY = marker('msg.continue.entity.not-set');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ProyectoEntidadConvocanteModalData {
  proyectoEntidadConvocante: IProyectoEntidadConvocante;
  selectedEntidadesConvocantes: IProyectoEntidadConvocante[];
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
export class ProyectoEntidadConvocanteModalComponent extends DialogFormComponent<ProyectoEntidadConvocanteModalData> implements OnInit {

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
    matDialogRef: MatDialogRef<ProyectoEntidadConvocanteModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoEntidadConvocanteModalData,
    private programaService: ProgramaService,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
  ) {
    super(matDialogRef, !!data.proyectoEntidadConvocante);

    if (!data.proyectoEntidadConvocante) {
      data.proyectoEntidadConvocante = {} as IProyectoEntidadConvocante;
      this.create = true;
    } else {
      this.create = false;
    }
  }

  ngOnInit() {
    super.ngOnInit();
    this.setupI18N();
    const subcription = this.programaService.findAllPlan().pipe(
      tap((response) => this.planes$.next(response.items)),
      tap(() => this.loadTreePrograma())
    ).subscribe();
    this.subscriptions.push(subcription);
  }

  private setupI18N(): void {

    this.translate.get(
      PROYECTO_ENTIDAD_CONVOCANTE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      PROYECTO_ENTIDAD_CONVOCANTE_PLAN_KEY
    ).subscribe((value) => this.msgParamPlanEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_ENTIDAD_CONVOCANTE_PROGRAMA_KEY
    ).subscribe((value) => this.msgParamProgramaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

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
    this.updateProgramas([]);
    this.nodeMap.clear();

    if (this.data.proyectoEntidadConvocante.programaConvocatoria
      && this.data.proyectoEntidadConvocante.programaConvocatoria?.padre?.id) {
      const node = new NodePrograma(this.data.proyectoEntidadConvocante.programaConvocatoria);
      this.nodeMap.set(node.programa.id, node);
      const subscription = this.getChilds(node).pipe(map(() => node)).pipe(
        tap(() => {
          this.checkedNode = this.nodeMap.get(this.formGroup.get('programa').value?.id);
          if (this.checkedNode) {
            this.expandNodes(this.checkedNode);
          }
        })
      ).subscribe((nodePrograma) => {
        this.dataSource.data = [nodePrograma];
      });
      this.subscriptions.push(subscription);
    } else {
      const id = this.data.proyectoEntidadConvocante.programaConvocatoria?.padre?.id ?? this.formGroup.get('plan').value?.id;
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
            this.checkedNode = this.nodeMap.get(this.formGroup.get('programa').value?.id);
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

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      entidad: new FormControl({ value: this.data.proyectoEntidadConvocante.entidad, disabled: !this.create }, Validators.required),
      plan: new FormControl(
        {
          value: this.getPlan(this.data.proyectoEntidadConvocante),
          disabled: !(this.create || this.data.proyectoEntidadConvocante.programaConvocatoria == null)
        }, IsEntityValidator.isValid()),
      programa: new FormControl(this.data.proyectoEntidadConvocante.programa)
    }, {
      validators: [
        this.notDuplicatedEntidadAndPrograma(this.data.selectedEntidadesConvocantes)
      ]
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

  protected getValue(): ProyectoEntidadConvocanteModalData {
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
    this.formGroup.get('programa').setValue(this.checkedNode?.programa);
  }

  doAction(): void {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      const plan = this.formGroup.get('plan').value;
      const programa = this.checkedNode;
      if (!plan && !programa) {
        this.saveIncompleteFormGroup(MSG_CONTINUE_ENTITY_NOTSET_KEY, this.msgParamPlanEntity);
      } else if (!programa) {
        this.saveIncompleteFormGroup(MSG_CONTINUE_ENTITY_NOTSET_KEY, this.msgParamProgramaEntity);
      } else {
        this.close(this.getValue());
      }
    }
  }

  private saveIncompleteFormGroup(message: string, params?: {}): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(message, params).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.close(this.getValue());
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
    if (!!!programa || !!!programa.padre) {
      return programa;
    }
    return this.getTopLevel(programa.padre);
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  private notDuplicatedEntidadAndPrograma(entidadesConvocantesProyecto: IProyectoEntidadConvocante[]): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {
      const entidadControl = formGroup.controls.entidad;
      const planControl = formGroup.controls.plan;
      const programaControl = formGroup.controls.programa;

      if (entidadControl.errors && !entidadControl.errors.duplicated) {
        return null;
      }

      const programaFormValue = programaControl.value
        ?? (this.data.proyectoEntidadConvocante.programaConvocatoria ?? planControl.value);

      if (entidadesConvocantesProyecto
        .some(entidad =>
          entidad.entidad.id === entidadControl.value?.id
          && (!!!this.getTopLevel(entidad.programa ?? entidad.programaConvocatoria)?.id
            || !!!planControl.value?.id
            || this.getTopLevel(entidad.programa ?? entidad.programaConvocatoria)?.id === planControl.value?.id)
          && (
            (!!!this.getTopLevel(entidad.programa ?? entidad.programaConvocatoria)?.id || !!!planControl.value?.id)
            || this.getProgramaParentsIds(entidad.programa ?? entidad.programaConvocatoria).includes(programaFormValue?.id)
            || this.getProgramaParentsIds(programaFormValue).includes(entidad.programa?.id ?? entidad.programaConvocatoria?.id)
          )
        )
      ) {
        entidadControl.setErrors({ duplicated: true });
        entidadControl.markAsTouched({ onlySelf: true });
      } else if (entidadControl.errors) {
        delete entidadControl.errors.duplicated;
        if (!entidadControl.errors?.length) {
          entidadControl.setErrors(null);
        }
        entidadControl.updateValueAndValidity({ onlySelf: true });
      }
    };
  }

  private getProgramaParentsIds(programa: IPrograma, tree: number[] = []): number[] {
    if (!!!programa) {
      return tree;
    }

    tree.push(programa.id);

    if (!!programa?.padre?.id) {
      this.getProgramaParentsIds(programa.padre, tree);
    }

    return tree;
  }

}

import { NestedTreeControl } from '@angular/cdk/tree';
import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { AreaTematicaService } from '@core/services/csp/area-tematica.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast } from 'rxjs/operators';

const AREA_TEMATICA_LISTADO_KEY = marker('list.entity');
const AREA_TEMATICA_KEY = marker('csp.area-tematica');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ProyectoContextoModalData {
  padre: IAreaTematica;
  areasTematicasConvocatoria: IAreaTematica[];
  areaTematicaProyecto: IAreaTematica;
}

class NodeAreaTematica {
  parent: NodeAreaTematica;
  areaTematica: StatusWrapper<IAreaTematica>;
  checked: boolean;
  disabled: boolean;
  // tslint:disable-next-line: variable-name
  _childs: NodeAreaTematica[];
  get childs(): NodeAreaTematica[] {
    return this._childs;
  }

  constructor(areaTematica: StatusWrapper<IAreaTematica>) {
    this.areaTematica = areaTematica;
    this._childs = [];
  }

  sortChildsByName(): void {
    this._childs = sortByName(this._childs);
  }
}

function sortByName(nodes: NodeAreaTematica[]): NodeAreaTematica[] {
  return nodes.sort((a, b) => {
    if (a.areaTematica.value.nombre < b.areaTematica.value.nombre) {
      return -1;
    }
    if (a.areaTematica.value.nombre > b.areaTematica.value.nombre) {
      return 1;
    }
    return 0;
  });
}

const MSG_ERROR_AREA_TEMATICA = marker('error.load');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

@Component({
  templateUrl: './proyecto-contexto-modal.component.html',
  styleUrls: ['./proyecto-contexto-modal.component.scss']
})
export class ProyectoContextoModalComponent extends DialogFormComponent<ProyectoContextoModalData> implements OnInit {

  areaTematicaTree$ = new BehaviorSubject<NodeAreaTematica[]>([]);
  areasTematicas$: BehaviorSubject<IAreaTematica[]> = new BehaviorSubject<IAreaTematica[]>([]);
  treeControl = new NestedTreeControl<NodeAreaTematica>(node => node.childs);
  dataSource = new MatTreeNestedDataSource<NodeAreaTematica>();
  private nodeMap = new Map<number, NodeAreaTematica>();

  rootNodeConvocatoria: NodeAreaTematica;
  checkedNode: NodeAreaTematica;
  textSaveOrUpdate: string;

  msgParamEntity = {};
  msgParamListadoEntity = {};
  title: string;

  hasChild = (_: number, node: NodeAreaTematica) => node.childs.length > 0;

  constructor(
    private readonly logger: NGXLogger,
    matDialogRef: MatDialogRef<ProyectoContextoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoContextoModalData,
    private areaTematicaService: AreaTematicaService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.areaTematicaProyecto);

    this.textSaveOrUpdate = this.data?.areaTematicaProyecto != null ? MSG_ACEPTAR : MSG_ANADIR;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.loadAreasTematicasGrupo();
    this.subscriptions.push(this.formGroup.get('padre').valueChanges.subscribe(
      (value) => this.loadAreasTematicas(value?.id)
    ));

    if (this.data.padre && this.data.areasTematicasConvocatoria) {
      this.formGroup.controls.padre.disable();
      this.formGroup.controls.padre.setValue(this.data.padre);
    }

    this.textSaveOrUpdate = this.data.padre ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      AREA_TEMATICA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          AREA_TEMATICA_LISTADO_KEY,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.msgParamListadoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      AREA_TEMATICA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (this.data?.areaTematicaProyecto != null) {
      this.translate.get(
        AREA_TEMATICA_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        AREA_TEMATICA_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      padre: new FormControl(null, [Validators.required]),
      area: new FormControl(this.data?.areaTematicaProyecto, Validators.required)
    });
    return formGroup;
  }

  protected getValue(): ProyectoContextoModalData {
    const padre = this.formGroup.controls.padre.value;
    const areasTematicasConvocatoria = this.data.areasTematicasConvocatoria;
    if (this.checkedNode) {
      return {
        padre,
        areasTematicasConvocatoria,
        areaTematicaProyecto: this.checkedNode?.areaTematica.value,
      } as ProyectoContextoModalData;
    }

  }

  private loadAreasTematicasGrupo(): void {
    this.dataSource.data = null;
    this.subscriptions.push(
      this.areaTematicaService.findAllGrupo().pipe(
        map(res => this.areasTematicas$.next(res.items)),
        catchError(error => {
          this.processError(error);
          return of([]);
        })
      ).subscribe());
  }

  private loadAreasTematicas(padreId: number): void {
    this.areaTematicaTree$.next([]);
    this.nodeMap.clear();
    if (padreId) {
      const susbcription = this.areaTematicaService.findAllHijosArea(padreId).pipe(
        switchMap(response => {
          return from(response.items).pipe(
            mergeMap((areaTematica) => {
              const node = new NodeAreaTematica(new StatusWrapper<IAreaTematica>(areaTematica));
              this.nodeMap.set(node.areaTematica.value.id, node);
              return this.getChilds(node).pipe(map(() => node));
            })
          );
        })
      ).subscribe(
        (result) => {
          const current = this.areaTematicaTree$.value;
          current.push(result);
          this.publishNodes(current);
          if (this.data.areaTematicaProyecto?.id === result.areaTematica.value.id) {
            this.checkedNode = result;
          } else {
            result.childs.map(node => {
              if (this.data.areaTematicaProyecto?.id === node.areaTematica.value.id) {
                this.checkedNode = node;
              }
            });
          }
          if (this.data.areasTematicasConvocatoria) {
            if (this.data.areasTematicasConvocatoria?.map(area => area.id)
              .includes(result.areaTematica.value.id)) {
              result.disabled = false;
              this.expandNodes(result);
            } else {
              result.disabled = true;
              this.disableChilds(result);

              result.childs.map(node => {
                if (this.data.areasTematicasConvocatoria?.map(area => area.id)
                  .includes(node.areaTematica.value.id)) {
                  node.disabled = false;
                  this.expandNodes(node);
                }
              });
            }
          }
          if (this.checkedNode) {
            this.expandNodes(this.checkedNode);
          }
        },
        (error) => {
          this.logger.error(error);
        }
      );
      this.subscriptions.push(susbcription);
    }
  }

  private disableChilds(node: NodeAreaTematica) {
    if (node && node.childs) {
      node.childs.forEach(child => {
        child.disabled = true;
        this.disableChilds(child);
      });
    }
  }

  private expandNodes(node: NodeAreaTematica) {
    if (node && node.parent) {
      this.treeControl.expand(node.parent);
      this.expandNodes(node.parent);
    }
  }

  private getChilds(parent: NodeAreaTematica): Observable<NodeAreaTematica[]> {
    return this.areaTematicaService.findAllHijosArea(parent.areaTematica.value.id).pipe(
      map((result) => {
        const childs: NodeAreaTematica[] = result.items.map(
          (areaTematica) => {
            const child = new NodeAreaTematica(new StatusWrapper<IAreaTematica>(areaTematica));
            child.parent = parent;
            this.nodeMap.set(child.areaTematica.value.id, child);
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
        else {
          return of([]);
        }
      }),
      takeLast(1)
    );
  }

  private publishNodes(rootNodes?: NodeAreaTematica[]) {
    let nodes = rootNodes ? rootNodes : this.dataSource.data;
    nodes = sortByName(nodes);
    this.dataSource.data = nodes;
  }

  onCheckNode(node: NodeAreaTematica, $event: MatCheckboxChange): void {
    this.checkedNode = $event.checked ? node : undefined;
    this.formGroup.get('area').setValue(this.checkedNode?.areaTematica?.value);
  }
}


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
import { map, mergeMap, switchMap, takeLast } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const AREA_TEMATICA_KEY = marker('csp.area-tematica');
const AREA_TEMATICA_OBSERVACIONES_KEY = marker('csp.area-tematica.observaciones');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface AreaTematicaModalData {
  padre: IAreaTematica;
  areasTematicas: IAreaTematica[];
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

  addChild(child: NodeAreaTematica) {
    child.parent = this;
    child.areaTematica.value.padre = this.areaTematica.value;
    this._childs.push(child);
    this.sortChildsByName();
  }

  removeChild(child: NodeAreaTematica) {
    this._childs = this._childs.filter((areaTematica) => areaTematica !== child);
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

@Component({
  templateUrl: './convocatoria-area-tematica-modal.component.html',
  styleUrls: ['./convocatoria-area-tematica-modal.component.scss']
})
export class ConvocatoriaAreaTematicaModalComponent extends DialogFormComponent<AreaTematicaModalData> implements OnInit {

  areaTematicaTree$ = new BehaviorSubject<NodeAreaTematica[]>([]);
  treeControl = new NestedTreeControl<NodeAreaTematica>(node => node.childs);
  dataSource = new MatTreeNestedDataSource<NodeAreaTematica>();
  private nodeMap = new Map<number, NodeAreaTematica>();

  textSaveOrUpdate: string;

  msgParamEntities = {};
  msgParamEntity = {};
  msgParamObservacionesEntity = {};
  title: string;

  selectedAreasTematicas = [] as IAreaTematica[];
  hasChangesSelectedAreas = false;
  checkedNodes: NodeAreaTematica[];
  hasChild = (_: number, node: NodeAreaTematica) => node.childs.length > 0;

  constructor(
    private readonly logger: NGXLogger,
    matDialogRef: MatDialogRef<ConvocatoriaAreaTematicaModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AreaTematicaModalData,
    private areaTematicaService: AreaTematicaService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data.padre);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.subscriptions.push(this.formGroup.get('padre').valueChanges.subscribe(
      (value) => this.loadTreeAreaTematica(value?.id)
    ));
    this.textSaveOrUpdate = this.data.padre ? MSG_ACEPTAR : MSG_ANADIR;
    if (this.data.padre?.id) {
      this.loadTreeAreaTematica(this.data.padre.id);
    }
  }

  private setupI18N(): void {
    this.translate.get(
      AREA_TEMATICA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntities = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      AREA_TEMATICA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      AREA_TEMATICA_OBSERVACIONES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamObservacionesEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    if (this.data.padre) {
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
      padre: new FormControl(this.data.padre, Validators.required),
    });
    return formGroup;
  }

  protected getValue(): AreaTematicaModalData {
    const padre = this.formGroup.get('padre').value;
    const areasTematicas = this.selectedAreasTematicas;
    const data = { padre, areasTematicas } as AreaTematicaModalData;
    return data;
  }

  private loadTreeAreaTematica(padreId: number) {
    this.areaTematicaTree$.next([]);
    this.nodeMap.clear();
    this.checkedNodes = [];
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
          this.data.areasTematicas.forEach(element => {
            const node = this.nodeMap.get(element.id);
            if (node) {
              node.checked = true;
              this.disableChilds(node);
              if (!this.checkedNodes.includes(node)) {
                this.checkedNodes.push(node);
              }
            }
          });
          if (this.checkedNodes) {
            this.checkedNodes.forEach(node => this.expandNodes(node));
            if (this.checkedNodes.map(node => !this.selectedAreasTematicas?.includes(node.areaTematica.value))) {
              this.selectedAreasTematicas = this.checkedNodes.map(node => node.areaTematica.value);
            }
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

  private unCheckChilds(node: NodeAreaTematica) {
    if (node && node.childs) {
      node.childs.forEach(child => {
        child.checked = false;
        this.unCheckChilds(child);
      });
      this.selectedAreasTematicas = this.selectedAreasTematicas.filter(checkedNode => checkedNode.id !== node.areaTematica.value.id);
    }
  }

  private enableChilds(node: NodeAreaTematica) {
    if (node && node.childs) {
      node.childs.forEach(child => {
        child.disabled = false;
        this.enableChilds(child);
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
    let nodes = rootNodes ? rootNodes : this.areaTematicaTree$.value;
    nodes = sortByName(nodes);
    this.areaTematicaTree$.next(nodes);
    this.areaTematicaTree$.subscribe(
      (areaTematicas) => {
        this.dataSource.data = areaTematicas;
      }
    );
  }

  onCheckNode(node: NodeAreaTematica, $event: MatCheckboxChange): void {
    node.checked = $event.checked;
    if ($event.checked) {
      this.disableChilds(node);
      this.unCheckChilds(node);
      this.selectedAreasTematicas.push(node.areaTematica.value);
    } else {
      this.enableChilds(node);
      this.selectedAreasTematicas = this.selectedAreasTematicas.filter(checkedNode => checkedNode.id !== node.areaTematica.value.id);
    }
    this.checkChangesSelectedAreas();
  }


  checkChangesSelectedAreas(): void {
    this.hasChangesSelectedAreas = this.selectedAreasTematicas.length !== this.data.areasTematicas.length
      || !this.selectedAreasTematicas.every(area => this.data.areasTematicas.map(a => a.id).includes(area.id));
  }

  get actionDisabled(): boolean {
    return (this.status$.value.errors)
      || (this.isEdit() && !this.status$.value.changes && !this.hasChangesSelectedAreas)
      || (!this.isEdit() && !this.status$.value.complete);
  }
}

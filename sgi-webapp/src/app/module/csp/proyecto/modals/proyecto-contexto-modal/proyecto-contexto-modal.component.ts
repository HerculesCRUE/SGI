import { NestedTreeControl } from '@angular/cdk/tree';
import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { AreaTematicaService } from '@core/services/csp/area-tematica.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast } from 'rxjs/operators';

const AREA_TEMATICA_LISTADO_KEY = marker('list.entity');
const AREA_TEMATICA_KEY = marker('csp.area-tematica');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ProyectoContextoModalData {
  root: IAreaTematica;
  areaTematica: IAreaTematica;
  areaTematicaConvocatoria: IAreaTematica;
}

class NodeAreaTematica {
  parent: NodeAreaTematica;
  areaTematica: StatusWrapper<IAreaTematica>;
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
export class ProyectoContextoModalComponent extends
  BaseModalComponent<ProyectoContextoModalData, ProyectoContextoModalComponent> implements OnInit {
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  areasTematicas$: Observable<IAreaTematica[]>;
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
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ProyectoContextoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoContextoModalData,
    private areaTematicaService: AreaTematicaService,
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
    this.textSaveOrUpdate = this.data?.areaTematica ? MSG_ACEPTAR : MSG_ANADIR;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.loadAreasTematicasGrupo();
    this.loadTreeAreaTematica();

    const subscription = this.formGroup.get('padre').valueChanges.subscribe(() => this.loadTreeAreaTematica());
    this.subscriptions.push(subscription);
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

    if (this.data?.areaTematica) {
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

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      padre: new FormControl({
        value: this.data?.root,
        disabled: Boolean(this.data?.root?.nombre),
      }, [Validators.required, IsEntityValidator.isValid()]),
    });
    return formGroup;
  }

  protected getDatosForm(): ProyectoContextoModalData {
    const padre = this.formGroup.get('padre').value;
    const areaTematica = this.checkedNode?.areaTematica?.value;
    if (areaTematica) {
      this.data.areaTematica = areaTematica;
    } else {
      this.data.areaTematica = null;
    }

    this.data.root = padre;
    return this.data;
  }

  private loadAreasTematicasGrupo(): void {
    this.dataSource.data = null;
    this.areasTematicas$ = this.areaTematicaService.findAllGrupo().pipe(
      map(res => res.items),
      catchError(error => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_AREA_TEMATICA);
        return of([]);
      })
    );
  }

  private loadTreeAreaTematica(): void {
    this.nodeMap.clear();
    this.dataSource.data = [];
    const padre = this.formGroup.get('padre').value;

    if (this.data.areaTematicaConvocatoria) {
      const node = new NodeAreaTematica(new StatusWrapper<IAreaTematica>(this.data.areaTematicaConvocatoria));
      this.rootNodeConvocatoria = node;
      this.nodeMap.set(node.areaTematica.value.id, node);
      const susbcription = this.getChilds(node).pipe(map(() => node))
        .subscribe((result) => {
          this.publishNodes([result]);
          this.checkedNode = this.nodeMap.get(this.data.areaTematica?.id);
          if (this.checkedNode) {
            this.expandNodes(this.checkedNode);
          }
        });
      this.subscriptions.push(susbcription);
    } else if (padre) {
      const susbcription = this.areaTematicaService.findAllHijosArea(padre.id).pipe(
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
          const current = this.dataSource.data ? this.dataSource.data : [];
          current.push(result);
          this.publishNodes(current);
          this.checkedNode = this.nodeMap?.get(this.data?.areaTematica?.id);
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

  getNombreAreaTematica(areaTematica?: IAreaTematica): string | undefined {
    return typeof areaTematica === 'string' ? areaTematica : areaTematica?.nombre;
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
  }
}


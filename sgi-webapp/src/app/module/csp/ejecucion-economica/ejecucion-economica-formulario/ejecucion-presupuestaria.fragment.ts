import { IConfiguracion } from '@core/models/csp/configuracion';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { Observable, of } from 'rxjs';
import { IRelacionEjecucionEconomicaWithResponsables } from '../ejecucion-economica.action.service';
import { DesgloseEconomicoFragment, IColumnDefinition, IRowConfig, RowTreeDesglose } from './desglose-economico.fragment';

export abstract class EjecucionPresupuestariaFragment extends DesgloseEconomicoFragment<IDatoEconomico> {

  constructor(
    key: number,
    protected proyectoSge: IProyectoSge,
    protected relaciones: IRelacionEjecucionEconomicaWithResponsables[],
    proyectoService: ProyectoService,
    proyectoAnualidadService: ProyectoAnualidadService,
    protected readonly config: IConfiguracion
  ) {
    super(key, proyectoSge, relaciones, proyectoService, proyectoAnualidadService, config);
    this.setComplete(true);
  }

  protected abstract getColumns(): Observable<IColumnDefinition[]>;

  protected abstract getDatosEconomicos(anualidades: string[]): Observable<IDatoEconomico[]>;

  protected abstract getRowConfig(): IRowConfig;

  protected buildRows(datosEconomicos: IDatoEconomico[], rowConfig: IRowConfig): Observable<RowTreeDesglose<IDatoEconomico>[]> {
    const root: RowTreeDesglose<IDatoEconomico>[] = [];
    const mapTree = new Map<string, RowTreeDesglose<IDatoEconomico>>();
    datosEconomicos.forEach(element => {
      const groupByAnualidad = rowConfig?.anualidadGroupBy ?? false;
      const groupByTipo = rowConfig?.tipoGroupBy ?? false;
      const groupByAplicacionPresupuestaria = rowConfig?.aplicacionPresupuestariaGroupBy ?? false;

      const keyAnualidad = groupByTipo ? `${element.anualidad}-${element.tipo}` : `${element.anualidad}-0`;
      const keyTipo = groupByAnualidad ? `${element.anualidad}-${element.tipo}` : `0-${element.tipo}`;
      const keyPartida = groupByAnualidad ? `${keyAnualidad}-${element.partidaPresupuestaria}` : `0-${element.partidaPresupuestaria}`;

      let anualidad = mapTree.get(keyAnualidad);

      let lastParent: RowTreeDesglose<IDatoEconomico>;

      if (groupByAnualidad) {
        if (!anualidad) {
          anualidad = new RowTreeDesglose(
            {
              anualidad: element.anualidad,
              tipo: element.tipo,
              partidaPresupuestaria: '',
              columnas: this.processColumnsValues(element.columnas, this.columns, true)
            } as IDatoEconomico
          );
          mapTree.set(keyAnualidad, anualidad);
          root.push(anualidad);
        }

        lastParent = anualidad;
      } else if (groupByTipo) {
        let tipo = mapTree.get(keyTipo);
        if (!tipo) {
          tipo = new RowTreeDesglose(
            {
              anualidad: element.anualidad,
              tipo: element.tipo,
              partidaPresupuestaria: '',
              columnas: this.processColumnsValues(element.columnas, this.columns, true)
            } as IDatoEconomico
          );
          mapTree.set(keyTipo, tipo);
          root.push(tipo);
        }

        lastParent = tipo;
      }

      if (groupByAplicacionPresupuestaria) {
        let partida = mapTree.get(keyPartida);
        if (!partida) {
          partida = new RowTreeDesglose(
            {
              anualidad: !groupByAnualidad && !groupByTipo ? element.anualidad : '',
              tipo: !groupByAnualidad && !groupByTipo ? element.tipo : '',
              partidaPresupuestaria: element.partidaPresupuestaria,
              columnas: this.processColumnsValues(element.columnas, this.columns, true)
            } as IDatoEconomico
          );
          mapTree.set(keyPartida, partida);

          if (lastParent) {
            lastParent.addChild(partida);
          } else {
            root.push(partida);
          }
        }

        lastParent = partida;
      }

      const datoEconomico = new RowTreeDesglose(
        {
          anualidad: element.anualidad,
          tipo: element.tipo,
          partidaPresupuestaria: element.partidaPresupuestaria,
          columnas: this.processColumnsValues(element.columnas, this.columns, false)
        } as IDatoEconomico
      );

      if (lastParent) {
        lastParent.addChild(datoEconomico);
      } else {
        root.push(datoEconomico);
      }

    });
    return of(root);
  }
}

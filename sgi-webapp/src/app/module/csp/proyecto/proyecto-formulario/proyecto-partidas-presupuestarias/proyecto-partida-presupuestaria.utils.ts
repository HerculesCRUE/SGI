import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { IPartidaPresupuestaria } from '@core/models/csp/partida-presupuestaria';

export function comparePartidaPresupuestaria(
  convocatoriaPartidaPresupuestaria: IConvocatoriaPartidaPresupuestaria,
  proyectoPartidaPresupuestaria: IPartidaPresupuestaria): boolean {


  return proyectoPartidaPresupuestaria.descripcion !== convocatoriaPartidaPresupuestaria?.descripcion
    || proyectoPartidaPresupuestaria.codigo !== convocatoriaPartidaPresupuestaria?.codigo
    || proyectoPartidaPresupuestaria.tipoPartida !== convocatoriaPartidaPresupuestaria?.tipoPartida;
}

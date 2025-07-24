import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { IPartidaPresupuestaria } from '@core/models/csp/partida-presupuestaria';

export function comparePartidaPresupuestaria(
  convocatoriaPartidaPresupuestaria: IConvocatoriaPartidaPresupuestaria,
  proyectoPartidaPresupuestaria: IPartidaPresupuestaria): boolean {


  return !isDescripcionEquals(proyectoPartidaPresupuestaria.descripcion, convocatoriaPartidaPresupuestaria?.descripcion)
    || proyectoPartidaPresupuestaria.codigo !== convocatoriaPartidaPresupuestaria?.codigo
    || proyectoPartidaPresupuestaria.tipoPartida !== convocatoriaPartidaPresupuestaria?.tipoPartida;
}

function isDescripcionEquals(
  descripcionProyecto: I18nFieldValue[] = [],
  descripcionConvocatoria: I18nFieldValue[] = []
): boolean {
  if (descripcionProyecto == null || descripcionConvocatoria == null) {
    return false;
  }
  const valoresProyecto = new Set(descripcionProyecto.map(d => `${d.lang}-${d.value}`));
  const valoresConvocatoria = new Set(descripcionConvocatoria.map(d => `${d.lang}-${d.value}`));

  return isSetsEqual(valoresProyecto, valoresConvocatoria);
}

function isSetsEqual(setA: Set<string>, setB: Set<string>): boolean {
  if (setA.size !== setB.size) return false;
  return [...setA].every(value => setB.has(value));
}

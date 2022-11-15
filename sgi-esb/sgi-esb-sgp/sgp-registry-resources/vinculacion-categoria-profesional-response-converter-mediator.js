/**
 * Conversion del objeto VinculacionCategoriaProfesional de la UM al del SGI
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("vinculacion-categoria-profesional-response-converter-mediator.mediate() - start");

  var vinculacionesCategoriasProfesionales = mc.getPayloadJSON();
  if (vinculacionesCategoriasProfesionales) {
    var vinculacionCategoriaProfesionalResponse = {};

    var sgiVinculacionCategoriaProfesional = getSGIVinculacionCategoriaProfesional(vinculacionesCategoriasProfesionales);

    if (sgiVinculacionCategoriaProfesional.categoriaProfesional) {
      vinculacionCategoriaProfesionalResponse.categoriaProfesional = sgiVinculacionCategoriaProfesional.categoriaProfesional;
    }
    if (sgiVinculacionCategoriaProfesional.fechaObtencion) {
      vinculacionCategoriaProfesionalResponse.fechaObtencion = sgiVinculacionCategoriaProfesional.fechaObtencion;
    }

    mc.setPayloadJSON(vinculacionCategoriaProfesionalResponse);
  }

  log.info("vinculacion-categoria-profesional-response-converter-mediator.mediate() - end");
}

function getSGIVinculacionCategoriaProfesional(vinculacionesCategoriasProfesionales) {
  var sgiVinculacionCategoriaProfesional;
  if (Array.isArray(vinculacionesCategoriasProfesionales)) {
    vinculacionesCategoriasProfesionales.forEach(
      function (vinculacionCategoriaProfesional) {
        if (vinculacionCategoriaProfesional.tipoPersonal === 'PDI') {
          sgiVinculacionCategoriaProfesional = vinculacionCategoriaProfesional;
        } else if (
          !sgiVinculacionCategoriaProfesional
          && vinculacionCategoriaProfesional.tipoPersonal === 'PAS'
        ) {
          sgiVinculacionCategoriaProfesional = vinculacionCategoriaProfesional;
        }
      }
    );
  }

  if (sgiVinculacionCategoriaProfesional
    && sgiVinculacionCategoriaProfesional.categoriaProfesional
    && sgiVinculacionCategoriaProfesional.categoriaProfesional.nombre) {
    sgiVinculacionCategoriaProfesional.categoriaProfesional.nombre = escape(sgiVinculacionCategoriaProfesional.categoriaProfesional.nombre);
  }

  return sgiVinculacionCategoriaProfesional ? sgiVinculacionCategoriaProfesional : {};
}

function escape(val) {
  if (typeof (val) != "string") return val;
  return val
    .replace(/[\\]/g, '\\\\')
    .replace(/[\/]/g, '\\/')
    .replace(/[\b]/g, '\\b')
    .replace(/[\f]/g, '\\f')
    .replace(/[\n]/g, '\\n')
    .replace(/[\r]/g, '\\r')
    .replace(/[\t]/g, '\\t')
    .replace(/[\"]/g, '\\"')
    .replace(/\\'/g, "\\'");
}

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

  return sgiVinculacionCategoriaProfesional ? sgiVinculacionCategoriaProfesional : {};
}

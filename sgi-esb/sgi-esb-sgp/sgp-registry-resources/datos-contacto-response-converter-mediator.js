/**
 * Conversion del objeto datosContacto de la UM al del SGI
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("datos-contacto-response-converter-mediator.mediate() - start");

  var datosContacto = mc.getPayloadJSON();
  if (datosContacto) {
    var datosContactoResponse = {};

    if (datosContacto.paisContacto) {
      datosContactoResponse.paisContacto = datosContacto.paisContacto;
    }
    if (datosContacto.comAutonomaContacto) {
      datosContactoResponse.comAutonomaContacto = datosContacto.comAutonomaContacto;
    }
    if (datosContacto.provinciaContacto) {
      datosContactoResponse.provinciaContacto = datosContacto.provinciaContacto;
    }
    if (datosContacto.ciudadContacto) {
      datosContactoResponse.ciudadContacto = datosContacto.ciudadContacto;
    }
    if (datosContacto.codigoPostalContacto) {
      datosContactoResponse.codigoPostalContacto = datosContacto.codigoPostalContacto;
    }
    if (datosContacto.emails) {
      datosContactoResponse.emails = datosContacto.emails;
    }
    if (datosContacto.telefonos) {
      datosContactoResponse.telefonos = datosContacto.telefonos;
    }
    if (datosContacto.moviles) {
      datosContactoResponse.moviles = datosContacto.moviles;
    }

    var direccionContacto = null;
    if (datosContacto.tipoViaContacto) {
      direccionContacto = datosContacto.tipoViaContacto.nombre;
    }
    if (datosContacto.nombreViaContacto) {
      direccionContacto = direccionContacto ?
        direccionContacto.concat(' ').concat(datosContacto.nombreViaContacto) : datosContacto.nombreViaContacto;
    }
    if (datosContacto.numeroViaContacto) {
      direccionContacto = direccionContacto ?
        direccionContacto.concat(', ').concat(datosContacto.numeroViaContacto) : datosContacto.numeroViaContacto;
    }
    if (datosContacto.ampliacionDireccionContacto) {
      direccionContacto = direccionContacto ?
        direccionContacto.concat(', ').concat(datosContacto.ampliacionDireccionContacto) : datosContacto.ampliacionDireccionContacto;
    }

    if (direccionContacto) {
      datosContactoResponse.direccionContacto = escape(direccionContacto);
    }

    mc.setPayloadJSON(datosContactoResponse);
  }

  log.info("datos-contacto-response-converter-mediator.mediate() - end");
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
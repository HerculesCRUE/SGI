/**
 * Conversion del objeto datosContacto de la UM al del SGI
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("datos-contacto-response-converter-mediator.mediate() - start");

  var datosContacto = mc.getPayloadJSON();
  var datosContactoResponse = {};
  var direccionContacto = null;

  if (datosContacto.paisContacto) {
    direccionContacto = datosContacto.paisContacto.nombre;
  }
  if (datosContacto.comAutonomaContacto) {
    direccionContacto = direccionContacto ?
      direccionContacto.concat(', ').concat(datosContacto.comAutonomaContacto.nombre) : datosContacto.comAutonomaContacto.nombre;
  }
  if (datosContacto.provinciaContacto) {
    direccionContacto = direccionContacto ?
      direccionContacto.concat(', ').concat(datosContacto.provinciaContacto.nombre) : datosContacto.provinciaContacto.nombre;
  }
  if (datosContacto.ciudadContacto) {
    direccionContacto = direccionContacto ?
      direccionContacto.concat(', ').concat(datosContacto.ciudadContacto) : datosContacto.ciudadContacto;
  }
  if (datosContacto.codigoPostal) {
    direccionContacto = direccionContacto ?
      direccionContacto.concat(', ').concat(datosContacto.codigoPostal) : datosContacto.codigoPostal;
  }
  if (datosContacto.tipoVia) {
    direccionContacto = direccionContacto ?
      direccionContacto.concat(', ').concat(datosContacto.tipoVia.nombre) : datosContacto.tipoVia.nombre;
  }
  if (datosContacto.nombreVia) {
    direccionContacto =
      direccionContacto ?
        datosContacto.tipoVia ? direccionContacto.concat(' ').concat(datosContacto.nombreVia)
          : direccionContacto.concat(', ').concat(datosContacto.nombreVia)
        : datosContacto.nombreVia;
  }
  if (datosContacto.numero) {
    direccionContacto = direccionContacto ? direccionContacto.concat(', ').concat(datosContacto.numero) : datosContacto.numero;
  }
  if (datosContacto.ampliacion) {
    direccionContacto = direccionContacto ?
      direccionContacto.concat(', ').concat(datosContacto.ampliacion) : datosContacto.ampliacion;
  }

  if (direccionContacto) {
    datosContactoResponse.direccion = direccionContacto;
  }

  mc.setPayloadJSON(datosContactoResponse);

  log.info("datos-contacto-response-converter-mediator.mediate() - end");
}


<#assign data = CSP_COM_SOL_CAMB_EST_SOLICITADA_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Con fecha ${data.fechaCambioEstadoSolicitud?datetime.iso?string("dd/MM/yyyy")} ha sido registrada en nuestra base de datos la solicitud presentada por D./D&ntilde;a. ${data.nombreApellidosSolicitante} a la convocatoria de ${data.tituloConvocatoria} <#if data.fechaPublicacionConvocatoria??>de ${data.fechaPublicacionConvocatoria?datetime.iso?string("dd/MM/yyyy")}</#if>.</p>
  </body>
</html>
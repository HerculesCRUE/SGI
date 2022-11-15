<#assign data = CSP_COM_SOL_USUARIO_EXTERNO_DATA?eval_json />
Estimado/a,

Le informamos que  su solicitud<#if data.tituloConvocatoria?has_content>, asociada a la convocatoria ${data.tituloConvocatoria},</#if> ha sido creada.

Puede consultar el estado de la misma desde la aplicación ${data.enlaceAplicacion} introduciendo su número de documento de identificación personal y el código ${data.uuid}.

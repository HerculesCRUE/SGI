INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(1, 'master', '2022-08-04 10:29:23.038', 'master', '2022-08-04 10:29:23.038', 'Plantilla de contenido genérico (texto + HTML)', 'GENERIC_CONTENT', '<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    ${GENERIC_CONTENT_HTML}
  </body>
</html>', '${GENERIC_CONTENT_TEXT}');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(2, 'master', '2022-08-04 10:29:23.038', 'master', '2022-08-04 10:29:23.038', 'Plantilla de contenido genérico (solo texto)', 'GENERIC_CONTENT_TEXT', NULL, '${GENERIC_CONTENT_TEXT}');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(8, 'master', '2022-08-04 10:29:23.751', 'master', '2022-08-04 10:29:23.751', 'Contenido del aviso de finalización del período de presentación de justificación de gastos (texto + HTML) para ips', 'CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_CONTENT', '<#assign data = CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA?eval />
<#--
  Formato CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA:
  { 
    "titulo": "Proyecto 1",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2
  } 
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a, le informamos que a partir de la fecha <b>${data.fechaFin?datetime.iso?string.medium}</b> finalizará el plazo de presentación 
    de la documentación de seguimiento científico del proyecto <b>${data.titulo}</b>, correspondiente al periodo número <b>${data.numPeriodo}</b>.</p>

    <p>Reciba un cordial saludo.</p>

    <p>Área de Investigación</p>

  </body>
</html>


', '<#assign data = CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA?eval />
<#--
  Formato CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA:
  { 
    "titulo": "Proyecto 1",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2
  }
-->
Estimado/a investigador/a, le informamos que con fecha ${data.fechaFin?datetime.iso?string.medium} finalizará el plazo de presentación 
de la documentación de seguimiento científico del proyecto ${data.titulo}, correspondiente al periodo número ${data.numPeriodo}.

Reciba un cordial saludo.

Área de Investigación');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(4, 'master', '2022-08-04 10:29:23.038', 'master', '2022-08-04 10:29:23.038', 'Contenido del aviso de inicio del período de presentación de justificación de gastos (texto + HTML)', 'CSP_COM_INICIO_PRESENTACION_GASTO_CONTENT', '<#assign data = CSP_COM_INICIO_PRESENTACION_GASTO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_GASTO_DATA:
  { 
    "fecha": "2022-01-01",
    "proyectos" : [
      {
        "titulo": "Proyecto 1",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      },
      {
        "titulo": "Proyecto 2",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      }
    ]
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>En el mes de <b>${data.fecha?date.iso?string.MMMM}</b> de <b>${data.fecha?date.iso?string.yyyy}</b> se inicia el per&iacute;odo de presentaci&oacute;n de la justificaci&oacute;n de gastos de los siguientes proyectos:</p>

    <ul>
    <#list data.proyectos as proyecto>
      <li><b><i><#outputformat "HTML">${proyecto.titulo?esc}</#outputformat></i></b>
        <ul>
          <li>Primer d&iacute;a para la presentaci&oacute;n de la documentaci&oacute;n de justificaci&oacute;n <b>${proyecto.fechaInicio?datetime.iso?string.medium}</b></li>
          <#if proyecto.fechaFin??>
          <li>&Uacute;ltimo d&iacute;a para la presentaci&oacute;n de la documentaci&oacute;n de justificaci&oacute;n <b>${proyecto.fechaFin?datetime.iso?string.medium}</b></li>
          </#if>
        </ul>
      </li>
    </#list>
    </ul>
  </body>
</html>', '<#assign data = CSP_COM_INICIO_PRESENTACION_GASTO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_GASTO_DATA:
  { 
    "fecha": "2022-01-01",
    "proyectos" : [
      {
        "titulo": "Proyecto 1",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      },
      {
        "titulo": "Proyecto 2",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      }
    ]
  }
-->
En el mes de ${data.fecha?date.iso?string.MMMM} de ${data.fecha?date.iso?string.yyyy} se inicia el período de presentación de la justificación de gastos de los siguientes proyectos:

<#list data.proyectos as proyecto> 
* ${proyecto.titulo}
  * Primer día para la presentación de la documentación de justificación ${proyecto.fechaInicio?datetime.iso?string.medium}
  <#if proyecto.fechaFin??>
  * Último día para la presentación de la documentación de justificación ${proyecto.fechaFin?datetime.iso?string.medium}
  </#if>

</#list>');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(9, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el alta de solicitud de petición de evaluación de ética', 'CSP_COM_SOLICITUD_PETICION_EVALUACION_CONTENT', '<#ftl output_format="HTML" auto_esc=false>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le informamos de que es necesario someter su investigaci&oacute;n ante la Comisi&oacute;n de &Eacute;tica, por lo que se ha creado una solicitud de petici&oacute;n de evaluaci&oacute;n con el c&oacute;digo &quot;${ETI_PETICION_EVALUACION_CODIGO?esc}&quot; asociada a la solicitud de convocatoria &quot;${CSP_SOLICITUD_CODIGO?esc}&quot;.</p>
  </body>
</html>', 'Estimado/a investigador/a,
le informamos de que es necesario someter su investigación ante la Comisión de Ética, por lo que se ha creado una solicitud de petición de evaluación con el código "${ETI_PETICION_EVALUACION_CODIGO}" asociada a la solicitud de convocatoria "${CSP_SOLICITUD_CODIGO}".');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(5, 'master', '2022-08-04 10:29:23.038', 'master', '2022-08-04 10:29:23.038', 'Content of the notice of the start of the period for submitting justification of expenses (text + HTML) ', 'CSP_COM_INICIO_PRESENTACION_GASTO_CONTENT_en', '<#assign data = CSP_COM_INICIO_PRESENTACION_GASTO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_GASTO_DATA:
  { 
    "fecha": "2022-01-01",
    "proyectos" : [
      {
        "titulo": "Proyecto 1",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      },
      {
        "titulo": "Proyecto 2",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      }
    ]
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>In the month of <b>${data.fecha?date.iso?string.MMMM}</b> of <b>${data.fecha?date.iso?string.yyyy}</b>, the period for submitting the justification of expenses for the following projects begins:</p>

    <ul>
    <#list data.proyectos as proyecto>
      <li><b><i><#outputformat "HTML">${proyecto.titulo?esc}</#outputformat></i></b>
        <ul>
          <li>First day for the presentation of the justification documentation <b>${proyecto.fechaInicio?datetime.iso?string.medium}</b></li>
          <#if proyecto.fechaFin??>
          <li>Last day for the presentation of the justification documentation <b>${proyecto.fechaFin?datetime.iso?string.medium}</b></li>
          </#if>
        </ul>
      </li>
    </#list>
    </ul>
  </body>
</html>', '<#assign data = CSP_COM_INICIO_PRESENTACION_GASTO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_GASTO_DATA:
  { 
    "fecha": "2022-01-01",
    "proyectos" : [
      {
        "titulo": "Proyecto 1",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      },
      {
        "titulo": "Proyecto 2",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      }
    ]
  }
-->
In the month of ${data.fecha?date.iso?string.MMMM} of ${data.fecha?date.iso?string.yyyy}, the period for submitting the justification of expenses for the following projects begins:

<#list data.proyectos as proyecto>
* ${proyecto.titulo}
   * First day for the presentation of the justification documentation ${proyecto.fechaInicio?datetime.iso?string.medium}
  <#if proyecto.fechaFin??>
   * Last day for the presentation of the justification documentation ${proyecto.fechaFin?datetime.iso?string.medium}
  </#if>

</#list>');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(6, 'master', '2022-08-04 10:29:23.751', 'master', '2022-08-04 10:29:23.751', 'Contenido del aviso de inicio del período de presentación de justificación de gastos (texto + HTML)', 'CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_CONTENT', '<#assign data = CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_DATA:
  { 
    "fecha": "2022-01-01",
    "proyectos" : [
      {
        "titulo": "Proyecto 1",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      },
      {
        "titulo": "Proyecto 2",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      }
    ]
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>En el mes de <b>${data.fecha?date.iso?string.MMMM}</b> de <b>${data.fecha?date.iso?string.yyyy}</b> se inicia el per&iacute;odo de presentaci&oacute;n de la justificaci&oacute;n del seguimiento científico de los siguientes proyectos:</p>

    <ul>
    <#list data.proyectos as proyecto>
      <li><b><i><#outputformat "HTML">${proyecto.titulo?esc}</#outputformat></i></b>
        <ul>
          <li>Primer d&iacute;a para la presentaci&oacute;n de la documentaci&oacute;n de justificaci&oacute;n <b>${proyecto.fechaInicio?datetime.iso?string.medium}</b></li>
          <#if proyecto.fechaFin??>
          <li>&Uacute;ltimo d&iacute;a para la presentaci&oacute;n de la documentaci&oacute;n de justificaci&oacute;n <b>${proyecto.fechaFin?datetime.iso?string.medium}</b></li>
          </#if>
        </ul>
      </li>
    </#list>
    </ul>
  </body>
</html>', '<#assign data = CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_DATA:
  { 
    "fecha": "2022-01-01",
    "proyectos" : [
      {
        "titulo": "Proyecto 1",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      },
      {
        "titulo": "Proyecto 2",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      }
    ]
  }
-->
En el mes de ${data.fecha?date.iso?string.MMMM} de ${data.fecha?date.iso?string.yyyy} se inicia el período de presentación de la justificación del seguimiento científico de los siguientes proyectos:

<#list data.proyectos as proyecto> 
* ${proyecto.titulo}
  * Primer día para la presentación de la documentación de justificación ${proyecto.fechaInicio?datetime.iso?string.medium}
  <#if proyecto.fechaFin??>
  * Último día para la presentación de la documentación de justificación ${proyecto.fechaFin?datetime.iso?string.medium}
  </#if>

</#list>');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(7, 'master', '2022-08-04 10:29:23.751', 'master', '2022-08-04 10:29:23.751', 'Contenido del aviso de inicio del período de presentación de justificación de gastos (texto + HTML) para IPs', 'CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_CONTENT', '<#assign data = CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA:
  { 
    "titulo": "Proyecto 1",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2,
    "nombreEntidad": "nombre"
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a, le informamos que a partir de la fecha <b>${data.fechaInicio?datetime.iso?string.medium}</b> se inicia el plazo de presentación 
    de la documentación de seguimiento científico del proyecto <b>${data.titulo}</b>, correspondiente al periodo número <b>${data.numPeriodo}</b>.</p>

    <p>Reciba un cordial saludo.</p>

    <p>Área de Investigación</p>

  </body>
</html>


', '<#assign data = CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA:
  { 
    "titulo": "Proyecto 1",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2
  }
-->
Estimado/a investigador/a, le informamos que a partir de la fecha ${data.fechaInicio?datetime.iso?string.medium} se inicia el plazo de presentación 
de la documentación de seguimiento científico del proyecto ${data.titulo}, correspondiente al periodo número ${data.numPeriodo}.

Reciba un cordial saludo.

Área de Investigación');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(10, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el cambio de estado de solicitud a solicitada', 'CSP_COM_SOL_CAMB_EST_SOLICITADA_CONTENT', '<#assign data = CSP_COM_SOL_CAMB_EST_SOLICITADA_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Con fecha ${data.fechaCambioEstadoSolicitud?datetime.iso?string("dd/MM/yyyy")} ha sido registrada en nuestra base de datos la solicitud presentada por D./D&ntilde;a. ${data.nombreApellidosSolicitante} a la convocatoria de ${data.tituloConvocatoria} <#if data.fechaPublicacionConvocatoria??>de ${data.fechaPublicacionConvocatoria?datetime.iso?string("dd/MM/yyyy")}</#if>.</p>
  </body>
</html>', '<#assign data = CSP_COM_SOL_CAMB_EST_SOLICITADA_DATA?eval_json />
Con fecha ${data.fechaCambioEstadoSolicitud?datetime.iso?string("dd/MM/yyyy")} ha sido registrada en nuestra base de datos la solicitud presentada por D./D&ntilde;a. ${data.nombreApellidosSolicitante} a la convocatoria de ${data.tituloConvocatoria} <#if data.fechaPublicacionConvocatoria??>de ${data.fechaPublicacionConvocatoria?datetime.iso?string("dd/MM/yyyy")}</#if>.');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(11, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el cambio de estado de solicitud a alegaciones', 'CSP_COM_SOL_CAMB_EST_ALEGACIONES_CONTENT', '<#assign data = CSP_COM_SOL_CAMB_EST_ALEGACIONES_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>${data.nombreApellidosSolicitante} ha registrado en nuestra base de datos, con fecha ${data.fechaCambioEstadoSolicitud?datetime.iso?string("dd/MM/yyyy")}, una alegación a la resolución de la convocatoria ${data.tituloConvocatoria}<#if data.fechaProvisionalConvocatoria??>, con fecha provisional ${data.fechaProvisionalConvocatoria?datetime.iso?string("dd/MM/yyyy")}</#if>, en relación a la solicitud ${data.codigoInternoSolicitud}.</p>
  </body>
</html>', '<#assign data = CSP_COM_SOL_CAMB_EST_ALEGACIONES_DATA?eval_json />
${data.nombreApellidosSolicitante} ha registrado en nuestra base de datos, con fecha ${data.fechaCambioEstadoSolicitud?datetime.iso?string("dd/MM/yyyy")}, una alegación a la resolución de la convocatoria ${data.tituloConvocatoria} <#if data.fechaProvisionalConvocatoria??>con fecha provisional ${data.fechaProvisionalConvocatoria?datetime.iso?string("dd/MM/yyyy")}</#if>, en relación a la solicitud ${data.codigoInternoSolicitud}.');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(12, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el cambio de estado de solicitud a excluida provisional', 'CSP_COM_SOL_CAMB_EST_EXCL_PROV_CONTENT', '<#assign data = CSP_COM_SOL_CAMB_EST_EXCL_PROV_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le informamos que <#if (data.fechaProvisionalConvocatoria)??>con fecha ${data.fechaProvisionalConvocatoria?datetime.iso?string("dd/MM/yyyy")} </#if>se ha publicado la “Resolución con carácter provisional de Admitidos y Excluidos” de la convocatoria de ${data.tituloConvocatoria} en la que su solicitud aparece excluida.</p>
    <#if data.enlaces?has_content>
    <p>Más información disponible en los siguientes enlaces:</p>
    <ul>
      <#list data.enlaces as enlace>
          <ul>
            <#assign url>${enlace.url}</#assign>
            <#if enlace.tipoEnlace?? && enlace.descripcion?? && enlace.url?? >
            <li><#outputformat "HTML">${enlace.tipoEnlace?esc}</#outputformat>: <#outputformat "HTML">${enlace.descripcion?esc}</#outputformat> (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.descripcion?? && enlace.url?? >          
            <li><#outputformat "HTML">${enlace.descripcion?esc}</#outputformat> (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.tipoEnlace?? && enlace.url?? >          
            <li><#outputformat "HTML">${enlace.tipoEnlace?esc}</#outputformat>: (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.url??>
            <li>Link a <a href="${url}">${url}</a></li>
            </#if>          
          </ul>
        </li>
      </#list>
    </ul>
    </#if>
    <p>Reciba un cordial saludo.</p>
    <p>Sección de Recursos Humanos de la Investigación y Plan Propio</p>
    <p>Área de Investigación</p>
  </body>
</html>', '<#assign data = CSP_COM_SOL_CAMB_EST_EXCL_PROV_DATA?eval_json />
Estimado/a investigador/a,

le informamos que <#if (data.fechaProvisionalConvocatoria)??>con fecha ${data.fechaProvisionalConvocatoria?datetime.iso?string("dd/MM/yyyy")} </#if>se ha publicado la “Resolución con carácter provisional de Admitidos y Excluidos” de la convocatoria de ${data.tituloConvocatoria} en la que su solicitud aparece excluida.

<#if data.enlaces?has_content>
Más información disponible en los siguientes enlaces:

<#list data.enlaces as enlace>
  <#if enlace.tipoEnlace?? && enlace.descripcion?? && enlace.url?? >
* ${enlace.tipoEnlace}: ${enlace.descripcion} (link a ${enlace.url})
  <#elseif enlace.descripcion?? && enlace.url??>
* ${enlace.descripcion} (link a ${enlace.url})
  <#elseif enlace.tipoEnlace?? && enlace.url??>
* ${enlace.tipoEnlace} (link a ${enlace.url})
  <#elseif enlace.url??>
* link a ${enlace.url}
  </#if>
</#list>
</#if>

Reciba un cordial saludo.

Sección de Recursos Humanos de la Investigación y Plan Propio
Área de Investigación');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(14, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el cambio de estado de solicitud a denegada provisional', 'CSP_COM_SOL_CAMB_EST_DEN_PROV_CONTENT', '<#assign data = CSP_COM_SOL_CAMB_EST_DEN_PROV_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le informamos que <#if (data.fechaProvisionalConvocatoria)??>con fecha ${data.fechaProvisionalConvocatoria?datetime.iso?string("dd/MM/yyyy")} </#if>se ha publicado la “Resolución Provisional” de la convocatoria de ${data.tituloConvocatoria} en la que su solicitud aparece denegada.</p>
    <#if data.enlaces?has_content>
    <p>Más información disponible en los siguientes enlaces:</p>
    <ul>
      <#list data.enlaces as enlace>
          <ul>
            <#assign url>${enlace.url}</#assign>
            <#if enlace.tipoEnlace?? && enlace.descripcion?? && enlace.url?? >
            <li><#outputformat "HTML">${enlace.tipoEnlace?esc}</#outputformat>: <#outputformat "HTML">${enlace.descripcion?esc}</#outputformat> (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.descripcion?? && enlace.url?? >          
            <li><#outputformat "HTML">${enlace.descripcion?esc}</#outputformat> (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.tipoEnlace?? && enlace.url?? >          
            <li><#outputformat "HTML">${enlace.tipoEnlace?esc}</#outputformat>: (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.url??>
            <li>Link a <a href="${url}">${url}</a></li>
            </#if>          
          </ul>
        </li>
      </#list>
    </ul>
    </#if> 
    <p>Reciba un cordial saludo.</p>
    <p>Sección de Recursos Humanos de la Investigación y Plan Propio</p>
    <p>Área de Investigación</p>
  </body>
</html>', '<#assign data = CSP_COM_SOL_CAMB_EST_DEN_PROV_DATA?eval_json />
Estimado/a investigador/a,

le informamos que <#if (data.fechaProvisionalConvocatoria)??>con fecha ${data.fechaProvisionalConvocatoria?datetime.iso?string("dd/MM/yyyy")} </#if>se ha publicado la “Resolución Provisional” de la convocatoria de ${data.tituloConvocatoria} en la que su solicitud aparece denegada.

<#if data.enlaces?has_content>
Más información disponible en los siguientes enlaces:

<#list data.enlaces as enlace>
  <#if enlace.tipoEnlace?? && enlace.descripcion?? && enlace.url?? >
* ${enlace.tipoEnlace}: ${enlace.descripcion} (link a ${enlace.url})
  <#elseif enlace.descripcion?? && enlace.url??>
* ${enlace.descripcion} (link a ${enlace.url})
  <#elseif enlace.tipoEnlace?? && enlace.url??>
* ${enlace.tipoEnlace} (link a ${enlace.url})
  <#elseif enlace.url??>
* link a ${enlace.url}
  </#if>
</#list>
</#if> 

Reciba un cordial saludo.

Sección de Recursos Humanos de la Investigación y Plan Propio
Área de Investigación');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(3, NULL, NULL, NULL, NULL, 'Plantilla de contenido para Convocatorias-Hitos', 'CSP_CONVOCATORIA_HITO_CONTENT', '<#ftl output_format="HTML" auto_esc=false>
<#assign date = CSP_HITO_FECHA?datetime.iso>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Con fecha ${date?string("dd/MM/yyyy")} a las ${date?string("HH:mm")} se alcanzar&aacute; el hito &quot;${CSP_HITO_TIPO?esc}&quot; de la convocatoria &quot;${CSP_CONVOCATORIA_TITULO?esc}&quot;.</p>
    <#if CSP_HITO_OBSERVACIONES?has_content>
    <p>En el hito se han indicado las siguientes observaciones:
      ${CSP_HITO_OBSERVACIONES?esc}
    </p>
    </#if>
  </body>
</html>', '<#assign date = CSP_HITO_FECHA?datetime.iso>
Con fecha ${date?string("dd/MM/yyyy")} a las ${date?string("HH:mm")} se alcanzará el hito "${CSP_HITO_TIPO}" de la convocatoria "${CSP_CONVOCATORIA_TITULO}".
<#if CSP_HITO_OBSERVACIONES?has_content>

En el hito se han indicado las siguientes observaciones:
${CSP_HITO_OBSERVACIONES}
</#if>');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(18, NULL, NULL, NULL, NULL, 'Plantilla de contenido para Solicitudes-Hitos', 'CSP_SOLICITUD_HITO_CONTENT', '<#ftl output_format="HTML" auto_esc=false>
<#assign date = CSP_HITO_FECHA?datetime.iso>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Con fecha ${date?string("dd/MM/yyyy")} a las ${date?string("HH:mm")} se alcanzar&aacute; el hito &quot;${CSP_HITO_TIPO?esc}&quot; de la solicitud &quot;${CSP_SOLICITUD_TITULO?esc}&quot; asociada a la convocatoria &quot;${CSP_CONVOCATORIA_TITULO?esc}&quot;.</p>
    <#if CSP_HITO_OBSERVACIONES?has_content>
    <p>En el hito se han indicado las siguientes observaciones:
      ${CSP_HITO_OBSERVACIONES?esc}
    </p>
    </#if>
  </body>
</html>', '<#assign date = CSP_HITO_FECHA?datetime.iso>
Con fecha ${date?string("dd/MM/yyyy")} a las ${date?string("HH:mm")} se alcanzará el hito "${CSP_HITO_TIPO}" de la solicitud "${CSP_SOLICITUD_TITULO}" asociada a la convocatoria "${CSP_CONVOCATORIA_TITULO}".
<#if CSP_HITO_OBSERVACIONES?has_content>

En el hito se han indicado las siguientes observaciones:
${CSP_HITO_OBSERVACIONES}
</#if>');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(19, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de una convocatoria de reunión de ética', 'ETI_COM_CONVOCATORIA_REUNION_CONTENT', '<#assign horaSegunda = ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA />
<#assign fechaEvaluacion = ETI_CONVOCATORIA_REUNION_FECHA_EVALUACION />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>En nombre del/de la Sr/a. Secretario/a del/de la ${ETI_COMITE_NOMBRE_INVESTIGACION}, le informo de que, como miembro del citado / de la citada ${ETI_COMITE}, se le convoca a la reuni&oacute;n que se celebrar&aacute; por videoconferencia el pr&oacute;ximo ${fechaEvaluacion?datetime("dd/MM/yyyy")?string("EEEE")?capitalize}, ${fechaEvaluacion?datetime("dd/MM/yyyy")?string("dd")} de ${fechaEvaluacion?datetime("dd/MM/yyyy")?string("MMMM")?capitalize}, a las ${ETI_CONVOCATORIA_REUNION_HORA_INICIO}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO} horas en primera convocatoria<#if horaSegunda!="null"> y a las ${ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO_SEGUNDA} horas en segunda convocatoria</#if>, con el siguiente orden del d&iacute;a:</p>
    <p>${ETI_CONVOCATORIA_REUNION_ORDEN_DEL_DIA}</p>
    <p>Unirse a la reuni&oacute;n Zoom.</p>
    <p>NOTA: La videoconferencia se realizar&aacute; a trav&eacute;s de la aplicaci&oacute;n ZOOM.</p>
    <a href="https://umurcia.zoom.us/j/00000000000" target="_blank">https://umurcia.zoom.us/j/00000000000</a>
    <p>Por favor, se ruega confirme su asistencia.</p>
    <p>Un saludo.</p>
  </body>
</html>', '<#assign horaSegunda = ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA />
<#assign fechaEvaluacion = ETI_CONVOCATORIA_REUNION_FECHA_EVALUACION />

En nombre del/de la Sr/a. Secretario/a del/de la ${ETI_COMITE_NOMBRE_INVESTIGACION}, le informo de que, como miembro del citado / de la citada ${ETI_COMITE}, se le convoca a la reunión que se celebrará por videoconferencia el próximo ${fechaEvaluacion?datetime("dd/MM/yyyy")?string("EEEE")?capitalize}, ${fechaEvaluacion?datetime("dd/MM/yyyy")?string("dd")} de ${fechaEvaluacion?datetime("dd/MM/yyyy")?string("MMMM")?capitalize}, a las ${ETI_CONVOCATORIA_REUNION_HORA_INICIO}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO} horas en primera convocatoria<#if horaSegunda!="null"> y a las ${ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO_SEGUNDA} horas en segunda convocatoria</#if>, con el siguiente orden del día:
${ETI_CONVOCATORIA_REUNION_ORDEN_DEL_DIA}
Unirse a la reunión Zoom.
NOTA: La videoconferencia se realizará a través de la aplicación ZOOM.
https://umurcia.zoom.us/j/00000000000
Por favor, se ruega confirme su asistencia.
Un saludo.');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(20, 'master', '2022-08-04 10:29:23.751', 'master', '2022-08-04 10:29:23.751', 'Contenido del aviso del vencimiento del período de pago al socio colaborador (texto + HTML)', 'CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_CONTENT', '<#assign data = CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DATA:
  { 
    "titulo": "Proyecto 1",
    "fechaPrevistaPago": "2022-01-01T00:00:00Z",
    "nombreEntidadColaboradora": "nombre"
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Con fecha <b>${data.fechaPrevistaPago?datetime.iso?string.medium}</b> se alcanzará la fecha prevista del pago al socio colaborador de la Universidad <b>${data.nombreEntidadColaboradora}</b> 
      en el proyecto <b>${data.titulo}</b>  y aún no se ha registrado la fecha de realización de dicho pago.</p>

  </body>
</html>


', '<#assign data = CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DATA:
  { 
    "titulo": "Proyecto 1",
    "fechaPrevistaPago": "2022-01-01T00:00:00Z",
    "nombreEntidadColaboradora": "nombre"
  }
-->

Con fecha ${data.fechaPrevistaPago?datetime.iso?string.medium} se alcanzará la fecha prevista del pago al socio colaborador de la Universidad ${data.nombreEntidadColaboradora} 
en el proyecto ${data.titulo}  y aún no se ha registrado la fecha de realización de dicho pago.');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(21, 'master', '2022-08-04 10:29:23.751', 'master', '2022-08-04 10:29:23.751', 'Contenido del aviso del inicio del período de entrega de justificación al socio colaborador (texto + HTML)', 'CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_CONTENT', '<#assign data = CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_DATA:
  { 
    "titulo": "Proyecto 1",
    "nombreEntidad": "Entidad Colaboradora",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Con fecha <b>${data.fechaInicio?datetime.iso?string.medium}</b> se iniciará el periodo en el que el socio <b>${data.nombreEntidad}</b>, que colabora en el 
proyecto ${data.titulo}, deberá remitir la documentación de justificación asociada al periodo <b>${data.numPeriodo}</b>.</p>
  </body>
</html>


', '<#assign data = CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_DATA:
  { 
    "titulo": "Proyecto 1",
    "nombreEntidad": "Entidad Colaboradora",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2
  }
-->
Con fecha ${data.fechaInicio?datetime.iso?string.medium} se iniciará el periodo en el que el socio ${data.nombreEntidad}, que colabora en el 
proyecto ${data.titulo}, deberá remitir la documentación de justificación asociada al periodo ${data.numPeriodo}.
');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(22, 'master', '2022-08-04 10:29:23.751', 'master', '2022-08-04 10:29:23.751', 'Contenido del aviso del vencimiento del período de entrega de justificación al socio colaborador (texto + HTML)', 'CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_CONTENT', '<#assign data = CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA:
  { 
    "titulo": "Proyecto 1",
    "nombreEntidad": "Entidad Colaboradora",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Con fecha <b>${data.fechaFin?datetime.iso?string.medium}</b> finaliza el periodo en el que  <b>${data.nombreEntidad}</b>, que 
colabora en el proyecto <b>${data.titulo}</b>, debería haber remitido la documentación de justificación asociada al periodo <b>${data.numPeriodo}</b> y aún 
no se ha registrado la recepción de dicha documentación.</p>
  </body>
</html>


', '<#assign data = CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA:
  { 
    "titulo": "Proyecto 1",
    "nombreEntidad": "Entidad Colaboradora",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2
  }
-->
Con fecha ${data.fechaFin?datetime.iso?string.medium} finaliza el periodo en el que  ${data.nombreEntidad}, que 
colabora en el proyecto ${data.titulo}, debería haber remitido la documentación de justificación asociada al periodo ${data.numPeriodo} y aún 
no se ha registrado la recepción de dicha documentación.
');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(23, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de la validación de una factura con estado validada', 'CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA_CONTENT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "motivoRechazo": "motivo rechazo"
  } 
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>El/La investigador/a <b>${data.nombreApellidosValidador}</b>, responsable del contrato titulado <b>${data.tituloProyecto}</b> <br/>
    asociado al/los proyectos con código/s 
    <b><span>
    <#list data.codigosSge?chunk(4) as row>
      <#list row as cell>${cell} </#list>
    </#list>
    </span></b>, ha dado el visto bueno para la emisión de la factura nº <b>${data.numPrevision}</b>.</p>

  </body>
</html>', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSGE": ["00001", "000002"],
    "numPrevision": 2,
    "motivoRechazo": "motivo rechazo"
  } 
-->
El/La investigador/a ${data.nombreApellidosValidador}, responsable del contrato titulado ${data.tituloProyecto}
asociado al/los proyectos con código/s 
<#list data.codigosSge?chunk(4) as row>
  <#list row as cell>${cell} </#list>
</#list>, ha dado el visto bueno para la emisión de la factura nº ${data.numPrevision}.');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(24, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de la validación de una factura con estado rechazada', 'CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA_CONTENT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "motivoRechazo": "motivo rechazo"
  } 
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>El/La investigador/a <b>${data.nombreApellidosValidador}</b>, responsable del contrato titulado <b>${data.tituloProyecto}</b> <br/>
    asociado al/los proyectos con código/s 
    <b><span>
    <#list data.codigosSge?chunk(4) as row>
      <#list row as cell>${cell} </#list>
    </#list>
    </span></b>, no ha dado su conformidad para la emisión de la factura nº <b>${data.numPrevision}</b> por el siguiente motivo: <b>${data.motivoRechazo}</b>.</p>

  </body>
</html>
', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "motivoRechazo": "motivo rechazo"
  } 
-->
El/La investigador/a ${data.nombreApellidosValidador}, responsable del contrato titulado ${data.tituloProyecto} asociado al/los proyectos 
con código/s <#list data.codigosSge?chunk(4) as row><#list row as cell>${cell} </#list></#list>, no ha dado su conformidad para la emisión de la factura nº ${data.numPrevision} por el siguiente motivo: ${data.motivoRechazo}.');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(25, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de notificación de una factura sin requisitos', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO_CONTENT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a Profesor/a: <b>${data.apellidosDestinatario}</b></p>
    <p>Una vez firmado el contrato con la/s empresa/s <b><#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list></b> titulado <b>${data.tituloProyecto}</b> asociado al/los proyectos con código/s <b><#list data.codigosSge as codigo>${codigo}<#sep>, </#list></b>, se tiene que emitir la factura. Para ello necesitamos que nos confirme que los trabajos han finalizado.</p>
    <p>En relación a los trabajos que ha realizado en el marco de este contrato, envíenos por favor un correo electrónico o informe de conclusión sobre los servicios que ha prestado a la/s empresa para conocer su opinión, grado de ejecución y cumplimiento de los trabajos finalizados.</p>
    <p>En espera de su respuesta reciba un cordial saludo, OTRI.</p>
  </body>
</html>', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
Estimado/a Profesor/a: ${data.apellidosDestinatario}

Una vez firmado el contrato con la/s empresa/s <#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list> titulado ${data.tituloProyecto} asociado al/los proyectos con código/s <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>, se tiene que emitir la factura. Para ello necesitamos que nos confirme que los trabajos han finalizado.

En relación a los trabajos que ha realizado en el marco de este contrato, envíenos por favor un correo electrónico o informe de conclusión sobre los servicios que ha prestado a la/s empresa para conocer su opinión, grado de ejecución y cumplimiento de los trabajos finalizados.

En espera de su respuesta reciba un cordial saludo, OTRI.');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(26, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de notificación de una factura', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_CONTENT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURCSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a Profesor/a: <b>${data.apellidosDestinatario}</b></p>
    <p>Una vez firmado el contrato con la/s empresa/s <b><#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list></b> titulado <b>${data.tituloProyecto}</b> asociado al/los proyectos con código/s <b><#list data.codigosSge as codigo>${codigo}<#sep>, </#list></b>, se tiene que emitir la factura, que está condicionada a la entrega del/de la <b>${data.tipoFacturacion}</b> correspondiente. Para ello necesitamos que nos confirme que los trabajos han finalizado.</p>
    <p>En relación a los trabajos que ha realizado en el marco de este contrato, envíenos por favor un correo electrónico o informe de conclusión sobre los servicios que ha prestado a la/s empresa/s para conocer su opinión, grado de ejecución y cumplimiento de los trabajos finalizados.</p>
    <p>En espera de su respuesta reciba un cordial saludo, OTRI.</p>
  </body>
</html>', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
Estimado/a Profesor/a: ${data.apellidosDestinatario}

Una vez firmado el contrato con la/s empresa/s <#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list> titulado ${data.tituloProyecto} asociado al/los proyectos con código/s <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>, se tiene que emitir la factura, que está condicionada a la entrega del/de la ${data.tipoFacturacion} correspondiente. Para ello necesitamos que nos confirme que los trabajos han finalizado.

En relación a los trabajos que ha realizado en el marco de este contrato, envíenos por favor un correo electrónico o informe de conclusión sobre los servicios que ha prestado a la/s empresa/s para conocer su opinión, grado de ejecución y cumplimiento de los trabajos finalizados.

En espera de su respuesta reciba un cordial saludo, OTRI.');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(27, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de notificación de una factura', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST_CONTENT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a Profesor/a: <b>${data.apellidosDestinatario}</b></p>
    <p>Una vez firmado el contrato con <b><#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list></b> titulado <b>${data.tituloProyecto}</b> asociado al/los proyectos con código/s <b><#list data.codigosSge as codigo>${codigo}<#sep>, </#list></b>, necesitamos que nos confirme que la factura nº <b>${data.numPrevision}</b> se puede emitir.</p>
    <p>En espera de su respuesta reciba un cordial saludo, OTRI.</p>
  </body>
</html>', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
Estimado/a Profesor/a: ${data.apellidosDestinatario}

Una vez firmado el contrato con <#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list> titulado ${data.tituloProyecto} asociado al/los proyectos con código/s <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>, necesitamos que nos confirme que la factura nº ${data.numPrevision} se puede emitir.

En espera de su respuesta reciba un cordial saludo, OTRI.');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(36, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de comunicados de aviso de evaluación modificada', 'ETI_COM_EVA_MODIFICADA_CONTENT', '<#assign data = ETI_COM_EVA_MODIFICADA_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a miembro del/de la ${data.nombreInvestigacion},</p>
    <p>le informo que para la solicitud con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria} se han enviado las modificaciones solicitadas y se encuentra a la espera de su revisión.</p>
    <p>Reciba un cordial saludo.</p>
    <p>Firma Secretaría ${data.nombreInvestigacion}</p>
  </body>
</html>', '<#assign data = ETI_COM_EVA_MODIFICADA_DATA?eval_json />
Estimado/a miembro del/de la ${data.nombreInvestigacion}
    
le informo que para la solicitud con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria} se han enviado las modificaciones solicitadas y se encuentra a la espera de su revisión.
    
Reciba un cordial saludo.

Firma Secretaría ${data.nombreInvestigacion}');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(31, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de notificación de una factura', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_CONTENT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas",
    "prorroga": true
  } 
-->

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a Profesor/a: <b>${data.apellidosDestinatario}</b></p>
    <p>Siguiendo el calendario de facturación del contrato con la/s empresa/s <b><#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list></b> titulado <b>${data.tituloProyecto}</b> asociado al/los proyectos con código/s <b><#list data.codigosSge as codigo>${codigo}<#sep>, </#list></b>, se tiene que emitir la ÚLTIMA factura <#if data.prorroga>de la <b>PRORROGA</b></#if>. Para ello es necesario que nos indique si ha hecho entrega del/de la <b>${data.tipoFacturacion}</b> correspondiente.</p>
    <p>En relación a los trabajos que ha realizado en el marco de este contrato, es aconsejable nos envíe copia de los informes finales entregados a la/s empresa/s, objeto del contrato, para conocer del desarrollo, ejecución y cumplimiento de los trabajos.</p>
    <p>En espera de su respuesta reciba un cordial saludo, OTRI.</p>
  </body>
</html>', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
Estimado/a Profesor/a: ${data.apellidosDestinatario}

Siguiendo el calendario de facturación del contrato con la/s empresa/s <#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list> titulado ${data.tituloProyecto} asociado al/los proyectos con código/s <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>, se tiene que emitir la ÚLTIMA factura <#if data.prorroga>de la PRORROGA</#if>. Para ello es necesario que nos indique si ha hecho entrega del/de la ${data.tipoFacturacion} correspondiente.

En relación a los trabajos que ha realizado en el marco de este contrato, es aconsejable nos envíe copia de los informes finales entregados a la/s empresa/s, objeto del contrato, para conocer del desarrollo, ejecución y cumplimiento de los trabajos.

En espera de su respuesta reciba un cordial saludo, OTRI.
');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(29, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de notificación de una factura', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_CONTENT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas",
    "prorroga": true
  } 
-->

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a Profesor/a: <b>${data.apellidosDestinatario}</b></p>
    <p>Siguiendo el calendario de facturación del contrato con la/s empresa/s <b><#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list></b> titulado <b>${data.tituloProyecto}</b> asociado al/los proyectos con código/s <b><#list data.codigosSge as codigo>${codigo}<#sep>, </#list></b>, está prevista la emisión de la factura nº <b>${data.numPrevision}</b> del/de la <b>${data.tipoFacturacion}</b> correspondiente. Es necesario que nos indique si ha hecho vd entrega del mismo para proceder a la emisión de la factura.</p>
    <p>En espera de su respuesta reciba un cordial saludo, OTRI.</p>
  </body>
</html>', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
Estimado/a Profesor/a: ${data.apellidosDestinatario}

Siguiendo el calendario de facturación del contrato con la/s empresa/s <#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list> titulado ${data.tituloProyecto} asociado al/los proyectos con código/s <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>, está prevista la emisión de la factura nº ${data.numPrevision} del/de la ${data.tipoFacturacion} correspondiente. Es necesario que nos indique si ha hecho vd entrega del mismo para proceder a la emisión de la factura.

En espera de su respuesta reciba un cordial saludo, OTRI.
');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(32, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envio de evaluaciones al finalizar acta', 'ETI_COM_ACTA_SIN_REV_MINIMA_CONTENT', '<#assign data = ETI_COM_ACTA_SIN_REV_MINIMA_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <#if data.generoComite == "M">
    <p>una vez evaluada su solicitud para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por el ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación <a href="${data.enlaceAplicacion}" target="_blank">${data.enlaceAplicacion}</a>.</p>
    <#else>
    <p>una vez evaluada su solicitud para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por la ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación <a href="${data.enlaceAplicacion}" target="_blank">${data.enlaceAplicacion}</a>.</p>
    </#if>
    <p>Reciba un cordial saludo.</p>
    <p>Firma Secretaría ${data.nombreInvestigacion}</p>
  </body>
</html>', '<#assign data = ETI_COM_ACTA_SIN_REV_MINIMA_DATA?eval_json />
Estimado/a investigador/a,
<#if data.generoComite == "M">
una vez evaluada su solicitud para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por el ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación ${data.enlaceAplicacion}.
<#else>
una vez evaluada su solicitud para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por la ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación ${data.enlaceAplicacion}.
</#if>

Reciba un cordial saludo.

Firma Secretaría ${data.nombreInvestigacion}');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(33, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envio de evaluaciones al finalizar acta con revisión mínima', 'ETI_COM_DICT_EVA_REV_MINIMA_CONTENT', '<#assign data = ETI_COM_DICT_EVA_REV_MINIMA_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <#if data.generoComite == "M">
    <p>una vez evaluadas las revisiones mínimas solicitadas para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por el ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación <a href="${data.enlaceAplicacion}" target="_blank">${data.enlaceAplicacion}</a>.</p>
    <#else>
    <p>una vez evaluadas las revisiones mínimas solicitadas para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por la ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación <a href="${data.enlaceAplicacion}" target="_blank">${data.enlaceAplicacion}</a>.</p>
    </#if>
    <p>Reciba un cordial saludo.</p>
    <p>Firma Secretaría ${data.nombreInvestigacion}</p>
  </body>
</html>', '<#assign data = ETI_COM_DICT_EVA_REV_MINIMA_DATA?eval_json />
Estimado/a investigador/a,
<#if data.generoComite == "M">
una vez evaluadas las revisiones mínimas solicitadas para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por el ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación ${data.enlaceAplicacion}.
<#else>
una vez evaluadas las revisiones mínimas solicitadas para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por la ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación ${data.enlaceAplicacion}.
</#if>

Reciba un cordial saludo.

Firma Secretaría ${data.nombreInvestigacion}');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(34, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envio de evaluaciones de seguimiento con revisión mínima', 'ETI_COM_DICT_EVA_SEG_REV_MINIMA_CONTENT', '<#assign data = ETI_COM_DICT_EVA_SEG_REV_MINIMA_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <#if data.generoComite == "M">
    <p>una vez evaluadas las revisiones mínimas solicitadas para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por el ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación <a href="${data.enlaceAplicacion}" target="_blank">${data.enlaceAplicacion}</a>.</p>
    <#else>
    <p>una vez evaluadas las revisiones mínimas solicitadas para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por la ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación <a href="${data.enlaceAplicacion}" target="_blank">${data.enlaceAplicacion}</a>.</p>
    </#if>
    <p>Reciba un cordial saludo.</p>
    <p>Firma Secretaría ${data.nombreInvestigacion}</p>
  </body>
</html>', '<#assign data = ETI_COM_DICT_EVA_SEG_REV_MINIMA_DATA?eval_json />
Estimado/a investigador/a,
<#if data.generoComite == "M">
una vez evaluadas las revisiones mínimas solicitadas para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por el ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación ${data.enlaceAplicacion}.
<#else>
una vez evaluadas las revisiones mínimas solicitadas para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por la ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación ${data.enlaceAplicacion}.
</#if>

Reciba un cordial saludo.

Firma Secretaría ${data.nombreInvestigacion}');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(35, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de comunicados de aviso de evaluación retrospectiva pendiente', 'ETI_COM_INF_RETRO_PENDIENTE_CONTENT', '<#assign data = ETI_COM_INF_RETRO_PENDIENTE_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le recordamos que, tal y como se refleja en la autorización de la CARM para la realización del/de la <b>${data.tipoActividad}</b> con título <b>${data.tituloSolicitudEvaluacion}</b> asociado/a a la memoria con referencia <b>${data.referenciaMemoria}</b> será necesario que solicite la evaluación retrospectiva del mismo, a través del formulario que puede encontrar en la web <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
    <p>Reciba un cordial saludo.</p>
    <p>Firma Secretaría <b>${data.nombreInvestigacion}</b></p>
  </body>
</html>', '<#assign data = ETI_COM_INF_RETRO_PENDIENTE_DATA?eval_json />
Estimado/a investigador/a,

le recordamos que, tal y como se refleja en la autorización de la CARM para la realización del/de la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria} será necesario que solicite la evaluación retrospectiva del mismo, a través del formulario que puede encontrar en la web ${data.enlaceAplicacion}.

Reciba un cordial saludo.

Firma Secretaría ${data.nombreInvestigacion}');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(30, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de notificación de una factura', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO_CONTENT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas",
    "prorroga": true
  } 
-->

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a Profesor/a: <b>${data.apellidosDestinatario}</b></p>
    <p>Siguiendo el calendario de facturación del contrato con la/s empresa/s <b><#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list></b> titulado <b>${data.tituloProyecto}</b> asociado al/los proyectos con código/s <b><#list data.codigosSge as codigo>${codigo}<#sep>, </#list></b>, se tiene que emitir la ÚLTIMA factura <#if data.prorroga> de la <b>PRORROGA</b></#if>. Para ello es necesario que nos indique si ha finalizado el trabajo.</p>
    <p>En relación a los trabajos que ha realizado en el marco de este contrato, envíenos por favor un correo electrónico o informe de conclusión sobre los servicios que ha prestado a la/s empresa/s para conocer su opinión, grado de ejecución y cumplimiento de los trabajos finalizados.</p>
    <p>En espera de su respuesta reciba un cordial saludo, OTRI.</p>
  </body>
</html>', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
Estimado/a Profesor/a: ${data.apellidosDestinatario}

Siguiendo el calendario de facturación del contrato con la/s empresa/s <#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list> titulado ${data.tituloProyecto} asociado al/los proyectos con código/s <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>, se tiene que emitir la ÚLTIMA factura <#if data.prorroga>de la PRORROGA</#if>. Para ello es necesario que nos indique si ha finalizado el trabajo.

En relación a los trabajos que ha realizado en el marco de este contrato, envíenos por favor un correo electrónico o informe de conclusión sobre los servicios que ha prestado a la/s empresa/s para conocer su opinión, grado de ejecución y cumplimiento de los trabajos finalizados.

En espera de su respuesta reciba un cordial saludo, OTRI.
');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(28, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de notificación de una factura', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO_CONTENT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas",
    "prorroga": true
  } 
-->

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a Profesor/a: <b>${data.apellidosDestinatario}</b></p>
    <p>Siguiendo el calendario de facturación del contrato con la/s empresa/s <b><#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list></b> titulado <b>${data.tituloProyecto}</b> asociado al/los proyectos con código/s <b><#list data.codigosSge as codigo>${codigo}<#sep>, </#list></b>, está prevista la emisión de la factura nº ${data.numPrevision} <#if data.prorroga> de la <b>PRORROGA</b></#if>. Es necesario para poder emitirla que nos confirme que los trabajos progresan adecuadamente y por lo tanto se puede enviar la factura a la/s empresa/s.</p>
    <p>En espera de su respuesta reciba un cordial saludo, OTRI.</p>
  </body>
</html>', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
Estimado/a Profesor/a: ${data.apellidosDestinatario}

Siguiendo el calendario de facturación del contrato con la/s empresa/s <#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list> titulado ${data.tituloProyecto} asociado al/los proyectos con código/s <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>, está prevista la emisión de la factura nº ${data.numPrevision} <#if data.prorroga> de la PRORROGA</#if>. Es necesario para poder emitirla que nos confirme que los trabajos progresan adecuadamente y por lo tanto se puede enviar la factura a la/s empresa/s.

En espera de su respuesta reciba un cordial saludo, OTRI.
');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(15, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el cambio de estado de solicitud a denegada', 'CSP_COM_SOL_CAMB_EST_DEN_CONTENT', '<#assign data = CSP_COM_SOL_CAMB_EST_DEN_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le informamos que <#if (data.fechaConcesionConvocatoria)??>con fecha ${data.fechaConcesionConvocatoria?datetime.iso?string("dd/MM/yyyy")} </#if>se ha publicado la “Resolución de Concesión” de la convocatoria de ${data.tituloConvocatoria} en la que su solicitud aparece denegada.</p>
    <#if data.enlaces?has_content>
    <p>Más información disponible en los siguientes enlaces:</p>
    <ul>
      <#list data.enlaces as enlace>
          <ul>
            <#assign url>${enlace.url}</#assign>
            <#if enlace.tipoEnlace?? && enlace.descripcion?? && enlace.url?? >
            <li><#outputformat "HTML">${enlace.tipoEnlace?esc}</#outputformat>: <#outputformat "HTML">${enlace.descripcion?esc}</#outputformat> (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.descripcion?? && enlace.url?? >          
            <li><#outputformat "HTML">${enlace.descripcion?esc}</#outputformat> (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.tipoEnlace?? && enlace.url?? >          
            <li><#outputformat "HTML">${enlace.tipoEnlace?esc}</#outputformat>: (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.url??>
            <li>Link a <a href="${url}">${url}</a></li>
            </#if>          
          </ul>
        </li>
      </#list>
    </ul>
    </#if> 
    <p>Reciba un cordial saludo.</p>
    <p>Sección de Recursos Humanos de la Investigación y Plan Propio</p>
    <p>Área de Investigación</p>
  </body>
</html>', '<#assign data = CSP_COM_SOL_CAMB_EST_DEN_DATA?eval_json />
Estimado/a investigador/a,

le informamos que <#if (data.fechaConcesionConvocatoria)??>con fecha ${data.fechaConcesionConvocatoria?datetime.iso?string("dd/MM/yyyy")} </#if>se ha publicado la “Resolución de Concesión” de la convocatoria de ${data.tituloConvocatoria} en la que su solicitud aparece denegada.

<#if data.enlaces?has_content>
Más información disponible en los siguientes enlaces:

<#list data.enlaces as enlace>
  <#if enlace.tipoEnlace?? && enlace.descripcion?? && enlace.url?? >
* ${enlace.tipoEnlace}: ${enlace.descripcion} (link a ${enlace.url})
  <#elseif enlace.descripcion?? && enlace.url??>
* ${enlace.descripcion} (link a ${enlace.url})
  <#elseif enlace.tipoEnlace?? && enlace.url??>
* ${enlace.tipoEnlace} (link a ${enlace.url})
  <#elseif enlace.url??>
* link a ${enlace.url}
  </#if>
</#list>
</#if> 

Reciba un cordial saludo.

Sección de Recursos Humanos de la Investigación y Plan Propio
Área de Investigación');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(43, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de comunicados de Modificación de autoriazación para la participación en un proyecto externo', 'CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_CONTENT', '<#assign data = CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Con fecha <b>${data.fecha?datetime.iso?string(''dd/MM/yyyy'')}</b>, ha sido registrada en nuestra base de datos la solicitud de autorización de participación en el proyecto externo <b>${data.tituloProyecto}</b> por parte de <b>${data.nombreSolicitante}</b>.</p>
  </body>
</html>', '<#assign data = CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
Con fecha ${data.fecha?datetime.iso?string(''dd/MM/yyyy'')}, ha sido registrada en nuestra base de datos la solicitud de autorización de participación en el proyecto externo ${data.tituloProyecto} por parte de ${data.nombreSolicitante}');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(44, NULL, NULL, NULL, NULL, 'Plantilla de contenido para Proyectos-Hitos', 'CSP_PROYECTO_HITO_CONTENT', '<#ftl output_format="HTML" auto_esc=false>
<#assign date = CSP_HITO_FECHA?datetime.iso>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Con fecha <b>${date?string("dd/MM/yyyy")}</b> a las <b>${date?string("HH:mm")}</b> se alcanzar&aacute; el hito &quot;${CSP_HITO_TIPO?esc}&quot; del proyecto &quot;${CSP_PROYECTO_TITULO?esc}&quot; <#if CSP_CONVOCATORIA_TITULO?has_content>de la convocatoria &quot;${CSP_CONVOCATORIA_TITULO?esc}&quot;</#if>.</p>
    <#if CSP_HITO_OBSERVACIONES?has_content>
    <p>&quot;En el hito se han indicado las siguientes observaciones: &quot;
      ${CSP_HITO_OBSERVACIONES?esc}
    </p>
    </#if>
  </body>
</html>', '<#assign date = CSP_HITO_FECHA?datetime.iso>
Con fecha ${date?string("dd/MM/yyyy")} a las ${date?string("HH:mm")} se alcanzará el hito "${CSP_HITO_TIPO}" del proyecto "${CSP_PROYECTO_TITULO}"<#if CSP_CONVOCATORIA_TITULO?has_content> de la convocatoria "${CSP_CONVOCATORIA_TITULO}"</#if>.
<#if CSP_HITO_OBSERVACIONES?has_content>

"En el hito se han indicado las siguientes observaciones: "
${CSP_HITO_OBSERVACIONES}
</#if>');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(13, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el cambio de estado de solicitud a excluida definitiva', 'CSP_COM_SOL_CAMB_EST_EXCL_DEF_CONTENT', '<#assign data = CSP_COM_SOL_CAMB_EST_EXCL_DEF_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le informamos que <#if (data.fechaConcesionConvocatoria)??>con fecha ${data.fechaConcesionConvocatoria?datetime.iso?string("dd/MM/yyyy")} </#if>se ha publicado la “Resolución con carácter definitivo de Admitidos y Excluidos” de la convocatoria de ${data.tituloConvocatoria} en la que su solicitud aparece excluida.</p>
    <#if data.enlaces?has_content>
    <p>Más información disponible en los siguientes enlaces:</p>
    <ul>
      <#list data.enlaces as enlace>
          <ul>
            <#assign url>${enlace.url}</#assign>
            <#if enlace.tipoEnlace?? && enlace.descripcion?? && enlace.url?? >
            <li><#outputformat "HTML">${enlace.tipoEnlace?esc}</#outputformat>: <#outputformat "HTML">${enlace.descripcion?esc}</#outputformat> (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.descripcion?? && enlace.url?? >          
            <li><#outputformat "HTML">${enlace.descripcion?esc}</#outputformat> (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.tipoEnlace?? && enlace.url?? >          
            <li><#outputformat "HTML">${enlace.tipoEnlace?esc}</#outputformat>: (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.url??>
            <li>Link a <a href="${url}">${url}</a></li>
            </#if>          
          </ul>
        </li>
      </#list>
    </ul>
    </#if>  
    <p>Reciba un cordial saludo.</p>
    <p>Sección de Recursos Humanos de la Investigación y Plan Propio</p>
    <p>Área de Investigación</p>
  </body>
</html>', '<#assign data = CSP_COM_SOL_CAMB_EST_EXCL_DEF_DATA?eval_json />
Estimado/a investigador/a,

le informamos que <#if (data.fechaConcesionConvocatoria)??>con fecha ${data.fechaConcesionConvocatoria?datetime.iso?string("dd/MM/yyyy")} </#if>se ha publicado la “Resolución con carácter definitivo de Admitidos y Excluidos” de la convocatoria de ${data.tituloConvocatoria} en la que su solicitud aparece excluida.

<#if data.enlaces?has_content>
Más información disponible en los siguientes enlaces:

<#list data.enlaces as enlace>
  <#if enlace.tipoEnlace?? && enlace.descripcion?? && enlace.url?? >
* ${enlace.tipoEnlace}: ${enlace.descripcion} (link a ${enlace.url})
  <#elseif enlace.descripcion?? && enlace.url??>
* ${enlace.descripcion} (link a ${enlace.url})
  <#elseif enlace.tipoEnlace?? && enlace.url??>
* ${enlace.tipoEnlace} (link a ${enlace.url})
  <#elseif enlace.url??>
* link a ${enlace.url}
  </#if>
</#list>
</#if>

Reciba un cordial saludo.

Sección de Recursos Humanos de la Investigación y Plan Propio
Área de Investigación');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(16, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el cambio de estado de solicitud a concedida provisional', 'CSP_COM_SOL_CAMB_EST_CONC_PROV_CONTENT', '<#assign data = CSP_COM_SOL_CAMB_EST_CONC_PROV_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le informamos que <#if data.fechaProvisionalConvocatoria??>con fecha ${data.fechaProvisionalConvocatoria?datetime.iso?string("dd/MM/yyyy")} </#if>se ha publicado la “Resolución Provisional” de la convocatoria de ${data.tituloConvocatoria} en la que su solicitud aparece propuesta para financiación.</p>
    <#if data.enlaces?has_content>
    <p>Más información disponible en los siguientes enlaces:</p>
    <ul>
      <#list data.enlaces as enlace>
          <ul>
            <#assign url>${enlace.url}</#assign>
            <#if enlace.tipoEnlace?? && enlace.descripcion?? && enlace.url?? >
            <li><#outputformat "HTML">${enlace.tipoEnlace?esc}</#outputformat>: <#outputformat "HTML">${enlace.descripcion?esc}</#outputformat> (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.descripcion?? && enlace.url?? >          
            <li><#outputformat "HTML">${enlace.descripcion?esc}</#outputformat> (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.tipoEnlace?? && enlace.url?? >          
            <li><#outputformat "HTML">${enlace.tipoEnlace?esc}</#outputformat>: (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.url??>
            <li>Link a <a href="${url}">${url}</a></li>
            </#if>          
          </ul>
        </li>
      </#list>
    </ul>
    </#if>  
    <p>Reciba un cordial saludo.</p>
    <p>Sección de Recursos Humanos de la Investigación y Plan Propio</p>
    <p>Área de Investigación</p>
  </body>
</html>', '<#assign data = CSP_COM_SOL_CAMB_EST_CONC_PROV_DATA?eval_json />
Estimado/a investigador/a,

le informamos que <#if data.fechaProvisionalConvocatoria??>con fecha ${data.fechaProvisionalConvocatoria?datetime.iso?string("dd/MM/yyyy")} </#if>se ha publicado la “Resolución Provisional” de la convocatoria de ${data.tituloConvocatoria} en la que su solicitud aparece propuesta para financiación.

<#if data.enlaces?has_content>
Más información disponible en los siguientes enlaces:

<#list data.enlaces as enlace>
  <#if enlace.tipoEnlace?? && enlace.descripcion?? && enlace.url?? >
* ${enlace.tipoEnlace}: ${enlace.descripcion} (link a ${enlace.url})
  <#elseif enlace.descripcion?? && enlace.url??>
* ${enlace.descripcion} (link a ${enlace.url})
  <#elseif enlace.tipoEnlace?? && enlace.url??>
* ${enlace.tipoEnlace} (link a ${enlace.url})
  <#elseif enlace.url??>
* link a ${enlace.url}
  </#if>
</#list>
</#if>

Reciba un cordial saludo.

Sección de Recursos Humanos de la Investigación y Plan Propio
Área de Investigación');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(45, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de comunicados de recepción de notificacion cvn de un proyecto externo', 'CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO_CONTENT', '<#assign data = CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Con fecha ${data.fechaCreacion?datetime.iso?string(''dd/MM/yyyy'')}, ha sido registrada en nuestra base de datos la notificación de creación del proyecto ${data.tituloProyecto} en el CVN de ${data.nombreApellidosCreador}.</p>
  </body>
</html>', '<#assign data = CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO_DATA?eval_json />
Con fecha ${data.fechaCreacion?datetime.iso?string(''dd/MM/yyyy'')}, ha sido registrada en nuestra base de datos la notificación de creación del proyecto ${data.tituloProyecto} en el CVN de ${data.nombreApellidosCreador}.
');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(46, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de comunicados de seguimiento final pendiente', 'ETI_COM_INF_SEG_FIN_CONTENT', '<#assign data = ETI_COM_INF_SEG_FIN_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le recordamos que una vez que ha pasado un año desde la fecha de fin de realización del/de la <b>${data.tipoActividad}</b> con título <b>${data.tituloSolicitudEvaluacion}</b> asociado/a a la memoria con referencia <b>${data.referenciaMemoria}</b>, será necesario que realice el informe de seguimiento final del mismo, a través de la aplicación <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
    <p>Reciba un cordial saludo.</p>
    <p>Firma Secretaría <b>${data.nombreInvestigacion}</b></p>
  </body>
</html>', '<#assign data = ETI_COM_INF_SEG_FIN_DATA?eval_json />
Estimado/a investigador/a,

le recordamos que una vez que ha pasado un año desde la fecha de fin de realización del/de la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, será necesario que realice el informe de seguimiento final del mismo, a través de la aplicación ${data.enlaceAplicacion}.

Reciba un cordial saludo.

Firma Secretaría ${data.nombreInvestigacion}');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(17, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el cambio de estado de solicitud a concedida', 'CSP_COM_SOL_CAMB_EST_CONC_CONTENT', '<#assign data = CSP_COM_SOL_CAMB_EST_CONC_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le informamos que <#if data.fechaConcesionConvocatoria??>con fecha ${data.fechaConcesionConvocatoria?datetime.iso?string("dd/MM/yyyy")} </#if>se ha publicado la “Resolución de Concesión” de la convocatoria de ${data.tituloConvocatoria} en la que su solicitud aparece concedida.</p>
    <#if data.enlaces?has_content>
    <p>Más información disponible en los siguientes enlaces:</p>
    <ul>
      <#list data.enlaces as enlace>
          <ul>
            <#assign url>${enlace.url}</#assign>
            <#if enlace.tipoEnlace?? && enlace.descripcion?? && enlace.url?? >
            <li><#outputformat "HTML">${enlace.tipoEnlace?esc}</#outputformat>: <#outputformat "HTML">${enlace.descripcion?esc}</#outputformat> (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.descripcion?? && enlace.url?? >          
            <li><#outputformat "HTML">${enlace.descripcion?esc}</#outputformat> (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.tipoEnlace?? && enlace.url?? >          
            <li><#outputformat "HTML">${enlace.tipoEnlace?esc}</#outputformat>: (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.url??>
            <li>Link a <a href="${url}">${url}</a></li>
            </#if>          
          </ul>
        </li>
      </#list>
    </ul>
    </#if>  
    <p>En breve nos pondremos en contacto con usted para tramitar su incorporación a la Universidad de Murcia.</p>
    <p>Reciba un cordial saludo.</p>
    <p>Sección de Recursos Humanos de la Investigación y Plan Propio</p>
    <p>Área de Investigación</p>
  </body>
</html>', '<#assign data = CSP_COM_SOL_CAMB_EST_CONC_DATA?eval_json />
Estimado/a investigador/a,

le informamos que <#if data.fechaConcesionConvocatoria??>con fecha ${data.fechaConcesionConvocatoria?datetime.iso?string("dd/MM/yyyy")} </#if>se ha publicado la “Resolución de Concesión” de la convocatoria de ${data.tituloConvocatoria} en la que su solicitud aparece concedida.

<#if data.enlaces?has_content>
Más información disponible en los siguientes enlaces:

<#list data.enlaces as enlace>
  <#if enlace.tipoEnlace?? && enlace.descripcion?? && enlace.url?? >
* ${enlace.tipoEnlace}: ${enlace.descripcion} (link a ${enlace.url})
  <#elseif enlace.descripcion?? && enlace.url??>
* ${enlace.descripcion} (link a ${enlace.url})
  <#elseif enlace.tipoEnlace?? && enlace.url??>
* ${enlace.tipoEnlace} (link a ${enlace.url})
  <#elseif enlace.url??>
* link a ${enlace.url}
  </#if>
</#list>
</#if>

En breve nos pondremos en contacto con usted para tramitar su incorporación a la Universidad de Murcia.

Reciba un cordial saludo.

Sección de Recursos Humanos de la Investigación y Plan Propio
Área de Investigación');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(39, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de comunicados de seguimiento anual pendiente', 'ETI_COM_INF_SEG_ANU_CONTENT', '<#assign data = ETI_COM_INF_SEG_ANU_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le recordamos que una vez que ha pasado un año desde la fecha de obtención del dictamen favorable para el/la <b>${data.tipoActividad}</b> con título <b>${data.tituloSolicitudEvaluacion}</b> asociado/a a la memoria con referencia <b>${data.referenciaMemoria}</b>, será necesario que realice el informe de seguimiento anual del mismo, a través de la aplicación <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
    <p>Reciba un cordial saludo.</p>
    <p>Firma Secretaría <b>${data.nombreInvestigacion}</b></p>
  </body>
</html>', '<#assign data = ETI_COM_INF_SEG_ANU_DATA?eval_json />
Estimado/a investigador/a,

le recordamos que una vez que ha pasado un año desde la fecha de obtención del dictamen favorable para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, será necesario que realice el informe de seguimiento anual del mismo, a través de la aplicación ${data.enlaceAplicacion}.

Reciba un cordial saludo.

Firma Secretaría ${data.nombreInvestigacion}');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(40, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de comunicados de Cambio de visibilidad del certificado de autoriazación para la participación en un proyecto externo', 'CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_CONTENT', '<#assign data = CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>
      Estimado/a investigador/a,

      <p>le informamos que tiene disponible para su descarga la autorización de participación en el proyecto externo ${data.tituloProyectoExt}. Puede realizar la descarga desde la aplicación <a href="${data.enlaceAplicacion}" target="_blank">${data.enlaceAplicacion}</a>.</p>

      <p>Reciba un cordial saludo.</p>

      <p>Área de Investigación</p>
    </p>
  </body>
</html>', '<#assign data = CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
Estimado/a investigador/a,

le informamos que tiene disponible para su descarga la autorización de participación en el proyecto externo ${data.tituloProyectoExt}. Puede realizar la descarga desde la aplicación ${data.enlaceAplicacion}.

Reciba un cordial saludo.

Área de Investigación');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(41, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de comunicados de Cambio de estado de autoriazación para la participación en un proyecto externo', 'CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_CONTENT', '<#assign data = CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,

    <p>le informamos que con fecha ${data.fechaEstadoSolicitudPext?datetime.iso?string(''dd/MM/yyyy'')} se ha modificado su solicitud de autorización de participación en el proyecto ${data.tituloPext} al estado ${data.estadoSolicitudPext}.</p>

    <p>Reciba un cordial saludo.</p>

    <p>Área de Investigación</p>
    </p>
  </body>
</html>', ' ');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(42, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de comunicados de Cambio de estado a solicitada de una soolicitud de tipo RRHH', 'CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH_CONTENT', '<#assign data = CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>

    <p>Le informamos que con fecha ${data.fechaEstado?datetime.iso?string(''dd/MM/yyyy'')}, ${data.nombreApellidosSolicitante} ha registrado la solicitud ${data.codigoInternoSolicitud} <#if data.tituloConvocatoria?has_content>dentro de la convocatoria ${data.tituloConvocatoria}<#if data.fechaProvisionalConvocatoria??> que tiene fecha de resolución provisional ${data.fechaProvisionalConvocatoria?datetime.iso?string(''dd/MM/yyyy'')}</#if></#if> indicando que usted participará como tutor/a del trabajo asociado. Es necesario que valide la solicitud en el sistema SGI Hércules <a href="${data.enlaceAplicacionMenuValidacionTutor}" target="_blank">${data.enlaceAplicacionMenuValidacionTutor}</a>.</p>

    <p>Reciba un cordial saludo.</p>

    <p>Sección de Recursos Humanos de la Investigación y Plan Propio</p>

    <p>Área de Investigación</p>
  </body>
</html>', '<#assign data = CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH_DATA?eval_json />
Estimado/a investigador/a,

Le informamos que con fecha ${data.fechaEstado?datetime.iso?string(''dd/MM/yyyy'')}, ${data.nombreApellidosSolicitante} ha registrado la solicitud ${data.codigoInternoSolicitud} <#if data.tituloConvocatoria?has_content>dentro de la convocatoria ${data.tituloConvocatoria}<#if data.fechaProvisionalConvocatoria??> que tiene fecha de resolución provisional ${data.fechaProvisionalConvocatoria?datetime.iso?string(''dd/MM/yyyy'')}</#if></#if> indicando que usted participará como tutor/a del trabajo asociado. Es necesario que valide la solicitud en el sistema SGI Hércules ${data.enlaceAplicacionMenuValidacionTutor}.

Reciba un cordial saludo.

Sección de Recursos Humanos de la Investigación y Plan Propio

Área de Investigación');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(47, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envio de Memoria con dictamen Revisión Mínima archivada automáticamente', 'ETI_COM_DICT_MEM_REV_MINIMA_ARCH_CONTENT', '<#assign data = ETI_COM_DICT_MEM_REV_MINIMA_ARCH_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>ante la ausencia de respuesta a las correcciones solicitadas por el/la <b>${data.nombreInvestigacion}</b>, con respecto a la solicitud de evaluación del/de la <b>${data.tipoActividad}</b> con título <b>${data.tituloSolicitudEvaluacion}</b> asociado/a a la memoria con referencia <b>${data.referenciaMemoria}</b>, le informamos que la situación de dicha solicitud pasará a archivada, debiendo enviar una nueva solicitud con el fin de obtener el correspondiente informe.</p>
    <br/>   
    <p>Reciba un cordial saludo.</p>
    <p>Firma Secretaría <b>${data.nombreInvestigacion}</b></p>
  </body>
</html>', '<#assign data = ETI_COM_DICT_MEM_REV_MINIMA_ARCH_DATA?eval_json />
Estimado/a investigador/a,

ante la ausencia de respuesta a las correcciones solicitadas por el/la ${data.nombreInvestigacion}, con respecto a la solicitud de evaluación del/de la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, le informamos que la situación de dicha solicitud pasará a archivada, debiendo enviar una nueva solicitud con el fin de obtener el correspondiente informe.

Reciba un cordial saludo.

Firma Secretaría ${data.nombreInvestigacion}');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(48, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de error en el proceso de baremacion', 'PRC_COM_PROCESO_BAREMACION_ERROR_CONTENT', '<#assign data = PRC_COM_PROCESO_BAREMACION_ERROR_DATA?eval />
<#--
  Formato PRC_COM_PROCESO_BAREMACION_ERROR_DATA:
  { 
    "anio": "2022",
    "error": "texto error"
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Se ha producido un error en el proceso de baremación del <b>${data.anio}</b>. El error producido es <b>${data.error}</b>.</p>
  </body>
</html>', '<#assign data = PRC_COM_PROCESO_BAREMACION_ERROR_DATA?eval />
<#--
  Formato PRC_COM_PROCESO_BAREMACION_ERROR_DATA:
  { 
    "anio": "2022",
    "error": "texto error"
  }
-->
Se ha producido un error en el proceso de baremación del ${data.anio}. El error producido es ${data.error}.');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(49, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de fin del proceso de baremacion', 'PRC_COM_PROCESO_BAREMACION_FIN_CONTENT', '<#assign data = PRC_COM_PROCESO_BAREMACION_FIN_DATA?eval />
<#--
  Formato PRC_COM_PROCESO_BAREMACION_FIN_DATA:
  { 
    "anio": "2022"
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>El proceso de baremación del <b>${data.anio}</b> ha finalizado con éxito. Puede ir a la opción de menú "Informes" para ver su resultado.</p>
  </body>
</html>', '<#assign data = PRC_COM_PROCESO_BAREMACION_FIN_DATA?eval />
<#--
  Formato PRC_COM_PROCESO_BAREMACION_FIN_DATA:
  { 
    "anio": "2022"
  }
-->
El proceso de baremación del <b>${data.anio}</b> ha finalizado con éxito. Puede ir a la opción de menú "Informes" para ver su resultado');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(50, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío del comunicado para validar item', 'PRC_COM_VALIDAR_ITEM_CONTENT', '<#assign data = PRC_COM_VALIDAR_ITEM_DATA?eval />
<#--
  Formato PRC_COM_VALIDAR_ITEM_DATA:
  { 
    "anio": "2022",
    "error": "texto error"
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le informamos que dispone de un nuevo item de tipo <b>${data.nombreEpigrafe}</b> con título/nombre <b>${data.tituloItem}</b> y fecha <b>${data.fechaItem?datetime.iso?string("dd/MM/yyyy")}</b> que precisa de su validación para poder realizar la baremación sobre él.</p>
  </body>
</html>', '<#assign data = PRC_COM_VALIDAR_ITEM_DATA?eval />
<#--
  Formato PRC_COM_VALIDAR_ITEM_DATA:
  { 
    "anio": "2022",
    "error": "texto error"
  }
-->
Estimado/a investigador/a,

le informamos que dispone de un nuevo item de tipo ${data.nombreEpigrafe} con título/nombre ${data.tituloItem} y fecha ${data.fechaItem?datetime.iso?string("dd/MM/yyyy")} que precisa de su validación para poder realizar la baremación sobre él.');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(51, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de comunicados de memoria archivada por inactividad', 'ETI_COM_MEM_ARCHIVADA_AUT_CONTENT', '<#assign data = ETI_COM_MEM_ARCHIVADA_AUT_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a, </p>
    <p>ante la ausencia de respuesta a las modificaciones solicitadas por el/la <b>${data.nombreInvestigacion}</b>, con respecto a la solicitud de evaluación del/de la <b>${data.tipoActividad}</b> con título <b>${data.tituloSolicitudEvaluacion}</b> asociado/a a la memoria con referencia <b>${data.referenciaMemoria}</b>, le informamos que la situación de dicha solicitud pasará a archivada, debiendo enviar una nueva solicitud con el fin de obtener el correspondiente informe.</p>
    
    <p>Reciba un cordial saludo.</p>

    <p>Firma Secretaría <b>${data.nombreInvestigacion}</b></p>
  </body>
</html>', '<#assign data = ETI_COM_MEM_ARCHIVADA_AUT_DATA?eval_json />

Estimado/a investigador/a,

ante la ausencia de respuesta a las modificaciones solicitadas por el/la ${data.nombreInvestigacion}, con respecto a la solicitud de evaluación
del/de la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion}  asociado/a a la memoria con referencia ${data.referenciaMemoria}, le informamos que la situación de dicha solicitud pasará a archivada, debiendo enviar una nueva solicitud con el fin de obtener el correspondiente informe.
Reciba un cordial saludo.

Firma Secretaría ${data.nombreInvestigacion}');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(52, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de comunicados de fecha límite de procedimiento', 'PII_COM_FECHA_LIMITE_PROCEDIMIENTO_CONTENT', '<#assign data = PII_COM_FECHA_LIMITE_PROCEDIMIENTO_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Se informa que el día ${data.fechaLimite?datetime.iso?string(''dd/MM/yyyy'')} acaba el plazo para solicitar el trámite de ${data.tipoProcedimiento}, para el que se han indicado las siguientes acciones a tomar: ${data.accionATomar}.</p>
  </body>
</html>', '<#assign data = PII_COM_FECHA_LIMITE_PROCEDIMIENTO_DATA?eval_json />
Se informa que el día ${data.fechaLimite?datetime.iso?string(''dd/MM/yyyy'')} acaba el plazo para solicitar el trámite de ${data.tipoProcedimiento}, para el que se han indicado las siguientes acciones a tomar: ${data.accionATomar}.
');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(37, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envio de avisos de vencimiento de fin de fecha de prioridad de unsa solicitud de protección', 'PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_CONTENT', '<#assign data = PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DATA?eval />
<#--
  Formato PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DATA:
  { 
    "solicitudTitle": "PROTECCION 1",
    "monthsBeforeFechaFinPrioridad": 6,
    "fechaFinPrioridad": "2022-01-01T00:00:00Z"
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Se informa que queda<#if data.monthsBeforeFechaFinPrioridad != 1>n</#if> ${data.monthsBeforeFechaFinPrioridad} mes<#if data.monthsBeforeFechaFinPrioridad != 1>es</#if> y que, el día ${data.fechaFinPrioridad?datetime.iso?string(''dd/MM/yyyy'')}, finaliza el plazo de prioridad para la extensión de la invención de referencia, con título ${data.solicitudTitle}.</p>
  </body>
</html>


', '<#assign data = PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DATA?eval />
<#--
  Formato PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DATA:
  { 
    "solicitudTitle": "PROTECCION 1",
    "monthsBeforeFechaFinPrioridad": 6,
    "fechaFinPrioridad": "2022-01-01T00:00:00Z"
  }
-->
Se informa que queda<#if data.monthsBeforeFechaFinPrioridad != 1>n</#if> ${data.monthsBeforeFechaFinPrioridad} mes<#if data.monthsBeforeFechaFinPrioridad != 1>es</#if> y que, el d&iacute;a ${data.fechaFinPrioridad?datetime.iso?string(''dd/MM/yyyy'')}, finaliza el plazo de prioridad para la extensión de la invención de referencia, con título ${data.solicitudTitle}.
');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(38, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envio de avisos de vencimiento del plazo para entrar en las fases nacionales/regionales de una solicitud de protección', 'PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_CONTENT', '<#assign data = PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_DATA?eval />
<#--
  Formato PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_DATA:
  { 
    "solicitudTitle": "PROTECCION 1",
    "monthsBeforeFechaFinPrioridad": 6,
    "fechaFinPrioridad": "2022-01-01T00:00:00Z"
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Se informa que queda<#if data.monthsBeforeFechaFinPrioridad != 1>n</#if> ${data.monthsBeforeFechaFinPrioridad} mes<#if data.monthsBeforeFechaFinPrioridad != 1>es</#if> y que, el d&iacute;a ${data.fechaFinPrioridad?datetime.iso?string(''dd/MM/yyyy'')} finaliza la posibilidad de extensión o entrada en fases nacionales/regionales de la invención de referencia, con título ${data.solicitudTitle}.</p>
  </body>
</html>


', '<#assign data = PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_DATA?eval />
<#--
  Formato PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_DATA:
  { 
    "solicitudTitle": "PROTECCION 1",
    "monthsBeforeFechaFinPrioridad": 6,
    "fechaFinPrioridad": "2022-01-01T00:00:00Z"
  }
-->
Se informa que queda<#if data.monthsBeforeFechaFinPrioridad != 1>n</#if> ${data.monthsBeforeFechaFinPrioridad} mes<#if data.monthsBeforeFechaFinPrioridad != 1>es</#if> y que, el día ${data.fechaFinPrioridad?datetime.iso?string(''dd/MM/yyyy'')} finaliza la posibilidad de extensión o entrada en fases nacionales/regionales de la invención de referencia, con título ${data.solicitudTitle}.
');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(55, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de comunicados de Cambio de estado a validada de una soolicitud de tipo RRHH', 'CSP_COM_CAMBIO_ESTADO_VALIDADA_SOL_TIPO_RRHH_CONTENT', '<#assign data = CSP_COM_CAMBIO_ESTADO_VALIDADA_SOL_TIPO_RRHH_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>

    <p>Le informamos que con fecha ${data.fechaEstado?datetime.iso?string(''dd/MM/yyyy'')}, su solicitud ${data.codigoInternoSolicitud} <#if data.tituloConvocatoria?has_content>dentro de la convocatoria ${data.tituloConvocatoria} </#if>ha sido validada por el/la tutor/a.</p>

    <p>Reciba un cordial saludo.</p>

    <p>Secci&oacute;n de Recursos Humanos de la Investigaci&oacute;n y Plan Propio</p>

    <p>Área de Investigaci&oacute;n</p>
  </body>
</html>
', '<#assign data = CSP_COM_CAMBIO_ESTADO_VALIDADA_SOL_TIPO_RRHH_DATA?eval_json />
Estimado/a investigador/a,

Le informamos que con fecha ${data.fechaEstado?datetime.iso?string(''dd/MM/yyyy'')}, su solicitud ${data.codigoInternoSolicitud} <#if data.tituloConvocatoria?has_content>dentro de la convocatoria ${data.tituloConvocatoria} </#if>ha sido validada por el/la tutor/a.

Reciba un cordial saludo.

Sección de Recursos Humanos de la Investigación y Plan Propio

Área de Investigación');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(53, NULL, NULL, NULL, NULL, 'Plantilla de contenido para Convocatorias-Fases', 'CSP_COM_CONVOCATORIA_FASE_CONTENT', '<#ftl output_format="HTML" auto_esc=false>
<#assign dateFrom = CSP_CONV_FASE_FECHA_INICIO?datetime.iso>
<#assign dateTo = CSP_CONV_FASE_FECHA_FIN?datetime.iso>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Desde el ${dateFrom?string("dd/MM/yyyy")} a las ${dateFrom?string("HH:mm")} y hasta el ${dateTo?string("dd/MM/yyyy")} a las ${dateTo?string("HH:mm")} estará abierta la fase &quot;${CSP_CONV_TIPO_FASE}&quot; de la convocatoria &quot;${CSP_CONV_FASE_TITULO}&quot;.</p>
    
    <#if CSP_CONV_FASE_OBSERVACIONES?has_content>
    <p>En la fase se han indicado las siguientes observaciones:
       <p>${CSP_CONV_FASE_OBSERVACIONES}</p>
    </p>
    </#if>
  </body>
</html>
', '<#assign dateFrom = CSP_CONV_FASE_FECHA_INICIO?datetime.iso>
<#assign dateTo = CSP_CONV_FASE_FECHA_FIN?datetime.iso>

Desde el ${dateFrom?string("dd/MM/yyyy")} a las ${dateFrom?string("HH:mm")} y hasta el ${dateTo?string("dd/MM/yyyy")} a las ${dateTo?string("HH:mm")} estará abierta la fase "${CSP_CONV_TIPO_FASE}" de la convocatoria "${CSP_CONV_FASE_TITULO}".
<#if CSP_CONV_FASE_OBSERVACIONES?has_content>

En la fase se han indicado las siguientes observaciones:
${CSP_CONV_FASE_OBSERVACIONES}
</#if>');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(54, NULL, NULL, NULL, NULL, 'Plantilla de contenido para Proyecto-Fases', 'CSP_COM_PROYECTO_FASE_CONTENT', '<#ftl output_format="HTML" auto_esc=false>
<#assign dateFrom = CSP_PRO_FASE_FECHA_INICIO?datetime.iso>
<#assign dateTo = CSP_PRO_FASE_FECHA_FIN?datetime.iso>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Desde el ${dateFrom?string("dd/MM/yyyy")} a las ${dateFrom?string("HH:mm")} y hasta el ${dateTo?string("dd/MM/yyyy")} a las ${dateTo?string("HH:mm")} estará abierta la fase &quot;${CSP_PRO_TIPO_FASE}&quot; del proyecto &quot;${CSP_PRO_FASE_TITULO_PROYECTO}&quot;<#if CSP_PRO_FASE_TITULO_CONVOCATORIA?has_content> de la convocatoria &quot;${CSP_PRO_FASE_TITULO_CONVOCATORIA}&quot;</#if>.</p>

    <#if CSP_PRO_FASE_OBSERVACIONES?has_content>
    <p>En la fase se han indicado las siguientes observaciones:
       <p>${CSP_PRO_FASE_OBSERVACIONES}</p>
    </p>
    </#if>
  </body>
</html>
', '<#assign dateFrom = CSP_PRO_FASE_FECHA_INICIO?datetime.iso>
<#assign dateTo = CSP_PRO_FASE_FECHA_FIN?datetime.iso>

Desde el ${dateFrom?string("dd/MM/yyyy")} a las ${dateFrom?string("HH:mm")} y hasta el ${dateTo?string("dd/MM/yyyy")} a las ${dateTo?string("HH:mm")} estará abierta la fase "${CSP_PRO_TIPO_FASE}" del proyecto "${CSP_PRO_FASE_TITULO_PROYECTO}"<#if CSP_PRO_FASE_TITULO_CONVOCATORIA?has_content> de la convocatoria "${CSP_PRO_FASE_TITULO_CONVOCATORIA}"</#if>.
<#if CSP_PRO_FASE_OBSERVACIONES?has_content>

En la fase se han indicado las siguientes observaciones: 
${CSP_PRO_FASE_OBSERVACIONES}
</#if>');
INSERT INTO test.content_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl_html, tpl_text) VALUES(56, NULL, NULL, NULL, NULL, 'Plantilla de contenido para el envío de comunicados de Cambio de estado a rechazada de una soolicitud de tipo RRHH', 'CSP_COM_CAMBIO_ESTADO_RECHAZADA_SOL_TIPO_RRHH_CONTENT', '<#assign data = CSP_COM_CAMBIO_ESTADO_RECHAZADA_SOL_TIPO_RRHH_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>

    <p>Le informamos que con fecha ${data.fechaEstado?datetime.iso?string(''dd/MM/yyyy'')}, su solicitud ${data.codigoInternoSolicitud} <#if data.tituloConvocatoria?has_content>dentro de la convocatoria ${data.tituloConvocatoria} </#if>ha sido rechazada por el/la tutor/a.</p>

    <p>Reciba un cordial saludo.</p>

    <p>Secci&oacute;n de Recursos Humanos de la Investigaci&oacute;n y Plan Propio</p>

    <p>Área de Investigaci&oacute;n</p>
  </body>
</html>
', '<#assign data = CSP_COM_CAMBIO_ESTADO_RECHAZADA_SOL_TIPO_RRHH_DATA?eval_json />
Estimado/a investigador/a,

Le informamos que con fecha ${data.fechaEstado?datetime.iso?string(''dd/MM/yyyy'')}, su solicitud ${data.codigoInternoSolicitud} <#if data.tituloConvocatoria?has_content>dentro de la convocatoria ${data.tituloConvocatoria} </#if>ha sido rechazada por el/la tutor/a.

Reciba un cordial saludo.

Sección de Recursos Humanos de la Investigación y Plan Propio

Área de Investigación');

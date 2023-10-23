<#assign data = ETI_COM_ASIGNACION_EVALUACION_DATA?eval_json />
<#if data.fechaEvaluacionAnterior??>
Asignación de reevaluación de memoria ${data.referenciaMemoria}
<#else>
Asignación de evaluación de memoria ${data.referenciaMemoria}
</#if>
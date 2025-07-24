<#assign data = ETI_COM_ASIGNACION_EVALUACION_DATA?eval_json />
<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
<#if data.fechaEvaluacionAnterior??>
Asignación de reevaluación de memoria: ${data.referenciaMemoria}
<#else>
Asignación de evaluación de memoria: ${data.referenciaMemoria}
</#if>
</#macro>
<#macro renderEn>
<#setting locale="en">
<#if data.fechaEvaluacionAnterior??>
Report Re-Evaluation Assignment: ${data.referenciaMemoria}
<#else>
Report evaluation assignment: ${data.referenciaMemoria}
</#if>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<#if data.fechaEvaluacionAnterior??>
Memoriaren berrebaluazioaren esleipena: ${data.referenciaMemoria}
<#else>
Memoriaren ebaluazioaren esleipena: ${data.referenciaMemoria}
</#if>
</#macro>
<@.vars["render${renderLang?capitalize}"] />
package org.crue.hercules.sgi.prc.enums;

import java.util.stream.Stream;

import org.crue.hercules.sgi.prc.exceptions.CampoCVNNotFoundException;

public enum TablaMaestraCVN {
  /* Capítulo de libro */
  E060_010_010_010_004("060.010.010.010.004"),
  /* Informe científico-técnico */
  E060_010_010_010_018("060.010.010.010.018"),
  /* Artículo científico */
  E060_010_010_010_020("060.010.010.010.020"),
  /* Libro o monografía científica */
  E060_010_010_010_032("060.010.010.010.032"),
  /* Artículos en prensa */
  E060_010_010_010_075("060.010.010.010.075"),
  /* Diccionario común */
  E060_010_010_010_106("060.010.010.010.106"),
  /* Revistas de difusión General */
  E060_010_010_010_173("060.010.010.010.173"),
  /* Artículo de enciclopedia */
  E060_010_010_010_202("060.010.010.010.202"),
  /* Artículo de divulgación */
  E060_010_010_010_203("060.010.010.010.203"),
  /* Traducción */
  E060_010_010_010_204("060.010.010.010.204"),
  /* Reseña */
  E060_010_010_010_205("060.010.010.010.205"),
  /* Revisión bibliográfica */
  E060_010_010_010_206("060.010.010.010.206"),
  /* Libro de divulgación */
  E060_010_010_010_207("060.010.010.010.207"),
  /* Edición científica */
  E060_010_010_010_208("060.010.010.010.208"),
  /* Diccionario científico */
  E060_010_010_010_209("060.010.010.010.209"),
  /* Otros */
  E060_010_010_010_OTHERS("060.010.010.010.OTHERS"),
  /* Comentario sistemático a normas */
  E060_010_010_010_COMENTARIO_SISTEMATICO_NORMAS("060.010.010.010.COMENTARIO_SISTEMATICO_NORMAS"),
  /* Catálogo de obra artística */
  E060_010_010_070_006("060.010.010.070.006"),
  /* Documento o Informe científico-técnico */
  E060_010_010_070_018("060.010.010.070.018"),
  /* Libro */
  E060_010_010_070_032("060.010.010.070.032"),
  /* Revista */
  E060_010_010_070_057("060.010.010.070.057"),
  /* DOI */
  E060_010_010_410_040("060.010.010.410.040"),
  /* Handle */
  E060_010_010_410_120("060.010.010.410.120"),
  /* PMID */
  E060_010_010_410_130("060.010.010.410.130"),
  /* Otros */
  E060_010_010_410_OTHERS("060.010.010.410.OTHERS"),
  /* All open Access */
  TIPO_OPEN_ACCESS_ALL("TIPO_OPEN_ACCESS.ALL"),
  /* Gold */
  TIPO_OPEN_ACCESS_GOLD("TIPO_OPEN_ACCESS.GOLD"),
  /* Hybrid Gold */
  TIPO_OPEN_ACCESS_HYBRID_GOLD("TIPO_OPEN_ACCESS.HYBRID_GOLD"),
  /* Bronze */
  TIPO_OPEN_ACCESS_BRONZE("TIPO_OPEN_ACCESS.BRONZE"),
  /* Green */
  TIPO_OPEN_ACCESS_GREEN("TIPO_OPEN_ACCESS.GREEN"),
  /* Congreso */
  E060_010_020_010_008("060.010.020.010.008"),
  /* Jornada */
  E060_010_020_010_031("060.010.020.010.031"),
  /* Seminario */
  E060_010_020_010_063("060.010.020.010.063"),
  /* Otros */
  E060_010_020_010_OTHERS("060.010.020.010.OTHERS"),
  /* Autonómica */
  AMBITO_000("AMBITO.000"),
  /* Nacional */
  AMBITO_010("AMBITO.010"),
  /* Unión Europea */
  AMBITO_020("AMBITO.020"),
  /* Internacional no UE */
  AMBITO_030("AMBITO.030"),
  /* Otros */
  AMBITO_OTHERS("AMBITO.OTHERS"),
  /* Organizativo - Presidente Comité */
  E060_010_020_050_050("060.010.020.050.050"),
  /* Organizativo - Comité científico y organizador */
  E060_010_020_050_060("060.010.020.050.060"),
  /* Organizativo - Otros */
  E060_010_020_050_070("060.010.020.050.070"),
  /* Participativo - Plenaria */
  E060_010_020_050_080("060.010.020.050.080"),
  /* Participativo - Ponencia invitada/ Keynote */
  E060_010_020_050_730("060.010.020.050.730"),
  /* Participativo - Ponencia oral (comunicación oral) */
  E060_010_020_050_960("060.010.020.050.960"),
  /* Participativo - Póster */
  E060_010_020_050_970("060.010.020.050.970"),
  /* Comité organizador */
  E060_010_020_050_980("060.010.020.050.980"),
  /* Comité científico */
  E060_010_020_050_990("060.010.020.050.990"),
  /* Participativo - Otros */
  E060_010_020_050_OTHERS("060.010.020.050.OTHERS"),
  /* Albania */
  PAIS_008("PAIS.008"),
  /* Alemania */
  PAIS_276("PAIS.276"),
  /* Andorra */
  PAIS_020("PAIS.020"),
  /* Angola */
  PAIS_024("PAIS.024"),
  /* Anguilla */
  PAIS_660("PAIS.660"),
  /* Afganistán */
  PAIS_004("PAIS.004"),
  /* Antártida */
  PAIS_010("PAIS.010"),
  /* Antigua y Barbuda */
  PAIS_028("PAIS.028"),
  /* Antillas Holandesas */
  PAIS_530("PAIS.530"),
  /* Arabia Saudita */
  PAIS_682("PAIS.682"),
  /* Argelia */
  PAIS_012("PAIS.012"),
  /* Argentina */
  PAIS_032("PAIS.032"),
  /* Armenia */
  PAIS_051("PAIS.051"),
  /* Aruba */
  PAIS_533("PAIS.533"),
  /* Australia */
  PAIS_036("PAIS.036"),
  /* Austria */
  PAIS_040("PAIS.040"),
  /* Azerbaiyán */
  PAIS_031("PAIS.031"),
  /* Bahamas */
  PAIS_044("PAIS.044"),
  /* Bahrein */
  PAIS_048("PAIS.048"),
  /* Bangladesh */
  PAIS_050("PAIS.050"),
  /* Barbados */
  PAIS_052("PAIS.052"),
  /* Bélgica */
  PAIS_056("PAIS.056"),
  /* Belice */
  PAIS_084("PAIS.084"),
  /* Benín */
  PAIS_204("PAIS.204"),
  /* Bermudas */
  PAIS_060("PAIS.060"),
  /* Bhután */
  PAIS_064("PAIS.064"),
  /* Bielorrusia */
  PAIS_112("PAIS.112"),
  /* Bolivia */
  PAIS_068("PAIS.068"),
  /* Bosnia Herzegovina */
  PAIS_070("PAIS.070"),
  /* Botswana */
  PAIS_072("PAIS.072"),
  /* Bouvet, Isla */
  PAIS_074("PAIS.074"),
  /* Brasil */
  PAIS_076("PAIS.076"),
  /* Brunei Darussalam */
  PAIS_096("PAIS.096"),
  /* Bulgaria */
  PAIS_100("PAIS.100"),
  /* Burkina Fasso */
  PAIS_854("PAIS.854"),
  /* Burundi */
  PAIS_108("PAIS.108"),
  /* Cabo Verde */
  PAIS_132("PAIS.132"),
  /* Caimán, Islas */
  PAIS_136("PAIS.136"),
  /* Camboya */
  PAIS_116("PAIS.116"),
  /* Camerún */
  PAIS_120("PAIS.120"),
  /* Canadá */
  PAIS_124("PAIS.124"),
  /* Chad */
  PAIS_148("PAIS.148"),
  /* Chile */
  PAIS_152("PAIS.152"),
  /* China */
  PAIS_156("PAIS.156"),
  /* Chipre */
  PAIS_196("PAIS.196"),
  /* Christmas, Isla */
  PAIS_162("PAIS.162"),
  /* Cocos, Islas */
  PAIS_166("PAIS.166"),
  /* Colombia */
  PAIS_170("PAIS.170"),
  /* Comores */
  PAIS_174("PAIS.174"),
  /* Congo */
  PAIS_178("PAIS.178"),
  /* Cook, Islas */
  PAIS_184("PAIS.184"),
  /* Costa de Marfil */
  PAIS_384("PAIS.384"),
  /* Costa Rica */
  PAIS_188("PAIS.188"),
  /* Croacia */
  PAIS_191("PAIS.191"),
  /* Cuba */
  PAIS_192("PAIS.192"),
  /* Desconocido */
  PAIS_999("PAIS.999"),
  /* Dinamarca */
  PAIS_208("PAIS.208"),
  /* Djibouti */
  PAIS_262("PAIS.262"),
  /* Dominica */
  PAIS_212("PAIS.212"),
  /* Ecuador */
  PAIS_218("PAIS.218"),
  /* Egipto */
  PAIS_818("PAIS.818"),
  /* El Salvador */
  PAIS_222("PAIS.222"),
  /* Emiratos Árabes Unidos */
  PAIS_784("PAIS.784"),
  /* Eritrea */
  PAIS_232("PAIS.232"),
  /* Eslovaquia */
  PAIS_703("PAIS.703"),
  /* Eslovenia */
  PAIS_705("PAIS.705"),
  /* España */
  PAIS_724("PAIS.724"),
  /* Estados Unidos de América */
  PAIS_840("PAIS.840"),
  /* Estonia */
  PAIS_233("PAIS.233"),
  /* Etiopía */
  PAIS_231("PAIS.231"),
  /* Feroe, Islas */
  PAIS_234("PAIS.234"),
  /* Fiji */
  PAIS_242("PAIS.242"),
  /* Filipinas */
  PAIS_608("PAIS.608"),
  /* Finlandia */
  PAIS_246("PAIS.246"),
  /* Francia */
  PAIS_250("PAIS.250"),
  /* Francia, Territorios Sudeste */
  PAIS_260("PAIS.260"),
  /* Gabón */
  PAIS_266("PAIS.266"),
  /* Gambia */
  PAIS_270("PAIS.270"),
  /* Georgia */
  PAIS_268("PAIS.268"),
  /* Georgias del Sur y Sandwich del Sur, Islas */
  PAIS_239("PAIS.239"),
  /* Ghana */
  PAIS_288("PAIS.288"),
  /* Gibraltar */
  PAIS_292("PAIS.292"),
  /* Granada */
  PAIS_308("PAIS.308"),
  /* Grecia */
  PAIS_300("PAIS.300"),
  /* Groenlandia */
  PAIS_304("PAIS.304"),
  /* Guadalupe */
  PAIS_312("PAIS.312"),
  /* Guam */
  PAIS_316("PAIS.316"),
  /* Guatemala */
  PAIS_320("PAIS.320"),
  /* Guayana Francesa */
  PAIS_254("PAIS.254"),
  /* Guernsey */
  PAIS_831("PAIS.831"),
  /* Guinea */
  PAIS_324("PAIS.324"),
  /* Guinea Ecuatorial */
  PAIS_226("PAIS.226"),
  /* Guinea-Bissau */
  PAIS_624("PAIS.624"),
  /* Guyana */
  PAIS_328("PAIS.328"),
  /* Haití */
  PAIS_332("PAIS.332"),
  /* Heard and Mcdonald, Islas */
  PAIS_334("PAIS.334"),
  /* Holanda */
  PAIS_528("PAIS.528"),
  /* Honduras */
  PAIS_340("PAIS.340"),
  /* Hong Kong */
  PAIS_344("PAIS.344"),
  /* Hungría */
  PAIS_348("PAIS.348"),
  /* India */
  PAIS_356("PAIS.356"),
  /* Índico, Océano */
  PAIS_086("PAIS.086"),
  /* Indonesia */
  PAIS_360("PAIS.360"),
  /* Irán */
  PAIS_364("PAIS.364"),
  /* Iraq */
  PAIS_368("PAIS.368"),
  /* Irlanda */
  PAIS_372("PAIS.372"),
  /* Isla de Man */
  PAIS_833("PAIS.833"),
  /* Islandia */
  PAIS_352("PAIS.352"),
  /* Islas Aland */
  PAIS_248("PAIS.248"),
  /* Islas Marianas */
  PAIS_580("PAIS.580"),
  /* Islas Marshall */
  PAIS_584("PAIS.584"),
  /* Islas Menores EEUU */
  PAIS_581("PAIS.581"),
  /* Islas Salomón */
  PAIS_090("PAIS.090"),
  /* Israel */
  PAIS_376("PAIS.376"),
  /* Italia */
  PAIS_380("PAIS.380"),
  /* Jamaica */
  PAIS_388("PAIS.388"),
  /* Japón */
  PAIS_392("PAIS.392"),
  /* Jersey */
  PAIS_832("PAIS.832"),
  /* Jordania */
  PAIS_400("PAIS.400"),
  /* Kazajstán */
  PAIS_398("PAIS.398"),
  /* Kenia */
  PAIS_404("PAIS.404"),
  /* Kirguistán */
  PAIS_417("PAIS.417"),
  /* Kiribati */
  PAIS_296("PAIS.296"),
  /* Kuwait */
  PAIS_414("PAIS.414"),
  /* Laos */
  PAIS_418("PAIS.418"),
  /* Lesotho */
  PAIS_426("PAIS.426"),
  /* Letonia */
  PAIS_428("PAIS.428"),
  /* Líbano */
  PAIS_422("PAIS.422"),
  /* Liberia */
  PAIS_430("PAIS.430"),
  /* Libia */
  PAIS_434("PAIS.434"),
  /* Liechtenstein */
  PAIS_438("PAIS.438"),
  /* Lituania */
  PAIS_440("PAIS.440"),
  /* Luxemburgo */
  PAIS_442("PAIS.442"),
  /* Macao */
  PAIS_446("PAIS.446"),
  /* Macedonia */
  PAIS_807("PAIS.807"),
  /* Madagascar */
  PAIS_450("PAIS.450"),
  /* Malasia */
  PAIS_458("PAIS.458"),
  /* Malawi */
  PAIS_454("PAIS.454"),
  /* Maldivas */
  PAIS_462("PAIS.462"),
  /* Mali */
  PAIS_466("PAIS.466"),
  /* Malta */
  PAIS_470("PAIS.470"),
  /* Malvinas, Islas */
  PAIS_238("PAIS.238"),
  /* Marruecos */
  PAIS_504("PAIS.504"),
  /* Martinica */
  PAIS_474("PAIS.474"),
  /* Mauricio */
  PAIS_480("PAIS.480"),
  /* Mauritania */
  PAIS_478("PAIS.478"),
  /* Mayotte */
  PAIS_175("PAIS.175"),
  /* México */
  PAIS_484("PAIS.484"),
  /* Micronesia */
  PAIS_583("PAIS.583"),
  /* Moldavia */
  PAIS_498("PAIS.498"),
  /* Mónaco */
  PAIS_492("PAIS.492"),
  /* Mongolia */
  PAIS_496("PAIS.496"),
  /* Montenegro */
  PAIS_499("PAIS.499"),
  /* Montserrat */
  PAIS_500("PAIS.500"),
  /* Mozambique */
  PAIS_508("PAIS.508"),
  /* Myanmar */
  PAIS_104("PAIS.104"),
  /* Namibia */
  PAIS_516("PAIS.516"),
  /* Nauru */
  PAIS_520("PAIS.520"),
  /* Nepal */
  PAIS_524("PAIS.524"),
  /* Nicaragua */
  PAIS_558("PAIS.558"),
  /* Níger */
  PAIS_562("PAIS.562"),
  /* Nigeria */
  PAIS_566("PAIS.566"),
  /* Niue */
  PAIS_570("PAIS.570"),
  /* Norfolk, Isla */
  PAIS_574("PAIS.574"),
  /* Noruega */
  PAIS_578("PAIS.578"),
  /* Nueva Caledonia */
  PAIS_540("PAIS.540"),
  /* Nueva Zelanda */
  PAIS_554("PAIS.554"),
  /* Omán */
  PAIS_512("PAIS.512"),
  /* Pakistán */
  PAIS_586("PAIS.586"),
  /* Palau */
  PAIS_585("PAIS.585"),
  /* Panamá */
  PAIS_591("PAIS.591"),
  /* Papua Nueva Guinea */
  PAIS_598("PAIS.598"),
  /* Paraguay */
  PAIS_600("PAIS.600"),
  /* Perú */
  PAIS_604("PAIS.604"),
  /* Pitcairn, Isla */
  PAIS_612("PAIS.612"),
  /* Polinesia Francesa */
  PAIS_258("PAIS.258"),
  /* Polonia */
  PAIS_616("PAIS.616"),
  /* Portugal */
  PAIS_620("PAIS.620"),
  /* Puerto Rico */
  PAIS_630("PAIS.630"),
  /* Qatar */
  PAIS_634("PAIS.634"),
  /* Reino Unido */
  PAIS_826("PAIS.826"),
  /* República Centroafricana */
  PAIS_140("PAIS.140"),
  /* República Checa */
  PAIS_203("PAIS.203"),
  /* República de Corea */
  PAIS_410("PAIS.410"),
  /* República Democrática del Congo */
  PAIS_180("PAIS.180"),
  /* República Dominicana */
  PAIS_214("PAIS.214"),
  /* Champagne-Ardenne */
  COMUNIDAD_FR21("COMUNIDAD.FR21"),
  /* República Popular Democrática de Corea */
  PAIS_408("PAIS.408"),
  /* República Sudafricana */
  PAIS_710("PAIS.710"),
  /* Reunión, Isla */
  PAIS_638("PAIS.638"),
  /* Ruanda */
  PAIS_646("PAIS.646"),
  /* Rumanía */
  PAIS_642("PAIS.642"),
  /* Rusia */
  PAIS_643("PAIS.643"),
  /* Sahara Occidental */
  PAIS_732("PAIS.732"),
  /* Samoa */
  PAIS_882("PAIS.882"),
  /* Samoa Norteamericana */
  PAIS_016("PAIS.016"),
  /* San Bartolomé */
  PAIS_652("PAIS.652"),
  /* San Cristóbal y Nieves */
  PAIS_659("PAIS.659"),
  /* San Marino */
  PAIS_674("PAIS.674"),
  /* San Martín */
  PAIS_663("PAIS.663"),
  /* San Pedro y Miquelón */
  PAIS_666("PAIS.666"),
  /* San Vicente Y Las Granadinas */
  PAIS_670("PAIS.670"),
  /* Santa Elena */
  PAIS_654("PAIS.654"),
  /* Santa Lucía */
  PAIS_662("PAIS.662"),
  /* Santo Tomé y Príncipe */
  PAIS_678("PAIS.678"),
  /* Senegal */
  PAIS_686("PAIS.686"),
  /* Serbia */
  PAIS_688("PAIS.688"),
  /* Seychelles */
  PAIS_690("PAIS.690"),
  /* Sierra Leona */
  PAIS_694("PAIS.694"),
  /* Singapur */
  PAIS_702("PAIS.702"),
  /* Siria */
  PAIS_760("PAIS.760"),
  /* Somalia */
  PAIS_706("PAIS.706"),
  /* Sri Lanka */
  PAIS_144("PAIS.144"),
  /* Sudán */
  PAIS_736("PAIS.736"),
  /* Suecia */
  PAIS_752("PAIS.752"),
  /* Suiza */
  PAIS_756("PAIS.756"),
  /* Surinam */
  PAIS_740("PAIS.740"),
  /* Svalbard Y Jan Mayen, Isla */
  PAIS_744("PAIS.744"),
  /* Swazilandia */
  PAIS_748("PAIS.748"),
  /* Tailandia */
  PAIS_764("PAIS.764"),
  /* Taiwán */
  PAIS_158("PAIS.158"),
  /* Tanzania */
  PAIS_834("PAIS.834"),
  /* Tayikistán */
  PAIS_762("PAIS.762"),
  /* Territorios Ocupados de Palestina */
  PAIS_275("PAIS.275"),
  /* Timor */
  PAIS_626("PAIS.626"),
  /* Togo */
  PAIS_768("PAIS.768"),
  /* Tokelau */
  PAIS_772("PAIS.772"),
  /* Tonga */
  PAIS_776("PAIS.776"),
  /* Trinidad Y Tobago */
  PAIS_780("PAIS.780"),
  /* Túnez */
  PAIS_788("PAIS.788"),
  /* Turkmenistán */
  PAIS_795("PAIS.795"),
  /* Turks And Caicos, Islas */
  PAIS_796("PAIS.796"),
  /* Turquía */
  PAIS_792("PAIS.792"),
  /* Tuvalu */
  PAIS_798("PAIS.798"),
  /* Ucrania */
  PAIS_804("PAIS.804"),
  /* Uganda */
  PAIS_800("PAIS.800"),
  /* Uruguay */
  PAIS_858("PAIS.858"),
  /* Uzbekistán */
  PAIS_860("PAIS.860"),
  /* Vanuatu */
  PAIS_548("PAIS.548"),
  /* Vaticano */
  PAIS_336("PAIS.336"),
  /* Venezuela */
  PAIS_862("PAIS.862"),
  /* Vietnam */
  PAIS_704("PAIS.704"),
  /* Vírgenes, Islas */
  PAIS_092("PAIS.092"),
  /* Vírgenes, Islas (EEUU) */
  PAIS_850("PAIS.850"),
  /* Wallis And Futuna, Islas */
  PAIS_876("PAIS.876"),
  /* Yemen */
  PAIS_887("PAIS.887"),
  /* Zambia */
  PAIS_894("PAIS.894"),
  /* Zimbabwe */
  PAIS_716("PAIS.716"),
  /* Arnsberg */
  COMUNIDAD_DEA5("COMUNIDAD.DEA5"),
  /* Berlin */
  COMUNIDAD_DE30("COMUNIDAD.DE30"),
  /* Brandenburg - Nordost */
  COMUNIDAD_DE41("COMUNIDAD.DE41"),
  /* Brandenburg - Südwest */
  COMUNIDAD_DE42("COMUNIDAD.DE42"),
  /* Braunschweig */
  COMUNIDAD_DE91("COMUNIDAD.DE91"),
  /* Bremen */
  COMUNIDAD_DE50("COMUNIDAD.DE50"),
  /* Chemnitz */
  COMUNIDAD_DED1("COMUNIDAD.DED1"),
  /* Darmstadt */
  COMUNIDAD_DE71("COMUNIDAD.DE71"),
  /* Dessau */
  COMUNIDAD_DEE1("COMUNIDAD.DEE1"),
  /* Detmold */
  COMUNIDAD_DEA4("COMUNIDAD.DEA4"),
  /* Dresden */
  COMUNIDAD_DED2("COMUNIDAD.DED2"),
  /* Düsseldorf */
  COMUNIDAD_DEA1("COMUNIDAD.DEA1"),
  /* Extra-Regio */
  COMUNIDAD_DEZZ("COMUNIDAD.DEZZ"),
  /* Freiburg */
  COMUNIDAD_DE13("COMUNIDAD.DE13"),
  /* Gießen */
  COMUNIDAD_DE72("COMUNIDAD.DE72"),
  /* Halle */
  COMUNIDAD_DEE2("COMUNIDAD.DEE2"),
  /* Hamburg */
  COMUNIDAD_DE60("COMUNIDAD.DE60"),
  /* Hannover */
  COMUNIDAD_DE92("COMUNIDAD.DE92"),
  /* Karlsruhe */
  COMUNIDAD_DE12("COMUNIDAD.DE12"),
  /* Kassel */
  COMUNIDAD_DE73("COMUNIDAD.DE73"),
  /* Koblenz */
  COMUNIDAD_DEB1("COMUNIDAD.DEB1"),
  /* Köln */
  COMUNIDAD_DEA2("COMUNIDAD.DEA2"),
  /* Leipzig */
  COMUNIDAD_DED3("COMUNIDAD.DED3"),
  /* Lüneburg */
  COMUNIDAD_DE93("COMUNIDAD.DE93"),
  /* Magdeburg */
  COMUNIDAD_DEE3("COMUNIDAD.DEE3"),
  /* Mecklenburg-Vorpommern */
  COMUNIDAD_DE80("COMUNIDAD.DE80"),
  /* Mittelfranken */
  COMUNIDAD_DE25("COMUNIDAD.DE25"),
  /* Münster */
  COMUNIDAD_DEA3("COMUNIDAD.DEA3"),
  /* Niederbayern */
  COMUNIDAD_DE22("COMUNIDAD.DE22"),
  /* Oberbayern */
  COMUNIDAD_DE21("COMUNIDAD.DE21"),
  /* Oberfranken */
  COMUNIDAD_DE24("COMUNIDAD.DE24"),
  /* Oberpfalz */
  COMUNIDAD_DE23("COMUNIDAD.DE23"),
  /* Rheinhessen-Pfalz */
  COMUNIDAD_DEB3("COMUNIDAD.DEB3"),
  /* Saarland */
  COMUNIDAD_DEC0("COMUNIDAD.DEC0"),
  /* Schleswig-Holstein */
  COMUNIDAD_DEF0("COMUNIDAD.DEF0"),
  /* Schwaben */
  COMUNIDAD_DE27("COMUNIDAD.DE27"),
  /* Stuttgart */
  COMUNIDAD_DE11("COMUNIDAD.DE11"),
  /* Thüringen */
  COMUNIDAD_DEG0("COMUNIDAD.DEG0"),
  /* Trier */
  COMUNIDAD_DEB2("COMUNIDAD.DEB2"),
  /* Tübingen */
  COMUNIDAD_DE14("COMUNIDAD.DE14"),
  /* Unterfranken */
  COMUNIDAD_DE26("COMUNIDAD.DE26"),
  /* Weser-Ems */
  COMUNIDAD_DE94("COMUNIDAD.DE94"),
  /* Extra-Regio */
  COMUNIDAD_CYZZ("COMUNIDAD.CYZZ"),
  /* Kypros / Kibris */
  COMUNIDAD_CY00("COMUNIDAD.CY00"),
  /* Danmark */
  COMUNIDAD_DK00("COMUNIDAD.DK00"),
  /* Extra-Regio */
  COMUNIDAD_DKZZ("COMUNIDAD.DKZZ"),
  /* Extra-Regio */
  COMUNIDAD_SIZZ("COMUNIDAD.SIZZ"),
  /* Slovenija */
  COMUNIDAD_SI00("COMUNIDAD.SI00"),
  /* Andalucía */
  COMUNIDAD_ES61("COMUNIDAD.ES61"),
  /* Aragón */
  COMUNIDAD_ES24("COMUNIDAD.ES24"),
  /* Canarias */
  COMUNIDAD_ES70("COMUNIDAD.ES70"),
  /* Cantabria */
  COMUNIDAD_ES13("COMUNIDAD.ES13"),
  /* Castilla y León */
  COMUNIDAD_ES41("COMUNIDAD.ES41"),
  /* Castilla-La Mancha */
  COMUNIDAD_ES42("COMUNIDAD.ES42"),
  /* Cataluña */
  COMUNIDAD_ES51("COMUNIDAD.ES51"),
  /* Ciudad Autónoma de Ceuta */
  COMUNIDAD_ES63("COMUNIDAD.ES63"),
  /* Ciudad Autónoma de Melilla */
  COMUNIDAD_ES64("COMUNIDAD.ES64"),
  /* Comunidad de Madrid */
  COMUNIDAD_ES30("COMUNIDAD.ES30"),
  /* Comunidad Foral de Navarra */
  COMUNIDAD_ES22("COMUNIDAD.ES22"),
  /* Comunidad Valenciana */
  COMUNIDAD_ES52("COMUNIDAD.ES52"),
  /* Extra-Regio */
  COMUNIDAD_ESZZ("COMUNIDAD.ESZZ"),
  /* Extremadura */
  COMUNIDAD_ES43("COMUNIDAD.ES43"),
  /* Galicia */
  COMUNIDAD_ES11("COMUNIDAD.ES11"),
  /* Illes Balears */
  COMUNIDAD_ES53("COMUNIDAD.ES53"),
  /* La Rioja */
  COMUNIDAD_ES23("COMUNIDAD.ES23"),
  /* País Vasco */
  COMUNIDAD_ES21("COMUNIDAD.ES21"),
  /* Principado de Asturias */
  COMUNIDAD_ES12("COMUNIDAD.ES12"),
  /* Región de Murcia */
  COMUNIDAD_ES62("COMUNIDAD.ES62"),
  /* Eesti */
  COMUNIDAD_EE00("COMUNIDAD.EE00"),
  /* Extra-Regio */
  COMUNIDAD_EEZZ("COMUNIDAD.EEZZ"),
  /* Etelä-Suomi */
  COMUNIDAD_FI18("COMUNIDAD.FI18"),
  /* Extra-Regio */
  COMUNIDAD_FIZZ("COMUNIDAD.FIZZ"),
  /* Itä-Suomi */
  COMUNIDAD_FI13("COMUNIDAD.FI13"),
  /* Länsi-Suomi */
  COMUNIDAD_FI19("COMUNIDAD.FI19"),
  /* Pohjois-Suomi */
  COMUNIDAD_FI1A("COMUNIDAD.FI1A"),
  /* Åland */
  COMUNIDAD_FI20("COMUNIDAD.FI20"),
  /* Alsace */
  COMUNIDAD_FR42("COMUNIDAD.FR42"),
  /* Aquitaine */
  COMUNIDAD_FR61("COMUNIDAD.FR61"),
  /* Auvergne */
  COMUNIDAD_FR72("COMUNIDAD.FR72"),
  /* Basse-Normandie */
  COMUNIDAD_FR25("COMUNIDAD.FR25"),
  /* Bourgogne */
  COMUNIDAD_FR26("COMUNIDAD.FR26"),
  /* Bretagne */
  COMUNIDAD_FR52("COMUNIDAD.FR52"),
  /* Centre */
  COMUNIDAD_FR24("COMUNIDAD.FR24"),
  /* Corse */
  COMUNIDAD_FR83("COMUNIDAD.FR83"),
  /* Extra-Regio */
  COMUNIDAD_FRZZ("COMUNIDAD.FRZZ"),
  /* Franche-Comté */
  COMUNIDAD_FR43("COMUNIDAD.FR43"),
  /* Guadeloupe */
  COMUNIDAD_FR91("COMUNIDAD.FR91"),
  /* Guyane */
  COMUNIDAD_FR93("COMUNIDAD.FR93"),
  /* Haute-Normandie */
  COMUNIDAD_FR23("COMUNIDAD.FR23"),
  /* Île de France */
  COMUNIDAD_FR10("COMUNIDAD.FR10"),
  /* Languedoc-Roussillon */
  COMUNIDAD_FR81("COMUNIDAD.FR81"),
  /* Limousin */
  COMUNIDAD_FR63("COMUNIDAD.FR63"),
  /* Lorraine */
  COMUNIDAD_FR41("COMUNIDAD.FR41"),
  /* Martinique */
  COMUNIDAD_FR92("COMUNIDAD.FR92"),
  /* Midi-Pyrénées */
  COMUNIDAD_FR62("COMUNIDAD.FR62"),
  /* Nord - Pas-de-Calais */
  COMUNIDAD_FR30("COMUNIDAD.FR30"),
  /* Pays de la Loire */
  COMUNIDAD_FR51("COMUNIDAD.FR51"),
  /* Picardie */
  COMUNIDAD_FR22("COMUNIDAD.FR22"),
  /* Poitou-Charentes */
  COMUNIDAD_FR53("COMUNIDAD.FR53"),
  /* Provence-Alpes-Côte d'Azur */
  COMUNIDAD_FR82("COMUNIDAD.FR82"),
  /* Réunion */
  COMUNIDAD_FR94("COMUNIDAD.FR94"),
  /* Rhône-Alpes */
  COMUNIDAD_FR71("COMUNIDAD.FR71"),
  /* Anatoliki Makedonia, Thraki */
  COMUNIDAD_GR11("COMUNIDAD.GR11"),
  /* Attiki */
  COMUNIDAD_GR30("COMUNIDAD.GR30"),
  /* Dytiki Ellada */
  COMUNIDAD_GR23("COMUNIDAD.GR23"),
  /* Dytiki Makedonia */
  COMUNIDAD_GR13("COMUNIDAD.GR13"),
  /* Extra-Regio */
  COMUNIDAD_GRZZ("COMUNIDAD.GRZZ"),
  /* Ionia Nisia */
  COMUNIDAD_GR22("COMUNIDAD.GR22"),
  /* Ipeiros */
  COMUNIDAD_GR21("COMUNIDAD.GR21"),
  /* Kentriki Makedonia */
  COMUNIDAD_GR12("COMUNIDAD.GR12"),
  /* Kriti */
  COMUNIDAD_GR43("COMUNIDAD.GR43"),
  /* Notio Aigaio */
  COMUNIDAD_GR42("COMUNIDAD.GR42"),
  /* Peloponnisos */
  COMUNIDAD_GR25("COMUNIDAD.GR25"),
  /* Sterea Ellada */
  COMUNIDAD_GR24("COMUNIDAD.GR24"),
  /* Thessalia */
  COMUNIDAD_GR14("COMUNIDAD.GR14"),
  /* Voreio Aigaio */
  COMUNIDAD_GR41("COMUNIDAD.GR41"),
  /* Drenthe */
  COMUNIDAD_NL13("COMUNIDAD.NL13"),
  /* Extra-Regio */
  COMUNIDAD_NLZZ("COMUNIDAD.NLZZ"),
  /* Flevoland */
  COMUNIDAD_NL23("COMUNIDAD.NL23"),
  /* Friesland */
  COMUNIDAD_NL12("COMUNIDAD.NL12"),
  /* Gelderland */
  COMUNIDAD_NL22("COMUNIDAD.NL22"),
  /* Groningen */
  COMUNIDAD_NL11("COMUNIDAD.NL11"),
  /* Limburg (NL) */
  COMUNIDAD_NL42("COMUNIDAD.NL42"),
  /* Noord-Brabant */
  COMUNIDAD_NL41("COMUNIDAD.NL41"),
  /* Noord-Holland */
  COMUNIDAD_NL32("COMUNIDAD.NL32"),
  /* Overijssel */
  COMUNIDAD_NL21("COMUNIDAD.NL21"),
  /* Utrecht */
  COMUNIDAD_NL31("COMUNIDAD.NL31"),
  /* Zeeland */
  COMUNIDAD_NL34("COMUNIDAD.NL34"),
  /* Zuid-Holland */
  COMUNIDAD_NL33("COMUNIDAD.NL33"),
  /* Del-Alfold */
  COMUNIDAD_HU33("COMUNIDAD.HU33"),
  /* Del-Dunantul */
  COMUNIDAD_HU23("COMUNIDAD.HU23"),
  /* Eszak-Alfold */
  COMUNIDAD_HU32("COMUNIDAD.HU32"),
  /* Eszak-Magyarorszag */
  COMUNIDAD_HU31("COMUNIDAD.HU31"),
  /* Extra-Regio */
  COMUNIDAD_HUZZ("COMUNIDAD.HUZZ"),
  /* Kozep-Dunantul */
  COMUNIDAD_HU21("COMUNIDAD.HU21"),
  /* Kozep-Magyarorszag */
  COMUNIDAD_HU10("COMUNIDAD.HU10"),
  /* Nyugat-Dunantul */
  COMUNIDAD_HU22("COMUNIDAD.HU22"),
  /* Border, Midland and Western */
  COMUNIDAD_IE01("COMUNIDAD.IE01"),
  /* Extra-Regio */
  COMUNIDAD_IEZZ("COMUNIDAD.IEZZ"),
  /* Southern and Eastern */
  COMUNIDAD_IE02("COMUNIDAD.IE02"),
  /* Abruzzo */
  COMUNIDAD_ITF1("COMUNIDAD.ITF1"),
  /* Basilicata */
  COMUNIDAD_ITF5("COMUNIDAD.ITF5"),
  /* Calabria */
  COMUNIDAD_ITF6("COMUNIDAD.ITF6"),
  /* Campania */
  COMUNIDAD_ITF3("COMUNIDAD.ITF3"),
  /* Emilia-Romagna */
  COMUNIDAD_ITD5("COMUNIDAD.ITD5"),
  /* Extra-Regio */
  COMUNIDAD_ITZZ("COMUNIDAD.ITZZ"),
  /* Friuli-Venezia Giulia */
  COMUNIDAD_ITD4("COMUNIDAD.ITD4"),
  /* Lazio */
  COMUNIDAD_ITE4("COMUNIDAD.ITE4"),
  /* Liguria */
  COMUNIDAD_ITC3("COMUNIDAD.ITC3"),
  /* Lombardia */
  COMUNIDAD_ITC4("COMUNIDAD.ITC4"),
  /* Marche */
  COMUNIDAD_ITE3("COMUNIDAD.ITE3"),
  /* Molise */
  COMUNIDAD_ITF2("COMUNIDAD.ITF2"),
  /* Piemonte */
  COMUNIDAD_ITC1("COMUNIDAD.ITC1"),
  /* Provincia Autonoma Bolzano/Bozen */
  COMUNIDAD_ITD1("COMUNIDAD.ITD1"),
  /* Provincia Autonoma Trento */
  COMUNIDAD_ITD2("COMUNIDAD.ITD2"),
  /* Puglia */
  COMUNIDAD_ITF4("COMUNIDAD.ITF4"),
  /* Sardegna */
  COMUNIDAD_ITG2("COMUNIDAD.ITG2"),
  /* Sicilia */
  COMUNIDAD_ITG1("COMUNIDAD.ITG1"),
  /* Toscana */
  COMUNIDAD_ITE1("COMUNIDAD.ITE1"),
  /* Umbria */
  COMUNIDAD_ITE2("COMUNIDAD.ITE2"),
  /* Valle d'Aosta/Vallée d'Aoste */
  COMUNIDAD_ITC2("COMUNIDAD.ITC2"),
  /* Veneto */
  COMUNIDAD_ITD3("COMUNIDAD.ITD3"),
  /* Extra-Regio */
  COMUNIDAD_LVZZ("COMUNIDAD.LVZZ"),
  /* Latvija */
  COMUNIDAD_LV00("COMUNIDAD.LV00"),
  /* Extra-Regio */
  COMUNIDAD_LTZZ("COMUNIDAD.LTZZ"),
  /* Lietuva */
  COMUNIDAD_LT00("COMUNIDAD.LT00"),
  /* Extra-Regio */
  COMUNIDAD_LUZZ("COMUNIDAD.LUZZ"),
  /* Luxembourg (Grand-Duché) */
  COMUNIDAD_LU00("COMUNIDAD.LU00"),
  /* Extra-Regio */
  COMUNIDAD_MTZZ("COMUNIDAD.MTZZ"),
  /* Malta */
  COMUNIDAD_MT00("COMUNIDAD.MT00"),
  /* Dolnoslaskie */
  COMUNIDAD_PL51("COMUNIDAD.PL51"),
  /* Extra-Regio */
  COMUNIDAD_PLZZ("COMUNIDAD.PLZZ"),
  /* Kujawsko-Pomorskie */
  COMUNIDAD_PL61("COMUNIDAD.PL61"),
  /* Lodzkie */
  COMUNIDAD_PL11("COMUNIDAD.PL11"),
  /* Lubelskie */
  COMUNIDAD_PL31("COMUNIDAD.PL31"),
  /* Lubuskie */
  COMUNIDAD_PL43("COMUNIDAD.PL43"),
  /* Malopolskie */
  COMUNIDAD_PL21("COMUNIDAD.PL21"),
  /* Mazowieckie */
  COMUNIDAD_PL12("COMUNIDAD.PL12"),
  /* Opolskie */
  COMUNIDAD_PL52("COMUNIDAD.PL52"),
  /* Podkarpackie */
  COMUNIDAD_PL32("COMUNIDAD.PL32"),
  /* Podlaskie */
  COMUNIDAD_PL34("COMUNIDAD.PL34"),
  /* Pomorskie */
  COMUNIDAD_PL63("COMUNIDAD.PL63"),
  /* Slaskie */
  COMUNIDAD_PL22("COMUNIDAD.PL22"),
  /* Swietokrzyskie */
  COMUNIDAD_PL33("COMUNIDAD.PL33"),
  /* Warminsko-Mazurskie */
  COMUNIDAD_PL62("COMUNIDAD.PL62"),
  /* Wielkopolskie */
  COMUNIDAD_PL41("COMUNIDAD.PL41"),
  /* Zachodniopomorskie */
  COMUNIDAD_PL42("COMUNIDAD.PL42"),
  /* Alentejo */
  COMUNIDAD_PT18("COMUNIDAD.PT18"),
  /* Algarve */
  COMUNIDAD_PT15("COMUNIDAD.PT15"),
  /* Centro (P) */
  COMUNIDAD_PT16("COMUNIDAD.PT16"),
  /* Extra-Regio */
  COMUNIDAD_PTZZ("COMUNIDAD.PTZZ"),
  /* Lisboa */
  COMUNIDAD_PT17("COMUNIDAD.PT17"),
  /* Norte */
  COMUNIDAD_PT11("COMUNIDAD.PT11"),
  /* Região Autónoma da Madeira */
  COMUNIDAD_PT30("COMUNIDAD.PT30"),
  /* Região Autónoma dos Açores */
  COMUNIDAD_PT20("COMUNIDAD.PT20"),
  /* Bedfordshire and Hertfordshire */
  COMUNIDAD_UKH2("COMUNIDAD.UKH2"),
  /* Berkshire, Buckinghamshire and Oxfordshire */
  COMUNIDAD_UKJ1("COMUNIDAD.UKJ1"),
  /* Cheshire */
  COMUNIDAD_UKD2("COMUNIDAD.UKD2"),
  /* Cornwall and Isles of Scilly */
  COMUNIDAD_UKK3("COMUNIDAD.UKK3"),
  /* Cumbria */
  COMUNIDAD_UKD1("COMUNIDAD.UKD1"),
  /* Derbyshire and Nottinghamshire */
  COMUNIDAD_UKF1("COMUNIDAD.UKF1"),
  /* Devon */
  COMUNIDAD_UKK4("COMUNIDAD.UKK4"),
  /* Dorset and Somerset */
  COMUNIDAD_UKK2("COMUNIDAD.UKK2"),
  /* East Anglia */
  COMUNIDAD_UKH1("COMUNIDAD.UKH1"),
  /* East Riding and North Lincolnshire */
  COMUNIDAD_UKE1("COMUNIDAD.UKE1"),
  /* East Wales */
  COMUNIDAD_UKL2("COMUNIDAD.UKL2"),
  /* Eastern Scotland */
  COMUNIDAD_UKM2("COMUNIDAD.UKM2"),
  /* Essex */
  COMUNIDAD_UKH3("COMUNIDAD.UKH3"),
  /* Extra-Regio */
  COMUNIDAD_UKZZ("COMUNIDAD.UKZZ"),
  /* Gloucestershire, Wiltshire and North Somerset */
  COMUNIDAD_UKK1("COMUNIDAD.UKK1"),
  /* Greater Manchester */
  COMUNIDAD_UKD3("COMUNIDAD.UKD3"),
  /* Hampshire and Isle of Wight */
  COMUNIDAD_UKJ3("COMUNIDAD.UKJ3"),
  /* Herefordshire, Worcestershire and Warwickshire */
  COMUNIDAD_UKG1("COMUNIDAD.UKG1"),
  /* Highlands and Islands */
  COMUNIDAD_UKM4("COMUNIDAD.UKM4"),
  /* Inner London */
  COMUNIDAD_UKI1("COMUNIDAD.UKI1"),
  /* Kent */
  COMUNIDAD_UKJ4("COMUNIDAD.UKJ4"),
  /* Lancashire */
  COMUNIDAD_UKD4("COMUNIDAD.UKD4"),
  /* Leicestershire, Rutland and Northamptonshire */
  COMUNIDAD_UKF2("COMUNIDAD.UKF2"),
  /* Lincolnshire */
  COMUNIDAD_UKF3("COMUNIDAD.UKF3"),
  /* Merseyside */
  COMUNIDAD_UKD5("COMUNIDAD.UKD5"),
  /* North Eastern Scotland */
  COMUNIDAD_UKM1("COMUNIDAD.UKM1"),
  /* North Yorkshire */
  COMUNIDAD_UKE2("COMUNIDAD.UKE2"),
  /* Northern Ireland */
  COMUNIDAD_UKN0("COMUNIDAD.UKN0"),
  /* Northumberland and Tyne and Wear */
  COMUNIDAD_UKC2("COMUNIDAD.UKC2"),
  /* Outer London */
  COMUNIDAD_UKI2("COMUNIDAD.UKI2"),
  /* Shropshire and Staffordshire */
  COMUNIDAD_UKG2("COMUNIDAD.UKG2"),
  /* South Western Scotland */
  COMUNIDAD_UKM3("COMUNIDAD.UKM3"),
  /* South Yorkshire */
  COMUNIDAD_UKE3("COMUNIDAD.UKE3"),
  /* Surrey, East and West Sussex */
  COMUNIDAD_UKJ2("COMUNIDAD.UKJ2"),
  /* Tees Valley and Durham */
  COMUNIDAD_UKC1("COMUNIDAD.UKC1"),
  /* West Midlands */
  COMUNIDAD_UKG3("COMUNIDAD.UKG3"),
  /* West Wales and The Valleys */
  COMUNIDAD_UKL1("COMUNIDAD.UKL1"),
  /* West Yorkshire */
  COMUNIDAD_UKE4("COMUNIDAD.UKE4"),
  /* Extra-Regio */
  COMUNIDAD_CZZZ("COMUNIDAD.CZZZ"),
  /* Jihovychod */
  COMUNIDAD_CZ06("COMUNIDAD.CZ06"),
  /* Jihozapad */
  COMUNIDAD_CZ03("COMUNIDAD.CZ03"),
  /* Moravskoslezsko */
  COMUNIDAD_CZ08("COMUNIDAD.CZ08"),
  /* Praha */
  COMUNIDAD_CZ01("COMUNIDAD.CZ01"),
  /* Severovychod */
  COMUNIDAD_CZ05("COMUNIDAD.CZ05"),
  /* Severozapad */
  COMUNIDAD_CZ04("COMUNIDAD.CZ04"),
  /* Stredni Cechy */
  COMUNIDAD_CZ02("COMUNIDAD.CZ02"),
  /* Stredni Morava */
  COMUNIDAD_CZ07("COMUNIDAD.CZ07"),
  /* Extra-Regio */
  COMUNIDAD_SEZZ("COMUNIDAD.SEZZ"),
  /* Mellersta Norrland */
  COMUNIDAD_SE07("COMUNIDAD.SE07"),
  /* Norra Mellansverige */
  COMUNIDAD_SE06("COMUNIDAD.SE06"),
  /* Östra Mellansverige */
  COMUNIDAD_SE02("COMUNIDAD.SE02"),
  /* Övre Norrland */
  COMUNIDAD_SE08("COMUNIDAD.SE08"),
  /* Småland med öarna */
  COMUNIDAD_SE09("COMUNIDAD.SE09"),
  /* Stockholm */
  COMUNIDAD_SE01("COMUNIDAD.SE01"),
  /* Sydsverige */
  COMUNIDAD_SE04("COMUNIDAD.SE04"),
  /* Västsverige */
  COMUNIDAD_SE0A("COMUNIDAD.SE0A"),
  /* Exposición */
  TIPO_OBRA_EXPOSICION("TIPO_OBRA.EXPOSICION"),
  /* Diseño */
  TIPO_OBRA_DISENO("TIPO_OBRA.DISENO"),
  /* Otros */
  TIPO_OBRA_OTROS("TIPO_OBRA.OTROS"),
  /* Proyecto Final de Carrera */
  E030_040_000_010_055("030.040.000.010.055"),
  /* Tesina */
  E030_040_000_010_066("030.040.000.010.066"),
  /* Tesis Doctoral */
  E030_040_000_010_067("030.040.000.010.067"),
  /* Trabajo conducente a obtención de DEA */
  E030_040_000_010_071("030.040.000.010.071"),
  /* Otros */
  E030_040_000_010_OTHERS("030.040.000.010.OTHERS"),
  /* Comisario/a de exposición */
  E060_020_030_110_230("060.020.030.110.230"),
  /* Organizador */
  E060_020_030_110_650("060.020.030.110.650"),
  /* Presidente */
  E060_020_030_110_740("060.020.030.110.740"),
  /* Secretario/a */
  E060_020_030_110_830("060.020.030.110.830"),
  /* Otros */
  E060_020_030_110_OTHERS("060.020.030.110.OTHERS"),
  /* Organizativo - Comité científico y organizador */
  E060_020_030_110_ORGANIZATIVO_COMITE("060.020.030.110.ORGANIZATIVO_COMITE"),
  /* Organizativo - Presidente comité */
  E060_020_030_110_ORGANIZATIVO_PRESIDENTE_COMITE("060.020.030.110.ORGANIZATIVO_PRESIDENTE_COMITE"),
  /* Organizativo - Otros */
  E060_020_030_110_ORGANIZATIVO_OTROS("060.020.030.110.ORGANIZATIVO_OTROS");

  private String internValue;

  private TablaMaestraCVN(String internValue) {
    this.internValue = internValue;
  }

  public String getInternValue() {
    return internValue;
  }

  public static TablaMaestraCVN getByInternValue(String internValue) {
    try {
      return Stream.of(TablaMaestraCVN.values())
          .filter(campoValue -> campoValue.getInternValue().equalsIgnoreCase(internValue))
          .findFirst()
          .orElseThrow(() -> new CampoCVNNotFoundException(internValue));

    } catch (Exception e) {
      throw new CampoCVNNotFoundException(internValue);
    }
  }
}

// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import { NgxLoggerLevel, LoggerConfig } from 'ngx-logger';
import { SgiAuthMode, SgiAuthConfig } from '@sgi/framework/auth';
import { version } from '../../package.json';

export const environment = {
  production: false,
  serviceServers: {
    eti: '/api/eti',
    sgp: '/api/sgp',
    csp: '/api/csp',
    usr: '/api/usr',
    sgdoc: '/api/sgdoc',
    sge: '/api/sge',
    sgemp: '/api/sgemp',
    sgepii: '/api/sgepii',
    sgo: '/api/sgo',
    pii: '/api/pii',
    rel: '/api/rel',
    rep: '/api/rep',
    prc: '/api/prc',
    cnf: '/api/cnf',
    com: '/api/com',
    tp: '/api/tp',
    eer: '/api/eer'
  },
  loggerConfig: {
    enableSourceMaps: true, // <-- THIS IS REQUIRED, to make "line-numbers" work in SourceMap Object defition (without evalSourceMap)
    level: NgxLoggerLevel.DEBUG
  } as LoggerConfig,
  authConfig: {
    mode: SgiAuthMode.Keycloak,
    ssoRealm: 'sgi',
    ssoClientId: 'front',
    ssoUrl: 'http://sgi-auth:8080/auth',
    // InMemory  auth config
    inMemoryConfig: {
      userRefId: '',
      authorities: [],
      isInvestigador: false
    },
    protectedResources: [
      /\/api\/eti(?!\/config\/time-zone$).*/i,
      /\/api\/csp(?!\/public\/|\/config\/time-zone$).*/i,
      /\/api\/usr(?!\/public\/|\/config\/time-zone$).*/i,
      /\/api\/sgp(?!\/public\/).*/i,
      /\/api\/sgemp(?!\/public\/).*/i,
      /\/api\/sgepii.*/i,
      /\/api\/sgo(?!\/public\/).*/i,
      /\/api\/sge\/(?!public\/).*/i,
      /\/api\/pii(?!\/config\/time-zone$).*/i,
      /\/api\/sgdoc(?!\/public\/).*/i,
      /\/api\/rel(?!\/config\/time-zone$).*/i,
      /\/api\/rep(?!\/config\/time-zone$).*/i,
      /\/api\/prc(?!\/config\/time-zone$).*/i,
      /\/api\/cnf(?!\/config\/time-zone$).*/i,
      /\/api\/com.*/i,
      /\/api\/tp.*/i,
      /\/api\/eer(?!\/config\/time-zone$).*/i
    ]
  } as SgiAuthConfig,
  version,
  defaultTimeZone: 'Europe/Madrid'
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.

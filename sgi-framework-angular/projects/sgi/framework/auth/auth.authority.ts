
/**
 * Checks if the user authorities contains an authority
 *
 * @param userAuthorities User authorities to scan
 * @param authority Authority to check
 */
export function hasAuthority(userAuthorities: string[], authority: string): boolean {
  return userAuthorities.find((auth) => auth === authority) ? true : false;
}

/**
 * Checks if the user authorities contains any of the provided authorities
 *
 * @param userAuthorities User authorities to scan
 * @param authorities Authorities to check
 */
export function hasAnyAuthority(userAuthorities: string[], authorities: string[]): boolean {
  return userAuthorities.find((auth) => authorities.find((au) => auth === au)) ? true : false;
}

/**
 * Checks if the user authorities contains an authority.
 * OU prefix in the user authorities is ignored, so the authority to find must not containt it.
 *
 * @param userAuthorities User authorities to scan
 * @param authority Authority to check
 */
export function hasAuthorityForAnyUO(userAuthorities: string[], authority: string): boolean {
  const regex = new RegExp('^' + authority + '($|_.+$)');
  return userAuthorities.find((auth) => auth.match(regex)) ? true : false;
}

/**
 * Checks if the user authorities contains any of the provided authorities.
 * OU prefix in the user authorities is ignored, so the authorities to find must not containt it.
 *
 * @param userAuthorities User authorities to scan
 * @param authorities Authorities to check
 */
export function hasAnyAuthorityForAnyUO(userAuthorities: string[], authorities: string[]): boolean {
  return userAuthorities.find((auth) => authorities.find((au) => auth.match(new RegExp('^' + au + '($|_.+$)')))) ? true : false;
}

/**
 * Checks if the user modules contains an module
 *
 * @param userModules User modules to scan
 * @param module Module code to check
 */
export function hasModuleAccess(userModules: string[], module: string): boolean {
  return userModules.find((mod) => mod === module) ? true : false;
}

/**
 * Checks if the user modules contains any of the provided modules
 *
 * @param userModules User modules to scan
 * @param modules Module names to check
 */
export function hasAnyModuleAccess(userModules: string[], modules: string[]): boolean {
  return userModules.find((module) => modules.find((mod) => module === mod)) ? true : false;
}

/**
 * Extract the user modules that user has access based on their authorities
 *
 * @param userAuthorities The user authorities to scan
 */
export function extractModuleAccess(userAuthorities: string[]): string[] {
  const modules: string[] = [];
  userAuthorities.forEach((authority) => {
    // TODO: Remove support for endWith -INV when full permission migration
    const virtualModuleMatch = /^[A-Z]+-[A-Z]+(-(?<module>[A-Z]+)-)?[A-Z]+($|_.+$)|^.+-INV$/gm.exec(authority);
    if (virtualModuleMatch?.length) {
      if ((virtualModuleMatch.groups.module === 'INV' || authority.endsWith('-INV')) && modules.indexOf('INV') < 0) {
        modules.push('INV');
      }
    }
    else {
      const match = authority.match(/^([^-]+)/gm);
      if (match.length === 1 && modules.indexOf(match[0]) < 0) {
        modules.push(match[0]);
      }
    }
  });
  return modules;
}

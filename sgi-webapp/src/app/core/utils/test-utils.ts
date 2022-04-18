import { MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute, Data, ParamMap } from '@angular/router';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateTestingModule } from 'ngx-translate-testing';
import { Subject } from 'rxjs';

/**
 * A Utility Class for testing.
 */
export default class TestUtils {
  /** Gets an array of object own property names (including functions but excluding constructors) */
  static getOwnPropertyNames(obj: any) {
    const props = [];
    const objProperties = Object.getOwnPropertyNames(obj);
    for (const property of objProperties) {
      if (property !== 'constructor') {
        props.push(property);
      }
    }
    return props;
  }

  /** Gets an array of object own method names (excluding constructors) */
  static getOwnMethodNames(obj: any) {
    const meths = [];
    const objProperties = Object.getOwnPropertyNames(obj);
    for (const property of objProperties) {
      if (property !== 'constructor' && typeof obj[property] === 'function') {
        meths.push(property);
      }
    }
    return meths;
  }

  /** Gets an array of object hierarchy property names (including functions but excluding constructors) */
  static getPropertyNames(obj: any) {
    const props = [];
    for (; obj != null; obj = Object.getPrototypeOf(obj)) {
      const objProperties = Object.getOwnPropertyNames(obj);
      for (const property of objProperties) {
        if (property !== 'constructor' && props.indexOf(property) === -1) {
          props.push(property);
        }
      }
    }
    return props;
  }

  /** Gets an array of object hierarchy method names (excluding constructors) */
  static getMethodNames(obj: any) {
    const meths = [];
    for (; obj != null; obj = Object.getPrototypeOf(obj)) {
      const objProperties = Object.getOwnPropertyNames(obj);
      for (const property of objProperties) {
        if (
          property !== 'constructor' &&
          meths.indexOf(property) === -1 &&
          typeof obj[property] === 'function'
        ) {
          meths.push(property);
        }
      }
    }
    return meths;
  }

  /**
   * Devuelve módulo de test para cargar la internacionalización
   */
  static getIdiomas(): TranslateTestingModule {
    return TranslateTestingModule.withTranslations({
      es: require('src/assets/i18n/es.json'),
    }).withDefaultLanguage('es');
  }

  /**
   * Devuelve el mock del snackBarService
   */
  static getSnackBarServiceSpy(): jasmine.SpyObj<SnackBarService> {
    return jasmine.createSpyObj(
      SnackBarService.name,
      TestUtils.getOwnMethodNames(SnackBarService.prototype)
    );
  }

  static buildActivatedRouteMock(paramId: string, routeData: Data, parentData?: Data): ActivatedRoute {
    const paramMapSpy: jasmine.SpyObj<ParamMap> = jasmine.createSpyObj('paramMap', ['get']);
    paramMapSpy.get.and.returnValue(paramId);
    const routeMock: ActivatedRoute = {
      snapshot: {
        paramMap: paramMapSpy as ParamMap,
        data: routeData,
        parent: {
          data: parentData
        }
      }
    } as ActivatedRoute;
    return routeMock;
  }

  static buildDialogActionMatDialogRef(): MatDialogRef<DialogActionComponent<any>, any> {
    return {
      close: jasmine.createSpy('close'),
      addPanelClass: jasmine.createSpy('addPanelClass'),
      componentInstance: {
        problems$: new Subject<any>()
      } as DialogActionComponent<any>
    } as unknown as MatDialogRef<DialogActionComponent<any>, any>;
  }
}

import { DecimalPipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AliasEnumeradoService } from '@core/services/prc/alias-enumerado/alias-enumerado.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { aliasEnumeradosFactory, ALIAS_ENUMERADOS } from '../../alias-enumerado.token';
import { CvnPipesModule } from '../../pipe/cvn-pipes.module';

import { CvnFieldComponent } from './cvn-field.component';

describe('CvnFieldComponent', () => {
  let component: CvnFieldComponent;
  let fixture: ComponentFixture<CvnFieldComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        MaterialDesignModule,
        TestUtils.getIdiomas(),
        CvnPipesModule
      ],
      providers: [
        DecimalPipe,
        {
          provide: ALIAS_ENUMERADOS,
          useFactory: aliasEnumeradosFactory,
          deps: [AliasEnumeradoService]
        }
      ],
      declarations: [CvnFieldComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CvnFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

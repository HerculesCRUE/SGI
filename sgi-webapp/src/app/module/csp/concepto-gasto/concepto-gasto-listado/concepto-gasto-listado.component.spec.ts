import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ConceptoGastoListadoComponent } from './concepto-gasto-listado.component';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { MaterialDesignModule } from '@material/material-design.module';
import TestUtils from '@core/utils/test-utils';
import { SnackBarService } from '@core/services/snack-bar.service';
import { ConceptoGastoService } from '@core/services/csp/concepto-gasto.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SgiAuthService } from '@sgi/framework/auth';

describe('ConceptoGastoListadoComponent', () => {
  let component: ConceptoGastoListadoComponent;
  let fixture: ComponentFixture<ConceptoGastoListadoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        LoggerTestingModule,
        RouterTestingModule,
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        TestUtils.getIdiomas(),
      ],
      declarations: [ConceptoGastoListadoComponent],
      providers: [
        ConceptoGastoService,
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConceptoGastoListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ResultadoInformePatentabilidadListadoComponent } from './resultado-informe-patentabilidad-listado.component';


describe('ResultadoInformePatentabilidadListadoComponent', () => {
  let component: ResultadoInformePatentabilidadListadoComponent;
  let fixture: ComponentFixture<ResultadoInformePatentabilidadListadoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ResultadoInformePatentabilidadListadoComponent],
      imports: [
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        BrowserAnimationsModule,
        TestUtils.getIdiomas(),
      ],
      providers: [
        SgiAuthService,
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ResultadoInformePatentabilidadListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

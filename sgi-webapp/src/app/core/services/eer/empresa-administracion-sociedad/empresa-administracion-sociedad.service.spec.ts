import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EmpresaAdministracionSociedadService } from './empresa-administracion-sociedad.service';

describe('EmpresaAdministracionSociedadService', () => {
  let service: EmpresaAdministracionSociedadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(EmpresaAdministracionSociedadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

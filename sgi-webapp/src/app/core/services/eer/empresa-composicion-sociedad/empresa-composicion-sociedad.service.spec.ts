import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EmpresaComposicionSociedadService } from './empresa-composicion-sociedad.service';

describe('EmpresaComposicionSociedadService', () => {
  let service: EmpresaComposicionSociedadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(EmpresaComposicionSociedadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

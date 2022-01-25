import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { CertificadoAutorizacionService } from './certificado-autorizacion.service';

describe('CertificadoAutorizacionService', () => {
  let service: CertificadoAutorizacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(CertificadoAutorizacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

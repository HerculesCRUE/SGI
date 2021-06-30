import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { DocumentoRequeridoSolicitudService } from './documento-requerido-solicitud.service';

describe('DocumentoRequeridoSolicitudService', () => {
  let service: DocumentoRequeridoSolicitudService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(DocumentoRequeridoSolicitudService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

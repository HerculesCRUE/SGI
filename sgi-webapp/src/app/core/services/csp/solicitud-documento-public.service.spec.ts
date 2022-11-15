import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudDocumentoPublicService } from './solicitud-documento-public.service';

describe('SolicitudDocumentoPublicService', () => {
  let service: SolicitudDocumentoPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudDocumentoPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

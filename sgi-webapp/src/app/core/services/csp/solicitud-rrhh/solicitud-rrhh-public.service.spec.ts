import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudRrhhPublicService } from './solicitud-rrhh-public.service';

describe('SolicitudRrhhPublicService', () => {
  let service: SolicitudRrhhPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudRrhhPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

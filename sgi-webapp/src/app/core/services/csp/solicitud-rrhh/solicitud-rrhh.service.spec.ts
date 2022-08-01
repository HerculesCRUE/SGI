import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudRrhhService } from './solicitud-rrhh.service';

describe('SolicitudRrhhService', () => {
  let service: SolicitudRrhhService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudRrhhService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

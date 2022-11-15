
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudModalidadPublicService } from './solicitud-modalidad-public.service';

describe('SolicitudModalidadPublicService', () => {
  let service: SolicitudModalidadPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudModalidadPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

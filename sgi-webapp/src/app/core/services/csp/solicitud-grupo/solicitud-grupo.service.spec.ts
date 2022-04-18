import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { SolicitudGrupoService } from './solicitud-grupo.service';

describe('SolcitudGrupoService', () => {
  let service: SolicitudGrupoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudGrupoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

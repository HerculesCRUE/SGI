import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { RepartoEquipoInventorService } from './reparto-equipo-inventor.service';

describe('RepartoEquipoInventorService', () => {
  let service: RepartoEquipoInventorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(RepartoEquipoInventorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

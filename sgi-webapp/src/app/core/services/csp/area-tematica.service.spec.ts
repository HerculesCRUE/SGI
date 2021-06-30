import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { AreaTematicaService } from './area-tematica.service';

describe('AreaTematicaService', () => {
  let service: AreaTematicaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(AreaTematicaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

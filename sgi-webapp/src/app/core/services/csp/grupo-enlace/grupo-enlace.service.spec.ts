import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { GrupoEnlaceService } from './grupo-enlace.service';

describe('GrupoEnlaceService', () => {
  let service: GrupoEnlaceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(GrupoEnlaceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

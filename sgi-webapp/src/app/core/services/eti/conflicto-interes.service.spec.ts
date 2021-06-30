import { TestBed } from '@angular/core/testing';

import { ConflictoInteresService } from './conflicto-interes.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';

describe('ConflictoInteresService', () => {
  let service: ConflictoInteresService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ConflictoInteresService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

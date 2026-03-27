import { Component, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { SearchService, SearchFilters } from '../../data-access/search.service';
import { SearchResultCardComponent } from '../../ui/search-result-card/search-result-card.component';
import { PaginationComponent } from '../../../../shared/ui/pagination/pagination.component';
import { SearchResultResponse } from '../../../../shared/models/api.models';

@Component({
  selector: 'app-search-page',
  imports: [FormsModule, SearchResultCardComponent, PaginationComponent],
  templateUrl: './search-page.component.html',
  styleUrl: './search-page.component.css',
})
export class SearchPageComponent {
  private readonly searchService = inject(SearchService);

  query = '';
  minPrice: number | null = null;
  maxPrice: number | null = null;
  fromDate = '';

  readonly results = signal<SearchResultResponse[]>([]);
  readonly loading = signal(false);
  readonly page = signal(0);
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);
  readonly hasSearched = signal(false);

  onSearch(page = 0): void {
    this.loading.set(true);
    this.hasSearched.set(true);

    const filters: SearchFilters = {
      q: this.query || undefined,
      minPrice: this.minPrice,
      maxPrice: this.maxPrice,
      fromDate: this.fromDate || null,
      page,
      size: 12,
    };

    firstValueFrom(this.searchService.search(filters))
      .then((res) => {
        this.results.set(res.content);
        this.page.set(res.page);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
      })
      .catch(() => {
        this.results.set([]);
      })
      .finally(() => {
        this.loading.set(false);
      });
  }

  onPageChange(page: number): void {
    this.onSearch(page);
  }

  onClear(): void {
    this.query = '';
    this.minPrice = null;
    this.maxPrice = null;
    this.fromDate = '';
    this.results.set([]);
    this.hasSearched.set(false);
    this.totalElements.set(0);
  }
}

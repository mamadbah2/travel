import { computed, inject } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { TravelResponse } from '../../../shared/models/api.models';
import { ManagerService } from './manager.service';

interface ManagerState {
  travels: TravelResponse[];
  pagination: {
    page: number;
    size: number;
    totalPages: number;
    totalElements: number;
    first: boolean;
    last: boolean;
  };
  loading: boolean;
  error: string | null;
}

const initialState: ManagerState = {
  travels: [],
  pagination: { page: 0, size: 20, totalPages: 0, totalElements: 0, first: true, last: true },
  loading: false,
  error: null,
};

export const ManagerStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed((store) => ({
    isEmpty: computed(() => !store.loading() && store.travels().length === 0),
    draftCount: computed(() => store.travels().filter((t) => t.status === 'DRAFT').length),
    publishedCount: computed(() => store.travels().filter((t) => t.status === 'PUBLISHED').length),
  })),
  withMethods((store, managerService = inject(ManagerService)) => ({
    loadTravels: rxMethod<number>(
      pipe(
        tap((page) =>
          patchState(store, {
            loading: true,
            error: null,
            pagination: { ...store.pagination(), page },
          }),
        ),
        switchMap((page) => managerService.getMyTravels(page, store.pagination().size)),
        tap({
          next: (response) => {
            patchState(store, {
              travels: response.content,
              pagination: {
                page: response.page,
                size: response.size,
                totalPages: response.totalPages,
                totalElements: response.totalElements,
                first: response.first,
                last: response.last,
              },
              loading: false,
            });
          },
          error: (err: any) => {
            patchState(store, {
              loading: false,
              error: err?.error?.message || 'Failed to load travels',
            });
          },
        }),
      ),
    ),

    removeTravel(id: string): void {
      patchState(store, {
        travels: store.travels().filter((t) => t.id !== id),
      });
    },

    updateTravelInList(updated: TravelResponse): void {
      patchState(store, {
        travels: store.travels().map((t) => (t.id === updated.id ? updated : t)),
      });
    },
  })),
);

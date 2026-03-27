import { computed, inject } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { SubscriptionResponse } from '../../../shared/models/api.models';
import { SubscriptionService } from './subscription.service';

interface SubscriptionState {
  subscriptions: SubscriptionResponse[];
  selectedSubscription: SubscriptionResponse | null;
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

const initialState: SubscriptionState = {
  subscriptions: [],
  selectedSubscription: null,
  pagination: { page: 0, size: 20, totalPages: 0, totalElements: 0, first: true, last: true },
  loading: false,
  error: null,
};

export const SubscriptionStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed((store) => ({
    isEmpty: computed(() => !store.loading() && store.subscriptions().length === 0),
  })),
  withMethods((store, subscriptionService = inject(SubscriptionService)) => ({
    loadSubscriptions: rxMethod<number>(
      pipe(
        tap((page) =>
          patchState(store, {
            loading: true,
            error: null,
            pagination: { ...store.pagination(), page },
          }),
        ),
        switchMap((page) => subscriptionService.getSubscriptions(page, store.pagination().size)),
        tap({
          next: (response) => {
            patchState(store, {
              subscriptions: response.content,
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
              error: err?.error?.message || 'Failed to load subscriptions',
            });
          },
        }),
      ),
    ),

    loadSubscription: rxMethod<string>(
      pipe(
        tap(() => patchState(store, { loading: true, error: null })),
        switchMap((id) => subscriptionService.getSubscriptionById(id)),
        tap({
          next: (sub) => patchState(store, { selectedSubscription: sub, loading: false }),
          error: (err: any) => {
            patchState(store, {
              loading: false,
              error: err?.error?.message || 'Failed to load subscription',
            });
          },
        }),
      ),
    ),

    updateSelectedStatus(status: SubscriptionResponse['status']): void {
      const current = store.selectedSubscription();
      if (current) {
        patchState(store, {
          selectedSubscription: { ...current, status },
        });
      }
    },
  })),
);

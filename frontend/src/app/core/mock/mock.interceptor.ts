import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { delay, of } from 'rxjs';
import { MockData, paginate } from './mock-data';
import { environment } from '../../../environments/environment';

const BASE = environment.apiBaseUrl;

function ok(body: unknown) {
  return of(new HttpResponse({ status: 200, body })).pipe(delay(300));
}

function created(body: unknown) {
  return of(new HttpResponse({ status: 201, body })).pipe(delay(300));
}

function msg(message: string) {
  return ok({ message });
}

function getParam(url: string, name: string): string | null {
  try {
    const u = new URL(url);
    return u.searchParams.get(name);
  } catch {
    return null;
  }
}

function pageNum(url: string): number {
  return parseInt(getParam(url, 'page') || '0', 10);
}

function pageSize(url: string): number {
  return parseInt(getParam(url, 'size') || '12', 10);
}

/** Extract last path segment (ID) from URL */
function lastSegment(url: string): string {
  const path = new URL(url).pathname;
  const segments = path.split('/').filter(Boolean);
  return segments[segments.length - 1];
}

/** Build a fake JWT for mock auth */
function fakeJwt(user: typeof MockData.defaultUser): string {
  const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
  const payload = btoa(
    JSON.stringify({
      sub: user.id,
      email: user.email,
      role: user.role,
      exp: Math.floor(Date.now() / 1000) + 3600,
    }),
  );
  return `${header}.${payload}.mock-signature`;
}

function authResponse(user: typeof MockData.defaultUser) {
  return {
    accessToken: fakeJwt(user),
    refreshToken: 'mock-refresh-token',
    tokenType: 'Bearer',
    expiresIn: 3600,
    user,
  };
}

export const mockInterceptor: HttpInterceptorFn = (req, next) => {
  const url = req.url;
  const method = req.method;

  // Only intercept requests to our API
  if (!url.startsWith(BASE)) {
    return next(req);
  }

  const path = url.replace(BASE, '').split('?')[0];

  // ===== AUTH =====

  if (path === '/api/v1/auth/login' && method === 'POST') {
    const body = req.body as any;
    // Determine role from email for testing
    let user = MockData.defaultUser;
    if (body?.email?.includes('admin')) user = MockData.adminUser;
    else if (body?.email?.includes('marco') || body?.email?.includes('manager')) user = MockData.managerUser;
    return ok(authResponse(user));
  }

  if (path === '/api/v1/auth/register' && method === 'POST') {
    const body = req.body as any;
    const newUser = {
      ...MockData.defaultUser,
      id: 'u-new-' + Date.now(),
      email: body?.email || 'new@travelapp.com',
      firstName: body?.firstName || 'New',
      lastName: body?.lastName || 'User',
      role: body?.role || 'TRAVELER' as const,
      status: 'ACTIVE' as const,
      createdAt: new Date().toISOString(),
    };
    return created(authResponse(newUser));
  }

  if (path === '/api/v1/auth/refresh' && method === 'POST') {
    return ok(authResponse(MockData.defaultUser));
  }

  if (path === '/api/v1/auth/logout' && method === 'POST') {
    return msg('Logged out successfully');
  }

  // ===== USERS =====

  if (path === '/api/v1/users/me' && method === 'GET') {
    return ok(MockData.defaultUser);
  }

  if (path === '/api/v1/users/me/change-password' && method === 'PUT') {
    return msg('Password changed successfully');
  }

  if (path === '/api/v1/users/search' && method === 'GET') {
    const query = (getParam(url, 'query') || getParam(url, 'search') || '').toLowerCase();
    const filtered = MockData.users.filter(
      (u) =>
        u.firstName.toLowerCase().includes(query) ||
        u.lastName.toLowerCase().includes(query) ||
        u.email.toLowerCase().includes(query),
    );
    return ok(paginate(filtered, pageNum(url), pageSize(url)));
  }

  if (path === '/api/v1/users' && method === 'GET') {
    return ok(paginate(MockData.users, pageNum(url), pageSize(url)));
  }

  // /api/v1/users/role/{role}
  const roleMatch = path.match(/^\/api\/v1\/users\/role\/(\w+)$/);
  if (roleMatch && method === 'GET') {
    const role = roleMatch[1];
    const filtered = MockData.users.filter((u) => u.role === role);
    return ok(paginate(filtered, pageNum(url), pageSize(url)));
  }

  // /api/v1/users/status/{status}
  const statusMatch = path.match(/^\/api\/v1\/users\/status\/(\w+)$/);
  if (statusMatch && method === 'GET') {
    const status = statusMatch[1];
    const filtered = MockData.users.filter((u) => u.status === status);
    return ok(paginate(filtered, pageNum(url), pageSize(url)));
  }

  // /api/v1/users/{id}/ban
  if (path.match(/^\/api\/v1\/users\/[\w-]+\/ban$/) && method === 'PUT') {
    return msg('User banned');
  }

  // /api/v1/users/{id}/unban
  if (path.match(/^\/api\/v1\/users\/[\w-]+\/unban$/) && method === 'PUT') {
    return msg('User unbanned');
  }

  // /api/v1/users/{id}/performance-score
  if (path.match(/^\/api\/v1\/users\/[\w-]+\/performance-score$/) && method === 'PUT') {
    return msg('Performance score updated');
  }

  // /api/v1/users/{id}
  const userIdMatch = path.match(/^\/api\/v1\/users\/([\w-]+)$/);
  if (userIdMatch) {
    if (method === 'GET') {
      const user = MockData.users.find((u) => u.id === userIdMatch[1]);
      return user ? ok(user) : ok(MockData.defaultUser);
    }
    if (method === 'PUT') {
      const user = MockData.users.find((u) => u.id === userIdMatch[1]) || MockData.defaultUser;
      return ok({ ...user, ...(req.body as any) });
    }
    if (method === 'DELETE') {
      return msg('User deleted');
    }
  }

  // ===== TRAVELS =====

  if (path === '/api/v1/travels/search' && method === 'GET') {
    const query = (getParam(url, 'search') || getParam(url, 'query') || '').toLowerCase();
    const filtered = MockData.publishedTravels().filter(
      (t) =>
        t.title.toLowerCase().includes(query) ||
        (t.description || '').toLowerCase().includes(query) ||
        t.destinations.some((d) => d.name.toLowerCase().includes(query) || d.country.toLowerCase().includes(query)),
    );
    return ok(paginate(filtered, pageNum(url), pageSize(url)));
  }

  if (path === '/api/v1/travels/manager/me' && method === 'GET') {
    // Return travels for the manager user
    const managerTravels = MockData.managerTravels(MockData.managerUser.id);
    return ok(paginate(managerTravels, pageNum(url), pageSize(url)));
  }

  if (path === '/api/v1/travels' && method === 'GET') {
    return ok(paginate(MockData.publishedTravels(), pageNum(url), pageSize(url)));
  }

  if (path === '/api/v1/travels' && method === 'POST') {
    const body = req.body as any;
    const newTravel = {
      id: 't-new-' + Date.now(),
      managerId: MockData.managerUser.id,
      title: body?.title || 'New Travel',
      description: body?.description || null,
      startDate: body?.startDate || '2026-06-01',
      endDate: body?.endDate || '2026-06-10',
      duration: 9,
      price: body?.price || 0,
      maxCapacity: body?.maxCapacity || 20,
      currentBookings: 0,
      availableSpots: body?.maxCapacity || 20,
      status: 'DRAFT' as const,
      accommodationType: body?.accommodationType || null,
      accommodationName: body?.accommodationName || null,
      transportationType: body?.transportationType || null,
      transportationDetails: body?.transportationDetails || null,
      destinations: (body?.destinations || []).map((d: any, i: number) => ({ ...d, id: `d-new-${i}` })),
      activities: (body?.activities || []).map((a: any, i: number) => ({ ...a, id: `a-new-${i}` })),
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    return created(newTravel);
  }

  // /api/v1/travels/{id}/publish
  if (path.match(/^\/api\/v1\/travels\/[\w-]+\/publish$/) && method === 'PUT') {
    const id = path.split('/')[4];
    const travel = MockData.travels.find((t) => t.id === id);
    return ok(travel ? { ...travel, status: 'PUBLISHED' } : msg('Published'));
  }

  // /api/v1/travels/{id}/cancel
  if (path.match(/^\/api\/v1\/travels\/[\w-]+\/cancel$/) && method === 'PUT') {
    const id = path.split('/')[4];
    const travel = MockData.travels.find((t) => t.id === id);
    return ok(travel ? { ...travel, status: 'CANCELLED' } : msg('Cancelled'));
  }

  // /api/v1/travels/{id}/subscribers/{subId}
  if (path.match(/^\/api\/v1\/travels\/[\w-]+\/subscribers\/[\w-]+$/) && method === 'DELETE') {
    return msg('Subscriber removed');
  }

  // /api/v1/travels/{id}/subscribers
  if (path.match(/^\/api\/v1\/travels\/[\w-]+\/subscribers$/) && method === 'GET') {
    const travelId = path.split('/')[4];
    const subs = MockData.travelSubscribers(travelId);
    return ok(paginate(subs, pageNum(url), pageSize(url)));
  }

  // /api/v1/travels/{id}
  const travelIdMatch = path.match(/^\/api\/v1\/travels\/([\w-]+)$/);
  if (travelIdMatch) {
    if (method === 'GET') {
      const travel = MockData.travels.find((t) => t.id === travelIdMatch[1]);
      return ok(travel || MockData.travels[0]);
    }
    if (method === 'PUT') {
      const travel = MockData.travels.find((t) => t.id === travelIdMatch[1]) || MockData.travels[0];
      return ok({ ...travel, ...(req.body as any), updatedAt: new Date().toISOString() });
    }
    if (method === 'DELETE') {
      return msg('Travel deleted');
    }
  }

  // ===== SUBSCRIPTIONS =====

  if (path === '/api/v1/subscriptions' && method === 'GET') {
    const subs = MockData.travelerSubscriptions(MockData.defaultUser.id);
    return ok(paginate(subs, pageNum(url), pageSize(url)));
  }

  // /api/v1/subscriptions/travel/{travelId}
  const subCreateMatch = path.match(/^\/api\/v1\/subscriptions\/travel\/([\w-]+)$/);
  if (subCreateMatch && method === 'POST') {
    const travelId = subCreateMatch[1];
    const travel = MockData.travels.find((t) => t.id === travelId);
    const newSub: any = {
      id: 's-new-' + Date.now(),
      travelerId: MockData.defaultUser.id,
      travelId,
      travelTitle: travel?.title || 'Travel',
      status: 'PENDING_PAYMENT',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    return created(newSub);
  }

  // /api/v1/subscriptions/{id}/cancel
  if (path.match(/^\/api\/v1\/subscriptions\/[\w-]+\/cancel$/) && method === 'PUT') {
    return msg('Subscription cancelled');
  }

  // /api/v1/subscriptions/{id}
  const subIdMatch = path.match(/^\/api\/v1\/subscriptions\/([\w-]+)$/);
  if (subIdMatch && method === 'GET') {
    const sub = MockData.subscriptions.find((s) => s.id === subIdMatch[1]);
    return ok(sub || MockData.subscriptions[0]);
  }

  // ===== PAYMENTS =====

  if (path === '/api/v1/payments' && method === 'GET') {
    return ok(paginate(MockData.payments, pageNum(url), pageSize(url)));
  }

  // /api/v1/payments/subscription/{id}
  const paySubMatch = path.match(/^\/api\/v1\/payments\/subscription\/([\w-]+)$/);
  if (paySubMatch && method === 'GET') {
    const filtered = MockData.payments.filter((p) => p.subscriptionId === paySubMatch[1]);
    return ok(paginate(filtered, pageNum(url), pageSize(url)));
  }

  // /api/v1/payments/traveler/{id}
  const payTravelerMatch = path.match(/^\/api\/v1\/payments\/traveler\/([\w-]+)$/);
  if (payTravelerMatch && method === 'GET') {
    const filtered = MockData.payments.filter((p) => p.travelerId === payTravelerMatch[1]);
    return ok(paginate(filtered, pageNum(url), pageSize(url)));
  }

  // /api/v1/payments/travel/{id}
  const payTravelMatch = path.match(/^\/api\/v1\/payments\/travel\/([\w-]+)$/);
  if (payTravelMatch && method === 'GET') {
    const filtered = MockData.payments.filter((p) => p.travelId === payTravelMatch[1]);
    return ok(paginate(filtered, pageNum(url), pageSize(url)));
  }

  // /api/v1/payments/{id}
  const payIdMatch = path.match(/^\/api\/v1\/payments\/([\w-]+)$/);
  if (payIdMatch && method === 'GET') {
    const payment = MockData.payments.find((p) => p.id === payIdMatch[1]);
    return ok(payment || MockData.payments[0]);
  }

  // ===== NOTIFICATIONS =====

  if (path === '/api/v1/notifications' && method === 'GET') {
    return ok(paginate(MockData.notifications, pageNum(url), pageSize(url)));
  }

  // /api/v1/notifications/traveler/{id}
  const notifTravelerMatch = path.match(/^\/api\/v1\/notifications\/traveler\/([\w-]+)$/);
  if (notifTravelerMatch && method === 'GET') {
    const filtered = MockData.notifications.filter((n) => n.travelerId === notifTravelerMatch[1]);
    return ok(paginate(filtered, pageNum(url), pageSize(url)));
  }

  // /api/v1/notifications/travel/{id}
  const notifTravelMatch = path.match(/^\/api\/v1\/notifications\/travel\/([\w-]+)$/);
  if (notifTravelMatch && method === 'GET') {
    const filtered = MockData.notifications.filter((n) => n.travelId === notifTravelMatch[1]);
    return ok(paginate(filtered, pageNum(url), pageSize(url)));
  }

  // /api/v1/notifications/subscription/{id}
  const notifSubMatch = path.match(/^\/api\/v1\/notifications\/subscription\/([\w-]+)$/);
  if (notifSubMatch && method === 'GET') {
    const filtered = MockData.notifications.filter((n) => n.subscriptionId === notifSubMatch[1]);
    return ok(paginate(filtered, pageNum(url), pageSize(url)));
  }

  // /api/v1/notifications/{id}
  const notifIdMatch = path.match(/^\/api\/v1\/notifications\/([\w-]+)$/);
  if (notifIdMatch && method === 'GET') {
    const notif = MockData.notifications.find((n) => n.id === notifIdMatch[1]);
    return ok(notif || MockData.notifications[0]);
  }

  // ===== SEARCH (Elasticsearch) =====

  if (path === '/api/v1/search' && method === 'GET') {
    const query = (getParam(url, 'query') || getParam(url, 'q') || '').toLowerCase();
    const minPrice = parseFloat(getParam(url, 'minPrice') || '0');
    const maxPrice = parseFloat(getParam(url, 'maxPrice') || '999999');
    const fromDate = getParam(url, 'fromDate');

    let results = MockData.publishedTravels();
    if (query) {
      results = results.filter(
        (t) =>
          t.title.toLowerCase().includes(query) ||
          (t.description || '').toLowerCase().includes(query) ||
          t.destinations.some((d) => d.name.toLowerCase().includes(query) || d.country.toLowerCase().includes(query)),
      );
    }
    results = results.filter((t) => t.price >= minPrice && t.price <= maxPrice);
    if (fromDate) {
      results = results.filter((t) => t.startDate >= fromDate);
    }

    // Map to SearchResultResponse shape
    const searchResults = results.map((t) => ({
      ...t,
      description: t.description || '',
      destinations: t.destinations.map((d) => ({
        name: d.name,
        country: d.country,
        city: d.city,
        description: d.description,
      })),
      activities: t.activities.map((a) => ({
        name: a.name,
        description: a.description,
        location: a.location,
      })),
    }));

    return ok(paginate(searchResults, pageNum(url), pageSize(url)));
  }

  // /api/v1/search/{id}
  const searchIdMatch = path.match(/^\/api\/v1\/search\/([\w-]+)$/);
  if (searchIdMatch && method === 'GET') {
    const travel = MockData.travels.find((t) => t.id === searchIdMatch[1]);
    return ok(travel || MockData.travels[0]);
  }

  // ===== REVIEWS =====

  // /api/v1/reviews/travel/{id}
  const reviewTravelMatch = path.match(/^\/api\/v1\/reviews\/travel\/([\w-]+)$/);
  if (reviewTravelMatch && method === 'GET') {
    const filtered = MockData.reviewsByTravel(reviewTravelMatch[1]);
    return ok(paginate(filtered, pageNum(url), pageSize(url)));
  }

  // /api/v1/reviews/traveler/{id}
  const reviewTravelerMatch = path.match(/^\/api\/v1\/reviews\/traveler\/([\w-]+)$/);
  if (reviewTravelerMatch && method === 'GET') {
    const filtered = MockData.reviewsByTraveler(reviewTravelerMatch[1]);
    return ok(paginate(filtered, pageNum(url), pageSize(url)));
  }

  if (path === '/api/v1/reviews' && method === 'GET') {
    return ok(paginate(MockData.reviews, pageNum(url), pageSize(url)));
  }

  if (path === '/api/v1/reviews' && method === 'POST') {
    const body = req.body as any;
    const review = {
      id: 'r-new-' + Date.now(),
      travelId: body?.travelId || '',
      travelerId: MockData.defaultUser.id,
      travelerName: `${MockData.defaultUser.firstName} ${MockData.defaultUser.lastName}`,
      rating: body?.rating || 5,
      comment: body?.comment || '',
      createdAt: new Date().toISOString(),
    };
    return created(review);
  }

  // ===== REPORTS =====

  if (path === '/api/v1/reports' && method === 'GET') {
    return ok(paginate(MockData.reports, pageNum(url), pageSize(url)));
  }

  if (path === '/api/v1/reports' && method === 'POST') {
    const body = req.body as any;
    const report = {
      id: 'rpt-new-' + Date.now(),
      reporterId: MockData.defaultUser.id,
      reporterName: `${MockData.defaultUser.firstName} ${MockData.defaultUser.lastName}`,
      targetType: body?.targetType || 'TRAVEL',
      targetId: body?.targetId || '',
      targetLabel: body?.targetLabel || 'Unknown',
      reason: body?.reason || '',
      status: 'OPEN',
      createdAt: new Date().toISOString(),
    };
    return created(report);
  }

  // /api/v1/reports/{id}
  const reportIdMatch = path.match(/^\/api\/v1\/reports\/([\w-]+)$/);
  if (reportIdMatch && method === 'PUT') {
    const report = MockData.reports.find((r) => r.id === reportIdMatch[1]);
    return ok(report ? { ...report, ...(req.body as any) } : msg('Updated'));
  }

  // ===== RECOMMENDATIONS =====

  if (path === '/api/v1/recommendations/me' && method === 'GET') {
    return ok(MockData.recommendations);
  }

  // ===== FALLBACK: pass through =====
  return next(req);
};

import {
  TravelResponse,
  UserResponse,
  SubscriptionResponse,
  PaymentResponse,
  NotificationResponse,
  SearchResultResponse,
  AuthResponse,
  PageResponse,
  ReviewResponse,
  ReportResponse,
  RecommendationResponse,
} from '../../shared/models/api.models';

// ===== Users =====

const MOCK_USERS: UserResponse[] = [
  {
    id: 'u1',
    email: 'admin@travelapp.com',
    firstName: 'Sarah',
    lastName: 'Admin',
    phoneNumber: '+33612345678',
    role: 'ADMIN',
    status: 'ACTIVE',
    performanceScore: null,
    createdAt: '2025-01-10T08:00:00Z',
    lastLoginAt: '2026-03-25T14:30:00Z',
  },
  {
    id: 'u2',
    email: 'marco@travelapp.com',
    firstName: 'Marco',
    lastName: 'Rossi',
    phoneNumber: '+39612345678',
    role: 'MANAGER',
    status: 'ACTIVE',
    performanceScore: 87,
    createdAt: '2025-02-15T10:00:00Z',
    lastLoginAt: '2026-03-24T09:00:00Z',
  },
  {
    id: 'u3',
    email: 'cheikh@travelapp.com',
    firstName: 'Cheikh',
    lastName: 'Diop',
    phoneNumber: '+221771234567',
    role: 'TRAVELER',
    status: 'ACTIVE',
    performanceScore: null,
    createdAt: '2025-03-01T12:00:00Z',
    lastLoginAt: '2026-03-26T08:00:00Z',
  },
  {
    id: 'u4',
    email: 'aisha@travelapp.com',
    firstName: 'Aisha',
    lastName: 'Mbaye',
    phoneNumber: '+221781234567',
    role: 'TRAVELER',
    status: 'ACTIVE',
    performanceScore: null,
    createdAt: '2025-04-10T09:00:00Z',
    lastLoginAt: '2026-03-20T16:00:00Z',
  },
  {
    id: 'u5',
    email: 'john@travelapp.com',
    firstName: 'John',
    lastName: 'Smith',
    phoneNumber: '+14155551234',
    role: 'TRAVELER',
    status: 'BANNED',
    performanceScore: null,
    createdAt: '2025-05-20T11:00:00Z',
    lastLoginAt: '2026-02-10T12:00:00Z',
  },
  {
    id: 'u6',
    email: 'fatou@travelapp.com',
    firstName: 'Fatou',
    lastName: 'Sow',
    phoneNumber: '+221761234567',
    role: 'MANAGER',
    status: 'ACTIVE',
    performanceScore: 92,
    createdAt: '2025-03-15T14:00:00Z',
    lastLoginAt: '2026-03-25T11:00:00Z',
  },
  {
    id: 'u7',
    email: 'liam@travelapp.com',
    firstName: 'Liam',
    lastName: 'O\'Brien',
    phoneNumber: '+353851234567',
    role: 'TRAVELER',
    status: 'PENDING_VERIFICATION',
    performanceScore: null,
    createdAt: '2026-03-20T15:00:00Z',
    lastLoginAt: null,
  },
  {
    id: 'u8',
    email: 'maria@travelapp.com',
    firstName: 'Maria',
    lastName: 'Garcia',
    phoneNumber: '+34612345678',
    role: 'TRAVELER',
    status: 'ACTIVE',
    performanceScore: null,
    createdAt: '2025-06-01T08:00:00Z',
    lastLoginAt: '2026-03-22T10:00:00Z',
  },
  {
    id: 'u9',
    email: 'yuki@travelapp.com',
    firstName: 'Yuki',
    lastName: 'Tanaka',
    phoneNumber: '+81901234567',
    role: 'MANAGER',
    status: 'ACTIVE',
    performanceScore: 78,
    createdAt: '2025-07-10T06:00:00Z',
    lastLoginAt: '2026-03-23T07:00:00Z',
  },
  {
    id: 'u10',
    email: 'omar@travelapp.com',
    firstName: 'Omar',
    lastName: 'Hassan',
    phoneNumber: '+212612345678',
    role: 'TRAVELER',
    status: 'ACTIVE',
    performanceScore: null,
    createdAt: '2025-08-20T13:00:00Z',
    lastLoginAt: '2026-03-24T18:00:00Z',
  },
];

// ===== Travels =====

const MOCK_TRAVELS: TravelResponse[] = [
  {
    id: 't1',
    managerId: 'u2',
    title: 'Moroccan Desert Adventure',
    description: 'Explore the golden dunes of the Sahara with camel treks, traditional Berber camps, and stunning sunsets over Merzouga. Includes visits to Fes medina and the blue city of Chefchaouen.',
    startDate: '2026-05-15',
    endDate: '2026-05-25',
    duration: 10,
    price: 1200,
    maxCapacity: 20,
    currentBookings: 14,
    availableSpots: 6,
    status: 'PUBLISHED',
    accommodationType: 'HOTEL',
    accommodationName: 'Riad Sahara & Desert Camp',
    transportationType: 'MINIBUS',
    transportationDetails: 'Private minibus + camel trek in desert',
    destinations: [
      { id: 'd1', name: 'Merzouga Dunes', country: 'Morocco', city: 'Merzouga', description: 'Golden Sahara dunes with overnight desert camp', displayOrder: 1 },
      { id: 'd2', name: 'Fes Medina', country: 'Morocco', city: 'Fes', description: 'UNESCO World Heritage old city with 9000+ alleys', displayOrder: 2 },
      { id: 'd3', name: 'Chefchaouen', country: 'Morocco', city: 'Chefchaouen', description: 'The iconic blue-painted mountain town', displayOrder: 3 },
    ],
    activities: [
      { id: 'a1', name: 'Camel Trek', description: 'Sunset camel ride through the Erg Chebbi dunes', location: 'Merzouga', displayOrder: 1 },
      { id: 'a2', name: 'Medina Walking Tour', description: 'Guided tour through the ancient medina of Fes', location: 'Fes', displayOrder: 2 },
      { id: 'a3', name: 'Stargazing Night', description: 'Desert stargazing with traditional mint tea', location: 'Merzouga', displayOrder: 3 },
    ],
    createdAt: '2026-01-10T08:00:00Z',
    updatedAt: '2026-03-01T10:00:00Z',
  },
  {
    id: 't2',
    managerId: 'u2',
    title: 'Bali Serenity Retreat',
    description: 'A wellness journey through Bali: yoga sessions in Ubud rice terraces, temple visits, snorkeling in crystal-clear waters, and authentic Balinese cooking classes.',
    startDate: '2026-06-01',
    endDate: '2026-06-12',
    duration: 11,
    price: 1850,
    maxCapacity: 15,
    currentBookings: 15,
    availableSpots: 0,
    status: 'PUBLISHED',
    accommodationType: 'RESORT',
    accommodationName: 'Ubud Harmony Resort',
    transportationType: 'CAR',
    transportationDetails: 'Private car transfers + domestic flights',
    destinations: [
      { id: 'd4', name: 'Ubud', country: 'Indonesia', city: 'Ubud', description: 'Cultural heart of Bali with lush rice terraces', displayOrder: 1 },
      { id: 'd5', name: 'Nusa Penida', country: 'Indonesia', city: null, description: 'Pristine island with dramatic cliffs and manta rays', displayOrder: 2 },
    ],
    activities: [
      { id: 'a4', name: 'Sunrise Yoga', description: 'Morning yoga session overlooking Tegallalang rice terraces', location: 'Ubud', displayOrder: 1 },
      { id: 'a5', name: 'Temple Visit', description: 'Visit the sacred Tirta Empul water temple', location: 'Tampaksiring', displayOrder: 2 },
      { id: 'a6', name: 'Snorkeling', description: 'Swim with manta rays at Manta Point', location: 'Nusa Penida', displayOrder: 3 },
    ],
    createdAt: '2026-01-20T12:00:00Z',
    updatedAt: '2026-02-15T08:00:00Z',
  },
  {
    id: 't3',
    managerId: 'u6',
    title: 'Senegal Cultural Immersion',
    description: 'Discover the vibrant culture of Senegal: from the bustling streets of Dakar to the serene beauty of the Pink Lake and the history of Gorée Island.',
    startDate: '2026-04-20',
    endDate: '2026-04-28',
    duration: 8,
    price: 950,
    maxCapacity: 25,
    currentBookings: 8,
    availableSpots: 17,
    status: 'PUBLISHED',
    accommodationType: 'HOTEL',
    accommodationName: 'Teranga Beach Hotel',
    transportationType: 'MINIBUS',
    transportationDetails: 'Air-conditioned minibus + ferry to Gorée',
    destinations: [
      { id: 'd6', name: 'Dakar', country: 'Senegal', city: 'Dakar', description: 'Vibrant capital city with markets and nightlife', displayOrder: 1 },
      { id: 'd7', name: 'Lac Rose', country: 'Senegal', city: null, description: 'The famous pink-colored lake, unique in the world', displayOrder: 2 },
      { id: 'd8', name: 'Gorée Island', country: 'Senegal', city: null, description: 'Historic island and UNESCO World Heritage Site', displayOrder: 3 },
    ],
    activities: [
      { id: 'a7', name: 'Market Tour', description: 'Explore Sandaga and Kermel markets', location: 'Dakar', displayOrder: 1 },
      { id: 'a8', name: 'Salt Harvesting', description: 'Watch traditional salt harvesting at Pink Lake', location: 'Lac Rose', displayOrder: 2 },
      { id: 'a9', name: 'History Walk', description: 'Guided tour of the House of Slaves', location: 'Gorée Island', displayOrder: 3 },
    ],
    createdAt: '2026-02-01T09:00:00Z',
    updatedAt: '2026-03-10T14:00:00Z',
  },
  {
    id: 't4',
    managerId: 'u6',
    title: 'Iceland Northern Lights Expedition',
    description: 'Chase the aurora borealis across Iceland\'s dramatic landscapes. Visit geysers, glaciers, volcanic black-sand beaches, and soak in natural hot springs.',
    startDate: '2026-09-20',
    endDate: '2026-10-01',
    duration: 11,
    price: 2400,
    maxCapacity: 12,
    currentBookings: 5,
    availableSpots: 7,
    status: 'PUBLISHED',
    accommodationType: 'GUESTHOUSE',
    accommodationName: 'Nordic Guesthouses & Lodges',
    transportationType: 'CAR',
    transportationDetails: '4x4 jeeps on Ring Road',
    destinations: [
      { id: 'd9', name: 'Golden Circle', country: 'Iceland', city: null, description: 'Geysir, Gullfoss waterfall, and Thingvellir', displayOrder: 1 },
      { id: 'd10', name: 'Vik', country: 'Iceland', city: 'Vik', description: 'Black sand beach with basalt sea stacks', displayOrder: 2 },
      { id: 'd11', name: 'Jökulsárlón', country: 'Iceland', city: null, description: 'Glacial lagoon with floating icebergs', displayOrder: 3 },
    ],
    activities: [
      { id: 'a10', name: 'Northern Lights Hunt', description: 'Evening excursion to chase the aurora borealis', location: 'Various', displayOrder: 1 },
      { id: 'a11', name: 'Glacier Hike', description: 'Guided walk on Sólheimajökull glacier', location: 'Sólheimajökull', displayOrder: 2 },
      { id: 'a12', name: 'Hot Spring Soak', description: 'Relax in the Secret Lagoon natural hot spring', location: 'Flúðir', displayOrder: 3 },
    ],
    createdAt: '2026-02-10T11:00:00Z',
    updatedAt: '2026-03-15T09:00:00Z',
  },
  {
    id: 't5',
    managerId: 'u9',
    title: 'Japan Cherry Blossom Trail',
    description: 'Follow the cherry blossom front from Tokyo to Kyoto. Experience traditional tea ceremonies, visit ancient shrines, and savor authentic Japanese cuisine.',
    startDate: '2026-04-01',
    endDate: '2026-04-12',
    duration: 11,
    price: 2800,
    maxCapacity: 16,
    currentBookings: 12,
    availableSpots: 4,
    status: 'PUBLISHED',
    accommodationType: 'HOTEL',
    accommodationName: 'Traditional Ryokans & City Hotels',
    transportationType: 'TRAIN',
    transportationDetails: 'Japan Rail Pass — bullet train between cities',
    destinations: [
      { id: 'd12', name: 'Tokyo', country: 'Japan', city: 'Tokyo', description: 'Megacity mixing ultra-modern and traditional', displayOrder: 1 },
      { id: 'd13', name: 'Hakone', country: 'Japan', city: 'Hakone', description: 'Hot spring resort town with views of Mt. Fuji', displayOrder: 2 },
      { id: 'd14', name: 'Kyoto', country: 'Japan', city: 'Kyoto', description: 'Ancient capital with 2000+ temples and shrines', displayOrder: 3 },
    ],
    activities: [
      { id: 'a13', name: 'Tea Ceremony', description: 'Traditional matcha tea ceremony in a Kyoto teahouse', location: 'Kyoto', displayOrder: 1 },
      { id: 'a14', name: 'Sushi Workshop', description: 'Learn to make sushi at Tsukiji Outer Market', location: 'Tokyo', displayOrder: 2 },
      { id: 'a15', name: 'Bamboo Grove Walk', description: 'Stroll through the iconic Arashiyama bamboo forest', location: 'Kyoto', displayOrder: 3 },
    ],
    createdAt: '2026-01-05T07:00:00Z',
    updatedAt: '2026-03-05T16:00:00Z',
  },
  {
    id: 't6',
    managerId: 'u2',
    title: 'Patagonia Wilderness Trek',
    description: 'Hike through the raw beauty of Patagonia: Torres del Paine, Perito Moreno glacier, and the vast steppe. For adventurous spirits only.',
    startDate: '2026-11-10',
    endDate: '2026-11-22',
    duration: 12,
    price: 3100,
    maxCapacity: 10,
    currentBookings: 3,
    availableSpots: 7,
    status: 'PUBLISHED',
    accommodationType: 'CAMPING',
    accommodationName: 'Refugios & Mountain Camps',
    transportationType: 'BUS',
    transportationDetails: 'Long-distance bus + trekking',
    destinations: [
      { id: 'd15', name: 'Torres del Paine', country: 'Chile', city: null, description: 'Iconic granite towers and turquoise lakes', displayOrder: 1 },
      { id: 'd16', name: 'El Calafate', country: 'Argentina', city: 'El Calafate', description: 'Gateway to Perito Moreno glacier', displayOrder: 2 },
    ],
    activities: [
      { id: 'a16', name: 'W Trek', description: '4-day trek through Torres del Paine', location: 'Torres del Paine', displayOrder: 1 },
      { id: 'a17', name: 'Glacier Walk', description: 'Mini-trekking on Perito Moreno glacier', location: 'El Calafate', displayOrder: 2 },
    ],
    createdAt: '2026-02-20T10:00:00Z',
    updatedAt: '2026-03-18T12:00:00Z',
  },
  {
    id: 't7',
    managerId: 'u9',
    title: 'Greek Island Hopping',
    description: 'Sail between the most beautiful Greek islands: Santorini sunsets, Mykonos nightlife, and Crete\'s ancient ruins and beaches.',
    startDate: '2026-07-05',
    endDate: '2026-07-15',
    duration: 10,
    price: 1600,
    maxCapacity: 18,
    currentBookings: 11,
    availableSpots: 7,
    status: 'PUBLISHED',
    accommodationType: 'HOTEL',
    accommodationName: 'Boutique Island Hotels',
    transportationType: 'BOAT',
    transportationDetails: 'Ferry transfers between islands',
    destinations: [
      { id: 'd17', name: 'Santorini', country: 'Greece', city: 'Oia', description: 'Iconic whitewashed villages and caldera views', displayOrder: 1 },
      { id: 'd18', name: 'Mykonos', country: 'Greece', city: 'Mykonos', description: 'Lively island with windmills and beaches', displayOrder: 2 },
      { id: 'd19', name: 'Crete', country: 'Greece', city: 'Heraklion', description: 'Largest Greek island, home of Minoan civilization', displayOrder: 3 },
    ],
    activities: [
      { id: 'a18', name: 'Caldera Sailing', description: 'Catamaran cruise around the Santorini caldera at sunset', location: 'Santorini', displayOrder: 1 },
      { id: 'a19', name: 'Knossos Palace', description: 'Visit the ancient Minoan palace ruins', location: 'Crete', displayOrder: 2 },
      { id: 'a20', name: 'Beach Hopping', description: 'Visit Paradise and Super Paradise beaches', location: 'Mykonos', displayOrder: 3 },
    ],
    createdAt: '2026-03-01T08:00:00Z',
    updatedAt: '2026-03-20T10:00:00Z',
  },
  {
    id: 't8',
    managerId: 'u6',
    title: 'Colombian Coffee & Caribbean',
    description: 'From the coffee triangle\'s lush green mountains to Cartagena\'s colorful colonial streets and Caribbean beaches. A feast for all senses.',
    startDate: '2026-08-05',
    endDate: '2026-08-14',
    duration: 9,
    price: 1350,
    maxCapacity: 20,
    currentBookings: 7,
    availableSpots: 13,
    status: 'PUBLISHED',
    accommodationType: 'HOTEL',
    accommodationName: 'Finca & Colonial Hotels',
    transportationType: 'FLIGHT',
    transportationDetails: 'Domestic flights + ground transfers',
    destinations: [
      { id: 'd20', name: 'Salento', country: 'Colombia', city: 'Salento', description: 'Heart of the coffee region, Cocora Valley', displayOrder: 1 },
      { id: 'd21', name: 'Cartagena', country: 'Colombia', city: 'Cartagena', description: 'Colorful walled city on the Caribbean coast', displayOrder: 2 },
    ],
    activities: [
      { id: 'a21', name: 'Coffee Farm Tour', description: 'Visit a working coffee finca and learn the process', location: 'Salento', displayOrder: 1 },
      { id: 'a22', name: 'Old City Walk', description: 'Guided walk through Cartagena\'s walled old town', location: 'Cartagena', displayOrder: 2 },
      { id: 'a23', name: 'Rosario Islands', description: 'Day trip to Caribbean island with snorkeling', location: 'Islas del Rosario', displayOrder: 3 },
    ],
    createdAt: '2026-02-28T13:00:00Z',
    updatedAt: '2026-03-22T11:00:00Z',
  },
  {
    id: 't9',
    managerId: 'u2',
    title: 'Safari in Tanzania',
    description: 'Witness the Great Migration in the Serengeti, explore Ngorongoro Crater, and meet Maasai communities. The ultimate African wildlife experience.',
    startDate: '2026-07-20',
    endDate: '2026-07-30',
    duration: 10,
    price: 3500,
    maxCapacity: 12,
    currentBookings: 10,
    availableSpots: 2,
    status: 'PUBLISHED',
    accommodationType: 'CAMPING',
    accommodationName: 'Luxury Safari Tented Camps',
    transportationType: 'CAR',
    transportationDetails: 'Safari 4x4 vehicles',
    destinations: [
      { id: 'd22', name: 'Serengeti', country: 'Tanzania', city: null, description: 'Endless plains and the Great Migration', displayOrder: 1 },
      { id: 'd23', name: 'Ngorongoro Crater', country: 'Tanzania', city: null, description: 'World\'s largest intact volcanic caldera', displayOrder: 2 },
    ],
    activities: [
      { id: 'a24', name: 'Game Drive', description: 'Full-day game drive tracking the Big Five', location: 'Serengeti', displayOrder: 1 },
      { id: 'a25', name: 'Maasai Village', description: 'Cultural visit to a Maasai community', location: 'Ngorongoro', displayOrder: 2 },
      { id: 'a26', name: 'Hot Air Balloon', description: 'Sunrise balloon ride over the Serengeti', location: 'Serengeti', displayOrder: 3 },
    ],
    createdAt: '2026-01-15T09:00:00Z',
    updatedAt: '2026-03-12T15:00:00Z',
  },
  {
    id: 't10',
    managerId: 'u2',
    title: 'Portugal Coastal Road Trip',
    description: 'Draft itinerary — drive Portugal\'s Atlantic coast from Porto to the Algarve. Wine tastings, surfing, and seafood along the way.',
    startDate: '2026-09-01',
    endDate: '2026-09-10',
    duration: 9,
    price: 1100,
    maxCapacity: 14,
    currentBookings: 0,
    availableSpots: 14,
    status: 'DRAFT',
    accommodationType: 'APARTMENT',
    accommodationName: 'Coastal Apartments',
    transportationType: 'CAR',
    transportationDetails: 'Rental cars',
    destinations: [
      { id: 'd24', name: 'Porto', country: 'Portugal', city: 'Porto', description: 'Port wine cellars and Douro River views', displayOrder: 1 },
      { id: 'd25', name: 'Algarve', country: 'Portugal', city: 'Lagos', description: 'Dramatic sea cliffs and golden beaches', displayOrder: 2 },
    ],
    activities: [
      { id: 'a27', name: 'Wine Tasting', description: 'Port wine tasting in Vila Nova de Gaia cellars', location: 'Porto', displayOrder: 1 },
      { id: 'a28', name: 'Surf Lesson', description: 'Beginner surf session in Peniche', location: 'Peniche', displayOrder: 2 },
    ],
    createdAt: '2026-03-20T10:00:00Z',
    updatedAt: '2026-03-20T10:00:00Z',
  },
  {
    id: 't11',
    managerId: 'u6',
    title: 'Vietnam North to South',
    description: 'A complete journey through Vietnam: Hanoi\'s Old Quarter, Ha Long Bay cruise, Hoi An lanterns, and the Mekong Delta.',
    startDate: '2026-10-10',
    endDate: '2026-10-24',
    duration: 14,
    price: 1750,
    maxCapacity: 18,
    currentBookings: 0,
    availableSpots: 18,
    status: 'DRAFT',
    accommodationType: 'HOTEL',
    accommodationName: 'Local Boutique Hotels',
    transportationType: 'TRAIN',
    transportationDetails: 'Reunification Express train + domestic flights',
    destinations: [
      { id: 'd26', name: 'Hanoi', country: 'Vietnam', city: 'Hanoi', description: 'Charming capital with French colonial architecture', displayOrder: 1 },
      { id: 'd27', name: 'Ha Long Bay', country: 'Vietnam', city: null, description: 'Emerald waters with thousands of limestone islands', displayOrder: 2 },
      { id: 'd28', name: 'Hoi An', country: 'Vietnam', city: 'Hoi An', description: 'Ancient trading port with lantern-lit streets', displayOrder: 3 },
    ],
    activities: [
      { id: 'a29', name: 'Junk Boat Cruise', description: 'Overnight cruise through Ha Long Bay', location: 'Ha Long Bay', displayOrder: 1 },
      { id: 'a30', name: 'Cooking Class', description: 'Vietnamese cooking class at a local home', location: 'Hoi An', displayOrder: 2 },
    ],
    createdAt: '2026-03-15T14:00:00Z',
    updatedAt: '2026-03-15T14:00:00Z',
  },
  {
    id: 't12',
    managerId: 'u9',
    title: 'Cancelled: Swiss Alps Winter',
    description: 'This trip was cancelled due to insufficient bookings.',
    startDate: '2026-01-15',
    endDate: '2026-01-25',
    duration: 10,
    price: 2200,
    maxCapacity: 16,
    currentBookings: 2,
    availableSpots: 14,
    status: 'CANCELLED',
    accommodationType: 'RESORT',
    accommodationName: 'Alpine Chalets',
    transportationType: 'TRAIN',
    transportationDetails: 'Swiss Rail Pass',
    destinations: [
      { id: 'd29', name: 'Zermatt', country: 'Switzerland', city: 'Zermatt', description: 'Iconic Matterhorn views', displayOrder: 1 },
    ],
    activities: [
      { id: 'a31', name: 'Skiing', description: 'Full-day skiing on Matterhorn slopes', location: 'Zermatt', displayOrder: 1 },
    ],
    createdAt: '2025-10-01T08:00:00Z',
    updatedAt: '2025-12-20T09:00:00Z',
  },
];

// ===== Subscriptions =====

const MOCK_SUBSCRIPTIONS: SubscriptionResponse[] = [
  { id: 's1', travelerId: 'u3', travelId: 't1', travelTitle: 'Moroccan Desert Adventure', status: 'CONFIRMED', createdAt: '2026-03-10T10:00:00Z', updatedAt: '2026-03-10T10:30:00Z' },
  { id: 's2', travelerId: 'u3', travelId: 't5', travelTitle: 'Japan Cherry Blossom Trail', status: 'PENDING_PAYMENT', createdAt: '2026-03-20T14:00:00Z', updatedAt: '2026-03-20T14:00:00Z' },
  { id: 's3', travelerId: 'u3', travelId: 't3', travelTitle: 'Senegal Cultural Immersion', status: 'CANCELLED', createdAt: '2026-02-15T08:00:00Z', updatedAt: '2026-02-16T09:00:00Z' },
  { id: 's4', travelerId: 'u4', travelId: 't1', travelTitle: 'Moroccan Desert Adventure', status: 'CONFIRMED', createdAt: '2026-03-05T11:00:00Z', updatedAt: '2026-03-05T11:30:00Z' },
  { id: 's5', travelerId: 'u8', travelId: 't7', travelTitle: 'Greek Island Hopping', status: 'CONFIRMED', createdAt: '2026-03-12T16:00:00Z', updatedAt: '2026-03-12T16:30:00Z' },
  { id: 's6', travelerId: 'u10', travelId: 't9', travelTitle: 'Safari in Tanzania', status: 'CONFIRMED', createdAt: '2026-03-18T09:00:00Z', updatedAt: '2026-03-18T09:30:00Z' },
];

// ===== Payments =====

const MOCK_PAYMENTS: PaymentResponse[] = [
  { id: 'p1', subscriptionId: 's1', travelId: 't1', travelerId: 'u3', travelTitle: 'Moroccan Desert Adventure', amount: 1200, currency: 'EUR', method: 'STRIPE', transactionId: 'txn_abc123', status: 'SUCCESS', failureReason: null, createdAt: '2026-03-10T10:15:00Z', updatedAt: '2026-03-10T10:15:00Z' },
  { id: 'p2', subscriptionId: 's4', travelId: 't1', travelerId: 'u4', travelTitle: 'Moroccan Desert Adventure', amount: 1200, currency: 'EUR', method: 'PAYPAL', transactionId: 'txn_def456', status: 'SUCCESS', failureReason: null, createdAt: '2026-03-05T11:20:00Z', updatedAt: '2026-03-05T11:20:00Z' },
  { id: 'p3', subscriptionId: 's5', travelId: 't7', travelerId: 'u8', travelTitle: 'Greek Island Hopping', amount: 1600, currency: 'EUR', method: 'WAVE', transactionId: 'txn_ghi789', status: 'SUCCESS', failureReason: null, createdAt: '2026-03-12T16:15:00Z', updatedAt: '2026-03-12T16:15:00Z' },
  { id: 'p4', subscriptionId: 's6', travelId: 't9', travelerId: 'u10', travelTitle: 'Safari in Tanzania', amount: 3500, currency: 'EUR', method: 'STRIPE', transactionId: 'txn_jkl012', status: 'SUCCESS', failureReason: null, createdAt: '2026-03-18T09:15:00Z', updatedAt: '2026-03-18T09:15:00Z' },
  { id: 'p5', subscriptionId: 's2', travelId: 't5', travelerId: 'u3', travelTitle: 'Japan Cherry Blossom Trail', amount: 2800, currency: 'EUR', method: 'STRIPE', transactionId: 'txn_mno345', status: 'PENDING', failureReason: null, createdAt: '2026-03-20T14:05:00Z', updatedAt: '2026-03-20T14:05:00Z' },
  { id: 'p6', subscriptionId: 's3', travelId: 't3', travelerId: 'u3', travelTitle: 'Senegal Cultural Immersion', amount: 950, currency: 'EUR', method: 'WAVE', transactionId: 'txn_pqr678', status: 'FAILED', failureReason: 'Insufficient funds', createdAt: '2026-02-15T08:10:00Z', updatedAt: '2026-02-15T08:10:00Z' },
];

// ===== Notifications =====

const MOCK_NOTIFICATIONS: NotificationResponse[] = [
  { id: 'n1', travelerId: 'u3', travelId: 't1', subscriptionId: 's1', recipientEmail: 'cheikh@travelapp.com', subject: 'Subscription confirmed — Moroccan Desert Adventure', body: 'Your subscription has been confirmed. Get ready for an amazing journey!', type: 'SUBSCRIPTION_CREATED', status: 'SENT', failureReason: null, createdAt: '2026-03-10T10:30:00Z', updatedAt: '2026-03-10T10:30:00Z' },
  { id: 'n2', travelerId: 'u3', travelId: 't1', subscriptionId: 's1', recipientEmail: 'cheikh@travelapp.com', subject: 'Payment received — Moroccan Desert Adventure', body: 'Your payment of €1,200 has been processed successfully.', type: 'PAYMENT_SUCCESS', status: 'SENT', failureReason: null, createdAt: '2026-03-10T10:31:00Z', updatedAt: '2026-03-10T10:31:00Z' },
  { id: 'n3', travelerId: 'u3', travelId: 't3', subscriptionId: 's3', recipientEmail: 'cheikh@travelapp.com', subject: 'Payment failed — Senegal Cultural Immersion', body: 'Your payment could not be processed. Please try again.', type: 'PAYMENT_FAILED', status: 'SENT', failureReason: null, createdAt: '2026-02-15T08:11:00Z', updatedAt: '2026-02-15T08:11:00Z' },
  { id: 'n4', travelerId: 'u4', travelId: 't1', subscriptionId: 's4', recipientEmail: 'aisha@travelapp.com', subject: 'Subscription confirmed — Moroccan Desert Adventure', body: 'Your subscription has been confirmed.', type: 'SUBSCRIPTION_CREATED', status: 'SENT', failureReason: null, createdAt: '2026-03-05T11:30:00Z', updatedAt: '2026-03-05T11:30:00Z' },
  { id: 'n5', travelerId: 'u8', travelId: 't7', subscriptionId: 's5', recipientEmail: 'maria@travelapp.com', subject: 'Payment received — Greek Island Hopping', body: 'Your payment of €1,600 has been processed successfully.', type: 'PAYMENT_SUCCESS', status: 'SENT', failureReason: null, createdAt: '2026-03-12T16:31:00Z', updatedAt: '2026-03-12T16:31:00Z' },
  { id: 'n6', travelerId: 'u10', travelId: 't9', subscriptionId: 's6', recipientEmail: 'omar@travelapp.com', subject: 'Subscription confirmed — Safari in Tanzania', body: 'Your subscription has been confirmed.', type: 'SUBSCRIPTION_CREATED', status: 'SENT', failureReason: null, createdAt: '2026-03-18T09:30:00Z', updatedAt: '2026-03-18T09:30:00Z' },
  { id: 'n7', travelerId: 'u10', travelId: 't9', subscriptionId: 's6', recipientEmail: 'omar@travelapp.com', subject: 'Payment received — Safari in Tanzania', body: 'Payment processed.', type: 'PAYMENT_SUCCESS', status: 'FAILED', failureReason: 'SMTP timeout', createdAt: '2026-03-18T09:31:00Z', updatedAt: '2026-03-18T09:32:00Z' },
];

// ===== Reviews =====

const MOCK_REVIEWS: ReviewResponse[] = [
  { id: 'r1', travelId: 't1', travelerId: 'u3', travelerName: 'Cheikh Diop', rating: 5, comment: 'Absolutely magical experience! The desert camp under the stars was unforgettable. The guides were incredibly knowledgeable and friendly.', createdAt: '2026-03-20T10:00:00Z' },
  { id: 'r2', travelId: 't1', travelerId: 'u4', travelerName: 'Aisha Mbaye', rating: 4, comment: 'Great trip overall. Fes medina was a highlight. Only wish we had more time in Chefchaouen.', createdAt: '2026-03-18T14:00:00Z' },
  { id: 'r3', travelId: 't7', travelerId: 'u8', travelerName: 'Maria Garcia', rating: 5, comment: 'The Santorini sunset sailing was the best experience of my life. Highly recommend this trip!', createdAt: '2026-03-22T11:00:00Z' },
  { id: 'r4', travelId: 't9', travelerId: 'u10', travelerName: 'Omar Hassan', rating: 5, comment: 'Seeing the Great Migration was a dream come true. The hot air balloon ride was worth every penny.', createdAt: '2026-03-25T08:00:00Z' },
  { id: 'r5', travelId: 't3', travelerId: 'u8', travelerName: 'Maria Garcia', rating: 4, comment: 'Dakar is so vibrant! The Gorée Island visit was very moving. Food was incredible everywhere.', createdAt: '2026-03-15T16:00:00Z' },
  { id: 'r6', travelId: 't5', travelerId: 'u4', travelerName: 'Aisha Mbaye', rating: 5, comment: 'Japan exceeded all expectations. The ryokan experience and tea ceremony were perfectly curated.', createdAt: '2026-03-12T09:00:00Z' },
  { id: 'r7', travelId: 't1', travelerId: 'u10', travelerName: 'Omar Hassan', rating: 3, comment: 'Good trip but the bus transfers were quite long. Desert camp was amazing though.', createdAt: '2026-03-19T12:00:00Z' },
  { id: 'r8', travelId: 't7', travelerId: 'u4', travelerName: 'Aisha Mbaye', rating: 4, comment: 'Beautiful islands, great food. The ferry between islands was a nice touch. Mykonos nightlife was fun!', createdAt: '2026-03-21T15:00:00Z' },
];

// ===== Reports =====

const MOCK_REPORTS: ReportResponse[] = [
  { id: 'rpt1', reporterId: 'u3', reporterName: 'Cheikh Diop', targetType: 'TRAVEL', targetId: 't12', targetLabel: 'Cancelled: Swiss Alps Winter', reason: 'Trip was cancelled without proper notice. No refund received yet.', status: 'OPEN', createdAt: '2026-03-01T10:00:00Z' },
  { id: 'rpt2', reporterId: 'u8', reporterName: 'Maria Garcia', targetType: 'USER', targetId: 'u5', targetLabel: 'John Smith', reason: 'Inappropriate behavior during the group trip. Made other travelers uncomfortable.', status: 'REVIEWED', createdAt: '2026-02-20T14:00:00Z' },
  { id: 'rpt3', reporterId: 'u4', reporterName: 'Aisha Mbaye', targetType: 'REVIEW', targetId: 'r7', targetLabel: 'Review on Moroccan Desert Adventure', reason: 'This review contains misleading information about the trip.', status: 'DISMISSED', createdAt: '2026-03-20T09:00:00Z' },
  { id: 'rpt4', reporterId: 'u10', reporterName: 'Omar Hassan', targetType: 'TRAVEL', targetId: 't6', targetLabel: 'Patagonia Wilderness Trek', reason: 'Description mentions camping but doesn\'t mention how basic the facilities are.', status: 'OPEN', createdAt: '2026-03-22T11:00:00Z' },
];

// ===== Recommendations =====

const MOCK_RECOMMENDATIONS: RecommendationResponse[] = [
  { travelId: 't4', title: 'Iceland Northern Lights Expedition', description: 'Chase the aurora borealis across Iceland\'s dramatic landscapes.', price: 2400, startDate: '2026-09-20', endDate: '2026-10-01', destinations: ['Golden Circle', 'Vik', 'Jökulsárlón'], score: 95, reason: 'Based on your interest in adventure travel' },
  { travelId: 't7', title: 'Greek Island Hopping', description: 'Sail between the most beautiful Greek islands.', price: 1600, startDate: '2026-07-05', endDate: '2026-07-15', destinations: ['Santorini', 'Mykonos', 'Crete'], score: 90, reason: 'Popular among travelers like you' },
  { travelId: 't8', title: 'Colombian Coffee & Caribbean', description: 'From the coffee triangle to Cartagena\'s colorful streets.', price: 1350, startDate: '2026-08-05', endDate: '2026-08-14', destinations: ['Salento', 'Cartagena'], score: 87, reason: 'Matches your budget and travel style' },
  { travelId: 't6', title: 'Patagonia Wilderness Trek', description: 'Hike through the raw beauty of Patagonia.', price: 3100, startDate: '2026-11-10', endDate: '2026-11-22', destinations: ['Torres del Paine', 'El Calafate'], score: 82, reason: 'Based on your love for outdoor experiences' },
];

// ===== Helper: paginate =====

export function paginate<T>(items: T[], page: number, size: number): PageResponse<T> {
  const totalElements = items.length;
  const totalPages = Math.max(1, Math.ceil(totalElements / size));
  const p = Math.max(0, Math.min(page, totalPages - 1));
  const start = p * size;
  const content = items.slice(start, start + size);
  return {
    content,
    page: p,
    size,
    totalElements,
    totalPages,
    first: p === 0,
    last: p >= totalPages - 1,
  };
}

// ===== Exported accessors =====

export const MockData = {
  users: MOCK_USERS,
  travels: MOCK_TRAVELS,
  subscriptions: MOCK_SUBSCRIPTIONS,
  payments: MOCK_PAYMENTS,
  notifications: MOCK_NOTIFICATIONS,
  reviews: MOCK_REVIEWS,
  reports: MOCK_REPORTS,
  recommendations: MOCK_RECOMMENDATIONS,

  // Default logged-in user (traveler)
  defaultUser: MOCK_USERS[2], // Cheikh — TRAVELER
  adminUser: MOCK_USERS[0],   // Sarah — ADMIN
  managerUser: MOCK_USERS[1], // Marco — MANAGER

  publishedTravels(): TravelResponse[] {
    return MOCK_TRAVELS.filter((t) => t.status === 'PUBLISHED');
  },

  managerTravels(managerId: string): TravelResponse[] {
    return MOCK_TRAVELS.filter((t) => t.managerId === managerId);
  },

  travelerSubscriptions(travelerId: string): SubscriptionResponse[] {
    return MOCK_SUBSCRIPTIONS.filter((s) => s.travelerId === travelerId);
  },

  travelSubscribers(travelId: string): SubscriptionResponse[] {
    return MOCK_SUBSCRIPTIONS.filter((s) => s.travelId === travelId);
  },

  reviewsByTravel(travelId: string): ReviewResponse[] {
    return MOCK_REVIEWS.filter((r) => r.travelId === travelId);
  },

  reviewsByTraveler(travelerId: string): ReviewResponse[] {
    return MOCK_REVIEWS.filter((r) => r.travelerId === travelerId);
  },

  averageRating(travelId: string): number {
    const reviews = MOCK_REVIEWS.filter((r) => r.travelId === travelId);
    if (reviews.length === 0) return 0;
    return reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length;
  },
};

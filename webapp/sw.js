const CACHE_NAME = 'ipeot-dir-v7';
const ASSETS = [
  'index.html',
  'style.css?v=7',
  'app.js?v=7',
  'manifest.json',
  'https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;700&display=swap'
];

// Install Event
self.addEventListener('install', event => {
  self.skipWaiting();
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => {
      console.log('Caching assets');
      return cache.addAll(ASSETS);
    })
  );
});

// Activate Event
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(keys => {
      return Promise.all(
        keys.filter(key => key !== CACHE_NAME)
            .map(key => caches.delete(key))
      );
    }).then(() => {
        return self.clients.claim();
    })
  );
});

// Fetch Event
self.addEventListener('fetch', event => {
  event.respondWith(
    caches.match(event.request).then(response => {
      // Return cache hit or fetch from network
      return response || fetch(event.request).then(fetchResponse => {
          // Dynamic caching for CSV data or new assets
          if (event.request.url.includes('export?format=csv')) {
              return caches.open(CACHE_NAME).then(cache => {
                  cache.put(event.request.url, fetchResponse.clone());
                  return fetchResponse;
              });
          }
          return fetchResponse;
      });
    }).catch(() => {
        // Fallback for offline if not in cache
        if (event.request.url.includes('index.html')) {
            return caches.match('index.html');
        }
    })
  );
});

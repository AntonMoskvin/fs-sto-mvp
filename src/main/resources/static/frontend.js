// Robust minimal frontend: loads stations and shows markers on map
document.addEventListener('DOMContentLoaded', async () => {
  const status = document.createElement('div');
  status.id = 'status';
  status.style.position = 'fixed';
  status.style.bottom = '10px';
  status.style.left = '10px';
  status.style.background = 'rgba(0,0,0,0.7)';
  status.style.color = '#fff';
  status.style.padding = '6px 12px';
  status.style.borderRadius = '6px';
  status.style.zIndex = '1000';
  status.textContent = 'SFSTO MVP: loading...';
  document.body.appendChild(status);

  // Map setup
  const map = L.map('map').setView([55.7558, 37.6173], 10);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }).addTo(map);

  // Load work options
  let workOptions = [];
  try {
    const w = await fetch('/api/v1/workoptions').then(r => r.json());
    workOptions = w;
  } catch (e) { workOptions = []; }

  // Load stations with fallback to demo
  let stations = [];
  try {
    const res = await fetch('/api/v1/stations');
    stations = (res.ok ? await res.json() : await fetch('/api/v1/stations/demo').then(r => r.json()));
  } catch (e) { stations = []; }

  // Markers on map
  stations.forEach(s => {
    if (typeof s.latitude === 'number' && typeof s.longitude === 'number') {
      L.marker([s.latitude, s.longitude]).addTo(map).bindPopup('<b>' + s.name + '</b><br/>' + (s.address || ''));
    }
  });

  // Simple booking form in console (for MVP) when a station is selected from map
  // For now, nothing else is required; the core features are map + booking via API
  status.textContent = 'SFSTO MVP: ready';
});

// Simple modular SPA for SFSTO MVP
(function(){
  const content = document.getElementById('spa-content');
  const route = location.hash.replace('#/','/') || '/map';
  const render = {
    '/map': renderMap,
    '/stations': renderStations,
    '/booking': renderBooking,
    '/history': renderHistory,
    '/admin': renderAdmin
  };
  function navigate(hash) {
    const path = (hash || '#/map').replace('#/','/');
    const f = render[path] || render['/map'];
    f();
  }
  window.addEventListener('hashchange', () => navigate(location.hash));
  // Map view
  let mapInstance = null;
  async function renderMap(){
    content.innerHTML = `<div class="card" style="height:100%; display:flex; flex-direction:column;">
      <div style="height:60%;" id="spa-map"></div>
      <div style="flex:1; padding:8px; display:flex; gap:12px; flex-wrap:wrap; align-items:stretch;">
        <div id="spa-status" class="card" style="flex:1 1 280px; min-width:260px;">Загрузка станций...</div>
      </div>
    </div>`;
    if (!mapInstance) {
      mapInstance = L.map('spa-map').setView([55.7558, 37.6173], 10);
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }).addTo(mapInstance);
    }
    const stations = await fetch('/api/v1/stations').then(r => r.ok ? r.json() : fetch('/api/v1/stations/demo').then(r => r.json()));
    stations.forEach(s => {
      if (typeof s.latitude === 'number' && typeof s.longitude === 'number') {
        const m = L.marker([s.latitude, s.longitude]).addTo(mapInstance);
        m.bindPopup(`<strong>${s.name}</strong><br/>${s.address || ''}`);
      }
    });
    document.getElementById('spa-status').innerText = 'Станции: ' + stations.length;
  }
  async function renderStations(){
    const stations = await fetch('/api/v1/stations').then(r => r.ok ? r.json() : fetch('/api/v1/stations/demo').then(r => r.json()));
    content.innerHTML = '<div class="card" style="padding:12px;">'+
      '<h2>Станции</h2>'+
      stations.map(s => `<div class="station-item" style="padding:8px; border-bottom:1px solid #333; cursor:pointer;" data-id="${s.id}">${s.name}<br>${s.address}</div>`).join('')+
      '</div>';
    // Attach click to each item
    content.querySelectorAll('.station-item').forEach(el => el.addEventListener('click', () => {
      const id = el.getAttribute('data-id');
      fetch('/api/v1/stations/' + id).then(r => r.json()).then(st => renderBooking(st));
      mapInstance && mapInstance.setView([parseFloat(el.dataset.lat)||55.7558, parseFloat(el.dataset.lng)||37.6173], 12);
    }));
  }
  function renderBooking(station){
    content.innerHTML = `<div class="card"><h2>Booking — ${station.name}</h2><div id="book-controls"></div></div>`;
    const bc = document.getElementById('book-controls');
    if (bc) {
      const start = document.createElement('input'); start.type = 'datetime-local'; start.id = 'startTime';
      bc.appendChild(document.createTextNode('Start: ')); bc.appendChild(start); bc.appendChild(document.createElement('br'));
      // Work options
      const woLabel = document.createElement('div'); woLabel.textContent = 'Work options:'; bc.appendChild(woLabel);
      fetch('/api/v1/workoptions').then(r => r.json()).then(opts => {
        opts.forEach(o => {
          const l = document.createElement('label');
          l.innerHTML = `<input type="checkbox" class="wo" value="${o.id}"> ${o.name} (${o.durationMinutes}m)`;
          bc.appendChild(l); bc.appendChild(document.createElement('br'));
        });
      });
      const btn = document.createElement('button'); btn.textContent = 'Book';
      btn.addEventListener('click', async () => {
        const selected = Array.from(document.querySelectorAll('.wo:checked')).map(e => parseInt(e.value));
        const payload = { stationId: station.id, startTime: document.getElementById('startTime').value, workOptionIds: selected.length ? selected : null };
        const resp = await fetch('/api/v1/appointments', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify(payload) });
        const data = await resp.json();
        if (data.id) alert('Booked. ID: ' + data.id); else alert('Error: ' + (data.error || JSON.stringify(data)));
      });
      bc.appendChild(document.createElement('br'));
      bc.appendChild(btn);
    }
  }
  function renderHistory(){ content.innerHTML = '<div class="card" style="padding:12px;">История ещё не реализована</div>'; }
  function renderAdmin(){ content.innerHTML = '<div class="card" style="padding:12px;">Админка — раздел в разработке</div>'; }

  // boot
  navigate(location.hash || '#/map');
})();

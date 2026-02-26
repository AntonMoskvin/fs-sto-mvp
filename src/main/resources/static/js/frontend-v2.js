// Enhanced frontend: stations list + map with improved UI and booking flow
document.addEventListener('DOMContentLoaded', async () => {
  const stationsDiv = document.getElementById('stations');
  const mapDiv = document.getElementById('map');
  const bookingPanel = document.getElementById('bookingPanel');
  const bookingInfo = document.getElementById('bookingInfo');
  const bookingControls = document.getElementById('bookingControls');

  // Map setup
  const map = L.map(mapDiv).setView([55.7558, 37.6173], 10);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }).addTo(map);

  // Load work options
  let workOptions = [];
  try { workOptions = await fetch('/api/v1/workoptions').then(r => r.json()); } catch { workOptions = []; }

  // Load stations with fallback
  let stations = [];
  try {
    const res = await fetch('/api/v1/stations');
    stations = (res.ok ? await res.json() : await fetch('/api/v1/stations/demo').then(r => r.json()));
  } catch {
    stations = [];
  }

  // Render stations list and map markers
  stationsDiv.innerHTML = '';
  stations.forEach(st => {
    // station card
    const card = document.createElement('div');
    card.className = 'station';
    card.innerHTML = `<strong>${st.name}</strong><div>${st.address || ''}</div>`;
    card.addEventListener('click', () => {
      if (typeof st.latitude === 'number' && typeof st.longitude === 'number') {
        map.setView([st.latitude, st.longitude], 12);
      }
      renderBookingPanel(st);
    });
    stationsDiv.appendChild(card);
    // add marker
    if (typeof st.latitude === 'number' && typeof st.longitude === 'number') {
      const m = L.marker([st.latitude, st.longitude]).addTo(map);
      m.bindPopup(`<b>${st.name}</b><br/>${st.address || ''}`);
    }
  });

  // booking panel population
  function renderBookingPanel(station) {
    bookingPanel.style.display = 'block';
    bookingInfo.textContent = `Бронь станции: ${station.name}`;
    // build works and inputs
    bookingControls.innerHTML = '';
    const timeRow = document.createElement('div');
    timeRow.innerHTML = `Start: <input type="datetime-local" id="startTime">`;
    bookingControls.appendChild(timeRow);
    const worksDiv = document.createElement('div');
    worksDiv.id = 'worksList';
    if (workOptions.length) {
      workOptions.forEach(w => {
        const label = document.createElement('label');
        label.innerHTML = `<input type="checkbox" class="wo-${station.id}" value="${w.id}"> ${w.name} (${w.durationMinutes}m)`;
        worksDiv.appendChild(label);
        worksDiv.appendChild(document.createElement('br'));
      });
    } else {
      worksDiv.textContent = 'Нет доступных работ';
    }
    bookingControls.appendChild(worksDiv);
    const bookBtn = document.createElement('button'); bookBtn.textContent = 'Book';
    bookBtn.addEventListener('click', async () => {
      const startTime = document.getElementById('startTime').value;
      const ids = Array.from(document.querySelectorAll('.wo-' + station.id + ':checked')).map(n => parseInt(n.value));
      const payload = { stationId: station.id, startTime, workOptionIds: ids.length ? ids : null };
      const resp = await fetch('/api/v1/appointments', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify(payload) });
      const data = await resp.json();
      if (data.id) alert('Booked. ID: ' + data.id);
      else alert('Error: ' + (data.error || JSON.stringify(data)));
    });
    bookingControls.appendChild(document.createElement('br'));
    bookingControls.appendChild(bookBtn);
  }
});

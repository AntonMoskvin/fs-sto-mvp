// Patch A: left menu + service cards with 10 markers
(function(){
  // DOM refs
  const mapEl = document.getElementById('spa-map');
  const grid = document.getElementById('servicesGrid');
  const historyMini = document.getElementById('historyMini');

  // Init map
  const map = L.map('spa-map').setView([55.7558, 37.6173], 10);
  let lastPopup = null;
  map.on('popupopen', function(e){ if (lastPopup && lastPopup !== e.popup) { lastPopup._source.closePopup(); } lastPopup = e.popup; });
  let mapMarkers = [];
  // Simple in-browser role switch (CLIENT/ADMIN)
  const ROLE_KEY = 'fssto_role';
  function getRole(){ try { return localStorage.getItem(ROLE_KEY) || 'CLIENT'; } catch { return 'CLIENT'; } }
  function setRole(role){
    try { localStorage.setItem(ROLE_KEY, role); } catch {}
    applyRoleUI(role);
    const target = (role === 'ADMIN') ? 'admin' : 'map';
    showSection(target);
  }
  function applyRoleUI(role){
    const isAdmin = (role === 'ADMIN');
    // Admin link visibility (Admin tab removed from menu as requested)
    const adminLink = document.querySelector('#leftMenu a.menu-item[href="#admin"]');
    if (adminLink) adminLink.style.display = isAdmin ? '' : 'none';
    // Admin sections visibility (keep hidden since admin tab is removed)
    const adminSections = ['section-admin','adminPanel','adminNotifications','adminHistory','adminPendingList','adminPendingRefresh'];
    adminSections.forEach(id => { const el = document.getElementById(id); if (el) el.style.display = isAdmin ? '' : 'none'; });
    // Admin should not see the map
    const mapEl = document.getElementById('spa-map');
    if (mapEl) mapEl.style.display = isAdmin ? 'none' : '';
    // Hide worktypes tab for admins
    const worktypesLink = document.querySelector('#leftMenu a.menu-item[href="#/worktypes"]');
    if (worktypesLink) worktypesLink.style.display = isAdmin ? 'none' : '';
    if (!isAdmin && window.location.hash === '#admin') showSection('map');
  }
  // init role selector (buttonless login)
  const roleSel = document.getElementById('roleSelector');
  if (roleSel){
    roleSel.value = getRole();
    roleSel.addEventListener('change', (e)=>{ setRole(e.target.value); });
  }
  // initialize role on load
  applyRoleUI(getRole());
  // expose simple API for HTML to switch role without login
  window.__getRole = getRole;
  window.__setRole = setRole;
  // map service filter population
  function populateMapServiceFilter(){
    const sel = document.getElementById('mapServiceFilter');
    if (!sel) return;
    sel.innerHTML = '<option value="">Все</option>';
    (window.__servicesAll||[]).forEach(s => {
      const opt = document.createElement('option'); opt.value = s.id; opt.textContent = s.name || 'Услуга'; sel.appendChild(opt);
    });
  }
  function renderMapMarkersForService(serviceId){
    // clear existing markers
    mapMarkers.forEach(m => map.removeLayer(m));
    mapMarkers = [];
    const stations = window.__stationsAll || [];
    stations.forEach(s => {
      const offers = s.services || [];
      const has = !serviceId || offers.some(o => o.service && o.service.id == serviceId);
      if (!has) return;
      if (typeof s.latitude === 'number' && typeof s.longitude === 'number'){
        const logo = s.logoUrl ? `<img src="${s.logoUrl}" alt="logo" style="height:28px;width:28px;object-fit:cover; border-radius:4px;"/>` : `<img src="https://picsum.photos/28/28?random=${s.id||'0'}" alt='logo' style='height:28px;width:28px;border-radius:4px;'/>`;
        const popup = `<div style="min-width:240px;"><div style='display:flex;gap:6px;align-items:center'>${logo}<strong>${(s.name && s.name.toString().trim()) ? s.name : ('Станция ' + (s.id || '0'))}</strong></div><div>${s.address||''}</div><div>Телефон: ${s.phone||''}</div><div>Услуги: ${((s.services||[]).map(x => x.service && x.service.name ? x.service.name : (x.name||'Услуга'))).join(', ')}</div><button class='btn' style='margin-top:6px;' onclick="openBookingModal(${s.id||(1000+Math.floor(Math.random()*1000))})">Записаться</button></div>`;
        const m = L.marker([s.latitude, s.longitude]).addTo(map).bindPopup(popup);
        mapMarkers.push(m);
      }
    });
  }
  // initial population of map service filter options and markers
  function initMapFiltersAndMarkers(){ populateMapServiceFilter(); renderMapMarkersForService(''); }
  // wire map service filter change to refresh markers
  const mapServiceSel = document.getElementById('mapServiceFilter');
  if (mapServiceSel){ mapServiceSel.addEventListener('change', function(){ renderMapMarkersForService(this.value); }); }
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }).addTo(map);

  // Load data
  Promise.all([
    fetch('/api/v1/workoptions').then(r => r.json()).catch(()=>[]),
    fetch('/api/v1/stations').then(r => r.ok ? r.json() : fetch('/api/v1/stations/demo').then(r => r.json())).catch(()=>[]) ,
    fetch('/api/v1/services').then(r => r.json()).catch(()=>[])
  ]).then(([workOptions, stations, services]) => {
    // markers: show up to max stations (demo extended)
    const maxStations = 15;
    const items = (stations && stations.length) ? stations.slice(0, maxStations) : [];
    let mapMarkersLocal = 0;
    for (let i=0; i<maxStations; i++){
      let s = items[i];
      if (!s){
        s = { id: 1000 + i, name: 'Demo СТО ' + (i+1), latitude: 55.75 + i*0.01, longitude: 37.62 + i*0.02, address:'Москва', phone:'+7 495 000 0'+(i%10) };
        // assign some dummy services for visuals
        const allSvcs = (services||[]).slice(0, 3);
        s.services = allSvcs.map((sv)=>({ service: sv }));
      }
      if (typeof s.latitude === 'number' && typeof s.longitude === 'number'){
        const logo = s.logoUrl ? `<img src="${s.logoUrl}" alt="logo" style="height:28px;width:28px;object-fit:cover; border-radius:4px;"/>` : `<img src="https://picsum.photos/28/28?random=${s.id||i}" alt='logo' style='height:28px;width:28px;border-radius:4px;'/>`;
      const popup = `<div style="min-width:240px;"><div style='display:flex;gap:6px;align-items:center'>${logo}<strong>${s.name}</strong></div><div>${s.address||''}</div><div>Телефон: ${s.phone||''}</div><button class='btn' style='margin-top:6px;' onclick="openBookingModal(${s.id||(1000+i)})">Записаться</button></div>`;
        const m = L.marker([s.latitude, s.longitude]).addTo(map).bindPopup(popup);
        // collect markers for later filtering
        mapMarkers.push(m);
      }
    }
    // initialize map-side controls after stations/services data loaded
    initMapFiltersAndMarkers();
  // caches for stations and services; expose globally for patch E
  window.__stationsAll = stations || [];
  window.__servicesAll = services || [];
  // build stations filter options and render stations list
  buildStationsFilter(services || []);
  renderStationsList(stations || []);
  // render services cards
  renderServiceCards(services, stations);
  // history mini stub (initial state)
  if (historyMini) historyMini.innerHTML = '<div>История за сегодня: нет данных</div>';
  }).catch(()=>{
    console.error('Init data failed');
  });

  function renderServiceCards(services, stations){
    grid.innerHTML = '';
    (services||[]).forEach(s => {
      const card = document.createElement('div'); card.className = 'serviceCard';
      const logo = document.createElement('div'); logo.className = 'logo'; logo.textContent = s.logoUrl ? '' : (s.name ? s.name.charAt(0) : 'S');
      const title = document.createElement('div'); title.className = 'name'; title.textContent = s.name;
      const desc = document.createElement('div'); desc.className = 'desc'; desc.textContent = s.description || '';
      const dur = document.createElement('div'); dur.textContent = (s.durationMinutes||0) + ' мин';
      const phone = document.createElement('div'); phone.className='phone';
      // pick first station's phone that offers this service
      let phoneVal = '—';
      if (stations && stations.length){
        const st = stations.find(st => st.services && st.services.some(ss => ss.service && ss.service.id === s.id));
        if (st && st.phone) phoneVal = st.phone;
      }
      phone.textContent = 'Телефон: ' + phoneVal;
      const btn = document.createElement('button'); btn.className='btn'; btn.textContent='Записаться';
      btn.onclick = () => openBookingModal(s.id);
      // ensure only one service detail popup is open at a time (close others when opening a new one)
      card.appendChild(logo); card.appendChild(title); card.appendChild(desc); card.appendChild(dur); card.appendChild(phone); card.appendChild(btn);
      grid.appendChild(card);
    });
  }

  // ===================== Stations (Section) =====================
  // Build filter select for stations by service
  function buildStationsFilter(services){
    const sel = document.getElementById('stationsFilter');
    if (!sel) return;
    sel.innerHTML = '<option value="">Все</option>';
    (services||[]).forEach(s => {
      const opt = document.createElement('option'); opt.value = (s.id||''); opt.textContent = s.name || ('Услуга ' + s.id);
      sel.appendChild(opt);
    });
  }

  // Render stations list in the Stations section
  function renderStationsList(stations){
    const listEl = document.getElementById('stationsList');
    if (!listEl) return;
    listEl.innerHTML = '';
    (stations||[]).forEach(st => {
      const card = document.createElement('div'); card.className = 'stationCard';
      const img = document.createElement('img'); img.className = 'stationLogo'; img.style.height = '60px'; img.style.width = '60px'; img.style.objectFit = 'cover'; img.style.borderRadius='6px';
      img.src = st.logoUrl || `https://picsum.photos/60/60?random=${st.id||'0'}`;
      const info = document.createElement('div'); info.style.display='flex'; info.style.flexDirection='column'; info.style.marginLeft='8px';
      const name = document.createElement('div'); name.style.fontWeight='700'; name.textContent = (st.name && st.name.toString().trim()) ? st.name : ('Станция ' + (st.id || '0'));
      const addr = document.createElement('div'); addr.style.fontSize='12px'; addr.textContent = st.address || '';
      const phone = document.createElement('div'); phone.style.fontSize='12px'; phone.style.color='#93c5fd'; phone.textContent = 'Телефон: ' + (st.phone || '—');
      const services = document.createElement('ul'); services.style.fontSize='12px'; services.style.color='#cbd5e1'; services.style.margin='6px 0 0 0'; services.style.paddingLeft='18px';
      const list = (st.services||[]).map(x => (x.service && x.service.name) ? x.service.name : (x.name||'Услуга')).filter(Boolean);
      if (list.length){
        list.forEach(n => { const li = document.createElement('li'); li.textContent = n; services.appendChild(li); });
      } else {
        const li = document.createElement('li'); li.textContent = 'нет'; services.appendChild(li);
      }
      const btn = document.createElement('button'); btn.className='btn'; btn.textContent = 'Записаться';
      btn.onclick = () => openBookingModal(st.id);
      card.appendChild(img); card.appendChild(info); info.appendChild(name); info.appendChild(addr); info.appendChild(phone); info.appendChild(services); info.appendChild(btn);
      listEl.appendChild(card);
    });
  }

  // Filtering handler for stations by service
  const stationsFilterEl = document.getElementById('stationsFilter');
  if (stationsFilterEl){
    stationsFilterEl.addEventListener('change', () => {
      const serviceId = stationsFilterEl.value;
      let filtered = (window.__stationsAll || []);
      if (serviceId){
        filtered = filtered.filter(st => (st.services||[]).some(s => s.service && s.service.id?.toString() === serviceId));
      }
      renderStationsList(filtered);
    });
  }

  // Booking modal
  function ensureModal(){ let m = document.getElementById('spa-booking-modal'); if (m) return m; m = document.createElement('div'); m.id='spa-booking-modal'; m.style.position='fixed'; m.style.left='50%'; m.style.top='50%'; m.style.transform='translate(-50%, -50%)'; m.style.background='#111827'; m.style.border='1px solid #334155'; m.style.borderRadius='8px'; m.style.padding='12px'; m.style.zIndex='1000'; m.style.display='none'; m.innerHTML=`<div style='font-weight:700;'>Booking</div><div id='spa-booking-body' style='margin-top:8px;'></div><div style='text-align:right;margin-top:8px;'><button class='btn' id='spa-book-confirm'>Сохранить</button></div>`; document.body.appendChild(m); m.querySelector('#spa-book-confirm').addEventListener('click', ()=> m.style.display='none'); return m; }
  window.openBookingModal = function(serviceId){ const modal = ensureModal(); const body = modal.querySelector('#spa-booking-body'); body.innerHTML = '<div>Услуга: '+serviceId+'</div>'; modal.style.display='block'; }
  window_Record = window.openBookingModal; // compat

  // Section navigation helpers (left menu)
  function fetchHistory(){
    fetch('/api/v1/history').then(r => r.json()).then(data => {
      const historyEl = document.getElementById('historyMini');
      if (historyEl) {
        if (Array.isArray(data) && data.length) {
          const items = data.slice(0, 10);
          historyEl.innerHTML = items.map(it => {
            const ts = it.timestamp || it.date || '';
            const desc = it.description || '';
            const stat = it.status || '';
            return `<div class="history-item"><span class="history-dot"></span><span>${ts}</span><span>- ${desc} (status: ${stat})</span></div>`;
          }).join('');
        } else {
          historyEl.innerHTML = '<div>История пуста</div>';
        }
      }
    }).catch(()=>{ /* ignore history errors */ });
  }

  function fetchApplications(){
    fetch('/api/v1/applications')
      .then(r => r.json())
      .then(list => {
        const body = document.getElementById('applicationsBody');
        if (!body) return;
        if (Array.isArray(list) && list.length){
          body.innerHTML = list.map(a => `<tr>${
            `<td>${a.vehicleMake || ''} ${a.vehicleModel || ''} (${a.licensePlate || ''})</td>`+
            `<td>${a.workNames || ''}</td>`+
            `<td>${a.startTime ? new Date(a.startTime).toLocaleDateString() : ''}</td>`+
            `<td>${a.startTime ? new Date(a.startTime).toLocaleTimeString() : ''} - ${a.endTime ? new Date(a.endTime).toLocaleTimeString() : ''}</td>`+
            `<td>${a.status || ''}</td>`+
            `<td>${a.customerName || ''}</td>`+
            `<td>${a.customerPhone || ''}</td>`+
            `<td>${a.customerComment || ''}</td>`+
          `</tr>`;
          `).join('');
        } else {
          body.innerHTML = '<tr><td colspan="8">Нет заявок</td></tr>';
        }
      })
      .catch(()=>{ const body = document.getElementById('applicationsBody'); if (body) body.innerHTML = '<tr><td colspan="8">Ошибка загрузки</td></tr>'; });
  }

  // Section navigation helpers (left menu)
  function showSection(sec){
    // Role-based access: admin sections should be hidden for CLIENT
    if (sec === 'admin' && getRole() !== 'ADMIN') {
      sec = 'map';
    }
    const sections = ['section-map','section-stations','section-worktypes','section-booking','section-history','section-applications','section-admin'];
    sections.forEach(id => {
      const el = document.getElementById(id);
      if (el) el.style.display = (id === 'section-' + sec) ? 'block' : 'none';
    });
    // refresh map size when showing map
    if (sec === 'map' && typeof map.invalidateSize === 'function') map.invalidateSize();
    // Close any open booking dialogs when switching sections
    const b = document.getElementById('spa-booking-modal'); if (b) b.style.display='none';
    const b2 = document.getElementById('spa-booking-enhanced-modal'); if (b2) b2.style.display='none';
    // Close any open popups to ensure clean state when switching tabs
    if (typeof map.closePopup === 'function') map.closePopup();
    // highlight active menu item
    // load history when opening history tab or switching sections
    fetchHistory();
    // If we navigated to applications, load the detailed applications list
    if (sec === 'applications') fetchApplications();
    // highlight active menu item
  document.querySelectorAll('#leftMenu .menu-item').forEach(item => {
    const view = item.getAttribute('data-view');
    if (view === sec) {
      item.classList.add('active');
    } else {
      item.classList.remove('active');
    }
  });
  }
  // Wire left menu navigation (no hash in URL)
  const menuLinks = document.querySelectorAll('#leftMenu .menu-item');
  menuLinks.forEach(a => {
    a.addEventListener('click', (e) => {
      const view = a.getAttribute('data-view');
      if (view) {
        e.preventDefault();
        showSection(view);
      }
    });
  });

  // On load, show the main section by role (no hash in URL)
  showSection(getRole() === 'ADMIN' ? 'admin' : 'map');
  // Ensure history widget is populated on load for both roles
  fetchHistory();
  // end of initialization
})();

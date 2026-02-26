// Very simple extended SPA to show types -> stations -> services
document.addEventListener('DOMContentLoaded', async () => {
  const left = document.getElementById('ext-types');
  const history = document.getElementById('ext-history');
  const services = document.getElementById('ext-services');
  // load types (workoptions)
  const opts = await fetch('/api/v1/workoptions').then(r => r.json()).catch(() => []);
  // render types
  left.innerHTML = (opts || []).map(o => `<button class="ext-type" data-id="${o.id}">${o.name} (${o.durationMinutes}m)</button>`).join('<br/>');
  // on click, filter stations by serviceId
  document.querySelectorAll('.ext-type').forEach(btn => {
    btn.addEventListener('click', async (e) => {
      const id = e.currentTarget.getAttribute('data-id');
      const stations = await fetch('/api/v1/stations?serviceId=' + id).then(r => r.json());
      // show stations as a simple list in history panel as placeholder
      history.innerHTML = stations.map(s => `<div>${s.name} - ${s.address ?? ''} - ${s.phone ?? ''}</div>`).join('');
      // load services on right panel matching type
      const allServices = await fetch('/api/v1/services').then(r => r.json());
      services.innerHTML = allServices.map(s => `
        <div class="service-card card" style="padding:12px; border:1px solid #ddd; border-radius:8px; min-width:240px;"">
          <div style="font-weight:700; font-size:14px; margin-bottom:6px;">
            <img src="${s.logoUrl || ''}" alt="logo" style="height:28px; width:28px; vertical-align:middle; object-fit:cover; border-radius:4px;"/>&nbsp;${s.name}
          </div>
          <div style="font-size:12px; color:#555;">${s.description || ''}</div>
          <div style="font-size:12px; margin-top:6px;">${s.durationMinutes} мин</div>
          <button style="margin-top:8px;">Записаться</button>
        </div>
      `).join('');
    });
  });
  // initial seed
  const seedType = (opts[0] && opts[0].id) || null;
  if (seedType) {
    document.querySelector('.ext-type[data-id="' + seedType + '"]')?.dispatchEvent(new Event('click'));
  }
});

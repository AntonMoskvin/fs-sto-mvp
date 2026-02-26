/* Новый, упрощённый Booking Umbrella: браузерная реализация без реальной авторизации */
(function(){
  if (typeof window.openBookingModal !== 'function') return;
  window.openBookingModal = function(stationId){
    // модальное окно бронирования
    let modal = document.getElementById('spa-booking-enhanced-modal');
    if (!modal){
      modal = document.createElement('div'); modal.id = 'spa-booking-enhanced-modal';
      Object.assign(modal.style, {
        position:'fixed', left:'50%', top:'50%', transform:'translate(-50%, -50%)',
        background:'#111827', border:'1px solid #334155', borderRadius:'8px', padding:'12px', zIndex:'1000', display:'none', minWidth:'360px'
      });
      modal.innerHTML = `
        <div style='font-weight:700; margin-bottom:6px;'>Booking</div>
        <div id='spa-booking-enhanced-body' style='display:flex; flex-direction:column; gap:8px;'></div>
        <div style='display:flex; justify-content:flex-end; gap:8px; margin-top:6px;'>
          <button class='btn' id='spa-book-enhanced-cancel'>Отмена</button>
          <button class='btn' id='spa-book-enhanced-submit'>Записать</button>
        </div>`;
      document.body.appendChild(modal);
      modal.querySelector('#spa-book-enhanced-cancel').addEventListener('click', ()=> modal.style.display='none');
      modal.querySelector('#spa-book-enhanced-submit').addEventListener('click', ()=> {
        const body = modal.querySelector('#spa-booking-enhanced-body');
        const dateEl = document.getElementById('spa-booking-date');
        const timeEl = document.getElementById('spa-booking-time');
        const date = dateEl ? dateEl.value : '';
        const time = timeEl ? timeEl.value : '';
        if (!date || !time){ alert('Укажите дату и время'); return; }
        const startDate = new Date(date + 'T' + time);
        const minDate = new Date(Date.now() + 2*60*60*1000);
        if (startDate < minDate){ alert('Запись доступна не ранее чем через 2 часа. Пожалуйста выберите другую дату/время.'); return; }
        const startTime = date + 'T' + time;
        const checkedIds = Array.from(body.querySelectorAll('#spa-booking-options input[type="checkbox"]')).filter(i=>i.checked).map(i=>parseInt(i.value,10));
        const payload = { stationId, startTime };
        if (checkedIds.length) payload.workOptionIds = checkedIds;
        fetch('/api/v1/appointments', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(payload) })
          .then(r => r.json())
          .then(res => {
            modal.style.display='none';
            const hist = document.getElementById('historyMini'); if (hist) hist.innerHTML = '<div>Бронирование: ' + (res && res.id ? ('ID ' + res.id) : 'OK') + ' (Статус: ' + (res.status || '') + ')</div>';
            // спрятать нижнюю панель уведомлений больше не нужна по запросу
            const bp = document.getElementById('bottomPanel'); if (bp) bp.style.display='none';
          })
          .catch(()=>{ alert('Ошибка бронирования'); });
      });
    }
    // загрузить данные для формы
    Promise.all([
      fetch('/api/v1/stations').then(r=>r.json()),
      fetch('/api/v1/workoptions').then(r=>r.json())
    ]).then(([stations, workOptions]) => {
      const st = (stations||[]).find(x => x.id === stationId) || { id: stationId, name: 'Станция '+stationId };
      modal.dataset.stationId = stationId;
      const body = modal.querySelector('#spa-booking-enhanced-body');
      const dateStr = new Date().toISOString().slice(0,10);
      const timeStr = new Date().toTimeString().slice(0,5);
      const optsHtml = (workOptions||[]).map(o => `<label><input type='checkbox' value='${o.id}'> ${o.name} (${o.durationMinutes||60} мин)</label>`).join('');
      body.innerHTML = `
        <div><strong>Станция:</strong> ${st.name}</div>
        <div><strong>Услуги:</strong> выберите несколько</div>
        <div id='spa-booking-options'>${optsHtml}</div>
        <div><strong>Дата:</strong> <input id='spa-booking-date' type='date' value='${dateStr}'></div>
        <div><strong>Время:</strong> <input id='spa-booking-time' type='time' value='${timeStr}'></div>
      `;
      modal.style.display = 'block';
    }).catch(()=>{ modal.style.display='block'; body.innerHTML = '<div>Ошибка загрузки данных бронирования</div>'; });
  };
})();

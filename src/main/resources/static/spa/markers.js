// Service markers on the map: 10 markers (real stations + demo)
(function(){
  window.initServiceMarkers = function(map, stations, services){
    const markersData = [];
    const baseLat = 55.75, baseLon = 37.62;
    for (let i = 0; i < 10; i++){
      if (stations && i < stations.length && typeof stations[i].latitude === 'number' && typeof stations[i].longitude === 'number'){
        const s = stations[i];
        markersData.push({ id: s.id, name: s.name, lat: s.latitude, lon: s.longitude, address: s.address, phone: s.phone });
      } else {
        markersData.push({ id: 1000+i, name: 'Demo СТО ' + (i+1), lat: baseLat + i*0.04, lon: baseLon + i*0.04, address: 'Москва', phone: '+7 495 0' + (i+1).toString().padStart(2,'0') });
      }
    }
    markersData.forEach(m => {
      if (typeof m.lat === 'number' && typeof m.lon === 'number'){
        const popup = `<div style="min-width:240px;">
          <strong>${m.name}</strong><br/>${m.address||''}<br/>Телефон: ${m.phone||''}<br/>
          <button class="btn" onclick="window.startBookingFromMarker(${m.id})" style="margin-top:6px;">Записаться</button>
        </div>`;
        L.marker([m.lat, m.lon]).addTo(map).bindPopup(popup);
      }
    });
  };
  window.startBookingFromMarker = function(id){
    alert('Booking flow for marker id ' + id);
  };
})();

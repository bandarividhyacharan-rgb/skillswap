function togglePassword(id, button) {
  const field = document.getElementById(id);
  if (!field) return;
  const showing = field.type === 'text';
  field.type = showing ? 'password' : 'text';
  button.textContent = showing ? 'Show' : 'Hide';
}
function toggleMusic() {
  const audio = document.getElementById('bgMusic');
  const button = document.getElementById('musicToggle');
  if (!audio || !button) return;
  if (audio.paused) {
    audio.play().then(() => button.textContent = '♫ Music: on')
      .catch(() => button.textContent = '♫ Music unavailable');
  } else {
    audio.pause();
    button.textContent = '♫ Music: off';
  }
}

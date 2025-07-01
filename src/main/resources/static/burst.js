// Floating hearts for button click
function createHeart(x, y) {
    const heart = document.createElement('span');
    heart.className = 'heart';
    heart.innerHTML = '❤';
    heart.style.left = (x - 10) + 'px';
    heart.style.top = (y - 10) + 'px';
    heart.style.color = ['#ff80ab', '#b71c1c', '#ffb6b9', '#f8bbd0'][Math.floor(Math.random()*4)];
    document.body.appendChild(heart);
    setTimeout(() => heart.remove(), 2500);
}

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('button').forEach(btn => {
        btn.addEventListener('click', function(e) {
            const rect = btn.getBoundingClientRect();
            const x = e.clientX || (rect.left + rect.width/2);
            const y = e.clientY || (rect.top + rect.height/2);
            createHeart(x, y);
        });
    });
}); 
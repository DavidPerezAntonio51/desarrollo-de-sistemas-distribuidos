document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('captureButton').addEventListener('click', function() {
        window.location.href = 'capture_article.html';
    });

    document.getElementById('purchaseButton').addEventListener('click', function() {
        window.location.href = 'buy_article.html';
    });
});

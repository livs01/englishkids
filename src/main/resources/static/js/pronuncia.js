/*
 * Botões com o atributo data-falar="palavra" leem a palavra em voz alta, em inglês,
 * usando a síntese de voz do navegador (Web Speech API). Se o navegador não tiver
 * suporte, o botão é desabilitado e o restante da página continua funcionando.
 */
(function () {
    'use strict';

    var suportado = 'speechSynthesis' in window && 'SpeechSynthesisUtterance' in window;

    document.addEventListener('DOMContentLoaded', function () {
        if (suportado) {
            return;
        }
        document.querySelectorAll('[data-falar]').forEach(function (botao) {
            botao.disabled = true;
            botao.title = 'Seu navegador não suporta áudio de pronúncia.';
        });
    });

    document.addEventListener('click', function (evento) {
        var botao = evento.target.closest('[data-falar]');
        if (!botao || !suportado) {
            return;
        }
        var fala = new SpeechSynthesisUtterance(botao.getAttribute('data-falar'));
        fala.lang = 'en-US';
        fala.rate = 0.8;
        window.speechSynthesis.cancel();
        window.speechSynthesis.speak(fala);
    });
})();

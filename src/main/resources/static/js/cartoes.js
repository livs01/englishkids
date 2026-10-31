
(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        var lista = document.querySelector('.study-grid');
        var botao = document.getElementById('alternar-cartoes');
        var estado = document.getElementById('estado-estudo');

        if (!lista || !botao) {
            return;
        }

        var detalhes = Array.from(lista.querySelectorAll('details'));
        var total = detalhes.length;

        function atualizarEstado() {
            var abertos = detalhes.filter(function (item) {
                return item.open;
            }).length;

            if (estado) {
                if (abertos === 0) {
                    estado.textContent =
                        'Nenhum cartão revelado ainda. Vamos começar?';
                } else if (abertos === total) {
                    estado.textContent =
                        'Parabéns! Você revelou todos os ' + total + ' cartões!';
                } else {
                    estado.textContent =
                        abertos + ' de ' + total + ' cartões revelados.';
                }
            }

            var todosAbertos = abertos === total;

            botao.textContent = todosAbertos
                ? '🙈 Ocultar todas'
                : '👀 Revelar todas';

            botao.setAttribute('aria-expanded', String(todosAbertos));
        }

        botao.addEventListener('click', function () {
            var todosAbertos = detalhes.every(function (item) {
                return item.open;
            });

            detalhes.forEach(function (item) {
                item.open = !todosAbertos;
            });

            atualizarEstado();
        });

        detalhes.forEach(function (item) {
            item.addEventListener('toggle', atualizarEstado);
        });

        atualizarEstado();
    });
})();

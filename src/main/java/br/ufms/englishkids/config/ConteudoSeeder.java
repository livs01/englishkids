package br.ufms.englishkids.config;

import br.ufms.englishkids.model.Nivel;
import br.ufms.englishkids.model.Palavra;
import br.ufms.englishkids.model.Tema;
import br.ufms.englishkids.repository.PalavraRepository;
import br.ufms.englishkids.repository.TemaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Popula a plataforma com temas e palavras de EXEMPLO para a demonstração.
 * Só executa se app.seed.enabled=true e se ainda não houver temas cadastrados.
 * As pronúncias são aproximações em português, apenas para apoio didático.
 */
@Configuration
public class ConteudoSeeder {

    private static final Logger log = LoggerFactory.getLogger(ConteudoSeeder.class);

    @Bean
    CommandLineRunner seedConteudo(TemaRepository temas,
                                   PalavraRepository palavras,
                                   TransactionTemplate tx,
                                   @Value("${app.seed.enabled:false}") boolean enabled) {
        return args -> {
            if (!enabled || temas.count() > 0) {
                return;
            }
            tx.executeWithoutResult(status -> {
                Tema animais = temas.save(new Tema("Animals", "Animais", "🐾", "Bichinhos que moram na casa, na fazenda e na floresta."));
                Tema cores = temas.save(new Tema("Colors", "Cores", "🎨", "As cores do arco-íris."));
                Tema frutas = temas.save(new Tema("Fruits", "Frutas", "🍎", "Frutas gostosas e saudáveis."));
                Tema familia = temas.save(new Tema("Family", "Família", "👪", "As pessoas da nossa família."));
                Tema numeros = temas.save(new Tema("Numbers", "Números", "🔢", "Contando de um a três."));

                palavras.save(nova("Dog", "cachorro", "dóg", "The dog is happy.", "🐶", Nivel.INICIANTE, animais));
                palavras.save(nova("Cat", "gato", "quét", "The cat sleeps all day.", "🐱", Nivel.INICIANTE, animais));
                palavras.save(nova("Bird", "pássaro", "béd", "The bird can fly.", "🐦", Nivel.INICIANTE, animais));
                palavras.save(nova("Fish", "peixe", "fích", "The fish swims in the water.", "🐟", Nivel.INICIANTE, animais));

                palavras.save(nova("Red", "vermelho", "réd", "I like the red car.", "🔴", Nivel.INICIANTE, cores));
                palavras.save(nova("Blue", "azul", "blú", "The sky is blue.", "🔵", Nivel.INICIANTE, cores));
                palavras.save(nova("Green", "verde", "grín", "The grass is green.", "🟢", Nivel.INICIANTE, cores));
                palavras.save(nova("Yellow", "amarelo", "iélou", "The sun is yellow.", "🟡", Nivel.INTERMEDIARIO, cores));

                palavras.save(nova("Apple", "maçã", "épol", "I eat an apple every day.", "🍎", Nivel.INICIANTE, frutas));
                palavras.save(nova("Banana", "banana", "banâna", "Monkeys love bananas.", "🍌", Nivel.INICIANTE, frutas));
                palavras.save(nova("Grape", "uva", "gréip", "The grape is purple.", "🍇", Nivel.INTERMEDIARIO, frutas));

                palavras.save(nova("Mother", "mãe", "mâder", "I love my mother.", "👩", Nivel.INICIANTE, familia));
                palavras.save(nova("Father", "pai", "fáder", "My father is tall.", "👨", Nivel.INICIANTE, familia));
                palavras.save(nova("Sister", "irmã", "sísta", "My sister can sing.", "👧", Nivel.INTERMEDIARIO, familia));
                palavras.save(nova("Brother", "irmão", "bráder", "My brother plays soccer.", "👦", Nivel.INTERMEDIARIO, familia));

                palavras.save(nova("One", "um", "uãn", "I have one ball.", "1️⃣", Nivel.INICIANTE, numeros));
                palavras.save(nova("Two", "dois", "tú", "I have two eyes.", "2️⃣", Nivel.INICIANTE, numeros));
                palavras.save(nova("Three", "três", "thrí", "I see three birds.", "3️⃣", Nivel.INICIANTE, numeros));
            });
            log.info("Conteúdo de demonstração criado (5 temas, 18 palavras).");
        };
    }

    private static Palavra nova(String ingles, String portugues, String pronuncia, String exemplo,
                                String emoji, Nivel nivel, Tema tema) {
        Palavra p = new Palavra();
        p.setIngles(ingles);
        p.setPortugues(portugues);
        p.setPronuncia(pronuncia);
        p.setExemplo(exemplo);
        p.setEmoji(emoji);
        p.setNivel(nivel);
        p.setTema(tema);
        return p;
    }
}

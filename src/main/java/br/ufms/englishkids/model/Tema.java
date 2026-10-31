package br.ufms.englishkids.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Tema que agrupa palavras (ex.: Animals / Animais). */
@Entity
@Table(name = "temas")
public class Tema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_ingles", nullable = false, unique = true, length = 60)
    private String nomeIngles;

    @Column(name = "nome_portugues", nullable = false, length = 60)
    private String nomePortugues;

    @Column(length = 16)
    private String emoji;

    @Column(length = 300)
    private String descricao;

    public Tema() {
        // exigido pelo JPA
    }

    public Tema(String nomeIngles, String nomePortugues, String emoji, String descricao) {
        this.nomeIngles = nomeIngles;
        this.nomePortugues = nomePortugues;
        this.emoji = emoji;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public String getNomeIngles() { return nomeIngles; }
    public void setNomeIngles(String nomeIngles) { this.nomeIngles = nomeIngles; }
    public String getNomePortugues() { return nomePortugues; }
    public void setNomePortugues(String nomePortugues) { this.nomePortugues = nomePortugues; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}

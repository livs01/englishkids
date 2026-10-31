package br.ufms.englishkids.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Palavra de vocabulário em inglês, com tradução, pronúncia aproximada e frase de exemplo. */
@Entity
@Table(name = "palavras",
       uniqueConstraints = @UniqueConstraint(name = "uk_palavra_tema", columnNames = {"ingles", "tema_id"}))
public class Palavra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String ingles;

    @Column(nullable = false, length = 60)
    private String portugues;

    /** Pronúncia aproximada em português (ex.: "dóg" para "dog"). */
    @Column(length = 60)
    private String pronuncia;

    /** Frase de exemplo em inglês. */
    @Column(length = 200)
    private String exemplo;

    @Column(length = 16)
    private String emoji;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Nivel nivel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tema_id", nullable = false)
    private Tema tema;

    public Palavra() {
        // exigido pelo JPA (público para facilitar a criação nos services)
    }

    public Long getId() { return id; }
    public String getIngles() { return ingles; }
    public void setIngles(String ingles) { this.ingles = ingles; }
    public String getPortugues() { return portugues; }
    public void setPortugues(String portugues) { this.portugues = portugues; }
    public String getPronuncia() { return pronuncia; }
    public void setPronuncia(String pronuncia) { this.pronuncia = pronuncia; }
    public String getExemplo() { return exemplo; }
    public void setExemplo(String exemplo) { this.exemplo = exemplo; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public Nivel getNivel() { return nivel; }
    public void setNivel(Nivel nivel) { this.nivel = nivel; }
    public Tema getTema() { return tema; }
    public void setTema(Tema tema) { this.tema = tema; }
}

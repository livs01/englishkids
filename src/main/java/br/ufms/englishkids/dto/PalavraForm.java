package br.ufms.englishkids.dto;

import br.ufms.englishkids.model.Nivel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Dados de entrada (formulário) de Palavra. */
public class PalavraForm {

    @NotBlank(message = "Informe a palavra em inglês.")
    @Size(max = 60, message = "A palavra em inglês deve ter no máximo 60 caracteres.")
    private String ingles;

    @NotBlank(message = "Informe a tradução em português.")
    @Size(max = 60, message = "A tradução deve ter no máximo 60 caracteres.")
    private String portugues;

    @Size(max = 60, message = "A pronúncia deve ter no máximo 60 caracteres.")
    private String pronuncia;

    @Size(max = 200, message = "A frase de exemplo deve ter no máximo 200 caracteres.")
    private String exemplo;

    @Size(max = 16, message = "O emoji deve ter no máximo 16 caracteres.")
    private String emoji;

    @NotNull(message = "Selecione o nível.")
    private Nivel nivel;

    @NotNull(message = "Selecione o tema.")
    private Long temaId;

    public static PalavraForm from(PalavraResumo r) {
        PalavraForm f = new PalavraForm();
        f.setIngles(r.ingles());
        f.setPortugues(r.portugues());
        f.setPronuncia(r.pronuncia());
        f.setExemplo(r.exemplo());
        f.setEmoji(r.emoji());
        f.setNivel(r.nivel());
        f.setTemaId(r.temaId());
        return f;
    }

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
    public Long getTemaId() { return temaId; }
    public void setTemaId(Long temaId) { this.temaId = temaId; }
}

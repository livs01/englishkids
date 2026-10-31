package br.ufms.englishkids.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Dados de entrada (formulário) de Tema. */
public class TemaForm {

    @NotBlank(message = "Informe o nome do tema em inglês.")
    @Size(max = 60, message = "O nome em inglês deve ter no máximo 60 caracteres.")
    private String nomeIngles;

    @NotBlank(message = "Informe o nome do tema em português.")
    @Size(max = 60, message = "O nome em português deve ter no máximo 60 caracteres.")
    private String nomePortugues;

    @Size(max = 16, message = "O emoji deve ter no máximo 16 caracteres.")
    private String emoji;

    @Size(max = 300, message = "A descrição deve ter no máximo 300 caracteres.")
    private String descricao;

    public static TemaForm from(TemaResumo r) {
        TemaForm f = new TemaForm();
        f.setNomeIngles(r.nomeIngles());
        f.setNomePortugues(r.nomePortugues());
        f.setEmoji(r.emoji());
        f.setDescricao(r.descricao());
        return f;
    }

    public String getNomeIngles() { return nomeIngles; }
    public void setNomeIngles(String nomeIngles) { this.nomeIngles = nomeIngles; }
    public String getNomePortugues() { return nomePortugues; }
    public void setNomePortugues(String nomePortugues) { this.nomePortugues = nomePortugues; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
